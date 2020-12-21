/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.allarmi.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.AllarmeMail;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeScript;
import org.openspcoop2.core.allarmi.AllarmeFiltro;
import org.openspcoop2.core.allarmi.AllarmeRaggruppamento;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.ElencoAllarmi;
import org.openspcoop2.core.allarmi.ElencoIdAllarmi;

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
	 Object: id-allarme
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAllarme</var> of type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAllarme</var>
	 * @param idAllarme Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAllarme idAllarme) throws SerializerException {
		this.objToXml(fileName, IdAllarme.class, idAllarme, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAllarme</var> of type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAllarme</var>
	 * @param idAllarme Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAllarme idAllarme,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdAllarme.class, idAllarme, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAllarme</var> of type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param file Xml file to serialize the object <var>idAllarme</var>
	 * @param idAllarme Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAllarme idAllarme) throws SerializerException {
		this.objToXml(file, IdAllarme.class, idAllarme, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAllarme</var> of type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param file Xml file to serialize the object <var>idAllarme</var>
	 * @param idAllarme Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAllarme idAllarme,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdAllarme.class, idAllarme, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAllarme</var> of type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param out OutputStream to serialize the object <var>idAllarme</var>
	 * @param idAllarme Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAllarme idAllarme) throws SerializerException {
		this.objToXml(out, IdAllarme.class, idAllarme, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAllarme</var> of type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param out OutputStream to serialize the object <var>idAllarme</var>
	 * @param idAllarme Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAllarme idAllarme,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdAllarme.class, idAllarme, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idAllarme</var> of type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param idAllarme Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAllarme idAllarme) throws SerializerException {
		return this.objToXml(IdAllarme.class, idAllarme, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idAllarme</var> of type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param idAllarme Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAllarme idAllarme,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAllarme.class, idAllarme, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idAllarme</var> of type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param idAllarme Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAllarme idAllarme) throws SerializerException {
		return this.objToXml(IdAllarme.class, idAllarme, false).toString();
	}
	/**
	 * Serialize to String the object <var>idAllarme</var> of type {@link org.openspcoop2.core.allarmi.IdAllarme}
	 * 
	 * @param idAllarme Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAllarme idAllarme,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAllarme.class, idAllarme, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: allarme-history
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeHistory</var> of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeHistory</var>
	 * @param allarmeHistory Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeHistory allarmeHistory) throws SerializerException {
		this.objToXml(fileName, AllarmeHistory.class, allarmeHistory, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeHistory</var> of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeHistory</var>
	 * @param allarmeHistory Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeHistory allarmeHistory,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AllarmeHistory.class, allarmeHistory, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeHistory</var> of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeHistory</var>
	 * @param allarmeHistory Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeHistory allarmeHistory) throws SerializerException {
		this.objToXml(file, AllarmeHistory.class, allarmeHistory, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeHistory</var> of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeHistory</var>
	 * @param allarmeHistory Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeHistory allarmeHistory,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AllarmeHistory.class, allarmeHistory, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeHistory</var> of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeHistory</var>
	 * @param allarmeHistory Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeHistory allarmeHistory) throws SerializerException {
		this.objToXml(out, AllarmeHistory.class, allarmeHistory, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeHistory</var> of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeHistory</var>
	 * @param allarmeHistory Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeHistory allarmeHistory,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AllarmeHistory.class, allarmeHistory, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>allarmeHistory</var> of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param allarmeHistory Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeHistory allarmeHistory) throws SerializerException {
		return this.objToXml(AllarmeHistory.class, allarmeHistory, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allarmeHistory</var> of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param allarmeHistory Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeHistory allarmeHistory,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeHistory.class, allarmeHistory, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>allarmeHistory</var> of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param allarmeHistory Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeHistory allarmeHistory) throws SerializerException {
		return this.objToXml(AllarmeHistory.class, allarmeHistory, false).toString();
	}
	/**
	 * Serialize to String the object <var>allarmeHistory</var> of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 * 
	 * @param allarmeHistory Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeHistory allarmeHistory,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeHistory.class, allarmeHistory, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: allarme-mail
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeMail</var> of type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeMail</var>
	 * @param allarmeMail Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeMail allarmeMail) throws SerializerException {
		this.objToXml(fileName, AllarmeMail.class, allarmeMail, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeMail</var> of type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeMail</var>
	 * @param allarmeMail Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeMail allarmeMail,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AllarmeMail.class, allarmeMail, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeMail</var> of type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeMail</var>
	 * @param allarmeMail Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeMail allarmeMail) throws SerializerException {
		this.objToXml(file, AllarmeMail.class, allarmeMail, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeMail</var> of type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeMail</var>
	 * @param allarmeMail Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeMail allarmeMail,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AllarmeMail.class, allarmeMail, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeMail</var> of type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeMail</var>
	 * @param allarmeMail Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeMail allarmeMail) throws SerializerException {
		this.objToXml(out, AllarmeMail.class, allarmeMail, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeMail</var> of type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeMail</var>
	 * @param allarmeMail Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeMail allarmeMail,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AllarmeMail.class, allarmeMail, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>allarmeMail</var> of type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param allarmeMail Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeMail allarmeMail) throws SerializerException {
		return this.objToXml(AllarmeMail.class, allarmeMail, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allarmeMail</var> of type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param allarmeMail Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeMail allarmeMail,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeMail.class, allarmeMail, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>allarmeMail</var> of type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param allarmeMail Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeMail allarmeMail) throws SerializerException {
		return this.objToXml(AllarmeMail.class, allarmeMail, false).toString();
	}
	/**
	 * Serialize to String the object <var>allarmeMail</var> of type {@link org.openspcoop2.core.allarmi.AllarmeMail}
	 * 
	 * @param allarmeMail Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeMail allarmeMail,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeMail.class, allarmeMail, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: allarme
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarme</var> of type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarme</var>
	 * @param allarme Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Allarme allarme) throws SerializerException {
		this.objToXml(fileName, Allarme.class, allarme, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarme</var> of type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarme</var>
	 * @param allarme Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Allarme allarme,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Allarme.class, allarme, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarme</var> of type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param file Xml file to serialize the object <var>allarme</var>
	 * @param allarme Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Allarme allarme) throws SerializerException {
		this.objToXml(file, Allarme.class, allarme, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarme</var> of type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param file Xml file to serialize the object <var>allarme</var>
	 * @param allarme Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Allarme allarme,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Allarme.class, allarme, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarme</var> of type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param out OutputStream to serialize the object <var>allarme</var>
	 * @param allarme Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Allarme allarme) throws SerializerException {
		this.objToXml(out, Allarme.class, allarme, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarme</var> of type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param out OutputStream to serialize the object <var>allarme</var>
	 * @param allarme Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Allarme allarme,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Allarme.class, allarme, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>allarme</var> of type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param allarme Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Allarme allarme) throws SerializerException {
		return this.objToXml(Allarme.class, allarme, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allarme</var> of type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param allarme Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Allarme allarme,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Allarme.class, allarme, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>allarme</var> of type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param allarme Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Allarme allarme) throws SerializerException {
		return this.objToXml(Allarme.class, allarme, false).toString();
	}
	/**
	 * Serialize to String the object <var>allarme</var> of type {@link org.openspcoop2.core.allarmi.Allarme}
	 * 
	 * @param allarme Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Allarme allarme,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Allarme.class, allarme, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: allarme-script
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeScript</var> of type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeScript</var>
	 * @param allarmeScript Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeScript allarmeScript) throws SerializerException {
		this.objToXml(fileName, AllarmeScript.class, allarmeScript, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeScript</var> of type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeScript</var>
	 * @param allarmeScript Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeScript allarmeScript,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AllarmeScript.class, allarmeScript, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeScript</var> of type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeScript</var>
	 * @param allarmeScript Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeScript allarmeScript) throws SerializerException {
		this.objToXml(file, AllarmeScript.class, allarmeScript, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeScript</var> of type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeScript</var>
	 * @param allarmeScript Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeScript allarmeScript,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AllarmeScript.class, allarmeScript, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeScript</var> of type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeScript</var>
	 * @param allarmeScript Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeScript allarmeScript) throws SerializerException {
		this.objToXml(out, AllarmeScript.class, allarmeScript, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeScript</var> of type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeScript</var>
	 * @param allarmeScript Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeScript allarmeScript,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AllarmeScript.class, allarmeScript, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>allarmeScript</var> of type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param allarmeScript Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeScript allarmeScript) throws SerializerException {
		return this.objToXml(AllarmeScript.class, allarmeScript, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allarmeScript</var> of type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param allarmeScript Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeScript allarmeScript,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeScript.class, allarmeScript, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>allarmeScript</var> of type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param allarmeScript Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeScript allarmeScript) throws SerializerException {
		return this.objToXml(AllarmeScript.class, allarmeScript, false).toString();
	}
	/**
	 * Serialize to String the object <var>allarmeScript</var> of type {@link org.openspcoop2.core.allarmi.AllarmeScript}
	 * 
	 * @param allarmeScript Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeScript allarmeScript,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeScript.class, allarmeScript, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: allarme-filtro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeFiltro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeFiltro</var>
	 * @param allarmeFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeFiltro allarmeFiltro) throws SerializerException {
		this.objToXml(fileName, AllarmeFiltro.class, allarmeFiltro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeFiltro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeFiltro</var>
	 * @param allarmeFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeFiltro allarmeFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AllarmeFiltro.class, allarmeFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeFiltro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeFiltro</var>
	 * @param allarmeFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeFiltro allarmeFiltro) throws SerializerException {
		this.objToXml(file, AllarmeFiltro.class, allarmeFiltro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeFiltro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeFiltro</var>
	 * @param allarmeFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeFiltro allarmeFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AllarmeFiltro.class, allarmeFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeFiltro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeFiltro</var>
	 * @param allarmeFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeFiltro allarmeFiltro) throws SerializerException {
		this.objToXml(out, AllarmeFiltro.class, allarmeFiltro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeFiltro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeFiltro</var>
	 * @param allarmeFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeFiltro allarmeFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AllarmeFiltro.class, allarmeFiltro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>allarmeFiltro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param allarmeFiltro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeFiltro allarmeFiltro) throws SerializerException {
		return this.objToXml(AllarmeFiltro.class, allarmeFiltro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allarmeFiltro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param allarmeFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeFiltro allarmeFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeFiltro.class, allarmeFiltro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>allarmeFiltro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param allarmeFiltro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeFiltro allarmeFiltro) throws SerializerException {
		return this.objToXml(AllarmeFiltro.class, allarmeFiltro, false).toString();
	}
	/**
	 * Serialize to String the object <var>allarmeFiltro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeFiltro}
	 * 
	 * @param allarmeFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeFiltro allarmeFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeFiltro.class, allarmeFiltro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: allarme-raggruppamento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeRaggruppamento</var> of type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeRaggruppamento</var>
	 * @param allarmeRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeRaggruppamento allarmeRaggruppamento) throws SerializerException {
		this.objToXml(fileName, AllarmeRaggruppamento.class, allarmeRaggruppamento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeRaggruppamento</var> of type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeRaggruppamento</var>
	 * @param allarmeRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeRaggruppamento allarmeRaggruppamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AllarmeRaggruppamento.class, allarmeRaggruppamento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeRaggruppamento</var> of type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeRaggruppamento</var>
	 * @param allarmeRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeRaggruppamento allarmeRaggruppamento) throws SerializerException {
		this.objToXml(file, AllarmeRaggruppamento.class, allarmeRaggruppamento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeRaggruppamento</var> of type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeRaggruppamento</var>
	 * @param allarmeRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeRaggruppamento allarmeRaggruppamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AllarmeRaggruppamento.class, allarmeRaggruppamento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeRaggruppamento</var> of type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeRaggruppamento</var>
	 * @param allarmeRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeRaggruppamento allarmeRaggruppamento) throws SerializerException {
		this.objToXml(out, AllarmeRaggruppamento.class, allarmeRaggruppamento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeRaggruppamento</var> of type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeRaggruppamento</var>
	 * @param allarmeRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeRaggruppamento allarmeRaggruppamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AllarmeRaggruppamento.class, allarmeRaggruppamento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>allarmeRaggruppamento</var> of type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param allarmeRaggruppamento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeRaggruppamento allarmeRaggruppamento) throws SerializerException {
		return this.objToXml(AllarmeRaggruppamento.class, allarmeRaggruppamento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allarmeRaggruppamento</var> of type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param allarmeRaggruppamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeRaggruppamento allarmeRaggruppamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeRaggruppamento.class, allarmeRaggruppamento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>allarmeRaggruppamento</var> of type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param allarmeRaggruppamento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeRaggruppamento allarmeRaggruppamento) throws SerializerException {
		return this.objToXml(AllarmeRaggruppamento.class, allarmeRaggruppamento, false).toString();
	}
	/**
	 * Serialize to String the object <var>allarmeRaggruppamento</var> of type {@link org.openspcoop2.core.allarmi.AllarmeRaggruppamento}
	 * 
	 * @param allarmeRaggruppamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeRaggruppamento allarmeRaggruppamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeRaggruppamento.class, allarmeRaggruppamento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: allarme-parametro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeParametro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeParametro</var>
	 * @param allarmeParametro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeParametro allarmeParametro) throws SerializerException {
		this.objToXml(fileName, AllarmeParametro.class, allarmeParametro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allarmeParametro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param fileName Xml file to serialize the object <var>allarmeParametro</var>
	 * @param allarmeParametro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllarmeParametro allarmeParametro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AllarmeParametro.class, allarmeParametro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeParametro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeParametro</var>
	 * @param allarmeParametro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeParametro allarmeParametro) throws SerializerException {
		this.objToXml(file, AllarmeParametro.class, allarmeParametro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allarmeParametro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param file Xml file to serialize the object <var>allarmeParametro</var>
	 * @param allarmeParametro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllarmeParametro allarmeParametro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AllarmeParametro.class, allarmeParametro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeParametro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeParametro</var>
	 * @param allarmeParametro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeParametro allarmeParametro) throws SerializerException {
		this.objToXml(out, AllarmeParametro.class, allarmeParametro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allarmeParametro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param out OutputStream to serialize the object <var>allarmeParametro</var>
	 * @param allarmeParametro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllarmeParametro allarmeParametro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AllarmeParametro.class, allarmeParametro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>allarmeParametro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param allarmeParametro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeParametro allarmeParametro) throws SerializerException {
		return this.objToXml(AllarmeParametro.class, allarmeParametro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allarmeParametro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param allarmeParametro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllarmeParametro allarmeParametro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeParametro.class, allarmeParametro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>allarmeParametro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param allarmeParametro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeParametro allarmeParametro) throws SerializerException {
		return this.objToXml(AllarmeParametro.class, allarmeParametro, false).toString();
	}
	/**
	 * Serialize to String the object <var>allarmeParametro</var> of type {@link org.openspcoop2.core.allarmi.AllarmeParametro}
	 * 
	 * @param allarmeParametro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllarmeParametro allarmeParametro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllarmeParametro.class, allarmeParametro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-allarmi
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoAllarmi</var>
	 * @param elencoAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoAllarmi elencoAllarmi) throws SerializerException {
		this.objToXml(fileName, ElencoAllarmi.class, elencoAllarmi, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoAllarmi</var>
	 * @param elencoAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoAllarmi elencoAllarmi,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoAllarmi.class, elencoAllarmi, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param file Xml file to serialize the object <var>elencoAllarmi</var>
	 * @param elencoAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoAllarmi elencoAllarmi) throws SerializerException {
		this.objToXml(file, ElencoAllarmi.class, elencoAllarmi, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param file Xml file to serialize the object <var>elencoAllarmi</var>
	 * @param elencoAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoAllarmi elencoAllarmi,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoAllarmi.class, elencoAllarmi, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoAllarmi</var>
	 * @param elencoAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoAllarmi elencoAllarmi) throws SerializerException {
		this.objToXml(out, ElencoAllarmi.class, elencoAllarmi, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoAllarmi</var>
	 * @param elencoAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoAllarmi elencoAllarmi,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoAllarmi.class, elencoAllarmi, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param elencoAllarmi Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoAllarmi elencoAllarmi) throws SerializerException {
		return this.objToXml(ElencoAllarmi.class, elencoAllarmi, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param elencoAllarmi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoAllarmi elencoAllarmi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoAllarmi.class, elencoAllarmi, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param elencoAllarmi Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoAllarmi elencoAllarmi) throws SerializerException {
		return this.objToXml(ElencoAllarmi.class, elencoAllarmi, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoAllarmi}
	 * 
	 * @param elencoAllarmi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoAllarmi elencoAllarmi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoAllarmi.class, elencoAllarmi, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-allarmi
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdAllarmi</var>
	 * @param elencoIdAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdAllarmi elencoIdAllarmi) throws SerializerException {
		this.objToXml(fileName, ElencoIdAllarmi.class, elencoIdAllarmi, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdAllarmi</var>
	 * @param elencoIdAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdAllarmi elencoIdAllarmi,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoIdAllarmi.class, elencoIdAllarmi, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdAllarmi</var>
	 * @param elencoIdAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdAllarmi elencoIdAllarmi) throws SerializerException {
		this.objToXml(file, ElencoIdAllarmi.class, elencoIdAllarmi, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdAllarmi</var>
	 * @param elencoIdAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdAllarmi elencoIdAllarmi,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoIdAllarmi.class, elencoIdAllarmi, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdAllarmi</var>
	 * @param elencoIdAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdAllarmi elencoIdAllarmi) throws SerializerException {
		this.objToXml(out, ElencoIdAllarmi.class, elencoIdAllarmi, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdAllarmi</var>
	 * @param elencoIdAllarmi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdAllarmi elencoIdAllarmi,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoIdAllarmi.class, elencoIdAllarmi, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoIdAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param elencoIdAllarmi Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdAllarmi elencoIdAllarmi) throws SerializerException {
		return this.objToXml(ElencoIdAllarmi.class, elencoIdAllarmi, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoIdAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param elencoIdAllarmi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdAllarmi elencoIdAllarmi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdAllarmi.class, elencoIdAllarmi, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoIdAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param elencoIdAllarmi Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdAllarmi elencoIdAllarmi) throws SerializerException {
		return this.objToXml(ElencoIdAllarmi.class, elencoIdAllarmi, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoIdAllarmi</var> of type {@link org.openspcoop2.core.allarmi.ElencoIdAllarmi}
	 * 
	 * @param elencoIdAllarmi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdAllarmi elencoIdAllarmi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdAllarmi.class, elencoIdAllarmi, prettyPrint).toString();
	}
	
	
	

}
