/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.security.message.utils;

import java.util.Properties;

import org.openspcoop2.security.keystore.MultiKeystore;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;

/**
 * BaseSecurityBean
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BaseSecurityBean {

	private Properties properties;
	private KeyStore keystore;
	private KeyStore truststore;
	private JWKSet jwkSet;
	private String user;
	private String password;
	
	private MultiKeystore multiKeystore; // per mantenere la configurazione multikeystpre
	
	public Properties getProperties() {
		return this.properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	public KeyStore getKeystore() {
		return this.keystore;
	}
	public void setKeystore(KeyStore keystore) {
		this.keystore = keystore;
	}
	public void setKeystore(java.security.KeyStore keystore) {
		if(keystore!=null) {
			this.keystore = new KeyStore(keystore);
		}
	}
	public KeyStore getTruststore() {
		return this.truststore;
	}
	public void setTruststore(KeyStore truststore) {
		this.truststore = truststore;
	}
	public void setTruststore(java.security.KeyStore truststore) {
		if(truststore!=null) {
			this.truststore = new KeyStore(truststore);
		}
	}
	public JWKSet getJwkSet() {
		return this.jwkSet;
	}
	public void setJwkSet(JWKSet jwkSet) {
		this.jwkSet = jwkSet;
	}
	public String getUser() {
		return this.user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public MultiKeystore getMultiKeystore() {
		return this.multiKeystore;
	}
	public void setMultiKeystore(MultiKeystore multiKeystore) {
		this.multiKeystore = multiKeystore;
	}
}
