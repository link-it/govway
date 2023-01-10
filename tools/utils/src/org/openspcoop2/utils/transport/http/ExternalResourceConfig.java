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
package org.openspcoop2.utils.transport.http;

import java.security.KeyStore;
import java.security.cert.CertStore;
import java.util.ArrayList;
import java.util.List;

/**
 * ExternalResourceConfig
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExternalResourceConfig {

	private int readTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
	private int connectTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
	
	private List<Integer> returnCode = new ArrayList<>();
	{
		this.returnCode.add(200);
	}
	
	private boolean trustAllCerts = false;

	private KeyStore trustStore;
	
	private CertStore crlStore;
	
	private KeyStore keyStore;

	public int getReadTimeout() {
		return this.readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getConnectTimeout() {
		return this.connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	public List<Integer> getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(List<Integer> returnCode) {
		this.returnCode = returnCode;
	}
	
	public boolean isTrustAllCerts() {
		return this.trustAllCerts;
	}

	public void setTrustAllCerts(boolean trustAllCerts) {
		this.trustAllCerts = trustAllCerts;
	}

	public KeyStore getTrustStore() {
		return this.trustStore;
	}

	public void setTrustStore(KeyStore trustStore) {
		this.trustStore = trustStore;
	}

	public CertStore getCrlStore() {
		return this.crlStore;
	}

	public void setCrlStore(CertStore crlStore) {
		this.crlStore = crlStore;
	}

	public KeyStore getKeyStore() {
		return this.keyStore;
	}

	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
	}
}
