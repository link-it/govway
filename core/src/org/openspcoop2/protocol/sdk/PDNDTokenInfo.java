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
package org.openspcoop2.protocol.sdk;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**     
 * PDNDTokenInfoDetails
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$AuthorizationMessageSecurityToken
 */
public class PDNDTokenInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,PDNDTokenInfoDetails> pdnd = new HashMap<>();
	
	public Map<String, PDNDTokenInfoDetails> getPdnd() {
		return this.pdnd;
	}
	public void setPdnd(Map<String, PDNDTokenInfoDetails> pdnd) {
		this.pdnd = pdnd;
	}
	
	public PDNDTokenInfoDetails getInfo(String infoId) {
		return this.pdnd!=null ? this.pdnd.get(infoId) : null;
	}
	public void setInfo(String infoId, PDNDTokenInfoDetails info) {
		if(this.pdnd==null) {
			this.pdnd = new HashMap<>();
		}
		this.pdnd.put(infoId, info);
	}
	
	public static final String CLIENT_INFO = "client";
	public PDNDTokenInfoDetails getClient() {
		return getInfo(CLIENT_INFO);
	}
	public Serializable getClient(String claim) {
		PDNDTokenInfoDetails details = getInfo(CLIENT_INFO);
		return details!=null && details.getClaims()!=null ? details.getClaims().get(claim) : null;
	}
	public void setClient(PDNDTokenInfoDetails info) {
		setInfo(CLIENT_INFO, info);
	}
	
	public static final String ORGANIZATION_INFO = "organization";
	public PDNDTokenInfoDetails getOrganization() {
		return getInfo(ORGANIZATION_INFO);
	}
	public Serializable getOrganization(String claim) {
		PDNDTokenInfoDetails details = getInfo(ORGANIZATION_INFO);
		return details!=null && details.getClaims()!=null ? details.getClaims().get(claim) : null;
	}
	public void setOrganization(PDNDTokenInfoDetails info) {
		setInfo(ORGANIZATION_INFO, info);
	}
}
