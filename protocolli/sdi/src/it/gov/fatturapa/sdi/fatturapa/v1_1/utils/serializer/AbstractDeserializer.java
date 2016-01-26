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
package it.gov.fatturapa.sdi.fatturapa.v1_1.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer {



	protected abstract Object _xmlToObj(InputStream is, Class<?> c) throws Exception;
	
	private Object xmlToObj(InputStream is,Class<?> c) throws DeserializerException{
		try{
			return this._xmlToObj(is, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(), e);
		}
	}
	private Object xmlToObj(String fileName,Class<?> c) throws DeserializerException{
		try{
			return this.xmlToObj(new File(fileName), c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(), e);
		}
	}
	private Object xmlToObj(File file,Class<?> c) throws DeserializerException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(file);
			return this._xmlToObj(fin, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(),e);
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
		}
	}
	private Object xmlToObj(byte[] file,Class<?> c) throws DeserializerException{
		ByteArrayInputStream fin = null;
		try{
			fin = new ByteArrayInputStream(file);
			return this._xmlToObj(fin, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(),e);
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
		}
	}






	/*
	 =================================================================================
	 Object: CodiceArticoloType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceArticoloType readCodiceArticoloType(String fileName) throws DeserializerException {
		return (CodiceArticoloType) this.xmlToObj(fileName, CodiceArticoloType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceArticoloType readCodiceArticoloType(File file) throws DeserializerException {
		return (CodiceArticoloType) this.xmlToObj(file, CodiceArticoloType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceArticoloType readCodiceArticoloType(InputStream in) throws DeserializerException {
		return (CodiceArticoloType) this.xmlToObj(in, CodiceArticoloType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceArticoloType readCodiceArticoloType(byte[] in) throws DeserializerException {
		return (CodiceArticoloType) this.xmlToObj(in, CodiceArticoloType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CodiceArticoloType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceArticoloType readCodiceArticoloTypeFromString(String in) throws DeserializerException {
		return (CodiceArticoloType) this.xmlToObj(in.getBytes(), CodiceArticoloType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DettaglioLineeType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioLineeType readDettaglioLineeType(String fileName) throws DeserializerException {
		return (DettaglioLineeType) this.xmlToObj(fileName, DettaglioLineeType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioLineeType readDettaglioLineeType(File file) throws DeserializerException {
		return (DettaglioLineeType) this.xmlToObj(file, DettaglioLineeType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioLineeType readDettaglioLineeType(InputStream in) throws DeserializerException {
		return (DettaglioLineeType) this.xmlToObj(in, DettaglioLineeType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioLineeType readDettaglioLineeType(byte[] in) throws DeserializerException {
		return (DettaglioLineeType) this.xmlToObj(in, DettaglioLineeType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioLineeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioLineeType readDettaglioLineeTypeFromString(String in) throws DeserializerException {
		return (DettaglioLineeType) this.xmlToObj(in.getBytes(), DettaglioLineeType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ScontoMaggiorazioneType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ScontoMaggiorazioneType readScontoMaggiorazioneType(String fileName) throws DeserializerException {
		return (ScontoMaggiorazioneType) this.xmlToObj(fileName, ScontoMaggiorazioneType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ScontoMaggiorazioneType readScontoMaggiorazioneType(File file) throws DeserializerException {
		return (ScontoMaggiorazioneType) this.xmlToObj(file, ScontoMaggiorazioneType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ScontoMaggiorazioneType readScontoMaggiorazioneType(InputStream in) throws DeserializerException {
		return (ScontoMaggiorazioneType) this.xmlToObj(in, ScontoMaggiorazioneType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ScontoMaggiorazioneType readScontoMaggiorazioneType(byte[] in) throws DeserializerException {
		return (ScontoMaggiorazioneType) this.xmlToObj(in, ScontoMaggiorazioneType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ScontoMaggiorazioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ScontoMaggiorazioneType readScontoMaggiorazioneTypeFromString(String in) throws DeserializerException {
		return (ScontoMaggiorazioneType) this.xmlToObj(in.getBytes(), ScontoMaggiorazioneType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AltriDatiGestionaliType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AltriDatiGestionaliType readAltriDatiGestionaliType(String fileName) throws DeserializerException {
		return (AltriDatiGestionaliType) this.xmlToObj(fileName, AltriDatiGestionaliType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AltriDatiGestionaliType readAltriDatiGestionaliType(File file) throws DeserializerException {
		return (AltriDatiGestionaliType) this.xmlToObj(file, AltriDatiGestionaliType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AltriDatiGestionaliType readAltriDatiGestionaliType(InputStream in) throws DeserializerException {
		return (AltriDatiGestionaliType) this.xmlToObj(in, AltriDatiGestionaliType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AltriDatiGestionaliType readAltriDatiGestionaliType(byte[] in) throws DeserializerException {
		return (AltriDatiGestionaliType) this.xmlToObj(in, AltriDatiGestionaliType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AltriDatiGestionaliType readAltriDatiGestionaliTypeFromString(String in) throws DeserializerException {
		return (AltriDatiGestionaliType) this.xmlToObj(in.getBytes(), AltriDatiGestionaliType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiDocumentiCorrelatiType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiDocumentiCorrelatiType readDatiDocumentiCorrelatiType(String fileName) throws DeserializerException {
		return (DatiDocumentiCorrelatiType) this.xmlToObj(fileName, DatiDocumentiCorrelatiType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiDocumentiCorrelatiType readDatiDocumentiCorrelatiType(File file) throws DeserializerException {
		return (DatiDocumentiCorrelatiType) this.xmlToObj(file, DatiDocumentiCorrelatiType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiDocumentiCorrelatiType readDatiDocumentiCorrelatiType(InputStream in) throws DeserializerException {
		return (DatiDocumentiCorrelatiType) this.xmlToObj(in, DatiDocumentiCorrelatiType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiDocumentiCorrelatiType readDatiDocumentiCorrelatiType(byte[] in) throws DeserializerException {
		return (DatiDocumentiCorrelatiType) this.xmlToObj(in, DatiDocumentiCorrelatiType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDocumentiCorrelatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiDocumentiCorrelatiType readDatiDocumentiCorrelatiTypeFromString(String in) throws DeserializerException {
		return (DatiDocumentiCorrelatiType) this.xmlToObj(in.getBytes(), DatiDocumentiCorrelatiType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiRitenutaType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiRitenutaType readDatiRitenutaType(String fileName) throws DeserializerException {
		return (DatiRitenutaType) this.xmlToObj(fileName, DatiRitenutaType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiRitenutaType readDatiRitenutaType(File file) throws DeserializerException {
		return (DatiRitenutaType) this.xmlToObj(file, DatiRitenutaType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiRitenutaType readDatiRitenutaType(InputStream in) throws DeserializerException {
		return (DatiRitenutaType) this.xmlToObj(in, DatiRitenutaType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiRitenutaType readDatiRitenutaType(byte[] in) throws DeserializerException {
		return (DatiRitenutaType) this.xmlToObj(in, DatiRitenutaType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRitenutaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiRitenutaType readDatiRitenutaTypeFromString(String in) throws DeserializerException {
		return (DatiRitenutaType) this.xmlToObj(in.getBytes(), DatiRitenutaType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiAnagraficiTerzoIntermediarioType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiTerzoIntermediarioType readDatiAnagraficiTerzoIntermediarioType(String fileName) throws DeserializerException {
		return (DatiAnagraficiTerzoIntermediarioType) this.xmlToObj(fileName, DatiAnagraficiTerzoIntermediarioType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiTerzoIntermediarioType readDatiAnagraficiTerzoIntermediarioType(File file) throws DeserializerException {
		return (DatiAnagraficiTerzoIntermediarioType) this.xmlToObj(file, DatiAnagraficiTerzoIntermediarioType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiTerzoIntermediarioType readDatiAnagraficiTerzoIntermediarioType(InputStream in) throws DeserializerException {
		return (DatiAnagraficiTerzoIntermediarioType) this.xmlToObj(in, DatiAnagraficiTerzoIntermediarioType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiTerzoIntermediarioType readDatiAnagraficiTerzoIntermediarioType(byte[] in) throws DeserializerException {
		return (DatiAnagraficiTerzoIntermediarioType) this.xmlToObj(in, DatiAnagraficiTerzoIntermediarioType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiTerzoIntermediarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiTerzoIntermediarioType readDatiAnagraficiTerzoIntermediarioTypeFromString(String in) throws DeserializerException {
		return (DatiAnagraficiTerzoIntermediarioType) this.xmlToObj(in.getBytes(), DatiAnagraficiTerzoIntermediarioType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: TerzoIntermediarioSoggettoEmittenteType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TerzoIntermediarioSoggettoEmittenteType readTerzoIntermediarioSoggettoEmittenteType(String fileName) throws DeserializerException {
		return (TerzoIntermediarioSoggettoEmittenteType) this.xmlToObj(fileName, TerzoIntermediarioSoggettoEmittenteType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TerzoIntermediarioSoggettoEmittenteType readTerzoIntermediarioSoggettoEmittenteType(File file) throws DeserializerException {
		return (TerzoIntermediarioSoggettoEmittenteType) this.xmlToObj(file, TerzoIntermediarioSoggettoEmittenteType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TerzoIntermediarioSoggettoEmittenteType readTerzoIntermediarioSoggettoEmittenteType(InputStream in) throws DeserializerException {
		return (TerzoIntermediarioSoggettoEmittenteType) this.xmlToObj(in, TerzoIntermediarioSoggettoEmittenteType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TerzoIntermediarioSoggettoEmittenteType readTerzoIntermediarioSoggettoEmittenteType(byte[] in) throws DeserializerException {
		return (TerzoIntermediarioSoggettoEmittenteType) this.xmlToObj(in, TerzoIntermediarioSoggettoEmittenteType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.TerzoIntermediarioSoggettoEmittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TerzoIntermediarioSoggettoEmittenteType readTerzoIntermediarioSoggettoEmittenteTypeFromString(String in) throws DeserializerException {
		return (TerzoIntermediarioSoggettoEmittenteType) this.xmlToObj(in.getBytes(), TerzoIntermediarioSoggettoEmittenteType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DettaglioPagamentoType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioPagamentoType readDettaglioPagamentoType(String fileName) throws DeserializerException {
		return (DettaglioPagamentoType) this.xmlToObj(fileName, DettaglioPagamentoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioPagamentoType readDettaglioPagamentoType(File file) throws DeserializerException {
		return (DettaglioPagamentoType) this.xmlToObj(file, DettaglioPagamentoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioPagamentoType readDettaglioPagamentoType(InputStream in) throws DeserializerException {
		return (DettaglioPagamentoType) this.xmlToObj(in, DettaglioPagamentoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioPagamentoType readDettaglioPagamentoType(byte[] in) throws DeserializerException {
		return (DettaglioPagamentoType) this.xmlToObj(in, DettaglioPagamentoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DettaglioPagamentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioPagamentoType readDettaglioPagamentoTypeFromString(String in) throws DeserializerException {
		return (DettaglioPagamentoType) this.xmlToObj(in.getBytes(), DettaglioPagamentoType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IdFiscaleType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFiscaleType readIdFiscaleType(String fileName) throws DeserializerException {
		return (IdFiscaleType) this.xmlToObj(fileName, IdFiscaleType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFiscaleType readIdFiscaleType(File file) throws DeserializerException {
		return (IdFiscaleType) this.xmlToObj(file, IdFiscaleType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFiscaleType readIdFiscaleType(InputStream in) throws DeserializerException {
		return (IdFiscaleType) this.xmlToObj(in, IdFiscaleType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFiscaleType readIdFiscaleType(byte[] in) throws DeserializerException {
		return (IdFiscaleType) this.xmlToObj(in, IdFiscaleType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IdFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFiscaleType readIdFiscaleTypeFromString(String in) throws DeserializerException {
		return (IdFiscaleType) this.xmlToObj(in.getBytes(), IdFiscaleType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiAnagraficiRappresentanteType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiRappresentanteType readDatiAnagraficiRappresentanteType(String fileName) throws DeserializerException {
		return (DatiAnagraficiRappresentanteType) this.xmlToObj(fileName, DatiAnagraficiRappresentanteType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiRappresentanteType readDatiAnagraficiRappresentanteType(File file) throws DeserializerException {
		return (DatiAnagraficiRappresentanteType) this.xmlToObj(file, DatiAnagraficiRappresentanteType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiRappresentanteType readDatiAnagraficiRappresentanteType(InputStream in) throws DeserializerException {
		return (DatiAnagraficiRappresentanteType) this.xmlToObj(in, DatiAnagraficiRappresentanteType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiRappresentanteType readDatiAnagraficiRappresentanteType(byte[] in) throws DeserializerException {
		return (DatiAnagraficiRappresentanteType) this.xmlToObj(in, DatiAnagraficiRappresentanteType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiRappresentanteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiRappresentanteType readDatiAnagraficiRappresentanteTypeFromString(String in) throws DeserializerException {
		return (DatiAnagraficiRappresentanteType) this.xmlToObj(in.getBytes(), DatiAnagraficiRappresentanteType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AnagraficaType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AnagraficaType readAnagraficaType(String fileName) throws DeserializerException {
		return (AnagraficaType) this.xmlToObj(fileName, AnagraficaType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AnagraficaType readAnagraficaType(File file) throws DeserializerException {
		return (AnagraficaType) this.xmlToObj(file, AnagraficaType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AnagraficaType readAnagraficaType(InputStream in) throws DeserializerException {
		return (AnagraficaType) this.xmlToObj(in, AnagraficaType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AnagraficaType readAnagraficaType(byte[] in) throws DeserializerException {
		return (AnagraficaType) this.xmlToObj(in, AnagraficaType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AnagraficaType readAnagraficaTypeFromString(String in) throws DeserializerException {
		return (AnagraficaType) this.xmlToObj(in.getBytes(), AnagraficaType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiAnagraficiCedenteType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiCedenteType readDatiAnagraficiCedenteType(String fileName) throws DeserializerException {
		return (DatiAnagraficiCedenteType) this.xmlToObj(fileName, DatiAnagraficiCedenteType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiCedenteType readDatiAnagraficiCedenteType(File file) throws DeserializerException {
		return (DatiAnagraficiCedenteType) this.xmlToObj(file, DatiAnagraficiCedenteType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiCedenteType readDatiAnagraficiCedenteType(InputStream in) throws DeserializerException {
		return (DatiAnagraficiCedenteType) this.xmlToObj(in, DatiAnagraficiCedenteType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiCedenteType readDatiAnagraficiCedenteType(byte[] in) throws DeserializerException {
		return (DatiAnagraficiCedenteType) this.xmlToObj(in, DatiAnagraficiCedenteType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCedenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiCedenteType readDatiAnagraficiCedenteTypeFromString(String in) throws DeserializerException {
		return (DatiAnagraficiCedenteType) this.xmlToObj(in.getBytes(), DatiAnagraficiCedenteType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiBeniServiziType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBeniServiziType readDatiBeniServiziType(String fileName) throws DeserializerException {
		return (DatiBeniServiziType) this.xmlToObj(fileName, DatiBeniServiziType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBeniServiziType readDatiBeniServiziType(File file) throws DeserializerException {
		return (DatiBeniServiziType) this.xmlToObj(file, DatiBeniServiziType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBeniServiziType readDatiBeniServiziType(InputStream in) throws DeserializerException {
		return (DatiBeniServiziType) this.xmlToObj(in, DatiBeniServiziType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBeniServiziType readDatiBeniServiziType(byte[] in) throws DeserializerException {
		return (DatiBeniServiziType) this.xmlToObj(in, DatiBeniServiziType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBeniServiziType readDatiBeniServiziTypeFromString(String in) throws DeserializerException {
		return (DatiBeniServiziType) this.xmlToObj(in.getBytes(), DatiBeniServiziType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiRiepilogoType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiRiepilogoType readDatiRiepilogoType(String fileName) throws DeserializerException {
		return (DatiRiepilogoType) this.xmlToObj(fileName, DatiRiepilogoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiRiepilogoType readDatiRiepilogoType(File file) throws DeserializerException {
		return (DatiRiepilogoType) this.xmlToObj(file, DatiRiepilogoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiRiepilogoType readDatiRiepilogoType(InputStream in) throws DeserializerException {
		return (DatiRiepilogoType) this.xmlToObj(in, DatiRiepilogoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiRiepilogoType readDatiRiepilogoType(byte[] in) throws DeserializerException {
		return (DatiRiepilogoType) this.xmlToObj(in, DatiRiepilogoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiRiepilogoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiRiepilogoType readDatiRiepilogoTypeFromString(String in) throws DeserializerException {
		return (DatiRiepilogoType) this.xmlToObj(in.getBytes(), DatiRiepilogoType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiGeneraliDocumentoType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliDocumentoType readDatiGeneraliDocumentoType(String fileName) throws DeserializerException {
		return (DatiGeneraliDocumentoType) this.xmlToObj(fileName, DatiGeneraliDocumentoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliDocumentoType readDatiGeneraliDocumentoType(File file) throws DeserializerException {
		return (DatiGeneraliDocumentoType) this.xmlToObj(file, DatiGeneraliDocumentoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliDocumentoType readDatiGeneraliDocumentoType(InputStream in) throws DeserializerException {
		return (DatiGeneraliDocumentoType) this.xmlToObj(in, DatiGeneraliDocumentoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliDocumentoType readDatiGeneraliDocumentoType(byte[] in) throws DeserializerException {
		return (DatiGeneraliDocumentoType) this.xmlToObj(in, DatiGeneraliDocumentoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliDocumentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliDocumentoType readDatiGeneraliDocumentoTypeFromString(String in) throws DeserializerException {
		return (DatiGeneraliDocumentoType) this.xmlToObj(in.getBytes(), DatiGeneraliDocumentoType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiGeneraliType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliType readDatiGeneraliType(String fileName) throws DeserializerException {
		return (DatiGeneraliType) this.xmlToObj(fileName, DatiGeneraliType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliType readDatiGeneraliType(File file) throws DeserializerException {
		return (DatiGeneraliType) this.xmlToObj(file, DatiGeneraliType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliType readDatiGeneraliType(InputStream in) throws DeserializerException {
		return (DatiGeneraliType) this.xmlToObj(in, DatiGeneraliType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliType readDatiGeneraliType(byte[] in) throws DeserializerException {
		return (DatiGeneraliType) this.xmlToObj(in, DatiGeneraliType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliType readDatiGeneraliTypeFromString(String in) throws DeserializerException {
		return (DatiGeneraliType) this.xmlToObj(in.getBytes(), DatiGeneraliType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiSALType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiSALType readDatiSALType(String fileName) throws DeserializerException {
		return (DatiSALType) this.xmlToObj(fileName, DatiSALType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiSALType readDatiSALType(File file) throws DeserializerException {
		return (DatiSALType) this.xmlToObj(file, DatiSALType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiSALType readDatiSALType(InputStream in) throws DeserializerException {
		return (DatiSALType) this.xmlToObj(in, DatiSALType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiSALType readDatiSALType(byte[] in) throws DeserializerException {
		return (DatiSALType) this.xmlToObj(in, DatiSALType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiSALType readDatiSALTypeFromString(String in) throws DeserializerException {
		return (DatiSALType) this.xmlToObj(in.getBytes(), DatiSALType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiDDTType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiDDTType readDatiDDTType(String fileName) throws DeserializerException {
		return (DatiDDTType) this.xmlToObj(fileName, DatiDDTType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiDDTType readDatiDDTType(File file) throws DeserializerException {
		return (DatiDDTType) this.xmlToObj(file, DatiDDTType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiDDTType readDatiDDTType(InputStream in) throws DeserializerException {
		return (DatiDDTType) this.xmlToObj(in, DatiDDTType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiDDTType readDatiDDTType(byte[] in) throws DeserializerException {
		return (DatiDDTType) this.xmlToObj(in, DatiDDTType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiDDTType readDatiDDTTypeFromString(String in) throws DeserializerException {
		return (DatiDDTType) this.xmlToObj(in.getBytes(), DatiDDTType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiTrasportoType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasportoType readDatiTrasportoType(String fileName) throws DeserializerException {
		return (DatiTrasportoType) this.xmlToObj(fileName, DatiTrasportoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasportoType readDatiTrasportoType(File file) throws DeserializerException {
		return (DatiTrasportoType) this.xmlToObj(file, DatiTrasportoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasportoType readDatiTrasportoType(InputStream in) throws DeserializerException {
		return (DatiTrasportoType) this.xmlToObj(in, DatiTrasportoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasportoType readDatiTrasportoType(byte[] in) throws DeserializerException {
		return (DatiTrasportoType) this.xmlToObj(in, DatiTrasportoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasportoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasportoType readDatiTrasportoTypeFromString(String in) throws DeserializerException {
		return (DatiTrasportoType) this.xmlToObj(in.getBytes(), DatiTrasportoType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: FatturaPrincipaleType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaPrincipaleType readFatturaPrincipaleType(String fileName) throws DeserializerException {
		return (FatturaPrincipaleType) this.xmlToObj(fileName, FatturaPrincipaleType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaPrincipaleType readFatturaPrincipaleType(File file) throws DeserializerException {
		return (FatturaPrincipaleType) this.xmlToObj(file, FatturaPrincipaleType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaPrincipaleType readFatturaPrincipaleType(InputStream in) throws DeserializerException {
		return (FatturaPrincipaleType) this.xmlToObj(in, FatturaPrincipaleType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaPrincipaleType readFatturaPrincipaleType(byte[] in) throws DeserializerException {
		return (FatturaPrincipaleType) this.xmlToObj(in, FatturaPrincipaleType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaPrincipaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaPrincipaleType readFatturaPrincipaleTypeFromString(String in) throws DeserializerException {
		return (FatturaPrincipaleType) this.xmlToObj(in.getBytes(), FatturaPrincipaleType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiTrasmissioneType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasmissioneType readDatiTrasmissioneType(String fileName) throws DeserializerException {
		return (DatiTrasmissioneType) this.xmlToObj(fileName, DatiTrasmissioneType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasmissioneType readDatiTrasmissioneType(File file) throws DeserializerException {
		return (DatiTrasmissioneType) this.xmlToObj(file, DatiTrasmissioneType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasmissioneType readDatiTrasmissioneType(InputStream in) throws DeserializerException {
		return (DatiTrasmissioneType) this.xmlToObj(in, DatiTrasmissioneType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasmissioneType readDatiTrasmissioneType(byte[] in) throws DeserializerException {
		return (DatiTrasmissioneType) this.xmlToObj(in, DatiTrasmissioneType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiTrasmissioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasmissioneType readDatiTrasmissioneTypeFromString(String in) throws DeserializerException {
		return (DatiTrasmissioneType) this.xmlToObj(in.getBytes(), DatiTrasmissioneType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: FatturaElettronicaHeaderType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaHeaderType readFatturaElettronicaHeaderType(String fileName) throws DeserializerException {
		return (FatturaElettronicaHeaderType) this.xmlToObj(fileName, FatturaElettronicaHeaderType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaHeaderType readFatturaElettronicaHeaderType(File file) throws DeserializerException {
		return (FatturaElettronicaHeaderType) this.xmlToObj(file, FatturaElettronicaHeaderType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaHeaderType readFatturaElettronicaHeaderType(InputStream in) throws DeserializerException {
		return (FatturaElettronicaHeaderType) this.xmlToObj(in, FatturaElettronicaHeaderType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaHeaderType readFatturaElettronicaHeaderType(byte[] in) throws DeserializerException {
		return (FatturaElettronicaHeaderType) this.xmlToObj(in, FatturaElettronicaHeaderType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaHeaderType readFatturaElettronicaHeaderTypeFromString(String in) throws DeserializerException {
		return (FatturaElettronicaHeaderType) this.xmlToObj(in.getBytes(), FatturaElettronicaHeaderType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: CedentePrestatoreType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CedentePrestatoreType readCedentePrestatoreType(String fileName) throws DeserializerException {
		return (CedentePrestatoreType) this.xmlToObj(fileName, CedentePrestatoreType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CedentePrestatoreType readCedentePrestatoreType(File file) throws DeserializerException {
		return (CedentePrestatoreType) this.xmlToObj(file, CedentePrestatoreType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CedentePrestatoreType readCedentePrestatoreType(InputStream in) throws DeserializerException {
		return (CedentePrestatoreType) this.xmlToObj(in, CedentePrestatoreType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CedentePrestatoreType readCedentePrestatoreType(byte[] in) throws DeserializerException {
		return (CedentePrestatoreType) this.xmlToObj(in, CedentePrestatoreType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CedentePrestatoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CedentePrestatoreType readCedentePrestatoreTypeFromString(String in) throws DeserializerException {
		return (CedentePrestatoreType) this.xmlToObj(in.getBytes(), CedentePrestatoreType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RappresentanteFiscaleType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RappresentanteFiscaleType readRappresentanteFiscaleType(String fileName) throws DeserializerException {
		return (RappresentanteFiscaleType) this.xmlToObj(fileName, RappresentanteFiscaleType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RappresentanteFiscaleType readRappresentanteFiscaleType(File file) throws DeserializerException {
		return (RappresentanteFiscaleType) this.xmlToObj(file, RappresentanteFiscaleType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RappresentanteFiscaleType readRappresentanteFiscaleType(InputStream in) throws DeserializerException {
		return (RappresentanteFiscaleType) this.xmlToObj(in, RappresentanteFiscaleType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RappresentanteFiscaleType readRappresentanteFiscaleType(byte[] in) throws DeserializerException {
		return (RappresentanteFiscaleType) this.xmlToObj(in, RappresentanteFiscaleType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.RappresentanteFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RappresentanteFiscaleType readRappresentanteFiscaleTypeFromString(String in) throws DeserializerException {
		return (RappresentanteFiscaleType) this.xmlToObj(in.getBytes(), RappresentanteFiscaleType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: CessionarioCommittenteType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CessionarioCommittenteType readCessionarioCommittenteType(String fileName) throws DeserializerException {
		return (CessionarioCommittenteType) this.xmlToObj(fileName, CessionarioCommittenteType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CessionarioCommittenteType readCessionarioCommittenteType(File file) throws DeserializerException {
		return (CessionarioCommittenteType) this.xmlToObj(file, CessionarioCommittenteType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CessionarioCommittenteType readCessionarioCommittenteType(InputStream in) throws DeserializerException {
		return (CessionarioCommittenteType) this.xmlToObj(in, CessionarioCommittenteType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CessionarioCommittenteType readCessionarioCommittenteType(byte[] in) throws DeserializerException {
		return (CessionarioCommittenteType) this.xmlToObj(in, CessionarioCommittenteType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.CessionarioCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CessionarioCommittenteType readCessionarioCommittenteTypeFromString(String in) throws DeserializerException {
		return (CessionarioCommittenteType) this.xmlToObj(in.getBytes(), CessionarioCommittenteType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiAnagraficiCessionarioType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiCessionarioType readDatiAnagraficiCessionarioType(String fileName) throws DeserializerException {
		return (DatiAnagraficiCessionarioType) this.xmlToObj(fileName, DatiAnagraficiCessionarioType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiCessionarioType readDatiAnagraficiCessionarioType(File file) throws DeserializerException {
		return (DatiAnagraficiCessionarioType) this.xmlToObj(file, DatiAnagraficiCessionarioType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiCessionarioType readDatiAnagraficiCessionarioType(InputStream in) throws DeserializerException {
		return (DatiAnagraficiCessionarioType) this.xmlToObj(in, DatiAnagraficiCessionarioType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiCessionarioType readDatiAnagraficiCessionarioType(byte[] in) throws DeserializerException {
		return (DatiAnagraficiCessionarioType) this.xmlToObj(in, DatiAnagraficiCessionarioType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiCessionarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiCessionarioType readDatiAnagraficiCessionarioTypeFromString(String in) throws DeserializerException {
		return (DatiAnagraficiCessionarioType) this.xmlToObj(in.getBytes(), DatiAnagraficiCessionarioType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IndirizzoType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoType readIndirizzoType(String fileName) throws DeserializerException {
		return (IndirizzoType) this.xmlToObj(fileName, IndirizzoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoType readIndirizzoType(File file) throws DeserializerException {
		return (IndirizzoType) this.xmlToObj(file, IndirizzoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoType readIndirizzoType(InputStream in) throws DeserializerException {
		return (IndirizzoType) this.xmlToObj(in, IndirizzoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoType readIndirizzoType(byte[] in) throws DeserializerException {
		return (IndirizzoType) this.xmlToObj(in, IndirizzoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoType readIndirizzoTypeFromString(String in) throws DeserializerException {
		return (IndirizzoType) this.xmlToObj(in.getBytes(), IndirizzoType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IscrizioneREAType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IscrizioneREAType readIscrizioneREAType(String fileName) throws DeserializerException {
		return (IscrizioneREAType) this.xmlToObj(fileName, IscrizioneREAType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IscrizioneREAType readIscrizioneREAType(File file) throws DeserializerException {
		return (IscrizioneREAType) this.xmlToObj(file, IscrizioneREAType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IscrizioneREAType readIscrizioneREAType(InputStream in) throws DeserializerException {
		return (IscrizioneREAType) this.xmlToObj(in, IscrizioneREAType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IscrizioneREAType readIscrizioneREAType(byte[] in) throws DeserializerException {
		return (IscrizioneREAType) this.xmlToObj(in, IscrizioneREAType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.IscrizioneREAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IscrizioneREAType readIscrizioneREATypeFromString(String in) throws DeserializerException {
		return (IscrizioneREAType) this.xmlToObj(in.getBytes(), IscrizioneREAType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ContattiType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContattiType readContattiType(String fileName) throws DeserializerException {
		return (ContattiType) this.xmlToObj(fileName, ContattiType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContattiType readContattiType(File file) throws DeserializerException {
		return (ContattiType) this.xmlToObj(file, ContattiType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContattiType readContattiType(InputStream in) throws DeserializerException {
		return (ContattiType) this.xmlToObj(in, ContattiType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContattiType readContattiType(byte[] in) throws DeserializerException {
		return (ContattiType) this.xmlToObj(in, ContattiType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContattiType readContattiTypeFromString(String in) throws DeserializerException {
		return (ContattiType) this.xmlToObj(in.getBytes(), ContattiType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiAnagraficiVettoreType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiVettoreType readDatiAnagraficiVettoreType(String fileName) throws DeserializerException {
		return (DatiAnagraficiVettoreType) this.xmlToObj(fileName, DatiAnagraficiVettoreType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiVettoreType readDatiAnagraficiVettoreType(File file) throws DeserializerException {
		return (DatiAnagraficiVettoreType) this.xmlToObj(file, DatiAnagraficiVettoreType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiVettoreType readDatiAnagraficiVettoreType(InputStream in) throws DeserializerException {
		return (DatiAnagraficiVettoreType) this.xmlToObj(in, DatiAnagraficiVettoreType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiVettoreType readDatiAnagraficiVettoreType(byte[] in) throws DeserializerException {
		return (DatiAnagraficiVettoreType) this.xmlToObj(in, DatiAnagraficiVettoreType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiAnagraficiVettoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiAnagraficiVettoreType readDatiAnagraficiVettoreTypeFromString(String in) throws DeserializerException {
		return (DatiAnagraficiVettoreType) this.xmlToObj(in.getBytes(), DatiAnagraficiVettoreType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiVeicoliType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiVeicoliType readDatiVeicoliType(String fileName) throws DeserializerException {
		return (DatiVeicoliType) this.xmlToObj(fileName, DatiVeicoliType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiVeicoliType readDatiVeicoliType(File file) throws DeserializerException {
		return (DatiVeicoliType) this.xmlToObj(file, DatiVeicoliType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiVeicoliType readDatiVeicoliType(InputStream in) throws DeserializerException {
		return (DatiVeicoliType) this.xmlToObj(in, DatiVeicoliType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiVeicoliType readDatiVeicoliType(byte[] in) throws DeserializerException {
		return (DatiVeicoliType) this.xmlToObj(in, DatiVeicoliType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiVeicoliType readDatiVeicoliTypeFromString(String in) throws DeserializerException {
		return (DatiVeicoliType) this.xmlToObj(in.getBytes(), DatiVeicoliType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: FatturaElettronicaType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaType readFatturaElettronicaType(String fileName) throws DeserializerException {
		return (FatturaElettronicaType) this.xmlToObj(fileName, FatturaElettronicaType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaType readFatturaElettronicaType(File file) throws DeserializerException {
		return (FatturaElettronicaType) this.xmlToObj(file, FatturaElettronicaType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaType readFatturaElettronicaType(InputStream in) throws DeserializerException {
		return (FatturaElettronicaType) this.xmlToObj(in, FatturaElettronicaType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaType readFatturaElettronicaType(byte[] in) throws DeserializerException {
		return (FatturaElettronicaType) this.xmlToObj(in, FatturaElettronicaType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaType readFatturaElettronicaTypeFromString(String in) throws DeserializerException {
		return (FatturaElettronicaType) this.xmlToObj(in.getBytes(), FatturaElettronicaType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: FatturaElettronicaBodyType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaBodyType readFatturaElettronicaBodyType(String fileName) throws DeserializerException {
		return (FatturaElettronicaBodyType) this.xmlToObj(fileName, FatturaElettronicaBodyType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaBodyType readFatturaElettronicaBodyType(File file) throws DeserializerException {
		return (FatturaElettronicaBodyType) this.xmlToObj(file, FatturaElettronicaBodyType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaBodyType readFatturaElettronicaBodyType(InputStream in) throws DeserializerException {
		return (FatturaElettronicaBodyType) this.xmlToObj(in, FatturaElettronicaBodyType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaBodyType readFatturaElettronicaBodyType(byte[] in) throws DeserializerException {
		return (FatturaElettronicaBodyType) this.xmlToObj(in, FatturaElettronicaBodyType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaBodyType readFatturaElettronicaBodyTypeFromString(String in) throws DeserializerException {
		return (FatturaElettronicaBodyType) this.xmlToObj(in.getBytes(), FatturaElettronicaBodyType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiPagamentoType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiPagamentoType readDatiPagamentoType(String fileName) throws DeserializerException {
		return (DatiPagamentoType) this.xmlToObj(fileName, DatiPagamentoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiPagamentoType readDatiPagamentoType(File file) throws DeserializerException {
		return (DatiPagamentoType) this.xmlToObj(file, DatiPagamentoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiPagamentoType readDatiPagamentoType(InputStream in) throws DeserializerException {
		return (DatiPagamentoType) this.xmlToObj(in, DatiPagamentoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiPagamentoType readDatiPagamentoType(byte[] in) throws DeserializerException {
		return (DatiPagamentoType) this.xmlToObj(in, DatiPagamentoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiPagamentoType readDatiPagamentoTypeFromString(String in) throws DeserializerException {
		return (DatiPagamentoType) this.xmlToObj(in.getBytes(), DatiPagamentoType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AllegatiType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllegatiType readAllegatiType(String fileName) throws DeserializerException {
		return (AllegatiType) this.xmlToObj(fileName, AllegatiType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllegatiType readAllegatiType(File file) throws DeserializerException {
		return (AllegatiType) this.xmlToObj(file, AllegatiType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllegatiType readAllegatiType(InputStream in) throws DeserializerException {
		return (AllegatiType) this.xmlToObj(in, AllegatiType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllegatiType readAllegatiType(byte[] in) throws DeserializerException {
		return (AllegatiType) this.xmlToObj(in, AllegatiType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllegatiType readAllegatiTypeFromString(String in) throws DeserializerException {
		return (AllegatiType) this.xmlToObj(in.getBytes(), AllegatiType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiCassaPrevidenzialeType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiCassaPrevidenzialeType readDatiCassaPrevidenzialeType(String fileName) throws DeserializerException {
		return (DatiCassaPrevidenzialeType) this.xmlToObj(fileName, DatiCassaPrevidenzialeType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiCassaPrevidenzialeType readDatiCassaPrevidenzialeType(File file) throws DeserializerException {
		return (DatiCassaPrevidenzialeType) this.xmlToObj(file, DatiCassaPrevidenzialeType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiCassaPrevidenzialeType readDatiCassaPrevidenzialeType(InputStream in) throws DeserializerException {
		return (DatiCassaPrevidenzialeType) this.xmlToObj(in, DatiCassaPrevidenzialeType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiCassaPrevidenzialeType readDatiCassaPrevidenzialeType(byte[] in) throws DeserializerException {
		return (DatiCassaPrevidenzialeType) this.xmlToObj(in, DatiCassaPrevidenzialeType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiCassaPrevidenzialeType readDatiCassaPrevidenzialeTypeFromString(String in) throws DeserializerException {
		return (DatiCassaPrevidenzialeType) this.xmlToObj(in.getBytes(), DatiCassaPrevidenzialeType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiBolloType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBolloType readDatiBolloType(String fileName) throws DeserializerException {
		return (DatiBolloType) this.xmlToObj(fileName, DatiBolloType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBolloType readDatiBolloType(File file) throws DeserializerException {
		return (DatiBolloType) this.xmlToObj(file, DatiBolloType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBolloType readDatiBolloType(InputStream in) throws DeserializerException {
		return (DatiBolloType) this.xmlToObj(in, DatiBolloType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBolloType readDatiBolloType(byte[] in) throws DeserializerException {
		return (DatiBolloType) this.xmlToObj(in, DatiBolloType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBolloType readDatiBolloTypeFromString(String in) throws DeserializerException {
		return (DatiBolloType) this.xmlToObj(in.getBytes(), DatiBolloType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ContattiTrasmittenteType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContattiTrasmittenteType readContattiTrasmittenteType(String fileName) throws DeserializerException {
		return (ContattiTrasmittenteType) this.xmlToObj(fileName, ContattiTrasmittenteType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContattiTrasmittenteType readContattiTrasmittenteType(File file) throws DeserializerException {
		return (ContattiTrasmittenteType) this.xmlToObj(file, ContattiTrasmittenteType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContattiTrasmittenteType readContattiTrasmittenteType(InputStream in) throws DeserializerException {
		return (ContattiTrasmittenteType) this.xmlToObj(in, ContattiTrasmittenteType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContattiTrasmittenteType readContattiTrasmittenteType(byte[] in) throws DeserializerException {
		return (ContattiTrasmittenteType) this.xmlToObj(in, ContattiTrasmittenteType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.fatturapa.v1_1.ContattiTrasmittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContattiTrasmittenteType readContattiTrasmittenteTypeFromString(String in) throws DeserializerException {
		return (ContattiTrasmittenteType) this.xmlToObj(in.getBytes(), ContattiTrasmittenteType.class);
	}	
	
	
	

}
