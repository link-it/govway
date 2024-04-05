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
			return newKeyStore(this.config.getAlternativeTrustStoreCA_path(),
					this.config.getAlternativeTrustStoreCA_type(),
					this.config.getAlternativeTrustStoreCA_password());
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
				KeyStore ks = newKeyStore(this.config.getExternalResources_trustStorePath(),
						this.config.getExternalResources_trustStoreType(),
						this.config.getExternalResources_trustStorePassword());
				externalConfig.setTrustStore(ks.getKeystore());
			}
			
			if(this.config.getExternalResources_keyStorePath()!=null) {
				KeyStore ks = newKeyStore(this.config.getExternalResources_keyStorePath(),
						this.config.getExternalResources_keyStoreType(),
						this.config.getExternalResources_keyStorePassword());
				externalConfig.setKeyStore(ks.getKeystore());
				externalConfig.setKeyAlias(this.config.getExternalResources_keyAlias());
				externalConfig.setKeyPassword(this.config.getExternalResources_keyPassword());
			}
			
			if(this.config.getForwardProxy_url()!=null) {
				externalConfig.setForwardProxyUrl(this.config.getForwardProxy_url());
				externalConfig.setForwardProxyHeader(this.config.getForwardProxy_header());
				externalConfig.setForwardProxyQueryParameter(this.config.getForwardProxy_queryParameter());
				externalConfig.setForwardProxyBase64(this.config.isForwardProxy_base64());
			}
			
			externalConfig.setConnectTimeout(this.config.getConnectTimeout());
			externalConfig.setReadTimeout(this.config.getReadTimeout());
			
			byte [] r = ExternalResourceUtils.readResource(resource, externalConfig);
			if(r!=null) {
				holderResource.put(resource, r);
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
			return new CRLCertstore(crl, localResources);
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
			return newKeyStore(this.config.getTrustStoreSigner_path(),
					this.config.getTrustStoreSigner_type(),
					this.config.getTrustStoreSigner_password());
		}
		return null;
	}

	@Override
	public KeyStore getHttpsTrustStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException("OCSPConfig is null");
		}
		if(this.config.getExternalResources_trustStorePath()!=null) {
			return newKeyStore(this.config.getExternalResources_trustStorePath(),
					this.config.getExternalResources_trustStoreType(),
					this.config.getExternalResources_trustStorePassword());
		}
		return null;
	}
	
	@Override
	public KeyStore getHttpsKeyStore() throws UtilsException {
		if(this.config==null) {
			throw new UtilsException("OCSPConfig is null");
		}
		if(this.config.getExternalResources_keyStorePath()!=null) {
			return newKeyStore(this.config.getExternalResources_keyStorePath(),
					this.config.getExternalResources_keyStoreType(),
					this.config.getExternalResources_keyStorePassword());
		}
		return null;
	}

	@Override
	public KeyStore getCrlAlternativeTrustStore() throws UtilsException{
		if(this.config==null) {
			throw new UtilsException("OCSPConfig is null");
		}
		if(this.config.getAlternativeTrustStoreCRL_path()!=null) {
			return newKeyStore(this.config.getAlternativeTrustStoreCRL_path(),
					this.config.getAlternativeTrustStoreCRL_type(),
					this.config.getAlternativeTrustStoreCRL_password());
		}
		return null;
	}
	
	private static KeyStore newKeyStore(String path, String type, String password) throws UtilsException {
		File f = new File(path);
		if(!f.exists()) {
			//File fClass = null;
			try(InputStream is = OCSPResourceReader.class.getResourceAsStream(path)){
				if(is!=null) {
					byte[] content = Utilities.getAsByteArray(is);
					//fClass = File.createTempFile("test", ".crl");
					//org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(fClass, content);
					return new KeyStore(content, type,	password);
				}
			}
			catch(Throwable t) {
				throw new UtilsException(t.getMessage(),t);
			}
			finally {
//				if(fClass!=null) {
//					if(!fClass.delete()) {
//						// ignore
//					}
//				}
			}
		}
		return new KeyStore(path, type,	password);
	}

}
