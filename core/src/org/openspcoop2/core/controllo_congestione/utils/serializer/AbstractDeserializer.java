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
package org.openspcoop2.core.controllo_congestione.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.controllo_congestione.AttivazionePolicy;
import org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting;
import org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_congestione.ElencoPolicy;
import org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico;
import org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione;
import org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione;
import org.openspcoop2.core.controllo_congestione.Cache;
import org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive;
import org.openspcoop2.core.controllo_congestione.IdPolicy;
import org.openspcoop2.core.controllo_congestione.ElencoIdPolicy;
import org.openspcoop2.core.controllo_congestione.IdActivePolicy;
import org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive;

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
	 Object: attivazione-policy
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicy readAttivazionePolicy(String fileName) throws DeserializerException {
		return (AttivazionePolicy) this.xmlToObj(fileName, AttivazionePolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicy readAttivazionePolicy(File file) throws DeserializerException {
		return (AttivazionePolicy) this.xmlToObj(file, AttivazionePolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicy readAttivazionePolicy(InputStream in) throws DeserializerException {
		return (AttivazionePolicy) this.xmlToObj(in, AttivazionePolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicy readAttivazionePolicy(byte[] in) throws DeserializerException {
		return (AttivazionePolicy) this.xmlToObj(in, AttivazionePolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicy readAttivazionePolicyFromString(String in) throws DeserializerException {
		return (AttivazionePolicy) this.xmlToObj(in.getBytes(), AttivazionePolicy.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: attivazione-policy-filtro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyFiltro readAttivazionePolicyFiltro(String fileName) throws DeserializerException {
		return (AttivazionePolicyFiltro) this.xmlToObj(fileName, AttivazionePolicyFiltro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyFiltro readAttivazionePolicyFiltro(File file) throws DeserializerException {
		return (AttivazionePolicyFiltro) this.xmlToObj(file, AttivazionePolicyFiltro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyFiltro readAttivazionePolicyFiltro(InputStream in) throws DeserializerException {
		return (AttivazionePolicyFiltro) this.xmlToObj(in, AttivazionePolicyFiltro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyFiltro readAttivazionePolicyFiltro(byte[] in) throws DeserializerException {
		return (AttivazionePolicyFiltro) this.xmlToObj(in, AttivazionePolicyFiltro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyFiltro readAttivazionePolicyFiltroFromString(String in) throws DeserializerException {
		return (AttivazionePolicyFiltro) this.xmlToObj(in.getBytes(), AttivazionePolicyFiltro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: attivazione-policy-raggruppamento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyRaggruppamento readAttivazionePolicyRaggruppamento(String fileName) throws DeserializerException {
		return (AttivazionePolicyRaggruppamento) this.xmlToObj(fileName, AttivazionePolicyRaggruppamento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyRaggruppamento readAttivazionePolicyRaggruppamento(File file) throws DeserializerException {
		return (AttivazionePolicyRaggruppamento) this.xmlToObj(file, AttivazionePolicyRaggruppamento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyRaggruppamento readAttivazionePolicyRaggruppamento(InputStream in) throws DeserializerException {
		return (AttivazionePolicyRaggruppamento) this.xmlToObj(in, AttivazionePolicyRaggruppamento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyRaggruppamento readAttivazionePolicyRaggruppamento(byte[] in) throws DeserializerException {
		return (AttivazionePolicyRaggruppamento) this.xmlToObj(in, AttivazionePolicyRaggruppamento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.AttivazionePolicyRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyRaggruppamento readAttivazionePolicyRaggruppamentoFromString(String in) throws DeserializerException {
		return (AttivazionePolicyRaggruppamento) this.xmlToObj(in.getBytes(), AttivazionePolicyRaggruppamento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-rate-limiting
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRateLimiting readConfigurazioneRateLimiting(String fileName) throws DeserializerException {
		return (ConfigurazioneRateLimiting) this.xmlToObj(fileName, ConfigurazioneRateLimiting.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRateLimiting readConfigurazioneRateLimiting(File file) throws DeserializerException {
		return (ConfigurazioneRateLimiting) this.xmlToObj(file, ConfigurazioneRateLimiting.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRateLimiting readConfigurazioneRateLimiting(InputStream in) throws DeserializerException {
		return (ConfigurazioneRateLimiting) this.xmlToObj(in, ConfigurazioneRateLimiting.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRateLimiting readConfigurazioneRateLimiting(byte[] in) throws DeserializerException {
		return (ConfigurazioneRateLimiting) this.xmlToObj(in, ConfigurazioneRateLimiting.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRateLimiting readConfigurazioneRateLimitingFromString(String in) throws DeserializerException {
		return (ConfigurazioneRateLimiting) this.xmlToObj(in.getBytes(), ConfigurazioneRateLimiting.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-policy
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazionePolicy readConfigurazionePolicy(String fileName) throws DeserializerException {
		return (ConfigurazionePolicy) this.xmlToObj(fileName, ConfigurazionePolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazionePolicy readConfigurazionePolicy(File file) throws DeserializerException {
		return (ConfigurazionePolicy) this.xmlToObj(file, ConfigurazionePolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazionePolicy readConfigurazionePolicy(InputStream in) throws DeserializerException {
		return (ConfigurazionePolicy) this.xmlToObj(in, ConfigurazionePolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazionePolicy readConfigurazionePolicy(byte[] in) throws DeserializerException {
		return (ConfigurazionePolicy) this.xmlToObj(in, ConfigurazionePolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazionePolicy readConfigurazionePolicyFromString(String in) throws DeserializerException {
		return (ConfigurazionePolicy) this.xmlToObj(in.getBytes(), ConfigurazionePolicy.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-policy
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicy readElencoPolicy(String fileName) throws DeserializerException {
		return (ElencoPolicy) this.xmlToObj(fileName, ElencoPolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicy readElencoPolicy(File file) throws DeserializerException {
		return (ElencoPolicy) this.xmlToObj(file, ElencoPolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicy readElencoPolicy(InputStream in) throws DeserializerException {
		return (ElencoPolicy) this.xmlToObj(in, ElencoPolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicy readElencoPolicy(byte[] in) throws DeserializerException {
		return (ElencoPolicy) this.xmlToObj(in, ElencoPolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicy readElencoPolicyFromString(String in) throws DeserializerException {
		return (ElencoPolicy) this.xmlToObj(in.getBytes(), ElencoPolicy.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-controllo-traffico
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneControlloTraffico readConfigurazioneControlloTraffico(String fileName) throws DeserializerException {
		return (ConfigurazioneControlloTraffico) this.xmlToObj(fileName, ConfigurazioneControlloTraffico.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneControlloTraffico readConfigurazioneControlloTraffico(File file) throws DeserializerException {
		return (ConfigurazioneControlloTraffico) this.xmlToObj(file, ConfigurazioneControlloTraffico.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneControlloTraffico readConfigurazioneControlloTraffico(InputStream in) throws DeserializerException {
		return (ConfigurazioneControlloTraffico) this.xmlToObj(in, ConfigurazioneControlloTraffico.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneControlloTraffico readConfigurazioneControlloTraffico(byte[] in) throws DeserializerException {
		return (ConfigurazioneControlloTraffico) this.xmlToObj(in, ConfigurazioneControlloTraffico.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneControlloTraffico readConfigurazioneControlloTrafficoFromString(String in) throws DeserializerException {
		return (ConfigurazioneControlloTraffico) this.xmlToObj(in.getBytes(), ConfigurazioneControlloTraffico.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-generale
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGenerale readConfigurazioneGenerale(String fileName) throws DeserializerException {
		return (ConfigurazioneGenerale) this.xmlToObj(fileName, ConfigurazioneGenerale.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGenerale readConfigurazioneGenerale(File file) throws DeserializerException {
		return (ConfigurazioneGenerale) this.xmlToObj(file, ConfigurazioneGenerale.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGenerale readConfigurazioneGenerale(InputStream in) throws DeserializerException {
		return (ConfigurazioneGenerale) this.xmlToObj(in, ConfigurazioneGenerale.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGenerale readConfigurazioneGenerale(byte[] in) throws DeserializerException {
		return (ConfigurazioneGenerale) this.xmlToObj(in, ConfigurazioneGenerale.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGenerale readConfigurazioneGeneraleFromString(String in) throws DeserializerException {
		return (ConfigurazioneGenerale) this.xmlToObj(in.getBytes(), ConfigurazioneGenerale.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: tempi-risposta-fruizione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaFruizione readTempiRispostaFruizione(String fileName) throws DeserializerException {
		return (TempiRispostaFruizione) this.xmlToObj(fileName, TempiRispostaFruizione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaFruizione readTempiRispostaFruizione(File file) throws DeserializerException {
		return (TempiRispostaFruizione) this.xmlToObj(file, TempiRispostaFruizione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaFruizione readTempiRispostaFruizione(InputStream in) throws DeserializerException {
		return (TempiRispostaFruizione) this.xmlToObj(in, TempiRispostaFruizione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaFruizione readTempiRispostaFruizione(byte[] in) throws DeserializerException {
		return (TempiRispostaFruizione) this.xmlToObj(in, TempiRispostaFruizione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaFruizione readTempiRispostaFruizioneFromString(String in) throws DeserializerException {
		return (TempiRispostaFruizione) this.xmlToObj(in.getBytes(), TempiRispostaFruizione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: tempi-risposta-erogazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaErogazione readTempiRispostaErogazione(String fileName) throws DeserializerException {
		return (TempiRispostaErogazione) this.xmlToObj(fileName, TempiRispostaErogazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaErogazione readTempiRispostaErogazione(File file) throws DeserializerException {
		return (TempiRispostaErogazione) this.xmlToObj(file, TempiRispostaErogazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaErogazione readTempiRispostaErogazione(InputStream in) throws DeserializerException {
		return (TempiRispostaErogazione) this.xmlToObj(in, TempiRispostaErogazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaErogazione readTempiRispostaErogazione(byte[] in) throws DeserializerException {
		return (TempiRispostaErogazione) this.xmlToObj(in, TempiRispostaErogazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaErogazione readTempiRispostaErogazioneFromString(String in) throws DeserializerException {
		return (TempiRispostaErogazione) this.xmlToObj(in.getBytes(), TempiRispostaErogazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: cache
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(String fileName) throws DeserializerException {
		return (Cache) this.xmlToObj(fileName, Cache.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(File file) throws DeserializerException {
		return (Cache) this.xmlToObj(file, Cache.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(InputStream in) throws DeserializerException {
		return (Cache) this.xmlToObj(in, Cache.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(byte[] in) throws DeserializerException {
		return (Cache) this.xmlToObj(in, Cache.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCacheFromString(String in) throws DeserializerException {
		return (Cache) this.xmlToObj(in.getBytes(), Cache.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-policy-attive
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicyAttive readElencoPolicyAttive(String fileName) throws DeserializerException {
		return (ElencoPolicyAttive) this.xmlToObj(fileName, ElencoPolicyAttive.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicyAttive readElencoPolicyAttive(File file) throws DeserializerException {
		return (ElencoPolicyAttive) this.xmlToObj(file, ElencoPolicyAttive.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicyAttive readElencoPolicyAttive(InputStream in) throws DeserializerException {
		return (ElencoPolicyAttive) this.xmlToObj(in, ElencoPolicyAttive.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicyAttive readElencoPolicyAttive(byte[] in) throws DeserializerException {
		return (ElencoPolicyAttive) this.xmlToObj(in, ElencoPolicyAttive.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicyAttive readElencoPolicyAttiveFromString(String in) throws DeserializerException {
		return (ElencoPolicyAttive) this.xmlToObj(in.getBytes(), ElencoPolicyAttive.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-policy
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPolicy readIdPolicy(String fileName) throws DeserializerException {
		return (IdPolicy) this.xmlToObj(fileName, IdPolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPolicy readIdPolicy(File file) throws DeserializerException {
		return (IdPolicy) this.xmlToObj(file, IdPolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPolicy readIdPolicy(InputStream in) throws DeserializerException {
		return (IdPolicy) this.xmlToObj(in, IdPolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPolicy readIdPolicy(byte[] in) throws DeserializerException {
		return (IdPolicy) this.xmlToObj(in, IdPolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.IdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPolicy readIdPolicyFromString(String in) throws DeserializerException {
		return (IdPolicy) this.xmlToObj(in.getBytes(), IdPolicy.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-policy
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicy readElencoIdPolicy(String fileName) throws DeserializerException {
		return (ElencoIdPolicy) this.xmlToObj(fileName, ElencoIdPolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicy readElencoIdPolicy(File file) throws DeserializerException {
		return (ElencoIdPolicy) this.xmlToObj(file, ElencoIdPolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicy readElencoIdPolicy(InputStream in) throws DeserializerException {
		return (ElencoIdPolicy) this.xmlToObj(in, ElencoIdPolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicy readElencoIdPolicy(byte[] in) throws DeserializerException {
		return (ElencoIdPolicy) this.xmlToObj(in, ElencoIdPolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicy readElencoIdPolicyFromString(String in) throws DeserializerException {
		return (ElencoIdPolicy) this.xmlToObj(in.getBytes(), ElencoIdPolicy.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-active-policy
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdActivePolicy readIdActivePolicy(String fileName) throws DeserializerException {
		return (IdActivePolicy) this.xmlToObj(fileName, IdActivePolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdActivePolicy readIdActivePolicy(File file) throws DeserializerException {
		return (IdActivePolicy) this.xmlToObj(file, IdActivePolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdActivePolicy readIdActivePolicy(InputStream in) throws DeserializerException {
		return (IdActivePolicy) this.xmlToObj(in, IdActivePolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdActivePolicy readIdActivePolicy(byte[] in) throws DeserializerException {
		return (IdActivePolicy) this.xmlToObj(in, IdActivePolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.IdActivePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdActivePolicy readIdActivePolicyFromString(String in) throws DeserializerException {
		return (IdActivePolicy) this.xmlToObj(in.getBytes(), IdActivePolicy.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-policy-attive
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicyAttive readElencoIdPolicyAttive(String fileName) throws DeserializerException {
		return (ElencoIdPolicyAttive) this.xmlToObj(fileName, ElencoIdPolicyAttive.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicyAttive readElencoIdPolicyAttive(File file) throws DeserializerException {
		return (ElencoIdPolicyAttive) this.xmlToObj(file, ElencoIdPolicyAttive.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicyAttive readElencoIdPolicyAttive(InputStream in) throws DeserializerException {
		return (ElencoIdPolicyAttive) this.xmlToObj(in, ElencoIdPolicyAttive.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicyAttive readElencoIdPolicyAttive(byte[] in) throws DeserializerException {
		return (ElencoIdPolicyAttive) this.xmlToObj(in, ElencoIdPolicyAttive.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_congestione.ElencoIdPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicyAttive readElencoIdPolicyAttiveFromString(String in) throws DeserializerException {
		return (ElencoIdPolicyAttive) this.xmlToObj(in.getBytes(), ElencoIdPolicyAttive.class);
	}	
	
	
	

}
