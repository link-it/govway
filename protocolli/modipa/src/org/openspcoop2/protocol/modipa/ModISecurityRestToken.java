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

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * ModISecurityRestToken
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModISecurityRestToken extends AbstractModISecurityToken<String> {

	private String tokenIntegrityHeaderName;
	private String tokenIntegrity;
	private String headerName; 
	
	public ModISecurityRestToken(String tokenAuthorization, 
			String tokenIntegrityHeaderName, String tokenIntegrity) {
		super(tokenAuthorization);
		this.tokenIntegrityHeaderName = tokenIntegrityHeaderName;
		this.tokenIntegrity = tokenIntegrity;
	}
	public ModISecurityRestToken(String headerName, String token) {
		super(token);
		this.headerName = headerName;
	}

	@Override
	public String toString(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		switch (tipoSerializzazione) {
		case DEFAULT:
			StringBuilder sb = new StringBuilder();
			if(this.tokenIntegrity!=null) {
				// 2 header
				sb.append(HttpConstants.AUTHORIZATION).append(": ").append(HttpConstants.AUTHORIZATION_PREFIX_BEARER).append(this.getToken());
				sb.append("\n");
				sb.append(this.tokenIntegrityHeaderName).append(": ").append(this.tokenIntegrity);
			}
			else {
				if(HttpConstants.AUTHORIZATION.equals(this.headerName)) {
					sb.append(HttpConstants.AUTHORIZATION).append(": ").append(HttpConstants.AUTHORIZATION_PREFIX_BEARER).append(this.getToken());
				}
				else {
					sb.append(this.headerName).append(": ").append(this.tokenIntegrity);
				}
			}
			return sb.toString();
		default:
			throw new ProtocolException("Tipo di serializzazione ["+tipoSerializzazione+"] non supportata");
		}
	}
	
	@Override
	public byte[] toByteArray(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		switch (tipoSerializzazione) {
		case DEFAULT:
			//return this.getToken().getBytes();
			return this.toString(tipoSerializzazione).getBytes();
		default:
			throw new ProtocolException("Tipo di serializzazione ["+tipoSerializzazione+"] non supportata");
		}
	}
	
}