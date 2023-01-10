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

package org.openspcoop2.security.keystore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import javax.net.ssl.SSLContext;

import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.transport.http.SSLConfig;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**
 * SSLSocketFactory
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLSocketFactory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SSLConfig sslConfig;
	private transient javax.net.ssl.SSLSocketFactory sslSocketFactory;
	

	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("SSLContextFactory (").append(this.sslConfig).append(") ");
		return bf.toString();
	}
	
	public SSLSocketFactory(RequestInfo requestInfo, SSLConfig sslConfig) throws SecurityException{
		this.sslConfig = sslConfig;
		this.initFactory(requestInfo);
	}
	
	private void checkInit(RequestInfo requestInfo) throws SecurityException{
		if(this.sslSocketFactory==null) {
			this.initFactory(requestInfo);
		}
	}
	private synchronized void initFactory(RequestInfo requestInfo) throws SecurityException{
		if(this.sslSocketFactory==null) {
			try{
				// Gestione https
				if(this.sslConfig!=null){
					
					// provo a leggere i keystore dalla cache
					if(this.sslConfig.getKeyStoreLocation()!=null) {
						try {
							KeyStore keystore = GestoreKeystoreCache.getMerlinKeystore(requestInfo, this.sslConfig.getKeyStoreLocation(), 
									this.sslConfig.getKeyStoreType(), this.sslConfig.getKeyStorePassword()).getKeyStore();
							this.sslConfig.setKeyStore(keystore.getKeystore(), keystore.isKeystoreHsm());
						}catch(Exception e) {
							String msgError = "Lettura keystore '"+this.sslConfig.getKeyStoreLocation()+"' dalla cache fallita: "+e.getMessage();
							logError(msgError, e);
						}
					}
					if(this.sslConfig.getTrustStoreLocation()!=null) {
						try {
							KeyStore truststore = GestoreKeystoreCache.getMerlinTruststore(requestInfo, this.sslConfig.getTrustStoreLocation(), 
									this.sslConfig.getTrustStoreType(), this.sslConfig.getTrustStorePassword()).getTrustStore();
							this.sslConfig.setTrustStore(truststore.getKeystore(), truststore.isKeystoreHsm());
						}catch(Exception e) {
							String msgError = "Lettura truststore '"+this.sslConfig.getTrustStoreLocation()+"' dalla cache fallita: "+e.getMessage();
							logError(msgError, e);
						}
					}
					if(this.sslConfig.getTrustStoreCRLsLocation()!=null) {
						try {
							this.sslConfig.setTrustStoreCRLs(GestoreKeystoreCache.getCRLCertstore(requestInfo, this.sslConfig.getTrustStoreCRLsLocation()).getCertStore());
						}catch(Exception e) {
							String msgError = "Lettura CRLs '"+this.sslConfig.getTrustStoreLocation()+"' dalla cache fallita: "+e.getMessage();
							logError(msgError, e);
						}
					}
					
					StringBuilder bfSSLConfig = new StringBuilder();
					SSLContext sslContext = SSLUtilities.generateSSLContext(this.sslConfig, bfSSLConfig);
					this.sslSocketFactory = sslContext.getSocketFactory();
					
					if(this.sslConfig.getSbDebug()!=null) {
						String msgDebug = bfSSLConfig.toString();
						if(this.sslConfig.getSbDebug().length()>0) {
							this.sslConfig.getSbDebug().append("\n");
						}
						this.sslConfig.getSbDebug().append(msgDebug);		
					}
				}

			}catch(Exception e){
				throw new SecurityException(e.getMessage(),e);
			}
		}
	}
	
	public javax.net.ssl.SSLSocketFactory getSslSocketFactory(RequestInfo requestInfo) throws SecurityException {
		this.checkInit(requestInfo); // per ripristino da Serializable
		return this.sslSocketFactory;
	}	

	private void logError(String msgError, Exception e) throws IOException {
		if(this.sslConfig.getSbError()!=null || this.sslConfig.getSbDebug()!=null) {
			if(this.sslConfig.getSbError()!=null) {
				if(this.sslConfig.getSbError().length()>0) {
					this.sslConfig.getSbError().append("\n");
				}
				this.sslConfig.getSbError().append(msgError);
			}
			if(this.sslConfig.getSbDebug()!=null) {
				if(this.sslConfig.getSbDebug().length()>0) {
					this.sslConfig.getSbDebug().append("\n");
				}
				this.sslConfig.getSbDebug().append(msgError);
			}
			try( ByteArrayOutputStream out = new ByteArrayOutputStream(); PrintStream ps = new PrintStream(out);){
				e.printStackTrace(ps);
				ps.flush();
				out.flush();
				if(this.sslConfig.getSbError()!=null) {
					if(this.sslConfig.getSbError().length()>0) {
						this.sslConfig.getSbError().append("\n");
					}
					this.sslConfig.getSbError().append(out.toString());
				}
				if(this.sslConfig.getSbDebug()!=null) {
					if(this.sslConfig.getSbDebug().length()>0) {
						this.sslConfig.getSbDebug().append("\n");
					}
					this.sslConfig.getSbDebug().append(out.toString());
				}
			}
		}
	}
}
