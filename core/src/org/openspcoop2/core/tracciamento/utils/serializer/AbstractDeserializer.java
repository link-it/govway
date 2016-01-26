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
package org.openspcoop2.core.tracciamento.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.tracciamento.Dominio;
import org.openspcoop2.core.tracciamento.Traccia;
import org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione;
import org.openspcoop2.core.tracciamento.Busta;
import org.openspcoop2.core.tracciamento.Allegati;
import org.openspcoop2.core.tracciamento.Inoltro;
import org.openspcoop2.core.tracciamento.ProfiloTrasmissione;
import org.openspcoop2.core.tracciamento.IdTraccia;
import org.openspcoop2.core.tracciamento.DominioIdTraccia;
import org.openspcoop2.core.tracciamento.Allegato;
import org.openspcoop2.core.tracciamento.Soggetto;
import org.openspcoop2.core.tracciamento.ProfiloCollaborazione;
import org.openspcoop2.core.tracciamento.Servizio;
import org.openspcoop2.core.tracciamento.Data;
import org.openspcoop2.core.tracciamento.Trasmissioni;
import org.openspcoop2.core.tracciamento.Riscontri;
import org.openspcoop2.core.tracciamento.Eccezioni;
import org.openspcoop2.core.tracciamento.Protocollo;
import org.openspcoop2.core.tracciamento.TipoData;
import org.openspcoop2.core.tracciamento.SoggettoIdentificativo;
import org.openspcoop2.core.tracciamento.DominioSoggetto;
import org.openspcoop2.core.tracciamento.Riscontro;
import org.openspcoop2.core.tracciamento.Eccezione;
import org.openspcoop2.core.tracciamento.CodiceEccezione;
import org.openspcoop2.core.tracciamento.ContestoCodificaEccezione;
import org.openspcoop2.core.tracciamento.RilevanzaEccezione;
import org.openspcoop2.core.tracciamento.Proprieta;
import org.openspcoop2.core.tracciamento.Trasmissione;
import org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto;

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
	 Object: dominio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(String fileName) throws DeserializerException {
		return (Dominio) this.xmlToObj(fileName, Dominio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(File file) throws DeserializerException {
		return (Dominio) this.xmlToObj(file, Dominio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(InputStream in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in, Dominio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(byte[] in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in, Dominio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominioFromString(String in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in.getBytes(), Dominio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: traccia
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Traccia readTraccia(String fileName) throws DeserializerException {
		return (Traccia) this.xmlToObj(fileName, Traccia.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Traccia readTraccia(File file) throws DeserializerException {
		return (Traccia) this.xmlToObj(file, Traccia.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Traccia readTraccia(InputStream in) throws DeserializerException {
		return (Traccia) this.xmlToObj(in, Traccia.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Traccia readTraccia(byte[] in) throws DeserializerException {
		return (Traccia) this.xmlToObj(in, Traccia.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Traccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Traccia readTracciaFromString(String in) throws DeserializerException {
		return (Traccia) this.xmlToObj(in.getBytes(), Traccia.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: traccia-esito-elaborazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TracciaEsitoElaborazione readTracciaEsitoElaborazione(String fileName) throws DeserializerException {
		return (TracciaEsitoElaborazione) this.xmlToObj(fileName, TracciaEsitoElaborazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TracciaEsitoElaborazione readTracciaEsitoElaborazione(File file) throws DeserializerException {
		return (TracciaEsitoElaborazione) this.xmlToObj(file, TracciaEsitoElaborazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TracciaEsitoElaborazione readTracciaEsitoElaborazione(InputStream in) throws DeserializerException {
		return (TracciaEsitoElaborazione) this.xmlToObj(in, TracciaEsitoElaborazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TracciaEsitoElaborazione readTracciaEsitoElaborazione(byte[] in) throws DeserializerException {
		return (TracciaEsitoElaborazione) this.xmlToObj(in, TracciaEsitoElaborazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TracciaEsitoElaborazione readTracciaEsitoElaborazioneFromString(String in) throws DeserializerException {
		return (TracciaEsitoElaborazione) this.xmlToObj(in.getBytes(), TracciaEsitoElaborazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: busta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Busta readBusta(String fileName) throws DeserializerException {
		return (Busta) this.xmlToObj(fileName, Busta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Busta readBusta(File file) throws DeserializerException {
		return (Busta) this.xmlToObj(file, Busta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Busta readBusta(InputStream in) throws DeserializerException {
		return (Busta) this.xmlToObj(in, Busta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Busta readBusta(byte[] in) throws DeserializerException {
		return (Busta) this.xmlToObj(in, Busta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Busta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Busta readBustaFromString(String in) throws DeserializerException {
		return (Busta) this.xmlToObj(in.getBytes(), Busta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: allegati
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allegati readAllegati(String fileName) throws DeserializerException {
		return (Allegati) this.xmlToObj(fileName, Allegati.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allegati readAllegati(File file) throws DeserializerException {
		return (Allegati) this.xmlToObj(file, Allegati.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allegati readAllegati(InputStream in) throws DeserializerException {
		return (Allegati) this.xmlToObj(in, Allegati.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allegati readAllegati(byte[] in) throws DeserializerException {
		return (Allegati) this.xmlToObj(in, Allegati.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Allegati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allegati readAllegatiFromString(String in) throws DeserializerException {
		return (Allegati) this.xmlToObj(in.getBytes(), Allegati.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: inoltro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Inoltro readInoltro(String fileName) throws DeserializerException {
		return (Inoltro) this.xmlToObj(fileName, Inoltro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Inoltro readInoltro(File file) throws DeserializerException {
		return (Inoltro) this.xmlToObj(file, Inoltro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Inoltro readInoltro(InputStream in) throws DeserializerException {
		return (Inoltro) this.xmlToObj(in, Inoltro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Inoltro readInoltro(byte[] in) throws DeserializerException {
		return (Inoltro) this.xmlToObj(in, Inoltro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Inoltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Inoltro readInoltroFromString(String in) throws DeserializerException {
		return (Inoltro) this.xmlToObj(in.getBytes(), Inoltro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: profilo-trasmissione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloTrasmissione readProfiloTrasmissione(String fileName) throws DeserializerException {
		return (ProfiloTrasmissione) this.xmlToObj(fileName, ProfiloTrasmissione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloTrasmissione readProfiloTrasmissione(File file) throws DeserializerException {
		return (ProfiloTrasmissione) this.xmlToObj(file, ProfiloTrasmissione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloTrasmissione readProfiloTrasmissione(InputStream in) throws DeserializerException {
		return (ProfiloTrasmissione) this.xmlToObj(in, ProfiloTrasmissione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloTrasmissione readProfiloTrasmissione(byte[] in) throws DeserializerException {
		return (ProfiloTrasmissione) this.xmlToObj(in, ProfiloTrasmissione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ProfiloTrasmissione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloTrasmissione readProfiloTrasmissioneFromString(String in) throws DeserializerException {
		return (ProfiloTrasmissione) this.xmlToObj(in.getBytes(), ProfiloTrasmissione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-traccia
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdTraccia readIdTraccia(String fileName) throws DeserializerException {
		return (IdTraccia) this.xmlToObj(fileName, IdTraccia.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdTraccia readIdTraccia(File file) throws DeserializerException {
		return (IdTraccia) this.xmlToObj(file, IdTraccia.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdTraccia readIdTraccia(InputStream in) throws DeserializerException {
		return (IdTraccia) this.xmlToObj(in, IdTraccia.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdTraccia readIdTraccia(byte[] in) throws DeserializerException {
		return (IdTraccia) this.xmlToObj(in, IdTraccia.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.IdTraccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdTraccia readIdTracciaFromString(String in) throws DeserializerException {
		return (IdTraccia) this.xmlToObj(in.getBytes(), IdTraccia.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dominio-id-traccia
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioIdTraccia readDominioIdTraccia(String fileName) throws DeserializerException {
		return (DominioIdTraccia) this.xmlToObj(fileName, DominioIdTraccia.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioIdTraccia readDominioIdTraccia(File file) throws DeserializerException {
		return (DominioIdTraccia) this.xmlToObj(file, DominioIdTraccia.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioIdTraccia readDominioIdTraccia(InputStream in) throws DeserializerException {
		return (DominioIdTraccia) this.xmlToObj(in, DominioIdTraccia.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioIdTraccia readDominioIdTraccia(byte[] in) throws DeserializerException {
		return (DominioIdTraccia) this.xmlToObj(in, DominioIdTraccia.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioIdTraccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioIdTraccia readDominioIdTracciaFromString(String in) throws DeserializerException {
		return (DominioIdTraccia) this.xmlToObj(in.getBytes(), DominioIdTraccia.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: allegato
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allegato readAllegato(String fileName) throws DeserializerException {
		return (Allegato) this.xmlToObj(fileName, Allegato.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allegato readAllegato(File file) throws DeserializerException {
		return (Allegato) this.xmlToObj(file, Allegato.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allegato readAllegato(InputStream in) throws DeserializerException {
		return (Allegato) this.xmlToObj(in, Allegato.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allegato readAllegato(byte[] in) throws DeserializerException {
		return (Allegato) this.xmlToObj(in, Allegato.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Allegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Allegato readAllegatoFromString(String in) throws DeserializerException {
		return (Allegato) this.xmlToObj(in.getBytes(), Allegato.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(String fileName) throws DeserializerException {
		return (Soggetto) this.xmlToObj(fileName, Soggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(File file) throws DeserializerException {
		return (Soggetto) this.xmlToObj(file, Soggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(InputStream in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(byte[] in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggettoFromString(String in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in.getBytes(), Soggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: profilo-collaborazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloCollaborazione readProfiloCollaborazione(String fileName) throws DeserializerException {
		return (ProfiloCollaborazione) this.xmlToObj(fileName, ProfiloCollaborazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloCollaborazione readProfiloCollaborazione(File file) throws DeserializerException {
		return (ProfiloCollaborazione) this.xmlToObj(file, ProfiloCollaborazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloCollaborazione readProfiloCollaborazione(InputStream in) throws DeserializerException {
		return (ProfiloCollaborazione) this.xmlToObj(in, ProfiloCollaborazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloCollaborazione readProfiloCollaborazione(byte[] in) throws DeserializerException {
		return (ProfiloCollaborazione) this.xmlToObj(in, ProfiloCollaborazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ProfiloCollaborazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloCollaborazione readProfiloCollaborazioneFromString(String in) throws DeserializerException {
		return (ProfiloCollaborazione) this.xmlToObj(in.getBytes(), ProfiloCollaborazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(String fileName) throws DeserializerException {
		return (Servizio) this.xmlToObj(fileName, Servizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(File file) throws DeserializerException {
		return (Servizio) this.xmlToObj(file, Servizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(InputStream in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in, Servizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(byte[] in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in, Servizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizioFromString(String in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in.getBytes(), Servizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: data
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Data}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Data}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Data readData(String fileName) throws DeserializerException {
		return (Data) this.xmlToObj(fileName, Data.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Data}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Data}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Data readData(File file) throws DeserializerException {
		return (Data) this.xmlToObj(file, Data.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Data}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Data}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Data readData(InputStream in) throws DeserializerException {
		return (Data) this.xmlToObj(in, Data.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Data}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Data}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Data readData(byte[] in) throws DeserializerException {
		return (Data) this.xmlToObj(in, Data.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Data}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Data}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Data}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Data readDataFromString(String in) throws DeserializerException {
		return (Data) this.xmlToObj(in.getBytes(), Data.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: trasmissioni
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Trasmissioni readTrasmissioni(String fileName) throws DeserializerException {
		return (Trasmissioni) this.xmlToObj(fileName, Trasmissioni.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Trasmissioni readTrasmissioni(File file) throws DeserializerException {
		return (Trasmissioni) this.xmlToObj(file, Trasmissioni.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Trasmissioni readTrasmissioni(InputStream in) throws DeserializerException {
		return (Trasmissioni) this.xmlToObj(in, Trasmissioni.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Trasmissioni readTrasmissioni(byte[] in) throws DeserializerException {
		return (Trasmissioni) this.xmlToObj(in, Trasmissioni.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Trasmissioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Trasmissioni readTrasmissioniFromString(String in) throws DeserializerException {
		return (Trasmissioni) this.xmlToObj(in.getBytes(), Trasmissioni.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: riscontri
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Riscontri readRiscontri(String fileName) throws DeserializerException {
		return (Riscontri) this.xmlToObj(fileName, Riscontri.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Riscontri readRiscontri(File file) throws DeserializerException {
		return (Riscontri) this.xmlToObj(file, Riscontri.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Riscontri readRiscontri(InputStream in) throws DeserializerException {
		return (Riscontri) this.xmlToObj(in, Riscontri.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Riscontri readRiscontri(byte[] in) throws DeserializerException {
		return (Riscontri) this.xmlToObj(in, Riscontri.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Riscontri}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Riscontri readRiscontriFromString(String in) throws DeserializerException {
		return (Riscontri) this.xmlToObj(in.getBytes(), Riscontri.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: eccezioni
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezioni readEccezioni(String fileName) throws DeserializerException {
		return (Eccezioni) this.xmlToObj(fileName, Eccezioni.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezioni readEccezioni(File file) throws DeserializerException {
		return (Eccezioni) this.xmlToObj(file, Eccezioni.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezioni readEccezioni(InputStream in) throws DeserializerException {
		return (Eccezioni) this.xmlToObj(in, Eccezioni.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezioni readEccezioni(byte[] in) throws DeserializerException {
		return (Eccezioni) this.xmlToObj(in, Eccezioni.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Eccezioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezioni readEccezioniFromString(String in) throws DeserializerException {
		return (Eccezioni) this.xmlToObj(in.getBytes(), Eccezioni.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: protocollo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(String fileName) throws DeserializerException {
		return (Protocollo) this.xmlToObj(fileName, Protocollo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(File file) throws DeserializerException {
		return (Protocollo) this.xmlToObj(file, Protocollo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(InputStream in) throws DeserializerException {
		return (Protocollo) this.xmlToObj(in, Protocollo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(byte[] in) throws DeserializerException {
		return (Protocollo) this.xmlToObj(in, Protocollo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocolloFromString(String in) throws DeserializerException {
		return (Protocollo) this.xmlToObj(in.getBytes(), Protocollo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: TipoData
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TipoData readTipoData(String fileName) throws DeserializerException {
		return (TipoData) this.xmlToObj(fileName, TipoData.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TipoData readTipoData(File file) throws DeserializerException {
		return (TipoData) this.xmlToObj(file, TipoData.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TipoData readTipoData(InputStream in) throws DeserializerException {
		return (TipoData) this.xmlToObj(in, TipoData.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TipoData readTipoData(byte[] in) throws DeserializerException {
		return (TipoData) this.xmlToObj(in, TipoData.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.TipoData}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TipoData readTipoDataFromString(String in) throws DeserializerException {
		return (TipoData) this.xmlToObj(in.getBytes(), TipoData.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetto-identificativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(String fileName) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(fileName, SoggettoIdentificativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(File file) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(file, SoggettoIdentificativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(InputStream in) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(in, SoggettoIdentificativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(byte[] in) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(in, SoggettoIdentificativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativoFromString(String in) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(in.getBytes(), SoggettoIdentificativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dominio-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(String fileName) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(fileName, DominioSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(File file) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(file, DominioSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(InputStream in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(byte[] in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggettoFromString(String in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in.getBytes(), DominioSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: riscontro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Riscontro readRiscontro(String fileName) throws DeserializerException {
		return (Riscontro) this.xmlToObj(fileName, Riscontro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Riscontro readRiscontro(File file) throws DeserializerException {
		return (Riscontro) this.xmlToObj(file, Riscontro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Riscontro readRiscontro(InputStream in) throws DeserializerException {
		return (Riscontro) this.xmlToObj(in, Riscontro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Riscontro readRiscontro(byte[] in) throws DeserializerException {
		return (Riscontro) this.xmlToObj(in, Riscontro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Riscontro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Riscontro readRiscontroFromString(String in) throws DeserializerException {
		return (Riscontro) this.xmlToObj(in.getBytes(), Riscontro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: eccezione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(String fileName) throws DeserializerException {
		return (Eccezione) this.xmlToObj(fileName, Eccezione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(File file) throws DeserializerException {
		return (Eccezione) this.xmlToObj(file, Eccezione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(InputStream in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in, Eccezione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(byte[] in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in, Eccezione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezioneFromString(String in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in.getBytes(), Eccezione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: CodiceEccezione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceEccezione readCodiceEccezione(String fileName) throws DeserializerException {
		return (CodiceEccezione) this.xmlToObj(fileName, CodiceEccezione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceEccezione readCodiceEccezione(File file) throws DeserializerException {
		return (CodiceEccezione) this.xmlToObj(file, CodiceEccezione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceEccezione readCodiceEccezione(InputStream in) throws DeserializerException {
		return (CodiceEccezione) this.xmlToObj(in, CodiceEccezione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceEccezione readCodiceEccezione(byte[] in) throws DeserializerException {
		return (CodiceEccezione) this.xmlToObj(in, CodiceEccezione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.CodiceEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceEccezione readCodiceEccezioneFromString(String in) throws DeserializerException {
		return (CodiceEccezione) this.xmlToObj(in.getBytes(), CodiceEccezione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ContestoCodificaEccezione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContestoCodificaEccezione readContestoCodificaEccezione(String fileName) throws DeserializerException {
		return (ContestoCodificaEccezione) this.xmlToObj(fileName, ContestoCodificaEccezione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContestoCodificaEccezione readContestoCodificaEccezione(File file) throws DeserializerException {
		return (ContestoCodificaEccezione) this.xmlToObj(file, ContestoCodificaEccezione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContestoCodificaEccezione readContestoCodificaEccezione(InputStream in) throws DeserializerException {
		return (ContestoCodificaEccezione) this.xmlToObj(in, ContestoCodificaEccezione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContestoCodificaEccezione readContestoCodificaEccezione(byte[] in) throws DeserializerException {
		return (ContestoCodificaEccezione) this.xmlToObj(in, ContestoCodificaEccezione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.ContestoCodificaEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContestoCodificaEccezione readContestoCodificaEccezioneFromString(String in) throws DeserializerException {
		return (ContestoCodificaEccezione) this.xmlToObj(in.getBytes(), ContestoCodificaEccezione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RilevanzaEccezione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RilevanzaEccezione readRilevanzaEccezione(String fileName) throws DeserializerException {
		return (RilevanzaEccezione) this.xmlToObj(fileName, RilevanzaEccezione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RilevanzaEccezione readRilevanzaEccezione(File file) throws DeserializerException {
		return (RilevanzaEccezione) this.xmlToObj(file, RilevanzaEccezione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RilevanzaEccezione readRilevanzaEccezione(InputStream in) throws DeserializerException {
		return (RilevanzaEccezione) this.xmlToObj(in, RilevanzaEccezione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RilevanzaEccezione readRilevanzaEccezione(byte[] in) throws DeserializerException {
		return (RilevanzaEccezione) this.xmlToObj(in, RilevanzaEccezione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.RilevanzaEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RilevanzaEccezione readRilevanzaEccezioneFromString(String in) throws DeserializerException {
		return (RilevanzaEccezione) this.xmlToObj(in.getBytes(), RilevanzaEccezione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: proprieta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(String fileName) throws DeserializerException {
		return (Proprieta) this.xmlToObj(fileName, Proprieta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(File file) throws DeserializerException {
		return (Proprieta) this.xmlToObj(file, Proprieta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(InputStream in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(byte[] in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprietaFromString(String in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in.getBytes(), Proprieta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: trasmissione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Trasmissione readTrasmissione(String fileName) throws DeserializerException {
		return (Trasmissione) this.xmlToObj(fileName, Trasmissione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Trasmissione readTrasmissione(File file) throws DeserializerException {
		return (Trasmissione) this.xmlToObj(file, Trasmissione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Trasmissione readTrasmissione(InputStream in) throws DeserializerException {
		return (Trasmissione) this.xmlToObj(in, Trasmissione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Trasmissione readTrasmissione(byte[] in) throws DeserializerException {
		return (Trasmissione) this.xmlToObj(in, Trasmissione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.Trasmissione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Trasmissione readTrasmissioneFromString(String in) throws DeserializerException {
		return (Trasmissione) this.xmlToObj(in.getBytes(), Trasmissione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dominio-id-traccia-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioIdTracciaSoggetto readDominioIdTracciaSoggetto(String fileName) throws DeserializerException {
		return (DominioIdTracciaSoggetto) this.xmlToObj(fileName, DominioIdTracciaSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioIdTracciaSoggetto readDominioIdTracciaSoggetto(File file) throws DeserializerException {
		return (DominioIdTracciaSoggetto) this.xmlToObj(file, DominioIdTracciaSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioIdTracciaSoggetto readDominioIdTracciaSoggetto(InputStream in) throws DeserializerException {
		return (DominioIdTracciaSoggetto) this.xmlToObj(in, DominioIdTracciaSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioIdTracciaSoggetto readDominioIdTracciaSoggetto(byte[] in) throws DeserializerException {
		return (DominioIdTracciaSoggetto) this.xmlToObj(in, DominioIdTracciaSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * @return Object type {@link org.openspcoop2.core.tracciamento.DominioIdTracciaSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioIdTracciaSoggetto readDominioIdTracciaSoggettoFromString(String in) throws DeserializerException {
		return (DominioIdTracciaSoggetto) this.xmlToObj(in.getBytes(), DominioIdTracciaSoggetto.class);
	}	
	
	
	

}
