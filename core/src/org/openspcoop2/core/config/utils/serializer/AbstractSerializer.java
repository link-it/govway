/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.TrasformazioneSoapRisposta;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RoutingTableDefault;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.GestioneErroreSoapFault;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.ServizioApplicativoRuoli;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRest;
import org.openspcoop2.core.config.TrasformazioneSoap;
import org.openspcoop2.core.config.IdSoggetto;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.IdPortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore;
import org.openspcoop2.core.config.IdPortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.PortaDelegataLocalForward;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.PortaTracciamento;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.IdServizioApplicativo;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CorsConfigurazioneHeaders;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.CanaleConfigurazioneNodo;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.Openspcoop2;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.CorsConfigurazioneOrigin;
import org.openspcoop2.core.config.CorsConfigurazioneMethods;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.StatoServiziPddPortaApplicativa;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneControl;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.ConfigurazioneGestioneErrore;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaBehaviour;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.ProtocolProperty;
import org.openspcoop2.core.config.StatoServiziPddPortaDelegata;
import org.openspcoop2.core.config.StatoServiziPddIntegrationManager;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.MessageSecurityFlow;

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
	 Object: porta-applicativa-autorizzazione-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAutorizzazioneSoggetto</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAutorizzazioneSoggetto</var>
	 * @param portaApplicativaAutorizzazioneSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAutorizzazioneSoggetto portaApplicativaAutorizzazioneSoggetto) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAutorizzazioneSoggetto.class, portaApplicativaAutorizzazioneSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAutorizzazioneSoggetto</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAutorizzazioneSoggetto</var>
	 * @param portaApplicativaAutorizzazioneSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAutorizzazioneSoggetto portaApplicativaAutorizzazioneSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAutorizzazioneSoggetto.class, portaApplicativaAutorizzazioneSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAutorizzazioneSoggetto</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAutorizzazioneSoggetto</var>
	 * @param portaApplicativaAutorizzazioneSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAutorizzazioneSoggetto portaApplicativaAutorizzazioneSoggetto) throws SerializerException {
		this.objToXml(file, PortaApplicativaAutorizzazioneSoggetto.class, portaApplicativaAutorizzazioneSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAutorizzazioneSoggetto</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAutorizzazioneSoggetto</var>
	 * @param portaApplicativaAutorizzazioneSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAutorizzazioneSoggetto portaApplicativaAutorizzazioneSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaAutorizzazioneSoggetto.class, portaApplicativaAutorizzazioneSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAutorizzazioneSoggetto</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAutorizzazioneSoggetto</var>
	 * @param portaApplicativaAutorizzazioneSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAutorizzazioneSoggetto portaApplicativaAutorizzazioneSoggetto) throws SerializerException {
		this.objToXml(out, PortaApplicativaAutorizzazioneSoggetto.class, portaApplicativaAutorizzazioneSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAutorizzazioneSoggetto</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAutorizzazioneSoggetto</var>
	 * @param portaApplicativaAutorizzazioneSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAutorizzazioneSoggetto portaApplicativaAutorizzazioneSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaAutorizzazioneSoggetto.class, portaApplicativaAutorizzazioneSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaAutorizzazioneSoggetto</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto}
	 * 
	 * @param portaApplicativaAutorizzazioneSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAutorizzazioneSoggetto portaApplicativaAutorizzazioneSoggetto) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneSoggetto.class, portaApplicativaAutorizzazioneSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaAutorizzazioneSoggetto</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto}
	 * 
	 * @param portaApplicativaAutorizzazioneSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAutorizzazioneSoggetto portaApplicativaAutorizzazioneSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneSoggetto.class, portaApplicativaAutorizzazioneSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaAutorizzazioneSoggetto</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto}
	 * 
	 * @param portaApplicativaAutorizzazioneSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAutorizzazioneSoggetto portaApplicativaAutorizzazioneSoggetto) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneSoggetto.class, portaApplicativaAutorizzazioneSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaAutorizzazioneSoggetto</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto}
	 * 
	 * @param portaApplicativaAutorizzazioneSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAutorizzazioneSoggetto portaApplicativaAutorizzazioneSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneSoggetto.class, portaApplicativaAutorizzazioneSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ruolo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.config.Ruolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Ruolo ruolo) throws SerializerException {
		this.objToXml(fileName, Ruolo.class, ruolo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.config.Ruolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Ruolo.class, ruolo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.config.Ruolo}
	 * 
	 * @param file Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Ruolo ruolo) throws SerializerException {
		this.objToXml(file, Ruolo.class, ruolo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.config.Ruolo}
	 * 
	 * @param file Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Ruolo.class, ruolo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.config.Ruolo}
	 * 
	 * @param out OutputStream to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Ruolo ruolo) throws SerializerException {
		this.objToXml(out, Ruolo.class, ruolo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.config.Ruolo}
	 * 
	 * @param out OutputStream to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Ruolo.class, ruolo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>ruolo</var> of type {@link org.openspcoop2.core.config.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Ruolo ruolo) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>ruolo</var> of type {@link org.openspcoop2.core.config.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>ruolo</var> of type {@link org.openspcoop2.core.config.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Ruolo ruolo) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, false).toString();
	}
	/**
	 * Serialize to String the object <var>ruolo</var> of type {@link org.openspcoop2.core.config.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazione-soap-risposta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneSoapRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoapRisposta}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneSoapRisposta</var>
	 * @param trasformazioneSoapRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneSoapRisposta trasformazioneSoapRisposta) throws SerializerException {
		this.objToXml(fileName, TrasformazioneSoapRisposta.class, trasformazioneSoapRisposta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneSoapRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoapRisposta}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneSoapRisposta</var>
	 * @param trasformazioneSoapRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneSoapRisposta trasformazioneSoapRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneSoapRisposta.class, trasformazioneSoapRisposta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneSoapRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoapRisposta}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneSoapRisposta</var>
	 * @param trasformazioneSoapRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneSoapRisposta trasformazioneSoapRisposta) throws SerializerException {
		this.objToXml(file, TrasformazioneSoapRisposta.class, trasformazioneSoapRisposta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneSoapRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoapRisposta}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneSoapRisposta</var>
	 * @param trasformazioneSoapRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneSoapRisposta trasformazioneSoapRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneSoapRisposta.class, trasformazioneSoapRisposta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneSoapRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoapRisposta}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneSoapRisposta</var>
	 * @param trasformazioneSoapRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneSoapRisposta trasformazioneSoapRisposta) throws SerializerException {
		this.objToXml(out, TrasformazioneSoapRisposta.class, trasformazioneSoapRisposta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneSoapRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoapRisposta}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneSoapRisposta</var>
	 * @param trasformazioneSoapRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneSoapRisposta trasformazioneSoapRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneSoapRisposta.class, trasformazioneSoapRisposta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneSoapRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoapRisposta}
	 * 
	 * @param trasformazioneSoapRisposta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneSoapRisposta trasformazioneSoapRisposta) throws SerializerException {
		return this.objToXml(TrasformazioneSoapRisposta.class, trasformazioneSoapRisposta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneSoapRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoapRisposta}
	 * 
	 * @param trasformazioneSoapRisposta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneSoapRisposta trasformazioneSoapRisposta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneSoapRisposta.class, trasformazioneSoapRisposta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneSoapRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoapRisposta}
	 * 
	 * @param trasformazioneSoapRisposta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneSoapRisposta trasformazioneSoapRisposta) throws SerializerException {
		return this.objToXml(TrasformazioneSoapRisposta.class, trasformazioneSoapRisposta, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneSoapRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoapRisposta}
	 * 
	 * @param trasformazioneSoapRisposta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneSoapRisposta trasformazioneSoapRisposta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneSoapRisposta.class, trasformazioneSoapRisposta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-autorizzazione-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var>
	 * @param portaApplicativaAutorizzazioneServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAutorizzazioneServizioApplicativo.class, portaApplicativaAutorizzazioneServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var>
	 * @param portaApplicativaAutorizzazioneServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAutorizzazioneServizioApplicativo.class, portaApplicativaAutorizzazioneServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var>
	 * @param portaApplicativaAutorizzazioneServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo) throws SerializerException {
		this.objToXml(file, PortaApplicativaAutorizzazioneServizioApplicativo.class, portaApplicativaAutorizzazioneServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var>
	 * @param portaApplicativaAutorizzazioneServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaAutorizzazioneServizioApplicativo.class, portaApplicativaAutorizzazioneServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var>
	 * @param portaApplicativaAutorizzazioneServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo) throws SerializerException {
		this.objToXml(out, PortaApplicativaAutorizzazioneServizioApplicativo.class, portaApplicativaAutorizzazioneServizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var>
	 * @param portaApplicativaAutorizzazioneServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaAutorizzazioneServizioApplicativo.class, portaApplicativaAutorizzazioneServizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo}
	 * 
	 * @param portaApplicativaAutorizzazioneServizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneServizioApplicativo.class, portaApplicativaAutorizzazioneServizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo}
	 * 
	 * @param portaApplicativaAutorizzazioneServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneServizioApplicativo.class, portaApplicativaAutorizzazioneServizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo}
	 * 
	 * @param portaApplicativaAutorizzazioneServizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneServizioApplicativo.class, portaApplicativaAutorizzazioneServizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaAutorizzazioneServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo}
	 * 
	 * @param portaApplicativaAutorizzazioneServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAutorizzazioneServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneServizioApplicativo.class, portaApplicativaAutorizzazioneServizioApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-autorizzazione-soggetti
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAutorizzazioneSoggetti</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAutorizzazioneSoggetti</var>
	 * @param portaApplicativaAutorizzazioneSoggetti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAutorizzazioneSoggetti portaApplicativaAutorizzazioneSoggetti) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAutorizzazioneSoggetti.class, portaApplicativaAutorizzazioneSoggetti, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAutorizzazioneSoggetti</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAutorizzazioneSoggetti</var>
	 * @param portaApplicativaAutorizzazioneSoggetti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAutorizzazioneSoggetti portaApplicativaAutorizzazioneSoggetti,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAutorizzazioneSoggetti.class, portaApplicativaAutorizzazioneSoggetti, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAutorizzazioneSoggetti</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAutorizzazioneSoggetti</var>
	 * @param portaApplicativaAutorizzazioneSoggetti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAutorizzazioneSoggetti portaApplicativaAutorizzazioneSoggetti) throws SerializerException {
		this.objToXml(file, PortaApplicativaAutorizzazioneSoggetti.class, portaApplicativaAutorizzazioneSoggetti, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAutorizzazioneSoggetti</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAutorizzazioneSoggetti</var>
	 * @param portaApplicativaAutorizzazioneSoggetti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAutorizzazioneSoggetti portaApplicativaAutorizzazioneSoggetti,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaAutorizzazioneSoggetti.class, portaApplicativaAutorizzazioneSoggetti, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAutorizzazioneSoggetti</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAutorizzazioneSoggetti</var>
	 * @param portaApplicativaAutorizzazioneSoggetti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAutorizzazioneSoggetti portaApplicativaAutorizzazioneSoggetti) throws SerializerException {
		this.objToXml(out, PortaApplicativaAutorizzazioneSoggetti.class, portaApplicativaAutorizzazioneSoggetti, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAutorizzazioneSoggetti</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAutorizzazioneSoggetti</var>
	 * @param portaApplicativaAutorizzazioneSoggetti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAutorizzazioneSoggetti portaApplicativaAutorizzazioneSoggetti,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaAutorizzazioneSoggetti.class, portaApplicativaAutorizzazioneSoggetti, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaAutorizzazioneSoggetti</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti}
	 * 
	 * @param portaApplicativaAutorizzazioneSoggetti Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAutorizzazioneSoggetti portaApplicativaAutorizzazioneSoggetti) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneSoggetti.class, portaApplicativaAutorizzazioneSoggetti, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaAutorizzazioneSoggetti</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti}
	 * 
	 * @param portaApplicativaAutorizzazioneSoggetti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAutorizzazioneSoggetti portaApplicativaAutorizzazioneSoggetti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneSoggetti.class, portaApplicativaAutorizzazioneSoggetti, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaAutorizzazioneSoggetti</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti}
	 * 
	 * @param portaApplicativaAutorizzazioneSoggetti Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAutorizzazioneSoggetti portaApplicativaAutorizzazioneSoggetti) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneSoggetti.class, portaApplicativaAutorizzazioneSoggetti, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaAutorizzazioneSoggetti</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti}
	 * 
	 * @param portaApplicativaAutorizzazioneSoggetti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAutorizzazioneSoggetti portaApplicativaAutorizzazioneSoggetti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneSoggetti.class, portaApplicativaAutorizzazioneSoggetti, prettyPrint).toString();
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
	 Object: accesso-dati-gestione-token
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoDatiGestioneToken</var> of type {@link org.openspcoop2.core.config.AccessoDatiGestioneToken}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoDatiGestioneToken</var>
	 * @param accessoDatiGestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoDatiGestioneToken accessoDatiGestioneToken) throws SerializerException {
		this.objToXml(fileName, AccessoDatiGestioneToken.class, accessoDatiGestioneToken, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoDatiGestioneToken</var> of type {@link org.openspcoop2.core.config.AccessoDatiGestioneToken}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoDatiGestioneToken</var>
	 * @param accessoDatiGestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoDatiGestioneToken accessoDatiGestioneToken,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccessoDatiGestioneToken.class, accessoDatiGestioneToken, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoDatiGestioneToken</var> of type {@link org.openspcoop2.core.config.AccessoDatiGestioneToken}
	 * 
	 * @param file Xml file to serialize the object <var>accessoDatiGestioneToken</var>
	 * @param accessoDatiGestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoDatiGestioneToken accessoDatiGestioneToken) throws SerializerException {
		this.objToXml(file, AccessoDatiGestioneToken.class, accessoDatiGestioneToken, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoDatiGestioneToken</var> of type {@link org.openspcoop2.core.config.AccessoDatiGestioneToken}
	 * 
	 * @param file Xml file to serialize the object <var>accessoDatiGestioneToken</var>
	 * @param accessoDatiGestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoDatiGestioneToken accessoDatiGestioneToken,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccessoDatiGestioneToken.class, accessoDatiGestioneToken, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoDatiGestioneToken</var> of type {@link org.openspcoop2.core.config.AccessoDatiGestioneToken}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoDatiGestioneToken</var>
	 * @param accessoDatiGestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoDatiGestioneToken accessoDatiGestioneToken) throws SerializerException {
		this.objToXml(out, AccessoDatiGestioneToken.class, accessoDatiGestioneToken, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoDatiGestioneToken</var> of type {@link org.openspcoop2.core.config.AccessoDatiGestioneToken}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoDatiGestioneToken</var>
	 * @param accessoDatiGestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoDatiGestioneToken accessoDatiGestioneToken,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccessoDatiGestioneToken.class, accessoDatiGestioneToken, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accessoDatiGestioneToken</var> of type {@link org.openspcoop2.core.config.AccessoDatiGestioneToken}
	 * 
	 * @param accessoDatiGestioneToken Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoDatiGestioneToken accessoDatiGestioneToken) throws SerializerException {
		return this.objToXml(AccessoDatiGestioneToken.class, accessoDatiGestioneToken, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accessoDatiGestioneToken</var> of type {@link org.openspcoop2.core.config.AccessoDatiGestioneToken}
	 * 
	 * @param accessoDatiGestioneToken Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoDatiGestioneToken accessoDatiGestioneToken,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoDatiGestioneToken.class, accessoDatiGestioneToken, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accessoDatiGestioneToken</var> of type {@link org.openspcoop2.core.config.AccessoDatiGestioneToken}
	 * 
	 * @param accessoDatiGestioneToken Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoDatiGestioneToken accessoDatiGestioneToken) throws SerializerException {
		return this.objToXml(AccessoDatiGestioneToken.class, accessoDatiGestioneToken, false).toString();
	}
	/**
	 * Serialize to String the object <var>accessoDatiGestioneToken</var> of type {@link org.openspcoop2.core.config.AccessoDatiGestioneToken}
	 * 
	 * @param accessoDatiGestioneToken Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoDatiGestioneToken accessoDatiGestioneToken,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoDatiGestioneToken.class, accessoDatiGestioneToken, prettyPrint).toString();
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
	 Object: gestione-token-autenticazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gestioneTokenAutenticazione</var> of type {@link org.openspcoop2.core.config.GestioneTokenAutenticazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>gestioneTokenAutenticazione</var>
	 * @param gestioneTokenAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GestioneTokenAutenticazione gestioneTokenAutenticazione) throws SerializerException {
		this.objToXml(fileName, GestioneTokenAutenticazione.class, gestioneTokenAutenticazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gestioneTokenAutenticazione</var> of type {@link org.openspcoop2.core.config.GestioneTokenAutenticazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>gestioneTokenAutenticazione</var>
	 * @param gestioneTokenAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GestioneTokenAutenticazione gestioneTokenAutenticazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GestioneTokenAutenticazione.class, gestioneTokenAutenticazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>gestioneTokenAutenticazione</var> of type {@link org.openspcoop2.core.config.GestioneTokenAutenticazione}
	 * 
	 * @param file Xml file to serialize the object <var>gestioneTokenAutenticazione</var>
	 * @param gestioneTokenAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GestioneTokenAutenticazione gestioneTokenAutenticazione) throws SerializerException {
		this.objToXml(file, GestioneTokenAutenticazione.class, gestioneTokenAutenticazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>gestioneTokenAutenticazione</var> of type {@link org.openspcoop2.core.config.GestioneTokenAutenticazione}
	 * 
	 * @param file Xml file to serialize the object <var>gestioneTokenAutenticazione</var>
	 * @param gestioneTokenAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GestioneTokenAutenticazione gestioneTokenAutenticazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GestioneTokenAutenticazione.class, gestioneTokenAutenticazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>gestioneTokenAutenticazione</var> of type {@link org.openspcoop2.core.config.GestioneTokenAutenticazione}
	 * 
	 * @param out OutputStream to serialize the object <var>gestioneTokenAutenticazione</var>
	 * @param gestioneTokenAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GestioneTokenAutenticazione gestioneTokenAutenticazione) throws SerializerException {
		this.objToXml(out, GestioneTokenAutenticazione.class, gestioneTokenAutenticazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>gestioneTokenAutenticazione</var> of type {@link org.openspcoop2.core.config.GestioneTokenAutenticazione}
	 * 
	 * @param out OutputStream to serialize the object <var>gestioneTokenAutenticazione</var>
	 * @param gestioneTokenAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GestioneTokenAutenticazione gestioneTokenAutenticazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GestioneTokenAutenticazione.class, gestioneTokenAutenticazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>gestioneTokenAutenticazione</var> of type {@link org.openspcoop2.core.config.GestioneTokenAutenticazione}
	 * 
	 * @param gestioneTokenAutenticazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GestioneTokenAutenticazione gestioneTokenAutenticazione) throws SerializerException {
		return this.objToXml(GestioneTokenAutenticazione.class, gestioneTokenAutenticazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>gestioneTokenAutenticazione</var> of type {@link org.openspcoop2.core.config.GestioneTokenAutenticazione}
	 * 
	 * @param gestioneTokenAutenticazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GestioneTokenAutenticazione gestioneTokenAutenticazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GestioneTokenAutenticazione.class, gestioneTokenAutenticazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>gestioneTokenAutenticazione</var> of type {@link org.openspcoop2.core.config.GestioneTokenAutenticazione}
	 * 
	 * @param gestioneTokenAutenticazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GestioneTokenAutenticazione gestioneTokenAutenticazione) throws SerializerException {
		return this.objToXml(GestioneTokenAutenticazione.class, gestioneTokenAutenticazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>gestioneTokenAutenticazione</var> of type {@link org.openspcoop2.core.config.GestioneTokenAutenticazione}
	 * 
	 * @param gestioneTokenAutenticazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GestioneTokenAutenticazione gestioneTokenAutenticazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GestioneTokenAutenticazione.class, gestioneTokenAutenticazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: gestione-token
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gestioneToken</var> of type {@link org.openspcoop2.core.config.GestioneToken}
	 * 
	 * @param fileName Xml file to serialize the object <var>gestioneToken</var>
	 * @param gestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GestioneToken gestioneToken) throws SerializerException {
		this.objToXml(fileName, GestioneToken.class, gestioneToken, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gestioneToken</var> of type {@link org.openspcoop2.core.config.GestioneToken}
	 * 
	 * @param fileName Xml file to serialize the object <var>gestioneToken</var>
	 * @param gestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GestioneToken gestioneToken,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GestioneToken.class, gestioneToken, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>gestioneToken</var> of type {@link org.openspcoop2.core.config.GestioneToken}
	 * 
	 * @param file Xml file to serialize the object <var>gestioneToken</var>
	 * @param gestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GestioneToken gestioneToken) throws SerializerException {
		this.objToXml(file, GestioneToken.class, gestioneToken, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>gestioneToken</var> of type {@link org.openspcoop2.core.config.GestioneToken}
	 * 
	 * @param file Xml file to serialize the object <var>gestioneToken</var>
	 * @param gestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GestioneToken gestioneToken,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GestioneToken.class, gestioneToken, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>gestioneToken</var> of type {@link org.openspcoop2.core.config.GestioneToken}
	 * 
	 * @param out OutputStream to serialize the object <var>gestioneToken</var>
	 * @param gestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GestioneToken gestioneToken) throws SerializerException {
		this.objToXml(out, GestioneToken.class, gestioneToken, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>gestioneToken</var> of type {@link org.openspcoop2.core.config.GestioneToken}
	 * 
	 * @param out OutputStream to serialize the object <var>gestioneToken</var>
	 * @param gestioneToken Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GestioneToken gestioneToken,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GestioneToken.class, gestioneToken, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>gestioneToken</var> of type {@link org.openspcoop2.core.config.GestioneToken}
	 * 
	 * @param gestioneToken Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GestioneToken gestioneToken) throws SerializerException {
		return this.objToXml(GestioneToken.class, gestioneToken, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>gestioneToken</var> of type {@link org.openspcoop2.core.config.GestioneToken}
	 * 
	 * @param gestioneToken Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GestioneToken gestioneToken,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GestioneToken.class, gestioneToken, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>gestioneToken</var> of type {@link org.openspcoop2.core.config.GestioneToken}
	 * 
	 * @param gestioneToken Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GestioneToken gestioneToken) throws SerializerException {
		return this.objToXml(GestioneToken.class, gestioneToken, false).toString();
	}
	/**
	 * Serialize to String the object <var>gestioneToken</var> of type {@link org.openspcoop2.core.config.GestioneToken}
	 * 
	 * @param gestioneToken Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GestioneToken gestioneToken,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GestioneToken.class, gestioneToken, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo-ruoli
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativoRuoli</var> of type {@link org.openspcoop2.core.config.ServizioApplicativoRuoli}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativoRuoli</var>
	 * @param servizioApplicativoRuoli Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativoRuoli servizioApplicativoRuoli) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativoRuoli.class, servizioApplicativoRuoli, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativoRuoli</var> of type {@link org.openspcoop2.core.config.ServizioApplicativoRuoli}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativoRuoli</var>
	 * @param servizioApplicativoRuoli Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativoRuoli servizioApplicativoRuoli,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativoRuoli.class, servizioApplicativoRuoli, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativoRuoli</var> of type {@link org.openspcoop2.core.config.ServizioApplicativoRuoli}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativoRuoli</var>
	 * @param servizioApplicativoRuoli Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativoRuoli servizioApplicativoRuoli) throws SerializerException {
		this.objToXml(file, ServizioApplicativoRuoli.class, servizioApplicativoRuoli, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativoRuoli</var> of type {@link org.openspcoop2.core.config.ServizioApplicativoRuoli}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativoRuoli</var>
	 * @param servizioApplicativoRuoli Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativoRuoli servizioApplicativoRuoli,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServizioApplicativoRuoli.class, servizioApplicativoRuoli, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativoRuoli</var> of type {@link org.openspcoop2.core.config.ServizioApplicativoRuoli}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativoRuoli</var>
	 * @param servizioApplicativoRuoli Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativoRuoli servizioApplicativoRuoli) throws SerializerException {
		this.objToXml(out, ServizioApplicativoRuoli.class, servizioApplicativoRuoli, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativoRuoli</var> of type {@link org.openspcoop2.core.config.ServizioApplicativoRuoli}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativoRuoli</var>
	 * @param servizioApplicativoRuoli Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativoRuoli servizioApplicativoRuoli,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServizioApplicativoRuoli.class, servizioApplicativoRuoli, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizioApplicativoRuoli</var> of type {@link org.openspcoop2.core.config.ServizioApplicativoRuoli}
	 * 
	 * @param servizioApplicativoRuoli Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativoRuoli servizioApplicativoRuoli) throws SerializerException {
		return this.objToXml(ServizioApplicativoRuoli.class, servizioApplicativoRuoli, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizioApplicativoRuoli</var> of type {@link org.openspcoop2.core.config.ServizioApplicativoRuoli}
	 * 
	 * @param servizioApplicativoRuoli Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativoRuoli servizioApplicativoRuoli,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativoRuoli.class, servizioApplicativoRuoli, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizioApplicativoRuoli</var> of type {@link org.openspcoop2.core.config.ServizioApplicativoRuoli}
	 * 
	 * @param servizioApplicativoRuoli Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativoRuoli servizioApplicativoRuoli) throws SerializerException {
		return this.objToXml(ServizioApplicativoRuoli.class, servizioApplicativoRuoli, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizioApplicativoRuoli</var> of type {@link org.openspcoop2.core.config.ServizioApplicativoRuoli}
	 * 
	 * @param servizioApplicativoRuoli Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativoRuoli servizioApplicativoRuoli,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativoRuoli.class, servizioApplicativoRuoli, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazione-regola-applicabilita-risposta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaApplicabilitaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaRisposta</var>
	 * @param trasformazioneRegolaApplicabilitaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaApplicabilitaRisposta trasformazioneRegolaApplicabilitaRisposta) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaApplicabilitaRisposta.class, trasformazioneRegolaApplicabilitaRisposta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaApplicabilitaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaRisposta</var>
	 * @param trasformazioneRegolaApplicabilitaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaApplicabilitaRisposta trasformazioneRegolaApplicabilitaRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaApplicabilitaRisposta.class, trasformazioneRegolaApplicabilitaRisposta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaApplicabilitaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaRisposta</var>
	 * @param trasformazioneRegolaApplicabilitaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaApplicabilitaRisposta trasformazioneRegolaApplicabilitaRisposta) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaApplicabilitaRisposta.class, trasformazioneRegolaApplicabilitaRisposta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaApplicabilitaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaRisposta</var>
	 * @param trasformazioneRegolaApplicabilitaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaApplicabilitaRisposta trasformazioneRegolaApplicabilitaRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaApplicabilitaRisposta.class, trasformazioneRegolaApplicabilitaRisposta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaApplicabilitaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaApplicabilitaRisposta</var>
	 * @param trasformazioneRegolaApplicabilitaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaApplicabilitaRisposta trasformazioneRegolaApplicabilitaRisposta) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaApplicabilitaRisposta.class, trasformazioneRegolaApplicabilitaRisposta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaApplicabilitaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaApplicabilitaRisposta</var>
	 * @param trasformazioneRegolaApplicabilitaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaApplicabilitaRisposta trasformazioneRegolaApplicabilitaRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaApplicabilitaRisposta.class, trasformazioneRegolaApplicabilitaRisposta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaApplicabilitaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta}
	 * 
	 * @param trasformazioneRegolaApplicabilitaRisposta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaApplicabilitaRisposta trasformazioneRegolaApplicabilitaRisposta) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaRisposta.class, trasformazioneRegolaApplicabilitaRisposta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaApplicabilitaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta}
	 * 
	 * @param trasformazioneRegolaApplicabilitaRisposta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaApplicabilitaRisposta trasformazioneRegolaApplicabilitaRisposta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaRisposta.class, trasformazioneRegolaApplicabilitaRisposta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneRegolaApplicabilitaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta}
	 * 
	 * @param trasformazioneRegolaApplicabilitaRisposta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaApplicabilitaRisposta trasformazioneRegolaApplicabilitaRisposta) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaRisposta.class, trasformazioneRegolaApplicabilitaRisposta, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneRegolaApplicabilitaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta}
	 * 
	 * @param trasformazioneRegolaApplicabilitaRisposta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaApplicabilitaRisposta trasformazioneRegolaApplicabilitaRisposta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaRisposta.class, trasformazioneRegolaApplicabilitaRisposta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: proprieta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.config.Proprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Proprieta proprieta) throws SerializerException {
		this.objToXml(fileName, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.config.Proprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Proprieta.class, proprieta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.config.Proprieta}
	 * 
	 * @param file Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Proprieta proprieta) throws SerializerException {
		this.objToXml(file, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.config.Proprieta}
	 * 
	 * @param file Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Proprieta.class, proprieta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.config.Proprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Proprieta proprieta) throws SerializerException {
		this.objToXml(out, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.config.Proprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Proprieta.class, proprieta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.core.config.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.core.config.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.core.config.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toString();
	}
	/**
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.core.config.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, prettyPrint).toString();
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
	 Object: invocazione-credenziali
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>invocazioneCredenziali</var> of type {@link org.openspcoop2.core.config.InvocazioneCredenziali}
	 * 
	 * @param fileName Xml file to serialize the object <var>invocazioneCredenziali</var>
	 * @param invocazioneCredenziali Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InvocazioneCredenziali invocazioneCredenziali) throws SerializerException {
		this.objToXml(fileName, InvocazioneCredenziali.class, invocazioneCredenziali, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>invocazioneCredenziali</var> of type {@link org.openspcoop2.core.config.InvocazioneCredenziali}
	 * 
	 * @param fileName Xml file to serialize the object <var>invocazioneCredenziali</var>
	 * @param invocazioneCredenziali Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InvocazioneCredenziali invocazioneCredenziali,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InvocazioneCredenziali.class, invocazioneCredenziali, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>invocazioneCredenziali</var> of type {@link org.openspcoop2.core.config.InvocazioneCredenziali}
	 * 
	 * @param file Xml file to serialize the object <var>invocazioneCredenziali</var>
	 * @param invocazioneCredenziali Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InvocazioneCredenziali invocazioneCredenziali) throws SerializerException {
		this.objToXml(file, InvocazioneCredenziali.class, invocazioneCredenziali, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>invocazioneCredenziali</var> of type {@link org.openspcoop2.core.config.InvocazioneCredenziali}
	 * 
	 * @param file Xml file to serialize the object <var>invocazioneCredenziali</var>
	 * @param invocazioneCredenziali Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InvocazioneCredenziali invocazioneCredenziali,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InvocazioneCredenziali.class, invocazioneCredenziali, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>invocazioneCredenziali</var> of type {@link org.openspcoop2.core.config.InvocazioneCredenziali}
	 * 
	 * @param out OutputStream to serialize the object <var>invocazioneCredenziali</var>
	 * @param invocazioneCredenziali Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InvocazioneCredenziali invocazioneCredenziali) throws SerializerException {
		this.objToXml(out, InvocazioneCredenziali.class, invocazioneCredenziali, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>invocazioneCredenziali</var> of type {@link org.openspcoop2.core.config.InvocazioneCredenziali}
	 * 
	 * @param out OutputStream to serialize the object <var>invocazioneCredenziali</var>
	 * @param invocazioneCredenziali Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InvocazioneCredenziali invocazioneCredenziali,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InvocazioneCredenziali.class, invocazioneCredenziali, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>invocazioneCredenziali</var> of type {@link org.openspcoop2.core.config.InvocazioneCredenziali}
	 * 
	 * @param invocazioneCredenziali Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InvocazioneCredenziali invocazioneCredenziali) throws SerializerException {
		return this.objToXml(InvocazioneCredenziali.class, invocazioneCredenziali, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>invocazioneCredenziali</var> of type {@link org.openspcoop2.core.config.InvocazioneCredenziali}
	 * 
	 * @param invocazioneCredenziali Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InvocazioneCredenziali invocazioneCredenziali,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InvocazioneCredenziali.class, invocazioneCredenziali, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>invocazioneCredenziali</var> of type {@link org.openspcoop2.core.config.InvocazioneCredenziali}
	 * 
	 * @param invocazioneCredenziali Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InvocazioneCredenziali invocazioneCredenziali) throws SerializerException {
		return this.objToXml(InvocazioneCredenziali.class, invocazioneCredenziali, false).toString();
	}
	/**
	 * Serialize to String the object <var>invocazioneCredenziali</var> of type {@link org.openspcoop2.core.config.InvocazioneCredenziali}
	 * 
	 * @param invocazioneCredenziali Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InvocazioneCredenziali invocazioneCredenziali,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InvocazioneCredenziali.class, invocazioneCredenziali, prettyPrint).toString();
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
	 Object: accesso-dati-keystore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoDatiKeystore</var> of type {@link org.openspcoop2.core.config.AccessoDatiKeystore}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoDatiKeystore</var>
	 * @param accessoDatiKeystore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoDatiKeystore accessoDatiKeystore) throws SerializerException {
		this.objToXml(fileName, AccessoDatiKeystore.class, accessoDatiKeystore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoDatiKeystore</var> of type {@link org.openspcoop2.core.config.AccessoDatiKeystore}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoDatiKeystore</var>
	 * @param accessoDatiKeystore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoDatiKeystore accessoDatiKeystore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccessoDatiKeystore.class, accessoDatiKeystore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoDatiKeystore</var> of type {@link org.openspcoop2.core.config.AccessoDatiKeystore}
	 * 
	 * @param file Xml file to serialize the object <var>accessoDatiKeystore</var>
	 * @param accessoDatiKeystore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoDatiKeystore accessoDatiKeystore) throws SerializerException {
		this.objToXml(file, AccessoDatiKeystore.class, accessoDatiKeystore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoDatiKeystore</var> of type {@link org.openspcoop2.core.config.AccessoDatiKeystore}
	 * 
	 * @param file Xml file to serialize the object <var>accessoDatiKeystore</var>
	 * @param accessoDatiKeystore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoDatiKeystore accessoDatiKeystore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccessoDatiKeystore.class, accessoDatiKeystore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoDatiKeystore</var> of type {@link org.openspcoop2.core.config.AccessoDatiKeystore}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoDatiKeystore</var>
	 * @param accessoDatiKeystore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoDatiKeystore accessoDatiKeystore) throws SerializerException {
		this.objToXml(out, AccessoDatiKeystore.class, accessoDatiKeystore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoDatiKeystore</var> of type {@link org.openspcoop2.core.config.AccessoDatiKeystore}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoDatiKeystore</var>
	 * @param accessoDatiKeystore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoDatiKeystore accessoDatiKeystore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccessoDatiKeystore.class, accessoDatiKeystore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accessoDatiKeystore</var> of type {@link org.openspcoop2.core.config.AccessoDatiKeystore}
	 * 
	 * @param accessoDatiKeystore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoDatiKeystore accessoDatiKeystore) throws SerializerException {
		return this.objToXml(AccessoDatiKeystore.class, accessoDatiKeystore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accessoDatiKeystore</var> of type {@link org.openspcoop2.core.config.AccessoDatiKeystore}
	 * 
	 * @param accessoDatiKeystore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoDatiKeystore accessoDatiKeystore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoDatiKeystore.class, accessoDatiKeystore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accessoDatiKeystore</var> of type {@link org.openspcoop2.core.config.AccessoDatiKeystore}
	 * 
	 * @param accessoDatiKeystore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoDatiKeystore accessoDatiKeystore) throws SerializerException {
		return this.objToXml(AccessoDatiKeystore.class, accessoDatiKeystore, false).toString();
	}
	/**
	 * Serialize to String the object <var>accessoDatiKeystore</var> of type {@link org.openspcoop2.core.config.AccessoDatiKeystore}
	 * 
	 * @param accessoDatiKeystore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoDatiKeystore accessoDatiKeystore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoDatiKeystore.class, accessoDatiKeystore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazione-regola-richiesta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRichiesta}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaRichiesta</var>
	 * @param trasformazioneRegolaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaRichiesta trasformazioneRegolaRichiesta) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaRichiesta.class, trasformazioneRegolaRichiesta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRichiesta}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaRichiesta</var>
	 * @param trasformazioneRegolaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaRichiesta trasformazioneRegolaRichiesta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaRichiesta.class, trasformazioneRegolaRichiesta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRichiesta}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaRichiesta</var>
	 * @param trasformazioneRegolaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaRichiesta trasformazioneRegolaRichiesta) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaRichiesta.class, trasformazioneRegolaRichiesta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRichiesta}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaRichiesta</var>
	 * @param trasformazioneRegolaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaRichiesta trasformazioneRegolaRichiesta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaRichiesta.class, trasformazioneRegolaRichiesta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRichiesta}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaRichiesta</var>
	 * @param trasformazioneRegolaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaRichiesta trasformazioneRegolaRichiesta) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaRichiesta.class, trasformazioneRegolaRichiesta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRichiesta}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaRichiesta</var>
	 * @param trasformazioneRegolaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaRichiesta trasformazioneRegolaRichiesta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaRichiesta.class, trasformazioneRegolaRichiesta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRichiesta}
	 * 
	 * @param trasformazioneRegolaRichiesta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaRichiesta trasformazioneRegolaRichiesta) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaRichiesta.class, trasformazioneRegolaRichiesta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRichiesta}
	 * 
	 * @param trasformazioneRegolaRichiesta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaRichiesta trasformazioneRegolaRichiesta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaRichiesta.class, trasformazioneRegolaRichiesta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneRegolaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRichiesta}
	 * 
	 * @param trasformazioneRegolaRichiesta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaRichiesta trasformazioneRegolaRichiesta) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaRichiesta.class, trasformazioneRegolaRichiesta, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneRegolaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRichiesta}
	 * 
	 * @param trasformazioneRegolaRichiesta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaRichiesta trasformazioneRegolaRichiesta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaRichiesta.class, trasformazioneRegolaRichiesta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazione-regola-parametro
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaParametro</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaParametro}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaParametro</var>
	 * @param trasformazioneRegolaParametro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaParametro trasformazioneRegolaParametro) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaParametro.class, trasformazioneRegolaParametro, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaParametro</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaParametro}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaParametro</var>
	 * @param trasformazioneRegolaParametro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaParametro trasformazioneRegolaParametro,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaParametro.class, trasformazioneRegolaParametro, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaParametro</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaParametro}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaParametro</var>
	 * @param trasformazioneRegolaParametro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaParametro trasformazioneRegolaParametro) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaParametro.class, trasformazioneRegolaParametro, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaParametro</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaParametro}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaParametro</var>
	 * @param trasformazioneRegolaParametro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaParametro trasformazioneRegolaParametro,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaParametro.class, trasformazioneRegolaParametro, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaParametro</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaParametro}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaParametro</var>
	 * @param trasformazioneRegolaParametro Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaParametro trasformazioneRegolaParametro) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaParametro.class, trasformazioneRegolaParametro, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaParametro</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaParametro}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaParametro</var>
	 * @param trasformazioneRegolaParametro Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaParametro trasformazioneRegolaParametro,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaParametro.class, trasformazioneRegolaParametro, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaParametro</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaParametro}
	 * 
	 * @param trasformazioneRegolaParametro Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaParametro trasformazioneRegolaParametro) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaParametro.class, trasformazioneRegolaParametro, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaParametro</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaParametro}
	 * 
	 * @param trasformazioneRegolaParametro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaParametro trasformazioneRegolaParametro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaParametro.class, trasformazioneRegolaParametro, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneRegolaParametro</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaParametro}
	 * 
	 * @param trasformazioneRegolaParametro Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaParametro trasformazioneRegolaParametro) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaParametro.class, trasformazioneRegolaParametro, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneRegolaParametro</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaParametro}
	 * 
	 * @param trasformazioneRegolaParametro Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaParametro trasformazioneRegolaParametro,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaParametro.class, trasformazioneRegolaParametro, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazione-rest
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRest</var> of type {@link org.openspcoop2.core.config.TrasformazioneRest}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRest</var>
	 * @param trasformazioneRest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRest trasformazioneRest) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRest.class, trasformazioneRest, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRest</var> of type {@link org.openspcoop2.core.config.TrasformazioneRest}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRest</var>
	 * @param trasformazioneRest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRest trasformazioneRest,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRest.class, trasformazioneRest, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRest</var> of type {@link org.openspcoop2.core.config.TrasformazioneRest}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRest</var>
	 * @param trasformazioneRest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRest trasformazioneRest) throws SerializerException {
		this.objToXml(file, TrasformazioneRest.class, trasformazioneRest, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRest</var> of type {@link org.openspcoop2.core.config.TrasformazioneRest}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRest</var>
	 * @param trasformazioneRest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRest trasformazioneRest,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneRest.class, trasformazioneRest, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRest</var> of type {@link org.openspcoop2.core.config.TrasformazioneRest}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRest</var>
	 * @param trasformazioneRest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRest trasformazioneRest) throws SerializerException {
		this.objToXml(out, TrasformazioneRest.class, trasformazioneRest, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRest</var> of type {@link org.openspcoop2.core.config.TrasformazioneRest}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRest</var>
	 * @param trasformazioneRest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRest trasformazioneRest,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneRest.class, trasformazioneRest, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneRest</var> of type {@link org.openspcoop2.core.config.TrasformazioneRest}
	 * 
	 * @param trasformazioneRest Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRest trasformazioneRest) throws SerializerException {
		return this.objToXml(TrasformazioneRest.class, trasformazioneRest, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneRest</var> of type {@link org.openspcoop2.core.config.TrasformazioneRest}
	 * 
	 * @param trasformazioneRest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRest trasformazioneRest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRest.class, trasformazioneRest, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneRest</var> of type {@link org.openspcoop2.core.config.TrasformazioneRest}
	 * 
	 * @param trasformazioneRest Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRest trasformazioneRest) throws SerializerException {
		return this.objToXml(TrasformazioneRest.class, trasformazioneRest, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneRest</var> of type {@link org.openspcoop2.core.config.TrasformazioneRest}
	 * 
	 * @param trasformazioneRest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRest trasformazioneRest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRest.class, trasformazioneRest, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazione-soap
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneSoap</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoap}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneSoap</var>
	 * @param trasformazioneSoap Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneSoap trasformazioneSoap) throws SerializerException {
		this.objToXml(fileName, TrasformazioneSoap.class, trasformazioneSoap, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneSoap</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoap}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneSoap</var>
	 * @param trasformazioneSoap Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneSoap trasformazioneSoap,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneSoap.class, trasformazioneSoap, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneSoap</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoap}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneSoap</var>
	 * @param trasformazioneSoap Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneSoap trasformazioneSoap) throws SerializerException {
		this.objToXml(file, TrasformazioneSoap.class, trasformazioneSoap, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneSoap</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoap}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneSoap</var>
	 * @param trasformazioneSoap Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneSoap trasformazioneSoap,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneSoap.class, trasformazioneSoap, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneSoap</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoap}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneSoap</var>
	 * @param trasformazioneSoap Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneSoap trasformazioneSoap) throws SerializerException {
		this.objToXml(out, TrasformazioneSoap.class, trasformazioneSoap, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneSoap</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoap}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneSoap</var>
	 * @param trasformazioneSoap Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneSoap trasformazioneSoap,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneSoap.class, trasformazioneSoap, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneSoap</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoap}
	 * 
	 * @param trasformazioneSoap Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneSoap trasformazioneSoap) throws SerializerException {
		return this.objToXml(TrasformazioneSoap.class, trasformazioneSoap, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneSoap</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoap}
	 * 
	 * @param trasformazioneSoap Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneSoap trasformazioneSoap,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneSoap.class, trasformazioneSoap, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneSoap</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoap}
	 * 
	 * @param trasformazioneSoap Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneSoap trasformazioneSoap) throws SerializerException {
		return this.objToXml(TrasformazioneSoap.class, trasformazioneSoap, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneSoap</var> of type {@link org.openspcoop2.core.config.TrasformazioneSoap}
	 * 
	 * @param trasformazioneSoap Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneSoap trasformazioneSoap,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneSoap.class, trasformazioneSoap, prettyPrint).toString();
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
	 Object: configurazione-url-invocazione-regola
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneUrlInvocazioneRegola</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneUrlInvocazioneRegola</var>
	 * @param configurazioneUrlInvocazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneUrlInvocazioneRegola configurazioneUrlInvocazioneRegola) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneUrlInvocazioneRegola.class, configurazioneUrlInvocazioneRegola, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneUrlInvocazioneRegola</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneUrlInvocazioneRegola</var>
	 * @param configurazioneUrlInvocazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneUrlInvocazioneRegola configurazioneUrlInvocazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneUrlInvocazioneRegola.class, configurazioneUrlInvocazioneRegola, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneUrlInvocazioneRegola</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneUrlInvocazioneRegola</var>
	 * @param configurazioneUrlInvocazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneUrlInvocazioneRegola configurazioneUrlInvocazioneRegola) throws SerializerException {
		this.objToXml(file, ConfigurazioneUrlInvocazioneRegola.class, configurazioneUrlInvocazioneRegola, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneUrlInvocazioneRegola</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneUrlInvocazioneRegola</var>
	 * @param configurazioneUrlInvocazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneUrlInvocazioneRegola configurazioneUrlInvocazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneUrlInvocazioneRegola.class, configurazioneUrlInvocazioneRegola, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneUrlInvocazioneRegola</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneUrlInvocazioneRegola</var>
	 * @param configurazioneUrlInvocazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneUrlInvocazioneRegola configurazioneUrlInvocazioneRegola) throws SerializerException {
		this.objToXml(out, ConfigurazioneUrlInvocazioneRegola.class, configurazioneUrlInvocazioneRegola, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneUrlInvocazioneRegola</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneUrlInvocazioneRegola</var>
	 * @param configurazioneUrlInvocazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneUrlInvocazioneRegola configurazioneUrlInvocazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneUrlInvocazioneRegola.class, configurazioneUrlInvocazioneRegola, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneUrlInvocazioneRegola</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola}
	 * 
	 * @param configurazioneUrlInvocazioneRegola Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneUrlInvocazioneRegola configurazioneUrlInvocazioneRegola) throws SerializerException {
		return this.objToXml(ConfigurazioneUrlInvocazioneRegola.class, configurazioneUrlInvocazioneRegola, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneUrlInvocazioneRegola</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola}
	 * 
	 * @param configurazioneUrlInvocazioneRegola Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneUrlInvocazioneRegola configurazioneUrlInvocazioneRegola,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneUrlInvocazioneRegola.class, configurazioneUrlInvocazioneRegola, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneUrlInvocazioneRegola</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola}
	 * 
	 * @param configurazioneUrlInvocazioneRegola Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneUrlInvocazioneRegola configurazioneUrlInvocazioneRegola) throws SerializerException {
		return this.objToXml(ConfigurazioneUrlInvocazioneRegola.class, configurazioneUrlInvocazioneRegola, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneUrlInvocazioneRegola</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola}
	 * 
	 * @param configurazioneUrlInvocazioneRegola Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneUrlInvocazioneRegola configurazioneUrlInvocazioneRegola,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneUrlInvocazioneRegola.class, configurazioneUrlInvocazioneRegola, prettyPrint).toString();
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
	 Object: porta-applicativa-autorizzazione-servizi-applicativi
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var>
	 * @param portaApplicativaAutorizzazioneServiziApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAutorizzazioneServiziApplicativi portaApplicativaAutorizzazioneServiziApplicativi) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAutorizzazioneServiziApplicativi.class, portaApplicativaAutorizzazioneServiziApplicativi, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var>
	 * @param portaApplicativaAutorizzazioneServiziApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaAutorizzazioneServiziApplicativi portaApplicativaAutorizzazioneServiziApplicativi,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaAutorizzazioneServiziApplicativi.class, portaApplicativaAutorizzazioneServiziApplicativi, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var>
	 * @param portaApplicativaAutorizzazioneServiziApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAutorizzazioneServiziApplicativi portaApplicativaAutorizzazioneServiziApplicativi) throws SerializerException {
		this.objToXml(file, PortaApplicativaAutorizzazioneServiziApplicativi.class, portaApplicativaAutorizzazioneServiziApplicativi, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var>
	 * @param portaApplicativaAutorizzazioneServiziApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaAutorizzazioneServiziApplicativi portaApplicativaAutorizzazioneServiziApplicativi,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaAutorizzazioneServiziApplicativi.class, portaApplicativaAutorizzazioneServiziApplicativi, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var>
	 * @param portaApplicativaAutorizzazioneServiziApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAutorizzazioneServiziApplicativi portaApplicativaAutorizzazioneServiziApplicativi) throws SerializerException {
		this.objToXml(out, PortaApplicativaAutorizzazioneServiziApplicativi.class, portaApplicativaAutorizzazioneServiziApplicativi, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var>
	 * @param portaApplicativaAutorizzazioneServiziApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaAutorizzazioneServiziApplicativi portaApplicativaAutorizzazioneServiziApplicativi,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaAutorizzazioneServiziApplicativi.class, portaApplicativaAutorizzazioneServiziApplicativi, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi}
	 * 
	 * @param portaApplicativaAutorizzazioneServiziApplicativi Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAutorizzazioneServiziApplicativi portaApplicativaAutorizzazioneServiziApplicativi) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneServiziApplicativi.class, portaApplicativaAutorizzazioneServiziApplicativi, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi}
	 * 
	 * @param portaApplicativaAutorizzazioneServiziApplicativi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaAutorizzazioneServiziApplicativi portaApplicativaAutorizzazioneServiziApplicativi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneServiziApplicativi.class, portaApplicativaAutorizzazioneServiziApplicativi, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi}
	 * 
	 * @param portaApplicativaAutorizzazioneServiziApplicativi Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAutorizzazioneServiziApplicativi portaApplicativaAutorizzazioneServiziApplicativi) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneServiziApplicativi.class, portaApplicativaAutorizzazioneServiziApplicativi, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaAutorizzazioneServiziApplicativi</var> of type {@link org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi}
	 * 
	 * @param portaApplicativaAutorizzazioneServiziApplicativi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaAutorizzazioneServiziApplicativi portaApplicativaAutorizzazioneServiziApplicativi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaAutorizzazioneServiziApplicativi.class, portaApplicativaAutorizzazioneServiziApplicativi, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazione-regola-risposta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRisposta}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaRisposta</var>
	 * @param trasformazioneRegolaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaRisposta trasformazioneRegolaRisposta) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaRisposta.class, trasformazioneRegolaRisposta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRisposta}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaRisposta</var>
	 * @param trasformazioneRegolaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaRisposta trasformazioneRegolaRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaRisposta.class, trasformazioneRegolaRisposta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRisposta}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaRisposta</var>
	 * @param trasformazioneRegolaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaRisposta trasformazioneRegolaRisposta) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaRisposta.class, trasformazioneRegolaRisposta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRisposta}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaRisposta</var>
	 * @param trasformazioneRegolaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaRisposta trasformazioneRegolaRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaRisposta.class, trasformazioneRegolaRisposta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRisposta}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaRisposta</var>
	 * @param trasformazioneRegolaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaRisposta trasformazioneRegolaRisposta) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaRisposta.class, trasformazioneRegolaRisposta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRisposta}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaRisposta</var>
	 * @param trasformazioneRegolaRisposta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaRisposta trasformazioneRegolaRisposta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaRisposta.class, trasformazioneRegolaRisposta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRisposta}
	 * 
	 * @param trasformazioneRegolaRisposta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaRisposta trasformazioneRegolaRisposta) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaRisposta.class, trasformazioneRegolaRisposta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRisposta}
	 * 
	 * @param trasformazioneRegolaRisposta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaRisposta trasformazioneRegolaRisposta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaRisposta.class, trasformazioneRegolaRisposta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneRegolaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRisposta}
	 * 
	 * @param trasformazioneRegolaRisposta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaRisposta trasformazioneRegolaRisposta) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaRisposta.class, trasformazioneRegolaRisposta, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneRegolaRisposta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaRisposta}
	 * 
	 * @param trasformazioneRegolaRisposta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaRisposta trasformazioneRegolaRisposta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaRisposta.class, trasformazioneRegolaRisposta, prettyPrint).toString();
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
	 Object: porta-applicativa-servizio-applicativo-connettore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaServizioApplicativoConnettore</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaServizioApplicativoConnettore</var>
	 * @param portaApplicativaServizioApplicativoConnettore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaServizioApplicativoConnettore portaApplicativaServizioApplicativoConnettore) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaServizioApplicativoConnettore.class, portaApplicativaServizioApplicativoConnettore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaServizioApplicativoConnettore</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaServizioApplicativoConnettore</var>
	 * @param portaApplicativaServizioApplicativoConnettore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaServizioApplicativoConnettore portaApplicativaServizioApplicativoConnettore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaServizioApplicativoConnettore.class, portaApplicativaServizioApplicativoConnettore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaServizioApplicativoConnettore</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaServizioApplicativoConnettore</var>
	 * @param portaApplicativaServizioApplicativoConnettore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaServizioApplicativoConnettore portaApplicativaServizioApplicativoConnettore) throws SerializerException {
		this.objToXml(file, PortaApplicativaServizioApplicativoConnettore.class, portaApplicativaServizioApplicativoConnettore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaServizioApplicativoConnettore</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaServizioApplicativoConnettore</var>
	 * @param portaApplicativaServizioApplicativoConnettore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaServizioApplicativoConnettore portaApplicativaServizioApplicativoConnettore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaServizioApplicativoConnettore.class, portaApplicativaServizioApplicativoConnettore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaServizioApplicativoConnettore</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaServizioApplicativoConnettore</var>
	 * @param portaApplicativaServizioApplicativoConnettore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaServizioApplicativoConnettore portaApplicativaServizioApplicativoConnettore) throws SerializerException {
		this.objToXml(out, PortaApplicativaServizioApplicativoConnettore.class, portaApplicativaServizioApplicativoConnettore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaServizioApplicativoConnettore</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaServizioApplicativoConnettore</var>
	 * @param portaApplicativaServizioApplicativoConnettore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaServizioApplicativoConnettore portaApplicativaServizioApplicativoConnettore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaServizioApplicativoConnettore.class, portaApplicativaServizioApplicativoConnettore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaServizioApplicativoConnettore</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore}
	 * 
	 * @param portaApplicativaServizioApplicativoConnettore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaServizioApplicativoConnettore portaApplicativaServizioApplicativoConnettore) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativoConnettore.class, portaApplicativaServizioApplicativoConnettore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaServizioApplicativoConnettore</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore}
	 * 
	 * @param portaApplicativaServizioApplicativoConnettore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaServizioApplicativoConnettore portaApplicativaServizioApplicativoConnettore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativoConnettore.class, portaApplicativaServizioApplicativoConnettore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaServizioApplicativoConnettore</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore}
	 * 
	 * @param portaApplicativaServizioApplicativoConnettore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaServizioApplicativoConnettore portaApplicativaServizioApplicativoConnettore) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativoConnettore.class, portaApplicativaServizioApplicativoConnettore, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaServizioApplicativoConnettore</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore}
	 * 
	 * @param portaApplicativaServizioApplicativoConnettore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaServizioApplicativoConnettore portaApplicativaServizioApplicativoConnettore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativoConnettore.class, portaApplicativaServizioApplicativoConnettore, prettyPrint).toString();
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
	 Object: porta-delegata-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo) throws SerializerException {
		this.objToXml(fileName, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo) throws SerializerException {
		this.objToXml(file, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo) throws SerializerException {
		this.objToXml(out, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataServizioApplicativo</var>
	 * @param portaDelegataServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataServizioApplicativo portaDelegataServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizioApplicativo}
	 * 
	 * @param portaDelegataServizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataServizioApplicativo portaDelegataServizioApplicativo) throws SerializerException {
		return this.objToXml(PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizioApplicativo}
	 * 
	 * @param portaDelegataServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataServizioApplicativo portaDelegataServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizioApplicativo}
	 * 
	 * @param portaDelegataServizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataServizioApplicativo portaDelegataServizioApplicativo) throws SerializerException {
		return this.objToXml(PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDelegataServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaDelegataServizioApplicativo}
	 * 
	 * @param portaDelegataServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataServizioApplicativo portaDelegataServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataServizioApplicativo.class, portaDelegataServizioApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: autorizzazione-ruoli
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>autorizzazioneRuoli</var> of type {@link org.openspcoop2.core.config.AutorizzazioneRuoli}
	 * 
	 * @param fileName Xml file to serialize the object <var>autorizzazioneRuoli</var>
	 * @param autorizzazioneRuoli Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AutorizzazioneRuoli autorizzazioneRuoli) throws SerializerException {
		this.objToXml(fileName, AutorizzazioneRuoli.class, autorizzazioneRuoli, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>autorizzazioneRuoli</var> of type {@link org.openspcoop2.core.config.AutorizzazioneRuoli}
	 * 
	 * @param fileName Xml file to serialize the object <var>autorizzazioneRuoli</var>
	 * @param autorizzazioneRuoli Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AutorizzazioneRuoli autorizzazioneRuoli,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AutorizzazioneRuoli.class, autorizzazioneRuoli, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>autorizzazioneRuoli</var> of type {@link org.openspcoop2.core.config.AutorizzazioneRuoli}
	 * 
	 * @param file Xml file to serialize the object <var>autorizzazioneRuoli</var>
	 * @param autorizzazioneRuoli Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AutorizzazioneRuoli autorizzazioneRuoli) throws SerializerException {
		this.objToXml(file, AutorizzazioneRuoli.class, autorizzazioneRuoli, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>autorizzazioneRuoli</var> of type {@link org.openspcoop2.core.config.AutorizzazioneRuoli}
	 * 
	 * @param file Xml file to serialize the object <var>autorizzazioneRuoli</var>
	 * @param autorizzazioneRuoli Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AutorizzazioneRuoli autorizzazioneRuoli,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AutorizzazioneRuoli.class, autorizzazioneRuoli, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>autorizzazioneRuoli</var> of type {@link org.openspcoop2.core.config.AutorizzazioneRuoli}
	 * 
	 * @param out OutputStream to serialize the object <var>autorizzazioneRuoli</var>
	 * @param autorizzazioneRuoli Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AutorizzazioneRuoli autorizzazioneRuoli) throws SerializerException {
		this.objToXml(out, AutorizzazioneRuoli.class, autorizzazioneRuoli, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>autorizzazioneRuoli</var> of type {@link org.openspcoop2.core.config.AutorizzazioneRuoli}
	 * 
	 * @param out OutputStream to serialize the object <var>autorizzazioneRuoli</var>
	 * @param autorizzazioneRuoli Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AutorizzazioneRuoli autorizzazioneRuoli,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AutorizzazioneRuoli.class, autorizzazioneRuoli, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>autorizzazioneRuoli</var> of type {@link org.openspcoop2.core.config.AutorizzazioneRuoli}
	 * 
	 * @param autorizzazioneRuoli Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AutorizzazioneRuoli autorizzazioneRuoli) throws SerializerException {
		return this.objToXml(AutorizzazioneRuoli.class, autorizzazioneRuoli, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>autorizzazioneRuoli</var> of type {@link org.openspcoop2.core.config.AutorizzazioneRuoli}
	 * 
	 * @param autorizzazioneRuoli Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AutorizzazioneRuoli autorizzazioneRuoli,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AutorizzazioneRuoli.class, autorizzazioneRuoli, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>autorizzazioneRuoli</var> of type {@link org.openspcoop2.core.config.AutorizzazioneRuoli}
	 * 
	 * @param autorizzazioneRuoli Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AutorizzazioneRuoli autorizzazioneRuoli) throws SerializerException {
		return this.objToXml(AutorizzazioneRuoli.class, autorizzazioneRuoli, false).toString();
	}
	/**
	 * Serialize to String the object <var>autorizzazioneRuoli</var> of type {@link org.openspcoop2.core.config.AutorizzazioneRuoli}
	 * 
	 * @param autorizzazioneRuoli Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AutorizzazioneRuoli autorizzazioneRuoli,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AutorizzazioneRuoli.class, autorizzazioneRuoli, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: autorizzazione-scope
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>autorizzazioneScope</var> of type {@link org.openspcoop2.core.config.AutorizzazioneScope}
	 * 
	 * @param fileName Xml file to serialize the object <var>autorizzazioneScope</var>
	 * @param autorizzazioneScope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AutorizzazioneScope autorizzazioneScope) throws SerializerException {
		this.objToXml(fileName, AutorizzazioneScope.class, autorizzazioneScope, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>autorizzazioneScope</var> of type {@link org.openspcoop2.core.config.AutorizzazioneScope}
	 * 
	 * @param fileName Xml file to serialize the object <var>autorizzazioneScope</var>
	 * @param autorizzazioneScope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AutorizzazioneScope autorizzazioneScope,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AutorizzazioneScope.class, autorizzazioneScope, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>autorizzazioneScope</var> of type {@link org.openspcoop2.core.config.AutorizzazioneScope}
	 * 
	 * @param file Xml file to serialize the object <var>autorizzazioneScope</var>
	 * @param autorizzazioneScope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AutorizzazioneScope autorizzazioneScope) throws SerializerException {
		this.objToXml(file, AutorizzazioneScope.class, autorizzazioneScope, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>autorizzazioneScope</var> of type {@link org.openspcoop2.core.config.AutorizzazioneScope}
	 * 
	 * @param file Xml file to serialize the object <var>autorizzazioneScope</var>
	 * @param autorizzazioneScope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AutorizzazioneScope autorizzazioneScope,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AutorizzazioneScope.class, autorizzazioneScope, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>autorizzazioneScope</var> of type {@link org.openspcoop2.core.config.AutorizzazioneScope}
	 * 
	 * @param out OutputStream to serialize the object <var>autorizzazioneScope</var>
	 * @param autorizzazioneScope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AutorizzazioneScope autorizzazioneScope) throws SerializerException {
		this.objToXml(out, AutorizzazioneScope.class, autorizzazioneScope, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>autorizzazioneScope</var> of type {@link org.openspcoop2.core.config.AutorizzazioneScope}
	 * 
	 * @param out OutputStream to serialize the object <var>autorizzazioneScope</var>
	 * @param autorizzazioneScope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AutorizzazioneScope autorizzazioneScope,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AutorizzazioneScope.class, autorizzazioneScope, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>autorizzazioneScope</var> of type {@link org.openspcoop2.core.config.AutorizzazioneScope}
	 * 
	 * @param autorizzazioneScope Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AutorizzazioneScope autorizzazioneScope) throws SerializerException {
		return this.objToXml(AutorizzazioneScope.class, autorizzazioneScope, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>autorizzazioneScope</var> of type {@link org.openspcoop2.core.config.AutorizzazioneScope}
	 * 
	 * @param autorizzazioneScope Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AutorizzazioneScope autorizzazioneScope,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AutorizzazioneScope.class, autorizzazioneScope, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>autorizzazioneScope</var> of type {@link org.openspcoop2.core.config.AutorizzazioneScope}
	 * 
	 * @param autorizzazioneScope Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AutorizzazioneScope autorizzazioneScope) throws SerializerException {
		return this.objToXml(AutorizzazioneScope.class, autorizzazioneScope, false).toString();
	}
	/**
	 * Serialize to String the object <var>autorizzazioneScope</var> of type {@link org.openspcoop2.core.config.AutorizzazioneScope}
	 * 
	 * @param autorizzazioneScope Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AutorizzazioneScope autorizzazioneScope,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AutorizzazioneScope.class, autorizzazioneScope, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-local-forward
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataLocalForward</var> of type {@link org.openspcoop2.core.config.PortaDelegataLocalForward}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataLocalForward</var>
	 * @param portaDelegataLocalForward Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataLocalForward portaDelegataLocalForward) throws SerializerException {
		this.objToXml(fileName, PortaDelegataLocalForward.class, portaDelegataLocalForward, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDelegataLocalForward</var> of type {@link org.openspcoop2.core.config.PortaDelegataLocalForward}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDelegataLocalForward</var>
	 * @param portaDelegataLocalForward Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDelegataLocalForward portaDelegataLocalForward,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDelegataLocalForward.class, portaDelegataLocalForward, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataLocalForward</var> of type {@link org.openspcoop2.core.config.PortaDelegataLocalForward}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataLocalForward</var>
	 * @param portaDelegataLocalForward Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataLocalForward portaDelegataLocalForward) throws SerializerException {
		this.objToXml(file, PortaDelegataLocalForward.class, portaDelegataLocalForward, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDelegataLocalForward</var> of type {@link org.openspcoop2.core.config.PortaDelegataLocalForward}
	 * 
	 * @param file Xml file to serialize the object <var>portaDelegataLocalForward</var>
	 * @param portaDelegataLocalForward Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDelegataLocalForward portaDelegataLocalForward,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDelegataLocalForward.class, portaDelegataLocalForward, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataLocalForward</var> of type {@link org.openspcoop2.core.config.PortaDelegataLocalForward}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataLocalForward</var>
	 * @param portaDelegataLocalForward Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataLocalForward portaDelegataLocalForward) throws SerializerException {
		this.objToXml(out, PortaDelegataLocalForward.class, portaDelegataLocalForward, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDelegataLocalForward</var> of type {@link org.openspcoop2.core.config.PortaDelegataLocalForward}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDelegataLocalForward</var>
	 * @param portaDelegataLocalForward Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDelegataLocalForward portaDelegataLocalForward,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDelegataLocalForward.class, portaDelegataLocalForward, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDelegataLocalForward</var> of type {@link org.openspcoop2.core.config.PortaDelegataLocalForward}
	 * 
	 * @param portaDelegataLocalForward Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataLocalForward portaDelegataLocalForward) throws SerializerException {
		return this.objToXml(PortaDelegataLocalForward.class, portaDelegataLocalForward, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDelegataLocalForward</var> of type {@link org.openspcoop2.core.config.PortaDelegataLocalForward}
	 * 
	 * @param portaDelegataLocalForward Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDelegataLocalForward portaDelegataLocalForward,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataLocalForward.class, portaDelegataLocalForward, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDelegataLocalForward</var> of type {@link org.openspcoop2.core.config.PortaDelegataLocalForward}
	 * 
	 * @param portaDelegataLocalForward Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataLocalForward portaDelegataLocalForward) throws SerializerException {
		return this.objToXml(PortaDelegataLocalForward.class, portaDelegataLocalForward, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDelegataLocalForward</var> of type {@link org.openspcoop2.core.config.PortaDelegataLocalForward}
	 * 
	 * @param portaDelegataLocalForward Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDelegataLocalForward portaDelegataLocalForward,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDelegataLocalForward.class, portaDelegataLocalForward, prettyPrint).toString();
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
	 Object: dump-configurazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dumpConfigurazione</var> of type {@link org.openspcoop2.core.config.DumpConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>dumpConfigurazione</var>
	 * @param dumpConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DumpConfigurazione dumpConfigurazione) throws SerializerException {
		this.objToXml(fileName, DumpConfigurazione.class, dumpConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dumpConfigurazione</var> of type {@link org.openspcoop2.core.config.DumpConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>dumpConfigurazione</var>
	 * @param dumpConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DumpConfigurazione dumpConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DumpConfigurazione.class, dumpConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dumpConfigurazione</var> of type {@link org.openspcoop2.core.config.DumpConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>dumpConfigurazione</var>
	 * @param dumpConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DumpConfigurazione dumpConfigurazione) throws SerializerException {
		this.objToXml(file, DumpConfigurazione.class, dumpConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dumpConfigurazione</var> of type {@link org.openspcoop2.core.config.DumpConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>dumpConfigurazione</var>
	 * @param dumpConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DumpConfigurazione dumpConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DumpConfigurazione.class, dumpConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dumpConfigurazione</var> of type {@link org.openspcoop2.core.config.DumpConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>dumpConfigurazione</var>
	 * @param dumpConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DumpConfigurazione dumpConfigurazione) throws SerializerException {
		this.objToXml(out, DumpConfigurazione.class, dumpConfigurazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dumpConfigurazione</var> of type {@link org.openspcoop2.core.config.DumpConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>dumpConfigurazione</var>
	 * @param dumpConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DumpConfigurazione dumpConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DumpConfigurazione.class, dumpConfigurazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dumpConfigurazione</var> of type {@link org.openspcoop2.core.config.DumpConfigurazione}
	 * 
	 * @param dumpConfigurazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DumpConfigurazione dumpConfigurazione) throws SerializerException {
		return this.objToXml(DumpConfigurazione.class, dumpConfigurazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dumpConfigurazione</var> of type {@link org.openspcoop2.core.config.DumpConfigurazione}
	 * 
	 * @param dumpConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DumpConfigurazione dumpConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DumpConfigurazione.class, dumpConfigurazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dumpConfigurazione</var> of type {@link org.openspcoop2.core.config.DumpConfigurazione}
	 * 
	 * @param dumpConfigurazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DumpConfigurazione dumpConfigurazione) throws SerializerException {
		return this.objToXml(DumpConfigurazione.class, dumpConfigurazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>dumpConfigurazione</var> of type {@link org.openspcoop2.core.config.DumpConfigurazione}
	 * 
	 * @param dumpConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DumpConfigurazione dumpConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DumpConfigurazione.class, dumpConfigurazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-tracciamento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaTracciamento</var> of type {@link org.openspcoop2.core.config.PortaTracciamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaTracciamento</var>
	 * @param portaTracciamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaTracciamento portaTracciamento) throws SerializerException {
		this.objToXml(fileName, PortaTracciamento.class, portaTracciamento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaTracciamento</var> of type {@link org.openspcoop2.core.config.PortaTracciamento}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaTracciamento</var>
	 * @param portaTracciamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaTracciamento portaTracciamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaTracciamento.class, portaTracciamento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaTracciamento</var> of type {@link org.openspcoop2.core.config.PortaTracciamento}
	 * 
	 * @param file Xml file to serialize the object <var>portaTracciamento</var>
	 * @param portaTracciamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaTracciamento portaTracciamento) throws SerializerException {
		this.objToXml(file, PortaTracciamento.class, portaTracciamento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaTracciamento</var> of type {@link org.openspcoop2.core.config.PortaTracciamento}
	 * 
	 * @param file Xml file to serialize the object <var>portaTracciamento</var>
	 * @param portaTracciamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaTracciamento portaTracciamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaTracciamento.class, portaTracciamento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaTracciamento</var> of type {@link org.openspcoop2.core.config.PortaTracciamento}
	 * 
	 * @param out OutputStream to serialize the object <var>portaTracciamento</var>
	 * @param portaTracciamento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaTracciamento portaTracciamento) throws SerializerException {
		this.objToXml(out, PortaTracciamento.class, portaTracciamento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaTracciamento</var> of type {@link org.openspcoop2.core.config.PortaTracciamento}
	 * 
	 * @param out OutputStream to serialize the object <var>portaTracciamento</var>
	 * @param portaTracciamento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaTracciamento portaTracciamento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaTracciamento.class, portaTracciamento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaTracciamento</var> of type {@link org.openspcoop2.core.config.PortaTracciamento}
	 * 
	 * @param portaTracciamento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaTracciamento portaTracciamento) throws SerializerException {
		return this.objToXml(PortaTracciamento.class, portaTracciamento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaTracciamento</var> of type {@link org.openspcoop2.core.config.PortaTracciamento}
	 * 
	 * @param portaTracciamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaTracciamento portaTracciamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaTracciamento.class, portaTracciamento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaTracciamento</var> of type {@link org.openspcoop2.core.config.PortaTracciamento}
	 * 
	 * @param portaTracciamento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaTracciamento portaTracciamento) throws SerializerException {
		return this.objToXml(PortaTracciamento.class, portaTracciamento, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaTracciamento</var> of type {@link org.openspcoop2.core.config.PortaTracciamento}
	 * 
	 * @param portaTracciamento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaTracciamento portaTracciamento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaTracciamento.class, portaTracciamento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: cors-configurazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>corsConfigurazione</var> of type {@link org.openspcoop2.core.config.CorsConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>corsConfigurazione</var>
	 * @param corsConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorsConfigurazione corsConfigurazione) throws SerializerException {
		this.objToXml(fileName, CorsConfigurazione.class, corsConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>corsConfigurazione</var> of type {@link org.openspcoop2.core.config.CorsConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>corsConfigurazione</var>
	 * @param corsConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorsConfigurazione corsConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CorsConfigurazione.class, corsConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>corsConfigurazione</var> of type {@link org.openspcoop2.core.config.CorsConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>corsConfigurazione</var>
	 * @param corsConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorsConfigurazione corsConfigurazione) throws SerializerException {
		this.objToXml(file, CorsConfigurazione.class, corsConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>corsConfigurazione</var> of type {@link org.openspcoop2.core.config.CorsConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>corsConfigurazione</var>
	 * @param corsConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorsConfigurazione corsConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CorsConfigurazione.class, corsConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>corsConfigurazione</var> of type {@link org.openspcoop2.core.config.CorsConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>corsConfigurazione</var>
	 * @param corsConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorsConfigurazione corsConfigurazione) throws SerializerException {
		this.objToXml(out, CorsConfigurazione.class, corsConfigurazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>corsConfigurazione</var> of type {@link org.openspcoop2.core.config.CorsConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>corsConfigurazione</var>
	 * @param corsConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorsConfigurazione corsConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CorsConfigurazione.class, corsConfigurazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>corsConfigurazione</var> of type {@link org.openspcoop2.core.config.CorsConfigurazione}
	 * 
	 * @param corsConfigurazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorsConfigurazione corsConfigurazione) throws SerializerException {
		return this.objToXml(CorsConfigurazione.class, corsConfigurazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>corsConfigurazione</var> of type {@link org.openspcoop2.core.config.CorsConfigurazione}
	 * 
	 * @param corsConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorsConfigurazione corsConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorsConfigurazione.class, corsConfigurazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>corsConfigurazione</var> of type {@link org.openspcoop2.core.config.CorsConfigurazione}
	 * 
	 * @param corsConfigurazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorsConfigurazione corsConfigurazione) throws SerializerException {
		return this.objToXml(CorsConfigurazione.class, corsConfigurazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>corsConfigurazione</var> of type {@link org.openspcoop2.core.config.CorsConfigurazione}
	 * 
	 * @param corsConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorsConfigurazione corsConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorsConfigurazione.class, corsConfigurazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: response-caching-configurazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responseCachingConfigurazione</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>responseCachingConfigurazione</var>
	 * @param responseCachingConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponseCachingConfigurazione responseCachingConfigurazione) throws SerializerException {
		this.objToXml(fileName, ResponseCachingConfigurazione.class, responseCachingConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responseCachingConfigurazione</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>responseCachingConfigurazione</var>
	 * @param responseCachingConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponseCachingConfigurazione responseCachingConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResponseCachingConfigurazione.class, responseCachingConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>responseCachingConfigurazione</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>responseCachingConfigurazione</var>
	 * @param responseCachingConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponseCachingConfigurazione responseCachingConfigurazione) throws SerializerException {
		this.objToXml(file, ResponseCachingConfigurazione.class, responseCachingConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>responseCachingConfigurazione</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>responseCachingConfigurazione</var>
	 * @param responseCachingConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponseCachingConfigurazione responseCachingConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResponseCachingConfigurazione.class, responseCachingConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>responseCachingConfigurazione</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>responseCachingConfigurazione</var>
	 * @param responseCachingConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponseCachingConfigurazione responseCachingConfigurazione) throws SerializerException {
		this.objToXml(out, ResponseCachingConfigurazione.class, responseCachingConfigurazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>responseCachingConfigurazione</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>responseCachingConfigurazione</var>
	 * @param responseCachingConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponseCachingConfigurazione responseCachingConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResponseCachingConfigurazione.class, responseCachingConfigurazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>responseCachingConfigurazione</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazione}
	 * 
	 * @param responseCachingConfigurazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponseCachingConfigurazione responseCachingConfigurazione) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazione.class, responseCachingConfigurazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>responseCachingConfigurazione</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazione}
	 * 
	 * @param responseCachingConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponseCachingConfigurazione responseCachingConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazione.class, responseCachingConfigurazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>responseCachingConfigurazione</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazione}
	 * 
	 * @param responseCachingConfigurazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponseCachingConfigurazione responseCachingConfigurazione) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazione.class, responseCachingConfigurazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>responseCachingConfigurazione</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazione}
	 * 
	 * @param responseCachingConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponseCachingConfigurazione responseCachingConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazione.class, responseCachingConfigurazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazioni
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioni</var> of type {@link org.openspcoop2.core.config.Trasformazioni}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioni</var>
	 * @param trasformazioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Trasformazioni trasformazioni) throws SerializerException {
		this.objToXml(fileName, Trasformazioni.class, trasformazioni, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioni</var> of type {@link org.openspcoop2.core.config.Trasformazioni}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioni</var>
	 * @param trasformazioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Trasformazioni trasformazioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Trasformazioni.class, trasformazioni, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioni</var> of type {@link org.openspcoop2.core.config.Trasformazioni}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioni</var>
	 * @param trasformazioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Trasformazioni trasformazioni) throws SerializerException {
		this.objToXml(file, Trasformazioni.class, trasformazioni, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioni</var> of type {@link org.openspcoop2.core.config.Trasformazioni}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioni</var>
	 * @param trasformazioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Trasformazioni trasformazioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Trasformazioni.class, trasformazioni, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioni</var> of type {@link org.openspcoop2.core.config.Trasformazioni}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioni</var>
	 * @param trasformazioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Trasformazioni trasformazioni) throws SerializerException {
		this.objToXml(out, Trasformazioni.class, trasformazioni, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioni</var> of type {@link org.openspcoop2.core.config.Trasformazioni}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioni</var>
	 * @param trasformazioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Trasformazioni trasformazioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Trasformazioni.class, trasformazioni, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioni</var> of type {@link org.openspcoop2.core.config.Trasformazioni}
	 * 
	 * @param trasformazioni Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Trasformazioni trasformazioni) throws SerializerException {
		return this.objToXml(Trasformazioni.class, trasformazioni, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioni</var> of type {@link org.openspcoop2.core.config.Trasformazioni}
	 * 
	 * @param trasformazioni Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Trasformazioni trasformazioni,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Trasformazioni.class, trasformazioni, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioni</var> of type {@link org.openspcoop2.core.config.Trasformazioni}
	 * 
	 * @param trasformazioni Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Trasformazioni trasformazioni) throws SerializerException {
		return this.objToXml(Trasformazioni.class, trasformazioni, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioni</var> of type {@link org.openspcoop2.core.config.Trasformazioni}
	 * 
	 * @param trasformazioni Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Trasformazioni trasformazioni,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Trasformazioni.class, trasformazioni, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: response-caching-configurazione-regola
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responseCachingConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola}
	 * 
	 * @param fileName Xml file to serialize the object <var>responseCachingConfigurazioneRegola</var>
	 * @param responseCachingConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponseCachingConfigurazioneRegola responseCachingConfigurazioneRegola) throws SerializerException {
		this.objToXml(fileName, ResponseCachingConfigurazioneRegola.class, responseCachingConfigurazioneRegola, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responseCachingConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola}
	 * 
	 * @param fileName Xml file to serialize the object <var>responseCachingConfigurazioneRegola</var>
	 * @param responseCachingConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponseCachingConfigurazioneRegola responseCachingConfigurazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResponseCachingConfigurazioneRegola.class, responseCachingConfigurazioneRegola, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>responseCachingConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola}
	 * 
	 * @param file Xml file to serialize the object <var>responseCachingConfigurazioneRegola</var>
	 * @param responseCachingConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponseCachingConfigurazioneRegola responseCachingConfigurazioneRegola) throws SerializerException {
		this.objToXml(file, ResponseCachingConfigurazioneRegola.class, responseCachingConfigurazioneRegola, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>responseCachingConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola}
	 * 
	 * @param file Xml file to serialize the object <var>responseCachingConfigurazioneRegola</var>
	 * @param responseCachingConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponseCachingConfigurazioneRegola responseCachingConfigurazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResponseCachingConfigurazioneRegola.class, responseCachingConfigurazioneRegola, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>responseCachingConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola}
	 * 
	 * @param out OutputStream to serialize the object <var>responseCachingConfigurazioneRegola</var>
	 * @param responseCachingConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponseCachingConfigurazioneRegola responseCachingConfigurazioneRegola) throws SerializerException {
		this.objToXml(out, ResponseCachingConfigurazioneRegola.class, responseCachingConfigurazioneRegola, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>responseCachingConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola}
	 * 
	 * @param out OutputStream to serialize the object <var>responseCachingConfigurazioneRegola</var>
	 * @param responseCachingConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponseCachingConfigurazioneRegola responseCachingConfigurazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResponseCachingConfigurazioneRegola.class, responseCachingConfigurazioneRegola, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>responseCachingConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola}
	 * 
	 * @param responseCachingConfigurazioneRegola Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponseCachingConfigurazioneRegola responseCachingConfigurazioneRegola) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneRegola.class, responseCachingConfigurazioneRegola, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>responseCachingConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola}
	 * 
	 * @param responseCachingConfigurazioneRegola Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponseCachingConfigurazioneRegola responseCachingConfigurazioneRegola,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneRegola.class, responseCachingConfigurazioneRegola, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>responseCachingConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola}
	 * 
	 * @param responseCachingConfigurazioneRegola Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponseCachingConfigurazioneRegola responseCachingConfigurazioneRegola) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneRegola.class, responseCachingConfigurazioneRegola, false).toString();
	}
	/**
	 * Serialize to String the object <var>responseCachingConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola}
	 * 
	 * @param responseCachingConfigurazioneRegola Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponseCachingConfigurazioneRegola responseCachingConfigurazioneRegola,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneRegola.class, responseCachingConfigurazioneRegola, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: generic-properties
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>genericProperties</var> of type {@link org.openspcoop2.core.config.GenericProperties}
	 * 
	 * @param fileName Xml file to serialize the object <var>genericProperties</var>
	 * @param genericProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GenericProperties genericProperties) throws SerializerException {
		this.objToXml(fileName, GenericProperties.class, genericProperties, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>genericProperties</var> of type {@link org.openspcoop2.core.config.GenericProperties}
	 * 
	 * @param fileName Xml file to serialize the object <var>genericProperties</var>
	 * @param genericProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GenericProperties genericProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GenericProperties.class, genericProperties, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>genericProperties</var> of type {@link org.openspcoop2.core.config.GenericProperties}
	 * 
	 * @param file Xml file to serialize the object <var>genericProperties</var>
	 * @param genericProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GenericProperties genericProperties) throws SerializerException {
		this.objToXml(file, GenericProperties.class, genericProperties, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>genericProperties</var> of type {@link org.openspcoop2.core.config.GenericProperties}
	 * 
	 * @param file Xml file to serialize the object <var>genericProperties</var>
	 * @param genericProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GenericProperties genericProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GenericProperties.class, genericProperties, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>genericProperties</var> of type {@link org.openspcoop2.core.config.GenericProperties}
	 * 
	 * @param out OutputStream to serialize the object <var>genericProperties</var>
	 * @param genericProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GenericProperties genericProperties) throws SerializerException {
		this.objToXml(out, GenericProperties.class, genericProperties, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>genericProperties</var> of type {@link org.openspcoop2.core.config.GenericProperties}
	 * 
	 * @param out OutputStream to serialize the object <var>genericProperties</var>
	 * @param genericProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GenericProperties genericProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GenericProperties.class, genericProperties, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>genericProperties</var> of type {@link org.openspcoop2.core.config.GenericProperties}
	 * 
	 * @param genericProperties Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GenericProperties genericProperties) throws SerializerException {
		return this.objToXml(GenericProperties.class, genericProperties, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>genericProperties</var> of type {@link org.openspcoop2.core.config.GenericProperties}
	 * 
	 * @param genericProperties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GenericProperties genericProperties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GenericProperties.class, genericProperties, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>genericProperties</var> of type {@link org.openspcoop2.core.config.GenericProperties}
	 * 
	 * @param genericProperties Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GenericProperties genericProperties) throws SerializerException {
		return this.objToXml(GenericProperties.class, genericProperties, false).toString();
	}
	/**
	 * Serialize to String the object <var>genericProperties</var> of type {@link org.openspcoop2.core.config.GenericProperties}
	 * 
	 * @param genericProperties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GenericProperties genericProperties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GenericProperties.class, genericProperties, prettyPrint).toString();
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
	 Object: canale-configurazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>canaleConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>canaleConfigurazione</var>
	 * @param canaleConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CanaleConfigurazione canaleConfigurazione) throws SerializerException {
		this.objToXml(fileName, CanaleConfigurazione.class, canaleConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>canaleConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>canaleConfigurazione</var>
	 * @param canaleConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CanaleConfigurazione canaleConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CanaleConfigurazione.class, canaleConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>canaleConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>canaleConfigurazione</var>
	 * @param canaleConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CanaleConfigurazione canaleConfigurazione) throws SerializerException {
		this.objToXml(file, CanaleConfigurazione.class, canaleConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>canaleConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>canaleConfigurazione</var>
	 * @param canaleConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CanaleConfigurazione canaleConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CanaleConfigurazione.class, canaleConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>canaleConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>canaleConfigurazione</var>
	 * @param canaleConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CanaleConfigurazione canaleConfigurazione) throws SerializerException {
		this.objToXml(out, CanaleConfigurazione.class, canaleConfigurazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>canaleConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>canaleConfigurazione</var>
	 * @param canaleConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CanaleConfigurazione canaleConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CanaleConfigurazione.class, canaleConfigurazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>canaleConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazione}
	 * 
	 * @param canaleConfigurazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CanaleConfigurazione canaleConfigurazione) throws SerializerException {
		return this.objToXml(CanaleConfigurazione.class, canaleConfigurazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>canaleConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazione}
	 * 
	 * @param canaleConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CanaleConfigurazione canaleConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CanaleConfigurazione.class, canaleConfigurazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>canaleConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazione}
	 * 
	 * @param canaleConfigurazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CanaleConfigurazione canaleConfigurazione) throws SerializerException {
		return this.objToXml(CanaleConfigurazione.class, canaleConfigurazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>canaleConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazione}
	 * 
	 * @param canaleConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CanaleConfigurazione canaleConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CanaleConfigurazione.class, canaleConfigurazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: cors-configurazione-headers
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>corsConfigurazioneHeaders</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneHeaders}
	 * 
	 * @param fileName Xml file to serialize the object <var>corsConfigurazioneHeaders</var>
	 * @param corsConfigurazioneHeaders Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorsConfigurazioneHeaders corsConfigurazioneHeaders) throws SerializerException {
		this.objToXml(fileName, CorsConfigurazioneHeaders.class, corsConfigurazioneHeaders, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>corsConfigurazioneHeaders</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneHeaders}
	 * 
	 * @param fileName Xml file to serialize the object <var>corsConfigurazioneHeaders</var>
	 * @param corsConfigurazioneHeaders Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorsConfigurazioneHeaders corsConfigurazioneHeaders,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CorsConfigurazioneHeaders.class, corsConfigurazioneHeaders, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>corsConfigurazioneHeaders</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneHeaders}
	 * 
	 * @param file Xml file to serialize the object <var>corsConfigurazioneHeaders</var>
	 * @param corsConfigurazioneHeaders Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorsConfigurazioneHeaders corsConfigurazioneHeaders) throws SerializerException {
		this.objToXml(file, CorsConfigurazioneHeaders.class, corsConfigurazioneHeaders, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>corsConfigurazioneHeaders</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneHeaders}
	 * 
	 * @param file Xml file to serialize the object <var>corsConfigurazioneHeaders</var>
	 * @param corsConfigurazioneHeaders Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorsConfigurazioneHeaders corsConfigurazioneHeaders,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CorsConfigurazioneHeaders.class, corsConfigurazioneHeaders, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>corsConfigurazioneHeaders</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneHeaders}
	 * 
	 * @param out OutputStream to serialize the object <var>corsConfigurazioneHeaders</var>
	 * @param corsConfigurazioneHeaders Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorsConfigurazioneHeaders corsConfigurazioneHeaders) throws SerializerException {
		this.objToXml(out, CorsConfigurazioneHeaders.class, corsConfigurazioneHeaders, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>corsConfigurazioneHeaders</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneHeaders}
	 * 
	 * @param out OutputStream to serialize the object <var>corsConfigurazioneHeaders</var>
	 * @param corsConfigurazioneHeaders Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorsConfigurazioneHeaders corsConfigurazioneHeaders,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CorsConfigurazioneHeaders.class, corsConfigurazioneHeaders, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>corsConfigurazioneHeaders</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneHeaders}
	 * 
	 * @param corsConfigurazioneHeaders Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorsConfigurazioneHeaders corsConfigurazioneHeaders) throws SerializerException {
		return this.objToXml(CorsConfigurazioneHeaders.class, corsConfigurazioneHeaders, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>corsConfigurazioneHeaders</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneHeaders}
	 * 
	 * @param corsConfigurazioneHeaders Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorsConfigurazioneHeaders corsConfigurazioneHeaders,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorsConfigurazioneHeaders.class, corsConfigurazioneHeaders, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>corsConfigurazioneHeaders</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneHeaders}
	 * 
	 * @param corsConfigurazioneHeaders Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorsConfigurazioneHeaders corsConfigurazioneHeaders) throws SerializerException {
		return this.objToXml(CorsConfigurazioneHeaders.class, corsConfigurazioneHeaders, false).toString();
	}
	/**
	 * Serialize to String the object <var>corsConfigurazioneHeaders</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneHeaders}
	 * 
	 * @param corsConfigurazioneHeaders Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorsConfigurazioneHeaders corsConfigurazioneHeaders,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorsConfigurazioneHeaders.class, corsConfigurazioneHeaders, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: canali-configurazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>canaliConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaliConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>canaliConfigurazione</var>
	 * @param canaliConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CanaliConfigurazione canaliConfigurazione) throws SerializerException {
		this.objToXml(fileName, CanaliConfigurazione.class, canaliConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>canaliConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaliConfigurazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>canaliConfigurazione</var>
	 * @param canaliConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CanaliConfigurazione canaliConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CanaliConfigurazione.class, canaliConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>canaliConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaliConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>canaliConfigurazione</var>
	 * @param canaliConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CanaliConfigurazione canaliConfigurazione) throws SerializerException {
		this.objToXml(file, CanaliConfigurazione.class, canaliConfigurazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>canaliConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaliConfigurazione}
	 * 
	 * @param file Xml file to serialize the object <var>canaliConfigurazione</var>
	 * @param canaliConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CanaliConfigurazione canaliConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CanaliConfigurazione.class, canaliConfigurazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>canaliConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaliConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>canaliConfigurazione</var>
	 * @param canaliConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CanaliConfigurazione canaliConfigurazione) throws SerializerException {
		this.objToXml(out, CanaliConfigurazione.class, canaliConfigurazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>canaliConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaliConfigurazione}
	 * 
	 * @param out OutputStream to serialize the object <var>canaliConfigurazione</var>
	 * @param canaliConfigurazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CanaliConfigurazione canaliConfigurazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CanaliConfigurazione.class, canaliConfigurazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>canaliConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaliConfigurazione}
	 * 
	 * @param canaliConfigurazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CanaliConfigurazione canaliConfigurazione) throws SerializerException {
		return this.objToXml(CanaliConfigurazione.class, canaliConfigurazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>canaliConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaliConfigurazione}
	 * 
	 * @param canaliConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CanaliConfigurazione canaliConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CanaliConfigurazione.class, canaliConfigurazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>canaliConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaliConfigurazione}
	 * 
	 * @param canaliConfigurazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CanaliConfigurazione canaliConfigurazione) throws SerializerException {
		return this.objToXml(CanaliConfigurazione.class, canaliConfigurazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>canaliConfigurazione</var> of type {@link org.openspcoop2.core.config.CanaliConfigurazione}
	 * 
	 * @param canaliConfigurazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CanaliConfigurazione canaliConfigurazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CanaliConfigurazione.class, canaliConfigurazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: canale-configurazione-nodo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>canaleConfigurazioneNodo</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazioneNodo}
	 * 
	 * @param fileName Xml file to serialize the object <var>canaleConfigurazioneNodo</var>
	 * @param canaleConfigurazioneNodo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CanaleConfigurazioneNodo canaleConfigurazioneNodo) throws SerializerException {
		this.objToXml(fileName, CanaleConfigurazioneNodo.class, canaleConfigurazioneNodo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>canaleConfigurazioneNodo</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazioneNodo}
	 * 
	 * @param fileName Xml file to serialize the object <var>canaleConfigurazioneNodo</var>
	 * @param canaleConfigurazioneNodo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CanaleConfigurazioneNodo canaleConfigurazioneNodo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CanaleConfigurazioneNodo.class, canaleConfigurazioneNodo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>canaleConfigurazioneNodo</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazioneNodo}
	 * 
	 * @param file Xml file to serialize the object <var>canaleConfigurazioneNodo</var>
	 * @param canaleConfigurazioneNodo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CanaleConfigurazioneNodo canaleConfigurazioneNodo) throws SerializerException {
		this.objToXml(file, CanaleConfigurazioneNodo.class, canaleConfigurazioneNodo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>canaleConfigurazioneNodo</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazioneNodo}
	 * 
	 * @param file Xml file to serialize the object <var>canaleConfigurazioneNodo</var>
	 * @param canaleConfigurazioneNodo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CanaleConfigurazioneNodo canaleConfigurazioneNodo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CanaleConfigurazioneNodo.class, canaleConfigurazioneNodo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>canaleConfigurazioneNodo</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazioneNodo}
	 * 
	 * @param out OutputStream to serialize the object <var>canaleConfigurazioneNodo</var>
	 * @param canaleConfigurazioneNodo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CanaleConfigurazioneNodo canaleConfigurazioneNodo) throws SerializerException {
		this.objToXml(out, CanaleConfigurazioneNodo.class, canaleConfigurazioneNodo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>canaleConfigurazioneNodo</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazioneNodo}
	 * 
	 * @param out OutputStream to serialize the object <var>canaleConfigurazioneNodo</var>
	 * @param canaleConfigurazioneNodo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CanaleConfigurazioneNodo canaleConfigurazioneNodo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CanaleConfigurazioneNodo.class, canaleConfigurazioneNodo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>canaleConfigurazioneNodo</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazioneNodo}
	 * 
	 * @param canaleConfigurazioneNodo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CanaleConfigurazioneNodo canaleConfigurazioneNodo) throws SerializerException {
		return this.objToXml(CanaleConfigurazioneNodo.class, canaleConfigurazioneNodo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>canaleConfigurazioneNodo</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazioneNodo}
	 * 
	 * @param canaleConfigurazioneNodo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CanaleConfigurazioneNodo canaleConfigurazioneNodo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CanaleConfigurazioneNodo.class, canaleConfigurazioneNodo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>canaleConfigurazioneNodo</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazioneNodo}
	 * 
	 * @param canaleConfigurazioneNodo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CanaleConfigurazioneNodo canaleConfigurazioneNodo) throws SerializerException {
		return this.objToXml(CanaleConfigurazioneNodo.class, canaleConfigurazioneNodo, false).toString();
	}
	/**
	 * Serialize to String the object <var>canaleConfigurazioneNodo</var> of type {@link org.openspcoop2.core.config.CanaleConfigurazioneNodo}
	 * 
	 * @param canaleConfigurazioneNodo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CanaleConfigurazioneNodo canaleConfigurazioneNodo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CanaleConfigurazioneNodo.class, canaleConfigurazioneNodo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazione-regola-applicabilita-richiesta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaApplicabilitaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaRichiesta</var>
	 * @param trasformazioneRegolaApplicabilitaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaApplicabilitaRichiesta trasformazioneRegolaApplicabilitaRichiesta) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaApplicabilitaRichiesta.class, trasformazioneRegolaApplicabilitaRichiesta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaApplicabilitaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaRichiesta</var>
	 * @param trasformazioneRegolaApplicabilitaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaApplicabilitaRichiesta trasformazioneRegolaApplicabilitaRichiesta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaApplicabilitaRichiesta.class, trasformazioneRegolaApplicabilitaRichiesta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaApplicabilitaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaRichiesta</var>
	 * @param trasformazioneRegolaApplicabilitaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaApplicabilitaRichiesta trasformazioneRegolaApplicabilitaRichiesta) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaApplicabilitaRichiesta.class, trasformazioneRegolaApplicabilitaRichiesta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaApplicabilitaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaRichiesta</var>
	 * @param trasformazioneRegolaApplicabilitaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaApplicabilitaRichiesta trasformazioneRegolaApplicabilitaRichiesta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaApplicabilitaRichiesta.class, trasformazioneRegolaApplicabilitaRichiesta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaApplicabilitaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaApplicabilitaRichiesta</var>
	 * @param trasformazioneRegolaApplicabilitaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaApplicabilitaRichiesta trasformazioneRegolaApplicabilitaRichiesta) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaApplicabilitaRichiesta.class, trasformazioneRegolaApplicabilitaRichiesta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaApplicabilitaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaApplicabilitaRichiesta</var>
	 * @param trasformazioneRegolaApplicabilitaRichiesta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaApplicabilitaRichiesta trasformazioneRegolaApplicabilitaRichiesta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaApplicabilitaRichiesta.class, trasformazioneRegolaApplicabilitaRichiesta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaApplicabilitaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta}
	 * 
	 * @param trasformazioneRegolaApplicabilitaRichiesta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaApplicabilitaRichiesta trasformazioneRegolaApplicabilitaRichiesta) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaRichiesta.class, trasformazioneRegolaApplicabilitaRichiesta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaApplicabilitaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta}
	 * 
	 * @param trasformazioneRegolaApplicabilitaRichiesta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaApplicabilitaRichiesta trasformazioneRegolaApplicabilitaRichiesta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaRichiesta.class, trasformazioneRegolaApplicabilitaRichiesta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneRegolaApplicabilitaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta}
	 * 
	 * @param trasformazioneRegolaApplicabilitaRichiesta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaApplicabilitaRichiesta trasformazioneRegolaApplicabilitaRichiesta) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaRichiesta.class, trasformazioneRegolaApplicabilitaRichiesta, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneRegolaApplicabilitaRichiesta</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta}
	 * 
	 * @param trasformazioneRegolaApplicabilitaRichiesta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaApplicabilitaRichiesta trasformazioneRegolaApplicabilitaRichiesta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaRichiesta.class, trasformazioneRegolaApplicabilitaRichiesta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazione-regola-applicabilita-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaApplicabilitaSoggetto</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaSoggetto</var>
	 * @param trasformazioneRegolaApplicabilitaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaApplicabilitaSoggetto.class, trasformazioneRegolaApplicabilitaSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaApplicabilitaSoggetto</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaSoggetto</var>
	 * @param trasformazioneRegolaApplicabilitaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaApplicabilitaSoggetto.class, trasformazioneRegolaApplicabilitaSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaApplicabilitaSoggetto</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaSoggetto</var>
	 * @param trasformazioneRegolaApplicabilitaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaApplicabilitaSoggetto.class, trasformazioneRegolaApplicabilitaSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaApplicabilitaSoggetto</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaSoggetto</var>
	 * @param trasformazioneRegolaApplicabilitaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaApplicabilitaSoggetto.class, trasformazioneRegolaApplicabilitaSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaApplicabilitaSoggetto</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaApplicabilitaSoggetto</var>
	 * @param trasformazioneRegolaApplicabilitaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaApplicabilitaSoggetto.class, trasformazioneRegolaApplicabilitaSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaApplicabilitaSoggetto</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaApplicabilitaSoggetto</var>
	 * @param trasformazioneRegolaApplicabilitaSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaApplicabilitaSoggetto.class, trasformazioneRegolaApplicabilitaSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaApplicabilitaSoggetto</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto}
	 * 
	 * @param trasformazioneRegolaApplicabilitaSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaSoggetto.class, trasformazioneRegolaApplicabilitaSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaApplicabilitaSoggetto</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto}
	 * 
	 * @param trasformazioneRegolaApplicabilitaSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaSoggetto.class, trasformazioneRegolaApplicabilitaSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneRegolaApplicabilitaSoggetto</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto}
	 * 
	 * @param trasformazioneRegolaApplicabilitaSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaSoggetto.class, trasformazioneRegolaApplicabilitaSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneRegolaApplicabilitaSoggetto</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto}
	 * 
	 * @param trasformazioneRegolaApplicabilitaSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaSoggetto.class, trasformazioneRegolaApplicabilitaSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: trasformazione-regola-applicabilita-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var>
	 * @param trasformazioneRegolaApplicabilitaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaServizioApplicativo) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaApplicabilitaServizioApplicativo.class, trasformazioneRegolaApplicabilitaServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var>
	 * @param trasformazioneRegolaApplicabilitaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegolaApplicabilitaServizioApplicativo.class, trasformazioneRegolaApplicabilitaServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var>
	 * @param trasformazioneRegolaApplicabilitaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaServizioApplicativo) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaApplicabilitaServizioApplicativo.class, trasformazioneRegolaApplicabilitaServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var>
	 * @param trasformazioneRegolaApplicabilitaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneRegolaApplicabilitaServizioApplicativo.class, trasformazioneRegolaApplicabilitaServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var>
	 * @param trasformazioneRegolaApplicabilitaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaServizioApplicativo) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaApplicabilitaServizioApplicativo.class, trasformazioneRegolaApplicabilitaServizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var>
	 * @param trasformazioneRegolaApplicabilitaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneRegolaApplicabilitaServizioApplicativo.class, trasformazioneRegolaApplicabilitaServizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo}
	 * 
	 * @param trasformazioneRegolaApplicabilitaServizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaServizioApplicativo) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaServizioApplicativo.class, trasformazioneRegolaApplicabilitaServizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo}
	 * 
	 * @param trasformazioneRegolaApplicabilitaServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaServizioApplicativo.class, trasformazioneRegolaApplicabilitaServizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo}
	 * 
	 * @param trasformazioneRegolaApplicabilitaServizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaServizioApplicativo) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaServizioApplicativo.class, trasformazioneRegolaApplicabilitaServizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneRegolaApplicabilitaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo}
	 * 
	 * @param trasformazioneRegolaApplicabilitaServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegolaApplicabilitaServizioApplicativo.class, trasformazioneRegolaApplicabilitaServizioApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) throws SerializerException {
		this.objToXml(file, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) throws SerializerException {
		this.objToXml(out, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaServizioApplicativo</var>
	 * @param portaApplicativaServizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativo}
	 * 
	 * @param portaApplicativaServizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativo}
	 * 
	 * @param portaApplicativaServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativo}
	 * 
	 * @param portaApplicativaServizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaServizioApplicativo</var> of type {@link org.openspcoop2.core.config.PortaApplicativaServizioApplicativo}
	 * 
	 * @param portaApplicativaServizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaServizioApplicativo.class, portaApplicativaServizioApplicativo, prettyPrint).toString();
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
	 Object: trasformazione-regola
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegola</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegola}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegola</var>
	 * @param trasformazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegola trasformazioneRegola) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegola.class, trasformazioneRegola, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>trasformazioneRegola</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegola}
	 * 
	 * @param fileName Xml file to serialize the object <var>trasformazioneRegola</var>
	 * @param trasformazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TrasformazioneRegola trasformazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TrasformazioneRegola.class, trasformazioneRegola, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegola</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegola}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegola</var>
	 * @param trasformazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegola trasformazioneRegola) throws SerializerException {
		this.objToXml(file, TrasformazioneRegola.class, trasformazioneRegola, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>trasformazioneRegola</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegola}
	 * 
	 * @param file Xml file to serialize the object <var>trasformazioneRegola</var>
	 * @param trasformazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TrasformazioneRegola trasformazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TrasformazioneRegola.class, trasformazioneRegola, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegola</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegola}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegola</var>
	 * @param trasformazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegola trasformazioneRegola) throws SerializerException {
		this.objToXml(out, TrasformazioneRegola.class, trasformazioneRegola, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>trasformazioneRegola</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegola}
	 * 
	 * @param out OutputStream to serialize the object <var>trasformazioneRegola</var>
	 * @param trasformazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TrasformazioneRegola trasformazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TrasformazioneRegola.class, trasformazioneRegola, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>trasformazioneRegola</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegola}
	 * 
	 * @param trasformazioneRegola Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegola trasformazioneRegola) throws SerializerException {
		return this.objToXml(TrasformazioneRegola.class, trasformazioneRegola, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>trasformazioneRegola</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegola}
	 * 
	 * @param trasformazioneRegola Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TrasformazioneRegola trasformazioneRegola,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegola.class, trasformazioneRegola, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>trasformazioneRegola</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegola}
	 * 
	 * @param trasformazioneRegola Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegola trasformazioneRegola) throws SerializerException {
		return this.objToXml(TrasformazioneRegola.class, trasformazioneRegola, false).toString();
	}
	/**
	 * Serialize to String the object <var>trasformazioneRegola</var> of type {@link org.openspcoop2.core.config.TrasformazioneRegola}
	 * 
	 * @param trasformazioneRegola Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TrasformazioneRegola trasformazioneRegola,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TrasformazioneRegola.class, trasformazioneRegola, prettyPrint).toString();
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
	 Object: cors-configurazione-origin
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>corsConfigurazioneOrigin</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneOrigin}
	 * 
	 * @param fileName Xml file to serialize the object <var>corsConfigurazioneOrigin</var>
	 * @param corsConfigurazioneOrigin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorsConfigurazioneOrigin corsConfigurazioneOrigin) throws SerializerException {
		this.objToXml(fileName, CorsConfigurazioneOrigin.class, corsConfigurazioneOrigin, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>corsConfigurazioneOrigin</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneOrigin}
	 * 
	 * @param fileName Xml file to serialize the object <var>corsConfigurazioneOrigin</var>
	 * @param corsConfigurazioneOrigin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorsConfigurazioneOrigin corsConfigurazioneOrigin,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CorsConfigurazioneOrigin.class, corsConfigurazioneOrigin, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>corsConfigurazioneOrigin</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneOrigin}
	 * 
	 * @param file Xml file to serialize the object <var>corsConfigurazioneOrigin</var>
	 * @param corsConfigurazioneOrigin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorsConfigurazioneOrigin corsConfigurazioneOrigin) throws SerializerException {
		this.objToXml(file, CorsConfigurazioneOrigin.class, corsConfigurazioneOrigin, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>corsConfigurazioneOrigin</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneOrigin}
	 * 
	 * @param file Xml file to serialize the object <var>corsConfigurazioneOrigin</var>
	 * @param corsConfigurazioneOrigin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorsConfigurazioneOrigin corsConfigurazioneOrigin,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CorsConfigurazioneOrigin.class, corsConfigurazioneOrigin, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>corsConfigurazioneOrigin</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneOrigin}
	 * 
	 * @param out OutputStream to serialize the object <var>corsConfigurazioneOrigin</var>
	 * @param corsConfigurazioneOrigin Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorsConfigurazioneOrigin corsConfigurazioneOrigin) throws SerializerException {
		this.objToXml(out, CorsConfigurazioneOrigin.class, corsConfigurazioneOrigin, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>corsConfigurazioneOrigin</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneOrigin}
	 * 
	 * @param out OutputStream to serialize the object <var>corsConfigurazioneOrigin</var>
	 * @param corsConfigurazioneOrigin Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorsConfigurazioneOrigin corsConfigurazioneOrigin,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CorsConfigurazioneOrigin.class, corsConfigurazioneOrigin, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>corsConfigurazioneOrigin</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneOrigin}
	 * 
	 * @param corsConfigurazioneOrigin Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorsConfigurazioneOrigin corsConfigurazioneOrigin) throws SerializerException {
		return this.objToXml(CorsConfigurazioneOrigin.class, corsConfigurazioneOrigin, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>corsConfigurazioneOrigin</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneOrigin}
	 * 
	 * @param corsConfigurazioneOrigin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorsConfigurazioneOrigin corsConfigurazioneOrigin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorsConfigurazioneOrigin.class, corsConfigurazioneOrigin, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>corsConfigurazioneOrigin</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneOrigin}
	 * 
	 * @param corsConfigurazioneOrigin Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorsConfigurazioneOrigin corsConfigurazioneOrigin) throws SerializerException {
		return this.objToXml(CorsConfigurazioneOrigin.class, corsConfigurazioneOrigin, false).toString();
	}
	/**
	 * Serialize to String the object <var>corsConfigurazioneOrigin</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneOrigin}
	 * 
	 * @param corsConfigurazioneOrigin Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorsConfigurazioneOrigin corsConfigurazioneOrigin,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorsConfigurazioneOrigin.class, corsConfigurazioneOrigin, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: cors-configurazione-methods
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>corsConfigurazioneMethods</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneMethods}
	 * 
	 * @param fileName Xml file to serialize the object <var>corsConfigurazioneMethods</var>
	 * @param corsConfigurazioneMethods Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorsConfigurazioneMethods corsConfigurazioneMethods) throws SerializerException {
		this.objToXml(fileName, CorsConfigurazioneMethods.class, corsConfigurazioneMethods, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>corsConfigurazioneMethods</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneMethods}
	 * 
	 * @param fileName Xml file to serialize the object <var>corsConfigurazioneMethods</var>
	 * @param corsConfigurazioneMethods Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CorsConfigurazioneMethods corsConfigurazioneMethods,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CorsConfigurazioneMethods.class, corsConfigurazioneMethods, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>corsConfigurazioneMethods</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneMethods}
	 * 
	 * @param file Xml file to serialize the object <var>corsConfigurazioneMethods</var>
	 * @param corsConfigurazioneMethods Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorsConfigurazioneMethods corsConfigurazioneMethods) throws SerializerException {
		this.objToXml(file, CorsConfigurazioneMethods.class, corsConfigurazioneMethods, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>corsConfigurazioneMethods</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneMethods}
	 * 
	 * @param file Xml file to serialize the object <var>corsConfigurazioneMethods</var>
	 * @param corsConfigurazioneMethods Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CorsConfigurazioneMethods corsConfigurazioneMethods,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CorsConfigurazioneMethods.class, corsConfigurazioneMethods, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>corsConfigurazioneMethods</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneMethods}
	 * 
	 * @param out OutputStream to serialize the object <var>corsConfigurazioneMethods</var>
	 * @param corsConfigurazioneMethods Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorsConfigurazioneMethods corsConfigurazioneMethods) throws SerializerException {
		this.objToXml(out, CorsConfigurazioneMethods.class, corsConfigurazioneMethods, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>corsConfigurazioneMethods</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneMethods}
	 * 
	 * @param out OutputStream to serialize the object <var>corsConfigurazioneMethods</var>
	 * @param corsConfigurazioneMethods Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CorsConfigurazioneMethods corsConfigurazioneMethods,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CorsConfigurazioneMethods.class, corsConfigurazioneMethods, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>corsConfigurazioneMethods</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneMethods}
	 * 
	 * @param corsConfigurazioneMethods Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorsConfigurazioneMethods corsConfigurazioneMethods) throws SerializerException {
		return this.objToXml(CorsConfigurazioneMethods.class, corsConfigurazioneMethods, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>corsConfigurazioneMethods</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneMethods}
	 * 
	 * @param corsConfigurazioneMethods Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CorsConfigurazioneMethods corsConfigurazioneMethods,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorsConfigurazioneMethods.class, corsConfigurazioneMethods, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>corsConfigurazioneMethods</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneMethods}
	 * 
	 * @param corsConfigurazioneMethods Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorsConfigurazioneMethods corsConfigurazioneMethods) throws SerializerException {
		return this.objToXml(CorsConfigurazioneMethods.class, corsConfigurazioneMethods, false).toString();
	}
	/**
	 * Serialize to String the object <var>corsConfigurazioneMethods</var> of type {@link org.openspcoop2.core.config.CorsConfigurazioneMethods}
	 * 
	 * @param corsConfigurazioneMethods Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CorsConfigurazioneMethods corsConfigurazioneMethods,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CorsConfigurazioneMethods.class, corsConfigurazioneMethods, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: scope
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>scope</var> of type {@link org.openspcoop2.core.config.Scope}
	 * 
	 * @param fileName Xml file to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Scope scope) throws SerializerException {
		this.objToXml(fileName, Scope.class, scope, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>scope</var> of type {@link org.openspcoop2.core.config.Scope}
	 * 
	 * @param fileName Xml file to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Scope scope,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Scope.class, scope, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>scope</var> of type {@link org.openspcoop2.core.config.Scope}
	 * 
	 * @param file Xml file to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Scope scope) throws SerializerException {
		this.objToXml(file, Scope.class, scope, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>scope</var> of type {@link org.openspcoop2.core.config.Scope}
	 * 
	 * @param file Xml file to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Scope scope,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Scope.class, scope, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>scope</var> of type {@link org.openspcoop2.core.config.Scope}
	 * 
	 * @param out OutputStream to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Scope scope) throws SerializerException {
		this.objToXml(out, Scope.class, scope, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>scope</var> of type {@link org.openspcoop2.core.config.Scope}
	 * 
	 * @param out OutputStream to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Scope scope,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Scope.class, scope, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>scope</var> of type {@link org.openspcoop2.core.config.Scope}
	 * 
	 * @param scope Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Scope scope) throws SerializerException {
		return this.objToXml(Scope.class, scope, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>scope</var> of type {@link org.openspcoop2.core.config.Scope}
	 * 
	 * @param scope Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Scope scope,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Scope.class, scope, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>scope</var> of type {@link org.openspcoop2.core.config.Scope}
	 * 
	 * @param scope Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Scope scope) throws SerializerException {
		return this.objToXml(Scope.class, scope, false).toString();
	}
	/**
	 * Serialize to String the object <var>scope</var> of type {@link org.openspcoop2.core.config.Scope}
	 * 
	 * @param scope Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Scope scope,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Scope.class, scope, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accesso-dati-consegna-applicativi
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoDatiConsegnaApplicativi</var> of type {@link org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoDatiConsegnaApplicativi</var>
	 * @param accessoDatiConsegnaApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi) throws SerializerException {
		this.objToXml(fileName, AccessoDatiConsegnaApplicativi.class, accessoDatiConsegnaApplicativi, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoDatiConsegnaApplicativi</var> of type {@link org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoDatiConsegnaApplicativi</var>
	 * @param accessoDatiConsegnaApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccessoDatiConsegnaApplicativi.class, accessoDatiConsegnaApplicativi, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoDatiConsegnaApplicativi</var> of type {@link org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi}
	 * 
	 * @param file Xml file to serialize the object <var>accessoDatiConsegnaApplicativi</var>
	 * @param accessoDatiConsegnaApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi) throws SerializerException {
		this.objToXml(file, AccessoDatiConsegnaApplicativi.class, accessoDatiConsegnaApplicativi, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoDatiConsegnaApplicativi</var> of type {@link org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi}
	 * 
	 * @param file Xml file to serialize the object <var>accessoDatiConsegnaApplicativi</var>
	 * @param accessoDatiConsegnaApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccessoDatiConsegnaApplicativi.class, accessoDatiConsegnaApplicativi, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoDatiConsegnaApplicativi</var> of type {@link org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoDatiConsegnaApplicativi</var>
	 * @param accessoDatiConsegnaApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi) throws SerializerException {
		this.objToXml(out, AccessoDatiConsegnaApplicativi.class, accessoDatiConsegnaApplicativi, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoDatiConsegnaApplicativi</var> of type {@link org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoDatiConsegnaApplicativi</var>
	 * @param accessoDatiConsegnaApplicativi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccessoDatiConsegnaApplicativi.class, accessoDatiConsegnaApplicativi, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accessoDatiConsegnaApplicativi</var> of type {@link org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi}
	 * 
	 * @param accessoDatiConsegnaApplicativi Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi) throws SerializerException {
		return this.objToXml(AccessoDatiConsegnaApplicativi.class, accessoDatiConsegnaApplicativi, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accessoDatiConsegnaApplicativi</var> of type {@link org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi}
	 * 
	 * @param accessoDatiConsegnaApplicativi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoDatiConsegnaApplicativi.class, accessoDatiConsegnaApplicativi, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accessoDatiConsegnaApplicativi</var> of type {@link org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi}
	 * 
	 * @param accessoDatiConsegnaApplicativi Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi) throws SerializerException {
		return this.objToXml(AccessoDatiConsegnaApplicativi.class, accessoDatiConsegnaApplicativi, false).toString();
	}
	/**
	 * Serialize to String the object <var>accessoDatiConsegnaApplicativi</var> of type {@link org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi}
	 * 
	 * @param accessoDatiConsegnaApplicativi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoDatiConsegnaApplicativi.class, accessoDatiConsegnaApplicativi, prettyPrint).toString();
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
	 Object: response-caching-configurazione-control
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responseCachingConfigurazioneControl</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneControl}
	 * 
	 * @param fileName Xml file to serialize the object <var>responseCachingConfigurazioneControl</var>
	 * @param responseCachingConfigurazioneControl Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponseCachingConfigurazioneControl responseCachingConfigurazioneControl) throws SerializerException {
		this.objToXml(fileName, ResponseCachingConfigurazioneControl.class, responseCachingConfigurazioneControl, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responseCachingConfigurazioneControl</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneControl}
	 * 
	 * @param fileName Xml file to serialize the object <var>responseCachingConfigurazioneControl</var>
	 * @param responseCachingConfigurazioneControl Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponseCachingConfigurazioneControl responseCachingConfigurazioneControl,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResponseCachingConfigurazioneControl.class, responseCachingConfigurazioneControl, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>responseCachingConfigurazioneControl</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneControl}
	 * 
	 * @param file Xml file to serialize the object <var>responseCachingConfigurazioneControl</var>
	 * @param responseCachingConfigurazioneControl Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponseCachingConfigurazioneControl responseCachingConfigurazioneControl) throws SerializerException {
		this.objToXml(file, ResponseCachingConfigurazioneControl.class, responseCachingConfigurazioneControl, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>responseCachingConfigurazioneControl</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneControl}
	 * 
	 * @param file Xml file to serialize the object <var>responseCachingConfigurazioneControl</var>
	 * @param responseCachingConfigurazioneControl Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponseCachingConfigurazioneControl responseCachingConfigurazioneControl,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResponseCachingConfigurazioneControl.class, responseCachingConfigurazioneControl, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>responseCachingConfigurazioneControl</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneControl}
	 * 
	 * @param out OutputStream to serialize the object <var>responseCachingConfigurazioneControl</var>
	 * @param responseCachingConfigurazioneControl Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponseCachingConfigurazioneControl responseCachingConfigurazioneControl) throws SerializerException {
		this.objToXml(out, ResponseCachingConfigurazioneControl.class, responseCachingConfigurazioneControl, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>responseCachingConfigurazioneControl</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneControl}
	 * 
	 * @param out OutputStream to serialize the object <var>responseCachingConfigurazioneControl</var>
	 * @param responseCachingConfigurazioneControl Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponseCachingConfigurazioneControl responseCachingConfigurazioneControl,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResponseCachingConfigurazioneControl.class, responseCachingConfigurazioneControl, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>responseCachingConfigurazioneControl</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneControl}
	 * 
	 * @param responseCachingConfigurazioneControl Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponseCachingConfigurazioneControl responseCachingConfigurazioneControl) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneControl.class, responseCachingConfigurazioneControl, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>responseCachingConfigurazioneControl</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneControl}
	 * 
	 * @param responseCachingConfigurazioneControl Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponseCachingConfigurazioneControl responseCachingConfigurazioneControl,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneControl.class, responseCachingConfigurazioneControl, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>responseCachingConfigurazioneControl</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneControl}
	 * 
	 * @param responseCachingConfigurazioneControl Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponseCachingConfigurazioneControl responseCachingConfigurazioneControl) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneControl.class, responseCachingConfigurazioneControl, false).toString();
	}
	/**
	 * Serialize to String the object <var>responseCachingConfigurazioneControl</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneControl}
	 * 
	 * @param responseCachingConfigurazioneControl Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponseCachingConfigurazioneControl responseCachingConfigurazioneControl,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneControl.class, responseCachingConfigurazioneControl, prettyPrint).toString();
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
	 Object: porta-applicativa-behaviour
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaBehaviour</var> of type {@link org.openspcoop2.core.config.PortaApplicativaBehaviour}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaBehaviour</var>
	 * @param portaApplicativaBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaBehaviour portaApplicativaBehaviour) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaBehaviour.class, portaApplicativaBehaviour, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaApplicativaBehaviour</var> of type {@link org.openspcoop2.core.config.PortaApplicativaBehaviour}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaApplicativaBehaviour</var>
	 * @param portaApplicativaBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaApplicativaBehaviour portaApplicativaBehaviour,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaApplicativaBehaviour.class, portaApplicativaBehaviour, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaBehaviour</var> of type {@link org.openspcoop2.core.config.PortaApplicativaBehaviour}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaBehaviour</var>
	 * @param portaApplicativaBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaBehaviour portaApplicativaBehaviour) throws SerializerException {
		this.objToXml(file, PortaApplicativaBehaviour.class, portaApplicativaBehaviour, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaApplicativaBehaviour</var> of type {@link org.openspcoop2.core.config.PortaApplicativaBehaviour}
	 * 
	 * @param file Xml file to serialize the object <var>portaApplicativaBehaviour</var>
	 * @param portaApplicativaBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaApplicativaBehaviour portaApplicativaBehaviour,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaApplicativaBehaviour.class, portaApplicativaBehaviour, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaBehaviour</var> of type {@link org.openspcoop2.core.config.PortaApplicativaBehaviour}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaBehaviour</var>
	 * @param portaApplicativaBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaBehaviour portaApplicativaBehaviour) throws SerializerException {
		this.objToXml(out, PortaApplicativaBehaviour.class, portaApplicativaBehaviour, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaApplicativaBehaviour</var> of type {@link org.openspcoop2.core.config.PortaApplicativaBehaviour}
	 * 
	 * @param out OutputStream to serialize the object <var>portaApplicativaBehaviour</var>
	 * @param portaApplicativaBehaviour Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaApplicativaBehaviour portaApplicativaBehaviour,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaApplicativaBehaviour.class, portaApplicativaBehaviour, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaApplicativaBehaviour</var> of type {@link org.openspcoop2.core.config.PortaApplicativaBehaviour}
	 * 
	 * @param portaApplicativaBehaviour Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaBehaviour portaApplicativaBehaviour) throws SerializerException {
		return this.objToXml(PortaApplicativaBehaviour.class, portaApplicativaBehaviour, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaApplicativaBehaviour</var> of type {@link org.openspcoop2.core.config.PortaApplicativaBehaviour}
	 * 
	 * @param portaApplicativaBehaviour Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaApplicativaBehaviour portaApplicativaBehaviour,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaBehaviour.class, portaApplicativaBehaviour, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaApplicativaBehaviour</var> of type {@link org.openspcoop2.core.config.PortaApplicativaBehaviour}
	 * 
	 * @param portaApplicativaBehaviour Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaBehaviour portaApplicativaBehaviour) throws SerializerException {
		return this.objToXml(PortaApplicativaBehaviour.class, portaApplicativaBehaviour, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaApplicativaBehaviour</var> of type {@link org.openspcoop2.core.config.PortaApplicativaBehaviour}
	 * 
	 * @param portaApplicativaBehaviour Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaApplicativaBehaviour portaApplicativaBehaviour,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaApplicativaBehaviour.class, portaApplicativaBehaviour, prettyPrint).toString();
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
	 Object: accesso-dati-autenticazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoDatiAutenticazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutenticazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoDatiAutenticazione</var>
	 * @param accessoDatiAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoDatiAutenticazione accessoDatiAutenticazione) throws SerializerException {
		this.objToXml(fileName, AccessoDatiAutenticazione.class, accessoDatiAutenticazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accessoDatiAutenticazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutenticazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accessoDatiAutenticazione</var>
	 * @param accessoDatiAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccessoDatiAutenticazione accessoDatiAutenticazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccessoDatiAutenticazione.class, accessoDatiAutenticazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoDatiAutenticazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutenticazione}
	 * 
	 * @param file Xml file to serialize the object <var>accessoDatiAutenticazione</var>
	 * @param accessoDatiAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoDatiAutenticazione accessoDatiAutenticazione) throws SerializerException {
		this.objToXml(file, AccessoDatiAutenticazione.class, accessoDatiAutenticazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accessoDatiAutenticazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutenticazione}
	 * 
	 * @param file Xml file to serialize the object <var>accessoDatiAutenticazione</var>
	 * @param accessoDatiAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccessoDatiAutenticazione accessoDatiAutenticazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccessoDatiAutenticazione.class, accessoDatiAutenticazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoDatiAutenticazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutenticazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoDatiAutenticazione</var>
	 * @param accessoDatiAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoDatiAutenticazione accessoDatiAutenticazione) throws SerializerException {
		this.objToXml(out, AccessoDatiAutenticazione.class, accessoDatiAutenticazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accessoDatiAutenticazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutenticazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accessoDatiAutenticazione</var>
	 * @param accessoDatiAutenticazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccessoDatiAutenticazione accessoDatiAutenticazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccessoDatiAutenticazione.class, accessoDatiAutenticazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accessoDatiAutenticazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutenticazione}
	 * 
	 * @param accessoDatiAutenticazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoDatiAutenticazione accessoDatiAutenticazione) throws SerializerException {
		return this.objToXml(AccessoDatiAutenticazione.class, accessoDatiAutenticazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accessoDatiAutenticazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutenticazione}
	 * 
	 * @param accessoDatiAutenticazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccessoDatiAutenticazione accessoDatiAutenticazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoDatiAutenticazione.class, accessoDatiAutenticazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accessoDatiAutenticazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutenticazione}
	 * 
	 * @param accessoDatiAutenticazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoDatiAutenticazione accessoDatiAutenticazione) throws SerializerException {
		return this.objToXml(AccessoDatiAutenticazione.class, accessoDatiAutenticazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>accessoDatiAutenticazione</var> of type {@link org.openspcoop2.core.config.AccessoDatiAutenticazione}
	 * 
	 * @param accessoDatiAutenticazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccessoDatiAutenticazione accessoDatiAutenticazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccessoDatiAutenticazione.class, accessoDatiAutenticazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-multitenant
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneMultitenant</var> of type {@link org.openspcoop2.core.config.ConfigurazioneMultitenant}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneMultitenant</var>
	 * @param configurazioneMultitenant Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneMultitenant configurazioneMultitenant) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneMultitenant.class, configurazioneMultitenant, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneMultitenant</var> of type {@link org.openspcoop2.core.config.ConfigurazioneMultitenant}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneMultitenant</var>
	 * @param configurazioneMultitenant Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneMultitenant configurazioneMultitenant,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneMultitenant.class, configurazioneMultitenant, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneMultitenant</var> of type {@link org.openspcoop2.core.config.ConfigurazioneMultitenant}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneMultitenant</var>
	 * @param configurazioneMultitenant Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneMultitenant configurazioneMultitenant) throws SerializerException {
		this.objToXml(file, ConfigurazioneMultitenant.class, configurazioneMultitenant, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneMultitenant</var> of type {@link org.openspcoop2.core.config.ConfigurazioneMultitenant}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneMultitenant</var>
	 * @param configurazioneMultitenant Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneMultitenant configurazioneMultitenant,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneMultitenant.class, configurazioneMultitenant, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneMultitenant</var> of type {@link org.openspcoop2.core.config.ConfigurazioneMultitenant}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneMultitenant</var>
	 * @param configurazioneMultitenant Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneMultitenant configurazioneMultitenant) throws SerializerException {
		this.objToXml(out, ConfigurazioneMultitenant.class, configurazioneMultitenant, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneMultitenant</var> of type {@link org.openspcoop2.core.config.ConfigurazioneMultitenant}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneMultitenant</var>
	 * @param configurazioneMultitenant Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneMultitenant configurazioneMultitenant,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneMultitenant.class, configurazioneMultitenant, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneMultitenant</var> of type {@link org.openspcoop2.core.config.ConfigurazioneMultitenant}
	 * 
	 * @param configurazioneMultitenant Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneMultitenant configurazioneMultitenant) throws SerializerException {
		return this.objToXml(ConfigurazioneMultitenant.class, configurazioneMultitenant, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneMultitenant</var> of type {@link org.openspcoop2.core.config.ConfigurazioneMultitenant}
	 * 
	 * @param configurazioneMultitenant Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneMultitenant configurazioneMultitenant,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneMultitenant.class, configurazioneMultitenant, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneMultitenant</var> of type {@link org.openspcoop2.core.config.ConfigurazioneMultitenant}
	 * 
	 * @param configurazioneMultitenant Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneMultitenant configurazioneMultitenant) throws SerializerException {
		return this.objToXml(ConfigurazioneMultitenant.class, configurazioneMultitenant, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneMultitenant</var> of type {@link org.openspcoop2.core.config.ConfigurazioneMultitenant}
	 * 
	 * @param configurazioneMultitenant Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneMultitenant configurazioneMultitenant,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneMultitenant.class, configurazioneMultitenant, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-url-invocazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneUrlInvocazione</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneUrlInvocazione</var>
	 * @param configurazioneUrlInvocazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneUrlInvocazione configurazioneUrlInvocazione) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneUrlInvocazione.class, configurazioneUrlInvocazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneUrlInvocazione</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneUrlInvocazione</var>
	 * @param configurazioneUrlInvocazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneUrlInvocazione configurazioneUrlInvocazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneUrlInvocazione.class, configurazioneUrlInvocazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneUrlInvocazione</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneUrlInvocazione</var>
	 * @param configurazioneUrlInvocazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneUrlInvocazione configurazioneUrlInvocazione) throws SerializerException {
		this.objToXml(file, ConfigurazioneUrlInvocazione.class, configurazioneUrlInvocazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneUrlInvocazione</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneUrlInvocazione</var>
	 * @param configurazioneUrlInvocazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneUrlInvocazione configurazioneUrlInvocazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneUrlInvocazione.class, configurazioneUrlInvocazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneUrlInvocazione</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneUrlInvocazione</var>
	 * @param configurazioneUrlInvocazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneUrlInvocazione configurazioneUrlInvocazione) throws SerializerException {
		this.objToXml(out, ConfigurazioneUrlInvocazione.class, configurazioneUrlInvocazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneUrlInvocazione</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneUrlInvocazione</var>
	 * @param configurazioneUrlInvocazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneUrlInvocazione configurazioneUrlInvocazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneUrlInvocazione.class, configurazioneUrlInvocazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneUrlInvocazione</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazione}
	 * 
	 * @param configurazioneUrlInvocazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneUrlInvocazione configurazioneUrlInvocazione) throws SerializerException {
		return this.objToXml(ConfigurazioneUrlInvocazione.class, configurazioneUrlInvocazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneUrlInvocazione</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazione}
	 * 
	 * @param configurazioneUrlInvocazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneUrlInvocazione configurazioneUrlInvocazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneUrlInvocazione.class, configurazioneUrlInvocazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneUrlInvocazione</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazione}
	 * 
	 * @param configurazioneUrlInvocazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneUrlInvocazione configurazioneUrlInvocazione) throws SerializerException {
		return this.objToXml(ConfigurazioneUrlInvocazione.class, configurazioneUrlInvocazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneUrlInvocazione</var> of type {@link org.openspcoop2.core.config.ConfigurazioneUrlInvocazione}
	 * 
	 * @param configurazioneUrlInvocazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneUrlInvocazione configurazioneUrlInvocazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneUrlInvocazione.class, configurazioneUrlInvocazione, prettyPrint).toString();
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
	 Object: dump
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dump</var> of type {@link org.openspcoop2.core.config.Dump}
	 * 
	 * @param fileName Xml file to serialize the object <var>dump</var>
	 * @param dump Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Dump dump) throws SerializerException {
		this.objToXml(fileName, Dump.class, dump, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dump</var> of type {@link org.openspcoop2.core.config.Dump}
	 * 
	 * @param fileName Xml file to serialize the object <var>dump</var>
	 * @param dump Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Dump dump,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Dump.class, dump, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dump</var> of type {@link org.openspcoop2.core.config.Dump}
	 * 
	 * @param file Xml file to serialize the object <var>dump</var>
	 * @param dump Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Dump dump) throws SerializerException {
		this.objToXml(file, Dump.class, dump, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dump</var> of type {@link org.openspcoop2.core.config.Dump}
	 * 
	 * @param file Xml file to serialize the object <var>dump</var>
	 * @param dump Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Dump dump,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Dump.class, dump, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dump</var> of type {@link org.openspcoop2.core.config.Dump}
	 * 
	 * @param out OutputStream to serialize the object <var>dump</var>
	 * @param dump Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Dump dump) throws SerializerException {
		this.objToXml(out, Dump.class, dump, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dump</var> of type {@link org.openspcoop2.core.config.Dump}
	 * 
	 * @param out OutputStream to serialize the object <var>dump</var>
	 * @param dump Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Dump dump,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Dump.class, dump, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dump</var> of type {@link org.openspcoop2.core.config.Dump}
	 * 
	 * @param dump Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Dump dump) throws SerializerException {
		return this.objToXml(Dump.class, dump, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dump</var> of type {@link org.openspcoop2.core.config.Dump}
	 * 
	 * @param dump Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Dump dump,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Dump.class, dump, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dump</var> of type {@link org.openspcoop2.core.config.Dump}
	 * 
	 * @param dump Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Dump dump) throws SerializerException {
		return this.objToXml(Dump.class, dump, false).toString();
	}
	/**
	 * Serialize to String the object <var>dump</var> of type {@link org.openspcoop2.core.config.Dump}
	 * 
	 * @param dump Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Dump dump,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Dump.class, dump, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: transazioni
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transazioni</var> of type {@link org.openspcoop2.core.config.Transazioni}
	 * 
	 * @param fileName Xml file to serialize the object <var>transazioni</var>
	 * @param transazioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Transazioni transazioni) throws SerializerException {
		this.objToXml(fileName, Transazioni.class, transazioni, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transazioni</var> of type {@link org.openspcoop2.core.config.Transazioni}
	 * 
	 * @param fileName Xml file to serialize the object <var>transazioni</var>
	 * @param transazioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Transazioni transazioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Transazioni.class, transazioni, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>transazioni</var> of type {@link org.openspcoop2.core.config.Transazioni}
	 * 
	 * @param file Xml file to serialize the object <var>transazioni</var>
	 * @param transazioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Transazioni transazioni) throws SerializerException {
		this.objToXml(file, Transazioni.class, transazioni, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>transazioni</var> of type {@link org.openspcoop2.core.config.Transazioni}
	 * 
	 * @param file Xml file to serialize the object <var>transazioni</var>
	 * @param transazioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Transazioni transazioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Transazioni.class, transazioni, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>transazioni</var> of type {@link org.openspcoop2.core.config.Transazioni}
	 * 
	 * @param out OutputStream to serialize the object <var>transazioni</var>
	 * @param transazioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Transazioni transazioni) throws SerializerException {
		this.objToXml(out, Transazioni.class, transazioni, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>transazioni</var> of type {@link org.openspcoop2.core.config.Transazioni}
	 * 
	 * @param out OutputStream to serialize the object <var>transazioni</var>
	 * @param transazioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Transazioni transazioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Transazioni.class, transazioni, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>transazioni</var> of type {@link org.openspcoop2.core.config.Transazioni}
	 * 
	 * @param transazioni Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Transazioni transazioni) throws SerializerException {
		return this.objToXml(Transazioni.class, transazioni, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>transazioni</var> of type {@link org.openspcoop2.core.config.Transazioni}
	 * 
	 * @param transazioni Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Transazioni transazioni,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Transazioni.class, transazioni, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>transazioni</var> of type {@link org.openspcoop2.core.config.Transazioni}
	 * 
	 * @param transazioni Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Transazioni transazioni) throws SerializerException {
		return this.objToXml(Transazioni.class, transazioni, false).toString();
	}
	/**
	 * Serialize to String the object <var>transazioni</var> of type {@link org.openspcoop2.core.config.Transazioni}
	 * 
	 * @param transazioni Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Transazioni transazioni,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Transazioni.class, transazioni, prettyPrint).toString();
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
	 Object: response-caching-configurazione-generale
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responseCachingConfigurazioneGenerale</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale}
	 * 
	 * @param fileName Xml file to serialize the object <var>responseCachingConfigurazioneGenerale</var>
	 * @param responseCachingConfigurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponseCachingConfigurazioneGenerale responseCachingConfigurazioneGenerale) throws SerializerException {
		this.objToXml(fileName, ResponseCachingConfigurazioneGenerale.class, responseCachingConfigurazioneGenerale, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responseCachingConfigurazioneGenerale</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale}
	 * 
	 * @param fileName Xml file to serialize the object <var>responseCachingConfigurazioneGenerale</var>
	 * @param responseCachingConfigurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponseCachingConfigurazioneGenerale responseCachingConfigurazioneGenerale,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResponseCachingConfigurazioneGenerale.class, responseCachingConfigurazioneGenerale, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>responseCachingConfigurazioneGenerale</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale}
	 * 
	 * @param file Xml file to serialize the object <var>responseCachingConfigurazioneGenerale</var>
	 * @param responseCachingConfigurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponseCachingConfigurazioneGenerale responseCachingConfigurazioneGenerale) throws SerializerException {
		this.objToXml(file, ResponseCachingConfigurazioneGenerale.class, responseCachingConfigurazioneGenerale, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>responseCachingConfigurazioneGenerale</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale}
	 * 
	 * @param file Xml file to serialize the object <var>responseCachingConfigurazioneGenerale</var>
	 * @param responseCachingConfigurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponseCachingConfigurazioneGenerale responseCachingConfigurazioneGenerale,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResponseCachingConfigurazioneGenerale.class, responseCachingConfigurazioneGenerale, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>responseCachingConfigurazioneGenerale</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale}
	 * 
	 * @param out OutputStream to serialize the object <var>responseCachingConfigurazioneGenerale</var>
	 * @param responseCachingConfigurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponseCachingConfigurazioneGenerale responseCachingConfigurazioneGenerale) throws SerializerException {
		this.objToXml(out, ResponseCachingConfigurazioneGenerale.class, responseCachingConfigurazioneGenerale, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>responseCachingConfigurazioneGenerale</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale}
	 * 
	 * @param out OutputStream to serialize the object <var>responseCachingConfigurazioneGenerale</var>
	 * @param responseCachingConfigurazioneGenerale Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponseCachingConfigurazioneGenerale responseCachingConfigurazioneGenerale,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResponseCachingConfigurazioneGenerale.class, responseCachingConfigurazioneGenerale, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>responseCachingConfigurazioneGenerale</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale}
	 * 
	 * @param responseCachingConfigurazioneGenerale Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponseCachingConfigurazioneGenerale responseCachingConfigurazioneGenerale) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneGenerale.class, responseCachingConfigurazioneGenerale, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>responseCachingConfigurazioneGenerale</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale}
	 * 
	 * @param responseCachingConfigurazioneGenerale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponseCachingConfigurazioneGenerale responseCachingConfigurazioneGenerale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneGenerale.class, responseCachingConfigurazioneGenerale, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>responseCachingConfigurazioneGenerale</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale}
	 * 
	 * @param responseCachingConfigurazioneGenerale Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponseCachingConfigurazioneGenerale responseCachingConfigurazioneGenerale) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneGenerale.class, responseCachingConfigurazioneGenerale, false).toString();
	}
	/**
	 * Serialize to String the object <var>responseCachingConfigurazioneGenerale</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale}
	 * 
	 * @param responseCachingConfigurazioneGenerale Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponseCachingConfigurazioneGenerale responseCachingConfigurazioneGenerale,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneGenerale.class, responseCachingConfigurazioneGenerale, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: response-caching-configurazione-hash-generator
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responseCachingConfigurazioneHashGenerator</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator}
	 * 
	 * @param fileName Xml file to serialize the object <var>responseCachingConfigurazioneHashGenerator</var>
	 * @param responseCachingConfigurazioneHashGenerator Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponseCachingConfigurazioneHashGenerator responseCachingConfigurazioneHashGenerator) throws SerializerException {
		this.objToXml(fileName, ResponseCachingConfigurazioneHashGenerator.class, responseCachingConfigurazioneHashGenerator, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>responseCachingConfigurazioneHashGenerator</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator}
	 * 
	 * @param fileName Xml file to serialize the object <var>responseCachingConfigurazioneHashGenerator</var>
	 * @param responseCachingConfigurazioneHashGenerator Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResponseCachingConfigurazioneHashGenerator responseCachingConfigurazioneHashGenerator,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResponseCachingConfigurazioneHashGenerator.class, responseCachingConfigurazioneHashGenerator, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>responseCachingConfigurazioneHashGenerator</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator}
	 * 
	 * @param file Xml file to serialize the object <var>responseCachingConfigurazioneHashGenerator</var>
	 * @param responseCachingConfigurazioneHashGenerator Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponseCachingConfigurazioneHashGenerator responseCachingConfigurazioneHashGenerator) throws SerializerException {
		this.objToXml(file, ResponseCachingConfigurazioneHashGenerator.class, responseCachingConfigurazioneHashGenerator, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>responseCachingConfigurazioneHashGenerator</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator}
	 * 
	 * @param file Xml file to serialize the object <var>responseCachingConfigurazioneHashGenerator</var>
	 * @param responseCachingConfigurazioneHashGenerator Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResponseCachingConfigurazioneHashGenerator responseCachingConfigurazioneHashGenerator,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResponseCachingConfigurazioneHashGenerator.class, responseCachingConfigurazioneHashGenerator, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>responseCachingConfigurazioneHashGenerator</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator}
	 * 
	 * @param out OutputStream to serialize the object <var>responseCachingConfigurazioneHashGenerator</var>
	 * @param responseCachingConfigurazioneHashGenerator Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponseCachingConfigurazioneHashGenerator responseCachingConfigurazioneHashGenerator) throws SerializerException {
		this.objToXml(out, ResponseCachingConfigurazioneHashGenerator.class, responseCachingConfigurazioneHashGenerator, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>responseCachingConfigurazioneHashGenerator</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator}
	 * 
	 * @param out OutputStream to serialize the object <var>responseCachingConfigurazioneHashGenerator</var>
	 * @param responseCachingConfigurazioneHashGenerator Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResponseCachingConfigurazioneHashGenerator responseCachingConfigurazioneHashGenerator,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResponseCachingConfigurazioneHashGenerator.class, responseCachingConfigurazioneHashGenerator, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>responseCachingConfigurazioneHashGenerator</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator}
	 * 
	 * @param responseCachingConfigurazioneHashGenerator Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponseCachingConfigurazioneHashGenerator responseCachingConfigurazioneHashGenerator) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneHashGenerator.class, responseCachingConfigurazioneHashGenerator, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>responseCachingConfigurazioneHashGenerator</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator}
	 * 
	 * @param responseCachingConfigurazioneHashGenerator Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResponseCachingConfigurazioneHashGenerator responseCachingConfigurazioneHashGenerator,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneHashGenerator.class, responseCachingConfigurazioneHashGenerator, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>responseCachingConfigurazioneHashGenerator</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator}
	 * 
	 * @param responseCachingConfigurazioneHashGenerator Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponseCachingConfigurazioneHashGenerator responseCachingConfigurazioneHashGenerator) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneHashGenerator.class, responseCachingConfigurazioneHashGenerator, false).toString();
	}
	/**
	 * Serialize to String the object <var>responseCachingConfigurazioneHashGenerator</var> of type {@link org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator}
	 * 
	 * @param responseCachingConfigurazioneHashGenerator Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResponseCachingConfigurazioneHashGenerator responseCachingConfigurazioneHashGenerator,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResponseCachingConfigurazioneHashGenerator.class, responseCachingConfigurazioneHashGenerator, prettyPrint).toString();
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
	 Object: protocol-property
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.config.ProtocolProperty}
	 * 
	 * @param fileName Xml file to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ProtocolProperty protocolProperty) throws SerializerException {
		this.objToXml(fileName, ProtocolProperty.class, protocolProperty, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.config.ProtocolProperty}
	 * 
	 * @param fileName Xml file to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ProtocolProperty protocolProperty,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ProtocolProperty.class, protocolProperty, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.config.ProtocolProperty}
	 * 
	 * @param file Xml file to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ProtocolProperty protocolProperty) throws SerializerException {
		this.objToXml(file, ProtocolProperty.class, protocolProperty, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.config.ProtocolProperty}
	 * 
	 * @param file Xml file to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ProtocolProperty protocolProperty,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ProtocolProperty.class, protocolProperty, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.config.ProtocolProperty}
	 * 
	 * @param out OutputStream to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ProtocolProperty protocolProperty) throws SerializerException {
		this.objToXml(out, ProtocolProperty.class, protocolProperty, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.config.ProtocolProperty}
	 * 
	 * @param out OutputStream to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ProtocolProperty protocolProperty,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ProtocolProperty.class, protocolProperty, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.config.ProtocolProperty}
	 * 
	 * @param protocolProperty Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ProtocolProperty protocolProperty) throws SerializerException {
		return this.objToXml(ProtocolProperty.class, protocolProperty, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.config.ProtocolProperty}
	 * 
	 * @param protocolProperty Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ProtocolProperty protocolProperty,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ProtocolProperty.class, protocolProperty, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.config.ProtocolProperty}
	 * 
	 * @param protocolProperty Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ProtocolProperty protocolProperty) throws SerializerException {
		return this.objToXml(ProtocolProperty.class, protocolProperty, false).toString();
	}
	/**
	 * Serialize to String the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.config.ProtocolProperty}
	 * 
	 * @param protocolProperty Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ProtocolProperty protocolProperty,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ProtocolProperty.class, protocolProperty, prettyPrint).toString();
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
	 Object: dump-configurazione-regola
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dumpConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.DumpConfigurazioneRegola}
	 * 
	 * @param fileName Xml file to serialize the object <var>dumpConfigurazioneRegola</var>
	 * @param dumpConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DumpConfigurazioneRegola dumpConfigurazioneRegola) throws SerializerException {
		this.objToXml(fileName, DumpConfigurazioneRegola.class, dumpConfigurazioneRegola, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>dumpConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.DumpConfigurazioneRegola}
	 * 
	 * @param fileName Xml file to serialize the object <var>dumpConfigurazioneRegola</var>
	 * @param dumpConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DumpConfigurazioneRegola dumpConfigurazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DumpConfigurazioneRegola.class, dumpConfigurazioneRegola, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>dumpConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.DumpConfigurazioneRegola}
	 * 
	 * @param file Xml file to serialize the object <var>dumpConfigurazioneRegola</var>
	 * @param dumpConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DumpConfigurazioneRegola dumpConfigurazioneRegola) throws SerializerException {
		this.objToXml(file, DumpConfigurazioneRegola.class, dumpConfigurazioneRegola, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>dumpConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.DumpConfigurazioneRegola}
	 * 
	 * @param file Xml file to serialize the object <var>dumpConfigurazioneRegola</var>
	 * @param dumpConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DumpConfigurazioneRegola dumpConfigurazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DumpConfigurazioneRegola.class, dumpConfigurazioneRegola, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>dumpConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.DumpConfigurazioneRegola}
	 * 
	 * @param out OutputStream to serialize the object <var>dumpConfigurazioneRegola</var>
	 * @param dumpConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DumpConfigurazioneRegola dumpConfigurazioneRegola) throws SerializerException {
		this.objToXml(out, DumpConfigurazioneRegola.class, dumpConfigurazioneRegola, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>dumpConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.DumpConfigurazioneRegola}
	 * 
	 * @param out OutputStream to serialize the object <var>dumpConfigurazioneRegola</var>
	 * @param dumpConfigurazioneRegola Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DumpConfigurazioneRegola dumpConfigurazioneRegola,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DumpConfigurazioneRegola.class, dumpConfigurazioneRegola, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>dumpConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.DumpConfigurazioneRegola}
	 * 
	 * @param dumpConfigurazioneRegola Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DumpConfigurazioneRegola dumpConfigurazioneRegola) throws SerializerException {
		return this.objToXml(DumpConfigurazioneRegola.class, dumpConfigurazioneRegola, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>dumpConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.DumpConfigurazioneRegola}
	 * 
	 * @param dumpConfigurazioneRegola Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DumpConfigurazioneRegola dumpConfigurazioneRegola,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DumpConfigurazioneRegola.class, dumpConfigurazioneRegola, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>dumpConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.DumpConfigurazioneRegola}
	 * 
	 * @param dumpConfigurazioneRegola Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DumpConfigurazioneRegola dumpConfigurazioneRegola) throws SerializerException {
		return this.objToXml(DumpConfigurazioneRegola.class, dumpConfigurazioneRegola, false).toString();
	}
	/**
	 * Serialize to String the object <var>dumpConfigurazioneRegola</var> of type {@link org.openspcoop2.core.config.DumpConfigurazioneRegola}
	 * 
	 * @param dumpConfigurazioneRegola Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DumpConfigurazioneRegola dumpConfigurazioneRegola,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DumpConfigurazioneRegola.class, dumpConfigurazioneRegola, prettyPrint).toString();
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
	
	
	

}
