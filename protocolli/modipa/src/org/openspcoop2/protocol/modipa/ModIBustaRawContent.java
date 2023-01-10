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

package org.openspcoop2.protocol.modipa;

import javax.xml.soap.SOAPEnvelope;

import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;

/**
 * SDIBustaRawContent
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIBustaRawContent extends BustaRawContent<AbstractModISecurityToken<?>> {

	public ModIBustaRawContent(AbstractModISecurityToken<?> token) {
		super(token);
	}
	public ModIBustaRawContent(String headerName, String token) {
		super(new ModISecurityRestToken(headerName, token));
	}
	public ModIBustaRawContent(String tokenAuthorization, String tokenIntegrityHeaderName, String tokenIntegrity) {
		super(new ModISecurityRestToken(tokenAuthorization, 
				tokenIntegrityHeaderName, tokenIntegrity));
	}
	public ModIBustaRawContent(SOAPEnvelope token) {
		super(new ModISecuritySoapToken(token));
	}
	
	@Override
	public String toString(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		return this.element.toString(tipoSerializzazione);
	}
	
	@Override
	public byte[] toByteArray(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		return this.element.toByteArray(tipoSerializzazione);
	}

}
