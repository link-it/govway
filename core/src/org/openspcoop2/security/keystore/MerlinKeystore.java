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

package org.openspcoop2.security.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Properties;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.hsm.HSMManager;

/**
 * MerlinKeystore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MerlinKeystore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private transient org.openspcoop2.utils.certificate.KeyStore ks = null;
	
	private byte[] ksBytes;
	private String tipoStore = null;
	private String pathStore = null;
	private String passwordStore = null;
	private String passwordPrivateKey = null;
	
	private boolean hsm;
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("KeyStore (").append(this.tipoStore).append(") ").append(this.pathStore);
		return bf.toString();
	}
	
	public MerlinKeystore(String propertyFilePath) throws SecurityException{
		this._initMerlinKeystore(propertyFilePath, null, false);
	}
	public MerlinKeystore(String propertyFilePath,String passwordPrivateKey) throws SecurityException{
		this._initMerlinKeystore(propertyFilePath, passwordPrivateKey, true);
	}
	private void _initMerlinKeystore(String propertyFilePath,String passwordPrivateKey, boolean privatePasswordRequired) throws SecurityException{
		
		InputStream isStore = null;
		try{
			if(propertyFilePath==null){
				throw new Exception("PropertyFilePath per lo Store non indicato");
			}
			
			File fStore = new File(propertyFilePath);
			if(fStore.exists()){
				isStore = new FileInputStream(fStore);
			}else{
				isStore = MerlinKeystore.class.getResourceAsStream(propertyFilePath);
				if(isStore==null){
					isStore = MerlinKeystore.class.getResourceAsStream("/"+propertyFilePath);
				}
				if(isStore==null){
					throw new Exception("Store ["+propertyFilePath+"] not found");
				}
			}
			Properties propStore = new Properties();
			propStore.load(isStore);
			isStore.close();
			
			this._initMerlinKeystore(propStore,passwordPrivateKey, privatePasswordRequired);
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}finally{
			try{
				if(isStore!=null){
					isStore.close();
				}
			}catch(Exception eClose){
				// close
			}
		}
		
	}
	
	public MerlinKeystore(Properties propStore) throws SecurityException{
		this._initMerlinKeystore(propStore,null, false);
	}
	public MerlinKeystore(Properties propStore,String passwordPrivateKey) throws SecurityException{
		this._initMerlinKeystore(propStore,passwordPrivateKey, true);
	}
	private void _initMerlinKeystore(Properties propStore,String passwordPrivateKey, boolean privatePasswordRequired) throws SecurityException{
		
		try{
			if(propStore==null){
				throw new Exception("Properties per lo Store non indicato");
			}
			
			this.tipoStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_TYPE);
			if(this.tipoStore!=null){
				this.tipoStore = this.tipoStore.trim();
			}else{
				this.tipoStore = KeyStore.getDefaultType();
			}
			
			this.pathStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_PATH);
			
			this.passwordStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_PASSWORD);
			
			init(passwordPrivateKey, privatePasswordRequired);
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}
		
	public MerlinKeystore(String pathStore,String tipoStore,String passwordStore) throws SecurityException{
		_initMerlinKeystore(pathStore,tipoStore,passwordStore,null, false);
	}
	public MerlinKeystore(String pathStore,String tipoStore,String passwordStore,String passwordPrivateKey) throws SecurityException{
		_initMerlinKeystore(pathStore,tipoStore,passwordStore,passwordPrivateKey, true);
	}
	public void _initMerlinKeystore(String pathStore,String tipoStore,String passwordStore,String passwordPrivateKey, boolean privatePasswordRequired) throws SecurityException{
			
		this.pathStore = pathStore;
		this.tipoStore = tipoStore;
		this.passwordStore = passwordStore;
		
		init(passwordPrivateKey, privatePasswordRequired);
		
	}
	
	public MerlinKeystore(byte[]bytesKeystore,String tipoStore,String passwordStore) throws SecurityException{
		_initMerlinKeystore(bytesKeystore,tipoStore,passwordStore,null, false);
	}
	public MerlinKeystore(byte[]bytesKeystore,String tipoStore,String passwordStore,String passwordPrivateKey) throws SecurityException{
		_initMerlinKeystore(bytesKeystore,tipoStore,passwordStore,passwordPrivateKey, true);
	}
	public void _initMerlinKeystore(byte[]bytesKeystore,String tipoStore,String passwordStore,String passwordPrivateKey, boolean privatePasswordRequired) throws SecurityException{
			
		this.ksBytes = bytesKeystore;
		this.tipoStore = tipoStore;
		this.passwordStore = passwordStore;
		
		init(passwordPrivateKey, privatePasswordRequired);
		
	}
	
	private void init(String passwordPrivateKey, boolean privatePasswordRequired) throws SecurityException{
		try{
			if(this.tipoStore==null){
				throw new Exception("Tipo dello Store non indicato");
			}
			if(this.passwordStore==null){
				throw new Exception("Password dello Store non indicata");
			}
			
			HSMManager hsmManager = HSMManager.getInstance();
			if(hsmManager!=null) {
				this.hsm = hsmManager.existsKeystoreType(this.tipoStore);
			}
			
			if(!this.hsm) {

				if(this.ksBytes==null && this.pathStore==null){
					throw new Exception("Path per lo Store non indicato");
				}
	
				if(this.ksBytes==null) {
					InputStream isStore = null;
					try{
						File fPathStore = new File(this.pathStore);
						if(fPathStore.exists()){
							isStore = new FileInputStream(fPathStore);
						}else{
							isStore = MerlinKeystore.class.getResourceAsStream(this.pathStore);
							if(isStore==null){
								isStore = MerlinKeystore.class.getResourceAsStream("/"+this.pathStore);
							}
							if(isStore==null){
								throw new Exception("Keystore ["+this.pathStore+"] not found");
							}
						}
						this.ksBytes = Utilities.getAsByteArray(isStore);
					}finally{
						try{
							if(isStore!=null){
								isStore.close();
							}
						}catch(Exception eClose){
							// close
						}
					}
				}
	
			}
			
			this.initKS();
			
			if(passwordPrivateKey==null && privatePasswordRequired){
				if(this.pathStore!=null) {
					throw new Exception("Password chiave privata non indicata per lo Store ["+this.pathStore+"] ");
				}
				else {
					throw new Exception("Password chiave privata non indicata per lo Store ");
				}
			}
			this.passwordPrivateKey = passwordPrivateKey;
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public boolean isHsm() {
		return this.hsm;
	}

	private void checkInit() throws SecurityException{
		if(this.ks==null) {
			this.initKS();
		}
	}
	private synchronized void initKS() throws SecurityException{
		if(this.ks==null) {
			try{
				if(this.hsm) {
					this.ks = HSMManager.getInstance().getKeystore(this.tipoStore);
				}
				else {
					this.ks = new org.openspcoop2.utils.certificate.KeyStore(this.ksBytes, this.tipoStore, this.passwordStore);
					
					// non utilizzabile in hsm, si ottiene errore: java.lang.UnsupportedOperationException: trusted certificates may only be set by token initialization application
					// at jdk.crypto.cryptoki/sun.security.pkcs11.P11KeyStore.engineSetEntry(P11KeyStore.java:1022)
					FixTrustAnchorsNotEmpty.addCertificate(this.ks.getKeystore()); 
				}
			}
			catch(Exception e){
				throw new SecurityException(e.getMessage(),e);
			}
		}
	}
	
	
	public Key getKey(String alias) throws SecurityException {
		return this.getKey(alias, this.passwordPrivateKey);
	}
	public Key getKey(String alias, String password) throws SecurityException {
		if(alias==null) {
			throw new SecurityException("Alias della chiave non fornita");
		}
		if(password==null) {
			throw new SecurityException("Password della chiave non fornita");
		}
		this.checkInit(); // per ripristino da Serializable
		try {
			return this.ks.getPrivateKey(alias, password);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public Certificate getCertificate(String alias) throws SecurityException {
		if(alias==null) {
			throw new SecurityException("Alias non fornito");
		}
		this.checkInit(); // per ripristino da Serializable
		try{
			return this.ks.getCertificate(alias);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

	public org.openspcoop2.utils.certificate.KeyStore getKeyStore() throws SecurityException {
		this.checkInit(); // per ripristino da Serializable
		try{
			return this.ks;
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

}
