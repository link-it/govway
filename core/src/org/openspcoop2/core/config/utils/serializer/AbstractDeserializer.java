/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.core.config.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.Openspcoop2;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GestioneErroreSoapFault;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ProprietaProtocollo;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDefault;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.StatoServiziPddPortaDelegata;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.IdSoggetto;
import org.openspcoop2.core.config.IdServizioApplicativo;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.IdPortaDelegata;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.ConfigurazioneGestioneErrore;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.StatoServiziPddPortaApplicativa;
import org.openspcoop2.core.config.StatoServiziPddIntegrationManager;
import org.openspcoop2.core.config.IdPortaApplicativa;

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
	 Object: message-security-flow-parameter
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurityFlowParameter readMessageSecurityFlowParameter(String fileName) throws DeserializerException {
		return (MessageSecurityFlowParameter) this.xmlToObj(fileName, MessageSecurityFlowParameter.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurityFlowParameter readMessageSecurityFlowParameter(File file) throws DeserializerException {
		return (MessageSecurityFlowParameter) this.xmlToObj(file, MessageSecurityFlowParameter.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurityFlowParameter readMessageSecurityFlowParameter(InputStream in) throws DeserializerException {
		return (MessageSecurityFlowParameter) this.xmlToObj(in, MessageSecurityFlowParameter.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurityFlowParameter readMessageSecurityFlowParameter(byte[] in) throws DeserializerException {
		return (MessageSecurityFlowParameter) this.xmlToObj(in, MessageSecurityFlowParameter.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurityFlowParameter readMessageSecurityFlowParameterFromString(String in) throws DeserializerException {
		return (MessageSecurityFlowParameter) this.xmlToObj(in.getBytes(), MessageSecurityFlowParameter.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: message-security-flow
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurityFlow readMessageSecurityFlow(String fileName) throws DeserializerException {
		return (MessageSecurityFlow) this.xmlToObj(fileName, MessageSecurityFlow.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurityFlow readMessageSecurityFlow(File file) throws DeserializerException {
		return (MessageSecurityFlow) this.xmlToObj(file, MessageSecurityFlow.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurityFlow readMessageSecurityFlow(InputStream in) throws DeserializerException {
		return (MessageSecurityFlow) this.xmlToObj(in, MessageSecurityFlow.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurityFlow readMessageSecurityFlow(byte[] in) throws DeserializerException {
		return (MessageSecurityFlow) this.xmlToObj(in, MessageSecurityFlow.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurityFlow readMessageSecurityFlowFromString(String in) throws DeserializerException {
		return (MessageSecurityFlow) this.xmlToObj(in.getBytes(), MessageSecurityFlow.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: correlazione-applicativa-risposta-elemento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaRispostaElemento readCorrelazioneApplicativaRispostaElemento(String fileName) throws DeserializerException {
		return (CorrelazioneApplicativaRispostaElemento) this.xmlToObj(fileName, CorrelazioneApplicativaRispostaElemento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaRispostaElemento readCorrelazioneApplicativaRispostaElemento(File file) throws DeserializerException {
		return (CorrelazioneApplicativaRispostaElemento) this.xmlToObj(file, CorrelazioneApplicativaRispostaElemento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaRispostaElemento readCorrelazioneApplicativaRispostaElemento(InputStream in) throws DeserializerException {
		return (CorrelazioneApplicativaRispostaElemento) this.xmlToObj(in, CorrelazioneApplicativaRispostaElemento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaRispostaElemento readCorrelazioneApplicativaRispostaElemento(byte[] in) throws DeserializerException {
		return (CorrelazioneApplicativaRispostaElemento) this.xmlToObj(in, CorrelazioneApplicativaRispostaElemento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaRispostaElemento readCorrelazioneApplicativaRispostaElementoFromString(String in) throws DeserializerException {
		return (CorrelazioneApplicativaRispostaElemento) this.xmlToObj(in.getBytes(), CorrelazioneApplicativaRispostaElemento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: correlazione-applicativa-risposta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaRisposta readCorrelazioneApplicativaRisposta(String fileName) throws DeserializerException {
		return (CorrelazioneApplicativaRisposta) this.xmlToObj(fileName, CorrelazioneApplicativaRisposta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaRisposta readCorrelazioneApplicativaRisposta(File file) throws DeserializerException {
		return (CorrelazioneApplicativaRisposta) this.xmlToObj(file, CorrelazioneApplicativaRisposta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaRisposta readCorrelazioneApplicativaRisposta(InputStream in) throws DeserializerException {
		return (CorrelazioneApplicativaRisposta) this.xmlToObj(in, CorrelazioneApplicativaRisposta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaRisposta readCorrelazioneApplicativaRisposta(byte[] in) throws DeserializerException {
		return (CorrelazioneApplicativaRisposta) this.xmlToObj(in, CorrelazioneApplicativaRisposta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaRisposta readCorrelazioneApplicativaRispostaFromString(String in) throws DeserializerException {
		return (CorrelazioneApplicativaRisposta) this.xmlToObj(in.getBytes(), CorrelazioneApplicativaRisposta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: cache
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Cache}
	 * @return Object type {@link org.openspcoop2.core.config.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(String fileName) throws DeserializerException {
		return (Cache) this.xmlToObj(fileName, Cache.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Cache}
	 * @return Object type {@link org.openspcoop2.core.config.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(File file) throws DeserializerException {
		return (Cache) this.xmlToObj(file, Cache.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Cache}
	 * @return Object type {@link org.openspcoop2.core.config.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(InputStream in) throws DeserializerException {
		return (Cache) this.xmlToObj(in, Cache.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Cache}
	 * @return Object type {@link org.openspcoop2.core.config.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCache(byte[] in) throws DeserializerException {
		return (Cache) this.xmlToObj(in, Cache.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Cache}
	 * @return Object type {@link org.openspcoop2.core.config.Cache}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Cache readCacheFromString(String in) throws DeserializerException {
		return (Cache) this.xmlToObj(in.getBytes(), Cache.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.config.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(String fileName) throws DeserializerException {
		return (Soggetto) this.xmlToObj(fileName, Soggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.config.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(File file) throws DeserializerException {
		return (Soggetto) this.xmlToObj(file, Soggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.config.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(InputStream in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.config.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(byte[] in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.config.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggettoFromString(String in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in.getBytes(), Soggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(String fileName) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(fileName, Openspcoop2.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(File file) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(file, Openspcoop2.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(InputStream in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(byte[] in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.core.config.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2FromString(String in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in.getBytes(), Openspcoop2.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Configurazione}
	 * @return Object type {@link org.openspcoop2.core.config.Configurazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Configurazione readConfigurazione(String fileName) throws DeserializerException {
		return (Configurazione) this.xmlToObj(fileName, Configurazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Configurazione}
	 * @return Object type {@link org.openspcoop2.core.config.Configurazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Configurazione readConfigurazione(File file) throws DeserializerException {
		return (Configurazione) this.xmlToObj(file, Configurazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Configurazione}
	 * @return Object type {@link org.openspcoop2.core.config.Configurazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Configurazione readConfigurazione(InputStream in) throws DeserializerException {
		return (Configurazione) this.xmlToObj(in, Configurazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Configurazione}
	 * @return Object type {@link org.openspcoop2.core.config.Configurazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Configurazione readConfigurazione(byte[] in) throws DeserializerException {
		return (Configurazione) this.xmlToObj(in, Configurazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Configurazione}
	 * @return Object type {@link org.openspcoop2.core.config.Configurazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Configurazione readConfigurazioneFromString(String in) throws DeserializerException {
		return (Configurazione) this.xmlToObj(in.getBytes(), Configurazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: gestione-errore-soap-fault
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErroreSoapFault readGestioneErroreSoapFault(String fileName) throws DeserializerException {
		return (GestioneErroreSoapFault) this.xmlToObj(fileName, GestioneErroreSoapFault.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErroreSoapFault readGestioneErroreSoapFault(File file) throws DeserializerException {
		return (GestioneErroreSoapFault) this.xmlToObj(file, GestioneErroreSoapFault.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErroreSoapFault readGestioneErroreSoapFault(InputStream in) throws DeserializerException {
		return (GestioneErroreSoapFault) this.xmlToObj(in, GestioneErroreSoapFault.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErroreSoapFault readGestioneErroreSoapFault(byte[] in) throws DeserializerException {
		return (GestioneErroreSoapFault) this.xmlToObj(in, GestioneErroreSoapFault.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErroreSoapFault readGestioneErroreSoapFaultFromString(String in) throws DeserializerException {
		return (GestioneErroreSoapFault) this.xmlToObj(in.getBytes(), GestioneErroreSoapFault.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Property
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Property}
	 * @return Object type {@link org.openspcoop2.core.config.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(String fileName) throws DeserializerException {
		return (Property) this.xmlToObj(fileName, Property.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Property}
	 * @return Object type {@link org.openspcoop2.core.config.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(File file) throws DeserializerException {
		return (Property) this.xmlToObj(file, Property.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Property}
	 * @return Object type {@link org.openspcoop2.core.config.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(InputStream in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Property}
	 * @return Object type {@link org.openspcoop2.core.config.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(byte[] in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Property}
	 * @return Object type {@link org.openspcoop2.core.config.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readPropertyFromString(String in) throws DeserializerException {
		return (Property) this.xmlToObj(in.getBytes(), Property.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop-sorgente-dati
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * @return Object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OpenspcoopSorgenteDati readOpenspcoopSorgenteDati(String fileName) throws DeserializerException {
		return (OpenspcoopSorgenteDati) this.xmlToObj(fileName, OpenspcoopSorgenteDati.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * @return Object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OpenspcoopSorgenteDati readOpenspcoopSorgenteDati(File file) throws DeserializerException {
		return (OpenspcoopSorgenteDati) this.xmlToObj(file, OpenspcoopSorgenteDati.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * @return Object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OpenspcoopSorgenteDati readOpenspcoopSorgenteDati(InputStream in) throws DeserializerException {
		return (OpenspcoopSorgenteDati) this.xmlToObj(in, OpenspcoopSorgenteDati.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * @return Object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OpenspcoopSorgenteDati readOpenspcoopSorgenteDati(byte[] in) throws DeserializerException {
		return (OpenspcoopSorgenteDati) this.xmlToObj(in, OpenspcoopSorgenteDati.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * @return Object type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OpenspcoopSorgenteDati readOpenspcoopSorgenteDatiFromString(String in) throws DeserializerException {
		return (OpenspcoopSorgenteDati) this.xmlToObj(in.getBytes(), OpenspcoopSorgenteDati.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-soggetto-virtuale
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaSoggettoVirtuale readPortaApplicativaSoggettoVirtuale(String fileName) throws DeserializerException {
		return (PortaApplicativaSoggettoVirtuale) this.xmlToObj(fileName, PortaApplicativaSoggettoVirtuale.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaSoggettoVirtuale readPortaApplicativaSoggettoVirtuale(File file) throws DeserializerException {
		return (PortaApplicativaSoggettoVirtuale) this.xmlToObj(file, PortaApplicativaSoggettoVirtuale.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaSoggettoVirtuale readPortaApplicativaSoggettoVirtuale(InputStream in) throws DeserializerException {
		return (PortaApplicativaSoggettoVirtuale) this.xmlToObj(in, PortaApplicativaSoggettoVirtuale.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaSoggettoVirtuale readPortaApplicativaSoggettoVirtuale(byte[] in) throws DeserializerException {
		return (PortaApplicativaSoggettoVirtuale) this.xmlToObj(in, PortaApplicativaSoggettoVirtuale.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaSoggettoVirtuale readPortaApplicativaSoggettoVirtualeFromString(String in) throws DeserializerException {
		return (PortaApplicativaSoggettoVirtuale) this.xmlToObj(in.getBytes(), PortaApplicativaSoggettoVirtuale.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(String fileName) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(fileName, PortaApplicativa.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(File file) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(file, PortaApplicativa.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(InputStream in) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(in, PortaApplicativa.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(byte[] in) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(in, PortaApplicativa.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativaFromString(String in) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(in.getBytes(), PortaApplicativa.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaServizio readPortaApplicativaServizio(String fileName) throws DeserializerException {
		return (PortaApplicativaServizio) this.xmlToObj(fileName, PortaApplicativaServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaServizio readPortaApplicativaServizio(File file) throws DeserializerException {
		return (PortaApplicativaServizio) this.xmlToObj(file, PortaApplicativaServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaServizio readPortaApplicativaServizio(InputStream in) throws DeserializerException {
		return (PortaApplicativaServizio) this.xmlToObj(in, PortaApplicativaServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaServizio readPortaApplicativaServizio(byte[] in) throws DeserializerException {
		return (PortaApplicativaServizio) this.xmlToObj(in, PortaApplicativaServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaServizio readPortaApplicativaServizioFromString(String in) throws DeserializerException {
		return (PortaApplicativaServizio) this.xmlToObj(in.getBytes(), PortaApplicativaServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaAzione readPortaApplicativaAzione(String fileName) throws DeserializerException {
		return (PortaApplicativaAzione) this.xmlToObj(fileName, PortaApplicativaAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaAzione readPortaApplicativaAzione(File file) throws DeserializerException {
		return (PortaApplicativaAzione) this.xmlToObj(file, PortaApplicativaAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaAzione readPortaApplicativaAzione(InputStream in) throws DeserializerException {
		return (PortaApplicativaAzione) this.xmlToObj(in, PortaApplicativaAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaAzione readPortaApplicativaAzione(byte[] in) throws DeserializerException {
		return (PortaApplicativaAzione) this.xmlToObj(in, PortaApplicativaAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * @return Object type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaAzione readPortaApplicativaAzioneFromString(String in) throws DeserializerException {
		return (PortaApplicativaAzione) this.xmlToObj(in.getBytes(), PortaApplicativaAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(String fileName) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(fileName, ServizioApplicativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(File file) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(file, ServizioApplicativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(InputStream in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in, ServizioApplicativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(byte[] in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in, ServizioApplicativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativoFromString(String in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in.getBytes(), ServizioApplicativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: proprieta-protocollo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * @return Object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaProtocollo readProprietaProtocollo(String fileName) throws DeserializerException {
		return (ProprietaProtocollo) this.xmlToObj(fileName, ProprietaProtocollo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * @return Object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaProtocollo readProprietaProtocollo(File file) throws DeserializerException {
		return (ProprietaProtocollo) this.xmlToObj(file, ProprietaProtocollo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * @return Object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaProtocollo readProprietaProtocollo(InputStream in) throws DeserializerException {
		return (ProprietaProtocollo) this.xmlToObj(in, ProprietaProtocollo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * @return Object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaProtocollo readProprietaProtocollo(byte[] in) throws DeserializerException {
		return (ProprietaProtocollo) this.xmlToObj(in, ProprietaProtocollo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * @return Object type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaProtocollo readProprietaProtocolloFromString(String in) throws DeserializerException {
		return (ProprietaProtocollo) this.xmlToObj(in.getBytes(), ProprietaProtocollo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: mtom-processor
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessor readMtomProcessor(String fileName) throws DeserializerException {
		return (MtomProcessor) this.xmlToObj(fileName, MtomProcessor.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessor readMtomProcessor(File file) throws DeserializerException {
		return (MtomProcessor) this.xmlToObj(file, MtomProcessor.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessor readMtomProcessor(InputStream in) throws DeserializerException {
		return (MtomProcessor) this.xmlToObj(in, MtomProcessor.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessor readMtomProcessor(byte[] in) throws DeserializerException {
		return (MtomProcessor) this.xmlToObj(in, MtomProcessor.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessor}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessor readMtomProcessorFromString(String in) throws DeserializerException {
		return (MtomProcessor) this.xmlToObj(in.getBytes(), MtomProcessor.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: message-security
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurity readMessageSecurity(String fileName) throws DeserializerException {
		return (MessageSecurity) this.xmlToObj(fileName, MessageSecurity.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurity readMessageSecurity(File file) throws DeserializerException {
		return (MessageSecurity) this.xmlToObj(file, MessageSecurity.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurity readMessageSecurity(InputStream in) throws DeserializerException {
		return (MessageSecurity) this.xmlToObj(in, MessageSecurity.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurity readMessageSecurity(byte[] in) throws DeserializerException {
		return (MessageSecurity) this.xmlToObj(in, MessageSecurity.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * @return Object type {@link org.openspcoop2.core.config.MessageSecurity}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageSecurity readMessageSecurityFromString(String in) throws DeserializerException {
		return (MessageSecurity) this.xmlToObj(in.getBytes(), MessageSecurity.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: validazione-contenuti-applicativi
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * @return Object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ValidazioneContenutiApplicativi readValidazioneContenutiApplicativi(String fileName) throws DeserializerException {
		return (ValidazioneContenutiApplicativi) this.xmlToObj(fileName, ValidazioneContenutiApplicativi.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * @return Object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ValidazioneContenutiApplicativi readValidazioneContenutiApplicativi(File file) throws DeserializerException {
		return (ValidazioneContenutiApplicativi) this.xmlToObj(file, ValidazioneContenutiApplicativi.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * @return Object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ValidazioneContenutiApplicativi readValidazioneContenutiApplicativi(InputStream in) throws DeserializerException {
		return (ValidazioneContenutiApplicativi) this.xmlToObj(in, ValidazioneContenutiApplicativi.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * @return Object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ValidazioneContenutiApplicativi readValidazioneContenutiApplicativi(byte[] in) throws DeserializerException {
		return (ValidazioneContenutiApplicativi) this.xmlToObj(in, ValidazioneContenutiApplicativi.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * @return Object type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ValidazioneContenutiApplicativi readValidazioneContenutiApplicativiFromString(String in) throws DeserializerException {
		return (ValidazioneContenutiApplicativi) this.xmlToObj(in.getBytes(), ValidazioneContenutiApplicativi.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: correlazione-applicativa
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativa readCorrelazioneApplicativa(String fileName) throws DeserializerException {
		return (CorrelazioneApplicativa) this.xmlToObj(fileName, CorrelazioneApplicativa.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativa readCorrelazioneApplicativa(File file) throws DeserializerException {
		return (CorrelazioneApplicativa) this.xmlToObj(file, CorrelazioneApplicativa.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativa readCorrelazioneApplicativa(InputStream in) throws DeserializerException {
		return (CorrelazioneApplicativa) this.xmlToObj(in, CorrelazioneApplicativa.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativa readCorrelazioneApplicativa(byte[] in) throws DeserializerException {
		return (CorrelazioneApplicativa) this.xmlToObj(in, CorrelazioneApplicativa.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativa readCorrelazioneApplicativaFromString(String in) throws DeserializerException {
		return (CorrelazioneApplicativa) this.xmlToObj(in.getBytes(), CorrelazioneApplicativa.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop-appender
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * @return Object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OpenspcoopAppender readOpenspcoopAppender(String fileName) throws DeserializerException {
		return (OpenspcoopAppender) this.xmlToObj(fileName, OpenspcoopAppender.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * @return Object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OpenspcoopAppender readOpenspcoopAppender(File file) throws DeserializerException {
		return (OpenspcoopAppender) this.xmlToObj(file, OpenspcoopAppender.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * @return Object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OpenspcoopAppender readOpenspcoopAppender(InputStream in) throws DeserializerException {
		return (OpenspcoopAppender) this.xmlToObj(in, OpenspcoopAppender.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * @return Object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OpenspcoopAppender readOpenspcoopAppender(byte[] in) throws DeserializerException {
		return (OpenspcoopAppender) this.xmlToObj(in, OpenspcoopAppender.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * @return Object type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OpenspcoopAppender readOpenspcoopAppenderFromString(String in) throws DeserializerException {
		return (OpenspcoopAppender) this.xmlToObj(in.getBytes(), OpenspcoopAppender.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: messaggi-diagnostici
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggiDiagnostici readMessaggiDiagnostici(String fileName) throws DeserializerException {
		return (MessaggiDiagnostici) this.xmlToObj(fileName, MessaggiDiagnostici.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggiDiagnostici readMessaggiDiagnostici(File file) throws DeserializerException {
		return (MessaggiDiagnostici) this.xmlToObj(file, MessaggiDiagnostici.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggiDiagnostici readMessaggiDiagnostici(InputStream in) throws DeserializerException {
		return (MessaggiDiagnostici) this.xmlToObj(in, MessaggiDiagnostici.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggiDiagnostici readMessaggiDiagnostici(byte[] in) throws DeserializerException {
		return (MessaggiDiagnostici) this.xmlToObj(in, MessaggiDiagnostici.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggiDiagnostici readMessaggiDiagnosticiFromString(String in) throws DeserializerException {
		return (MessaggiDiagnostici) this.xmlToObj(in.getBytes(), MessaggiDiagnostici.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accesso-dati-autorizzazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoDatiAutorizzazione readAccessoDatiAutorizzazione(String fileName) throws DeserializerException {
		return (AccessoDatiAutorizzazione) this.xmlToObj(fileName, AccessoDatiAutorizzazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoDatiAutorizzazione readAccessoDatiAutorizzazione(File file) throws DeserializerException {
		return (AccessoDatiAutorizzazione) this.xmlToObj(file, AccessoDatiAutorizzazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoDatiAutorizzazione readAccessoDatiAutorizzazione(InputStream in) throws DeserializerException {
		return (AccessoDatiAutorizzazione) this.xmlToObj(in, AccessoDatiAutorizzazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoDatiAutorizzazione readAccessoDatiAutorizzazione(byte[] in) throws DeserializerException {
		return (AccessoDatiAutorizzazione) this.xmlToObj(in, AccessoDatiAutorizzazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoDatiAutorizzazione readAccessoDatiAutorizzazioneFromString(String in) throws DeserializerException {
		return (AccessoDatiAutorizzazione) this.xmlToObj(in.getBytes(), AccessoDatiAutorizzazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: routing-table-destinazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTableDestinazione readRoutingTableDestinazione(String fileName) throws DeserializerException {
		return (RoutingTableDestinazione) this.xmlToObj(fileName, RoutingTableDestinazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTableDestinazione readRoutingTableDestinazione(File file) throws DeserializerException {
		return (RoutingTableDestinazione) this.xmlToObj(file, RoutingTableDestinazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTableDestinazione readRoutingTableDestinazione(InputStream in) throws DeserializerException {
		return (RoutingTableDestinazione) this.xmlToObj(in, RoutingTableDestinazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTableDestinazione readRoutingTableDestinazione(byte[] in) throws DeserializerException {
		return (RoutingTableDestinazione) this.xmlToObj(in, RoutingTableDestinazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTableDestinazione readRoutingTableDestinazioneFromString(String in) throws DeserializerException {
		return (RoutingTableDestinazione) this.xmlToObj(in.getBytes(), RoutingTableDestinazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: routing-table
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTable}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTable}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTable readRoutingTable(String fileName) throws DeserializerException {
		return (RoutingTable) this.xmlToObj(fileName, RoutingTable.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTable}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTable}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTable readRoutingTable(File file) throws DeserializerException {
		return (RoutingTable) this.xmlToObj(file, RoutingTable.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTable}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTable}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTable readRoutingTable(InputStream in) throws DeserializerException {
		return (RoutingTable) this.xmlToObj(in, RoutingTable.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTable}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTable}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTable readRoutingTable(byte[] in) throws DeserializerException {
		return (RoutingTable) this.xmlToObj(in, RoutingTable.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTable}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTable}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTable readRoutingTableFromString(String in) throws DeserializerException {
		return (RoutingTable) this.xmlToObj(in.getBytes(), RoutingTable.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: routing-table-default
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTableDefault readRoutingTableDefault(String fileName) throws DeserializerException {
		return (RoutingTableDefault) this.xmlToObj(fileName, RoutingTableDefault.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTableDefault readRoutingTableDefault(File file) throws DeserializerException {
		return (RoutingTableDefault) this.xmlToObj(file, RoutingTableDefault.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTableDefault readRoutingTableDefault(InputStream in) throws DeserializerException {
		return (RoutingTableDefault) this.xmlToObj(in, RoutingTableDefault.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTableDefault readRoutingTableDefault(byte[] in) throws DeserializerException {
		return (RoutingTableDefault) this.xmlToObj(in, RoutingTableDefault.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * @return Object type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RoutingTableDefault readRoutingTableDefaultFromString(String in) throws DeserializerException {
		return (RoutingTableDefault) this.xmlToObj(in.getBytes(), RoutingTableDefault.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: mtom-processor-flow-parameter
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessorFlowParameter readMtomProcessorFlowParameter(String fileName) throws DeserializerException {
		return (MtomProcessorFlowParameter) this.xmlToObj(fileName, MtomProcessorFlowParameter.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessorFlowParameter readMtomProcessorFlowParameter(File file) throws DeserializerException {
		return (MtomProcessorFlowParameter) this.xmlToObj(file, MtomProcessorFlowParameter.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessorFlowParameter readMtomProcessorFlowParameter(InputStream in) throws DeserializerException {
		return (MtomProcessorFlowParameter) this.xmlToObj(in, MtomProcessorFlowParameter.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessorFlowParameter readMtomProcessorFlowParameter(byte[] in) throws DeserializerException {
		return (MtomProcessorFlowParameter) this.xmlToObj(in, MtomProcessorFlowParameter.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessorFlowParameter readMtomProcessorFlowParameterFromString(String in) throws DeserializerException {
		return (MtomProcessorFlowParameter) this.xmlToObj(in.getBytes(), MtomProcessorFlowParameter.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: mtom-processor-flow
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessorFlow readMtomProcessorFlow(String fileName) throws DeserializerException {
		return (MtomProcessorFlow) this.xmlToObj(fileName, MtomProcessorFlow.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessorFlow readMtomProcessorFlow(File file) throws DeserializerException {
		return (MtomProcessorFlow) this.xmlToObj(file, MtomProcessorFlow.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessorFlow readMtomProcessorFlow(InputStream in) throws DeserializerException {
		return (MtomProcessorFlow) this.xmlToObj(in, MtomProcessorFlow.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessorFlow readMtomProcessorFlow(byte[] in) throws DeserializerException {
		return (MtomProcessorFlow) this.xmlToObj(in, MtomProcessorFlow.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * @return Object type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MtomProcessorFlow readMtomProcessorFlowFromString(String in) throws DeserializerException {
		return (MtomProcessorFlow) this.xmlToObj(in.getBytes(), MtomProcessorFlow.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: gestione-errore-codice-trasporto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErroreCodiceTrasporto readGestioneErroreCodiceTrasporto(String fileName) throws DeserializerException {
		return (GestioneErroreCodiceTrasporto) this.xmlToObj(fileName, GestioneErroreCodiceTrasporto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErroreCodiceTrasporto readGestioneErroreCodiceTrasporto(File file) throws DeserializerException {
		return (GestioneErroreCodiceTrasporto) this.xmlToObj(file, GestioneErroreCodiceTrasporto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErroreCodiceTrasporto readGestioneErroreCodiceTrasporto(InputStream in) throws DeserializerException {
		return (GestioneErroreCodiceTrasporto) this.xmlToObj(in, GestioneErroreCodiceTrasporto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErroreCodiceTrasporto readGestioneErroreCodiceTrasporto(byte[] in) throws DeserializerException {
		return (GestioneErroreCodiceTrasporto) this.xmlToObj(in, GestioneErroreCodiceTrasporto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErroreCodiceTrasporto readGestioneErroreCodiceTrasportoFromString(String in) throws DeserializerException {
		return (GestioneErroreCodiceTrasporto) this.xmlToObj(in.getBytes(), GestioneErroreCodiceTrasporto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: gestione-errore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErrore readGestioneErrore(String fileName) throws DeserializerException {
		return (GestioneErrore) this.xmlToObj(fileName, GestioneErrore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErrore readGestioneErrore(File file) throws DeserializerException {
		return (GestioneErrore) this.xmlToObj(file, GestioneErrore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErrore readGestioneErrore(InputStream in) throws DeserializerException {
		return (GestioneErrore) this.xmlToObj(in, GestioneErrore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErrore readGestioneErrore(byte[] in) throws DeserializerException {
		return (GestioneErrore) this.xmlToObj(in, GestioneErrore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.GestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GestioneErrore readGestioneErroreFromString(String in) throws DeserializerException {
		return (GestioneErrore) this.xmlToObj(in.getBytes(), GestioneErrore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: tracciamento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Tracciamento}
	 * @return Object type {@link org.openspcoop2.core.config.Tracciamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tracciamento readTracciamento(String fileName) throws DeserializerException {
		return (Tracciamento) this.xmlToObj(fileName, Tracciamento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Tracciamento}
	 * @return Object type {@link org.openspcoop2.core.config.Tracciamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tracciamento readTracciamento(File file) throws DeserializerException {
		return (Tracciamento) this.xmlToObj(file, Tracciamento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Tracciamento}
	 * @return Object type {@link org.openspcoop2.core.config.Tracciamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tracciamento readTracciamento(InputStream in) throws DeserializerException {
		return (Tracciamento) this.xmlToObj(in, Tracciamento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Tracciamento}
	 * @return Object type {@link org.openspcoop2.core.config.Tracciamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tracciamento readTracciamento(byte[] in) throws DeserializerException {
		return (Tracciamento) this.xmlToObj(in, Tracciamento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Tracciamento}
	 * @return Object type {@link org.openspcoop2.core.config.Tracciamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tracciamento readTracciamentoFromString(String in) throws DeserializerException {
		return (Tracciamento) this.xmlToObj(in.getBytes(), Tracciamento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: correlazione-applicativa-elemento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaElemento readCorrelazioneApplicativaElemento(String fileName) throws DeserializerException {
		return (CorrelazioneApplicativaElemento) this.xmlToObj(fileName, CorrelazioneApplicativaElemento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaElemento readCorrelazioneApplicativaElemento(File file) throws DeserializerException {
		return (CorrelazioneApplicativaElemento) this.xmlToObj(file, CorrelazioneApplicativaElemento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaElemento readCorrelazioneApplicativaElemento(InputStream in) throws DeserializerException {
		return (CorrelazioneApplicativaElemento) this.xmlToObj(in, CorrelazioneApplicativaElemento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaElemento readCorrelazioneApplicativaElemento(byte[] in) throws DeserializerException {
		return (CorrelazioneApplicativaElemento) this.xmlToObj(in, CorrelazioneApplicativaElemento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * @return Object type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CorrelazioneApplicativaElemento readCorrelazioneApplicativaElementoFromString(String in) throws DeserializerException {
		return (CorrelazioneApplicativaElemento) this.xmlToObj(in.getBytes(), CorrelazioneApplicativaElemento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: system-properties
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.SystemProperties}
	 * @return Object type {@link org.openspcoop2.core.config.SystemProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SystemProperties readSystemProperties(String fileName) throws DeserializerException {
		return (SystemProperties) this.xmlToObj(fileName, SystemProperties.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.SystemProperties}
	 * @return Object type {@link org.openspcoop2.core.config.SystemProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SystemProperties readSystemProperties(File file) throws DeserializerException {
		return (SystemProperties) this.xmlToObj(file, SystemProperties.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.SystemProperties}
	 * @return Object type {@link org.openspcoop2.core.config.SystemProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SystemProperties readSystemProperties(InputStream in) throws DeserializerException {
		return (SystemProperties) this.xmlToObj(in, SystemProperties.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.SystemProperties}
	 * @return Object type {@link org.openspcoop2.core.config.SystemProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SystemProperties readSystemProperties(byte[] in) throws DeserializerException {
		return (SystemProperties) this.xmlToObj(in, SystemProperties.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.SystemProperties}
	 * @return Object type {@link org.openspcoop2.core.config.SystemProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SystemProperties readSystemPropertiesFromString(String in) throws DeserializerException {
		return (SystemProperties) this.xmlToObj(in.getBytes(), SystemProperties.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: tipo-filtro-abilitazione-servizi
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * @return Object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TipoFiltroAbilitazioneServizi readTipoFiltroAbilitazioneServizi(String fileName) throws DeserializerException {
		return (TipoFiltroAbilitazioneServizi) this.xmlToObj(fileName, TipoFiltroAbilitazioneServizi.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * @return Object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TipoFiltroAbilitazioneServizi readTipoFiltroAbilitazioneServizi(File file) throws DeserializerException {
		return (TipoFiltroAbilitazioneServizi) this.xmlToObj(file, TipoFiltroAbilitazioneServizi.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * @return Object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TipoFiltroAbilitazioneServizi readTipoFiltroAbilitazioneServizi(InputStream in) throws DeserializerException {
		return (TipoFiltroAbilitazioneServizi) this.xmlToObj(in, TipoFiltroAbilitazioneServizi.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * @return Object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TipoFiltroAbilitazioneServizi readTipoFiltroAbilitazioneServizi(byte[] in) throws DeserializerException {
		return (TipoFiltroAbilitazioneServizi) this.xmlToObj(in, TipoFiltroAbilitazioneServizi.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * @return Object type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TipoFiltroAbilitazioneServizi readTipoFiltroAbilitazioneServiziFromString(String in) throws DeserializerException {
		return (TipoFiltroAbilitazioneServizi) this.xmlToObj(in.getBytes(), TipoFiltroAbilitazioneServizi.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: stato-servizi-pdd-porta-delegata
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddPortaDelegata readStatoServiziPddPortaDelegata(String fileName) throws DeserializerException {
		return (StatoServiziPddPortaDelegata) this.xmlToObj(fileName, StatoServiziPddPortaDelegata.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddPortaDelegata readStatoServiziPddPortaDelegata(File file) throws DeserializerException {
		return (StatoServiziPddPortaDelegata) this.xmlToObj(file, StatoServiziPddPortaDelegata.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddPortaDelegata readStatoServiziPddPortaDelegata(InputStream in) throws DeserializerException {
		return (StatoServiziPddPortaDelegata) this.xmlToObj(in, StatoServiziPddPortaDelegata.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddPortaDelegata readStatoServiziPddPortaDelegata(byte[] in) throws DeserializerException {
		return (StatoServiziPddPortaDelegata) this.xmlToObj(in, StatoServiziPddPortaDelegata.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddPortaDelegata readStatoServiziPddPortaDelegataFromString(String in) throws DeserializerException {
		return (StatoServiziPddPortaDelegata) this.xmlToObj(in.getBytes(), StatoServiziPddPortaDelegata.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: route
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Route}
	 * @return Object type {@link org.openspcoop2.core.config.Route}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Route readRoute(String fileName) throws DeserializerException {
		return (Route) this.xmlToObj(fileName, Route.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Route}
	 * @return Object type {@link org.openspcoop2.core.config.Route}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Route readRoute(File file) throws DeserializerException {
		return (Route) this.xmlToObj(file, Route.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Route}
	 * @return Object type {@link org.openspcoop2.core.config.Route}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Route readRoute(InputStream in) throws DeserializerException {
		return (Route) this.xmlToObj(in, Route.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Route}
	 * @return Object type {@link org.openspcoop2.core.config.Route}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Route readRoute(byte[] in) throws DeserializerException {
		return (Route) this.xmlToObj(in, Route.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Route}
	 * @return Object type {@link org.openspcoop2.core.config.Route}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Route readRouteFromString(String in) throws DeserializerException {
		return (Route) this.xmlToObj(in.getBytes(), Route.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: invocazione-porta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazionePorta readInvocazionePorta(String fileName) throws DeserializerException {
		return (InvocazionePorta) this.xmlToObj(fileName, InvocazionePorta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazionePorta readInvocazionePorta(File file) throws DeserializerException {
		return (InvocazionePorta) this.xmlToObj(file, InvocazionePorta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazionePorta readInvocazionePorta(InputStream in) throws DeserializerException {
		return (InvocazionePorta) this.xmlToObj(in, InvocazionePorta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazionePorta readInvocazionePorta(byte[] in) throws DeserializerException {
		return (InvocazionePorta) this.xmlToObj(in, InvocazionePorta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazionePorta readInvocazionePortaFromString(String in) throws DeserializerException {
		return (InvocazionePorta) this.xmlToObj(in.getBytes(), InvocazionePorta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: invocazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazioneServizio readInvocazioneServizio(String fileName) throws DeserializerException {
		return (InvocazioneServizio) this.xmlToObj(fileName, InvocazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazioneServizio readInvocazioneServizio(File file) throws DeserializerException {
		return (InvocazioneServizio) this.xmlToObj(file, InvocazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazioneServizio readInvocazioneServizio(InputStream in) throws DeserializerException {
		return (InvocazioneServizio) this.xmlToObj(in, InvocazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazioneServizio readInvocazioneServizio(byte[] in) throws DeserializerException {
		return (InvocazioneServizio) this.xmlToObj(in, InvocazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazioneServizio readInvocazioneServizioFromString(String in) throws DeserializerException {
		return (InvocazioneServizio) this.xmlToObj(in.getBytes(), InvocazioneServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: risposta-asincrona
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * @return Object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaAsincrona readRispostaAsincrona(String fileName) throws DeserializerException {
		return (RispostaAsincrona) this.xmlToObj(fileName, RispostaAsincrona.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * @return Object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaAsincrona readRispostaAsincrona(File file) throws DeserializerException {
		return (RispostaAsincrona) this.xmlToObj(file, RispostaAsincrona.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * @return Object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaAsincrona readRispostaAsincrona(InputStream in) throws DeserializerException {
		return (RispostaAsincrona) this.xmlToObj(in, RispostaAsincrona.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * @return Object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaAsincrona readRispostaAsincrona(byte[] in) throws DeserializerException {
		return (RispostaAsincrona) this.xmlToObj(in, RispostaAsincrona.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * @return Object type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaAsincrona readRispostaAsincronaFromString(String in) throws DeserializerException {
		return (RispostaAsincrona) this.xmlToObj(in.getBytes(), RispostaAsincrona.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(String fileName) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(fileName, IdSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(File file) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(file, IdSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(InputStream in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in, IdSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(byte[] in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in, IdSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.config.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggettoFromString(String in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in.getBytes(), IdSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdServizioApplicativo readIdServizioApplicativo(String fileName) throws DeserializerException {
		return (IdServizioApplicativo) this.xmlToObj(fileName, IdServizioApplicativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdServizioApplicativo readIdServizioApplicativo(File file) throws DeserializerException {
		return (IdServizioApplicativo) this.xmlToObj(file, IdServizioApplicativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdServizioApplicativo readIdServizioApplicativo(InputStream in) throws DeserializerException {
		return (IdServizioApplicativo) this.xmlToObj(in, IdServizioApplicativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdServizioApplicativo readIdServizioApplicativo(byte[] in) throws DeserializerException {
		return (IdServizioApplicativo) this.xmlToObj(in, IdServizioApplicativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdServizioApplicativo readIdServizioApplicativoFromString(String in) throws DeserializerException {
		return (IdServizioApplicativo) this.xmlToObj(in.getBytes(), IdServizioApplicativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: integration-manager
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * @return Object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationManager readIntegrationManager(String fileName) throws DeserializerException {
		return (IntegrationManager) this.xmlToObj(fileName, IntegrationManager.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * @return Object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationManager readIntegrationManager(File file) throws DeserializerException {
		return (IntegrationManager) this.xmlToObj(file, IntegrationManager.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * @return Object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationManager readIntegrationManager(InputStream in) throws DeserializerException {
		return (IntegrationManager) this.xmlToObj(in, IntegrationManager.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * @return Object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationManager readIntegrationManager(byte[] in) throws DeserializerException {
		return (IntegrationManager) this.xmlToObj(in, IntegrationManager.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * @return Object type {@link org.openspcoop2.core.config.IntegrationManager}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationManager readIntegrationManagerFromString(String in) throws DeserializerException {
		return (IntegrationManager) this.xmlToObj(in.getBytes(), IntegrationManager.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-delegata
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDelegata readIdPortaDelegata(String fileName) throws DeserializerException {
		return (IdPortaDelegata) this.xmlToObj(fileName, IdPortaDelegata.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDelegata readIdPortaDelegata(File file) throws DeserializerException {
		return (IdPortaDelegata) this.xmlToObj(file, IdPortaDelegata.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDelegata readIdPortaDelegata(InputStream in) throws DeserializerException {
		return (IdPortaDelegata) this.xmlToObj(in, IdPortaDelegata.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDelegata readIdPortaDelegata(byte[] in) throws DeserializerException {
		return (IdPortaDelegata) this.xmlToObj(in, IdPortaDelegata.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDelegata readIdPortaDelegataFromString(String in) throws DeserializerException {
		return (IdPortaDelegata) this.xmlToObj(in.getBytes(), IdPortaDelegata.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: route-gateway
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RouteGateway}
	 * @return Object type {@link org.openspcoop2.core.config.RouteGateway}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RouteGateway readRouteGateway(String fileName) throws DeserializerException {
		return (RouteGateway) this.xmlToObj(fileName, RouteGateway.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RouteGateway}
	 * @return Object type {@link org.openspcoop2.core.config.RouteGateway}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RouteGateway readRouteGateway(File file) throws DeserializerException {
		return (RouteGateway) this.xmlToObj(file, RouteGateway.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RouteGateway}
	 * @return Object type {@link org.openspcoop2.core.config.RouteGateway}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RouteGateway readRouteGateway(InputStream in) throws DeserializerException {
		return (RouteGateway) this.xmlToObj(in, RouteGateway.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RouteGateway}
	 * @return Object type {@link org.openspcoop2.core.config.RouteGateway}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RouteGateway readRouteGateway(byte[] in) throws DeserializerException {
		return (RouteGateway) this.xmlToObj(in, RouteGateway.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RouteGateway}
	 * @return Object type {@link org.openspcoop2.core.config.RouteGateway}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RouteGateway readRouteGatewayFromString(String in) throws DeserializerException {
		return (RouteGateway) this.xmlToObj(in.getBytes(), RouteGateway.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: inoltro-buste-non-riscontrate
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * @return Object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InoltroBusteNonRiscontrate readInoltroBusteNonRiscontrate(String fileName) throws DeserializerException {
		return (InoltroBusteNonRiscontrate) this.xmlToObj(fileName, InoltroBusteNonRiscontrate.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * @return Object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InoltroBusteNonRiscontrate readInoltroBusteNonRiscontrate(File file) throws DeserializerException {
		return (InoltroBusteNonRiscontrate) this.xmlToObj(file, InoltroBusteNonRiscontrate.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * @return Object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InoltroBusteNonRiscontrate readInoltroBusteNonRiscontrate(InputStream in) throws DeserializerException {
		return (InoltroBusteNonRiscontrate) this.xmlToObj(in, InoltroBusteNonRiscontrate.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * @return Object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InoltroBusteNonRiscontrate readInoltroBusteNonRiscontrate(byte[] in) throws DeserializerException {
		return (InoltroBusteNonRiscontrate) this.xmlToObj(in, InoltroBusteNonRiscontrate.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * @return Object type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InoltroBusteNonRiscontrate readInoltroBusteNonRiscontrateFromString(String in) throws DeserializerException {
		return (InoltroBusteNonRiscontrate) this.xmlToObj(in.getBytes(), InoltroBusteNonRiscontrate.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataServizio readPortaDelegataServizio(String fileName) throws DeserializerException {
		return (PortaDelegataServizio) this.xmlToObj(fileName, PortaDelegataServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataServizio readPortaDelegataServizio(File file) throws DeserializerException {
		return (PortaDelegataServizio) this.xmlToObj(file, PortaDelegataServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataServizio readPortaDelegataServizio(InputStream in) throws DeserializerException {
		return (PortaDelegataServizio) this.xmlToObj(in, PortaDelegataServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataServizio readPortaDelegataServizio(byte[] in) throws DeserializerException {
		return (PortaDelegataServizio) this.xmlToObj(in, PortaDelegataServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataServizio readPortaDelegataServizioFromString(String in) throws DeserializerException {
		return (PortaDelegataServizio) this.xmlToObj(in.getBytes(), PortaDelegataServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: credenziali
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Credenziali}
	 * @return Object type {@link org.openspcoop2.core.config.Credenziali}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Credenziali readCredenziali(String fileName) throws DeserializerException {
		return (Credenziali) this.xmlToObj(fileName, Credenziali.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Credenziali}
	 * @return Object type {@link org.openspcoop2.core.config.Credenziali}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Credenziali readCredenziali(File file) throws DeserializerException {
		return (Credenziali) this.xmlToObj(file, Credenziali.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Credenziali}
	 * @return Object type {@link org.openspcoop2.core.config.Credenziali}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Credenziali readCredenziali(InputStream in) throws DeserializerException {
		return (Credenziali) this.xmlToObj(in, Credenziali.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Credenziali}
	 * @return Object type {@link org.openspcoop2.core.config.Credenziali}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Credenziali readCredenziali(byte[] in) throws DeserializerException {
		return (Credenziali) this.xmlToObj(in, Credenziali.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Credenziali}
	 * @return Object type {@link org.openspcoop2.core.config.Credenziali}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Credenziali readCredenzialiFromString(String in) throws DeserializerException {
		return (Credenziali) this.xmlToObj(in.getBytes(), Credenziali.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: connettore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Connettore}
	 * @return Object type {@link org.openspcoop2.core.config.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(String fileName) throws DeserializerException {
		return (Connettore) this.xmlToObj(fileName, Connettore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Connettore}
	 * @return Object type {@link org.openspcoop2.core.config.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(File file) throws DeserializerException {
		return (Connettore) this.xmlToObj(file, Connettore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Connettore}
	 * @return Object type {@link org.openspcoop2.core.config.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(InputStream in) throws DeserializerException {
		return (Connettore) this.xmlToObj(in, Connettore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Connettore}
	 * @return Object type {@link org.openspcoop2.core.config.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(byte[] in) throws DeserializerException {
		return (Connettore) this.xmlToObj(in, Connettore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Connettore}
	 * @return Object type {@link org.openspcoop2.core.config.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettoreFromString(String in) throws DeserializerException {
		return (Connettore) this.xmlToObj(in.getBytes(), Connettore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accesso-registro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoRegistro readAccessoRegistro(String fileName) throws DeserializerException {
		return (AccessoRegistro) this.xmlToObj(fileName, AccessoRegistro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoRegistro readAccessoRegistro(File file) throws DeserializerException {
		return (AccessoRegistro) this.xmlToObj(file, AccessoRegistro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoRegistro readAccessoRegistro(InputStream in) throws DeserializerException {
		return (AccessoRegistro) this.xmlToObj(in, AccessoRegistro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoRegistro readAccessoRegistro(byte[] in) throws DeserializerException {
		return (AccessoRegistro) this.xmlToObj(in, AccessoRegistro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoRegistro readAccessoRegistroFromString(String in) throws DeserializerException {
		return (AccessoRegistro) this.xmlToObj(in.getBytes(), AccessoRegistro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accesso-configurazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoConfigurazione readAccessoConfigurazione(String fileName) throws DeserializerException {
		return (AccessoConfigurazione) this.xmlToObj(fileName, AccessoConfigurazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoConfigurazione readAccessoConfigurazione(File file) throws DeserializerException {
		return (AccessoConfigurazione) this.xmlToObj(file, AccessoConfigurazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoConfigurazione readAccessoConfigurazione(InputStream in) throws DeserializerException {
		return (AccessoConfigurazione) this.xmlToObj(in, AccessoConfigurazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoConfigurazione readAccessoConfigurazione(byte[] in) throws DeserializerException {
		return (AccessoConfigurazione) this.xmlToObj(in, AccessoConfigurazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoConfigurazione readAccessoConfigurazioneFromString(String in) throws DeserializerException {
		return (AccessoConfigurazione) this.xmlToObj(in.getBytes(), AccessoConfigurazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: validazione-buste
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * @return Object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ValidazioneBuste readValidazioneBuste(String fileName) throws DeserializerException {
		return (ValidazioneBuste) this.xmlToObj(fileName, ValidazioneBuste.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * @return Object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ValidazioneBuste readValidazioneBuste(File file) throws DeserializerException {
		return (ValidazioneBuste) this.xmlToObj(file, ValidazioneBuste.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * @return Object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ValidazioneBuste readValidazioneBuste(InputStream in) throws DeserializerException {
		return (ValidazioneBuste) this.xmlToObj(in, ValidazioneBuste.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * @return Object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ValidazioneBuste readValidazioneBuste(byte[] in) throws DeserializerException {
		return (ValidazioneBuste) this.xmlToObj(in, ValidazioneBuste.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * @return Object type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ValidazioneBuste readValidazioneBusteFromString(String in) throws DeserializerException {
		return (ValidazioneBuste) this.xmlToObj(in.getBytes(), ValidazioneBuste.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: indirizzo-risposta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * @return Object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoRisposta readIndirizzoRisposta(String fileName) throws DeserializerException {
		return (IndirizzoRisposta) this.xmlToObj(fileName, IndirizzoRisposta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * @return Object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoRisposta readIndirizzoRisposta(File file) throws DeserializerException {
		return (IndirizzoRisposta) this.xmlToObj(file, IndirizzoRisposta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * @return Object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoRisposta readIndirizzoRisposta(InputStream in) throws DeserializerException {
		return (IndirizzoRisposta) this.xmlToObj(in, IndirizzoRisposta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * @return Object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoRisposta readIndirizzoRisposta(byte[] in) throws DeserializerException {
		return (IndirizzoRisposta) this.xmlToObj(in, IndirizzoRisposta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * @return Object type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IndirizzoRisposta readIndirizzoRispostaFromString(String in) throws DeserializerException {
		return (IndirizzoRisposta) this.xmlToObj(in.getBytes(), IndirizzoRisposta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: attachments
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Attachments}
	 * @return Object type {@link org.openspcoop2.core.config.Attachments}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Attachments readAttachments(String fileName) throws DeserializerException {
		return (Attachments) this.xmlToObj(fileName, Attachments.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Attachments}
	 * @return Object type {@link org.openspcoop2.core.config.Attachments}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Attachments readAttachments(File file) throws DeserializerException {
		return (Attachments) this.xmlToObj(file, Attachments.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Attachments}
	 * @return Object type {@link org.openspcoop2.core.config.Attachments}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Attachments readAttachments(InputStream in) throws DeserializerException {
		return (Attachments) this.xmlToObj(in, Attachments.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Attachments}
	 * @return Object type {@link org.openspcoop2.core.config.Attachments}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Attachments readAttachments(byte[] in) throws DeserializerException {
		return (Attachments) this.xmlToObj(in, Attachments.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Attachments}
	 * @return Object type {@link org.openspcoop2.core.config.Attachments}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Attachments readAttachmentsFromString(String in) throws DeserializerException {
		return (Attachments) this.xmlToObj(in.getBytes(), Attachments.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: risposte
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Risposte}
	 * @return Object type {@link org.openspcoop2.core.config.Risposte}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Risposte readRisposte(String fileName) throws DeserializerException {
		return (Risposte) this.xmlToObj(fileName, Risposte.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Risposte}
	 * @return Object type {@link org.openspcoop2.core.config.Risposte}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Risposte readRisposte(File file) throws DeserializerException {
		return (Risposte) this.xmlToObj(file, Risposte.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Risposte}
	 * @return Object type {@link org.openspcoop2.core.config.Risposte}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Risposte readRisposte(InputStream in) throws DeserializerException {
		return (Risposte) this.xmlToObj(in, Risposte.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Risposte}
	 * @return Object type {@link org.openspcoop2.core.config.Risposte}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Risposte readRisposte(byte[] in) throws DeserializerException {
		return (Risposte) this.xmlToObj(in, Risposte.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.Risposte}
	 * @return Object type {@link org.openspcoop2.core.config.Risposte}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Risposte readRisposteFromString(String in) throws DeserializerException {
		return (Risposte) this.xmlToObj(in.getBytes(), Risposte.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-gestione-errore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGestioneErrore readConfigurazioneGestioneErrore(String fileName) throws DeserializerException {
		return (ConfigurazioneGestioneErrore) this.xmlToObj(fileName, ConfigurazioneGestioneErrore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGestioneErrore readConfigurazioneGestioneErrore(File file) throws DeserializerException {
		return (ConfigurazioneGestioneErrore) this.xmlToObj(file, ConfigurazioneGestioneErrore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGestioneErrore readConfigurazioneGestioneErrore(InputStream in) throws DeserializerException {
		return (ConfigurazioneGestioneErrore) this.xmlToObj(in, ConfigurazioneGestioneErrore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGestioneErrore readConfigurazioneGestioneErrore(byte[] in) throws DeserializerException {
		return (ConfigurazioneGestioneErrore) this.xmlToObj(in, ConfigurazioneGestioneErrore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneGestioneErrore readConfigurazioneGestioneErroreFromString(String in) throws DeserializerException {
		return (ConfigurazioneGestioneErrore) this.xmlToObj(in.getBytes(), ConfigurazioneGestioneErrore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: stato-servizi-pdd
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPdd readStatoServiziPdd(String fileName) throws DeserializerException {
		return (StatoServiziPdd) this.xmlToObj(fileName, StatoServiziPdd.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPdd readStatoServiziPdd(File file) throws DeserializerException {
		return (StatoServiziPdd) this.xmlToObj(file, StatoServiziPdd.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPdd readStatoServiziPdd(InputStream in) throws DeserializerException {
		return (StatoServiziPdd) this.xmlToObj(in, StatoServiziPdd.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPdd readStatoServiziPdd(byte[] in) throws DeserializerException {
		return (StatoServiziPdd) this.xmlToObj(in, StatoServiziPdd.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPdd readStatoServiziPddFromString(String in) throws DeserializerException {
		return (StatoServiziPdd) this.xmlToObj(in.getBytes(), StatoServiziPdd.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-soggetto-erogatore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataSoggettoErogatore readPortaDelegataSoggettoErogatore(String fileName) throws DeserializerException {
		return (PortaDelegataSoggettoErogatore) this.xmlToObj(fileName, PortaDelegataSoggettoErogatore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataSoggettoErogatore readPortaDelegataSoggettoErogatore(File file) throws DeserializerException {
		return (PortaDelegataSoggettoErogatore) this.xmlToObj(file, PortaDelegataSoggettoErogatore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataSoggettoErogatore readPortaDelegataSoggettoErogatore(InputStream in) throws DeserializerException {
		return (PortaDelegataSoggettoErogatore) this.xmlToObj(in, PortaDelegataSoggettoErogatore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataSoggettoErogatore readPortaDelegataSoggettoErogatore(byte[] in) throws DeserializerException {
		return (PortaDelegataSoggettoErogatore) this.xmlToObj(in, PortaDelegataSoggettoErogatore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataSoggettoErogatore readPortaDelegataSoggettoErogatoreFromString(String in) throws DeserializerException {
		return (PortaDelegataSoggettoErogatore) this.xmlToObj(in.getBytes(), PortaDelegataSoggettoErogatore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataAzione readPortaDelegataAzione(String fileName) throws DeserializerException {
		return (PortaDelegataAzione) this.xmlToObj(fileName, PortaDelegataAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataAzione readPortaDelegataAzione(File file) throws DeserializerException {
		return (PortaDelegataAzione) this.xmlToObj(file, PortaDelegataAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataAzione readPortaDelegataAzione(InputStream in) throws DeserializerException {
		return (PortaDelegataAzione) this.xmlToObj(in, PortaDelegataAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataAzione readPortaDelegataAzione(byte[] in) throws DeserializerException {
		return (PortaDelegataAzione) this.xmlToObj(in, PortaDelegataAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataAzione readPortaDelegataAzioneFromString(String in) throws DeserializerException {
		return (PortaDelegataAzione) this.xmlToObj(in.getBytes(), PortaDelegataAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: route-registro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RouteRegistro readRouteRegistro(String fileName) throws DeserializerException {
		return (RouteRegistro) this.xmlToObj(fileName, RouteRegistro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RouteRegistro readRouteRegistro(File file) throws DeserializerException {
		return (RouteRegistro) this.xmlToObj(file, RouteRegistro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RouteRegistro readRouteRegistro(InputStream in) throws DeserializerException {
		return (RouteRegistro) this.xmlToObj(in, RouteRegistro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RouteRegistro readRouteRegistro(byte[] in) throws DeserializerException {
		return (RouteRegistro) this.xmlToObj(in, RouteRegistro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.RouteRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RouteRegistro readRouteRegistroFromString(String in) throws DeserializerException {
		return (RouteRegistro) this.xmlToObj(in.getBytes(), RouteRegistro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accesso-registro-registro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoRegistroRegistro readAccessoRegistroRegistro(String fileName) throws DeserializerException {
		return (AccessoRegistroRegistro) this.xmlToObj(fileName, AccessoRegistroRegistro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoRegistroRegistro readAccessoRegistroRegistro(File file) throws DeserializerException {
		return (AccessoRegistroRegistro) this.xmlToObj(file, AccessoRegistroRegistro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoRegistroRegistro readAccessoRegistroRegistro(InputStream in) throws DeserializerException {
		return (AccessoRegistroRegistro) this.xmlToObj(in, AccessoRegistroRegistro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoRegistroRegistro readAccessoRegistroRegistro(byte[] in) throws DeserializerException {
		return (AccessoRegistroRegistro) this.xmlToObj(in, AccessoRegistroRegistro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * @return Object type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccessoRegistroRegistro readAccessoRegistroRegistroFromString(String in) throws DeserializerException {
		return (AccessoRegistroRegistro) this.xmlToObj(in.getBytes(), AccessoRegistroRegistro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: invocazione-porta-gestione-errore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazionePortaGestioneErrore readInvocazionePortaGestioneErrore(String fileName) throws DeserializerException {
		return (InvocazionePortaGestioneErrore) this.xmlToObj(fileName, InvocazionePortaGestioneErrore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazionePortaGestioneErrore readInvocazionePortaGestioneErrore(File file) throws DeserializerException {
		return (InvocazionePortaGestioneErrore) this.xmlToObj(file, InvocazionePortaGestioneErrore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazionePortaGestioneErrore readInvocazionePortaGestioneErrore(InputStream in) throws DeserializerException {
		return (InvocazionePortaGestioneErrore) this.xmlToObj(in, InvocazionePortaGestioneErrore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazionePortaGestioneErrore readInvocazionePortaGestioneErrore(byte[] in) throws DeserializerException {
		return (InvocazionePortaGestioneErrore) this.xmlToObj(in, InvocazionePortaGestioneErrore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * @return Object type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InvocazionePortaGestioneErrore readInvocazionePortaGestioneErroreFromString(String in) throws DeserializerException {
		return (InvocazionePortaGestioneErrore) this.xmlToObj(in.getBytes(), InvocazionePortaGestioneErrore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(String fileName) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(fileName, PortaDelegata.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(File file) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(file, PortaDelegata.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(InputStream in) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(in, PortaDelegata.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(byte[] in) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(in, PortaDelegata.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.config.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegataFromString(String in) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(in.getBytes(), PortaDelegata.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: stato-servizi-pdd-porta-applicativa
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddPortaApplicativa readStatoServiziPddPortaApplicativa(String fileName) throws DeserializerException {
		return (StatoServiziPddPortaApplicativa) this.xmlToObj(fileName, StatoServiziPddPortaApplicativa.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddPortaApplicativa readStatoServiziPddPortaApplicativa(File file) throws DeserializerException {
		return (StatoServiziPddPortaApplicativa) this.xmlToObj(file, StatoServiziPddPortaApplicativa.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddPortaApplicativa readStatoServiziPddPortaApplicativa(InputStream in) throws DeserializerException {
		return (StatoServiziPddPortaApplicativa) this.xmlToObj(in, StatoServiziPddPortaApplicativa.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddPortaApplicativa readStatoServiziPddPortaApplicativa(byte[] in) throws DeserializerException {
		return (StatoServiziPddPortaApplicativa) this.xmlToObj(in, StatoServiziPddPortaApplicativa.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddPortaApplicativa readStatoServiziPddPortaApplicativaFromString(String in) throws DeserializerException {
		return (StatoServiziPddPortaApplicativa) this.xmlToObj(in.getBytes(), StatoServiziPddPortaApplicativa.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: stato-servizi-pdd-integration-manager
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddIntegrationManager readStatoServiziPddIntegrationManager(String fileName) throws DeserializerException {
		return (StatoServiziPddIntegrationManager) this.xmlToObj(fileName, StatoServiziPddIntegrationManager.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddIntegrationManager readStatoServiziPddIntegrationManager(File file) throws DeserializerException {
		return (StatoServiziPddIntegrationManager) this.xmlToObj(file, StatoServiziPddIntegrationManager.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddIntegrationManager readStatoServiziPddIntegrationManager(InputStream in) throws DeserializerException {
		return (StatoServiziPddIntegrationManager) this.xmlToObj(in, StatoServiziPddIntegrationManager.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddIntegrationManager readStatoServiziPddIntegrationManager(byte[] in) throws DeserializerException {
		return (StatoServiziPddIntegrationManager) this.xmlToObj(in, StatoServiziPddIntegrationManager.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * @return Object type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoServiziPddIntegrationManager readStatoServiziPddIntegrationManagerFromString(String in) throws DeserializerException {
		return (StatoServiziPddIntegrationManager) this.xmlToObj(in.getBytes(), StatoServiziPddIntegrationManager.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-applicativa
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaApplicativa readIdPortaApplicativa(String fileName) throws DeserializerException {
		return (IdPortaApplicativa) this.xmlToObj(fileName, IdPortaApplicativa.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaApplicativa readIdPortaApplicativa(File file) throws DeserializerException {
		return (IdPortaApplicativa) this.xmlToObj(file, IdPortaApplicativa.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaApplicativa readIdPortaApplicativa(InputStream in) throws DeserializerException {
		return (IdPortaApplicativa) this.xmlToObj(in, IdPortaApplicativa.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaApplicativa readIdPortaApplicativa(byte[] in) throws DeserializerException {
		return (IdPortaApplicativa) this.xmlToObj(in, IdPortaApplicativa.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaApplicativa readIdPortaApplicativaFromString(String in) throws DeserializerException {
		return (IdPortaApplicativa) this.xmlToObj(in.getBytes(), IdPortaApplicativa.class);
	}	
	
	
	

}
