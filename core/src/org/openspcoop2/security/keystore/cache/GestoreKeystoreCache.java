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

package org.openspcoop2.security.keystore.cache;

import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.HttpStore;
import org.openspcoop2.security.keystore.JWKSetStore;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.MerlinTruststore;
import org.openspcoop2.security.keystore.MultiKeystore;
import org.openspcoop2.security.keystore.SSLSocketFactory;
import org.openspcoop2.security.keystore.SymmetricKeystore;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.transport.http.SSLConfig;

/**
 * GestoreKeystoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreKeystoreCache {

	private static final MerlinTruststoreCache merlinTruststoreCache = new MerlinTruststoreCache();
	private static final MerlinKeystoreCache merlinKeystoreCache = new MerlinKeystoreCache();
	private static final SymmetricKeystoreCache symmetricKeystoreCache = new SymmetricKeystoreCache();
	private static final MultiKeystoreCache multiKeystoreCache = new MultiKeystoreCache();
	private static final JWKSetStoreCache jwkSetStoreCache = new JWKSetStoreCache();
	private static final HttpStoreCache httpStoreCache = new HttpStoreCache();
	private static final CRLCertstoreCache crlCertstoreCache = new CRLCertstoreCache();
	private static final SSLSocketFactoryCache sslSocketFactoryCache = new SSLSocketFactoryCache();
	private static boolean cacheEnabled = false;
	
	public static void setKeystoreCacheParameters(boolean cacheEnabled,int cacheLifeSecond,int cacheSize){
		GestoreKeystoreCache.cacheEnabled = cacheEnabled;
		GestoreKeystoreCache.merlinTruststoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.merlinKeystoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.symmetricKeystoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.multiKeystoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.jwkSetStoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.httpStoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.crlCertstoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.sslSocketFactoryCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
	}
	public static void setKeystoreCacheJCS(boolean cacheEnabled,int cacheLifeSecond, Cache cacheJCS){
		GestoreKeystoreCache.cacheEnabled = cacheEnabled;
		GestoreKeystoreCache.merlinTruststoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.merlinKeystoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.symmetricKeystoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.multiKeystoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.jwkSetStoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.httpStoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.crlCertstoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.sslSocketFactoryCache.setCacheJCS(cacheLifeSecond, cacheJCS);
	}
	public static void setKeystoreCacheJCS_crlLifeSeconds(int cacheCrlLifeSecond){
		GestoreKeystoreCache.crlCertstoreCache.updateCacheLifeSecond(cacheCrlLifeSecond);
	}
	
	
	public static MerlinTruststore getMerlinTruststore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && propertyFilePath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getMerlinTruststore(propertyFilePath);
			if(o!=null && o instanceof MerlinTruststore) {
				return (MerlinTruststore) o;
			}
		}
		MerlinTruststore t = null;
		if(GestoreKeystoreCache.cacheEnabled)
			t = GestoreKeystoreCache.merlinTruststoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath);
		else
			t = new MerlinTruststore(propertyFilePath);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addMerlinTruststore(propertyFilePath, t, requestInfo.getIdTransazione());
		}
		return t;
	}
	public static MerlinTruststore getMerlinTruststore(RequestInfo requestInfo, String pathStore,String tipoStore,String passwordStore) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && pathStore!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getMerlinTruststore(pathStore);
			if(o!=null && o instanceof MerlinTruststore) {
				return (MerlinTruststore) o;
			}
		}
		MerlinTruststore t = null;
		if(GestoreKeystoreCache.cacheEnabled)
			t = GestoreKeystoreCache.merlinTruststoreCache.getKeystoreAndCreateIfNotExists(pathStore, tipoStore, passwordStore);
		else
			t = new MerlinTruststore(pathStore, tipoStore, passwordStore);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addMerlinTruststore(pathStore, t, requestInfo.getIdTransazione());
		}
		return t;
	}
	public static MerlinTruststore getMerlinTruststore(RequestInfo requestInfo, byte[] bytesStore,String tipoStore,String passwordStore) throws SecurityException{
		String keyParam = null;
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && bytesStore!=null;
		if(useRequestInfo) {
			keyParam = AbstractKeystoreCache.buildKeyCacheFromBytes(bytesStore);
			Object o = requestInfo.getRequestConfig().getMerlinTruststore(keyParam);
			if(o!=null && o instanceof MerlinTruststore) {
				return (MerlinTruststore) o;
			}
		}
		MerlinTruststore t = null;
		if(GestoreKeystoreCache.cacheEnabled)
			t = GestoreKeystoreCache.merlinTruststoreCache.getKeystoreAndCreateIfNotExists(bytesStore, tipoStore, passwordStore);
		else
			t = new MerlinTruststore(bytesStore, tipoStore, passwordStore);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addMerlinTruststore(keyParam, t, requestInfo.getIdTransazione());
		}
		return t;
	}
	
	
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && propertyFilePath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getMerlinKeystore(propertyFilePath);
			if(o!=null && o instanceof MerlinKeystore) {
				return (MerlinKeystore) o;
			}
		}
		MerlinKeystore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath);
		else
			k = new MerlinKeystore(propertyFilePath);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addMerlinKeystore(propertyFilePath, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo, String propertyFilePath,String passwordPrivateKey) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && propertyFilePath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getMerlinKeystore(propertyFilePath);
			if(o!=null && o instanceof MerlinKeystore) {
				return (MerlinKeystore) o;
			}
		}
		MerlinKeystore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath, passwordPrivateKey);
		else
			k = new MerlinKeystore(propertyFilePath, passwordPrivateKey);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addMerlinKeystore(propertyFilePath, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo, String pathStore,String tipoStore,String passwordStore) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && pathStore!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getMerlinKeystore(pathStore);
			if(o!=null && o instanceof MerlinKeystore) {
				return (MerlinKeystore) o;
			}
		}
		MerlinKeystore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(pathStore, tipoStore, passwordStore);
		else
			k = new MerlinKeystore(pathStore, tipoStore, passwordStore);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addMerlinKeystore(pathStore, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo, String pathStore,String tipoStore,String passwordStore,String passwordPrivateKey) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && pathStore!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getMerlinKeystore(pathStore);
			if(o!=null && o instanceof MerlinKeystore) {
				return (MerlinKeystore) o;
			}
		}
		MerlinKeystore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(pathStore, tipoStore, passwordStore, passwordPrivateKey);
		else
			k = new MerlinKeystore(pathStore, tipoStore, passwordStore, passwordPrivateKey);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addMerlinKeystore(pathStore, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo, byte[] bytesStore,String tipoStore,String passwordStore) throws SecurityException{
		String keyParam = null;
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && bytesStore!=null;
		if(useRequestInfo) {
			keyParam = AbstractKeystoreCache.buildKeyCacheFromBytes(bytesStore);
			Object o = requestInfo.getRequestConfig().getMerlinKeystore(keyParam);
			if(o!=null && o instanceof MerlinKeystore) {
				return (MerlinKeystore) o;
			}
		}
		MerlinKeystore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(bytesStore, tipoStore, passwordStore);
		else
			k = new MerlinKeystore(bytesStore, tipoStore, passwordStore);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addMerlinKeystore(keyParam, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo,byte[] bytesStore,String tipoStore,String passwordStore,String passwordPrivateKey) throws SecurityException{
		String keyParam = null;
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && bytesStore!=null;
		if(useRequestInfo) {
			keyParam = AbstractKeystoreCache.buildKeyCacheFromBytes(bytesStore);
			Object o = requestInfo.getRequestConfig().getMerlinKeystore(keyParam);
			if(o!=null && o instanceof MerlinKeystore) {
				return (MerlinKeystore) o;
			}
		}
		MerlinKeystore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(bytesStore, tipoStore, passwordStore, passwordPrivateKey);
		else
			k = new MerlinKeystore(bytesStore, tipoStore, passwordStore, passwordPrivateKey);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addMerlinKeystore(keyParam, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	
	public static SymmetricKeystore getSymmetricKeystore(RequestInfo requestInfo, String alias,String key,String algoritmo) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && key!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getSymmetricKeystore(key);
			if(o!=null && o instanceof SymmetricKeystore) {
				return (SymmetricKeystore) o;
			}
		}
		SymmetricKeystore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.symmetricKeystoreCache.getKeystoreAndCreateIfNotExists(key, alias, algoritmo);
		else
			k = new SymmetricKeystore(alias, key, algoritmo);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addSymmetricKeystore(key, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	
	public static MultiKeystore getMultiKeystore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && propertyFilePath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getMultiKeystore(propertyFilePath);
			if(o!=null && o instanceof MultiKeystore) {
				return (MultiKeystore) o;
			}
		}
		MultiKeystore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.multiKeystoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath);
		else
			k = new MultiKeystore(propertyFilePath);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addMultiKeystore(propertyFilePath, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	
	public static JWKSetStore getJwkSetStore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && propertyFilePath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getJWKSetStore(propertyFilePath);
			if(o!=null && o instanceof JWKSetStore) {
				return (JWKSetStore) o;
			}
		}
		JWKSetStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.jwkSetStoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath);
		else
			k = new JWKSetStore(propertyFilePath);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addJWKSetStore(propertyFilePath, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static JWKSetStore getJwkSetStore(RequestInfo requestInfo, byte[] archive) throws SecurityException{
		String keyParam = null;
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && archive!=null;
		if(useRequestInfo) {
			keyParam = AbstractKeystoreCache.buildKeyCacheFromBytes(archive);
			Object o = requestInfo.getRequestConfig().getJWKSetStore(keyParam);
			if(o!=null && o instanceof JWKSetStore) {
				return (JWKSetStore) o;
			}
		}
		JWKSetStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.jwkSetStoreCache.getKeystoreAndCreateIfNotExists(archive);
		else
			k = new JWKSetStore(archive);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addJWKSetStore(keyParam, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o!=null && o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint);
		else
			k = new HttpStore(endpoint);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, Integer connectionTimeout, Integer readTimeout) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o!=null && o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, connectionTimeout, readTimeout);
		else
			k = new HttpStore(endpoint, connectionTimeout, readTimeout);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, MerlinTruststore trustStoreSsl) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o!=null && o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, trustStoreSsl);
		else
			k = new HttpStore(endpoint, trustStoreSsl);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o!=null && o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, trustStoreSsl, crlTrustStoreSsl);
		else
			k = new HttpStore(endpoint, trustStoreSsl, crlTrustStoreSsl);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o!=null && o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl);
		else
			k = new HttpStore(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo,String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o!=null && o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl, crlTrustStoreSsl);
		else
			k = new HttpStore(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl, crlTrustStoreSsl);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	
	public static CRLCertstore getCRLCertstore(RequestInfo requestInfo,String crlPath) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && crlPath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getCRLCertstore(crlPath);
			if(o!=null && o instanceof CRLCertstore) {
				return (CRLCertstore) o;
			}
		}
		CRLCertstore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.crlCertstoreCache.getKeystoreAndCreateIfNotExists(crlPath);
		else
			k = new CRLCertstore(crlPath);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addCRLCertstore(crlPath, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	public static SSLSocketFactory getSSLSocketFactory(RequestInfo requestInfo,SSLConfig sslConfig) throws SecurityException{
		String sslConfigKey = sslConfig.toString();
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && sslConfigKey!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getSSLSocketFactory(sslConfigKey);
			if(o!=null && o instanceof SSLSocketFactory) {
				return (SSLSocketFactory) o;
			}
		}
		SSLSocketFactory k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.sslSocketFactoryCache.getKeystoreAndCreateIfNotExists(sslConfigKey, requestInfo, sslConfig);
		else
			k = new SSLSocketFactory(requestInfo, sslConfig);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addSSLSocketFactory(sslConfigKey, k, requestInfo.getIdTransazione());
		}
		return k;	
	}
}
