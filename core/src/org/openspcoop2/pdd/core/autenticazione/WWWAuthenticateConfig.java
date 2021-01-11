/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.core.autenticazione;

import org.openspcoop2.message.utils.WWWAuthenticateGenerator;

/**     
 * WWWAuthenticateConfig
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WWWAuthenticateConfig {

	private String authType;
	private String realm;
	private String notFound_error;
	private String notFound_error_description;
	private String invalid_error;
	private String invalid_error_description;
	
	public String buildWWWAuthenticateHeaderValue_notFound() {
		return WWWAuthenticateGenerator.buildCustomHeaderValue(this.authType, this.realm, this.notFound_error, this.notFound_error_description);
	}
	public String buildWWWAuthenticateHeaderValue_invalid() {
		return WWWAuthenticateGenerator.buildCustomHeaderValue(this.authType, this.realm, this.invalid_error, this.invalid_error_description);
	}
	
	@Override
	public Object clone() {
		WWWAuthenticateConfig newInstance = new WWWAuthenticateConfig();
		
		newInstance.authType = new String(this.authType);
		newInstance.realm = new String(this.realm);
		
		if(this.notFound_error!=null) {
			newInstance.notFound_error = new String(this.notFound_error);
		}
		if(this.notFound_error_description!=null) {
			newInstance.notFound_error_description = new String(this.notFound_error_description);
		}
		
		if(this.invalid_error!=null) {
			newInstance.invalid_error = new String(this.invalid_error);
		}
		if(this.invalid_error_description!=null) {
			newInstance.invalid_error_description = new String(this.invalid_error_description);
		}
		
		return newInstance;
	}
	
	public String getAuthType() {
		return this.authType;
	}
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	public String getRealm() {
		return this.realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getNotFound_error() {
		return this.notFound_error;
	}
	public void setNotFound_error(String notFound_error) {
		this.notFound_error = notFound_error;
	}
	public String getNotFound_error_description() {
		return this.notFound_error_description;
	}
	public void setNotFound_error_description(String notFound_error_description) {
		this.notFound_error_description = notFound_error_description;
	}

	public String getInvalid_error() {
		return this.invalid_error;
	}
	public void setInvalid_error(String invalid_error) {
		this.invalid_error = invalid_error;
	}
	public String getInvalid_error_description() {
		return this.invalid_error_description;
	}
	public void setInvalid_error_description(String invalid_error_description) {
		this.invalid_error_description = invalid_error_description;
	}
}
