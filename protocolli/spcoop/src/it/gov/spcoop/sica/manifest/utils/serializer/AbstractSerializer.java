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
package it.gov.spcoop.sica.manifest.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import it.gov.spcoop.sica.manifest.DocumentoSicurezza;
import it.gov.spcoop.sica.manifest.ElencoServiziComponenti;
import it.gov.spcoop.sica.manifest.DocumentoSemiformale;
import it.gov.spcoop.sica.manifest.SpecificaSemiformale;
import it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica;
import it.gov.spcoop.sica.manifest.SpecificaPortiAccesso;
import it.gov.spcoop.sica.manifest.SpecificaSicurezza;
import it.gov.spcoop.sica.manifest.SpecificaLivelliServizio;
import it.gov.spcoop.sica.manifest.SpecificaInterfaccia;
import it.gov.spcoop.sica.manifest.AccordoServizioParteComune;
import it.gov.spcoop.sica.manifest.SpecificaConversazione;
import it.gov.spcoop.sica.manifest.DocumentoConversazione;
import it.gov.spcoop.sica.manifest.DocumentoInterfaccia;
import it.gov.spcoop.sica.manifest.ElencoPartecipanti;
import it.gov.spcoop.sica.manifest.ServizioComposto;
import it.gov.spcoop.sica.manifest.ElencoAllegati;
import it.gov.spcoop.sica.manifest.SpecificaCoordinamento;
import it.gov.spcoop.sica.manifest.DocumentoLivelloServizio;
import it.gov.spcoop.sica.manifest.AccordoCooperazione;
import it.gov.spcoop.sica.manifest.ElencoServiziComposti;
import it.gov.spcoop.sica.manifest.DocumentoCoordinamento;
import it.gov.spcoop.sica.manifest.AccordoServizio;

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
	 Object: DocumentoSicurezza
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoSicurezza</var>
	 * @param documentoSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoSicurezza documentoSicurezza) throws SerializerException {
		this.objToXml(fileName, DocumentoSicurezza.class, documentoSicurezza, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoSicurezza</var>
	 * @param documentoSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoSicurezza documentoSicurezza,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DocumentoSicurezza.class, documentoSicurezza, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param file Xml file to serialize the object <var>documentoSicurezza</var>
	 * @param documentoSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoSicurezza documentoSicurezza) throws SerializerException {
		this.objToXml(file, DocumentoSicurezza.class, documentoSicurezza, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param file Xml file to serialize the object <var>documentoSicurezza</var>
	 * @param documentoSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoSicurezza documentoSicurezza,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DocumentoSicurezza.class, documentoSicurezza, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoSicurezza</var>
	 * @param documentoSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoSicurezza documentoSicurezza) throws SerializerException {
		this.objToXml(out, DocumentoSicurezza.class, documentoSicurezza, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoSicurezza</var>
	 * @param documentoSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoSicurezza documentoSicurezza,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DocumentoSicurezza.class, documentoSicurezza, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>documentoSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param documentoSicurezza Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoSicurezza documentoSicurezza) throws SerializerException {
		return this.objToXml(DocumentoSicurezza.class, documentoSicurezza, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>documentoSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param documentoSicurezza Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoSicurezza documentoSicurezza,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoSicurezza.class, documentoSicurezza, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>documentoSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param documentoSicurezza Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoSicurezza documentoSicurezza) throws SerializerException {
		return this.objToXml(DocumentoSicurezza.class, documentoSicurezza, false).toString();
	}
	/**
	 * Serialize to String the object <var>documentoSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param documentoSicurezza Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoSicurezza documentoSicurezza,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoSicurezza.class, documentoSicurezza, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ElencoServiziComponenti
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoServiziComponenti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoServiziComponenti</var>
	 * @param elencoServiziComponenti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoServiziComponenti elencoServiziComponenti) throws SerializerException {
		this.objToXml(fileName, ElencoServiziComponenti.class, elencoServiziComponenti, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoServiziComponenti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoServiziComponenti</var>
	 * @param elencoServiziComponenti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoServiziComponenti elencoServiziComponenti,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoServiziComponenti.class, elencoServiziComponenti, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoServiziComponenti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param file Xml file to serialize the object <var>elencoServiziComponenti</var>
	 * @param elencoServiziComponenti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoServiziComponenti elencoServiziComponenti) throws SerializerException {
		this.objToXml(file, ElencoServiziComponenti.class, elencoServiziComponenti, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoServiziComponenti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param file Xml file to serialize the object <var>elencoServiziComponenti</var>
	 * @param elencoServiziComponenti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoServiziComponenti elencoServiziComponenti,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoServiziComponenti.class, elencoServiziComponenti, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoServiziComponenti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoServiziComponenti</var>
	 * @param elencoServiziComponenti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoServiziComponenti elencoServiziComponenti) throws SerializerException {
		this.objToXml(out, ElencoServiziComponenti.class, elencoServiziComponenti, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoServiziComponenti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoServiziComponenti</var>
	 * @param elencoServiziComponenti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoServiziComponenti elencoServiziComponenti,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoServiziComponenti.class, elencoServiziComponenti, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoServiziComponenti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param elencoServiziComponenti Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoServiziComponenti elencoServiziComponenti) throws SerializerException {
		return this.objToXml(ElencoServiziComponenti.class, elencoServiziComponenti, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoServiziComponenti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param elencoServiziComponenti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoServiziComponenti elencoServiziComponenti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoServiziComponenti.class, elencoServiziComponenti, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoServiziComponenti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param elencoServiziComponenti Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoServiziComponenti elencoServiziComponenti) throws SerializerException {
		return this.objToXml(ElencoServiziComponenti.class, elencoServiziComponenti, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoServiziComponenti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param elencoServiziComponenti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoServiziComponenti elencoServiziComponenti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoServiziComponenti.class, elencoServiziComponenti, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DocumentoSemiformale
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoSemiformale</var>
	 * @param documentoSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoSemiformale documentoSemiformale) throws SerializerException {
		this.objToXml(fileName, DocumentoSemiformale.class, documentoSemiformale, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoSemiformale</var>
	 * @param documentoSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoSemiformale documentoSemiformale,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DocumentoSemiformale.class, documentoSemiformale, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param file Xml file to serialize the object <var>documentoSemiformale</var>
	 * @param documentoSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoSemiformale documentoSemiformale) throws SerializerException {
		this.objToXml(file, DocumentoSemiformale.class, documentoSemiformale, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param file Xml file to serialize the object <var>documentoSemiformale</var>
	 * @param documentoSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoSemiformale documentoSemiformale,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DocumentoSemiformale.class, documentoSemiformale, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoSemiformale</var>
	 * @param documentoSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoSemiformale documentoSemiformale) throws SerializerException {
		this.objToXml(out, DocumentoSemiformale.class, documentoSemiformale, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoSemiformale</var>
	 * @param documentoSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoSemiformale documentoSemiformale,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DocumentoSemiformale.class, documentoSemiformale, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>documentoSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param documentoSemiformale Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoSemiformale documentoSemiformale) throws SerializerException {
		return this.objToXml(DocumentoSemiformale.class, documentoSemiformale, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>documentoSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param documentoSemiformale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoSemiformale documentoSemiformale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoSemiformale.class, documentoSemiformale, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>documentoSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param documentoSemiformale Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoSemiformale documentoSemiformale) throws SerializerException {
		return this.objToXml(DocumentoSemiformale.class, documentoSemiformale, false).toString();
	}
	/**
	 * Serialize to String the object <var>documentoSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param documentoSemiformale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoSemiformale documentoSemiformale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoSemiformale.class, documentoSemiformale, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaSemiformale
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaSemiformale</var>
	 * @param specificaSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaSemiformale specificaSemiformale) throws SerializerException {
		this.objToXml(fileName, SpecificaSemiformale.class, specificaSemiformale, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaSemiformale</var>
	 * @param specificaSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaSemiformale specificaSemiformale,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SpecificaSemiformale.class, specificaSemiformale, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param file Xml file to serialize the object <var>specificaSemiformale</var>
	 * @param specificaSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaSemiformale specificaSemiformale) throws SerializerException {
		this.objToXml(file, SpecificaSemiformale.class, specificaSemiformale, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param file Xml file to serialize the object <var>specificaSemiformale</var>
	 * @param specificaSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaSemiformale specificaSemiformale,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SpecificaSemiformale.class, specificaSemiformale, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaSemiformale</var>
	 * @param specificaSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaSemiformale specificaSemiformale) throws SerializerException {
		this.objToXml(out, SpecificaSemiformale.class, specificaSemiformale, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaSemiformale</var>
	 * @param specificaSemiformale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaSemiformale specificaSemiformale,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SpecificaSemiformale.class, specificaSemiformale, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>specificaSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param specificaSemiformale Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaSemiformale specificaSemiformale) throws SerializerException {
		return this.objToXml(SpecificaSemiformale.class, specificaSemiformale, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>specificaSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param specificaSemiformale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaSemiformale specificaSemiformale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaSemiformale.class, specificaSemiformale, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>specificaSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param specificaSemiformale Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaSemiformale specificaSemiformale) throws SerializerException {
		return this.objToXml(SpecificaSemiformale.class, specificaSemiformale, false).toString();
	}
	/**
	 * Serialize to String the object <var>specificaSemiformale</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param specificaSemiformale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaSemiformale specificaSemiformale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaSemiformale.class, specificaSemiformale, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordoServizioParteSpecifica
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteSpecifica</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteSpecifica</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteSpecifica</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(file, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteSpecifica</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteSpecifica</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(out, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteSpecifica</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteSpecifica</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteSpecifica</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteSpecifica</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteSpecifica</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaPortiAccesso
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaPortiAccesso</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaPortiAccesso</var>
	 * @param specificaPortiAccesso Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaPortiAccesso specificaPortiAccesso) throws SerializerException {
		this.objToXml(fileName, SpecificaPortiAccesso.class, specificaPortiAccesso, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaPortiAccesso</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaPortiAccesso</var>
	 * @param specificaPortiAccesso Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaPortiAccesso specificaPortiAccesso,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SpecificaPortiAccesso.class, specificaPortiAccesso, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaPortiAccesso</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param file Xml file to serialize the object <var>specificaPortiAccesso</var>
	 * @param specificaPortiAccesso Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaPortiAccesso specificaPortiAccesso) throws SerializerException {
		this.objToXml(file, SpecificaPortiAccesso.class, specificaPortiAccesso, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaPortiAccesso</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param file Xml file to serialize the object <var>specificaPortiAccesso</var>
	 * @param specificaPortiAccesso Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaPortiAccesso specificaPortiAccesso,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SpecificaPortiAccesso.class, specificaPortiAccesso, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaPortiAccesso</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaPortiAccesso</var>
	 * @param specificaPortiAccesso Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaPortiAccesso specificaPortiAccesso) throws SerializerException {
		this.objToXml(out, SpecificaPortiAccesso.class, specificaPortiAccesso, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaPortiAccesso</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaPortiAccesso</var>
	 * @param specificaPortiAccesso Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaPortiAccesso specificaPortiAccesso,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SpecificaPortiAccesso.class, specificaPortiAccesso, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>specificaPortiAccesso</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param specificaPortiAccesso Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaPortiAccesso specificaPortiAccesso) throws SerializerException {
		return this.objToXml(SpecificaPortiAccesso.class, specificaPortiAccesso, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>specificaPortiAccesso</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param specificaPortiAccesso Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaPortiAccesso specificaPortiAccesso,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaPortiAccesso.class, specificaPortiAccesso, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>specificaPortiAccesso</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param specificaPortiAccesso Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaPortiAccesso specificaPortiAccesso) throws SerializerException {
		return this.objToXml(SpecificaPortiAccesso.class, specificaPortiAccesso, false).toString();
	}
	/**
	 * Serialize to String the object <var>specificaPortiAccesso</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param specificaPortiAccesso Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaPortiAccesso specificaPortiAccesso,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaPortiAccesso.class, specificaPortiAccesso, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaSicurezza
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaSicurezza</var>
	 * @param specificaSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaSicurezza specificaSicurezza) throws SerializerException {
		this.objToXml(fileName, SpecificaSicurezza.class, specificaSicurezza, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaSicurezza</var>
	 * @param specificaSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaSicurezza specificaSicurezza,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SpecificaSicurezza.class, specificaSicurezza, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param file Xml file to serialize the object <var>specificaSicurezza</var>
	 * @param specificaSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaSicurezza specificaSicurezza) throws SerializerException {
		this.objToXml(file, SpecificaSicurezza.class, specificaSicurezza, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param file Xml file to serialize the object <var>specificaSicurezza</var>
	 * @param specificaSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaSicurezza specificaSicurezza,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SpecificaSicurezza.class, specificaSicurezza, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaSicurezza</var>
	 * @param specificaSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaSicurezza specificaSicurezza) throws SerializerException {
		this.objToXml(out, SpecificaSicurezza.class, specificaSicurezza, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaSicurezza</var>
	 * @param specificaSicurezza Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaSicurezza specificaSicurezza,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SpecificaSicurezza.class, specificaSicurezza, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>specificaSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param specificaSicurezza Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaSicurezza specificaSicurezza) throws SerializerException {
		return this.objToXml(SpecificaSicurezza.class, specificaSicurezza, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>specificaSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param specificaSicurezza Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaSicurezza specificaSicurezza,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaSicurezza.class, specificaSicurezza, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>specificaSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param specificaSicurezza Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaSicurezza specificaSicurezza) throws SerializerException {
		return this.objToXml(SpecificaSicurezza.class, specificaSicurezza, false).toString();
	}
	/**
	 * Serialize to String the object <var>specificaSicurezza</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param specificaSicurezza Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaSicurezza specificaSicurezza,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaSicurezza.class, specificaSicurezza, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaLivelliServizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaLivelliServizio</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaLivelliServizio</var>
	 * @param specificaLivelliServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaLivelliServizio specificaLivelliServizio) throws SerializerException {
		this.objToXml(fileName, SpecificaLivelliServizio.class, specificaLivelliServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaLivelliServizio</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaLivelliServizio</var>
	 * @param specificaLivelliServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaLivelliServizio specificaLivelliServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SpecificaLivelliServizio.class, specificaLivelliServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaLivelliServizio</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param file Xml file to serialize the object <var>specificaLivelliServizio</var>
	 * @param specificaLivelliServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaLivelliServizio specificaLivelliServizio) throws SerializerException {
		this.objToXml(file, SpecificaLivelliServizio.class, specificaLivelliServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaLivelliServizio</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param file Xml file to serialize the object <var>specificaLivelliServizio</var>
	 * @param specificaLivelliServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaLivelliServizio specificaLivelliServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SpecificaLivelliServizio.class, specificaLivelliServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaLivelliServizio</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaLivelliServizio</var>
	 * @param specificaLivelliServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaLivelliServizio specificaLivelliServizio) throws SerializerException {
		this.objToXml(out, SpecificaLivelliServizio.class, specificaLivelliServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaLivelliServizio</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaLivelliServizio</var>
	 * @param specificaLivelliServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaLivelliServizio specificaLivelliServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SpecificaLivelliServizio.class, specificaLivelliServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>specificaLivelliServizio</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param specificaLivelliServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaLivelliServizio specificaLivelliServizio) throws SerializerException {
		return this.objToXml(SpecificaLivelliServizio.class, specificaLivelliServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>specificaLivelliServizio</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param specificaLivelliServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaLivelliServizio specificaLivelliServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaLivelliServizio.class, specificaLivelliServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>specificaLivelliServizio</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param specificaLivelliServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaLivelliServizio specificaLivelliServizio) throws SerializerException {
		return this.objToXml(SpecificaLivelliServizio.class, specificaLivelliServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>specificaLivelliServizio</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param specificaLivelliServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaLivelliServizio specificaLivelliServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaLivelliServizio.class, specificaLivelliServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaInterfaccia
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaInterfaccia</var>
	 * @param specificaInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaInterfaccia specificaInterfaccia) throws SerializerException {
		this.objToXml(fileName, SpecificaInterfaccia.class, specificaInterfaccia, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaInterfaccia</var>
	 * @param specificaInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaInterfaccia specificaInterfaccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SpecificaInterfaccia.class, specificaInterfaccia, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param file Xml file to serialize the object <var>specificaInterfaccia</var>
	 * @param specificaInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaInterfaccia specificaInterfaccia) throws SerializerException {
		this.objToXml(file, SpecificaInterfaccia.class, specificaInterfaccia, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param file Xml file to serialize the object <var>specificaInterfaccia</var>
	 * @param specificaInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaInterfaccia specificaInterfaccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SpecificaInterfaccia.class, specificaInterfaccia, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaInterfaccia</var>
	 * @param specificaInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaInterfaccia specificaInterfaccia) throws SerializerException {
		this.objToXml(out, SpecificaInterfaccia.class, specificaInterfaccia, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaInterfaccia</var>
	 * @param specificaInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaInterfaccia specificaInterfaccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SpecificaInterfaccia.class, specificaInterfaccia, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>specificaInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param specificaInterfaccia Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaInterfaccia specificaInterfaccia) throws SerializerException {
		return this.objToXml(SpecificaInterfaccia.class, specificaInterfaccia, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>specificaInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param specificaInterfaccia Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaInterfaccia specificaInterfaccia,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaInterfaccia.class, specificaInterfaccia, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>specificaInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param specificaInterfaccia Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaInterfaccia specificaInterfaccia) throws SerializerException {
		return this.objToXml(SpecificaInterfaccia.class, specificaInterfaccia, false).toString();
	}
	/**
	 * Serialize to String the object <var>specificaInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param specificaInterfaccia Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaInterfaccia specificaInterfaccia,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaInterfaccia.class, specificaInterfaccia, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordoServizioParteComune
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComune</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComune</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComune</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComune</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComune</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComune</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComune</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComune</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteComune</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteComune</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaConversazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaConversazione</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaConversazione</var>
	 * @param specificaConversazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaConversazione specificaConversazione) throws SerializerException {
		this.objToXml(fileName, SpecificaConversazione.class, specificaConversazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaConversazione</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaConversazione</var>
	 * @param specificaConversazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaConversazione specificaConversazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SpecificaConversazione.class, specificaConversazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaConversazione</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param file Xml file to serialize the object <var>specificaConversazione</var>
	 * @param specificaConversazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaConversazione specificaConversazione) throws SerializerException {
		this.objToXml(file, SpecificaConversazione.class, specificaConversazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaConversazione</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param file Xml file to serialize the object <var>specificaConversazione</var>
	 * @param specificaConversazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaConversazione specificaConversazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SpecificaConversazione.class, specificaConversazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaConversazione</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaConversazione</var>
	 * @param specificaConversazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaConversazione specificaConversazione) throws SerializerException {
		this.objToXml(out, SpecificaConversazione.class, specificaConversazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaConversazione</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaConversazione</var>
	 * @param specificaConversazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaConversazione specificaConversazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SpecificaConversazione.class, specificaConversazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>specificaConversazione</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param specificaConversazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaConversazione specificaConversazione) throws SerializerException {
		return this.objToXml(SpecificaConversazione.class, specificaConversazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>specificaConversazione</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param specificaConversazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaConversazione specificaConversazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaConversazione.class, specificaConversazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>specificaConversazione</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param specificaConversazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaConversazione specificaConversazione) throws SerializerException {
		return this.objToXml(SpecificaConversazione.class, specificaConversazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>specificaConversazione</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param specificaConversazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaConversazione specificaConversazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaConversazione.class, specificaConversazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DocumentoConversazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoConversazione</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoConversazione</var>
	 * @param documentoConversazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoConversazione documentoConversazione) throws SerializerException {
		this.objToXml(fileName, DocumentoConversazione.class, documentoConversazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoConversazione</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoConversazione</var>
	 * @param documentoConversazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoConversazione documentoConversazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DocumentoConversazione.class, documentoConversazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoConversazione</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param file Xml file to serialize the object <var>documentoConversazione</var>
	 * @param documentoConversazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoConversazione documentoConversazione) throws SerializerException {
		this.objToXml(file, DocumentoConversazione.class, documentoConversazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoConversazione</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param file Xml file to serialize the object <var>documentoConversazione</var>
	 * @param documentoConversazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoConversazione documentoConversazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DocumentoConversazione.class, documentoConversazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoConversazione</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoConversazione</var>
	 * @param documentoConversazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoConversazione documentoConversazione) throws SerializerException {
		this.objToXml(out, DocumentoConversazione.class, documentoConversazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoConversazione</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoConversazione</var>
	 * @param documentoConversazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoConversazione documentoConversazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DocumentoConversazione.class, documentoConversazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>documentoConversazione</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param documentoConversazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoConversazione documentoConversazione) throws SerializerException {
		return this.objToXml(DocumentoConversazione.class, documentoConversazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>documentoConversazione</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param documentoConversazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoConversazione documentoConversazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoConversazione.class, documentoConversazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>documentoConversazione</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param documentoConversazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoConversazione documentoConversazione) throws SerializerException {
		return this.objToXml(DocumentoConversazione.class, documentoConversazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>documentoConversazione</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param documentoConversazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoConversazione documentoConversazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoConversazione.class, documentoConversazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DocumentoInterfaccia
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoInterfaccia</var>
	 * @param documentoInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoInterfaccia documentoInterfaccia) throws SerializerException {
		this.objToXml(fileName, DocumentoInterfaccia.class, documentoInterfaccia, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoInterfaccia</var>
	 * @param documentoInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoInterfaccia documentoInterfaccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DocumentoInterfaccia.class, documentoInterfaccia, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param file Xml file to serialize the object <var>documentoInterfaccia</var>
	 * @param documentoInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoInterfaccia documentoInterfaccia) throws SerializerException {
		this.objToXml(file, DocumentoInterfaccia.class, documentoInterfaccia, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param file Xml file to serialize the object <var>documentoInterfaccia</var>
	 * @param documentoInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoInterfaccia documentoInterfaccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DocumentoInterfaccia.class, documentoInterfaccia, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoInterfaccia</var>
	 * @param documentoInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoInterfaccia documentoInterfaccia) throws SerializerException {
		this.objToXml(out, DocumentoInterfaccia.class, documentoInterfaccia, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoInterfaccia</var>
	 * @param documentoInterfaccia Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoInterfaccia documentoInterfaccia,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DocumentoInterfaccia.class, documentoInterfaccia, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>documentoInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param documentoInterfaccia Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoInterfaccia documentoInterfaccia) throws SerializerException {
		return this.objToXml(DocumentoInterfaccia.class, documentoInterfaccia, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>documentoInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param documentoInterfaccia Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoInterfaccia documentoInterfaccia,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoInterfaccia.class, documentoInterfaccia, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>documentoInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param documentoInterfaccia Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoInterfaccia documentoInterfaccia) throws SerializerException {
		return this.objToXml(DocumentoInterfaccia.class, documentoInterfaccia, false).toString();
	}
	/**
	 * Serialize to String the object <var>documentoInterfaccia</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param documentoInterfaccia Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoInterfaccia documentoInterfaccia,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoInterfaccia.class, documentoInterfaccia, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ElencoPartecipanti
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoPartecipanti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoPartecipanti</var>
	 * @param elencoPartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoPartecipanti elencoPartecipanti) throws SerializerException {
		this.objToXml(fileName, ElencoPartecipanti.class, elencoPartecipanti, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoPartecipanti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoPartecipanti</var>
	 * @param elencoPartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoPartecipanti elencoPartecipanti,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoPartecipanti.class, elencoPartecipanti, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoPartecipanti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param file Xml file to serialize the object <var>elencoPartecipanti</var>
	 * @param elencoPartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoPartecipanti elencoPartecipanti) throws SerializerException {
		this.objToXml(file, ElencoPartecipanti.class, elencoPartecipanti, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoPartecipanti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param file Xml file to serialize the object <var>elencoPartecipanti</var>
	 * @param elencoPartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoPartecipanti elencoPartecipanti,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoPartecipanti.class, elencoPartecipanti, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoPartecipanti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoPartecipanti</var>
	 * @param elencoPartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoPartecipanti elencoPartecipanti) throws SerializerException {
		this.objToXml(out, ElencoPartecipanti.class, elencoPartecipanti, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoPartecipanti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoPartecipanti</var>
	 * @param elencoPartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoPartecipanti elencoPartecipanti,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoPartecipanti.class, elencoPartecipanti, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoPartecipanti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param elencoPartecipanti Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoPartecipanti elencoPartecipanti) throws SerializerException {
		return this.objToXml(ElencoPartecipanti.class, elencoPartecipanti, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoPartecipanti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param elencoPartecipanti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoPartecipanti elencoPartecipanti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoPartecipanti.class, elencoPartecipanti, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoPartecipanti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param elencoPartecipanti Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoPartecipanti elencoPartecipanti) throws SerializerException {
		return this.objToXml(ElencoPartecipanti.class, elencoPartecipanti, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoPartecipanti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param elencoPartecipanti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoPartecipanti elencoPartecipanti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoPartecipanti.class, elencoPartecipanti, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizioComposto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioComposto</var> of type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioComposto</var>
	 * @param servizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioComposto servizioComposto) throws SerializerException {
		this.objToXml(fileName, ServizioComposto.class, servizioComposto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioComposto</var> of type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioComposto</var>
	 * @param servizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioComposto servizioComposto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServizioComposto.class, servizioComposto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioComposto</var> of type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param file Xml file to serialize the object <var>servizioComposto</var>
	 * @param servizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioComposto servizioComposto) throws SerializerException {
		this.objToXml(file, ServizioComposto.class, servizioComposto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioComposto</var> of type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param file Xml file to serialize the object <var>servizioComposto</var>
	 * @param servizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioComposto servizioComposto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServizioComposto.class, servizioComposto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioComposto</var> of type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioComposto</var>
	 * @param servizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioComposto servizioComposto) throws SerializerException {
		this.objToXml(out, ServizioComposto.class, servizioComposto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioComposto</var> of type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioComposto</var>
	 * @param servizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioComposto servizioComposto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServizioComposto.class, servizioComposto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizioComposto</var> of type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param servizioComposto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioComposto servizioComposto) throws SerializerException {
		return this.objToXml(ServizioComposto.class, servizioComposto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizioComposto</var> of type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param servizioComposto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioComposto servizioComposto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioComposto.class, servizioComposto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizioComposto</var> of type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param servizioComposto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioComposto servizioComposto) throws SerializerException {
		return this.objToXml(ServizioComposto.class, servizioComposto, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizioComposto</var> of type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param servizioComposto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioComposto servizioComposto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioComposto.class, servizioComposto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ElencoAllegati
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoAllegati</var> of type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoAllegati</var>
	 * @param elencoAllegati Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoAllegati elencoAllegati) throws SerializerException {
		this.objToXml(fileName, ElencoAllegati.class, elencoAllegati, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoAllegati</var> of type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoAllegati</var>
	 * @param elencoAllegati Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoAllegati elencoAllegati,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoAllegati.class, elencoAllegati, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoAllegati</var> of type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param file Xml file to serialize the object <var>elencoAllegati</var>
	 * @param elencoAllegati Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoAllegati elencoAllegati) throws SerializerException {
		this.objToXml(file, ElencoAllegati.class, elencoAllegati, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoAllegati</var> of type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param file Xml file to serialize the object <var>elencoAllegati</var>
	 * @param elencoAllegati Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoAllegati elencoAllegati,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoAllegati.class, elencoAllegati, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoAllegati</var> of type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoAllegati</var>
	 * @param elencoAllegati Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoAllegati elencoAllegati) throws SerializerException {
		this.objToXml(out, ElencoAllegati.class, elencoAllegati, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoAllegati</var> of type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoAllegati</var>
	 * @param elencoAllegati Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoAllegati elencoAllegati,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoAllegati.class, elencoAllegati, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoAllegati</var> of type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param elencoAllegati Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoAllegati elencoAllegati) throws SerializerException {
		return this.objToXml(ElencoAllegati.class, elencoAllegati, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoAllegati</var> of type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param elencoAllegati Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoAllegati elencoAllegati,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoAllegati.class, elencoAllegati, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoAllegati</var> of type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param elencoAllegati Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoAllegati elencoAllegati) throws SerializerException {
		return this.objToXml(ElencoAllegati.class, elencoAllegati, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoAllegati</var> of type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param elencoAllegati Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoAllegati elencoAllegati,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoAllegati.class, elencoAllegati, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaCoordinamento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaCoordinamento</var>
	 * @param specificaCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaCoordinamento specificaCoordinamento) throws SerializerException {
		this.objToXml(fileName, SpecificaCoordinamento.class, specificaCoordinamento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>specificaCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>specificaCoordinamento</var>
	 * @param specificaCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SpecificaCoordinamento specificaCoordinamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SpecificaCoordinamento.class, specificaCoordinamento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param file Xml file to serialize the object <var>specificaCoordinamento</var>
	 * @param specificaCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaCoordinamento specificaCoordinamento) throws SerializerException {
		this.objToXml(file, SpecificaCoordinamento.class, specificaCoordinamento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>specificaCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param file Xml file to serialize the object <var>specificaCoordinamento</var>
	 * @param specificaCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SpecificaCoordinamento specificaCoordinamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SpecificaCoordinamento.class, specificaCoordinamento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaCoordinamento</var>
	 * @param specificaCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaCoordinamento specificaCoordinamento) throws SerializerException {
		this.objToXml(out, SpecificaCoordinamento.class, specificaCoordinamento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>specificaCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param out OutputStream to serialize the object <var>specificaCoordinamento</var>
	 * @param specificaCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SpecificaCoordinamento specificaCoordinamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SpecificaCoordinamento.class, specificaCoordinamento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>specificaCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param specificaCoordinamento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaCoordinamento specificaCoordinamento) throws SerializerException {
		return this.objToXml(SpecificaCoordinamento.class, specificaCoordinamento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>specificaCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param specificaCoordinamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SpecificaCoordinamento specificaCoordinamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaCoordinamento.class, specificaCoordinamento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>specificaCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param specificaCoordinamento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaCoordinamento specificaCoordinamento) throws SerializerException {
		return this.objToXml(SpecificaCoordinamento.class, specificaCoordinamento, false).toString();
	}
	/**
	 * Serialize to String the object <var>specificaCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param specificaCoordinamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SpecificaCoordinamento specificaCoordinamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SpecificaCoordinamento.class, specificaCoordinamento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DocumentoLivelloServizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoLivelloServizio</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoLivelloServizio</var>
	 * @param documentoLivelloServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoLivelloServizio documentoLivelloServizio) throws SerializerException {
		this.objToXml(fileName, DocumentoLivelloServizio.class, documentoLivelloServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoLivelloServizio</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoLivelloServizio</var>
	 * @param documentoLivelloServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoLivelloServizio documentoLivelloServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DocumentoLivelloServizio.class, documentoLivelloServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoLivelloServizio</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param file Xml file to serialize the object <var>documentoLivelloServizio</var>
	 * @param documentoLivelloServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoLivelloServizio documentoLivelloServizio) throws SerializerException {
		this.objToXml(file, DocumentoLivelloServizio.class, documentoLivelloServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoLivelloServizio</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param file Xml file to serialize the object <var>documentoLivelloServizio</var>
	 * @param documentoLivelloServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoLivelloServizio documentoLivelloServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DocumentoLivelloServizio.class, documentoLivelloServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoLivelloServizio</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoLivelloServizio</var>
	 * @param documentoLivelloServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoLivelloServizio documentoLivelloServizio) throws SerializerException {
		this.objToXml(out, DocumentoLivelloServizio.class, documentoLivelloServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoLivelloServizio</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoLivelloServizio</var>
	 * @param documentoLivelloServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoLivelloServizio documentoLivelloServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DocumentoLivelloServizio.class, documentoLivelloServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>documentoLivelloServizio</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param documentoLivelloServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoLivelloServizio documentoLivelloServizio) throws SerializerException {
		return this.objToXml(DocumentoLivelloServizio.class, documentoLivelloServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>documentoLivelloServizio</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param documentoLivelloServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoLivelloServizio documentoLivelloServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoLivelloServizio.class, documentoLivelloServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>documentoLivelloServizio</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param documentoLivelloServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoLivelloServizio documentoLivelloServizio) throws SerializerException {
		return this.objToXml(DocumentoLivelloServizio.class, documentoLivelloServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>documentoLivelloServizio</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param documentoLivelloServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoLivelloServizio documentoLivelloServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoLivelloServizio.class, documentoLivelloServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordoCooperazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoCooperazione</var> of type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoCooperazione accordoCooperazione) throws SerializerException {
		this.objToXml(fileName, AccordoCooperazione.class, accordoCooperazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoCooperazione</var> of type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoCooperazione.class, accordoCooperazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoCooperazione</var> of type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param file Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoCooperazione accordoCooperazione) throws SerializerException {
		this.objToXml(file, AccordoCooperazione.class, accordoCooperazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoCooperazione</var> of type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param file Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoCooperazione.class, accordoCooperazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoCooperazione</var> of type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoCooperazione accordoCooperazione) throws SerializerException {
		this.objToXml(out, AccordoCooperazione.class, accordoCooperazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoCooperazione</var> of type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoCooperazione.class, accordoCooperazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoCooperazione</var> of type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoCooperazione accordoCooperazione) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoCooperazione</var> of type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoCooperazione</var> of type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoCooperazione accordoCooperazione) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoCooperazione</var> of type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ElencoServiziComposti
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoServiziComposti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoServiziComposti</var>
	 * @param elencoServiziComposti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoServiziComposti elencoServiziComposti) throws SerializerException {
		this.objToXml(fileName, ElencoServiziComposti.class, elencoServiziComposti, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>elencoServiziComposti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param fileName Xml file to serialize the object <var>elencoServiziComposti</var>
	 * @param elencoServiziComposti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ElencoServiziComposti elencoServiziComposti,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ElencoServiziComposti.class, elencoServiziComposti, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoServiziComposti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param file Xml file to serialize the object <var>elencoServiziComposti</var>
	 * @param elencoServiziComposti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoServiziComposti elencoServiziComposti) throws SerializerException {
		this.objToXml(file, ElencoServiziComposti.class, elencoServiziComposti, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>elencoServiziComposti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param file Xml file to serialize the object <var>elencoServiziComposti</var>
	 * @param elencoServiziComposti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ElencoServiziComposti elencoServiziComposti,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ElencoServiziComposti.class, elencoServiziComposti, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoServiziComposti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoServiziComposti</var>
	 * @param elencoServiziComposti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoServiziComposti elencoServiziComposti) throws SerializerException {
		this.objToXml(out, ElencoServiziComposti.class, elencoServiziComposti, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>elencoServiziComposti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param out OutputStream to serialize the object <var>elencoServiziComposti</var>
	 * @param elencoServiziComposti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ElencoServiziComposti elencoServiziComposti,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ElencoServiziComposti.class, elencoServiziComposti, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>elencoServiziComposti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param elencoServiziComposti Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoServiziComposti elencoServiziComposti) throws SerializerException {
		return this.objToXml(ElencoServiziComposti.class, elencoServiziComposti, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>elencoServiziComposti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param elencoServiziComposti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ElencoServiziComposti elencoServiziComposti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoServiziComposti.class, elencoServiziComposti, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>elencoServiziComposti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param elencoServiziComposti Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoServiziComposti elencoServiziComposti) throws SerializerException {
		return this.objToXml(ElencoServiziComposti.class, elencoServiziComposti, false).toString();
	}
	/**
	 * Serialize to String the object <var>elencoServiziComposti</var> of type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param elencoServiziComposti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ElencoServiziComposti elencoServiziComposti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ElencoServiziComposti.class, elencoServiziComposti, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DocumentoCoordinamento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoCoordinamento</var>
	 * @param documentoCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoCoordinamento documentoCoordinamento) throws SerializerException {
		this.objToXml(fileName, DocumentoCoordinamento.class, documentoCoordinamento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documentoCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>documentoCoordinamento</var>
	 * @param documentoCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DocumentoCoordinamento documentoCoordinamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DocumentoCoordinamento.class, documentoCoordinamento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param file Xml file to serialize the object <var>documentoCoordinamento</var>
	 * @param documentoCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoCoordinamento documentoCoordinamento) throws SerializerException {
		this.objToXml(file, DocumentoCoordinamento.class, documentoCoordinamento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>documentoCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param file Xml file to serialize the object <var>documentoCoordinamento</var>
	 * @param documentoCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DocumentoCoordinamento documentoCoordinamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DocumentoCoordinamento.class, documentoCoordinamento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoCoordinamento</var>
	 * @param documentoCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoCoordinamento documentoCoordinamento) throws SerializerException {
		this.objToXml(out, DocumentoCoordinamento.class, documentoCoordinamento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>documentoCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param out OutputStream to serialize the object <var>documentoCoordinamento</var>
	 * @param documentoCoordinamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DocumentoCoordinamento documentoCoordinamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DocumentoCoordinamento.class, documentoCoordinamento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>documentoCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param documentoCoordinamento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoCoordinamento documentoCoordinamento) throws SerializerException {
		return this.objToXml(DocumentoCoordinamento.class, documentoCoordinamento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>documentoCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param documentoCoordinamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DocumentoCoordinamento documentoCoordinamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoCoordinamento.class, documentoCoordinamento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>documentoCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param documentoCoordinamento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoCoordinamento documentoCoordinamento) throws SerializerException {
		return this.objToXml(DocumentoCoordinamento.class, documentoCoordinamento, false).toString();
	}
	/**
	 * Serialize to String the object <var>documentoCoordinamento</var> of type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param documentoCoordinamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DocumentoCoordinamento documentoCoordinamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DocumentoCoordinamento.class, documentoCoordinamento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordoServizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizio</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizio</var>
	 * @param accordoServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizio accordoServizio) throws SerializerException {
		this.objToXml(fileName, AccordoServizio.class, accordoServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizio</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizio</var>
	 * @param accordoServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizio accordoServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizio.class, accordoServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizio</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizio</var>
	 * @param accordoServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizio accordoServizio) throws SerializerException {
		this.objToXml(file, AccordoServizio.class, accordoServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizio</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizio</var>
	 * @param accordoServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizio accordoServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizio.class, accordoServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizio</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizio</var>
	 * @param accordoServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizio accordoServizio) throws SerializerException {
		this.objToXml(out, AccordoServizio.class, accordoServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizio</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizio</var>
	 * @param accordoServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizio accordoServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizio.class, accordoServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizio</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param accordoServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizio accordoServizio) throws SerializerException {
		return this.objToXml(AccordoServizio.class, accordoServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizio</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param accordoServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizio accordoServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizio.class, accordoServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizio</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param accordoServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizio accordoServizio) throws SerializerException {
		return this.objToXml(AccordoServizio.class, accordoServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizio</var> of type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param accordoServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizio accordoServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizio.class, accordoServizio, prettyPrint).toString();
	}
	
	
	

}
