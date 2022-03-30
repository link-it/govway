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

package org.openspcoop2.utils.security;

import java.security.Provider;

import javax.crypto.SecretKey;

/**	
 * SecretKeyPkcs11
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecretKeyPkcs11 {

	public SecretKeyPkcs11(Provider provider, SecretKey secretKey) {
		this.provider = provider;
		this.secretKey = secretKey;
	}
	
	private Provider provider;
	private SecretKey secretKey;
	
	public Provider getProvider() {
		return this.provider;
	}
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	public SecretKey getSecretKey() {
		return this.secretKey;
	}
	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}
}
