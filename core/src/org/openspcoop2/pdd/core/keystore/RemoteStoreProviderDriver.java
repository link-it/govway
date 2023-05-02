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

import java.io.ByteArrayOutputStream;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.certificate.remote.IRemoteStoreProvider;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.certificate.remote.RemoteStoreUtils;
import org.slf4j.Logger;

/**
 * RemoteStoreProviderDriver
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreProviderDriver implements IRemoteStoreProvider {

	
	private static final Map<String, RemoteStoreProviderDriver> _providerStore = new HashMap<>();
	public static synchronized void initialize(Logger log, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		String storeConfigName = getRemoteStoreConfigName(remoteStoreConfig);
		RemoteStoreProviderDriver s = new RemoteStoreProviderDriver(log, remoteStoreConfig);
		_providerStore.put(storeConfigName, s);
	}
	static RemoteStoreProviderDriver getProviderStore(String storeConfigName) throws KeystoreException{
		RemoteStoreProviderDriver s = _providerStore.get(storeConfigName);
		if(s==null) {
			throw new KeystoreException("ProviderStore '"+storeConfigName+"' not exists");
		}
		return s;
	}
	
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockStore = new HashMap<>();
	private static synchronized org.openspcoop2.utils.Semaphore initLockStore(String nomeRemoteStore){
		org.openspcoop2.utils.Semaphore s = _lockStore.get(nomeRemoteStore);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getGestioneToken_validazioneJWT_lock_permits();
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenValidazioneJWT_"+nomeRemoteStore, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenValidazioneJWT_"+nomeRemoteStore);
			}
			_lockStore.put(nomeRemoteStore, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockStore(String nomeRemoteStore){
		org.openspcoop2.utils.Semaphore s = _lockStore.get(nomeRemoteStore);
		if(s==null) {
			s = initLockStore(nomeRemoteStore);
		}
		return s;
	}
	
	
	static String getRemoteStoreConfigName(RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		if(remoteStoreConfig==null) {
			throw new KeystoreException("RemoteStoreConfig undefined");
		}
		String remoteStoreName = remoteStoreConfig.getStoreName();
		if(remoteStoreName==null) {
			throw new KeystoreException("RemoteStoreConfig name undefined");
		}
		return remoteStoreName;
	}
	
	
	
	private DriverConfigurazioneDB driverConfigurazioneDB = null;
	
	private Logger log;
	
	private RemoteStoreConfig remoteStoreConfig;
	private String keyAlgorithm;
	private long remoteStoreId;
	
	private RemoteStoreProviderDriver(Logger log, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		
		this.log = log;
		
		getRemoteStoreConfigName(remoteStoreConfig);
		this.remoteStoreConfig = remoteStoreConfig;
		
		this.keyAlgorithm = remoteStoreConfig.getKeyAlgorithm();
		if(this.keyAlgorithm==null) {
			this.keyAlgorithm = KeyUtils.ALGO_RSA;
		}
			
		Object oConfig = ConfigurazionePdDReader.getDriverConfigurazionePdD();
		if(oConfig instanceof DriverConfigurazioneDB) {
			this.driverConfigurazioneDB = (DriverConfigurazioneDB) oConfig;
		}
		else {
			throw new KeystoreException("RemoteStoreProvider utilizzabile solamente con una configurazione su database");
		}
		
		this.remoteStoreId = RemoteStoreProviderDriverUtils.registerIfNotExistsRemoteStore(this.driverConfigurazioneDB, this.remoteStoreConfig);
	}
	
	
	private String getPrefixKid(String keyId) {
		return "Chiave con kid '"+keyId+"'";
	}
	
	public Object readKey(RemoteKeyType remoteStoreKeyType, String keyId, ByteArrayOutputStream bout) throws KeystoreException {
		
		byte [] key = null;
		try {
			key = RemoteStoreProviderDriverUtils.getRemoteStoreKey(this.driverConfigurazioneDB, this.remoteStoreId, keyId);
		}catch(KeystoreNotFoundException notFound) {
			String msg = getPrefixKid(keyId)+" non presente su database";
			this.log.debug(msg);
			// ignore
		}
		if(key!=null) {
			try {
				switch (remoteStoreKeyType) {
					case JWK:
						return new JWK(new String(key));
					case PUBLIC_KEY:
						return KeyUtils.getInstance(this.keyAlgorithm).getPublicKey(key);
					case X509:
						return ArchiveLoader.load(key);
				}
			}catch(Exception e) {
				throw new KeystoreException(e.getMessage(),e);
			}
		}
		
		org.openspcoop2.utils.Semaphore semaphore = getLockStore(this.remoteStoreConfig.getStoreName());
		try {
			semaphore.acquire("readKey");
		}catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		try (ByteArrayOutputStream boutInternal = new ByteArrayOutputStream()){
			
			ByteArrayOutputStream b = bout!=null ? bout : boutInternal;
			
			Object objectKey = null;
			switch (remoteStoreKeyType) {
			case JWK:
				objectKey = RemoteStoreUtils.readJWK(keyId, this.remoteStoreConfig, b);
				break;
			case PUBLIC_KEY:
				objectKey = RemoteStoreUtils.readPublicKey(keyId, this.remoteStoreConfig, b);
				break;
			case X509:
				objectKey = RemoteStoreUtils.readX509(keyId, this.remoteStoreConfig, b);
				break;
			}
			
			String msg = getPrefixKid(keyId)+" ottenuta da remote store config, aggiunto come entry sul db ...";
			this.log.debug(msg);
			int rows = RemoteStoreProviderDriverUtils.addRemoteStoreKey(this.driverConfigurazioneDB, this.remoteStoreId, keyId, b.toByteArray());
			msg = getPrefixKid(keyId)+" ottenuta da remote store config aggiunta come entry sul db (updateRows:"+rows+")";
			this.log.debug(msg);
			
			return objectKey;
			
		}catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}finally {
			semaphore.release("readKey");
		}
		
	}
	
	@Override
	public JWK readJWK(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		try {
			return (JWK) readKey(RemoteKeyType.JWK, keyId, null);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public JWK readJWK(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException {
		try {
			return (JWK) readKey(RemoteKeyType.JWK, keyId, bout);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public Certificate readX509(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		try {
			return (Certificate) readKey(RemoteKeyType.X509, keyId, null);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public Certificate readX509(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout)
			throws UtilsException {
		try {
			return (Certificate) readKey(RemoteKeyType.X509, keyId, bout);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public PublicKey readPublicKey(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		try {
			return (PublicKey) readKey(RemoteKeyType.PUBLIC_KEY, keyId, null);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public PublicKey readPublicKey(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout)
			throws UtilsException {
		try {
			return (PublicKey) readKey(RemoteKeyType.PUBLIC_KEY, keyId, bout);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
}
