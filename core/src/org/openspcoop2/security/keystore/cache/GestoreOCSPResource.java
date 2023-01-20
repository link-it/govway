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
package org.openspcoop2.security.keystore.cache;

import java.util.List;
import java.util.Map;

import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.keystore.ExternalResource;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CRLCertstore;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.ocsp.IOCSPResourceReader;
import org.openspcoop2.utils.certificate.ocsp.OCSPConfig;
import org.openspcoop2.utils.transport.http.ExternalResourceConfig;

/**
 * GestoreOCSPResource
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreOCSPResource implements IOCSPResourceReader {

	private OCSPConfig config;
	private RequestInfo requestInfo;
	
	public GestoreOCSPResource(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
	
	@Override
	public void initConfig(OCSPConfig config) throws UtilsException {
		this.config = config;
	}

	@Override
	public KeyStore getIssuerAlternativeTrustStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException("OCSPConfig is null");
		}
		if(this.config.getAlternativeTrustStoreCA_path()!=null) {
			try {
				return GestoreKeystoreCache.getMerlinTruststore(this.requestInfo, 
						this.config.getAlternativeTrustStoreCA_path(), 
						this.config.getAlternativeTrustStoreCA_type(),
						this.config.getAlternativeTrustStoreCA_password()).getTrustStore();
			}catch(Throwable t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return null;
	}

	@Override
	public void readExternalResource(String resource, Map<String, byte[]> holderResource) throws UtilsException {
		if(this.config==null) {
			throw new UtilsException("OCSPConfig is null");
		}
		try {
			ExternalResourceConfig externalConfig = new ExternalResourceConfig();
			
			externalConfig.setHostnameVerifier(this.config.isExternalResources_hostnameVerifier());
			externalConfig.setTrustAllCerts(this.config.isExternalResources_trustAllCerts());
			if(this.config.getExternalResources_trustStorePath()!=null) {
				try {
					externalConfig.setTrustStore(GestoreKeystoreCache.getMerlinTruststore(this.requestInfo, 
							this.config.getExternalResources_trustStorePath(), 
							this.config.getExternalResources_trustStoreType(),
							this.config.getExternalResources_trustStorePassword()).getTrustStore().getKeystore());
				}catch(Throwable t) {
					throw new UtilsException(t.getMessage(),t);
				}
			}
			
			if(this.config.getExternalResources_keyStorePath()!=null) {
				try {
					externalConfig.setKeyStore(GestoreKeystoreCache.getMerlinKeystore(this.requestInfo, 
							this.config.getExternalResources_keyStorePath(), 
							this.config.getExternalResources_keyStoreType(),
							this.config.getExternalResources_keyStorePassword()).getKeyStore().getKeystore());
				}catch(Throwable t) {
					throw new UtilsException(t.getMessage(),t);
				}
				externalConfig.setKeyAlias(this.config.getExternalResources_keyAlias());
				externalConfig.setKeyPassword(this.config.getExternalResources_keyPassword());
			}
			
			if(this.config.getForwardProxy_url()!=null) {
				externalConfig.setForwardProxy_url(this.config.getForwardProxy_url());
				externalConfig.setForwardProxy_header(this.config.getForwardProxy_header());
				externalConfig.setForwardProxy_queryParameter(this.config.getForwardProxy_queryParameter());
				externalConfig.setForwardProxy_base64(this.config.isForwardProxy_base64());
			}
			
			externalConfig.setConnectTimeout(this.config.getConnectTimeout());
			externalConfig.setReadTimeout(this.config.getReadTimeout());
			
			ExternalResource externalResource = GestoreKeystoreCache.getExternalResource(this.requestInfo, resource, externalConfig);
			if(externalResource!=null && externalResource.getId()!=null) {
				holderResource.put(externalResource.getId(), externalResource.getResource());
			}
		}catch(Throwable t) {
			throw new UtilsException(t.getMessage(),t);
		}
	}

	@Override
	public CRLCertstore readCRL(List<String> crl, Map<String, byte[]> localResources) throws UtilsException {
		if(this.config==null) {
			throw new UtilsException("OCSPConfig is null");
		}
		try {
			return GestoreKeystoreCache.getCRLCertstore(this.requestInfo, CRLCertstore.convertToCrlPaths(crl), localResources).getWrappedCRLCertStore();
		}catch(Throwable t) {
			throw new UtilsException(t.getMessage(),t);
		}
	}

	@Override
	public KeyStore getSignerTrustStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException("OCSPConfig is null");
		}
		if(this.config.getTrustStoreSigner_path()!=null) {
			try {
				return GestoreKeystoreCache.getMerlinTruststore(this.requestInfo, 
						this.config.getTrustStoreSigner_path(), 
						this.config.getTrustStoreSigner_type(),
						this.config.getTrustStoreSigner_password()).getTrustStore();
			}catch(Throwable t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return null;
	}

	@Override
	public KeyStore getHttpsTrustStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException("OCSPConfig is null");
		}
		if(this.config.getExternalResources_trustStorePath()!=null) {
			try {
				return GestoreKeystoreCache.getMerlinTruststore(this.requestInfo, 
						this.config.getExternalResources_trustStorePath(), 
						this.config.getExternalResources_trustStoreType(),
						this.config.getExternalResources_trustStorePassword()).getTrustStore();
			}catch(Throwable t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return null;
	}
	
	@Override
	public KeyStore getHttpsKeyStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException("OCSPConfig is null");
		}
		if(this.config.getExternalResources_keyStorePath()!=null) {
			try {
				return GestoreKeystoreCache.getMerlinKeystore(this.requestInfo, 
						this.config.getExternalResources_keyStorePath(), 
						this.config.getExternalResources_keyStoreType(),
						this.config.getExternalResources_keyStorePassword()).getKeyStore();
			}catch(Throwable t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return null;
	}

	@Override
	public KeyStore getCrlAlternativeTrustStore() throws UtilsException{
		if(this.config==null) {
			throw new UtilsException("OCSPConfig is null");
		}
		if(this.config.getAlternativeTrustStoreCRL_path()!=null) {
			try {
				return GestoreKeystoreCache.getMerlinTruststore(this.requestInfo, 
						this.config.getAlternativeTrustStoreCRL_path(), 
						this.config.getAlternativeTrustStoreCRL_type(),
						this.config.getAlternativeTrustStoreCRL_password()).getTrustStore();
			}catch(Throwable t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return null;
	}
}
