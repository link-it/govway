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
package org.openspcoop2.core.eccezione.errore_applicativo.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.Eccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.Servizio;
import org.openspcoop2.core.eccezione.errore_applicativo.Dominio;
import org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo;
import org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione;
import org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto;
import org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo;
import org.openspcoop2.core.eccezione.errore_applicativo.Soggetto;

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
	 Object: CodiceEccezione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>codiceEccezione</var>
	 * @param codiceEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CodiceEccezione codiceEccezione) throws SerializerException {
		this.objToXml(fileName, CodiceEccezione.class, codiceEccezione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>codiceEccezione</var>
	 * @param codiceEccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CodiceEccezione codiceEccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CodiceEccezione.class, codiceEccezione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param file Xml file to serialize the object <var>codiceEccezione</var>
	 * @param codiceEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CodiceEccezione codiceEccezione) throws SerializerException {
		this.objToXml(file, CodiceEccezione.class, codiceEccezione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param file Xml file to serialize the object <var>codiceEccezione</var>
	 * @param codiceEccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CodiceEccezione codiceEccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CodiceEccezione.class, codiceEccezione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>codiceEccezione</var>
	 * @param codiceEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CodiceEccezione codiceEccezione) throws SerializerException {
		this.objToXml(out, CodiceEccezione.class, codiceEccezione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>codiceEccezione</var>
	 * @param codiceEccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CodiceEccezione codiceEccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CodiceEccezione.class, codiceEccezione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param codiceEccezione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CodiceEccezione codiceEccezione) throws SerializerException {
		return this.objToXml(CodiceEccezione.class, codiceEccezione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param codiceEccezione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CodiceEccezione codiceEccezione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CodiceEccezione.class, codiceEccezione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param codiceEccezione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CodiceEccezione codiceEccezione) throws SerializerException {
		return this.objToXml(CodiceEccezione.class, codiceEccezione, false).toString();
	}
	/**
	 * Serialize to String the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param codiceEccezione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CodiceEccezione codiceEccezione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CodiceEccezione.class, codiceEccezione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: eccezione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Eccezione eccezione) throws SerializerException {
		this.objToXml(fileName, Eccezione.class, eccezione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Eccezione eccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Eccezione.class, eccezione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param file Xml file to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Eccezione eccezione) throws SerializerException {
		this.objToXml(file, Eccezione.class, eccezione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param file Xml file to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Eccezione eccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Eccezione.class, eccezione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Eccezione eccezione) throws SerializerException {
		this.objToXml(out, Eccezione.class, eccezione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Eccezione eccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Eccezione.class, eccezione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>eccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param eccezione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Eccezione eccezione) throws SerializerException {
		return this.objToXml(Eccezione.class, eccezione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>eccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param eccezione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Eccezione eccezione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Eccezione.class, eccezione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>eccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param eccezione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Eccezione eccezione) throws SerializerException {
		return this.objToXml(Eccezione.class, eccezione, false).toString();
	}
	/**
	 * Serialize to String the object <var>eccezione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param eccezione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Eccezione eccezione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Eccezione.class, eccezione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Servizio servizio) throws SerializerException {
		this.objToXml(fileName, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Servizio servizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Servizio.class, servizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param file Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Servizio servizio) throws SerializerException {
		this.objToXml(file, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param file Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Servizio servizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Servizio.class, servizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param out OutputStream to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Servizio servizio) throws SerializerException {
		this.objToXml(out, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param out OutputStream to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Servizio servizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Servizio.class, servizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Servizio servizio) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Servizio servizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Servizio servizio) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Servizio servizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: dominio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Dominio dominio) throws SerializerException {
		this.objToXml(fileName, Dominio.class, dominio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
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
	 * Serialize to file system in <var>file</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * 
	 * @param file Xml file to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Dominio dominio) throws SerializerException {
		this.objToXml(file, Dominio.class, dominio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
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
	 * Serialize to output stream <var>out</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * 
	 * @param out OutputStream to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Dominio dominio) throws SerializerException {
		this.objToXml(out, Dominio.class, dominio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
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
	 * Serialize to byte array the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * 
	 * @param dominio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Dominio dominio) throws SerializerException {
		return this.objToXml(Dominio.class, dominio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
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
	 * Serialize to String the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * 
	 * @param dominio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Dominio dominio) throws SerializerException {
		return this.objToXml(Dominio.class, dominio, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominio</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
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
	 Object: errore-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>erroreApplicativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>erroreApplicativo</var>
	 * @param erroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErroreApplicativo erroreApplicativo) throws SerializerException {
		this.objToXml(fileName, ErroreApplicativo.class, erroreApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>erroreApplicativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>erroreApplicativo</var>
	 * @param erroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErroreApplicativo erroreApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ErroreApplicativo.class, erroreApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>erroreApplicativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>erroreApplicativo</var>
	 * @param erroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErroreApplicativo erroreApplicativo) throws SerializerException {
		this.objToXml(file, ErroreApplicativo.class, erroreApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>erroreApplicativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>erroreApplicativo</var>
	 * @param erroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErroreApplicativo erroreApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ErroreApplicativo.class, erroreApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>erroreApplicativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>erroreApplicativo</var>
	 * @param erroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErroreApplicativo erroreApplicativo) throws SerializerException {
		this.objToXml(out, ErroreApplicativo.class, erroreApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>erroreApplicativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>erroreApplicativo</var>
	 * @param erroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErroreApplicativo erroreApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ErroreApplicativo.class, erroreApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>erroreApplicativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param erroreApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErroreApplicativo erroreApplicativo) throws SerializerException {
		return this.objToXml(ErroreApplicativo.class, erroreApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>erroreApplicativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param erroreApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErroreApplicativo erroreApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErroreApplicativo.class, erroreApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>erroreApplicativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param erroreApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErroreApplicativo erroreApplicativo) throws SerializerException {
		return this.objToXml(ErroreApplicativo.class, erroreApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>erroreApplicativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param erroreApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErroreApplicativo erroreApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErroreApplicativo.class, erroreApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: dati-cooperazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiCooperazione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiCooperazione</var>
	 * @param datiCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiCooperazione datiCooperazione) throws SerializerException {
		this.objToXml(fileName, DatiCooperazione.class, datiCooperazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiCooperazione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiCooperazione</var>
	 * @param datiCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiCooperazione datiCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiCooperazione.class, datiCooperazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiCooperazione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param file Xml file to serialize the object <var>datiCooperazione</var>
	 * @param datiCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiCooperazione datiCooperazione) throws SerializerException {
		this.objToXml(file, DatiCooperazione.class, datiCooperazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiCooperazione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param file Xml file to serialize the object <var>datiCooperazione</var>
	 * @param datiCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiCooperazione datiCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiCooperazione.class, datiCooperazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiCooperazione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param out OutputStream to serialize the object <var>datiCooperazione</var>
	 * @param datiCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiCooperazione datiCooperazione) throws SerializerException {
		this.objToXml(out, DatiCooperazione.class, datiCooperazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiCooperazione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param out OutputStream to serialize the object <var>datiCooperazione</var>
	 * @param datiCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiCooperazione datiCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiCooperazione.class, datiCooperazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiCooperazione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param datiCooperazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiCooperazione datiCooperazione) throws SerializerException {
		return this.objToXml(DatiCooperazione.class, datiCooperazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiCooperazione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param datiCooperazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiCooperazione datiCooperazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiCooperazione.class, datiCooperazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiCooperazione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param datiCooperazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiCooperazione datiCooperazione) throws SerializerException {
		return this.objToXml(DatiCooperazione.class, datiCooperazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiCooperazione</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param datiCooperazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiCooperazione datiCooperazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiCooperazione.class, datiCooperazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: dominio-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(fileName, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
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
	 * Serialize to file system in <var>file</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(file, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
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
	 * Serialize to output stream <var>out</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(out, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
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
	 * Serialize to byte array the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * 
	 * @param dominioSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioSoggetto dominioSoggetto) throws SerializerException {
		return this.objToXml(DominioSoggetto.class, dominioSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
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
	 * Serialize to String the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * 
	 * @param dominioSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioSoggetto dominioSoggetto) throws SerializerException {
		return this.objToXml(DominioSoggetto.class, dominioSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
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
	 Object: soggetto-identificativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		this.objToXml(fileName, SoggettoIdentificativo.class, soggettoIdentificativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoggettoIdentificativo soggettoIdentificativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoggettoIdentificativo.class, soggettoIdentificativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param file Xml file to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		this.objToXml(file, SoggettoIdentificativo.class, soggettoIdentificativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param file Xml file to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoggettoIdentificativo soggettoIdentificativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoggettoIdentificativo.class, soggettoIdentificativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param out OutputStream to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		this.objToXml(out, SoggettoIdentificativo.class, soggettoIdentificativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param out OutputStream to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoggettoIdentificativo soggettoIdentificativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoggettoIdentificativo.class, soggettoIdentificativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param soggettoIdentificativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		return this.objToXml(SoggettoIdentificativo.class, soggettoIdentificativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param soggettoIdentificativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoggettoIdentificativo soggettoIdentificativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoggettoIdentificativo.class, soggettoIdentificativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param soggettoIdentificativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		return this.objToXml(SoggettoIdentificativo.class, soggettoIdentificativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param soggettoIdentificativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoggettoIdentificativo soggettoIdentificativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoggettoIdentificativo.class, soggettoIdentificativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, prettyPrint).toString();
	}
	
	
	

}
