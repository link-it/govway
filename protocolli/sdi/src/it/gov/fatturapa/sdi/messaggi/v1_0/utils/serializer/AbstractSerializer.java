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
package it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType;
import it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType;
import it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType;
import it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType;
import it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType;
import it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType;
import it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType;
import it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType;
import it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType;
import it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType;

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
	 Object: NotificaEsitoCommittente_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>notificaEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>notificaEsitoCommittenteType</var>
	 * @param notificaEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,NotificaEsitoCommittenteType notificaEsitoCommittenteType) throws SerializerException {
		this.objToXml(fileName, NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>notificaEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>notificaEsitoCommittenteType</var>
	 * @param notificaEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,NotificaEsitoCommittenteType notificaEsitoCommittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>notificaEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>notificaEsitoCommittenteType</var>
	 * @param notificaEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,NotificaEsitoCommittenteType notificaEsitoCommittenteType) throws SerializerException {
		this.objToXml(file, NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>notificaEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>notificaEsitoCommittenteType</var>
	 * @param notificaEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,NotificaEsitoCommittenteType notificaEsitoCommittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>notificaEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>notificaEsitoCommittenteType</var>
	 * @param notificaEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,NotificaEsitoCommittenteType notificaEsitoCommittenteType) throws SerializerException {
		this.objToXml(out, NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>notificaEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>notificaEsitoCommittenteType</var>
	 * @param notificaEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,NotificaEsitoCommittenteType notificaEsitoCommittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>notificaEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param notificaEsitoCommittenteType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(NotificaEsitoCommittenteType notificaEsitoCommittenteType) throws SerializerException {
		return this.objToXml(NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>notificaEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param notificaEsitoCommittenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(NotificaEsitoCommittenteType notificaEsitoCommittenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>notificaEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param notificaEsitoCommittenteType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(NotificaEsitoCommittenteType notificaEsitoCommittenteType) throws SerializerException {
		return this.objToXml(NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType, false).toString();
	}
	/**
	 * Serialize to String the object <var>notificaEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param notificaEsitoCommittenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(NotificaEsitoCommittenteType notificaEsitoCommittenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoFattura_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoFatturaType</var>
	 * @param riferimentoFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoFatturaType riferimentoFatturaType) throws SerializerException {
		this.objToXml(fileName, RiferimentoFatturaType.class, riferimentoFatturaType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
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
	 * Serialize to file system in <var>file</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoFatturaType</var>
	 * @param riferimentoFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoFatturaType riferimentoFatturaType) throws SerializerException {
		this.objToXml(file, RiferimentoFatturaType.class, riferimentoFatturaType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
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
	 * Serialize to output stream <var>out</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoFatturaType</var>
	 * @param riferimentoFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoFatturaType riferimentoFatturaType) throws SerializerException {
		this.objToXml(out, RiferimentoFatturaType.class, riferimentoFatturaType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
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
	 * Serialize to byte array the object <var>riferimentoFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param riferimentoFatturaType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoFatturaType riferimentoFatturaType) throws SerializerException {
		return this.objToXml(RiferimentoFatturaType.class, riferimentoFatturaType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riferimentoFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
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
	 * Serialize to String the object <var>riferimentoFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param riferimentoFatturaType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoFatturaType riferimentoFatturaType) throws SerializerException {
		return this.objToXml(RiferimentoFatturaType.class, riferimentoFatturaType, false).toString();
	}
	/**
	 * Serialize to String the object <var>riferimentoFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
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
	 Object: RicevutaConsegna_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>ricevutaConsegnaType</var>
	 * @param ricevutaConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RicevutaConsegnaType ricevutaConsegnaType) throws SerializerException {
		this.objToXml(fileName, RicevutaConsegnaType.class, ricevutaConsegnaType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
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
	 * Serialize to file system in <var>file</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param file Xml file to serialize the object <var>ricevutaConsegnaType</var>
	 * @param ricevutaConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RicevutaConsegnaType ricevutaConsegnaType) throws SerializerException {
		this.objToXml(file, RicevutaConsegnaType.class, ricevutaConsegnaType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
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
	 * Serialize to output stream <var>out</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param out OutputStream to serialize the object <var>ricevutaConsegnaType</var>
	 * @param ricevutaConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RicevutaConsegnaType ricevutaConsegnaType) throws SerializerException {
		this.objToXml(out, RicevutaConsegnaType.class, ricevutaConsegnaType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>ricevutaConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
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
	 * Serialize to byte array the object <var>ricevutaConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param ricevutaConsegnaType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RicevutaConsegnaType ricevutaConsegnaType) throws SerializerException {
		return this.objToXml(RicevutaConsegnaType.class, ricevutaConsegnaType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>ricevutaConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
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
	 * Serialize to String the object <var>ricevutaConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param ricevutaConsegnaType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RicevutaConsegnaType ricevutaConsegnaType) throws SerializerException {
		return this.objToXml(RicevutaConsegnaType.class, ricevutaConsegnaType, false).toString();
	}
	/**
	 * Serialize to String the object <var>ricevutaConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
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
	 * Serialize to file system in <var>fileName</var> the object <var>destinatarioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param fileName Xml file to serialize the object <var>destinatarioType</var>
	 * @param destinatarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DestinatarioType destinatarioType) throws SerializerException {
		this.objToXml(fileName, DestinatarioType.class, destinatarioType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>destinatarioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
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
	 * Serialize to file system in <var>file</var> the object <var>destinatarioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param file Xml file to serialize the object <var>destinatarioType</var>
	 * @param destinatarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DestinatarioType destinatarioType) throws SerializerException {
		this.objToXml(file, DestinatarioType.class, destinatarioType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>destinatarioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
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
	 * Serialize to output stream <var>out</var> the object <var>destinatarioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param out OutputStream to serialize the object <var>destinatarioType</var>
	 * @param destinatarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DestinatarioType destinatarioType) throws SerializerException {
		this.objToXml(out, DestinatarioType.class, destinatarioType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>destinatarioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
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
	 * Serialize to byte array the object <var>destinatarioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param destinatarioType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DestinatarioType destinatarioType) throws SerializerException {
		return this.objToXml(DestinatarioType.class, destinatarioType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>destinatarioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
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
	 * Serialize to String the object <var>destinatarioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param destinatarioType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DestinatarioType destinatarioType) throws SerializerException {
		return this.objToXml(DestinatarioType.class, destinatarioType, false).toString();
	}
	/**
	 * Serialize to String the object <var>destinatarioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
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
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoArchivioType</var>
	 * @param riferimentoArchivioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoArchivioType riferimentoArchivioType) throws SerializerException {
		this.objToXml(fileName, RiferimentoArchivioType.class, riferimentoArchivioType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
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
	 * Serialize to file system in <var>file</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoArchivioType</var>
	 * @param riferimentoArchivioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoArchivioType riferimentoArchivioType) throws SerializerException {
		this.objToXml(file, RiferimentoArchivioType.class, riferimentoArchivioType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
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
	 * Serialize to output stream <var>out</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoArchivioType</var>
	 * @param riferimentoArchivioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoArchivioType riferimentoArchivioType) throws SerializerException {
		this.objToXml(out, RiferimentoArchivioType.class, riferimentoArchivioType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoArchivioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
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
	 * Serialize to byte array the object <var>riferimentoArchivioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param riferimentoArchivioType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoArchivioType riferimentoArchivioType) throws SerializerException {
		return this.objToXml(RiferimentoArchivioType.class, riferimentoArchivioType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riferimentoArchivioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
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
	 * Serialize to String the object <var>riferimentoArchivioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param riferimentoArchivioType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoArchivioType riferimentoArchivioType) throws SerializerException {
		return this.objToXml(RiferimentoArchivioType.class, riferimentoArchivioType, false).toString();
	}
	/**
	 * Serialize to String the object <var>riferimentoArchivioType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
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
	 Object: MetadatiInvioFile_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>metadatiInvioFileType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param fileName Xml file to serialize the object <var>metadatiInvioFileType</var>
	 * @param metadatiInvioFileType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MetadatiInvioFileType metadatiInvioFileType) throws SerializerException {
		this.objToXml(fileName, MetadatiInvioFileType.class, metadatiInvioFileType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>metadatiInvioFileType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param fileName Xml file to serialize the object <var>metadatiInvioFileType</var>
	 * @param metadatiInvioFileType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MetadatiInvioFileType metadatiInvioFileType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MetadatiInvioFileType.class, metadatiInvioFileType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>metadatiInvioFileType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param file Xml file to serialize the object <var>metadatiInvioFileType</var>
	 * @param metadatiInvioFileType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MetadatiInvioFileType metadatiInvioFileType) throws SerializerException {
		this.objToXml(file, MetadatiInvioFileType.class, metadatiInvioFileType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>metadatiInvioFileType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param file Xml file to serialize the object <var>metadatiInvioFileType</var>
	 * @param metadatiInvioFileType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MetadatiInvioFileType metadatiInvioFileType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MetadatiInvioFileType.class, metadatiInvioFileType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>metadatiInvioFileType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param out OutputStream to serialize the object <var>metadatiInvioFileType</var>
	 * @param metadatiInvioFileType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MetadatiInvioFileType metadatiInvioFileType) throws SerializerException {
		this.objToXml(out, MetadatiInvioFileType.class, metadatiInvioFileType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>metadatiInvioFileType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param out OutputStream to serialize the object <var>metadatiInvioFileType</var>
	 * @param metadatiInvioFileType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MetadatiInvioFileType metadatiInvioFileType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MetadatiInvioFileType.class, metadatiInvioFileType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>metadatiInvioFileType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param metadatiInvioFileType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MetadatiInvioFileType metadatiInvioFileType) throws SerializerException {
		return this.objToXml(MetadatiInvioFileType.class, metadatiInvioFileType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>metadatiInvioFileType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param metadatiInvioFileType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MetadatiInvioFileType metadatiInvioFileType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MetadatiInvioFileType.class, metadatiInvioFileType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>metadatiInvioFileType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param metadatiInvioFileType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MetadatiInvioFileType metadatiInvioFileType) throws SerializerException {
		return this.objToXml(MetadatiInvioFileType.class, metadatiInvioFileType, false).toString();
	}
	/**
	 * Serialize to String the object <var>metadatiInvioFileType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param metadatiInvioFileType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MetadatiInvioFileType metadatiInvioFileType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MetadatiInvioFileType.class, metadatiInvioFileType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: NotificaDecorrenzaTermini_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>notificaDecorrenzaTerminiType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>notificaDecorrenzaTerminiType</var>
	 * @param notificaDecorrenzaTerminiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType) throws SerializerException {
		this.objToXml(fileName, NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTerminiType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>notificaDecorrenzaTerminiType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>notificaDecorrenzaTerminiType</var>
	 * @param notificaDecorrenzaTerminiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTerminiType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>notificaDecorrenzaTerminiType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param file Xml file to serialize the object <var>notificaDecorrenzaTerminiType</var>
	 * @param notificaDecorrenzaTerminiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType) throws SerializerException {
		this.objToXml(file, NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTerminiType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>notificaDecorrenzaTerminiType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param file Xml file to serialize the object <var>notificaDecorrenzaTerminiType</var>
	 * @param notificaDecorrenzaTerminiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTerminiType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>notificaDecorrenzaTerminiType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param out OutputStream to serialize the object <var>notificaDecorrenzaTerminiType</var>
	 * @param notificaDecorrenzaTerminiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType) throws SerializerException {
		this.objToXml(out, NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTerminiType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>notificaDecorrenzaTerminiType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param out OutputStream to serialize the object <var>notificaDecorrenzaTerminiType</var>
	 * @param notificaDecorrenzaTerminiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTerminiType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>notificaDecorrenzaTerminiType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param notificaDecorrenzaTerminiType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType) throws SerializerException {
		return this.objToXml(NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTerminiType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>notificaDecorrenzaTerminiType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param notificaDecorrenzaTerminiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTerminiType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>notificaDecorrenzaTerminiType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param notificaDecorrenzaTerminiType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType) throws SerializerException {
		return this.objToXml(NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTerminiType, false).toString();
	}
	/**
	 * Serialize to String the object <var>notificaDecorrenzaTerminiType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param notificaDecorrenzaTerminiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTerminiType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ScartoEsitoCommittente_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>scartoEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>scartoEsitoCommittenteType</var>
	 * @param scartoEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ScartoEsitoCommittenteType scartoEsitoCommittenteType) throws SerializerException {
		this.objToXml(fileName, ScartoEsitoCommittenteType.class, scartoEsitoCommittenteType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>scartoEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>scartoEsitoCommittenteType</var>
	 * @param scartoEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ScartoEsitoCommittenteType scartoEsitoCommittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ScartoEsitoCommittenteType.class, scartoEsitoCommittenteType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>scartoEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>scartoEsitoCommittenteType</var>
	 * @param scartoEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ScartoEsitoCommittenteType scartoEsitoCommittenteType) throws SerializerException {
		this.objToXml(file, ScartoEsitoCommittenteType.class, scartoEsitoCommittenteType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>scartoEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>scartoEsitoCommittenteType</var>
	 * @param scartoEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ScartoEsitoCommittenteType scartoEsitoCommittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ScartoEsitoCommittenteType.class, scartoEsitoCommittenteType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>scartoEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>scartoEsitoCommittenteType</var>
	 * @param scartoEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ScartoEsitoCommittenteType scartoEsitoCommittenteType) throws SerializerException {
		this.objToXml(out, ScartoEsitoCommittenteType.class, scartoEsitoCommittenteType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>scartoEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>scartoEsitoCommittenteType</var>
	 * @param scartoEsitoCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ScartoEsitoCommittenteType scartoEsitoCommittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ScartoEsitoCommittenteType.class, scartoEsitoCommittenteType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>scartoEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param scartoEsitoCommittenteType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ScartoEsitoCommittenteType scartoEsitoCommittenteType) throws SerializerException {
		return this.objToXml(ScartoEsitoCommittenteType.class, scartoEsitoCommittenteType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>scartoEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param scartoEsitoCommittenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ScartoEsitoCommittenteType scartoEsitoCommittenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ScartoEsitoCommittenteType.class, scartoEsitoCommittenteType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>scartoEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param scartoEsitoCommittenteType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ScartoEsitoCommittenteType scartoEsitoCommittenteType) throws SerializerException {
		return this.objToXml(ScartoEsitoCommittenteType.class, scartoEsitoCommittenteType, false).toString();
	}
	/**
	 * Serialize to String the object <var>scartoEsitoCommittenteType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param scartoEsitoCommittenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ScartoEsitoCommittenteType scartoEsitoCommittenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ScartoEsitoCommittenteType.class, scartoEsitoCommittenteType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Errore_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>erroreType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * 
	 * @param fileName Xml file to serialize the object <var>erroreType</var>
	 * @param erroreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErroreType erroreType) throws SerializerException {
		this.objToXml(fileName, ErroreType.class, erroreType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>erroreType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
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
	 * Serialize to file system in <var>file</var> the object <var>erroreType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * 
	 * @param file Xml file to serialize the object <var>erroreType</var>
	 * @param erroreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErroreType erroreType) throws SerializerException {
		this.objToXml(file, ErroreType.class, erroreType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>erroreType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
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
	 * Serialize to output stream <var>out</var> the object <var>erroreType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * 
	 * @param out OutputStream to serialize the object <var>erroreType</var>
	 * @param erroreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErroreType erroreType) throws SerializerException {
		this.objToXml(out, ErroreType.class, erroreType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>erroreType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
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
	 * Serialize to byte array the object <var>erroreType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * 
	 * @param erroreType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErroreType erroreType) throws SerializerException {
		return this.objToXml(ErroreType.class, erroreType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>erroreType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
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
	 * Serialize to String the object <var>erroreType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * 
	 * @param erroreType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErroreType erroreType) throws SerializerException {
		return this.objToXml(ErroreType.class, erroreType, false).toString();
	}
	/**
	 * Serialize to String the object <var>erroreType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
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
	 * Serialize to file system in <var>fileName</var> the object <var>listaErroriType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param fileName Xml file to serialize the object <var>listaErroriType</var>
	 * @param listaErroriType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ListaErroriType listaErroriType) throws SerializerException {
		this.objToXml(fileName, ListaErroriType.class, listaErroriType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>listaErroriType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
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
	 * Serialize to file system in <var>file</var> the object <var>listaErroriType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param file Xml file to serialize the object <var>listaErroriType</var>
	 * @param listaErroriType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ListaErroriType listaErroriType) throws SerializerException {
		this.objToXml(file, ListaErroriType.class, listaErroriType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>listaErroriType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
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
	 * Serialize to output stream <var>out</var> the object <var>listaErroriType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param out OutputStream to serialize the object <var>listaErroriType</var>
	 * @param listaErroriType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ListaErroriType listaErroriType) throws SerializerException {
		this.objToXml(out, ListaErroriType.class, listaErroriType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>listaErroriType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
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
	 * Serialize to byte array the object <var>listaErroriType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param listaErroriType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ListaErroriType listaErroriType) throws SerializerException {
		return this.objToXml(ListaErroriType.class, listaErroriType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>listaErroriType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
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
	 * Serialize to String the object <var>listaErroriType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param listaErroriType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ListaErroriType listaErroriType) throws SerializerException {
		return this.objToXml(ListaErroriType.class, listaErroriType, false).toString();
	}
	/**
	 * Serialize to String the object <var>listaErroriType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
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
	 Object: NotificaEsito_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>notificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>notificaEsitoType</var>
	 * @param notificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,NotificaEsitoType notificaEsitoType) throws SerializerException {
		this.objToXml(fileName, NotificaEsitoType.class, notificaEsitoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>notificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>notificaEsitoType</var>
	 * @param notificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,NotificaEsitoType notificaEsitoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, NotificaEsitoType.class, notificaEsitoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>notificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param file Xml file to serialize the object <var>notificaEsitoType</var>
	 * @param notificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,NotificaEsitoType notificaEsitoType) throws SerializerException {
		this.objToXml(file, NotificaEsitoType.class, notificaEsitoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>notificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param file Xml file to serialize the object <var>notificaEsitoType</var>
	 * @param notificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,NotificaEsitoType notificaEsitoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, NotificaEsitoType.class, notificaEsitoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>notificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param out OutputStream to serialize the object <var>notificaEsitoType</var>
	 * @param notificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,NotificaEsitoType notificaEsitoType) throws SerializerException {
		this.objToXml(out, NotificaEsitoType.class, notificaEsitoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>notificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param out OutputStream to serialize the object <var>notificaEsitoType</var>
	 * @param notificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,NotificaEsitoType notificaEsitoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, NotificaEsitoType.class, notificaEsitoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>notificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param notificaEsitoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(NotificaEsitoType notificaEsitoType) throws SerializerException {
		return this.objToXml(NotificaEsitoType.class, notificaEsitoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>notificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param notificaEsitoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(NotificaEsitoType notificaEsitoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(NotificaEsitoType.class, notificaEsitoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>notificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param notificaEsitoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(NotificaEsitoType notificaEsitoType) throws SerializerException {
		return this.objToXml(NotificaEsitoType.class, notificaEsitoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>notificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param notificaEsitoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(NotificaEsitoType notificaEsitoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(NotificaEsitoType.class, notificaEsitoType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: NotificaScarto_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>notificaScartoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>notificaScartoType</var>
	 * @param notificaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,NotificaScartoType notificaScartoType) throws SerializerException {
		this.objToXml(fileName, NotificaScartoType.class, notificaScartoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>notificaScartoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>notificaScartoType</var>
	 * @param notificaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,NotificaScartoType notificaScartoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, NotificaScartoType.class, notificaScartoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>notificaScartoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param file Xml file to serialize the object <var>notificaScartoType</var>
	 * @param notificaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,NotificaScartoType notificaScartoType) throws SerializerException {
		this.objToXml(file, NotificaScartoType.class, notificaScartoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>notificaScartoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param file Xml file to serialize the object <var>notificaScartoType</var>
	 * @param notificaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,NotificaScartoType notificaScartoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, NotificaScartoType.class, notificaScartoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>notificaScartoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param out OutputStream to serialize the object <var>notificaScartoType</var>
	 * @param notificaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,NotificaScartoType notificaScartoType) throws SerializerException {
		this.objToXml(out, NotificaScartoType.class, notificaScartoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>notificaScartoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param out OutputStream to serialize the object <var>notificaScartoType</var>
	 * @param notificaScartoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,NotificaScartoType notificaScartoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, NotificaScartoType.class, notificaScartoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>notificaScartoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param notificaScartoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(NotificaScartoType notificaScartoType) throws SerializerException {
		return this.objToXml(NotificaScartoType.class, notificaScartoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>notificaScartoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param notificaScartoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(NotificaScartoType notificaScartoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(NotificaScartoType.class, notificaScartoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>notificaScartoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param notificaScartoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(NotificaScartoType notificaScartoType) throws SerializerException {
		return this.objToXml(NotificaScartoType.class, notificaScartoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>notificaScartoType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param notificaScartoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(NotificaScartoType notificaScartoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(NotificaScartoType.class, notificaScartoType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: NotificaMancataConsegna_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>notificaMancataConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>notificaMancataConsegnaType</var>
	 * @param notificaMancataConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,NotificaMancataConsegnaType notificaMancataConsegnaType) throws SerializerException {
		this.objToXml(fileName, NotificaMancataConsegnaType.class, notificaMancataConsegnaType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>notificaMancataConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>notificaMancataConsegnaType</var>
	 * @param notificaMancataConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,NotificaMancataConsegnaType notificaMancataConsegnaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, NotificaMancataConsegnaType.class, notificaMancataConsegnaType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>notificaMancataConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param file Xml file to serialize the object <var>notificaMancataConsegnaType</var>
	 * @param notificaMancataConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,NotificaMancataConsegnaType notificaMancataConsegnaType) throws SerializerException {
		this.objToXml(file, NotificaMancataConsegnaType.class, notificaMancataConsegnaType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>notificaMancataConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param file Xml file to serialize the object <var>notificaMancataConsegnaType</var>
	 * @param notificaMancataConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,NotificaMancataConsegnaType notificaMancataConsegnaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, NotificaMancataConsegnaType.class, notificaMancataConsegnaType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>notificaMancataConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param out OutputStream to serialize the object <var>notificaMancataConsegnaType</var>
	 * @param notificaMancataConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,NotificaMancataConsegnaType notificaMancataConsegnaType) throws SerializerException {
		this.objToXml(out, NotificaMancataConsegnaType.class, notificaMancataConsegnaType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>notificaMancataConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param out OutputStream to serialize the object <var>notificaMancataConsegnaType</var>
	 * @param notificaMancataConsegnaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,NotificaMancataConsegnaType notificaMancataConsegnaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, NotificaMancataConsegnaType.class, notificaMancataConsegnaType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>notificaMancataConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param notificaMancataConsegnaType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(NotificaMancataConsegnaType notificaMancataConsegnaType) throws SerializerException {
		return this.objToXml(NotificaMancataConsegnaType.class, notificaMancataConsegnaType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>notificaMancataConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param notificaMancataConsegnaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(NotificaMancataConsegnaType notificaMancataConsegnaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(NotificaMancataConsegnaType.class, notificaMancataConsegnaType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>notificaMancataConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param notificaMancataConsegnaType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(NotificaMancataConsegnaType notificaMancataConsegnaType) throws SerializerException {
		return this.objToXml(NotificaMancataConsegnaType.class, notificaMancataConsegnaType, false).toString();
	}
	/**
	 * Serialize to String the object <var>notificaMancataConsegnaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param notificaMancataConsegnaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(NotificaMancataConsegnaType notificaMancataConsegnaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(NotificaMancataConsegnaType.class, notificaMancataConsegnaType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: AttestazioneTrasmissioneFattura_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attestazioneTrasmissioneFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>attestazioneTrasmissioneFatturaType</var>
	 * @param attestazioneTrasmissioneFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFatturaType) throws SerializerException {
		this.objToXml(fileName, AttestazioneTrasmissioneFatturaType.class, attestazioneTrasmissioneFatturaType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attestazioneTrasmissioneFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>attestazioneTrasmissioneFatturaType</var>
	 * @param attestazioneTrasmissioneFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFatturaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AttestazioneTrasmissioneFatturaType.class, attestazioneTrasmissioneFatturaType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>attestazioneTrasmissioneFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param file Xml file to serialize the object <var>attestazioneTrasmissioneFatturaType</var>
	 * @param attestazioneTrasmissioneFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFatturaType) throws SerializerException {
		this.objToXml(file, AttestazioneTrasmissioneFatturaType.class, attestazioneTrasmissioneFatturaType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>attestazioneTrasmissioneFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param file Xml file to serialize the object <var>attestazioneTrasmissioneFatturaType</var>
	 * @param attestazioneTrasmissioneFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFatturaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AttestazioneTrasmissioneFatturaType.class, attestazioneTrasmissioneFatturaType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>attestazioneTrasmissioneFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param out OutputStream to serialize the object <var>attestazioneTrasmissioneFatturaType</var>
	 * @param attestazioneTrasmissioneFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFatturaType) throws SerializerException {
		this.objToXml(out, AttestazioneTrasmissioneFatturaType.class, attestazioneTrasmissioneFatturaType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>attestazioneTrasmissioneFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param out OutputStream to serialize the object <var>attestazioneTrasmissioneFatturaType</var>
	 * @param attestazioneTrasmissioneFatturaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFatturaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AttestazioneTrasmissioneFatturaType.class, attestazioneTrasmissioneFatturaType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>attestazioneTrasmissioneFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param attestazioneTrasmissioneFatturaType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFatturaType) throws SerializerException {
		return this.objToXml(AttestazioneTrasmissioneFatturaType.class, attestazioneTrasmissioneFatturaType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>attestazioneTrasmissioneFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param attestazioneTrasmissioneFatturaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFatturaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AttestazioneTrasmissioneFatturaType.class, attestazioneTrasmissioneFatturaType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>attestazioneTrasmissioneFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param attestazioneTrasmissioneFatturaType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFatturaType) throws SerializerException {
		return this.objToXml(AttestazioneTrasmissioneFatturaType.class, attestazioneTrasmissioneFatturaType, false).toString();
	}
	/**
	 * Serialize to String the object <var>attestazioneTrasmissioneFatturaType</var> of type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param attestazioneTrasmissioneFatturaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AttestazioneTrasmissioneFatturaType attestazioneTrasmissioneFatturaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AttestazioneTrasmissioneFatturaType.class, attestazioneTrasmissioneFatturaType, prettyPrint).toString();
	}
	
	
	

}
