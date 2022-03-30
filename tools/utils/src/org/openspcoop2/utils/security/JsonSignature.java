/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.util.Iterator;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.JwsCompactProducer;
import org.apache.cxf.rs.security.jose.jws.JwsException;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJsonProducer;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureProvider;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;

/**	
 * JsonSignature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonSignature {

	private JwsSignatureProvider provider;
	private JWSOptions options;
	private JwsHeaders headers;
	private JwtHeaders jwtHeaders;
	
	public JsonSignature(Properties props, JWSOptions options) throws UtilsException{
		this(props, null, options);
	}
	public JsonSignature(Properties props, JwtHeaders jwtHeaders, JWSOptions options) throws UtilsException{
		try {
			this.headers = new JwsHeaders(props);
			this.provider = JsonUtils.getJwsSymmetricProvider(props);
			if(this.provider==null) {
				try {
					this.provider = JwsUtils.loadSignatureProvider(JsonUtils.newMessage(), props, this.headers);
				}catch(JwsException jwsExc) {
					if(jwsExc!=null && jwsExc.getMessage()!=null && jwsExc.getMessage().contains("NO_PROVIDER")) {
						// caso in cui la chiave privata PKCS11 non e' stata mappata in un PrivateKeyJwsSignatureProvider
						// caso pkcs11 dove la chiave privata non e' ne ECPrivateKey ne RSAPrivateKey gestita dal metodo getPrivateKeySignatureProvider
						// ad es. può essere sun.security.pkcs11.P11Key$P11PrivateKey
						this.provider = JsonUtils.getJwsAsymmetricProvider(props);
						if(this.provider==null) {
							// rilancio eccezione precedente
							throw jwsExc;
						}
					}
				}
			}
			this.options=options;
			this.jwtHeaders = jwtHeaders;
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.SENDER,t);
		}
	}

	public JsonSignature(java.security.KeyStore keystore, boolean secretKey, String alias, String passwordPrivateKey, String signatureAlgorithm, JWSOptions options) throws UtilsException{
		this(new KeyStore(keystore), secretKey, alias, passwordPrivateKey, signatureAlgorithm, 
				null, options);
	}
	public JsonSignature(KeyStore keystore, boolean secretKey, String alias, String passwordPrivateKey, String signatureAlgorithm, JWSOptions options) throws UtilsException{
		this(keystore, secretKey, alias, passwordPrivateKey, signatureAlgorithm, 
				null, options);
	}
	public JsonSignature(java.security.KeyStore keystore, boolean secretKey, String alias, String passwordPrivateKey, String signatureAlgorithm, 
			JwtHeaders jwtHeaders, JWSOptions options) throws UtilsException{
		this(new KeyStore(keystore),secretKey, alias, passwordPrivateKey, signatureAlgorithm, jwtHeaders, options);
	}
	public JsonSignature(KeyStore keystore, boolean secretKey, String alias, String passwordPrivateKey, String signatureAlgorithm, 
			JwtHeaders jwtHeaders, JWSOptions options) throws UtilsException{
		try {
			
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			if(secretKey) {
				String tipo = "JCEKS";
				SecretKey secret = keystore.getSecretKey(alias, passwordPrivateKey);
				if("pkcs11".equalsIgnoreCase(keystore.getKeystoreType())) {
					tipo = "PKCS11";
					SecretKeyPkcs11 secretKeyPkcs11 = new SecretKeyPkcs11(keystore.getKeystoreProvider(), secret);
					this.provider = new HmacJwsSignatureProvider(secretKeyPkcs11, algo);
				}
				else {
					this.provider = JwsUtils.getHmacSignatureProvider(secret.getEncoded(), algo);
				}
				if(this.provider==null) {
					throw new Exception("("+tipo+") JwsSignatureProvider init failed; check signature algorithm ("+signatureAlgorithm+")");
				}
			}
			else {
				PrivateKey pKey = keystore.getPrivateKey(alias, passwordPrivateKey);
				this.provider = JwsUtils.getPrivateKeySignatureProvider(pKey, algo);
				if(this.provider==null) {
					// caso in cui la chiave privata PKCS11 non e' stata mappata in un PrivateKeyJwsSignatureProvider
					// caso pkcs11 dove la chiave privata non e' ne ECPrivateKey ne RSAPrivateKey gestita dal metodo getPrivateKeySignatureProvider
					// ad es. può essere sun.security.pkcs11.P11Key$P11PrivateKey
					this.provider = JsonUtils.getJwsAsymmetricProvider(pKey, algo);
					if(this.provider==null) {
						throw new Exception("("+keystore.getKeystore().getType()+") JwsSignatureProvider init failed; check signature algorithm ("+signatureAlgorithm+")");
					}
				}
			}
			this.options=options;
			this.jwtHeaders = jwtHeaders;
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.SENDER,t);
		}
	}
	
	public JsonSignature(JsonWebKeys jsonWebKeys, boolean secretKey, String alias, String signatureAlgorithm, JWSOptions options) throws UtilsException{
		this(jsonWebKeys, secretKey, alias, signatureAlgorithm, 
				null, options);
	}
	public JsonSignature(JsonWebKeys jsonWebKeys, boolean secretKey, String alias, String signatureAlgorithm, 
			JwtHeaders jwtHeaders, JWSOptions options) throws UtilsException{
		this(JsonUtils.readKey(jsonWebKeys, alias),secretKey,signatureAlgorithm,jwtHeaders,options);
	}
	
	public JsonSignature(JsonWebKey jsonWebKey, boolean secretKey, String signatureAlgorithm, JWSOptions options) throws UtilsException{
		this(jsonWebKey, secretKey, signatureAlgorithm, 
				null, options);
	}
	public JsonSignature(JsonWebKey jsonWebKey, boolean secretKey, String signatureAlgorithm, 
			JwtHeaders jwtHeaders, JWSOptions options) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			if(secretKey) {
				//this.provider = JwsUtils.getHmacSignatureProvider(JwkUtils.toSecretKey(jsonWebKey).getEncoded(), algo);
				this.provider = JwsUtils.getSignatureProvider(jsonWebKey, algo);
				if(this.provider==null) {
					throw new Exception("(JsonWebKey) JwsSignatureProvider init failed; check signature algorithm ("+signatureAlgorithm+")");
				}
			}else {
				this.provider = JwsUtils.getPrivateKeySignatureProvider(JwkUtils.toRSAPrivateKey(jsonWebKey), algo);
			}
			this.options=options;
			this.jwtHeaders = jwtHeaders;
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.SENDER,t);
		}
	}

	public JsonSignature(String secret,  String signatureAlgorithm, JWSOptions options) throws UtilsException{
		this(secret, signatureAlgorithm, null, options);
	}
	public JsonSignature(String secret,  String signatureAlgorithm, JwtHeaders jwtHeaders, JWSOptions options) throws UtilsException{
		try {
			
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getHmacSignatureProvider(secret.getBytes(), algo);
			if(this.provider==null) {
				throw new Exception("(Secret) JwsSignatureProvider init failed; check signature algorithm ("+signatureAlgorithm+")");
			}
			this.options=options;
			this.jwtHeaders = jwtHeaders;
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.SENDER,t);
		}
	}

	public String sign(String jsonString) throws UtilsException{
		try {
			switch(this.options.getSerialization()) {
				case JSON: return signJson(jsonString);
				case COMPACT: return signCompact(jsonString);
				default: throw new UtilsException("Unsupported serialization '"+this.options.getSerialization()+"'");
			}
		}
		catch(Throwable t) {
			throw JsonUtils.convert(this.options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.SENDER,t);
		}
	}

	private String signCompact(String jsonString) throws Exception {
		JwsHeaders headers = new JwsHeaders(this.provider.getAlgorithm());
		// utilizzabile solamente per JSON. Per COMPACT è sconsigliato poichè limitato nei caratteri, non utilizzabile quindi per un json https://tools.ietf.org/html/rfc7797#page-8
		// Infatti JwsCompactProducer di cxf non lo implementa proprio se si va a vedere il codice, il metodo 'getUnsignedEncodedJws' utilizzato per comporre il jwt non prevede il caso di lasciarlo in chiaro
//		if(this.options.isPayloadEncoding()==false) {
//			headers.setPayloadEncodingStatus(this.options.isPayloadEncoding()); // RFC: https://tools.ietf.org/html/rfc7797
//		}
		JwsCompactProducer jwsProducer = new JwsCompactProducer(headers, jsonString, this.options.isDetached());
		fillJwtHeaders(jwsProducer.getJwsHeaders(), this.provider.getAlgorithm());
		return jwsProducer.signWith(this.provider);
	}

	private String signJson(String jsonString) throws Exception {
		JwsJsonProducer jwsProducer = new JwsJsonProducer(jsonString, false, this.options.isDetached());
		JwsHeaders headers = new JwsHeaders(this.provider.getAlgorithm());
		if(this.options.isPayloadEncoding()==false) {
			headers.setPayloadEncodingStatus(this.options.isPayloadEncoding()); // RFC: https://tools.ietf.org/html/rfc7797
		}
		fillJwtHeaders(headers, this.provider.getAlgorithm());
		return jwsProducer.signWith(this.provider, headers);
	}

	
	private void fillJwtHeaders(JwsHeaders headers,
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm signatureAlgo) throws Exception {
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
			this.jwtHeaders.fillJwsHeaders(headers, false, signatureAlgo.getJwaName());
		}
	}
}
