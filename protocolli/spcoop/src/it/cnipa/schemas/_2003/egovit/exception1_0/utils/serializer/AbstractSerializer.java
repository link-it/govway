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
package it.cnipa.schemas._2003.egovit.exception1_0.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta;
import it.cnipa.schemas._2003.egovit.exception1_0.Eccezione;
import it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento;
import it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo;

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
	 Object: EccezioneBusta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezioneBusta</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param fileName Xml file to serialize the object <var>eccezioneBusta</var>
	 * @param eccezioneBusta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EccezioneBusta eccezioneBusta) throws SerializerException {
		this.objToXml(fileName, EccezioneBusta.class, eccezioneBusta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezioneBusta</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param fileName Xml file to serialize the object <var>eccezioneBusta</var>
	 * @param eccezioneBusta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EccezioneBusta eccezioneBusta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, EccezioneBusta.class, eccezioneBusta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>eccezioneBusta</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param file Xml file to serialize the object <var>eccezioneBusta</var>
	 * @param eccezioneBusta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EccezioneBusta eccezioneBusta) throws SerializerException {
		this.objToXml(file, EccezioneBusta.class, eccezioneBusta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>eccezioneBusta</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param file Xml file to serialize the object <var>eccezioneBusta</var>
	 * @param eccezioneBusta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EccezioneBusta eccezioneBusta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, EccezioneBusta.class, eccezioneBusta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>eccezioneBusta</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param out OutputStream to serialize the object <var>eccezioneBusta</var>
	 * @param eccezioneBusta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EccezioneBusta eccezioneBusta) throws SerializerException {
		this.objToXml(out, EccezioneBusta.class, eccezioneBusta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>eccezioneBusta</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param out OutputStream to serialize the object <var>eccezioneBusta</var>
	 * @param eccezioneBusta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EccezioneBusta eccezioneBusta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, EccezioneBusta.class, eccezioneBusta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>eccezioneBusta</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param eccezioneBusta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EccezioneBusta eccezioneBusta) throws SerializerException {
		return this.objToXml(EccezioneBusta.class, eccezioneBusta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>eccezioneBusta</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param eccezioneBusta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EccezioneBusta eccezioneBusta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EccezioneBusta.class, eccezioneBusta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>eccezioneBusta</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param eccezioneBusta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EccezioneBusta eccezioneBusta) throws SerializerException {
		return this.objToXml(EccezioneBusta.class, eccezioneBusta, false).toString();
	}
	/**
	 * Serialize to String the object <var>eccezioneBusta</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta}
	 * 
	 * @param eccezioneBusta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EccezioneBusta eccezioneBusta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EccezioneBusta.class, eccezioneBusta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Eccezione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezione</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Eccezione eccezione) throws SerializerException {
		this.objToXml(fileName, Eccezione.class, eccezione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezione</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
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
	 * Serialize to file system in <var>file</var> the object <var>eccezione</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * 
	 * @param file Xml file to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Eccezione eccezione) throws SerializerException {
		this.objToXml(file, Eccezione.class, eccezione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>eccezione</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
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
	 * Serialize to output stream <var>out</var> the object <var>eccezione</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Eccezione eccezione) throws SerializerException {
		this.objToXml(out, Eccezione.class, eccezione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>eccezione</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
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
	 * Serialize to byte array the object <var>eccezione</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * 
	 * @param eccezione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Eccezione eccezione) throws SerializerException {
		return this.objToXml(Eccezione.class, eccezione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>eccezione</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
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
	 * Serialize to String the object <var>eccezione</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
	 * 
	 * @param eccezione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Eccezione eccezione) throws SerializerException {
		return this.objToXml(Eccezione.class, eccezione, false).toString();
	}
	/**
	 * Serialize to String the object <var>eccezione</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.Eccezione}
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
	 Object: EccezioneProcessamento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezioneProcessamento</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>eccezioneProcessamento</var>
	 * @param eccezioneProcessamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EccezioneProcessamento eccezioneProcessamento) throws SerializerException {
		this.objToXml(fileName, EccezioneProcessamento.class, eccezioneProcessamento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezioneProcessamento</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>eccezioneProcessamento</var>
	 * @param eccezioneProcessamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EccezioneProcessamento eccezioneProcessamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, EccezioneProcessamento.class, eccezioneProcessamento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>eccezioneProcessamento</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param file Xml file to serialize the object <var>eccezioneProcessamento</var>
	 * @param eccezioneProcessamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EccezioneProcessamento eccezioneProcessamento) throws SerializerException {
		this.objToXml(file, EccezioneProcessamento.class, eccezioneProcessamento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>eccezioneProcessamento</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param file Xml file to serialize the object <var>eccezioneProcessamento</var>
	 * @param eccezioneProcessamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EccezioneProcessamento eccezioneProcessamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, EccezioneProcessamento.class, eccezioneProcessamento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>eccezioneProcessamento</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param out OutputStream to serialize the object <var>eccezioneProcessamento</var>
	 * @param eccezioneProcessamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EccezioneProcessamento eccezioneProcessamento) throws SerializerException {
		this.objToXml(out, EccezioneProcessamento.class, eccezioneProcessamento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>eccezioneProcessamento</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param out OutputStream to serialize the object <var>eccezioneProcessamento</var>
	 * @param eccezioneProcessamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EccezioneProcessamento eccezioneProcessamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, EccezioneProcessamento.class, eccezioneProcessamento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>eccezioneProcessamento</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param eccezioneProcessamento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EccezioneProcessamento eccezioneProcessamento) throws SerializerException {
		return this.objToXml(EccezioneProcessamento.class, eccezioneProcessamento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>eccezioneProcessamento</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param eccezioneProcessamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EccezioneProcessamento eccezioneProcessamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EccezioneProcessamento.class, eccezioneProcessamento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>eccezioneProcessamento</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param eccezioneProcessamento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EccezioneProcessamento eccezioneProcessamento) throws SerializerException {
		return this.objToXml(EccezioneProcessamento.class, eccezioneProcessamento, false).toString();
	}
	/**
	 * Serialize to String the object <var>eccezioneProcessamento</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento}
	 * 
	 * @param eccezioneProcessamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EccezioneProcessamento eccezioneProcessamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EccezioneProcessamento.class, eccezioneProcessamento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: MessaggioDiErroreApplicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messaggioDiErroreApplicativo</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>messaggioDiErroreApplicativo</var>
	 * @param messaggioDiErroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessaggioDiErroreApplicativo messaggioDiErroreApplicativo) throws SerializerException {
		this.objToXml(fileName, MessaggioDiErroreApplicativo.class, messaggioDiErroreApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messaggioDiErroreApplicativo</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>messaggioDiErroreApplicativo</var>
	 * @param messaggioDiErroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessaggioDiErroreApplicativo messaggioDiErroreApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessaggioDiErroreApplicativo.class, messaggioDiErroreApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messaggioDiErroreApplicativo</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>messaggioDiErroreApplicativo</var>
	 * @param messaggioDiErroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessaggioDiErroreApplicativo messaggioDiErroreApplicativo) throws SerializerException {
		this.objToXml(file, MessaggioDiErroreApplicativo.class, messaggioDiErroreApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messaggioDiErroreApplicativo</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>messaggioDiErroreApplicativo</var>
	 * @param messaggioDiErroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessaggioDiErroreApplicativo messaggioDiErroreApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessaggioDiErroreApplicativo.class, messaggioDiErroreApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messaggioDiErroreApplicativo</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>messaggioDiErroreApplicativo</var>
	 * @param messaggioDiErroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessaggioDiErroreApplicativo messaggioDiErroreApplicativo) throws SerializerException {
		this.objToXml(out, MessaggioDiErroreApplicativo.class, messaggioDiErroreApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messaggioDiErroreApplicativo</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>messaggioDiErroreApplicativo</var>
	 * @param messaggioDiErroreApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessaggioDiErroreApplicativo messaggioDiErroreApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessaggioDiErroreApplicativo.class, messaggioDiErroreApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messaggioDiErroreApplicativo</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param messaggioDiErroreApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessaggioDiErroreApplicativo messaggioDiErroreApplicativo) throws SerializerException {
		return this.objToXml(MessaggioDiErroreApplicativo.class, messaggioDiErroreApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messaggioDiErroreApplicativo</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param messaggioDiErroreApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessaggioDiErroreApplicativo messaggioDiErroreApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessaggioDiErroreApplicativo.class, messaggioDiErroreApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messaggioDiErroreApplicativo</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param messaggioDiErroreApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessaggioDiErroreApplicativo messaggioDiErroreApplicativo) throws SerializerException {
		return this.objToXml(MessaggioDiErroreApplicativo.class, messaggioDiErroreApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>messaggioDiErroreApplicativo</var> of type {@link it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo}
	 * 
	 * @param messaggioDiErroreApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessaggioDiErroreApplicativo messaggioDiErroreApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessaggioDiErroreApplicativo.class, messaggioDiErroreApplicativo, prettyPrint).toString();
	}
	
	
	

}
