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
package org.openspcoop2.core.integrazione.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.integrazione.EsitoRichiesta;

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
	 Object: esito-richiesta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>esitoRichiesta</var> of type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param fileName Xml file to serialize the object <var>esitoRichiesta</var>
	 * @param esitoRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EsitoRichiesta esitoRichiesta) throws SerializerException {
		this.objToXml(fileName, EsitoRichiesta.class, esitoRichiesta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>esitoRichiesta</var> of type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param fileName Xml file to serialize the object <var>esitoRichiesta</var>
	 * @param esitoRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EsitoRichiesta esitoRichiesta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, EsitoRichiesta.class, esitoRichiesta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>esitoRichiesta</var> of type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param file Xml file to serialize the object <var>esitoRichiesta</var>
	 * @param esitoRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EsitoRichiesta esitoRichiesta) throws SerializerException {
		this.objToXml(file, EsitoRichiesta.class, esitoRichiesta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>esitoRichiesta</var> of type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param file Xml file to serialize the object <var>esitoRichiesta</var>
	 * @param esitoRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EsitoRichiesta esitoRichiesta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, EsitoRichiesta.class, esitoRichiesta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>esitoRichiesta</var> of type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param out OutputStream to serialize the object <var>esitoRichiesta</var>
	 * @param esitoRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EsitoRichiesta esitoRichiesta) throws SerializerException {
		this.objToXml(out, EsitoRichiesta.class, esitoRichiesta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>esitoRichiesta</var> of type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param out OutputStream to serialize the object <var>esitoRichiesta</var>
	 * @param esitoRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EsitoRichiesta esitoRichiesta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, EsitoRichiesta.class, esitoRichiesta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>esitoRichiesta</var> of type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param esitoRichiesta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EsitoRichiesta esitoRichiesta) throws SerializerException {
		return this.objToXml(EsitoRichiesta.class, esitoRichiesta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>esitoRichiesta</var> of type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param esitoRichiesta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EsitoRichiesta esitoRichiesta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EsitoRichiesta.class, esitoRichiesta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>esitoRichiesta</var> of type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param esitoRichiesta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EsitoRichiesta esitoRichiesta) throws SerializerException {
		return this.objToXml(EsitoRichiesta.class, esitoRichiesta, false).toString();
	}
	/**
	 * Serialize to String the object <var>esitoRichiesta</var> of type {@link org.openspcoop2.core.integrazione.EsitoRichiesta}
	 * 
	 * @param esitoRichiesta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EsitoRichiesta esitoRichiesta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EsitoRichiesta.class, esitoRichiesta, prettyPrint).toString();
	}
	
	
	

}
