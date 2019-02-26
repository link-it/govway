/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.spcoop;

import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;

/**
 * SPCoopBustaRawContent
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopBustaRawContent extends BustaRawContent<SOAPHeaderElement> {

	public SPCoopBustaRawContent(SOAPHeaderElement element) {
		super(element);
	}
	
	@Override
	public String toString(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		switch (tipoSerializzazione) {
		case XML:
		case DEFAULT:
			return Utilities.toString(this.element,false);
		default:
			throw new ProtocolException("Tipo di serializzazione ["+tipoSerializzazione+"] non supportata");
		}
	}
	
	@Override
	public byte[] toByteArray(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		switch (tipoSerializzazione) {
		case XML:
		case DEFAULT:
			return Utilities.toByteArray(this.element,false);
		default:
			throw new ProtocolException("Tipo di serializzazione ["+tipoSerializzazione+"] non supportata");
		}
	}

}
