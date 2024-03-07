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

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.JWKSetStore;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.PublicKeyStore;
import org.openspcoop2.security.keystore.SecretKeyStore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.security.Encrypt;
import org.openspcoop2.utils.security.EncryptWrapKey;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWEOptions;
import org.openspcoop2.utils.security.JsonEncrypt;
import org.openspcoop2.utils.security.JsonUtils;
import org.openspcoop2.utils.security.JwtHeaders;

import com.nimbusds.jose.jwk.KeyUse;

/**     
 * FileTraceEncrypt
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileTraceEncrypt {

	private RequestInfo requestInfo;
	
	public FileTraceEncrypt(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
	
	private static final String JAVA_SEPARATOR = ".";
	
	private String getKeystoreError(FileTraceEncryptConfig config) {
		return "Accesso al keystore ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' non riuscito";
	}
	private String getKeyError(FileTraceEncryptConfig config) {
		return "Accesso alla chiave ["+config.getKeystoreType().getNome()+"] '"+config.getKeystorePath()+"' non riuscito";
	}
	
	public String encrypt(FileTraceEncryptConfig config, 
			String value) throws UtilsException {
	
		FileTraceEncryptKey fileTraceEncryptKey = new FileTraceEncryptKey();

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
			default:
				throw new UtilsException("Keystore ["+config.getKeystoreType().getNome()+"] non supportato");
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		/**System.out.println("CONF ["+config.getName()+"] java["+config.isJavaEngine()+"] jose["+config.isJoseEngine()+"]");*/
		if(config.isJavaEngine()) {
			if(config.isKeyWrap()) {
				return encJavaKeyWrap(fileTraceEncryptKey, config, value);
			}
			else {
				return encJava(fileTraceEncryptKey, config, value);
			}
		} 
		else if(config.isJoseEngine()) {
			return encJose(fileTraceEncryptKey, config, value);
		} 
		else {
			throw new UtilsException("Encrypt mode undefined");
		}
		
	}
	
	private void readKeystore(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config) throws UtilsException, SecurityException {
		String type = KeystoreType.PKCS11.equals(config.getKeystoreType()) ? config.getKeystoreHsmType() : config.getKeystoreType().getNome();
		MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(this.requestInfo,
				config.getKeystorePath(), type, 
				config.getKeystorePassword());
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
		JWKSetStore jwtStore = GestoreKeystoreCache.getJwkSetStore(this.requestInfo, config.getKeystorePath());
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
		if(config.getKeyAlgorithm().contains("RSA")) {
			fileTraceEncryptKey.key = JwkUtils.toRSAPublicKey(jwk);
		}
		else if(config.getKeyAlgorithm().contains("EC")) {
			fileTraceEncryptKey.key = JwkUtils.toECPublicKey(jwk);
		}
		else {
			fileTraceEncryptKey.key = JwkUtils.toSecretKey(jwk);
			fileTraceEncryptKey.secret = true;
		}
		/**}*/
	}
	private void readPublicKey(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config) throws UtilsException, SecurityException {
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
		PublicKeyStore publicKeyStore = GestoreKeystoreCache.getPublicKeyStore(this.requestInfo, config.getKeyPath(), algo);
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
		SecretKeyStore secretKeyStore = GestoreKeystoreCache.getSecretKeyStore(this.requestInfo, config.getKeyPath(), algo);
		if(secretKeyStore==null) {
			throw new UtilsException(getKeyError(config));
		}
		fileTraceEncryptKey.key = secretKeyStore.getSecretKey();
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
		
		Encrypt encrypt = new Encrypt(fileTraceEncryptKey.key);
		
		if(fileTraceEncryptKey.secret) {
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
		if(fileTraceEncryptKey.secret) {
			if(config.isJavaBase64Encoding()) {
				return encrypt.getIVBase64AsString()+JAVA_SEPARATOR+en;
			}
			else {
				return encrypt.getIVHexBinaryAsString()+JAVA_SEPARATOR+en;
			}
		}
		return en;
	}
	
	private String encJavaKeyWrap(FileTraceEncryptKey fileTraceEncryptKey, FileTraceEncryptConfig config, String value) throws UtilsException {
		
		EncryptWrapKey encrypt = new EncryptWrapKey(fileTraceEncryptKey.key);
				
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
}

class FileTraceEncryptKey{
	Key key = null;
	boolean secret = false;
	KeyStore ks = null;
	JsonWebKeys jsonWebKeys = null;
}
