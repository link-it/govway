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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType;

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
	 Object: AllegatiType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allegatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>allegatiType</var>
	 * @param allegatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllegatiType allegatiType) throws SerializerException {
		this.objToXml(fileName, AllegatiType.class, allegatiType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allegatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>allegatiType</var>
	 * @param allegatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllegatiType allegatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AllegatiType.class, allegatiType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>allegatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param file Xml file to serialize the object <var>allegatiType</var>
	 * @param allegatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllegatiType allegatiType) throws SerializerException {
		this.objToXml(file, AllegatiType.class, allegatiType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allegatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param file Xml file to serialize the object <var>allegatiType</var>
	 * @param allegatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllegatiType allegatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AllegatiType.class, allegatiType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>allegatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param out OutputStream to serialize the object <var>allegatiType</var>
	 * @param allegatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllegatiType allegatiType) throws SerializerException {
		this.objToXml(out, AllegatiType.class, allegatiType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allegatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param out OutputStream to serialize the object <var>allegatiType</var>
	 * @param allegatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllegatiType allegatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AllegatiType.class, allegatiType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>allegatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param allegatiType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllegatiType allegatiType) throws SerializerException {
		return this.objToXml(AllegatiType.class, allegatiType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allegatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param allegatiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllegatiType allegatiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllegatiType.class, allegatiType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>allegatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param allegatiType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllegatiType allegatiType) throws SerializerException {
		return this.objToXml(AllegatiType.class, allegatiType, false).toString();
	}
	/**
	 * Serialize to String the object <var>allegatiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param allegatiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllegatiType allegatiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AllegatiType.class, allegatiType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiIVAType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiIVAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiIVAType</var>
	 * @param datiIVAType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiIVAType datiIVAType) throws SerializerException {
		this.objToXml(fileName, DatiIVAType.class, datiIVAType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiIVAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiIVAType</var>
	 * @param datiIVAType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiIVAType datiIVAType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiIVAType.class, datiIVAType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiIVAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param file Xml file to serialize the object <var>datiIVAType</var>
	 * @param datiIVAType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiIVAType datiIVAType) throws SerializerException {
		this.objToXml(file, DatiIVAType.class, datiIVAType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiIVAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param file Xml file to serialize the object <var>datiIVAType</var>
	 * @param datiIVAType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiIVAType datiIVAType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiIVAType.class, datiIVAType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiIVAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiIVAType</var>
	 * @param datiIVAType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiIVAType datiIVAType) throws SerializerException {
		this.objToXml(out, DatiIVAType.class, datiIVAType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiIVAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiIVAType</var>
	 * @param datiIVAType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiIVAType datiIVAType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiIVAType.class, datiIVAType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiIVAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param datiIVAType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiIVAType datiIVAType) throws SerializerException {
		return this.objToXml(DatiIVAType.class, datiIVAType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiIVAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param datiIVAType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiIVAType datiIVAType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiIVAType.class, datiIVAType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiIVAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param datiIVAType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiIVAType datiIVAType) throws SerializerException {
		return this.objToXml(DatiIVAType.class, datiIVAType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiIVAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param datiIVAType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiIVAType datiIVAType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiIVAType.class, datiIVAType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: AltriDatiIdentificativiType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>altriDatiIdentificativiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>altriDatiIdentificativiType</var>
	 * @param altriDatiIdentificativiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AltriDatiIdentificativiType altriDatiIdentificativiType) throws SerializerException {
		this.objToXml(fileName, AltriDatiIdentificativiType.class, altriDatiIdentificativiType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>altriDatiIdentificativiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>altriDatiIdentificativiType</var>
	 * @param altriDatiIdentificativiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AltriDatiIdentificativiType altriDatiIdentificativiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AltriDatiIdentificativiType.class, altriDatiIdentificativiType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>altriDatiIdentificativiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param file Xml file to serialize the object <var>altriDatiIdentificativiType</var>
	 * @param altriDatiIdentificativiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AltriDatiIdentificativiType altriDatiIdentificativiType) throws SerializerException {
		this.objToXml(file, AltriDatiIdentificativiType.class, altriDatiIdentificativiType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>altriDatiIdentificativiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param file Xml file to serialize the object <var>altriDatiIdentificativiType</var>
	 * @param altriDatiIdentificativiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AltriDatiIdentificativiType altriDatiIdentificativiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AltriDatiIdentificativiType.class, altriDatiIdentificativiType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>altriDatiIdentificativiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param out OutputStream to serialize the object <var>altriDatiIdentificativiType</var>
	 * @param altriDatiIdentificativiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AltriDatiIdentificativiType altriDatiIdentificativiType) throws SerializerException {
		this.objToXml(out, AltriDatiIdentificativiType.class, altriDatiIdentificativiType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>altriDatiIdentificativiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param out OutputStream to serialize the object <var>altriDatiIdentificativiType</var>
	 * @param altriDatiIdentificativiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AltriDatiIdentificativiType altriDatiIdentificativiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AltriDatiIdentificativiType.class, altriDatiIdentificativiType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>altriDatiIdentificativiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param altriDatiIdentificativiType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AltriDatiIdentificativiType altriDatiIdentificativiType) throws SerializerException {
		return this.objToXml(AltriDatiIdentificativiType.class, altriDatiIdentificativiType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>altriDatiIdentificativiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param altriDatiIdentificativiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AltriDatiIdentificativiType altriDatiIdentificativiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AltriDatiIdentificativiType.class, altriDatiIdentificativiType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>altriDatiIdentificativiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param altriDatiIdentificativiType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AltriDatiIdentificativiType altriDatiIdentificativiType) throws SerializerException {
		return this.objToXml(AltriDatiIdentificativiType.class, altriDatiIdentificativiType, false).toString();
	}
	/**
	 * Serialize to String the object <var>altriDatiIdentificativiType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param altriDatiIdentificativiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AltriDatiIdentificativiType altriDatiIdentificativiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AltriDatiIdentificativiType.class, altriDatiIdentificativiType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IndirizzoType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>indirizzoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>indirizzoType</var>
	 * @param indirizzoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IndirizzoType indirizzoType) throws SerializerException {
		this.objToXml(fileName, IndirizzoType.class, indirizzoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>indirizzoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>indirizzoType</var>
	 * @param indirizzoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IndirizzoType indirizzoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IndirizzoType.class, indirizzoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>indirizzoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param file Xml file to serialize the object <var>indirizzoType</var>
	 * @param indirizzoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IndirizzoType indirizzoType) throws SerializerException {
		this.objToXml(file, IndirizzoType.class, indirizzoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>indirizzoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param file Xml file to serialize the object <var>indirizzoType</var>
	 * @param indirizzoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IndirizzoType indirizzoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IndirizzoType.class, indirizzoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>indirizzoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param out OutputStream to serialize the object <var>indirizzoType</var>
	 * @param indirizzoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IndirizzoType indirizzoType) throws SerializerException {
		this.objToXml(out, IndirizzoType.class, indirizzoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>indirizzoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param out OutputStream to serialize the object <var>indirizzoType</var>
	 * @param indirizzoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IndirizzoType indirizzoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IndirizzoType.class, indirizzoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>indirizzoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param indirizzoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IndirizzoType indirizzoType) throws SerializerException {
		return this.objToXml(IndirizzoType.class, indirizzoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>indirizzoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param indirizzoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IndirizzoType indirizzoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IndirizzoType.class, indirizzoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>indirizzoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param indirizzoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IndirizzoType indirizzoType) throws SerializerException {
		return this.objToXml(IndirizzoType.class, indirizzoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>indirizzoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param indirizzoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IndirizzoType indirizzoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IndirizzoType.class, indirizzoType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RappresentanteFiscaleType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param fileName Xml file to serialize the object <var>rappresentanteFiscaleType</var>
	 * @param rappresentanteFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RappresentanteFiscaleType rappresentanteFiscaleType) throws SerializerException {
		this.objToXml(fileName, RappresentanteFiscaleType.class, rappresentanteFiscaleType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param fileName Xml file to serialize the object <var>rappresentanteFiscaleType</var>
	 * @param rappresentanteFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RappresentanteFiscaleType rappresentanteFiscaleType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RappresentanteFiscaleType.class, rappresentanteFiscaleType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param file Xml file to serialize the object <var>rappresentanteFiscaleType</var>
	 * @param rappresentanteFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RappresentanteFiscaleType rappresentanteFiscaleType) throws SerializerException {
		this.objToXml(file, RappresentanteFiscaleType.class, rappresentanteFiscaleType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param file Xml file to serialize the object <var>rappresentanteFiscaleType</var>
	 * @param rappresentanteFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RappresentanteFiscaleType rappresentanteFiscaleType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RappresentanteFiscaleType.class, rappresentanteFiscaleType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param out OutputStream to serialize the object <var>rappresentanteFiscaleType</var>
	 * @param rappresentanteFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RappresentanteFiscaleType rappresentanteFiscaleType) throws SerializerException {
		this.objToXml(out, RappresentanteFiscaleType.class, rappresentanteFiscaleType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param out OutputStream to serialize the object <var>rappresentanteFiscaleType</var>
	 * @param rappresentanteFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RappresentanteFiscaleType rappresentanteFiscaleType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RappresentanteFiscaleType.class, rappresentanteFiscaleType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param rappresentanteFiscaleType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RappresentanteFiscaleType rappresentanteFiscaleType) throws SerializerException {
		return this.objToXml(RappresentanteFiscaleType.class, rappresentanteFiscaleType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param rappresentanteFiscaleType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RappresentanteFiscaleType rappresentanteFiscaleType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RappresentanteFiscaleType.class, rappresentanteFiscaleType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param rappresentanteFiscaleType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RappresentanteFiscaleType rappresentanteFiscaleType) throws SerializerException {
		return this.objToXml(RappresentanteFiscaleType.class, rappresentanteFiscaleType, false).toString();
	}
	/**
	 * Serialize to String the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param rappresentanteFiscaleType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RappresentanteFiscaleType rappresentanteFiscaleType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RappresentanteFiscaleType.class, rappresentanteFiscaleType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IdFiscaleType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param fileName Xml file to serialize the object <var>idFiscaleType</var>
	 * @param idFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdFiscaleType idFiscaleType) throws SerializerException {
		this.objToXml(fileName, IdFiscaleType.class, idFiscaleType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param fileName Xml file to serialize the object <var>idFiscaleType</var>
	 * @param idFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdFiscaleType idFiscaleType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdFiscaleType.class, idFiscaleType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param file Xml file to serialize the object <var>idFiscaleType</var>
	 * @param idFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdFiscaleType idFiscaleType) throws SerializerException {
		this.objToXml(file, IdFiscaleType.class, idFiscaleType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param file Xml file to serialize the object <var>idFiscaleType</var>
	 * @param idFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdFiscaleType idFiscaleType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdFiscaleType.class, idFiscaleType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param out OutputStream to serialize the object <var>idFiscaleType</var>
	 * @param idFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdFiscaleType idFiscaleType) throws SerializerException {
		this.objToXml(out, IdFiscaleType.class, idFiscaleType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param out OutputStream to serialize the object <var>idFiscaleType</var>
	 * @param idFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdFiscaleType idFiscaleType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdFiscaleType.class, idFiscaleType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param idFiscaleType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdFiscaleType idFiscaleType) throws SerializerException {
		return this.objToXml(IdFiscaleType.class, idFiscaleType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param idFiscaleType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdFiscaleType idFiscaleType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdFiscaleType.class, idFiscaleType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param idFiscaleType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdFiscaleType idFiscaleType) throws SerializerException {
		return this.objToXml(IdFiscaleType.class, idFiscaleType, false).toString();
	}
	/**
	 * Serialize to String the object <var>idFiscaleType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param idFiscaleType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdFiscaleType idFiscaleType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdFiscaleType.class, idFiscaleType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IdentificativiFiscaliType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>identificativiFiscaliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param fileName Xml file to serialize the object <var>identificativiFiscaliType</var>
	 * @param identificativiFiscaliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdentificativiFiscaliType identificativiFiscaliType) throws SerializerException {
		this.objToXml(fileName, IdentificativiFiscaliType.class, identificativiFiscaliType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>identificativiFiscaliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param fileName Xml file to serialize the object <var>identificativiFiscaliType</var>
	 * @param identificativiFiscaliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdentificativiFiscaliType identificativiFiscaliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdentificativiFiscaliType.class, identificativiFiscaliType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>identificativiFiscaliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param file Xml file to serialize the object <var>identificativiFiscaliType</var>
	 * @param identificativiFiscaliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdentificativiFiscaliType identificativiFiscaliType) throws SerializerException {
		this.objToXml(file, IdentificativiFiscaliType.class, identificativiFiscaliType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>identificativiFiscaliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param file Xml file to serialize the object <var>identificativiFiscaliType</var>
	 * @param identificativiFiscaliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdentificativiFiscaliType identificativiFiscaliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdentificativiFiscaliType.class, identificativiFiscaliType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>identificativiFiscaliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param out OutputStream to serialize the object <var>identificativiFiscaliType</var>
	 * @param identificativiFiscaliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdentificativiFiscaliType identificativiFiscaliType) throws SerializerException {
		this.objToXml(out, IdentificativiFiscaliType.class, identificativiFiscaliType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>identificativiFiscaliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param out OutputStream to serialize the object <var>identificativiFiscaliType</var>
	 * @param identificativiFiscaliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdentificativiFiscaliType identificativiFiscaliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdentificativiFiscaliType.class, identificativiFiscaliType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>identificativiFiscaliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param identificativiFiscaliType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdentificativiFiscaliType identificativiFiscaliType) throws SerializerException {
		return this.objToXml(IdentificativiFiscaliType.class, identificativiFiscaliType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>identificativiFiscaliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param identificativiFiscaliType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdentificativiFiscaliType identificativiFiscaliType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdentificativiFiscaliType.class, identificativiFiscaliType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>identificativiFiscaliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param identificativiFiscaliType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdentificativiFiscaliType identificativiFiscaliType) throws SerializerException {
		return this.objToXml(IdentificativiFiscaliType.class, identificativiFiscaliType, false).toString();
	}
	/**
	 * Serialize to String the object <var>identificativiFiscaliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param identificativiFiscaliType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdentificativiFiscaliType identificativiFiscaliType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdentificativiFiscaliType.class, identificativiFiscaliType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiGeneraliDocumentoType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiGeneraliDocumentoType</var>
	 * @param datiGeneraliDocumentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiGeneraliDocumentoType datiGeneraliDocumentoType) throws SerializerException {
		this.objToXml(fileName, DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiGeneraliDocumentoType</var>
	 * @param datiGeneraliDocumentoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiGeneraliDocumentoType datiGeneraliDocumentoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param file Xml file to serialize the object <var>datiGeneraliDocumentoType</var>
	 * @param datiGeneraliDocumentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiGeneraliDocumentoType datiGeneraliDocumentoType) throws SerializerException {
		this.objToXml(file, DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param file Xml file to serialize the object <var>datiGeneraliDocumentoType</var>
	 * @param datiGeneraliDocumentoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiGeneraliDocumentoType datiGeneraliDocumentoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiGeneraliDocumentoType</var>
	 * @param datiGeneraliDocumentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiGeneraliDocumentoType datiGeneraliDocumentoType) throws SerializerException {
		this.objToXml(out, DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiGeneraliDocumentoType</var>
	 * @param datiGeneraliDocumentoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiGeneraliDocumentoType datiGeneraliDocumentoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param datiGeneraliDocumentoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiGeneraliDocumentoType datiGeneraliDocumentoType) throws SerializerException {
		return this.objToXml(DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param datiGeneraliDocumentoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiGeneraliDocumentoType datiGeneraliDocumentoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param datiGeneraliDocumentoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiGeneraliDocumentoType datiGeneraliDocumentoType) throws SerializerException {
		return this.objToXml(DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param datiGeneraliDocumentoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiGeneraliDocumentoType datiGeneraliDocumentoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiGeneraliType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiGeneraliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiGeneraliType</var>
	 * @param datiGeneraliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiGeneraliType datiGeneraliType) throws SerializerException {
		this.objToXml(fileName, DatiGeneraliType.class, datiGeneraliType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiGeneraliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiGeneraliType</var>
	 * @param datiGeneraliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiGeneraliType datiGeneraliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiGeneraliType.class, datiGeneraliType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiGeneraliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param file Xml file to serialize the object <var>datiGeneraliType</var>
	 * @param datiGeneraliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiGeneraliType datiGeneraliType) throws SerializerException {
		this.objToXml(file, DatiGeneraliType.class, datiGeneraliType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiGeneraliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param file Xml file to serialize the object <var>datiGeneraliType</var>
	 * @param datiGeneraliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiGeneraliType datiGeneraliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiGeneraliType.class, datiGeneraliType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiGeneraliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiGeneraliType</var>
	 * @param datiGeneraliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiGeneraliType datiGeneraliType) throws SerializerException {
		this.objToXml(out, DatiGeneraliType.class, datiGeneraliType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiGeneraliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiGeneraliType</var>
	 * @param datiGeneraliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiGeneraliType datiGeneraliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiGeneraliType.class, datiGeneraliType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiGeneraliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param datiGeneraliType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiGeneraliType datiGeneraliType) throws SerializerException {
		return this.objToXml(DatiGeneraliType.class, datiGeneraliType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiGeneraliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param datiGeneraliType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiGeneraliType datiGeneraliType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiGeneraliType.class, datiGeneraliType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiGeneraliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param datiGeneraliType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiGeneraliType datiGeneraliType) throws SerializerException {
		return this.objToXml(DatiGeneraliType.class, datiGeneraliType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiGeneraliType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param datiGeneraliType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiGeneraliType datiGeneraliType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiGeneraliType.class, datiGeneraliType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiFatturaRettificataType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiFatturaRettificataType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiFatturaRettificataType</var>
	 * @param datiFatturaRettificataType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiFatturaRettificataType datiFatturaRettificataType) throws SerializerException {
		this.objToXml(fileName, DatiFatturaRettificataType.class, datiFatturaRettificataType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiFatturaRettificataType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiFatturaRettificataType</var>
	 * @param datiFatturaRettificataType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiFatturaRettificataType datiFatturaRettificataType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiFatturaRettificataType.class, datiFatturaRettificataType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiFatturaRettificataType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param file Xml file to serialize the object <var>datiFatturaRettificataType</var>
	 * @param datiFatturaRettificataType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiFatturaRettificataType datiFatturaRettificataType) throws SerializerException {
		this.objToXml(file, DatiFatturaRettificataType.class, datiFatturaRettificataType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiFatturaRettificataType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param file Xml file to serialize the object <var>datiFatturaRettificataType</var>
	 * @param datiFatturaRettificataType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiFatturaRettificataType datiFatturaRettificataType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiFatturaRettificataType.class, datiFatturaRettificataType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiFatturaRettificataType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiFatturaRettificataType</var>
	 * @param datiFatturaRettificataType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiFatturaRettificataType datiFatturaRettificataType) throws SerializerException {
		this.objToXml(out, DatiFatturaRettificataType.class, datiFatturaRettificataType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiFatturaRettificataType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiFatturaRettificataType</var>
	 * @param datiFatturaRettificataType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiFatturaRettificataType datiFatturaRettificataType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiFatturaRettificataType.class, datiFatturaRettificataType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiFatturaRettificataType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param datiFatturaRettificataType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiFatturaRettificataType datiFatturaRettificataType) throws SerializerException {
		return this.objToXml(DatiFatturaRettificataType.class, datiFatturaRettificataType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiFatturaRettificataType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param datiFatturaRettificataType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiFatturaRettificataType datiFatturaRettificataType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiFatturaRettificataType.class, datiFatturaRettificataType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiFatturaRettificataType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param datiFatturaRettificataType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiFatturaRettificataType datiFatturaRettificataType) throws SerializerException {
		return this.objToXml(DatiFatturaRettificataType.class, datiFatturaRettificataType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiFatturaRettificataType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param datiFatturaRettificataType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiFatturaRettificataType datiFatturaRettificataType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiFatturaRettificataType.class, datiFatturaRettificataType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: FatturaElettronicaBodyType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaElettronicaBodyType</var>
	 * @param fatturaElettronicaBodyType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaElettronicaBodyType fatturaElettronicaBodyType) throws SerializerException {
		this.objToXml(fileName, FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaElettronicaBodyType</var>
	 * @param fatturaElettronicaBodyType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaElettronicaBodyType fatturaElettronicaBodyType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaElettronicaBodyType</var>
	 * @param fatturaElettronicaBodyType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaElettronicaBodyType fatturaElettronicaBodyType) throws SerializerException {
		this.objToXml(file, FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaElettronicaBodyType</var>
	 * @param fatturaElettronicaBodyType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaElettronicaBodyType fatturaElettronicaBodyType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaElettronicaBodyType</var>
	 * @param fatturaElettronicaBodyType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaElettronicaBodyType fatturaElettronicaBodyType) throws SerializerException {
		this.objToXml(out, FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaElettronicaBodyType</var>
	 * @param fatturaElettronicaBodyType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaElettronicaBodyType fatturaElettronicaBodyType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param fatturaElettronicaBodyType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaElettronicaBodyType fatturaElettronicaBodyType) throws SerializerException {
		return this.objToXml(FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param fatturaElettronicaBodyType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaElettronicaBodyType fatturaElettronicaBodyType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param fatturaElettronicaBodyType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaElettronicaBodyType fatturaElettronicaBodyType) throws SerializerException {
		return this.objToXml(FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param fatturaElettronicaBodyType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaElettronicaBodyType fatturaElettronicaBodyType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiBeniServiziType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiBeniServiziType</var>
	 * @param datiBeniServiziType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiBeniServiziType datiBeniServiziType) throws SerializerException {
		this.objToXml(fileName, DatiBeniServiziType.class, datiBeniServiziType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiBeniServiziType</var>
	 * @param datiBeniServiziType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiBeniServiziType datiBeniServiziType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiBeniServiziType.class, datiBeniServiziType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param file Xml file to serialize the object <var>datiBeniServiziType</var>
	 * @param datiBeniServiziType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiBeniServiziType datiBeniServiziType) throws SerializerException {
		this.objToXml(file, DatiBeniServiziType.class, datiBeniServiziType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param file Xml file to serialize the object <var>datiBeniServiziType</var>
	 * @param datiBeniServiziType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiBeniServiziType datiBeniServiziType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiBeniServiziType.class, datiBeniServiziType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiBeniServiziType</var>
	 * @param datiBeniServiziType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiBeniServiziType datiBeniServiziType) throws SerializerException {
		this.objToXml(out, DatiBeniServiziType.class, datiBeniServiziType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiBeniServiziType</var>
	 * @param datiBeniServiziType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiBeniServiziType datiBeniServiziType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiBeniServiziType.class, datiBeniServiziType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiBeniServiziType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param datiBeniServiziType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiBeniServiziType datiBeniServiziType) throws SerializerException {
		return this.objToXml(DatiBeniServiziType.class, datiBeniServiziType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiBeniServiziType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param datiBeniServiziType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiBeniServiziType datiBeniServiziType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiBeniServiziType.class, datiBeniServiziType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiBeniServiziType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param datiBeniServiziType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiBeniServiziType datiBeniServiziType) throws SerializerException {
		return this.objToXml(DatiBeniServiziType.class, datiBeniServiziType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiBeniServiziType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param datiBeniServiziType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiBeniServiziType datiBeniServiziType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiBeniServiziType.class, datiBeniServiziType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: FatturaElettronicaHeaderType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaElettronicaHeaderType</var>
	 * @param fatturaElettronicaHeaderType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaElettronicaHeaderType fatturaElettronicaHeaderType) throws SerializerException {
		this.objToXml(fileName, FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaElettronicaHeaderType</var>
	 * @param fatturaElettronicaHeaderType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaElettronicaHeaderType fatturaElettronicaHeaderType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaElettronicaHeaderType</var>
	 * @param fatturaElettronicaHeaderType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaElettronicaHeaderType fatturaElettronicaHeaderType) throws SerializerException {
		this.objToXml(file, FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaElettronicaHeaderType</var>
	 * @param fatturaElettronicaHeaderType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaElettronicaHeaderType fatturaElettronicaHeaderType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaElettronicaHeaderType</var>
	 * @param fatturaElettronicaHeaderType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaElettronicaHeaderType fatturaElettronicaHeaderType) throws SerializerException {
		this.objToXml(out, FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaElettronicaHeaderType</var>
	 * @param fatturaElettronicaHeaderType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaElettronicaHeaderType fatturaElettronicaHeaderType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param fatturaElettronicaHeaderType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaElettronicaHeaderType fatturaElettronicaHeaderType) throws SerializerException {
		return this.objToXml(FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param fatturaElettronicaHeaderType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaElettronicaHeaderType fatturaElettronicaHeaderType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param fatturaElettronicaHeaderType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaElettronicaHeaderType fatturaElettronicaHeaderType) throws SerializerException {
		return this.objToXml(FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param fatturaElettronicaHeaderType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaElettronicaHeaderType fatturaElettronicaHeaderType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: FatturaElettronicaType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaElettronicaType</var>
	 * @param fatturaElettronicaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaElettronicaType fatturaElettronicaType) throws SerializerException {
		this.objToXml(fileName, FatturaElettronicaType.class, fatturaElettronicaType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaElettronicaType</var>
	 * @param fatturaElettronicaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaElettronicaType fatturaElettronicaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FatturaElettronicaType.class, fatturaElettronicaType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaElettronicaType</var>
	 * @param fatturaElettronicaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaElettronicaType fatturaElettronicaType) throws SerializerException {
		this.objToXml(file, FatturaElettronicaType.class, fatturaElettronicaType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaElettronicaType</var>
	 * @param fatturaElettronicaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaElettronicaType fatturaElettronicaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FatturaElettronicaType.class, fatturaElettronicaType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaElettronicaType</var>
	 * @param fatturaElettronicaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaElettronicaType fatturaElettronicaType) throws SerializerException {
		this.objToXml(out, FatturaElettronicaType.class, fatturaElettronicaType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaElettronicaType</var>
	 * @param fatturaElettronicaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaElettronicaType fatturaElettronicaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FatturaElettronicaType.class, fatturaElettronicaType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fatturaElettronicaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param fatturaElettronicaType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaElettronicaType fatturaElettronicaType) throws SerializerException {
		return this.objToXml(FatturaElettronicaType.class, fatturaElettronicaType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fatturaElettronicaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param fatturaElettronicaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaElettronicaType fatturaElettronicaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FatturaElettronicaType.class, fatturaElettronicaType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fatturaElettronicaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param fatturaElettronicaType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaElettronicaType fatturaElettronicaType) throws SerializerException {
		return this.objToXml(FatturaElettronicaType.class, fatturaElettronicaType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fatturaElettronicaType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param fatturaElettronicaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaElettronicaType fatturaElettronicaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FatturaElettronicaType.class, fatturaElettronicaType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiTrasmissioneType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiTrasmissioneType</var>
	 * @param datiTrasmissioneType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiTrasmissioneType datiTrasmissioneType) throws SerializerException {
		this.objToXml(fileName, DatiTrasmissioneType.class, datiTrasmissioneType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiTrasmissioneType</var>
	 * @param datiTrasmissioneType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiTrasmissioneType datiTrasmissioneType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiTrasmissioneType.class, datiTrasmissioneType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param file Xml file to serialize the object <var>datiTrasmissioneType</var>
	 * @param datiTrasmissioneType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiTrasmissioneType datiTrasmissioneType) throws SerializerException {
		this.objToXml(file, DatiTrasmissioneType.class, datiTrasmissioneType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param file Xml file to serialize the object <var>datiTrasmissioneType</var>
	 * @param datiTrasmissioneType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiTrasmissioneType datiTrasmissioneType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiTrasmissioneType.class, datiTrasmissioneType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiTrasmissioneType</var>
	 * @param datiTrasmissioneType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiTrasmissioneType datiTrasmissioneType) throws SerializerException {
		this.objToXml(out, DatiTrasmissioneType.class, datiTrasmissioneType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiTrasmissioneType</var>
	 * @param datiTrasmissioneType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiTrasmissioneType datiTrasmissioneType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiTrasmissioneType.class, datiTrasmissioneType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiTrasmissioneType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param datiTrasmissioneType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiTrasmissioneType datiTrasmissioneType) throws SerializerException {
		return this.objToXml(DatiTrasmissioneType.class, datiTrasmissioneType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiTrasmissioneType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param datiTrasmissioneType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiTrasmissioneType datiTrasmissioneType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiTrasmissioneType.class, datiTrasmissioneType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiTrasmissioneType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param datiTrasmissioneType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiTrasmissioneType datiTrasmissioneType) throws SerializerException {
		return this.objToXml(DatiTrasmissioneType.class, datiTrasmissioneType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiTrasmissioneType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param datiTrasmissioneType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiTrasmissioneType datiTrasmissioneType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiTrasmissioneType.class, datiTrasmissioneType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: CedentePrestatoreType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param fileName Xml file to serialize the object <var>cedentePrestatoreType</var>
	 * @param cedentePrestatoreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CedentePrestatoreType cedentePrestatoreType) throws SerializerException {
		this.objToXml(fileName, CedentePrestatoreType.class, cedentePrestatoreType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param fileName Xml file to serialize the object <var>cedentePrestatoreType</var>
	 * @param cedentePrestatoreType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CedentePrestatoreType cedentePrestatoreType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CedentePrestatoreType.class, cedentePrestatoreType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param file Xml file to serialize the object <var>cedentePrestatoreType</var>
	 * @param cedentePrestatoreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CedentePrestatoreType cedentePrestatoreType) throws SerializerException {
		this.objToXml(file, CedentePrestatoreType.class, cedentePrestatoreType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param file Xml file to serialize the object <var>cedentePrestatoreType</var>
	 * @param cedentePrestatoreType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CedentePrestatoreType cedentePrestatoreType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CedentePrestatoreType.class, cedentePrestatoreType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param out OutputStream to serialize the object <var>cedentePrestatoreType</var>
	 * @param cedentePrestatoreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CedentePrestatoreType cedentePrestatoreType) throws SerializerException {
		this.objToXml(out, CedentePrestatoreType.class, cedentePrestatoreType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param out OutputStream to serialize the object <var>cedentePrestatoreType</var>
	 * @param cedentePrestatoreType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CedentePrestatoreType cedentePrestatoreType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CedentePrestatoreType.class, cedentePrestatoreType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>cedentePrestatoreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param cedentePrestatoreType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CedentePrestatoreType cedentePrestatoreType) throws SerializerException {
		return this.objToXml(CedentePrestatoreType.class, cedentePrestatoreType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>cedentePrestatoreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param cedentePrestatoreType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CedentePrestatoreType cedentePrestatoreType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CedentePrestatoreType.class, cedentePrestatoreType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>cedentePrestatoreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param cedentePrestatoreType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CedentePrestatoreType cedentePrestatoreType) throws SerializerException {
		return this.objToXml(CedentePrestatoreType.class, cedentePrestatoreType, false).toString();
	}
	/**
	 * Serialize to String the object <var>cedentePrestatoreType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param cedentePrestatoreType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CedentePrestatoreType cedentePrestatoreType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CedentePrestatoreType.class, cedentePrestatoreType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: CessionarioCommittenteType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>cessionarioCommittenteType</var>
	 * @param cessionarioCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CessionarioCommittenteType cessionarioCommittenteType) throws SerializerException {
		this.objToXml(fileName, CessionarioCommittenteType.class, cessionarioCommittenteType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>cessionarioCommittenteType</var>
	 * @param cessionarioCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CessionarioCommittenteType cessionarioCommittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CessionarioCommittenteType.class, cessionarioCommittenteType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>cessionarioCommittenteType</var>
	 * @param cessionarioCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CessionarioCommittenteType cessionarioCommittenteType) throws SerializerException {
		this.objToXml(file, CessionarioCommittenteType.class, cessionarioCommittenteType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>cessionarioCommittenteType</var>
	 * @param cessionarioCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CessionarioCommittenteType cessionarioCommittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CessionarioCommittenteType.class, cessionarioCommittenteType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>cessionarioCommittenteType</var>
	 * @param cessionarioCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CessionarioCommittenteType cessionarioCommittenteType) throws SerializerException {
		this.objToXml(out, CessionarioCommittenteType.class, cessionarioCommittenteType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>cessionarioCommittenteType</var>
	 * @param cessionarioCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CessionarioCommittenteType cessionarioCommittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CessionarioCommittenteType.class, cessionarioCommittenteType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>cessionarioCommittenteType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param cessionarioCommittenteType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CessionarioCommittenteType cessionarioCommittenteType) throws SerializerException {
		return this.objToXml(CessionarioCommittenteType.class, cessionarioCommittenteType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>cessionarioCommittenteType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param cessionarioCommittenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CessionarioCommittenteType cessionarioCommittenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CessionarioCommittenteType.class, cessionarioCommittenteType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>cessionarioCommittenteType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param cessionarioCommittenteType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CessionarioCommittenteType cessionarioCommittenteType) throws SerializerException {
		return this.objToXml(CessionarioCommittenteType.class, cessionarioCommittenteType, false).toString();
	}
	/**
	 * Serialize to String the object <var>cessionarioCommittenteType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param cessionarioCommittenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CessionarioCommittenteType cessionarioCommittenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CessionarioCommittenteType.class, cessionarioCommittenteType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IscrizioneREAType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param fileName Xml file to serialize the object <var>iscrizioneREAType</var>
	 * @param iscrizioneREAType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IscrizioneREAType iscrizioneREAType) throws SerializerException {
		this.objToXml(fileName, IscrizioneREAType.class, iscrizioneREAType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param fileName Xml file to serialize the object <var>iscrizioneREAType</var>
	 * @param iscrizioneREAType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IscrizioneREAType iscrizioneREAType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IscrizioneREAType.class, iscrizioneREAType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param file Xml file to serialize the object <var>iscrizioneREAType</var>
	 * @param iscrizioneREAType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IscrizioneREAType iscrizioneREAType) throws SerializerException {
		this.objToXml(file, IscrizioneREAType.class, iscrizioneREAType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param file Xml file to serialize the object <var>iscrizioneREAType</var>
	 * @param iscrizioneREAType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IscrizioneREAType iscrizioneREAType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IscrizioneREAType.class, iscrizioneREAType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param out OutputStream to serialize the object <var>iscrizioneREAType</var>
	 * @param iscrizioneREAType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IscrizioneREAType iscrizioneREAType) throws SerializerException {
		this.objToXml(out, IscrizioneREAType.class, iscrizioneREAType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param out OutputStream to serialize the object <var>iscrizioneREAType</var>
	 * @param iscrizioneREAType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IscrizioneREAType iscrizioneREAType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IscrizioneREAType.class, iscrizioneREAType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>iscrizioneREAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param iscrizioneREAType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IscrizioneREAType iscrizioneREAType) throws SerializerException {
		return this.objToXml(IscrizioneREAType.class, iscrizioneREAType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>iscrizioneREAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param iscrizioneREAType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IscrizioneREAType iscrizioneREAType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IscrizioneREAType.class, iscrizioneREAType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>iscrizioneREAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param iscrizioneREAType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IscrizioneREAType iscrizioneREAType) throws SerializerException {
		return this.objToXml(IscrizioneREAType.class, iscrizioneREAType, false).toString();
	}
	/**
	 * Serialize to String the object <var>iscrizioneREAType</var> of type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param iscrizioneREAType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IscrizioneREAType iscrizioneREAType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IscrizioneREAType.class, iscrizioneREAType, prettyPrint).toString();
	}
	
	
	

}
