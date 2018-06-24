/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.core.controllo_traffico.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting;
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

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializer {



	/*
	 =================================================================================
	 Object: id-active-policy
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdActivePolicy readIdActivePolicy(String fileName) throws DeserializerException {
		return (IdActivePolicy) this.xmlToObj(fileName, IdActivePolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdActivePolicy readIdActivePolicy(File file) throws DeserializerException {
		return (IdActivePolicy) this.xmlToObj(file, IdActivePolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdActivePolicy readIdActivePolicy(InputStream in) throws DeserializerException {
		return (IdActivePolicy) this.xmlToObj(in, IdActivePolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdActivePolicy readIdActivePolicy(byte[] in) throws DeserializerException {
		return (IdActivePolicy) this.xmlToObj(in, IdActivePolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.IdActivePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdActivePolicy readIdActivePolicyFromString(String in) throws DeserializerException {
		return (IdActivePolicy) this.xmlToObj(in.getBytes(), IdActivePolicy.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: attivazione-policy
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicy readAttivazionePolicy(String fileName) throws DeserializerException {
		return (AttivazionePolicy) this.xmlToObj(fileName, AttivazionePolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicy readAttivazionePolicy(File file) throws DeserializerException {
		return (AttivazionePolicy) this.xmlToObj(file, AttivazionePolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicy readAttivazionePolicy(InputStream in) throws DeserializerException {
		return (AttivazionePolicy) this.xmlToObj(in, AttivazionePolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicy readAttivazionePolicy(byte[] in) throws DeserializerException {
		return (AttivazionePolicy) this.xmlToObj(in, AttivazionePolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyFiltro readAttivazionePolicyFiltro(String fileName) throws DeserializerException {
		return (AttivazionePolicyFiltro) this.xmlToObj(fileName, AttivazionePolicyFiltro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyFiltro readAttivazionePolicyFiltro(File file) throws DeserializerException {
		return (AttivazionePolicyFiltro) this.xmlToObj(file, AttivazionePolicyFiltro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyFiltro readAttivazionePolicyFiltro(InputStream in) throws DeserializerException {
		return (AttivazionePolicyFiltro) this.xmlToObj(in, AttivazionePolicyFiltro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyFiltro readAttivazionePolicyFiltro(byte[] in) throws DeserializerException {
		return (AttivazionePolicyFiltro) this.xmlToObj(in, AttivazionePolicyFiltro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyRaggruppamento readAttivazionePolicyRaggruppamento(String fileName) throws DeserializerException {
		return (AttivazionePolicyRaggruppamento) this.xmlToObj(fileName, AttivazionePolicyRaggruppamento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyRaggruppamento readAttivazionePolicyRaggruppamento(File file) throws DeserializerException {
		return (AttivazionePolicyRaggruppamento) this.xmlToObj(file, AttivazionePolicyRaggruppamento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyRaggruppamento readAttivazionePolicyRaggruppamento(InputStream in) throws DeserializerException {
		return (AttivazionePolicyRaggruppamento) this.xmlToObj(in, AttivazionePolicyRaggruppamento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttivazionePolicyRaggruppamento readAttivazionePolicyRaggruppamento(byte[] in) throws DeserializerException {
		return (AttivazionePolicyRaggruppamento) this.xmlToObj(in, AttivazionePolicyRaggruppamento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRateLimiting readConfigurazioneRateLimiting(String fileName) throws DeserializerException {
		return (ConfigurazioneRateLimiting) this.xmlToObj(fileName, ConfigurazioneRateLimiting.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRateLimiting readConfigurazioneRateLimiting(File file) throws DeserializerException {
		return (ConfigurazioneRateLimiting) this.xmlToObj(file, ConfigurazioneRateLimiting.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRateLimiting readConfigurazioneRateLimiting(InputStream in) throws DeserializerException {
		return (ConfigurazioneRateLimiting) this.xmlToObj(in, ConfigurazioneRateLimiting.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRateLimiting readConfigurazioneRateLimiting(byte[] in) throws DeserializerException {
		return (ConfigurazioneRateLimiting) this.xmlToObj(in, ConfigurazioneRateLimiting.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazionePolicy readConfigurazionePolicy(String fileName) throws DeserializerException {
		return (ConfigurazionePolicy) this.xmlToObj(fileName, ConfigurazionePolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazionePolicy readConfigurazionePolicy(File file) throws DeserializerException {
		return (ConfigurazionePolicy) this.xmlToObj(file, ConfigurazionePolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazionePolicy readConfigurazionePolicy(InputStream in) throws DeserializerException {
		return (ConfigurazionePolicy) this.xmlToObj(in, ConfigurazionePolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazionePolicy readConfigurazionePolicy(byte[] in) throws DeserializerException {
		return (ConfigurazionePolicy) this.xmlToObj(in, ConfigurazionePolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicy readElencoPolicy(String fileName) throws DeserializerException {
		return (ElencoPolicy) this.xmlToObj(fileName, ElencoPolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicy readElencoPolicy(File file) throws DeserializerException {
		return (ElencoPolicy) this.xmlToObj(file, ElencoPolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicy readElencoPolicy(InputStream in) throws DeserializerException {
		return (ElencoPolicy) this.xmlToObj(in, ElencoPolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicy readElencoPolicy(byte[] in) throws DeserializerException {
		return (ElencoPolicy) this.xmlToObj(in, ElencoPolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicy}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneControlloTraffico readConfigurazioneControlloTraffico(String fileName) throws DeserializerException {
		return (ConfigurazioneControlloTraffico) this.xmlToObj(fileName, ConfigurazioneControlloTraffico.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneControlloTraffico readConfigurazioneControlloTraffico(File file) throws DeserializerException {
		return (ConfigurazioneControlloTraffico) this.xmlToObj(file, ConfigurazioneControlloTraffico.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneControlloTraffico readConfigurazioneControlloTraffico(InputStream in) throws DeserializerException {
		return (ConfigurazioneControlloTraffico) this.xmlToObj(in, ConfigurazioneControlloTraffico.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneControlloTraffico readConfigurazioneControlloTraffico(byte[] in) throws DeserializerException {
		return (ConfigurazioneControlloTraffico) this.xmlToObj(in, ConfigurazioneControlloTraffico.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGenerale readConfigurazioneGenerale(String fileName) throws DeserializerException {
		return (ConfigurazioneGenerale) this.xmlToObj(fileName, ConfigurazioneGenerale.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGenerale readConfigurazioneGenerale(File file) throws DeserializerException {
		return (ConfigurazioneGenerale) this.xmlToObj(file, ConfigurazioneGenerale.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGenerale readConfigurazioneGenerale(InputStream in) throws DeserializerException {
		return (ConfigurazioneGenerale) this.xmlToObj(in, ConfigurazioneGenerale.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGenerale readConfigurazioneGenerale(byte[] in) throws DeserializerException {
		return (ConfigurazioneGenerale) this.xmlToObj(in, ConfigurazioneGenerale.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaFruizione readTempiRispostaFruizione(String fileName) throws DeserializerException {
		return (TempiRispostaFruizione) this.xmlToObj(fileName, TempiRispostaFruizione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaFruizione readTempiRispostaFruizione(File file) throws DeserializerException {
		return (TempiRispostaFruizione) this.xmlToObj(file, TempiRispostaFruizione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaFruizione readTempiRispostaFruizione(InputStream in) throws DeserializerException {
		return (TempiRispostaFruizione) this.xmlToObj(in, TempiRispostaFruizione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaFruizione readTempiRispostaFruizione(byte[] in) throws DeserializerException {
		return (TempiRispostaFruizione) this.xmlToObj(in, TempiRispostaFruizione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaErogazione readTempiRispostaErogazione(String fileName) throws DeserializerException {
		return (TempiRispostaErogazione) this.xmlToObj(fileName, TempiRispostaErogazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaErogazione readTempiRispostaErogazione(File file) throws DeserializerException {
		return (TempiRispostaErogazione) this.xmlToObj(file, TempiRispostaErogazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaErogazione readTempiRispostaErogazione(InputStream in) throws DeserializerException {
		return (TempiRispostaErogazione) this.xmlToObj(in, TempiRispostaErogazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TempiRispostaErogazione readTempiRispostaErogazione(byte[] in) throws DeserializerException {
		return (TempiRispostaErogazione) this.xmlToObj(in, TempiRispostaErogazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(String fileName) throws DeserializerException {
		return (Cache) this.xmlToObj(fileName, Cache.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(File file) throws DeserializerException {
		return (Cache) this.xmlToObj(file, Cache.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(InputStream in) throws DeserializerException {
		return (Cache) this.xmlToObj(in, Cache.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(byte[] in) throws DeserializerException {
		return (Cache) this.xmlToObj(in, Cache.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.Cache}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.Cache}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicyAttive readElencoPolicyAttive(String fileName) throws DeserializerException {
		return (ElencoPolicyAttive) this.xmlToObj(fileName, ElencoPolicyAttive.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicyAttive readElencoPolicyAttive(File file) throws DeserializerException {
		return (ElencoPolicyAttive) this.xmlToObj(file, ElencoPolicyAttive.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicyAttive readElencoPolicyAttive(InputStream in) throws DeserializerException {
		return (ElencoPolicyAttive) this.xmlToObj(in, ElencoPolicyAttive.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPolicyAttive readElencoPolicyAttive(byte[] in) throws DeserializerException {
		return (ElencoPolicyAttive) this.xmlToObj(in, ElencoPolicyAttive.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoPolicyAttive}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPolicy readIdPolicy(String fileName) throws DeserializerException {
		return (IdPolicy) this.xmlToObj(fileName, IdPolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPolicy readIdPolicy(File file) throws DeserializerException {
		return (IdPolicy) this.xmlToObj(file, IdPolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPolicy readIdPolicy(InputStream in) throws DeserializerException {
		return (IdPolicy) this.xmlToObj(in, IdPolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPolicy readIdPolicy(byte[] in) throws DeserializerException {
		return (IdPolicy) this.xmlToObj(in, IdPolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.IdPolicy}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicy readElencoIdPolicy(String fileName) throws DeserializerException {
		return (ElencoIdPolicy) this.xmlToObj(fileName, ElencoIdPolicy.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicy readElencoIdPolicy(File file) throws DeserializerException {
		return (ElencoIdPolicy) this.xmlToObj(file, ElencoIdPolicy.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicy readElencoIdPolicy(InputStream in) throws DeserializerException {
		return (ElencoIdPolicy) this.xmlToObj(in, ElencoIdPolicy.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicy readElencoIdPolicy(byte[] in) throws DeserializerException {
		return (ElencoIdPolicy) this.xmlToObj(in, ElencoIdPolicy.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicy}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicy readElencoIdPolicyFromString(String in) throws DeserializerException {
		return (ElencoIdPolicy) this.xmlToObj(in.getBytes(), ElencoIdPolicy.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-policy-attive
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicyAttive readElencoIdPolicyAttive(String fileName) throws DeserializerException {
		return (ElencoIdPolicyAttive) this.xmlToObj(fileName, ElencoIdPolicyAttive.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicyAttive readElencoIdPolicyAttive(File file) throws DeserializerException {
		return (ElencoIdPolicyAttive) this.xmlToObj(file, ElencoIdPolicyAttive.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicyAttive readElencoIdPolicyAttive(InputStream in) throws DeserializerException {
		return (ElencoIdPolicyAttive) this.xmlToObj(in, ElencoIdPolicyAttive.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicyAttive readElencoIdPolicyAttive(byte[] in) throws DeserializerException {
		return (ElencoIdPolicyAttive) this.xmlToObj(in, ElencoIdPolicyAttive.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * @return Object type {@link org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPolicyAttive readElencoIdPolicyAttiveFromString(String in) throws DeserializerException {
		return (ElencoIdPolicyAttive) this.xmlToObj(in.getBytes(), ElencoIdPolicyAttive.class);
	}	
	
	
	

}
