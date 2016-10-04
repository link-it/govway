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
package org.openspcoop2.core.diagnostica.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.diagnostica.DominioTransazione;
import org.openspcoop2.core.diagnostica.DominioSoggetto;
import org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione;
import org.openspcoop2.core.diagnostica.DominioDiagnostico;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.Protocollo;
import org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo;
import org.openspcoop2.core.diagnostica.Servizio;
import org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici;
import org.openspcoop2.core.diagnostica.SoggettoIdentificativo;
import org.openspcoop2.core.diagnostica.Soggetto;
import org.openspcoop2.core.diagnostica.Proprieta;
import org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione;

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
	 Object: dominio-transazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioTransazione</var> of type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioTransazione</var>
	 * @param dominioTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioTransazione dominioTransazione) throws SerializerException {
		this.objToXml(fileName, DominioTransazione.class, dominioTransazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioTransazione</var> of type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioTransazione</var>
	 * @param dominioTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioTransazione dominioTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DominioTransazione.class, dominioTransazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioTransazione</var> of type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param file Xml file to serialize the object <var>dominioTransazione</var>
	 * @param dominioTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioTransazione dominioTransazione) throws SerializerException {
		this.objToXml(file, DominioTransazione.class, dominioTransazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioTransazione</var> of type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param file Xml file to serialize the object <var>dominioTransazione</var>
	 * @param dominioTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioTransazione dominioTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DominioTransazione.class, dominioTransazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioTransazione</var> of type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioTransazione</var>
	 * @param dominioTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioTransazione dominioTransazione) throws SerializerException {
		this.objToXml(out, DominioTransazione.class, dominioTransazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioTransazione</var> of type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioTransazione</var>
	 * @param dominioTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioTransazione dominioTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DominioTransazione.class, dominioTransazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dominioTransazione</var> of type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param dominioTransazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioTransazione dominioTransazione) throws SerializerException {
		return this.objToXml(DominioTransazione.class, dominioTransazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominioTransazione</var> of type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param dominioTransazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioTransazione dominioTransazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DominioTransazione.class, dominioTransazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dominioTransazione</var> of type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param dominioTransazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioTransazione dominioTransazione) throws SerializerException {
		return this.objToXml(DominioTransazione.class, dominioTransazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominioTransazione</var> of type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param dominioTransazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioTransazione dominioTransazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DominioTransazione.class, dominioTransazione, prettyPrint).toString();
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
	 Object: id-informazioni-protocollo-transazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idInformazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idInformazioniProtocolloTransazione</var>
	 * @param idInformazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione) throws SerializerException {
		this.objToXml(fileName, IdInformazioniProtocolloTransazione.class, idInformazioniProtocolloTransazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idInformazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idInformazioniProtocolloTransazione</var>
	 * @param idInformazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdInformazioniProtocolloTransazione.class, idInformazioniProtocolloTransazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idInformazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param file Xml file to serialize the object <var>idInformazioniProtocolloTransazione</var>
	 * @param idInformazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione) throws SerializerException {
		this.objToXml(file, IdInformazioniProtocolloTransazione.class, idInformazioniProtocolloTransazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idInformazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param file Xml file to serialize the object <var>idInformazioniProtocolloTransazione</var>
	 * @param idInformazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdInformazioniProtocolloTransazione.class, idInformazioniProtocolloTransazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idInformazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param out OutputStream to serialize the object <var>idInformazioniProtocolloTransazione</var>
	 * @param idInformazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione) throws SerializerException {
		this.objToXml(out, IdInformazioniProtocolloTransazione.class, idInformazioniProtocolloTransazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idInformazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param out OutputStream to serialize the object <var>idInformazioniProtocolloTransazione</var>
	 * @param idInformazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdInformazioniProtocolloTransazione.class, idInformazioniProtocolloTransazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idInformazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param idInformazioniProtocolloTransazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione) throws SerializerException {
		return this.objToXml(IdInformazioniProtocolloTransazione.class, idInformazioniProtocolloTransazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idInformazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param idInformazioniProtocolloTransazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdInformazioniProtocolloTransazione.class, idInformazioniProtocolloTransazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idInformazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param idInformazioniProtocolloTransazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione) throws SerializerException {
		return this.objToXml(IdInformazioniProtocolloTransazione.class, idInformazioniProtocolloTransazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>idInformazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param idInformazioniProtocolloTransazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdInformazioniProtocolloTransazione.class, idInformazioniProtocolloTransazione, prettyPrint).toString();
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
	
	
	
	/*
	 =================================================================================
	 Object: filtro-informazione-protocollo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>filtroInformazioneProtocollo</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>filtroInformazioneProtocollo</var>
	 * @param filtroInformazioneProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FiltroInformazioneProtocollo filtroInformazioneProtocollo) throws SerializerException {
		this.objToXml(fileName, FiltroInformazioneProtocollo.class, filtroInformazioneProtocollo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>filtroInformazioneProtocollo</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>filtroInformazioneProtocollo</var>
	 * @param filtroInformazioneProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FiltroInformazioneProtocollo filtroInformazioneProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FiltroInformazioneProtocollo.class, filtroInformazioneProtocollo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>filtroInformazioneProtocollo</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param file Xml file to serialize the object <var>filtroInformazioneProtocollo</var>
	 * @param filtroInformazioneProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FiltroInformazioneProtocollo filtroInformazioneProtocollo) throws SerializerException {
		this.objToXml(file, FiltroInformazioneProtocollo.class, filtroInformazioneProtocollo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>filtroInformazioneProtocollo</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param file Xml file to serialize the object <var>filtroInformazioneProtocollo</var>
	 * @param filtroInformazioneProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FiltroInformazioneProtocollo filtroInformazioneProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FiltroInformazioneProtocollo.class, filtroInformazioneProtocollo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>filtroInformazioneProtocollo</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>filtroInformazioneProtocollo</var>
	 * @param filtroInformazioneProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FiltroInformazioneProtocollo filtroInformazioneProtocollo) throws SerializerException {
		this.objToXml(out, FiltroInformazioneProtocollo.class, filtroInformazioneProtocollo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>filtroInformazioneProtocollo</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>filtroInformazioneProtocollo</var>
	 * @param filtroInformazioneProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FiltroInformazioneProtocollo filtroInformazioneProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FiltroInformazioneProtocollo.class, filtroInformazioneProtocollo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>filtroInformazioneProtocollo</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param filtroInformazioneProtocollo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FiltroInformazioneProtocollo filtroInformazioneProtocollo) throws SerializerException {
		return this.objToXml(FiltroInformazioneProtocollo.class, filtroInformazioneProtocollo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>filtroInformazioneProtocollo</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param filtroInformazioneProtocollo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FiltroInformazioneProtocollo filtroInformazioneProtocollo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FiltroInformazioneProtocollo.class, filtroInformazioneProtocollo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>filtroInformazioneProtocollo</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param filtroInformazioneProtocollo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FiltroInformazioneProtocollo filtroInformazioneProtocollo) throws SerializerException {
		return this.objToXml(FiltroInformazioneProtocollo.class, filtroInformazioneProtocollo, false).toString();
	}
	/**
	 * Serialize to String the object <var>filtroInformazioneProtocollo</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param filtroInformazioneProtocollo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FiltroInformazioneProtocollo filtroInformazioneProtocollo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FiltroInformazioneProtocollo.class, filtroInformazioneProtocollo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Servizio servizio) throws SerializerException {
		this.objToXml(fileName, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.diagnostica.Servizio}
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
	 * Serialize to file system in <var>file</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * 
	 * @param file Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Servizio servizio) throws SerializerException {
		this.objToXml(file, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.diagnostica.Servizio}
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
	 * Serialize to output stream <var>out</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * 
	 * @param out OutputStream to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Servizio servizio) throws SerializerException {
		this.objToXml(out, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.diagnostica.Servizio}
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
	 * Serialize to byte array the object <var>servizio</var> of type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Servizio servizio) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizio</var> of type {@link org.openspcoop2.core.diagnostica.Servizio}
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
	 * Serialize to String the object <var>servizio</var> of type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Servizio servizio) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizio</var> of type {@link org.openspcoop2.core.diagnostica.Servizio}
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
	 Object: filtro-informazioni-diagnostici
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>filtroInformazioniDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param fileName Xml file to serialize the object <var>filtroInformazioniDiagnostici</var>
	 * @param filtroInformazioniDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FiltroInformazioniDiagnostici filtroInformazioniDiagnostici) throws SerializerException {
		this.objToXml(fileName, FiltroInformazioniDiagnostici.class, filtroInformazioniDiagnostici, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>filtroInformazioniDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param fileName Xml file to serialize the object <var>filtroInformazioniDiagnostici</var>
	 * @param filtroInformazioniDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FiltroInformazioniDiagnostici filtroInformazioniDiagnostici,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FiltroInformazioniDiagnostici.class, filtroInformazioniDiagnostici, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>filtroInformazioniDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param file Xml file to serialize the object <var>filtroInformazioniDiagnostici</var>
	 * @param filtroInformazioniDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FiltroInformazioniDiagnostici filtroInformazioniDiagnostici) throws SerializerException {
		this.objToXml(file, FiltroInformazioniDiagnostici.class, filtroInformazioniDiagnostici, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>filtroInformazioniDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param file Xml file to serialize the object <var>filtroInformazioniDiagnostici</var>
	 * @param filtroInformazioniDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FiltroInformazioniDiagnostici filtroInformazioniDiagnostici,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FiltroInformazioniDiagnostici.class, filtroInformazioniDiagnostici, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>filtroInformazioniDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param out OutputStream to serialize the object <var>filtroInformazioniDiagnostici</var>
	 * @param filtroInformazioniDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FiltroInformazioniDiagnostici filtroInformazioniDiagnostici) throws SerializerException {
		this.objToXml(out, FiltroInformazioniDiagnostici.class, filtroInformazioniDiagnostici, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>filtroInformazioniDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param out OutputStream to serialize the object <var>filtroInformazioniDiagnostici</var>
	 * @param filtroInformazioniDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FiltroInformazioniDiagnostici filtroInformazioniDiagnostici,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FiltroInformazioniDiagnostici.class, filtroInformazioniDiagnostici, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>filtroInformazioniDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param filtroInformazioniDiagnostici Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FiltroInformazioniDiagnostici filtroInformazioniDiagnostici) throws SerializerException {
		return this.objToXml(FiltroInformazioniDiagnostici.class, filtroInformazioniDiagnostici, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>filtroInformazioniDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param filtroInformazioniDiagnostici Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FiltroInformazioniDiagnostici filtroInformazioniDiagnostici,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FiltroInformazioniDiagnostici.class, filtroInformazioniDiagnostici, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>filtroInformazioniDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param filtroInformazioniDiagnostici Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FiltroInformazioniDiagnostici filtroInformazioniDiagnostici) throws SerializerException {
		return this.objToXml(FiltroInformazioniDiagnostici.class, filtroInformazioniDiagnostici, false).toString();
	}
	/**
	 * Serialize to String the object <var>filtroInformazioniDiagnostici</var> of type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param filtroInformazioniDiagnostici Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FiltroInformazioniDiagnostici filtroInformazioniDiagnostici,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FiltroInformazioniDiagnostici.class, filtroInformazioniDiagnostici, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soggetto-identificativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		this.objToXml(fileName, SoggettoIdentificativo.class, soggettoIdentificativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
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
	 * Serialize to file system in <var>file</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * 
	 * @param file Xml file to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		this.objToXml(file, SoggettoIdentificativo.class, soggettoIdentificativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
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
	 * Serialize to output stream <var>out</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * 
	 * @param out OutputStream to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		this.objToXml(out, SoggettoIdentificativo.class, soggettoIdentificativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
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
	 * Serialize to byte array the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * 
	 * @param soggettoIdentificativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		return this.objToXml(SoggettoIdentificativo.class, soggettoIdentificativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
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
	 * Serialize to String the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * 
	 * @param soggettoIdentificativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		return this.objToXml(SoggettoIdentificativo.class, soggettoIdentificativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
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
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.diagnostica.Soggetto}
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
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.diagnostica.Soggetto}
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
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.diagnostica.Soggetto}
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
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.diagnostica.Soggetto}
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
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.diagnostica.Soggetto}
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
	 Object: informazioni-protocollo-transazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>informazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>informazioniProtocolloTransazione</var>
	 * @param informazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InformazioniProtocolloTransazione informazioniProtocolloTransazione) throws SerializerException {
		this.objToXml(fileName, InformazioniProtocolloTransazione.class, informazioniProtocolloTransazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>informazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>informazioniProtocolloTransazione</var>
	 * @param informazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InformazioniProtocolloTransazione informazioniProtocolloTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InformazioniProtocolloTransazione.class, informazioniProtocolloTransazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>informazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param file Xml file to serialize the object <var>informazioniProtocolloTransazione</var>
	 * @param informazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InformazioniProtocolloTransazione informazioniProtocolloTransazione) throws SerializerException {
		this.objToXml(file, InformazioniProtocolloTransazione.class, informazioniProtocolloTransazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>informazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param file Xml file to serialize the object <var>informazioniProtocolloTransazione</var>
	 * @param informazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InformazioniProtocolloTransazione informazioniProtocolloTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InformazioniProtocolloTransazione.class, informazioniProtocolloTransazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>informazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param out OutputStream to serialize the object <var>informazioniProtocolloTransazione</var>
	 * @param informazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InformazioniProtocolloTransazione informazioniProtocolloTransazione) throws SerializerException {
		this.objToXml(out, InformazioniProtocolloTransazione.class, informazioniProtocolloTransazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>informazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param out OutputStream to serialize the object <var>informazioniProtocolloTransazione</var>
	 * @param informazioniProtocolloTransazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InformazioniProtocolloTransazione informazioniProtocolloTransazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InformazioniProtocolloTransazione.class, informazioniProtocolloTransazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>informazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param informazioniProtocolloTransazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InformazioniProtocolloTransazione informazioniProtocolloTransazione) throws SerializerException {
		return this.objToXml(InformazioniProtocolloTransazione.class, informazioniProtocolloTransazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>informazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param informazioniProtocolloTransazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InformazioniProtocolloTransazione informazioniProtocolloTransazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InformazioniProtocolloTransazione.class, informazioniProtocolloTransazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>informazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param informazioniProtocolloTransazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InformazioniProtocolloTransazione informazioniProtocolloTransazione) throws SerializerException {
		return this.objToXml(InformazioniProtocolloTransazione.class, informazioniProtocolloTransazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>informazioniProtocolloTransazione</var> of type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param informazioniProtocolloTransazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InformazioniProtocolloTransazione informazioniProtocolloTransazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InformazioniProtocolloTransazione.class, informazioniProtocolloTransazione, prettyPrint).toString();
	}
	
	
	

}
