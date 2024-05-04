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

package org.openspcoop2.security.keystore;

import java.security.Key;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

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
import org.openspcoop2.utils.certificate.byok.BYOKLocalConfig;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.security.Decrypt;
import org.openspcoop2.utils.security.DecryptWrapKey;
import org.openspcoop2.utils.security.Encrypt;
import org.openspcoop2.utils.security.EncryptWrapKey;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWEOptions;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonDecrypt;
import org.openspcoop2.utils.security.JsonEncrypt;
import org.openspcoop2.utils.security.JsonUtils;
import org.openspcoop2.utils.security.JwtHeaders;

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
		return "Accesso al keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' non riuscito";
	}
	private String getKeyError(BYOKLocalConfig config) {
		return "Accesso alla chiave ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' non riuscito";
	}
	
	public String wrap(BYOKLocalConfig config, 
			String value) throws UtilsException {
		return wrap(config, 
				value.getBytes());
	}
	public String wrap(BYOKLocalConfig config, 
			byte[] value) throws UtilsException {
		
		BYOKEncryptKey byokEncryptKey = new BYOKEncryptKey();

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
			default:
				throw new UtilsException("Keystore ["+config.getKeystoreType().getNome()+"] non supportato");
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		/**System.out.println("CONF ["+config.getName()+"] java["+config.isJavaEngine()+"] jose["+config.isJoseEngine()+"]");*/
		if(config.isJavaEngine()) {
			if(config.isKeyWrap()) {
				return encJavaKeyWrap(byokEncryptKey, config, value);
			}
			else {
				return encJava(byokEncryptKey, config, value);
			}
		} 
		else if(config.isJoseEngine()) {
			return encJose(byokEncryptKey, config, value);
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
			default:
				throw new UtilsException("Keystore ["+config.getKeystoreType().getNome()+"] non supportato");
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		/**System.out.println("CONF ["+config.getName()+"] java["+config.isJavaEngine()+"] jose["+config.isJoseEngine()+"]");*/
		if(config.isJavaEngine()) {
			if(config.isKeyWrap()) {
				return decryptJavaKeyWrap(byokEncryptKey, config, value);
			}
			else {
				return decryptJava(byokEncryptKey, config, value);
			}
		} 
		else if(config.isJoseEngine()) {
			return decryptJose(byokEncryptKey, config, value);
		} 
		else {
			throw new UtilsException("Encrypt mode undefined");
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
			throw new UtilsException("Accesso al keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' non riuscito per l'alias '"+config.getKeyAlias()+"'");
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
	
	private void readPublicKey(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config) throws UtilsException, SecurityException {
		
		String algo = readKeyAlgo(config);
		
		PublicKeyStore publicKeyStore = null;
		if(config.getKeyInline()!=null && StringUtils.isNotEmpty(config.getKeyInline())) {
			publicKeyStore = GestoreKeystoreCache.getPublicKeyStore(this.requestInfo, config.getKeyInline().getBytes(), algo);
		}
		else {
			publicKeyStore = GestoreKeystoreCache.getPublicKeyStore(this.requestInfo, config.getKeyPath(), algo);
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
			if(config.getPublicKeyInline()!=null && StringUtils.isNotEmpty(config.getPublicKeyInline())) {
				keyPairStore = GestoreKeystoreCache.getKeyPairStore(this.requestInfo, config.getKeyInline().getBytes(), config.getPublicKeyInline().getBytes(), config.getKeyPassword(), algo);
			}
			else {
				keyPairStore = GestoreKeystoreCache.getKeyPairStore(this.requestInfo, config.getKeyInline().getBytes(), config.getPublicKeyPath(), config.getKeyPassword(), algo);
			}
		}
		else {
			if(config.getPublicKeyInline()!=null && StringUtils.isNotEmpty(config.getPublicKeyInline())) {
				keyPairStore = GestoreKeystoreCache.getKeyPairStore(this.requestInfo, config.getKeyPath(), config.getPublicKeyInline().getBytes(), config.getKeyPassword(), algo);
			}
			else {
				keyPairStore = GestoreKeystoreCache.getKeyPairStore(this.requestInfo, config.getKeyPath(), config.getPublicKeyPath(), config.getKeyPassword(), algo);
			}
		}
		return keyPairStore;
	}
	private void readSecretKey(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config) throws UtilsException, SecurityException {
		String algo = config.isJoseEngine() ? SymmetricKeyUtils.ALGO_AES : config.getKeyAlgorithm();
		SecretKeyStore secretKeyStore = null;
		if(config.getKeyInline()!=null && StringUtils.isNotEmpty(config.getKeyInline())) {
			secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, config.getKeyInline().getBytes(), algo);
		}
		else {
			secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, config.getKeyPath(), algo);
		}
		if(secretKeyStore==null) {
			throw new UtilsException(getKeyError(config));
		}
		byokEncryptKey.key = secretKeyStore.getSecretKey();
		byokEncryptKey.secret = true;
		if(config.isJoseEngine()) {
			if(config.getKeyId()!=null && StringUtils.isNotEmpty(config.getKeyId())) {
				config.setKeyAlias(config.getKeyId());
			}
			else {
				config.generateKeyAlias();
			}
			JWK jwk = new JWK(secretKeyStore.getSecretKey(), config.getKeyAlias(), KeyUse.ENCRYPTION);
			JWKSet jwkSet = new JWKSet();
			jwkSet.addJwk(jwk);
			jwkSet.getJson(); // rebuild
			byokEncryptKey.jsonWebKeys = jwkSet.getJsonWebKeys();
		}
	}
	
	private String encJava(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, byte[] value) throws UtilsException {
		
		Encrypt encrypt = new Encrypt(byokEncryptKey.key);
		
		if(byokEncryptKey.secret) {
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
		
		String en = null;
		if(config.isJavaBase64Encoding()) {
			en = Base64Utilities.encodeAsString(encrypted);
		}
		else if(config.isJavaHexEncoding()) {
			en = HexBinaryUtilities.encodeAsString(encrypted);
		}
		else {
			throw new UtilsException("Java algorithm undefined in keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"'");
		}
		if(byokEncryptKey.secret) {
			if(config.isJavaBase64Encoding()) {
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
		if(config.isJavaBase64Encoding()) {
			en = Base64Utilities.encodeAsString(encrypted);
		}
		else if(config.isJavaHexEncoding()) {
			en = HexBinaryUtilities.encodeAsString(encrypted);
		}
		else {
			throw new UtilsException("Java algorithm undefined in keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"'");
		}

		if(config.isJavaBase64Encoding()) {
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
	
	private byte[] decryptJava(BYOKEncryptKey byokEncryptKey, BYOKLocalConfig config, String value) throws UtilsException {
		
		Decrypt d = null;
		byte[]dataEncrypted = null;
		if(byokEncryptKey.secret) {
			String [] tmp = value.split("\\.");
			if(tmp==null || tmp.length!=2) {
				throw new UtilsException("Atteso formato iv.secret (enc)");
			}
			byte[]iv = null;
			if(config.isJavaBase64Encoding()) {
				iv = Base64Utilities.decode(tmp[0]);
				dataEncrypted = Base64Utilities.decode(tmp[1]);
			}
			else if(config.isJavaHexEncoding()) {
				iv = HexBinaryUtilities.decode(tmp[0]);
				dataEncrypted = HexBinaryUtilities.decode(tmp[1]);
			}
			else {
				iv = tmp[0].getBytes(); 
				dataEncrypted = tmp[1].getBytes();
			}
			d = new Decrypt(byokEncryptKey.key, iv);
		}
		else {
			if(config.isJavaBase64Encoding()) {
				dataEncrypted = Base64Utilities.decode(value);
			}
			else if(config.isJavaHexEncoding()) {
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
			throw new UtilsException("Atteso formato wrappedKey.iv.secret ("+config.getJavaEncoding()+")");
		}
		byte[]wrappedKey = null;
		byte[]iv = null;
		byte[]dataEncrypted = null;
		if(config.isJavaBase64Encoding()) {
			wrappedKey = Base64Utilities.decode(tmp[0]);
			iv = Base64Utilities.decode(tmp[1]);
			dataEncrypted = Base64Utilities.decode(tmp[2]);
		}
		else if(config.isJavaHexEncoding()) {
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
}

class BYOKEncryptKey{
	Key key = null;
	boolean secret = false;
	KeyStore ks = null;
	JsonWebKeys jsonWebKeys = null;
}
