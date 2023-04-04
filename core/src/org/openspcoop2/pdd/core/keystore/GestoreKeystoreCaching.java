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

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.ExternalResource;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.MerlinTruststore;
import org.openspcoop2.security.keystore.MultiKeystore;
import org.openspcoop2.security.keystore.OCSPResponse;
import org.openspcoop2.security.keystore.SSLSocketFactory;
import org.openspcoop2.security.keystore.SymmetricKeystore;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.cache.CacheType;
import org.openspcoop2.utils.certificate.CertificateInfo;
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
	

	/* --------------- Cache --------------------*/
	public static boolean isCacheAbilitata() {
		return cache!=null;
	}
	public static void resetCache() throws Exception{
		try{
			if(cache!=null){
				cache.clear();
			}
		}catch(Exception e){
			throw new Exception("Reset della cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	public static String printStatsCache(String separator) throws Exception{
		try{
			if(cache!=null){
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
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Visualizzazione Statistiche riguardante la cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws Exception{
		try{
			if(cache!=null)
				throw new Exception("Cache gia' abilitata");
			else{
				_abilitaCache();
			}
		}catch(Exception e){
			throw new Exception("Abilitazione cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	private static synchronized void _abilitaCache() throws Exception{
		try{
			if(cache==null) {
				cache = new Cache(CacheType.JCS, KEYSTORE_CACHE_NAME); // lascio JCS come default abilitato via jmx
				cache.build();
			}
		}catch(Exception e){
			throw new Exception("Abilitazione cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond, Logger log) throws Exception{
		try{
			if(cache!=null)
				throw new Exception("Cache gia' abilitata");
			else{
				int dimensione = -1;
				if(dimensioneCache!=null){
					dimensione = dimensioneCache.intValue();
				}
				initCache(CacheType.JCS, dimensione, algoritmoCacheLRU, itemIdleTime, itemLifeSecond, log); // lascio JCS come default abilitato via jmx
			}
		}catch(Exception e){
			throw new Exception("Abilitazione cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	public static void disabilitaCache() throws Exception{
		try{
			if(cache==null)
				throw new Exception("Cache gia' disabilitata");
			else{
				_disabilitaCache();
			}
		}catch(Exception e){
			throw new Exception("Disabilitazione cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}	
	private static synchronized void _disabilitaCache() throws Exception{
		try{
			if(cache!=null) {
				cache.clear();
				cache = null;
			}
		}catch(Exception e){
			throw new Exception("Disabilitazione cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}	
	public static String listKeysCache(String separator) throws Exception{
		try{
			if(cache!=null){
				try{
					return cache.printKeys(separator);
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Visualizzazione chiavi presenti nella cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static String getObjectCache(String key) throws Exception{
		try{
			if(cache!=null){
				try{
					Object o = cache.get(key);
					if(o!=null){
						return o.toString();
					}else{
						return "oggetto con chiave ["+key+"] non presente";
					}
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Visualizzazione oggetto presente nella cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static void removeObjectCache(String key) throws Exception{
		try{
			if(cache!=null){
				try{
					cache.remove(key);
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Rimozione oggetto presente nella cache per i dati contenenti i keystore non riuscita: "+e.getMessage(),e);
		}
	}
	

	/*----------------- INIZIALIZZAZIONE --------------------*/

	public static void initialize(Logger log) throws Exception{
		GestoreKeystoreCaching.initialize(null, false, -1,null,-1l,-1l, log);
	}
	public static void initialize(CacheType cacheType, int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{
		GestoreKeystoreCaching.initialize(cacheType, true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(CacheType cacheType, boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{

		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreKeystoreCaching.initCache(cacheType, dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}
	private static void initCache(CacheType cacheType, Integer dimensioneCache,String algoritmoCache,Long itemIdleTime,Long itemLifeSecond,Logger alog) throws Exception{
		initCache(cacheType, dimensioneCache, CostantiConfigurazione.CACHE_LRU.toString().equalsIgnoreCase(algoritmoCache), itemIdleTime, itemLifeSecond, alog);
	}
	
	private static void initCache(CacheType cacheType, Integer dimensioneCache,boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond,Logger alog) throws Exception{
		
		cache = new Cache(cacheType, KEYSTORE_CACHE_NAME);
	
		// dimensione
		if(dimensioneCache!=null && dimensioneCache>0){
			try{
				String msg = "Dimensione della cache (ResponseCaching) impostata al valore: "+dimensioneCache;
				alog.info(msg);
				cache.setCacheSize(dimensioneCache);
			}catch(Exception error){
				String msg = "Parametro errato per la dimensione della cache (ResponseCaching): "+error.getMessage();
				alog.error(msg);
				throw new Exception(msg,error);
			}
		}
		
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
				throw new Exception(msg,error);
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
			throw new Exception(msg,error);
		}
		
		cache.build();
		
		// impostazione di JCS nel gestore delle cache dei keystore 
		
		org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.setKeystoreCacheJCS(true, longItemLife>0 ? (int)longItemLife : 7200, cache);
	}
	

	@SuppressWarnings("deprecation")
	@Deprecated
	public static void disableSyncronizedGet() throws UtilsException {
		if(cache==null) {
			throw new UtilsException("Cache disabled");
		}
		cache.disableSyncronizedGet();
	}
	@SuppressWarnings("deprecation")
	@Deprecated
	public static boolean isDisableSyncronizedGet() throws UtilsException {
		if(cache==null) {
			throw new UtilsException("Cache disabled");
		}
		return cache.isDisableSyncronizedGet();
	}
	
	
	
	private static long itemCrlLifeSecond; 
	public static void setCacheCrlLifeSeconds(long itemCrlLifeSecondParam,Logger alog) throws Exception{
		itemCrlLifeSecond = itemCrlLifeSecondParam;
		org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.setKeystoreCacheJCSCrlLifeSeconds(itemCrlLifeSecond>0 ? (int)itemCrlLifeSecond : 7200);
	}
	public static long getItemCrlLifeSecond() {
		return itemCrlLifeSecond;
	}
	
	
	private static GestoreKeystoreCaching staticInstance = null;
	public static synchronized void initialize() throws Exception{
		if(staticInstance==null){
			staticInstance = new GestoreKeystoreCaching();
		}
	}
	public static GestoreKeystoreCaching getInstance() throws Exception{
		if(staticInstance==null){
			throw new Exception("GestoreKeystore non inizializzato");
		}
		return staticInstance;
	}
	
	@SuppressWarnings("unused")
	private Logger log;
	
	public GestoreKeystoreCaching() throws Exception{
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	}
	
	
	
	
	
	
	
	
	
	/* ********************** ENGINE ************************** */
	
	public static MerlinTruststore getMerlinTruststore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinTruststore(requestInfo, propertyFilePath);
	}
	public static MerlinTruststore getMerlinTruststore(RequestInfo requestInfo, String pathStore,String tipoStore,String passwordStore) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMerlinTruststore(requestInfo, pathStore, tipoStore, passwordStore);
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
	
	
	public static SymmetricKeystore getSymmetricKeystore(RequestInfo requestInfo, String alias,String key,String algoritmo) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getSymmetricKeystore(requestInfo, key, alias, algoritmo);
	}
	
	
	public static MultiKeystore getMultiKeystore(RequestInfo requestInfo, String propertyFilePath) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getMultiKeystore(requestInfo, propertyFilePath);
	}
	
	
	public static CRLCertstore getCRLCertstore(RequestInfo requestInfo,String crlPath) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getCRLCertstore(requestInfo,crlPath);
	}
	
	
	public static SSLSocketFactory getSSLSocketFactory(RequestInfo requestInfo,SSLConfig sslConfig) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getSSLSocketFactory(requestInfo,sslConfig);
	}
	
	public static ExternalResource getExternalResource(RequestInfo requestInfo,String resource, ExternalResourceConfig externalConfig) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getExternalResource(requestInfo, resource, externalConfig);
	}
	
	public static OCSPResponse getOCSPResponse(RequestInfo requestInfo,IOCSPValidator ocspValidator, X509Certificate cert) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getOCSPResponse(requestInfo, ocspValidator, cert);
	}
	public static OCSPResponse getOCSPResponse(RequestInfo requestInfo,IOCSPValidator ocspValidator, CertificateInfo cert) throws SecurityException{
		return org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.getOCSPResponse(requestInfo, ocspValidator, cert);
	}
}
