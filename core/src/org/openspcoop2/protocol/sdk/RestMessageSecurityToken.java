/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.sdk;

import org.openspcoop2.utils.io.Base64Utilities;

/**     
 * RestMessageSecurityToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$AuthorizationMessageSecurityToken
 */
public class RestMessageSecurityToken extends AbstractMessageSecurityToken<String> {

	public String getHeader() {
		if(this.token!=null) {
			String [] split = this.token.split("\\.");
			if(split!=null && split.length>0) {
				return split[0];
			}
		}
		return null;
	}
	public String getDecodedHeader() {
		String hdr = this.getHeader();
		if(hdr!=null) {
			return new String(Base64Utilities.decode(hdr));
		}
		return null;
	}
	
	public String getPayload() {
		if(this.token!=null) {
			String [] split = this.token.split("\\.");
			if(split!=null && split.length>1) {
				return split[1];
			}
		}
		return null;
	}
	public String getDecodedPayload() {
		String payload = this.getPayload();
		if(payload!=null) {
			return new String(Base64Utilities.decode(payload));
		}
		return null;
	}
	
}
