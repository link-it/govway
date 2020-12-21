/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config.base.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro;
import org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.ElencoPlugin;
import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro;
import org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro;
import org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita;
import org.openspcoop2.monitor.engine.config.base.IdPlugin;
import org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin;
import org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita;
import org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita;
import org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBElement;

/**     
 * XML Serializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractSerializer {


	protected abstract WriteToSerializerType getType();
	
	protected void _objToXml(OutputStream out, Class<?> c, Object object,
			boolean prettyPrint) throws Exception {
		if(object instanceof JAXBElement){
			// solo per il tipo WriteToSerializerType.JAXB
			JaxbUtils.objToXml(out, c, object, prettyPrint);
		}else{
			Method m = c.getMethod("writeTo", OutputStream.class, WriteToSerializerType.class);
			m.invoke(object, out, this.getType());
		}
	}
	
	protected void objToXml(OutputStream out,Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		try{
			this._objToXml(out, c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
		finally{
			try{
				out.flush();
			}catch(Exception e){}
		}
	}
	protected void objToXml(String fileName,Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		try{
			this.objToXml(new File(fileName), c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
	}
	protected void objToXml(File file,Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			this._objToXml(fout, c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
		finally{
			try{
				fout.flush();
			}catch(Exception e){}
			try{
				fout.close();
			}catch(Exception e){}
		}
	}
	protected ByteArrayOutputStream objToXml(Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		ByteArrayOutputStream bout = null;
		try{
			bout = new ByteArrayOutputStream();
			this._objToXml(bout, c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
		finally{
			try{
				bout.flush();
			}catch(Exception e){}
			try{
				bout.close();
			}catch(Exception e){}
		}
		return bout;
	}




	/*
	 =================================================================================
	 Object: id-configurazione-filtro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneFiltro</var>
	 * @param idConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneFiltro idConfigurazioneFiltro) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneFiltro.class, idConfigurazioneFiltro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneFiltro</var>
	 * @param idConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneFiltro idConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneFiltro.class, idConfigurazioneFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneFiltro</var>
	 * @param idConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneFiltro idConfigurazioneFiltro) throws SerializerException {
		this.objToXml(file, IdConfigurazioneFiltro.class, idConfigurazioneFiltro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneFiltro</var>
	 * @param idConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneFiltro idConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdConfigurazioneFiltro.class, idConfigurazioneFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneFiltro</var>
	 * @param idConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneFiltro idConfigurazioneFiltro) throws SerializerException {
		this.objToXml(out, IdConfigurazioneFiltro.class, idConfigurazioneFiltro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneFiltro</var>
	 * @param idConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneFiltro idConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdConfigurazioneFiltro.class, idConfigurazioneFiltro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro}
	 * 
	 * @param idConfigurazioneFiltro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneFiltro idConfigurazioneFiltro) throws SerializerException {
		return this.objToXml(IdConfigurazioneFiltro.class, idConfigurazioneFiltro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro}
	 * 
	 * @param idConfigurazioneFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneFiltro idConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdConfigurazioneFiltro.class, idConfigurazioneFiltro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro}
	 * 
	 * @param idConfigurazioneFiltro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneFiltro idConfigurazioneFiltro) throws SerializerException {
		return this.objToXml(IdConfigurazioneFiltro.class, idConfigurazioneFiltro, false).toString();
	}
	/**
	 * Serialize to String the object <var>idConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro}
	 * 
	 * @param idConfigurazioneFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneFiltro idConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdConfigurazioneFiltro.class, idConfigurazioneFiltro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-configurazione-filtro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdConfigurazioneFiltro</var>
	 * @param elencoIdConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdConfigurazioneFiltro elencoIdConfigurazioneFiltro) throws SerializerException {
		this.objToXml(fileName, ElencoIdConfigurazioneFiltro.class, elencoIdConfigurazioneFiltro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdConfigurazioneFiltro</var>
	 * @param elencoIdConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdConfigurazioneFiltro elencoIdConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoIdConfigurazioneFiltro.class, elencoIdConfigurazioneFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdConfigurazioneFiltro</var>
	 * @param elencoIdConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdConfigurazioneFiltro elencoIdConfigurazioneFiltro) throws SerializerException {
		this.objToXml(file, ElencoIdConfigurazioneFiltro.class, elencoIdConfigurazioneFiltro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdConfigurazioneFiltro</var>
	 * @param elencoIdConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdConfigurazioneFiltro elencoIdConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoIdConfigurazioneFiltro.class, elencoIdConfigurazioneFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdConfigurazioneFiltro</var>
	 * @param elencoIdConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdConfigurazioneFiltro elencoIdConfigurazioneFiltro) throws SerializerException {
		this.objToXml(out, ElencoIdConfigurazioneFiltro.class, elencoIdConfigurazioneFiltro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdConfigurazioneFiltro</var>
	 * @param elencoIdConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdConfigurazioneFiltro elencoIdConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoIdConfigurazioneFiltro.class, elencoIdConfigurazioneFiltro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoIdConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param elencoIdConfigurazioneFiltro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdConfigurazioneFiltro elencoIdConfigurazioneFiltro) throws SerializerException {
		return this.objToXml(ElencoIdConfigurazioneFiltro.class, elencoIdConfigurazioneFiltro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoIdConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param elencoIdConfigurazioneFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdConfigurazioneFiltro elencoIdConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdConfigurazioneFiltro.class, elencoIdConfigurazioneFiltro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoIdConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param elencoIdConfigurazioneFiltro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdConfigurazioneFiltro elencoIdConfigurazioneFiltro) throws SerializerException {
		return this.objToXml(ElencoIdConfigurazioneFiltro.class, elencoIdConfigurazioneFiltro, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoIdConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param elencoIdConfigurazioneFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdConfigurazioneFiltro elencoIdConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdConfigurazioneFiltro.class, elencoIdConfigurazioneFiltro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: plugin
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>plugin</var>
	 * @param plugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Plugin plugin) throws SerializerException {
		this.objToXml(fileName, Plugin.class, plugin, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>plugin</var>
	 * @param plugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Plugin plugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Plugin.class, plugin, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 * 
	 * @param file Xml file to serialize the object <var>plugin</var>
	 * @param plugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Plugin plugin) throws SerializerException {
		this.objToXml(file, Plugin.class, plugin, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 * 
	 * @param file Xml file to serialize the object <var>plugin</var>
	 * @param plugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Plugin plugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Plugin.class, plugin, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 * 
	 * @param out OutputStream to serialize the object <var>plugin</var>
	 * @param plugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Plugin plugin) throws SerializerException {
		this.objToXml(out, Plugin.class, plugin, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 * 
	 * @param out OutputStream to serialize the object <var>plugin</var>
	 * @param plugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Plugin plugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Plugin.class, plugin, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 * 
	 * @param plugin Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Plugin plugin) throws SerializerException {
		return this.objToXml(Plugin.class, plugin, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 * 
	 * @param plugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Plugin plugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Plugin.class, plugin, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 * 
	 * @param plugin Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Plugin plugin) throws SerializerException {
		return this.objToXml(Plugin.class, plugin, false).toString();
	}
	/**
	 * Serialize to String the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 * 
	 * @param plugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Plugin plugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Plugin.class, plugin, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-plugin
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoPlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoPlugin</var>
	 * @param elencoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoPlugin elencoPlugin) throws SerializerException {
		this.objToXml(fileName, ElencoPlugin.class, elencoPlugin, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoPlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoPlugin</var>
	 * @param elencoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoPlugin elencoPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoPlugin.class, elencoPlugin, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoPlugin}
	 * 
	 * @param file Xml file to serialize the object <var>elencoPlugin</var>
	 * @param elencoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoPlugin elencoPlugin) throws SerializerException {
		this.objToXml(file, ElencoPlugin.class, elencoPlugin, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoPlugin}
	 * 
	 * @param file Xml file to serialize the object <var>elencoPlugin</var>
	 * @param elencoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoPlugin elencoPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoPlugin.class, elencoPlugin, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoPlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoPlugin</var>
	 * @param elencoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoPlugin elencoPlugin) throws SerializerException {
		this.objToXml(out, ElencoPlugin.class, elencoPlugin, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoPlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoPlugin</var>
	 * @param elencoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoPlugin elencoPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoPlugin.class, elencoPlugin, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoPlugin}
	 * 
	 * @param elencoPlugin Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoPlugin elencoPlugin) throws SerializerException {
		return this.objToXml(ElencoPlugin.class, elencoPlugin, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoPlugin}
	 * 
	 * @param elencoPlugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoPlugin elencoPlugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoPlugin.class, elencoPlugin, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoPlugin}
	 * 
	 * @param elencoPlugin Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoPlugin elencoPlugin) throws SerializerException {
		return this.objToXml(ElencoPlugin.class, elencoPlugin, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoPlugin}
	 * 
	 * @param elencoPlugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoPlugin elencoPlugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoPlugin.class, elencoPlugin, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneServizioAzione configurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		this.objToXml(file, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneServizioAzione configurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		this.objToXml(out, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneServizioAzione configurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 * 
	 * @param configurazioneServizioAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		return this.objToXml(ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 * 
	 * @param configurazioneServizioAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneServizioAzione configurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneServizioAzione.class, configurazioneServizioAzione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 * 
	 * @param configurazioneServizioAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		return this.objToXml(ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 * 
	 * @param configurazioneServizioAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneServizioAzione configurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneServizioAzione.class, configurazioneServizioAzione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneServizio</var>
	 * @param idConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneServizio idConfigurazioneServizio) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneServizio.class, idConfigurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneServizio</var>
	 * @param idConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneServizio idConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneServizio.class, idConfigurazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneServizio</var>
	 * @param idConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneServizio idConfigurazioneServizio) throws SerializerException {
		this.objToXml(file, IdConfigurazioneServizio.class, idConfigurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneServizio</var>
	 * @param idConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneServizio idConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdConfigurazioneServizio.class, idConfigurazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneServizio</var>
	 * @param idConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneServizio idConfigurazioneServizio) throws SerializerException {
		this.objToXml(out, IdConfigurazioneServizio.class, idConfigurazioneServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneServizio</var>
	 * @param idConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneServizio idConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdConfigurazioneServizio.class, idConfigurazioneServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio}
	 * 
	 * @param idConfigurazioneServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneServizio idConfigurazioneServizio) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizio.class, idConfigurazioneServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio}
	 * 
	 * @param idConfigurazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneServizio idConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizio.class, idConfigurazioneServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio}
	 * 
	 * @param idConfigurazioneServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneServizio idConfigurazioneServizio) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizio.class, idConfigurazioneServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio}
	 * 
	 * @param idConfigurazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneServizio idConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizio.class, idConfigurazioneServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneServizio.class, configurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneServizio configurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneServizio.class, configurazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		this.objToXml(file, ConfigurazioneServizio.class, configurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneServizio configurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneServizio.class, configurazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		this.objToXml(out, ConfigurazioneServizio.class, configurazioneServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneServizio configurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneServizio.class, configurazioneServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 * 
	 * @param configurazioneServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		return this.objToXml(ConfigurazioneServizio.class, configurazioneServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 * 
	 * @param configurazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneServizio configurazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneServizio.class, configurazioneServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 * 
	 * @param configurazioneServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		return this.objToXml(ConfigurazioneServizio.class, configurazioneServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 * 
	 * @param configurazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneServizio configurazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneServizio.class, configurazioneServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneServizioAzione</var>
	 * @param idConfigurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneServizioAzione</var>
	 * @param idConfigurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneServizioAzione idConfigurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneServizioAzione</var>
	 * @param idConfigurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws SerializerException {
		this.objToXml(file, IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneServizioAzione</var>
	 * @param idConfigurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneServizioAzione idConfigurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneServizioAzione</var>
	 * @param idConfigurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws SerializerException {
		this.objToXml(out, IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneServizioAzione</var>
	 * @param idConfigurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneServizioAzione idConfigurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione}
	 * 
	 * @param idConfigurazioneServizioAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione}
	 * 
	 * @param idConfigurazioneServizioAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneServizioAzione idConfigurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione}
	 * 
	 * @param idConfigurazioneServizioAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione}
	 * 
	 * @param idConfigurazioneServizioAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneServizioAzione idConfigurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-filtro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneFiltro</var>
	 * @param configurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneFiltro configurazioneFiltro) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneFiltro.class, configurazioneFiltro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneFiltro</var>
	 * @param configurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneFiltro configurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneFiltro.class, configurazioneFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneFiltro</var>
	 * @param configurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneFiltro configurazioneFiltro) throws SerializerException {
		this.objToXml(file, ConfigurazioneFiltro.class, configurazioneFiltro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneFiltro</var>
	 * @param configurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneFiltro configurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneFiltro.class, configurazioneFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneFiltro</var>
	 * @param configurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneFiltro configurazioneFiltro) throws SerializerException {
		this.objToXml(out, ConfigurazioneFiltro.class, configurazioneFiltro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneFiltro</var>
	 * @param configurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneFiltro configurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneFiltro.class, configurazioneFiltro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 * 
	 * @param configurazioneFiltro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneFiltro configurazioneFiltro) throws SerializerException {
		return this.objToXml(ConfigurazioneFiltro.class, configurazioneFiltro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 * 
	 * @param configurazioneFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneFiltro configurazioneFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneFiltro.class, configurazioneFiltro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 * 
	 * @param configurazioneFiltro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneFiltro configurazioneFiltro) throws SerializerException {
		return this.objToXml(ConfigurazioneFiltro.class, configurazioneFiltro, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 * 
	 * @param configurazioneFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneFiltro configurazioneFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneFiltro.class, configurazioneFiltro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-configurazione-filtro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoConfigurazioneFiltro</var>
	 * @param elencoConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoConfigurazioneFiltro elencoConfigurazioneFiltro) throws SerializerException {
		this.objToXml(fileName, ElencoConfigurazioneFiltro.class, elencoConfigurazioneFiltro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoConfigurazioneFiltro</var>
	 * @param elencoConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoConfigurazioneFiltro elencoConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoConfigurazioneFiltro.class, elencoConfigurazioneFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>elencoConfigurazioneFiltro</var>
	 * @param elencoConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoConfigurazioneFiltro elencoConfigurazioneFiltro) throws SerializerException {
		this.objToXml(file, ElencoConfigurazioneFiltro.class, elencoConfigurazioneFiltro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>elencoConfigurazioneFiltro</var>
	 * @param elencoConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoConfigurazioneFiltro elencoConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoConfigurazioneFiltro.class, elencoConfigurazioneFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoConfigurazioneFiltro</var>
	 * @param elencoConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoConfigurazioneFiltro elencoConfigurazioneFiltro) throws SerializerException {
		this.objToXml(out, ElencoConfigurazioneFiltro.class, elencoConfigurazioneFiltro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoConfigurazioneFiltro</var>
	 * @param elencoConfigurazioneFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoConfigurazioneFiltro elencoConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoConfigurazioneFiltro.class, elencoConfigurazioneFiltro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro}
	 * 
	 * @param elencoConfigurazioneFiltro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoConfigurazioneFiltro elencoConfigurazioneFiltro) throws SerializerException {
		return this.objToXml(ElencoConfigurazioneFiltro.class, elencoConfigurazioneFiltro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro}
	 * 
	 * @param elencoConfigurazioneFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoConfigurazioneFiltro elencoConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoConfigurazioneFiltro.class, elencoConfigurazioneFiltro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro}
	 * 
	 * @param elencoConfigurazioneFiltro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoConfigurazioneFiltro elencoConfigurazioneFiltro) throws SerializerException {
		return this.objToXml(ElencoConfigurazioneFiltro.class, elencoConfigurazioneFiltro, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoConfigurazioneFiltro</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneFiltro}
	 * 
	 * @param elencoConfigurazioneFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoConfigurazioneFiltro elencoConfigurazioneFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoConfigurazioneFiltro.class, elencoConfigurazioneFiltro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: plugin-servizio-azione-compatibilita
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>pluginServizioAzioneCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita}
	 * 
	 * @param fileName Xml file to serialize the object <var>pluginServizioAzioneCompatibilita</var>
	 * @param pluginServizioAzioneCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita) throws SerializerException {
		this.objToXml(fileName, PluginServizioAzioneCompatibilita.class, pluginServizioAzioneCompatibilita, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>pluginServizioAzioneCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita}
	 * 
	 * @param fileName Xml file to serialize the object <var>pluginServizioAzioneCompatibilita</var>
	 * @param pluginServizioAzioneCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PluginServizioAzioneCompatibilita.class, pluginServizioAzioneCompatibilita, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>pluginServizioAzioneCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita}
	 * 
	 * @param file Xml file to serialize the object <var>pluginServizioAzioneCompatibilita</var>
	 * @param pluginServizioAzioneCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita) throws SerializerException {
		this.objToXml(file, PluginServizioAzioneCompatibilita.class, pluginServizioAzioneCompatibilita, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>pluginServizioAzioneCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita}
	 * 
	 * @param file Xml file to serialize the object <var>pluginServizioAzioneCompatibilita</var>
	 * @param pluginServizioAzioneCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PluginServizioAzioneCompatibilita.class, pluginServizioAzioneCompatibilita, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>pluginServizioAzioneCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita}
	 * 
	 * @param out OutputStream to serialize the object <var>pluginServizioAzioneCompatibilita</var>
	 * @param pluginServizioAzioneCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita) throws SerializerException {
		this.objToXml(out, PluginServizioAzioneCompatibilita.class, pluginServizioAzioneCompatibilita, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>pluginServizioAzioneCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita}
	 * 
	 * @param out OutputStream to serialize the object <var>pluginServizioAzioneCompatibilita</var>
	 * @param pluginServizioAzioneCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PluginServizioAzioneCompatibilita.class, pluginServizioAzioneCompatibilita, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>pluginServizioAzioneCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita}
	 * 
	 * @param pluginServizioAzioneCompatibilita Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita) throws SerializerException {
		return this.objToXml(PluginServizioAzioneCompatibilita.class, pluginServizioAzioneCompatibilita, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>pluginServizioAzioneCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita}
	 * 
	 * @param pluginServizioAzioneCompatibilita Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PluginServizioAzioneCompatibilita.class, pluginServizioAzioneCompatibilita, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>pluginServizioAzioneCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita}
	 * 
	 * @param pluginServizioAzioneCompatibilita Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita) throws SerializerException {
		return this.objToXml(PluginServizioAzioneCompatibilita.class, pluginServizioAzioneCompatibilita, false).toString();
	}
	/**
	 * Serialize to String the object <var>pluginServizioAzioneCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita}
	 * 
	 * @param pluginServizioAzioneCompatibilita Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PluginServizioAzioneCompatibilita.class, pluginServizioAzioneCompatibilita, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-plugin
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdPlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPlugin</var>
	 * @param idPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPlugin idPlugin) throws SerializerException {
		this.objToXml(fileName, IdPlugin.class, idPlugin, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdPlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPlugin</var>
	 * @param idPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPlugin idPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdPlugin.class, idPlugin, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdPlugin}
	 * 
	 * @param file Xml file to serialize the object <var>idPlugin</var>
	 * @param idPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPlugin idPlugin) throws SerializerException {
		this.objToXml(file, IdPlugin.class, idPlugin, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdPlugin}
	 * 
	 * @param file Xml file to serialize the object <var>idPlugin</var>
	 * @param idPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPlugin idPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdPlugin.class, idPlugin, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdPlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>idPlugin</var>
	 * @param idPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPlugin idPlugin) throws SerializerException {
		this.objToXml(out, IdPlugin.class, idPlugin, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdPlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>idPlugin</var>
	 * @param idPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPlugin idPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdPlugin.class, idPlugin, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdPlugin}
	 * 
	 * @param idPlugin Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPlugin idPlugin) throws SerializerException {
		return this.objToXml(IdPlugin.class, idPlugin, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdPlugin}
	 * 
	 * @param idPlugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPlugin idPlugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPlugin.class, idPlugin, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdPlugin}
	 * 
	 * @param idPlugin Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPlugin idPlugin) throws SerializerException {
		return this.objToXml(IdPlugin.class, idPlugin, false).toString();
	}
	/**
	 * Serialize to String the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.IdPlugin}
	 * 
	 * @param idPlugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPlugin idPlugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPlugin.class, idPlugin, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-plugin
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdPlugin</var>
	 * @param elencoIdPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdPlugin elencoIdPlugin) throws SerializerException {
		this.objToXml(fileName, ElencoIdPlugin.class, elencoIdPlugin, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdPlugin</var>
	 * @param elencoIdPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdPlugin elencoIdPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoIdPlugin.class, elencoIdPlugin, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdPlugin</var>
	 * @param elencoIdPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdPlugin elencoIdPlugin) throws SerializerException {
		this.objToXml(file, ElencoIdPlugin.class, elencoIdPlugin, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdPlugin</var>
	 * @param elencoIdPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdPlugin elencoIdPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoIdPlugin.class, elencoIdPlugin, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdPlugin</var>
	 * @param elencoIdPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdPlugin elencoIdPlugin) throws SerializerException {
		this.objToXml(out, ElencoIdPlugin.class, elencoIdPlugin, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdPlugin</var>
	 * @param elencoIdPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdPlugin elencoIdPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoIdPlugin.class, elencoIdPlugin, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoIdPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin}
	 * 
	 * @param elencoIdPlugin Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdPlugin elencoIdPlugin) throws SerializerException {
		return this.objToXml(ElencoIdPlugin.class, elencoIdPlugin, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoIdPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin}
	 * 
	 * @param elencoIdPlugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdPlugin elencoIdPlugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdPlugin.class, elencoIdPlugin, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoIdPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin}
	 * 
	 * @param elencoIdPlugin Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdPlugin elencoIdPlugin) throws SerializerException {
		return this.objToXml(ElencoIdPlugin.class, elencoIdPlugin, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoIdPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdPlugin}
	 * 
	 * @param elencoIdPlugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdPlugin elencoIdPlugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdPlugin.class, elencoIdPlugin, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: plugin-servizio-compatibilita
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>pluginServizioCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita}
	 * 
	 * @param fileName Xml file to serialize the object <var>pluginServizioCompatibilita</var>
	 * @param pluginServizioCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PluginServizioCompatibilita pluginServizioCompatibilita) throws SerializerException {
		this.objToXml(fileName, PluginServizioCompatibilita.class, pluginServizioCompatibilita, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>pluginServizioCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita}
	 * 
	 * @param fileName Xml file to serialize the object <var>pluginServizioCompatibilita</var>
	 * @param pluginServizioCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PluginServizioCompatibilita pluginServizioCompatibilita,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PluginServizioCompatibilita.class, pluginServizioCompatibilita, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>pluginServizioCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita}
	 * 
	 * @param file Xml file to serialize the object <var>pluginServizioCompatibilita</var>
	 * @param pluginServizioCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PluginServizioCompatibilita pluginServizioCompatibilita) throws SerializerException {
		this.objToXml(file, PluginServizioCompatibilita.class, pluginServizioCompatibilita, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>pluginServizioCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita}
	 * 
	 * @param file Xml file to serialize the object <var>pluginServizioCompatibilita</var>
	 * @param pluginServizioCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PluginServizioCompatibilita pluginServizioCompatibilita,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PluginServizioCompatibilita.class, pluginServizioCompatibilita, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>pluginServizioCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita}
	 * 
	 * @param out OutputStream to serialize the object <var>pluginServizioCompatibilita</var>
	 * @param pluginServizioCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PluginServizioCompatibilita pluginServizioCompatibilita) throws SerializerException {
		this.objToXml(out, PluginServizioCompatibilita.class, pluginServizioCompatibilita, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>pluginServizioCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita}
	 * 
	 * @param out OutputStream to serialize the object <var>pluginServizioCompatibilita</var>
	 * @param pluginServizioCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PluginServizioCompatibilita pluginServizioCompatibilita,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PluginServizioCompatibilita.class, pluginServizioCompatibilita, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>pluginServizioCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita}
	 * 
	 * @param pluginServizioCompatibilita Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PluginServizioCompatibilita pluginServizioCompatibilita) throws SerializerException {
		return this.objToXml(PluginServizioCompatibilita.class, pluginServizioCompatibilita, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>pluginServizioCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita}
	 * 
	 * @param pluginServizioCompatibilita Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PluginServizioCompatibilita pluginServizioCompatibilita,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PluginServizioCompatibilita.class, pluginServizioCompatibilita, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>pluginServizioCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita}
	 * 
	 * @param pluginServizioCompatibilita Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PluginServizioCompatibilita pluginServizioCompatibilita) throws SerializerException {
		return this.objToXml(PluginServizioCompatibilita.class, pluginServizioCompatibilita, false).toString();
	}
	/**
	 * Serialize to String the object <var>pluginServizioCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita}
	 * 
	 * @param pluginServizioCompatibilita Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PluginServizioCompatibilita pluginServizioCompatibilita,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PluginServizioCompatibilita.class, pluginServizioCompatibilita, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: plugin-proprieta-compatibilita
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>pluginProprietaCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita}
	 * 
	 * @param fileName Xml file to serialize the object <var>pluginProprietaCompatibilita</var>
	 * @param pluginProprietaCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PluginProprietaCompatibilita pluginProprietaCompatibilita) throws SerializerException {
		this.objToXml(fileName, PluginProprietaCompatibilita.class, pluginProprietaCompatibilita, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>pluginProprietaCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita}
	 * 
	 * @param fileName Xml file to serialize the object <var>pluginProprietaCompatibilita</var>
	 * @param pluginProprietaCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PluginProprietaCompatibilita pluginProprietaCompatibilita,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PluginProprietaCompatibilita.class, pluginProprietaCompatibilita, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>pluginProprietaCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita}
	 * 
	 * @param file Xml file to serialize the object <var>pluginProprietaCompatibilita</var>
	 * @param pluginProprietaCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PluginProprietaCompatibilita pluginProprietaCompatibilita) throws SerializerException {
		this.objToXml(file, PluginProprietaCompatibilita.class, pluginProprietaCompatibilita, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>pluginProprietaCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita}
	 * 
	 * @param file Xml file to serialize the object <var>pluginProprietaCompatibilita</var>
	 * @param pluginProprietaCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PluginProprietaCompatibilita pluginProprietaCompatibilita,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PluginProprietaCompatibilita.class, pluginProprietaCompatibilita, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>pluginProprietaCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita}
	 * 
	 * @param out OutputStream to serialize the object <var>pluginProprietaCompatibilita</var>
	 * @param pluginProprietaCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PluginProprietaCompatibilita pluginProprietaCompatibilita) throws SerializerException {
		this.objToXml(out, PluginProprietaCompatibilita.class, pluginProprietaCompatibilita, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>pluginProprietaCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita}
	 * 
	 * @param out OutputStream to serialize the object <var>pluginProprietaCompatibilita</var>
	 * @param pluginProprietaCompatibilita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PluginProprietaCompatibilita pluginProprietaCompatibilita,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PluginProprietaCompatibilita.class, pluginProprietaCompatibilita, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>pluginProprietaCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita}
	 * 
	 * @param pluginProprietaCompatibilita Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PluginProprietaCompatibilita pluginProprietaCompatibilita) throws SerializerException {
		return this.objToXml(PluginProprietaCompatibilita.class, pluginProprietaCompatibilita, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>pluginProprietaCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita}
	 * 
	 * @param pluginProprietaCompatibilita Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PluginProprietaCompatibilita pluginProprietaCompatibilita,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PluginProprietaCompatibilita.class, pluginProprietaCompatibilita, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>pluginProprietaCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita}
	 * 
	 * @param pluginProprietaCompatibilita Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PluginProprietaCompatibilita pluginProprietaCompatibilita) throws SerializerException {
		return this.objToXml(PluginProprietaCompatibilita.class, pluginProprietaCompatibilita, false).toString();
	}
	/**
	 * Serialize to String the object <var>pluginProprietaCompatibilita</var> of type {@link org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita}
	 * 
	 * @param pluginProprietaCompatibilita Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PluginProprietaCompatibilita pluginProprietaCompatibilita,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PluginProprietaCompatibilita.class, pluginProprietaCompatibilita, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdConfigurazioneServizio</var>
	 * @param elencoIdConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdConfigurazioneServizio elencoIdConfigurazioneServizio) throws SerializerException {
		this.objToXml(fileName, ElencoIdConfigurazioneServizio.class, elencoIdConfigurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdConfigurazioneServizio</var>
	 * @param elencoIdConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdConfigurazioneServizio elencoIdConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoIdConfigurazioneServizio.class, elencoIdConfigurazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdConfigurazioneServizio</var>
	 * @param elencoIdConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdConfigurazioneServizio elencoIdConfigurazioneServizio) throws SerializerException {
		this.objToXml(file, ElencoIdConfigurazioneServizio.class, elencoIdConfigurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdConfigurazioneServizio</var>
	 * @param elencoIdConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdConfigurazioneServizio elencoIdConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoIdConfigurazioneServizio.class, elencoIdConfigurazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdConfigurazioneServizio</var>
	 * @param elencoIdConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdConfigurazioneServizio elencoIdConfigurazioneServizio) throws SerializerException {
		this.objToXml(out, ElencoIdConfigurazioneServizio.class, elencoIdConfigurazioneServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdConfigurazioneServizio</var>
	 * @param elencoIdConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdConfigurazioneServizio elencoIdConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoIdConfigurazioneServizio.class, elencoIdConfigurazioneServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoIdConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio}
	 * 
	 * @param elencoIdConfigurazioneServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdConfigurazioneServizio elencoIdConfigurazioneServizio) throws SerializerException {
		return this.objToXml(ElencoIdConfigurazioneServizio.class, elencoIdConfigurazioneServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoIdConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio}
	 * 
	 * @param elencoIdConfigurazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdConfigurazioneServizio elencoIdConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdConfigurazioneServizio.class, elencoIdConfigurazioneServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoIdConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio}
	 * 
	 * @param elencoIdConfigurazioneServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdConfigurazioneServizio elencoIdConfigurazioneServizio) throws SerializerException {
		return this.objToXml(ElencoIdConfigurazioneServizio.class, elencoIdConfigurazioneServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoIdConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoIdConfigurazioneServizio}
	 * 
	 * @param elencoIdConfigurazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdConfigurazioneServizio elencoIdConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdConfigurazioneServizio.class, elencoIdConfigurazioneServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoConfigurazioneServizio</var>
	 * @param elencoConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoConfigurazioneServizio elencoConfigurazioneServizio) throws SerializerException {
		this.objToXml(fileName, ElencoConfigurazioneServizio.class, elencoConfigurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoConfigurazioneServizio</var>
	 * @param elencoConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoConfigurazioneServizio elencoConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoConfigurazioneServizio.class, elencoConfigurazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>elencoConfigurazioneServizio</var>
	 * @param elencoConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoConfigurazioneServizio elencoConfigurazioneServizio) throws SerializerException {
		this.objToXml(file, ElencoConfigurazioneServizio.class, elencoConfigurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>elencoConfigurazioneServizio</var>
	 * @param elencoConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoConfigurazioneServizio elencoConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoConfigurazioneServizio.class, elencoConfigurazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoConfigurazioneServizio</var>
	 * @param elencoConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoConfigurazioneServizio elencoConfigurazioneServizio) throws SerializerException {
		this.objToXml(out, ElencoConfigurazioneServizio.class, elencoConfigurazioneServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoConfigurazioneServizio</var>
	 * @param elencoConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoConfigurazioneServizio elencoConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoConfigurazioneServizio.class, elencoConfigurazioneServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio}
	 * 
	 * @param elencoConfigurazioneServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoConfigurazioneServizio elencoConfigurazioneServizio) throws SerializerException {
		return this.objToXml(ElencoConfigurazioneServizio.class, elencoConfigurazioneServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio}
	 * 
	 * @param elencoConfigurazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoConfigurazioneServizio elencoConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoConfigurazioneServizio.class, elencoConfigurazioneServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio}
	 * 
	 * @param elencoConfigurazioneServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoConfigurazioneServizio elencoConfigurazioneServizio) throws SerializerException {
		return this.objToXml(ElencoConfigurazioneServizio.class, elencoConfigurazioneServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.base.ElencoConfigurazioneServizio}
	 * 
	 * @param elencoConfigurazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoConfigurazioneServizio elencoConfigurazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoConfigurazioneServizio.class, elencoConfigurazioneServizio, prettyPrint).toString();
	}
	
	
	

}
