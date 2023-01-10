/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk.builder;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.AbstractEccezioneBuilderParameter;
import org.openspcoop2.protocol.sdk.EccezioneIntegrazioneBuilderParameters;
import org.openspcoop2.protocol.sdk.EccezioneProtocolloBuilderParameters;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoErroreApplicativo;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Gestisce la generazione dei messaggi in caso di errore applicativo.
 * Se durante il processamento dei messaggi la Porta di Dominio riscontra
 * un errore, questo viene inserito nel messaggio di risposta applicativa che 
 * viene inoltrata al servizio applicativo fruitore.  
 * 
 * 
 * 
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IErroreApplicativoBuilder extends IComponentFactory {

	
	// NAMESPACE
	
	/**
	 * Ritorna il namespace che definisce una eccezione di protocollo
	 * 
	 * @return namespace che definisce una eccezione di protocollo
	 */
	public String getNamespaceEccezioneProtocollo(String defaultNamespace);
	
	/**
	 * Ritorna il QName che definisce una eccezione di protocollo
	 * 
	 * @param codice Codice da utilizzare come localName
	 * @return QName che definisce una eccezione di protocollo
	 */
	public QName getQNameEccezioneProtocollo(String defaultNamespace, String codice);
	
	/**
	 * Ritorna il namespace che definisce una eccezione di integrazione
	 * 
	 * @return namespace che definisce una eccezione di integrazione
	 */
	public String getNamespaceEccezioneIntegrazione(String defaultNamespacea);
	
	/**
	 * Ritorna il QName che definisce una eccezione di integrazione
	 * 
	 * @param codice Codice da utilizzare come localName
	 * @return QName che definisce una eccezione di integrazione
	 */
	public QName getQNameEccezioneIntegrazione(String defaultNamespace, String codice);
	
	
	
	// UTILITY DI RICONOSCIMENTO
	
	/**
	 * Indica se il nodo contiene una definizione di errore applicativo
	 * 
	 * @param node nodo
	 * @return Indica se il nodo contiene una definizione di errore applicativo
	 */
	public boolean isErroreApplicativo(Node node);
	/**
	 * Indica se il nodo contiene una definizione di errore applicativo
	 * 
	 * @param namespace Namespace
	 * @param localName LocalName
	 * @return Indica se il nodo contiene una definizione di errore applicativo
	 */
	public boolean isErroreApplicativo(String namespace, String localName);

		
	
	
	// BUILDER
	
	/**
	 * Costruisce un messaggio SOAP contenente un Fault generato secondo lo standard del protocollo
	 * 
	 * @param parameters parametri
	 * @return Il MessaggioSOAP contenente un Fault generato secondo lo standard del protocollo
	 * @throws ProtocolException
	 */
	public OpenSPCoop2Message toMessage(EccezioneProtocolloBuilderParameters parameters) throws ProtocolException;
	/**
	 * Costruisce un messaggio SOAP contenente un SOAPFault o un SOAPBody (a seconda della configurazione) generato secondo lo standard del protocollo
	 * 
	 * @param parameters parametri
	 * @return Il MessaggioSOAP contenente un SOAPFault generato secondo lo standard del protocollo
	 * @throws ProtocolException
	 */
	public OpenSPCoop2Message toMessage(EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException;

		
	/**
	 * Costruisce un SOAPElement contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param parameters parametri
	 * @return SOAPElement contenente l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public SOAPElement toSoapElement(EccezioneProtocolloBuilderParameters parameters) throws ProtocolException;
	/**
	 * Costruisce un SOAPElement contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param parameters parametri
	 * @return SOAPElement contenente l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public SOAPElement toSoapElement(EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException;
	
	
	/**
	 * Costruisce un SOAPElement contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param parameters parametri
	 * @return SOAPElement contenente l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public Element toElement(EccezioneProtocolloBuilderParameters parameters) throws ProtocolException;
	/**
	 * Costruisce un SOAPElement contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param parameters parametri
	 * @return SOAPElement contenente l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public Element toElement(EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException;
	
	
	/**
	 * Costruisce una stringa contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param tipoErroreApplicativo tipo di errore Applicativo (soap,xml,json)
	 * @param parameters parametri
	 * @return l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public String toString(TipoErroreApplicativo tipoErroreApplicativo, EccezioneProtocolloBuilderParameters parameters) throws ProtocolException;
	/**
	 * Costruisce una stringa contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param tipoErroreApplicativo tipo di errore Applicativo (soap,xml,json)
	 * @param parameters parametri
	 * @return l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public String toString(TipoErroreApplicativo tipoErroreApplicativo, EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException;
	
	
	/**
	 * Costruisce un byte[] contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param tipoErroreApplicativo tipo di errore Applicativo (soap,xml,json)
	 * @param parameters parametri
	 * @return l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public byte[] toByteArray(TipoErroreApplicativo tipoErroreApplicativo, EccezioneProtocolloBuilderParameters parameters) throws ProtocolException;
	/**
	 * Costruisce un byte[] contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param tipoErroreApplicativo tipo di errore Applicativo (soap,xml,json)
	 * @param parameters parametri
	 * @return l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public byte[] toByteArray(TipoErroreApplicativo tipoErroreApplicativo, EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException;
	
	
	
	
	
	// PARSER
	
	/**
	 * Si occupa di interpretare l'errore applicativo e di mapparne il codice eccezione e la descrizione nell'oggetto da ritornare
	 * 
	 * @param tipoErroreApplicativo tipo di errore Applicativo (soap,xml,json)
	 * @param erroreApplicativo errore Applicativo
	 * @param prefixCodiceErroreApplicativoIntegrazione prefixCodiceErroreApplicativoIntegrazione
	 * @return AbstractEccezioneBuilderParameter
	 * @throws ProtocolException
	 */
	public AbstractEccezioneBuilderParameter readErroreApplicativo(TipoErroreApplicativo tipoErroreApplicativo, String erroreApplicativo,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException;
	
	/**
	 * Si occupa di interpretare l'errore applicativo e di mapparne il codice eccezione e la descrizione nell'oggetto da ritornare
	 * 
	 * @param tipoErroreApplicativo tipo di errore Applicativo (soap,xml,json)
	 * @param erroreApplicativo errore Applicativo
	 * @param prefixCodiceErroreApplicativoIntegrazione prefixCodiceErroreApplicativoIntegrazione
	 * @return AbstractEccezioneBuilderParameter
	 * @throws ProtocolException
	 */
	public AbstractEccezioneBuilderParameter readErroreApplicativo(TipoErroreApplicativo tipoErroreApplicativo, byte[] erroreApplicativo,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException;
	
	/**
	 * Si occupa di interpretare l'errore applicativo e di mapparne il codice eccezione e la descrizione nell'oggetto da ritornare
	 * 
	 * @param erroreApplicativo errore Applicativo
	 * @param prefixCodiceErroreApplicativoIntegrazione prefixCodiceErroreApplicativoIntegrazione
	 * @return AbstractEccezioneBuilderParameter
	 * @throws ProtocolException
	 */
	public AbstractEccezioneBuilderParameter readErroreApplicativo(Node erroreApplicativo,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException;
	
	
	
	
	
	// INSERT INTO SOAP FAULT
	
	/**
	 * Aggiunge ad un messaggio contenente un SOAPFault le informazioni di un errore applicativo. 
	 *
	 * @param parameters parametri
	 * @param msg Messaggio contenente il SOAPFault da modificare.
	 * @throws ProtocolException
	 */	
	public void insertInSOAPFault(EccezioneProtocolloBuilderParameters parameters,
			OpenSPCoop2Message msg) throws ProtocolException;
	/**
	 * Aggiunge ad un messaggio contenente un SOAPFault le informazioni di un errore applicativo. 
	 *
	 * @param parameters parametri
	 * @param msg Messaggio contenente il SOAPFault da modificare.
	 * @throws ProtocolException
	 */	
	public void insertInSOAPFault(EccezioneIntegrazioneBuilderParameters parameters,
			OpenSPCoop2Message msg) throws ProtocolException;
	
	/**
	 * Aggiunge ad un messaggio contenente un SOAPFault le informazioni di un errore di routing
	 * 
	 * @param identitaRouter Riferimento al soggetto che identifica il router
	 * @param idFunzione identificativo del modulo funzionale
	 * @param msgErrore messaggio di errore
	 * @param msg Messaggio contenente il SOAPFault da modificare.
	 * @throws ProtocolException 
	 */	
	public void insertRoutingErrorInSOAPFault(IDSoggetto identitaRouter,String idFunzione,String msgErrore,OpenSPCoop2Message msg) throws ProtocolException;
	
	
	
	

}