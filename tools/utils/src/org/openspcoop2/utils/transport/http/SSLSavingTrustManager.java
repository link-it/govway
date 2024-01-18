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

package org.openspcoop2.utils.transport.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * SSLSavingTrustManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLSavingTrustManager implements X509TrustManager {

	private X509TrustManager tm;
	private X509Certificate[] chain;
	private boolean processClient;

	public X509Certificate[] getPeerCertificates() {
		return this.chain;
	}

	public SSLSavingTrustManager(X509TrustManager tm) {
		this(tm, false);
	}
	public SSLSavingTrustManager(X509TrustManager tm, boolean processClient) {
		this.tm = tm;
		this.processClient = processClient;
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		if(this.processClient) {
			return this.tm.getAcceptedIssuers();
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		if(this.processClient) {
			this.tm.checkClientTrusted(chain, authType);
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		this.chain = chain;
		this.tm.checkServerTrusted(chain, authType);
	}

	public static TrustManager[] wrap(TrustManager[] tmArray) {
		if(tmArray!=null && tmArray.length>0) {
			for (int i = 0; i < tmArray.length; i++) {
				TrustManager tm = tmArray[i];
				if(tm!=null && tm instanceof X509TrustManager) {
					// wrap

					// clono perche' nel caso di SSLTrustAll si tratta di una istanza statica
					TrustManager[] cloned = new TrustManager[tmArray.length];
					for (int j = 0; j < tmArray.length; j++) {
						if(j == i) {
							cloned[j] = new SSLSavingTrustManager((X509TrustManager)tm, true);
						}
						else {
							cloned[j] = tmArray[j];
						}
					}
					return cloned;
				}
			}
		}
		return tmArray;
	}
	
	public static SSLSavingTrustManager read(TrustManager[] tmArray) {
		if(tmArray!=null && tmArray.length>0) {
			for (int i = 0; i < tmArray.length; i++) {
				TrustManager tm = tmArray[i];
				if(tm!=null && tm instanceof SSLSavingTrustManager) {
					return (SSLSavingTrustManager) tm;
				}
			}
		}
		return null;
	}
}
