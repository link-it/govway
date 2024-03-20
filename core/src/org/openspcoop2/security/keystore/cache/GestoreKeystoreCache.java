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

package org.openspcoop2.security.keystore.cache;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.ExternalResource;
import org.openspcoop2.security.keystore.HttpStore;
import org.openspcoop2.security.keystore.JWKSetStore;
import org.openspcoop2.security.keystore.KeyPairStore;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.MerlinTruststore;
import org.openspcoop2.security.keystore.MultiKeystore;
import org.openspcoop2.security.keystore.OCSPResponse;
import org.openspcoop2.security.keystore.PublicKeyStore;
import org.openspcoop2.security.keystore.RemoteStore;
import org.openspcoop2.security.keystore.SSLConfigProps;
import org.openspcoop2.security.keystore.SSLSocketFactory;
import org.openspcoop2.security.keystore.SecretKeyStore;
import org.openspcoop2.security.keystore.SymmetricKeystore;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.remote.IRemoteStoreProvider;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreClientInfo;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.transport.http.ExternalResourceConfig;
import org.openspcoop2.utils.transport.http.HttpOptions;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.openspcoop2.utils.transport.http.SSLConfig;

/**
 * GestoreKeystoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreKeystoreCache {
	
	private GestoreKeystoreCache() {}

	private static final MerlinTruststoreCache merlinTruststoreCache = new MerlinTruststoreCache();
	private static final MerlinKeystoreCache merlinKeystoreCache = new MerlinKeystoreCache();
	private static final SymmetricKeystoreCache symmetricKeystoreCache = new SymmetricKeystoreCache();
	private static final MultiKeystoreCache multiKeystoreCache = new MultiKeystoreCache();
	private static final JWKSetStoreCache jwkSetStoreCache = new JWKSetStoreCache();
	private static final KeyPairStoreCache keyPairStoreCache = new KeyPairStoreCache();
	private static final PublicKeyStoreCache publicKeyStoreCache = new PublicKeyStoreCache();
	private static final SecretKeyStoreCache secretKeyStoreCache = new SecretKeyStoreCache();
	private static final RemoteStoreCache remoteStoreCache = new RemoteStoreCache();
	private static final RemoteStoreClientInfoCache remoteStoreClientInfoCache = new RemoteStoreClientInfoCache();
	private static final HttpStoreCache httpStoreCache = new HttpStoreCache();
	private static final CRLCertstoreCache crlCertstoreCache = new CRLCertstoreCache();
	private static final SSLSocketFactoryCache sslSocketFactoryCache = new SSLSocketFactoryCache();
	private static final SSLConfigPropsCache sslConfigPropsCache = new SSLConfigPropsCache();
	private static final ExternalResourceCache externalResourceCache = new ExternalResourceCache();
	private static final OCSPResponseCache ocspResponseCache = new OCSPResponseCache();
	private static boolean cacheEnabled = false;
	
	public static void setKeystoreCacheParameters(boolean cacheEnabled,int cacheLifeSecond,int cacheSize){
		GestoreKeystoreCache.cacheEnabled = cacheEnabled;
		GestoreKeystoreCache.merlinTruststoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.merlinKeystoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.symmetricKeystoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.multiKeystoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.jwkSetStoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.keyPairStoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.publicKeyStoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.secretKeyStoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.remoteStoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.remoteStoreClientInfoCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.httpStoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.crlCertstoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.sslSocketFactoryCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.sslConfigPropsCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.externalResourceCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.ocspResponseCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
	}
	public static void setKeystoreCacheJCS(boolean cacheEnabled,int cacheLifeSecond, Cache cacheJCS){
		GestoreKeystoreCache.cacheEnabled = cacheEnabled;
		GestoreKeystoreCache.merlinTruststoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.merlinKeystoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.symmetricKeystoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.multiKeystoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.jwkSetStoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.keyPairStoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.publicKeyStoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.secretKeyStoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.remoteStoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.remoteStoreClientInfoCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.httpStoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.crlCertstoreCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.sslSocketFactoryCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.sslConfigPropsCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.externalResourceCache.setCacheJCS(cacheLifeSecond, cacheJCS);
		GestoreKeystoreCache.ocspResponseCache.setCacheJCS(cacheLifeSecond, cacheJCS);
	}
	public static void setKeystoreCacheJCSCrlLifeSeconds(int cacheCrlLifeSecond){
		GestoreKeystoreCache.crlCertstoreCache.updateCacheLifeSecond(cacheCrlLifeSecond);
		GestoreKeystoreCache.ocspResponseCache.updateCacheLifeSecond(cacheCrlLifeSecond);
		GestoreKeystoreCache.externalResourceCache.updateCacheLifeSecond(cacheCrlLifeSecond); // Per adesso utilizzo la stessa impostazione che consente di indicare un tempo minore rispetto al resto della cache
	}
	
	
	public static MerlinTruststore getMerlinTruststore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && propertyFilePath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getMerlinTruststore(propertyFilePath);
			if(o instanceof MerlinTruststore) {
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
			if(o instanceof MerlinTruststore) {
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
			if(o instanceof MerlinTruststore) {
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
			if(o instanceof MerlinKeystore) {
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
			if(o instanceof MerlinKeystore) {
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
			if(o instanceof MerlinKeystore) {
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
			if(o instanceof MerlinKeystore) {
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
			if(o instanceof MerlinKeystore) {
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
			if(o instanceof MerlinKeystore) {
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
			if(o instanceof SymmetricKeystore) {
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
			if(o instanceof MultiKeystore) {
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
			if(o instanceof JWKSetStore) {
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
			if(o instanceof JWKSetStore) {
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
	
	
	
	public static KeyPairStore getKeyPairStore(RequestInfo requestInfo, String privateKeyPath, String publicKeyPath, String privateKeyPassword, String algorithm) throws SecurityException{
		// come chiave di cache si usa solo la chiave privata
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && privateKeyPath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getKeyPairStore(privateKeyPath);
			if(o instanceof KeyPairStore) {
				return (KeyPairStore) o;
			}
		}
		KeyPairStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.keyPairStoreCache.getKeystoreAndCreateIfNotExists(privateKeyPath, publicKeyPath, privateKeyPassword, algorithm);
		else
			k = new KeyPairStore(privateKeyPath, publicKeyPath, privateKeyPassword, algorithm);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addKeyPairStore(privateKeyPath, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static KeyPairStore getKeyPairStore(RequestInfo requestInfo, byte[] privateKey, byte[] publicKey, String privateKeyPassword, String algorithm) throws SecurityException{
		// come chiave di cache si usa solo la chiave privata
		String keyParam = null;
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && privateKey!=null;
		if(useRequestInfo) {
			keyParam = AbstractKeystoreCache.buildKeyCacheFromBytes(privateKey);
			Object o = requestInfo.getRequestConfig().getKeyPairStore(keyParam);
			if(o instanceof KeyPairStore) {
				return (KeyPairStore) o;
			}
		}
		KeyPairStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.keyPairStoreCache.getKeystoreAndCreateIfNotExists(privateKey, publicKey, privateKeyPassword, algorithm);
		else
			k = new KeyPairStore(privateKey, publicKey, privateKeyPassword, algorithm);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addKeyPairStore(keyParam, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	
	
	public static PublicKeyStore getPublicKeyStore(RequestInfo requestInfo, String publicKeyPath, String algorithm) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && publicKeyPath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getPublicKeyStore(publicKeyPath);
			if(o instanceof PublicKeyStore) {
				return (PublicKeyStore) o;
			}
		}
		PublicKeyStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.publicKeyStoreCache.getKeystoreAndCreateIfNotExists(publicKeyPath, algorithm);
		else
			k = new PublicKeyStore(publicKeyPath, algorithm);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addPublicKeyStore(publicKeyPath, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static PublicKeyStore getPublicKeyStore(RequestInfo requestInfo, byte[] publicKey, String algorithm) throws SecurityException{
		String keyParam = null;
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && publicKey!=null;
		if(useRequestInfo) {
			keyParam = AbstractKeystoreCache.buildKeyCacheFromBytes(publicKey);
			Object o = requestInfo.getRequestConfig().getPublicKeyStore(keyParam);
			if(o instanceof PublicKeyStore) {
				return (PublicKeyStore) o;
			}
		}
		PublicKeyStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.publicKeyStoreCache.getKeystoreAndCreateIfNotExists(publicKey, algorithm);
		else
			k = new PublicKeyStore(publicKey, algorithm);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addPublicKeyStore(keyParam, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	
	
	
	
	
	public static SecretKeyStore getSecretKeyStore(RequestInfo requestInfo, String secretKeyPath, String algorithm) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && secretKeyPath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getSecretKeyStore(secretKeyPath);
			if(o instanceof SecretKeyStore) {
				return (SecretKeyStore) o;
			}
		}
		SecretKeyStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.secretKeyStoreCache.getKeystoreAndCreateIfNotExists(secretKeyPath, algorithm);
		else
			k = new SecretKeyStore(secretKeyPath, algorithm);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addSecretKeyStore(secretKeyPath, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static SecretKeyStore getSecretKeyStore(RequestInfo requestInfo, byte[] secretKey, String algorithm) throws SecurityException{
		String keyParam = null;
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && secretKey!=null;
		if(useRequestInfo) {
			keyParam = AbstractKeystoreCache.buildKeyCacheFromBytes(secretKey);
			Object o = requestInfo.getRequestConfig().getSecretKeyStore(keyParam);
			if(o instanceof SecretKeyStore) {
				return (SecretKeyStore) o;
			}
		}
		SecretKeyStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.secretKeyStoreCache.getKeystoreAndCreateIfNotExists(secretKey, algorithm);
		else
			k = new SecretKeyStore(secretKey, algorithm);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addSecretKeyStore(keyParam, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	
	
	
	
	
	public static RemoteStore getRemoteStore(RequestInfo requestInfo, String keyId, RemoteKeyType keyType, RemoteStoreConfig remoteStoreConfig, IRemoteStoreProvider provider) throws SecurityException{
		
		String keyCache = RemoteStoreCache.getKeyCache(remoteStoreConfig, keyId, keyType);
		
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && keyCache!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getRemoteStore(keyCache);
			if(o instanceof RemoteStore) {
				return (RemoteStore) o;
			}
		}
		RemoteStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.remoteStoreCache.getKeystoreAndCreateIfNotExists(keyCache, keyId, keyType, remoteStoreConfig, provider);
		else
			k = new RemoteStore(keyId, keyType, remoteStoreConfig, provider);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addRemoteStore(keyCache, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static void removeRemoteStore(RequestInfo requestInfo, String keyId, RemoteKeyType keyType, RemoteStoreConfig remoteStoreConfig) throws SecurityException{
		String keyCache = RemoteStoreCache.getKeyCache(remoteStoreConfig, keyId, keyType);
		
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && keyCache!=null;
		if(useRequestInfo) {
			requestInfo.getRequestConfig().removeRemoteStore(keyCache);
		}
		if(GestoreKeystoreCache.cacheEnabled) {
			GestoreKeystoreCache.remoteStoreCache.removeObjectFromCache(keyCache);
		}
	}
	public static List<String> keysRemoteStore() throws SecurityException{
		List<String> l = null;
		if(GestoreKeystoreCache.cacheEnabled) {
			return GestoreKeystoreCache.remoteStoreCache.keys();
		}
		return l;
	}
	
	
	
	
	public static RemoteStoreClientInfo getRemoteStoreClientInfo(RequestInfo requestInfo, String keyId, String clientId, RemoteStoreConfig remoteStoreConfig, IRemoteStoreProvider provider) throws SecurityException{
		
		String keyCache = RemoteStoreClientInfoCache.getKeyCache(remoteStoreConfig, keyId);
		
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && keyCache!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getRemoteStoreClientInfo(keyCache);
			if(o instanceof RemoteStoreClientInfo) {
				return (RemoteStoreClientInfo) o;
			}
		}
		org.openspcoop2.security.keystore.RemoteStoreClientInfo k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.remoteStoreClientInfoCache.getKeystoreAndCreateIfNotExists(keyCache, keyId, clientId, remoteStoreConfig, provider);
		else
			k = new org.openspcoop2.security.keystore.RemoteStoreClientInfo(keyId, clientId, remoteStoreConfig, provider);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addRemoteStoreClientInfo(keyCache, k, requestInfo.getIdTransazione());
		}
		return k.getClientInfo();
	}
	public static void removeRemoteStoreClientInfo(RequestInfo requestInfo, String keyId, RemoteStoreConfig remoteStoreConfig) throws SecurityException{
		String keyCache = RemoteStoreClientInfoCache.getKeyCache(remoteStoreConfig, keyId);
		
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && keyCache!=null;
		if(useRequestInfo) {
			requestInfo.getRequestConfig().removeRemoteStoreClientInfo(keyCache);
		}
		if(GestoreKeystoreCache.cacheEnabled) {
			GestoreKeystoreCache.remoteStoreClientInfoCache.removeObjectFromCache(keyCache);
		}
	}
	
	
	
	
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint,
			HttpOptions ... options) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled) {
			if(options!=null && options.length>0) {
				List<HttpOptions> list = new ArrayList<>();
				list.addAll(Arrays.asList(options));
				k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint,
						list);
			}
			else {
				k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint);
			}
		}else
			k = new HttpStore(endpoint,
					options);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, Integer connectionTimeout, Integer readTimeout,
			HttpOptions ... options) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, connectionTimeout, readTimeout,
					options);
		else
			k = new HttpStore(endpoint, connectionTimeout, readTimeout,
					options);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, MerlinTruststore trustStoreSsl,
			HttpOptions ... options) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, trustStoreSsl,
					options);
		else
			k = new HttpStore(endpoint, trustStoreSsl,
					options);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl,
			HttpOptions ... options) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, trustStoreSsl, crlTrustStoreSsl,
					options);
		else
			k = new HttpStore(endpoint, trustStoreSsl, crlTrustStoreSsl,
					options);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl,
			HttpOptions ... options) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl,
					options);
		else
			k = new HttpStore(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl,
					options);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo,String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl,
			HttpOptions ... options) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl, crlTrustStoreSsl,
					options);
		else
			k = new HttpStore(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl, crlTrustStoreSsl,
					options);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, Boolean trustAll,
			HttpOptions ... options) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, trustAll,
					options);
		else
			k = new HttpStore(endpoint, trustAll,
					options);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			Boolean trustAll,
			HttpOptions ... options) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && endpoint!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getHttpStore(endpoint);
			if(o instanceof HttpStore) {
				return (HttpStore) o;
			}
		}
		HttpStore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, 
					connectionTimeout, readTimeout, 
					trustAll,
					options);
		else
			k = new HttpStore(endpoint, 
					connectionTimeout, readTimeout, 
					trustAll,
					options);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addHttpStore(endpoint, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	
	public static CRLCertstore getCRLCertstore(RequestInfo requestInfo,String crlPath) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && crlPath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getCRLCertstore(crlPath);
			if(o instanceof CRLCertstore) {
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
	public static CRLCertstore getCRLCertstore(RequestInfo requestInfo,String crlPath, Map<String, byte[]> localResources) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && crlPath!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getCRLCertstore(crlPath);
			if(o instanceof CRLCertstore) {
				return (CRLCertstore) o;
			}
		}
		CRLCertstore k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.crlCertstoreCache.getKeystoreAndCreateIfNotExists(crlPath, localResources);
		else
			k = new CRLCertstore(crlPath, localResources);
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
			if(o instanceof SSLSocketFactory) {
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
	
	public static ExternalResource getExternalResource(RequestInfo requestInfo,String resource, ExternalResourceConfig externalConfig) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && resource!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getExternalResource(resource);
			if(o instanceof ExternalResource) {
				return (ExternalResource) o;
			}
		}
		ExternalResource k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.externalResourceCache.getKeystoreAndCreateIfNotExists(resource, externalConfig); // gestito nell'oggetto ExternalResourceCache l'utilizzo della risorsa come id
		else
			k = new ExternalResource(resource, resource, externalConfig); // l'id della risorsa esterna è la url stessa della risorsa, non serve un id aggiuntivo
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addExternalResource(resource, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	public static SSLConfigProps getSSLConfigProps(RequestInfo requestInfo,String resource, boolean sslConfigRequired) throws SecurityException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && resource!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getSSLConfigProps(resource);
			if(o instanceof SSLConfigProps) {
				return (SSLConfigProps) o;
			}
		}
		SSLConfigProps k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.sslConfigPropsCache.getKeystoreAndCreateIfNotExists(resource, sslConfigRequired);
		else
			k = new SSLConfigProps(resource, sslConfigRequired);
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addSSLConfigProps(resource, k, requestInfo.getIdTransazione());
		}
		return k;
	}
	
	public static OCSPResponse getOCSPResponse(RequestInfo requestInfo,IOCSPValidator ocspValidator, X509Certificate cert) throws SecurityException{
		if(cert==null) {
			throw new SecurityException("Certificate is null");
		}
		CertificateInfo certInfo = new CertificateInfo(cert, "cer");
		return getOCSPResponse(requestInfo, ocspValidator, certInfo);
	}
	public static OCSPResponse getOCSPResponse(RequestInfo requestInfo,IOCSPValidator ocspValidator, CertificateInfo cert) throws SecurityException{
		if(cert==null) {
			throw new SecurityException("Certificate is null");
		}
		String pem = null;
		try {
			pem = cert.getPEMEncoded();
		}catch(Exception t) {
			throw new SecurityException(t.getMessage(),t);
		}
		if(pem==null) {
			throw new SecurityException("Certificate PEM Encoding is null");
		}
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getOCSPResponse(pem);
			if(o instanceof OCSPResponse) {
				return (OCSPResponse) o;
			}
		}
		OCSPResponse k = null;
		if(GestoreKeystoreCache.cacheEnabled)
			k = GestoreKeystoreCache.ocspResponseCache.getKeystoreAndCreateIfNotExists(pem, ocspValidator, cert.getCertificate());
		else
			k = new OCSPResponse(ocspValidator, cert.getCertificate()); 
		if(useRequestInfo) {
			requestInfo.getRequestConfig().addOCSPResponse(pem, k, requestInfo.getIdTransazione());
		}
		return k;
	}
}
