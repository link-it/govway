/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.security.Security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.xml.security.encryption.XMLCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openspcoop2.utils.UtilsException;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractXmlCipher {

	private java.security.Key key;
	private java.security.cert.Certificate certificate;
	private int mode;
	protected SecretKey secretKey;
	private boolean encryptedKey = true;

	public boolean isEncryptedKey() {
		return this.encryptedKey;
	}
	
	// BOTH
	
	protected AbstractXmlCipher(int mode, java.security.KeyStore keystore, boolean symmetricKey, String alias, String passwordPrivateKey) throws UtilsException{
		this(mode, new KeyStore(keystore), symmetricKey, alias, passwordPrivateKey, false);
	}
	protected AbstractXmlCipher(int mode, java.security.KeyStore keystore, boolean symmetricKey, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		this(mode, new KeyStore(keystore), symmetricKey, alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	protected AbstractXmlCipher(int mode, KeyStore keystore, boolean symmetricKey, String alias, String passwordPrivateKey) throws UtilsException{
		this(mode, keystore, symmetricKey, alias, passwordPrivateKey, false);
	}
	protected AbstractXmlCipher(int mode, KeyStore keystore, boolean symmetricKey, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		if(symmetricKey) {
			SecretKey secretKey = keystore.getSecretKey(alias, passwordPrivateKey);
			this.initSymmetric(mode, secretKey, addBouncyCastleProvider);
		}
		else {
			this.initPrivate(mode, keystore, alias, passwordPrivateKey, addBouncyCastleProvider);
		}
	}
	
	
	// SYMMETRIC
	
	protected AbstractXmlCipher(int mode, SecretKey secretKey) throws UtilsException{
		this(mode, secretKey, false);
	}
	protected AbstractXmlCipher(int mode, SecretKey secretKey,boolean addBouncyCastleProvider) throws UtilsException{
		this.initSymmetric(mode, secretKey, addBouncyCastleProvider);
	}
	
	protected AbstractXmlCipher(int mode, String keyAlgorithm) throws UtilsException{
		this(mode, keyAlgorithm, false);
	}
	protected AbstractXmlCipher(int mode, String keyAlgorithm,boolean addBouncyCastleProvider) throws UtilsException{
		this(mode, generateSecretKey(keyAlgorithm),addBouncyCastleProvider);
	}
	
	private void initSymmetric(int mode, SecretKey secretKey,boolean addBouncyCastleProvider) throws UtilsException{
		this.mode = mode;
		this.secretKey = secretKey;
		this.encryptedKey = false;
		this.init(addBouncyCastleProvider);
	}
	
	public SecretKey getSecretKey() {
		return this.secretKey;
	}
	public static SecretKey generateSecretKey(String algorithm) throws UtilsException {
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance(algorithm);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		return keyGenerator.generateKey();
	}
	
	
	// ASYMMETRIC
	
	protected AbstractXmlCipher(int mode, java.security.Key key) throws UtilsException{
		this(mode, key, false);
	}
	protected AbstractXmlCipher(int mode, java.security.Key key,boolean addBouncyCastleProvider) throws UtilsException{
		if(key instanceof SecretKey){
			this.initSymmetric(mode, (SecretKey) key, addBouncyCastleProvider);
		}
		else{
			this.mode = mode;
			this.key = key;
			this.init(addBouncyCastleProvider);
		}
	}

	protected AbstractXmlCipher(int mode, java.security.KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException{
		this(mode, new KeyStore(keystore), alias, passwordPrivateKey, false);
	}
	protected AbstractXmlCipher(int mode, java.security.KeyStore keystore, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		this(mode, new KeyStore(keystore), alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	protected AbstractXmlCipher(int mode, KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException{
		this(mode, keystore, alias, passwordPrivateKey, false);
	}
	protected AbstractXmlCipher(int mode, KeyStore keystore, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		this.initPrivate(mode, keystore, alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	private void initPrivate(int mode, KeyStore keystore, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		this.mode = mode;
		this.key = keystore.getPrivateKey(alias, passwordPrivateKey);
		this.init(addBouncyCastleProvider);
	}

	protected AbstractXmlCipher(int mode, java.security.cert.Certificate certificate) throws UtilsException{
		this(mode, certificate, false);
	}
	protected AbstractXmlCipher(int mode, java.security.cert.Certificate certificate,boolean addBouncyCastleProvider) throws UtilsException{
		this.mode = mode;
		this.certificate = certificate;
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
		this.certificate = keystore.getCertificate(alias);
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
		this.certificate = keystore.getCertificate();
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
			org.apache.xml.security.encryption.XMLCipher xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance();
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
				xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance(algorithm,canon,digest);
			}
			else {
				xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance(algorithm);
			}
			xmlCipher.init(this.mode, this.secretKey);
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
				xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance(algorithm);
			}
			else{
				xmlCipher = org.apache.xml.security.encryption.XMLCipher.getInstance();
			}
			int wrapMode = -1;
			if(XMLCipher.ENCRYPT_MODE == this.mode){
				wrapMode = XMLCipher.WRAP_MODE;
			}
			else{
				wrapMode = XMLCipher.UNWRAP_MODE;
			}
			if(this.key!=null){
				xmlCipher.init(wrapMode, this.key);
			}
			else{
				xmlCipher.init(wrapMode, this.certificate.getPublicKey());
			}
			return xmlCipher;			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
