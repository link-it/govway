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

import java.security.Key;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.certificate.byok.BYOKCostanti;
import org.openspcoop2.utils.certificate.byok.BYOKLocalConfig;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.security.CipherInfo;
import org.openspcoop2.utils.security.Decrypt;
import org.openspcoop2.utils.security.DecryptOpenSSLPass;
import org.openspcoop2.utils.security.DecryptOpenSSLPassPBKDF2;
import org.openspcoop2.utils.security.DecryptWrapKey;
import org.openspcoop2.utils.security.Encrypt;
import org.openspcoop2.utils.security.EncryptOpenSSLPass;
import org.openspcoop2.utils.security.EncryptOpenSSLPassPBKDF2;
import org.openspcoop2.utils.security.EncryptWrapKey;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWEOptions;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonDecrypt;
import org.openspcoop2.utils.security.JsonEncrypt;
import org.openspcoop2.utils.security.JsonUtils;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.security.OpenSSLEncryptionMode;

import com.nimbusds.jose.jwk.KeyUse;

/**     
 * BYOKLocalEncrypt
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKLocalEncrypt {

	private RequestInfo requestInfo;
	
	public BYOKLocalEncrypt(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
	public BYOKLocalEncrypt() {
	}
	
	private static final String JAVA_SEPARATOR = ".";
	
	private String getKeystoreError(BYOKLocalConfig config) {
		return "Access to keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' failed";
	}
	private String getKeyError(BYOKLocalConfig config) {
		return "Access to key ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' failed";
	}
	private static final String ENCODING_MODE_UNDEFINED = "Encoding mode undefined";
	private static final String KEYSTORE_PREFIX = "Keystore [";
	
	public String wrap(BYOKLocalConfig config, 
			String value) throws UtilsException {
		return wrap(config, 
				value.getBytes());
	}
	public String wrap(BYOKLocalConfig config, 
			byte[] value) throws UtilsException {
		
		BYOKEncryptKey byokEncryptKey = new BYOKEncryptKey();

		boolean initPasswordKeyDerivation = false;
		try {
			switch (config.getKeystoreType()) {
			case JKS:
			case PKCS12:
			case PKCS11:
			case JCEKS:
				readKeystore(byokEncryptKey, config, true);
				break;
			case JWK_SET:
				readJwk(byokEncryptKey, config, true);
				break;
			case PUBLIC_KEY:
				readPublicKey(byokEncryptKey, config);
				break;
			case SYMMETRIC_KEY:
				readSecretKey(byokEncryptKey, config);
				break;
			case PASSWORD_KEY_DERIVATION:
				readPasswordKeyDerivation(byokEncryptKey, config, true);
				initPasswordKeyDerivation = true;
				break;
			default:
				throw new UtilsException(KEYSTORE_PREFIX+config.getKeystoreType().getNome()+"] unsupported");
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		/**System.out.println("CONF ["+config.getName()+"] java["+config.isJavaEngine()+"] jose["+config.isJoseEngine()+"]");*/
		if(config.isJavaEngine()) {
			if(config.isKeyWrap()) {
				if(initPasswordKeyDerivation) {
					throw new UtilsException(KEYSTORE_PREFIX+config.getKeystoreType().getNome()+"] unusable with key wrap java mode");
				}
				return encJavaKeyWrap(byokEncryptKey, config, value);
			}
			else {
				return encJava(byokEncryptKey, config, value);
			}
		} 
		else if(config.isJoseEngine()) {
			if(initPasswordKeyDerivation) {
				throw new UtilsException(KEYSTORE_PREFIX+config.getKeystoreType().getNome()+"] unusable with jose mode");
			}
			return encJose(byokEncryptKey, config, value);
		} 
		else if(config.isOpenSSLEngine()) {
			return encOpenSSL(byokEncryptKey, config, value);
		}
		else {
			throw new UtilsException("Encrypt mode undefined");
		}
		
	}
	
	public byte[] unwrap(BYOKLocalConfig config, 
			byte[] value) throws UtilsException {
		return unwrap(config, new String(value));
	}
	public byte[] unwrap(BYOKLocalConfig config, 
			String value) throws UtilsException {
		
		BYOKEncryptKey byokEncryptKey = new BYOKEncryptKey();

		boolean initPasswordKeyDerivation = false;
		try {
			switch (config.getKeystoreType()) {
			case JKS:
			case PKCS12:
			case PKCS11:
			case JCEKS:
				readKeystore(byokEncryptKey, config, false);
				break;
			case JWK_SET:
				readJwk(byokEncryptKey, config, false);
				break;
			case KEY_PAIR:
				readKeyPair(byokEncryptKey, config);
				break;
			case SYMMETRIC_KEY:
				readSecretKey(byokEncryptKey, config);
				break;
			case PASSWORD_KEY_DERIVATION:
				readPasswordKeyDerivation(byokEncryptKey, config, false);
				initPasswordKeyDerivation = true;
				break;
			default:
				throw new UtilsException(KEYSTORE_PREFIX+config.getKeystoreType().getNome()+"] unsupported");
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		/**System.out.println("CONF ["+config.getName()+"] java["+config.isJavaEngine()+"] jose["+config.isJoseEngine()+"]");*/
		if(config.isJavaEngine()) {
			return unwrapJava(initPasswordKeyDerivation, config, value, byokEncryptKey);
		} 
		else if(config.isJoseEngine()) {
			if(initPasswordKeyDerivation) {
				throw new UtilsException(KEYSTORE_PREFIX+config.getKeystoreType().getNome()+"] unusable with jose mode");
			}
			return decryptJose(byokEncryptKey, config, value);
		}
		else if(config.isOpenSSLEngine()) {
			return decryptOpenSSL(byokEncryptKey, config, value);
		}
		else {
			throw new UtilsException("Encrypt mode undefined");
		}
		
	}
	private byte[] unwrapJava(boolean initPasswordKeyDerivation, BYOKLocalConfig config, String value, BYOKEncryptKey byokEncryptKey) throws UtilsException {
		if(initPasswordKeyDerivation) {
			if(config.isKeyWrap()) {
				throw new UtilsException(KEYSTORE_PREFIX+config.getKeystoreType().getNome()+"] unusable with key wrap java mode");
			}
			else {
				readJavaPasswordKeyDerivationForUnwrap(byokEncryptKey, config, value);
			}
		}
		if(config.isKeyWrap()) {
			return decryptJavaKeyWrap(byokEncryptKey, config, value);
		}
		else {
			return decryptJava(byokEncryptKey, config, value);
		}
	}
	
	
	private void readKeystore(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, boolean wrap) throws UtilsException, SecurityException {
		String type = KeystoreType.PKCS11.equals(config.getKeystoreType()) ? config.getKeystoreHsmType() : config.getKeystoreType().getNome();
		MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(this.requestInfo,
				config.getKeystorePath(), type, 
				config.getKeystorePassword());
		if(merlinKs==null || merlinKs.getKeyStore()==null) {
			throw new UtilsException(getKeystoreError(config));
		}
		byokEncryptKey.ks = merlinKs.getKeyStore();
		// bisogna sapere se e' secret o meno
		if(KeystoreType.JCEKS.equals(config.getKeystoreType())) {
			byokEncryptKey.key = byokEncryptKey.ks.getSecretKey(config.getKeyAlias(), config.getKeyPassword());
			byokEncryptKey.secret = true;
		}
		else if(wrap) {
			byokEncryptKey.key = merlinKs.getKeyStore().getPublicKey(config.getKeyAlias());
		}
		else {
			byokEncryptKey.key = merlinKs.getKeyStore().getPrivateKey(config.getKeyAlias(), config.getKeyPassword());
		}
	}
	private void readJwk(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, boolean wrap) throws UtilsException, SecurityException {
		JWKSetStore jwtStore = GestoreKeystoreCache.getJwkSetStore(this.requestInfo, config.getKeystorePath());
		if(jwtStore==null || jwtStore.getJwkSet()==null) {
			throw new UtilsException(getKeystoreError(config));
		}
		byokEncryptKey.jsonWebKeys = jwtStore.getJwkSet().getJsonWebKeys();
		/**if(config.isJavaEngine()) {*/
		// bisogna sapere se e' secret o meno
		JsonWebKey jwk = JsonUtils.readKey(byokEncryptKey.jsonWebKeys, config.getKeyAlias());
		if(jwk==null) {
			throw new UtilsException("Access to keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' failed for alias '"+config.getKeyAlias()+"'");
		}
		if(jwk.getAlgorithm()==null) {
			jwk.setAlgorithm(config.isJavaEngine() ? "A256GCM" : config.getContentAlgorithm());
		}
		if(config.getKeyAlgorithm().contains(KeyUtils.ALGO_RSA)) {
			if(wrap) {
				byokEncryptKey.key = JwkUtils.toRSAPublicKey(jwk);
			}
			else {
				byokEncryptKey.key = JwkUtils.toRSAPrivateKey(jwk);
			}
		}
		else if(config.getKeyAlgorithm().contains(KeyUtils.ALGO_EC)) {
			if(wrap) {
				byokEncryptKey.key = JwkUtils.toECPublicKey(jwk);
			}
			else {
				byokEncryptKey.key = JwkUtils.toECPrivateKey(jwk);
			}
		}
		else {
			byokEncryptKey.key = JwkUtils.toSecretKey(jwk);
			byokEncryptKey.secret = true;
		}
		/**}*/
	}
	
	private String readKeyAlgo(BYOKLocalConfig config) {
		String algo = config.getKeyAlgorithm();
		if(config.isJoseEngine() || config.isKeyWrap()) {
			if(config.getKeyAlgorithm().contains(KeyUtils.ALGO_RSA)) {
				algo = KeyUtils.ALGO_RSA;
			}
			else if(config.getKeyAlgorithm().contains(KeyUtils.ALGO_DSA)) {
				algo = KeyUtils.ALGO_DSA;
			}
			else if(config.getKeyAlgorithm().contains(KeyUtils.ALGO_DH)) {
				algo = KeyUtils.ALGO_DH;
			}
			else if(config.getKeyAlgorithm().contains(KeyUtils.ALGO_EC)) {
				algo = KeyUtils.ALGO_EC;
			}
			else {
				algo = KeyUtils.ALGO_RSA;
			}
		}
		return algo;
	}
	
	private byte[] readKeyInline(BYOKLocalConfig config) throws SecurityException {
		String keyInLine = config.getKeyInline();
		byte [] key = null;
		if(config.isKeyBase64Encoding()) {
			key = Base64Utilities.decode(keyInLine.getBytes());
		}
		else if(config.isKeyHexEncoding()) {
			try {
				key = HexBinaryUtilities.decode(keyInLine);
			}catch(Exception e) {
				throw new SecurityException(e.getMessage());
			}
		}
		else {
			key = keyInLine.getBytes();
		}
		return key;
	}
	private byte[] readEncodedKeyFromPath(BYOKLocalConfig config) throws SecurityException {
		byte [] encodedKey = GestoreKeystoreCache.getExternalResource(this.requestInfo, config.getKeyPath(), null).getResource();
		byte [] key = null;
		if(config.isKeyBase64Encoding()) {
			key = Base64Utilities.decode(encodedKey);
		}
		else {
			try {
				key = HexBinaryUtilities.decode(new String(encodedKey));
			}catch(Exception e) {
				throw new SecurityException(e.getMessage());
			}
		}
		return key;
	}
	
	private byte[] readPublicKeyInline(BYOKLocalConfig config) throws SecurityException {
		String publicKeyInLine = config.getPublicKeyInline();
		byte [] key = null;
		if(config.isPublicKeyBase64Encoding()) {
			key = Base64Utilities.decode(publicKeyInLine.getBytes());
		}
		else if(config.isPublicKeyHexEncoding()) {
			try {
				key = HexBinaryUtilities.decode(publicKeyInLine);
			}catch(Exception e) {
				throw new SecurityException(e.getMessage());
			}
		}
		else {
			key = publicKeyInLine.getBytes();
		}
		return key;
	}
	private byte[] readEncodedPublicKeyFromPath(BYOKLocalConfig config) throws SecurityException {
		byte [] encodedKey = GestoreKeystoreCache.getExternalResource(this.requestInfo, config.getPublicKeyPath(), null).getResource();
		byte [] key = null;
		if(config.isPublicKeyBase64Encoding()) {
			key = Base64Utilities.decode(encodedKey);
		}
		else {
			try {
				key = HexBinaryUtilities.decode(new String(encodedKey));
			}catch(Exception e) {
				throw new SecurityException(e.getMessage());
			}
		}
		return key;
	}
	
	private void readPublicKey(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config) throws UtilsException, SecurityException {
		
		String algo = readKeyAlgo(config);
		
		PublicKeyStore publicKeyStore = null;
		if(config.getKeyInline()!=null && StringUtils.isNotEmpty(config.getKeyInline())) {
			byte [] key = readKeyInline(config);
			publicKeyStore = GestoreKeystoreCache.getPublicKeyStore(this.requestInfo, key, algo);
		}
		else {
			if(config.isKeyBase64Encoding() || config.isKeyHexEncoding()) {
				byte [] key = readEncodedKeyFromPath(config);
				publicKeyStore = GestoreKeystoreCache.getPublicKeyStore(this.requestInfo, key, algo);
			}
			else {
				publicKeyStore = GestoreKeystoreCache.getPublicKeyStore(this.requestInfo, config.getKeyPath(), algo);
			}
		}
		if(publicKeyStore==null) {
			throw new UtilsException(getKeyError(config));
		}
		byokEncryptKey.key = publicKeyStore.getPublicKey();
		if(config.isJoseEngine()) {
			if(config.getKeyId()!=null && StringUtils.isNotEmpty(config.getKeyId())) {
				config.setKeyAlias(config.getKeyId());
			}
			else {
				config.generateKeyAlias();
			}
			JWK jwk = new JWK(publicKeyStore.getPublicKey(), config.getKeyAlias());
			JWKSet jwkSet = new JWKSet();
			jwkSet.addJwk(jwk);
			jwkSet.getJson(); // rebuild
			byokEncryptKey.jsonWebKeys = jwkSet.getJsonWebKeys();
		}
	}
	private void readKeyPair(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config) throws UtilsException, SecurityException {
		
		String algo = readKeyAlgo(config);
		
		KeyPairStore keyPairStore = getKeyPairStore(algo, config);
		if(keyPairStore==null) {
			throw new UtilsException(getKeyError(config));
		}
		byokEncryptKey.key = keyPairStore.getPrivateKey();
		if(config.isJoseEngine()) {
			if(config.getKeyId()!=null && StringUtils.isNotEmpty(config.getKeyId())) {
				config.setKeyAlias(config.getKeyId());
			}
			else {
				config.generateKeyAlias();
			}
			JWK jwk = new JWK(keyPairStore.getPublicKey(), keyPairStore.getPrivateKey(), config.getKeyAlias(), KeyUse.ENCRYPTION);
			JWKSet jwkSet = new JWKSet();
			jwkSet.addJwk(jwk);
			jwkSet.getJson(); // rebuild
			byokEncryptKey.jsonWebKeys = jwkSet.getJsonWebKeys();
		}
	}
	private KeyPairStore getKeyPairStore(String algo, BYOKLocalConfig config) throws SecurityException {
		KeyPairStore keyPairStore = null;
		if(config.getKeyInline()!=null && StringUtils.isNotEmpty(config.getKeyInline())) {
			
			byte [] key = readKeyInline(config);
			
			if(config.getPublicKeyInline()!=null && StringUtils.isNotEmpty(config.getPublicKeyInline())) {
				byte [] publicKey = readPublicKeyInline(config);
				keyPairStore = GestoreKeystoreCache.getKeyPairStore(this.requestInfo, key, publicKey, config.getKeyPassword(), algo);
			}
			else {
				if(config.isPublicKeyBase64Encoding() || config.isPublicKeyHexEncoding()) {
					byte [] publicKey = readEncodedPublicKeyFromPath(config);
					keyPairStore = GestoreKeystoreCache.getKeyPairStore(this.requestInfo, key, publicKey, config.getKeyPassword(), algo);
				}
				else {
					keyPairStore = GestoreKeystoreCache.getKeyPairStore(this.requestInfo, key, config.getPublicKeyPath(), config.getKeyPassword(), algo);
				}
			}
		}
		else {
			keyPairStore = getKeyPairStoreFromPath(algo, config);
		}
		return keyPairStore;
	}
	private KeyPairStore getKeyPairStoreFromPath(String algo, BYOKLocalConfig config) throws SecurityException {
		KeyPairStore keyPairStore = null;
		if(config.isKeyBase64Encoding() || config.isKeyHexEncoding()) {
			keyPairStore = getEncodedKeyPairStoreFromPath(algo, config);
		}
		else {
		
			if(config.getPublicKeyInline()!=null && StringUtils.isNotEmpty(config.getPublicKeyInline())) {
				byte [] publicKey = readPublicKeyInline(config);
				keyPairStore = GestoreKeystoreCache.getKeyPairStore(this.requestInfo, config.getKeyPath(), publicKey, config.getKeyPassword(), algo);
			}
			else {
				if(config.isPublicKeyBase64Encoding() || config.isPublicKeyHexEncoding()) {
					byte [] publicKey = readEncodedPublicKeyFromPath(config);
					keyPairStore = GestoreKeystoreCache.getKeyPairStore(this.requestInfo, config.getKeyPath(), publicKey, config.getKeyPassword(), algo);
				}
				else {
					keyPairStore = GestoreKeystoreCache.getKeyPairStore(this.requestInfo, config.getKeyPath(), config.getPublicKeyPath(), config.getKeyPassword(), algo);
				}
			}
			
		}
		return keyPairStore;
	}
	private KeyPairStore getEncodedKeyPairStoreFromPath(String algo, BYOKLocalConfig config) throws SecurityException {
		byte [] key = readEncodedKeyFromPath(config);
		
		if(config.getPublicKeyInline()!=null && StringUtils.isNotEmpty(config.getPublicKeyInline())) {
			byte [] publicKey = readPublicKeyInline(config);
			return GestoreKeystoreCache.getKeyPairStore(this.requestInfo, key, publicKey, config.getKeyPassword(), algo);
		}
		else {
			if(config.isPublicKeyBase64Encoding() || config.isPublicKeyHexEncoding()) {
				byte [] publicKey = readEncodedPublicKeyFromPath(config);
				return GestoreKeystoreCache.getKeyPairStore(this.requestInfo, key, publicKey, config.getKeyPassword(), algo);
			}
			else {
				return GestoreKeystoreCache.getKeyPairStore(this.requestInfo, key, config.getPublicKeyPath(), config.getKeyPassword(), algo);
			}
		}
	}
	private void readSecretKey(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config) throws UtilsException, SecurityException {
		String algo = config.isJoseEngine() ? SymmetricKeyUtils.ALGO_AES : config.getKeyAlgorithm();
		SecretKeyStore secretKeyStore = null;
		if(config.getKeyInline()!=null && StringUtils.isNotEmpty(config.getKeyInline())) {
			byte [] key = readKeyInline(config);
			secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, key, algo);
		}
		else {
			if(config.isKeyBase64Encoding() || config.isKeyHexEncoding()) {
				byte [] key = readEncodedKeyFromPath(config);
				secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, key, algo);
			}
			else {
				secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, config.getKeyPath(), algo);
			}
		}
		initSecretKey(secretKeyStore, byokEncryptKey, config);
	}
	private void readPasswordKeyDerivation(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, boolean wrap) throws UtilsException, SecurityException {
		byokEncryptKey.pwdKeyDerivationConfig = new SecretPasswordKeyDerivationConfig(config.getPassword(), config.getPasswordType(), config.getPasswordIteration());
		if(!config.isOpenSSLEngine() && wrap ) {
			SecretKeyStore secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, byokEncryptKey.pwdKeyDerivationConfig);
			initSecretKey(secretKeyStore, byokEncryptKey, config);
		}
	}
	private void readJavaPasswordKeyDerivationForUnwrap(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, String wrapValue) throws UtilsException {
		
		String [] tmp = wrapValue.split("\\.");
		if(tmp==null || tmp.length!=2) {
			throw new UtilsException("Wrong format");
		}
		byte[]iv = null;
		byte[]dataEncrypted = null;
		if(config.isBase64Encoding()) {
			iv = Base64Utilities.decode(tmp[0]);
			dataEncrypted = Base64Utilities.decode(tmp[1]);
		}
		else if(config.isHexEncoding()) {
			iv = HexBinaryUtilities.decode(tmp[0]);
			dataEncrypted = HexBinaryUtilities.decode(tmp[1]);
		}
		else {
			throw new UtilsException(ENCODING_MODE_UNDEFINED);
		}
		
		CipherInfo cipherInfo = null;
		if(BYOKCostanti.isOpenSSLPBKDF2PasswordDerivationKeyMode(byokEncryptKey.pwdKeyDerivationConfig.getPasswordEncryptionMode())) {
			cipherInfo = DecryptOpenSSLPassPBKDF2.buildCipherInfo(dataEncrypted, 
					byokEncryptKey.pwdKeyDerivationConfig.getPassword(), 
					byokEncryptKey.pwdKeyDerivationConfig.getPasswordIterator(), 
					OpenSSLEncryptionMode.toMode(byokEncryptKey.pwdKeyDerivationConfig.getPasswordEncryptionMode()));
		}
		else {
			cipherInfo = DecryptOpenSSLPass.buildCipherInfo(dataEncrypted, 
					byokEncryptKey.pwdKeyDerivationConfig.getPassword(), 
					null, 
					OpenSSLEncryptionMode.toMode(byokEncryptKey.pwdKeyDerivationConfig.getPasswordEncryptionMode()));
		}
		try {
			initSecretKey((SecretKey) cipherInfo.getKey(), iv, cipherInfo.getSalt(),
					byokEncryptKey, config);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private void initSecretKey(SecretKeyStore secretKeyStore, BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config) throws UtilsException, SecurityException {
		if(secretKeyStore==null) {
			throw new UtilsException(getKeyError(config));
		}
		initSecretKey(secretKeyStore.getSecretKey(), secretKeyStore.getIv(), secretKeyStore.getSalt(),
				byokEncryptKey, config);
	}
	private void initSecretKey(SecretKey key, byte[]iv, byte[]salt, 
			BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config) throws UtilsException {
		byokEncryptKey.key = key;
		byokEncryptKey.iv = iv;
		byokEncryptKey.salt = salt;
		byokEncryptKey.secret = true;
		if(config.isJoseEngine()) {
			if(config.getKeyId()!=null && StringUtils.isNotEmpty(config.getKeyId())) {
				config.setKeyAlias(config.getKeyId());
			}
			else {
				config.generateKeyAlias();
			}
			JWK jwk = new JWK(key, config.getKeyAlias(), KeyUse.ENCRYPTION);
			JWKSet jwkSet = new JWKSet();
			jwkSet.addJwk(jwk);
			jwkSet.getJson(); // rebuild
			byokEncryptKey.jsonWebKeys = jwkSet.getJsonWebKeys();
		}
	}
	
	private String encJava(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, byte[] value) throws UtilsException {
		
		Encrypt encrypt = null;
		if(byokEncryptKey.secret &&
				byokEncryptKey.iv!=null) {
			encrypt = new Encrypt(byokEncryptKey.key, byokEncryptKey.iv);
		}
		else {
			encrypt = new Encrypt(byokEncryptKey.key);
		}
		
		if(byokEncryptKey.secret &&
				byokEncryptKey.iv==null) {
			encrypt.initIV(config.getContentAlgorithm());
		}
		
		byte [] encrypted = null;
		try {
			/**System.out.println("encrypt ["+config.getContentAlgorithm()+"]...");*/
			encrypted = encrypt.encrypt(value, config.getContentAlgorithm());
			/**System.out.println("encrypt ok");*/
		}catch(Exception e) {
			/**System.out.println("encrypt ERROR: "+e.getMessage());*/
			throw new UtilsException(e.getMessage(),e);
		}
		
		if(byokEncryptKey.secret &&
				byokEncryptKey.salt!=null){
			encrypted = EncryptOpenSSLPass.formatOutput(byokEncryptKey.salt, encrypted);
		}
		
		String en = null;
		if(config.isBase64Encoding()) {
			en = Base64Utilities.encodeAsString(encrypted);
		}
		else if(config.isHexEncoding()) {
			en = HexBinaryUtilities.encodeAsString(encrypted);
		}
		else {
			throw new UtilsException("Java algorithm undefined in keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"'");
		}
		if(byokEncryptKey.secret) {
			if(config.isBase64Encoding()) {
				return encrypt.getIVBase64AsString()+JAVA_SEPARATOR+en;
			}
			else {
				return encrypt.getIVHexBinaryAsString()+JAVA_SEPARATOR+en;
			}
		}
		return en;
	}
	
	private String encJavaKeyWrap(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, byte[] value) throws UtilsException {
		
		EncryptWrapKey encrypt = null;
		if(byokEncryptKey.ks!=null) {
			encrypt = new EncryptWrapKey(byokEncryptKey.ks, config.getKeyAlias());
		}
		else {
			encrypt = new EncryptWrapKey(byokEncryptKey.key);
		}
				
		byte [] encrypted = null;
		try {
			/**System.out.println("encrypt ["+config.getKeyAlgorithm()+"] ["+config.getContentAlgorithm()+"]...");*/
			encrypted = encrypt.encrypt(value, config.getKeyAlgorithm(), config.getContentAlgorithm());
			/**System.out.println("encrypt ok");*/
		}catch(Exception e) {
			/**System.out.println("encrypt ERROR: "+e.getMessage());*/
			throw new UtilsException(e.getMessage(),e);
		}
		
		String en = null;
		if(config.isBase64Encoding()) {
			en = Base64Utilities.encodeAsString(encrypted);
		}
		else if(config.isHexEncoding()) {
			en = HexBinaryUtilities.encodeAsString(encrypted);
		}
		else {
			throw new UtilsException("Java algorithm undefined in keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"'");
		}

		if(config.isBase64Encoding()) {
			return encrypt.getWrappedKeyBase64()+JAVA_SEPARATOR+encrypt.getIVBase64AsString()+JAVA_SEPARATOR+en;
		}
		else {
			return encrypt.getWrappedKeyHexBinary()+JAVA_SEPARATOR+encrypt.getIVHexBinaryAsString()+JAVA_SEPARATOR+en;
		}
		
	}
	
	private String encJose(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, byte[] value) throws UtilsException {
		JsonEncrypt encrypt = null;
		JwtHeaders jwtHeaders = null;
		JWEOptions options = new JWEOptions(JOSESerialization.COMPACT);
		if(byokEncryptKey.ks!=null) {
			jwtHeaders = this.getJwtHeaders(config, byokEncryptKey.ks);
			if(byokEncryptKey.secret) {
				encrypt = new JsonEncrypt(byokEncryptKey.ks, config.getKeyAlias(), config.getKeyPassword(), config.getKeyAlgorithm(), config.getContentAlgorithm(),
						jwtHeaders, options); 
			}
			else {
				encrypt = new JsonEncrypt(byokEncryptKey.ks, config.getKeyAlias(), config.getKeyAlgorithm(), config.getContentAlgorithm(),
						jwtHeaders, options); 
			}
		}
		else {
			jwtHeaders = this.getJwtHeaders(config, byokEncryptKey.jsonWebKeys);
			encrypt = new JsonEncrypt(byokEncryptKey.jsonWebKeys, byokEncryptKey.secret, config.getKeyAlias(), config.getKeyAlgorithm(), config.getContentAlgorithm(),
					jwtHeaders, options); 
		}
		return encrypt.encrypt(value);
	}
	
	private JwtHeaders getJwtHeaders(BYOKLocalConfig config, KeyStore ks) throws UtilsException {
		return getJwtHeaders(config, ks, null);
	}
	private JwtHeaders getJwtHeaders(BYOKLocalConfig config, JsonWebKeys jsonWebKeys) throws UtilsException {
		return getJwtHeaders(config, null, jsonWebKeys);
	}
	private JwtHeaders getJwtHeaders(BYOKLocalConfig config, KeyStore ks, JsonWebKeys jsonWebKeys) throws UtilsException {
		JwtHeaders jwtHeaders = new JwtHeaders();
		if(config.isJoseIncludeKeyId()) {
			jwtHeaders.setKid(config.getKeyAlias());
		}

		if(config.isJoseIncludeCert()) {
			jwtHeaders.setAddX5C(true);
		}
		if(config.isJoseIncludeCertSha1()) {
			jwtHeaders.setX509IncludeCertSha1(true);
		}
		if(config.isJoseIncludeCertSha256()) {
			jwtHeaders.setX509IncludeCertSha256(true);
		}
		if(ks!=null && (config.isJoseIncludeCert() || config.isJoseIncludeCertSha1 ()|| config.isJoseIncludeCertSha256())) {
			Certificate cert = ks.getCertificate(config.getKeyAlias());
			if(cert instanceof X509Certificate) {
				jwtHeaders.addX509cert((X509Certificate)cert);
			}
		}
		
		if(jsonWebKeys!=null && config.isJoseIncludePublicKey()) {
			jwtHeaders.setJwKey(jsonWebKeys, config.getKeyAlias());
		}
		
		return jwtHeaders;
	}
	
	private String encOpenSSL(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, byte[] value) throws UtilsException {
		if(BYOKCostanti.isOpenSSLPBKDF2PasswordDerivationKeyMode(byokEncryptKey.pwdKeyDerivationConfig.getPasswordEncryptionMode())) {
			return encOpenSSLPBKDF2(byokEncryptKey, config, value);
		}
		else {
			return encOpenSSLStandard(byokEncryptKey, config, value);
		}
	}
	private String encOpenSSLStandard(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, byte[] value) throws UtilsException {
		
		SecretPasswordKeyDerivationConfig passwordKeyDerivationConfig = byokEncryptKey.pwdKeyDerivationConfig;
		
		EncryptOpenSSLPass encrypt = new EncryptOpenSSLPass(passwordKeyDerivationConfig.getPassword(), 
				OpenSSLEncryptionMode.toMode(passwordKeyDerivationConfig.getPasswordEncryptionMode()));
		
		if(config.isBase64Encoding()) {
			return encrypt.encryptBase64AsString(value);
		}
		else if(config.isHexEncoding()) {
			return encrypt.encryptHexBinaryAsString(value);
		}
		else {
			throw new UtilsException(ENCODING_MODE_UNDEFINED);
		}
	}
	private String encOpenSSLPBKDF2(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, byte[] value) throws UtilsException {
		
		SecretPasswordKeyDerivationConfig passwordKeyDerivationConfig = byokEncryptKey.pwdKeyDerivationConfig;
		
		EncryptOpenSSLPassPBKDF2 encrypt = new EncryptOpenSSLPassPBKDF2(passwordKeyDerivationConfig.getPassword(), 
				passwordKeyDerivationConfig.getPasswordIterator(), 
				OpenSSLEncryptionMode.toMode(passwordKeyDerivationConfig.getPasswordEncryptionMode()));
		
		if(config.isBase64Encoding()) {
			return encrypt.encryptBase64AsString(value);
		}
		else if(config.isHexEncoding()) {
			return encrypt.encryptHexBinaryAsString(value);
		}
		else {
			throw new UtilsException(ENCODING_MODE_UNDEFINED);
		}
	}
	
	
	
	
	private byte[] decryptJava(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, String value) throws UtilsException {
		
		Decrypt d = null;
		byte[]dataEncrypted = null;
		if(byokEncryptKey.secret) {
			String [] tmp = value.split("\\.");
			if(tmp==null || tmp.length!=2) {
				throw new UtilsException("Atteso formato iv.secret (enc)");
			}
			byte[]iv = null;
			if(config.isBase64Encoding()) {
				iv = Base64Utilities.decode(tmp[0]);
				dataEncrypted = Base64Utilities.decode(tmp[1]);
			}
			else if(config.isHexEncoding()) {
				iv = HexBinaryUtilities.decode(tmp[0]);
				dataEncrypted = HexBinaryUtilities.decode(tmp[1]);
			}
			else {
				iv = tmp[0].getBytes(); 
				dataEncrypted = tmp[1].getBytes();
			}
			if(KeystoreType.PASSWORD_KEY_DERIVATION.equals(config.getKeystoreType())) {
				dataEncrypted = DecryptOpenSSLPass.extractCipherBytes(dataEncrypted);
			}
			d = new Decrypt(byokEncryptKey.key, iv);
		}
		else {
			if(config.isBase64Encoding()) {
				dataEncrypted = Base64Utilities.decode(value);
			}
			else if(config.isHexEncoding()) {
				dataEncrypted = HexBinaryUtilities.decode(value);
			}
			else {
				dataEncrypted = value.getBytes();
			}
			d = new Decrypt(byokEncryptKey.key);
		}
		
		return d.decrypt(dataEncrypted, config.getContentAlgorithm());
	}
	
	private byte[] decryptJavaKeyWrap(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, String value) throws UtilsException {
		String [] tmp = value.split("\\.");
		if(tmp==null || tmp.length!=3) {
			throw new UtilsException("Atteso formato wrappedKey.iv.secret ("+config.getEncoding()+")");
		}
		byte[]wrappedKey = null;
		byte[]iv = null;
		byte[]dataEncrypted = null;
		if(config.isBase64Encoding()) {
			wrappedKey = Base64Utilities.decode(tmp[0]);
			iv = Base64Utilities.decode(tmp[1]);
			dataEncrypted = Base64Utilities.decode(tmp[2]);
		}
		else if(config.isHexEncoding()) {
			wrappedKey = HexBinaryUtilities.decode(tmp[0]);
			iv = HexBinaryUtilities.decode(tmp[1]);
			dataEncrypted = HexBinaryUtilities.decode(tmp[2]);
		}
		else {
			wrappedKey = tmp[0].getBytes();
			iv = tmp[1].getBytes();
			dataEncrypted = tmp[2].getBytes();
		}
		
		DecryptWrapKey d = new DecryptWrapKey(byokEncryptKey.key);
		return d.decrypt(dataEncrypted, wrappedKey, iv, config.getKeyAlgorithm(), config.getContentAlgorithm());
	}
	
	private byte[] decryptJose(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, String value) throws UtilsException {
		JsonDecrypt decrypt = null;
		JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
		if(byokEncryptKey.ks!=null) {
			decrypt = new JsonDecrypt(byokEncryptKey.ks, byokEncryptKey.secret, config.getKeyAlias(), config.getKeyPassword(), config.getKeyAlgorithm(), config.getContentAlgorithm(),
						options); 
		}
		else {
			decrypt = new JsonDecrypt(byokEncryptKey.jsonWebKeys, byokEncryptKey.secret, config.getKeyAlias(), config.getKeyAlgorithm(), config.getContentAlgorithm(),
					options); 
		}
		decrypt.decrypt(value);
		return decrypt.getDecodedPayloadAsByte();
	}
	
	private byte[] decryptOpenSSL(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, String value) throws UtilsException {
		if(BYOKCostanti.isOpenSSLPBKDF2PasswordDerivationKeyMode(byokEncryptKey.pwdKeyDerivationConfig.getPasswordEncryptionMode())) {
			return decryptOpenSSLPBKDF2(byokEncryptKey, config, value);
		}
		else {
			return decryptOpenSSLStandard(byokEncryptKey, config, value);
		}
	}
	private byte[] decryptOpenSSLStandard(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, String value) throws UtilsException {
		
		SecretPasswordKeyDerivationConfig passwordKeyDerivationConfig = byokEncryptKey.pwdKeyDerivationConfig;
		
		DecryptOpenSSLPass decrypt = new DecryptOpenSSLPass(passwordKeyDerivationConfig.getPassword(), 
				OpenSSLEncryptionMode.toMode(passwordKeyDerivationConfig.getPasswordEncryptionMode()));
		
		if(config.isBase64Encoding()) {
			return decrypt.decryptBase64(value);
		}
		else if(config.isHexEncoding()) {
			return decrypt.decryptHexBinary(value);
		}
		else {
			throw new UtilsException(ENCODING_MODE_UNDEFINED);
		}
	}
	private byte[] decryptOpenSSLPBKDF2(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, String value) throws UtilsException {
		
		SecretPasswordKeyDerivationConfig passwordKeyDerivationConfig = byokEncryptKey.pwdKeyDerivationConfig;
		
		DecryptOpenSSLPassPBKDF2 decrypt = new DecryptOpenSSLPassPBKDF2(passwordKeyDerivationConfig.getPassword(), 
				passwordKeyDerivationConfig.getPasswordIterator(), 
				OpenSSLEncryptionMode.toMode(passwordKeyDerivationConfig.getPasswordEncryptionMode()));
		
		if(config.isBase64Encoding()) {
			return decrypt.decryptBase64(value);
		}
		else if(config.isHexEncoding()) {
			return decrypt.decryptHexBinary(value);
		}
		else {
			throw new UtilsException(ENCODING_MODE_UNDEFINED);
		}
	}
}

class BYOKEncryptKey{
	Key key = null;
	byte[]iv = null;
	byte[]salt = null;
	boolean secret = false;
	KeyStore ks = null;
	JsonWebKeys jsonWebKeys = null;
	SecretPasswordKeyDerivationConfig pwdKeyDerivationConfig = null;
}
