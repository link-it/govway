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

package org.openspcoop2.protocol.sdk.diagnostica;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.w3c.dom.Element;


/**
 * Questa classe si occupa di conformare i messaggi diagnostici 
 * emessi dalla Porta di Dominio alle specifiche del protocollo
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IXMLDiagnosticoBuilder {
	
	/**
	 * Recupera l'implementazione della factory per il protocollo in uso
	 * @return protocolFactory in uso.
	 */
	
	public IProtocolFactory getProtocolFactory();
	
	/**
	 * Serializza le informazioni diagnostiche nel formato previsto dal protocollo
	 * 
	 * @param msgDiag Messaggio Diagnostico
	 * @return Element XML del diagnostico
	 * @throws ProtocolException
	 */
	public Element toElement(MsgDiagnostico msgDiag) throws ProtocolException;
	
	/**
	 * Serializza le informazioni diagnostiche nel formato previsto dal protocollo
	 * 
	 * @param msgDiag Messaggio Diagnostico
	 * @return Element XML del diagnostico
	 * @throws ProtocolException
	 */
	public String toString(MsgDiagnostico msgDiag) throws ProtocolException;
	
	/**
	 * Serializza le informazioni diagnostiche nel formato previsto dal protocollo
	 * 
	 * @param msgDiag Messaggio Diagnostico
	 * @return Element XML del diagnostico
	 * @throws ProtocolException
	 */
	public byte[] toByteArray(MsgDiagnostico msgDiag) throws ProtocolException;
	
}