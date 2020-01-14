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

package org.openspcoop2.protocol.sdk.diagnostica;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.XMLRootElement;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.w3c.dom.Element;


/**
 * Questa classe si occupa di conformare i messaggi diagnostici 
 * emessi dalla Porta di Dominio alle specifiche del protocollo
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IDiagnosticSerializer extends IComponentFactory {
	
	
	/**
	 * Serializza le informazioni diagnostiche nel formato previsto dal protocollo
	 * 
	 * @param msgDiag Messaggio Diagnostico
	 * @return Element del diagnostico
	 * @throws ProtocolException
	 */
	public Element toElement(MsgDiagnostico msgDiag) throws ProtocolException;
	
	/**
	 * Serializza le informazioni diagnostiche nel formato previsto dal protocollo
	 * 
	 * @param msgDiag Messaggio Diagnostico
	 * @param tipoSerializzazione Tipologia di serializzazione
	 * @return Diagnostico serializzato nel tipo indicato nel parametro
	 * @throws ProtocolException
	 */
	public String toString(MsgDiagnostico msgDiag, TipoSerializzazione tipoSerializzazione) throws ProtocolException;
	
	/**
	 * Serializza le informazioni diagnostiche nel formato previsto dal protocollo
	 * 
	 * @param msgDiag Messaggio Diagnostico
	 * @param tipoSerializzazione Tipologia di serializzazione
	 * @return Diagnostico serializzato nel tipo indicato nel parametro
	 * @throws ProtocolException
	 */
	public byte[] toByteArray(MsgDiagnostico msgDiag, TipoSerializzazione tipoSerializzazione) throws ProtocolException;
	
	/**
	 * Indicazione sul xml contenitore dei messaggi diagnostici
	 * 
	 * @return Indicazione sul xml contenitore dei messaggi diagnostici
	 * @throws ProtocolException
	 */
	public XMLRootElement getXMLRootElement() throws ProtocolException;
	
	
}