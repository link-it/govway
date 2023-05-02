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
package org.openspcoop2.pdd.core.keystore;

import java.security.cert.X509Certificate;
import java.util.Map;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
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
import org.openspcoop2.security.keystore.SymmetricKeystore;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.cache.CacheType;
import org.openspcoop2.utils.cache.Constants;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.remote.IRemoteStoreProvider;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.transport.http.ExternalResourceConfig;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.openspcoop2.utils.transport.http.SSLConfig;
import org.slf4j.Logger;

/**     
 * GestoreCacheKeystore
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreKeystoreCaching {

	/** Chiave della cache */
	private static final String KEYSTORE_CACHE_NAME = "keystore";
	/** Cache */
	private static Cache cache = null;

	
	private static KeystoreException newKeystoreExceptionGenericError(Exception e) {
		return new KeystoreException("Abilitazione cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
	}

	/* --------------- Cache --------------------*/
	public static boolean isCacheAbilitata() {
		return cache!=null;
	}
	public static void resetCache() throws KeystoreException{
		try{
			if(cache!=null){
				cache.clear();
			}
		}catch(Exception e){
			throw new KeystoreException("Reset della cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	public static String printStatsCache(String separator) throws KeystoreException{
		try{
			if(cache!=null){
				return getStatsCache(separator);
			}else{
				throw new KeystoreException(Constants.MSG_CACHE_NON_ABILITATA);
			}
		}catch(Exception e){
			throw new KeystoreException("Visualizzazione Statistiche riguardante la cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	private static String getStatsCache(String separator) throws KeystoreException {
		try{
			String stats = cache.printStats(separator);
			String lifeTimeLabel = "LifeTime:";
			if(stats.contains(lifeTimeLabel)) {
				
				StringBuilder bf = new StringBuilder();
				bf.append("CRLsLifeTime:");
				long lifeTime = GestoreKeystoreCaching.getItemCrlLifeSecond();
				if(lifeTime>0){
					bf.append(Utilities.convertSystemTimeIntoString_millisecondi(lifeTime*1000,false));
				}
				else if(lifeTime==0){
					bf.append("0");
				}
				else if(lifeTime<0){
					bf.append("Infinito");
				}
				bf.append(separator);
			
				return stats.replace(lifeTimeLabel, bf.toString() + lifeTimeLabel);
			}
			else {
				return stats;
			}
		}catch(Exception e){
			throw new KeystoreException(e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws KeystoreException{
		try{
			if(cache!=null)
				throw new KeystoreException("Cache gia' abilitata");
			else{
				abilitaCacheEngine();
			}
		}catch(Exception e){
			throw newKeystoreExceptionGenericError(e);
		}
	}
	private static synchronized void abilitaCacheEngine() throws KeystoreException{
		try{
			if(cache==null) {
				cache = new Cache(CacheType.JCS, KEYSTORE_CACHE_NAME); // lascio JCS come default abilitato via jmx
				cache.build();
			}
		}catch(Exception e){
			throw newKeystoreExceptionGenericError(e);
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond, Logger log) throws KeystoreException{
		try{
			if(cache!=null)
				throw new KeystoreException("Cache gia' abilitata");
			else{
				int dimensione = -1;
				if(dimensioneCache!=null){
					dimensione = dimensioneCache.intValue();
				}
				initCache(CacheType.JCS, dimensione, algoritmoCacheLRU, itemIdleTime, itemLifeSecond, log); // lascio JCS come default abilitato via jmx
			}
		}catch(Exception e){
			throw newKeystoreExceptionGenericError(e);
		}
	}
	public static void disabilitaCache() throws KeystoreException{
		try{
			if(cache==null)
				throw new KeystoreException("Cache gia' disabilitata");
			else{
				disabilitaCacheEngine();
			}
		}catch(Exception e){
			throw new KeystoreException("Disabilitazione cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}	
	private static synchronized void disabilitaCacheEngine() throws KeystoreException{
		try{
			if(cache!=null) {
				cache.clear();
				cache = null;
			}
		}catch(Exception e){
			throw new KeystoreException("Disabilitazione cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}	
	public static String listKeysCache(String separator) throws KeystoreException{
		try{
			if(cache!=null){
				return getKeysCache(separator);
			}else{
				throw new KeystoreException(Constants.MSG_CACHE_NON_ABILITATA);
			}
		}catch(Exception e){
			throw new KeystoreException("Visualizzazione chiavi presenti nella cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	private static String getKeysCache(String separator) throws KeystoreException {
		try{
			return cache.printKeys(separator);
		}catch(Exception e){
			throw new KeystoreException(e.getMessage(),e);
		}
	}
	
	public static String getObjectCache(String key) throws KeystoreException{
		try{
			if(cache!=null){
				return getByKey(key);
			}else{
				throw new KeystoreException(Constants.MSG_CACHE_NON_ABILITATA);
			}
		}catch(Exception e){
			throw new KeystoreException("Visualizzazione oggetto presente nella cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	private static String getByKey(String key) throws KeystoreException {
		try{
			Object o = cache.get(key);
			if(o!=null){
				return o.toString();
			}else{
				return "oggetto con chiave ["+key+"] non presente";
			}
		}catch(Exception e){
			throw new KeystoreException(e.getMessage(),e);
		}
	}
	
	public static void removeObjectCache(String key) throws KeystoreException{
		try{
			if(cache!=null){
				removeByKey(key);
			}else{
				throw new KeystoreException(Constants.MSG_CACHE_NON_ABILITATA);
			}
		}catch(Exception e){
			throw new KeystoreException("Rimozione oggetto presente nella cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	private static void removeByKey(String key) throws KeystoreException {
		try{
			cache.remove(key);
		}catch(Exception e){
			throw new KeystoreException(e.getMessage(),e);
		}
	}
	

	/*----------------- INIZIALIZZAZIONE --------------------*/

	public static void initialize(Logger log) throws KeystoreException{
		GestoreKeystoreCaching.initialize(null, false, -1,null,-1l,-1l, log);
	}
	public static void initialize(CacheType cacheType, int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws KeystoreException{
		GestoreKeystoreCaching.initialize(cacheType, true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(CacheType cacheType, boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws KeystoreException{

		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreKeystoreCaching.initCache(cacheType, dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}
	private static void initCache(CacheType cacheType, Integer dimensioneCache,String algoritmoCache,Long itemIdleTime,Long itemLifeSecond,Logger alog) throws KeystoreException{
		initCache(cacheType, dimensioneCache, CostantiConfigurazione.CACHE_LRU.toString().equalsIgnoreCase(algoritmoCache), itemIdleTime, itemLifeSecond, alog);
	}
	
	private static void initCache(CacheType cacheType, Integer dimensioneCache,boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond,Logger alog) throws KeystoreException{
		
		try {
			cache = new Cache(cacheType, KEYSTORE_CACHE_NAME);
		}catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
	
		// dimensione
		initCacheSize(dimensioneCache, alog);
		
		// algoritno
		String msg = "Algoritmo di cache (ResponseCaching) impostato al valore: LRU";
		if(!algoritmoCacheLRU){
			msg = "Algoritmo di cache (ResponseCaching) impostato al valore: MRU";
		}
		alog.info(msg);
		if(!algoritmoCacheLRU)
			cache.setCacheAlgoritm(CacheAlgorithm.MRU);
		else
			cache.setCacheAlgoritm(CacheAlgorithm.LRU);
		
		
		// idle time
		if(itemIdleTime!=null && itemIdleTime>0){
			try{
				msg = "Attributo 'IdleTime' (ResponseCaching) impostato al valore: "+itemIdleTime;
				alog.info(msg);
				cache.setItemIdleTime(itemIdleTime);
			}catch(Exception error){
				msg = "Parametro errato per l'attributo 'IdleTime' (ResponseCaching): "+error.getMessage();
				alog.error(msg);
				throw new KeystoreException(msg,error);
			}
		}
		
		// LifeSecond
		long longItemLife = -1; 
		if(itemLifeSecond!=null && itemLifeSecond>0){
			longItemLife = itemLifeSecond.longValue();
		}
		try{
			msg = "Attributo 'MaxLifeSecond' (ResponseCaching) impostato al valore: "+longItemLife;
			alog.info(msg);
			cache.setItemLifeTime(longItemLife);
		}catch(Exception error){
			msg = "Parametro errato per l'attributo 'MaxLifeSecond' (ResponseCaching): "+error.getMessage();
			alog.error(msg);
			throw new KeystoreException(msg,error);
		}
		
		try {
			cache.build();
		}catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		
		// impostazione di JCS nel gestore delle cache dei keystore 
		
		org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.setKeystoreCacheJCS(true, longItemLife>0 ? (int)longItemLife : 7200, cache);
	}
	
	private static void initCacheSize(Integer dimensioneCache, Logger alog) throws KeystoreException {
		// dimensione
		if(dimensioneCache!=null && dimensioneCache>0){
			try{
				String msg = "Dimensione della cache (ResponseCaching) impostata al valore: "+dimensioneCache;
				alog.info(msg);
				cache.setCacheSize(dimensioneCache);
			}catch(Exception error){
				String msg = "Parametro errato per la dimensione della cache (ResponseCaching): "+error.getMessage();
				alog.error(msg);
				throw new KeystoreException(msg,error);
			}
		}
	}
	

	
	
	private static long itemCrlLifeSecond; 
	public static void setCacheCrlLifeSeconds(long itemCrlLifeSecondParam) {
		itemCrlLifeSecond = itemCrlLifeSecondParam;
		org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.setKeystoreCacheJCSCrlLifeSeconds(itemCrlLifeSecond>0 ? (int)itemCrlLifeSecond : 7200);
	}
	public static long getItemCrlLifeSecond() {
		return itemCrlLifeSecond;
	}
	
	
	private static GestoreKeystoreCaching staticInstance = null;
	public static synchronized void initialize() throws KeystoreException{
		if(staticInstance==null){
			staticInstance = new GestoreKeystoreCaching();
		}
	}
	public static GestoreKeystoreCaching getInstance() throws KeystoreException{
		if(staticInstance==null){
			throw new KeystoreException("GestoreKeystore non inizializzato");
		}
		return staticInstance;
	}
	
	@SuppressWarnings("unused")
	private Logger log;
	
	public GestoreKeystoreCaching() throws KeystoreException{
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	}
	
	
	
	
	
	
	
	
	
	/* ********************** ENGINE ************************** */
	
	public static MerlinTruststore getMerlinTruststore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinTruststore(requestInfo, propertyFilePath);
	}
	public static MerlinTruststore getMerlinTruststore(RequestInfo requestInfo, String pathStore,String tipoStore,String passwordStore) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinTruststore(requestInfo, pathStore, tipoStore, passwordStore);
	}
	public static MerlinTruststore getMerlinTruststore(RequestInfo requestInfo, byte[] bytesStore,String tipoStore,String passwordStore) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinTruststore(requestInfo, bytesStore, tipoStore, passwordStore);
	}
	
	
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinKeystore(requestInfo, propertyFilePath);
	}
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo, String propertyFilePath,String passwordPrivateKey) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinKeystore(requestInfo, propertyFilePath, passwordPrivateKey);
	}
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo, String pathStore,String tipoStore,String passwordStore) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinKeystore(requestInfo, pathStore, tipoStore, passwordStore);
	}
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo, String pathStore,String tipoStore,String passwordStore,String passwordPrivateKey) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinKeystore(requestInfo, pathStore, tipoStore, passwordStore, passwordPrivateKey);
	}
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo, byte[] bytesStore,String tipoStore,String passwordStore) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinKeystore(requestInfo, bytesStore, tipoStore, passwordStore);
	}
	public static MerlinKeystore getMerlinKeystore(RequestInfo requestInfo,byte[] bytesStore,String tipoStore,String passwordStore,String passwordPrivateKey) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinKeystore(requestInfo, bytesStore, tipoStore, passwordStore, passwordPrivateKey);
	}
	
	
	public static SymmetricKeystore getSymmetricKeystore(RequestInfo requestInfo, String alias,String key,String algoritmo) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getSymmetricKeystore(requestInfo, key, alias, algoritmo);
	}
	
	
	public static MultiKeystore getMultiKeystore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMultiKeystore(requestInfo, propertyFilePath);
	}
	
	
	public static JWKSetStore getJwkSetStore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getJwkSetStore(requestInfo, propertyFilePath);
	}
	public static JWKSetStore getJwkSetStore(RequestInfo requestInfo, byte[] archive) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getJwkSetStore(requestInfo, archive);
	}
	
	
	public static KeyPairStore getKeyPairStore(RequestInfo requestInfo, String privateKeyPath, String publicKeyPath, String privateKeyPassword, String algorithm) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getKeyPairStore(requestInfo, privateKeyPath, publicKeyPath, privateKeyPassword, algorithm);
	}
	public static KeyPairStore getKeyPairStore(RequestInfo requestInfo, byte[] privateKey, byte[] publicKey, String privateKeyPassword, String algorithm) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getKeyPairStore(requestInfo, privateKey, publicKey, privateKeyPassword, algorithm);
	}
	
	
	public static PublicKeyStore getPublicKeyStore(RequestInfo requestInfo, String publicKeyPath, String algorithm) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getPublicKeyStore(requestInfo, publicKeyPath, algorithm);
	}
	public static PublicKeyStore getPublicKeyStore(RequestInfo requestInfo, byte[] publicKey, String algorithm) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getPublicKeyStore(requestInfo, publicKey, algorithm);
	}
	
	
	public static RemoteStore getRemoteStore(RequestInfo requestInfo, String keyId, RemoteKeyType keyType, RemoteStoreConfig remoteStoreConfig, IRemoteStoreProvider provider) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getRemoteStore(requestInfo, keyId, keyType, remoteStoreConfig, provider);
	}
	public static void removeRemoteStore(RequestInfo requestInfo, String keyId, RemoteKeyType keyType, RemoteStoreConfig remoteStoreConfig) throws SecurityException{
		org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.removeRemoteStore(requestInfo, keyId, keyType, remoteStoreConfig);
	}
	
	
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getHttpStore(requestInfo, endpoint);
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, Integer connectionTimeout, Integer readTimeout) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getHttpStore(requestInfo, endpoint, connectionTimeout, readTimeout);
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, MerlinTruststore trustStoreSsl) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getHttpStore(requestInfo, endpoint, trustStoreSsl);
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getHttpStore(requestInfo, endpoint, trustStoreSsl, crlTrustStoreSsl);
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo, String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getHttpStore(requestInfo, endpoint,
				connectionTimeout, readTimeout, 
				trustStoreSsl);
	}
	public static HttpStore getHttpStore(RequestInfo requestInfo,String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getHttpStore(requestInfo, endpoint,
				connectionTimeout, readTimeout, 
				trustStoreSsl, crlTrustStoreSsl);
	}
	
	
	public static CRLCertstore getCRLCertstore(RequestInfo requestInfo,String crlPath) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getCRLCertstore(requestInfo,crlPath);
	}
	public static CRLCertstore getCRLCertstore(RequestInfo requestInfo,String crlPath, Map<String, byte[]> localResources) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getCRLCertstore(requestInfo,crlPath,localResources);
	}
	
	
	public static SSLSocketFactory getSSLSocketFactory(RequestInfo requestInfo,SSLConfig sslConfig) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getSSLSocketFactory(requestInfo,sslConfig);
	}
	
	
	public static ExternalResource getExternalResource(RequestInfo requestInfo,String resource, ExternalResourceConfig externalConfig) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getExternalResource(requestInfo, resource, externalConfig);
	}
	
	
	public static SSLConfigProps getSSLConfigProps(RequestInfo requestInfo,String resource, boolean sslConfigRequired) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getSSLConfigProps(requestInfo, resource, sslConfigRequired);
	}
	
	
	public static OCSPResponse getOCSPResponse(RequestInfo requestInfo,IOCSPValidator ocspValidator, X509Certificate cert) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getOCSPResponse(requestInfo, ocspValidator, cert);
	}
	public static OCSPResponse getOCSPResponse(RequestInfo requestInfo,IOCSPValidator ocspValidator, CertificateInfo cert) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getOCSPResponse(requestInfo, ocspValidator, cert);
	}
}
