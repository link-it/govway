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
package org.openspcoop2.protocol.abstraction.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.protocol.abstraction.Soggetto;
import org.openspcoop2.protocol.abstraction.RiferimentoSoggetto;
import org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour;
import org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic;
import org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore;
import org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione;
import org.openspcoop2.protocol.abstraction.IdentificatoreAccordo;
import org.openspcoop2.protocol.abstraction.IdentificatoreServizio;
import org.openspcoop2.protocol.abstraction.DatiServizio;
import org.openspcoop2.protocol.abstraction.Fruitori;
import org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore;
import org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione;
import org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.abstraction.DatiFruizione;
import org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune;
import org.openspcoop2.protocol.abstraction.Erogazione;
import org.openspcoop2.protocol.abstraction.Fruizione;

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
	 Object: Soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.abstraction.Soggetto}
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
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.abstraction.Soggetto}
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
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.abstraction.Soggetto}
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
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.abstraction.Soggetto}
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
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.abstraction.Soggetto}
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
	 Object: RiferimentoSoggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoSoggetto</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoSoggetto</var>
	 * @param riferimentoSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoSoggetto riferimentoSoggetto) throws SerializerException {
		this.objToXml(fileName, RiferimentoSoggetto.class, riferimentoSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoSoggetto</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoSoggetto</var>
	 * @param riferimentoSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoSoggetto riferimentoSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RiferimentoSoggetto.class, riferimentoSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoSoggetto</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoSoggetto</var>
	 * @param riferimentoSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoSoggetto riferimentoSoggetto) throws SerializerException {
		this.objToXml(file, RiferimentoSoggetto.class, riferimentoSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoSoggetto</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoSoggetto</var>
	 * @param riferimentoSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoSoggetto riferimentoSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RiferimentoSoggetto.class, riferimentoSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoSoggetto</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoSoggetto</var>
	 * @param riferimentoSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoSoggetto riferimentoSoggetto) throws SerializerException {
		this.objToXml(out, RiferimentoSoggetto.class, riferimentoSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoSoggetto</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoSoggetto</var>
	 * @param riferimentoSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoSoggetto riferimentoSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RiferimentoSoggetto.class, riferimentoSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>riferimentoSoggetto</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param riferimentoSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoSoggetto riferimentoSoggetto) throws SerializerException {
		return this.objToXml(RiferimentoSoggetto.class, riferimentoSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riferimentoSoggetto</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param riferimentoSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoSoggetto riferimentoSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoSoggetto.class, riferimentoSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>riferimentoSoggetto</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param riferimentoSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoSoggetto riferimentoSoggetto) throws SerializerException {
		return this.objToXml(RiferimentoSoggetto.class, riferimentoSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>riferimentoSoggetto</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param riferimentoSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoSoggetto riferimentoSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoSoggetto.class, riferimentoSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SoggettoNotExistsBehaviour
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggettoNotExistsBehaviour</var> of type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggettoNotExistsBehaviour</var>
	 * @param soggettoNotExistsBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoggettoNotExistsBehaviour soggettoNotExistsBehaviour) throws SerializerException {
		this.objToXml(fileName, SoggettoNotExistsBehaviour.class, soggettoNotExistsBehaviour, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggettoNotExistsBehaviour</var> of type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggettoNotExistsBehaviour</var>
	 * @param soggettoNotExistsBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoggettoNotExistsBehaviour soggettoNotExistsBehaviour,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoggettoNotExistsBehaviour.class, soggettoNotExistsBehaviour, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggettoNotExistsBehaviour</var> of type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param file Xml file to serialize the object <var>soggettoNotExistsBehaviour</var>
	 * @param soggettoNotExistsBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoggettoNotExistsBehaviour soggettoNotExistsBehaviour) throws SerializerException {
		this.objToXml(file, SoggettoNotExistsBehaviour.class, soggettoNotExistsBehaviour, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggettoNotExistsBehaviour</var> of type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param file Xml file to serialize the object <var>soggettoNotExistsBehaviour</var>
	 * @param soggettoNotExistsBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoggettoNotExistsBehaviour soggettoNotExistsBehaviour,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoggettoNotExistsBehaviour.class, soggettoNotExistsBehaviour, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggettoNotExistsBehaviour</var> of type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param out OutputStream to serialize the object <var>soggettoNotExistsBehaviour</var>
	 * @param soggettoNotExistsBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoggettoNotExistsBehaviour soggettoNotExistsBehaviour) throws SerializerException {
		this.objToXml(out, SoggettoNotExistsBehaviour.class, soggettoNotExistsBehaviour, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggettoNotExistsBehaviour</var> of type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param out OutputStream to serialize the object <var>soggettoNotExistsBehaviour</var>
	 * @param soggettoNotExistsBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoggettoNotExistsBehaviour soggettoNotExistsBehaviour,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoggettoNotExistsBehaviour.class, soggettoNotExistsBehaviour, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soggettoNotExistsBehaviour</var> of type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param soggettoNotExistsBehaviour Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoggettoNotExistsBehaviour soggettoNotExistsBehaviour) throws SerializerException {
		return this.objToXml(SoggettoNotExistsBehaviour.class, soggettoNotExistsBehaviour, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggettoNotExistsBehaviour</var> of type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param soggettoNotExistsBehaviour Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoggettoNotExistsBehaviour soggettoNotExistsBehaviour,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoggettoNotExistsBehaviour.class, soggettoNotExistsBehaviour, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soggettoNotExistsBehaviour</var> of type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param soggettoNotExistsBehaviour Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoggettoNotExistsBehaviour soggettoNotExistsBehaviour) throws SerializerException {
		return this.objToXml(SoggettoNotExistsBehaviour.class, soggettoNotExistsBehaviour, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggettoNotExistsBehaviour</var> of type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param soggettoNotExistsBehaviour Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoggettoNotExistsBehaviour soggettoNotExistsBehaviour,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoggettoNotExistsBehaviour.class, soggettoNotExistsBehaviour, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: CredenzialiInvocazioneBasic
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>credenzialiInvocazioneBasic</var> of type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param fileName Xml file to serialize the object <var>credenzialiInvocazioneBasic</var>
	 * @param credenzialiInvocazioneBasic Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CredenzialiInvocazioneBasic credenzialiInvocazioneBasic) throws SerializerException {
		this.objToXml(fileName, CredenzialiInvocazioneBasic.class, credenzialiInvocazioneBasic, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>credenzialiInvocazioneBasic</var> of type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param fileName Xml file to serialize the object <var>credenzialiInvocazioneBasic</var>
	 * @param credenzialiInvocazioneBasic Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CredenzialiInvocazioneBasic credenzialiInvocazioneBasic,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CredenzialiInvocazioneBasic.class, credenzialiInvocazioneBasic, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>credenzialiInvocazioneBasic</var> of type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param file Xml file to serialize the object <var>credenzialiInvocazioneBasic</var>
	 * @param credenzialiInvocazioneBasic Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CredenzialiInvocazioneBasic credenzialiInvocazioneBasic) throws SerializerException {
		this.objToXml(file, CredenzialiInvocazioneBasic.class, credenzialiInvocazioneBasic, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>credenzialiInvocazioneBasic</var> of type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param file Xml file to serialize the object <var>credenzialiInvocazioneBasic</var>
	 * @param credenzialiInvocazioneBasic Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CredenzialiInvocazioneBasic credenzialiInvocazioneBasic,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CredenzialiInvocazioneBasic.class, credenzialiInvocazioneBasic, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>credenzialiInvocazioneBasic</var> of type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param out OutputStream to serialize the object <var>credenzialiInvocazioneBasic</var>
	 * @param credenzialiInvocazioneBasic Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CredenzialiInvocazioneBasic credenzialiInvocazioneBasic) throws SerializerException {
		this.objToXml(out, CredenzialiInvocazioneBasic.class, credenzialiInvocazioneBasic, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>credenzialiInvocazioneBasic</var> of type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param out OutputStream to serialize the object <var>credenzialiInvocazioneBasic</var>
	 * @param credenzialiInvocazioneBasic Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CredenzialiInvocazioneBasic credenzialiInvocazioneBasic,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CredenzialiInvocazioneBasic.class, credenzialiInvocazioneBasic, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>credenzialiInvocazioneBasic</var> of type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param credenzialiInvocazioneBasic Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CredenzialiInvocazioneBasic credenzialiInvocazioneBasic) throws SerializerException {
		return this.objToXml(CredenzialiInvocazioneBasic.class, credenzialiInvocazioneBasic, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>credenzialiInvocazioneBasic</var> of type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param credenzialiInvocazioneBasic Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CredenzialiInvocazioneBasic credenzialiInvocazioneBasic,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CredenzialiInvocazioneBasic.class, credenzialiInvocazioneBasic, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>credenzialiInvocazioneBasic</var> of type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param credenzialiInvocazioneBasic Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CredenzialiInvocazioneBasic credenzialiInvocazioneBasic) throws SerializerException {
		return this.objToXml(CredenzialiInvocazioneBasic.class, credenzialiInvocazioneBasic, false).toString();
	}
	/**
	 * Serialize to String the object <var>credenzialiInvocazioneBasic</var> of type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param credenzialiInvocazioneBasic Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CredenzialiInvocazioneBasic credenzialiInvocazioneBasic,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CredenzialiInvocazioneBasic.class, credenzialiInvocazioneBasic, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoServizioApplicativoFruitore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoServizioApplicativoFruitore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoServizioApplicativoFruitore</var>
	 * @param riferimentoServizioApplicativoFruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoServizioApplicativoFruitore riferimentoServizioApplicativoFruitore) throws SerializerException {
		this.objToXml(fileName, RiferimentoServizioApplicativoFruitore.class, riferimentoServizioApplicativoFruitore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoServizioApplicativoFruitore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoServizioApplicativoFruitore</var>
	 * @param riferimentoServizioApplicativoFruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoServizioApplicativoFruitore riferimentoServizioApplicativoFruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RiferimentoServizioApplicativoFruitore.class, riferimentoServizioApplicativoFruitore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoServizioApplicativoFruitore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoServizioApplicativoFruitore</var>
	 * @param riferimentoServizioApplicativoFruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoServizioApplicativoFruitore riferimentoServizioApplicativoFruitore) throws SerializerException {
		this.objToXml(file, RiferimentoServizioApplicativoFruitore.class, riferimentoServizioApplicativoFruitore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoServizioApplicativoFruitore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoServizioApplicativoFruitore</var>
	 * @param riferimentoServizioApplicativoFruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoServizioApplicativoFruitore riferimentoServizioApplicativoFruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RiferimentoServizioApplicativoFruitore.class, riferimentoServizioApplicativoFruitore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoServizioApplicativoFruitore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoServizioApplicativoFruitore</var>
	 * @param riferimentoServizioApplicativoFruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoServizioApplicativoFruitore riferimentoServizioApplicativoFruitore) throws SerializerException {
		this.objToXml(out, RiferimentoServizioApplicativoFruitore.class, riferimentoServizioApplicativoFruitore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoServizioApplicativoFruitore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoServizioApplicativoFruitore</var>
	 * @param riferimentoServizioApplicativoFruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoServizioApplicativoFruitore riferimentoServizioApplicativoFruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RiferimentoServizioApplicativoFruitore.class, riferimentoServizioApplicativoFruitore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>riferimentoServizioApplicativoFruitore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param riferimentoServizioApplicativoFruitore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoServizioApplicativoFruitore riferimentoServizioApplicativoFruitore) throws SerializerException {
		return this.objToXml(RiferimentoServizioApplicativoFruitore.class, riferimentoServizioApplicativoFruitore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riferimentoServizioApplicativoFruitore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param riferimentoServizioApplicativoFruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoServizioApplicativoFruitore riferimentoServizioApplicativoFruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoServizioApplicativoFruitore.class, riferimentoServizioApplicativoFruitore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>riferimentoServizioApplicativoFruitore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param riferimentoServizioApplicativoFruitore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoServizioApplicativoFruitore riferimentoServizioApplicativoFruitore) throws SerializerException {
		return this.objToXml(RiferimentoServizioApplicativoFruitore.class, riferimentoServizioApplicativoFruitore, false).toString();
	}
	/**
	 * Serialize to String the object <var>riferimentoServizioApplicativoFruitore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param riferimentoServizioApplicativoFruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoServizioApplicativoFruitore riferimentoServizioApplicativoFruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoServizioApplicativoFruitore.class, riferimentoServizioApplicativoFruitore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiApplicativiFruizione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiApplicativiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiApplicativiFruizione</var>
	 * @param datiApplicativiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiApplicativiFruizione datiApplicativiFruizione) throws SerializerException {
		this.objToXml(fileName, DatiApplicativiFruizione.class, datiApplicativiFruizione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiApplicativiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiApplicativiFruizione</var>
	 * @param datiApplicativiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiApplicativiFruizione datiApplicativiFruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiApplicativiFruizione.class, datiApplicativiFruizione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiApplicativiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param file Xml file to serialize the object <var>datiApplicativiFruizione</var>
	 * @param datiApplicativiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiApplicativiFruizione datiApplicativiFruizione) throws SerializerException {
		this.objToXml(file, DatiApplicativiFruizione.class, datiApplicativiFruizione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiApplicativiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param file Xml file to serialize the object <var>datiApplicativiFruizione</var>
	 * @param datiApplicativiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiApplicativiFruizione datiApplicativiFruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiApplicativiFruizione.class, datiApplicativiFruizione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiApplicativiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param out OutputStream to serialize the object <var>datiApplicativiFruizione</var>
	 * @param datiApplicativiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiApplicativiFruizione datiApplicativiFruizione) throws SerializerException {
		this.objToXml(out, DatiApplicativiFruizione.class, datiApplicativiFruizione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiApplicativiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param out OutputStream to serialize the object <var>datiApplicativiFruizione</var>
	 * @param datiApplicativiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiApplicativiFruizione datiApplicativiFruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiApplicativiFruizione.class, datiApplicativiFruizione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiApplicativiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param datiApplicativiFruizione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiApplicativiFruizione datiApplicativiFruizione) throws SerializerException {
		return this.objToXml(DatiApplicativiFruizione.class, datiApplicativiFruizione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiApplicativiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param datiApplicativiFruizione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiApplicativiFruizione datiApplicativiFruizione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiApplicativiFruizione.class, datiApplicativiFruizione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiApplicativiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param datiApplicativiFruizione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiApplicativiFruizione datiApplicativiFruizione) throws SerializerException {
		return this.objToXml(DatiApplicativiFruizione.class, datiApplicativiFruizione, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiApplicativiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param datiApplicativiFruizione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiApplicativiFruizione datiApplicativiFruizione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiApplicativiFruizione.class, datiApplicativiFruizione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IdentificatoreAccordo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>identificatoreAccordo</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param fileName Xml file to serialize the object <var>identificatoreAccordo</var>
	 * @param identificatoreAccordo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdentificatoreAccordo identificatoreAccordo) throws SerializerException {
		this.objToXml(fileName, IdentificatoreAccordo.class, identificatoreAccordo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>identificatoreAccordo</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param fileName Xml file to serialize the object <var>identificatoreAccordo</var>
	 * @param identificatoreAccordo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdentificatoreAccordo identificatoreAccordo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdentificatoreAccordo.class, identificatoreAccordo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>identificatoreAccordo</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param file Xml file to serialize the object <var>identificatoreAccordo</var>
	 * @param identificatoreAccordo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdentificatoreAccordo identificatoreAccordo) throws SerializerException {
		this.objToXml(file, IdentificatoreAccordo.class, identificatoreAccordo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>identificatoreAccordo</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param file Xml file to serialize the object <var>identificatoreAccordo</var>
	 * @param identificatoreAccordo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdentificatoreAccordo identificatoreAccordo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdentificatoreAccordo.class, identificatoreAccordo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>identificatoreAccordo</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param out OutputStream to serialize the object <var>identificatoreAccordo</var>
	 * @param identificatoreAccordo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdentificatoreAccordo identificatoreAccordo) throws SerializerException {
		this.objToXml(out, IdentificatoreAccordo.class, identificatoreAccordo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>identificatoreAccordo</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param out OutputStream to serialize the object <var>identificatoreAccordo</var>
	 * @param identificatoreAccordo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdentificatoreAccordo identificatoreAccordo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdentificatoreAccordo.class, identificatoreAccordo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>identificatoreAccordo</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param identificatoreAccordo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdentificatoreAccordo identificatoreAccordo) throws SerializerException {
		return this.objToXml(IdentificatoreAccordo.class, identificatoreAccordo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>identificatoreAccordo</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param identificatoreAccordo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdentificatoreAccordo identificatoreAccordo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdentificatoreAccordo.class, identificatoreAccordo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>identificatoreAccordo</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param identificatoreAccordo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdentificatoreAccordo identificatoreAccordo) throws SerializerException {
		return this.objToXml(IdentificatoreAccordo.class, identificatoreAccordo, false).toString();
	}
	/**
	 * Serialize to String the object <var>identificatoreAccordo</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param identificatoreAccordo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdentificatoreAccordo identificatoreAccordo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdentificatoreAccordo.class, identificatoreAccordo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IdentificatoreServizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>identificatoreServizio</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>identificatoreServizio</var>
	 * @param identificatoreServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdentificatoreServizio identificatoreServizio) throws SerializerException {
		this.objToXml(fileName, IdentificatoreServizio.class, identificatoreServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>identificatoreServizio</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>identificatoreServizio</var>
	 * @param identificatoreServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdentificatoreServizio identificatoreServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdentificatoreServizio.class, identificatoreServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>identificatoreServizio</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param file Xml file to serialize the object <var>identificatoreServizio</var>
	 * @param identificatoreServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdentificatoreServizio identificatoreServizio) throws SerializerException {
		this.objToXml(file, IdentificatoreServizio.class, identificatoreServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>identificatoreServizio</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param file Xml file to serialize the object <var>identificatoreServizio</var>
	 * @param identificatoreServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdentificatoreServizio identificatoreServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdentificatoreServizio.class, identificatoreServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>identificatoreServizio</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>identificatoreServizio</var>
	 * @param identificatoreServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdentificatoreServizio identificatoreServizio) throws SerializerException {
		this.objToXml(out, IdentificatoreServizio.class, identificatoreServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>identificatoreServizio</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>identificatoreServizio</var>
	 * @param identificatoreServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdentificatoreServizio identificatoreServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdentificatoreServizio.class, identificatoreServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>identificatoreServizio</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param identificatoreServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdentificatoreServizio identificatoreServizio) throws SerializerException {
		return this.objToXml(IdentificatoreServizio.class, identificatoreServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>identificatoreServizio</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param identificatoreServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdentificatoreServizio identificatoreServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdentificatoreServizio.class, identificatoreServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>identificatoreServizio</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param identificatoreServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdentificatoreServizio identificatoreServizio) throws SerializerException {
		return this.objToXml(IdentificatoreServizio.class, identificatoreServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>identificatoreServizio</var> of type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param identificatoreServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdentificatoreServizio identificatoreServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdentificatoreServizio.class, identificatoreServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiServizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiServizio</var> of type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiServizio</var>
	 * @param datiServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiServizio datiServizio) throws SerializerException {
		this.objToXml(fileName, DatiServizio.class, datiServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiServizio</var> of type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiServizio</var>
	 * @param datiServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiServizio datiServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiServizio.class, datiServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiServizio</var> of type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param file Xml file to serialize the object <var>datiServizio</var>
	 * @param datiServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiServizio datiServizio) throws SerializerException {
		this.objToXml(file, DatiServizio.class, datiServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiServizio</var> of type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param file Xml file to serialize the object <var>datiServizio</var>
	 * @param datiServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiServizio datiServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiServizio.class, datiServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiServizio</var> of type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>datiServizio</var>
	 * @param datiServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiServizio datiServizio) throws SerializerException {
		this.objToXml(out, DatiServizio.class, datiServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiServizio</var> of type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>datiServizio</var>
	 * @param datiServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiServizio datiServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiServizio.class, datiServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiServizio</var> of type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param datiServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiServizio datiServizio) throws SerializerException {
		return this.objToXml(DatiServizio.class, datiServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiServizio</var> of type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param datiServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiServizio datiServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiServizio.class, datiServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiServizio</var> of type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param datiServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiServizio datiServizio) throws SerializerException {
		return this.objToXml(DatiServizio.class, datiServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiServizio</var> of type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param datiServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiServizio datiServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiServizio.class, datiServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Fruitori
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fruitori</var> of type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param fileName Xml file to serialize the object <var>fruitori</var>
	 * @param fruitori Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Fruitori fruitori) throws SerializerException {
		this.objToXml(fileName, Fruitori.class, fruitori, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fruitori</var> of type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param fileName Xml file to serialize the object <var>fruitori</var>
	 * @param fruitori Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Fruitori fruitori,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Fruitori.class, fruitori, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fruitori</var> of type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param file Xml file to serialize the object <var>fruitori</var>
	 * @param fruitori Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Fruitori fruitori) throws SerializerException {
		this.objToXml(file, Fruitori.class, fruitori, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fruitori</var> of type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param file Xml file to serialize the object <var>fruitori</var>
	 * @param fruitori Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Fruitori fruitori,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Fruitori.class, fruitori, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fruitori</var> of type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param out OutputStream to serialize the object <var>fruitori</var>
	 * @param fruitori Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Fruitori fruitori) throws SerializerException {
		this.objToXml(out, Fruitori.class, fruitori, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fruitori</var> of type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param out OutputStream to serialize the object <var>fruitori</var>
	 * @param fruitori Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Fruitori fruitori,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Fruitori.class, fruitori, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fruitori</var> of type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param fruitori Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Fruitori fruitori) throws SerializerException {
		return this.objToXml(Fruitori.class, fruitori, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fruitori</var> of type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param fruitori Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Fruitori fruitori,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Fruitori.class, fruitori, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fruitori</var> of type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param fruitori Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Fruitori fruitori) throws SerializerException {
		return this.objToXml(Fruitori.class, fruitori, false).toString();
	}
	/**
	 * Serialize to String the object <var>fruitori</var> of type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param fruitori Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Fruitori fruitori,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Fruitori.class, fruitori, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoServizioApplicativoErogatore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoServizioApplicativoErogatore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoServizioApplicativoErogatore</var>
	 * @param riferimentoServizioApplicativoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoServizioApplicativoErogatore riferimentoServizioApplicativoErogatore) throws SerializerException {
		this.objToXml(fileName, RiferimentoServizioApplicativoErogatore.class, riferimentoServizioApplicativoErogatore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoServizioApplicativoErogatore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoServizioApplicativoErogatore</var>
	 * @param riferimentoServizioApplicativoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoServizioApplicativoErogatore riferimentoServizioApplicativoErogatore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RiferimentoServizioApplicativoErogatore.class, riferimentoServizioApplicativoErogatore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoServizioApplicativoErogatore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoServizioApplicativoErogatore</var>
	 * @param riferimentoServizioApplicativoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoServizioApplicativoErogatore riferimentoServizioApplicativoErogatore) throws SerializerException {
		this.objToXml(file, RiferimentoServizioApplicativoErogatore.class, riferimentoServizioApplicativoErogatore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoServizioApplicativoErogatore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoServizioApplicativoErogatore</var>
	 * @param riferimentoServizioApplicativoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoServizioApplicativoErogatore riferimentoServizioApplicativoErogatore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RiferimentoServizioApplicativoErogatore.class, riferimentoServizioApplicativoErogatore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoServizioApplicativoErogatore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoServizioApplicativoErogatore</var>
	 * @param riferimentoServizioApplicativoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoServizioApplicativoErogatore riferimentoServizioApplicativoErogatore) throws SerializerException {
		this.objToXml(out, RiferimentoServizioApplicativoErogatore.class, riferimentoServizioApplicativoErogatore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoServizioApplicativoErogatore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoServizioApplicativoErogatore</var>
	 * @param riferimentoServizioApplicativoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoServizioApplicativoErogatore riferimentoServizioApplicativoErogatore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RiferimentoServizioApplicativoErogatore.class, riferimentoServizioApplicativoErogatore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>riferimentoServizioApplicativoErogatore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param riferimentoServizioApplicativoErogatore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoServizioApplicativoErogatore riferimentoServizioApplicativoErogatore) throws SerializerException {
		return this.objToXml(RiferimentoServizioApplicativoErogatore.class, riferimentoServizioApplicativoErogatore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riferimentoServizioApplicativoErogatore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param riferimentoServizioApplicativoErogatore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoServizioApplicativoErogatore riferimentoServizioApplicativoErogatore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoServizioApplicativoErogatore.class, riferimentoServizioApplicativoErogatore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>riferimentoServizioApplicativoErogatore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param riferimentoServizioApplicativoErogatore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoServizioApplicativoErogatore riferimentoServizioApplicativoErogatore) throws SerializerException {
		return this.objToXml(RiferimentoServizioApplicativoErogatore.class, riferimentoServizioApplicativoErogatore, false).toString();
	}
	/**
	 * Serialize to String the object <var>riferimentoServizioApplicativoErogatore</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param riferimentoServizioApplicativoErogatore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoServizioApplicativoErogatore riferimentoServizioApplicativoErogatore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoServizioApplicativoErogatore.class, riferimentoServizioApplicativoErogatore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiApplicativiErogazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiApplicativiErogazione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiApplicativiErogazione</var>
	 * @param datiApplicativiErogazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiApplicativiErogazione datiApplicativiErogazione) throws SerializerException {
		this.objToXml(fileName, DatiApplicativiErogazione.class, datiApplicativiErogazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiApplicativiErogazione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiApplicativiErogazione</var>
	 * @param datiApplicativiErogazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiApplicativiErogazione datiApplicativiErogazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiApplicativiErogazione.class, datiApplicativiErogazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiApplicativiErogazione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param file Xml file to serialize the object <var>datiApplicativiErogazione</var>
	 * @param datiApplicativiErogazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiApplicativiErogazione datiApplicativiErogazione) throws SerializerException {
		this.objToXml(file, DatiApplicativiErogazione.class, datiApplicativiErogazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiApplicativiErogazione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param file Xml file to serialize the object <var>datiApplicativiErogazione</var>
	 * @param datiApplicativiErogazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiApplicativiErogazione datiApplicativiErogazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiApplicativiErogazione.class, datiApplicativiErogazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiApplicativiErogazione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param out OutputStream to serialize the object <var>datiApplicativiErogazione</var>
	 * @param datiApplicativiErogazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiApplicativiErogazione datiApplicativiErogazione) throws SerializerException {
		this.objToXml(out, DatiApplicativiErogazione.class, datiApplicativiErogazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiApplicativiErogazione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param out OutputStream to serialize the object <var>datiApplicativiErogazione</var>
	 * @param datiApplicativiErogazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiApplicativiErogazione datiApplicativiErogazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiApplicativiErogazione.class, datiApplicativiErogazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiApplicativiErogazione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param datiApplicativiErogazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiApplicativiErogazione datiApplicativiErogazione) throws SerializerException {
		return this.objToXml(DatiApplicativiErogazione.class, datiApplicativiErogazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiApplicativiErogazione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param datiApplicativiErogazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiApplicativiErogazione datiApplicativiErogazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiApplicativiErogazione.class, datiApplicativiErogazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiApplicativiErogazione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param datiApplicativiErogazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiApplicativiErogazione datiApplicativiErogazione) throws SerializerException {
		return this.objToXml(DatiApplicativiErogazione.class, datiApplicativiErogazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiApplicativiErogazione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param datiApplicativiErogazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiApplicativiErogazione datiApplicativiErogazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiApplicativiErogazione.class, datiApplicativiErogazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoAccordoServizioParteSpecifica
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoAccordoServizioParteSpecifica</var>
	 * @param riferimentoAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoAccordoServizioParteSpecifica riferimentoAccordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(fileName, RiferimentoAccordoServizioParteSpecifica.class, riferimentoAccordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoAccordoServizioParteSpecifica</var>
	 * @param riferimentoAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoAccordoServizioParteSpecifica riferimentoAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RiferimentoAccordoServizioParteSpecifica.class, riferimentoAccordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoAccordoServizioParteSpecifica</var>
	 * @param riferimentoAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoAccordoServizioParteSpecifica riferimentoAccordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(file, RiferimentoAccordoServizioParteSpecifica.class, riferimentoAccordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoAccordoServizioParteSpecifica</var>
	 * @param riferimentoAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoAccordoServizioParteSpecifica riferimentoAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RiferimentoAccordoServizioParteSpecifica.class, riferimentoAccordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoAccordoServizioParteSpecifica</var>
	 * @param riferimentoAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoAccordoServizioParteSpecifica riferimentoAccordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(out, RiferimentoAccordoServizioParteSpecifica.class, riferimentoAccordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoAccordoServizioParteSpecifica</var>
	 * @param riferimentoAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoAccordoServizioParteSpecifica riferimentoAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RiferimentoAccordoServizioParteSpecifica.class, riferimentoAccordoServizioParteSpecifica, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>riferimentoAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param riferimentoAccordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoAccordoServizioParteSpecifica riferimentoAccordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(RiferimentoAccordoServizioParteSpecifica.class, riferimentoAccordoServizioParteSpecifica, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riferimentoAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param riferimentoAccordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoAccordoServizioParteSpecifica riferimentoAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoAccordoServizioParteSpecifica.class, riferimentoAccordoServizioParteSpecifica, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>riferimentoAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param riferimentoAccordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoAccordoServizioParteSpecifica riferimentoAccordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(RiferimentoAccordoServizioParteSpecifica.class, riferimentoAccordoServizioParteSpecifica, false).toString();
	}
	/**
	 * Serialize to String the object <var>riferimentoAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param riferimentoAccordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoAccordoServizioParteSpecifica riferimentoAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoAccordoServizioParteSpecifica.class, riferimentoAccordoServizioParteSpecifica, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiFruizione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiFruizione</var>
	 * @param datiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiFruizione datiFruizione) throws SerializerException {
		this.objToXml(fileName, DatiFruizione.class, datiFruizione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiFruizione</var>
	 * @param datiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiFruizione datiFruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiFruizione.class, datiFruizione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param file Xml file to serialize the object <var>datiFruizione</var>
	 * @param datiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiFruizione datiFruizione) throws SerializerException {
		this.objToXml(file, DatiFruizione.class, datiFruizione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param file Xml file to serialize the object <var>datiFruizione</var>
	 * @param datiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiFruizione datiFruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiFruizione.class, datiFruizione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param out OutputStream to serialize the object <var>datiFruizione</var>
	 * @param datiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiFruizione datiFruizione) throws SerializerException {
		this.objToXml(out, DatiFruizione.class, datiFruizione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param out OutputStream to serialize the object <var>datiFruizione</var>
	 * @param datiFruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiFruizione datiFruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiFruizione.class, datiFruizione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param datiFruizione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiFruizione datiFruizione) throws SerializerException {
		return this.objToXml(DatiFruizione.class, datiFruizione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param datiFruizione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiFruizione datiFruizione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiFruizione.class, datiFruizione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param datiFruizione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiFruizione datiFruizione) throws SerializerException {
		return this.objToXml(DatiFruizione.class, datiFruizione, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiFruizione</var> of type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param datiFruizione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiFruizione datiFruizione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiFruizione.class, datiFruizione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoAccordoServizioParteComune
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoAccordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoAccordoServizioParteComune</var>
	 * @param riferimentoAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoAccordoServizioParteComune riferimentoAccordoServizioParteComune) throws SerializerException {
		this.objToXml(fileName, RiferimentoAccordoServizioParteComune.class, riferimentoAccordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>riferimentoAccordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>riferimentoAccordoServizioParteComune</var>
	 * @param riferimentoAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RiferimentoAccordoServizioParteComune riferimentoAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RiferimentoAccordoServizioParteComune.class, riferimentoAccordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoAccordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoAccordoServizioParteComune</var>
	 * @param riferimentoAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoAccordoServizioParteComune riferimentoAccordoServizioParteComune) throws SerializerException {
		this.objToXml(file, RiferimentoAccordoServizioParteComune.class, riferimentoAccordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>riferimentoAccordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>riferimentoAccordoServizioParteComune</var>
	 * @param riferimentoAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RiferimentoAccordoServizioParteComune riferimentoAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RiferimentoAccordoServizioParteComune.class, riferimentoAccordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoAccordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoAccordoServizioParteComune</var>
	 * @param riferimentoAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoAccordoServizioParteComune riferimentoAccordoServizioParteComune) throws SerializerException {
		this.objToXml(out, RiferimentoAccordoServizioParteComune.class, riferimentoAccordoServizioParteComune, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>riferimentoAccordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>riferimentoAccordoServizioParteComune</var>
	 * @param riferimentoAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RiferimentoAccordoServizioParteComune riferimentoAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RiferimentoAccordoServizioParteComune.class, riferimentoAccordoServizioParteComune, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>riferimentoAccordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param riferimentoAccordoServizioParteComune Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoAccordoServizioParteComune riferimentoAccordoServizioParteComune) throws SerializerException {
		return this.objToXml(RiferimentoAccordoServizioParteComune.class, riferimentoAccordoServizioParteComune, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>riferimentoAccordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param riferimentoAccordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RiferimentoAccordoServizioParteComune riferimentoAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoAccordoServizioParteComune.class, riferimentoAccordoServizioParteComune, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>riferimentoAccordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param riferimentoAccordoServizioParteComune Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoAccordoServizioParteComune riferimentoAccordoServizioParteComune) throws SerializerException {
		return this.objToXml(RiferimentoAccordoServizioParteComune.class, riferimentoAccordoServizioParteComune, false).toString();
	}
	/**
	 * Serialize to String the object <var>riferimentoAccordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param riferimentoAccordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RiferimentoAccordoServizioParteComune riferimentoAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RiferimentoAccordoServizioParteComune.class, riferimentoAccordoServizioParteComune, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: erogazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>erogazione</var> of type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>erogazione</var>
	 * @param erogazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Erogazione erogazione) throws SerializerException {
		this.objToXml(fileName, Erogazione.class, erogazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>erogazione</var> of type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>erogazione</var>
	 * @param erogazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Erogazione erogazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Erogazione.class, erogazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>erogazione</var> of type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param file Xml file to serialize the object <var>erogazione</var>
	 * @param erogazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Erogazione erogazione) throws SerializerException {
		this.objToXml(file, Erogazione.class, erogazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>erogazione</var> of type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param file Xml file to serialize the object <var>erogazione</var>
	 * @param erogazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Erogazione erogazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Erogazione.class, erogazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>erogazione</var> of type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param out OutputStream to serialize the object <var>erogazione</var>
	 * @param erogazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Erogazione erogazione) throws SerializerException {
		this.objToXml(out, Erogazione.class, erogazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>erogazione</var> of type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param out OutputStream to serialize the object <var>erogazione</var>
	 * @param erogazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Erogazione erogazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Erogazione.class, erogazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>erogazione</var> of type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param erogazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Erogazione erogazione) throws SerializerException {
		return this.objToXml(Erogazione.class, erogazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>erogazione</var> of type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param erogazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Erogazione erogazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Erogazione.class, erogazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>erogazione</var> of type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param erogazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Erogazione erogazione) throws SerializerException {
		return this.objToXml(Erogazione.class, erogazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>erogazione</var> of type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param erogazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Erogazione erogazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Erogazione.class, erogazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: fruizione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fruizione</var> of type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param fileName Xml file to serialize the object <var>fruizione</var>
	 * @param fruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Fruizione fruizione) throws SerializerException {
		this.objToXml(fileName, Fruizione.class, fruizione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fruizione</var> of type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param fileName Xml file to serialize the object <var>fruizione</var>
	 * @param fruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Fruizione fruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Fruizione.class, fruizione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fruizione</var> of type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param file Xml file to serialize the object <var>fruizione</var>
	 * @param fruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Fruizione fruizione) throws SerializerException {
		this.objToXml(file, Fruizione.class, fruizione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fruizione</var> of type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param file Xml file to serialize the object <var>fruizione</var>
	 * @param fruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Fruizione fruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Fruizione.class, fruizione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fruizione</var> of type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param out OutputStream to serialize the object <var>fruizione</var>
	 * @param fruizione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Fruizione fruizione) throws SerializerException {
		this.objToXml(out, Fruizione.class, fruizione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fruizione</var> of type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param out OutputStream to serialize the object <var>fruizione</var>
	 * @param fruizione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Fruizione fruizione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Fruizione.class, fruizione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fruizione</var> of type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param fruizione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Fruizione fruizione) throws SerializerException {
		return this.objToXml(Fruizione.class, fruizione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fruizione</var> of type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param fruizione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Fruizione fruizione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Fruizione.class, fruizione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fruizione</var> of type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param fruizione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Fruizione fruizione) throws SerializerException {
		return this.objToXml(Fruizione.class, fruizione, false).toString();
	}
	/**
	 * Serialize to String the object <var>fruizione</var> of type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param fruizione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Fruizione fruizione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Fruizione.class, fruizione, prettyPrint).toString();
	}
	
	
	

}
