/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.security.PublicKey;
import java.util.Iterator;
import java.util.Properties;

import javax.crypto.SecretKey;

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
	private JOSERepresentation representation;
	
	private ContentAlgorithm contentAlgorithm;
	private KeyAlgorithm keyAlgorithm;
	
	private boolean deflate = false;
	
	private JweHeaders headers;
	private JwtHeaders jwtHeaders;
	
	public JsonEncrypt(Properties props, JOSERepresentation representation) throws UtilsException{
		this(props, null, representation);
	}
	public JsonEncrypt(Properties props, JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		try {
			this.headers = new JweHeaders();
			this.provider = JweUtils.loadEncryptionProvider(props, JsonUtils.newMessage(), this.headers);
			this.representation=representation;
			
			this.contentAlgorithm = JweUtils.getContentEncryptionAlgorithm(props, ContentAlgorithm.A256GCM);
			this.keyAlgorithm = JweUtils.getKeyEncryptionAlgorithm(props, null);
//			if(this.keyAlgorithm==null) {
//				throw new Exception("KeyAlgorithm undefined");
//			}
			
			String tmp = props.getProperty(JoseConstants.RSSEC_ENCRYPTION_ZIP_ALGORITHM);
			if(tmp!=null && JoseConstants.JWE_DEFLATE_ZIP_ALGORITHM.equalsIgnoreCase(tmp.trim())) {
				this.deflate = true;
			}
			
			this.jwtHeaders = jwtHeaders;
			
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.ENCRYPT,JsonUtils.SENDER,t);
		}
	}
	
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm, 
			JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, keyAlgorithm, contentAlgorithm, false, null, representation);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm, 
			JOSERepresentation representation) throws UtilsException{
		this(keystore, alias, keyAlgorithm, contentAlgorithm, false, null, representation);	
	}
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, keyAlgorithm, contentAlgorithm, deflate, null, representation);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JOSERepresentation representation) throws UtilsException{
		this(keystore, alias, keyAlgorithm, contentAlgorithm, deflate, null, representation);	
	}
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, keyAlgorithm, contentAlgorithm, false, jwtHeaders, representation);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(keystore, alias, keyAlgorithm, contentAlgorithm, false, jwtHeaders, representation);	
	}
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, keyAlgorithm, contentAlgorithm, deflate, jwtHeaders, representation);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		try {
			this.representation=representation;
			
			this.keyAlgorithm  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			this.contentAlgorithm = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			String compression = null;
			if(deflate) {
				this.deflate = deflate;
				compression = JoseConstants.JWE_DEFLATE_ZIP_ALGORITHM;
			}
			
			this.provider = JweUtils.createJweEncryptionProvider( (PublicKey) keystore.getPublicKey(alias), this.keyAlgorithm, this.contentAlgorithm, compression);
			
			this.jwtHeaders = jwtHeaders;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.ENCRYPT,JsonUtils.SENDER,t);
		}
	}
	
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, 
			JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, false, null, representation);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, 
			JOSERepresentation representation) throws UtilsException{
		this(keystore, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, false, null, representation);	
	}
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, deflate, null, representation);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JOSERepresentation representation) throws UtilsException{
		this(keystore, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, deflate, null, representation);	
	}
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, false, jwtHeaders, representation);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(keystore, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, false, jwtHeaders, representation);	
	}
	public JsonEncrypt(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, deflate, jwtHeaders, representation);
	}
	public JsonEncrypt(KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		try {
			this.representation=representation;
			
			this.keyAlgorithm  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			this.contentAlgorithm = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			String compression = null;
			if(deflate) {
				this.deflate = deflate;
				compression = JoseConstants.JWE_DEFLATE_ZIP_ALGORITHM;
			}
			
			this.provider = JweUtils.createJweEncryptionProvider( (SecretKey) keystore.getSecretKey(alias, passwordPrivateKey), this.keyAlgorithm, this.contentAlgorithm, compression);
			
			this.jwtHeaders = jwtHeaders;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.ENCRYPT,JsonUtils.SENDER,t);
		}
	}

	public JsonEncrypt(JsonWebKeys jsonWebKeys, String alias, String keyAlgorithm, String contentAlgorithm, 
			JOSERepresentation representation) throws UtilsException{
		this(jsonWebKeys, alias, keyAlgorithm, contentAlgorithm, false, null, representation);	
	}
	public JsonEncrypt(JsonWebKeys jsonWebKeys, String alias, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JOSERepresentation representation) throws UtilsException{
		this(jsonWebKeys, alias, keyAlgorithm, contentAlgorithm, deflate, null, representation);	
	}
	public JsonEncrypt(JsonWebKeys jsonWebKeys, String alias, String keyAlgorithm, String contentAlgorithm, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(jsonWebKeys, alias, keyAlgorithm, contentAlgorithm, false, jwtHeaders, representation);	
	}
	public JsonEncrypt(JsonWebKeys jsonWebKeys, String alias, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(JsonUtils.readKey(jsonWebKeys, alias),keyAlgorithm,contentAlgorithm,deflate,jwtHeaders,representation);
	}

	public JsonEncrypt(JsonWebKey jsonWebKey, String keyAlgorithm, String contentAlgorithm, 
			JOSERepresentation representation) throws UtilsException{
		this(jsonWebKey, keyAlgorithm, contentAlgorithm, false, null, representation);	
	}
	public JsonEncrypt(JsonWebKey jsonWebKey, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JOSERepresentation representation) throws UtilsException{
		this(jsonWebKey, keyAlgorithm, contentAlgorithm, deflate, null, representation);	
	}
	public JsonEncrypt(JsonWebKey jsonWebKey, String keyAlgorithm, String contentAlgorithm, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(jsonWebKey, keyAlgorithm, contentAlgorithm, false, jwtHeaders, representation);	
	}
	public JsonEncrypt(JsonWebKey jsonWebKey, String keyAlgorithm, String contentAlgorithm, boolean deflate, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		try {
			this.representation=representation;
			
			this.keyAlgorithm  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			this.contentAlgorithm = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			String compression = null;
			if(deflate) {
				this.deflate = deflate;
				compression = JoseConstants.JWE_DEFLATE_ZIP_ALGORITHM;
			}
			
			this.provider = JweUtils.createJweEncryptionProvider(JwkUtils.toRSAPublicKey(jsonWebKey), this.keyAlgorithm, this.contentAlgorithm, compression);
			
			this.jwtHeaders = jwtHeaders;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.ENCRYPT,JsonUtils.SENDER,t);
		}
	}
	
	public String encrypt(String jsonString) throws UtilsException{
		try {
			switch(this.representation) {
				case SELF_CONTAINED: return encryptSelfContained(jsonString);
				case COMPACT: return encryptCompact(jsonString);
				default: throw new UtilsException("Unsupported representation '"+this.representation+"'");
			}
		}
		catch(Throwable t) {
			throw JsonUtils.convert(this.representation, JsonUtils.ENCRYPT,JsonUtils.SENDER,t);
		}
	}


	private String encryptCompact(String jsonString) throws Exception {	
		JweHeaders headers = null;
		if(this.keyAlgorithm!=null) {
			headers = new JweHeaders(this.keyAlgorithm,this.contentAlgorithm,this.deflate);
		}
		else {
			headers = new JweHeaders(this.contentAlgorithm,this.deflate);
		}
		fillJwtHeaders(headers, this.keyAlgorithm);
		return this.provider.encrypt(jsonString.getBytes(), headers);
	}


	private String encryptSelfContained(String jsonString) {
		
		JweHeaders sharedUnprotectedHeaders = null;
		if(this.keyAlgorithm!=null) {
			sharedUnprotectedHeaders = new JweHeaders();
			sharedUnprotectedHeaders.setKeyEncryptionAlgorithm(this.keyAlgorithm);
		}

		JweHeaders protectedHeaders = new JweHeaders(this.contentAlgorithm, this.deflate);
		
		JweJsonProducer producer = null;
		if(sharedUnprotectedHeaders!=null) {
			producer = new JweJsonProducer(protectedHeaders, sharedUnprotectedHeaders, jsonString.getBytes());
		}
		else {
			producer = new JweJsonProducer(protectedHeaders, jsonString.getBytes());
		}
		
		return producer.encryptWith(this.provider);
	}
	
	private void fillJwtHeaders(JweHeaders headers, org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo) throws Exception {
		if(this.headers!=null) {
			if(this.headers.asMap()!=null && !this.headers.asMap().isEmpty()) {
				Iterator<String> itKeys = this.headers.asMap().keySet().iterator();
				while (itKeys.hasNext()) {
					String key = (String) itKeys.next();
					if(!headers.containsHeader(key)) {
						headers.setHeader(key, this.headers.getHeader(key));
					}
				}
			}
		}
		if(this.jwtHeaders!=null) {
			this.jwtHeaders.fillJwsHeaders(headers, false, keyAlgo.getJwaName());
		}
	}

}
