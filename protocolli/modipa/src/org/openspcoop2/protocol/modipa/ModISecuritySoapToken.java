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

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;

/**
 * ModISecuritySoapToken
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModISecuritySoapToken extends AbstractModISecurityToken<SOAPEnvelope> {

	public ModISecuritySoapToken(SOAPEnvelope token) {
		super(token);
	}

	@Override
	public String toString(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		switch (tipoSerializzazione) {
		case XML:
		case DEFAULT:
			StringBuilder sb = new StringBuilder();
			sb.append( Utilities.toString(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.getToken(),false) );
			if(this.tokenAudit!=null) {
				sb.append("\n");
				sb.append(this.tokenAuditHeaderName).append(": ").append(this.tokenAudit);
			}
			return sb.toString();
		default:
			throw new ProtocolException("Tipo di serializzazione ["+tipoSerializzazione+"] non supportata");
		}
	}
	
	@Override
	public byte[] toByteArray(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		switch (tipoSerializzazione) {
		case XML:
		case DEFAULT:
			/**return Utilities.toByteArray(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.getToken(),false);*/
			return this.toString(tipoSerializzazione).getBytes();
		default:
			throw new ProtocolException("Tipo di serializzazione ["+tipoSerializzazione+"] non supportata");
		}
	}
	
}