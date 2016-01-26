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

package org.openspcoop2.protocol.sdk.tracciamento;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * La Porta di Dominio tiene traccia dei metadati di 
 * cooperazione dei messaggi scambiati con le altre porte.
 * Il formato di tali tracce viene gestito da questa classe. 
 * 
 * @author Lorenzo Nardi (nardi@link.it)
* @author $Author$
* @version $Rev$, $Date$
 * */
public interface IXMLTracciaBuilder {
	
	/**
	 * Recupera l'implementazione della factory per il protocollo in uso
	 * @return protocolFactory in uso.
	 */
		
	public IProtocolFactory getProtocolFactory();
	
	/**
	 * Costruisce un SOAPElement contenente una traccia come definito da specifica
	 * 
	 * @param traccia Contiene i valori della traccia
	 * @return SOAPElement contenente la traccia. 
	 */
	public SOAPElement toElement(Traccia traccia) throws ProtocolException;
	
	/**
	 * Costruisce una traccia come definito da specifica
	 * 
	 * @param traccia Contiene i valori della traccia
	 * @return Stringa contenente la traccia. 
	 */
	public String toString(Traccia traccia) throws ProtocolException;
	
	/**
	 * Costruisce una traccia come definito da specifica
	 * 
	 * @param traccia Contiene i valori della traccia
	 * @return ByteArray contenente la traccia. 
	 */
	public byte[] toByteArray(Traccia traccia) throws ProtocolException;
	
}