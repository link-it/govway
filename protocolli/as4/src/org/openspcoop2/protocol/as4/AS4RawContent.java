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

package org.openspcoop2.protocol.as4;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;

/**
 * AS4RawContent
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4RawContent extends BustaRawContent<SOAPElement> {

	// in fase di spedizione e' una SOAPEnvelope, quando lo ricevo una SOAPElement
	
	public AS4RawContent(SOAPElement element) {
		super(element);
	}
	
	@Override
	public String toString(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		switch (tipoSerializzazione) {
		case XML:
		case DEFAULT:
			return Utilities.toString(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.element,false);
		default:
			throw new ProtocolException("Tipo di serializzazione ["+tipoSerializzazione+"] non supportata");
		}
	}
	
	@Override
	public byte[] toByteArray(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		switch (tipoSerializzazione) {
		case XML:
		case DEFAULT:
			return Utilities.toByteArray(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.element,false);
		default:
			throw new ProtocolException("Tipo di serializzazione ["+tipoSerializzazione+"] non supportata");
		}
	}

}
