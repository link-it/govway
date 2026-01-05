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
package org.openspcoop2.utils.certificate.ocsp;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CRLCertstore;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.transport.http.ExternalResourceConfig;
import org.openspcoop2.utils.transport.http.ExternalResourceUtils;

/**
 * OCSPResourceReader
 * NOTA: implementazione di esempio senza cache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPResourceReader implements IOCSPResourceReader {

	private OCSPConfig config;
	
	public OCSPResourceReader() {
		// public
	}
	
	private static final String OCSP_CONFIG_NULL = "OCSPConfig is null";
	
	@Override
	public void initConfig(OCSPConfig config) throws UtilsException {
		this.config = config;
	}

	@Override
	public KeyStore getIssuerAlternativeTrustStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		if(this.config.getAlternativeTrustStoreCAPath()!=null) {
			return newKeyStore(this.config.getAlternativeTrustStoreCAPath(),
					this.config.getAlternativeTrustStoreCAType(),
					this.config.getAlternativeTrustStoreCAPassword());
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
				KeyStore ks = newKeyStore(this.config.getExternalResourcesTrustStorePath(),
						this.config.getExternalResourcesTrustStoreType(),
						this.config.getExternalResourcesTrustStorePassword());
				externalConfig.setTrustStore(ks.getKeystore());
			}
			
			if(this.config.getExternalResourcesKeyStorePath()!=null) {
				KeyStore ks = newKeyStore(this.config.getExternalResourcesKeyStorePath(),
						this.config.getExternalResourcesKeyStoreType(),
						this.config.getExternalResourcesKeyStorePassword());
				externalConfig.setKeyStore(ks.getKeystore());
				externalConfig.setKeyAlias(this.config.getExternalResourcesKeyAlias());
				externalConfig.setKeyPassword(this.config.getExternalResourcesKeyPassword());
			}
			
			externalConfig.setBasicUsername(this.config.getExternalResourcesUsername());
			externalConfig.setBasicPassword(this.config.getExternalResourcesPassword());
			
			if(this.config.getForwardProxyUrl()!=null) {
				externalConfig.setForwardProxyUrl(this.config.getForwardProxyUrl());
				externalConfig.setForwardProxyHeader(this.config.getForwardProxyHeader());
				externalConfig.setForwardProxyQueryParameter(this.config.getForwardProxyQueryParameter());
				externalConfig.setForwardProxyBase64(this.config.isForwardProxyBase64());
			}
			
			externalConfig.setConnectTimeout(this.config.getConnectTimeout());
			externalConfig.setReadTimeout(this.config.getReadTimeout());
			
			byte [] r = ExternalResourceUtils.readResource(resource, externalConfig);
			if(r!=null) {
				holderResource.put(resource, r);
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
			return new CRLCertstore(crl, localResources);
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
			return newKeyStore(this.config.getTrustStoreSignerPath(),
					this.config.getTrustStoreSignerType(),
					this.config.getTrustStoreSignerPassword());
		}
		return null;
	}

	@Override
	public KeyStore getHttpsTrustStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		if(this.config.getExternalResourcesTrustStorePath()!=null) {
			return newKeyStore(this.config.getExternalResourcesTrustStorePath(),
					this.config.getExternalResourcesTrustStoreType(),
					this.config.getExternalResourcesTrustStorePassword());
		}
		return null;
	}
	
	@Override
	public KeyStore getHttpsKeyStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		if(this.config.getExternalResourcesKeyStorePath()!=null) {
			return newKeyStore(this.config.getExternalResourcesKeyStorePath(),
					this.config.getExternalResourcesKeyStoreType(),
					this.config.getExternalResourcesKeyStorePassword());
		}
		return null;
	}

	@Override
	public KeyStore getCrlAlternativeTrustStore() throws UtilsException{
		if(this.config==null) {
			throw new UtilsException(OCSP_CONFIG_NULL);
		}
		if(this.config.getAlternativeTrustStoreCRLPath()!=null) {
			return newKeyStore(this.config.getAlternativeTrustStoreCRLPath(),
					this.config.getAlternativeTrustStoreCRLType(),
					this.config.getAlternativeTrustStoreCRLPassword());
		}
		return null;
	}
	
	private static KeyStore newKeyStore(String path, String type, String password) throws UtilsException {
		File f = new File(path);
		if(!f.exists()) {
			try(InputStream is = OCSPResourceReader.class.getResourceAsStream(path)){
				if(is!=null) {
					byte[] content = Utilities.getAsByteArray(is);
					return new KeyStore(content, type,	password);
				}
			}
			catch(Exception t) {
				throw new UtilsException(t.getMessage(),t);
			}
		}
		return new KeyStore(path, type,	password);
	}

}
