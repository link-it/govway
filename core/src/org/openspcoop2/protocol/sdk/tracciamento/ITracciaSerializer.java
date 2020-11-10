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

package org.openspcoop2.protocol.sdk.tracciamento;

import java.util.List;

import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.XMLRootElement;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.w3c.dom.Element;

/**
 * La Porta di Dominio tiene traccia dei metadati di 
 * cooperazione dei messaggi scambiati con le altre porte.
 * Il formato di tali tracce viene gestito da questa classe. 
 * 
 * @author Lorenzo Nardi (nardi@link.it)
* @author $Author$
* @version $Rev$, $Date$
 * */
public interface ITracciaSerializer extends IComponentFactory {
		
	/**
	 * Costruisce un Element contenente una traccia come definito da specifica
	 * 
	 * @param traccia Contiene i valori della traccia
	 * @return Element contenente la traccia. 
	 */
	public Element toElement(Traccia traccia) throws ProtocolException;
	
	/**
	 * Costruisce una traccia come definito da specifica
	 * 
	 * @param traccia Contiene i valori della traccia
	 * @param tipoSerializzazione Tipologia di serializzazione
	 * @return Traccia serializzato nel tipo indicato nel parametro
	 */
	public String toString(Traccia traccia, TipoSerializzazione tipoSerializzazione) throws ProtocolException;
	
	/**
	 * Costruisce una traccia come definito da specifica
	 * 
	 * @param traccia Contiene i valori della traccia
	 * @param tipoSerializzazione Tipologia di serializzazione
	 * @return Traccia serializzato nel tipo indicato nel parametro
	 */
	public byte[] toByteArray(Traccia traccia, TipoSerializzazione tipoSerializzazione) throws ProtocolException;
	
	/**
	 * Indicazione sul xml contenitore delle tracce
	 * 
	 * @return Indicazione sul xml contenitore delle tracce
	 * @throws ProtocolException
	 */
	public XMLRootElement getXMLRootElement() throws ProtocolException;
	
	public List<TracciaExtInfoDefinition> getExtInfoDefinition();
	public List<TracciaExtInfo> extractExtInfo(Busta busta, ServiceBinding tipoApi);
}