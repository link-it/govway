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


package org.openspcoop2.utils.security;

import java.util.Iterator;
import java.util.Properties;

import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm;
import org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm;
import org.apache.cxf.rs.security.jose.jwe.JweEncryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweHeaders;
import org.apache.cxf.rs.security.jose.jwe.JweJsonProducer;
import org.apache.cxf.rs.security.jose.jwe.JweUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonEncrypt {

	private JweEncryptionProvider provider;
	
	private ContentAlgorithm contentAlgorithm;
	private KeyAlgorithm keyAlgorithm;
	
	private JWEOptions options;
	
	private JweHeaders headers;
	private JwtHeaders jwtHeaders;
	
	public JsonEncrypt(Properties props, JWEOptions options) throws UtilsException{
		this(props, null, options);
	}
	public JsonEncrypt(Properties props, JwtHeaders jwtHeaders, JWEOptions options) throws UtilsException{
		try {
			this.headers = new JweHeaders();
			
			this.options=options;
			String tmp = props.getProperty(JoseConstants.RSSEC_ENCRYPTION_ZIP_ALGORITHM);
			if(tmp!=null && JoseConstants.JWE_DEFLATE_ZIP_ALGORITHM.equalsIgnoreCase(tmp.trim())) {
				this.options.setDeflate(true); // overwrite options
			}
			
			this.provider = JsonUtils.getJweEncryptionProvider(props);
			if(this.provider==null) {
				
				KeyAlgorithm keyAlgorithmP = JweUtils.getKeyEncryptionAlgorithm(props, null);
				if (KeyAlgorithm.DIRECT.equals(keyAlgorithmP)) {
					this.provider = JsonUtils.getJweEncryptionProviderFromJWKSymmetric(props, this.headers);
				}
				else {
					this.provider = JweUtils.loadEncryptionProvider(props, JsonUtils.newMessage(), this.headers);
				}
			}
			
			this.contentAlgorithm = JweUtils.getContentEncryptionAlgorithm(props, ContentAlgorithm.A256GCM);
			this.keyAlgorithm = JweUtils.getKeyEncryptionAlgorithm(props, null);
/**			if(this.keyAlgorithm==null) {
//				throw new Exception("KeyAlgorithm undefined");
//			}*/
						
			this.jwtHeaders = jwtHeaders;
			
		}catch(Exception t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.ENCRYPT,JsonUtils.SENDER,t);
		}
	}
	
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm, 
			JWEOptions options) throws UtilsException{
		initTrustStore(new KeyStore(keystore), alias, keyAlgorithm, contentAlgorithm, null, options);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm, 
			JWEOptions options) throws UtilsException{
		initTrustStore(keystore, alias, keyAlgorithm, contentAlgorithm, null, options);	
	}
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm,
			JwtHeaders jwtHeaders, JWEOptions options) throws UtilsException{
		initTrustStore(new KeyStore(keystore), alias, keyAlgorithm, contentAlgorithm, jwtHeaders, options);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm,
			JwtHeaders jwtHeaders, JWEOptions options) throws UtilsException{
		initTrustStore(keystore, alias, keyAlgorithm, contentAlgorithm, jwtHeaders, options);
	}
	private void initTrustStore(KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm,
			JwtHeaders jwtHeaders, JWEOptions options) throws UtilsException{
		try {
			this.options=options;
			
			this.keyAlgorithm  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			this.contentAlgorithm = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			String compression = null;
			if(this.options.isDeflate()) {
				compression = JoseConstants.JWE_DEFLATE_ZIP_ALGORITHM;
			}
			
			this.provider = JweUtils.createJweEncryptionProvider( keystore.getPublicKey(alias), this.keyAlgorithm, this.contentAlgorithm, compression);
			
			this.jwtHeaders = jwtHeaders;
		}catch(Exception t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.ENCRYPT,JsonUtils.SENDER,t);
		}
	}
	
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, 
			JWEOptions options) throws UtilsException{
		initKeystore(new KeyStore(keystore), alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, null, options);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, 
			JWEOptions options) throws UtilsException{
		initKeystore(keystore, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, null, options);	
	}
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm,
			JwtHeaders jwtHeaders, JWEOptions options) throws UtilsException{
		initKeystore(new KeyStore(keystore), alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, jwtHeaders, options);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm,
			JwtHeaders jwtHeaders, JWEOptions options) throws UtilsException{
		initKeystore(keystore, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, jwtHeaders, options);
	}
	private void initKeystore(KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm,
			JwtHeaders jwtHeaders, JWEOptions options) throws UtilsException{
		try {
			this.options=options;
			
			this.keyAlgorithm  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			this.contentAlgorithm = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			String compression = null;
			if(this.options.isDeflate()) {
				compression = JoseConstants.JWE_DEFLATE_ZIP_ALGORITHM;
			}
			
			if (KeyAlgorithm.DIRECT.equals(this.keyAlgorithm)) {
				this.provider = JweUtils.getDirectKeyJweEncryption(keystore.getSecretKey(alias, passwordPrivateKey), this.contentAlgorithm);
			}
			else {
				this.provider = JweUtils.createJweEncryptionProvider(keystore.getSecretKey(alias, passwordPrivateKey), this.keyAlgorithm, this.contentAlgorithm, compression);
			}
			
			this.jwtHeaders = jwtHeaders;
		}catch(Exception t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.ENCRYPT,JsonUtils.SENDER,t);
		}
	}

	public JsonEncrypt(JsonWebKeys jsonWebKeys, boolean secretKey, String alias, String keyAlgorithm, String contentAlgorithm, 
			JWEOptions options) throws UtilsException{
		initJsonWebKey(JsonUtils.readKey(jsonWebKeys, alias),secretKey, keyAlgorithm, contentAlgorithm, null, options);	
	}
	public JsonEncrypt(JsonWebKeys jsonWebKeys, boolean secretKey, String alias, String keyAlgorithm, String contentAlgorithm, 
			JwtHeaders jwtHeaders, JWEOptions options) throws UtilsException{
		initJsonWebKey(JsonUtils.readKey(jsonWebKeys, alias),secretKey, keyAlgorithm, contentAlgorithm, jwtHeaders, options);	
	}
	public JsonEncrypt(JsonWebKey jsonWebKey, boolean secretKey, String keyAlgorithm, String contentAlgorithm, 
			JWEOptions options) throws UtilsException{
		initJsonWebKey(jsonWebKey,secretKey, keyAlgorithm, contentAlgorithm, null, options);	
	}
	public JsonEncrypt(JsonWebKey jsonWebKey, boolean secretKey, String keyAlgorithm, String contentAlgorithm, 
			JwtHeaders jwtHeaders, JWEOptions options) throws UtilsException{
		initJsonWebKey(jsonWebKey,secretKey, keyAlgorithm, contentAlgorithm, jwtHeaders, options);	
	}
	private void initJsonWebKey(JsonWebKey jsonWebKey, boolean secretKey, String keyAlgorithm, String contentAlgorithm, 
			JwtHeaders jwtHeaders, JWEOptions options) throws UtilsException{
		try {
			this.options=options;
			
			this.keyAlgorithm  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			this.contentAlgorithm = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			String compression = null;
			if(this.options.isDeflate()) {
				compression = JoseConstants.JWE_DEFLATE_ZIP_ALGORITHM;
			}
			
			if(secretKey) {
				if(jsonWebKey.getAlgorithm()==null) {
					jsonWebKey.setAlgorithm(contentAlgorithm);
				}
				if (KeyAlgorithm.DIRECT.equals(this.keyAlgorithm)) {
					this.provider = JweUtils.getDirectKeyJweEncryption(JwkUtils.toSecretKey(jsonWebKey), this.contentAlgorithm);
				}
				else {
					this.provider = JweUtils.createJweEncryptionProvider(JwkUtils.toSecretKey(jsonWebKey), this.keyAlgorithm, this.contentAlgorithm, compression);
				}
				if(this.provider==null) {
					throw new UtilsException("(JsonWebKey) JwsEncryptionProvider init failed; check content algorithm ("+contentAlgorithm+")");
				}
			}else {
				this.provider = JweUtils.createJweEncryptionProvider(JwkUtils.toRSAPublicKey(jsonWebKey), this.keyAlgorithm, this.contentAlgorithm, compression);
			}
			
			this.jwtHeaders = jwtHeaders;
		}catch(Exception t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.ENCRYPT,JsonUtils.SENDER,t);
		}
	}
	
	public String encrypt(String jsonString) throws UtilsException{
		try {
			switch(this.options.getSerialization()) {
				case JSON: return encryptJson(jsonString);
				case COMPACT: return encryptCompact(jsonString);
				default: throw new UtilsException("Unsupported serialization '"+this.options.getSerialization()+"'");
			}
		}
		catch(Exception t) {
			throw JsonUtils.convert(this.options.getSerialization(), JsonUtils.ENCRYPT,JsonUtils.SENDER,t);
		}
	}

	private String encryptCompact(String jsonString) throws Exception {	
		JweHeaders headersBuild = null;
		if(this.keyAlgorithm!=null) {
			headersBuild = new JweHeaders(this.keyAlgorithm,this.contentAlgorithm,this.options.isDeflate());
		}
		else {
			headersBuild = new JweHeaders(this.contentAlgorithm,this.options.isDeflate());
		}
		fillJwtHeaders(headersBuild, this.keyAlgorithm);
		return this.provider.encrypt(jsonString.getBytes(), headersBuild);
	}


	private String encryptJson(String jsonString) throws Exception {
		
		JweHeaders sharedUnprotectedHeaders = null;
		if(this.keyAlgorithm!=null) {
			sharedUnprotectedHeaders = new JweHeaders();
			sharedUnprotectedHeaders.setKeyEncryptionAlgorithm(this.keyAlgorithm);
		}

		JweHeaders protectedHeaders = new JweHeaders(this.contentAlgorithm, this.options.isDeflate());
		fillJwtHeaders(protectedHeaders, this.keyAlgorithm);
		
		JweJsonProducer producer = null;
		if(sharedUnprotectedHeaders!=null) {
			protectedHeaders.removeProperty("alg"); // e' in sharedUnprotectedHeaders
			producer = new JweJsonProducer(protectedHeaders, sharedUnprotectedHeaders, jsonString.getBytes());
		}
		else {
			producer = new JweJsonProducer(protectedHeaders, jsonString.getBytes());
		}
		
		return producer.encryptWith(this.provider);
	}
	
	private void fillJwtHeaders(JweHeaders headers, org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo) throws Exception {
		if(this.headers!=null &&
			this.headers.asMap()!=null && !this.headers.asMap().isEmpty()) {
			Iterator<String> itKeys = this.headers.asMap().keySet().iterator();
			while (itKeys.hasNext()) {
				String key = itKeys.next();
				if(!headers.containsHeader(key)) {
					headers.setHeader(key, this.headers.getHeader(key));
				}
			}
		}
		if(this.jwtHeaders!=null) {
			this.jwtHeaders.fillJwsHeaders(headers, false, keyAlgo!=null ? keyAlgo.getJwaName() : null);
		}
	}

}
