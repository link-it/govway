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
package org.openspcoop2.security.keystore;

import java.io.Serializable;

/**
 * SecretPasswordKeyDerivationConfig
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecretPasswordKeyDerivationConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String pwd = null;
	private String pwdEncryptionMode = null;
	private Integer pwdIteration = null;
	
	public SecretPasswordKeyDerivationConfig(String pwd, String pwdEncryptionMode) {
		this(pwd, pwdEncryptionMode, null);
	}
	public SecretPasswordKeyDerivationConfig(String pwd, String pwdEncryptionMode, Integer pwdIteration) {
		this.pwd = pwd;
		this.pwdEncryptionMode = pwdEncryptionMode;
		this.pwdIteration = pwdIteration;
	}
	
	public String toKey() {
		StringBuilder sb = new StringBuilder("PwdDerivationConfig_");
		sb.append("mode:").append(this.pwdEncryptionMode);
		sb.append("_");
		sb.append("iter:").append(this.pwdIteration!=null ? this.pwdIteration : "-");
		sb.append("_");
		sb.append(this.pwd);
		return sb.toString();
	}
	
	public String getPassword() {
		return this.pwd;
	}
	public String getPasswordEncryptionMode() {
		return this.pwdEncryptionMode;
	}
	public Integer getPasswordIterator() {
		return this.pwdIteration;
	}
}
