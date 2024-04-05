/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

/**     
 * WWWAuthenticate
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WWWAuthenticate {

	private String authType;
	private String realm;
	private String error;
	private String error_description;
	
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

	public String getError() {
		return this.error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getError_description() {
		return this.error_description;
	}
	public void setError_description(String error_description) {
		this.error_description = error_description;
	}
}
