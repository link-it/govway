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


package org.openspcoop2.utils.security;

import java.security.Provider;
import java.security.Security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.xml.security.encryption.XMLCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractXmlCipher {

	protected java.security.Provider provider;
	private java.security.Key keyWrapped;
	private java.security.cert.Certificate certificateWrapped;
	protected SecretKey secretKeyWrapped;
	private int mode;
	private SymmetricKeyWrappedMode encryptedKeyMode;
	protected SecretKey secretKeyEncrypt;

	public boolean isEncryptedKey() throws UtilsException {
		if(this.encryptedKeyMode!=null) {
			switch (this.encryptedKeyMode) {
				case SYM_ENC_KEY_NO_WRAPPED:
					return false;
				case SYM_ENC_KEY_WRAPPED_SYMMETRIC_KEY:
				case SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY:
					return true;
			}
		}
		throw new UtilsException("Wrapped Key Mode undefined");
	}
	
	// BOTH
	
	protected AbstractXmlCipher(int mode, java.security.KeyStore keystore, boolean symmetricKey, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String alias, String passwordPrivateKey) throws UtilsException{
		this(mode, new KeyStore(keystore), symmetricKey, wrappedSymmetricKeyMode, alias, passwordPrivateKey, false);
	}
	protected AbstractXmlCipher(int mode, java.security.KeyStore keystore, boolean symmetricKey, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		this(mode, new KeyStore(keystore), symmetricKey, wrappedSymmetricKeyMode, alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	protected AbstractXmlCipher(int mode, KeyStore keystore, boolean symmetricKey, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String alias, String passwordPrivateKey) throws UtilsException{
		this(mode, keystore, symmetricKey, wrappedSymmetricKeyMode, alias, passwordPrivateKey, false);
	}
	protected AbstractXmlCipher(int mode, KeyStore keystore, boolean symmetricKey, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		if(symmetricKey) {
			SecretKey secretKey = keystore.getSecretKey(alias, passwordPrivateKey);
			this.initSymmetric(mode, wrappedSymmetricKeyMode, secretKey, addBouncyCastleProvider);
		}
		else {
			this.initPrivate(mode, keystore, alias, passwordPrivateKey, addBouncyCastleProvider);
		}
	}
	
	protected AbstractXmlCipher(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, java.security.Key key) throws UtilsException{
		this(mode, wrappedSymmetricKeyMode, key, false);
	}
	protected AbstractXmlCipher(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, java.security.Key key,boolean addBouncyCastleProvider) throws UtilsException{
		if(key instanceof SecretKey){
			this.initSymmetric(mode, wrappedSymmetricKeyMode, (SecretKey) key, addBouncyCastleProvider);
		}
		else{
			this.mode = mode;
			this.keyWrapped = key;
			this.encryptedKeyMode = SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY;
			this.init(addBouncyCastleProvider);
		}
	}

	protected AbstractXmlCipher(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, java.security.KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException{
		this(mode, wrappedSymmetricKeyMode, new KeyStore(keystore), alias, passwordPrivateKey, false);
	}
	protected AbstractXmlCipher(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, java.security.KeyStore keystore, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		this(mode, wrappedSymmetricKeyMode, new KeyStore(keystore), alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	protected AbstractXmlCipher(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException{
		this(mode, wrappedSymmetricKeyMode, keystore, alias, passwordPrivateKey, false);
	}
	protected AbstractXmlCipher(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, KeyStore keystore, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		if("jceks".equalsIgnoreCase(keystore.getKeystore().getType())) {
			SecretKey secretKey = keystore.getSecretKey(alias, passwordPrivateKey);
			this.initSymmetric(mode, wrappedSymmetricKeyMode, secretKey, addBouncyCastleProvider);
		}
		else {
			this.initPrivate(mode, keystore, alias, passwordPrivateKey, addBouncyCastleProvider);
		}
	}
	
	
	// SYMMETRIC
	
	protected AbstractXmlCipher(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, SecretKey secretKey) throws UtilsException{
		this(mode, wrappedSymmetricKeyMode, secretKey, false);
	}
	protected AbstractXmlCipher(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, SecretKey secretKey,boolean addBouncyCastleProvider) throws UtilsException{
		this.initSymmetric(mode, wrappedSymmetricKeyMode, secretKey, addBouncyCastleProvider);
	}
	
	protected AbstractXmlCipher(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String keyAlgorithm) throws UtilsException{
		this(mode, wrappedSymmetricKeyMode, keyAlgorithm, false);
	}
	protected AbstractXmlCipher(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String keyAlgorithm,boolean addBouncyCastleProvider) throws UtilsException{
		this(mode, wrappedSymmetricKeyMode, generateSecretKey(keyAlgorithm),addBouncyCastleProvider);
	}
	
	private void initSymmetric(int mode, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, SecretKey secretKey, boolean addBouncyCastleProvider) throws UtilsException{
		this.mode = mode;
		switch (wrappedSymmetricKeyMode) {
		case SYM_ENC_KEY_NO_WRAPPED:
			this.secretKeyEncrypt  = secretKey;
			break;
		default:
			this.secretKeyWrapped = secretKey;
			break;
		}
		this.encryptedKeyMode = wrappedSymmetricKeyMode;
		this.init(addBouncyCastleProvider);
	}
	
	public SecretKey getSecretKeyEncryption() {
		return this.secretKeyEncrypt;
	}
	public static SecretKey generateSecretKey(String algorithm) throws UtilsException {
		return generateSecretKey(algorithm, null);
	}
	public static SecretKey generateSecretKey(String algorithm, Provider provider) throws UtilsException {
		KeyGenerator keyGenerator = null;
		try {
			if(provider!=null) {
				keyGenerator = KeyGenerator.getInstance(algorithm, provider);
			}
			else {
				keyGenerator = KeyGenerator.getInstance(algorithm);
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		return keyGenerator.generateKey();
	}
	
	
	// ASYMMETRIC
	
	private void initPrivate(int mode, KeyStore keystore, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		this.mode = mode;
		this.keyWrapped = keystore.getPrivateKey(alias, passwordPrivateKey);
		this.encryptedKeyMode = SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY;
		// Non sembra necessario
//		if(keystore.getKeystoreType().equalsIgnoreCase("pkcs11")) {
//			this.provider = keystore.getKeystoreProvider();
//		}
		this.init(addBouncyCastleProvider);
	}

	protected AbstractXmlCipher(int mode, java.security.cert.Certificate certificate) throws UtilsException{
		this(mode, certificate, false);
	}
	protected AbstractXmlCipher(int mode, java.security.cert.Certificate certificate,boolean addBouncyCastleProvider) throws UtilsException{
		this.mode = mode;
		this.certificateWrapped = certificate;
		this.encryptedKeyMode = SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY;
		this.init(addBouncyCastleProvider);
	}

	protected AbstractXmlCipher(int mode, java.security.KeyStore keystore, String alias) throws UtilsException{
		this(mode, new KeyStore(keystore), alias, false);
	}
	protected AbstractXmlCipher(int mode, java.security.KeyStore keystore, String alias,boolean addBouncyCastleProvider) throws UtilsException{
		this(mode, new KeyStore(keystore), alias, addBouncyCastleProvider);
	}
	protected AbstractXmlCipher(int mode, KeyStore keystore, String alias) throws UtilsException{
		this(mode, keystore, alias, false);
	}
	protected AbstractXmlCipher(int mode, KeyStore keystore, String alias,boolean addBouncyCastleProvider) throws UtilsException{
		this.mode = mode;
		this.certificateWrapped = keystore.getCertificate(alias);
		this.encryptedKeyMode = SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY;
		this.init(addBouncyCastleProvider);
	}

	protected AbstractXmlCipher(int mode, java.security.KeyStore keystore) throws UtilsException{
		this(mode, new KeyStore(keystore), false);
	}
	protected AbstractXmlCipher(int mode, java.security.KeyStore keystore,boolean addBouncyCastleProvider) throws UtilsException{
		this(mode, new KeyStore(keystore), addBouncyCastleProvider);
	}
	protected AbstractXmlCipher(int mode, KeyStore keystore) throws UtilsException{
		this(mode, keystore, false);
	}
	protected AbstractXmlCipher(int mode, KeyStore keystore,boolean addBouncyCastleProvider) throws UtilsException{
		this.mode = mode;
		this.certificateWrapped = keystore.getCertificate();
		this.encryptedKeyMode = SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY;
		this.init(addBouncyCastleProvider);
	}

	private void init(boolean addBouncyCastleProvider) throws UtilsException{
		try{

			// Providers
			if(addBouncyCastleProvider){
				BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
				Security.addProvider(bouncyCastleProvider);
			}

			org.apache.xml.security.Init.init();

		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	protected org.apache.xml.security.encryption.XMLCipher getXMLCipher() throws UtilsException{
		try{
			org.apache.xml.security.encryption.XMLCipher xmlCipher = null; 
			if(this.provider!=null) {
				xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance(this.provider.getName());
			}
			else {
				xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance();
			}
			xmlCipher.init(this.mode, null);
			return xmlCipher;			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	protected org.apache.xml.security.encryption.XMLCipher getXMLCipher(String algorithm, String canon, String digest) throws UtilsException{
		try{
			org.apache.xml.security.encryption.XMLCipher xmlCipher = null;
			if(canon!=null || digest!=null) {
				if(this.provider!=null) {
					xmlCipher = org.apache.xml.security.encryption.XMLCipher.getProviderInstance(algorithm, this.provider.getName(), canon, digest);
				}
				else {
					xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance(algorithm,canon,digest);
				}
			}
			else {
				if(this.provider!=null) {
					xmlCipher = org.apache.xml.security.encryption.XMLCipher.getProviderInstance(algorithm, this.provider.getName());
				}
				else {
					xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance(algorithm);
				}
			}
			if(this.secretKeyEncrypt==null) {
				throw new Exception("Key for initialize cipher engine not found");
			}
			xmlCipher.init(this.mode, this.secretKeyEncrypt);
			return xmlCipher;			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	protected org.apache.xml.security.encryption.XMLCipher getXMLCipherUnwrappedKey() throws UtilsException{
		return this.getXMLCipherWrappedKey(null);
	}
	protected org.apache.xml.security.encryption.XMLCipher getXMLCipherWrappedKey(String algorithm) throws UtilsException{
		try{
			org.apache.xml.security.encryption.XMLCipher xmlCipher = null;
			if(algorithm!=null){
				if(this.provider!=null) {
					xmlCipher = org.apache.xml.security.encryption.XMLCipher.getProviderInstance(algorithm, this.provider.getName());
				}
				else {
					xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance(algorithm);
				}
			}
			else{
				if(this.provider!=null) {
					xmlCipher = org.apache.xml.security.encryption.XMLCipher.getProviderInstance(this.provider.getName());
				}
				else {
					xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance();
				}
			}
			int wrapMode = -1;
			if(XMLCipher.ENCRYPT_MODE == this.mode){
				wrapMode = XMLCipher.WRAP_MODE;
			}
			else{
				wrapMode = XMLCipher.UNWRAP_MODE;
			}
			if(this.secretKeyWrapped!=null){
				xmlCipher.init(wrapMode, this.secretKeyWrapped);
			}
			else if(this.keyWrapped!=null){
				xmlCipher.init(wrapMode, this.keyWrapped);
			}
			else if(this.certificateWrapped!=null){
				xmlCipher.init(wrapMode, this.certificateWrapped.getPublicKey());
			}
			else {
				throw new Exception("Key for initialize cipher engine 'wrapped key' not found");
			}
			return xmlCipher;			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}

