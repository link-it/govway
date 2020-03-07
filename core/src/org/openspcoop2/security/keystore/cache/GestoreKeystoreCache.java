/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.HttpStore;
import org.openspcoop2.security.keystore.JWKSetStore;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.MerlinTruststore;
import org.openspcoop2.security.keystore.MultiKeystore;
import org.openspcoop2.security.keystore.SymmetricKeystore;
import org.openspcoop2.utils.cache.Cache;

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
	}
	public static void setKeystoreCacheJCS_crlLifeSeconds(int cacheCrlLifeSecond){
		GestoreKeystoreCache.crlCertstoreCache.updateCacheLifeSecond(cacheCrlLifeSecond);
	}
	
	
	public static MerlinTruststore getMerlinTruststore(String propertyFilePath) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.merlinTruststoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath);
		else
			return new MerlinTruststore(propertyFilePath);
	}
	public static MerlinTruststore getMerlinTruststore(String pathStore,String tipoStore,String passwordStore) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.merlinTruststoreCache.getKeystoreAndCreateIfNotExists(pathStore, tipoStore, passwordStore);
		else
			return new MerlinTruststore(pathStore, tipoStore, passwordStore);
	}
	
	
	public static MerlinKeystore getMerlinKeystore(String propertyFilePath) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath);
		else
			return new MerlinKeystore(propertyFilePath);
	}
	public static MerlinKeystore getMerlinKeystore(String propertyFilePath,String passwordPrivateKey) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath, passwordPrivateKey);
		else
			return new MerlinKeystore(propertyFilePath, passwordPrivateKey);
	}
	public static MerlinKeystore getMerlinKeystore(String pathStore,String tipoStore,String passwordStore) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(pathStore, tipoStore, passwordStore);
		else
			return new MerlinKeystore(pathStore, tipoStore, passwordStore);
	}
	public static MerlinKeystore getMerlinKeystore(String pathStore,String tipoStore,String passwordStore,String passwordPrivateKey) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(pathStore, tipoStore, passwordStore, passwordPrivateKey);
		else
			return new MerlinKeystore(pathStore, tipoStore, passwordStore, passwordPrivateKey);
	}
	
	
	public static SymmetricKeystore getSymmetricKeystore(String alias,String key,String algoritmo) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.symmetricKeystoreCache.getKeystoreAndCreateIfNotExists(key, alias, algoritmo);
		else
			return new SymmetricKeystore(alias, key, algoritmo);
	}
	
	
	public static MultiKeystore getMultiKeystore(String propertyFilePath) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.multiKeystoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath);
		else
			return new MultiKeystore(propertyFilePath);
	}
	
	
	public static JWKSetStore getJwkSetStore(String propertyFilePath) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.jwkSetStoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath);
		else
			return new JWKSetStore(propertyFilePath);
	}
	
	
	public static HttpStore getHttpStore(String endpoint) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint);
		else
			return new HttpStore(endpoint);
	}
	public static HttpStore getHttpStore(String endpoint, Integer connectionTimeout, Integer readTimeout) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, connectionTimeout, readTimeout);
		else
			return new HttpStore(endpoint, connectionTimeout, readTimeout);
	}
	public static HttpStore getHttpStore(String endpoint, MerlinTruststore trustStoreSsl) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, trustStoreSsl);
		else
			return new HttpStore(endpoint, trustStoreSsl);
	}
	public static HttpStore getHttpStore(String endpoint, MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, trustStoreSsl, crlTrustStoreSsl);
		else
			return new HttpStore(endpoint, trustStoreSsl, crlTrustStoreSsl);
	}
	public static HttpStore getHttpStore(String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl);
		else
			return new HttpStore(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl);
	}
	public static HttpStore getHttpStore(String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.httpStoreCache.getKeystoreAndCreateIfNotExists(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl, crlTrustStoreSsl);
		else
			return new HttpStore(endpoint, 
					connectionTimeout, readTimeout, 
					trustStoreSsl, crlTrustStoreSsl);
	}
	
	
	public static CRLCertstore getCRLCertstore(String crlPath) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.crlCertstoreCache.getKeystoreAndCreateIfNotExists(crlPath);
		else
			return new CRLCertstore(crlPath);
	}
}
