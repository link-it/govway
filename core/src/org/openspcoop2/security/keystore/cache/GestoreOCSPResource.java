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

	private static final String OCSP_CONFIG_NULL = "OCSPConfig is null";
	
	@Override
	public KeyStore getIssuerAlternativeTrustStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		if(this.config.getAlternativeTrustStoreCAPath()!=null) {
			try {
				return GestoreKeystoreCache.getMerlinTruststore(this.requestInfo, 
						this.config.getAlternativeTrustStoreCAPath(), 
						this.config.getAlternativeTrustStoreCAType(),
						this.config.getAlternativeTrustStoreCAPassword()).getTrustStore();
			}catch(Exception t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return null;
	}

	@Override
	public void readExternalResource(String resource, Map<String, byte[]> holderResource) throws UtilsException {
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		try {
			ExternalResourceConfig externalConfig = new ExternalResourceConfig();
			
			externalConfig.setHostnameVerifier(this.config.isExternalResourcesHostnameVerifier());
			externalConfig.setTrustAllCerts(this.config.isExternalResourcesTrustAllCerts());
			if(this.config.getExternalResourcesTrustStorePath()!=null) {
				try {
					externalConfig.setTrustStore(GestoreKeystoreCache.getMerlinTruststore(this.requestInfo, 
							this.config.getExternalResourcesTrustStorePath(), 
							this.config.getExternalResourcesTrustStoreType(),
							this.config.getExternalResourcesTrustStorePassword()).getTrustStore().getKeystore());
				}catch(Exception t) {
					throw new UtilsException(t.getMessage(),t);
				}
			}
			
			if(this.config.getExternalResourcesKeyStorePath()!=null) {
				try {
					externalConfig.setKeyStore(GestoreKeystoreCache.getMerlinKeystore(this.requestInfo, 
							this.config.getExternalResourcesKeyStorePath(), 
							this.config.getExternalResourcesKeyStoreType(),
							this.config.getExternalResourcesKeyStorePassword()).getKeyStore().getKeystore());
				}catch(Exception t) {
					throw new UtilsException(t.getMessage(),t);
				}
				externalConfig.setKeyAlias(this.config.getExternalResourcesKeyAlias());
				externalConfig.setKeyPassword(this.config.getExternalResourcesKeyPassword());
			}
			
			if(this.config.getForwardProxyUrl()!=null) {
				externalConfig.setForwardProxyUrl(this.config.getForwardProxyUrl());
				externalConfig.setForwardProxyHeader(this.config.getForwardProxyHeader());
				externalConfig.setForwardProxyQueryParameter(this.config.getForwardProxyQueryParameter());
				externalConfig.setForwardProxyBase64(this.config.isForwardProxyBase64());
			}
			
			externalConfig.setConnectTimeout(this.config.getConnectTimeout());
			externalConfig.setReadTimeout(this.config.getReadTimeout());
			
			ExternalResource externalResource = GestoreKeystoreCache.getExternalResource(this.requestInfo, resource, externalConfig);
			if(externalResource!=null && externalResource.getId()!=null) {
				holderResource.put(externalResource.getId(), externalResource.getResource());
			}
		}catch(Exception t) {
			throw new UtilsException(t.getMessage(),t);
		}
	}

	@Override
	public CRLCertstore readCRL(List<String> crl, Map<String, byte[]> localResources) throws UtilsException {
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		try {
			return GestoreKeystoreCache.getCRLCertstore(this.requestInfo, CRLCertstore.convertToCrlPaths(crl), localResources).getWrappedCRLCertStore();
		}catch(Exception t) {
			throw new UtilsException(t.getMessage(),t);
		}
	}

	@Override
	public KeyStore getSignerTrustStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		if(this.config.getTrustStoreSignerPath()!=null) {
			try {
				return GestoreKeystoreCache.getMerlinTruststore(this.requestInfo, 
						this.config.getTrustStoreSignerPath(), 
						this.config.getTrustStoreSignerType(),
						this.config.getTrustStoreSignerPassword()).getTrustStore();
			}catch(Exception t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return null;
	}

	@Override
	public KeyStore getHttpsTrustStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		if(this.config.getExternalResourcesTrustStorePath()!=null) {
			try {
				return GestoreKeystoreCache.getMerlinTruststore(this.requestInfo, 
						this.config.getExternalResourcesTrustStorePath(), 
						this.config.getExternalResourcesTrustStoreType(),
						this.config.getExternalResourcesTrustStorePassword()).getTrustStore();
			}catch(Exception t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return null;
	}
	
	@Override
	public KeyStore getHttpsKeyStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		if(this.config.getExternalResourcesKeyStorePath()!=null) {
			try {
				return GestoreKeystoreCache.getMerlinKeystore(this.requestInfo, 
						this.config.getExternalResourcesKeyStorePath(), 
						this.config.getExternalResourcesKeyStoreType(),
						this.config.getExternalResourcesKeyStorePassword()).getKeyStore();
			}catch(Exception t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return null;
	}

	@Override
	public KeyStore getCrlAlternativeTrustStore() throws UtilsException{
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		if(this.config.getAlternativeTrustStoreCRLPath()!=null) {
			try {
				return GestoreKeystoreCache.getMerlinTruststore(this.requestInfo, 
						this.config.getAlternativeTrustStoreCRLPath(), 
						this.config.getAlternativeTrustStoreCRLType(),
						this.config.getAlternativeTrustStoreCRLPassword()).getTrustStore();
			}catch(Exception t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return null;
	}
}
