/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.pdd.monitor.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.pdd.monitor.BustaSoggetto;
import org.openspcoop2.pdd.monitor.Busta;
import org.openspcoop2.pdd.monitor.BustaServizio;
import org.openspcoop2.pdd.monitor.StatoPdd;
import org.openspcoop2.pdd.monitor.Openspcoop2;
import org.openspcoop2.pdd.monitor.Messaggio;
import org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna;
import org.openspcoop2.pdd.monitor.Dettaglio;
import org.openspcoop2.pdd.monitor.Filtro;
import org.openspcoop2.pdd.monitor.Proprieta;

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
	 Object: busta-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>bustaSoggetto</var> of type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>bustaSoggetto</var>
	 * @param bustaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,BustaSoggetto bustaSoggetto) throws SerializerException {
		this.objToXml(fileName, BustaSoggetto.class, bustaSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>bustaSoggetto</var> of type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>bustaSoggetto</var>
	 * @param bustaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,BustaSoggetto bustaSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, BustaSoggetto.class, bustaSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>bustaSoggetto</var> of type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>bustaSoggetto</var>
	 * @param bustaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,BustaSoggetto bustaSoggetto) throws SerializerException {
		this.objToXml(file, BustaSoggetto.class, bustaSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>bustaSoggetto</var> of type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>bustaSoggetto</var>
	 * @param bustaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,BustaSoggetto bustaSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, BustaSoggetto.class, bustaSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>bustaSoggetto</var> of type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>bustaSoggetto</var>
	 * @param bustaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,BustaSoggetto bustaSoggetto) throws SerializerException {
		this.objToXml(out, BustaSoggetto.class, bustaSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>bustaSoggetto</var> of type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>bustaSoggetto</var>
	 * @param bustaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,BustaSoggetto bustaSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, BustaSoggetto.class, bustaSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>bustaSoggetto</var> of type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param bustaSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(BustaSoggetto bustaSoggetto) throws SerializerException {
		return this.objToXml(BustaSoggetto.class, bustaSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>bustaSoggetto</var> of type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param bustaSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(BustaSoggetto bustaSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(BustaSoggetto.class, bustaSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>bustaSoggetto</var> of type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param bustaSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(BustaSoggetto bustaSoggetto) throws SerializerException {
		return this.objToXml(BustaSoggetto.class, bustaSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>bustaSoggetto</var> of type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param bustaSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(BustaSoggetto bustaSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(BustaSoggetto.class, bustaSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: busta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>busta</var> of type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param fileName Xml file to serialize the object <var>busta</var>
	 * @param busta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Busta busta) throws SerializerException {
		this.objToXml(fileName, Busta.class, busta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>busta</var> of type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param fileName Xml file to serialize the object <var>busta</var>
	 * @param busta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Busta busta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Busta.class, busta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>busta</var> of type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param file Xml file to serialize the object <var>busta</var>
	 * @param busta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Busta busta) throws SerializerException {
		this.objToXml(file, Busta.class, busta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>busta</var> of type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param file Xml file to serialize the object <var>busta</var>
	 * @param busta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Busta busta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Busta.class, busta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>busta</var> of type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param out OutputStream to serialize the object <var>busta</var>
	 * @param busta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Busta busta) throws SerializerException {
		this.objToXml(out, Busta.class, busta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>busta</var> of type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param out OutputStream to serialize the object <var>busta</var>
	 * @param busta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Busta busta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Busta.class, busta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>busta</var> of type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param busta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Busta busta) throws SerializerException {
		return this.objToXml(Busta.class, busta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>busta</var> of type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param busta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Busta busta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Busta.class, busta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>busta</var> of type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param busta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Busta busta) throws SerializerException {
		return this.objToXml(Busta.class, busta, false).toString();
	}
	/**
	 * Serialize to String the object <var>busta</var> of type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param busta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Busta busta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Busta.class, busta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: busta-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>bustaServizio</var> of type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>bustaServizio</var>
	 * @param bustaServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,BustaServizio bustaServizio) throws SerializerException {
		this.objToXml(fileName, BustaServizio.class, bustaServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>bustaServizio</var> of type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>bustaServizio</var>
	 * @param bustaServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,BustaServizio bustaServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, BustaServizio.class, bustaServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>bustaServizio</var> of type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param file Xml file to serialize the object <var>bustaServizio</var>
	 * @param bustaServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,BustaServizio bustaServizio) throws SerializerException {
		this.objToXml(file, BustaServizio.class, bustaServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>bustaServizio</var> of type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param file Xml file to serialize the object <var>bustaServizio</var>
	 * @param bustaServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,BustaServizio bustaServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, BustaServizio.class, bustaServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>bustaServizio</var> of type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>bustaServizio</var>
	 * @param bustaServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,BustaServizio bustaServizio) throws SerializerException {
		this.objToXml(out, BustaServizio.class, bustaServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>bustaServizio</var> of type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>bustaServizio</var>
	 * @param bustaServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,BustaServizio bustaServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, BustaServizio.class, bustaServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>bustaServizio</var> of type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param bustaServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(BustaServizio bustaServizio) throws SerializerException {
		return this.objToXml(BustaServizio.class, bustaServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>bustaServizio</var> of type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param bustaServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(BustaServizio bustaServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(BustaServizio.class, bustaServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>bustaServizio</var> of type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param bustaServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(BustaServizio bustaServizio) throws SerializerException {
		return this.objToXml(BustaServizio.class, bustaServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>bustaServizio</var> of type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param bustaServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(BustaServizio bustaServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(BustaServizio.class, bustaServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: stato-pdd
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statoPdd</var> of type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param fileName Xml file to serialize the object <var>statoPdd</var>
	 * @param statoPdd Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatoPdd statoPdd) throws SerializerException {
		this.objToXml(fileName, StatoPdd.class, statoPdd, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statoPdd</var> of type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param fileName Xml file to serialize the object <var>statoPdd</var>
	 * @param statoPdd Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatoPdd statoPdd,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatoPdd.class, statoPdd, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statoPdd</var> of type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param file Xml file to serialize the object <var>statoPdd</var>
	 * @param statoPdd Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatoPdd statoPdd) throws SerializerException {
		this.objToXml(file, StatoPdd.class, statoPdd, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statoPdd</var> of type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param file Xml file to serialize the object <var>statoPdd</var>
	 * @param statoPdd Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatoPdd statoPdd,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatoPdd.class, statoPdd, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statoPdd</var> of type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param out OutputStream to serialize the object <var>statoPdd</var>
	 * @param statoPdd Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatoPdd statoPdd) throws SerializerException {
		this.objToXml(out, StatoPdd.class, statoPdd, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statoPdd</var> of type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param out OutputStream to serialize the object <var>statoPdd</var>
	 * @param statoPdd Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatoPdd statoPdd,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatoPdd.class, statoPdd, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statoPdd</var> of type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param statoPdd Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatoPdd statoPdd) throws SerializerException {
		return this.objToXml(StatoPdd.class, statoPdd, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statoPdd</var> of type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param statoPdd Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatoPdd statoPdd,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatoPdd.class, statoPdd, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statoPdd</var> of type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param statoPdd Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatoPdd statoPdd) throws SerializerException {
		return this.objToXml(StatoPdd.class, statoPdd, false).toString();
	}
	/**
	 * Serialize to String the object <var>statoPdd</var> of type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param statoPdd Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatoPdd statoPdd,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatoPdd.class, statoPdd, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(fileName, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Openspcoop2.class, openspcoop2, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(file, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Openspcoop2.class, openspcoop2, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(out, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Openspcoop2.class, openspcoop2, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>openspcoop2</var> of type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Openspcoop2 openspcoop2) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>openspcoop2</var> of type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>openspcoop2</var> of type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Openspcoop2 openspcoop2) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, false).toString();
	}
	/**
	 * Serialize to String the object <var>openspcoop2</var> of type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: messaggio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messaggio</var> of type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param fileName Xml file to serialize the object <var>messaggio</var>
	 * @param messaggio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Messaggio messaggio) throws SerializerException {
		this.objToXml(fileName, Messaggio.class, messaggio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messaggio</var> of type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param fileName Xml file to serialize the object <var>messaggio</var>
	 * @param messaggio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Messaggio messaggio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Messaggio.class, messaggio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messaggio</var> of type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param file Xml file to serialize the object <var>messaggio</var>
	 * @param messaggio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Messaggio messaggio) throws SerializerException {
		this.objToXml(file, Messaggio.class, messaggio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messaggio</var> of type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param file Xml file to serialize the object <var>messaggio</var>
	 * @param messaggio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Messaggio messaggio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Messaggio.class, messaggio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messaggio</var> of type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param out OutputStream to serialize the object <var>messaggio</var>
	 * @param messaggio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Messaggio messaggio) throws SerializerException {
		this.objToXml(out, Messaggio.class, messaggio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messaggio</var> of type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param out OutputStream to serialize the object <var>messaggio</var>
	 * @param messaggio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Messaggio messaggio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Messaggio.class, messaggio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messaggio</var> of type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param messaggio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Messaggio messaggio) throws SerializerException {
		return this.objToXml(Messaggio.class, messaggio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messaggio</var> of type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param messaggio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Messaggio messaggio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Messaggio.class, messaggio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messaggio</var> of type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param messaggio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Messaggio messaggio) throws SerializerException {
		return this.objToXml(Messaggio.class, messaggio, false).toString();
	}
	/**
	 * Serialize to String the object <var>messaggio</var> of type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param messaggio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Messaggio messaggio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Messaggio.class, messaggio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo-consegna
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativoConsegna</var> of type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativoConsegna</var>
	 * @param servizioApplicativoConsegna Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativoConsegna servizioApplicativoConsegna) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativoConsegna.class, servizioApplicativoConsegna, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativoConsegna</var> of type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativoConsegna</var>
	 * @param servizioApplicativoConsegna Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativoConsegna servizioApplicativoConsegna,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativoConsegna.class, servizioApplicativoConsegna, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativoConsegna</var> of type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativoConsegna</var>
	 * @param servizioApplicativoConsegna Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativoConsegna servizioApplicativoConsegna) throws SerializerException {
		this.objToXml(file, ServizioApplicativoConsegna.class, servizioApplicativoConsegna, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativoConsegna</var> of type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativoConsegna</var>
	 * @param servizioApplicativoConsegna Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativoConsegna servizioApplicativoConsegna,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServizioApplicativoConsegna.class, servizioApplicativoConsegna, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativoConsegna</var> of type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativoConsegna</var>
	 * @param servizioApplicativoConsegna Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativoConsegna servizioApplicativoConsegna) throws SerializerException {
		this.objToXml(out, ServizioApplicativoConsegna.class, servizioApplicativoConsegna, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativoConsegna</var> of type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativoConsegna</var>
	 * @param servizioApplicativoConsegna Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativoConsegna servizioApplicativoConsegna,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServizioApplicativoConsegna.class, servizioApplicativoConsegna, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizioApplicativoConsegna</var> of type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param servizioApplicativoConsegna Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativoConsegna servizioApplicativoConsegna) throws SerializerException {
		return this.objToXml(ServizioApplicativoConsegna.class, servizioApplicativoConsegna, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizioApplicativoConsegna</var> of type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param servizioApplicativoConsegna Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativoConsegna servizioApplicativoConsegna,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativoConsegna.class, servizioApplicativoConsegna, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizioApplicativoConsegna</var> of type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param servizioApplicativoConsegna Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativoConsegna servizioApplicativoConsegna) throws SerializerException {
		return this.objToXml(ServizioApplicativoConsegna.class, servizioApplicativoConsegna, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizioApplicativoConsegna</var> of type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param servizioApplicativoConsegna Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativoConsegna servizioApplicativoConsegna,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativoConsegna.class, servizioApplicativoConsegna, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: dettaglio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param fileName Xml file to serialize the object <var>dettaglio</var>
	 * @param dettaglio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Dettaglio dettaglio) throws SerializerException {
		this.objToXml(fileName, Dettaglio.class, dettaglio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param fileName Xml file to serialize the object <var>dettaglio</var>
	 * @param dettaglio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Dettaglio dettaglio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Dettaglio.class, dettaglio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param file Xml file to serialize the object <var>dettaglio</var>
	 * @param dettaglio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Dettaglio dettaglio) throws SerializerException {
		this.objToXml(file, Dettaglio.class, dettaglio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param file Xml file to serialize the object <var>dettaglio</var>
	 * @param dettaglio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Dettaglio dettaglio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Dettaglio.class, dettaglio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param out OutputStream to serialize the object <var>dettaglio</var>
	 * @param dettaglio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Dettaglio dettaglio) throws SerializerException {
		this.objToXml(out, Dettaglio.class, dettaglio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param out OutputStream to serialize the object <var>dettaglio</var>
	 * @param dettaglio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Dettaglio dettaglio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Dettaglio.class, dettaglio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dettaglio</var> of type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param dettaglio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Dettaglio dettaglio) throws SerializerException {
		return this.objToXml(Dettaglio.class, dettaglio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dettaglio</var> of type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param dettaglio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Dettaglio dettaglio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Dettaglio.class, dettaglio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dettaglio</var> of type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param dettaglio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Dettaglio dettaglio) throws SerializerException {
		return this.objToXml(Dettaglio.class, dettaglio, false).toString();
	}
	/**
	 * Serialize to String the object <var>dettaglio</var> of type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param dettaglio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Dettaglio dettaglio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Dettaglio.class, dettaglio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: filtro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>filtro</var> of type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param fileName Xml file to serialize the object <var>filtro</var>
	 * @param filtro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Filtro filtro) throws SerializerException {
		this.objToXml(fileName, Filtro.class, filtro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>filtro</var> of type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param fileName Xml file to serialize the object <var>filtro</var>
	 * @param filtro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Filtro filtro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Filtro.class, filtro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>filtro</var> of type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param file Xml file to serialize the object <var>filtro</var>
	 * @param filtro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Filtro filtro) throws SerializerException {
		this.objToXml(file, Filtro.class, filtro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>filtro</var> of type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param file Xml file to serialize the object <var>filtro</var>
	 * @param filtro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Filtro filtro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Filtro.class, filtro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>filtro</var> of type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param out OutputStream to serialize the object <var>filtro</var>
	 * @param filtro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Filtro filtro) throws SerializerException {
		this.objToXml(out, Filtro.class, filtro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>filtro</var> of type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param out OutputStream to serialize the object <var>filtro</var>
	 * @param filtro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Filtro filtro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Filtro.class, filtro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>filtro</var> of type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param filtro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Filtro filtro) throws SerializerException {
		return this.objToXml(Filtro.class, filtro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>filtro</var> of type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param filtro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Filtro filtro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Filtro.class, filtro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>filtro</var> of type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param filtro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Filtro filtro) throws SerializerException {
		return this.objToXml(Filtro.class, filtro, false).toString();
	}
	/**
	 * Serialize to String the object <var>filtro</var> of type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param filtro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Filtro filtro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Filtro.class, filtro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: proprieta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Proprieta proprieta) throws SerializerException {
		this.objToXml(fileName, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Proprieta.class, proprieta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param file Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Proprieta proprieta) throws SerializerException {
		this.objToXml(file, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param file Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Proprieta.class, proprieta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Proprieta proprieta) throws SerializerException {
		this.objToXml(out, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Proprieta.class, proprieta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toString();
	}
	/**
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, prettyPrint).toString();
	}
	
	
	

}
