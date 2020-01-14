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


package org.openspcoop2.utils.security;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XmlEncrypt extends AbstractXmlCipher {

	// BOTH 
	
	public XmlEncrypt(java.security.KeyStore keystore, boolean symmetricKey, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String alias, String passwordPrivateKey) throws UtilsException{
		super(XMLCipher.ENCRYPT_MODE, keystore, symmetricKey, wrappedSymmetricKeyMode, alias, passwordPrivateKey, false);
	}
	public XmlEncrypt(java.security.KeyStore keystore, boolean symmetricKey, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		super(XMLCipher.ENCRYPT_MODE, keystore, symmetricKey, wrappedSymmetricKeyMode, alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	public XmlEncrypt(KeyStore keystore, boolean symmetricKey, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String alias, String passwordPrivateKey) throws UtilsException{
		super(XMLCipher.ENCRYPT_MODE, keystore, symmetricKey, wrappedSymmetricKeyMode, alias, passwordPrivateKey, false);
	}
	public XmlEncrypt(KeyStore keystore, boolean symmetricKey, SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String alias, String passwordPrivateKey,boolean addBouncyCastleProvider) throws UtilsException{
		super(XMLCipher.ENCRYPT_MODE, keystore, symmetricKey, wrappedSymmetricKeyMode, alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	
	public XmlEncrypt(SymmetricKeyWrappedMode wrappedSymmetricKeyMode, java.security.KeyStore keystore, String alias, String passwordPrivateKey,
			boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, wrappedSymmetricKeyMode, keystore, alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	public XmlEncrypt(SymmetricKeyWrappedMode wrappedSymmetricKeyMode, java.security.KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, wrappedSymmetricKeyMode, keystore, alias, passwordPrivateKey);
	}
	public XmlEncrypt(SymmetricKeyWrappedMode wrappedSymmetricKeyMode, KeyStore keystore, String alias, String passwordPrivateKey,
			boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, wrappedSymmetricKeyMode, keystore, alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	public XmlEncrypt(SymmetricKeyWrappedMode wrappedSymmetricKeyMode, KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, wrappedSymmetricKeyMode, keystore, alias, passwordPrivateKey);
	}
	
	
	// SYMMETRIC
	
	public XmlEncrypt(SymmetricKeyWrappedMode wrappedSymmetricKeyMode, SecretKey secretKey) throws UtilsException{
		super(XMLCipher.ENCRYPT_MODE, wrappedSymmetricKeyMode, secretKey);
	}
	public XmlEncrypt(SymmetricKeyWrappedMode wrappedSymmetricKeyMode, SecretKey secretKey,boolean addBouncyCastleProvider) throws UtilsException{
		super(XMLCipher.ENCRYPT_MODE, wrappedSymmetricKeyMode, secretKey, addBouncyCastleProvider);
	}
	
	public XmlEncrypt(SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String keyAlgorithm, boolean addBouncyCastleProvider)
			throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, wrappedSymmetricKeyMode, keyAlgorithm, addBouncyCastleProvider);
	}
	public XmlEncrypt(SymmetricKeyWrappedMode wrappedSymmetricKeyMode, String keyAlgorithm) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, wrappedSymmetricKeyMode, keyAlgorithm);
	}

	
	// ASYMMETRIC
	
	public XmlEncrypt(Certificate certificate, boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, certificate, addBouncyCastleProvider);
	}
	public XmlEncrypt(Certificate certificate) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, certificate);
	}

	public XmlEncrypt(PrivateKey key, boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY, key, addBouncyCastleProvider);
	}
	public XmlEncrypt(PrivateKey key) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY, key);
	}
	
	public XmlEncrypt(PublicKey key, boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY, key, addBouncyCastleProvider);
	}
	public XmlEncrypt(PublicKey key) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY, key);
	}

	public XmlEncrypt(java.security.KeyStore keystore, boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, keystore, addBouncyCastleProvider);
	}
	public XmlEncrypt(java.security.KeyStore keystore) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, keystore);
	}
	public XmlEncrypt(KeyStore keystore, boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, keystore, addBouncyCastleProvider);
	}
	public XmlEncrypt(KeyStore keystore) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, keystore);
	}

	public XmlEncrypt(java.security.KeyStore keystore, String alias, boolean addBouncyCastleProvider)
			throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, keystore, alias, addBouncyCastleProvider);
	}
	public XmlEncrypt(java.security.KeyStore keystore, String alias) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, keystore, alias);
	}
	public XmlEncrypt(KeyStore keystore, String alias, boolean addBouncyCastleProvider)
			throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, keystore, alias, addBouncyCastleProvider);
	}
	public XmlEncrypt(KeyStore keystore, String alias) throws UtilsException {
		super(XMLCipher.ENCRYPT_MODE, keystore, alias);
	}



	public Document encryptSymmetric(Document document, String encryptAlgorithm) throws UtilsException{
		return this.encrypt(document, document.getDocumentElement(), encryptAlgorithm, null, null, null, null);
	}
	public Document encryptSymmetric(Document document, String encryptAlgorithm, String canonicalizationMethod, String digestMethod) throws UtilsException{
		return this.encrypt(document, document.getDocumentElement(), encryptAlgorithm, canonicalizationMethod, digestMethod, null, null);
	}
	public Document encrypt(Document document, String encryptAlgorithm, String keyAlgorithm, String wrappedKeyAlgorithm) throws UtilsException{
		return this.encrypt(document, document.getDocumentElement(), encryptAlgorithm, null, null, keyAlgorithm, wrappedKeyAlgorithm);
	}
	public Document encrypt(Document document, String encryptAlgorithm, String keyAlgorithm, String canonicalizationMethod, String digestMethod, 
			String wrappedKeyAlgorithm) throws UtilsException{
		return this.encrypt(document, document.getDocumentElement(), encryptAlgorithm, canonicalizationMethod, digestMethod,keyAlgorithm, wrappedKeyAlgorithm);
	}
	
	public Document encryptSymmetric(Element element, String encryptAlgorithm) throws UtilsException{
		return this.encrypt(element.getOwnerDocument(), element, encryptAlgorithm, null, null, null, null);
	}
	public Document encryptSymmetric(Element element, String encryptAlgorithm, String canonicalizationMethod, String digestMethod) throws UtilsException{
		return this.encrypt(element.getOwnerDocument(), element, encryptAlgorithm, canonicalizationMethod, digestMethod, null, null);
	}
	public Document encrypt(Element element, String encryptAlgorithm, String keyAlgorithm, String wrappedKeyAlgorithm) throws UtilsException{
		return this.encrypt(element.getOwnerDocument(), element, encryptAlgorithm, null,null, keyAlgorithm, wrappedKeyAlgorithm);
	}
	public Document encrypt(Element element, String encryptAlgorithm, String canonicalizationMethod, String digestMethod, 
			String keyAlgorithm, String wrappedKeyAlgorithm) throws UtilsException{
		return this.encrypt(element.getOwnerDocument(), element, encryptAlgorithm, canonicalizationMethod, digestMethod, keyAlgorithm, wrappedKeyAlgorithm);
	}
	
	// String canonicalizationMethod, String digestMethod
	
	public Document encryptSymmetric(Document document, Element element, String encryptAlgorithm) throws UtilsException{
		return this.encrypt(document, element, encryptAlgorithm, null, null, null, null);
	}
	public Document encryptSymmetric(Document document, Element element, String encryptAlgorithm, String canonicalizationMethod, String digestMethod) throws UtilsException{
		return this.encrypt(document, element, encryptAlgorithm, canonicalizationMethod, digestMethod, null, null);
	}
	public Document encrypt(Document document, Element element, String encryptAlgorithm, String keyAlgorithm, String wrappedKeyAlgorithm) throws UtilsException{
		return this.encrypt(document, element, encryptAlgorithm, null, null, keyAlgorithm, wrappedKeyAlgorithm);
	}
	public Document encrypt(Document document, Element element, String encryptAlgorithm, String canonicalizationMethod, String digestMethod,
			String keyAlgorithm, String wrappedKeyAlgorithm) throws UtilsException{
		try{
			if(encryptAlgorithm==null){
				throw new UtilsException("Encrypt Algorithm undefined");
			}
						
			EncryptedKey encryptedKey = null;
			if(super.isEncryptedKey()){
				if(wrappedKeyAlgorithm==null){
					throw new UtilsException("WrappedKeyAlgorithm undefined");
				}
				org.apache.xml.security.encryption.XMLCipher xmlCipherWrappedAlgorithm = super.getXMLCipherWrappedKey(wrappedKeyAlgorithm);
				if(keyAlgorithm==null){
					throw new UtilsException("KeyAlgorithm undefined");
				}
				super.secretKeyEncrypt = generateSecretKey(keyAlgorithm);
				encryptedKey = xmlCipherWrappedAlgorithm.encryptKey(document, super.secretKeyEncrypt);
			}
			
			org.apache.xml.security.encryption.XMLCipher xmlCipher = super.getXMLCipher(encryptAlgorithm, canonicalizationMethod, digestMethod);
			
			if(encryptedKey!=null){
				EncryptedData encryptedData = xmlCipher.getEncryptedData();
				KeyInfo info = new KeyInfo(document);
				info.add(encryptedKey);
				encryptedData.setKeyInfo(info);
			}
			
			return xmlCipher.doFinal(document, element, true);			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
