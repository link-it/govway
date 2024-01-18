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
package org.openspcoop2.core.transazioni.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.IdCredenzialeMittente;
import org.openspcoop2.core.transazioni.TransazioneClasseEsiti;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.TransazioneExtendedInfo;
import org.openspcoop2.core.transazioni.TransazioneEsiti;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;
import org.openspcoop2.core.transazioni.TransazioneInfo;
import org.openspcoop2.core.transazioni.TransazioneExport;

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
	 Object: dump-allegato
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpAllegato readDumpAllegato(String fileName) throws DeserializerException {
		return (DumpAllegato) this.xmlToObj(fileName, DumpAllegato.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpAllegato readDumpAllegato(File file) throws DeserializerException {
		return (DumpAllegato) this.xmlToObj(file, DumpAllegato.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpAllegato readDumpAllegato(InputStream in) throws DeserializerException {
		return (DumpAllegato) this.xmlToObj(in, DumpAllegato.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpAllegato readDumpAllegato(byte[] in) throws DeserializerException {
		return (DumpAllegato) this.xmlToObj(in, DumpAllegato.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpAllegato readDumpAllegatoFromString(String in) throws DeserializerException {
		return (DumpAllegato) this.xmlToObj(in.getBytes(), DumpAllegato.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dump-header-allegato
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderAllegato readDumpHeaderAllegato(String fileName) throws DeserializerException {
		return (DumpHeaderAllegato) this.xmlToObj(fileName, DumpHeaderAllegato.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderAllegato readDumpHeaderAllegato(File file) throws DeserializerException {
		return (DumpHeaderAllegato) this.xmlToObj(file, DumpHeaderAllegato.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderAllegato readDumpHeaderAllegato(InputStream in) throws DeserializerException {
		return (DumpHeaderAllegato) this.xmlToObj(in, DumpHeaderAllegato.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderAllegato readDumpHeaderAllegato(byte[] in) throws DeserializerException {
		return (DumpHeaderAllegato) this.xmlToObj(in, DumpHeaderAllegato.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderAllegato readDumpHeaderAllegatoFromString(String in) throws DeserializerException {
		return (DumpHeaderAllegato) this.xmlToObj(in.getBytes(), DumpHeaderAllegato.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dump-multipart-header
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMultipartHeader readDumpMultipartHeader(String fileName) throws DeserializerException {
		return (DumpMultipartHeader) this.xmlToObj(fileName, DumpMultipartHeader.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMultipartHeader readDumpMultipartHeader(File file) throws DeserializerException {
		return (DumpMultipartHeader) this.xmlToObj(file, DumpMultipartHeader.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMultipartHeader readDumpMultipartHeader(InputStream in) throws DeserializerException {
		return (DumpMultipartHeader) this.xmlToObj(in, DumpMultipartHeader.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMultipartHeader readDumpMultipartHeader(byte[] in) throws DeserializerException {
		return (DumpMultipartHeader) this.xmlToObj(in, DumpMultipartHeader.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMultipartHeader readDumpMultipartHeaderFromString(String in) throws DeserializerException {
		return (DumpMultipartHeader) this.xmlToObj(in.getBytes(), DumpMultipartHeader.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transazione-applicativo-server
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneApplicativoServer readTransazioneApplicativoServer(String fileName) throws DeserializerException {
		return (TransazioneApplicativoServer) this.xmlToObj(fileName, TransazioneApplicativoServer.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneApplicativoServer readTransazioneApplicativoServer(File file) throws DeserializerException {
		return (TransazioneApplicativoServer) this.xmlToObj(file, TransazioneApplicativoServer.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneApplicativoServer readTransazioneApplicativoServer(InputStream in) throws DeserializerException {
		return (TransazioneApplicativoServer) this.xmlToObj(in, TransazioneApplicativoServer.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneApplicativoServer readTransazioneApplicativoServer(byte[] in) throws DeserializerException {
		return (TransazioneApplicativoServer) this.xmlToObj(in, TransazioneApplicativoServer.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneApplicativoServer readTransazioneApplicativoServerFromString(String in) throws DeserializerException {
		return (TransazioneApplicativoServer) this.xmlToObj(in.getBytes(), TransazioneApplicativoServer.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-credenziale-mittente
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdCredenzialeMittente readIdCredenzialeMittente(String fileName) throws DeserializerException {
		return (IdCredenzialeMittente) this.xmlToObj(fileName, IdCredenzialeMittente.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdCredenzialeMittente readIdCredenzialeMittente(File file) throws DeserializerException {
		return (IdCredenzialeMittente) this.xmlToObj(file, IdCredenzialeMittente.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdCredenzialeMittente readIdCredenzialeMittente(InputStream in) throws DeserializerException {
		return (IdCredenzialeMittente) this.xmlToObj(in, IdCredenzialeMittente.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdCredenzialeMittente readIdCredenzialeMittente(byte[] in) throws DeserializerException {
		return (IdCredenzialeMittente) this.xmlToObj(in, IdCredenzialeMittente.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdCredenzialeMittente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdCredenzialeMittente readIdCredenzialeMittenteFromString(String in) throws DeserializerException {
		return (IdCredenzialeMittente) this.xmlToObj(in.getBytes(), IdCredenzialeMittente.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transazione-classe-esiti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneClasseEsiti readTransazioneClasseEsiti(String fileName) throws DeserializerException {
		return (TransazioneClasseEsiti) this.xmlToObj(fileName, TransazioneClasseEsiti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneClasseEsiti readTransazioneClasseEsiti(File file) throws DeserializerException {
		return (TransazioneClasseEsiti) this.xmlToObj(file, TransazioneClasseEsiti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneClasseEsiti readTransazioneClasseEsiti(InputStream in) throws DeserializerException {
		return (TransazioneClasseEsiti) this.xmlToObj(in, TransazioneClasseEsiti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneClasseEsiti readTransazioneClasseEsiti(byte[] in) throws DeserializerException {
		return (TransazioneClasseEsiti) this.xmlToObj(in, TransazioneClasseEsiti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneClasseEsiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneClasseEsiti readTransazioneClasseEsitiFromString(String in) throws DeserializerException {
		return (TransazioneClasseEsiti) this.xmlToObj(in.getBytes(), TransazioneClasseEsiti.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dump-header-trasporto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderTrasporto readDumpHeaderTrasporto(String fileName) throws DeserializerException {
		return (DumpHeaderTrasporto) this.xmlToObj(fileName, DumpHeaderTrasporto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderTrasporto readDumpHeaderTrasporto(File file) throws DeserializerException {
		return (DumpHeaderTrasporto) this.xmlToObj(file, DumpHeaderTrasporto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderTrasporto readDumpHeaderTrasporto(InputStream in) throws DeserializerException {
		return (DumpHeaderTrasporto) this.xmlToObj(in, DumpHeaderTrasporto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderTrasporto readDumpHeaderTrasporto(byte[] in) throws DeserializerException {
		return (DumpHeaderTrasporto) this.xmlToObj(in, DumpHeaderTrasporto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderTrasporto readDumpHeaderTrasportoFromString(String in) throws DeserializerException {
		return (DumpHeaderTrasporto) this.xmlToObj(in.getBytes(), DumpHeaderTrasporto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: credenziale-mittente
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * @return Object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialeMittente readCredenzialeMittente(String fileName) throws DeserializerException {
		return (CredenzialeMittente) this.xmlToObj(fileName, CredenzialeMittente.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * @return Object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialeMittente readCredenzialeMittente(File file) throws DeserializerException {
		return (CredenzialeMittente) this.xmlToObj(file, CredenzialeMittente.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * @return Object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialeMittente readCredenzialeMittente(InputStream in) throws DeserializerException {
		return (CredenzialeMittente) this.xmlToObj(in, CredenzialeMittente.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * @return Object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialeMittente readCredenzialeMittente(byte[] in) throws DeserializerException {
		return (CredenzialeMittente) this.xmlToObj(in, CredenzialeMittente.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * @return Object type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialeMittente readCredenzialeMittenteFromString(String in) throws DeserializerException {
		return (CredenzialeMittente) this.xmlToObj(in.getBytes(), CredenzialeMittente.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dump-contenuto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpContenuto readDumpContenuto(String fileName) throws DeserializerException {
		return (DumpContenuto) this.xmlToObj(fileName, DumpContenuto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpContenuto readDumpContenuto(File file) throws DeserializerException {
		return (DumpContenuto) this.xmlToObj(file, DumpContenuto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpContenuto readDumpContenuto(InputStream in) throws DeserializerException {
		return (DumpContenuto) this.xmlToObj(in, DumpContenuto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpContenuto readDumpContenuto(byte[] in) throws DeserializerException {
		return (DumpContenuto) this.xmlToObj(in, DumpContenuto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpContenuto readDumpContenutoFromString(String in) throws DeserializerException {
		return (DumpContenuto) this.xmlToObj(in.getBytes(), DumpContenuto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-transazione-applicativo-server
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdTransazioneApplicativoServer readIdTransazioneApplicativoServer(String fileName) throws DeserializerException {
		return (IdTransazioneApplicativoServer) this.xmlToObj(fileName, IdTransazioneApplicativoServer.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdTransazioneApplicativoServer readIdTransazioneApplicativoServer(File file) throws DeserializerException {
		return (IdTransazioneApplicativoServer) this.xmlToObj(file, IdTransazioneApplicativoServer.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdTransazioneApplicativoServer readIdTransazioneApplicativoServer(InputStream in) throws DeserializerException {
		return (IdTransazioneApplicativoServer) this.xmlToObj(in, IdTransazioneApplicativoServer.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdTransazioneApplicativoServer readIdTransazioneApplicativoServer(byte[] in) throws DeserializerException {
		return (IdTransazioneApplicativoServer) this.xmlToObj(in, IdTransazioneApplicativoServer.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdTransazioneApplicativoServer readIdTransazioneApplicativoServerFromString(String in) throws DeserializerException {
		return (IdTransazioneApplicativoServer) this.xmlToObj(in.getBytes(), IdTransazioneApplicativoServer.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @return Object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transazione readTransazione(String fileName) throws DeserializerException {
		return (Transazione) this.xmlToObj(fileName, Transazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @return Object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transazione readTransazione(File file) throws DeserializerException {
		return (Transazione) this.xmlToObj(file, Transazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @return Object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transazione readTransazione(InputStream in) throws DeserializerException {
		return (Transazione) this.xmlToObj(in, Transazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @return Object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transazione readTransazione(byte[] in) throws DeserializerException {
		return (Transazione) this.xmlToObj(in, Transazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @return Object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transazione readTransazioneFromString(String in) throws DeserializerException {
		return (Transazione) this.xmlToObj(in.getBytes(), Transazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dump-messaggio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMessaggio readDumpMessaggio(String fileName) throws DeserializerException {
		return (DumpMessaggio) this.xmlToObj(fileName, DumpMessaggio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMessaggio readDumpMessaggio(File file) throws DeserializerException {
		return (DumpMessaggio) this.xmlToObj(file, DumpMessaggio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMessaggio readDumpMessaggio(InputStream in) throws DeserializerException {
		return (DumpMessaggio) this.xmlToObj(in, DumpMessaggio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMessaggio readDumpMessaggio(byte[] in) throws DeserializerException {
		return (DumpMessaggio) this.xmlToObj(in, DumpMessaggio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMessaggio readDumpMessaggioFromString(String in) throws DeserializerException {
		return (DumpMessaggio) this.xmlToObj(in.getBytes(), DumpMessaggio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transazione-extended-info
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExtendedInfo readTransazioneExtendedInfo(String fileName) throws DeserializerException {
		return (TransazioneExtendedInfo) this.xmlToObj(fileName, TransazioneExtendedInfo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExtendedInfo readTransazioneExtendedInfo(File file) throws DeserializerException {
		return (TransazioneExtendedInfo) this.xmlToObj(file, TransazioneExtendedInfo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExtendedInfo readTransazioneExtendedInfo(InputStream in) throws DeserializerException {
		return (TransazioneExtendedInfo) this.xmlToObj(in, TransazioneExtendedInfo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExtendedInfo readTransazioneExtendedInfo(byte[] in) throws DeserializerException {
		return (TransazioneExtendedInfo) this.xmlToObj(in, TransazioneExtendedInfo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExtendedInfo readTransazioneExtendedInfoFromString(String in) throws DeserializerException {
		return (TransazioneExtendedInfo) this.xmlToObj(in.getBytes(), TransazioneExtendedInfo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transazione-esiti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneEsiti readTransazioneEsiti(String fileName) throws DeserializerException {
		return (TransazioneEsiti) this.xmlToObj(fileName, TransazioneEsiti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneEsiti readTransazioneEsiti(File file) throws DeserializerException {
		return (TransazioneEsiti) this.xmlToObj(file, TransazioneEsiti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneEsiti readTransazioneEsiti(InputStream in) throws DeserializerException {
		return (TransazioneEsiti) this.xmlToObj(in, TransazioneEsiti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneEsiti readTransazioneEsiti(byte[] in) throws DeserializerException {
		return (TransazioneEsiti) this.xmlToObj(in, TransazioneEsiti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneEsiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneEsiti readTransazioneEsitiFromString(String in) throws DeserializerException {
		return (TransazioneEsiti) this.xmlToObj(in.getBytes(), TransazioneEsiti.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-dump-messaggio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdDumpMessaggio readIdDumpMessaggio(String fileName) throws DeserializerException {
		return (IdDumpMessaggio) this.xmlToObj(fileName, IdDumpMessaggio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdDumpMessaggio readIdDumpMessaggio(File file) throws DeserializerException {
		return (IdDumpMessaggio) this.xmlToObj(file, IdDumpMessaggio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdDumpMessaggio readIdDumpMessaggio(InputStream in) throws DeserializerException {
		return (IdDumpMessaggio) this.xmlToObj(in, IdDumpMessaggio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdDumpMessaggio readIdDumpMessaggio(byte[] in) throws DeserializerException {
		return (IdDumpMessaggio) this.xmlToObj(in, IdDumpMessaggio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdDumpMessaggio readIdDumpMessaggioFromString(String in) throws DeserializerException {
		return (IdDumpMessaggio) this.xmlToObj(in.getBytes(), IdDumpMessaggio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transazione-info
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneInfo readTransazioneInfo(String fileName) throws DeserializerException {
		return (TransazioneInfo) this.xmlToObj(fileName, TransazioneInfo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneInfo readTransazioneInfo(File file) throws DeserializerException {
		return (TransazioneInfo) this.xmlToObj(file, TransazioneInfo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneInfo readTransazioneInfo(InputStream in) throws DeserializerException {
		return (TransazioneInfo) this.xmlToObj(in, TransazioneInfo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneInfo readTransazioneInfo(byte[] in) throws DeserializerException {
		return (TransazioneInfo) this.xmlToObj(in, TransazioneInfo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneInfo readTransazioneInfoFromString(String in) throws DeserializerException {
		return (TransazioneInfo) this.xmlToObj(in.getBytes(), TransazioneInfo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transazione-export
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExport readTransazioneExport(String fileName) throws DeserializerException {
		return (TransazioneExport) this.xmlToObj(fileName, TransazioneExport.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExport readTransazioneExport(File file) throws DeserializerException {
		return (TransazioneExport) this.xmlToObj(file, TransazioneExport.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExport readTransazioneExport(InputStream in) throws DeserializerException {
		return (TransazioneExport) this.xmlToObj(in, TransazioneExport.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExport readTransazioneExport(byte[] in) throws DeserializerException {
		return (TransazioneExport) this.xmlToObj(in, TransazioneExport.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExport readTransazioneExportFromString(String in) throws DeserializerException {
		return (TransazioneExport) this.xmlToObj(in.getBytes(), TransazioneExport.class);
	}	
	
	
	

}
