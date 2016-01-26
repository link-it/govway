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

package org.openspcoop2.protocol.sdk.builder;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.AbstractEccezioneBuilderParameter;
import org.openspcoop2.protocol.sdk.EccezioneIntegrazioneBuilderParameters;
import org.openspcoop2.protocol.sdk.EccezioneProtocolloBuilderParameters;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
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

public interface IErroreApplicativoBuilder {

	/**
	 * Recupera l'implementazione della factory per il protocollo in uso
	 * @return protocolFactory in uso.
	 */
		
	public IProtocolFactory getProtocolFactory();

	/**
	 * Ritorna il namespace che definisce una eccezione di protocollo
	 * 
	 * @return namespace che definisce una eccezione di protocollo
	 */
	public String getNamespaceEccezioneProtocollo();
	
	/**
	 * Ritorna il QName che definisce una eccezione di protocollo
	 * 
	 * @param codice Codice da utilizzare come localName
	 * @return QName che definisce una eccezione di protocollo
	 */
	public QName getQNameEccezioneProtocollo(String codice);
	
	/**
	 * Ritorna il namespace che definisce una eccezione di integrazione
	 * 
	 * @return namespace che definisce una eccezione di integrazione
	 */
	public String getNamespaceEccezioneIntegrazione();
	
	/**
	 * Ritorna il QName che definisce una eccezione di integrazione
	 * 
	 * @param codice Codice da utilizzare come localName
	 * @return QName che definisce una eccezione di integrazione
	 */
	public QName getQNameEccezioneIntegrazione(String codice);
	
	
	
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

	
	
	/**
	 * Costruisce un messaggio SOAP contenente un SOAPFault o un SOAPBody (a seconda della configurazione) generato secondo lo standard del protocollo
	 * 
	 * @param parameters parametri
	 * @return Il MessaggioSOAP contenente un SOAPFault generato secondo lo standard del protocollo
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
	public SOAPElement toElement(EccezioneProtocolloBuilderParameters parameters) throws ProtocolException;
	/**
	 * Costruisce un SOAPElement contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param parameters parametri
	 * @return SOAPElement contenente l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public SOAPElement toElement(EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException;
	
	
	/**
	 * Costruisce un SOAPElement contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param parameters parametri
	 * @return l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public String toString(EccezioneProtocolloBuilderParameters parameters) throws ProtocolException;
	/**
	 * Costruisce un SOAPElement contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param parameters parametri
	 * @return l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public String toString(EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException;
	
	
	/**
	 * Costruisce un SOAPElement contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param parameters parametri
	 * @return l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public byte[] toByteArray(EccezioneProtocolloBuilderParameters parameters) throws ProtocolException;
	/**
	 * Costruisce un SOAPElement contenente un'Eccezione come definito da specifica del protocollo in uso.
	 * L'elemento restituito sara' inserito nel corpo del messaggio di risposta.
	 * 
	 * @param parameters parametri
	 * @return l'Eccezione in caso di successo. 
	 * @throws ProtocolException
	 */
	public byte[] toByteArray(EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException;
	
	
	
	/**
	 * Si occupa di interpretare l'errore applicativo e di mapparne il codice eccezione e la descrizione nell'oggetto da ritornare
	 * 
	 * @param xml errore Applicativo
	 * @param prefixCodiceErroreApplicativoIntegrazione prefixCodiceErroreApplicativoIntegrazione
	 * @return AbstractEccezioneBuilderParameter
	 * @throws ProtocolException
	 */
	public AbstractEccezioneBuilderParameter readErroreApplicativo(String xml,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException;
	
	/**
	 * Si occupa di interpretare l'errore applicativo e di mapparne il codice eccezione e la descrizione nell'oggetto da ritornare
	 * 
	 * @param xml errore Applicativo
	 * @param prefixCodiceErroreApplicativoIntegrazione prefixCodiceErroreApplicativoIntegrazione
	 * @return AbstractEccezioneBuilderParameter
	 * @throws ProtocolException
	 */
	public AbstractEccezioneBuilderParameter readErroreApplicativo(byte[] xml,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException;
	
	/**
	 * Si occupa di interpretare l'errore applicativo e di mapparne il codice eccezione e la descrizione nell'oggetto da ritornare
	 * 
	 * @param xml errore Applicativo
	 * @param prefixCodiceErroreApplicativoIntegrazione prefixCodiceErroreApplicativoIntegrazione
	 * @return AbstractEccezioneBuilderParameter
	 * @throws ProtocolException
	 */
	public AbstractEccezioneBuilderParameter readErroreApplicativo(Node xml,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException;
	
	
	
	
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