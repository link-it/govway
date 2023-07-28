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
package org.openspcoop2.core.statistiche.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.statistiche.Statistica;
import org.openspcoop2.core.statistiche.StatisticaMensile;
import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.StatisticaGiornaliera;
import org.openspcoop2.core.statistiche.StatisticaSettimanale;
import org.openspcoop2.core.statistiche.StatisticaOraria;
import org.openspcoop2.core.statistiche.StatisticaInfo;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.lang.reflect.Method;

import jakarta.xml.bind.JAXBElement;

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
				if(fout!=null){
					fout.flush();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fout!=null){
					fout.close();
				}
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
				if(bout!=null){
					bout.flush();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(bout!=null){
					bout.close();
				}
			}catch(Exception e){
				// ignore
			}
		}
		return bout;
	}




	/*
	 =================================================================================
	 Object: statistica
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statistica</var> of type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param fileName Xml file to serialize the object <var>statistica</var>
	 * @param statistica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Statistica statistica) throws SerializerException {
		this.objToXml(fileName, Statistica.class, statistica, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statistica</var> of type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param fileName Xml file to serialize the object <var>statistica</var>
	 * @param statistica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Statistica statistica,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Statistica.class, statistica, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statistica</var> of type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param file Xml file to serialize the object <var>statistica</var>
	 * @param statistica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Statistica statistica) throws SerializerException {
		this.objToXml(file, Statistica.class, statistica, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statistica</var> of type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param file Xml file to serialize the object <var>statistica</var>
	 * @param statistica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Statistica statistica,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Statistica.class, statistica, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statistica</var> of type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param out OutputStream to serialize the object <var>statistica</var>
	 * @param statistica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Statistica statistica) throws SerializerException {
		this.objToXml(out, Statistica.class, statistica, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statistica</var> of type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param out OutputStream to serialize the object <var>statistica</var>
	 * @param statistica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Statistica statistica,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Statistica.class, statistica, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statistica</var> of type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param statistica Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Statistica statistica) throws SerializerException {
		return this.objToXml(Statistica.class, statistica, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statistica</var> of type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param statistica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Statistica statistica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Statistica.class, statistica, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statistica</var> of type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param statistica Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Statistica statistica) throws SerializerException {
		return this.objToXml(Statistica.class, statistica, false).toString();
	}
	/**
	 * Serialize to String the object <var>statistica</var> of type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param statistica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Statistica statistica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Statistica.class, statistica, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: statistica-mensile
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaMensile</var> of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaMensile</var>
	 * @param statisticaMensile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaMensile statisticaMensile) throws SerializerException {
		this.objToXml(fileName, StatisticaMensile.class, statisticaMensile, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaMensile</var> of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaMensile</var>
	 * @param statisticaMensile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaMensile statisticaMensile,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatisticaMensile.class, statisticaMensile, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaMensile</var> of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaMensile</var>
	 * @param statisticaMensile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaMensile statisticaMensile) throws SerializerException {
		this.objToXml(file, StatisticaMensile.class, statisticaMensile, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaMensile</var> of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaMensile</var>
	 * @param statisticaMensile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaMensile statisticaMensile,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatisticaMensile.class, statisticaMensile, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaMensile</var> of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaMensile</var>
	 * @param statisticaMensile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaMensile statisticaMensile) throws SerializerException {
		this.objToXml(out, StatisticaMensile.class, statisticaMensile, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaMensile</var> of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaMensile</var>
	 * @param statisticaMensile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaMensile statisticaMensile,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatisticaMensile.class, statisticaMensile, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statisticaMensile</var> of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param statisticaMensile Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaMensile statisticaMensile) throws SerializerException {
		return this.objToXml(StatisticaMensile.class, statisticaMensile, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statisticaMensile</var> of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param statisticaMensile Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaMensile statisticaMensile,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaMensile.class, statisticaMensile, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statisticaMensile</var> of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param statisticaMensile Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaMensile statisticaMensile) throws SerializerException {
		return this.objToXml(StatisticaMensile.class, statisticaMensile, false).toString();
	}
	/**
	 * Serialize to String the object <var>statisticaMensile</var> of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param statisticaMensile Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaMensile statisticaMensile,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaMensile.class, statisticaMensile, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: statistica-contenuti
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaContenuti</var> of type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaContenuti</var>
	 * @param statisticaContenuti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaContenuti statisticaContenuti) throws SerializerException {
		this.objToXml(fileName, StatisticaContenuti.class, statisticaContenuti, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaContenuti</var> of type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaContenuti</var>
	 * @param statisticaContenuti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaContenuti statisticaContenuti,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatisticaContenuti.class, statisticaContenuti, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaContenuti</var> of type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaContenuti</var>
	 * @param statisticaContenuti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaContenuti statisticaContenuti) throws SerializerException {
		this.objToXml(file, StatisticaContenuti.class, statisticaContenuti, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaContenuti</var> of type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaContenuti</var>
	 * @param statisticaContenuti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaContenuti statisticaContenuti,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatisticaContenuti.class, statisticaContenuti, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaContenuti</var> of type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaContenuti</var>
	 * @param statisticaContenuti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaContenuti statisticaContenuti) throws SerializerException {
		this.objToXml(out, StatisticaContenuti.class, statisticaContenuti, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaContenuti</var> of type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaContenuti</var>
	 * @param statisticaContenuti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaContenuti statisticaContenuti,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatisticaContenuti.class, statisticaContenuti, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statisticaContenuti</var> of type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param statisticaContenuti Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaContenuti statisticaContenuti) throws SerializerException {
		return this.objToXml(StatisticaContenuti.class, statisticaContenuti, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statisticaContenuti</var> of type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param statisticaContenuti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaContenuti statisticaContenuti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaContenuti.class, statisticaContenuti, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statisticaContenuti</var> of type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param statisticaContenuti Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaContenuti statisticaContenuti) throws SerializerException {
		return this.objToXml(StatisticaContenuti.class, statisticaContenuti, false).toString();
	}
	/**
	 * Serialize to String the object <var>statisticaContenuti</var> of type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param statisticaContenuti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaContenuti statisticaContenuti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaContenuti.class, statisticaContenuti, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: statistica-giornaliera
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaGiornaliera</var> of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaGiornaliera</var>
	 * @param statisticaGiornaliera Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaGiornaliera statisticaGiornaliera) throws SerializerException {
		this.objToXml(fileName, StatisticaGiornaliera.class, statisticaGiornaliera, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaGiornaliera</var> of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaGiornaliera</var>
	 * @param statisticaGiornaliera Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaGiornaliera statisticaGiornaliera,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatisticaGiornaliera.class, statisticaGiornaliera, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaGiornaliera</var> of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaGiornaliera</var>
	 * @param statisticaGiornaliera Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaGiornaliera statisticaGiornaliera) throws SerializerException {
		this.objToXml(file, StatisticaGiornaliera.class, statisticaGiornaliera, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaGiornaliera</var> of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaGiornaliera</var>
	 * @param statisticaGiornaliera Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaGiornaliera statisticaGiornaliera,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatisticaGiornaliera.class, statisticaGiornaliera, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaGiornaliera</var> of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaGiornaliera</var>
	 * @param statisticaGiornaliera Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaGiornaliera statisticaGiornaliera) throws SerializerException {
		this.objToXml(out, StatisticaGiornaliera.class, statisticaGiornaliera, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaGiornaliera</var> of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaGiornaliera</var>
	 * @param statisticaGiornaliera Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaGiornaliera statisticaGiornaliera,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatisticaGiornaliera.class, statisticaGiornaliera, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statisticaGiornaliera</var> of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param statisticaGiornaliera Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaGiornaliera statisticaGiornaliera) throws SerializerException {
		return this.objToXml(StatisticaGiornaliera.class, statisticaGiornaliera, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statisticaGiornaliera</var> of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param statisticaGiornaliera Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaGiornaliera statisticaGiornaliera,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaGiornaliera.class, statisticaGiornaliera, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statisticaGiornaliera</var> of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param statisticaGiornaliera Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaGiornaliera statisticaGiornaliera) throws SerializerException {
		return this.objToXml(StatisticaGiornaliera.class, statisticaGiornaliera, false).toString();
	}
	/**
	 * Serialize to String the object <var>statisticaGiornaliera</var> of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param statisticaGiornaliera Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaGiornaliera statisticaGiornaliera,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaGiornaliera.class, statisticaGiornaliera, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: statistica-settimanale
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaSettimanale</var> of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaSettimanale</var>
	 * @param statisticaSettimanale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaSettimanale statisticaSettimanale) throws SerializerException {
		this.objToXml(fileName, StatisticaSettimanale.class, statisticaSettimanale, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaSettimanale</var> of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaSettimanale</var>
	 * @param statisticaSettimanale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaSettimanale statisticaSettimanale,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatisticaSettimanale.class, statisticaSettimanale, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaSettimanale</var> of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaSettimanale</var>
	 * @param statisticaSettimanale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaSettimanale statisticaSettimanale) throws SerializerException {
		this.objToXml(file, StatisticaSettimanale.class, statisticaSettimanale, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaSettimanale</var> of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaSettimanale</var>
	 * @param statisticaSettimanale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaSettimanale statisticaSettimanale,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatisticaSettimanale.class, statisticaSettimanale, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaSettimanale</var> of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaSettimanale</var>
	 * @param statisticaSettimanale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaSettimanale statisticaSettimanale) throws SerializerException {
		this.objToXml(out, StatisticaSettimanale.class, statisticaSettimanale, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaSettimanale</var> of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaSettimanale</var>
	 * @param statisticaSettimanale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaSettimanale statisticaSettimanale,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatisticaSettimanale.class, statisticaSettimanale, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statisticaSettimanale</var> of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param statisticaSettimanale Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaSettimanale statisticaSettimanale) throws SerializerException {
		return this.objToXml(StatisticaSettimanale.class, statisticaSettimanale, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statisticaSettimanale</var> of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param statisticaSettimanale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaSettimanale statisticaSettimanale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaSettimanale.class, statisticaSettimanale, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statisticaSettimanale</var> of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param statisticaSettimanale Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaSettimanale statisticaSettimanale) throws SerializerException {
		return this.objToXml(StatisticaSettimanale.class, statisticaSettimanale, false).toString();
	}
	/**
	 * Serialize to String the object <var>statisticaSettimanale</var> of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param statisticaSettimanale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaSettimanale statisticaSettimanale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaSettimanale.class, statisticaSettimanale, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: statistica-oraria
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaOraria</var> of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaOraria</var>
	 * @param statisticaOraria Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaOraria statisticaOraria) throws SerializerException {
		this.objToXml(fileName, StatisticaOraria.class, statisticaOraria, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaOraria</var> of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaOraria</var>
	 * @param statisticaOraria Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaOraria statisticaOraria,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatisticaOraria.class, statisticaOraria, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaOraria</var> of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaOraria</var>
	 * @param statisticaOraria Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaOraria statisticaOraria) throws SerializerException {
		this.objToXml(file, StatisticaOraria.class, statisticaOraria, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaOraria</var> of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaOraria</var>
	 * @param statisticaOraria Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaOraria statisticaOraria,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatisticaOraria.class, statisticaOraria, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaOraria</var> of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaOraria</var>
	 * @param statisticaOraria Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaOraria statisticaOraria) throws SerializerException {
		this.objToXml(out, StatisticaOraria.class, statisticaOraria, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaOraria</var> of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaOraria</var>
	 * @param statisticaOraria Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaOraria statisticaOraria,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatisticaOraria.class, statisticaOraria, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statisticaOraria</var> of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param statisticaOraria Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaOraria statisticaOraria) throws SerializerException {
		return this.objToXml(StatisticaOraria.class, statisticaOraria, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statisticaOraria</var> of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param statisticaOraria Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaOraria statisticaOraria,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaOraria.class, statisticaOraria, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statisticaOraria</var> of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param statisticaOraria Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaOraria statisticaOraria) throws SerializerException {
		return this.objToXml(StatisticaOraria.class, statisticaOraria, false).toString();
	}
	/**
	 * Serialize to String the object <var>statisticaOraria</var> of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param statisticaOraria Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaOraria statisticaOraria,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaOraria.class, statisticaOraria, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: statistica-info
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaInfo</var> of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaInfo</var>
	 * @param statisticaInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaInfo statisticaInfo) throws SerializerException {
		this.objToXml(fileName, StatisticaInfo.class, statisticaInfo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statisticaInfo</var> of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>statisticaInfo</var>
	 * @param statisticaInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatisticaInfo statisticaInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatisticaInfo.class, statisticaInfo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaInfo</var> of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaInfo</var>
	 * @param statisticaInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaInfo statisticaInfo) throws SerializerException {
		this.objToXml(file, StatisticaInfo.class, statisticaInfo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statisticaInfo</var> of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param file Xml file to serialize the object <var>statisticaInfo</var>
	 * @param statisticaInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatisticaInfo statisticaInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatisticaInfo.class, statisticaInfo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaInfo</var> of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaInfo</var>
	 * @param statisticaInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaInfo statisticaInfo) throws SerializerException {
		this.objToXml(out, StatisticaInfo.class, statisticaInfo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statisticaInfo</var> of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>statisticaInfo</var>
	 * @param statisticaInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatisticaInfo statisticaInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatisticaInfo.class, statisticaInfo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statisticaInfo</var> of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param statisticaInfo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaInfo statisticaInfo) throws SerializerException {
		return this.objToXml(StatisticaInfo.class, statisticaInfo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statisticaInfo</var> of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param statisticaInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatisticaInfo statisticaInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaInfo.class, statisticaInfo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statisticaInfo</var> of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param statisticaInfo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaInfo statisticaInfo) throws SerializerException {
		return this.objToXml(StatisticaInfo.class, statisticaInfo, false).toString();
	}
	/**
	 * Serialize to String the object <var>statisticaInfo</var> of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param statisticaInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatisticaInfo statisticaInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatisticaInfo.class, statisticaInfo, prettyPrint).toString();
	}
	
	
	

}
