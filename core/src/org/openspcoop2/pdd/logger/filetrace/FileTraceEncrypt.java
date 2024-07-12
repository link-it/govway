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

package org.openspcoop2.pdd.logger.filetrace;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicMapBuilderUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.JWKSetStore;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.PublicKeyStore;
import org.openspcoop2.security.keystore.SecretKeyStore;
import org.openspcoop2.security.keystore.SecretPasswordKeyDerivationConfig;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.certificate.byok.BYOKConfig;
import org.openspcoop2.utils.certificate.byok.BYOKCostanti;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.security.Encrypt;
import org.openspcoop2.utils.security.EncryptOpenSSLPass;
import org.openspcoop2.utils.security.EncryptOpenSSLPassPBKDF2;
import org.openspcoop2.utils.security.EncryptWrapKey;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWEOptions;
import org.openspcoop2.utils.security.JsonEncrypt;
import org.openspcoop2.utils.security.JsonUtils;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.security.OpenSSLEncryptionMode;
import org.slf4j.Logger;

import com.nimbusds.jose.jwk.KeyUse;

/**     
 * FileTraceEncrypt
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileTraceEncrypt {

	private Logger log;
	private RequestInfo requestInfo;
	private Context context;
	private Busta busta;
	
	public FileTraceEncrypt(Logger log, RequestInfo requestInfo, Context context, Busta busta) {
		this.log = log;
		this.requestInfo = requestInfo;
		this.context = context;
		this.busta = busta;
	}
	
	private static final String JAVA_SEPARATOR = ".";
	
	private String getKeystoreError(FileTraceEncryptConfig config) {
		return "Access to keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' failed";
	}
	private String getKeyError(FileTraceEncryptConfig config) {
		return "Access to key ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' failed";
	}

	private static final String KEYSTORE_PREFIX = "Keystore [";
	
	public String encrypt(FileTraceEncryptConfig config, 
			String value) throws UtilsException {
	
		FileTraceEncryptKey fileTraceEncryptKey = new FileTraceEncryptKey();

		boolean initPasswordKeyDerivation = false;
		try {
			switch (config.getKeystoreType()) {
			case JKS:
			case PKCS12:
			case PKCS11:
			case JCEKS:
				readKeystore(fileTraceEncryptKey, config);
				break;
			case JWK_SET:
				readJwk(fileTraceEncryptKey, config);
				break;
			case PUBLIC_KEY:
				readPublicKey(fileTraceEncryptKey, config);
				break;
			case SYMMETRIC_KEY:
				readSecretKey(fileTraceEncryptKey, config);
				break;
			case PASSWORD_KEY_DERIVATION:
				readPasswordKeyDerivation(fileTraceEncryptKey, config);
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
				return encJavaKeyWrap(fileTraceEncryptKey, config, value);
			}
			else {
				return encJava(fileTraceEncryptKey, config, value);
			}
		} 
		else if(config.isJoseEngine()) {
			if(initPasswordKeyDerivation) {
				throw new UtilsException(KEYSTORE_PREFIX+config.getKeystoreType().getNome()+"] unusable with jose mode");
			}
			return encJose(fileTraceEncryptKey, config, value);
		} 
		else if(config.isOpenSSLEngine()) {
			return encOpenSSL(fileTraceEncryptKey, config, value);
		}
		else {
			throw new UtilsException("Encrypt mode undefined");
		}
		
	}
	
	private BYOKRequestParams getBYOKRequestParams(FileTraceEncryptConfig config) throws UtilsException {
		BYOKRequestParams req = null;
		if(config!=null && config.getKsmId()!=null) {
			BYOKManager manager = BYOKManager.getInstance();
			if(manager==null) {
				throw new UtilsException("BYOKManager not initialized");
			}
			BYOKConfig bconfig = manager.getKSMConfigByType(config.getKsmId());
			if(bconfig==null) {
				throw new UtilsException("BYOK configuration for ksm id '"+config.getKsmId()+"' not found");
			}
			req = new BYOKRequestParams();
			req.setConfig(bconfig);
			
			Map<String, String> inputMap = new HashMap<>();
			if(config.getKsmInput()!=null && !config.getKsmInput().isEmpty()) {
				inputMap.putAll(config.getKsmInput());
			}
			req.setInputMap(inputMap);
			
			Map<String,Object> dynamicMap = DynamicMapBuilderUtils.buildDynamicMap(this.busta, this.requestInfo, this.context, this.log);
			req.setDynamicMap(dynamicMap);
		}
		return req;
	}
	
	private void readKeystore(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config) throws UtilsException, SecurityException {
		String type = KeystoreType.PKCS11.equals(config.getKeystoreType()) ? config.getKeystoreHsmType() : config.getKeystoreType().getNome();
		MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(this.requestInfo,
				config.getKeystorePath(), type, 
				config.getKeystorePassword(),
				getBYOKRequestParams(config));
		if(merlinKs==null || merlinKs.getKeyStore()==null) {
			throw new UtilsException(getKeystoreError(config));
		}
		fileTraceEncryptKey.ks = merlinKs.getKeyStore();
		/**if(config.isJavaEngine()) {*/
		// bisogna sapere se e' secret o meno
		if(KeystoreType.JCEKS.equals(config.getKeystoreType())) {
			fileTraceEncryptKey.key = fileTraceEncryptKey.ks.getSecretKey(config.getKeyAlias(), config.getKeyPassword());
			fileTraceEncryptKey.secret = true;
		}
		else {
			fileTraceEncryptKey.key = merlinKs.getKeyStore().getPublicKey(config.getKeyAlias());
		}
		/**}*/
	}
	private void readJwk(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config) throws UtilsException, SecurityException {
		JWKSetStore jwtStore = GestoreKeystoreCache.getJwkSetStore(this.requestInfo, config.getKeystorePath(),
				getBYOKRequestParams(config));
		if(jwtStore==null || jwtStore.getJwkSet()==null) {
			throw new UtilsException(getKeystoreError(config));
		}
		fileTraceEncryptKey.jsonWebKeys = jwtStore.getJwkSet().getJsonWebKeys();
		/**if(config.isJavaEngine()) {*/
		// bisogna sapere se e' secret o meno
		JsonWebKey jwk = JsonUtils.readKey(fileTraceEncryptKey.jsonWebKeys, config.getKeyAlias());
		if(jwk==null) {
			throw new UtilsException("Accesso al keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' non riuscito per l'alias '"+config.getKeyAlias()+"'");
		}
		if(jwk.getAlgorithm()==null) {
			jwk.setAlgorithm(config.isJavaEngine() ? "A256GCM" : config.getContentAlgorithm());
		}
		if(config.getKeyAlgorithm().contains(KeyUtils.ALGO_RSA)) {
			fileTraceEncryptKey.key = JwkUtils.toRSAPublicKey(jwk);
		}
		else if(config.getKeyAlgorithm().contains(KeyUtils.ALGO_EC)) {
			fileTraceEncryptKey.key = JwkUtils.toECPublicKey(jwk);
		}
		else {
			fileTraceEncryptKey.key = JwkUtils.toSecretKey(jwk);
			fileTraceEncryptKey.secret = true;
		}
		/**}*/
	}
	
	private byte[] readKeyInline(FileTraceEncryptConfig config) throws SecurityException {
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
	private byte[] readEncodedKeyFromPath(FileTraceEncryptConfig config) throws SecurityException {
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
	
	private String readPublicKeyAlgo(FileTraceEncryptConfig config) {
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
	private void readPublicKey(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config) throws UtilsException, SecurityException {
		String algo = readPublicKeyAlgo(config);
		
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
		fileTraceEncryptKey.key = publicKeyStore.getPublicKey();
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
			fileTraceEncryptKey.jsonWebKeys = jwkSet.getJsonWebKeys();
		}
	}
	private void readSecretKey(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config) throws UtilsException, SecurityException {
		String algo = config.isJoseEngine() ? SymmetricKeyUtils.ALGO_AES : config.getKeyAlgorithm();
		SecretKeyStore secretKeyStore = null;
		if(config.getKeyInline()!=null && StringUtils.isNotEmpty(config.getKeyInline())) {
			byte [] key = readKeyInline(config);
			secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, key, algo,
					getBYOKRequestParams(config));
		}
		else {
			if(config.isKeyBase64Encoding() || config.isKeyHexEncoding()) {
				byte [] key = readEncodedKeyFromPath(config);
				secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, key, algo,
						getBYOKRequestParams(config));
			}
			else {
				secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, config.getKeyPath(), algo,
						getBYOKRequestParams(config));
			}
		}
		initSecretKey(secretKeyStore, fileTraceEncryptKey, config);
	}
	private void readPasswordKeyDerivation(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config) throws UtilsException, SecurityException {
		fileTraceEncryptKey.pwdKeyDerivationConfig = new SecretPasswordKeyDerivationConfig(config.getPassword(), config.getPasswordType(), config.getPasswordIteration());
		if(!config.isOpenSSLEngine() ) {
			SecretKeyStore secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, fileTraceEncryptKey.pwdKeyDerivationConfig,
					getBYOKRequestParams(config));
			initSecretKey(secretKeyStore, fileTraceEncryptKey, config);
		}
	}
	private void initSecretKey(SecretKeyStore secretKeyStore, FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config) throws UtilsException, SecurityException {
		if(secretKeyStore==null) {
			throw new UtilsException(getKeyError(config));
		}
		fileTraceEncryptKey.key = secretKeyStore.getSecretKey();
		fileTraceEncryptKey.iv = secretKeyStore.getIv();
		fileTraceEncryptKey.salt = secretKeyStore.getSalt();
		fileTraceEncryptKey.secret = true;
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
			fileTraceEncryptKey.jsonWebKeys = jwkSet.getJsonWebKeys();
		}
	}
	
	private String encJava(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config, String value) throws UtilsException {
		
		Encrypt encrypt = null;
		if(fileTraceEncryptKey.secret &&
				fileTraceEncryptKey.iv!=null) {
			encrypt = new Encrypt(fileTraceEncryptKey.key, fileTraceEncryptKey.iv);
		}
		else {
			encrypt = new Encrypt(fileTraceEncryptKey.key);
		}
		
		if(fileTraceEncryptKey.secret &&
				fileTraceEncryptKey.iv==null) {
			encrypt.initIV(config.getContentAlgorithm());
		}
		
		byte [] encrypted = null;
		try {
			/**System.out.println("encrypt ["+config.getContentAlgorithm()+"]...");*/
			encrypted = encrypt.encrypt(value.getBytes(), config.getContentAlgorithm());
			/**System.out.println("encrypt ok");*/
		}catch(Exception e) {
			/**System.out.println("encrypt ERROR: "+e.getMessage());*/
			throw new UtilsException(e.getMessage(),e);
		}
		
		if(fileTraceEncryptKey.secret &&
				fileTraceEncryptKey.salt!=null){
			encrypted = EncryptOpenSSLPass.formatOutput(fileTraceEncryptKey.salt, encrypted);
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
		if(fileTraceEncryptKey.secret) {
			if(config.isBase64Encoding()) {
				return encrypt.getIVBase64AsString()+JAVA_SEPARATOR+en;
			}
			else {
				return encrypt.getIVHexBinaryAsString()+JAVA_SEPARATOR+en;
			}
		}
		return en;
	}
	
	private String encJavaKeyWrap(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config, String value) throws UtilsException {
		
		EncryptWrapKey encrypt = null;
		if(fileTraceEncryptKey.ks!=null) {
			encrypt = new EncryptWrapKey(fileTraceEncryptKey.ks, config.getKeyAlias());
		}
		else {
			encrypt = new EncryptWrapKey(fileTraceEncryptKey.key);
		}
				
		byte [] encrypted = null;
		try {
			/**System.out.println("encrypt ["+config.getKeyAlgorithm()+"] ["+config.getContentAlgorithm()+"]...");*/
			encrypted = encrypt.encrypt(value.getBytes(), config.getKeyAlgorithm(), config.getContentAlgorithm());
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
	
	private String encJose(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config, String value) throws UtilsException {
		JsonEncrypt encrypt = null;
		JwtHeaders jwtHeaders = null;
		JWEOptions options = new JWEOptions(JOSESerialization.COMPACT);
		if(fileTraceEncryptKey.ks!=null) {
			jwtHeaders = config.getJwtHeaders(fileTraceEncryptKey.ks);
			if(fileTraceEncryptKey.secret) {
				encrypt = new JsonEncrypt(fileTraceEncryptKey.ks, config.getKeyAlias(), config.getKeyPassword(), config.getKeyAlgorithm(), config.getContentAlgorithm(),
						jwtHeaders, options); 
			}
			else {
				encrypt = new JsonEncrypt(fileTraceEncryptKey.ks, config.getKeyAlias(), config.getKeyAlgorithm(), config.getContentAlgorithm(),
						jwtHeaders, options); 
			}
		}
		else {
			jwtHeaders = config.getJwtHeaders(fileTraceEncryptKey.jsonWebKeys);
			encrypt = new JsonEncrypt(fileTraceEncryptKey.jsonWebKeys, fileTraceEncryptKey.secret, config.getKeyAlias(), config.getKeyAlgorithm(), config.getContentAlgorithm(),
					jwtHeaders, options); 
		}
		return encrypt.encrypt(value);
	}
	
	private String encOpenSSL(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config, String value) throws UtilsException {
		if(BYOKCostanti.isOpenSSLPBKDF2PasswordDerivationKeyMode(fileTraceEncryptKey.pwdKeyDerivationConfig.getPasswordEncryptionMode())) {
			return encOpenSSLPBKDF2(fileTraceEncryptKey, config, value);
		}
		else {
			return encOpenSSLStandard(fileTraceEncryptKey, config, value);
		}
	}
	private String encOpenSSLStandard(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config, String value) throws UtilsException {
		
		SecretPasswordKeyDerivationConfig passwordKeyDerivationConfig = fileTraceEncryptKey.pwdKeyDerivationConfig;
		
		EncryptOpenSSLPass encrypt = new EncryptOpenSSLPass(passwordKeyDerivationConfig.getPassword(), 
				OpenSSLEncryptionMode.toMode(passwordKeyDerivationConfig.getPasswordEncryptionMode()));
		
		if(config.isBase64Encoding()) {
			return encrypt.encryptBase64AsString(value.getBytes());
		}
		else if(config.isHexEncoding()) {
			return encrypt.encryptHexBinaryAsString(value.getBytes());
		}
		else {
			throw new UtilsException("Encoding mode undefined");
		}
	}
	private String encOpenSSLPBKDF2(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config, String value) throws UtilsException {
		
		SecretPasswordKeyDerivationConfig passwordKeyDerivationConfig = fileTraceEncryptKey.pwdKeyDerivationConfig;
		
		EncryptOpenSSLPassPBKDF2 encrypt = new EncryptOpenSSLPassPBKDF2(passwordKeyDerivationConfig.getPassword(), 
				passwordKeyDerivationConfig.getPasswordIterator(), 
				OpenSSLEncryptionMode.toMode(passwordKeyDerivationConfig.getPasswordEncryptionMode()));
		
		if(config.isBase64Encoding()) {
			return encrypt.encryptBase64AsString(value.getBytes());
		}
		else if(config.isHexEncoding()) {
			return encrypt.encryptHexBinaryAsString(value.getBytes());
		}
		else {
			throw new UtilsException("Encoding mode undefined");
		}
	}
}

class FileTraceEncryptKey{
	Key key = null;
	byte[]iv = null;
	byte[]salt = null;
	boolean secret = false;
	KeyStore ks = null;
	JsonWebKeys jsonWebKeys = null;
	SecretPasswordKeyDerivationConfig pwdKeyDerivationConfig = null;
}
