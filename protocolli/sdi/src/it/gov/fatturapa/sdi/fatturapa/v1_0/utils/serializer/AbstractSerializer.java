/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package it.gov.fatturapa.sdi.fatturapa.v1_0.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType;

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
	 Object: CodiceArticoloType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>codiceArticoloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType}
	 * 
	 * @param fileName Xml file to serialize the object <var>codiceArticoloType</var>
	 * @param codiceArticoloType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CodiceArticoloType codiceArticoloType) throws SerializerException {
		this.objToXml(fileName, CodiceArticoloType.class, codiceArticoloType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>codiceArticoloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType}
	 * 
	 * @param fileName Xml file to serialize the object <var>codiceArticoloType</var>
	 * @param codiceArticoloType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CodiceArticoloType codiceArticoloType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CodiceArticoloType.class, codiceArticoloType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>codiceArticoloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType}
	 * 
	 * @param file Xml file to serialize the object <var>codiceArticoloType</var>
	 * @param codiceArticoloType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CodiceArticoloType codiceArticoloType) throws SerializerException {
		this.objToXml(file, CodiceArticoloType.class, codiceArticoloType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>codiceArticoloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType}
	 * 
	 * @param file Xml file to serialize the object <var>codiceArticoloType</var>
	 * @param codiceArticoloType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CodiceArticoloType codiceArticoloType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CodiceArticoloType.class, codiceArticoloType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>codiceArticoloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType}
	 * 
	 * @param out OutputStream to serialize the object <var>codiceArticoloType</var>
	 * @param codiceArticoloType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CodiceArticoloType codiceArticoloType) throws SerializerException {
		this.objToXml(out, CodiceArticoloType.class, codiceArticoloType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>codiceArticoloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType}
	 * 
	 * @param out OutputStream to serialize the object <var>codiceArticoloType</var>
	 * @param codiceArticoloType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CodiceArticoloType codiceArticoloType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CodiceArticoloType.class, codiceArticoloType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>codiceArticoloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType}
	 * 
	 * @param codiceArticoloType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CodiceArticoloType codiceArticoloType) throws SerializerException {
		return this.objToXml(CodiceArticoloType.class, codiceArticoloType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>codiceArticoloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType}
	 * 
	 * @param codiceArticoloType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CodiceArticoloType codiceArticoloType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CodiceArticoloType.class, codiceArticoloType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>codiceArticoloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType}
	 * 
	 * @param codiceArticoloType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CodiceArticoloType codiceArticoloType) throws SerializerException {
		return this.objToXml(CodiceArticoloType.class, codiceArticoloType, false).toString();
	}
	/**
	 * Serialize to String the object <var>codiceArticoloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CodiceArticoloType}
	 * 
	 * @param codiceArticoloType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CodiceArticoloType codiceArticoloType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CodiceArticoloType.class, codiceArticoloType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DettaglioLineeType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dettaglioLineeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType}
	 * 
	 * @param fileName Xml file to serialize the object <var>dettaglioLineeType</var>
	 * @param dettaglioLineeType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DettaglioLineeType dettaglioLineeType) throws SerializerException {
		this.objToXml(fileName, DettaglioLineeType.class, dettaglioLineeType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dettaglioLineeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType}
	 * 
	 * @param fileName Xml file to serialize the object <var>dettaglioLineeType</var>
	 * @param dettaglioLineeType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DettaglioLineeType dettaglioLineeType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DettaglioLineeType.class, dettaglioLineeType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dettaglioLineeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType}
	 * 
	 * @param file Xml file to serialize the object <var>dettaglioLineeType</var>
	 * @param dettaglioLineeType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DettaglioLineeType dettaglioLineeType) throws SerializerException {
		this.objToXml(file, DettaglioLineeType.class, dettaglioLineeType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dettaglioLineeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType}
	 * 
	 * @param file Xml file to serialize the object <var>dettaglioLineeType</var>
	 * @param dettaglioLineeType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DettaglioLineeType dettaglioLineeType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DettaglioLineeType.class, dettaglioLineeType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dettaglioLineeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType}
	 * 
	 * @param out OutputStream to serialize the object <var>dettaglioLineeType</var>
	 * @param dettaglioLineeType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DettaglioLineeType dettaglioLineeType) throws SerializerException {
		this.objToXml(out, DettaglioLineeType.class, dettaglioLineeType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dettaglioLineeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType}
	 * 
	 * @param out OutputStream to serialize the object <var>dettaglioLineeType</var>
	 * @param dettaglioLineeType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DettaglioLineeType dettaglioLineeType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DettaglioLineeType.class, dettaglioLineeType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dettaglioLineeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType}
	 * 
	 * @param dettaglioLineeType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DettaglioLineeType dettaglioLineeType) throws SerializerException {
		return this.objToXml(DettaglioLineeType.class, dettaglioLineeType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dettaglioLineeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType}
	 * 
	 * @param dettaglioLineeType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DettaglioLineeType dettaglioLineeType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DettaglioLineeType.class, dettaglioLineeType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dettaglioLineeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType}
	 * 
	 * @param dettaglioLineeType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DettaglioLineeType dettaglioLineeType) throws SerializerException {
		return this.objToXml(DettaglioLineeType.class, dettaglioLineeType, false).toString();
	}
	/**
	 * Serialize to String the object <var>dettaglioLineeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioLineeType}
	 * 
	 * @param dettaglioLineeType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DettaglioLineeType dettaglioLineeType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DettaglioLineeType.class, dettaglioLineeType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ScontoMaggiorazioneType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>scontoMaggiorazioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType}
	 * 
	 * @param fileName Xml file to serialize the object <var>scontoMaggiorazioneType</var>
	 * @param scontoMaggiorazioneType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ScontoMaggiorazioneType scontoMaggiorazioneType) throws SerializerException {
		this.objToXml(fileName, ScontoMaggiorazioneType.class, scontoMaggiorazioneType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>scontoMaggiorazioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType}
	 * 
	 * @param fileName Xml file to serialize the object <var>scontoMaggiorazioneType</var>
	 * @param scontoMaggiorazioneType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ScontoMaggiorazioneType scontoMaggiorazioneType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ScontoMaggiorazioneType.class, scontoMaggiorazioneType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>scontoMaggiorazioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType}
	 * 
	 * @param file Xml file to serialize the object <var>scontoMaggiorazioneType</var>
	 * @param scontoMaggiorazioneType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ScontoMaggiorazioneType scontoMaggiorazioneType) throws SerializerException {
		this.objToXml(file, ScontoMaggiorazioneType.class, scontoMaggiorazioneType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>scontoMaggiorazioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType}
	 * 
	 * @param file Xml file to serialize the object <var>scontoMaggiorazioneType</var>
	 * @param scontoMaggiorazioneType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ScontoMaggiorazioneType scontoMaggiorazioneType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ScontoMaggiorazioneType.class, scontoMaggiorazioneType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>scontoMaggiorazioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType}
	 * 
	 * @param out OutputStream to serialize the object <var>scontoMaggiorazioneType</var>
	 * @param scontoMaggiorazioneType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ScontoMaggiorazioneType scontoMaggiorazioneType) throws SerializerException {
		this.objToXml(out, ScontoMaggiorazioneType.class, scontoMaggiorazioneType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>scontoMaggiorazioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType}
	 * 
	 * @param out OutputStream to serialize the object <var>scontoMaggiorazioneType</var>
	 * @param scontoMaggiorazioneType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ScontoMaggiorazioneType scontoMaggiorazioneType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ScontoMaggiorazioneType.class, scontoMaggiorazioneType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>scontoMaggiorazioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType}
	 * 
	 * @param scontoMaggiorazioneType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ScontoMaggiorazioneType scontoMaggiorazioneType) throws SerializerException {
		return this.objToXml(ScontoMaggiorazioneType.class, scontoMaggiorazioneType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>scontoMaggiorazioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType}
	 * 
	 * @param scontoMaggiorazioneType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ScontoMaggiorazioneType scontoMaggiorazioneType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ScontoMaggiorazioneType.class, scontoMaggiorazioneType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>scontoMaggiorazioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType}
	 * 
	 * @param scontoMaggiorazioneType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ScontoMaggiorazioneType scontoMaggiorazioneType) throws SerializerException {
		return this.objToXml(ScontoMaggiorazioneType.class, scontoMaggiorazioneType, false).toString();
	}
	/**
	 * Serialize to String the object <var>scontoMaggiorazioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ScontoMaggiorazioneType}
	 * 
	 * @param scontoMaggiorazioneType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ScontoMaggiorazioneType scontoMaggiorazioneType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ScontoMaggiorazioneType.class, scontoMaggiorazioneType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: AltriDatiGestionaliType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>altriDatiGestionaliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType}
	 * 
	 * @param fileName Xml file to serialize the object <var>altriDatiGestionaliType</var>
	 * @param altriDatiGestionaliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AltriDatiGestionaliType altriDatiGestionaliType) throws SerializerException {
		this.objToXml(fileName, AltriDatiGestionaliType.class, altriDatiGestionaliType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>altriDatiGestionaliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType}
	 * 
	 * @param fileName Xml file to serialize the object <var>altriDatiGestionaliType</var>
	 * @param altriDatiGestionaliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AltriDatiGestionaliType altriDatiGestionaliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AltriDatiGestionaliType.class, altriDatiGestionaliType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>altriDatiGestionaliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType}
	 * 
	 * @param file Xml file to serialize the object <var>altriDatiGestionaliType</var>
	 * @param altriDatiGestionaliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AltriDatiGestionaliType altriDatiGestionaliType) throws SerializerException {
		this.objToXml(file, AltriDatiGestionaliType.class, altriDatiGestionaliType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>altriDatiGestionaliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType}
	 * 
	 * @param file Xml file to serialize the object <var>altriDatiGestionaliType</var>
	 * @param altriDatiGestionaliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AltriDatiGestionaliType altriDatiGestionaliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AltriDatiGestionaliType.class, altriDatiGestionaliType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>altriDatiGestionaliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType}
	 * 
	 * @param out OutputStream to serialize the object <var>altriDatiGestionaliType</var>
	 * @param altriDatiGestionaliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AltriDatiGestionaliType altriDatiGestionaliType) throws SerializerException {
		this.objToXml(out, AltriDatiGestionaliType.class, altriDatiGestionaliType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>altriDatiGestionaliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType}
	 * 
	 * @param out OutputStream to serialize the object <var>altriDatiGestionaliType</var>
	 * @param altriDatiGestionaliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AltriDatiGestionaliType altriDatiGestionaliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AltriDatiGestionaliType.class, altriDatiGestionaliType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>altriDatiGestionaliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType}
	 * 
	 * @param altriDatiGestionaliType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AltriDatiGestionaliType altriDatiGestionaliType) throws SerializerException {
		return this.objToXml(AltriDatiGestionaliType.class, altriDatiGestionaliType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>altriDatiGestionaliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType}
	 * 
	 * @param altriDatiGestionaliType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AltriDatiGestionaliType altriDatiGestionaliType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AltriDatiGestionaliType.class, altriDatiGestionaliType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>altriDatiGestionaliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType}
	 * 
	 * @param altriDatiGestionaliType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AltriDatiGestionaliType altriDatiGestionaliType) throws SerializerException {
		return this.objToXml(AltriDatiGestionaliType.class, altriDatiGestionaliType, false).toString();
	}
	/**
	 * Serialize to String the object <var>altriDatiGestionaliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AltriDatiGestionaliType}
	 * 
	 * @param altriDatiGestionaliType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AltriDatiGestionaliType altriDatiGestionaliType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AltriDatiGestionaliType.class, altriDatiGestionaliType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiDocumentiCorrelatiType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiDocumentiCorrelatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiDocumentiCorrelatiType</var>
	 * @param datiDocumentiCorrelatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiDocumentiCorrelatiType datiDocumentiCorrelatiType) throws SerializerException {
		this.objToXml(fileName, DatiDocumentiCorrelatiType.class, datiDocumentiCorrelatiType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiDocumentiCorrelatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiDocumentiCorrelatiType</var>
	 * @param datiDocumentiCorrelatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiDocumentiCorrelatiType datiDocumentiCorrelatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiDocumentiCorrelatiType.class, datiDocumentiCorrelatiType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiDocumentiCorrelatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType}
	 * 
	 * @param file Xml file to serialize the object <var>datiDocumentiCorrelatiType</var>
	 * @param datiDocumentiCorrelatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiDocumentiCorrelatiType datiDocumentiCorrelatiType) throws SerializerException {
		this.objToXml(file, DatiDocumentiCorrelatiType.class, datiDocumentiCorrelatiType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiDocumentiCorrelatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType}
	 * 
	 * @param file Xml file to serialize the object <var>datiDocumentiCorrelatiType</var>
	 * @param datiDocumentiCorrelatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiDocumentiCorrelatiType datiDocumentiCorrelatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiDocumentiCorrelatiType.class, datiDocumentiCorrelatiType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiDocumentiCorrelatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiDocumentiCorrelatiType</var>
	 * @param datiDocumentiCorrelatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiDocumentiCorrelatiType datiDocumentiCorrelatiType) throws SerializerException {
		this.objToXml(out, DatiDocumentiCorrelatiType.class, datiDocumentiCorrelatiType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiDocumentiCorrelatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiDocumentiCorrelatiType</var>
	 * @param datiDocumentiCorrelatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiDocumentiCorrelatiType datiDocumentiCorrelatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiDocumentiCorrelatiType.class, datiDocumentiCorrelatiType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiDocumentiCorrelatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType}
	 * 
	 * @param datiDocumentiCorrelatiType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiDocumentiCorrelatiType datiDocumentiCorrelatiType) throws SerializerException {
		return this.objToXml(DatiDocumentiCorrelatiType.class, datiDocumentiCorrelatiType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiDocumentiCorrelatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType}
	 * 
	 * @param datiDocumentiCorrelatiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiDocumentiCorrelatiType datiDocumentiCorrelatiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiDocumentiCorrelatiType.class, datiDocumentiCorrelatiType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiDocumentiCorrelatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType}
	 * 
	 * @param datiDocumentiCorrelatiType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiDocumentiCorrelatiType datiDocumentiCorrelatiType) throws SerializerException {
		return this.objToXml(DatiDocumentiCorrelatiType.class, datiDocumentiCorrelatiType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiDocumentiCorrelatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType}
	 * 
	 * @param datiDocumentiCorrelatiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiDocumentiCorrelatiType datiDocumentiCorrelatiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiDocumentiCorrelatiType.class, datiDocumentiCorrelatiType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiRitenutaType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiRitenutaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiRitenutaType</var>
	 * @param datiRitenutaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiRitenutaType datiRitenutaType) throws SerializerException {
		this.objToXml(fileName, DatiRitenutaType.class, datiRitenutaType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiRitenutaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiRitenutaType</var>
	 * @param datiRitenutaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiRitenutaType datiRitenutaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiRitenutaType.class, datiRitenutaType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiRitenutaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType}
	 * 
	 * @param file Xml file to serialize the object <var>datiRitenutaType</var>
	 * @param datiRitenutaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiRitenutaType datiRitenutaType) throws SerializerException {
		this.objToXml(file, DatiRitenutaType.class, datiRitenutaType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiRitenutaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType}
	 * 
	 * @param file Xml file to serialize the object <var>datiRitenutaType</var>
	 * @param datiRitenutaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiRitenutaType datiRitenutaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiRitenutaType.class, datiRitenutaType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiRitenutaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiRitenutaType</var>
	 * @param datiRitenutaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiRitenutaType datiRitenutaType) throws SerializerException {
		this.objToXml(out, DatiRitenutaType.class, datiRitenutaType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiRitenutaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiRitenutaType</var>
	 * @param datiRitenutaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiRitenutaType datiRitenutaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiRitenutaType.class, datiRitenutaType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiRitenutaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType}
	 * 
	 * @param datiRitenutaType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiRitenutaType datiRitenutaType) throws SerializerException {
		return this.objToXml(DatiRitenutaType.class, datiRitenutaType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiRitenutaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType}
	 * 
	 * @param datiRitenutaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiRitenutaType datiRitenutaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiRitenutaType.class, datiRitenutaType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiRitenutaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType}
	 * 
	 * @param datiRitenutaType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiRitenutaType datiRitenutaType) throws SerializerException {
		return this.objToXml(DatiRitenutaType.class, datiRitenutaType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiRitenutaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRitenutaType}
	 * 
	 * @param datiRitenutaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiRitenutaType datiRitenutaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiRitenutaType.class, datiRitenutaType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiAnagraficiTerzoIntermediarioType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiAnagraficiTerzoIntermediarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiAnagraficiTerzoIntermediarioType</var>
	 * @param datiAnagraficiTerzoIntermediarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiAnagraficiTerzoIntermediarioType datiAnagraficiTerzoIntermediarioType) throws SerializerException {
		this.objToXml(fileName, DatiAnagraficiTerzoIntermediarioType.class, datiAnagraficiTerzoIntermediarioType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiAnagraficiTerzoIntermediarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiAnagraficiTerzoIntermediarioType</var>
	 * @param datiAnagraficiTerzoIntermediarioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiAnagraficiTerzoIntermediarioType datiAnagraficiTerzoIntermediarioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiAnagraficiTerzoIntermediarioType.class, datiAnagraficiTerzoIntermediarioType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiAnagraficiTerzoIntermediarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param file Xml file to serialize the object <var>datiAnagraficiTerzoIntermediarioType</var>
	 * @param datiAnagraficiTerzoIntermediarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiAnagraficiTerzoIntermediarioType datiAnagraficiTerzoIntermediarioType) throws SerializerException {
		this.objToXml(file, DatiAnagraficiTerzoIntermediarioType.class, datiAnagraficiTerzoIntermediarioType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiAnagraficiTerzoIntermediarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param file Xml file to serialize the object <var>datiAnagraficiTerzoIntermediarioType</var>
	 * @param datiAnagraficiTerzoIntermediarioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiAnagraficiTerzoIntermediarioType datiAnagraficiTerzoIntermediarioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiAnagraficiTerzoIntermediarioType.class, datiAnagraficiTerzoIntermediarioType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiAnagraficiTerzoIntermediarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiAnagraficiTerzoIntermediarioType</var>
	 * @param datiAnagraficiTerzoIntermediarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiAnagraficiTerzoIntermediarioType datiAnagraficiTerzoIntermediarioType) throws SerializerException {
		this.objToXml(out, DatiAnagraficiTerzoIntermediarioType.class, datiAnagraficiTerzoIntermediarioType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiAnagraficiTerzoIntermediarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiAnagraficiTerzoIntermediarioType</var>
	 * @param datiAnagraficiTerzoIntermediarioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiAnagraficiTerzoIntermediarioType datiAnagraficiTerzoIntermediarioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiAnagraficiTerzoIntermediarioType.class, datiAnagraficiTerzoIntermediarioType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiAnagraficiTerzoIntermediarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param datiAnagraficiTerzoIntermediarioType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiAnagraficiTerzoIntermediarioType datiAnagraficiTerzoIntermediarioType) throws SerializerException {
		return this.objToXml(DatiAnagraficiTerzoIntermediarioType.class, datiAnagraficiTerzoIntermediarioType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiAnagraficiTerzoIntermediarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param datiAnagraficiTerzoIntermediarioType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiAnagraficiTerzoIntermediarioType datiAnagraficiTerzoIntermediarioType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiAnagraficiTerzoIntermediarioType.class, datiAnagraficiTerzoIntermediarioType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiAnagraficiTerzoIntermediarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param datiAnagraficiTerzoIntermediarioType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiAnagraficiTerzoIntermediarioType datiAnagraficiTerzoIntermediarioType) throws SerializerException {
		return this.objToXml(DatiAnagraficiTerzoIntermediarioType.class, datiAnagraficiTerzoIntermediarioType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiAnagraficiTerzoIntermediarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param datiAnagraficiTerzoIntermediarioType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiAnagraficiTerzoIntermediarioType datiAnagraficiTerzoIntermediarioType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiAnagraficiTerzoIntermediarioType.class, datiAnagraficiTerzoIntermediarioType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: TerzoIntermediarioSoggettoEmittenteType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>terzoIntermediarioSoggettoEmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>terzoIntermediarioSoggettoEmittenteType</var>
	 * @param terzoIntermediarioSoggettoEmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioSoggettoEmittenteType) throws SerializerException {
		this.objToXml(fileName, TerzoIntermediarioSoggettoEmittenteType.class, terzoIntermediarioSoggettoEmittenteType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>terzoIntermediarioSoggettoEmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>terzoIntermediarioSoggettoEmittenteType</var>
	 * @param terzoIntermediarioSoggettoEmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioSoggettoEmittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TerzoIntermediarioSoggettoEmittenteType.class, terzoIntermediarioSoggettoEmittenteType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>terzoIntermediarioSoggettoEmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>terzoIntermediarioSoggettoEmittenteType</var>
	 * @param terzoIntermediarioSoggettoEmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioSoggettoEmittenteType) throws SerializerException {
		this.objToXml(file, TerzoIntermediarioSoggettoEmittenteType.class, terzoIntermediarioSoggettoEmittenteType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>terzoIntermediarioSoggettoEmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>terzoIntermediarioSoggettoEmittenteType</var>
	 * @param terzoIntermediarioSoggettoEmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioSoggettoEmittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TerzoIntermediarioSoggettoEmittenteType.class, terzoIntermediarioSoggettoEmittenteType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>terzoIntermediarioSoggettoEmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>terzoIntermediarioSoggettoEmittenteType</var>
	 * @param terzoIntermediarioSoggettoEmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioSoggettoEmittenteType) throws SerializerException {
		this.objToXml(out, TerzoIntermediarioSoggettoEmittenteType.class, terzoIntermediarioSoggettoEmittenteType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>terzoIntermediarioSoggettoEmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>terzoIntermediarioSoggettoEmittenteType</var>
	 * @param terzoIntermediarioSoggettoEmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioSoggettoEmittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TerzoIntermediarioSoggettoEmittenteType.class, terzoIntermediarioSoggettoEmittenteType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>terzoIntermediarioSoggettoEmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param terzoIntermediarioSoggettoEmittenteType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioSoggettoEmittenteType) throws SerializerException {
		return this.objToXml(TerzoIntermediarioSoggettoEmittenteType.class, terzoIntermediarioSoggettoEmittenteType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>terzoIntermediarioSoggettoEmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param terzoIntermediarioSoggettoEmittenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioSoggettoEmittenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TerzoIntermediarioSoggettoEmittenteType.class, terzoIntermediarioSoggettoEmittenteType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>terzoIntermediarioSoggettoEmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param terzoIntermediarioSoggettoEmittenteType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioSoggettoEmittenteType) throws SerializerException {
		return this.objToXml(TerzoIntermediarioSoggettoEmittenteType.class, terzoIntermediarioSoggettoEmittenteType, false).toString();
	}
	/**
	 * Serialize to String the object <var>terzoIntermediarioSoggettoEmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param terzoIntermediarioSoggettoEmittenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TerzoIntermediarioSoggettoEmittenteType terzoIntermediarioSoggettoEmittenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TerzoIntermediarioSoggettoEmittenteType.class, terzoIntermediarioSoggettoEmittenteType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DettaglioPagamentoType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dettaglioPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>dettaglioPagamentoType</var>
	 * @param dettaglioPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DettaglioPagamentoType dettaglioPagamentoType) throws SerializerException {
		this.objToXml(fileName, DettaglioPagamentoType.class, dettaglioPagamentoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dettaglioPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>dettaglioPagamentoType</var>
	 * @param dettaglioPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DettaglioPagamentoType dettaglioPagamentoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DettaglioPagamentoType.class, dettaglioPagamentoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dettaglioPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType}
	 * 
	 * @param file Xml file to serialize the object <var>dettaglioPagamentoType</var>
	 * @param dettaglioPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DettaglioPagamentoType dettaglioPagamentoType) throws SerializerException {
		this.objToXml(file, DettaglioPagamentoType.class, dettaglioPagamentoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dettaglioPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType}
	 * 
	 * @param file Xml file to serialize the object <var>dettaglioPagamentoType</var>
	 * @param dettaglioPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DettaglioPagamentoType dettaglioPagamentoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DettaglioPagamentoType.class, dettaglioPagamentoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dettaglioPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType}
	 * 
	 * @param out OutputStream to serialize the object <var>dettaglioPagamentoType</var>
	 * @param dettaglioPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DettaglioPagamentoType dettaglioPagamentoType) throws SerializerException {
		this.objToXml(out, DettaglioPagamentoType.class, dettaglioPagamentoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dettaglioPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType}
	 * 
	 * @param out OutputStream to serialize the object <var>dettaglioPagamentoType</var>
	 * @param dettaglioPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DettaglioPagamentoType dettaglioPagamentoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DettaglioPagamentoType.class, dettaglioPagamentoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dettaglioPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType}
	 * 
	 * @param dettaglioPagamentoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DettaglioPagamentoType dettaglioPagamentoType) throws SerializerException {
		return this.objToXml(DettaglioPagamentoType.class, dettaglioPagamentoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dettaglioPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType}
	 * 
	 * @param dettaglioPagamentoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DettaglioPagamentoType dettaglioPagamentoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DettaglioPagamentoType.class, dettaglioPagamentoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dettaglioPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType}
	 * 
	 * @param dettaglioPagamentoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DettaglioPagamentoType dettaglioPagamentoType) throws SerializerException {
		return this.objToXml(DettaglioPagamentoType.class, dettaglioPagamentoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>dettaglioPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType}
	 * 
	 * @param dettaglioPagamentoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DettaglioPagamentoType dettaglioPagamentoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DettaglioPagamentoType.class, dettaglioPagamentoType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IdFiscaleType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType}
	 * 
	 * @param fileName Xml file to serialize the object <var>idFiscaleType</var>
	 * @param idFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdFiscaleType idFiscaleType) throws SerializerException {
		this.objToXml(fileName, IdFiscaleType.class, idFiscaleType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType}
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
	 * Serialize to file system in <var>file</var> the object <var>idFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType}
	 * 
	 * @param file Xml file to serialize the object <var>idFiscaleType</var>
	 * @param idFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdFiscaleType idFiscaleType) throws SerializerException {
		this.objToXml(file, IdFiscaleType.class, idFiscaleType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType}
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
	 * Serialize to output stream <var>out</var> the object <var>idFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType}
	 * 
	 * @param out OutputStream to serialize the object <var>idFiscaleType</var>
	 * @param idFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdFiscaleType idFiscaleType) throws SerializerException {
		this.objToXml(out, IdFiscaleType.class, idFiscaleType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType}
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
	 * Serialize to byte array the object <var>idFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType}
	 * 
	 * @param idFiscaleType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdFiscaleType idFiscaleType) throws SerializerException {
		return this.objToXml(IdFiscaleType.class, idFiscaleType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType}
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
	 * Serialize to String the object <var>idFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType}
	 * 
	 * @param idFiscaleType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdFiscaleType idFiscaleType) throws SerializerException {
		return this.objToXml(IdFiscaleType.class, idFiscaleType, false).toString();
	}
	/**
	 * Serialize to String the object <var>idFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IdFiscaleType}
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
	 Object: DatiAnagraficiRappresentanteType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiAnagraficiRappresentanteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiAnagraficiRappresentanteType</var>
	 * @param datiAnagraficiRappresentanteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiAnagraficiRappresentanteType datiAnagraficiRappresentanteType) throws SerializerException {
		this.objToXml(fileName, DatiAnagraficiRappresentanteType.class, datiAnagraficiRappresentanteType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiAnagraficiRappresentanteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiAnagraficiRappresentanteType</var>
	 * @param datiAnagraficiRappresentanteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiAnagraficiRappresentanteType datiAnagraficiRappresentanteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiAnagraficiRappresentanteType.class, datiAnagraficiRappresentanteType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiAnagraficiRappresentanteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType}
	 * 
	 * @param file Xml file to serialize the object <var>datiAnagraficiRappresentanteType</var>
	 * @param datiAnagraficiRappresentanteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiAnagraficiRappresentanteType datiAnagraficiRappresentanteType) throws SerializerException {
		this.objToXml(file, DatiAnagraficiRappresentanteType.class, datiAnagraficiRappresentanteType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiAnagraficiRappresentanteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType}
	 * 
	 * @param file Xml file to serialize the object <var>datiAnagraficiRappresentanteType</var>
	 * @param datiAnagraficiRappresentanteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiAnagraficiRappresentanteType datiAnagraficiRappresentanteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiAnagraficiRappresentanteType.class, datiAnagraficiRappresentanteType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiAnagraficiRappresentanteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiAnagraficiRappresentanteType</var>
	 * @param datiAnagraficiRappresentanteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiAnagraficiRappresentanteType datiAnagraficiRappresentanteType) throws SerializerException {
		this.objToXml(out, DatiAnagraficiRappresentanteType.class, datiAnagraficiRappresentanteType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiAnagraficiRappresentanteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiAnagraficiRappresentanteType</var>
	 * @param datiAnagraficiRappresentanteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiAnagraficiRappresentanteType datiAnagraficiRappresentanteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiAnagraficiRappresentanteType.class, datiAnagraficiRappresentanteType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiAnagraficiRappresentanteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType}
	 * 
	 * @param datiAnagraficiRappresentanteType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiAnagraficiRappresentanteType datiAnagraficiRappresentanteType) throws SerializerException {
		return this.objToXml(DatiAnagraficiRappresentanteType.class, datiAnagraficiRappresentanteType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiAnagraficiRappresentanteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType}
	 * 
	 * @param datiAnagraficiRappresentanteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiAnagraficiRappresentanteType datiAnagraficiRappresentanteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiAnagraficiRappresentanteType.class, datiAnagraficiRappresentanteType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiAnagraficiRappresentanteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType}
	 * 
	 * @param datiAnagraficiRappresentanteType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiAnagraficiRappresentanteType datiAnagraficiRappresentanteType) throws SerializerException {
		return this.objToXml(DatiAnagraficiRappresentanteType.class, datiAnagraficiRappresentanteType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiAnagraficiRappresentanteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiRappresentanteType}
	 * 
	 * @param datiAnagraficiRappresentanteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiAnagraficiRappresentanteType datiAnagraficiRappresentanteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiAnagraficiRappresentanteType.class, datiAnagraficiRappresentanteType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: AnagraficaType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>anagraficaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>anagraficaType</var>
	 * @param anagraficaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AnagraficaType anagraficaType) throws SerializerException {
		this.objToXml(fileName, AnagraficaType.class, anagraficaType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>anagraficaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>anagraficaType</var>
	 * @param anagraficaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AnagraficaType anagraficaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AnagraficaType.class, anagraficaType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>anagraficaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType}
	 * 
	 * @param file Xml file to serialize the object <var>anagraficaType</var>
	 * @param anagraficaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AnagraficaType anagraficaType) throws SerializerException {
		this.objToXml(file, AnagraficaType.class, anagraficaType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>anagraficaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType}
	 * 
	 * @param file Xml file to serialize the object <var>anagraficaType</var>
	 * @param anagraficaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AnagraficaType anagraficaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AnagraficaType.class, anagraficaType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>anagraficaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType}
	 * 
	 * @param out OutputStream to serialize the object <var>anagraficaType</var>
	 * @param anagraficaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AnagraficaType anagraficaType) throws SerializerException {
		this.objToXml(out, AnagraficaType.class, anagraficaType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>anagraficaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType}
	 * 
	 * @param out OutputStream to serialize the object <var>anagraficaType</var>
	 * @param anagraficaType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AnagraficaType anagraficaType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AnagraficaType.class, anagraficaType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>anagraficaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType}
	 * 
	 * @param anagraficaType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AnagraficaType anagraficaType) throws SerializerException {
		return this.objToXml(AnagraficaType.class, anagraficaType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>anagraficaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType}
	 * 
	 * @param anagraficaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AnagraficaType anagraficaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AnagraficaType.class, anagraficaType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>anagraficaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType}
	 * 
	 * @param anagraficaType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AnagraficaType anagraficaType) throws SerializerException {
		return this.objToXml(AnagraficaType.class, anagraficaType, false).toString();
	}
	/**
	 * Serialize to String the object <var>anagraficaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AnagraficaType}
	 * 
	 * @param anagraficaType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AnagraficaType anagraficaType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AnagraficaType.class, anagraficaType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiAnagraficiCedenteType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiAnagraficiCedenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiAnagraficiCedenteType</var>
	 * @param datiAnagraficiCedenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiAnagraficiCedenteType datiAnagraficiCedenteType) throws SerializerException {
		this.objToXml(fileName, DatiAnagraficiCedenteType.class, datiAnagraficiCedenteType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiAnagraficiCedenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiAnagraficiCedenteType</var>
	 * @param datiAnagraficiCedenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiAnagraficiCedenteType datiAnagraficiCedenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiAnagraficiCedenteType.class, datiAnagraficiCedenteType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiAnagraficiCedenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType}
	 * 
	 * @param file Xml file to serialize the object <var>datiAnagraficiCedenteType</var>
	 * @param datiAnagraficiCedenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiAnagraficiCedenteType datiAnagraficiCedenteType) throws SerializerException {
		this.objToXml(file, DatiAnagraficiCedenteType.class, datiAnagraficiCedenteType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiAnagraficiCedenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType}
	 * 
	 * @param file Xml file to serialize the object <var>datiAnagraficiCedenteType</var>
	 * @param datiAnagraficiCedenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiAnagraficiCedenteType datiAnagraficiCedenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiAnagraficiCedenteType.class, datiAnagraficiCedenteType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiAnagraficiCedenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiAnagraficiCedenteType</var>
	 * @param datiAnagraficiCedenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiAnagraficiCedenteType datiAnagraficiCedenteType) throws SerializerException {
		this.objToXml(out, DatiAnagraficiCedenteType.class, datiAnagraficiCedenteType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiAnagraficiCedenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiAnagraficiCedenteType</var>
	 * @param datiAnagraficiCedenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiAnagraficiCedenteType datiAnagraficiCedenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiAnagraficiCedenteType.class, datiAnagraficiCedenteType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiAnagraficiCedenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType}
	 * 
	 * @param datiAnagraficiCedenteType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiAnagraficiCedenteType datiAnagraficiCedenteType) throws SerializerException {
		return this.objToXml(DatiAnagraficiCedenteType.class, datiAnagraficiCedenteType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiAnagraficiCedenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType}
	 * 
	 * @param datiAnagraficiCedenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiAnagraficiCedenteType datiAnagraficiCedenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiAnagraficiCedenteType.class, datiAnagraficiCedenteType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiAnagraficiCedenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType}
	 * 
	 * @param datiAnagraficiCedenteType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiAnagraficiCedenteType datiAnagraficiCedenteType) throws SerializerException {
		return this.objToXml(DatiAnagraficiCedenteType.class, datiAnagraficiCedenteType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiAnagraficiCedenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCedenteType}
	 * 
	 * @param datiAnagraficiCedenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiAnagraficiCedenteType datiAnagraficiCedenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiAnagraficiCedenteType.class, datiAnagraficiCedenteType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiBeniServiziType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiBeniServiziType</var>
	 * @param datiBeniServiziType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiBeniServiziType datiBeniServiziType) throws SerializerException {
		this.objToXml(fileName, DatiBeniServiziType.class, datiBeniServiziType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType}
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
	 * Serialize to file system in <var>file</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType}
	 * 
	 * @param file Xml file to serialize the object <var>datiBeniServiziType</var>
	 * @param datiBeniServiziType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiBeniServiziType datiBeniServiziType) throws SerializerException {
		this.objToXml(file, DatiBeniServiziType.class, datiBeniServiziType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType}
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
	 * Serialize to output stream <var>out</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiBeniServiziType</var>
	 * @param datiBeniServiziType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiBeniServiziType datiBeniServiziType) throws SerializerException {
		this.objToXml(out, DatiBeniServiziType.class, datiBeniServiziType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiBeniServiziType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType}
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
	 * Serialize to byte array the object <var>datiBeniServiziType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType}
	 * 
	 * @param datiBeniServiziType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiBeniServiziType datiBeniServiziType) throws SerializerException {
		return this.objToXml(DatiBeniServiziType.class, datiBeniServiziType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiBeniServiziType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType}
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
	 * Serialize to String the object <var>datiBeniServiziType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType}
	 * 
	 * @param datiBeniServiziType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiBeniServiziType datiBeniServiziType) throws SerializerException {
		return this.objToXml(DatiBeniServiziType.class, datiBeniServiziType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiBeniServiziType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBeniServiziType}
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
	 Object: DatiRiepilogoType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiRiepilogoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiRiepilogoType</var>
	 * @param datiRiepilogoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiRiepilogoType datiRiepilogoType) throws SerializerException {
		this.objToXml(fileName, DatiRiepilogoType.class, datiRiepilogoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiRiepilogoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiRiepilogoType</var>
	 * @param datiRiepilogoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiRiepilogoType datiRiepilogoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiRiepilogoType.class, datiRiepilogoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiRiepilogoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType}
	 * 
	 * @param file Xml file to serialize the object <var>datiRiepilogoType</var>
	 * @param datiRiepilogoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiRiepilogoType datiRiepilogoType) throws SerializerException {
		this.objToXml(file, DatiRiepilogoType.class, datiRiepilogoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiRiepilogoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType}
	 * 
	 * @param file Xml file to serialize the object <var>datiRiepilogoType</var>
	 * @param datiRiepilogoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiRiepilogoType datiRiepilogoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiRiepilogoType.class, datiRiepilogoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiRiepilogoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiRiepilogoType</var>
	 * @param datiRiepilogoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiRiepilogoType datiRiepilogoType) throws SerializerException {
		this.objToXml(out, DatiRiepilogoType.class, datiRiepilogoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiRiepilogoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiRiepilogoType</var>
	 * @param datiRiepilogoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiRiepilogoType datiRiepilogoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiRiepilogoType.class, datiRiepilogoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiRiepilogoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType}
	 * 
	 * @param datiRiepilogoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiRiepilogoType datiRiepilogoType) throws SerializerException {
		return this.objToXml(DatiRiepilogoType.class, datiRiepilogoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiRiepilogoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType}
	 * 
	 * @param datiRiepilogoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiRiepilogoType datiRiepilogoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiRiepilogoType.class, datiRiepilogoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiRiepilogoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType}
	 * 
	 * @param datiRiepilogoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiRiepilogoType datiRiepilogoType) throws SerializerException {
		return this.objToXml(DatiRiepilogoType.class, datiRiepilogoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiRiepilogoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType}
	 * 
	 * @param datiRiepilogoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiRiepilogoType datiRiepilogoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiRiepilogoType.class, datiRiepilogoType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiGeneraliDocumentoType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiGeneraliDocumentoType</var>
	 * @param datiGeneraliDocumentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiGeneraliDocumentoType datiGeneraliDocumentoType) throws SerializerException {
		this.objToXml(fileName, DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType}
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
	 * Serialize to file system in <var>file</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param file Xml file to serialize the object <var>datiGeneraliDocumentoType</var>
	 * @param datiGeneraliDocumentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiGeneraliDocumentoType datiGeneraliDocumentoType) throws SerializerException {
		this.objToXml(file, DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType}
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
	 * Serialize to output stream <var>out</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiGeneraliDocumentoType</var>
	 * @param datiGeneraliDocumentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiGeneraliDocumentoType datiGeneraliDocumentoType) throws SerializerException {
		this.objToXml(out, DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType}
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
	 * Serialize to byte array the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param datiGeneraliDocumentoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiGeneraliDocumentoType datiGeneraliDocumentoType) throws SerializerException {
		return this.objToXml(DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType}
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
	 * Serialize to String the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param datiGeneraliDocumentoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiGeneraliDocumentoType datiGeneraliDocumentoType) throws SerializerException {
		return this.objToXml(DatiGeneraliDocumentoType.class, datiGeneraliDocumentoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiGeneraliDocumentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliDocumentoType}
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
	 * Serialize to file system in <var>fileName</var> the object <var>datiGeneraliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiGeneraliType</var>
	 * @param datiGeneraliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiGeneraliType datiGeneraliType) throws SerializerException {
		this.objToXml(fileName, DatiGeneraliType.class, datiGeneraliType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiGeneraliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType}
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
	 * Serialize to file system in <var>file</var> the object <var>datiGeneraliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType}
	 * 
	 * @param file Xml file to serialize the object <var>datiGeneraliType</var>
	 * @param datiGeneraliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiGeneraliType datiGeneraliType) throws SerializerException {
		this.objToXml(file, DatiGeneraliType.class, datiGeneraliType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiGeneraliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType}
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
	 * Serialize to output stream <var>out</var> the object <var>datiGeneraliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiGeneraliType</var>
	 * @param datiGeneraliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiGeneraliType datiGeneraliType) throws SerializerException {
		this.objToXml(out, DatiGeneraliType.class, datiGeneraliType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiGeneraliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType}
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
	 * Serialize to byte array the object <var>datiGeneraliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType}
	 * 
	 * @param datiGeneraliType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiGeneraliType datiGeneraliType) throws SerializerException {
		return this.objToXml(DatiGeneraliType.class, datiGeneraliType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiGeneraliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType}
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
	 * Serialize to String the object <var>datiGeneraliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType}
	 * 
	 * @param datiGeneraliType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiGeneraliType datiGeneraliType) throws SerializerException {
		return this.objToXml(DatiGeneraliType.class, datiGeneraliType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiGeneraliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiGeneraliType}
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
	 Object: DatiSALType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiSALType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiSALType</var>
	 * @param datiSALType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiSALType datiSALType) throws SerializerException {
		this.objToXml(fileName, DatiSALType.class, datiSALType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiSALType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiSALType</var>
	 * @param datiSALType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiSALType datiSALType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiSALType.class, datiSALType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiSALType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType}
	 * 
	 * @param file Xml file to serialize the object <var>datiSALType</var>
	 * @param datiSALType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiSALType datiSALType) throws SerializerException {
		this.objToXml(file, DatiSALType.class, datiSALType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiSALType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType}
	 * 
	 * @param file Xml file to serialize the object <var>datiSALType</var>
	 * @param datiSALType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiSALType datiSALType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiSALType.class, datiSALType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiSALType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiSALType</var>
	 * @param datiSALType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiSALType datiSALType) throws SerializerException {
		this.objToXml(out, DatiSALType.class, datiSALType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiSALType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiSALType</var>
	 * @param datiSALType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiSALType datiSALType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiSALType.class, datiSALType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiSALType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType}
	 * 
	 * @param datiSALType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiSALType datiSALType) throws SerializerException {
		return this.objToXml(DatiSALType.class, datiSALType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiSALType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType}
	 * 
	 * @param datiSALType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiSALType datiSALType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiSALType.class, datiSALType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiSALType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType}
	 * 
	 * @param datiSALType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiSALType datiSALType) throws SerializerException {
		return this.objToXml(DatiSALType.class, datiSALType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiSALType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiSALType}
	 * 
	 * @param datiSALType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiSALType datiSALType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiSALType.class, datiSALType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiDDTType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiDDTType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiDDTType</var>
	 * @param datiDDTType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiDDTType datiDDTType) throws SerializerException {
		this.objToXml(fileName, DatiDDTType.class, datiDDTType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiDDTType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiDDTType</var>
	 * @param datiDDTType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiDDTType datiDDTType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiDDTType.class, datiDDTType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiDDTType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType}
	 * 
	 * @param file Xml file to serialize the object <var>datiDDTType</var>
	 * @param datiDDTType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiDDTType datiDDTType) throws SerializerException {
		this.objToXml(file, DatiDDTType.class, datiDDTType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiDDTType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType}
	 * 
	 * @param file Xml file to serialize the object <var>datiDDTType</var>
	 * @param datiDDTType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiDDTType datiDDTType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiDDTType.class, datiDDTType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiDDTType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiDDTType</var>
	 * @param datiDDTType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiDDTType datiDDTType) throws SerializerException {
		this.objToXml(out, DatiDDTType.class, datiDDTType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiDDTType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiDDTType</var>
	 * @param datiDDTType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiDDTType datiDDTType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiDDTType.class, datiDDTType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiDDTType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType}
	 * 
	 * @param datiDDTType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiDDTType datiDDTType) throws SerializerException {
		return this.objToXml(DatiDDTType.class, datiDDTType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiDDTType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType}
	 * 
	 * @param datiDDTType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiDDTType datiDDTType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiDDTType.class, datiDDTType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiDDTType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType}
	 * 
	 * @param datiDDTType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiDDTType datiDDTType) throws SerializerException {
		return this.objToXml(DatiDDTType.class, datiDDTType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiDDTType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType}
	 * 
	 * @param datiDDTType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiDDTType datiDDTType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiDDTType.class, datiDDTType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiTrasportoType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiTrasportoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiTrasportoType</var>
	 * @param datiTrasportoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiTrasportoType datiTrasportoType) throws SerializerException {
		this.objToXml(fileName, DatiTrasportoType.class, datiTrasportoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiTrasportoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiTrasportoType</var>
	 * @param datiTrasportoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiTrasportoType datiTrasportoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiTrasportoType.class, datiTrasportoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiTrasportoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType}
	 * 
	 * @param file Xml file to serialize the object <var>datiTrasportoType</var>
	 * @param datiTrasportoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiTrasportoType datiTrasportoType) throws SerializerException {
		this.objToXml(file, DatiTrasportoType.class, datiTrasportoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiTrasportoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType}
	 * 
	 * @param file Xml file to serialize the object <var>datiTrasportoType</var>
	 * @param datiTrasportoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiTrasportoType datiTrasportoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiTrasportoType.class, datiTrasportoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiTrasportoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiTrasportoType</var>
	 * @param datiTrasportoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiTrasportoType datiTrasportoType) throws SerializerException {
		this.objToXml(out, DatiTrasportoType.class, datiTrasportoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiTrasportoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiTrasportoType</var>
	 * @param datiTrasportoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiTrasportoType datiTrasportoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiTrasportoType.class, datiTrasportoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiTrasportoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType}
	 * 
	 * @param datiTrasportoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiTrasportoType datiTrasportoType) throws SerializerException {
		return this.objToXml(DatiTrasportoType.class, datiTrasportoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiTrasportoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType}
	 * 
	 * @param datiTrasportoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiTrasportoType datiTrasportoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiTrasportoType.class, datiTrasportoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiTrasportoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType}
	 * 
	 * @param datiTrasportoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiTrasportoType datiTrasportoType) throws SerializerException {
		return this.objToXml(DatiTrasportoType.class, datiTrasportoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiTrasportoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasportoType}
	 * 
	 * @param datiTrasportoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiTrasportoType datiTrasportoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiTrasportoType.class, datiTrasportoType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: FatturaPrincipaleType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaPrincipaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaPrincipaleType</var>
	 * @param fatturaPrincipaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaPrincipaleType fatturaPrincipaleType) throws SerializerException {
		this.objToXml(fileName, FatturaPrincipaleType.class, fatturaPrincipaleType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaPrincipaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaPrincipaleType</var>
	 * @param fatturaPrincipaleType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaPrincipaleType fatturaPrincipaleType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FatturaPrincipaleType.class, fatturaPrincipaleType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaPrincipaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaPrincipaleType</var>
	 * @param fatturaPrincipaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaPrincipaleType fatturaPrincipaleType) throws SerializerException {
		this.objToXml(file, FatturaPrincipaleType.class, fatturaPrincipaleType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaPrincipaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaPrincipaleType</var>
	 * @param fatturaPrincipaleType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaPrincipaleType fatturaPrincipaleType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FatturaPrincipaleType.class, fatturaPrincipaleType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaPrincipaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaPrincipaleType</var>
	 * @param fatturaPrincipaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaPrincipaleType fatturaPrincipaleType) throws SerializerException {
		this.objToXml(out, FatturaPrincipaleType.class, fatturaPrincipaleType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaPrincipaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaPrincipaleType</var>
	 * @param fatturaPrincipaleType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaPrincipaleType fatturaPrincipaleType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FatturaPrincipaleType.class, fatturaPrincipaleType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fatturaPrincipaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType}
	 * 
	 * @param fatturaPrincipaleType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaPrincipaleType fatturaPrincipaleType) throws SerializerException {
		return this.objToXml(FatturaPrincipaleType.class, fatturaPrincipaleType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fatturaPrincipaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType}
	 * 
	 * @param fatturaPrincipaleType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaPrincipaleType fatturaPrincipaleType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FatturaPrincipaleType.class, fatturaPrincipaleType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fatturaPrincipaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType}
	 * 
	 * @param fatturaPrincipaleType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaPrincipaleType fatturaPrincipaleType) throws SerializerException {
		return this.objToXml(FatturaPrincipaleType.class, fatturaPrincipaleType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fatturaPrincipaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType}
	 * 
	 * @param fatturaPrincipaleType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaPrincipaleType fatturaPrincipaleType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FatturaPrincipaleType.class, fatturaPrincipaleType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiTrasmissioneType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiTrasmissioneType</var>
	 * @param datiTrasmissioneType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiTrasmissioneType datiTrasmissioneType) throws SerializerException {
		this.objToXml(fileName, DatiTrasmissioneType.class, datiTrasmissioneType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType}
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
	 * Serialize to file system in <var>file</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType}
	 * 
	 * @param file Xml file to serialize the object <var>datiTrasmissioneType</var>
	 * @param datiTrasmissioneType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiTrasmissioneType datiTrasmissioneType) throws SerializerException {
		this.objToXml(file, DatiTrasmissioneType.class, datiTrasmissioneType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType}
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
	 * Serialize to output stream <var>out</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiTrasmissioneType</var>
	 * @param datiTrasmissioneType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiTrasmissioneType datiTrasmissioneType) throws SerializerException {
		this.objToXml(out, DatiTrasmissioneType.class, datiTrasmissioneType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiTrasmissioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType}
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
	 * Serialize to byte array the object <var>datiTrasmissioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType}
	 * 
	 * @param datiTrasmissioneType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiTrasmissioneType datiTrasmissioneType) throws SerializerException {
		return this.objToXml(DatiTrasmissioneType.class, datiTrasmissioneType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiTrasmissioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType}
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
	 * Serialize to String the object <var>datiTrasmissioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType}
	 * 
	 * @param datiTrasmissioneType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiTrasmissioneType datiTrasmissioneType) throws SerializerException {
		return this.objToXml(DatiTrasmissioneType.class, datiTrasmissioneType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiTrasmissioneType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiTrasmissioneType}
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
	 Object: FatturaElettronicaHeaderType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaElettronicaHeaderType</var>
	 * @param fatturaElettronicaHeaderType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaElettronicaHeaderType fatturaElettronicaHeaderType) throws SerializerException {
		this.objToXml(fileName, FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType}
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
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaElettronicaHeaderType</var>
	 * @param fatturaElettronicaHeaderType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaElettronicaHeaderType fatturaElettronicaHeaderType) throws SerializerException {
		this.objToXml(file, FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType}
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
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaElettronicaHeaderType</var>
	 * @param fatturaElettronicaHeaderType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaElettronicaHeaderType fatturaElettronicaHeaderType) throws SerializerException {
		this.objToXml(out, FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType}
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
	 * Serialize to byte array the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param fatturaElettronicaHeaderType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaElettronicaHeaderType fatturaElettronicaHeaderType) throws SerializerException {
		return this.objToXml(FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType}
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
	 * Serialize to String the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param fatturaElettronicaHeaderType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaElettronicaHeaderType fatturaElettronicaHeaderType) throws SerializerException {
		return this.objToXml(FatturaElettronicaHeaderType.class, fatturaElettronicaHeaderType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fatturaElettronicaHeaderType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType}
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
	 Object: CedentePrestatoreType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType}
	 * 
	 * @param fileName Xml file to serialize the object <var>cedentePrestatoreType</var>
	 * @param cedentePrestatoreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CedentePrestatoreType cedentePrestatoreType) throws SerializerException {
		this.objToXml(fileName, CedentePrestatoreType.class, cedentePrestatoreType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType}
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
	 * Serialize to file system in <var>file</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType}
	 * 
	 * @param file Xml file to serialize the object <var>cedentePrestatoreType</var>
	 * @param cedentePrestatoreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CedentePrestatoreType cedentePrestatoreType) throws SerializerException {
		this.objToXml(file, CedentePrestatoreType.class, cedentePrestatoreType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType}
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
	 * Serialize to output stream <var>out</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType}
	 * 
	 * @param out OutputStream to serialize the object <var>cedentePrestatoreType</var>
	 * @param cedentePrestatoreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CedentePrestatoreType cedentePrestatoreType) throws SerializerException {
		this.objToXml(out, CedentePrestatoreType.class, cedentePrestatoreType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>cedentePrestatoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType}
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
	 * Serialize to byte array the object <var>cedentePrestatoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType}
	 * 
	 * @param cedentePrestatoreType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CedentePrestatoreType cedentePrestatoreType) throws SerializerException {
		return this.objToXml(CedentePrestatoreType.class, cedentePrestatoreType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>cedentePrestatoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType}
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
	 * Serialize to String the object <var>cedentePrestatoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType}
	 * 
	 * @param cedentePrestatoreType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CedentePrestatoreType cedentePrestatoreType) throws SerializerException {
		return this.objToXml(CedentePrestatoreType.class, cedentePrestatoreType, false).toString();
	}
	/**
	 * Serialize to String the object <var>cedentePrestatoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CedentePrestatoreType}
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
	 Object: RappresentanteFiscaleType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param fileName Xml file to serialize the object <var>rappresentanteFiscaleType</var>
	 * @param rappresentanteFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RappresentanteFiscaleType rappresentanteFiscaleType) throws SerializerException {
		this.objToXml(fileName, RappresentanteFiscaleType.class, rappresentanteFiscaleType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType}
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
	 * Serialize to file system in <var>file</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param file Xml file to serialize the object <var>rappresentanteFiscaleType</var>
	 * @param rappresentanteFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RappresentanteFiscaleType rappresentanteFiscaleType) throws SerializerException {
		this.objToXml(file, RappresentanteFiscaleType.class, rappresentanteFiscaleType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType}
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
	 * Serialize to output stream <var>out</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param out OutputStream to serialize the object <var>rappresentanteFiscaleType</var>
	 * @param rappresentanteFiscaleType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RappresentanteFiscaleType rappresentanteFiscaleType) throws SerializerException {
		this.objToXml(out, RappresentanteFiscaleType.class, rappresentanteFiscaleType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType}
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
	 * Serialize to byte array the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param rappresentanteFiscaleType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RappresentanteFiscaleType rappresentanteFiscaleType) throws SerializerException {
		return this.objToXml(RappresentanteFiscaleType.class, rappresentanteFiscaleType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType}
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
	 * Serialize to String the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param rappresentanteFiscaleType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RappresentanteFiscaleType rappresentanteFiscaleType) throws SerializerException {
		return this.objToXml(RappresentanteFiscaleType.class, rappresentanteFiscaleType, false).toString();
	}
	/**
	 * Serialize to String the object <var>rappresentanteFiscaleType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.RappresentanteFiscaleType}
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
	 Object: CessionarioCommittenteType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>cessionarioCommittenteType</var>
	 * @param cessionarioCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CessionarioCommittenteType cessionarioCommittenteType) throws SerializerException {
		this.objToXml(fileName, CessionarioCommittenteType.class, cessionarioCommittenteType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType}
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
	 * Serialize to file system in <var>file</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>cessionarioCommittenteType</var>
	 * @param cessionarioCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CessionarioCommittenteType cessionarioCommittenteType) throws SerializerException {
		this.objToXml(file, CessionarioCommittenteType.class, cessionarioCommittenteType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType}
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
	 * Serialize to output stream <var>out</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>cessionarioCommittenteType</var>
	 * @param cessionarioCommittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CessionarioCommittenteType cessionarioCommittenteType) throws SerializerException {
		this.objToXml(out, CessionarioCommittenteType.class, cessionarioCommittenteType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>cessionarioCommittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType}
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
	 * Serialize to byte array the object <var>cessionarioCommittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType}
	 * 
	 * @param cessionarioCommittenteType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CessionarioCommittenteType cessionarioCommittenteType) throws SerializerException {
		return this.objToXml(CessionarioCommittenteType.class, cessionarioCommittenteType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>cessionarioCommittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType}
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
	 * Serialize to String the object <var>cessionarioCommittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType}
	 * 
	 * @param cessionarioCommittenteType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CessionarioCommittenteType cessionarioCommittenteType) throws SerializerException {
		return this.objToXml(CessionarioCommittenteType.class, cessionarioCommittenteType, false).toString();
	}
	/**
	 * Serialize to String the object <var>cessionarioCommittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType}
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
	 Object: DatiAnagraficiCessionarioType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiAnagraficiCessionarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiAnagraficiCessionarioType</var>
	 * @param datiAnagraficiCessionarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiAnagraficiCessionarioType datiAnagraficiCessionarioType) throws SerializerException {
		this.objToXml(fileName, DatiAnagraficiCessionarioType.class, datiAnagraficiCessionarioType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiAnagraficiCessionarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiAnagraficiCessionarioType</var>
	 * @param datiAnagraficiCessionarioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiAnagraficiCessionarioType datiAnagraficiCessionarioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiAnagraficiCessionarioType.class, datiAnagraficiCessionarioType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiAnagraficiCessionarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType}
	 * 
	 * @param file Xml file to serialize the object <var>datiAnagraficiCessionarioType</var>
	 * @param datiAnagraficiCessionarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiAnagraficiCessionarioType datiAnagraficiCessionarioType) throws SerializerException {
		this.objToXml(file, DatiAnagraficiCessionarioType.class, datiAnagraficiCessionarioType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiAnagraficiCessionarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType}
	 * 
	 * @param file Xml file to serialize the object <var>datiAnagraficiCessionarioType</var>
	 * @param datiAnagraficiCessionarioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiAnagraficiCessionarioType datiAnagraficiCessionarioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiAnagraficiCessionarioType.class, datiAnagraficiCessionarioType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiAnagraficiCessionarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiAnagraficiCessionarioType</var>
	 * @param datiAnagraficiCessionarioType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiAnagraficiCessionarioType datiAnagraficiCessionarioType) throws SerializerException {
		this.objToXml(out, DatiAnagraficiCessionarioType.class, datiAnagraficiCessionarioType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiAnagraficiCessionarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiAnagraficiCessionarioType</var>
	 * @param datiAnagraficiCessionarioType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiAnagraficiCessionarioType datiAnagraficiCessionarioType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiAnagraficiCessionarioType.class, datiAnagraficiCessionarioType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiAnagraficiCessionarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType}
	 * 
	 * @param datiAnagraficiCessionarioType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiAnagraficiCessionarioType datiAnagraficiCessionarioType) throws SerializerException {
		return this.objToXml(DatiAnagraficiCessionarioType.class, datiAnagraficiCessionarioType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiAnagraficiCessionarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType}
	 * 
	 * @param datiAnagraficiCessionarioType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiAnagraficiCessionarioType datiAnagraficiCessionarioType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiAnagraficiCessionarioType.class, datiAnagraficiCessionarioType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiAnagraficiCessionarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType}
	 * 
	 * @param datiAnagraficiCessionarioType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiAnagraficiCessionarioType datiAnagraficiCessionarioType) throws SerializerException {
		return this.objToXml(DatiAnagraficiCessionarioType.class, datiAnagraficiCessionarioType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiAnagraficiCessionarioType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType}
	 * 
	 * @param datiAnagraficiCessionarioType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiAnagraficiCessionarioType datiAnagraficiCessionarioType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiAnagraficiCessionarioType.class, datiAnagraficiCessionarioType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IndirizzoType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>indirizzoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>indirizzoType</var>
	 * @param indirizzoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IndirizzoType indirizzoType) throws SerializerException {
		this.objToXml(fileName, IndirizzoType.class, indirizzoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>indirizzoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType}
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
	 * Serialize to file system in <var>file</var> the object <var>indirizzoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType}
	 * 
	 * @param file Xml file to serialize the object <var>indirizzoType</var>
	 * @param indirizzoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IndirizzoType indirizzoType) throws SerializerException {
		this.objToXml(file, IndirizzoType.class, indirizzoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>indirizzoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType}
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
	 * Serialize to output stream <var>out</var> the object <var>indirizzoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType}
	 * 
	 * @param out OutputStream to serialize the object <var>indirizzoType</var>
	 * @param indirizzoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IndirizzoType indirizzoType) throws SerializerException {
		this.objToXml(out, IndirizzoType.class, indirizzoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>indirizzoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType}
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
	 * Serialize to byte array the object <var>indirizzoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType}
	 * 
	 * @param indirizzoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IndirizzoType indirizzoType) throws SerializerException {
		return this.objToXml(IndirizzoType.class, indirizzoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>indirizzoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType}
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
	 * Serialize to String the object <var>indirizzoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType}
	 * 
	 * @param indirizzoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IndirizzoType indirizzoType) throws SerializerException {
		return this.objToXml(IndirizzoType.class, indirizzoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>indirizzoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType}
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
	 Object: IscrizioneREAType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType}
	 * 
	 * @param fileName Xml file to serialize the object <var>iscrizioneREAType</var>
	 * @param iscrizioneREAType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IscrizioneREAType iscrizioneREAType) throws SerializerException {
		this.objToXml(fileName, IscrizioneREAType.class, iscrizioneREAType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType}
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
	 * Serialize to file system in <var>file</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType}
	 * 
	 * @param file Xml file to serialize the object <var>iscrizioneREAType</var>
	 * @param iscrizioneREAType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IscrizioneREAType iscrizioneREAType) throws SerializerException {
		this.objToXml(file, IscrizioneREAType.class, iscrizioneREAType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType}
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
	 * Serialize to output stream <var>out</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType}
	 * 
	 * @param out OutputStream to serialize the object <var>iscrizioneREAType</var>
	 * @param iscrizioneREAType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IscrizioneREAType iscrizioneREAType) throws SerializerException {
		this.objToXml(out, IscrizioneREAType.class, iscrizioneREAType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>iscrizioneREAType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType}
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
	 * Serialize to byte array the object <var>iscrizioneREAType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType}
	 * 
	 * @param iscrizioneREAType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IscrizioneREAType iscrizioneREAType) throws SerializerException {
		return this.objToXml(IscrizioneREAType.class, iscrizioneREAType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>iscrizioneREAType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType}
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
	 * Serialize to String the object <var>iscrizioneREAType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType}
	 * 
	 * @param iscrizioneREAType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IscrizioneREAType iscrizioneREAType) throws SerializerException {
		return this.objToXml(IscrizioneREAType.class, iscrizioneREAType, false).toString();
	}
	/**
	 * Serialize to String the object <var>iscrizioneREAType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.IscrizioneREAType}
	 * 
	 * @param iscrizioneREAType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IscrizioneREAType iscrizioneREAType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IscrizioneREAType.class, iscrizioneREAType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ContattiType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>contattiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>contattiType</var>
	 * @param contattiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ContattiType contattiType) throws SerializerException {
		this.objToXml(fileName, ContattiType.class, contattiType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>contattiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>contattiType</var>
	 * @param contattiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ContattiType contattiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ContattiType.class, contattiType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>contattiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType}
	 * 
	 * @param file Xml file to serialize the object <var>contattiType</var>
	 * @param contattiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ContattiType contattiType) throws SerializerException {
		this.objToXml(file, ContattiType.class, contattiType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>contattiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType}
	 * 
	 * @param file Xml file to serialize the object <var>contattiType</var>
	 * @param contattiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ContattiType contattiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ContattiType.class, contattiType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>contattiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType}
	 * 
	 * @param out OutputStream to serialize the object <var>contattiType</var>
	 * @param contattiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ContattiType contattiType) throws SerializerException {
		this.objToXml(out, ContattiType.class, contattiType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>contattiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType}
	 * 
	 * @param out OutputStream to serialize the object <var>contattiType</var>
	 * @param contattiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ContattiType contattiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ContattiType.class, contattiType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>contattiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType}
	 * 
	 * @param contattiType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ContattiType contattiType) throws SerializerException {
		return this.objToXml(ContattiType.class, contattiType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>contattiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType}
	 * 
	 * @param contattiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ContattiType contattiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ContattiType.class, contattiType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>contattiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType}
	 * 
	 * @param contattiType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ContattiType contattiType) throws SerializerException {
		return this.objToXml(ContattiType.class, contattiType, false).toString();
	}
	/**
	 * Serialize to String the object <var>contattiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiType}
	 * 
	 * @param contattiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ContattiType contattiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ContattiType.class, contattiType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiAnagraficiVettoreType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiAnagraficiVettoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiAnagraficiVettoreType</var>
	 * @param datiAnagraficiVettoreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiAnagraficiVettoreType datiAnagraficiVettoreType) throws SerializerException {
		this.objToXml(fileName, DatiAnagraficiVettoreType.class, datiAnagraficiVettoreType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiAnagraficiVettoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiAnagraficiVettoreType</var>
	 * @param datiAnagraficiVettoreType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiAnagraficiVettoreType datiAnagraficiVettoreType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiAnagraficiVettoreType.class, datiAnagraficiVettoreType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiAnagraficiVettoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType}
	 * 
	 * @param file Xml file to serialize the object <var>datiAnagraficiVettoreType</var>
	 * @param datiAnagraficiVettoreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiAnagraficiVettoreType datiAnagraficiVettoreType) throws SerializerException {
		this.objToXml(file, DatiAnagraficiVettoreType.class, datiAnagraficiVettoreType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiAnagraficiVettoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType}
	 * 
	 * @param file Xml file to serialize the object <var>datiAnagraficiVettoreType</var>
	 * @param datiAnagraficiVettoreType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiAnagraficiVettoreType datiAnagraficiVettoreType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiAnagraficiVettoreType.class, datiAnagraficiVettoreType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiAnagraficiVettoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiAnagraficiVettoreType</var>
	 * @param datiAnagraficiVettoreType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiAnagraficiVettoreType datiAnagraficiVettoreType) throws SerializerException {
		this.objToXml(out, DatiAnagraficiVettoreType.class, datiAnagraficiVettoreType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiAnagraficiVettoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiAnagraficiVettoreType</var>
	 * @param datiAnagraficiVettoreType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiAnagraficiVettoreType datiAnagraficiVettoreType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiAnagraficiVettoreType.class, datiAnagraficiVettoreType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiAnagraficiVettoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType}
	 * 
	 * @param datiAnagraficiVettoreType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiAnagraficiVettoreType datiAnagraficiVettoreType) throws SerializerException {
		return this.objToXml(DatiAnagraficiVettoreType.class, datiAnagraficiVettoreType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiAnagraficiVettoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType}
	 * 
	 * @param datiAnagraficiVettoreType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiAnagraficiVettoreType datiAnagraficiVettoreType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiAnagraficiVettoreType.class, datiAnagraficiVettoreType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiAnagraficiVettoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType}
	 * 
	 * @param datiAnagraficiVettoreType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiAnagraficiVettoreType datiAnagraficiVettoreType) throws SerializerException {
		return this.objToXml(DatiAnagraficiVettoreType.class, datiAnagraficiVettoreType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiAnagraficiVettoreType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiVettoreType}
	 * 
	 * @param datiAnagraficiVettoreType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiAnagraficiVettoreType datiAnagraficiVettoreType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiAnagraficiVettoreType.class, datiAnagraficiVettoreType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiVeicoliType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiVeicoliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiVeicoliType</var>
	 * @param datiVeicoliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiVeicoliType datiVeicoliType) throws SerializerException {
		this.objToXml(fileName, DatiVeicoliType.class, datiVeicoliType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiVeicoliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiVeicoliType</var>
	 * @param datiVeicoliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiVeicoliType datiVeicoliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiVeicoliType.class, datiVeicoliType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiVeicoliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType}
	 * 
	 * @param file Xml file to serialize the object <var>datiVeicoliType</var>
	 * @param datiVeicoliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiVeicoliType datiVeicoliType) throws SerializerException {
		this.objToXml(file, DatiVeicoliType.class, datiVeicoliType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiVeicoliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType}
	 * 
	 * @param file Xml file to serialize the object <var>datiVeicoliType</var>
	 * @param datiVeicoliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiVeicoliType datiVeicoliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiVeicoliType.class, datiVeicoliType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiVeicoliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiVeicoliType</var>
	 * @param datiVeicoliType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiVeicoliType datiVeicoliType) throws SerializerException {
		this.objToXml(out, DatiVeicoliType.class, datiVeicoliType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiVeicoliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiVeicoliType</var>
	 * @param datiVeicoliType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiVeicoliType datiVeicoliType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiVeicoliType.class, datiVeicoliType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiVeicoliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType}
	 * 
	 * @param datiVeicoliType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiVeicoliType datiVeicoliType) throws SerializerException {
		return this.objToXml(DatiVeicoliType.class, datiVeicoliType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiVeicoliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType}
	 * 
	 * @param datiVeicoliType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiVeicoliType datiVeicoliType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiVeicoliType.class, datiVeicoliType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiVeicoliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType}
	 * 
	 * @param datiVeicoliType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiVeicoliType datiVeicoliType) throws SerializerException {
		return this.objToXml(DatiVeicoliType.class, datiVeicoliType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiVeicoliType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiVeicoliType}
	 * 
	 * @param datiVeicoliType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiVeicoliType datiVeicoliType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiVeicoliType.class, datiVeicoliType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: FatturaElettronicaType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaElettronicaType</var>
	 * @param fatturaElettronicaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaElettronicaType fatturaElettronicaType) throws SerializerException {
		this.objToXml(fileName, FatturaElettronicaType.class, fatturaElettronicaType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType}
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
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaElettronicaType</var>
	 * @param fatturaElettronicaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaElettronicaType fatturaElettronicaType) throws SerializerException {
		this.objToXml(file, FatturaElettronicaType.class, fatturaElettronicaType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType}
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
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaElettronicaType</var>
	 * @param fatturaElettronicaType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaElettronicaType fatturaElettronicaType) throws SerializerException {
		this.objToXml(out, FatturaElettronicaType.class, fatturaElettronicaType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType}
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
	 * Serialize to byte array the object <var>fatturaElettronicaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType}
	 * 
	 * @param fatturaElettronicaType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaElettronicaType fatturaElettronicaType) throws SerializerException {
		return this.objToXml(FatturaElettronicaType.class, fatturaElettronicaType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fatturaElettronicaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType}
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
	 * Serialize to String the object <var>fatturaElettronicaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType}
	 * 
	 * @param fatturaElettronicaType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaElettronicaType fatturaElettronicaType) throws SerializerException {
		return this.objToXml(FatturaElettronicaType.class, fatturaElettronicaType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fatturaElettronicaType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType}
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
	 Object: FatturaElettronicaBodyType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fatturaElettronicaBodyType</var>
	 * @param fatturaElettronicaBodyType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FatturaElettronicaBodyType fatturaElettronicaBodyType) throws SerializerException {
		this.objToXml(fileName, FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType}
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
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param file Xml file to serialize the object <var>fatturaElettronicaBodyType</var>
	 * @param fatturaElettronicaBodyType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FatturaElettronicaBodyType fatturaElettronicaBodyType) throws SerializerException {
		this.objToXml(file, FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType}
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
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param out OutputStream to serialize the object <var>fatturaElettronicaBodyType</var>
	 * @param fatturaElettronicaBodyType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FatturaElettronicaBodyType fatturaElettronicaBodyType) throws SerializerException {
		this.objToXml(out, FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType}
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
	 * Serialize to byte array the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param fatturaElettronicaBodyType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FatturaElettronicaBodyType fatturaElettronicaBodyType) throws SerializerException {
		return this.objToXml(FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType}
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
	 * Serialize to String the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param fatturaElettronicaBodyType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FatturaElettronicaBodyType fatturaElettronicaBodyType) throws SerializerException {
		return this.objToXml(FatturaElettronicaBodyType.class, fatturaElettronicaBodyType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fatturaElettronicaBodyType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaBodyType}
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
	 Object: DatiPagamentoType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiPagamentoType</var>
	 * @param datiPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiPagamentoType datiPagamentoType) throws SerializerException {
		this.objToXml(fileName, DatiPagamentoType.class, datiPagamentoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiPagamentoType</var>
	 * @param datiPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiPagamentoType datiPagamentoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiPagamentoType.class, datiPagamentoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType}
	 * 
	 * @param file Xml file to serialize the object <var>datiPagamentoType</var>
	 * @param datiPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiPagamentoType datiPagamentoType) throws SerializerException {
		this.objToXml(file, DatiPagamentoType.class, datiPagamentoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType}
	 * 
	 * @param file Xml file to serialize the object <var>datiPagamentoType</var>
	 * @param datiPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiPagamentoType datiPagamentoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiPagamentoType.class, datiPagamentoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiPagamentoType</var>
	 * @param datiPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiPagamentoType datiPagamentoType) throws SerializerException {
		this.objToXml(out, DatiPagamentoType.class, datiPagamentoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiPagamentoType</var>
	 * @param datiPagamentoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiPagamentoType datiPagamentoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiPagamentoType.class, datiPagamentoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType}
	 * 
	 * @param datiPagamentoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiPagamentoType datiPagamentoType) throws SerializerException {
		return this.objToXml(DatiPagamentoType.class, datiPagamentoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType}
	 * 
	 * @param datiPagamentoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiPagamentoType datiPagamentoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiPagamentoType.class, datiPagamentoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType}
	 * 
	 * @param datiPagamentoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiPagamentoType datiPagamentoType) throws SerializerException {
		return this.objToXml(DatiPagamentoType.class, datiPagamentoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiPagamentoType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType}
	 * 
	 * @param datiPagamentoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiPagamentoType datiPagamentoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiPagamentoType.class, datiPagamentoType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: AllegatiType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allegatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>allegatiType</var>
	 * @param allegatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AllegatiType allegatiType) throws SerializerException {
		this.objToXml(fileName, AllegatiType.class, allegatiType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>allegatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType}
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
	 * Serialize to file system in <var>file</var> the object <var>allegatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType}
	 * 
	 * @param file Xml file to serialize the object <var>allegatiType</var>
	 * @param allegatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AllegatiType allegatiType) throws SerializerException {
		this.objToXml(file, AllegatiType.class, allegatiType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>allegatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType}
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
	 * Serialize to output stream <var>out</var> the object <var>allegatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType}
	 * 
	 * @param out OutputStream to serialize the object <var>allegatiType</var>
	 * @param allegatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AllegatiType allegatiType) throws SerializerException {
		this.objToXml(out, AllegatiType.class, allegatiType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>allegatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType}
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
	 * Serialize to byte array the object <var>allegatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType}
	 * 
	 * @param allegatiType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AllegatiType allegatiType) throws SerializerException {
		return this.objToXml(AllegatiType.class, allegatiType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>allegatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType}
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
	 * Serialize to String the object <var>allegatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType}
	 * 
	 * @param allegatiType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AllegatiType allegatiType) throws SerializerException {
		return this.objToXml(AllegatiType.class, allegatiType, false).toString();
	}
	/**
	 * Serialize to String the object <var>allegatiType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType}
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
	 Object: DatiCassaPrevidenzialeType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiCassaPrevidenzialeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiCassaPrevidenzialeType</var>
	 * @param datiCassaPrevidenzialeType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiCassaPrevidenzialeType datiCassaPrevidenzialeType) throws SerializerException {
		this.objToXml(fileName, DatiCassaPrevidenzialeType.class, datiCassaPrevidenzialeType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiCassaPrevidenzialeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiCassaPrevidenzialeType</var>
	 * @param datiCassaPrevidenzialeType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiCassaPrevidenzialeType datiCassaPrevidenzialeType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiCassaPrevidenzialeType.class, datiCassaPrevidenzialeType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiCassaPrevidenzialeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType}
	 * 
	 * @param file Xml file to serialize the object <var>datiCassaPrevidenzialeType</var>
	 * @param datiCassaPrevidenzialeType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiCassaPrevidenzialeType datiCassaPrevidenzialeType) throws SerializerException {
		this.objToXml(file, DatiCassaPrevidenzialeType.class, datiCassaPrevidenzialeType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiCassaPrevidenzialeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType}
	 * 
	 * @param file Xml file to serialize the object <var>datiCassaPrevidenzialeType</var>
	 * @param datiCassaPrevidenzialeType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiCassaPrevidenzialeType datiCassaPrevidenzialeType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiCassaPrevidenzialeType.class, datiCassaPrevidenzialeType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiCassaPrevidenzialeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiCassaPrevidenzialeType</var>
	 * @param datiCassaPrevidenzialeType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiCassaPrevidenzialeType datiCassaPrevidenzialeType) throws SerializerException {
		this.objToXml(out, DatiCassaPrevidenzialeType.class, datiCassaPrevidenzialeType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiCassaPrevidenzialeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiCassaPrevidenzialeType</var>
	 * @param datiCassaPrevidenzialeType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiCassaPrevidenzialeType datiCassaPrevidenzialeType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiCassaPrevidenzialeType.class, datiCassaPrevidenzialeType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiCassaPrevidenzialeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType}
	 * 
	 * @param datiCassaPrevidenzialeType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiCassaPrevidenzialeType datiCassaPrevidenzialeType) throws SerializerException {
		return this.objToXml(DatiCassaPrevidenzialeType.class, datiCassaPrevidenzialeType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiCassaPrevidenzialeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType}
	 * 
	 * @param datiCassaPrevidenzialeType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiCassaPrevidenzialeType datiCassaPrevidenzialeType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiCassaPrevidenzialeType.class, datiCassaPrevidenzialeType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiCassaPrevidenzialeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType}
	 * 
	 * @param datiCassaPrevidenzialeType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiCassaPrevidenzialeType datiCassaPrevidenzialeType) throws SerializerException {
		return this.objToXml(DatiCassaPrevidenzialeType.class, datiCassaPrevidenzialeType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiCassaPrevidenzialeType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiCassaPrevidenzialeType}
	 * 
	 * @param datiCassaPrevidenzialeType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiCassaPrevidenzialeType datiCassaPrevidenzialeType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiCassaPrevidenzialeType.class, datiCassaPrevidenzialeType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DatiBolloType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiBolloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiBolloType</var>
	 * @param datiBolloType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiBolloType datiBolloType) throws SerializerException {
		this.objToXml(fileName, DatiBolloType.class, datiBolloType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>datiBolloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType}
	 * 
	 * @param fileName Xml file to serialize the object <var>datiBolloType</var>
	 * @param datiBolloType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DatiBolloType datiBolloType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DatiBolloType.class, datiBolloType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiBolloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType}
	 * 
	 * @param file Xml file to serialize the object <var>datiBolloType</var>
	 * @param datiBolloType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiBolloType datiBolloType) throws SerializerException {
		this.objToXml(file, DatiBolloType.class, datiBolloType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>datiBolloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType}
	 * 
	 * @param file Xml file to serialize the object <var>datiBolloType</var>
	 * @param datiBolloType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DatiBolloType datiBolloType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DatiBolloType.class, datiBolloType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiBolloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiBolloType</var>
	 * @param datiBolloType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiBolloType datiBolloType) throws SerializerException {
		this.objToXml(out, DatiBolloType.class, datiBolloType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>datiBolloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType}
	 * 
	 * @param out OutputStream to serialize the object <var>datiBolloType</var>
	 * @param datiBolloType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DatiBolloType datiBolloType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DatiBolloType.class, datiBolloType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>datiBolloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType}
	 * 
	 * @param datiBolloType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiBolloType datiBolloType) throws SerializerException {
		return this.objToXml(DatiBolloType.class, datiBolloType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>datiBolloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType}
	 * 
	 * @param datiBolloType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DatiBolloType datiBolloType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiBolloType.class, datiBolloType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>datiBolloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType}
	 * 
	 * @param datiBolloType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiBolloType datiBolloType) throws SerializerException {
		return this.objToXml(DatiBolloType.class, datiBolloType, false).toString();
	}
	/**
	 * Serialize to String the object <var>datiBolloType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType}
	 * 
	 * @param datiBolloType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DatiBolloType datiBolloType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DatiBolloType.class, datiBolloType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ContattiTrasmittenteType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>contattiTrasmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>contattiTrasmittenteType</var>
	 * @param contattiTrasmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ContattiTrasmittenteType contattiTrasmittenteType) throws SerializerException {
		this.objToXml(fileName, ContattiTrasmittenteType.class, contattiTrasmittenteType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>contattiTrasmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType}
	 * 
	 * @param fileName Xml file to serialize the object <var>contattiTrasmittenteType</var>
	 * @param contattiTrasmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ContattiTrasmittenteType contattiTrasmittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ContattiTrasmittenteType.class, contattiTrasmittenteType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>contattiTrasmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>contattiTrasmittenteType</var>
	 * @param contattiTrasmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ContattiTrasmittenteType contattiTrasmittenteType) throws SerializerException {
		this.objToXml(file, ContattiTrasmittenteType.class, contattiTrasmittenteType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>contattiTrasmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType}
	 * 
	 * @param file Xml file to serialize the object <var>contattiTrasmittenteType</var>
	 * @param contattiTrasmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ContattiTrasmittenteType contattiTrasmittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ContattiTrasmittenteType.class, contattiTrasmittenteType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>contattiTrasmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>contattiTrasmittenteType</var>
	 * @param contattiTrasmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ContattiTrasmittenteType contattiTrasmittenteType) throws SerializerException {
		this.objToXml(out, ContattiTrasmittenteType.class, contattiTrasmittenteType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>contattiTrasmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType}
	 * 
	 * @param out OutputStream to serialize the object <var>contattiTrasmittenteType</var>
	 * @param contattiTrasmittenteType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ContattiTrasmittenteType contattiTrasmittenteType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ContattiTrasmittenteType.class, contattiTrasmittenteType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>contattiTrasmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType}
	 * 
	 * @param contattiTrasmittenteType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ContattiTrasmittenteType contattiTrasmittenteType) throws SerializerException {
		return this.objToXml(ContattiTrasmittenteType.class, contattiTrasmittenteType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>contattiTrasmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType}
	 * 
	 * @param contattiTrasmittenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ContattiTrasmittenteType contattiTrasmittenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ContattiTrasmittenteType.class, contattiTrasmittenteType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>contattiTrasmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType}
	 * 
	 * @param contattiTrasmittenteType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ContattiTrasmittenteType contattiTrasmittenteType) throws SerializerException {
		return this.objToXml(ContattiTrasmittenteType.class, contattiTrasmittenteType, false).toString();
	}
	/**
	 * Serialize to String the object <var>contattiTrasmittenteType</var> of type {@link it.gov.fatturapa.sdi.fatturapa.v1_0.ContattiTrasmittenteType}
	 * 
	 * @param contattiTrasmittenteType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ContattiTrasmittenteType contattiTrasmittenteType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ContattiTrasmittenteType.class, contattiTrasmittenteType, prettyPrint).toString();
	}
	
	
	

}
