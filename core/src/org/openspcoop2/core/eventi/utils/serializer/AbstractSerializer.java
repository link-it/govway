/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.eventi.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.DatiEventoGenerico;

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
	 Object: evento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>evento</var> of type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param fileName Xml file to serialize the object <var>evento</var>
	 * @param evento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Evento evento) throws SerializerException {
		this.objToXml(fileName, Evento.class, evento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>evento</var> of type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param fileName Xml file to serialize the object <var>evento</var>
	 * @param evento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Evento evento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Evento.class, evento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>evento</var> of type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param file Xml file to serialize the object <var>evento</var>
	 * @param evento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Evento evento) throws SerializerException {
		this.objToXml(file, Evento.class, evento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>evento</var> of type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param file Xml file to serialize the object <var>evento</var>
	 * @param evento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Evento evento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Evento.class, evento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>evento</var> of type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param out OutputStream to serialize the object <var>evento</var>
	 * @param evento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Evento evento) throws SerializerException {
		this.objToXml(out, Evento.class, evento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>evento</var> of type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param out OutputStream to serialize the object <var>evento</var>
	 * @param evento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Evento evento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Evento.class, evento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>evento</var> of type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param evento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Evento evento) throws SerializerException {
		return this.objToXml(Evento.class, evento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>evento</var> of type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param evento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Evento evento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Evento.class, evento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>evento</var> of type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param evento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Evento evento) throws SerializerException {
		return this.objToXml(Evento.class, evento, false).toString();
	}
	/**
	 * Serialize to String the object <var>evento</var> of type {@link org.openspcoop2.core.eventi.Evento}
	 * 
	 * @param evento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Evento evento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Evento.class, evento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: dati-evento-generico
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiEventoGenerico</var> of type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiEventoGenerico</var>
	 * @param datiEventoGenerico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiEventoGenerico datiEventoGenerico) throws SerializerException {
		this.objToXml(fileName, DatiEventoGenerico.class, datiEventoGenerico, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiEventoGenerico</var> of type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiEventoGenerico</var>
	 * @param datiEventoGenerico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiEventoGenerico datiEventoGenerico,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiEventoGenerico.class, datiEventoGenerico, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiEventoGenerico</var> of type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param file Xml file to serialize the object <var>datiEventoGenerico</var>
	 * @param datiEventoGenerico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiEventoGenerico datiEventoGenerico) throws SerializerException {
		this.objToXml(file, DatiEventoGenerico.class, datiEventoGenerico, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiEventoGenerico</var> of type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param file Xml file to serialize the object <var>datiEventoGenerico</var>
	 * @param datiEventoGenerico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiEventoGenerico datiEventoGenerico,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiEventoGenerico.class, datiEventoGenerico, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiEventoGenerico</var> of type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param out OutputStream to serialize the object <var>datiEventoGenerico</var>
	 * @param datiEventoGenerico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiEventoGenerico datiEventoGenerico) throws SerializerException {
		this.objToXml(out, DatiEventoGenerico.class, datiEventoGenerico, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiEventoGenerico</var> of type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param out OutputStream to serialize the object <var>datiEventoGenerico</var>
	 * @param datiEventoGenerico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiEventoGenerico datiEventoGenerico,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiEventoGenerico.class, datiEventoGenerico, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiEventoGenerico</var> of type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param datiEventoGenerico Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiEventoGenerico datiEventoGenerico) throws SerializerException {
		return this.objToXml(DatiEventoGenerico.class, datiEventoGenerico, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiEventoGenerico</var> of type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param datiEventoGenerico Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiEventoGenerico datiEventoGenerico,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiEventoGenerico.class, datiEventoGenerico, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiEventoGenerico</var> of type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param datiEventoGenerico Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiEventoGenerico datiEventoGenerico) throws SerializerException {
		return this.objToXml(DatiEventoGenerico.class, datiEventoGenerico, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiEventoGenerico</var> of type {@link org.openspcoop2.core.eventi.DatiEventoGenerico}
	 * 
	 * @param datiEventoGenerico Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiEventoGenerico datiEventoGenerico,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiEventoGenerico.class, datiEventoGenerico, prettyPrint).toString();
	}
	
	
	

}
