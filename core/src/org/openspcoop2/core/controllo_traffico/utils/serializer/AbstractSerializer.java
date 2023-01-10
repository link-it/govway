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
package org.openspcoop2.core.controllo_traffico.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoPolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione;
import org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione;
import org.openspcoop2.core.controllo_traffico.Cache;
import org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;

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
				fout.flush();
			}catch(Exception e){
				// ignore
			}
			try{
				fout.close();
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
				bout.flush();
			}catch(Exception e){
				// ignore
			}
			try{
				bout.close();
			}catch(Exception e){
				// ignore
			}
		}
		return bout;
	}




	/*
	 =================================================================================
	 Object: id-active-policy
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idActivePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>idActivePolicy</var>
	 * @param idActivePolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdActivePolicy idActivePolicy) throws SerializerException {
		this.objToXml(fileName, IdActivePolicy.class, idActivePolicy, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idActivePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>idActivePolicy</var>
	 * @param idActivePolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdActivePolicy idActivePolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdActivePolicy.class, idActivePolicy, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idActivePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param file Xml file to serialize the object <var>idActivePolicy</var>
	 * @param idActivePolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdActivePolicy idActivePolicy) throws SerializerException {
		this.objToXml(file, IdActivePolicy.class, idActivePolicy, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idActivePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param file Xml file to serialize the object <var>idActivePolicy</var>
	 * @param idActivePolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdActivePolicy idActivePolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdActivePolicy.class, idActivePolicy, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idActivePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>idActivePolicy</var>
	 * @param idActivePolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdActivePolicy idActivePolicy) throws SerializerException {
		this.objToXml(out, IdActivePolicy.class, idActivePolicy, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idActivePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>idActivePolicy</var>
	 * @param idActivePolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdActivePolicy idActivePolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdActivePolicy.class, idActivePolicy, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idActivePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param idActivePolicy Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdActivePolicy idActivePolicy) throws SerializerException {
		return this.objToXml(IdActivePolicy.class, idActivePolicy, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idActivePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param idActivePolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdActivePolicy idActivePolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdActivePolicy.class, idActivePolicy, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idActivePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param idActivePolicy Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdActivePolicy idActivePolicy) throws SerializerException {
		return this.objToXml(IdActivePolicy.class, idActivePolicy, false).toString();
	}
	/**
	 * Serialize to String the object <var>idActivePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param idActivePolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdActivePolicy idActivePolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdActivePolicy.class, idActivePolicy, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: attivazione-policy
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attivazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>attivazionePolicy</var>
	 * @param attivazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AttivazionePolicy attivazionePolicy) throws SerializerException {
		this.objToXml(fileName, AttivazionePolicy.class, attivazionePolicy, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attivazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>attivazionePolicy</var>
	 * @param attivazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AttivazionePolicy attivazionePolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AttivazionePolicy.class, attivazionePolicy, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>attivazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param file Xml file to serialize the object <var>attivazionePolicy</var>
	 * @param attivazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AttivazionePolicy attivazionePolicy) throws SerializerException {
		this.objToXml(file, AttivazionePolicy.class, attivazionePolicy, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>attivazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param file Xml file to serialize the object <var>attivazionePolicy</var>
	 * @param attivazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AttivazionePolicy attivazionePolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AttivazionePolicy.class, attivazionePolicy, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>attivazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>attivazionePolicy</var>
	 * @param attivazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AttivazionePolicy attivazionePolicy) throws SerializerException {
		this.objToXml(out, AttivazionePolicy.class, attivazionePolicy, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>attivazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>attivazionePolicy</var>
	 * @param attivazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AttivazionePolicy attivazionePolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AttivazionePolicy.class, attivazionePolicy, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>attivazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param attivazionePolicy Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AttivazionePolicy attivazionePolicy) throws SerializerException {
		return this.objToXml(AttivazionePolicy.class, attivazionePolicy, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>attivazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param attivazionePolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AttivazionePolicy attivazionePolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AttivazionePolicy.class, attivazionePolicy, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>attivazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param attivazionePolicy Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AttivazionePolicy attivazionePolicy) throws SerializerException {
		return this.objToXml(AttivazionePolicy.class, attivazionePolicy, false).toString();
	}
	/**
	 * Serialize to String the object <var>attivazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param attivazionePolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AttivazionePolicy attivazionePolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AttivazionePolicy.class, attivazionePolicy, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: attivazione-policy-filtro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attivazionePolicyFiltro</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>attivazionePolicyFiltro</var>
	 * @param attivazionePolicyFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AttivazionePolicyFiltro attivazionePolicyFiltro) throws SerializerException {
		this.objToXml(fileName, AttivazionePolicyFiltro.class, attivazionePolicyFiltro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attivazionePolicyFiltro</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param fileName Xml file to serialize the object <var>attivazionePolicyFiltro</var>
	 * @param attivazionePolicyFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AttivazionePolicyFiltro attivazionePolicyFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AttivazionePolicyFiltro.class, attivazionePolicyFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>attivazionePolicyFiltro</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>attivazionePolicyFiltro</var>
	 * @param attivazionePolicyFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AttivazionePolicyFiltro attivazionePolicyFiltro) throws SerializerException {
		this.objToXml(file, AttivazionePolicyFiltro.class, attivazionePolicyFiltro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>attivazionePolicyFiltro</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param file Xml file to serialize the object <var>attivazionePolicyFiltro</var>
	 * @param attivazionePolicyFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AttivazionePolicyFiltro attivazionePolicyFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AttivazionePolicyFiltro.class, attivazionePolicyFiltro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>attivazionePolicyFiltro</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>attivazionePolicyFiltro</var>
	 * @param attivazionePolicyFiltro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AttivazionePolicyFiltro attivazionePolicyFiltro) throws SerializerException {
		this.objToXml(out, AttivazionePolicyFiltro.class, attivazionePolicyFiltro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>attivazionePolicyFiltro</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param out OutputStream to serialize the object <var>attivazionePolicyFiltro</var>
	 * @param attivazionePolicyFiltro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AttivazionePolicyFiltro attivazionePolicyFiltro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AttivazionePolicyFiltro.class, attivazionePolicyFiltro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>attivazionePolicyFiltro</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param attivazionePolicyFiltro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AttivazionePolicyFiltro attivazionePolicyFiltro) throws SerializerException {
		return this.objToXml(AttivazionePolicyFiltro.class, attivazionePolicyFiltro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>attivazionePolicyFiltro</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param attivazionePolicyFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AttivazionePolicyFiltro attivazionePolicyFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AttivazionePolicyFiltro.class, attivazionePolicyFiltro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>attivazionePolicyFiltro</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param attivazionePolicyFiltro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AttivazionePolicyFiltro attivazionePolicyFiltro) throws SerializerException {
		return this.objToXml(AttivazionePolicyFiltro.class, attivazionePolicyFiltro, false).toString();
	}
	/**
	 * Serialize to String the object <var>attivazionePolicyFiltro</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param attivazionePolicyFiltro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AttivazionePolicyFiltro attivazionePolicyFiltro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AttivazionePolicyFiltro.class, attivazionePolicyFiltro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: attivazione-policy-raggruppamento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attivazionePolicyRaggruppamento</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>attivazionePolicyRaggruppamento</var>
	 * @param attivazionePolicyRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AttivazionePolicyRaggruppamento attivazionePolicyRaggruppamento) throws SerializerException {
		this.objToXml(fileName, AttivazionePolicyRaggruppamento.class, attivazionePolicyRaggruppamento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attivazionePolicyRaggruppamento</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>attivazionePolicyRaggruppamento</var>
	 * @param attivazionePolicyRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AttivazionePolicyRaggruppamento attivazionePolicyRaggruppamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AttivazionePolicyRaggruppamento.class, attivazionePolicyRaggruppamento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>attivazionePolicyRaggruppamento</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param file Xml file to serialize the object <var>attivazionePolicyRaggruppamento</var>
	 * @param attivazionePolicyRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AttivazionePolicyRaggruppamento attivazionePolicyRaggruppamento) throws SerializerException {
		this.objToXml(file, AttivazionePolicyRaggruppamento.class, attivazionePolicyRaggruppamento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>attivazionePolicyRaggruppamento</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param file Xml file to serialize the object <var>attivazionePolicyRaggruppamento</var>
	 * @param attivazionePolicyRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AttivazionePolicyRaggruppamento attivazionePolicyRaggruppamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AttivazionePolicyRaggruppamento.class, attivazionePolicyRaggruppamento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>attivazionePolicyRaggruppamento</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param out OutputStream to serialize the object <var>attivazionePolicyRaggruppamento</var>
	 * @param attivazionePolicyRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AttivazionePolicyRaggruppamento attivazionePolicyRaggruppamento) throws SerializerException {
		this.objToXml(out, AttivazionePolicyRaggruppamento.class, attivazionePolicyRaggruppamento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>attivazionePolicyRaggruppamento</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param out OutputStream to serialize the object <var>attivazionePolicyRaggruppamento</var>
	 * @param attivazionePolicyRaggruppamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AttivazionePolicyRaggruppamento attivazionePolicyRaggruppamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AttivazionePolicyRaggruppamento.class, attivazionePolicyRaggruppamento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>attivazionePolicyRaggruppamento</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param attivazionePolicyRaggruppamento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AttivazionePolicyRaggruppamento attivazionePolicyRaggruppamento) throws SerializerException {
		return this.objToXml(AttivazionePolicyRaggruppamento.class, attivazionePolicyRaggruppamento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>attivazionePolicyRaggruppamento</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param attivazionePolicyRaggruppamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AttivazionePolicyRaggruppamento attivazionePolicyRaggruppamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AttivazionePolicyRaggruppamento.class, attivazionePolicyRaggruppamento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>attivazionePolicyRaggruppamento</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param attivazionePolicyRaggruppamento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AttivazionePolicyRaggruppamento attivazionePolicyRaggruppamento) throws SerializerException {
		return this.objToXml(AttivazionePolicyRaggruppamento.class, attivazionePolicyRaggruppamento, false).toString();
	}
	/**
	 * Serialize to String the object <var>attivazionePolicyRaggruppamento</var> of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param attivazionePolicyRaggruppamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AttivazionePolicyRaggruppamento attivazionePolicyRaggruppamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AttivazionePolicyRaggruppamento.class, attivazionePolicyRaggruppamento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-rate-limiting
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneRateLimiting</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneRateLimiting</var>
	 * @param configurazioneRateLimiting Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneRateLimiting configurazioneRateLimiting) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneRateLimiting.class, configurazioneRateLimiting, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneRateLimiting</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneRateLimiting</var>
	 * @param configurazioneRateLimiting Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneRateLimiting configurazioneRateLimiting,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneRateLimiting.class, configurazioneRateLimiting, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneRateLimiting</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneRateLimiting</var>
	 * @param configurazioneRateLimiting Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneRateLimiting configurazioneRateLimiting) throws SerializerException {
		this.objToXml(file, ConfigurazioneRateLimiting.class, configurazioneRateLimiting, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneRateLimiting</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneRateLimiting</var>
	 * @param configurazioneRateLimiting Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneRateLimiting configurazioneRateLimiting,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneRateLimiting.class, configurazioneRateLimiting, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneRateLimiting</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneRateLimiting</var>
	 * @param configurazioneRateLimiting Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneRateLimiting configurazioneRateLimiting) throws SerializerException {
		this.objToXml(out, ConfigurazioneRateLimiting.class, configurazioneRateLimiting, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneRateLimiting</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneRateLimiting</var>
	 * @param configurazioneRateLimiting Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneRateLimiting configurazioneRateLimiting,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneRateLimiting.class, configurazioneRateLimiting, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneRateLimiting</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param configurazioneRateLimiting Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneRateLimiting configurazioneRateLimiting) throws SerializerException {
		return this.objToXml(ConfigurazioneRateLimiting.class, configurazioneRateLimiting, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneRateLimiting</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param configurazioneRateLimiting Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneRateLimiting configurazioneRateLimiting,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneRateLimiting.class, configurazioneRateLimiting, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneRateLimiting</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param configurazioneRateLimiting Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneRateLimiting configurazioneRateLimiting) throws SerializerException {
		return this.objToXml(ConfigurazioneRateLimiting.class, configurazioneRateLimiting, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneRateLimiting</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param configurazioneRateLimiting Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneRateLimiting configurazioneRateLimiting,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneRateLimiting.class, configurazioneRateLimiting, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-rate-limiting-proprieta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneRateLimitingProprieta</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneRateLimitingProprieta</var>
	 * @param configurazioneRateLimitingProprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneRateLimitingProprieta.class, configurazioneRateLimitingProprieta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneRateLimitingProprieta</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneRateLimitingProprieta</var>
	 * @param configurazioneRateLimitingProprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneRateLimitingProprieta.class, configurazioneRateLimitingProprieta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneRateLimitingProprieta</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneRateLimitingProprieta</var>
	 * @param configurazioneRateLimitingProprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta) throws SerializerException {
		this.objToXml(file, ConfigurazioneRateLimitingProprieta.class, configurazioneRateLimitingProprieta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneRateLimitingProprieta</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneRateLimitingProprieta</var>
	 * @param configurazioneRateLimitingProprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneRateLimitingProprieta.class, configurazioneRateLimitingProprieta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneRateLimitingProprieta</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneRateLimitingProprieta</var>
	 * @param configurazioneRateLimitingProprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta) throws SerializerException {
		this.objToXml(out, ConfigurazioneRateLimitingProprieta.class, configurazioneRateLimitingProprieta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneRateLimitingProprieta</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneRateLimitingProprieta</var>
	 * @param configurazioneRateLimitingProprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneRateLimitingProprieta.class, configurazioneRateLimitingProprieta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneRateLimitingProprieta</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 * 
	 * @param configurazioneRateLimitingProprieta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta) throws SerializerException {
		return this.objToXml(ConfigurazioneRateLimitingProprieta.class, configurazioneRateLimitingProprieta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneRateLimitingProprieta</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 * 
	 * @param configurazioneRateLimitingProprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneRateLimitingProprieta.class, configurazioneRateLimitingProprieta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneRateLimitingProprieta</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 * 
	 * @param configurazioneRateLimitingProprieta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta) throws SerializerException {
		return this.objToXml(ConfigurazioneRateLimitingProprieta.class, configurazioneRateLimitingProprieta, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneRateLimitingProprieta</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 * 
	 * @param configurazioneRateLimitingProprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneRateLimitingProprieta.class, configurazioneRateLimitingProprieta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-policy
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazionePolicy</var>
	 * @param configurazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazionePolicy configurazionePolicy) throws SerializerException {
		this.objToXml(fileName, ConfigurazionePolicy.class, configurazionePolicy, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazionePolicy</var>
	 * @param configurazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazionePolicy configurazionePolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazionePolicy.class, configurazionePolicy, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param file Xml file to serialize the object <var>configurazionePolicy</var>
	 * @param configurazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazionePolicy configurazionePolicy) throws SerializerException {
		this.objToXml(file, ConfigurazionePolicy.class, configurazionePolicy, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param file Xml file to serialize the object <var>configurazionePolicy</var>
	 * @param configurazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazionePolicy configurazionePolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazionePolicy.class, configurazionePolicy, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazionePolicy</var>
	 * @param configurazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazionePolicy configurazionePolicy) throws SerializerException {
		this.objToXml(out, ConfigurazionePolicy.class, configurazionePolicy, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazionePolicy</var>
	 * @param configurazionePolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazionePolicy configurazionePolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazionePolicy.class, configurazionePolicy, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param configurazionePolicy Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazionePolicy configurazionePolicy) throws SerializerException {
		return this.objToXml(ConfigurazionePolicy.class, configurazionePolicy, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param configurazionePolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazionePolicy configurazionePolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazionePolicy.class, configurazionePolicy, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param configurazionePolicy Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazionePolicy configurazionePolicy) throws SerializerException {
		return this.objToXml(ConfigurazionePolicy.class, configurazionePolicy, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazionePolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param configurazionePolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazionePolicy configurazionePolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazionePolicy.class, configurazionePolicy, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-policy
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoPolicy</var>
	 * @param elencoPolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoPolicy elencoPolicy) throws SerializerException {
		this.objToXml(fileName, ElencoPolicy.class, elencoPolicy, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoPolicy</var>
	 * @param elencoPolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoPolicy elencoPolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoPolicy.class, elencoPolicy, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param file Xml file to serialize the object <var>elencoPolicy</var>
	 * @param elencoPolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoPolicy elencoPolicy) throws SerializerException {
		this.objToXml(file, ElencoPolicy.class, elencoPolicy, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param file Xml file to serialize the object <var>elencoPolicy</var>
	 * @param elencoPolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoPolicy elencoPolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoPolicy.class, elencoPolicy, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoPolicy</var>
	 * @param elencoPolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoPolicy elencoPolicy) throws SerializerException {
		this.objToXml(out, ElencoPolicy.class, elencoPolicy, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoPolicy</var>
	 * @param elencoPolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoPolicy elencoPolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoPolicy.class, elencoPolicy, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param elencoPolicy Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoPolicy elencoPolicy) throws SerializerException {
		return this.objToXml(ElencoPolicy.class, elencoPolicy, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param elencoPolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoPolicy elencoPolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoPolicy.class, elencoPolicy, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param elencoPolicy Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoPolicy elencoPolicy) throws SerializerException {
		return this.objToXml(ElencoPolicy.class, elencoPolicy, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param elencoPolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoPolicy elencoPolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoPolicy.class, elencoPolicy, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-controllo-traffico
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneControlloTraffico</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneControlloTraffico</var>
	 * @param configurazioneControlloTraffico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneControlloTraffico.class, configurazioneControlloTraffico, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneControlloTraffico</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneControlloTraffico</var>
	 * @param configurazioneControlloTraffico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneControlloTraffico configurazioneControlloTraffico,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneControlloTraffico.class, configurazioneControlloTraffico, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneControlloTraffico</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneControlloTraffico</var>
	 * @param configurazioneControlloTraffico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws SerializerException {
		this.objToXml(file, ConfigurazioneControlloTraffico.class, configurazioneControlloTraffico, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneControlloTraffico</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneControlloTraffico</var>
	 * @param configurazioneControlloTraffico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneControlloTraffico configurazioneControlloTraffico,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneControlloTraffico.class, configurazioneControlloTraffico, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneControlloTraffico</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneControlloTraffico</var>
	 * @param configurazioneControlloTraffico Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws SerializerException {
		this.objToXml(out, ConfigurazioneControlloTraffico.class, configurazioneControlloTraffico, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneControlloTraffico</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneControlloTraffico</var>
	 * @param configurazioneControlloTraffico Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneControlloTraffico configurazioneControlloTraffico,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneControlloTraffico.class, configurazioneControlloTraffico, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneControlloTraffico</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param configurazioneControlloTraffico Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws SerializerException {
		return this.objToXml(ConfigurazioneControlloTraffico.class, configurazioneControlloTraffico, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneControlloTraffico</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param configurazioneControlloTraffico Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneControlloTraffico configurazioneControlloTraffico,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneControlloTraffico.class, configurazioneControlloTraffico, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneControlloTraffico</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param configurazioneControlloTraffico Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws SerializerException {
		return this.objToXml(ConfigurazioneControlloTraffico.class, configurazioneControlloTraffico, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneControlloTraffico</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param configurazioneControlloTraffico Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneControlloTraffico configurazioneControlloTraffico,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneControlloTraffico.class, configurazioneControlloTraffico, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-generale
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneGenerale</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneGenerale</var>
	 * @param configurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneGenerale configurazioneGenerale) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneGenerale.class, configurazioneGenerale, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneGenerale</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneGenerale</var>
	 * @param configurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneGenerale configurazioneGenerale,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneGenerale.class, configurazioneGenerale, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneGenerale</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneGenerale</var>
	 * @param configurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneGenerale configurazioneGenerale) throws SerializerException {
		this.objToXml(file, ConfigurazioneGenerale.class, configurazioneGenerale, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneGenerale</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneGenerale</var>
	 * @param configurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneGenerale configurazioneGenerale,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneGenerale.class, configurazioneGenerale, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneGenerale</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneGenerale</var>
	 * @param configurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneGenerale configurazioneGenerale) throws SerializerException {
		this.objToXml(out, ConfigurazioneGenerale.class, configurazioneGenerale, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneGenerale</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneGenerale</var>
	 * @param configurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneGenerale configurazioneGenerale,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneGenerale.class, configurazioneGenerale, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneGenerale</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param configurazioneGenerale Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneGenerale configurazioneGenerale) throws SerializerException {
		return this.objToXml(ConfigurazioneGenerale.class, configurazioneGenerale, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneGenerale</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param configurazioneGenerale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneGenerale configurazioneGenerale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneGenerale.class, configurazioneGenerale, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneGenerale</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param configurazioneGenerale Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneGenerale configurazioneGenerale) throws SerializerException {
		return this.objToXml(ConfigurazioneGenerale.class, configurazioneGenerale, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneGenerale</var> of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param configurazioneGenerale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneGenerale configurazioneGenerale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneGenerale.class, configurazioneGenerale, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: tempi-risposta-fruizione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tempiRispostaFruizione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param fileName Xml file to serialize the object <var>tempiRispostaFruizione</var>
	 * @param tempiRispostaFruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TempiRispostaFruizione tempiRispostaFruizione) throws SerializerException {
		this.objToXml(fileName, TempiRispostaFruizione.class, tempiRispostaFruizione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tempiRispostaFruizione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param fileName Xml file to serialize the object <var>tempiRispostaFruizione</var>
	 * @param tempiRispostaFruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TempiRispostaFruizione tempiRispostaFruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TempiRispostaFruizione.class, tempiRispostaFruizione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>tempiRispostaFruizione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param file Xml file to serialize the object <var>tempiRispostaFruizione</var>
	 * @param tempiRispostaFruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TempiRispostaFruizione tempiRispostaFruizione) throws SerializerException {
		this.objToXml(file, TempiRispostaFruizione.class, tempiRispostaFruizione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>tempiRispostaFruizione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param file Xml file to serialize the object <var>tempiRispostaFruizione</var>
	 * @param tempiRispostaFruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TempiRispostaFruizione tempiRispostaFruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TempiRispostaFruizione.class, tempiRispostaFruizione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>tempiRispostaFruizione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param out OutputStream to serialize the object <var>tempiRispostaFruizione</var>
	 * @param tempiRispostaFruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TempiRispostaFruizione tempiRispostaFruizione) throws SerializerException {
		this.objToXml(out, TempiRispostaFruizione.class, tempiRispostaFruizione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>tempiRispostaFruizione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param out OutputStream to serialize the object <var>tempiRispostaFruizione</var>
	 * @param tempiRispostaFruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TempiRispostaFruizione tempiRispostaFruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TempiRispostaFruizione.class, tempiRispostaFruizione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>tempiRispostaFruizione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param tempiRispostaFruizione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TempiRispostaFruizione tempiRispostaFruizione) throws SerializerException {
		return this.objToXml(TempiRispostaFruizione.class, tempiRispostaFruizione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>tempiRispostaFruizione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param tempiRispostaFruizione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TempiRispostaFruizione tempiRispostaFruizione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TempiRispostaFruizione.class, tempiRispostaFruizione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>tempiRispostaFruizione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param tempiRispostaFruizione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TempiRispostaFruizione tempiRispostaFruizione) throws SerializerException {
		return this.objToXml(TempiRispostaFruizione.class, tempiRispostaFruizione, false).toString();
	}
	/**
	 * Serialize to String the object <var>tempiRispostaFruizione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param tempiRispostaFruizione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TempiRispostaFruizione tempiRispostaFruizione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TempiRispostaFruizione.class, tempiRispostaFruizione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: tempi-risposta-erogazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tempiRispostaErogazione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>tempiRispostaErogazione</var>
	 * @param tempiRispostaErogazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TempiRispostaErogazione tempiRispostaErogazione) throws SerializerException {
		this.objToXml(fileName, TempiRispostaErogazione.class, tempiRispostaErogazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tempiRispostaErogazione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>tempiRispostaErogazione</var>
	 * @param tempiRispostaErogazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TempiRispostaErogazione tempiRispostaErogazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TempiRispostaErogazione.class, tempiRispostaErogazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>tempiRispostaErogazione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param file Xml file to serialize the object <var>tempiRispostaErogazione</var>
	 * @param tempiRispostaErogazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TempiRispostaErogazione tempiRispostaErogazione) throws SerializerException {
		this.objToXml(file, TempiRispostaErogazione.class, tempiRispostaErogazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>tempiRispostaErogazione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param file Xml file to serialize the object <var>tempiRispostaErogazione</var>
	 * @param tempiRispostaErogazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TempiRispostaErogazione tempiRispostaErogazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TempiRispostaErogazione.class, tempiRispostaErogazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>tempiRispostaErogazione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param out OutputStream to serialize the object <var>tempiRispostaErogazione</var>
	 * @param tempiRispostaErogazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TempiRispostaErogazione tempiRispostaErogazione) throws SerializerException {
		this.objToXml(out, TempiRispostaErogazione.class, tempiRispostaErogazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>tempiRispostaErogazione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param out OutputStream to serialize the object <var>tempiRispostaErogazione</var>
	 * @param tempiRispostaErogazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TempiRispostaErogazione tempiRispostaErogazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TempiRispostaErogazione.class, tempiRispostaErogazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>tempiRispostaErogazione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param tempiRispostaErogazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TempiRispostaErogazione tempiRispostaErogazione) throws SerializerException {
		return this.objToXml(TempiRispostaErogazione.class, tempiRispostaErogazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>tempiRispostaErogazione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param tempiRispostaErogazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TempiRispostaErogazione tempiRispostaErogazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TempiRispostaErogazione.class, tempiRispostaErogazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>tempiRispostaErogazione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param tempiRispostaErogazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TempiRispostaErogazione tempiRispostaErogazione) throws SerializerException {
		return this.objToXml(TempiRispostaErogazione.class, tempiRispostaErogazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>tempiRispostaErogazione</var> of type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param tempiRispostaErogazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TempiRispostaErogazione tempiRispostaErogazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TempiRispostaErogazione.class, tempiRispostaErogazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: cache
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cache</var> of type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param fileName Xml file to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Cache cache) throws SerializerException {
		this.objToXml(fileName, Cache.class, cache, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cache</var> of type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param fileName Xml file to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Cache cache,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Cache.class, cache, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>cache</var> of type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param file Xml file to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Cache cache) throws SerializerException {
		this.objToXml(file, Cache.class, cache, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>cache</var> of type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param file Xml file to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Cache cache,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Cache.class, cache, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>cache</var> of type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param out OutputStream to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Cache cache) throws SerializerException {
		this.objToXml(out, Cache.class, cache, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>cache</var> of type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param out OutputStream to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Cache cache,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Cache.class, cache, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>cache</var> of type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param cache Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Cache cache) throws SerializerException {
		return this.objToXml(Cache.class, cache, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>cache</var> of type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param cache Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Cache cache,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Cache.class, cache, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>cache</var> of type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param cache Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Cache cache) throws SerializerException {
		return this.objToXml(Cache.class, cache, false).toString();
	}
	/**
	 * Serialize to String the object <var>cache</var> of type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param cache Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Cache cache,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Cache.class, cache, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-policy-attive
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoPolicyAttive</var>
	 * @param elencoPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoPolicyAttive elencoPolicyAttive) throws SerializerException {
		this.objToXml(fileName, ElencoPolicyAttive.class, elencoPolicyAttive, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoPolicyAttive</var>
	 * @param elencoPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoPolicyAttive elencoPolicyAttive,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoPolicyAttive.class, elencoPolicyAttive, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param file Xml file to serialize the object <var>elencoPolicyAttive</var>
	 * @param elencoPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoPolicyAttive elencoPolicyAttive) throws SerializerException {
		this.objToXml(file, ElencoPolicyAttive.class, elencoPolicyAttive, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param file Xml file to serialize the object <var>elencoPolicyAttive</var>
	 * @param elencoPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoPolicyAttive elencoPolicyAttive,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoPolicyAttive.class, elencoPolicyAttive, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoPolicyAttive</var>
	 * @param elencoPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoPolicyAttive elencoPolicyAttive) throws SerializerException {
		this.objToXml(out, ElencoPolicyAttive.class, elencoPolicyAttive, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoPolicyAttive</var>
	 * @param elencoPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoPolicyAttive elencoPolicyAttive,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoPolicyAttive.class, elencoPolicyAttive, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param elencoPolicyAttive Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoPolicyAttive elencoPolicyAttive) throws SerializerException {
		return this.objToXml(ElencoPolicyAttive.class, elencoPolicyAttive, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param elencoPolicyAttive Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoPolicyAttive elencoPolicyAttive,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoPolicyAttive.class, elencoPolicyAttive, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param elencoPolicyAttive Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoPolicyAttive elencoPolicyAttive) throws SerializerException {
		return this.objToXml(ElencoPolicyAttive.class, elencoPolicyAttive, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param elencoPolicyAttive Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoPolicyAttive elencoPolicyAttive,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoPolicyAttive.class, elencoPolicyAttive, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-policy
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPolicy</var>
	 * @param idPolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPolicy idPolicy) throws SerializerException {
		this.objToXml(fileName, IdPolicy.class, idPolicy, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPolicy</var>
	 * @param idPolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPolicy idPolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdPolicy.class, idPolicy, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param file Xml file to serialize the object <var>idPolicy</var>
	 * @param idPolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPolicy idPolicy) throws SerializerException {
		this.objToXml(file, IdPolicy.class, idPolicy, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param file Xml file to serialize the object <var>idPolicy</var>
	 * @param idPolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPolicy idPolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdPolicy.class, idPolicy, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>idPolicy</var>
	 * @param idPolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPolicy idPolicy) throws SerializerException {
		this.objToXml(out, IdPolicy.class, idPolicy, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>idPolicy</var>
	 * @param idPolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPolicy idPolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdPolicy.class, idPolicy, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param idPolicy Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPolicy idPolicy) throws SerializerException {
		return this.objToXml(IdPolicy.class, idPolicy, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param idPolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPolicy idPolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPolicy.class, idPolicy, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param idPolicy Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPolicy idPolicy) throws SerializerException {
		return this.objToXml(IdPolicy.class, idPolicy, false).toString();
	}
	/**
	 * Serialize to String the object <var>idPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param idPolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPolicy idPolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPolicy.class, idPolicy, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-policy
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdPolicy</var>
	 * @param elencoIdPolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdPolicy elencoIdPolicy) throws SerializerException {
		this.objToXml(fileName, ElencoIdPolicy.class, elencoIdPolicy, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdPolicy</var>
	 * @param elencoIdPolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdPolicy elencoIdPolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoIdPolicy.class, elencoIdPolicy, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdPolicy</var>
	 * @param elencoIdPolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdPolicy elencoIdPolicy) throws SerializerException {
		this.objToXml(file, ElencoIdPolicy.class, elencoIdPolicy, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdPolicy</var>
	 * @param elencoIdPolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdPolicy elencoIdPolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoIdPolicy.class, elencoIdPolicy, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdPolicy</var>
	 * @param elencoIdPolicy Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdPolicy elencoIdPolicy) throws SerializerException {
		this.objToXml(out, ElencoIdPolicy.class, elencoIdPolicy, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdPolicy</var>
	 * @param elencoIdPolicy Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdPolicy elencoIdPolicy,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoIdPolicy.class, elencoIdPolicy, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoIdPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param elencoIdPolicy Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdPolicy elencoIdPolicy) throws SerializerException {
		return this.objToXml(ElencoIdPolicy.class, elencoIdPolicy, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoIdPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param elencoIdPolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdPolicy elencoIdPolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdPolicy.class, elencoIdPolicy, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoIdPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param elencoIdPolicy Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdPolicy elencoIdPolicy) throws SerializerException {
		return this.objToXml(ElencoIdPolicy.class, elencoIdPolicy, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoIdPolicy</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param elencoIdPolicy Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdPolicy elencoIdPolicy,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdPolicy.class, elencoIdPolicy, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-policy-attive
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdPolicyAttive</var>
	 * @param elencoIdPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdPolicyAttive elencoIdPolicyAttive) throws SerializerException {
		this.objToXml(fileName, ElencoIdPolicyAttive.class, elencoIdPolicyAttive, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoIdPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoIdPolicyAttive</var>
	 * @param elencoIdPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoIdPolicyAttive elencoIdPolicyAttive,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoIdPolicyAttive.class, elencoIdPolicyAttive, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdPolicyAttive</var>
	 * @param elencoIdPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdPolicyAttive elencoIdPolicyAttive) throws SerializerException {
		this.objToXml(file, ElencoIdPolicyAttive.class, elencoIdPolicyAttive, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoIdPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param file Xml file to serialize the object <var>elencoIdPolicyAttive</var>
	 * @param elencoIdPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoIdPolicyAttive elencoIdPolicyAttive,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoIdPolicyAttive.class, elencoIdPolicyAttive, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdPolicyAttive</var>
	 * @param elencoIdPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdPolicyAttive elencoIdPolicyAttive) throws SerializerException {
		this.objToXml(out, ElencoIdPolicyAttive.class, elencoIdPolicyAttive, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoIdPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoIdPolicyAttive</var>
	 * @param elencoIdPolicyAttive Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoIdPolicyAttive elencoIdPolicyAttive,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoIdPolicyAttive.class, elencoIdPolicyAttive, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoIdPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param elencoIdPolicyAttive Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdPolicyAttive elencoIdPolicyAttive) throws SerializerException {
		return this.objToXml(ElencoIdPolicyAttive.class, elencoIdPolicyAttive, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoIdPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param elencoIdPolicyAttive Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoIdPolicyAttive elencoIdPolicyAttive,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdPolicyAttive.class, elencoIdPolicyAttive, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoIdPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param elencoIdPolicyAttive Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdPolicyAttive elencoIdPolicyAttive) throws SerializerException {
		return this.objToXml(ElencoIdPolicyAttive.class, elencoIdPolicyAttive, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoIdPolicyAttive</var> of type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param elencoIdPolicyAttive Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoIdPolicyAttive elencoIdPolicyAttive,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoIdPolicyAttive.class, elencoIdPolicyAttive, prettyPrint).toString();
	}
	
	
	

}
