/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

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
			}catch(Exception e){
				// ignore
			}
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
			}catch(Exception e){
				// ignore
			}
			try{
				fout.close();
			}catch(Exception e){
				// ignore
			}
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
			}catch(Exception e){
				// ignore
			}
			try{
				bout.close();
			}catch(Exception e){
				// ignore
			}
		}
		return bout;
	}




	/*
	 =================================================================================
	 Object: id-configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneServizioAzione</var>
	 * @param idConfigurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
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
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneServizioAzione</var>
	 * @param idConfigurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws SerializerException {
		this.objToXml(file, IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
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
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneServizioAzione</var>
	 * @param idConfigurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws SerializerException {
		this.objToXml(out, IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
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
	 * Serialize to byte array the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * 
	 * @param idConfigurazioneServizioAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
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
	 * Serialize to String the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * 
	 * @param idConfigurazioneServizioAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizioAzione.class, idConfigurazioneServizioAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>idConfigurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
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
	 Object: id-configurazione-transazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneTransazione</var>
	 * @param idConfigurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneTransazione idConfigurazioneTransazione) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneTransazione.class, idConfigurazioneTransazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneTransazione</var>
	 * @param idConfigurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneTransazione idConfigurazioneTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneTransazione.class, idConfigurazioneTransazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneTransazione</var>
	 * @param idConfigurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneTransazione idConfigurazioneTransazione) throws SerializerException {
		this.objToXml(file, IdConfigurazioneTransazione.class, idConfigurazioneTransazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneTransazione</var>
	 * @param idConfigurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneTransazione idConfigurazioneTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdConfigurazioneTransazione.class, idConfigurazioneTransazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneTransazione</var>
	 * @param idConfigurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneTransazione idConfigurazioneTransazione) throws SerializerException {
		this.objToXml(out, IdConfigurazioneTransazione.class, idConfigurazioneTransazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneTransazione</var>
	 * @param idConfigurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneTransazione idConfigurazioneTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdConfigurazioneTransazione.class, idConfigurazioneTransazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idConfigurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param idConfigurazioneTransazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneTransazione idConfigurazioneTransazione) throws SerializerException {
		return this.objToXml(IdConfigurazioneTransazione.class, idConfigurazioneTransazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idConfigurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param idConfigurazioneTransazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneTransazione idConfigurazioneTransazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdConfigurazioneTransazione.class, idConfigurazioneTransazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idConfigurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param idConfigurazioneTransazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneTransazione idConfigurazioneTransazione) throws SerializerException {
		return this.objToXml(IdConfigurazioneTransazione.class, idConfigurazioneTransazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>idConfigurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param idConfigurazioneTransazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneTransazione idConfigurazioneTransazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdConfigurazioneTransazione.class, idConfigurazioneTransazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: info-plugin
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>infoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>infoPlugin</var>
	 * @param infoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InfoPlugin infoPlugin) throws SerializerException {
		this.objToXml(fileName, InfoPlugin.class, infoPlugin, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>infoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>infoPlugin</var>
	 * @param infoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InfoPlugin infoPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InfoPlugin.class, infoPlugin, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>infoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param file Xml file to serialize the object <var>infoPlugin</var>
	 * @param infoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InfoPlugin infoPlugin) throws SerializerException {
		this.objToXml(file, InfoPlugin.class, infoPlugin, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>infoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param file Xml file to serialize the object <var>infoPlugin</var>
	 * @param infoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InfoPlugin infoPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InfoPlugin.class, infoPlugin, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>infoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>infoPlugin</var>
	 * @param infoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InfoPlugin infoPlugin) throws SerializerException {
		this.objToXml(out, InfoPlugin.class, infoPlugin, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>infoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>infoPlugin</var>
	 * @param infoPlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InfoPlugin infoPlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InfoPlugin.class, infoPlugin, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>infoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param infoPlugin Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InfoPlugin infoPlugin) throws SerializerException {
		return this.objToXml(InfoPlugin.class, infoPlugin, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>infoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param infoPlugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InfoPlugin infoPlugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InfoPlugin.class, infoPlugin, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>infoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param infoPlugin Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InfoPlugin infoPlugin) throws SerializerException {
		return this.objToXml(InfoPlugin.class, infoPlugin, false).toString();
	}
	/**
	 * Serialize to String the object <var>infoPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param infoPlugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InfoPlugin infoPlugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InfoPlugin.class, infoPlugin, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
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
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		this.objToXml(file, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
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
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		this.objToXml(out, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
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
	 * Serialize to byte array the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * 
	 * @param configurazioneServizioAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		return this.objToXml(ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
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
	 * Serialize to String the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * 
	 * @param configurazioneServizioAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		return this.objToXml(ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
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
	 Object: configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneServizio.class, configurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
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
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		this.objToXml(file, ConfigurazioneServizio.class, configurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
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
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		this.objToXml(out, ConfigurazioneServizio.class, configurazioneServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
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
	 * Serialize to byte array the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * 
	 * @param configurazioneServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		return this.objToXml(ConfigurazioneServizio.class, configurazioneServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
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
	 * Serialize to String the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * 
	 * @param configurazioneServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		return this.objToXml(ConfigurazioneServizio.class, configurazioneServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
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
	 Object: id-configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneServizio</var>
	 * @param idConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneServizio idConfigurazioneServizio) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneServizio.class, idConfigurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
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
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneServizio</var>
	 * @param idConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneServizio idConfigurazioneServizio) throws SerializerException {
		this.objToXml(file, IdConfigurazioneServizio.class, idConfigurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
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
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneServizio</var>
	 * @param idConfigurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneServizio idConfigurazioneServizio) throws SerializerException {
		this.objToXml(out, IdConfigurazioneServizio.class, idConfigurazioneServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
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
	 * Serialize to byte array the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * 
	 * @param idConfigurazioneServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneServizio idConfigurazioneServizio) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizio.class, idConfigurazioneServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
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
	 * Serialize to String the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * 
	 * @param idConfigurazioneServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneServizio idConfigurazioneServizio) throws SerializerException {
		return this.objToXml(IdConfigurazioneServizio.class, idConfigurazioneServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>idConfigurazioneServizio</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
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
	 Object: configurazione-transazione-risorsa-contenuto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneTransazioneRisorsaContenuto</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneTransazioneRisorsaContenuto</var>
	 * @param configurazioneTransazioneRisorsaContenuto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneTransazioneRisorsaContenuto.class, configurazioneTransazioneRisorsaContenuto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneTransazioneRisorsaContenuto</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneTransazioneRisorsaContenuto</var>
	 * @param configurazioneTransazioneRisorsaContenuto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneTransazioneRisorsaContenuto.class, configurazioneTransazioneRisorsaContenuto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneTransazioneRisorsaContenuto</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneTransazioneRisorsaContenuto</var>
	 * @param configurazioneTransazioneRisorsaContenuto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto) throws SerializerException {
		this.objToXml(file, ConfigurazioneTransazioneRisorsaContenuto.class, configurazioneTransazioneRisorsaContenuto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneTransazioneRisorsaContenuto</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneTransazioneRisorsaContenuto</var>
	 * @param configurazioneTransazioneRisorsaContenuto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneTransazioneRisorsaContenuto.class, configurazioneTransazioneRisorsaContenuto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneTransazioneRisorsaContenuto</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneTransazioneRisorsaContenuto</var>
	 * @param configurazioneTransazioneRisorsaContenuto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto) throws SerializerException {
		this.objToXml(out, ConfigurazioneTransazioneRisorsaContenuto.class, configurazioneTransazioneRisorsaContenuto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneTransazioneRisorsaContenuto</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneTransazioneRisorsaContenuto</var>
	 * @param configurazioneTransazioneRisorsaContenuto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneTransazioneRisorsaContenuto.class, configurazioneTransazioneRisorsaContenuto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneTransazioneRisorsaContenuto</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param configurazioneTransazioneRisorsaContenuto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazioneRisorsaContenuto.class, configurazioneTransazioneRisorsaContenuto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneTransazioneRisorsaContenuto</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param configurazioneTransazioneRisorsaContenuto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazioneRisorsaContenuto.class, configurazioneTransazioneRisorsaContenuto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneTransazioneRisorsaContenuto</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param configurazioneTransazioneRisorsaContenuto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazioneRisorsaContenuto.class, configurazioneTransazioneRisorsaContenuto, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneTransazioneRisorsaContenuto</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param configurazioneTransazioneRisorsaContenuto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazioneRisorsaContenuto.class, configurazioneTransazioneRisorsaContenuto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-transazione-stato
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneTransazioneStato</var>
	 * @param idConfigurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneTransazioneStato.class, idConfigurazioneTransazioneStato, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idConfigurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param fileName Xml file to serialize the object <var>idConfigurazioneTransazioneStato</var>
	 * @param idConfigurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdConfigurazioneTransazioneStato.class, idConfigurazioneTransazioneStato, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneTransazioneStato</var>
	 * @param idConfigurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato) throws SerializerException {
		this.objToXml(file, IdConfigurazioneTransazioneStato.class, idConfigurazioneTransazioneStato, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idConfigurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param file Xml file to serialize the object <var>idConfigurazioneTransazioneStato</var>
	 * @param idConfigurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdConfigurazioneTransazioneStato.class, idConfigurazioneTransazioneStato, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneTransazioneStato</var>
	 * @param idConfigurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato) throws SerializerException {
		this.objToXml(out, IdConfigurazioneTransazioneStato.class, idConfigurazioneTransazioneStato, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idConfigurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param out OutputStream to serialize the object <var>idConfigurazioneTransazioneStato</var>
	 * @param idConfigurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdConfigurazioneTransazioneStato.class, idConfigurazioneTransazioneStato, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idConfigurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param idConfigurazioneTransazioneStato Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato) throws SerializerException {
		return this.objToXml(IdConfigurazioneTransazioneStato.class, idConfigurazioneTransazioneStato, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idConfigurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param idConfigurazioneTransazioneStato Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdConfigurazioneTransazioneStato.class, idConfigurazioneTransazioneStato, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idConfigurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param idConfigurazioneTransazioneStato Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato) throws SerializerException {
		return this.objToXml(IdConfigurazioneTransazioneStato.class, idConfigurazioneTransazioneStato, false).toString();
	}
	/**
	 * Serialize to String the object <var>idConfigurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param idConfigurazioneTransazioneStato Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdConfigurazioneTransazioneStato.class, idConfigurazioneTransazioneStato, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-transazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneTransazione</var>
	 * @param configurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneTransazione configurazioneTransazione) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneTransazione.class, configurazioneTransazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneTransazione</var>
	 * @param configurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneTransazione configurazioneTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneTransazione.class, configurazioneTransazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneTransazione</var>
	 * @param configurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneTransazione configurazioneTransazione) throws SerializerException {
		this.objToXml(file, ConfigurazioneTransazione.class, configurazioneTransazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneTransazione</var>
	 * @param configurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneTransazione configurazioneTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneTransazione.class, configurazioneTransazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneTransazione</var>
	 * @param configurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneTransazione configurazioneTransazione) throws SerializerException {
		this.objToXml(out, ConfigurazioneTransazione.class, configurazioneTransazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneTransazione</var>
	 * @param configurazioneTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneTransazione configurazioneTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneTransazione.class, configurazioneTransazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param configurazioneTransazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneTransazione configurazioneTransazione) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazione.class, configurazioneTransazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param configurazioneTransazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneTransazione configurazioneTransazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazione.class, configurazioneTransazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param configurazioneTransazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneTransazione configurazioneTransazione) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazione.class, configurazioneTransazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneTransazione</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param configurazioneTransazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneTransazione configurazioneTransazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazione.class, configurazioneTransazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-transazione-plugin
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneTransazionePlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneTransazionePlugin</var>
	 * @param configurazioneTransazionePlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneTransazionePlugin configurazioneTransazionePlugin) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneTransazionePlugin.class, configurazioneTransazionePlugin, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneTransazionePlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneTransazionePlugin</var>
	 * @param configurazioneTransazionePlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneTransazionePlugin configurazioneTransazionePlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneTransazionePlugin.class, configurazioneTransazionePlugin, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneTransazionePlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneTransazionePlugin</var>
	 * @param configurazioneTransazionePlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneTransazionePlugin configurazioneTransazionePlugin) throws SerializerException {
		this.objToXml(file, ConfigurazioneTransazionePlugin.class, configurazioneTransazionePlugin, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneTransazionePlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneTransazionePlugin</var>
	 * @param configurazioneTransazionePlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneTransazionePlugin configurazioneTransazionePlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneTransazionePlugin.class, configurazioneTransazionePlugin, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneTransazionePlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneTransazionePlugin</var>
	 * @param configurazioneTransazionePlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneTransazionePlugin configurazioneTransazionePlugin) throws SerializerException {
		this.objToXml(out, ConfigurazioneTransazionePlugin.class, configurazioneTransazionePlugin, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneTransazionePlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneTransazionePlugin</var>
	 * @param configurazioneTransazionePlugin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneTransazionePlugin configurazioneTransazionePlugin,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneTransazionePlugin.class, configurazioneTransazionePlugin, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneTransazionePlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param configurazioneTransazionePlugin Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneTransazionePlugin configurazioneTransazionePlugin) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazionePlugin.class, configurazioneTransazionePlugin, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneTransazionePlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param configurazioneTransazionePlugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneTransazionePlugin configurazioneTransazionePlugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazionePlugin.class, configurazioneTransazionePlugin, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneTransazionePlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param configurazioneTransazionePlugin Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneTransazionePlugin configurazioneTransazionePlugin) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazionePlugin.class, configurazioneTransazionePlugin, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneTransazionePlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param configurazioneTransazionePlugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneTransazionePlugin configurazioneTransazionePlugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazionePlugin.class, configurazioneTransazionePlugin, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-transazione-stato
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneTransazioneStato</var>
	 * @param configurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneTransazioneStato configurazioneTransazioneStato) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneTransazioneStato.class, configurazioneTransazioneStato, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneTransazioneStato</var>
	 * @param configurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneTransazioneStato configurazioneTransazioneStato,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneTransazioneStato.class, configurazioneTransazioneStato, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneTransazioneStato</var>
	 * @param configurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneTransazioneStato configurazioneTransazioneStato) throws SerializerException {
		this.objToXml(file, ConfigurazioneTransazioneStato.class, configurazioneTransazioneStato, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneTransazioneStato</var>
	 * @param configurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneTransazioneStato configurazioneTransazioneStato,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneTransazioneStato.class, configurazioneTransazioneStato, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneTransazioneStato</var>
	 * @param configurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneTransazioneStato configurazioneTransazioneStato) throws SerializerException {
		this.objToXml(out, ConfigurazioneTransazioneStato.class, configurazioneTransazioneStato, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneTransazioneStato</var>
	 * @param configurazioneTransazioneStato Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneTransazioneStato configurazioneTransazioneStato,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneTransazioneStato.class, configurazioneTransazioneStato, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param configurazioneTransazioneStato Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneTransazioneStato configurazioneTransazioneStato) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazioneStato.class, configurazioneTransazioneStato, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param configurazioneTransazioneStato Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneTransazioneStato configurazioneTransazioneStato,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazioneStato.class, configurazioneTransazioneStato, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param configurazioneTransazioneStato Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneTransazioneStato configurazioneTransazioneStato) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazioneStato.class, configurazioneTransazioneStato, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneTransazioneStato</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param configurazioneTransazioneStato Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneTransazioneStato configurazioneTransazioneStato,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneTransazioneStato.class, configurazioneTransazioneStato, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-plugin
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPlugin</var>
	 * @param idPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPlugin idPlugin) throws SerializerException {
		this.objToXml(fileName, IdPlugin.class, idPlugin, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
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
	 * Serialize to file system in <var>file</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * 
	 * @param file Xml file to serialize the object <var>idPlugin</var>
	 * @param idPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPlugin idPlugin) throws SerializerException {
		this.objToXml(file, IdPlugin.class, idPlugin, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
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
	 * Serialize to output stream <var>out</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * 
	 * @param out OutputStream to serialize the object <var>idPlugin</var>
	 * @param idPlugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPlugin idPlugin) throws SerializerException {
		this.objToXml(out, IdPlugin.class, idPlugin, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
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
	 * Serialize to byte array the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * 
	 * @param idPlugin Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPlugin idPlugin) throws SerializerException {
		return this.objToXml(IdPlugin.class, idPlugin, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
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
	 * Serialize to String the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * 
	 * @param idPlugin Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPlugin idPlugin) throws SerializerException {
		return this.objToXml(IdPlugin.class, idPlugin, false).toString();
	}
	/**
	 * Serialize to String the object <var>idPlugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
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
	 Object: plugin
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param fileName Xml file to serialize the object <var>plugin</var>
	 * @param plugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Plugin plugin) throws SerializerException {
		this.objToXml(fileName, Plugin.class, plugin, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
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
	 * Serialize to file system in <var>file</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param file Xml file to serialize the object <var>plugin</var>
	 * @param plugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Plugin plugin) throws SerializerException {
		this.objToXml(file, Plugin.class, plugin, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
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
	 * Serialize to output stream <var>out</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param out OutputStream to serialize the object <var>plugin</var>
	 * @param plugin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Plugin plugin) throws SerializerException {
		this.objToXml(out, Plugin.class, plugin, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
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
	 * Serialize to byte array the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param plugin Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Plugin plugin) throws SerializerException {
		return this.objToXml(Plugin.class, plugin, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
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
	 * Serialize to String the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param plugin Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Plugin plugin) throws SerializerException {
		return this.objToXml(Plugin.class, plugin, false).toString();
	}
	/**
	 * Serialize to String the object <var>plugin</var> of type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param plugin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Plugin plugin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Plugin.class, plugin, prettyPrint).toString();
	}
	
	
	

}
