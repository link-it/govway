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

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

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
	 Object: message-security-flow-parameter
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageSecurityFlowParameter</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageSecurityFlowParameter</var>
	 * @param messageSecurityFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageSecurityFlowParameter messageSecurityFlowParameter) throws SerializerException {
		this.objToXml(fileName, MessageSecurityFlowParameter.class, messageSecurityFlowParameter, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageSecurityFlowParameter</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageSecurityFlowParameter</var>
	 * @param messageSecurityFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageSecurityFlowParameter messageSecurityFlowParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessageSecurityFlowParameter.class, messageSecurityFlowParameter, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageSecurityFlowParameter</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param file Xml file to serialize the object <var>messageSecurityFlowParameter</var>
	 * @param messageSecurityFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageSecurityFlowParameter messageSecurityFlowParameter) throws SerializerException {
		this.objToXml(file, MessageSecurityFlowParameter.class, messageSecurityFlowParameter, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageSecurityFlowParameter</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param file Xml file to serialize the object <var>messageSecurityFlowParameter</var>
	 * @param messageSecurityFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageSecurityFlowParameter messageSecurityFlowParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessageSecurityFlowParameter.class, messageSecurityFlowParameter, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageSecurityFlowParameter</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>messageSecurityFlowParameter</var>
	 * @param messageSecurityFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageSecurityFlowParameter messageSecurityFlowParameter) throws SerializerException {
		this.objToXml(out, MessageSecurityFlowParameter.class, messageSecurityFlowParameter, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageSecurityFlowParameter</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>messageSecurityFlowParameter</var>
	 * @param messageSecurityFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageSecurityFlowParameter messageSecurityFlowParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessageSecurityFlowParameter.class, messageSecurityFlowParameter, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messageSecurityFlowParameter</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param messageSecurityFlowParameter Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageSecurityFlowParameter messageSecurityFlowParameter) throws SerializerException {
		return this.objToXml(MessageSecurityFlowParameter.class, messageSecurityFlowParameter, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messageSecurityFlowParameter</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param messageSecurityFlowParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageSecurityFlowParameter messageSecurityFlowParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageSecurityFlowParameter.class, messageSecurityFlowParameter, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messageSecurityFlowParameter</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param messageSecurityFlowParameter Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageSecurityFlowParameter messageSecurityFlowParameter) throws SerializerException {
		return this.objToXml(MessageSecurityFlowParameter.class, messageSecurityFlowParameter, false).toString();
	}
	/**
	 * Serialize to String the object <var>messageSecurityFlowParameter</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlowParameter}
	 * 
	 * @param messageSecurityFlowParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageSecurityFlowParameter messageSecurityFlowParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageSecurityFlowParameter.class, messageSecurityFlowParameter, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: message-security-flow
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageSecurityFlow</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageSecurityFlow</var>
	 * @param messageSecurityFlow Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageSecurityFlow messageSecurityFlow) throws SerializerException {
		this.objToXml(fileName, MessageSecurityFlow.class, messageSecurityFlow, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageSecurityFlow</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageSecurityFlow</var>
	 * @param messageSecurityFlow Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageSecurityFlow messageSecurityFlow,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessageSecurityFlow.class, messageSecurityFlow, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageSecurityFlow</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param file Xml file to serialize the object <var>messageSecurityFlow</var>
	 * @param messageSecurityFlow Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageSecurityFlow messageSecurityFlow) throws SerializerException {
		this.objToXml(file, MessageSecurityFlow.class, messageSecurityFlow, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageSecurityFlow</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param file Xml file to serialize the object <var>messageSecurityFlow</var>
	 * @param messageSecurityFlow Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageSecurityFlow messageSecurityFlow,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessageSecurityFlow.class, messageSecurityFlow, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageSecurityFlow</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param out OutputStream to serialize the object <var>messageSecurityFlow</var>
	 * @param messageSecurityFlow Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageSecurityFlow messageSecurityFlow) throws SerializerException {
		this.objToXml(out, MessageSecurityFlow.class, messageSecurityFlow, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageSecurityFlow</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param out OutputStream to serialize the object <var>messageSecurityFlow</var>
	 * @param messageSecurityFlow Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageSecurityFlow messageSecurityFlow,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessageSecurityFlow.class, messageSecurityFlow, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messageSecurityFlow</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param messageSecurityFlow Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageSecurityFlow messageSecurityFlow) throws SerializerException {
		return this.objToXml(MessageSecurityFlow.class, messageSecurityFlow, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messageSecurityFlow</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param messageSecurityFlow Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageSecurityFlow messageSecurityFlow,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageSecurityFlow.class, messageSecurityFlow, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messageSecurityFlow</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param messageSecurityFlow Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageSecurityFlow messageSecurityFlow) throws SerializerException {
		return this.objToXml(MessageSecurityFlow.class, messageSecurityFlow, false).toString();
	}
	/**
	 * Serialize to String the object <var>messageSecurityFlow</var> of type {@link org.openspcoop2.core.config.MessageSecurityFlow}
	 * 
	 * @param messageSecurityFlow Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageSecurityFlow messageSecurityFlow,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageSecurityFlow.class, messageSecurityFlow, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: correlazione-applicativa-risposta-elemento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>correlazioneApplicativaRispostaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param fileName Xml file to serialize the object <var>correlazioneApplicativaRispostaElemento</var>
	 * @param correlazioneApplicativaRispostaElemento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorrelazioneApplicativaRispostaElemento correlazioneApplicativaRispostaElemento) throws SerializerException {
		this.objToXml(fileName, CorrelazioneApplicativaRispostaElemento.class, correlazioneApplicativaRispostaElemento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>correlazioneApplicativaRispostaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param fileName Xml file to serialize the object <var>correlazioneApplicativaRispostaElemento</var>
	 * @param correlazioneApplicativaRispostaElemento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorrelazioneApplicativaRispostaElemento correlazioneApplicativaRispostaElemento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CorrelazioneApplicativaRispostaElemento.class, correlazioneApplicativaRispostaElemento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>correlazioneApplicativaRispostaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param file Xml file to serialize the object <var>correlazioneApplicativaRispostaElemento</var>
	 * @param correlazioneApplicativaRispostaElemento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorrelazioneApplicativaRispostaElemento correlazioneApplicativaRispostaElemento) throws SerializerException {
		this.objToXml(file, CorrelazioneApplicativaRispostaElemento.class, correlazioneApplicativaRispostaElemento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>correlazioneApplicativaRispostaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param file Xml file to serialize the object <var>correlazioneApplicativaRispostaElemento</var>
	 * @param correlazioneApplicativaRispostaElemento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorrelazioneApplicativaRispostaElemento correlazioneApplicativaRispostaElemento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CorrelazioneApplicativaRispostaElemento.class, correlazioneApplicativaRispostaElemento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>correlazioneApplicativaRispostaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param out OutputStream to serialize the object <var>correlazioneApplicativaRispostaElemento</var>
	 * @param correlazioneApplicativaRispostaElemento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorrelazioneApplicativaRispostaElemento correlazioneApplicativaRispostaElemento) throws SerializerException {
		this.objToXml(out, CorrelazioneApplicativaRispostaElemento.class, correlazioneApplicativaRispostaElemento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>correlazioneApplicativaRispostaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param out OutputStream to serialize the object <var>correlazioneApplicativaRispostaElemento</var>
	 * @param correlazioneApplicativaRispostaElemento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorrelazioneApplicativaRispostaElemento correlazioneApplicativaRispostaElemento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CorrelazioneApplicativaRispostaElemento.class, correlazioneApplicativaRispostaElemento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>correlazioneApplicativaRispostaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param correlazioneApplicativaRispostaElemento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorrelazioneApplicativaRispostaElemento correlazioneApplicativaRispostaElemento) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaRispostaElemento.class, correlazioneApplicativaRispostaElemento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>correlazioneApplicativaRispostaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param correlazioneApplicativaRispostaElemento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorrelazioneApplicativaRispostaElemento correlazioneApplicativaRispostaElemento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaRispostaElemento.class, correlazioneApplicativaRispostaElemento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>correlazioneApplicativaRispostaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param correlazioneApplicativaRispostaElemento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorrelazioneApplicativaRispostaElemento correlazioneApplicativaRispostaElemento) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaRispostaElemento.class, correlazioneApplicativaRispostaElemento, false).toString();
	}
	/**
	 * Serialize to String the object <var>correlazioneApplicativaRispostaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento}
	 * 
	 * @param correlazioneApplicativaRispostaElemento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorrelazioneApplicativaRispostaElemento correlazioneApplicativaRispostaElemento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaRispostaElemento.class, correlazioneApplicativaRispostaElemento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: correlazione-applicativa-risposta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>correlazioneApplicativaRisposta</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param fileName Xml file to serialize the object <var>correlazioneApplicativaRisposta</var>
	 * @param correlazioneApplicativaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta) throws SerializerException {
		this.objToXml(fileName, CorrelazioneApplicativaRisposta.class, correlazioneApplicativaRisposta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>correlazioneApplicativaRisposta</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param fileName Xml file to serialize the object <var>correlazioneApplicativaRisposta</var>
	 * @param correlazioneApplicativaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CorrelazioneApplicativaRisposta.class, correlazioneApplicativaRisposta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>correlazioneApplicativaRisposta</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param file Xml file to serialize the object <var>correlazioneApplicativaRisposta</var>
	 * @param correlazioneApplicativaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta) throws SerializerException {
		this.objToXml(file, CorrelazioneApplicativaRisposta.class, correlazioneApplicativaRisposta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>correlazioneApplicativaRisposta</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param file Xml file to serialize the object <var>correlazioneApplicativaRisposta</var>
	 * @param correlazioneApplicativaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CorrelazioneApplicativaRisposta.class, correlazioneApplicativaRisposta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>correlazioneApplicativaRisposta</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param out OutputStream to serialize the object <var>correlazioneApplicativaRisposta</var>
	 * @param correlazioneApplicativaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta) throws SerializerException {
		this.objToXml(out, CorrelazioneApplicativaRisposta.class, correlazioneApplicativaRisposta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>correlazioneApplicativaRisposta</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param out OutputStream to serialize the object <var>correlazioneApplicativaRisposta</var>
	 * @param correlazioneApplicativaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CorrelazioneApplicativaRisposta.class, correlazioneApplicativaRisposta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>correlazioneApplicativaRisposta</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param correlazioneApplicativaRisposta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaRisposta.class, correlazioneApplicativaRisposta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>correlazioneApplicativaRisposta</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param correlazioneApplicativaRisposta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaRisposta.class, correlazioneApplicativaRisposta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>correlazioneApplicativaRisposta</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param correlazioneApplicativaRisposta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaRisposta.class, correlazioneApplicativaRisposta, false).toString();
	}
	/**
	 * Serialize to String the object <var>correlazioneApplicativaRisposta</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaRisposta}
	 * 
	 * @param correlazioneApplicativaRisposta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaRisposta.class, correlazioneApplicativaRisposta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: cache
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cache</var> of type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param fileName Xml file to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Cache cache) throws SerializerException {
		this.objToXml(fileName, Cache.class, cache, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>cache</var> of type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param fileName Xml file to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Cache cache,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Cache.class, cache, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>cache</var> of type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param file Xml file to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Cache cache) throws SerializerException {
		this.objToXml(file, Cache.class, cache, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>cache</var> of type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param file Xml file to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Cache cache,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Cache.class, cache, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>cache</var> of type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param out OutputStream to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Cache cache) throws SerializerException {
		this.objToXml(out, Cache.class, cache, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>cache</var> of type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param out OutputStream to serialize the object <var>cache</var>
	 * @param cache Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Cache cache,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Cache.class, cache, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>cache</var> of type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param cache Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Cache cache) throws SerializerException {
		return this.objToXml(Cache.class, cache, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>cache</var> of type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param cache Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Cache cache,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Cache.class, cache, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>cache</var> of type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param cache Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Cache cache) throws SerializerException {
		return this.objToXml(Cache.class, cache, false).toString();
	}
	/**
	 * Serialize to String the object <var>cache</var> of type {@link org.openspcoop2.core.config.Cache}
	 * 
	 * @param cache Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Cache cache,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Cache.class, cache, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.config.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(fileName, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Openspcoop2.class, openspcoop2, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(file, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Openspcoop2.class, openspcoop2, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(out, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Openspcoop2.class, openspcoop2, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>openspcoop2</var> of type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Openspcoop2 openspcoop2) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>openspcoop2</var> of type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>openspcoop2</var> of type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Openspcoop2 openspcoop2) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, false).toString();
	}
	/**
	 * Serialize to String the object <var>openspcoop2</var> of type {@link org.openspcoop2.core.config.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazione</var> of type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazione</var>
	 * @param configurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Configurazione configurazione) throws SerializerException {
		this.objToXml(fileName, Configurazione.class, configurazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazione</var> of type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazione</var>
	 * @param configurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Configurazione configurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Configurazione.class, configurazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazione</var> of type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazione</var>
	 * @param configurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Configurazione configurazione) throws SerializerException {
		this.objToXml(file, Configurazione.class, configurazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazione</var> of type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazione</var>
	 * @param configurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Configurazione configurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Configurazione.class, configurazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazione</var> of type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazione</var>
	 * @param configurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Configurazione configurazione) throws SerializerException {
		this.objToXml(out, Configurazione.class, configurazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazione</var> of type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazione</var>
	 * @param configurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Configurazione configurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Configurazione.class, configurazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazione</var> of type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param configurazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Configurazione configurazione) throws SerializerException {
		return this.objToXml(Configurazione.class, configurazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazione</var> of type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param configurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Configurazione configurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Configurazione.class, configurazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazione</var> of type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param configurazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Configurazione configurazione) throws SerializerException {
		return this.objToXml(Configurazione.class, configurazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazione</var> of type {@link org.openspcoop2.core.config.Configurazione}
	 * 
	 * @param configurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Configurazione configurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Configurazione.class, configurazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: gestione-errore-soap-fault
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gestioneErroreSoapFault</var> of type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param fileName Xml file to serialize the object <var>gestioneErroreSoapFault</var>
	 * @param gestioneErroreSoapFault Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GestioneErroreSoapFault gestioneErroreSoapFault) throws SerializerException {
		this.objToXml(fileName, GestioneErroreSoapFault.class, gestioneErroreSoapFault, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gestioneErroreSoapFault</var> of type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param fileName Xml file to serialize the object <var>gestioneErroreSoapFault</var>
	 * @param gestioneErroreSoapFault Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GestioneErroreSoapFault gestioneErroreSoapFault,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GestioneErroreSoapFault.class, gestioneErroreSoapFault, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>gestioneErroreSoapFault</var> of type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param file Xml file to serialize the object <var>gestioneErroreSoapFault</var>
	 * @param gestioneErroreSoapFault Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GestioneErroreSoapFault gestioneErroreSoapFault) throws SerializerException {
		this.objToXml(file, GestioneErroreSoapFault.class, gestioneErroreSoapFault, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>gestioneErroreSoapFault</var> of type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param file Xml file to serialize the object <var>gestioneErroreSoapFault</var>
	 * @param gestioneErroreSoapFault Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GestioneErroreSoapFault gestioneErroreSoapFault,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GestioneErroreSoapFault.class, gestioneErroreSoapFault, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>gestioneErroreSoapFault</var> of type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param out OutputStream to serialize the object <var>gestioneErroreSoapFault</var>
	 * @param gestioneErroreSoapFault Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GestioneErroreSoapFault gestioneErroreSoapFault) throws SerializerException {
		this.objToXml(out, GestioneErroreSoapFault.class, gestioneErroreSoapFault, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>gestioneErroreSoapFault</var> of type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param out OutputStream to serialize the object <var>gestioneErroreSoapFault</var>
	 * @param gestioneErroreSoapFault Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GestioneErroreSoapFault gestioneErroreSoapFault,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GestioneErroreSoapFault.class, gestioneErroreSoapFault, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>gestioneErroreSoapFault</var> of type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param gestioneErroreSoapFault Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GestioneErroreSoapFault gestioneErroreSoapFault) throws SerializerException {
		return this.objToXml(GestioneErroreSoapFault.class, gestioneErroreSoapFault, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>gestioneErroreSoapFault</var> of type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param gestioneErroreSoapFault Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GestioneErroreSoapFault gestioneErroreSoapFault,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GestioneErroreSoapFault.class, gestioneErroreSoapFault, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>gestioneErroreSoapFault</var> of type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param gestioneErroreSoapFault Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GestioneErroreSoapFault gestioneErroreSoapFault) throws SerializerException {
		return this.objToXml(GestioneErroreSoapFault.class, gestioneErroreSoapFault, false).toString();
	}
	/**
	 * Serialize to String the object <var>gestioneErroreSoapFault</var> of type {@link org.openspcoop2.core.config.GestioneErroreSoapFault}
	 * 
	 * @param gestioneErroreSoapFault Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GestioneErroreSoapFault gestioneErroreSoapFault,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GestioneErroreSoapFault.class, gestioneErroreSoapFault, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Property
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>property</var> of type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param fileName Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Property property) throws SerializerException {
		this.objToXml(fileName, Property.class, property, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>property</var> of type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param fileName Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Property.class, property, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>property</var> of type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param file Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Property property) throws SerializerException {
		this.objToXml(file, Property.class, property, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>property</var> of type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param file Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Property.class, property, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>property</var> of type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param out OutputStream to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Property property) throws SerializerException {
		this.objToXml(out, Property.class, property, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>property</var> of type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param out OutputStream to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Property.class, property, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>property</var> of type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param property Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Property property) throws SerializerException {
		return this.objToXml(Property.class, property, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>property</var> of type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param property Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Property property,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Property.class, property, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>property</var> of type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param property Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Property property) throws SerializerException {
		return this.objToXml(Property.class, property, false).toString();
	}
	/**
	 * Serialize to String the object <var>property</var> of type {@link org.openspcoop2.core.config.Property}
	 * 
	 * @param property Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Property property,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Property.class, property, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop-sorgente-dati
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoopSorgenteDati</var> of type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoopSorgenteDati</var>
	 * @param openspcoopSorgenteDati Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OpenspcoopSorgenteDati openspcoopSorgenteDati) throws SerializerException {
		this.objToXml(fileName, OpenspcoopSorgenteDati.class, openspcoopSorgenteDati, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoopSorgenteDati</var> of type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoopSorgenteDati</var>
	 * @param openspcoopSorgenteDati Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OpenspcoopSorgenteDati openspcoopSorgenteDati,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, OpenspcoopSorgenteDati.class, openspcoopSorgenteDati, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoopSorgenteDati</var> of type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoopSorgenteDati</var>
	 * @param openspcoopSorgenteDati Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OpenspcoopSorgenteDati openspcoopSorgenteDati) throws SerializerException {
		this.objToXml(file, OpenspcoopSorgenteDati.class, openspcoopSorgenteDati, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoopSorgenteDati</var> of type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoopSorgenteDati</var>
	 * @param openspcoopSorgenteDati Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OpenspcoopSorgenteDati openspcoopSorgenteDati,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, OpenspcoopSorgenteDati.class, openspcoopSorgenteDati, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoopSorgenteDati</var> of type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoopSorgenteDati</var>
	 * @param openspcoopSorgenteDati Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OpenspcoopSorgenteDati openspcoopSorgenteDati) throws SerializerException {
		this.objToXml(out, OpenspcoopSorgenteDati.class, openspcoopSorgenteDati, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoopSorgenteDati</var> of type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoopSorgenteDati</var>
	 * @param openspcoopSorgenteDati Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OpenspcoopSorgenteDati openspcoopSorgenteDati,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, OpenspcoopSorgenteDati.class, openspcoopSorgenteDati, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>openspcoopSorgenteDati</var> of type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param openspcoopSorgenteDati Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OpenspcoopSorgenteDati openspcoopSorgenteDati) throws SerializerException {
		return this.objToXml(OpenspcoopSorgenteDati.class, openspcoopSorgenteDati, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>openspcoopSorgenteDati</var> of type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param openspcoopSorgenteDati Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OpenspcoopSorgenteDati openspcoopSorgenteDati,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OpenspcoopSorgenteDati.class, openspcoopSorgenteDati, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>openspcoopSorgenteDati</var> of type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param openspcoopSorgenteDati Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OpenspcoopSorgenteDati openspcoopSorgenteDati) throws SerializerException {
		return this.objToXml(OpenspcoopSorgenteDati.class, openspcoopSorgenteDati, false).toString();
	}
	/**
	 * Serialize to String the object <var>openspcoopSorgenteDati</var> of type {@link org.openspcoop2.core.config.OpenspcoopSorgenteDati}
	 * 
	 * @param openspcoopSorgenteDati Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OpenspcoopSorgenteDati openspcoopSorgenteDati,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OpenspcoopSorgenteDati.class, openspcoopSorgenteDati, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-soggetto-virtuale
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaSoggettoVirtuale</var> of type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaSoggettoVirtuale</var>
	 * @param portaApplicativaSoggettoVirtuale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaSoggettoVirtuale portaApplicativaSoggettoVirtuale) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaSoggettoVirtuale.class, portaApplicativaSoggettoVirtuale, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaSoggettoVirtuale</var> of type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaSoggettoVirtuale</var>
	 * @param portaApplicativaSoggettoVirtuale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaSoggettoVirtuale portaApplicativaSoggettoVirtuale,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaSoggettoVirtuale.class, portaApplicativaSoggettoVirtuale, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaSoggettoVirtuale</var> of type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaSoggettoVirtuale</var>
	 * @param portaApplicativaSoggettoVirtuale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaSoggettoVirtuale portaApplicativaSoggettoVirtuale) throws SerializerException {
		this.objToXml(file, PortaApplicativaSoggettoVirtuale.class, portaApplicativaSoggettoVirtuale, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaSoggettoVirtuale</var> of type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaSoggettoVirtuale</var>
	 * @param portaApplicativaSoggettoVirtuale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaSoggettoVirtuale portaApplicativaSoggettoVirtuale,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaSoggettoVirtuale.class, portaApplicativaSoggettoVirtuale, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaSoggettoVirtuale</var> of type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaSoggettoVirtuale</var>
	 * @param portaApplicativaSoggettoVirtuale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaSoggettoVirtuale portaApplicativaSoggettoVirtuale) throws SerializerException {
		this.objToXml(out, PortaApplicativaSoggettoVirtuale.class, portaApplicativaSoggettoVirtuale, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaSoggettoVirtuale</var> of type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaSoggettoVirtuale</var>
	 * @param portaApplicativaSoggettoVirtuale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaSoggettoVirtuale portaApplicativaSoggettoVirtuale,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaSoggettoVirtuale.class, portaApplicativaSoggettoVirtuale, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaSoggettoVirtuale</var> of type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param portaApplicativaSoggettoVirtuale Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaSoggettoVirtuale portaApplicativaSoggettoVirtuale) throws SerializerException {
		return this.objToXml(PortaApplicativaSoggettoVirtuale.class, portaApplicativaSoggettoVirtuale, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaSoggettoVirtuale</var> of type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param portaApplicativaSoggettoVirtuale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaSoggettoVirtuale portaApplicativaSoggettoVirtuale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaSoggettoVirtuale.class, portaApplicativaSoggettoVirtuale, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaSoggettoVirtuale</var> of type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param portaApplicativaSoggettoVirtuale Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaSoggettoVirtuale portaApplicativaSoggettoVirtuale) throws SerializerException {
		return this.objToXml(PortaApplicativaSoggettoVirtuale.class, portaApplicativaSoggettoVirtuale, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaSoggettoVirtuale</var> of type {@link org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale}
	 * 
	 * @param portaApplicativaSoggettoVirtuale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaSoggettoVirtuale portaApplicativaSoggettoVirtuale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaSoggettoVirtuale.class, portaApplicativaSoggettoVirtuale, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativa portaApplicativa) throws SerializerException {
		this.objToXml(fileName, PortaApplicativa.class, portaApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativa portaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativa.class, portaApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativa portaApplicativa) throws SerializerException {
		this.objToXml(file, PortaApplicativa.class, portaApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativa portaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativa.class, portaApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativa portaApplicativa) throws SerializerException {
		this.objToXml(out, PortaApplicativa.class, portaApplicativa, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativa</var>
	 * @param portaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativa portaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativa.class, portaApplicativa, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param portaApplicativa Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativa portaApplicativa) throws SerializerException {
		return this.objToXml(PortaApplicativa.class, portaApplicativa, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param portaApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativa portaApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativa.class, portaApplicativa, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param portaApplicativa Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativa portaApplicativa) throws SerializerException {
		return this.objToXml(PortaApplicativa.class, portaApplicativa, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativa</var> of type {@link org.openspcoop2.core.config.PortaApplicativa}
	 * 
	 * @param portaApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativa portaApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativa.class, portaApplicativa, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaServizio</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaServizio</var>
	 * @param portaApplicativaServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaServizio portaApplicativaServizio) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaServizio.class, portaApplicativaServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaServizio</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaServizio</var>
	 * @param portaApplicativaServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaServizio portaApplicativaServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaServizio.class, portaApplicativaServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaServizio</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaServizio</var>
	 * @param portaApplicativaServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaServizio portaApplicativaServizio) throws SerializerException {
		this.objToXml(file, PortaApplicativaServizio.class, portaApplicativaServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaServizio</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaServizio</var>
	 * @param portaApplicativaServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaServizio portaApplicativaServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaServizio.class, portaApplicativaServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaServizio</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaServizio</var>
	 * @param portaApplicativaServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaServizio portaApplicativaServizio) throws SerializerException {
		this.objToXml(out, PortaApplicativaServizio.class, portaApplicativaServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaServizio</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaServizio</var>
	 * @param portaApplicativaServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaServizio portaApplicativaServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaServizio.class, portaApplicativaServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaServizio</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param portaApplicativaServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaServizio portaApplicativaServizio) throws SerializerException {
		return this.objToXml(PortaApplicativaServizio.class, portaApplicativaServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaServizio</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param portaApplicativaServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaServizio portaApplicativaServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaServizio.class, portaApplicativaServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaServizio</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param portaApplicativaServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaServizio portaApplicativaServizio) throws SerializerException {
		return this.objToXml(PortaApplicativaServizio.class, portaApplicativaServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaServizio</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizio}
	 * 
	 * @param portaApplicativaServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaServizio portaApplicativaServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaServizio.class, portaApplicativaServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAzione portaApplicativaAzione) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAzione.class, portaApplicativaAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAzione portaApplicativaAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAzione.class, portaApplicativaAzione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAzione portaApplicativaAzione) throws SerializerException {
		this.objToXml(file, PortaApplicativaAzione.class, portaApplicativaAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAzione portaApplicativaAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaAzione.class, portaApplicativaAzione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAzione portaApplicativaAzione) throws SerializerException {
		this.objToXml(out, PortaApplicativaAzione.class, portaApplicativaAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAzione</var>
	 * @param portaApplicativaAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAzione portaApplicativaAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaAzione.class, portaApplicativaAzione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param portaApplicativaAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAzione portaApplicativaAzione) throws SerializerException {
		return this.objToXml(PortaApplicativaAzione.class, portaApplicativaAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param portaApplicativaAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAzione portaApplicativaAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAzione.class, portaApplicativaAzione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param portaApplicativaAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAzione portaApplicativaAzione) throws SerializerException {
		return this.objToXml(PortaApplicativaAzione.class, portaApplicativaAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaAzione</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAzione}
	 * 
	 * @param portaApplicativaAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAzione portaApplicativaAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAzione.class, portaApplicativaAzione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativo servizioApplicativo) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativo.class, servizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativo.class, servizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativo servizioApplicativo) throws SerializerException {
		this.objToXml(file, ServizioApplicativo.class, servizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServizioApplicativo.class, servizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativo servizioApplicativo) throws SerializerException {
		this.objToXml(out, ServizioApplicativo.class, servizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServizioApplicativo.class, servizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativo servizioApplicativo) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativo servizioApplicativo) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.core.config.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: proprieta-protocollo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprietaProtocollo</var> of type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprietaProtocollo</var>
	 * @param proprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ProprietaProtocollo proprietaProtocollo) throws SerializerException {
		this.objToXml(fileName, ProprietaProtocollo.class, proprietaProtocollo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprietaProtocollo</var> of type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprietaProtocollo</var>
	 * @param proprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ProprietaProtocollo proprietaProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ProprietaProtocollo.class, proprietaProtocollo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprietaProtocollo</var> of type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param file Xml file to serialize the object <var>proprietaProtocollo</var>
	 * @param proprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ProprietaProtocollo proprietaProtocollo) throws SerializerException {
		this.objToXml(file, ProprietaProtocollo.class, proprietaProtocollo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprietaProtocollo</var> of type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param file Xml file to serialize the object <var>proprietaProtocollo</var>
	 * @param proprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ProprietaProtocollo proprietaProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ProprietaProtocollo.class, proprietaProtocollo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprietaProtocollo</var> of type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>proprietaProtocollo</var>
	 * @param proprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ProprietaProtocollo proprietaProtocollo) throws SerializerException {
		this.objToXml(out, ProprietaProtocollo.class, proprietaProtocollo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprietaProtocollo</var> of type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>proprietaProtocollo</var>
	 * @param proprietaProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ProprietaProtocollo proprietaProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ProprietaProtocollo.class, proprietaProtocollo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>proprietaProtocollo</var> of type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param proprietaProtocollo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ProprietaProtocollo proprietaProtocollo) throws SerializerException {
		return this.objToXml(ProprietaProtocollo.class, proprietaProtocollo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>proprietaProtocollo</var> of type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param proprietaProtocollo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ProprietaProtocollo proprietaProtocollo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ProprietaProtocollo.class, proprietaProtocollo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>proprietaProtocollo</var> of type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param proprietaProtocollo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ProprietaProtocollo proprietaProtocollo) throws SerializerException {
		return this.objToXml(ProprietaProtocollo.class, proprietaProtocollo, false).toString();
	}
	/**
	 * Serialize to String the object <var>proprietaProtocollo</var> of type {@link org.openspcoop2.core.config.ProprietaProtocollo}
	 * 
	 * @param proprietaProtocollo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ProprietaProtocollo proprietaProtocollo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ProprietaProtocollo.class, proprietaProtocollo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: mtom-processor
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mtomProcessor</var> of type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param fileName Xml file to serialize the object <var>mtomProcessor</var>
	 * @param mtomProcessor Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MtomProcessor mtomProcessor) throws SerializerException {
		this.objToXml(fileName, MtomProcessor.class, mtomProcessor, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mtomProcessor</var> of type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param fileName Xml file to serialize the object <var>mtomProcessor</var>
	 * @param mtomProcessor Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MtomProcessor mtomProcessor,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MtomProcessor.class, mtomProcessor, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>mtomProcessor</var> of type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param file Xml file to serialize the object <var>mtomProcessor</var>
	 * @param mtomProcessor Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MtomProcessor mtomProcessor) throws SerializerException {
		this.objToXml(file, MtomProcessor.class, mtomProcessor, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>mtomProcessor</var> of type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param file Xml file to serialize the object <var>mtomProcessor</var>
	 * @param mtomProcessor Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MtomProcessor mtomProcessor,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MtomProcessor.class, mtomProcessor, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>mtomProcessor</var> of type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param out OutputStream to serialize the object <var>mtomProcessor</var>
	 * @param mtomProcessor Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MtomProcessor mtomProcessor) throws SerializerException {
		this.objToXml(out, MtomProcessor.class, mtomProcessor, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>mtomProcessor</var> of type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param out OutputStream to serialize the object <var>mtomProcessor</var>
	 * @param mtomProcessor Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MtomProcessor mtomProcessor,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MtomProcessor.class, mtomProcessor, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>mtomProcessor</var> of type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param mtomProcessor Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MtomProcessor mtomProcessor) throws SerializerException {
		return this.objToXml(MtomProcessor.class, mtomProcessor, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>mtomProcessor</var> of type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param mtomProcessor Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MtomProcessor mtomProcessor,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MtomProcessor.class, mtomProcessor, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>mtomProcessor</var> of type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param mtomProcessor Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MtomProcessor mtomProcessor) throws SerializerException {
		return this.objToXml(MtomProcessor.class, mtomProcessor, false).toString();
	}
	/**
	 * Serialize to String the object <var>mtomProcessor</var> of type {@link org.openspcoop2.core.config.MtomProcessor}
	 * 
	 * @param mtomProcessor Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MtomProcessor mtomProcessor,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MtomProcessor.class, mtomProcessor, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: message-security
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageSecurity</var> of type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageSecurity</var>
	 * @param messageSecurity Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageSecurity messageSecurity) throws SerializerException {
		this.objToXml(fileName, MessageSecurity.class, messageSecurity, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageSecurity</var> of type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageSecurity</var>
	 * @param messageSecurity Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageSecurity messageSecurity,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessageSecurity.class, messageSecurity, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageSecurity</var> of type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param file Xml file to serialize the object <var>messageSecurity</var>
	 * @param messageSecurity Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageSecurity messageSecurity) throws SerializerException {
		this.objToXml(file, MessageSecurity.class, messageSecurity, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageSecurity</var> of type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param file Xml file to serialize the object <var>messageSecurity</var>
	 * @param messageSecurity Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageSecurity messageSecurity,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessageSecurity.class, messageSecurity, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageSecurity</var> of type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param out OutputStream to serialize the object <var>messageSecurity</var>
	 * @param messageSecurity Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageSecurity messageSecurity) throws SerializerException {
		this.objToXml(out, MessageSecurity.class, messageSecurity, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageSecurity</var> of type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param out OutputStream to serialize the object <var>messageSecurity</var>
	 * @param messageSecurity Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageSecurity messageSecurity,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessageSecurity.class, messageSecurity, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messageSecurity</var> of type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param messageSecurity Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageSecurity messageSecurity) throws SerializerException {
		return this.objToXml(MessageSecurity.class, messageSecurity, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messageSecurity</var> of type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param messageSecurity Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageSecurity messageSecurity,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageSecurity.class, messageSecurity, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messageSecurity</var> of type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param messageSecurity Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageSecurity messageSecurity) throws SerializerException {
		return this.objToXml(MessageSecurity.class, messageSecurity, false).toString();
	}
	/**
	 * Serialize to String the object <var>messageSecurity</var> of type {@link org.openspcoop2.core.config.MessageSecurity}
	 * 
	 * @param messageSecurity Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageSecurity messageSecurity,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageSecurity.class, messageSecurity, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: validazione-contenuti-applicativi
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>validazioneContenutiApplicativi</var> of type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param fileName Xml file to serialize the object <var>validazioneContenutiApplicativi</var>
	 * @param validazioneContenutiApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ValidazioneContenutiApplicativi validazioneContenutiApplicativi) throws SerializerException {
		this.objToXml(fileName, ValidazioneContenutiApplicativi.class, validazioneContenutiApplicativi, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>validazioneContenutiApplicativi</var> of type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param fileName Xml file to serialize the object <var>validazioneContenutiApplicativi</var>
	 * @param validazioneContenutiApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ValidazioneContenutiApplicativi validazioneContenutiApplicativi,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ValidazioneContenutiApplicativi.class, validazioneContenutiApplicativi, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>validazioneContenutiApplicativi</var> of type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param file Xml file to serialize the object <var>validazioneContenutiApplicativi</var>
	 * @param validazioneContenutiApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ValidazioneContenutiApplicativi validazioneContenutiApplicativi) throws SerializerException {
		this.objToXml(file, ValidazioneContenutiApplicativi.class, validazioneContenutiApplicativi, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>validazioneContenutiApplicativi</var> of type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param file Xml file to serialize the object <var>validazioneContenutiApplicativi</var>
	 * @param validazioneContenutiApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ValidazioneContenutiApplicativi validazioneContenutiApplicativi,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ValidazioneContenutiApplicativi.class, validazioneContenutiApplicativi, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>validazioneContenutiApplicativi</var> of type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param out OutputStream to serialize the object <var>validazioneContenutiApplicativi</var>
	 * @param validazioneContenutiApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ValidazioneContenutiApplicativi validazioneContenutiApplicativi) throws SerializerException {
		this.objToXml(out, ValidazioneContenutiApplicativi.class, validazioneContenutiApplicativi, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>validazioneContenutiApplicativi</var> of type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param out OutputStream to serialize the object <var>validazioneContenutiApplicativi</var>
	 * @param validazioneContenutiApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ValidazioneContenutiApplicativi validazioneContenutiApplicativi,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ValidazioneContenutiApplicativi.class, validazioneContenutiApplicativi, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>validazioneContenutiApplicativi</var> of type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param validazioneContenutiApplicativi Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ValidazioneContenutiApplicativi validazioneContenutiApplicativi) throws SerializerException {
		return this.objToXml(ValidazioneContenutiApplicativi.class, validazioneContenutiApplicativi, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>validazioneContenutiApplicativi</var> of type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param validazioneContenutiApplicativi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ValidazioneContenutiApplicativi validazioneContenutiApplicativi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ValidazioneContenutiApplicativi.class, validazioneContenutiApplicativi, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>validazioneContenutiApplicativi</var> of type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param validazioneContenutiApplicativi Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ValidazioneContenutiApplicativi validazioneContenutiApplicativi) throws SerializerException {
		return this.objToXml(ValidazioneContenutiApplicativi.class, validazioneContenutiApplicativi, false).toString();
	}
	/**
	 * Serialize to String the object <var>validazioneContenutiApplicativi</var> of type {@link org.openspcoop2.core.config.ValidazioneContenutiApplicativi}
	 * 
	 * @param validazioneContenutiApplicativi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ValidazioneContenutiApplicativi validazioneContenutiApplicativi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ValidazioneContenutiApplicativi.class, validazioneContenutiApplicativi, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: correlazione-applicativa
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>correlazioneApplicativa</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>correlazioneApplicativa</var>
	 * @param correlazioneApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorrelazioneApplicativa correlazioneApplicativa) throws SerializerException {
		this.objToXml(fileName, CorrelazioneApplicativa.class, correlazioneApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>correlazioneApplicativa</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>correlazioneApplicativa</var>
	 * @param correlazioneApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorrelazioneApplicativa correlazioneApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CorrelazioneApplicativa.class, correlazioneApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>correlazioneApplicativa</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>correlazioneApplicativa</var>
	 * @param correlazioneApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorrelazioneApplicativa correlazioneApplicativa) throws SerializerException {
		this.objToXml(file, CorrelazioneApplicativa.class, correlazioneApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>correlazioneApplicativa</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>correlazioneApplicativa</var>
	 * @param correlazioneApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorrelazioneApplicativa correlazioneApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CorrelazioneApplicativa.class, correlazioneApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>correlazioneApplicativa</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>correlazioneApplicativa</var>
	 * @param correlazioneApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorrelazioneApplicativa correlazioneApplicativa) throws SerializerException {
		this.objToXml(out, CorrelazioneApplicativa.class, correlazioneApplicativa, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>correlazioneApplicativa</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>correlazioneApplicativa</var>
	 * @param correlazioneApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorrelazioneApplicativa correlazioneApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CorrelazioneApplicativa.class, correlazioneApplicativa, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>correlazioneApplicativa</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param correlazioneApplicativa Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorrelazioneApplicativa correlazioneApplicativa) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativa.class, correlazioneApplicativa, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>correlazioneApplicativa</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param correlazioneApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorrelazioneApplicativa correlazioneApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativa.class, correlazioneApplicativa, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>correlazioneApplicativa</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param correlazioneApplicativa Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorrelazioneApplicativa correlazioneApplicativa) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativa.class, correlazioneApplicativa, false).toString();
	}
	/**
	 * Serialize to String the object <var>correlazioneApplicativa</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativa}
	 * 
	 * @param correlazioneApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorrelazioneApplicativa correlazioneApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativa.class, correlazioneApplicativa, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop-appender
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoopAppender</var> of type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoopAppender</var>
	 * @param openspcoopAppender Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OpenspcoopAppender openspcoopAppender) throws SerializerException {
		this.objToXml(fileName, OpenspcoopAppender.class, openspcoopAppender, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoopAppender</var> of type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoopAppender</var>
	 * @param openspcoopAppender Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OpenspcoopAppender openspcoopAppender,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, OpenspcoopAppender.class, openspcoopAppender, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoopAppender</var> of type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoopAppender</var>
	 * @param openspcoopAppender Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OpenspcoopAppender openspcoopAppender) throws SerializerException {
		this.objToXml(file, OpenspcoopAppender.class, openspcoopAppender, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoopAppender</var> of type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoopAppender</var>
	 * @param openspcoopAppender Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OpenspcoopAppender openspcoopAppender,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, OpenspcoopAppender.class, openspcoopAppender, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoopAppender</var> of type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoopAppender</var>
	 * @param openspcoopAppender Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OpenspcoopAppender openspcoopAppender) throws SerializerException {
		this.objToXml(out, OpenspcoopAppender.class, openspcoopAppender, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoopAppender</var> of type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoopAppender</var>
	 * @param openspcoopAppender Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OpenspcoopAppender openspcoopAppender,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, OpenspcoopAppender.class, openspcoopAppender, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>openspcoopAppender</var> of type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param openspcoopAppender Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OpenspcoopAppender openspcoopAppender) throws SerializerException {
		return this.objToXml(OpenspcoopAppender.class, openspcoopAppender, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>openspcoopAppender</var> of type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param openspcoopAppender Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OpenspcoopAppender openspcoopAppender,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OpenspcoopAppender.class, openspcoopAppender, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>openspcoopAppender</var> of type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param openspcoopAppender Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OpenspcoopAppender openspcoopAppender) throws SerializerException {
		return this.objToXml(OpenspcoopAppender.class, openspcoopAppender, false).toString();
	}
	/**
	 * Serialize to String the object <var>openspcoopAppender</var> of type {@link org.openspcoop2.core.config.OpenspcoopAppender}
	 * 
	 * @param openspcoopAppender Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OpenspcoopAppender openspcoopAppender,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OpenspcoopAppender.class, openspcoopAppender, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: messaggi-diagnostici
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messaggiDiagnostici</var> of type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param fileName Xml file to serialize the object <var>messaggiDiagnostici</var>
	 * @param messaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessaggiDiagnostici messaggiDiagnostici) throws SerializerException {
		this.objToXml(fileName, MessaggiDiagnostici.class, messaggiDiagnostici, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messaggiDiagnostici</var> of type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param fileName Xml file to serialize the object <var>messaggiDiagnostici</var>
	 * @param messaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessaggiDiagnostici messaggiDiagnostici,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessaggiDiagnostici.class, messaggiDiagnostici, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messaggiDiagnostici</var> of type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param file Xml file to serialize the object <var>messaggiDiagnostici</var>
	 * @param messaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessaggiDiagnostici messaggiDiagnostici) throws SerializerException {
		this.objToXml(file, MessaggiDiagnostici.class, messaggiDiagnostici, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messaggiDiagnostici</var> of type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param file Xml file to serialize the object <var>messaggiDiagnostici</var>
	 * @param messaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessaggiDiagnostici messaggiDiagnostici,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessaggiDiagnostici.class, messaggiDiagnostici, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messaggiDiagnostici</var> of type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param out OutputStream to serialize the object <var>messaggiDiagnostici</var>
	 * @param messaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessaggiDiagnostici messaggiDiagnostici) throws SerializerException {
		this.objToXml(out, MessaggiDiagnostici.class, messaggiDiagnostici, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messaggiDiagnostici</var> of type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param out OutputStream to serialize the object <var>messaggiDiagnostici</var>
	 * @param messaggiDiagnostici Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessaggiDiagnostici messaggiDiagnostici,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessaggiDiagnostici.class, messaggiDiagnostici, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messaggiDiagnostici</var> of type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param messaggiDiagnostici Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessaggiDiagnostici messaggiDiagnostici) throws SerializerException {
		return this.objToXml(MessaggiDiagnostici.class, messaggiDiagnostici, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messaggiDiagnostici</var> of type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param messaggiDiagnostici Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessaggiDiagnostici messaggiDiagnostici,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessaggiDiagnostici.class, messaggiDiagnostici, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messaggiDiagnostici</var> of type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param messaggiDiagnostici Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessaggiDiagnostici messaggiDiagnostici) throws SerializerException {
		return this.objToXml(MessaggiDiagnostici.class, messaggiDiagnostici, false).toString();
	}
	/**
	 * Serialize to String the object <var>messaggiDiagnostici</var> of type {@link org.openspcoop2.core.config.MessaggiDiagnostici}
	 * 
	 * @param messaggiDiagnostici Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessaggiDiagnostici messaggiDiagnostici,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessaggiDiagnostici.class, messaggiDiagnostici, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accesso-dati-autorizzazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoDatiAutorizzazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoDatiAutorizzazione</var>
	 * @param accessoDatiAutorizzazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws SerializerException {
		this.objToXml(fileName, AccessoDatiAutorizzazione.class, accessoDatiAutorizzazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoDatiAutorizzazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoDatiAutorizzazione</var>
	 * @param accessoDatiAutorizzazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoDatiAutorizzazione accessoDatiAutorizzazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccessoDatiAutorizzazione.class, accessoDatiAutorizzazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoDatiAutorizzazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param file Xml file to serialize the object <var>accessoDatiAutorizzazione</var>
	 * @param accessoDatiAutorizzazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws SerializerException {
		this.objToXml(file, AccessoDatiAutorizzazione.class, accessoDatiAutorizzazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoDatiAutorizzazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param file Xml file to serialize the object <var>accessoDatiAutorizzazione</var>
	 * @param accessoDatiAutorizzazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoDatiAutorizzazione accessoDatiAutorizzazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccessoDatiAutorizzazione.class, accessoDatiAutorizzazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoDatiAutorizzazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoDatiAutorizzazione</var>
	 * @param accessoDatiAutorizzazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws SerializerException {
		this.objToXml(out, AccessoDatiAutorizzazione.class, accessoDatiAutorizzazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoDatiAutorizzazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoDatiAutorizzazione</var>
	 * @param accessoDatiAutorizzazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoDatiAutorizzazione accessoDatiAutorizzazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccessoDatiAutorizzazione.class, accessoDatiAutorizzazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accessoDatiAutorizzazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param accessoDatiAutorizzazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws SerializerException {
		return this.objToXml(AccessoDatiAutorizzazione.class, accessoDatiAutorizzazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accessoDatiAutorizzazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param accessoDatiAutorizzazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoDatiAutorizzazione accessoDatiAutorizzazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoDatiAutorizzazione.class, accessoDatiAutorizzazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accessoDatiAutorizzazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param accessoDatiAutorizzazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws SerializerException {
		return this.objToXml(AccessoDatiAutorizzazione.class, accessoDatiAutorizzazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>accessoDatiAutorizzazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutorizzazione}
	 * 
	 * @param accessoDatiAutorizzazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoDatiAutorizzazione accessoDatiAutorizzazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoDatiAutorizzazione.class, accessoDatiAutorizzazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: routing-table-destinazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>routingTableDestinazione</var> of type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>routingTableDestinazione</var>
	 * @param routingTableDestinazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RoutingTableDestinazione routingTableDestinazione) throws SerializerException {
		this.objToXml(fileName, RoutingTableDestinazione.class, routingTableDestinazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>routingTableDestinazione</var> of type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>routingTableDestinazione</var>
	 * @param routingTableDestinazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RoutingTableDestinazione routingTableDestinazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RoutingTableDestinazione.class, routingTableDestinazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>routingTableDestinazione</var> of type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param file Xml file to serialize the object <var>routingTableDestinazione</var>
	 * @param routingTableDestinazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RoutingTableDestinazione routingTableDestinazione) throws SerializerException {
		this.objToXml(file, RoutingTableDestinazione.class, routingTableDestinazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>routingTableDestinazione</var> of type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param file Xml file to serialize the object <var>routingTableDestinazione</var>
	 * @param routingTableDestinazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RoutingTableDestinazione routingTableDestinazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RoutingTableDestinazione.class, routingTableDestinazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>routingTableDestinazione</var> of type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param out OutputStream to serialize the object <var>routingTableDestinazione</var>
	 * @param routingTableDestinazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RoutingTableDestinazione routingTableDestinazione) throws SerializerException {
		this.objToXml(out, RoutingTableDestinazione.class, routingTableDestinazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>routingTableDestinazione</var> of type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param out OutputStream to serialize the object <var>routingTableDestinazione</var>
	 * @param routingTableDestinazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RoutingTableDestinazione routingTableDestinazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RoutingTableDestinazione.class, routingTableDestinazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>routingTableDestinazione</var> of type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param routingTableDestinazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RoutingTableDestinazione routingTableDestinazione) throws SerializerException {
		return this.objToXml(RoutingTableDestinazione.class, routingTableDestinazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>routingTableDestinazione</var> of type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param routingTableDestinazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RoutingTableDestinazione routingTableDestinazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RoutingTableDestinazione.class, routingTableDestinazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>routingTableDestinazione</var> of type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param routingTableDestinazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RoutingTableDestinazione routingTableDestinazione) throws SerializerException {
		return this.objToXml(RoutingTableDestinazione.class, routingTableDestinazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>routingTableDestinazione</var> of type {@link org.openspcoop2.core.config.RoutingTableDestinazione}
	 * 
	 * @param routingTableDestinazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RoutingTableDestinazione routingTableDestinazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RoutingTableDestinazione.class, routingTableDestinazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: routing-table
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>routingTable</var> of type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param fileName Xml file to serialize the object <var>routingTable</var>
	 * @param routingTable Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RoutingTable routingTable) throws SerializerException {
		this.objToXml(fileName, RoutingTable.class, routingTable, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>routingTable</var> of type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param fileName Xml file to serialize the object <var>routingTable</var>
	 * @param routingTable Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RoutingTable routingTable,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RoutingTable.class, routingTable, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>routingTable</var> of type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param file Xml file to serialize the object <var>routingTable</var>
	 * @param routingTable Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RoutingTable routingTable) throws SerializerException {
		this.objToXml(file, RoutingTable.class, routingTable, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>routingTable</var> of type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param file Xml file to serialize the object <var>routingTable</var>
	 * @param routingTable Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RoutingTable routingTable,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RoutingTable.class, routingTable, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>routingTable</var> of type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param out OutputStream to serialize the object <var>routingTable</var>
	 * @param routingTable Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RoutingTable routingTable) throws SerializerException {
		this.objToXml(out, RoutingTable.class, routingTable, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>routingTable</var> of type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param out OutputStream to serialize the object <var>routingTable</var>
	 * @param routingTable Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RoutingTable routingTable,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RoutingTable.class, routingTable, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>routingTable</var> of type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param routingTable Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RoutingTable routingTable) throws SerializerException {
		return this.objToXml(RoutingTable.class, routingTable, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>routingTable</var> of type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param routingTable Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RoutingTable routingTable,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RoutingTable.class, routingTable, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>routingTable</var> of type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param routingTable Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RoutingTable routingTable) throws SerializerException {
		return this.objToXml(RoutingTable.class, routingTable, false).toString();
	}
	/**
	 * Serialize to String the object <var>routingTable</var> of type {@link org.openspcoop2.core.config.RoutingTable}
	 * 
	 * @param routingTable Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RoutingTable routingTable,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RoutingTable.class, routingTable, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: routing-table-default
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>routingTableDefault</var> of type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param fileName Xml file to serialize the object <var>routingTableDefault</var>
	 * @param routingTableDefault Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RoutingTableDefault routingTableDefault) throws SerializerException {
		this.objToXml(fileName, RoutingTableDefault.class, routingTableDefault, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>routingTableDefault</var> of type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param fileName Xml file to serialize the object <var>routingTableDefault</var>
	 * @param routingTableDefault Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RoutingTableDefault routingTableDefault,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RoutingTableDefault.class, routingTableDefault, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>routingTableDefault</var> of type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param file Xml file to serialize the object <var>routingTableDefault</var>
	 * @param routingTableDefault Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RoutingTableDefault routingTableDefault) throws SerializerException {
		this.objToXml(file, RoutingTableDefault.class, routingTableDefault, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>routingTableDefault</var> of type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param file Xml file to serialize the object <var>routingTableDefault</var>
	 * @param routingTableDefault Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RoutingTableDefault routingTableDefault,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RoutingTableDefault.class, routingTableDefault, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>routingTableDefault</var> of type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param out OutputStream to serialize the object <var>routingTableDefault</var>
	 * @param routingTableDefault Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RoutingTableDefault routingTableDefault) throws SerializerException {
		this.objToXml(out, RoutingTableDefault.class, routingTableDefault, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>routingTableDefault</var> of type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param out OutputStream to serialize the object <var>routingTableDefault</var>
	 * @param routingTableDefault Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RoutingTableDefault routingTableDefault,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RoutingTableDefault.class, routingTableDefault, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>routingTableDefault</var> of type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param routingTableDefault Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RoutingTableDefault routingTableDefault) throws SerializerException {
		return this.objToXml(RoutingTableDefault.class, routingTableDefault, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>routingTableDefault</var> of type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param routingTableDefault Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RoutingTableDefault routingTableDefault,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RoutingTableDefault.class, routingTableDefault, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>routingTableDefault</var> of type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param routingTableDefault Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RoutingTableDefault routingTableDefault) throws SerializerException {
		return this.objToXml(RoutingTableDefault.class, routingTableDefault, false).toString();
	}
	/**
	 * Serialize to String the object <var>routingTableDefault</var> of type {@link org.openspcoop2.core.config.RoutingTableDefault}
	 * 
	 * @param routingTableDefault Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RoutingTableDefault routingTableDefault,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RoutingTableDefault.class, routingTableDefault, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: mtom-processor-flow-parameter
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mtomProcessorFlowParameter</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>mtomProcessorFlowParameter</var>
	 * @param mtomProcessorFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MtomProcessorFlowParameter mtomProcessorFlowParameter) throws SerializerException {
		this.objToXml(fileName, MtomProcessorFlowParameter.class, mtomProcessorFlowParameter, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mtomProcessorFlowParameter</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>mtomProcessorFlowParameter</var>
	 * @param mtomProcessorFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MtomProcessorFlowParameter mtomProcessorFlowParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MtomProcessorFlowParameter.class, mtomProcessorFlowParameter, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>mtomProcessorFlowParameter</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param file Xml file to serialize the object <var>mtomProcessorFlowParameter</var>
	 * @param mtomProcessorFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MtomProcessorFlowParameter mtomProcessorFlowParameter) throws SerializerException {
		this.objToXml(file, MtomProcessorFlowParameter.class, mtomProcessorFlowParameter, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>mtomProcessorFlowParameter</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param file Xml file to serialize the object <var>mtomProcessorFlowParameter</var>
	 * @param mtomProcessorFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MtomProcessorFlowParameter mtomProcessorFlowParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MtomProcessorFlowParameter.class, mtomProcessorFlowParameter, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>mtomProcessorFlowParameter</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>mtomProcessorFlowParameter</var>
	 * @param mtomProcessorFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MtomProcessorFlowParameter mtomProcessorFlowParameter) throws SerializerException {
		this.objToXml(out, MtomProcessorFlowParameter.class, mtomProcessorFlowParameter, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>mtomProcessorFlowParameter</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>mtomProcessorFlowParameter</var>
	 * @param mtomProcessorFlowParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MtomProcessorFlowParameter mtomProcessorFlowParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MtomProcessorFlowParameter.class, mtomProcessorFlowParameter, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>mtomProcessorFlowParameter</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param mtomProcessorFlowParameter Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MtomProcessorFlowParameter mtomProcessorFlowParameter) throws SerializerException {
		return this.objToXml(MtomProcessorFlowParameter.class, mtomProcessorFlowParameter, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>mtomProcessorFlowParameter</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param mtomProcessorFlowParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MtomProcessorFlowParameter mtomProcessorFlowParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MtomProcessorFlowParameter.class, mtomProcessorFlowParameter, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>mtomProcessorFlowParameter</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param mtomProcessorFlowParameter Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MtomProcessorFlowParameter mtomProcessorFlowParameter) throws SerializerException {
		return this.objToXml(MtomProcessorFlowParameter.class, mtomProcessorFlowParameter, false).toString();
	}
	/**
	 * Serialize to String the object <var>mtomProcessorFlowParameter</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlowParameter}
	 * 
	 * @param mtomProcessorFlowParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MtomProcessorFlowParameter mtomProcessorFlowParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MtomProcessorFlowParameter.class, mtomProcessorFlowParameter, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: mtom-processor-flow
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mtomProcessorFlow</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param fileName Xml file to serialize the object <var>mtomProcessorFlow</var>
	 * @param mtomProcessorFlow Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MtomProcessorFlow mtomProcessorFlow) throws SerializerException {
		this.objToXml(fileName, MtomProcessorFlow.class, mtomProcessorFlow, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>mtomProcessorFlow</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param fileName Xml file to serialize the object <var>mtomProcessorFlow</var>
	 * @param mtomProcessorFlow Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MtomProcessorFlow mtomProcessorFlow,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MtomProcessorFlow.class, mtomProcessorFlow, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>mtomProcessorFlow</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param file Xml file to serialize the object <var>mtomProcessorFlow</var>
	 * @param mtomProcessorFlow Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MtomProcessorFlow mtomProcessorFlow) throws SerializerException {
		this.objToXml(file, MtomProcessorFlow.class, mtomProcessorFlow, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>mtomProcessorFlow</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param file Xml file to serialize the object <var>mtomProcessorFlow</var>
	 * @param mtomProcessorFlow Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MtomProcessorFlow mtomProcessorFlow,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MtomProcessorFlow.class, mtomProcessorFlow, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>mtomProcessorFlow</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param out OutputStream to serialize the object <var>mtomProcessorFlow</var>
	 * @param mtomProcessorFlow Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MtomProcessorFlow mtomProcessorFlow) throws SerializerException {
		this.objToXml(out, MtomProcessorFlow.class, mtomProcessorFlow, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>mtomProcessorFlow</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param out OutputStream to serialize the object <var>mtomProcessorFlow</var>
	 * @param mtomProcessorFlow Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MtomProcessorFlow mtomProcessorFlow,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MtomProcessorFlow.class, mtomProcessorFlow, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>mtomProcessorFlow</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param mtomProcessorFlow Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MtomProcessorFlow mtomProcessorFlow) throws SerializerException {
		return this.objToXml(MtomProcessorFlow.class, mtomProcessorFlow, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>mtomProcessorFlow</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param mtomProcessorFlow Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MtomProcessorFlow mtomProcessorFlow,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MtomProcessorFlow.class, mtomProcessorFlow, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>mtomProcessorFlow</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param mtomProcessorFlow Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MtomProcessorFlow mtomProcessorFlow) throws SerializerException {
		return this.objToXml(MtomProcessorFlow.class, mtomProcessorFlow, false).toString();
	}
	/**
	 * Serialize to String the object <var>mtomProcessorFlow</var> of type {@link org.openspcoop2.core.config.MtomProcessorFlow}
	 * 
	 * @param mtomProcessorFlow Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MtomProcessorFlow mtomProcessorFlow,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MtomProcessorFlow.class, mtomProcessorFlow, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: gestione-errore-codice-trasporto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gestioneErroreCodiceTrasporto</var> of type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param fileName Xml file to serialize the object <var>gestioneErroreCodiceTrasporto</var>
	 * @param gestioneErroreCodiceTrasporto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GestioneErroreCodiceTrasporto gestioneErroreCodiceTrasporto) throws SerializerException {
		this.objToXml(fileName, GestioneErroreCodiceTrasporto.class, gestioneErroreCodiceTrasporto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gestioneErroreCodiceTrasporto</var> of type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param fileName Xml file to serialize the object <var>gestioneErroreCodiceTrasporto</var>
	 * @param gestioneErroreCodiceTrasporto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GestioneErroreCodiceTrasporto gestioneErroreCodiceTrasporto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GestioneErroreCodiceTrasporto.class, gestioneErroreCodiceTrasporto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>gestioneErroreCodiceTrasporto</var> of type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param file Xml file to serialize the object <var>gestioneErroreCodiceTrasporto</var>
	 * @param gestioneErroreCodiceTrasporto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GestioneErroreCodiceTrasporto gestioneErroreCodiceTrasporto) throws SerializerException {
		this.objToXml(file, GestioneErroreCodiceTrasporto.class, gestioneErroreCodiceTrasporto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>gestioneErroreCodiceTrasporto</var> of type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param file Xml file to serialize the object <var>gestioneErroreCodiceTrasporto</var>
	 * @param gestioneErroreCodiceTrasporto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GestioneErroreCodiceTrasporto gestioneErroreCodiceTrasporto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GestioneErroreCodiceTrasporto.class, gestioneErroreCodiceTrasporto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>gestioneErroreCodiceTrasporto</var> of type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param out OutputStream to serialize the object <var>gestioneErroreCodiceTrasporto</var>
	 * @param gestioneErroreCodiceTrasporto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GestioneErroreCodiceTrasporto gestioneErroreCodiceTrasporto) throws SerializerException {
		this.objToXml(out, GestioneErroreCodiceTrasporto.class, gestioneErroreCodiceTrasporto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>gestioneErroreCodiceTrasporto</var> of type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param out OutputStream to serialize the object <var>gestioneErroreCodiceTrasporto</var>
	 * @param gestioneErroreCodiceTrasporto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GestioneErroreCodiceTrasporto gestioneErroreCodiceTrasporto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GestioneErroreCodiceTrasporto.class, gestioneErroreCodiceTrasporto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>gestioneErroreCodiceTrasporto</var> of type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param gestioneErroreCodiceTrasporto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GestioneErroreCodiceTrasporto gestioneErroreCodiceTrasporto) throws SerializerException {
		return this.objToXml(GestioneErroreCodiceTrasporto.class, gestioneErroreCodiceTrasporto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>gestioneErroreCodiceTrasporto</var> of type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param gestioneErroreCodiceTrasporto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GestioneErroreCodiceTrasporto gestioneErroreCodiceTrasporto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GestioneErroreCodiceTrasporto.class, gestioneErroreCodiceTrasporto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>gestioneErroreCodiceTrasporto</var> of type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param gestioneErroreCodiceTrasporto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GestioneErroreCodiceTrasporto gestioneErroreCodiceTrasporto) throws SerializerException {
		return this.objToXml(GestioneErroreCodiceTrasporto.class, gestioneErroreCodiceTrasporto, false).toString();
	}
	/**
	 * Serialize to String the object <var>gestioneErroreCodiceTrasporto</var> of type {@link org.openspcoop2.core.config.GestioneErroreCodiceTrasporto}
	 * 
	 * @param gestioneErroreCodiceTrasporto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GestioneErroreCodiceTrasporto gestioneErroreCodiceTrasporto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GestioneErroreCodiceTrasporto.class, gestioneErroreCodiceTrasporto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: gestione-errore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gestioneErrore</var> of type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param fileName Xml file to serialize the object <var>gestioneErrore</var>
	 * @param gestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GestioneErrore gestioneErrore) throws SerializerException {
		this.objToXml(fileName, GestioneErrore.class, gestioneErrore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gestioneErrore</var> of type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param fileName Xml file to serialize the object <var>gestioneErrore</var>
	 * @param gestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GestioneErrore gestioneErrore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GestioneErrore.class, gestioneErrore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>gestioneErrore</var> of type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param file Xml file to serialize the object <var>gestioneErrore</var>
	 * @param gestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GestioneErrore gestioneErrore) throws SerializerException {
		this.objToXml(file, GestioneErrore.class, gestioneErrore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>gestioneErrore</var> of type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param file Xml file to serialize the object <var>gestioneErrore</var>
	 * @param gestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GestioneErrore gestioneErrore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GestioneErrore.class, gestioneErrore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>gestioneErrore</var> of type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param out OutputStream to serialize the object <var>gestioneErrore</var>
	 * @param gestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GestioneErrore gestioneErrore) throws SerializerException {
		this.objToXml(out, GestioneErrore.class, gestioneErrore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>gestioneErrore</var> of type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param out OutputStream to serialize the object <var>gestioneErrore</var>
	 * @param gestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GestioneErrore gestioneErrore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GestioneErrore.class, gestioneErrore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>gestioneErrore</var> of type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param gestioneErrore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GestioneErrore gestioneErrore) throws SerializerException {
		return this.objToXml(GestioneErrore.class, gestioneErrore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>gestioneErrore</var> of type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param gestioneErrore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GestioneErrore gestioneErrore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GestioneErrore.class, gestioneErrore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>gestioneErrore</var> of type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param gestioneErrore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GestioneErrore gestioneErrore) throws SerializerException {
		return this.objToXml(GestioneErrore.class, gestioneErrore, false).toString();
	}
	/**
	 * Serialize to String the object <var>gestioneErrore</var> of type {@link org.openspcoop2.core.config.GestioneErrore}
	 * 
	 * @param gestioneErrore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GestioneErrore gestioneErrore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GestioneErrore.class, gestioneErrore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: tracciamento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tracciamento</var> of type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>tracciamento</var>
	 * @param tracciamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Tracciamento tracciamento) throws SerializerException {
		this.objToXml(fileName, Tracciamento.class, tracciamento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tracciamento</var> of type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>tracciamento</var>
	 * @param tracciamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Tracciamento tracciamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Tracciamento.class, tracciamento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>tracciamento</var> of type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param file Xml file to serialize the object <var>tracciamento</var>
	 * @param tracciamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Tracciamento tracciamento) throws SerializerException {
		this.objToXml(file, Tracciamento.class, tracciamento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>tracciamento</var> of type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param file Xml file to serialize the object <var>tracciamento</var>
	 * @param tracciamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Tracciamento tracciamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Tracciamento.class, tracciamento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>tracciamento</var> of type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param out OutputStream to serialize the object <var>tracciamento</var>
	 * @param tracciamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Tracciamento tracciamento) throws SerializerException {
		this.objToXml(out, Tracciamento.class, tracciamento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>tracciamento</var> of type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param out OutputStream to serialize the object <var>tracciamento</var>
	 * @param tracciamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Tracciamento tracciamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Tracciamento.class, tracciamento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>tracciamento</var> of type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param tracciamento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Tracciamento tracciamento) throws SerializerException {
		return this.objToXml(Tracciamento.class, tracciamento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>tracciamento</var> of type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param tracciamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Tracciamento tracciamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Tracciamento.class, tracciamento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>tracciamento</var> of type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param tracciamento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Tracciamento tracciamento) throws SerializerException {
		return this.objToXml(Tracciamento.class, tracciamento, false).toString();
	}
	/**
	 * Serialize to String the object <var>tracciamento</var> of type {@link org.openspcoop2.core.config.Tracciamento}
	 * 
	 * @param tracciamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Tracciamento tracciamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Tracciamento.class, tracciamento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: correlazione-applicativa-elemento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>correlazioneApplicativaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param fileName Xml file to serialize the object <var>correlazioneApplicativaElemento</var>
	 * @param correlazioneApplicativaElemento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorrelazioneApplicativaElemento correlazioneApplicativaElemento) throws SerializerException {
		this.objToXml(fileName, CorrelazioneApplicativaElemento.class, correlazioneApplicativaElemento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>correlazioneApplicativaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param fileName Xml file to serialize the object <var>correlazioneApplicativaElemento</var>
	 * @param correlazioneApplicativaElemento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorrelazioneApplicativaElemento correlazioneApplicativaElemento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CorrelazioneApplicativaElemento.class, correlazioneApplicativaElemento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>correlazioneApplicativaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param file Xml file to serialize the object <var>correlazioneApplicativaElemento</var>
	 * @param correlazioneApplicativaElemento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorrelazioneApplicativaElemento correlazioneApplicativaElemento) throws SerializerException {
		this.objToXml(file, CorrelazioneApplicativaElemento.class, correlazioneApplicativaElemento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>correlazioneApplicativaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param file Xml file to serialize the object <var>correlazioneApplicativaElemento</var>
	 * @param correlazioneApplicativaElemento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorrelazioneApplicativaElemento correlazioneApplicativaElemento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CorrelazioneApplicativaElemento.class, correlazioneApplicativaElemento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>correlazioneApplicativaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param out OutputStream to serialize the object <var>correlazioneApplicativaElemento</var>
	 * @param correlazioneApplicativaElemento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorrelazioneApplicativaElemento correlazioneApplicativaElemento) throws SerializerException {
		this.objToXml(out, CorrelazioneApplicativaElemento.class, correlazioneApplicativaElemento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>correlazioneApplicativaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param out OutputStream to serialize the object <var>correlazioneApplicativaElemento</var>
	 * @param correlazioneApplicativaElemento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorrelazioneApplicativaElemento correlazioneApplicativaElemento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CorrelazioneApplicativaElemento.class, correlazioneApplicativaElemento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>correlazioneApplicativaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param correlazioneApplicativaElemento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorrelazioneApplicativaElemento correlazioneApplicativaElemento) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaElemento.class, correlazioneApplicativaElemento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>correlazioneApplicativaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param correlazioneApplicativaElemento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorrelazioneApplicativaElemento correlazioneApplicativaElemento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaElemento.class, correlazioneApplicativaElemento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>correlazioneApplicativaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param correlazioneApplicativaElemento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorrelazioneApplicativaElemento correlazioneApplicativaElemento) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaElemento.class, correlazioneApplicativaElemento, false).toString();
	}
	/**
	 * Serialize to String the object <var>correlazioneApplicativaElemento</var> of type {@link org.openspcoop2.core.config.CorrelazioneApplicativaElemento}
	 * 
	 * @param correlazioneApplicativaElemento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorrelazioneApplicativaElemento correlazioneApplicativaElemento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorrelazioneApplicativaElemento.class, correlazioneApplicativaElemento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: system-properties
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>systemProperties</var> of type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param fileName Xml file to serialize the object <var>systemProperties</var>
	 * @param systemProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SystemProperties systemProperties) throws SerializerException {
		this.objToXml(fileName, SystemProperties.class, systemProperties, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>systemProperties</var> of type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param fileName Xml file to serialize the object <var>systemProperties</var>
	 * @param systemProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SystemProperties systemProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SystemProperties.class, systemProperties, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>systemProperties</var> of type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param file Xml file to serialize the object <var>systemProperties</var>
	 * @param systemProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SystemProperties systemProperties) throws SerializerException {
		this.objToXml(file, SystemProperties.class, systemProperties, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>systemProperties</var> of type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param file Xml file to serialize the object <var>systemProperties</var>
	 * @param systemProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SystemProperties systemProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SystemProperties.class, systemProperties, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>systemProperties</var> of type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param out OutputStream to serialize the object <var>systemProperties</var>
	 * @param systemProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SystemProperties systemProperties) throws SerializerException {
		this.objToXml(out, SystemProperties.class, systemProperties, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>systemProperties</var> of type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param out OutputStream to serialize the object <var>systemProperties</var>
	 * @param systemProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SystemProperties systemProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SystemProperties.class, systemProperties, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>systemProperties</var> of type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param systemProperties Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SystemProperties systemProperties) throws SerializerException {
		return this.objToXml(SystemProperties.class, systemProperties, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>systemProperties</var> of type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param systemProperties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SystemProperties systemProperties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SystemProperties.class, systemProperties, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>systemProperties</var> of type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param systemProperties Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SystemProperties systemProperties) throws SerializerException {
		return this.objToXml(SystemProperties.class, systemProperties, false).toString();
	}
	/**
	 * Serialize to String the object <var>systemProperties</var> of type {@link org.openspcoop2.core.config.SystemProperties}
	 * 
	 * @param systemProperties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SystemProperties systemProperties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SystemProperties.class, systemProperties, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: tipo-filtro-abilitazione-servizi
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tipoFiltroAbilitazioneServizi</var> of type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param fileName Xml file to serialize the object <var>tipoFiltroAbilitazioneServizi</var>
	 * @param tipoFiltroAbilitazioneServizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TipoFiltroAbilitazioneServizi tipoFiltroAbilitazioneServizi) throws SerializerException {
		this.objToXml(fileName, TipoFiltroAbilitazioneServizi.class, tipoFiltroAbilitazioneServizi, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tipoFiltroAbilitazioneServizi</var> of type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param fileName Xml file to serialize the object <var>tipoFiltroAbilitazioneServizi</var>
	 * @param tipoFiltroAbilitazioneServizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TipoFiltroAbilitazioneServizi tipoFiltroAbilitazioneServizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TipoFiltroAbilitazioneServizi.class, tipoFiltroAbilitazioneServizi, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>tipoFiltroAbilitazioneServizi</var> of type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param file Xml file to serialize the object <var>tipoFiltroAbilitazioneServizi</var>
	 * @param tipoFiltroAbilitazioneServizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TipoFiltroAbilitazioneServizi tipoFiltroAbilitazioneServizi) throws SerializerException {
		this.objToXml(file, TipoFiltroAbilitazioneServizi.class, tipoFiltroAbilitazioneServizi, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>tipoFiltroAbilitazioneServizi</var> of type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param file Xml file to serialize the object <var>tipoFiltroAbilitazioneServizi</var>
	 * @param tipoFiltroAbilitazioneServizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TipoFiltroAbilitazioneServizi tipoFiltroAbilitazioneServizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TipoFiltroAbilitazioneServizi.class, tipoFiltroAbilitazioneServizi, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>tipoFiltroAbilitazioneServizi</var> of type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param out OutputStream to serialize the object <var>tipoFiltroAbilitazioneServizi</var>
	 * @param tipoFiltroAbilitazioneServizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TipoFiltroAbilitazioneServizi tipoFiltroAbilitazioneServizi) throws SerializerException {
		this.objToXml(out, TipoFiltroAbilitazioneServizi.class, tipoFiltroAbilitazioneServizi, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>tipoFiltroAbilitazioneServizi</var> of type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param out OutputStream to serialize the object <var>tipoFiltroAbilitazioneServizi</var>
	 * @param tipoFiltroAbilitazioneServizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TipoFiltroAbilitazioneServizi tipoFiltroAbilitazioneServizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TipoFiltroAbilitazioneServizi.class, tipoFiltroAbilitazioneServizi, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>tipoFiltroAbilitazioneServizi</var> of type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param tipoFiltroAbilitazioneServizi Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TipoFiltroAbilitazioneServizi tipoFiltroAbilitazioneServizi) throws SerializerException {
		return this.objToXml(TipoFiltroAbilitazioneServizi.class, tipoFiltroAbilitazioneServizi, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>tipoFiltroAbilitazioneServizi</var> of type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param tipoFiltroAbilitazioneServizi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TipoFiltroAbilitazioneServizi tipoFiltroAbilitazioneServizi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TipoFiltroAbilitazioneServizi.class, tipoFiltroAbilitazioneServizi, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>tipoFiltroAbilitazioneServizi</var> of type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param tipoFiltroAbilitazioneServizi Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TipoFiltroAbilitazioneServizi tipoFiltroAbilitazioneServizi) throws SerializerException {
		return this.objToXml(TipoFiltroAbilitazioneServizi.class, tipoFiltroAbilitazioneServizi, false).toString();
	}
	/**
	 * Serialize to String the object <var>tipoFiltroAbilitazioneServizi</var> of type {@link org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi}
	 * 
	 * @param tipoFiltroAbilitazioneServizi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TipoFiltroAbilitazioneServizi tipoFiltroAbilitazioneServizi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TipoFiltroAbilitazioneServizi.class, tipoFiltroAbilitazioneServizi, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: stato-servizi-pdd-porta-delegata
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statoServiziPddPortaDelegata</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param fileName Xml file to serialize the object <var>statoServiziPddPortaDelegata</var>
	 * @param statoServiziPddPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatoServiziPddPortaDelegata statoServiziPddPortaDelegata) throws SerializerException {
		this.objToXml(fileName, StatoServiziPddPortaDelegata.class, statoServiziPddPortaDelegata, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statoServiziPddPortaDelegata</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param fileName Xml file to serialize the object <var>statoServiziPddPortaDelegata</var>
	 * @param statoServiziPddPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatoServiziPddPortaDelegata statoServiziPddPortaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatoServiziPddPortaDelegata.class, statoServiziPddPortaDelegata, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statoServiziPddPortaDelegata</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param file Xml file to serialize the object <var>statoServiziPddPortaDelegata</var>
	 * @param statoServiziPddPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatoServiziPddPortaDelegata statoServiziPddPortaDelegata) throws SerializerException {
		this.objToXml(file, StatoServiziPddPortaDelegata.class, statoServiziPddPortaDelegata, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statoServiziPddPortaDelegata</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param file Xml file to serialize the object <var>statoServiziPddPortaDelegata</var>
	 * @param statoServiziPddPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatoServiziPddPortaDelegata statoServiziPddPortaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatoServiziPddPortaDelegata.class, statoServiziPddPortaDelegata, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statoServiziPddPortaDelegata</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param out OutputStream to serialize the object <var>statoServiziPddPortaDelegata</var>
	 * @param statoServiziPddPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatoServiziPddPortaDelegata statoServiziPddPortaDelegata) throws SerializerException {
		this.objToXml(out, StatoServiziPddPortaDelegata.class, statoServiziPddPortaDelegata, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statoServiziPddPortaDelegata</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param out OutputStream to serialize the object <var>statoServiziPddPortaDelegata</var>
	 * @param statoServiziPddPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatoServiziPddPortaDelegata statoServiziPddPortaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatoServiziPddPortaDelegata.class, statoServiziPddPortaDelegata, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statoServiziPddPortaDelegata</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param statoServiziPddPortaDelegata Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatoServiziPddPortaDelegata statoServiziPddPortaDelegata) throws SerializerException {
		return this.objToXml(StatoServiziPddPortaDelegata.class, statoServiziPddPortaDelegata, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statoServiziPddPortaDelegata</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param statoServiziPddPortaDelegata Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatoServiziPddPortaDelegata statoServiziPddPortaDelegata,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatoServiziPddPortaDelegata.class, statoServiziPddPortaDelegata, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statoServiziPddPortaDelegata</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param statoServiziPddPortaDelegata Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatoServiziPddPortaDelegata statoServiziPddPortaDelegata) throws SerializerException {
		return this.objToXml(StatoServiziPddPortaDelegata.class, statoServiziPddPortaDelegata, false).toString();
	}
	/**
	 * Serialize to String the object <var>statoServiziPddPortaDelegata</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaDelegata}
	 * 
	 * @param statoServiziPddPortaDelegata Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatoServiziPddPortaDelegata statoServiziPddPortaDelegata,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatoServiziPddPortaDelegata.class, statoServiziPddPortaDelegata, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: route
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>route</var> of type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param fileName Xml file to serialize the object <var>route</var>
	 * @param route Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Route route) throws SerializerException {
		this.objToXml(fileName, Route.class, route, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>route</var> of type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param fileName Xml file to serialize the object <var>route</var>
	 * @param route Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Route route,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Route.class, route, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>route</var> of type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param file Xml file to serialize the object <var>route</var>
	 * @param route Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Route route) throws SerializerException {
		this.objToXml(file, Route.class, route, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>route</var> of type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param file Xml file to serialize the object <var>route</var>
	 * @param route Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Route route,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Route.class, route, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>route</var> of type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param out OutputStream to serialize the object <var>route</var>
	 * @param route Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Route route) throws SerializerException {
		this.objToXml(out, Route.class, route, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>route</var> of type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param out OutputStream to serialize the object <var>route</var>
	 * @param route Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Route route,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Route.class, route, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>route</var> of type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param route Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Route route) throws SerializerException {
		return this.objToXml(Route.class, route, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>route</var> of type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param route Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Route route,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Route.class, route, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>route</var> of type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param route Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Route route) throws SerializerException {
		return this.objToXml(Route.class, route, false).toString();
	}
	/**
	 * Serialize to String the object <var>route</var> of type {@link org.openspcoop2.core.config.Route}
	 * 
	 * @param route Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Route route,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Route.class, route, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: invocazione-porta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>invocazionePorta</var> of type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param fileName Xml file to serialize the object <var>invocazionePorta</var>
	 * @param invocazionePorta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InvocazionePorta invocazionePorta) throws SerializerException {
		this.objToXml(fileName, InvocazionePorta.class, invocazionePorta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>invocazionePorta</var> of type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param fileName Xml file to serialize the object <var>invocazionePorta</var>
	 * @param invocazionePorta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InvocazionePorta invocazionePorta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InvocazionePorta.class, invocazionePorta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>invocazionePorta</var> of type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param file Xml file to serialize the object <var>invocazionePorta</var>
	 * @param invocazionePorta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InvocazionePorta invocazionePorta) throws SerializerException {
		this.objToXml(file, InvocazionePorta.class, invocazionePorta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>invocazionePorta</var> of type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param file Xml file to serialize the object <var>invocazionePorta</var>
	 * @param invocazionePorta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InvocazionePorta invocazionePorta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InvocazionePorta.class, invocazionePorta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>invocazionePorta</var> of type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param out OutputStream to serialize the object <var>invocazionePorta</var>
	 * @param invocazionePorta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InvocazionePorta invocazionePorta) throws SerializerException {
		this.objToXml(out, InvocazionePorta.class, invocazionePorta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>invocazionePorta</var> of type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param out OutputStream to serialize the object <var>invocazionePorta</var>
	 * @param invocazionePorta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InvocazionePorta invocazionePorta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InvocazionePorta.class, invocazionePorta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>invocazionePorta</var> of type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param invocazionePorta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InvocazionePorta invocazionePorta) throws SerializerException {
		return this.objToXml(InvocazionePorta.class, invocazionePorta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>invocazionePorta</var> of type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param invocazionePorta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InvocazionePorta invocazionePorta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InvocazionePorta.class, invocazionePorta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>invocazionePorta</var> of type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param invocazionePorta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InvocazionePorta invocazionePorta) throws SerializerException {
		return this.objToXml(InvocazionePorta.class, invocazionePorta, false).toString();
	}
	/**
	 * Serialize to String the object <var>invocazionePorta</var> of type {@link org.openspcoop2.core.config.InvocazionePorta}
	 * 
	 * @param invocazionePorta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InvocazionePorta invocazionePorta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InvocazionePorta.class, invocazionePorta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: invocazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>invocazioneServizio</var> of type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>invocazioneServizio</var>
	 * @param invocazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InvocazioneServizio invocazioneServizio) throws SerializerException {
		this.objToXml(fileName, InvocazioneServizio.class, invocazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>invocazioneServizio</var> of type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>invocazioneServizio</var>
	 * @param invocazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InvocazioneServizio invocazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InvocazioneServizio.class, invocazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>invocazioneServizio</var> of type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>invocazioneServizio</var>
	 * @param invocazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InvocazioneServizio invocazioneServizio) throws SerializerException {
		this.objToXml(file, InvocazioneServizio.class, invocazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>invocazioneServizio</var> of type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>invocazioneServizio</var>
	 * @param invocazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InvocazioneServizio invocazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InvocazioneServizio.class, invocazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>invocazioneServizio</var> of type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>invocazioneServizio</var>
	 * @param invocazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InvocazioneServizio invocazioneServizio) throws SerializerException {
		this.objToXml(out, InvocazioneServizio.class, invocazioneServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>invocazioneServizio</var> of type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>invocazioneServizio</var>
	 * @param invocazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InvocazioneServizio invocazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InvocazioneServizio.class, invocazioneServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>invocazioneServizio</var> of type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param invocazioneServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InvocazioneServizio invocazioneServizio) throws SerializerException {
		return this.objToXml(InvocazioneServizio.class, invocazioneServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>invocazioneServizio</var> of type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param invocazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InvocazioneServizio invocazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InvocazioneServizio.class, invocazioneServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>invocazioneServizio</var> of type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param invocazioneServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InvocazioneServizio invocazioneServizio) throws SerializerException {
		return this.objToXml(InvocazioneServizio.class, invocazioneServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>invocazioneServizio</var> of type {@link org.openspcoop2.core.config.InvocazioneServizio}
	 * 
	 * @param invocazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InvocazioneServizio invocazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InvocazioneServizio.class, invocazioneServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: risposta-asincrona
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rispostaAsincrona</var> of type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param fileName Xml file to serialize the object <var>rispostaAsincrona</var>
	 * @param rispostaAsincrona Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RispostaAsincrona rispostaAsincrona) throws SerializerException {
		this.objToXml(fileName, RispostaAsincrona.class, rispostaAsincrona, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rispostaAsincrona</var> of type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param fileName Xml file to serialize the object <var>rispostaAsincrona</var>
	 * @param rispostaAsincrona Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RispostaAsincrona rispostaAsincrona,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RispostaAsincrona.class, rispostaAsincrona, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>rispostaAsincrona</var> of type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param file Xml file to serialize the object <var>rispostaAsincrona</var>
	 * @param rispostaAsincrona Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RispostaAsincrona rispostaAsincrona) throws SerializerException {
		this.objToXml(file, RispostaAsincrona.class, rispostaAsincrona, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>rispostaAsincrona</var> of type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param file Xml file to serialize the object <var>rispostaAsincrona</var>
	 * @param rispostaAsincrona Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RispostaAsincrona rispostaAsincrona,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RispostaAsincrona.class, rispostaAsincrona, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>rispostaAsincrona</var> of type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param out OutputStream to serialize the object <var>rispostaAsincrona</var>
	 * @param rispostaAsincrona Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RispostaAsincrona rispostaAsincrona) throws SerializerException {
		this.objToXml(out, RispostaAsincrona.class, rispostaAsincrona, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>rispostaAsincrona</var> of type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param out OutputStream to serialize the object <var>rispostaAsincrona</var>
	 * @param rispostaAsincrona Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RispostaAsincrona rispostaAsincrona,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RispostaAsincrona.class, rispostaAsincrona, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>rispostaAsincrona</var> of type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param rispostaAsincrona Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RispostaAsincrona rispostaAsincrona) throws SerializerException {
		return this.objToXml(RispostaAsincrona.class, rispostaAsincrona, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>rispostaAsincrona</var> of type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param rispostaAsincrona Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RispostaAsincrona rispostaAsincrona,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RispostaAsincrona.class, rispostaAsincrona, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>rispostaAsincrona</var> of type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param rispostaAsincrona Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RispostaAsincrona rispostaAsincrona) throws SerializerException {
		return this.objToXml(RispostaAsincrona.class, rispostaAsincrona, false).toString();
	}
	/**
	 * Serialize to String the object <var>rispostaAsincrona</var> of type {@link org.openspcoop2.core.config.RispostaAsincrona}
	 * 
	 * @param rispostaAsincrona Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RispostaAsincrona rispostaAsincrona,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RispostaAsincrona.class, rispostaAsincrona, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdSoggetto idSoggetto) throws SerializerException {
		this.objToXml(fileName, IdSoggetto.class, idSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdSoggetto.class, idSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdSoggetto idSoggetto) throws SerializerException {
		this.objToXml(file, IdSoggetto.class, idSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdSoggetto.class, idSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdSoggetto idSoggetto) throws SerializerException {
		this.objToXml(out, IdSoggetto.class, idSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdSoggetto.class, idSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdSoggetto idSoggetto) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdSoggetto idSoggetto) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.config.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdServizioApplicativo idServizioApplicativo) throws SerializerException {
		this.objToXml(fileName, IdServizioApplicativo.class, idServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdServizioApplicativo idServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdServizioApplicativo.class, idServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdServizioApplicativo idServizioApplicativo) throws SerializerException {
		this.objToXml(file, IdServizioApplicativo.class, idServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdServizioApplicativo idServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdServizioApplicativo.class, idServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdServizioApplicativo idServizioApplicativo) throws SerializerException {
		this.objToXml(out, IdServizioApplicativo.class, idServizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>idServizioApplicativo</var>
	 * @param idServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdServizioApplicativo idServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdServizioApplicativo.class, idServizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param idServizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdServizioApplicativo idServizioApplicativo) throws SerializerException {
		return this.objToXml(IdServizioApplicativo.class, idServizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param idServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdServizioApplicativo idServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdServizioApplicativo.class, idServizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param idServizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdServizioApplicativo idServizioApplicativo) throws SerializerException {
		return this.objToXml(IdServizioApplicativo.class, idServizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>idServizioApplicativo</var> of type {@link org.openspcoop2.core.config.IdServizioApplicativo}
	 * 
	 * @param idServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdServizioApplicativo idServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdServizioApplicativo.class, idServizioApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: integration-manager
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationManager</var> of type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationManager</var>
	 * @param integrationManager Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationManager integrationManager) throws SerializerException {
		this.objToXml(fileName, IntegrationManager.class, integrationManager, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationManager</var> of type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationManager</var>
	 * @param integrationManager Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationManager integrationManager,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationManager.class, integrationManager, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationManager</var> of type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param file Xml file to serialize the object <var>integrationManager</var>
	 * @param integrationManager Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationManager integrationManager) throws SerializerException {
		this.objToXml(file, IntegrationManager.class, integrationManager, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationManager</var> of type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param file Xml file to serialize the object <var>integrationManager</var>
	 * @param integrationManager Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationManager integrationManager,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationManager.class, integrationManager, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationManager</var> of type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationManager</var>
	 * @param integrationManager Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationManager integrationManager) throws SerializerException {
		this.objToXml(out, IntegrationManager.class, integrationManager, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationManager</var> of type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationManager</var>
	 * @param integrationManager Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationManager integrationManager,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationManager.class, integrationManager, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationManager</var> of type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param integrationManager Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationManager integrationManager) throws SerializerException {
		return this.objToXml(IntegrationManager.class, integrationManager, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationManager</var> of type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param integrationManager Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationManager integrationManager,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationManager.class, integrationManager, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationManager</var> of type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param integrationManager Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationManager integrationManager) throws SerializerException {
		return this.objToXml(IntegrationManager.class, integrationManager, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationManager</var> of type {@link org.openspcoop2.core.config.IntegrationManager}
	 * 
	 * @param integrationManager Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationManager integrationManager,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationManager.class, integrationManager, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-delegata
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaDelegata idPortaDelegata) throws SerializerException {
		this.objToXml(fileName, IdPortaDelegata.class, idPortaDelegata, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaDelegata idPortaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdPortaDelegata.class, idPortaDelegata, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaDelegata idPortaDelegata) throws SerializerException {
		this.objToXml(file, IdPortaDelegata.class, idPortaDelegata, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaDelegata idPortaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdPortaDelegata.class, idPortaDelegata, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaDelegata idPortaDelegata) throws SerializerException {
		this.objToXml(out, IdPortaDelegata.class, idPortaDelegata, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaDelegata</var>
	 * @param idPortaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaDelegata idPortaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdPortaDelegata.class, idPortaDelegata, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param idPortaDelegata Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaDelegata idPortaDelegata) throws SerializerException {
		return this.objToXml(IdPortaDelegata.class, idPortaDelegata, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param idPortaDelegata Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaDelegata idPortaDelegata,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaDelegata.class, idPortaDelegata, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param idPortaDelegata Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaDelegata idPortaDelegata) throws SerializerException {
		return this.objToXml(IdPortaDelegata.class, idPortaDelegata, false).toString();
	}
	/**
	 * Serialize to String the object <var>idPortaDelegata</var> of type {@link org.openspcoop2.core.config.IdPortaDelegata}
	 * 
	 * @param idPortaDelegata Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaDelegata idPortaDelegata,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaDelegata.class, idPortaDelegata, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: route-gateway
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>routeGateway</var> of type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param fileName Xml file to serialize the object <var>routeGateway</var>
	 * @param routeGateway Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RouteGateway routeGateway) throws SerializerException {
		this.objToXml(fileName, RouteGateway.class, routeGateway, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>routeGateway</var> of type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param fileName Xml file to serialize the object <var>routeGateway</var>
	 * @param routeGateway Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RouteGateway routeGateway,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RouteGateway.class, routeGateway, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>routeGateway</var> of type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param file Xml file to serialize the object <var>routeGateway</var>
	 * @param routeGateway Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RouteGateway routeGateway) throws SerializerException {
		this.objToXml(file, RouteGateway.class, routeGateway, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>routeGateway</var> of type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param file Xml file to serialize the object <var>routeGateway</var>
	 * @param routeGateway Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RouteGateway routeGateway,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RouteGateway.class, routeGateway, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>routeGateway</var> of type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param out OutputStream to serialize the object <var>routeGateway</var>
	 * @param routeGateway Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RouteGateway routeGateway) throws SerializerException {
		this.objToXml(out, RouteGateway.class, routeGateway, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>routeGateway</var> of type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param out OutputStream to serialize the object <var>routeGateway</var>
	 * @param routeGateway Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RouteGateway routeGateway,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RouteGateway.class, routeGateway, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>routeGateway</var> of type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param routeGateway Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RouteGateway routeGateway) throws SerializerException {
		return this.objToXml(RouteGateway.class, routeGateway, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>routeGateway</var> of type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param routeGateway Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RouteGateway routeGateway,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RouteGateway.class, routeGateway, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>routeGateway</var> of type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param routeGateway Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RouteGateway routeGateway) throws SerializerException {
		return this.objToXml(RouteGateway.class, routeGateway, false).toString();
	}
	/**
	 * Serialize to String the object <var>routeGateway</var> of type {@link org.openspcoop2.core.config.RouteGateway}
	 * 
	 * @param routeGateway Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RouteGateway routeGateway,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RouteGateway.class, routeGateway, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: inoltro-buste-non-riscontrate
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>inoltroBusteNonRiscontrate</var> of type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param fileName Xml file to serialize the object <var>inoltroBusteNonRiscontrate</var>
	 * @param inoltroBusteNonRiscontrate Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate) throws SerializerException {
		this.objToXml(fileName, InoltroBusteNonRiscontrate.class, inoltroBusteNonRiscontrate, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>inoltroBusteNonRiscontrate</var> of type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param fileName Xml file to serialize the object <var>inoltroBusteNonRiscontrate</var>
	 * @param inoltroBusteNonRiscontrate Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InoltroBusteNonRiscontrate.class, inoltroBusteNonRiscontrate, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>inoltroBusteNonRiscontrate</var> of type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param file Xml file to serialize the object <var>inoltroBusteNonRiscontrate</var>
	 * @param inoltroBusteNonRiscontrate Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate) throws SerializerException {
		this.objToXml(file, InoltroBusteNonRiscontrate.class, inoltroBusteNonRiscontrate, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>inoltroBusteNonRiscontrate</var> of type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param file Xml file to serialize the object <var>inoltroBusteNonRiscontrate</var>
	 * @param inoltroBusteNonRiscontrate Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InoltroBusteNonRiscontrate.class, inoltroBusteNonRiscontrate, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>inoltroBusteNonRiscontrate</var> of type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param out OutputStream to serialize the object <var>inoltroBusteNonRiscontrate</var>
	 * @param inoltroBusteNonRiscontrate Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate) throws SerializerException {
		this.objToXml(out, InoltroBusteNonRiscontrate.class, inoltroBusteNonRiscontrate, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>inoltroBusteNonRiscontrate</var> of type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param out OutputStream to serialize the object <var>inoltroBusteNonRiscontrate</var>
	 * @param inoltroBusteNonRiscontrate Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InoltroBusteNonRiscontrate.class, inoltroBusteNonRiscontrate, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>inoltroBusteNonRiscontrate</var> of type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param inoltroBusteNonRiscontrate Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate) throws SerializerException {
		return this.objToXml(InoltroBusteNonRiscontrate.class, inoltroBusteNonRiscontrate, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>inoltroBusteNonRiscontrate</var> of type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param inoltroBusteNonRiscontrate Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InoltroBusteNonRiscontrate.class, inoltroBusteNonRiscontrate, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>inoltroBusteNonRiscontrate</var> of type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param inoltroBusteNonRiscontrate Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate) throws SerializerException {
		return this.objToXml(InoltroBusteNonRiscontrate.class, inoltroBusteNonRiscontrate, false).toString();
	}
	/**
	 * Serialize to String the object <var>inoltroBusteNonRiscontrate</var> of type {@link org.openspcoop2.core.config.InoltroBusteNonRiscontrate}
	 * 
	 * @param inoltroBusteNonRiscontrate Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InoltroBusteNonRiscontrate.class, inoltroBusteNonRiscontrate, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataServizio</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataServizio</var>
	 * @param portaDelegataServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataServizio portaDelegataServizio) throws SerializerException {
		this.objToXml(fileName, PortaDelegataServizio.class, portaDelegataServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataServizio</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataServizio</var>
	 * @param portaDelegataServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataServizio portaDelegataServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDelegataServizio.class, portaDelegataServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataServizio</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataServizio</var>
	 * @param portaDelegataServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataServizio portaDelegataServizio) throws SerializerException {
		this.objToXml(file, PortaDelegataServizio.class, portaDelegataServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataServizio</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataServizio</var>
	 * @param portaDelegataServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataServizio portaDelegataServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDelegataServizio.class, portaDelegataServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataServizio</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataServizio</var>
	 * @param portaDelegataServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataServizio portaDelegataServizio) throws SerializerException {
		this.objToXml(out, PortaDelegataServizio.class, portaDelegataServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataServizio</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataServizio</var>
	 * @param portaDelegataServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataServizio portaDelegataServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDelegataServizio.class, portaDelegataServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDelegataServizio</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param portaDelegataServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataServizio portaDelegataServizio) throws SerializerException {
		return this.objToXml(PortaDelegataServizio.class, portaDelegataServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDelegataServizio</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param portaDelegataServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataServizio portaDelegataServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataServizio.class, portaDelegataServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDelegataServizio</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param portaDelegataServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataServizio portaDelegataServizio) throws SerializerException {
		return this.objToXml(PortaDelegataServizio.class, portaDelegataServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDelegataServizio</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizio}
	 * 
	 * @param portaDelegataServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataServizio portaDelegataServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataServizio.class, portaDelegataServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: credenziali
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>credenziali</var> of type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param fileName Xml file to serialize the object <var>credenziali</var>
	 * @param credenziali Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Credenziali credenziali) throws SerializerException {
		this.objToXml(fileName, Credenziali.class, credenziali, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>credenziali</var> of type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param fileName Xml file to serialize the object <var>credenziali</var>
	 * @param credenziali Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Credenziali credenziali,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Credenziali.class, credenziali, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>credenziali</var> of type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param file Xml file to serialize the object <var>credenziali</var>
	 * @param credenziali Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Credenziali credenziali) throws SerializerException {
		this.objToXml(file, Credenziali.class, credenziali, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>credenziali</var> of type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param file Xml file to serialize the object <var>credenziali</var>
	 * @param credenziali Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Credenziali credenziali,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Credenziali.class, credenziali, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>credenziali</var> of type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param out OutputStream to serialize the object <var>credenziali</var>
	 * @param credenziali Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Credenziali credenziali) throws SerializerException {
		this.objToXml(out, Credenziali.class, credenziali, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>credenziali</var> of type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param out OutputStream to serialize the object <var>credenziali</var>
	 * @param credenziali Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Credenziali credenziali,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Credenziali.class, credenziali, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>credenziali</var> of type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param credenziali Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Credenziali credenziali) throws SerializerException {
		return this.objToXml(Credenziali.class, credenziali, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>credenziali</var> of type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param credenziali Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Credenziali credenziali,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Credenziali.class, credenziali, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>credenziali</var> of type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param credenziali Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Credenziali credenziali) throws SerializerException {
		return this.objToXml(Credenziali.class, credenziali, false).toString();
	}
	/**
	 * Serialize to String the object <var>credenziali</var> of type {@link org.openspcoop2.core.config.Credenziali}
	 * 
	 * @param credenziali Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Credenziali credenziali,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Credenziali.class, credenziali, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: connettore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param fileName Xml file to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Connettore connettore) throws SerializerException {
		this.objToXml(fileName, Connettore.class, connettore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param fileName Xml file to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Connettore connettore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Connettore.class, connettore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param file Xml file to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Connettore connettore) throws SerializerException {
		this.objToXml(file, Connettore.class, connettore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param file Xml file to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Connettore connettore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Connettore.class, connettore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param out OutputStream to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Connettore connettore) throws SerializerException {
		this.objToXml(out, Connettore.class, connettore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param out OutputStream to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Connettore connettore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Connettore.class, connettore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>connettore</var> of type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param connettore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Connettore connettore) throws SerializerException {
		return this.objToXml(Connettore.class, connettore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>connettore</var> of type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param connettore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Connettore connettore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Connettore.class, connettore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>connettore</var> of type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param connettore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Connettore connettore) throws SerializerException {
		return this.objToXml(Connettore.class, connettore, false).toString();
	}
	/**
	 * Serialize to String the object <var>connettore</var> of type {@link org.openspcoop2.core.config.Connettore}
	 * 
	 * @param connettore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Connettore connettore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Connettore.class, connettore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accesso-registro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoRegistro</var>
	 * @param accessoRegistro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoRegistro accessoRegistro) throws SerializerException {
		this.objToXml(fileName, AccessoRegistro.class, accessoRegistro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoRegistro</var>
	 * @param accessoRegistro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoRegistro accessoRegistro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccessoRegistro.class, accessoRegistro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param file Xml file to serialize the object <var>accessoRegistro</var>
	 * @param accessoRegistro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoRegistro accessoRegistro) throws SerializerException {
		this.objToXml(file, AccessoRegistro.class, accessoRegistro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param file Xml file to serialize the object <var>accessoRegistro</var>
	 * @param accessoRegistro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoRegistro accessoRegistro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccessoRegistro.class, accessoRegistro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoRegistro</var>
	 * @param accessoRegistro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoRegistro accessoRegistro) throws SerializerException {
		this.objToXml(out, AccessoRegistro.class, accessoRegistro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoRegistro</var>
	 * @param accessoRegistro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoRegistro accessoRegistro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccessoRegistro.class, accessoRegistro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accessoRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param accessoRegistro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoRegistro accessoRegistro) throws SerializerException {
		return this.objToXml(AccessoRegistro.class, accessoRegistro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accessoRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param accessoRegistro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoRegistro accessoRegistro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoRegistro.class, accessoRegistro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accessoRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param accessoRegistro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoRegistro accessoRegistro) throws SerializerException {
		return this.objToXml(AccessoRegistro.class, accessoRegistro, false).toString();
	}
	/**
	 * Serialize to String the object <var>accessoRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistro}
	 * 
	 * @param accessoRegistro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoRegistro accessoRegistro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoRegistro.class, accessoRegistro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accesso-configurazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoConfigurazione</var> of type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoConfigurazione</var>
	 * @param accessoConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoConfigurazione accessoConfigurazione) throws SerializerException {
		this.objToXml(fileName, AccessoConfigurazione.class, accessoConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoConfigurazione</var> of type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoConfigurazione</var>
	 * @param accessoConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoConfigurazione accessoConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccessoConfigurazione.class, accessoConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoConfigurazione</var> of type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>accessoConfigurazione</var>
	 * @param accessoConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoConfigurazione accessoConfigurazione) throws SerializerException {
		this.objToXml(file, AccessoConfigurazione.class, accessoConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoConfigurazione</var> of type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>accessoConfigurazione</var>
	 * @param accessoConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoConfigurazione accessoConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccessoConfigurazione.class, accessoConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoConfigurazione</var> of type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoConfigurazione</var>
	 * @param accessoConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoConfigurazione accessoConfigurazione) throws SerializerException {
		this.objToXml(out, AccessoConfigurazione.class, accessoConfigurazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoConfigurazione</var> of type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoConfigurazione</var>
	 * @param accessoConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoConfigurazione accessoConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccessoConfigurazione.class, accessoConfigurazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accessoConfigurazione</var> of type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param accessoConfigurazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoConfigurazione accessoConfigurazione) throws SerializerException {
		return this.objToXml(AccessoConfigurazione.class, accessoConfigurazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accessoConfigurazione</var> of type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param accessoConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoConfigurazione accessoConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoConfigurazione.class, accessoConfigurazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accessoConfigurazione</var> of type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param accessoConfigurazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoConfigurazione accessoConfigurazione) throws SerializerException {
		return this.objToXml(AccessoConfigurazione.class, accessoConfigurazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>accessoConfigurazione</var> of type {@link org.openspcoop2.core.config.AccessoConfigurazione}
	 * 
	 * @param accessoConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoConfigurazione accessoConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoConfigurazione.class, accessoConfigurazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: validazione-buste
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>validazioneBuste</var> of type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param fileName Xml file to serialize the object <var>validazioneBuste</var>
	 * @param validazioneBuste Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ValidazioneBuste validazioneBuste) throws SerializerException {
		this.objToXml(fileName, ValidazioneBuste.class, validazioneBuste, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>validazioneBuste</var> of type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param fileName Xml file to serialize the object <var>validazioneBuste</var>
	 * @param validazioneBuste Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ValidazioneBuste validazioneBuste,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ValidazioneBuste.class, validazioneBuste, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>validazioneBuste</var> of type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param file Xml file to serialize the object <var>validazioneBuste</var>
	 * @param validazioneBuste Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ValidazioneBuste validazioneBuste) throws SerializerException {
		this.objToXml(file, ValidazioneBuste.class, validazioneBuste, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>validazioneBuste</var> of type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param file Xml file to serialize the object <var>validazioneBuste</var>
	 * @param validazioneBuste Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ValidazioneBuste validazioneBuste,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ValidazioneBuste.class, validazioneBuste, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>validazioneBuste</var> of type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param out OutputStream to serialize the object <var>validazioneBuste</var>
	 * @param validazioneBuste Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ValidazioneBuste validazioneBuste) throws SerializerException {
		this.objToXml(out, ValidazioneBuste.class, validazioneBuste, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>validazioneBuste</var> of type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param out OutputStream to serialize the object <var>validazioneBuste</var>
	 * @param validazioneBuste Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ValidazioneBuste validazioneBuste,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ValidazioneBuste.class, validazioneBuste, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>validazioneBuste</var> of type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param validazioneBuste Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ValidazioneBuste validazioneBuste) throws SerializerException {
		return this.objToXml(ValidazioneBuste.class, validazioneBuste, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>validazioneBuste</var> of type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param validazioneBuste Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ValidazioneBuste validazioneBuste,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ValidazioneBuste.class, validazioneBuste, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>validazioneBuste</var> of type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param validazioneBuste Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ValidazioneBuste validazioneBuste) throws SerializerException {
		return this.objToXml(ValidazioneBuste.class, validazioneBuste, false).toString();
	}
	/**
	 * Serialize to String the object <var>validazioneBuste</var> of type {@link org.openspcoop2.core.config.ValidazioneBuste}
	 * 
	 * @param validazioneBuste Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ValidazioneBuste validazioneBuste,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ValidazioneBuste.class, validazioneBuste, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: indirizzo-risposta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>indirizzoRisposta</var> of type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param fileName Xml file to serialize the object <var>indirizzoRisposta</var>
	 * @param indirizzoRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IndirizzoRisposta indirizzoRisposta) throws SerializerException {
		this.objToXml(fileName, IndirizzoRisposta.class, indirizzoRisposta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>indirizzoRisposta</var> of type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param fileName Xml file to serialize the object <var>indirizzoRisposta</var>
	 * @param indirizzoRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IndirizzoRisposta indirizzoRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IndirizzoRisposta.class, indirizzoRisposta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>indirizzoRisposta</var> of type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param file Xml file to serialize the object <var>indirizzoRisposta</var>
	 * @param indirizzoRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IndirizzoRisposta indirizzoRisposta) throws SerializerException {
		this.objToXml(file, IndirizzoRisposta.class, indirizzoRisposta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>indirizzoRisposta</var> of type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param file Xml file to serialize the object <var>indirizzoRisposta</var>
	 * @param indirizzoRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IndirizzoRisposta indirizzoRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IndirizzoRisposta.class, indirizzoRisposta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>indirizzoRisposta</var> of type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param out OutputStream to serialize the object <var>indirizzoRisposta</var>
	 * @param indirizzoRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IndirizzoRisposta indirizzoRisposta) throws SerializerException {
		this.objToXml(out, IndirizzoRisposta.class, indirizzoRisposta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>indirizzoRisposta</var> of type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param out OutputStream to serialize the object <var>indirizzoRisposta</var>
	 * @param indirizzoRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IndirizzoRisposta indirizzoRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IndirizzoRisposta.class, indirizzoRisposta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>indirizzoRisposta</var> of type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param indirizzoRisposta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IndirizzoRisposta indirizzoRisposta) throws SerializerException {
		return this.objToXml(IndirizzoRisposta.class, indirizzoRisposta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>indirizzoRisposta</var> of type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param indirizzoRisposta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IndirizzoRisposta indirizzoRisposta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IndirizzoRisposta.class, indirizzoRisposta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>indirizzoRisposta</var> of type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param indirizzoRisposta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IndirizzoRisposta indirizzoRisposta) throws SerializerException {
		return this.objToXml(IndirizzoRisposta.class, indirizzoRisposta, false).toString();
	}
	/**
	 * Serialize to String the object <var>indirizzoRisposta</var> of type {@link org.openspcoop2.core.config.IndirizzoRisposta}
	 * 
	 * @param indirizzoRisposta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IndirizzoRisposta indirizzoRisposta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IndirizzoRisposta.class, indirizzoRisposta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: attachments
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attachments</var> of type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param fileName Xml file to serialize the object <var>attachments</var>
	 * @param attachments Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Attachments attachments) throws SerializerException {
		this.objToXml(fileName, Attachments.class, attachments, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>attachments</var> of type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param fileName Xml file to serialize the object <var>attachments</var>
	 * @param attachments Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Attachments attachments,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Attachments.class, attachments, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>attachments</var> of type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param file Xml file to serialize the object <var>attachments</var>
	 * @param attachments Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Attachments attachments) throws SerializerException {
		this.objToXml(file, Attachments.class, attachments, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>attachments</var> of type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param file Xml file to serialize the object <var>attachments</var>
	 * @param attachments Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Attachments attachments,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Attachments.class, attachments, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>attachments</var> of type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param out OutputStream to serialize the object <var>attachments</var>
	 * @param attachments Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Attachments attachments) throws SerializerException {
		this.objToXml(out, Attachments.class, attachments, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>attachments</var> of type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param out OutputStream to serialize the object <var>attachments</var>
	 * @param attachments Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Attachments attachments,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Attachments.class, attachments, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>attachments</var> of type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param attachments Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Attachments attachments) throws SerializerException {
		return this.objToXml(Attachments.class, attachments, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>attachments</var> of type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param attachments Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Attachments attachments,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Attachments.class, attachments, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>attachments</var> of type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param attachments Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Attachments attachments) throws SerializerException {
		return this.objToXml(Attachments.class, attachments, false).toString();
	}
	/**
	 * Serialize to String the object <var>attachments</var> of type {@link org.openspcoop2.core.config.Attachments}
	 * 
	 * @param attachments Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Attachments attachments,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Attachments.class, attachments, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: risposte
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>risposte</var> of type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param fileName Xml file to serialize the object <var>risposte</var>
	 * @param risposte Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Risposte risposte) throws SerializerException {
		this.objToXml(fileName, Risposte.class, risposte, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>risposte</var> of type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param fileName Xml file to serialize the object <var>risposte</var>
	 * @param risposte Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Risposte risposte,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Risposte.class, risposte, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>risposte</var> of type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param file Xml file to serialize the object <var>risposte</var>
	 * @param risposte Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Risposte risposte) throws SerializerException {
		this.objToXml(file, Risposte.class, risposte, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>risposte</var> of type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param file Xml file to serialize the object <var>risposte</var>
	 * @param risposte Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Risposte risposte,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Risposte.class, risposte, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>risposte</var> of type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param out OutputStream to serialize the object <var>risposte</var>
	 * @param risposte Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Risposte risposte) throws SerializerException {
		this.objToXml(out, Risposte.class, risposte, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>risposte</var> of type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param out OutputStream to serialize the object <var>risposte</var>
	 * @param risposte Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Risposte risposte,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Risposte.class, risposte, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>risposte</var> of type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param risposte Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Risposte risposte) throws SerializerException {
		return this.objToXml(Risposte.class, risposte, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>risposte</var> of type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param risposte Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Risposte risposte,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Risposte.class, risposte, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>risposte</var> of type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param risposte Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Risposte risposte) throws SerializerException {
		return this.objToXml(Risposte.class, risposte, false).toString();
	}
	/**
	 * Serialize to String the object <var>risposte</var> of type {@link org.openspcoop2.core.config.Risposte}
	 * 
	 * @param risposte Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Risposte risposte,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Risposte.class, risposte, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-gestione-errore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneGestioneErrore</var> of type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneGestioneErrore</var>
	 * @param configurazioneGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneGestioneErrore configurazioneGestioneErrore) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneGestioneErrore.class, configurazioneGestioneErrore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneGestioneErrore</var> of type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneGestioneErrore</var>
	 * @param configurazioneGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneGestioneErrore configurazioneGestioneErrore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneGestioneErrore.class, configurazioneGestioneErrore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneGestioneErrore</var> of type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneGestioneErrore</var>
	 * @param configurazioneGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneGestioneErrore configurazioneGestioneErrore) throws SerializerException {
		this.objToXml(file, ConfigurazioneGestioneErrore.class, configurazioneGestioneErrore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneGestioneErrore</var> of type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneGestioneErrore</var>
	 * @param configurazioneGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneGestioneErrore configurazioneGestioneErrore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneGestioneErrore.class, configurazioneGestioneErrore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneGestioneErrore</var> of type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneGestioneErrore</var>
	 * @param configurazioneGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneGestioneErrore configurazioneGestioneErrore) throws SerializerException {
		this.objToXml(out, ConfigurazioneGestioneErrore.class, configurazioneGestioneErrore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneGestioneErrore</var> of type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneGestioneErrore</var>
	 * @param configurazioneGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneGestioneErrore configurazioneGestioneErrore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneGestioneErrore.class, configurazioneGestioneErrore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneGestioneErrore</var> of type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param configurazioneGestioneErrore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneGestioneErrore configurazioneGestioneErrore) throws SerializerException {
		return this.objToXml(ConfigurazioneGestioneErrore.class, configurazioneGestioneErrore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneGestioneErrore</var> of type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param configurazioneGestioneErrore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneGestioneErrore configurazioneGestioneErrore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneGestioneErrore.class, configurazioneGestioneErrore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneGestioneErrore</var> of type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param configurazioneGestioneErrore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneGestioneErrore configurazioneGestioneErrore) throws SerializerException {
		return this.objToXml(ConfigurazioneGestioneErrore.class, configurazioneGestioneErrore, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneGestioneErrore</var> of type {@link org.openspcoop2.core.config.ConfigurazioneGestioneErrore}
	 * 
	 * @param configurazioneGestioneErrore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneGestioneErrore configurazioneGestioneErrore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneGestioneErrore.class, configurazioneGestioneErrore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: stato-servizi-pdd
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statoServiziPdd</var> of type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param fileName Xml file to serialize the object <var>statoServiziPdd</var>
	 * @param statoServiziPdd Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatoServiziPdd statoServiziPdd) throws SerializerException {
		this.objToXml(fileName, StatoServiziPdd.class, statoServiziPdd, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statoServiziPdd</var> of type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param fileName Xml file to serialize the object <var>statoServiziPdd</var>
	 * @param statoServiziPdd Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatoServiziPdd statoServiziPdd,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatoServiziPdd.class, statoServiziPdd, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statoServiziPdd</var> of type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param file Xml file to serialize the object <var>statoServiziPdd</var>
	 * @param statoServiziPdd Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatoServiziPdd statoServiziPdd) throws SerializerException {
		this.objToXml(file, StatoServiziPdd.class, statoServiziPdd, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statoServiziPdd</var> of type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param file Xml file to serialize the object <var>statoServiziPdd</var>
	 * @param statoServiziPdd Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatoServiziPdd statoServiziPdd,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatoServiziPdd.class, statoServiziPdd, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statoServiziPdd</var> of type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param out OutputStream to serialize the object <var>statoServiziPdd</var>
	 * @param statoServiziPdd Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatoServiziPdd statoServiziPdd) throws SerializerException {
		this.objToXml(out, StatoServiziPdd.class, statoServiziPdd, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statoServiziPdd</var> of type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param out OutputStream to serialize the object <var>statoServiziPdd</var>
	 * @param statoServiziPdd Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatoServiziPdd statoServiziPdd,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatoServiziPdd.class, statoServiziPdd, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statoServiziPdd</var> of type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param statoServiziPdd Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatoServiziPdd statoServiziPdd) throws SerializerException {
		return this.objToXml(StatoServiziPdd.class, statoServiziPdd, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statoServiziPdd</var> of type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param statoServiziPdd Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatoServiziPdd statoServiziPdd,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatoServiziPdd.class, statoServiziPdd, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statoServiziPdd</var> of type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param statoServiziPdd Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatoServiziPdd statoServiziPdd) throws SerializerException {
		return this.objToXml(StatoServiziPdd.class, statoServiziPdd, false).toString();
	}
	/**
	 * Serialize to String the object <var>statoServiziPdd</var> of type {@link org.openspcoop2.core.config.StatoServiziPdd}
	 * 
	 * @param statoServiziPdd Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatoServiziPdd statoServiziPdd,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatoServiziPdd.class, statoServiziPdd, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-soggetto-erogatore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataSoggettoErogatore</var> of type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataSoggettoErogatore</var>
	 * @param portaDelegataSoggettoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataSoggettoErogatore portaDelegataSoggettoErogatore) throws SerializerException {
		this.objToXml(fileName, PortaDelegataSoggettoErogatore.class, portaDelegataSoggettoErogatore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataSoggettoErogatore</var> of type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataSoggettoErogatore</var>
	 * @param portaDelegataSoggettoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataSoggettoErogatore portaDelegataSoggettoErogatore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDelegataSoggettoErogatore.class, portaDelegataSoggettoErogatore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataSoggettoErogatore</var> of type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataSoggettoErogatore</var>
	 * @param portaDelegataSoggettoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataSoggettoErogatore portaDelegataSoggettoErogatore) throws SerializerException {
		this.objToXml(file, PortaDelegataSoggettoErogatore.class, portaDelegataSoggettoErogatore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataSoggettoErogatore</var> of type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataSoggettoErogatore</var>
	 * @param portaDelegataSoggettoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataSoggettoErogatore portaDelegataSoggettoErogatore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDelegataSoggettoErogatore.class, portaDelegataSoggettoErogatore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataSoggettoErogatore</var> of type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataSoggettoErogatore</var>
	 * @param portaDelegataSoggettoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataSoggettoErogatore portaDelegataSoggettoErogatore) throws SerializerException {
		this.objToXml(out, PortaDelegataSoggettoErogatore.class, portaDelegataSoggettoErogatore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataSoggettoErogatore</var> of type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataSoggettoErogatore</var>
	 * @param portaDelegataSoggettoErogatore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataSoggettoErogatore portaDelegataSoggettoErogatore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDelegataSoggettoErogatore.class, portaDelegataSoggettoErogatore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDelegataSoggettoErogatore</var> of type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param portaDelegataSoggettoErogatore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataSoggettoErogatore portaDelegataSoggettoErogatore) throws SerializerException {
		return this.objToXml(PortaDelegataSoggettoErogatore.class, portaDelegataSoggettoErogatore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDelegataSoggettoErogatore</var> of type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param portaDelegataSoggettoErogatore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataSoggettoErogatore portaDelegataSoggettoErogatore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataSoggettoErogatore.class, portaDelegataSoggettoErogatore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDelegataSoggettoErogatore</var> of type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param portaDelegataSoggettoErogatore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataSoggettoErogatore portaDelegataSoggettoErogatore) throws SerializerException {
		return this.objToXml(PortaDelegataSoggettoErogatore.class, portaDelegataSoggettoErogatore, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDelegataSoggettoErogatore</var> of type {@link org.openspcoop2.core.config.PortaDelegataSoggettoErogatore}
	 * 
	 * @param portaDelegataSoggettoErogatore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataSoggettoErogatore portaDelegataSoggettoErogatore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataSoggettoErogatore.class, portaDelegataSoggettoErogatore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataAzione portaDelegataAzione) throws SerializerException {
		this.objToXml(fileName, PortaDelegataAzione.class, portaDelegataAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataAzione portaDelegataAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDelegataAzione.class, portaDelegataAzione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataAzione portaDelegataAzione) throws SerializerException {
		this.objToXml(file, PortaDelegataAzione.class, portaDelegataAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataAzione portaDelegataAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDelegataAzione.class, portaDelegataAzione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataAzione portaDelegataAzione) throws SerializerException {
		this.objToXml(out, PortaDelegataAzione.class, portaDelegataAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataAzione</var>
	 * @param portaDelegataAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataAzione portaDelegataAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDelegataAzione.class, portaDelegataAzione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param portaDelegataAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataAzione portaDelegataAzione) throws SerializerException {
		return this.objToXml(PortaDelegataAzione.class, portaDelegataAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param portaDelegataAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataAzione portaDelegataAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataAzione.class, portaDelegataAzione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param portaDelegataAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataAzione portaDelegataAzione) throws SerializerException {
		return this.objToXml(PortaDelegataAzione.class, portaDelegataAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDelegataAzione</var> of type {@link org.openspcoop2.core.config.PortaDelegataAzione}
	 * 
	 * @param portaDelegataAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataAzione portaDelegataAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataAzione.class, portaDelegataAzione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: route-registro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>routeRegistro</var> of type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param fileName Xml file to serialize the object <var>routeRegistro</var>
	 * @param routeRegistro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RouteRegistro routeRegistro) throws SerializerException {
		this.objToXml(fileName, RouteRegistro.class, routeRegistro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>routeRegistro</var> of type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param fileName Xml file to serialize the object <var>routeRegistro</var>
	 * @param routeRegistro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RouteRegistro routeRegistro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RouteRegistro.class, routeRegistro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>routeRegistro</var> of type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param file Xml file to serialize the object <var>routeRegistro</var>
	 * @param routeRegistro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RouteRegistro routeRegistro) throws SerializerException {
		this.objToXml(file, RouteRegistro.class, routeRegistro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>routeRegistro</var> of type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param file Xml file to serialize the object <var>routeRegistro</var>
	 * @param routeRegistro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RouteRegistro routeRegistro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RouteRegistro.class, routeRegistro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>routeRegistro</var> of type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param out OutputStream to serialize the object <var>routeRegistro</var>
	 * @param routeRegistro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RouteRegistro routeRegistro) throws SerializerException {
		this.objToXml(out, RouteRegistro.class, routeRegistro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>routeRegistro</var> of type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param out OutputStream to serialize the object <var>routeRegistro</var>
	 * @param routeRegistro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RouteRegistro routeRegistro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RouteRegistro.class, routeRegistro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>routeRegistro</var> of type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param routeRegistro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RouteRegistro routeRegistro) throws SerializerException {
		return this.objToXml(RouteRegistro.class, routeRegistro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>routeRegistro</var> of type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param routeRegistro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RouteRegistro routeRegistro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RouteRegistro.class, routeRegistro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>routeRegistro</var> of type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param routeRegistro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RouteRegistro routeRegistro) throws SerializerException {
		return this.objToXml(RouteRegistro.class, routeRegistro, false).toString();
	}
	/**
	 * Serialize to String the object <var>routeRegistro</var> of type {@link org.openspcoop2.core.config.RouteRegistro}
	 * 
	 * @param routeRegistro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RouteRegistro routeRegistro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RouteRegistro.class, routeRegistro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accesso-registro-registro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoRegistroRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoRegistroRegistro</var>
	 * @param accessoRegistroRegistro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoRegistroRegistro accessoRegistroRegistro) throws SerializerException {
		this.objToXml(fileName, AccessoRegistroRegistro.class, accessoRegistroRegistro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoRegistroRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoRegistroRegistro</var>
	 * @param accessoRegistroRegistro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoRegistroRegistro accessoRegistroRegistro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccessoRegistroRegistro.class, accessoRegistroRegistro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoRegistroRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param file Xml file to serialize the object <var>accessoRegistroRegistro</var>
	 * @param accessoRegistroRegistro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoRegistroRegistro accessoRegistroRegistro) throws SerializerException {
		this.objToXml(file, AccessoRegistroRegistro.class, accessoRegistroRegistro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoRegistroRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param file Xml file to serialize the object <var>accessoRegistroRegistro</var>
	 * @param accessoRegistroRegistro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoRegistroRegistro accessoRegistroRegistro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccessoRegistroRegistro.class, accessoRegistroRegistro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoRegistroRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoRegistroRegistro</var>
	 * @param accessoRegistroRegistro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoRegistroRegistro accessoRegistroRegistro) throws SerializerException {
		this.objToXml(out, AccessoRegistroRegistro.class, accessoRegistroRegistro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoRegistroRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoRegistroRegistro</var>
	 * @param accessoRegistroRegistro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoRegistroRegistro accessoRegistroRegistro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccessoRegistroRegistro.class, accessoRegistroRegistro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accessoRegistroRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param accessoRegistroRegistro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoRegistroRegistro accessoRegistroRegistro) throws SerializerException {
		return this.objToXml(AccessoRegistroRegistro.class, accessoRegistroRegistro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accessoRegistroRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param accessoRegistroRegistro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoRegistroRegistro accessoRegistroRegistro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoRegistroRegistro.class, accessoRegistroRegistro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accessoRegistroRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param accessoRegistroRegistro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoRegistroRegistro accessoRegistroRegistro) throws SerializerException {
		return this.objToXml(AccessoRegistroRegistro.class, accessoRegistroRegistro, false).toString();
	}
	/**
	 * Serialize to String the object <var>accessoRegistroRegistro</var> of type {@link org.openspcoop2.core.config.AccessoRegistroRegistro}
	 * 
	 * @param accessoRegistroRegistro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoRegistroRegistro accessoRegistroRegistro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoRegistroRegistro.class, accessoRegistroRegistro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: invocazione-porta-gestione-errore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>invocazionePortaGestioneErrore</var> of type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param fileName Xml file to serialize the object <var>invocazionePortaGestioneErrore</var>
	 * @param invocazionePortaGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InvocazionePortaGestioneErrore invocazionePortaGestioneErrore) throws SerializerException {
		this.objToXml(fileName, InvocazionePortaGestioneErrore.class, invocazionePortaGestioneErrore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>invocazionePortaGestioneErrore</var> of type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param fileName Xml file to serialize the object <var>invocazionePortaGestioneErrore</var>
	 * @param invocazionePortaGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InvocazionePortaGestioneErrore invocazionePortaGestioneErrore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InvocazionePortaGestioneErrore.class, invocazionePortaGestioneErrore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>invocazionePortaGestioneErrore</var> of type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param file Xml file to serialize the object <var>invocazionePortaGestioneErrore</var>
	 * @param invocazionePortaGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InvocazionePortaGestioneErrore invocazionePortaGestioneErrore) throws SerializerException {
		this.objToXml(file, InvocazionePortaGestioneErrore.class, invocazionePortaGestioneErrore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>invocazionePortaGestioneErrore</var> of type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param file Xml file to serialize the object <var>invocazionePortaGestioneErrore</var>
	 * @param invocazionePortaGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InvocazionePortaGestioneErrore invocazionePortaGestioneErrore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InvocazionePortaGestioneErrore.class, invocazionePortaGestioneErrore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>invocazionePortaGestioneErrore</var> of type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param out OutputStream to serialize the object <var>invocazionePortaGestioneErrore</var>
	 * @param invocazionePortaGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InvocazionePortaGestioneErrore invocazionePortaGestioneErrore) throws SerializerException {
		this.objToXml(out, InvocazionePortaGestioneErrore.class, invocazionePortaGestioneErrore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>invocazionePortaGestioneErrore</var> of type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param out OutputStream to serialize the object <var>invocazionePortaGestioneErrore</var>
	 * @param invocazionePortaGestioneErrore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InvocazionePortaGestioneErrore invocazionePortaGestioneErrore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InvocazionePortaGestioneErrore.class, invocazionePortaGestioneErrore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>invocazionePortaGestioneErrore</var> of type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param invocazionePortaGestioneErrore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InvocazionePortaGestioneErrore invocazionePortaGestioneErrore) throws SerializerException {
		return this.objToXml(InvocazionePortaGestioneErrore.class, invocazionePortaGestioneErrore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>invocazionePortaGestioneErrore</var> of type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param invocazionePortaGestioneErrore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InvocazionePortaGestioneErrore invocazionePortaGestioneErrore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InvocazionePortaGestioneErrore.class, invocazionePortaGestioneErrore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>invocazionePortaGestioneErrore</var> of type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param invocazionePortaGestioneErrore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InvocazionePortaGestioneErrore invocazionePortaGestioneErrore) throws SerializerException {
		return this.objToXml(InvocazionePortaGestioneErrore.class, invocazionePortaGestioneErrore, false).toString();
	}
	/**
	 * Serialize to String the object <var>invocazionePortaGestioneErrore</var> of type {@link org.openspcoop2.core.config.InvocazionePortaGestioneErrore}
	 * 
	 * @param invocazionePortaGestioneErrore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InvocazionePortaGestioneErrore invocazionePortaGestioneErrore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InvocazionePortaGestioneErrore.class, invocazionePortaGestioneErrore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegata portaDelegata) throws SerializerException {
		this.objToXml(fileName, PortaDelegata.class, portaDelegata, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegata portaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDelegata.class, portaDelegata, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegata portaDelegata) throws SerializerException {
		this.objToXml(file, PortaDelegata.class, portaDelegata, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegata portaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDelegata.class, portaDelegata, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegata portaDelegata) throws SerializerException {
		this.objToXml(out, PortaDelegata.class, portaDelegata, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegata</var>
	 * @param portaDelegata Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegata portaDelegata,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDelegata.class, portaDelegata, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param portaDelegata Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegata portaDelegata) throws SerializerException {
		return this.objToXml(PortaDelegata.class, portaDelegata, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param portaDelegata Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegata portaDelegata,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegata.class, portaDelegata, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param portaDelegata Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegata portaDelegata) throws SerializerException {
		return this.objToXml(PortaDelegata.class, portaDelegata, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDelegata</var> of type {@link org.openspcoop2.core.config.PortaDelegata}
	 * 
	 * @param portaDelegata Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegata portaDelegata,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegata.class, portaDelegata, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: stato-servizi-pdd-porta-applicativa
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statoServiziPddPortaApplicativa</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>statoServiziPddPortaApplicativa</var>
	 * @param statoServiziPddPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatoServiziPddPortaApplicativa statoServiziPddPortaApplicativa) throws SerializerException {
		this.objToXml(fileName, StatoServiziPddPortaApplicativa.class, statoServiziPddPortaApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statoServiziPddPortaApplicativa</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>statoServiziPddPortaApplicativa</var>
	 * @param statoServiziPddPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatoServiziPddPortaApplicativa statoServiziPddPortaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatoServiziPddPortaApplicativa.class, statoServiziPddPortaApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statoServiziPddPortaApplicativa</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>statoServiziPddPortaApplicativa</var>
	 * @param statoServiziPddPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatoServiziPddPortaApplicativa statoServiziPddPortaApplicativa) throws SerializerException {
		this.objToXml(file, StatoServiziPddPortaApplicativa.class, statoServiziPddPortaApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statoServiziPddPortaApplicativa</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>statoServiziPddPortaApplicativa</var>
	 * @param statoServiziPddPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatoServiziPddPortaApplicativa statoServiziPddPortaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatoServiziPddPortaApplicativa.class, statoServiziPddPortaApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statoServiziPddPortaApplicativa</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>statoServiziPddPortaApplicativa</var>
	 * @param statoServiziPddPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatoServiziPddPortaApplicativa statoServiziPddPortaApplicativa) throws SerializerException {
		this.objToXml(out, StatoServiziPddPortaApplicativa.class, statoServiziPddPortaApplicativa, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statoServiziPddPortaApplicativa</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>statoServiziPddPortaApplicativa</var>
	 * @param statoServiziPddPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatoServiziPddPortaApplicativa statoServiziPddPortaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatoServiziPddPortaApplicativa.class, statoServiziPddPortaApplicativa, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statoServiziPddPortaApplicativa</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param statoServiziPddPortaApplicativa Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatoServiziPddPortaApplicativa statoServiziPddPortaApplicativa) throws SerializerException {
		return this.objToXml(StatoServiziPddPortaApplicativa.class, statoServiziPddPortaApplicativa, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statoServiziPddPortaApplicativa</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param statoServiziPddPortaApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatoServiziPddPortaApplicativa statoServiziPddPortaApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatoServiziPddPortaApplicativa.class, statoServiziPddPortaApplicativa, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statoServiziPddPortaApplicativa</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param statoServiziPddPortaApplicativa Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatoServiziPddPortaApplicativa statoServiziPddPortaApplicativa) throws SerializerException {
		return this.objToXml(StatoServiziPddPortaApplicativa.class, statoServiziPddPortaApplicativa, false).toString();
	}
	/**
	 * Serialize to String the object <var>statoServiziPddPortaApplicativa</var> of type {@link org.openspcoop2.core.config.StatoServiziPddPortaApplicativa}
	 * 
	 * @param statoServiziPddPortaApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatoServiziPddPortaApplicativa statoServiziPddPortaApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatoServiziPddPortaApplicativa.class, statoServiziPddPortaApplicativa, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: stato-servizi-pdd-integration-manager
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statoServiziPddIntegrationManager</var> of type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param fileName Xml file to serialize the object <var>statoServiziPddIntegrationManager</var>
	 * @param statoServiziPddIntegrationManager Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatoServiziPddIntegrationManager statoServiziPddIntegrationManager) throws SerializerException {
		this.objToXml(fileName, StatoServiziPddIntegrationManager.class, statoServiziPddIntegrationManager, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statoServiziPddIntegrationManager</var> of type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param fileName Xml file to serialize the object <var>statoServiziPddIntegrationManager</var>
	 * @param statoServiziPddIntegrationManager Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatoServiziPddIntegrationManager statoServiziPddIntegrationManager,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatoServiziPddIntegrationManager.class, statoServiziPddIntegrationManager, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statoServiziPddIntegrationManager</var> of type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param file Xml file to serialize the object <var>statoServiziPddIntegrationManager</var>
	 * @param statoServiziPddIntegrationManager Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatoServiziPddIntegrationManager statoServiziPddIntegrationManager) throws SerializerException {
		this.objToXml(file, StatoServiziPddIntegrationManager.class, statoServiziPddIntegrationManager, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statoServiziPddIntegrationManager</var> of type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param file Xml file to serialize the object <var>statoServiziPddIntegrationManager</var>
	 * @param statoServiziPddIntegrationManager Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatoServiziPddIntegrationManager statoServiziPddIntegrationManager,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatoServiziPddIntegrationManager.class, statoServiziPddIntegrationManager, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statoServiziPddIntegrationManager</var> of type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param out OutputStream to serialize the object <var>statoServiziPddIntegrationManager</var>
	 * @param statoServiziPddIntegrationManager Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatoServiziPddIntegrationManager statoServiziPddIntegrationManager) throws SerializerException {
		this.objToXml(out, StatoServiziPddIntegrationManager.class, statoServiziPddIntegrationManager, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statoServiziPddIntegrationManager</var> of type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param out OutputStream to serialize the object <var>statoServiziPddIntegrationManager</var>
	 * @param statoServiziPddIntegrationManager Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatoServiziPddIntegrationManager statoServiziPddIntegrationManager,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatoServiziPddIntegrationManager.class, statoServiziPddIntegrationManager, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statoServiziPddIntegrationManager</var> of type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param statoServiziPddIntegrationManager Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatoServiziPddIntegrationManager statoServiziPddIntegrationManager) throws SerializerException {
		return this.objToXml(StatoServiziPddIntegrationManager.class, statoServiziPddIntegrationManager, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statoServiziPddIntegrationManager</var> of type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param statoServiziPddIntegrationManager Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatoServiziPddIntegrationManager statoServiziPddIntegrationManager,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatoServiziPddIntegrationManager.class, statoServiziPddIntegrationManager, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statoServiziPddIntegrationManager</var> of type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param statoServiziPddIntegrationManager Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatoServiziPddIntegrationManager statoServiziPddIntegrationManager) throws SerializerException {
		return this.objToXml(StatoServiziPddIntegrationManager.class, statoServiziPddIntegrationManager, false).toString();
	}
	/**
	 * Serialize to String the object <var>statoServiziPddIntegrationManager</var> of type {@link org.openspcoop2.core.config.StatoServiziPddIntegrationManager}
	 * 
	 * @param statoServiziPddIntegrationManager Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatoServiziPddIntegrationManager statoServiziPddIntegrationManager,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatoServiziPddIntegrationManager.class, statoServiziPddIntegrationManager, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-applicativa
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaApplicativa idPortaApplicativa) throws SerializerException {
		this.objToXml(fileName, IdPortaApplicativa.class, idPortaApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaApplicativa idPortaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdPortaApplicativa.class, idPortaApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaApplicativa idPortaApplicativa) throws SerializerException {
		this.objToXml(file, IdPortaApplicativa.class, idPortaApplicativa, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaApplicativa idPortaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdPortaApplicativa.class, idPortaApplicativa, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaApplicativa idPortaApplicativa) throws SerializerException {
		this.objToXml(out, IdPortaApplicativa.class, idPortaApplicativa, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaApplicativa</var>
	 * @param idPortaApplicativa Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaApplicativa idPortaApplicativa,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdPortaApplicativa.class, idPortaApplicativa, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param idPortaApplicativa Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaApplicativa idPortaApplicativa) throws SerializerException {
		return this.objToXml(IdPortaApplicativa.class, idPortaApplicativa, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param idPortaApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaApplicativa idPortaApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaApplicativa.class, idPortaApplicativa, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param idPortaApplicativa Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaApplicativa idPortaApplicativa) throws SerializerException {
		return this.objToXml(IdPortaApplicativa.class, idPortaApplicativa, false).toString();
	}
	/**
	 * Serialize to String the object <var>idPortaApplicativa</var> of type {@link org.openspcoop2.core.config.IdPortaApplicativa}
	 * 
	 * @param idPortaApplicativa Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaApplicativa idPortaApplicativa,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaApplicativa.class, idPortaApplicativa, prettyPrint).toString();
	}
	
	
	

}
