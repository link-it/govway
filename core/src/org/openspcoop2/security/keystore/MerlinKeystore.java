/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.io.Serializable;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Properties;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
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
		this.initMerlinKeystoreEngine(propertyFilePath, null, false,
				null);
	}
	public MerlinKeystore(String propertyFilePath, 
			BYOKRequestParams requestParams) throws SecurityException{
		this.initMerlinKeystoreEngine(propertyFilePath, null, false, 
				requestParams);
	}
	public MerlinKeystore(String propertyFilePath,String passwordPrivateKey) throws SecurityException{
		this.initMerlinKeystoreEngine(propertyFilePath, passwordPrivateKey, true,
				null);
	}
	public MerlinKeystore(String propertyFilePath,String passwordPrivateKey, 
			BYOKRequestParams requestParams) throws SecurityException{
		this.initMerlinKeystoreEngine(propertyFilePath, passwordPrivateKey, true,
				requestParams);
	}
	private void initMerlinKeystoreEngine(String propertyFilePath,String passwordPrivateKey, boolean privatePasswordRequired, 
			BYOKRequestParams requestParams) throws SecurityException{
		
		Properties propStore = StoreUtils.readProperties("PropertyFilePath", propertyFilePath);
		this.initMerlinKeystoreEngine(propStore,passwordPrivateKey, privatePasswordRequired,
				requestParams);
				
	}
	
	public MerlinKeystore(Properties propStore) throws SecurityException{
		this.initMerlinKeystoreEngine(propStore,null, false,
				null);
	}
	public MerlinKeystore(Properties propStore,
			BYOKRequestParams requestParams) throws SecurityException{
		this.initMerlinKeystoreEngine(propStore,null, false,
				requestParams);
	}
	public MerlinKeystore(Properties propStore,String passwordPrivateKey) throws SecurityException{
		this.initMerlinKeystoreEngine(propStore,passwordPrivateKey, true,
				null);
	}
	public MerlinKeystore(Properties propStore,String passwordPrivateKey,
			BYOKRequestParams requestParams) throws SecurityException{
		this.initMerlinKeystoreEngine(propStore,passwordPrivateKey, true,
				requestParams);
	}
	private void initMerlinKeystoreEngine(Properties propStore,String passwordPrivateKey, boolean privatePasswordRequired,
			BYOKRequestParams requestParams) throws SecurityException{
		
		try{
			if(propStore==null){
				throw new SecurityException("Properties per lo Store non indicato");
			}
			
			this.tipoStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_TYPE);
			if(this.tipoStore!=null){
				this.tipoStore = this.tipoStore.trim();
			}else{
				this.tipoStore = KeyStore.getDefaultType();
			}
			
			this.pathStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_PATH);
			
			this.passwordStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_PASSWORD);
						
			init(passwordPrivateKey, privatePasswordRequired,
					requestParams);
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}
		
	public MerlinKeystore(String pathStore,String tipoStore,String passwordStore) throws SecurityException{
		initMerlinKeystoreEngine(pathStore,tipoStore,passwordStore,null, false,
				null);
	}
	public MerlinKeystore(String pathStore,String tipoStore,String passwordStore,
			BYOKRequestParams requestParams) throws SecurityException{
		initMerlinKeystoreEngine(pathStore,tipoStore,passwordStore,null, false,
				requestParams);
	}
	public MerlinKeystore(String pathStore,String tipoStore,String passwordStore,String passwordPrivateKey) throws SecurityException{
		initMerlinKeystoreEngine(pathStore,tipoStore,passwordStore,passwordPrivateKey, true,
				null);
	}
	public MerlinKeystore(String pathStore,String tipoStore,String passwordStore,String passwordPrivateKey,
			BYOKRequestParams requestParams) throws SecurityException{
		initMerlinKeystoreEngine(pathStore,tipoStore,passwordStore,passwordPrivateKey, true,
				requestParams);
	}
	public void initMerlinKeystoreEngine(String pathStore,String tipoStore,String passwordStore,String passwordPrivateKey, boolean privatePasswordRequired,
			BYOKRequestParams requestParams) throws SecurityException{
			
		this.pathStore = pathStore;
		this.tipoStore = tipoStore;
		this.passwordStore = passwordStore;
		
		init(passwordPrivateKey, privatePasswordRequired,
				requestParams);
		
	}
	
	public MerlinKeystore(byte[]bytesKeystore,String tipoStore,String passwordStore) throws SecurityException{
		initMerlinKeystoreEngine(bytesKeystore,tipoStore,passwordStore,null, false,
				null);
	}
	public MerlinKeystore(byte[]bytesKeystore,String tipoStore,String passwordStore,
			BYOKRequestParams requestParams) throws SecurityException{
		initMerlinKeystoreEngine(bytesKeystore,tipoStore,passwordStore,null, false,
				requestParams);
	}
	public MerlinKeystore(byte[]bytesKeystore,String tipoStore,String passwordStore,String passwordPrivateKey) throws SecurityException{
		initMerlinKeystoreEngine(bytesKeystore,tipoStore,passwordStore,passwordPrivateKey, true,
				null);
	}
	public MerlinKeystore(byte[]bytesKeystore,String tipoStore,String passwordStore,String passwordPrivateKey,
			BYOKRequestParams requestParams) throws SecurityException{
		initMerlinKeystoreEngine(bytesKeystore,tipoStore,passwordStore,passwordPrivateKey, true,
				requestParams);
	}
	public void initMerlinKeystoreEngine(byte[]bytesKeystore,String tipoStore,String passwordStore,String passwordPrivateKey, boolean privatePasswordRequired,
			BYOKRequestParams requestParams) throws SecurityException{
			
		this.ksBytes = bytesKeystore;
		this.tipoStore = tipoStore;
		this.passwordStore = passwordStore;
		
		init(passwordPrivateKey, privatePasswordRequired,
				requestParams);
		
	}
	
	private void init(String passwordPrivateKey, boolean privatePasswordRequired,
			BYOKRequestParams requestParams) throws SecurityException{
		try{
			if(this.tipoStore==null){
				throw new SecurityException("Tipo dello Store non indicato");
			}
			if(this.passwordStore==null){
				boolean required = true;
				if(KeystoreType.JKS.isType(this.tipoStore)) {
					required = DBUtils.isKeystoreJksPasswordRequired();
				}
				else if(KeystoreType.PKCS12.isType(this.tipoStore)) {
					required = DBUtils.isKeystorePkcs12PasswordRequired();
				}
				if(required) {
					throw new SecurityException("Password dello Store non indicata");
				}
			}
			
			HSMManager hsmManager = HSMManager.getInstance();
			if(hsmManager!=null) {
				this.hsm = hsmManager.existsKeystoreType(this.tipoStore);
			}
			
			if(!this.hsm) {
				initKsBytes(requestParams);
			}
			
			this.initKS();
			
			initPrivateKey(passwordPrivateKey, privatePasswordRequired);
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	private void initPrivateKey(String passwordPrivateKey, boolean privatePasswordRequired) throws SecurityException {
		if(passwordPrivateKey==null && privatePasswordRequired){
			
			boolean required = true;
			if(KeystoreType.JKS.isType(this.tipoStore)) {
				required = DBUtils.isKeystoreJksKeyPasswordRequired();
			}
			else if(KeystoreType.PKCS12.isType(this.tipoStore)) {
				required = DBUtils.isKeystorePkcs12KeyPasswordRequired();
			}
			if(required) {
				if(this.pathStore!=null) {
					throw new SecurityException("Password chiave privata non indicata per lo Store ["+this.pathStore+"] ");
				}
				else {
					throw new SecurityException("Password chiave privata non indicata per lo Store ");
				}
			}
		}
		this.passwordPrivateKey = passwordPrivateKey;
	}
	
	private void initKsBytes(BYOKRequestParams requestParams) throws SecurityException {
		
		if(this.ksBytes==null && this.pathStore==null){
			throw new SecurityException("Path per lo Store non indicato");
		}

		if(this.ksBytes==null) {
			this.ksBytes = StoreUtils.readContent("Path", this.pathStore);
		}
		
		if(requestParams!=null) {
			this.ksBytes = StoreUtils.unwrapBYOK(this.ksBytes, requestParams);
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
			boolean required = true;
			if(KeystoreType.JKS.isType(this.tipoStore)) {
				required = DBUtils.isKeystoreJksKeyPasswordRequired();
			}
			else if(KeystoreType.PKCS12.isType(this.tipoStore)) {
				required = DBUtils.isKeystorePkcs12KeyPasswordRequired();
			}
			if(required) {
				throw new SecurityException("Password della chiave non fornita");
			}
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
