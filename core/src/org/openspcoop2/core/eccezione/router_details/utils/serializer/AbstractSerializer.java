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
package org.openspcoop2.core.eccezione.router_details.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.eccezione.router_details.DominioSoggetto;
import org.openspcoop2.core.eccezione.router_details.Dettaglio;
import org.openspcoop2.core.eccezione.router_details.Dominio;
import org.openspcoop2.core.eccezione.router_details.DettaglioRouting;

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
	 Object: dominio-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(fileName, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioSoggetto dominioSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DominioSoggetto.class, dominioSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(file, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioSoggetto dominioSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DominioSoggetto.class, dominioSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(out, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioSoggetto dominioSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DominioSoggetto.class, dominioSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param dominioSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioSoggetto dominioSoggetto) throws SerializerException {
		return this.objToXml(DominioSoggetto.class, dominioSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param dominioSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioSoggetto dominioSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DominioSoggetto.class, dominioSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param dominioSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioSoggetto dominioSoggetto) throws SerializerException {
		return this.objToXml(DominioSoggetto.class, dominioSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param dominioSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioSoggetto dominioSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DominioSoggetto.class, dominioSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: dettaglio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * 
	 * @param fileName Xml file to serialize the object <var>dettaglio</var>
	 * @param dettaglio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Dettaglio dettaglio) throws SerializerException {
		this.objToXml(fileName, Dettaglio.class, dettaglio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
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
	 * Serialize to file system in <var>file</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * 
	 * @param file Xml file to serialize the object <var>dettaglio</var>
	 * @param dettaglio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Dettaglio dettaglio) throws SerializerException {
		this.objToXml(file, Dettaglio.class, dettaglio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
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
	 * Serialize to output stream <var>out</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * 
	 * @param out OutputStream to serialize the object <var>dettaglio</var>
	 * @param dettaglio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Dettaglio dettaglio) throws SerializerException {
		this.objToXml(out, Dettaglio.class, dettaglio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dettaglio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
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
	 * Serialize to byte array the object <var>dettaglio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * 
	 * @param dettaglio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Dettaglio dettaglio) throws SerializerException {
		return this.objToXml(Dettaglio.class, dettaglio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dettaglio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
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
	 * Serialize to String the object <var>dettaglio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * 
	 * @param dettaglio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Dettaglio dettaglio) throws SerializerException {
		return this.objToXml(Dettaglio.class, dettaglio, false).toString();
	}
	/**
	 * Serialize to String the object <var>dettaglio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
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
	 Object: dominio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Dominio dominio) throws SerializerException {
		this.objToXml(fileName, Dominio.class, dominio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Dominio dominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Dominio.class, dominio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param file Xml file to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Dominio dominio) throws SerializerException {
		this.objToXml(file, Dominio.class, dominio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param file Xml file to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Dominio dominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Dominio.class, dominio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param out OutputStream to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Dominio dominio) throws SerializerException {
		this.objToXml(out, Dominio.class, dominio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param out OutputStream to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Dominio dominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Dominio.class, dominio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param dominio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Dominio dominio) throws SerializerException {
		return this.objToXml(Dominio.class, dominio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param dominio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Dominio dominio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Dominio.class, dominio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param dominio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Dominio dominio) throws SerializerException {
		return this.objToXml(Dominio.class, dominio, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param dominio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Dominio dominio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Dominio.class, dominio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: dettaglio-routing
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dettaglioRouting</var> of type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param fileName Xml file to serialize the object <var>dettaglioRouting</var>
	 * @param dettaglioRouting Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DettaglioRouting dettaglioRouting) throws SerializerException {
		this.objToXml(fileName, DettaglioRouting.class, dettaglioRouting, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dettaglioRouting</var> of type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param fileName Xml file to serialize the object <var>dettaglioRouting</var>
	 * @param dettaglioRouting Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DettaglioRouting dettaglioRouting,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DettaglioRouting.class, dettaglioRouting, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dettaglioRouting</var> of type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param file Xml file to serialize the object <var>dettaglioRouting</var>
	 * @param dettaglioRouting Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DettaglioRouting dettaglioRouting) throws SerializerException {
		this.objToXml(file, DettaglioRouting.class, dettaglioRouting, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dettaglioRouting</var> of type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param file Xml file to serialize the object <var>dettaglioRouting</var>
	 * @param dettaglioRouting Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DettaglioRouting dettaglioRouting,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DettaglioRouting.class, dettaglioRouting, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dettaglioRouting</var> of type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param out OutputStream to serialize the object <var>dettaglioRouting</var>
	 * @param dettaglioRouting Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DettaglioRouting dettaglioRouting) throws SerializerException {
		this.objToXml(out, DettaglioRouting.class, dettaglioRouting, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dettaglioRouting</var> of type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param out OutputStream to serialize the object <var>dettaglioRouting</var>
	 * @param dettaglioRouting Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DettaglioRouting dettaglioRouting,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DettaglioRouting.class, dettaglioRouting, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dettaglioRouting</var> of type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param dettaglioRouting Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DettaglioRouting dettaglioRouting) throws SerializerException {
		return this.objToXml(DettaglioRouting.class, dettaglioRouting, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dettaglioRouting</var> of type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param dettaglioRouting Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DettaglioRouting dettaglioRouting,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DettaglioRouting.class, dettaglioRouting, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dettaglioRouting</var> of type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param dettaglioRouting Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DettaglioRouting dettaglioRouting) throws SerializerException {
		return this.objToXml(DettaglioRouting.class, dettaglioRouting, false).toString();
	}
	/**
	 * Serialize to String the object <var>dettaglioRouting</var> of type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param dettaglioRouting Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DettaglioRouting dettaglioRouting,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DettaglioRouting.class, dettaglioRouting, prettyPrint).toString();
	}
	
	
	

}
