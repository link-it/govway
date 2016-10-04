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
package org.openspcoop2.core.tracciamento.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.tracciamento.Dominio;
import org.openspcoop2.core.tracciamento.Traccia;
import org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione;
import org.openspcoop2.core.tracciamento.Busta;
import org.openspcoop2.core.tracciamento.Allegati;
import org.openspcoop2.core.tracciamento.Inoltro;
import org.openspcoop2.core.tracciamento.ProfiloTrasmissione;
import org.openspcoop2.core.tracciamento.IdTraccia;
import org.openspcoop2.core.tracciamento.DominioIdTraccia;
import org.openspcoop2.core.tracciamento.Allegato;
import org.openspcoop2.core.tracciamento.Soggetto;
import org.openspcoop2.core.tracciamento.ProfiloCollaborazione;
import org.openspcoop2.core.tracciamento.Servizio;
import org.openspcoop2.core.tracciamento.Data;
import org.openspcoop2.core.tracciamento.Trasmissioni;
import org.openspcoop2.core.tracciamento.Riscontri;
import org.openspcoop2.core.tracciamento.Eccezioni;
import org.openspcoop2.core.tracciamento.Protocollo;
import org.openspcoop2.core.tracciamento.TipoData;
import org.openspcoop2.core.tracciamento.SoggettoIdentificativo;
import org.openspcoop2.core.tracciamento.DominioSoggetto;
import org.openspcoop2.core.tracciamento.Riscontro;
import org.openspcoop2.core.tracciamento.Eccezione;
import org.openspcoop2.core.tracciamento.CodiceEccezione;
import org.openspcoop2.core.tracciamento.ContestoCodificaEccezione;
import org.openspcoop2.core.tracciamento.RilevanzaEccezione;
import org.openspcoop2.core.tracciamento.Proprieta;
import org.openspcoop2.core.tracciamento.Trasmissione;
import org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto;

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
	 Object: dominio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Dominio dominio) throws SerializerException {
		this.objToXml(fileName, Dominio.class, dominio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.tracciamento.Dominio}
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
	 * Serialize to file system in <var>file</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * 
	 * @param file Xml file to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Dominio dominio) throws SerializerException {
		this.objToXml(file, Dominio.class, dominio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.tracciamento.Dominio}
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
	 * Serialize to output stream <var>out</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * 
	 * @param out OutputStream to serialize the object <var>dominio</var>
	 * @param dominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Dominio dominio) throws SerializerException {
		this.objToXml(out, Dominio.class, dominio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominio</var> of type {@link org.openspcoop2.core.tracciamento.Dominio}
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
	 * Serialize to byte array the object <var>dominio</var> of type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * 
	 * @param dominio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Dominio dominio) throws SerializerException {
		return this.objToXml(Dominio.class, dominio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominio</var> of type {@link org.openspcoop2.core.tracciamento.Dominio}
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
	 * Serialize to String the object <var>dominio</var> of type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * 
	 * @param dominio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Dominio dominio) throws SerializerException {
		return this.objToXml(Dominio.class, dominio, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominio</var> of type {@link org.openspcoop2.core.tracciamento.Dominio}
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
	 Object: traccia
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>traccia</var> of type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param fileName Xml file to serialize the object <var>traccia</var>
	 * @param traccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Traccia traccia) throws SerializerException {
		this.objToXml(fileName, Traccia.class, traccia, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>traccia</var> of type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param fileName Xml file to serialize the object <var>traccia</var>
	 * @param traccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Traccia traccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Traccia.class, traccia, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>traccia</var> of type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param file Xml file to serialize the object <var>traccia</var>
	 * @param traccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Traccia traccia) throws SerializerException {
		this.objToXml(file, Traccia.class, traccia, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>traccia</var> of type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param file Xml file to serialize the object <var>traccia</var>
	 * @param traccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Traccia traccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Traccia.class, traccia, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>traccia</var> of type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param out OutputStream to serialize the object <var>traccia</var>
	 * @param traccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Traccia traccia) throws SerializerException {
		this.objToXml(out, Traccia.class, traccia, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>traccia</var> of type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param out OutputStream to serialize the object <var>traccia</var>
	 * @param traccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Traccia traccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Traccia.class, traccia, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>traccia</var> of type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param traccia Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Traccia traccia) throws SerializerException {
		return this.objToXml(Traccia.class, traccia, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>traccia</var> of type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param traccia Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Traccia traccia,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Traccia.class, traccia, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>traccia</var> of type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param traccia Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Traccia traccia) throws SerializerException {
		return this.objToXml(Traccia.class, traccia, false).toString();
	}
	/**
	 * Serialize to String the object <var>traccia</var> of type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param traccia Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Traccia traccia,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Traccia.class, traccia, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: traccia-esito-elaborazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tracciaEsitoElaborazione</var> of type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>tracciaEsitoElaborazione</var>
	 * @param tracciaEsitoElaborazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TracciaEsitoElaborazione tracciaEsitoElaborazione) throws SerializerException {
		this.objToXml(fileName, TracciaEsitoElaborazione.class, tracciaEsitoElaborazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tracciaEsitoElaborazione</var> of type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>tracciaEsitoElaborazione</var>
	 * @param tracciaEsitoElaborazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TracciaEsitoElaborazione tracciaEsitoElaborazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TracciaEsitoElaborazione.class, tracciaEsitoElaborazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>tracciaEsitoElaborazione</var> of type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param file Xml file to serialize the object <var>tracciaEsitoElaborazione</var>
	 * @param tracciaEsitoElaborazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TracciaEsitoElaborazione tracciaEsitoElaborazione) throws SerializerException {
		this.objToXml(file, TracciaEsitoElaborazione.class, tracciaEsitoElaborazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>tracciaEsitoElaborazione</var> of type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param file Xml file to serialize the object <var>tracciaEsitoElaborazione</var>
	 * @param tracciaEsitoElaborazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TracciaEsitoElaborazione tracciaEsitoElaborazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TracciaEsitoElaborazione.class, tracciaEsitoElaborazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>tracciaEsitoElaborazione</var> of type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param out OutputStream to serialize the object <var>tracciaEsitoElaborazione</var>
	 * @param tracciaEsitoElaborazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TracciaEsitoElaborazione tracciaEsitoElaborazione) throws SerializerException {
		this.objToXml(out, TracciaEsitoElaborazione.class, tracciaEsitoElaborazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>tracciaEsitoElaborazione</var> of type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param out OutputStream to serialize the object <var>tracciaEsitoElaborazione</var>
	 * @param tracciaEsitoElaborazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TracciaEsitoElaborazione tracciaEsitoElaborazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TracciaEsitoElaborazione.class, tracciaEsitoElaborazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>tracciaEsitoElaborazione</var> of type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param tracciaEsitoElaborazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TracciaEsitoElaborazione tracciaEsitoElaborazione) throws SerializerException {
		return this.objToXml(TracciaEsitoElaborazione.class, tracciaEsitoElaborazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>tracciaEsitoElaborazione</var> of type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param tracciaEsitoElaborazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TracciaEsitoElaborazione tracciaEsitoElaborazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TracciaEsitoElaborazione.class, tracciaEsitoElaborazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>tracciaEsitoElaborazione</var> of type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param tracciaEsitoElaborazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TracciaEsitoElaborazione tracciaEsitoElaborazione) throws SerializerException {
		return this.objToXml(TracciaEsitoElaborazione.class, tracciaEsitoElaborazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>tracciaEsitoElaborazione</var> of type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param tracciaEsitoElaborazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TracciaEsitoElaborazione tracciaEsitoElaborazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TracciaEsitoElaborazione.class, tracciaEsitoElaborazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: busta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>busta</var> of type {@link org.openspcoop2.core.tracciamento.Busta}
	 * 
	 * @param fileName Xml file to serialize the object <var>busta</var>
	 * @param busta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Busta busta) throws SerializerException {
		this.objToXml(fileName, Busta.class, busta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>busta</var> of type {@link org.openspcoop2.core.tracciamento.Busta}
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
	 * Serialize to file system in <var>file</var> the object <var>busta</var> of type {@link org.openspcoop2.core.tracciamento.Busta}
	 * 
	 * @param file Xml file to serialize the object <var>busta</var>
	 * @param busta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Busta busta) throws SerializerException {
		this.objToXml(file, Busta.class, busta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>busta</var> of type {@link org.openspcoop2.core.tracciamento.Busta}
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
	 * Serialize to output stream <var>out</var> the object <var>busta</var> of type {@link org.openspcoop2.core.tracciamento.Busta}
	 * 
	 * @param out OutputStream to serialize the object <var>busta</var>
	 * @param busta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Busta busta) throws SerializerException {
		this.objToXml(out, Busta.class, busta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>busta</var> of type {@link org.openspcoop2.core.tracciamento.Busta}
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
	 * Serialize to byte array the object <var>busta</var> of type {@link org.openspcoop2.core.tracciamento.Busta}
	 * 
	 * @param busta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Busta busta) throws SerializerException {
		return this.objToXml(Busta.class, busta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>busta</var> of type {@link org.openspcoop2.core.tracciamento.Busta}
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
	 * Serialize to String the object <var>busta</var> of type {@link org.openspcoop2.core.tracciamento.Busta}
	 * 
	 * @param busta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Busta busta) throws SerializerException {
		return this.objToXml(Busta.class, busta, false).toString();
	}
	/**
	 * Serialize to String the object <var>busta</var> of type {@link org.openspcoop2.core.tracciamento.Busta}
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
	 Object: allegati
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allegati</var> of type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param fileName Xml file to serialize the object <var>allegati</var>
	 * @param allegati Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Allegati allegati) throws SerializerException {
		this.objToXml(fileName, Allegati.class, allegati, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allegati</var> of type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param fileName Xml file to serialize the object <var>allegati</var>
	 * @param allegati Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Allegati allegati,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Allegati.class, allegati, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>allegati</var> of type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param file Xml file to serialize the object <var>allegati</var>
	 * @param allegati Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Allegati allegati) throws SerializerException {
		this.objToXml(file, Allegati.class, allegati, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allegati</var> of type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param file Xml file to serialize the object <var>allegati</var>
	 * @param allegati Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Allegati allegati,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Allegati.class, allegati, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>allegati</var> of type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param out OutputStream to serialize the object <var>allegati</var>
	 * @param allegati Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Allegati allegati) throws SerializerException {
		this.objToXml(out, Allegati.class, allegati, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allegati</var> of type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param out OutputStream to serialize the object <var>allegati</var>
	 * @param allegati Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Allegati allegati,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Allegati.class, allegati, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>allegati</var> of type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param allegati Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Allegati allegati) throws SerializerException {
		return this.objToXml(Allegati.class, allegati, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allegati</var> of type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param allegati Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Allegati allegati,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Allegati.class, allegati, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>allegati</var> of type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param allegati Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Allegati allegati) throws SerializerException {
		return this.objToXml(Allegati.class, allegati, false).toString();
	}
	/**
	 * Serialize to String the object <var>allegati</var> of type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param allegati Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Allegati allegati,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Allegati.class, allegati, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: inoltro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>inoltro</var> of type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>inoltro</var>
	 * @param inoltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Inoltro inoltro) throws SerializerException {
		this.objToXml(fileName, Inoltro.class, inoltro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>inoltro</var> of type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>inoltro</var>
	 * @param inoltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Inoltro inoltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Inoltro.class, inoltro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>inoltro</var> of type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param file Xml file to serialize the object <var>inoltro</var>
	 * @param inoltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Inoltro inoltro) throws SerializerException {
		this.objToXml(file, Inoltro.class, inoltro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>inoltro</var> of type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param file Xml file to serialize the object <var>inoltro</var>
	 * @param inoltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Inoltro inoltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Inoltro.class, inoltro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>inoltro</var> of type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param out OutputStream to serialize the object <var>inoltro</var>
	 * @param inoltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Inoltro inoltro) throws SerializerException {
		this.objToXml(out, Inoltro.class, inoltro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>inoltro</var> of type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param out OutputStream to serialize the object <var>inoltro</var>
	 * @param inoltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Inoltro inoltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Inoltro.class, inoltro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>inoltro</var> of type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param inoltro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Inoltro inoltro) throws SerializerException {
		return this.objToXml(Inoltro.class, inoltro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>inoltro</var> of type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param inoltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Inoltro inoltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Inoltro.class, inoltro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>inoltro</var> of type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param inoltro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Inoltro inoltro) throws SerializerException {
		return this.objToXml(Inoltro.class, inoltro, false).toString();
	}
	/**
	 * Serialize to String the object <var>inoltro</var> of type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param inoltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Inoltro inoltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Inoltro.class, inoltro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: profilo-trasmissione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>profiloTrasmissione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param fileName Xml file to serialize the object <var>profiloTrasmissione</var>
	 * @param profiloTrasmissione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ProfiloTrasmissione profiloTrasmissione) throws SerializerException {
		this.objToXml(fileName, ProfiloTrasmissione.class, profiloTrasmissione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>profiloTrasmissione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param fileName Xml file to serialize the object <var>profiloTrasmissione</var>
	 * @param profiloTrasmissione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ProfiloTrasmissione profiloTrasmissione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ProfiloTrasmissione.class, profiloTrasmissione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>profiloTrasmissione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param file Xml file to serialize the object <var>profiloTrasmissione</var>
	 * @param profiloTrasmissione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ProfiloTrasmissione profiloTrasmissione) throws SerializerException {
		this.objToXml(file, ProfiloTrasmissione.class, profiloTrasmissione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>profiloTrasmissione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param file Xml file to serialize the object <var>profiloTrasmissione</var>
	 * @param profiloTrasmissione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ProfiloTrasmissione profiloTrasmissione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ProfiloTrasmissione.class, profiloTrasmissione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>profiloTrasmissione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param out OutputStream to serialize the object <var>profiloTrasmissione</var>
	 * @param profiloTrasmissione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ProfiloTrasmissione profiloTrasmissione) throws SerializerException {
		this.objToXml(out, ProfiloTrasmissione.class, profiloTrasmissione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>profiloTrasmissione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param out OutputStream to serialize the object <var>profiloTrasmissione</var>
	 * @param profiloTrasmissione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ProfiloTrasmissione profiloTrasmissione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ProfiloTrasmissione.class, profiloTrasmissione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>profiloTrasmissione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param profiloTrasmissione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ProfiloTrasmissione profiloTrasmissione) throws SerializerException {
		return this.objToXml(ProfiloTrasmissione.class, profiloTrasmissione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>profiloTrasmissione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param profiloTrasmissione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ProfiloTrasmissione profiloTrasmissione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ProfiloTrasmissione.class, profiloTrasmissione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>profiloTrasmissione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param profiloTrasmissione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ProfiloTrasmissione profiloTrasmissione) throws SerializerException {
		return this.objToXml(ProfiloTrasmissione.class, profiloTrasmissione, false).toString();
	}
	/**
	 * Serialize to String the object <var>profiloTrasmissione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param profiloTrasmissione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ProfiloTrasmissione profiloTrasmissione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ProfiloTrasmissione.class, profiloTrasmissione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-traccia
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idTraccia</var> of type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param fileName Xml file to serialize the object <var>idTraccia</var>
	 * @param idTraccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdTraccia idTraccia) throws SerializerException {
		this.objToXml(fileName, IdTraccia.class, idTraccia, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idTraccia</var> of type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param fileName Xml file to serialize the object <var>idTraccia</var>
	 * @param idTraccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdTraccia idTraccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdTraccia.class, idTraccia, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idTraccia</var> of type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param file Xml file to serialize the object <var>idTraccia</var>
	 * @param idTraccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdTraccia idTraccia) throws SerializerException {
		this.objToXml(file, IdTraccia.class, idTraccia, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idTraccia</var> of type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param file Xml file to serialize the object <var>idTraccia</var>
	 * @param idTraccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdTraccia idTraccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdTraccia.class, idTraccia, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idTraccia</var> of type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param out OutputStream to serialize the object <var>idTraccia</var>
	 * @param idTraccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdTraccia idTraccia) throws SerializerException {
		this.objToXml(out, IdTraccia.class, idTraccia, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idTraccia</var> of type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param out OutputStream to serialize the object <var>idTraccia</var>
	 * @param idTraccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdTraccia idTraccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdTraccia.class, idTraccia, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idTraccia</var> of type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param idTraccia Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdTraccia idTraccia) throws SerializerException {
		return this.objToXml(IdTraccia.class, idTraccia, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idTraccia</var> of type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param idTraccia Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdTraccia idTraccia,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdTraccia.class, idTraccia, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idTraccia</var> of type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param idTraccia Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdTraccia idTraccia) throws SerializerException {
		return this.objToXml(IdTraccia.class, idTraccia, false).toString();
	}
	/**
	 * Serialize to String the object <var>idTraccia</var> of type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param idTraccia Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdTraccia idTraccia,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdTraccia.class, idTraccia, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: dominio-id-traccia
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioIdTraccia</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioIdTraccia</var>
	 * @param dominioIdTraccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioIdTraccia dominioIdTraccia) throws SerializerException {
		this.objToXml(fileName, DominioIdTraccia.class, dominioIdTraccia, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioIdTraccia</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioIdTraccia</var>
	 * @param dominioIdTraccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioIdTraccia dominioIdTraccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DominioIdTraccia.class, dominioIdTraccia, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioIdTraccia</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param file Xml file to serialize the object <var>dominioIdTraccia</var>
	 * @param dominioIdTraccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioIdTraccia dominioIdTraccia) throws SerializerException {
		this.objToXml(file, DominioIdTraccia.class, dominioIdTraccia, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioIdTraccia</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param file Xml file to serialize the object <var>dominioIdTraccia</var>
	 * @param dominioIdTraccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioIdTraccia dominioIdTraccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DominioIdTraccia.class, dominioIdTraccia, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioIdTraccia</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioIdTraccia</var>
	 * @param dominioIdTraccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioIdTraccia dominioIdTraccia) throws SerializerException {
		this.objToXml(out, DominioIdTraccia.class, dominioIdTraccia, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioIdTraccia</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioIdTraccia</var>
	 * @param dominioIdTraccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioIdTraccia dominioIdTraccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DominioIdTraccia.class, dominioIdTraccia, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dominioIdTraccia</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param dominioIdTraccia Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioIdTraccia dominioIdTraccia) throws SerializerException {
		return this.objToXml(DominioIdTraccia.class, dominioIdTraccia, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominioIdTraccia</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param dominioIdTraccia Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioIdTraccia dominioIdTraccia,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DominioIdTraccia.class, dominioIdTraccia, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dominioIdTraccia</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param dominioIdTraccia Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioIdTraccia dominioIdTraccia) throws SerializerException {
		return this.objToXml(DominioIdTraccia.class, dominioIdTraccia, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominioIdTraccia</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param dominioIdTraccia Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioIdTraccia dominioIdTraccia,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DominioIdTraccia.class, dominioIdTraccia, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: allegato
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allegato</var> of type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param fileName Xml file to serialize the object <var>allegato</var>
	 * @param allegato Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Allegato allegato) throws SerializerException {
		this.objToXml(fileName, Allegato.class, allegato, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allegato</var> of type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param fileName Xml file to serialize the object <var>allegato</var>
	 * @param allegato Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Allegato allegato,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Allegato.class, allegato, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>allegato</var> of type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param file Xml file to serialize the object <var>allegato</var>
	 * @param allegato Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Allegato allegato) throws SerializerException {
		this.objToXml(file, Allegato.class, allegato, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allegato</var> of type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param file Xml file to serialize the object <var>allegato</var>
	 * @param allegato Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Allegato allegato,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Allegato.class, allegato, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>allegato</var> of type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param out OutputStream to serialize the object <var>allegato</var>
	 * @param allegato Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Allegato allegato) throws SerializerException {
		this.objToXml(out, Allegato.class, allegato, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allegato</var> of type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param out OutputStream to serialize the object <var>allegato</var>
	 * @param allegato Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Allegato allegato,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Allegato.class, allegato, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>allegato</var> of type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param allegato Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Allegato allegato) throws SerializerException {
		return this.objToXml(Allegato.class, allegato, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allegato</var> of type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param allegato Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Allegato allegato,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Allegato.class, allegato, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>allegato</var> of type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param allegato Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Allegato allegato) throws SerializerException {
		return this.objToXml(Allegato.class, allegato, false).toString();
	}
	/**
	 * Serialize to String the object <var>allegato</var> of type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param allegato Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Allegato allegato,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Allegato.class, allegato, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.tracciamento.Soggetto}
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
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.tracciamento.Soggetto}
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
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.tracciamento.Soggetto}
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
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.tracciamento.Soggetto}
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
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.tracciamento.Soggetto}
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
	 Object: profilo-collaborazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>profiloCollaborazione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>profiloCollaborazione</var>
	 * @param profiloCollaborazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ProfiloCollaborazione profiloCollaborazione) throws SerializerException {
		this.objToXml(fileName, ProfiloCollaborazione.class, profiloCollaborazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>profiloCollaborazione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>profiloCollaborazione</var>
	 * @param profiloCollaborazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ProfiloCollaborazione profiloCollaborazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ProfiloCollaborazione.class, profiloCollaborazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>profiloCollaborazione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param file Xml file to serialize the object <var>profiloCollaborazione</var>
	 * @param profiloCollaborazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ProfiloCollaborazione profiloCollaborazione) throws SerializerException {
		this.objToXml(file, ProfiloCollaborazione.class, profiloCollaborazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>profiloCollaborazione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param file Xml file to serialize the object <var>profiloCollaborazione</var>
	 * @param profiloCollaborazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ProfiloCollaborazione profiloCollaborazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ProfiloCollaborazione.class, profiloCollaborazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>profiloCollaborazione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param out OutputStream to serialize the object <var>profiloCollaborazione</var>
	 * @param profiloCollaborazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ProfiloCollaborazione profiloCollaborazione) throws SerializerException {
		this.objToXml(out, ProfiloCollaborazione.class, profiloCollaborazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>profiloCollaborazione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param out OutputStream to serialize the object <var>profiloCollaborazione</var>
	 * @param profiloCollaborazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ProfiloCollaborazione profiloCollaborazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ProfiloCollaborazione.class, profiloCollaborazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>profiloCollaborazione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param profiloCollaborazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ProfiloCollaborazione profiloCollaborazione) throws SerializerException {
		return this.objToXml(ProfiloCollaborazione.class, profiloCollaborazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>profiloCollaborazione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param profiloCollaborazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ProfiloCollaborazione profiloCollaborazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ProfiloCollaborazione.class, profiloCollaborazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>profiloCollaborazione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param profiloCollaborazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ProfiloCollaborazione profiloCollaborazione) throws SerializerException {
		return this.objToXml(ProfiloCollaborazione.class, profiloCollaborazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>profiloCollaborazione</var> of type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param profiloCollaborazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ProfiloCollaborazione profiloCollaborazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ProfiloCollaborazione.class, profiloCollaborazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Servizio servizio) throws SerializerException {
		this.objToXml(fileName, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.tracciamento.Servizio}
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
	 * Serialize to file system in <var>file</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * 
	 * @param file Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Servizio servizio) throws SerializerException {
		this.objToXml(file, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.tracciamento.Servizio}
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
	 * Serialize to output stream <var>out</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * 
	 * @param out OutputStream to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Servizio servizio) throws SerializerException {
		this.objToXml(out, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.tracciamento.Servizio}
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
	 * Serialize to byte array the object <var>servizio</var> of type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Servizio servizio) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizio</var> of type {@link org.openspcoop2.core.tracciamento.Servizio}
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
	 * Serialize to String the object <var>servizio</var> of type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Servizio servizio) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizio</var> of type {@link org.openspcoop2.core.tracciamento.Servizio}
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
	 Object: data
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>data</var> of type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param fileName Xml file to serialize the object <var>data</var>
	 * @param data Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Data data) throws SerializerException {
		this.objToXml(fileName, Data.class, data, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>data</var> of type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param fileName Xml file to serialize the object <var>data</var>
	 * @param data Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Data data,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Data.class, data, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>data</var> of type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param file Xml file to serialize the object <var>data</var>
	 * @param data Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Data data) throws SerializerException {
		this.objToXml(file, Data.class, data, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>data</var> of type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param file Xml file to serialize the object <var>data</var>
	 * @param data Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Data data,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Data.class, data, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>data</var> of type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param out OutputStream to serialize the object <var>data</var>
	 * @param data Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Data data) throws SerializerException {
		this.objToXml(out, Data.class, data, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>data</var> of type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param out OutputStream to serialize the object <var>data</var>
	 * @param data Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Data data,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Data.class, data, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>data</var> of type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param data Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Data data) throws SerializerException {
		return this.objToXml(Data.class, data, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>data</var> of type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param data Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Data data,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Data.class, data, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>data</var> of type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param data Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Data data) throws SerializerException {
		return this.objToXml(Data.class, data, false).toString();
	}
	/**
	 * Serialize to String the object <var>data</var> of type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param data Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Data data,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Data.class, data, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasmissioni
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasmissioni</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasmissioni</var>
	 * @param trasmissioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Trasmissioni trasmissioni) throws SerializerException {
		this.objToXml(fileName, Trasmissioni.class, trasmissioni, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasmissioni</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasmissioni</var>
	 * @param trasmissioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Trasmissioni trasmissioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Trasmissioni.class, trasmissioni, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasmissioni</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param file Xml file to serialize the object <var>trasmissioni</var>
	 * @param trasmissioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Trasmissioni trasmissioni) throws SerializerException {
		this.objToXml(file, Trasmissioni.class, trasmissioni, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasmissioni</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param file Xml file to serialize the object <var>trasmissioni</var>
	 * @param trasmissioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Trasmissioni trasmissioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Trasmissioni.class, trasmissioni, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasmissioni</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param out OutputStream to serialize the object <var>trasmissioni</var>
	 * @param trasmissioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Trasmissioni trasmissioni) throws SerializerException {
		this.objToXml(out, Trasmissioni.class, trasmissioni, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasmissioni</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param out OutputStream to serialize the object <var>trasmissioni</var>
	 * @param trasmissioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Trasmissioni trasmissioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Trasmissioni.class, trasmissioni, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasmissioni</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param trasmissioni Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Trasmissioni trasmissioni) throws SerializerException {
		return this.objToXml(Trasmissioni.class, trasmissioni, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasmissioni</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param trasmissioni Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Trasmissioni trasmissioni,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Trasmissioni.class, trasmissioni, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasmissioni</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param trasmissioni Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Trasmissioni trasmissioni) throws SerializerException {
		return this.objToXml(Trasmissioni.class, trasmissioni, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasmissioni</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param trasmissioni Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Trasmissioni trasmissioni,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Trasmissioni.class, trasmissioni, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: riscontri
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riscontri</var> of type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param fileName Xml file to serialize the object <var>riscontri</var>
	 * @param riscontri Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Riscontri riscontri) throws SerializerException {
		this.objToXml(fileName, Riscontri.class, riscontri, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riscontri</var> of type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param fileName Xml file to serialize the object <var>riscontri</var>
	 * @param riscontri Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Riscontri riscontri,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Riscontri.class, riscontri, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>riscontri</var> of type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param file Xml file to serialize the object <var>riscontri</var>
	 * @param riscontri Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Riscontri riscontri) throws SerializerException {
		this.objToXml(file, Riscontri.class, riscontri, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riscontri</var> of type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param file Xml file to serialize the object <var>riscontri</var>
	 * @param riscontri Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Riscontri riscontri,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Riscontri.class, riscontri, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>riscontri</var> of type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param out OutputStream to serialize the object <var>riscontri</var>
	 * @param riscontri Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Riscontri riscontri) throws SerializerException {
		this.objToXml(out, Riscontri.class, riscontri, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riscontri</var> of type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param out OutputStream to serialize the object <var>riscontri</var>
	 * @param riscontri Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Riscontri riscontri,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Riscontri.class, riscontri, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>riscontri</var> of type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param riscontri Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Riscontri riscontri) throws SerializerException {
		return this.objToXml(Riscontri.class, riscontri, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riscontri</var> of type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param riscontri Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Riscontri riscontri,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Riscontri.class, riscontri, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>riscontri</var> of type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param riscontri Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Riscontri riscontri) throws SerializerException {
		return this.objToXml(Riscontri.class, riscontri, false).toString();
	}
	/**
	 * Serialize to String the object <var>riscontri</var> of type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param riscontri Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Riscontri riscontri,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Riscontri.class, riscontri, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: eccezioni
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezioni</var> of type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param fileName Xml file to serialize the object <var>eccezioni</var>
	 * @param eccezioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Eccezioni eccezioni) throws SerializerException {
		this.objToXml(fileName, Eccezioni.class, eccezioni, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezioni</var> of type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param fileName Xml file to serialize the object <var>eccezioni</var>
	 * @param eccezioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Eccezioni eccezioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Eccezioni.class, eccezioni, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>eccezioni</var> of type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param file Xml file to serialize the object <var>eccezioni</var>
	 * @param eccezioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Eccezioni eccezioni) throws SerializerException {
		this.objToXml(file, Eccezioni.class, eccezioni, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>eccezioni</var> of type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param file Xml file to serialize the object <var>eccezioni</var>
	 * @param eccezioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Eccezioni eccezioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Eccezioni.class, eccezioni, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>eccezioni</var> of type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param out OutputStream to serialize the object <var>eccezioni</var>
	 * @param eccezioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Eccezioni eccezioni) throws SerializerException {
		this.objToXml(out, Eccezioni.class, eccezioni, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>eccezioni</var> of type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param out OutputStream to serialize the object <var>eccezioni</var>
	 * @param eccezioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Eccezioni eccezioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Eccezioni.class, eccezioni, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>eccezioni</var> of type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param eccezioni Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Eccezioni eccezioni) throws SerializerException {
		return this.objToXml(Eccezioni.class, eccezioni, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>eccezioni</var> of type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param eccezioni Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Eccezioni eccezioni,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Eccezioni.class, eccezioni, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>eccezioni</var> of type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param eccezioni Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Eccezioni eccezioni) throws SerializerException {
		return this.objToXml(Eccezioni.class, eccezioni, false).toString();
	}
	/**
	 * Serialize to String the object <var>eccezioni</var> of type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param eccezioni Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Eccezioni eccezioni,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Eccezioni.class, eccezioni, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: protocollo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>protocollo</var>
	 * @param protocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Protocollo protocollo) throws SerializerException {
		this.objToXml(fileName, Protocollo.class, protocollo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.tracciamento.Protocollo}
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
	 * Serialize to file system in <var>file</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * 
	 * @param file Xml file to serialize the object <var>protocollo</var>
	 * @param protocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Protocollo protocollo) throws SerializerException {
		this.objToXml(file, Protocollo.class, protocollo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.tracciamento.Protocollo}
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
	 * Serialize to output stream <var>out</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>protocollo</var>
	 * @param protocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Protocollo protocollo) throws SerializerException {
		this.objToXml(out, Protocollo.class, protocollo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>protocollo</var> of type {@link org.openspcoop2.core.tracciamento.Protocollo}
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
	 * Serialize to byte array the object <var>protocollo</var> of type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * 
	 * @param protocollo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Protocollo protocollo) throws SerializerException {
		return this.objToXml(Protocollo.class, protocollo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>protocollo</var> of type {@link org.openspcoop2.core.tracciamento.Protocollo}
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
	 * Serialize to String the object <var>protocollo</var> of type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * 
	 * @param protocollo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Protocollo protocollo) throws SerializerException {
		return this.objToXml(Protocollo.class, protocollo, false).toString();
	}
	/**
	 * Serialize to String the object <var>protocollo</var> of type {@link org.openspcoop2.core.tracciamento.Protocollo}
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
	 Object: TipoData
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tipoData</var> of type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param fileName Xml file to serialize the object <var>tipoData</var>
	 * @param tipoData Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TipoData tipoData) throws SerializerException {
		this.objToXml(fileName, TipoData.class, tipoData, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tipoData</var> of type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param fileName Xml file to serialize the object <var>tipoData</var>
	 * @param tipoData Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TipoData tipoData,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TipoData.class, tipoData, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>tipoData</var> of type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param file Xml file to serialize the object <var>tipoData</var>
	 * @param tipoData Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TipoData tipoData) throws SerializerException {
		this.objToXml(file, TipoData.class, tipoData, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>tipoData</var> of type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param file Xml file to serialize the object <var>tipoData</var>
	 * @param tipoData Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TipoData tipoData,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TipoData.class, tipoData, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>tipoData</var> of type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param out OutputStream to serialize the object <var>tipoData</var>
	 * @param tipoData Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TipoData tipoData) throws SerializerException {
		this.objToXml(out, TipoData.class, tipoData, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>tipoData</var> of type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param out OutputStream to serialize the object <var>tipoData</var>
	 * @param tipoData Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TipoData tipoData,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TipoData.class, tipoData, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>tipoData</var> of type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param tipoData Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TipoData tipoData) throws SerializerException {
		return this.objToXml(TipoData.class, tipoData, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>tipoData</var> of type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param tipoData Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TipoData tipoData,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TipoData.class, tipoData, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>tipoData</var> of type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param tipoData Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TipoData tipoData) throws SerializerException {
		return this.objToXml(TipoData.class, tipoData, false).toString();
	}
	/**
	 * Serialize to String the object <var>tipoData</var> of type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param tipoData Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TipoData tipoData,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TipoData.class, tipoData, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soggetto-identificativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		this.objToXml(fileName, SoggettoIdentificativo.class, soggettoIdentificativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
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
	 * Serialize to file system in <var>file</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * 
	 * @param file Xml file to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		this.objToXml(file, SoggettoIdentificativo.class, soggettoIdentificativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
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
	 * Serialize to output stream <var>out</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * 
	 * @param out OutputStream to serialize the object <var>soggettoIdentificativo</var>
	 * @param soggettoIdentificativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		this.objToXml(out, SoggettoIdentificativo.class, soggettoIdentificativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
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
	 * Serialize to byte array the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * 
	 * @param soggettoIdentificativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		return this.objToXml(SoggettoIdentificativo.class, soggettoIdentificativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
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
	 * Serialize to String the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * 
	 * @param soggettoIdentificativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoggettoIdentificativo soggettoIdentificativo) throws SerializerException {
		return this.objToXml(SoggettoIdentificativo.class, soggettoIdentificativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggettoIdentificativo</var> of type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
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
	 Object: dominio-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(fileName, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
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
	 * Serialize to file system in <var>file</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(file, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
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
	 * Serialize to output stream <var>out</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioSoggetto</var>
	 * @param dominioSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioSoggetto dominioSoggetto) throws SerializerException {
		this.objToXml(out, DominioSoggetto.class, dominioSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
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
	 * Serialize to byte array the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * 
	 * @param dominioSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioSoggetto dominioSoggetto) throws SerializerException {
		return this.objToXml(DominioSoggetto.class, dominioSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
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
	 * Serialize to String the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * 
	 * @param dominioSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioSoggetto dominioSoggetto) throws SerializerException {
		return this.objToXml(DominioSoggetto.class, dominioSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominioSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
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
	 Object: riscontro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riscontro</var> of type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param fileName Xml file to serialize the object <var>riscontro</var>
	 * @param riscontro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Riscontro riscontro) throws SerializerException {
		this.objToXml(fileName, Riscontro.class, riscontro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riscontro</var> of type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param fileName Xml file to serialize the object <var>riscontro</var>
	 * @param riscontro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Riscontro riscontro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Riscontro.class, riscontro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>riscontro</var> of type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param file Xml file to serialize the object <var>riscontro</var>
	 * @param riscontro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Riscontro riscontro) throws SerializerException {
		this.objToXml(file, Riscontro.class, riscontro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riscontro</var> of type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param file Xml file to serialize the object <var>riscontro</var>
	 * @param riscontro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Riscontro riscontro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Riscontro.class, riscontro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>riscontro</var> of type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param out OutputStream to serialize the object <var>riscontro</var>
	 * @param riscontro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Riscontro riscontro) throws SerializerException {
		this.objToXml(out, Riscontro.class, riscontro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riscontro</var> of type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param out OutputStream to serialize the object <var>riscontro</var>
	 * @param riscontro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Riscontro riscontro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Riscontro.class, riscontro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>riscontro</var> of type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param riscontro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Riscontro riscontro) throws SerializerException {
		return this.objToXml(Riscontro.class, riscontro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riscontro</var> of type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param riscontro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Riscontro riscontro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Riscontro.class, riscontro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>riscontro</var> of type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param riscontro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Riscontro riscontro) throws SerializerException {
		return this.objToXml(Riscontro.class, riscontro, false).toString();
	}
	/**
	 * Serialize to String the object <var>riscontro</var> of type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param riscontro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Riscontro riscontro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Riscontro.class, riscontro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: eccezione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Eccezione eccezione) throws SerializerException {
		this.objToXml(fileName, Eccezione.class, eccezione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.tracciamento.Eccezione}
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
	 * Serialize to file system in <var>file</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * 
	 * @param file Xml file to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Eccezione eccezione) throws SerializerException {
		this.objToXml(file, Eccezione.class, eccezione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.tracciamento.Eccezione}
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
	 * Serialize to output stream <var>out</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>eccezione</var>
	 * @param eccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Eccezione eccezione) throws SerializerException {
		this.objToXml(out, Eccezione.class, eccezione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>eccezione</var> of type {@link org.openspcoop2.core.tracciamento.Eccezione}
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
	 * Serialize to byte array the object <var>eccezione</var> of type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * 
	 * @param eccezione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Eccezione eccezione) throws SerializerException {
		return this.objToXml(Eccezione.class, eccezione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>eccezione</var> of type {@link org.openspcoop2.core.tracciamento.Eccezione}
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
	 * Serialize to String the object <var>eccezione</var> of type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * 
	 * @param eccezione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Eccezione eccezione) throws SerializerException {
		return this.objToXml(Eccezione.class, eccezione, false).toString();
	}
	/**
	 * Serialize to String the object <var>eccezione</var> of type {@link org.openspcoop2.core.tracciamento.Eccezione}
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
	 Object: CodiceEccezione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>codiceEccezione</var>
	 * @param codiceEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CodiceEccezione codiceEccezione) throws SerializerException {
		this.objToXml(fileName, CodiceEccezione.class, codiceEccezione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
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
	 * Serialize to file system in <var>file</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * 
	 * @param file Xml file to serialize the object <var>codiceEccezione</var>
	 * @param codiceEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CodiceEccezione codiceEccezione) throws SerializerException {
		this.objToXml(file, CodiceEccezione.class, codiceEccezione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
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
	 * Serialize to output stream <var>out</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>codiceEccezione</var>
	 * @param codiceEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CodiceEccezione codiceEccezione) throws SerializerException {
		this.objToXml(out, CodiceEccezione.class, codiceEccezione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
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
	 * Serialize to byte array the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * 
	 * @param codiceEccezione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CodiceEccezione codiceEccezione) throws SerializerException {
		return this.objToXml(CodiceEccezione.class, codiceEccezione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
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
	 * Serialize to String the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * 
	 * @param codiceEccezione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CodiceEccezione codiceEccezione) throws SerializerException {
		return this.objToXml(CodiceEccezione.class, codiceEccezione, false).toString();
	}
	/**
	 * Serialize to String the object <var>codiceEccezione</var> of type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
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
	 Object: ContestoCodificaEccezione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>contestoCodificaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>contestoCodificaEccezione</var>
	 * @param contestoCodificaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ContestoCodificaEccezione contestoCodificaEccezione) throws SerializerException {
		this.objToXml(fileName, ContestoCodificaEccezione.class, contestoCodificaEccezione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>contestoCodificaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>contestoCodificaEccezione</var>
	 * @param contestoCodificaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ContestoCodificaEccezione contestoCodificaEccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ContestoCodificaEccezione.class, contestoCodificaEccezione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>contestoCodificaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param file Xml file to serialize the object <var>contestoCodificaEccezione</var>
	 * @param contestoCodificaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ContestoCodificaEccezione contestoCodificaEccezione) throws SerializerException {
		this.objToXml(file, ContestoCodificaEccezione.class, contestoCodificaEccezione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>contestoCodificaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param file Xml file to serialize the object <var>contestoCodificaEccezione</var>
	 * @param contestoCodificaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ContestoCodificaEccezione contestoCodificaEccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ContestoCodificaEccezione.class, contestoCodificaEccezione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>contestoCodificaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>contestoCodificaEccezione</var>
	 * @param contestoCodificaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ContestoCodificaEccezione contestoCodificaEccezione) throws SerializerException {
		this.objToXml(out, ContestoCodificaEccezione.class, contestoCodificaEccezione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>contestoCodificaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>contestoCodificaEccezione</var>
	 * @param contestoCodificaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ContestoCodificaEccezione contestoCodificaEccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ContestoCodificaEccezione.class, contestoCodificaEccezione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>contestoCodificaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param contestoCodificaEccezione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ContestoCodificaEccezione contestoCodificaEccezione) throws SerializerException {
		return this.objToXml(ContestoCodificaEccezione.class, contestoCodificaEccezione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>contestoCodificaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param contestoCodificaEccezione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ContestoCodificaEccezione contestoCodificaEccezione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ContestoCodificaEccezione.class, contestoCodificaEccezione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>contestoCodificaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param contestoCodificaEccezione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ContestoCodificaEccezione contestoCodificaEccezione) throws SerializerException {
		return this.objToXml(ContestoCodificaEccezione.class, contestoCodificaEccezione, false).toString();
	}
	/**
	 * Serialize to String the object <var>contestoCodificaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param contestoCodificaEccezione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ContestoCodificaEccezione contestoCodificaEccezione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ContestoCodificaEccezione.class, contestoCodificaEccezione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RilevanzaEccezione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rilevanzaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>rilevanzaEccezione</var>
	 * @param rilevanzaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RilevanzaEccezione rilevanzaEccezione) throws SerializerException {
		this.objToXml(fileName, RilevanzaEccezione.class, rilevanzaEccezione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rilevanzaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param fileName Xml file to serialize the object <var>rilevanzaEccezione</var>
	 * @param rilevanzaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RilevanzaEccezione rilevanzaEccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RilevanzaEccezione.class, rilevanzaEccezione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>rilevanzaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param file Xml file to serialize the object <var>rilevanzaEccezione</var>
	 * @param rilevanzaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RilevanzaEccezione rilevanzaEccezione) throws SerializerException {
		this.objToXml(file, RilevanzaEccezione.class, rilevanzaEccezione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>rilevanzaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param file Xml file to serialize the object <var>rilevanzaEccezione</var>
	 * @param rilevanzaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RilevanzaEccezione rilevanzaEccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RilevanzaEccezione.class, rilevanzaEccezione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>rilevanzaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>rilevanzaEccezione</var>
	 * @param rilevanzaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RilevanzaEccezione rilevanzaEccezione) throws SerializerException {
		this.objToXml(out, RilevanzaEccezione.class, rilevanzaEccezione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>rilevanzaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param out OutputStream to serialize the object <var>rilevanzaEccezione</var>
	 * @param rilevanzaEccezione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RilevanzaEccezione rilevanzaEccezione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RilevanzaEccezione.class, rilevanzaEccezione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>rilevanzaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param rilevanzaEccezione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RilevanzaEccezione rilevanzaEccezione) throws SerializerException {
		return this.objToXml(RilevanzaEccezione.class, rilevanzaEccezione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>rilevanzaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param rilevanzaEccezione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RilevanzaEccezione rilevanzaEccezione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RilevanzaEccezione.class, rilevanzaEccezione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>rilevanzaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param rilevanzaEccezione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RilevanzaEccezione rilevanzaEccezione) throws SerializerException {
		return this.objToXml(RilevanzaEccezione.class, rilevanzaEccezione, false).toString();
	}
	/**
	 * Serialize to String the object <var>rilevanzaEccezione</var> of type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param rilevanzaEccezione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RilevanzaEccezione rilevanzaEccezione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RilevanzaEccezione.class, rilevanzaEccezione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: proprieta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Proprieta proprieta) throws SerializerException {
		this.objToXml(fileName, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.tracciamento.Proprieta}
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
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * 
	 * @param file Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Proprieta proprieta) throws SerializerException {
		this.objToXml(file, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.tracciamento.Proprieta}
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
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Proprieta proprieta) throws SerializerException {
		this.objToXml(out, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.tracciamento.Proprieta}
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
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.core.tracciamento.Proprieta}
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
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toString();
	}
	/**
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.core.tracciamento.Proprieta}
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
	 Object: trasmissione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasmissione</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasmissione</var>
	 * @param trasmissione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Trasmissione trasmissione) throws SerializerException {
		this.objToXml(fileName, Trasmissione.class, trasmissione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasmissione</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasmissione</var>
	 * @param trasmissione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Trasmissione trasmissione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Trasmissione.class, trasmissione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasmissione</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param file Xml file to serialize the object <var>trasmissione</var>
	 * @param trasmissione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Trasmissione trasmissione) throws SerializerException {
		this.objToXml(file, Trasmissione.class, trasmissione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasmissione</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param file Xml file to serialize the object <var>trasmissione</var>
	 * @param trasmissione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Trasmissione trasmissione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Trasmissione.class, trasmissione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasmissione</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param out OutputStream to serialize the object <var>trasmissione</var>
	 * @param trasmissione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Trasmissione trasmissione) throws SerializerException {
		this.objToXml(out, Trasmissione.class, trasmissione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasmissione</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param out OutputStream to serialize the object <var>trasmissione</var>
	 * @param trasmissione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Trasmissione trasmissione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Trasmissione.class, trasmissione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasmissione</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param trasmissione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Trasmissione trasmissione) throws SerializerException {
		return this.objToXml(Trasmissione.class, trasmissione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasmissione</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param trasmissione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Trasmissione trasmissione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Trasmissione.class, trasmissione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasmissione</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param trasmissione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Trasmissione trasmissione) throws SerializerException {
		return this.objToXml(Trasmissione.class, trasmissione, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasmissione</var> of type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param trasmissione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Trasmissione trasmissione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Trasmissione.class, trasmissione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: dominio-id-traccia-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioIdTracciaSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioIdTracciaSoggetto</var>
	 * @param dominioIdTracciaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioIdTracciaSoggetto dominioIdTracciaSoggetto) throws SerializerException {
		this.objToXml(fileName, DominioIdTracciaSoggetto.class, dominioIdTracciaSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dominioIdTracciaSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>dominioIdTracciaSoggetto</var>
	 * @param dominioIdTracciaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DominioIdTracciaSoggetto dominioIdTracciaSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DominioIdTracciaSoggetto.class, dominioIdTracciaSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioIdTracciaSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>dominioIdTracciaSoggetto</var>
	 * @param dominioIdTracciaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioIdTracciaSoggetto dominioIdTracciaSoggetto) throws SerializerException {
		this.objToXml(file, DominioIdTracciaSoggetto.class, dominioIdTracciaSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dominioIdTracciaSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>dominioIdTracciaSoggetto</var>
	 * @param dominioIdTracciaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DominioIdTracciaSoggetto dominioIdTracciaSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DominioIdTracciaSoggetto.class, dominioIdTracciaSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioIdTracciaSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioIdTracciaSoggetto</var>
	 * @param dominioIdTracciaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioIdTracciaSoggetto dominioIdTracciaSoggetto) throws SerializerException {
		this.objToXml(out, DominioIdTracciaSoggetto.class, dominioIdTracciaSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dominioIdTracciaSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>dominioIdTracciaSoggetto</var>
	 * @param dominioIdTracciaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DominioIdTracciaSoggetto dominioIdTracciaSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DominioIdTracciaSoggetto.class, dominioIdTracciaSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dominioIdTracciaSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param dominioIdTracciaSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioIdTracciaSoggetto dominioIdTracciaSoggetto) throws SerializerException {
		return this.objToXml(DominioIdTracciaSoggetto.class, dominioIdTracciaSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dominioIdTracciaSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param dominioIdTracciaSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DominioIdTracciaSoggetto dominioIdTracciaSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DominioIdTracciaSoggetto.class, dominioIdTracciaSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dominioIdTracciaSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param dominioIdTracciaSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioIdTracciaSoggetto dominioIdTracciaSoggetto) throws SerializerException {
		return this.objToXml(DominioIdTracciaSoggetto.class, dominioIdTracciaSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>dominioIdTracciaSoggetto</var> of type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param dominioIdTracciaSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DominioIdTracciaSoggetto dominioIdTracciaSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DominioIdTracciaSoggetto.class, dominioIdTracciaSoggetto, prettyPrint).toString();
	}
	
	
	

}
