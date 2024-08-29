/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.certificate;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.util.io.pem.PemReader;
import org.openspcoop2.utils.UtilsException;

/**	
 * KeyUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeyUtils {

	public static final String ALGO_RSA = "RSA";
	public static final String ALGO_DSA = "DSA";
	public static final String ALGO_DH = "DH"; // Diffie-Hellman
	public static final String ALGO_EC = "EC"; // Elliptic Curve Digital Signature Algorithm o ECDH (Elliptic Curve Diffie-Hellman).

	
	public static KeyUtils getInstance() throws UtilsException {
		return new KeyUtils();
	}
	public static KeyUtils getInstance(String algo) throws UtilsException {
		return new KeyUtils(algo);
	}
	
	private KeyFactory kf;
	
	public KeyUtils() throws UtilsException {
		this(ALGO_RSA);
	}
	public KeyUtils(String algo) throws UtilsException {
		try {
			this.kf = KeyFactory.getInstance(algo);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	// ** PUBLIC KEY **/
	
	public PublicKey readPublicKeyPEMFormat(byte[] publicKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(publicKey);
		if(pemArchive.getPublicKey()!=null) {
			publicKey = pemArchive.getPublicKey().getBytes();
		}
		
		try {
			try (ByteArrayInputStream bin = new ByteArrayInputStream(publicKey);
					InputStreamReader ir = new InputStreamReader(bin);
					PemReader pemReader = new PemReader(ir);){
				byte [] encoded = pemReader.readPemObject().getContent();
				X509EncodedKeySpec specPub = new X509EncodedKeySpec(encoded);
				return this.kf.generatePublic(specPub);
	        } 
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public PublicKey readPublicKeyDERFormat(byte[] publicKey) throws UtilsException {
		try {
			X509EncodedKeySpec specPub = new X509EncodedKeySpec(publicKey);
			return this.kf.generatePublic(specPub);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public PublicKey readCertificate(byte[] publicKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(publicKey);
		if(pemArchive.getCertificates()!=null && !pemArchive.getCertificates().isEmpty()) {
			String cert = pemArchive.getCertificates().get(0); // prendo il primo
			if(cert!=null && StringUtils.isNotEmpty(cert)) {
				publicKey = cert.getBytes();
			}
		}
		
		return ArchiveLoader.load(publicKey).getCertificate().getCertificate().getPublicKey();
	}
	
	public PublicKey getPublicKey(byte[] publicKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(publicKey);
		
		if(pemArchive.getPublicKey()!=null) {
			return this.readPublicKeyPEMFormat(pemArchive.getPublicKey().getBytes());
		}
		else if(pemArchive.getCertificates()!=null && !pemArchive.getCertificates().isEmpty()) {
			String cert = pemArchive.getCertificates().get(0); // prendo il primo
			if(cert!=null && StringUtils.isNotEmpty(cert)) {
				return this.readCertificate(cert.getBytes());
			}
		}
		
		try {
			return readPublicKeyDERFormat(publicKey);
		}catch(Exception e) {
			// provo X509
			try {
				return readCertificate(publicKey);
			}catch(Exception ignore) {
				// rilancio eccezione precedente
				throw new UtilsException(e.getMessage(),e);
			}
		}
		
	}
	
	
	// ** PRIVATE KEY **/
	
	public PrivateKey readPKCS1PrivateKeyPEMFormat(byte[] privateKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(privateKey,true,false,false);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();
		}
		
		// Legge nel formato PEM PKCS1 e lo porta in PKCS8
		
		try {
			try (ByteArrayInputStream bin = new ByteArrayInputStream(privateKey);
					InputStreamReader ir = new InputStreamReader(bin);
					PEMParser pemParser = new PEMParser(ir);){
				JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
				Object object = pemParser.readObject();
				KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
				return kp.getPrivate(); 
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public PrivateKey readPKCS8PrivateKeyPEMFormat(byte[] privateKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(privateKey,false,true,false);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();
		}
		
		try {
			try (ByteArrayInputStream bin = new ByteArrayInputStream(privateKey);
					InputStreamReader ir = new InputStreamReader(bin);
					PemReader pemReader = new PemReader(ir);){
				byte [] encoded = pemReader.readPemObject().getContent();
				PKCS8EncodedKeySpec specPriv = new PKCS8EncodedKeySpec(encoded);
				return this.kf.generatePrivate(specPriv);
	        } 
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public PrivateKey readPKCS8PrivateKeyDERFormat(byte[] privateKey) throws UtilsException {
		try {
			PKCS8EncodedKeySpec specPriv = new PKCS8EncodedKeySpec(privateKey);
			return this.kf.generatePrivate(specPriv);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public PrivateKey getPrivateKey(byte[] privateKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(privateKey);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();
			
			if(pemArchive.isPkcs1()) {
				return this.readPKCS1PrivateKeyPEMFormat(privateKey);	
			}
			else if(pemArchive.isPkcs8()) {
				return this.readPKCS8PrivateKeyPEMFormat(privateKey);
			}
		}
		
		return readPKCS8PrivateKeyDERFormat(privateKey);
	}
	
	
	// ** PRIVATE KEY ENCRYPTED **/
	
	public PrivateKey readPKCS1EncryptedPrivateKeyPEMFormat(byte[] privateKey, String password) throws UtilsException{
		
		PEMReader pemArchive = new PEMReader(privateKey,true,false,false);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();
		}
		
		// Legge nel formato PEM PKCS1 e lo porta in PKCS8
		
		try {
			try (ByteArrayInputStream bin = new ByteArrayInputStream(privateKey);
					InputStreamReader ir = new InputStreamReader(bin);
					PEMParser pemParser = new PEMParser(ir);){
				JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
				Object object = pemParser.readObject();
				PEMEncryptedKeyPair pair = (PEMEncryptedKeyPair) object;
				JcePEMDecryptorProviderBuilder jce = new JcePEMDecryptorProviderBuilder().setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
				PEMDecryptorProvider decProv = jce.build(password.toCharArray());
				KeyPair kp = converter.getKeyPair(pair.decryptKeyPair(decProv));
				return kp.getPrivate(); 
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}

	}
		
	public PrivateKey readPKCS8EncryptedPrivateKeyPEMFormat(byte[] privateKey, String password) throws UtilsException{
			
		PEMReader pemArchive = new PEMReader(privateKey,false,false,true);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();
		}
		
		PKCS8EncryptedPrivateKeyInfo pair = null;
		try {
			try (ByteArrayInputStream bin = new ByteArrayInputStream(privateKey);
					InputStreamReader ir = new InputStreamReader(bin);
					PEMParser parser = new PEMParser(ir);){
				pair = (PKCS8EncryptedPrivateKeyInfo)parser.readObject();
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		return readPKCS8EncryptedPrivateKey(pair, password);

	}
	
	public PrivateKey readPKCS8EncryptedPrivateKeyDERFormat(byte[] privateKey, String password) throws UtilsException{
		PKCS8EncryptedPrivateKeyInfo pair = null;
		try {
			pair = new PKCS8EncryptedPrivateKeyInfo(privateKey);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		return readPKCS8EncryptedPrivateKey(pair, password);
	}
	
	private PrivateKey readPKCS8EncryptedPrivateKey(PKCS8EncryptedPrivateKeyInfo pair, String password) throws UtilsException{
		try {
			JceOpenSSLPKCS8DecryptorProviderBuilder jce = new JceOpenSSLPKCS8DecryptorProviderBuilder().setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			InputDecryptorProvider decProv = jce.build(password.toCharArray());
			PrivateKeyInfo keyInfo = pair.decryptPrivateKeyInfo(decProv);
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			return converter.getPrivateKey(keyInfo);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public PrivateKey getPrivateKey(byte[] privateKey, String password) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(privateKey);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();
			
			if(pemArchive.isPkcs8encrypted()) {
				return this.readPKCS8EncryptedPrivateKeyPEMFormat(privateKey, password);
			}
			else if(pemArchive.isPkcs1()) {
				try {
					return this.readPKCS1EncryptedPrivateKeyPEMFormat(privateKey, password);
				}catch(Exception e) {
					// provo senza password
					try {
						return this.readPKCS1PrivateKeyPEMFormat(privateKey);
					}catch(Exception ignore) {
						// rilancio eccezione precedente
						throw new UtilsException(e.getMessage(),e);
					}
				}	
			}
			else if(pemArchive.isPkcs8()) {
				return this.readPKCS8PrivateKeyPEMFormat(privateKey);
			}
		}
				
		try {
			return readPKCS8EncryptedPrivateKeyDERFormat(privateKey, password);
		}catch(Exception e) {
			// provo senza password
			try {
				return readPKCS8PrivateKeyDERFormat(privateKey);
			}catch(Exception ignore) {
				// rilancio eccezione precedente
				throw new UtilsException(e.getMessage(),e);
			}
		}
		
	}

	
}
