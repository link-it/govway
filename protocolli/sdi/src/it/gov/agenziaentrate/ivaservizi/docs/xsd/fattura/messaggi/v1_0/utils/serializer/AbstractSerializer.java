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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType;

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
				// close
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
				// close
			}
			try{
				fout.close();
			}catch(Exception e){
				// close
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
				// close
			}
			try{
				bout.close();
			}catch(Exception e){
				// close
			}
		}
		return bout;
	}




	/*
	 =================================================================================
	 Object: RicevutaConsegna_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>ricevutaConsegnaType</var>
	 * @param ricevutaConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RicevutaConsegnaType ricevutaConsegnaType) throws SerializerException {
		this.objToXml(fileName, RicevutaConsegnaType.class, ricevutaConsegnaType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>ricevutaConsegnaType</var>
	 * @param ricevutaConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RicevutaConsegnaType ricevutaConsegnaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RicevutaConsegnaType.class, ricevutaConsegnaType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param file Xml file to serialize the object <var>ricevutaConsegnaType</var>
	 * @param ricevutaConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RicevutaConsegnaType ricevutaConsegnaType) throws SerializerException {
		this.objToXml(file, RicevutaConsegnaType.class, ricevutaConsegnaType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param file Xml file to serialize the object <var>ricevutaConsegnaType</var>
	 * @param ricevutaConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RicevutaConsegnaType ricevutaConsegnaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RicevutaConsegnaType.class, ricevutaConsegnaType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param out OutputStream to serialize the object <var>ricevutaConsegnaType</var>
	 * @param ricevutaConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RicevutaConsegnaType ricevutaConsegnaType) throws SerializerException {
		this.objToXml(out, RicevutaConsegnaType.class, ricevutaConsegnaType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param out OutputStream to serialize the object <var>ricevutaConsegnaType</var>
	 * @param ricevutaConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RicevutaConsegnaType ricevutaConsegnaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RicevutaConsegnaType.class, ricevutaConsegnaType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>ricevutaConsegnaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param ricevutaConsegnaType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RicevutaConsegnaType ricevutaConsegnaType) throws SerializerException {
		return this.objToXml(RicevutaConsegnaType.class, ricevutaConsegnaType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>ricevutaConsegnaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param ricevutaConsegnaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RicevutaConsegnaType ricevutaConsegnaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RicevutaConsegnaType.class, ricevutaConsegnaType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>ricevutaConsegnaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param ricevutaConsegnaType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RicevutaConsegnaType ricevutaConsegnaType) throws SerializerException {
		return this.objToXml(RicevutaConsegnaType.class, ricevutaConsegnaType, false).toString();
	}
	/**
	 * Serialize to String the object <var>ricevutaConsegnaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param ricevutaConsegnaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RicevutaConsegnaType ricevutaConsegnaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RicevutaConsegnaType.class, ricevutaConsegnaType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Destinatario_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>destinatarioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param fileName Xml file to serialize the object <var>destinatarioType</var>
	 * @param destinatarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DestinatarioType destinatarioType) throws SerializerException {
		this.objToXml(fileName, DestinatarioType.class, destinatarioType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>destinatarioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param fileName Xml file to serialize the object <var>destinatarioType</var>
	 * @param destinatarioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DestinatarioType destinatarioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DestinatarioType.class, destinatarioType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>destinatarioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param file Xml file to serialize the object <var>destinatarioType</var>
	 * @param destinatarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DestinatarioType destinatarioType) throws SerializerException {
		this.objToXml(file, DestinatarioType.class, destinatarioType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>destinatarioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param file Xml file to serialize the object <var>destinatarioType</var>
	 * @param destinatarioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DestinatarioType destinatarioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DestinatarioType.class, destinatarioType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>destinatarioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param out OutputStream to serialize the object <var>destinatarioType</var>
	 * @param destinatarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DestinatarioType destinatarioType) throws SerializerException {
		this.objToXml(out, DestinatarioType.class, destinatarioType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>destinatarioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param out OutputStream to serialize the object <var>destinatarioType</var>
	 * @param destinatarioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DestinatarioType destinatarioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DestinatarioType.class, destinatarioType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>destinatarioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param destinatarioType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DestinatarioType destinatarioType) throws SerializerException {
		return this.objToXml(DestinatarioType.class, destinatarioType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>destinatarioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param destinatarioType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DestinatarioType destinatarioType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DestinatarioType.class, destinatarioType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>destinatarioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param destinatarioType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DestinatarioType destinatarioType) throws SerializerException {
		return this.objToXml(DestinatarioType.class, destinatarioType, false).toString();
	}
	/**
	 * Serialize to String the object <var>destinatarioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param destinatarioType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DestinatarioType destinatarioType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DestinatarioType.class, destinatarioType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoArchivio_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoArchivioType</var>
	 * @param riferimentoArchivioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoArchivioType riferimentoArchivioType) throws SerializerException {
		this.objToXml(fileName, RiferimentoArchivioType.class, riferimentoArchivioType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoArchivioType</var>
	 * @param riferimentoArchivioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoArchivioType riferimentoArchivioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RiferimentoArchivioType.class, riferimentoArchivioType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoArchivioType</var>
	 * @param riferimentoArchivioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoArchivioType riferimentoArchivioType) throws SerializerException {
		this.objToXml(file, RiferimentoArchivioType.class, riferimentoArchivioType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoArchivioType</var>
	 * @param riferimentoArchivioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoArchivioType riferimentoArchivioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RiferimentoArchivioType.class, riferimentoArchivioType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoArchivioType</var>
	 * @param riferimentoArchivioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoArchivioType riferimentoArchivioType) throws SerializerException {
		this.objToXml(out, RiferimentoArchivioType.class, riferimentoArchivioType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoArchivioType</var>
	 * @param riferimentoArchivioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoArchivioType riferimentoArchivioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RiferimentoArchivioType.class, riferimentoArchivioType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>riferimentoArchivioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param riferimentoArchivioType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoArchivioType riferimentoArchivioType) throws SerializerException {
		return this.objToXml(RiferimentoArchivioType.class, riferimentoArchivioType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riferimentoArchivioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param riferimentoArchivioType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoArchivioType riferimentoArchivioType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoArchivioType.class, riferimentoArchivioType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>riferimentoArchivioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param riferimentoArchivioType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoArchivioType riferimentoArchivioType) throws SerializerException {
		return this.objToXml(RiferimentoArchivioType.class, riferimentoArchivioType, false).toString();
	}
	/**
	 * Serialize to String the object <var>riferimentoArchivioType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param riferimentoArchivioType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoArchivioType riferimentoArchivioType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoArchivioType.class, riferimentoArchivioType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Errore_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>erroreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param fileName Xml file to serialize the object <var>erroreType</var>
	 * @param erroreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErroreType erroreType) throws SerializerException {
		this.objToXml(fileName, ErroreType.class, erroreType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>erroreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param fileName Xml file to serialize the object <var>erroreType</var>
	 * @param erroreType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErroreType erroreType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ErroreType.class, erroreType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>erroreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param file Xml file to serialize the object <var>erroreType</var>
	 * @param erroreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErroreType erroreType) throws SerializerException {
		this.objToXml(file, ErroreType.class, erroreType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>erroreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param file Xml file to serialize the object <var>erroreType</var>
	 * @param erroreType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErroreType erroreType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ErroreType.class, erroreType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>erroreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param out OutputStream to serialize the object <var>erroreType</var>
	 * @param erroreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErroreType erroreType) throws SerializerException {
		this.objToXml(out, ErroreType.class, erroreType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>erroreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param out OutputStream to serialize the object <var>erroreType</var>
	 * @param erroreType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErroreType erroreType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ErroreType.class, erroreType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>erroreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param erroreType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErroreType erroreType) throws SerializerException {
		return this.objToXml(ErroreType.class, erroreType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>erroreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param erroreType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErroreType erroreType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErroreType.class, erroreType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>erroreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param erroreType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErroreType erroreType) throws SerializerException {
		return this.objToXml(ErroreType.class, erroreType, false).toString();
	}
	/**
	 * Serialize to String the object <var>erroreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param erroreType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErroreType erroreType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErroreType.class, erroreType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ListaErrori_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>listaErroriType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param fileName Xml file to serialize the object <var>listaErroriType</var>
	 * @param listaErroriType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ListaErroriType listaErroriType) throws SerializerException {
		this.objToXml(fileName, ListaErroriType.class, listaErroriType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>listaErroriType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param fileName Xml file to serialize the object <var>listaErroriType</var>
	 * @param listaErroriType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ListaErroriType listaErroriType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ListaErroriType.class, listaErroriType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>listaErroriType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param file Xml file to serialize the object <var>listaErroriType</var>
	 * @param listaErroriType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ListaErroriType listaErroriType) throws SerializerException {
		this.objToXml(file, ListaErroriType.class, listaErroriType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>listaErroriType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param file Xml file to serialize the object <var>listaErroriType</var>
	 * @param listaErroriType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ListaErroriType listaErroriType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ListaErroriType.class, listaErroriType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>listaErroriType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param out OutputStream to serialize the object <var>listaErroriType</var>
	 * @param listaErroriType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ListaErroriType listaErroriType) throws SerializerException {
		this.objToXml(out, ListaErroriType.class, listaErroriType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>listaErroriType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param out OutputStream to serialize the object <var>listaErroriType</var>
	 * @param listaErroriType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ListaErroriType listaErroriType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ListaErroriType.class, listaErroriType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>listaErroriType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param listaErroriType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ListaErroriType listaErroriType) throws SerializerException {
		return this.objToXml(ListaErroriType.class, listaErroriType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>listaErroriType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param listaErroriType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ListaErroriType listaErroriType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ListaErroriType.class, listaErroriType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>listaErroriType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param listaErroriType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ListaErroriType listaErroriType) throws SerializerException {
		return this.objToXml(ListaErroriType.class, listaErroriType, false).toString();
	}
	/**
	 * Serialize to String the object <var>listaErroriType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param listaErroriType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ListaErroriType listaErroriType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ListaErroriType.class, listaErroriType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: FileMetadati_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileMetadatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fileMetadatiType</var>
	 * @param fileMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FileMetadatiType fileMetadatiType) throws SerializerException {
		this.objToXml(fileName, FileMetadatiType.class, fileMetadatiType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileMetadatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fileMetadatiType</var>
	 * @param fileMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FileMetadatiType fileMetadatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FileMetadatiType.class, fileMetadatiType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fileMetadatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param file Xml file to serialize the object <var>fileMetadatiType</var>
	 * @param fileMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FileMetadatiType fileMetadatiType) throws SerializerException {
		this.objToXml(file, FileMetadatiType.class, fileMetadatiType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fileMetadatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param file Xml file to serialize the object <var>fileMetadatiType</var>
	 * @param fileMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FileMetadatiType fileMetadatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FileMetadatiType.class, fileMetadatiType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fileMetadatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param out OutputStream to serialize the object <var>fileMetadatiType</var>
	 * @param fileMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FileMetadatiType fileMetadatiType) throws SerializerException {
		this.objToXml(out, FileMetadatiType.class, fileMetadatiType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fileMetadatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param out OutputStream to serialize the object <var>fileMetadatiType</var>
	 * @param fileMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FileMetadatiType fileMetadatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FileMetadatiType.class, fileMetadatiType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fileMetadatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param fileMetadatiType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FileMetadatiType fileMetadatiType) throws SerializerException {
		return this.objToXml(FileMetadatiType.class, fileMetadatiType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fileMetadatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param fileMetadatiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FileMetadatiType fileMetadatiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FileMetadatiType.class, fileMetadatiType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fileMetadatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param fileMetadatiType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FileMetadatiType fileMetadatiType) throws SerializerException {
		return this.objToXml(FileMetadatiType.class, fileMetadatiType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fileMetadatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param fileMetadatiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FileMetadatiType fileMetadatiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FileMetadatiType.class, fileMetadatiType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoFattura_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoFatturaType</var>
	 * @param riferimentoFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoFatturaType riferimentoFatturaType) throws SerializerException {
		this.objToXml(fileName, RiferimentoFatturaType.class, riferimentoFatturaType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoFatturaType</var>
	 * @param riferimentoFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoFatturaType riferimentoFatturaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RiferimentoFatturaType.class, riferimentoFatturaType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoFatturaType</var>
	 * @param riferimentoFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoFatturaType riferimentoFatturaType) throws SerializerException {
		this.objToXml(file, RiferimentoFatturaType.class, riferimentoFatturaType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoFatturaType</var>
	 * @param riferimentoFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoFatturaType riferimentoFatturaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RiferimentoFatturaType.class, riferimentoFatturaType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoFatturaType</var>
	 * @param riferimentoFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoFatturaType riferimentoFatturaType) throws SerializerException {
		this.objToXml(out, RiferimentoFatturaType.class, riferimentoFatturaType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoFatturaType</var>
	 * @param riferimentoFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoFatturaType riferimentoFatturaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RiferimentoFatturaType.class, riferimentoFatturaType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>riferimentoFatturaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param riferimentoFatturaType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoFatturaType riferimentoFatturaType) throws SerializerException {
		return this.objToXml(RiferimentoFatturaType.class, riferimentoFatturaType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riferimentoFatturaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param riferimentoFatturaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoFatturaType riferimentoFatturaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoFatturaType.class, riferimentoFatturaType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>riferimentoFatturaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param riferimentoFatturaType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoFatturaType riferimentoFatturaType) throws SerializerException {
		return this.objToXml(RiferimentoFatturaType.class, riferimentoFatturaType, false).toString();
	}
	/**
	 * Serialize to String the object <var>riferimentoFatturaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param riferimentoFatturaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoFatturaType riferimentoFatturaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoFatturaType.class, riferimentoFatturaType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RicevutaScarto_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ricevutaScartoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>ricevutaScartoType</var>
	 * @param ricevutaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RicevutaScartoType ricevutaScartoType) throws SerializerException {
		this.objToXml(fileName, RicevutaScartoType.class, ricevutaScartoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ricevutaScartoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>ricevutaScartoType</var>
	 * @param ricevutaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RicevutaScartoType ricevutaScartoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RicevutaScartoType.class, ricevutaScartoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>ricevutaScartoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param file Xml file to serialize the object <var>ricevutaScartoType</var>
	 * @param ricevutaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RicevutaScartoType ricevutaScartoType) throws SerializerException {
		this.objToXml(file, RicevutaScartoType.class, ricevutaScartoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>ricevutaScartoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param file Xml file to serialize the object <var>ricevutaScartoType</var>
	 * @param ricevutaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RicevutaScartoType ricevutaScartoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RicevutaScartoType.class, ricevutaScartoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>ricevutaScartoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param out OutputStream to serialize the object <var>ricevutaScartoType</var>
	 * @param ricevutaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RicevutaScartoType ricevutaScartoType) throws SerializerException {
		this.objToXml(out, RicevutaScartoType.class, ricevutaScartoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>ricevutaScartoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param out OutputStream to serialize the object <var>ricevutaScartoType</var>
	 * @param ricevutaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RicevutaScartoType ricevutaScartoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RicevutaScartoType.class, ricevutaScartoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>ricevutaScartoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param ricevutaScartoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RicevutaScartoType ricevutaScartoType) throws SerializerException {
		return this.objToXml(RicevutaScartoType.class, ricevutaScartoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>ricevutaScartoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param ricevutaScartoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RicevutaScartoType ricevutaScartoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RicevutaScartoType.class, ricevutaScartoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>ricevutaScartoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param ricevutaScartoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RicevutaScartoType ricevutaScartoType) throws SerializerException {
		return this.objToXml(RicevutaScartoType.class, ricevutaScartoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>ricevutaScartoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param ricevutaScartoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RicevutaScartoType ricevutaScartoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RicevutaScartoType.class, ricevutaScartoType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RicevutaImpossibilitaRecapito_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ricevutaImpossibilitaRecapitoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>ricevutaImpossibilitaRecapitoType</var>
	 * @param ricevutaImpossibilitaRecapitoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapitoType) throws SerializerException {
		this.objToXml(fileName, RicevutaImpossibilitaRecapitoType.class, ricevutaImpossibilitaRecapitoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ricevutaImpossibilitaRecapitoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>ricevutaImpossibilitaRecapitoType</var>
	 * @param ricevutaImpossibilitaRecapitoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapitoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RicevutaImpossibilitaRecapitoType.class, ricevutaImpossibilitaRecapitoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>ricevutaImpossibilitaRecapitoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param file Xml file to serialize the object <var>ricevutaImpossibilitaRecapitoType</var>
	 * @param ricevutaImpossibilitaRecapitoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapitoType) throws SerializerException {
		this.objToXml(file, RicevutaImpossibilitaRecapitoType.class, ricevutaImpossibilitaRecapitoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>ricevutaImpossibilitaRecapitoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param file Xml file to serialize the object <var>ricevutaImpossibilitaRecapitoType</var>
	 * @param ricevutaImpossibilitaRecapitoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapitoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RicevutaImpossibilitaRecapitoType.class, ricevutaImpossibilitaRecapitoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>ricevutaImpossibilitaRecapitoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param out OutputStream to serialize the object <var>ricevutaImpossibilitaRecapitoType</var>
	 * @param ricevutaImpossibilitaRecapitoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapitoType) throws SerializerException {
		this.objToXml(out, RicevutaImpossibilitaRecapitoType.class, ricevutaImpossibilitaRecapitoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>ricevutaImpossibilitaRecapitoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param out OutputStream to serialize the object <var>ricevutaImpossibilitaRecapitoType</var>
	 * @param ricevutaImpossibilitaRecapitoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapitoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RicevutaImpossibilitaRecapitoType.class, ricevutaImpossibilitaRecapitoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>ricevutaImpossibilitaRecapitoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param ricevutaImpossibilitaRecapitoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapitoType) throws SerializerException {
		return this.objToXml(RicevutaImpossibilitaRecapitoType.class, ricevutaImpossibilitaRecapitoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>ricevutaImpossibilitaRecapitoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param ricevutaImpossibilitaRecapitoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapitoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RicevutaImpossibilitaRecapitoType.class, ricevutaImpossibilitaRecapitoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>ricevutaImpossibilitaRecapitoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param ricevutaImpossibilitaRecapitoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapitoType) throws SerializerException {
		return this.objToXml(RicevutaImpossibilitaRecapitoType.class, ricevutaImpossibilitaRecapitoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>ricevutaImpossibilitaRecapitoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param ricevutaImpossibilitaRecapitoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RicevutaImpossibilitaRecapitoType ricevutaImpossibilitaRecapitoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RicevutaImpossibilitaRecapitoType.class, ricevutaImpossibilitaRecapitoType, prettyPrint).toString();
	}
	
	
	

}
