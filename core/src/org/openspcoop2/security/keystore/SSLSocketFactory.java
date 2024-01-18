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

import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.keystore.cache.GestoreOCSPResource;
import org.openspcoop2.security.keystore.cache.GestoreOCSPValidator;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
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
	private transient javax.net.ssl.SSLSocketFactory sslSocketFactoryObject;
	

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
		if(this.sslSocketFactoryObject==null) {
			this.initFactory(requestInfo);
		}
	}
	private String getErrorMessage(String location, Exception e, boolean keystore) {
		return "Lettura "+(keystore?"keystore":"truststore")+" '"+location+"' dalla cache fallita: "+e.getMessage();
	}
	private synchronized void initFactory(RequestInfo requestInfo) throws SecurityException{
		if(this.sslSocketFactoryObject==null) {
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
							String msgError = getErrorMessage(this.sslConfig.getKeyStoreLocation(),e,true);
							this.sslConfig.getLoggerBuffer().error(msgError, e);
						}
					}
					if(this.sslConfig.getTrustStoreLocation()!=null) {
						try {
							KeyStore truststore = GestoreKeystoreCache.getMerlinTruststore(requestInfo, this.sslConfig.getTrustStoreLocation(), 
									this.sslConfig.getTrustStoreType(), this.sslConfig.getTrustStorePassword()).getTrustStore();
							this.sslConfig.setTrustStore(truststore.getKeystore(), truststore.isKeystoreHsm());
						}catch(Exception e) {
							String msgError = getErrorMessage(this.sslConfig.getTrustStoreLocation(),e,false);
							this.sslConfig.getLoggerBuffer().error(msgError, e);
						}
					}
					
					IOCSPValidator ocspValidator = null;
					boolean crlByOcsp = false;
					if(this.sslConfig.getTrustStoreOCSPPolicy()!=null){
						String policyType = this.sslConfig.getTrustStoreOCSPPolicy();
						if(policyType!=null && StringUtils.isNotEmpty(policyType)) {
							GestoreOCSPResource ocspResourceReader = new GestoreOCSPResource(requestInfo);
							String crlInputConfig = this.sslConfig.getTrustStoreCRLsLocation();
							ocspValidator = new GestoreOCSPValidator(requestInfo, this.sslConfig.getLog4jBuffer(), crlInputConfig, policyType, ocspResourceReader);
							if(ocspValidator!=null) {
								GestoreOCSPValidator gOcspValidator = (GestoreOCSPValidator) ocspValidator;
								if(gOcspValidator.getOcspConfig()!=null) {
									crlByOcsp = gOcspValidator.getOcspConfig().isCrl();
								}
							}
						}
					}
					
					if(this.sslConfig.getTrustStoreCRLsLocation()!=null && !crlByOcsp) {
						try {
							this.sslConfig.setTrustStoreCRLs(GestoreKeystoreCache.getCRLCertstore(requestInfo, this.sslConfig.getTrustStoreCRLsLocation()).getCertStore());
						}catch(Exception e) {
							String msgError = "Lettura CRLs '"+this.sslConfig.getTrustStoreLocation()+"' dalla cache fallita: "+e.getMessage();
							this.sslConfig.getLoggerBuffer().error(msgError, e);
						}
					}
					
					StringBuilder bfSSLConfig = new StringBuilder();
					SSLContext sslContext = SSLUtilities.generateSSLContext(this.sslConfig, ocspValidator, bfSSLConfig);
					this.sslSocketFactoryObject = sslContext.getSocketFactory();
					
					if(this.sslConfig.getLoggerBuffer()!=null) {
						String msgDebug = bfSSLConfig.toString();
						this.sslConfig.getLoggerBuffer().debug(msgDebug);		
					}
				}

			}catch(Exception e){
				throw new SecurityException(e.getMessage(),e);
			}
		}
	}
	
	public javax.net.ssl.SSLSocketFactory getSslSocketFactory(RequestInfo requestInfo) throws SecurityException {
		this.checkInit(requestInfo); // per ripristino da Serializable
		return this.sslSocketFactoryObject;
	}	


}
