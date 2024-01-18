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

import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeystoreType;

/**
 * OCSPTrustManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPTrustManager implements X509TrustManager {

	private IOCSPValidator ocspValidator;
	private X509TrustManager tm;
	private X509Certificate[] chain;
	private boolean trustAll;
	
	public X509Certificate[] getPeerCertificates() {
		return this.chain;
	}

	public OCSPTrustManager(X509TrustManager tm, IOCSPValidator ocspValidator) {
		this.tm = tm;
		this.ocspValidator = ocspValidator;
		this.trustAll = tm instanceof SSLTrustAllManager;
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return this.tm.getAcceptedIssuers();
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		this.tm.checkClientTrusted(chain, authType);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		this.chain = chain;
		this.tm.checkServerTrusted(chain, authType);
		
		if(chain!=null && chain.length>0) {
			
			if(this.trustAll) {
				// non ho usato un truststore, ne genero uno con i certificati server ritornati dal server, che ho accettato
				// il truststore serve per porte generare la richiesta OCSP
				if(this.ocspValidator.getTrustStore()==null) {
					try {
						java.security.KeyStore ks = java.security.KeyStore.getInstance(KeystoreType.JKS.getNome());
						ks.load(null, null);
						KeyStore trustStore = new KeyStore(ks);
						int index = 0;
						for (X509Certificate x509Certificate : chain) {
							trustStore.putCertificate("cert-"+index, x509Certificate, false);
							index++;
						}
//						java.util.Enumeration<String> en = trustStore.aliases();
//						while (en.hasMoreElements()) {
//							String alias = (String) en.nextElement();
//							System.out.println(alias);	
//						}
						this.ocspValidator.setTrustStore(trustStore);
					}catch(Throwable t) {
						throw new CertificateException(t.getMessage(),t);
					}
				}
			}
			
			try {
				// valido il certificato principale nell'OCSP (la validazione a catena avviene all'interno del motore se abilitato a partire dal certificato principale)
				this.ocspValidator.valid(chain[0]); 
			}catch(Throwable t) {
				throw new CertificateException(t.getMessage(),t);
			}
		}
	}

	public static TrustManager[] wrap(TrustManager[] tmArray, IOCSPValidator ocspValidator) {
		if(tmArray!=null && tmArray.length>0) {
			for (int i = 0; i < tmArray.length; i++) {
				TrustManager tm = tmArray[i];
				if(tm!=null && tm instanceof X509TrustManager) {
					// wrap
					// clono perche' nel caso di SSLTrustAll si tratta di una istanza statica
					TrustManager[] cloned = new TrustManager[tmArray.length];
					for (int j = 0; j < tmArray.length; j++) {
						if(j == i) {
							cloned[j] = new OCSPTrustManager((X509TrustManager)tm, ocspValidator);
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
	
	public static OCSPTrustManager read(TrustManager[] tmArray) {
		if(tmArray!=null && tmArray.length>0) {
			for (int i = 0; i < tmArray.length; i++) {
				TrustManager tm = tmArray[i];
				if(tm!=null && tm instanceof OCSPTrustManager) {
					return (OCSPTrustManager) tm;
				}
			}
		}
		return null;
	}
}
