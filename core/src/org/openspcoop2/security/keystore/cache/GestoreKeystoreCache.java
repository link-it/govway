/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.MerlinTruststore;
import org.openspcoop2.security.keystore.MultiKeystore;
import org.openspcoop2.security.keystore.SymmetricKeystore;

/**
 * GestoreKeystoreCache
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreKeystoreCache {

	private static final MerlinTruststoreCache merlinTruststoreCache = new MerlinTruststoreCache();
	private static final MerlinKeystoreCache merlinKeystoreCache = new MerlinKeystoreCache();
	private static final SymmetricKeystoreCache symmetricKeystoreCache = new SymmetricKeystoreCache();
	private static final MultiKeystoreCache multiKeystoreCache = new MultiKeystoreCache();
	private static final CRLCertstoreCache crlCertstoreCache = new CRLCertstoreCache();
	private static boolean cacheEnabled = false;
	
	public static void setKeystoreCacheParameters(boolean cacheEnabled,int cacheLifeSecond,int cacheSize){
		GestoreKeystoreCache.cacheEnabled = cacheEnabled;
		GestoreKeystoreCache.merlinTruststoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.merlinKeystoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.symmetricKeystoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.multiKeystoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
		GestoreKeystoreCache.crlCertstoreCache.setKeystoreCacheParameters(cacheLifeSecond, cacheSize);
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
	
	public static MerlinKeystore getMerlinKeystore(String propertyFilePath,String passwordPrivateKey) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.merlinKeystoreCache.getKeystoreAndCreateIfNotExists(propertyFilePath, passwordPrivateKey);
		else
			return new MerlinKeystore(propertyFilePath, passwordPrivateKey);
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
	
	public static CRLCertstore getCRLCertstore(String crlPath) throws SecurityException{
		if(GestoreKeystoreCache.cacheEnabled)
			return GestoreKeystoreCache.crlCertstoreCache.getKeystoreAndCreateIfNotExists(crlPath);
		else
			return new CRLCertstore(crlPath);
	}
}
