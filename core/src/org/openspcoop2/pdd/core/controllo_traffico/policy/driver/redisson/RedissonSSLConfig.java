/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson;

import org.openspcoop2.utils.certificate.KeystoreType;

/**
 * RedissonSSLConfig
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RedissonSSLConfig implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private boolean trustAll = false;
	private boolean hostnameVerifier = true;
	private String truststorePath = null;
	private String truststoreType = KeystoreType.JKS.getNome();
	private String truststorePassword = null;

	public boolean isTrustAll() {
		return this.trustAll;
	}
	public void setTrustAll(boolean trustAll) {
		this.trustAll = trustAll;
	}

	public boolean isHostnameVerifier() {
		return this.hostnameVerifier;
	}
	public void setHostnameVerifier(boolean hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}

	public String getTruststorePath() {
		return this.truststorePath;
	}
	public void setTruststorePath(String truststorePath) {
		this.truststorePath = truststorePath;
	}

	public String getTruststoreType() {
		return this.truststoreType;
	}
	public void setTruststoreType(String truststoreType) {
		this.truststoreType = truststoreType;
	}

	public String getTruststorePassword() {
		return this.truststorePassword;
	}
	public void setTruststorePassword(String truststorePassword) {
		this.truststorePassword = truststorePassword;
	}

}
