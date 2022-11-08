/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.openspcoop2.generic_project.exception.DeserializerException;

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

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializerBase {



	/*
	 =================================================================================
	 Object: AllegatiType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllegatiType readAllegatiType(String fileName) throws DeserializerException {
		return (AllegatiType) this.xmlToObj(fileName, AllegatiType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllegatiType readAllegatiType(File file) throws DeserializerException {
		return (AllegatiType) this.xmlToObj(file, AllegatiType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllegatiType readAllegatiType(InputStream in) throws DeserializerException {
		return (AllegatiType) this.xmlToObj(in, AllegatiType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllegatiType readAllegatiType(byte[] in) throws DeserializerException {
		return (AllegatiType) this.xmlToObj(in, AllegatiType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AllegatiType readAllegatiTypeFromString(String in) throws DeserializerException {
		return (AllegatiType) this.xmlToObj(in.getBytes(), AllegatiType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiIVAType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiIVAType readDatiIVAType(String fileName) throws DeserializerException {
		return (DatiIVAType) this.xmlToObj(fileName, DatiIVAType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiIVAType readDatiIVAType(File file) throws DeserializerException {
		return (DatiIVAType) this.xmlToObj(file, DatiIVAType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiIVAType readDatiIVAType(InputStream in) throws DeserializerException {
		return (DatiIVAType) this.xmlToObj(in, DatiIVAType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiIVAType readDatiIVAType(byte[] in) throws DeserializerException {
		return (DatiIVAType) this.xmlToObj(in, DatiIVAType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiIVAType readDatiIVATypeFromString(String in) throws DeserializerException {
		return (DatiIVAType) this.xmlToObj(in.getBytes(), DatiIVAType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AltriDatiIdentificativiType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AltriDatiIdentificativiType readAltriDatiIdentificativiType(String fileName) throws DeserializerException {
		return (AltriDatiIdentificativiType) this.xmlToObj(fileName, AltriDatiIdentificativiType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AltriDatiIdentificativiType readAltriDatiIdentificativiType(File file) throws DeserializerException {
		return (AltriDatiIdentificativiType) this.xmlToObj(file, AltriDatiIdentificativiType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AltriDatiIdentificativiType readAltriDatiIdentificativiType(InputStream in) throws DeserializerException {
		return (AltriDatiIdentificativiType) this.xmlToObj(in, AltriDatiIdentificativiType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AltriDatiIdentificativiType readAltriDatiIdentificativiType(byte[] in) throws DeserializerException {
		return (AltriDatiIdentificativiType) this.xmlToObj(in, AltriDatiIdentificativiType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AltriDatiIdentificativiType readAltriDatiIdentificativiTypeFromString(String in) throws DeserializerException {
		return (AltriDatiIdentificativiType) this.xmlToObj(in.getBytes(), AltriDatiIdentificativiType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IndirizzoType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoType readIndirizzoType(String fileName) throws DeserializerException {
		return (IndirizzoType) this.xmlToObj(fileName, IndirizzoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoType readIndirizzoType(File file) throws DeserializerException {
		return (IndirizzoType) this.xmlToObj(file, IndirizzoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoType readIndirizzoType(InputStream in) throws DeserializerException {
		return (IndirizzoType) this.xmlToObj(in, IndirizzoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoType readIndirizzoType(byte[] in) throws DeserializerException {
		return (IndirizzoType) this.xmlToObj(in, IndirizzoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoType readIndirizzoTypeFromString(String in) throws DeserializerException {
		return (IndirizzoType) this.xmlToObj(in.getBytes(), IndirizzoType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RappresentanteFiscaleType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RappresentanteFiscaleType readRappresentanteFiscaleType(String fileName) throws DeserializerException {
		return (RappresentanteFiscaleType) this.xmlToObj(fileName, RappresentanteFiscaleType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RappresentanteFiscaleType readRappresentanteFiscaleType(File file) throws DeserializerException {
		return (RappresentanteFiscaleType) this.xmlToObj(file, RappresentanteFiscaleType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RappresentanteFiscaleType readRappresentanteFiscaleType(InputStream in) throws DeserializerException {
		return (RappresentanteFiscaleType) this.xmlToObj(in, RappresentanteFiscaleType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RappresentanteFiscaleType readRappresentanteFiscaleType(byte[] in) throws DeserializerException {
		return (RappresentanteFiscaleType) this.xmlToObj(in, RappresentanteFiscaleType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RappresentanteFiscaleType readRappresentanteFiscaleTypeFromString(String in) throws DeserializerException {
		return (RappresentanteFiscaleType) this.xmlToObj(in.getBytes(), RappresentanteFiscaleType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IdFiscaleType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFiscaleType readIdFiscaleType(String fileName) throws DeserializerException {
		return (IdFiscaleType) this.xmlToObj(fileName, IdFiscaleType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFiscaleType readIdFiscaleType(File file) throws DeserializerException {
		return (IdFiscaleType) this.xmlToObj(file, IdFiscaleType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFiscaleType readIdFiscaleType(InputStream in) throws DeserializerException {
		return (IdFiscaleType) this.xmlToObj(in, IdFiscaleType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFiscaleType readIdFiscaleType(byte[] in) throws DeserializerException {
		return (IdFiscaleType) this.xmlToObj(in, IdFiscaleType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFiscaleType readIdFiscaleTypeFromString(String in) throws DeserializerException {
		return (IdFiscaleType) this.xmlToObj(in.getBytes(), IdFiscaleType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IdentificativiFiscaliType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificativiFiscaliType readIdentificativiFiscaliType(String fileName) throws DeserializerException {
		return (IdentificativiFiscaliType) this.xmlToObj(fileName, IdentificativiFiscaliType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificativiFiscaliType readIdentificativiFiscaliType(File file) throws DeserializerException {
		return (IdentificativiFiscaliType) this.xmlToObj(file, IdentificativiFiscaliType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificativiFiscaliType readIdentificativiFiscaliType(InputStream in) throws DeserializerException {
		return (IdentificativiFiscaliType) this.xmlToObj(in, IdentificativiFiscaliType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificativiFiscaliType readIdentificativiFiscaliType(byte[] in) throws DeserializerException {
		return (IdentificativiFiscaliType) this.xmlToObj(in, IdentificativiFiscaliType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdentificativiFiscaliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificativiFiscaliType readIdentificativiFiscaliTypeFromString(String in) throws DeserializerException {
		return (IdentificativiFiscaliType) this.xmlToObj(in.getBytes(), IdentificativiFiscaliType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiGeneraliDocumentoType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliDocumentoType readDatiGeneraliDocumentoType(String fileName) throws DeserializerException {
		return (DatiGeneraliDocumentoType) this.xmlToObj(fileName, DatiGeneraliDocumentoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliDocumentoType readDatiGeneraliDocumentoType(File file) throws DeserializerException {
		return (DatiGeneraliDocumentoType) this.xmlToObj(file, DatiGeneraliDocumentoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliDocumentoType readDatiGeneraliDocumentoType(InputStream in) throws DeserializerException {
		return (DatiGeneraliDocumentoType) this.xmlToObj(in, DatiGeneraliDocumentoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliDocumentoType readDatiGeneraliDocumentoType(byte[] in) throws DeserializerException {
		return (DatiGeneraliDocumentoType) this.xmlToObj(in, DatiGeneraliDocumentoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliType readDatiGeneraliType(String fileName) throws DeserializerException {
		return (DatiGeneraliType) this.xmlToObj(fileName, DatiGeneraliType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliType readDatiGeneraliType(File file) throws DeserializerException {
		return (DatiGeneraliType) this.xmlToObj(file, DatiGeneraliType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliType readDatiGeneraliType(InputStream in) throws DeserializerException {
		return (DatiGeneraliType) this.xmlToObj(in, DatiGeneraliType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliType readDatiGeneraliType(byte[] in) throws DeserializerException {
		return (DatiGeneraliType) this.xmlToObj(in, DatiGeneraliType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiGeneraliType readDatiGeneraliTypeFromString(String in) throws DeserializerException {
		return (DatiGeneraliType) this.xmlToObj(in.getBytes(), DatiGeneraliType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiFatturaRettificataType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiFatturaRettificataType readDatiFatturaRettificataType(String fileName) throws DeserializerException {
		return (DatiFatturaRettificataType) this.xmlToObj(fileName, DatiFatturaRettificataType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiFatturaRettificataType readDatiFatturaRettificataType(File file) throws DeserializerException {
		return (DatiFatturaRettificataType) this.xmlToObj(file, DatiFatturaRettificataType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiFatturaRettificataType readDatiFatturaRettificataType(InputStream in) throws DeserializerException {
		return (DatiFatturaRettificataType) this.xmlToObj(in, DatiFatturaRettificataType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiFatturaRettificataType readDatiFatturaRettificataType(byte[] in) throws DeserializerException {
		return (DatiFatturaRettificataType) this.xmlToObj(in, DatiFatturaRettificataType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiFatturaRettificataType readDatiFatturaRettificataTypeFromString(String in) throws DeserializerException {
		return (DatiFatturaRettificataType) this.xmlToObj(in.getBytes(), DatiFatturaRettificataType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: FatturaElettronicaBodyType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaBodyType readFatturaElettronicaBodyType(String fileName) throws DeserializerException {
		return (FatturaElettronicaBodyType) this.xmlToObj(fileName, FatturaElettronicaBodyType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaBodyType readFatturaElettronicaBodyType(File file) throws DeserializerException {
		return (FatturaElettronicaBodyType) this.xmlToObj(file, FatturaElettronicaBodyType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaBodyType readFatturaElettronicaBodyType(InputStream in) throws DeserializerException {
		return (FatturaElettronicaBodyType) this.xmlToObj(in, FatturaElettronicaBodyType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaBodyType readFatturaElettronicaBodyType(byte[] in) throws DeserializerException {
		return (FatturaElettronicaBodyType) this.xmlToObj(in, FatturaElettronicaBodyType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaBodyType readFatturaElettronicaBodyTypeFromString(String in) throws DeserializerException {
		return (FatturaElettronicaBodyType) this.xmlToObj(in.getBytes(), FatturaElettronicaBodyType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiBeniServiziType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBeniServiziType readDatiBeniServiziType(String fileName) throws DeserializerException {
		return (DatiBeniServiziType) this.xmlToObj(fileName, DatiBeniServiziType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBeniServiziType readDatiBeniServiziType(File file) throws DeserializerException {
		return (DatiBeniServiziType) this.xmlToObj(file, DatiBeniServiziType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBeniServiziType readDatiBeniServiziType(InputStream in) throws DeserializerException {
		return (DatiBeniServiziType) this.xmlToObj(in, DatiBeniServiziType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBeniServiziType readDatiBeniServiziType(byte[] in) throws DeserializerException {
		return (DatiBeniServiziType) this.xmlToObj(in, DatiBeniServiziType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiBeniServiziType readDatiBeniServiziTypeFromString(String in) throws DeserializerException {
		return (DatiBeniServiziType) this.xmlToObj(in.getBytes(), DatiBeniServiziType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: FatturaElettronicaHeaderType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaHeaderType readFatturaElettronicaHeaderType(String fileName) throws DeserializerException {
		return (FatturaElettronicaHeaderType) this.xmlToObj(fileName, FatturaElettronicaHeaderType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaHeaderType readFatturaElettronicaHeaderType(File file) throws DeserializerException {
		return (FatturaElettronicaHeaderType) this.xmlToObj(file, FatturaElettronicaHeaderType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaHeaderType readFatturaElettronicaHeaderType(InputStream in) throws DeserializerException {
		return (FatturaElettronicaHeaderType) this.xmlToObj(in, FatturaElettronicaHeaderType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaHeaderType readFatturaElettronicaHeaderType(byte[] in) throws DeserializerException {
		return (FatturaElettronicaHeaderType) this.xmlToObj(in, FatturaElettronicaHeaderType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaHeaderType readFatturaElettronicaHeaderTypeFromString(String in) throws DeserializerException {
		return (FatturaElettronicaHeaderType) this.xmlToObj(in.getBytes(), FatturaElettronicaHeaderType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: FatturaElettronicaType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaType readFatturaElettronicaType(String fileName) throws DeserializerException {
		return (FatturaElettronicaType) this.xmlToObj(fileName, FatturaElettronicaType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaType readFatturaElettronicaType(File file) throws DeserializerException {
		return (FatturaElettronicaType) this.xmlToObj(file, FatturaElettronicaType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaType readFatturaElettronicaType(InputStream in) throws DeserializerException {
		return (FatturaElettronicaType) this.xmlToObj(in, FatturaElettronicaType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaType readFatturaElettronicaType(byte[] in) throws DeserializerException {
		return (FatturaElettronicaType) this.xmlToObj(in, FatturaElettronicaType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FatturaElettronicaType readFatturaElettronicaTypeFromString(String in) throws DeserializerException {
		return (FatturaElettronicaType) this.xmlToObj(in.getBytes(), FatturaElettronicaType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiTrasmissioneType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasmissioneType readDatiTrasmissioneType(String fileName) throws DeserializerException {
		return (DatiTrasmissioneType) this.xmlToObj(fileName, DatiTrasmissioneType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasmissioneType readDatiTrasmissioneType(File file) throws DeserializerException {
		return (DatiTrasmissioneType) this.xmlToObj(file, DatiTrasmissioneType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasmissioneType readDatiTrasmissioneType(InputStream in) throws DeserializerException {
		return (DatiTrasmissioneType) this.xmlToObj(in, DatiTrasmissioneType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasmissioneType readDatiTrasmissioneType(byte[] in) throws DeserializerException {
		return (DatiTrasmissioneType) this.xmlToObj(in, DatiTrasmissioneType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiTrasmissioneType readDatiTrasmissioneTypeFromString(String in) throws DeserializerException {
		return (DatiTrasmissioneType) this.xmlToObj(in.getBytes(), DatiTrasmissioneType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: CedentePrestatoreType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CedentePrestatoreType readCedentePrestatoreType(String fileName) throws DeserializerException {
		return (CedentePrestatoreType) this.xmlToObj(fileName, CedentePrestatoreType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CedentePrestatoreType readCedentePrestatoreType(File file) throws DeserializerException {
		return (CedentePrestatoreType) this.xmlToObj(file, CedentePrestatoreType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CedentePrestatoreType readCedentePrestatoreType(InputStream in) throws DeserializerException {
		return (CedentePrestatoreType) this.xmlToObj(in, CedentePrestatoreType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CedentePrestatoreType readCedentePrestatoreType(byte[] in) throws DeserializerException {
		return (CedentePrestatoreType) this.xmlToObj(in, CedentePrestatoreType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CedentePrestatoreType readCedentePrestatoreTypeFromString(String in) throws DeserializerException {
		return (CedentePrestatoreType) this.xmlToObj(in.getBytes(), CedentePrestatoreType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: CessionarioCommittenteType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CessionarioCommittenteType readCessionarioCommittenteType(String fileName) throws DeserializerException {
		return (CessionarioCommittenteType) this.xmlToObj(fileName, CessionarioCommittenteType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CessionarioCommittenteType readCessionarioCommittenteType(File file) throws DeserializerException {
		return (CessionarioCommittenteType) this.xmlToObj(file, CessionarioCommittenteType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CessionarioCommittenteType readCessionarioCommittenteType(InputStream in) throws DeserializerException {
		return (CessionarioCommittenteType) this.xmlToObj(in, CessionarioCommittenteType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CessionarioCommittenteType readCessionarioCommittenteType(byte[] in) throws DeserializerException {
		return (CessionarioCommittenteType) this.xmlToObj(in, CessionarioCommittenteType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CessionarioCommittenteType readCessionarioCommittenteTypeFromString(String in) throws DeserializerException {
		return (CessionarioCommittenteType) this.xmlToObj(in.getBytes(), CessionarioCommittenteType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IscrizioneREAType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IscrizioneREAType readIscrizioneREAType(String fileName) throws DeserializerException {
		return (IscrizioneREAType) this.xmlToObj(fileName, IscrizioneREAType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IscrizioneREAType readIscrizioneREAType(File file) throws DeserializerException {
		return (IscrizioneREAType) this.xmlToObj(file, IscrizioneREAType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IscrizioneREAType readIscrizioneREAType(InputStream in) throws DeserializerException {
		return (IscrizioneREAType) this.xmlToObj(in, IscrizioneREAType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IscrizioneREAType readIscrizioneREAType(byte[] in) throws DeserializerException {
		return (IscrizioneREAType) this.xmlToObj(in, IscrizioneREAType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IscrizioneREAType readIscrizioneREATypeFromString(String in) throws DeserializerException {
		return (IscrizioneREAType) this.xmlToObj(in.getBytes(), IscrizioneREAType.class);
	}	
	
	
	

}
