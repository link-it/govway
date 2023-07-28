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
package org.openspcoop2.core.diagnostica.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.diagnostica.DominioSoggetto;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici;
import org.openspcoop2.core.diagnostica.DominioDiagnostico;
import org.openspcoop2.core.diagnostica.Proprieta;
import org.openspcoop2.core.diagnostica.Protocollo;

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
	 Object: dominio-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(fileName, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
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
	 * Serialize to file system in <var>file</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(file, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
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
	 * Serialize to output stream <var>out</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(out, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
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
	 * Serialize to byte array the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param dominioSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioSoggetto dominioSoggetto) throws SerializerException {
		return this.objToXml(DominioSoggetto.class, dominioSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
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
	 * Serialize to String the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param dominioSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioSoggetto dominioSoggetto) throws SerializerException {
		return this.objToXml(DominioSoggetto.class, dominioSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
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
	 Object: messaggio-diagnostico
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messaggioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param fileName Xml file to serialize the object <var>messaggioDiagnostico</var>
	 * @param messaggioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessaggioDiagnostico messaggioDiagnostico) throws SerializerException {
		this.objToXml(fileName, MessaggioDiagnostico.class, messaggioDiagnostico, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messaggioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param fileName Xml file to serialize the object <var>messaggioDiagnostico</var>
	 * @param messaggioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessaggioDiagnostico messaggioDiagnostico,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessaggioDiagnostico.class, messaggioDiagnostico, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messaggioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param file Xml file to serialize the object <var>messaggioDiagnostico</var>
	 * @param messaggioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessaggioDiagnostico messaggioDiagnostico) throws SerializerException {
		this.objToXml(file, MessaggioDiagnostico.class, messaggioDiagnostico, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messaggioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param file Xml file to serialize the object <var>messaggioDiagnostico</var>
	 * @param messaggioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessaggioDiagnostico messaggioDiagnostico,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessaggioDiagnostico.class, messaggioDiagnostico, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messaggioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param out OutputStream to serialize the object <var>messaggioDiagnostico</var>
	 * @param messaggioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessaggioDiagnostico messaggioDiagnostico) throws SerializerException {
		this.objToXml(out, MessaggioDiagnostico.class, messaggioDiagnostico, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messaggioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param out OutputStream to serialize the object <var>messaggioDiagnostico</var>
	 * @param messaggioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessaggioDiagnostico messaggioDiagnostico,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessaggioDiagnostico.class, messaggioDiagnostico, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messaggioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param messaggioDiagnostico Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessaggioDiagnostico messaggioDiagnostico) throws SerializerException {
		return this.objToXml(MessaggioDiagnostico.class, messaggioDiagnostico, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messaggioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param messaggioDiagnostico Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessaggioDiagnostico messaggioDiagnostico,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessaggioDiagnostico.class, messaggioDiagnostico, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messaggioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param messaggioDiagnostico Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessaggioDiagnostico messaggioDiagnostico) throws SerializerException {
		return this.objToXml(MessaggioDiagnostico.class, messaggioDiagnostico, false).toString();
	}
	/**
	 * Serialize to String the object <var>messaggioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param messaggioDiagnostico Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessaggioDiagnostico messaggioDiagnostico,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessaggioDiagnostico.class, messaggioDiagnostico, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-messaggi-diagnostici
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoMessaggiDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoMessaggiDiagnostici</var>
	 * @param elencoMessaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoMessaggiDiagnostici elencoMessaggiDiagnostici) throws SerializerException {
		this.objToXml(fileName, ElencoMessaggiDiagnostici.class, elencoMessaggiDiagnostici, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoMessaggiDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoMessaggiDiagnostici</var>
	 * @param elencoMessaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoMessaggiDiagnostici elencoMessaggiDiagnostici,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoMessaggiDiagnostici.class, elencoMessaggiDiagnostici, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoMessaggiDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param file Xml file to serialize the object <var>elencoMessaggiDiagnostici</var>
	 * @param elencoMessaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoMessaggiDiagnostici elencoMessaggiDiagnostici) throws SerializerException {
		this.objToXml(file, ElencoMessaggiDiagnostici.class, elencoMessaggiDiagnostici, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoMessaggiDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param file Xml file to serialize the object <var>elencoMessaggiDiagnostici</var>
	 * @param elencoMessaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoMessaggiDiagnostici elencoMessaggiDiagnostici,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoMessaggiDiagnostici.class, elencoMessaggiDiagnostici, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoMessaggiDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoMessaggiDiagnostici</var>
	 * @param elencoMessaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoMessaggiDiagnostici elencoMessaggiDiagnostici) throws SerializerException {
		this.objToXml(out, ElencoMessaggiDiagnostici.class, elencoMessaggiDiagnostici, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoMessaggiDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoMessaggiDiagnostici</var>
	 * @param elencoMessaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoMessaggiDiagnostici elencoMessaggiDiagnostici,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoMessaggiDiagnostici.class, elencoMessaggiDiagnostici, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoMessaggiDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param elencoMessaggiDiagnostici Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoMessaggiDiagnostici elencoMessaggiDiagnostici) throws SerializerException {
		return this.objToXml(ElencoMessaggiDiagnostici.class, elencoMessaggiDiagnostici, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoMessaggiDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param elencoMessaggiDiagnostici Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoMessaggiDiagnostici elencoMessaggiDiagnostici,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoMessaggiDiagnostici.class, elencoMessaggiDiagnostici, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoMessaggiDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param elencoMessaggiDiagnostici Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoMessaggiDiagnostici elencoMessaggiDiagnostici) throws SerializerException {
		return this.objToXml(ElencoMessaggiDiagnostici.class, elencoMessaggiDiagnostici, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoMessaggiDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici}
	 * 
	 * @param elencoMessaggiDiagnostici Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoMessaggiDiagnostici elencoMessaggiDiagnostici,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoMessaggiDiagnostici.class, elencoMessaggiDiagnostici, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: dominio-diagnostico
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioDiagnostico</var>
	 * @param dominioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioDiagnostico dominioDiagnostico) throws SerializerException {
		this.objToXml(fileName, DominioDiagnostico.class, dominioDiagnostico, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioDiagnostico</var>
	 * @param dominioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioDiagnostico dominioDiagnostico,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DominioDiagnostico.class, dominioDiagnostico, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param file Xml file to serialize the object <var>dominioDiagnostico</var>
	 * @param dominioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioDiagnostico dominioDiagnostico) throws SerializerException {
		this.objToXml(file, DominioDiagnostico.class, dominioDiagnostico, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param file Xml file to serialize the object <var>dominioDiagnostico</var>
	 * @param dominioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioDiagnostico dominioDiagnostico,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DominioDiagnostico.class, dominioDiagnostico, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioDiagnostico</var>
	 * @param dominioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioDiagnostico dominioDiagnostico) throws SerializerException {
		this.objToXml(out, DominioDiagnostico.class, dominioDiagnostico, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioDiagnostico</var>
	 * @param dominioDiagnostico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioDiagnostico dominioDiagnostico,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DominioDiagnostico.class, dominioDiagnostico, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dominioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param dominioDiagnostico Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioDiagnostico dominioDiagnostico) throws SerializerException {
		return this.objToXml(DominioDiagnostico.class, dominioDiagnostico, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param dominioDiagnostico Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioDiagnostico dominioDiagnostico,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DominioDiagnostico.class, dominioDiagnostico, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dominioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param dominioDiagnostico Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioDiagnostico dominioDiagnostico) throws SerializerException {
		return this.objToXml(DominioDiagnostico.class, dominioDiagnostico, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominioDiagnostico</var> of type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param dominioDiagnostico Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioDiagnostico dominioDiagnostico,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DominioDiagnostico.class, dominioDiagnostico, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: proprieta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Proprieta proprieta) throws SerializerException {
		this.objToXml(fileName, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.diagnostica.Proprieta}
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
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param file Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Proprieta proprieta) throws SerializerException {
		this.objToXml(file, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.diagnostica.Proprieta}
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
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Proprieta proprieta) throws SerializerException {
		this.objToXml(out, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.diagnostica.Proprieta}
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
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.core.diagnostica.Proprieta}
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
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toString();
	}
	/**
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: protocollo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>protocollo</var>
	 * @param protocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Protocollo protocollo) throws SerializerException {
		this.objToXml(fileName, Protocollo.class, protocollo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>protocollo</var>
	 * @param protocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Protocollo protocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Protocollo.class, protocollo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param file Xml file to serialize the object <var>protocollo</var>
	 * @param protocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Protocollo protocollo) throws SerializerException {
		this.objToXml(file, Protocollo.class, protocollo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param file Xml file to serialize the object <var>protocollo</var>
	 * @param protocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Protocollo protocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Protocollo.class, protocollo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>protocollo</var>
	 * @param protocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Protocollo protocollo) throws SerializerException {
		this.objToXml(out, Protocollo.class, protocollo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>protocollo</var>
	 * @param protocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Protocollo protocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Protocollo.class, protocollo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>protocollo</var> of type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param protocollo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Protocollo protocollo) throws SerializerException {
		return this.objToXml(Protocollo.class, protocollo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>protocollo</var> of type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param protocollo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Protocollo protocollo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Protocollo.class, protocollo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>protocollo</var> of type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param protocollo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Protocollo protocollo) throws SerializerException {
		return this.objToXml(Protocollo.class, protocollo, false).toString();
	}
	/**
	 * Serialize to String the object <var>protocollo</var> of type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param protocollo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Protocollo protocollo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Protocollo.class, protocollo, prettyPrint).toString();
	}
	
	
	

}
