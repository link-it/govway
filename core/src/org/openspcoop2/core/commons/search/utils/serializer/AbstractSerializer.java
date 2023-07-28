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
package org.openspcoop2.core.commons.search.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.commons.search.Operation;
import org.openspcoop2.core.commons.search.IdPortType;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.commons.search.PortaApplicativaAzione;
import org.openspcoop2.core.commons.search.Resource;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione;
import org.openspcoop2.core.commons.search.IdRuolo;
import org.openspcoop2.core.commons.search.IdPortaDominio;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.ServizioApplicativoRuolo;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo;
import org.openspcoop2.core.commons.search.Ruolo;
import org.openspcoop2.core.commons.search.SoggettoRuolo;
import org.openspcoop2.core.commons.search.ServizioApplicativo;
import org.openspcoop2.core.commons.search.IdGruppo;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.IdServizioApplicativo;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.IdPortaDelegata;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.PortaDelegataAzione;
import org.openspcoop2.core.commons.search.IdFruitore;
import org.openspcoop2.core.commons.search.IdOperation;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.Gruppo;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.PortaDominio;
import org.openspcoop2.core.commons.search.IdPortaApplicativa;
import org.openspcoop2.core.commons.search.IdResource;
import org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.commons.search.Fruitore;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.IdSoggettoRuolo;

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
	 Object: operation
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>operation</var> of type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param fileName Xml file to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Operation operation) throws SerializerException {
		this.objToXml(fileName, Operation.class, operation, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>operation</var> of type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param fileName Xml file to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Operation operation,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Operation.class, operation, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>operation</var> of type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param file Xml file to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Operation operation) throws SerializerException {
		this.objToXml(file, Operation.class, operation, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>operation</var> of type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param file Xml file to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Operation operation,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Operation.class, operation, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>operation</var> of type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param out OutputStream to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Operation operation) throws SerializerException {
		this.objToXml(out, Operation.class, operation, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>operation</var> of type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param out OutputStream to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Operation operation,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Operation.class, operation, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>operation</var> of type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param operation Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Operation operation) throws SerializerException {
		return this.objToXml(Operation.class, operation, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>operation</var> of type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param operation Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Operation operation,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Operation.class, operation, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>operation</var> of type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param operation Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Operation operation) throws SerializerException {
		return this.objToXml(Operation.class, operation, false).toString();
	}
	/**
	 * Serialize to String the object <var>operation</var> of type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param operation Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Operation operation,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Operation.class, operation, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-port-type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortType</var> of type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortType</var>
	 * @param idPortType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortType idPortType) throws SerializerException {
		this.objToXml(fileName, IdPortType.class, idPortType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortType</var> of type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortType</var>
	 * @param idPortType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortType idPortType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdPortType.class, idPortType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortType</var> of type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param file Xml file to serialize the object <var>idPortType</var>
	 * @param idPortType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortType idPortType) throws SerializerException {
		this.objToXml(file, IdPortType.class, idPortType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortType</var> of type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param file Xml file to serialize the object <var>idPortType</var>
	 * @param idPortType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortType idPortType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdPortType.class, idPortType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortType</var> of type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortType</var>
	 * @param idPortType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortType idPortType) throws SerializerException {
		this.objToXml(out, IdPortType.class, idPortType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortType</var> of type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortType</var>
	 * @param idPortType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortType idPortType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdPortType.class, idPortType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idPortType</var> of type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param idPortType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortType idPortType) throws SerializerException {
		return this.objToXml(IdPortType.class, idPortType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idPortType</var> of type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param idPortType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortType idPortType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortType.class, idPortType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idPortType</var> of type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param idPortType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortType idPortType) throws SerializerException {
		return this.objToXml(IdPortType.class, idPortType, false).toString();
	}
	/**
	 * Serialize to String the object <var>idPortType</var> of type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param idPortType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortType idPortType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortType.class, idPortType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativa portaApplicativa) throws SerializerException {
		this.objToXml(fileName, PortaApplicativa.class, portaApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativa portaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativa.class, portaApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativa portaApplicativa) throws SerializerException {
		this.objToXml(file, PortaApplicativa.class, portaApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativa portaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativa.class, portaApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativa portaApplicativa) throws SerializerException {
		this.objToXml(out, PortaApplicativa.class, portaApplicativa, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativa portaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativa.class, portaApplicativa, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param portaApplicativa Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativa portaApplicativa) throws SerializerException {
		return this.objToXml(PortaApplicativa.class, portaApplicativa, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param portaApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativa portaApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativa.class, portaApplicativa, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param portaApplicativa Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativa portaApplicativa) throws SerializerException {
		return this.objToXml(PortaApplicativa.class, portaApplicativa, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param portaApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativa portaApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativa.class, portaApplicativa, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdSoggetto idSoggetto) throws SerializerException {
		this.objToXml(fileName, IdSoggetto.class, idSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdSoggetto.class, idSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdSoggetto idSoggetto) throws SerializerException {
		this.objToXml(file, IdSoggetto.class, idSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdSoggetto.class, idSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdSoggetto idSoggetto) throws SerializerException {
		this.objToXml(out, IdSoggetto.class, idSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdSoggetto.class, idSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdSoggetto idSoggetto) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdSoggetto idSoggetto) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) throws SerializerException {
		this.objToXml(file, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) throws SerializerException {
		this.objToXml(out, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param portaApplicativaServizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param portaApplicativaServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param portaApplicativaServizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param portaApplicativaServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAzione portaApplicativaAzione) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAzione.class, portaApplicativaAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAzione portaApplicativaAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAzione.class, portaApplicativaAzione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAzione portaApplicativaAzione) throws SerializerException {
		this.objToXml(file, PortaApplicativaAzione.class, portaApplicativaAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAzione portaApplicativaAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaAzione.class, portaApplicativaAzione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAzione portaApplicativaAzione) throws SerializerException {
		this.objToXml(out, PortaApplicativaAzione.class, portaApplicativaAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAzione portaApplicativaAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaAzione.class, portaApplicativaAzione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param portaApplicativaAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAzione portaApplicativaAzione) throws SerializerException {
		return this.objToXml(PortaApplicativaAzione.class, portaApplicativaAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param portaApplicativaAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAzione portaApplicativaAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAzione.class, portaApplicativaAzione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param portaApplicativaAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAzione portaApplicativaAzione) throws SerializerException {
		return this.objToXml(PortaApplicativaAzione.class, portaApplicativaAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param portaApplicativaAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAzione portaApplicativaAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAzione.class, portaApplicativaAzione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: resource
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resource</var> of type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param fileName Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Resource resource) throws SerializerException {
		this.objToXml(fileName, Resource.class, resource, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resource</var> of type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param fileName Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Resource resource,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Resource.class, resource, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>resource</var> of type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param file Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Resource resource) throws SerializerException {
		this.objToXml(file, Resource.class, resource, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>resource</var> of type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param file Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Resource resource,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Resource.class, resource, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>resource</var> of type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param out OutputStream to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Resource resource) throws SerializerException {
		this.objToXml(out, Resource.class, resource, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>resource</var> of type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param out OutputStream to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Resource resource,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Resource.class, resource, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>resource</var> of type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Resource resource) throws SerializerException {
		return this.objToXml(Resource.class, resource, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>resource</var> of type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Resource resource,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Resource.class, resource, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>resource</var> of type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Resource resource) throws SerializerException {
		return this.objToXml(Resource.class, resource, false).toString();
	}
	/**
	 * Serialize to String the object <var>resource</var> of type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Resource resource,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Resource.class, resource, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-comune
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteComune idAccordoServizioParteComune) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteComune idAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteComune idAccordoServizioParteComune) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteComune idAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteComune idAccordoServizioParteComune) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteComune idAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param idAccordoServizioParteComune Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteComune idAccordoServizioParteComune) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComune.class, idAccordoServizioParteComune, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param idAccordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteComune idAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComune.class, idAccordoServizioParteComune, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param idAccordoServizioParteComune Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteComune idAccordoServizioParteComune) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComune.class, idAccordoServizioParteComune, false).toString();
	}
	/**
	 * Serialize to String the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param idAccordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteComune idAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComune.class, idAccordoServizioParteComune, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComuneAzione</var>
	 * @param accordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComuneAzione accordoServizioParteComuneAzione) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComuneAzione.class, accordoServizioParteComuneAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComuneAzione</var>
	 * @param accordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComuneAzione accordoServizioParteComuneAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComuneAzione.class, accordoServizioParteComuneAzione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComuneAzione</var>
	 * @param accordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComuneAzione accordoServizioParteComuneAzione) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComuneAzione.class, accordoServizioParteComuneAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComuneAzione</var>
	 * @param accordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComuneAzione accordoServizioParteComuneAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComuneAzione.class, accordoServizioParteComuneAzione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComuneAzione</var>
	 * @param accordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComuneAzione accordoServizioParteComuneAzione) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComuneAzione.class, accordoServizioParteComuneAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComuneAzione</var>
	 * @param accordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComuneAzione accordoServizioParteComuneAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComuneAzione.class, accordoServizioParteComuneAzione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param accordoServizioParteComuneAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComuneAzione accordoServizioParteComuneAzione) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneAzione.class, accordoServizioParteComuneAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param accordoServizioParteComuneAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComuneAzione accordoServizioParteComuneAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneAzione.class, accordoServizioParteComuneAzione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param accordoServizioParteComuneAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComuneAzione accordoServizioParteComuneAzione) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneAzione.class, accordoServizioParteComuneAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param accordoServizioParteComuneAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComuneAzione accordoServizioParteComuneAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneAzione.class, accordoServizioParteComuneAzione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-ruolo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdRuolo idRuolo) throws SerializerException {
		this.objToXml(fileName, IdRuolo.class, idRuolo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdRuolo idRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdRuolo.class, idRuolo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param file Xml file to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdRuolo idRuolo) throws SerializerException {
		this.objToXml(file, IdRuolo.class, idRuolo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param file Xml file to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdRuolo idRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdRuolo.class, idRuolo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param out OutputStream to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdRuolo idRuolo) throws SerializerException {
		this.objToXml(out, IdRuolo.class, idRuolo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param out OutputStream to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdRuolo idRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdRuolo.class, idRuolo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param idRuolo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdRuolo idRuolo) throws SerializerException {
		return this.objToXml(IdRuolo.class, idRuolo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param idRuolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdRuolo idRuolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdRuolo.class, idRuolo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param idRuolo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdRuolo idRuolo) throws SerializerException {
		return this.objToXml(IdRuolo.class, idRuolo, false).toString();
	}
	/**
	 * Serialize to String the object <var>idRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param idRuolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdRuolo idRuolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdRuolo.class, idRuolo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-dominio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaDominio idPortaDominio) throws SerializerException {
		this.objToXml(fileName, IdPortaDominio.class, idPortaDominio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaDominio idPortaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdPortaDominio.class, idPortaDominio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaDominio idPortaDominio) throws SerializerException {
		this.objToXml(file, IdPortaDominio.class, idPortaDominio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaDominio idPortaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdPortaDominio.class, idPortaDominio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaDominio idPortaDominio) throws SerializerException {
		this.objToXml(out, IdPortaDominio.class, idPortaDominio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaDominio idPortaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdPortaDominio.class, idPortaDominio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param idPortaDominio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaDominio idPortaDominio) throws SerializerException {
		return this.objToXml(IdPortaDominio.class, idPortaDominio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param idPortaDominio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaDominio idPortaDominio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaDominio.class, idPortaDominio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param idPortaDominio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaDominio idPortaDominio) throws SerializerException {
		return this.objToXml(IdPortaDominio.class, idPortaDominio, false).toString();
	}
	/**
	 * Serialize to String the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param idPortaDominio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaDominio idPortaDominio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaDominio.class, idPortaDominio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-specifica
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param idAccordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param idAccordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param idAccordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, false).toString();
	}
	/**
	 * Serialize to String the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param idAccordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo-ruolo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativoRuolo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativoRuolo</var>
	 * @param servizioApplicativoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativoRuolo servizioApplicativoRuolo) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativoRuolo.class, servizioApplicativoRuolo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativoRuolo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativoRuolo</var>
	 * @param servizioApplicativoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativoRuolo servizioApplicativoRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativoRuolo.class, servizioApplicativoRuolo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativoRuolo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativoRuolo</var>
	 * @param servizioApplicativoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativoRuolo servizioApplicativoRuolo) throws SerializerException {
		this.objToXml(file, ServizioApplicativoRuolo.class, servizioApplicativoRuolo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativoRuolo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativoRuolo</var>
	 * @param servizioApplicativoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativoRuolo servizioApplicativoRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServizioApplicativoRuolo.class, servizioApplicativoRuolo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativoRuolo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativoRuolo</var>
	 * @param servizioApplicativoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativoRuolo servizioApplicativoRuolo) throws SerializerException {
		this.objToXml(out, ServizioApplicativoRuolo.class, servizioApplicativoRuolo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativoRuolo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativoRuolo</var>
	 * @param servizioApplicativoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativoRuolo servizioApplicativoRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServizioApplicativoRuolo.class, servizioApplicativoRuolo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizioApplicativoRuolo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param servizioApplicativoRuolo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativoRuolo servizioApplicativoRuolo) throws SerializerException {
		return this.objToXml(ServizioApplicativoRuolo.class, servizioApplicativoRuolo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizioApplicativoRuolo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param servizioApplicativoRuolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativoRuolo servizioApplicativoRuolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativoRuolo.class, servizioApplicativoRuolo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizioApplicativoRuolo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param servizioApplicativoRuolo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativoRuolo servizioApplicativoRuolo) throws SerializerException {
		return this.objToXml(ServizioApplicativoRuolo.class, servizioApplicativoRuolo, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizioApplicativoRuolo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param servizioApplicativoRuolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativoRuolo servizioApplicativoRuolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativoRuolo.class, servizioApplicativoRuolo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-comune-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteComuneAzione</var>
	 * @param idAccordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteComuneAzione.class, idAccordoServizioParteComuneAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteComuneAzione</var>
	 * @param idAccordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteComuneAzione.class, idAccordoServizioParteComuneAzione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteComuneAzione</var>
	 * @param idAccordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteComuneAzione.class, idAccordoServizioParteComuneAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteComuneAzione</var>
	 * @param idAccordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteComuneAzione.class, idAccordoServizioParteComuneAzione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteComuneAzione</var>
	 * @param idAccordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteComuneAzione.class, idAccordoServizioParteComuneAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteComuneAzione</var>
	 * @param idAccordoServizioParteComuneAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteComuneAzione.class, idAccordoServizioParteComuneAzione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param idAccordoServizioParteComuneAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComuneAzione.class, idAccordoServizioParteComuneAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param idAccordoServizioParteComuneAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComuneAzione.class, idAccordoServizioParteComuneAzione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idAccordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param idAccordoServizioParteComuneAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComuneAzione.class, idAccordoServizioParteComuneAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>idAccordoServizioParteComuneAzione</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param idAccordoServizioParteComuneAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComuneAzione.class, idAccordoServizioParteComuneAzione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-specifica
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(file, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(out, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo-proprieta-protocollo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativoProprietaProtocollo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativoProprietaProtocollo</var>
	 * @param servizioApplicativoProprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativoProprietaProtocollo.class, servizioApplicativoProprietaProtocollo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativoProprietaProtocollo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativoProprietaProtocollo</var>
	 * @param servizioApplicativoProprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativoProprietaProtocollo.class, servizioApplicativoProprietaProtocollo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativoProprietaProtocollo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativoProprietaProtocollo</var>
	 * @param servizioApplicativoProprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo) throws SerializerException {
		this.objToXml(file, ServizioApplicativoProprietaProtocollo.class, servizioApplicativoProprietaProtocollo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativoProprietaProtocollo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativoProprietaProtocollo</var>
	 * @param servizioApplicativoProprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServizioApplicativoProprietaProtocollo.class, servizioApplicativoProprietaProtocollo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativoProprietaProtocollo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativoProprietaProtocollo</var>
	 * @param servizioApplicativoProprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo) throws SerializerException {
		this.objToXml(out, ServizioApplicativoProprietaProtocollo.class, servizioApplicativoProprietaProtocollo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativoProprietaProtocollo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativoProprietaProtocollo</var>
	 * @param servizioApplicativoProprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServizioApplicativoProprietaProtocollo.class, servizioApplicativoProprietaProtocollo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizioApplicativoProprietaProtocollo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param servizioApplicativoProprietaProtocollo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo) throws SerializerException {
		return this.objToXml(ServizioApplicativoProprietaProtocollo.class, servizioApplicativoProprietaProtocollo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizioApplicativoProprietaProtocollo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param servizioApplicativoProprietaProtocollo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativoProprietaProtocollo.class, servizioApplicativoProprietaProtocollo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizioApplicativoProprietaProtocollo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param servizioApplicativoProprietaProtocollo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo) throws SerializerException {
		return this.objToXml(ServizioApplicativoProprietaProtocollo.class, servizioApplicativoProprietaProtocollo, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizioApplicativoProprietaProtocollo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param servizioApplicativoProprietaProtocollo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativoProprietaProtocollo servizioApplicativoProprietaProtocollo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativoProprietaProtocollo.class, servizioApplicativoProprietaProtocollo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ruolo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Ruolo ruolo) throws SerializerException {
		this.objToXml(fileName, Ruolo.class, ruolo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Ruolo.class, ruolo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param file Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Ruolo ruolo) throws SerializerException {
		this.objToXml(file, Ruolo.class, ruolo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param file Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Ruolo.class, ruolo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param out OutputStream to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Ruolo ruolo) throws SerializerException {
		this.objToXml(out, Ruolo.class, ruolo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param out OutputStream to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Ruolo.class, ruolo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>ruolo</var> of type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Ruolo ruolo) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>ruolo</var> of type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>ruolo</var> of type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Ruolo ruolo) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, false).toString();
	}
	/**
	 * Serialize to String the object <var>ruolo</var> of type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soggetto-ruolo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggettoRuolo</var>
	 * @param soggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoggettoRuolo soggettoRuolo) throws SerializerException {
		this.objToXml(fileName, SoggettoRuolo.class, soggettoRuolo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggettoRuolo</var>
	 * @param soggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoggettoRuolo soggettoRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoggettoRuolo.class, soggettoRuolo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param file Xml file to serialize the object <var>soggettoRuolo</var>
	 * @param soggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoggettoRuolo soggettoRuolo) throws SerializerException {
		this.objToXml(file, SoggettoRuolo.class, soggettoRuolo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param file Xml file to serialize the object <var>soggettoRuolo</var>
	 * @param soggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoggettoRuolo soggettoRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoggettoRuolo.class, soggettoRuolo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param out OutputStream to serialize the object <var>soggettoRuolo</var>
	 * @param soggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoggettoRuolo soggettoRuolo) throws SerializerException {
		this.objToXml(out, SoggettoRuolo.class, soggettoRuolo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param out OutputStream to serialize the object <var>soggettoRuolo</var>
	 * @param soggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoggettoRuolo soggettoRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoggettoRuolo.class, soggettoRuolo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param soggettoRuolo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoggettoRuolo soggettoRuolo) throws SerializerException {
		return this.objToXml(SoggettoRuolo.class, soggettoRuolo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param soggettoRuolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoggettoRuolo soggettoRuolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoggettoRuolo.class, soggettoRuolo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param soggettoRuolo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoggettoRuolo soggettoRuolo) throws SerializerException {
		return this.objToXml(SoggettoRuolo.class, soggettoRuolo, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param soggettoRuolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoggettoRuolo soggettoRuolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoggettoRuolo.class, soggettoRuolo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativo servizioApplicativo) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativo.class, servizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativo.class, servizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativo servizioApplicativo) throws SerializerException {
		this.objToXml(file, ServizioApplicativo.class, servizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServizioApplicativo.class, servizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativo servizioApplicativo) throws SerializerException {
		this.objToXml(out, ServizioApplicativo.class, servizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServizioApplicativo.class, servizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativo servizioApplicativo) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativo servizioApplicativo) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-gruppo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdGruppo idGruppo) throws SerializerException {
		this.objToXml(fileName, IdGruppo.class, idGruppo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdGruppo idGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdGruppo.class, idGruppo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param file Xml file to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdGruppo idGruppo) throws SerializerException {
		this.objToXml(file, IdGruppo.class, idGruppo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param file Xml file to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdGruppo idGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdGruppo.class, idGruppo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdGruppo idGruppo) throws SerializerException {
		this.objToXml(out, IdGruppo.class, idGruppo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdGruppo idGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdGruppo.class, idGruppo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param idGruppo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdGruppo idGruppo) throws SerializerException {
		return this.objToXml(IdGruppo.class, idGruppo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param idGruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdGruppo idGruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdGruppo.class, idGruppo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param idGruppo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdGruppo idGruppo) throws SerializerException {
		return this.objToXml(IdGruppo.class, idGruppo, false).toString();
	}
	/**
	 * Serialize to String the object <var>idGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param idGruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdGruppo idGruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdGruppo.class, idGruppo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune-gruppo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComuneGruppo</var>
	 * @param accordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComuneGruppo.class, accordoServizioParteComuneGruppo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComuneGruppo</var>
	 * @param accordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComuneGruppo.class, accordoServizioParteComuneGruppo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComuneGruppo</var>
	 * @param accordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComuneGruppo.class, accordoServizioParteComuneGruppo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComuneGruppo</var>
	 * @param accordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComuneGruppo.class, accordoServizioParteComuneGruppo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComuneGruppo</var>
	 * @param accordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComuneGruppo.class, accordoServizioParteComuneGruppo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComuneGruppo</var>
	 * @param accordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComuneGruppo.class, accordoServizioParteComuneGruppo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param accordoServizioParteComuneGruppo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneGruppo.class, accordoServizioParteComuneGruppo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param accordoServizioParteComuneGruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneGruppo.class, accordoServizioParteComuneGruppo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param accordoServizioParteComuneGruppo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneGruppo.class, accordoServizioParteComuneGruppo, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param accordoServizioParteComuneGruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneGruppo.class, accordoServizioParteComuneGruppo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdServizioApplicativo idServizioApplicativo) throws SerializerException {
		this.objToXml(fileName, IdServizioApplicativo.class, idServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdServizioApplicativo idServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdServizioApplicativo.class, idServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdServizioApplicativo idServizioApplicativo) throws SerializerException {
		this.objToXml(file, IdServizioApplicativo.class, idServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdServizioApplicativo idServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdServizioApplicativo.class, idServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdServizioApplicativo idServizioApplicativo) throws SerializerException {
		this.objToXml(out, IdServizioApplicativo.class, idServizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdServizioApplicativo idServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdServizioApplicativo.class, idServizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param idServizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdServizioApplicativo idServizioApplicativo) throws SerializerException {
		return this.objToXml(IdServizioApplicativo.class, idServizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param idServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdServizioApplicativo idServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdServizioApplicativo.class, idServizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param idServizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdServizioApplicativo idServizioApplicativo) throws SerializerException {
		return this.objToXml(IdServizioApplicativo.class, idServizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param idServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdServizioApplicativo idServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdServizioApplicativo.class, idServizioApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: port-type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portType</var> of type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param fileName Xml file to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortType portType) throws SerializerException {
		this.objToXml(fileName, PortType.class, portType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portType</var> of type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param fileName Xml file to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortType portType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortType.class, portType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portType</var> of type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param file Xml file to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortType portType) throws SerializerException {
		this.objToXml(file, PortType.class, portType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portType</var> of type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param file Xml file to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortType portType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortType.class, portType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portType</var> of type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param out OutputStream to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortType portType) throws SerializerException {
		this.objToXml(out, PortType.class, portType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portType</var> of type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param out OutputStream to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortType portType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortType.class, portType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portType</var> of type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param portType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortType portType) throws SerializerException {
		return this.objToXml(PortType.class, portType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portType</var> of type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param portType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortType portType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortType.class, portType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portType</var> of type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param portType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortType portType) throws SerializerException {
		return this.objToXml(PortType.class, portType, false).toString();
	}
	/**
	 * Serialize to String the object <var>portType</var> of type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param portType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortType portType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortType.class, portType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-delegata
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaDelegata idPortaDelegata) throws SerializerException {
		this.objToXml(fileName, IdPortaDelegata.class, idPortaDelegata, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaDelegata idPortaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdPortaDelegata.class, idPortaDelegata, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaDelegata idPortaDelegata) throws SerializerException {
		this.objToXml(file, IdPortaDelegata.class, idPortaDelegata, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaDelegata idPortaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdPortaDelegata.class, idPortaDelegata, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaDelegata idPortaDelegata) throws SerializerException {
		this.objToXml(out, IdPortaDelegata.class, idPortaDelegata, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaDelegata idPortaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdPortaDelegata.class, idPortaDelegata, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param idPortaDelegata Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaDelegata idPortaDelegata) throws SerializerException {
		return this.objToXml(IdPortaDelegata.class, idPortaDelegata, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param idPortaDelegata Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaDelegata idPortaDelegata,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaDelegata.class, idPortaDelegata, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param idPortaDelegata Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaDelegata idPortaDelegata) throws SerializerException {
		return this.objToXml(IdPortaDelegata.class, idPortaDelegata, false).toString();
	}
	/**
	 * Serialize to String the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param idPortaDelegata Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaDelegata idPortaDelegata,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaDelegata.class, idPortaDelegata, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-comune-gruppo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteComuneGruppo</var>
	 * @param idAccordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteComuneGruppo.class, idAccordoServizioParteComuneGruppo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteComuneGruppo</var>
	 * @param idAccordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteComuneGruppo.class, idAccordoServizioParteComuneGruppo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteComuneGruppo</var>
	 * @param idAccordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteComuneGruppo.class, idAccordoServizioParteComuneGruppo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteComuneGruppo</var>
	 * @param idAccordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteComuneGruppo.class, idAccordoServizioParteComuneGruppo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteComuneGruppo</var>
	 * @param idAccordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteComuneGruppo.class, idAccordoServizioParteComuneGruppo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteComuneGruppo</var>
	 * @param idAccordoServizioParteComuneGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteComuneGruppo.class, idAccordoServizioParteComuneGruppo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param idAccordoServizioParteComuneGruppo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComuneGruppo.class, idAccordoServizioParteComuneGruppo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param idAccordoServizioParteComuneGruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComuneGruppo.class, idAccordoServizioParteComuneGruppo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idAccordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param idAccordoServizioParteComuneGruppo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComuneGruppo.class, idAccordoServizioParteComuneGruppo, false).toString();
	}
	/**
	 * Serialize to String the object <var>idAccordoServizioParteComuneGruppo</var> of type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param idAccordoServizioParteComuneGruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComuneGruppo.class, idAccordoServizioParteComuneGruppo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataAzione portaDelegataAzione) throws SerializerException {
		this.objToXml(fileName, PortaDelegataAzione.class, portaDelegataAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataAzione portaDelegataAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDelegataAzione.class, portaDelegataAzione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataAzione portaDelegataAzione) throws SerializerException {
		this.objToXml(file, PortaDelegataAzione.class, portaDelegataAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataAzione portaDelegataAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDelegataAzione.class, portaDelegataAzione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataAzione portaDelegataAzione) throws SerializerException {
		this.objToXml(out, PortaDelegataAzione.class, portaDelegataAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataAzione portaDelegataAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDelegataAzione.class, portaDelegataAzione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param portaDelegataAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataAzione portaDelegataAzione) throws SerializerException {
		return this.objToXml(PortaDelegataAzione.class, portaDelegataAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param portaDelegataAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataAzione portaDelegataAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataAzione.class, portaDelegataAzione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param portaDelegataAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataAzione portaDelegataAzione) throws SerializerException {
		return this.objToXml(PortaDelegataAzione.class, portaDelegataAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param portaDelegataAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataAzione portaDelegataAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataAzione.class, portaDelegataAzione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-fruitore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idFruitore</var> of type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>idFruitore</var>
	 * @param idFruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdFruitore idFruitore) throws SerializerException {
		this.objToXml(fileName, IdFruitore.class, idFruitore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idFruitore</var> of type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>idFruitore</var>
	 * @param idFruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdFruitore idFruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdFruitore.class, idFruitore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idFruitore</var> of type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param file Xml file to serialize the object <var>idFruitore</var>
	 * @param idFruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdFruitore idFruitore) throws SerializerException {
		this.objToXml(file, IdFruitore.class, idFruitore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idFruitore</var> of type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param file Xml file to serialize the object <var>idFruitore</var>
	 * @param idFruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdFruitore idFruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdFruitore.class, idFruitore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idFruitore</var> of type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>idFruitore</var>
	 * @param idFruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdFruitore idFruitore) throws SerializerException {
		this.objToXml(out, IdFruitore.class, idFruitore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idFruitore</var> of type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>idFruitore</var>
	 * @param idFruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdFruitore idFruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdFruitore.class, idFruitore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idFruitore</var> of type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param idFruitore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdFruitore idFruitore) throws SerializerException {
		return this.objToXml(IdFruitore.class, idFruitore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idFruitore</var> of type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param idFruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdFruitore idFruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdFruitore.class, idFruitore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idFruitore</var> of type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param idFruitore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdFruitore idFruitore) throws SerializerException {
		return this.objToXml(IdFruitore.class, idFruitore, false).toString();
	}
	/**
	 * Serialize to String the object <var>idFruitore</var> of type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param idFruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdFruitore idFruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdFruitore.class, idFruitore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-operation
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idOperation</var> of type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param fileName Xml file to serialize the object <var>idOperation</var>
	 * @param idOperation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdOperation idOperation) throws SerializerException {
		this.objToXml(fileName, IdOperation.class, idOperation, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idOperation</var> of type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param fileName Xml file to serialize the object <var>idOperation</var>
	 * @param idOperation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdOperation idOperation,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdOperation.class, idOperation, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idOperation</var> of type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param file Xml file to serialize the object <var>idOperation</var>
	 * @param idOperation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdOperation idOperation) throws SerializerException {
		this.objToXml(file, IdOperation.class, idOperation, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idOperation</var> of type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param file Xml file to serialize the object <var>idOperation</var>
	 * @param idOperation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdOperation idOperation,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdOperation.class, idOperation, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idOperation</var> of type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param out OutputStream to serialize the object <var>idOperation</var>
	 * @param idOperation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdOperation idOperation) throws SerializerException {
		this.objToXml(out, IdOperation.class, idOperation, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idOperation</var> of type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param out OutputStream to serialize the object <var>idOperation</var>
	 * @param idOperation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdOperation idOperation,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdOperation.class, idOperation, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idOperation</var> of type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param idOperation Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdOperation idOperation) throws SerializerException {
		return this.objToXml(IdOperation.class, idOperation, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idOperation</var> of type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param idOperation Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdOperation idOperation,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdOperation.class, idOperation, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idOperation</var> of type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param idOperation Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdOperation idOperation) throws SerializerException {
		return this.objToXml(IdOperation.class, idOperation, false).toString();
	}
	/**
	 * Serialize to String the object <var>idOperation</var> of type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param idOperation Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdOperation idOperation,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdOperation.class, idOperation, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.commons.search.Soggetto}
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
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.commons.search.Soggetto}
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
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.commons.search.Soggetto}
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
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.commons.search.Soggetto}
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
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: gruppo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Gruppo gruppo) throws SerializerException {
		this.objToXml(fileName, Gruppo.class, gruppo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Gruppo gruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Gruppo.class, gruppo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param file Xml file to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Gruppo gruppo) throws SerializerException {
		this.objToXml(file, Gruppo.class, gruppo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param file Xml file to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Gruppo gruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Gruppo.class, gruppo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Gruppo gruppo) throws SerializerException {
		this.objToXml(out, Gruppo.class, gruppo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Gruppo gruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Gruppo.class, gruppo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>gruppo</var> of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param gruppo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Gruppo gruppo) throws SerializerException {
		return this.objToXml(Gruppo.class, gruppo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>gruppo</var> of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param gruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Gruppo gruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Gruppo.class, gruppo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>gruppo</var> of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param gruppo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Gruppo gruppo) throws SerializerException {
		return this.objToXml(Gruppo.class, gruppo, false).toString();
	}
	/**
	 * Serialize to String the object <var>gruppo</var> of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param gruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Gruppo gruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Gruppo.class, gruppo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-dominio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDominio portaDominio) throws SerializerException {
		this.objToXml(fileName, PortaDominio.class, portaDominio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDominio portaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDominio.class, portaDominio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param file Xml file to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDominio portaDominio) throws SerializerException {
		this.objToXml(file, PortaDominio.class, portaDominio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param file Xml file to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDominio portaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDominio.class, portaDominio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDominio portaDominio) throws SerializerException {
		this.objToXml(out, PortaDominio.class, portaDominio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDominio portaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDominio.class, portaDominio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDominio</var> of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param portaDominio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDominio portaDominio) throws SerializerException {
		return this.objToXml(PortaDominio.class, portaDominio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDominio</var> of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param portaDominio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDominio portaDominio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDominio.class, portaDominio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDominio</var> of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param portaDominio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDominio portaDominio) throws SerializerException {
		return this.objToXml(PortaDominio.class, portaDominio, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDominio</var> of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param portaDominio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDominio portaDominio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDominio.class, portaDominio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-applicativa
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaApplicativa idPortaApplicativa) throws SerializerException {
		this.objToXml(fileName, IdPortaApplicativa.class, idPortaApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaApplicativa idPortaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdPortaApplicativa.class, idPortaApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaApplicativa idPortaApplicativa) throws SerializerException {
		this.objToXml(file, IdPortaApplicativa.class, idPortaApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaApplicativa idPortaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdPortaApplicativa.class, idPortaApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaApplicativa idPortaApplicativa) throws SerializerException {
		this.objToXml(out, IdPortaApplicativa.class, idPortaApplicativa, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaApplicativa idPortaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdPortaApplicativa.class, idPortaApplicativa, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param idPortaApplicativa Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaApplicativa idPortaApplicativa) throws SerializerException {
		return this.objToXml(IdPortaApplicativa.class, idPortaApplicativa, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param idPortaApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaApplicativa idPortaApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaApplicativa.class, idPortaApplicativa, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param idPortaApplicativa Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaApplicativa idPortaApplicativa) throws SerializerException {
		return this.objToXml(IdPortaApplicativa.class, idPortaApplicativa, false).toString();
	}
	/**
	 * Serialize to String the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param idPortaApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaApplicativa idPortaApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaApplicativa.class, idPortaApplicativa, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-resource
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idResource</var> of type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param fileName Xml file to serialize the object <var>idResource</var>
	 * @param idResource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdResource idResource) throws SerializerException {
		this.objToXml(fileName, IdResource.class, idResource, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idResource</var> of type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param fileName Xml file to serialize the object <var>idResource</var>
	 * @param idResource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdResource idResource,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdResource.class, idResource, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idResource</var> of type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param file Xml file to serialize the object <var>idResource</var>
	 * @param idResource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdResource idResource) throws SerializerException {
		this.objToXml(file, IdResource.class, idResource, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idResource</var> of type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param file Xml file to serialize the object <var>idResource</var>
	 * @param idResource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdResource idResource,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdResource.class, idResource, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idResource</var> of type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param out OutputStream to serialize the object <var>idResource</var>
	 * @param idResource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdResource idResource) throws SerializerException {
		this.objToXml(out, IdResource.class, idResource, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idResource</var> of type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param out OutputStream to serialize the object <var>idResource</var>
	 * @param idResource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdResource idResource,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdResource.class, idResource, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idResource</var> of type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param idResource Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdResource idResource) throws SerializerException {
		return this.objToXml(IdResource.class, idResource, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idResource</var> of type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param idResource Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdResource idResource,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdResource.class, idResource, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idResource</var> of type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param idResource Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdResource idResource) throws SerializerException {
		return this.objToXml(IdResource.class, idResource, false).toString();
	}
	/**
	 * Serialize to String the object <var>idResource</var> of type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param idResource Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdResource idResource,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdResource.class, idResource, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo) throws SerializerException {
		this.objToXml(fileName, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo) throws SerializerException {
		this.objToXml(file, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo) throws SerializerException {
		this.objToXml(out, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param portaDelegataServizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataServizioApplicativo portaDelegataServizioApplicativo) throws SerializerException {
		return this.objToXml(PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param portaDelegataServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataServizioApplicativo portaDelegataServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param portaDelegataServizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataServizioApplicativo portaDelegataServizioApplicativo) throws SerializerException {
		return this.objToXml(PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param portaDelegataServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataServizioApplicativo portaDelegataServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: fruitore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Fruitore fruitore) throws SerializerException {
		this.objToXml(fileName, Fruitore.class, fruitore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Fruitore.class, fruitore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param file Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Fruitore fruitore) throws SerializerException {
		this.objToXml(file, Fruitore.class, fruitore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param file Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Fruitore.class, fruitore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Fruitore fruitore) throws SerializerException {
		this.objToXml(out, Fruitore.class, fruitore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Fruitore.class, fruitore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fruitore</var> of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Fruitore fruitore) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fruitore</var> of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fruitore</var> of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Fruitore fruitore) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, false).toString();
	}
	/**
	 * Serialize to String the object <var>fruitore</var> of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegata portaDelegata) throws SerializerException {
		this.objToXml(fileName, PortaDelegata.class, portaDelegata, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegata portaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDelegata.class, portaDelegata, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegata portaDelegata) throws SerializerException {
		this.objToXml(file, PortaDelegata.class, portaDelegata, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegata portaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDelegata.class, portaDelegata, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegata portaDelegata) throws SerializerException {
		this.objToXml(out, PortaDelegata.class, portaDelegata, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegata portaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDelegata.class, portaDelegata, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param portaDelegata Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegata portaDelegata) throws SerializerException {
		return this.objToXml(PortaDelegata.class, portaDelegata, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param portaDelegata Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegata portaDelegata,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegata.class, portaDelegata, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param portaDelegata Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegata portaDelegata) throws SerializerException {
		return this.objToXml(PortaDelegata.class, portaDelegata, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param portaDelegata Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegata portaDelegata,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegata.class, portaDelegata, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-soggetto-ruolo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idSoggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idSoggettoRuolo</var>
	 * @param idSoggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdSoggettoRuolo idSoggettoRuolo) throws SerializerException {
		this.objToXml(fileName, IdSoggettoRuolo.class, idSoggettoRuolo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idSoggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idSoggettoRuolo</var>
	 * @param idSoggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdSoggettoRuolo idSoggettoRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdSoggettoRuolo.class, idSoggettoRuolo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idSoggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param file Xml file to serialize the object <var>idSoggettoRuolo</var>
	 * @param idSoggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdSoggettoRuolo idSoggettoRuolo) throws SerializerException {
		this.objToXml(file, IdSoggettoRuolo.class, idSoggettoRuolo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idSoggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param file Xml file to serialize the object <var>idSoggettoRuolo</var>
	 * @param idSoggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdSoggettoRuolo idSoggettoRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdSoggettoRuolo.class, idSoggettoRuolo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idSoggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param out OutputStream to serialize the object <var>idSoggettoRuolo</var>
	 * @param idSoggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdSoggettoRuolo idSoggettoRuolo) throws SerializerException {
		this.objToXml(out, IdSoggettoRuolo.class, idSoggettoRuolo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idSoggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param out OutputStream to serialize the object <var>idSoggettoRuolo</var>
	 * @param idSoggettoRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdSoggettoRuolo idSoggettoRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdSoggettoRuolo.class, idSoggettoRuolo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idSoggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param idSoggettoRuolo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdSoggettoRuolo idSoggettoRuolo) throws SerializerException {
		return this.objToXml(IdSoggettoRuolo.class, idSoggettoRuolo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idSoggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param idSoggettoRuolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdSoggettoRuolo idSoggettoRuolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdSoggettoRuolo.class, idSoggettoRuolo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idSoggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param idSoggettoRuolo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdSoggettoRuolo idSoggettoRuolo) throws SerializerException {
		return this.objToXml(IdSoggettoRuolo.class, idSoggettoRuolo, false).toString();
	}
	/**
	 * Serialize to String the object <var>idSoggettoRuolo</var> of type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param idSoggettoRuolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdSoggettoRuolo idSoggettoRuolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdSoggettoRuolo.class, idSoggettoRuolo, prettyPrint).toString();
	}
	
	
	

}
