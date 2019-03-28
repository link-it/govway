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

import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Properties;

import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.JwsCompactConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJsonConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsJsonProducer;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonVerifySignature {

	private JwsSignatureVerifier provider;
	private JWTOptions options;
	private Properties properties;
	private boolean dynamicProvider;
	
	private String decodedPayload;
	private byte[] decodedPayloadAsByte;
	
	private KeyStore trustStoreCertificatiX509; // per verificare i certificati presenti nell'header 
	private JsonWebKeys jsonWebKeys; // dove prendere il certificato per validare rispetto al kid
	
	public JsonVerifySignature(Properties props, JWTOptions options) throws UtilsException{
		try {
			this.dynamicProvider = JsonUtils.isDynamicProvider(props); // rimuove l'alias
			if(this.dynamicProvider) {
				this.properties = props;
			}
			else {
				this.provider = loadProviderFromProperties(props);
			}
			this.options = options;
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}
	
	private JwsSignatureVerifier loadProviderFromProperties(Properties props) throws Exception {
		File fTmp = null;
		try {
			fTmp = JsonUtils.normalizeProperties(props); // in caso di url http viene letta la risorsa remota e salvata in tmp
			/*java.util.Enumeration<?> en = props.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				System.out.println("- ["+key+"] ["+props.getProperty(key)+"]");
			}*/
			JwsSignatureVerifier provider = JwsUtils.loadSignatureVerifier(JsonUtils.newMessage(), props, new JwsHeaders());
			if(provider==null) {
				throw new Exception("JwsSignatureVerifier provider not found");
			}
			return provider;
		}finally {
			try {
				if(fTmp!=null) {
					fTmp.delete();
				}
			}catch(Throwable t) {}
		}
	}
	
	public JsonVerifySignature(java.security.KeyStore keystore, String alias, String signatureAlgorithm, JWTOptions options) throws UtilsException{
		this(new KeyStore(keystore), alias, signatureAlgorithm, options);
	}
	public JsonVerifySignature(KeyStore keystore, String alias, String signatureAlgorithm, JWTOptions options) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getPublicKeySignatureVerifier((X509Certificate) keystore.getCertificate(alias), algo);
			this.options=options;
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}
	
	public JsonVerifySignature(JsonWebKeys jsonWebKeys, String alias, String signatureAlgorithm, JWTOptions options) throws UtilsException{
		this(JsonUtils.readKey(jsonWebKeys, alias), signatureAlgorithm, options);
	}
	
	public JsonVerifySignature(JsonWebKey jsonWebKey, String signatureAlgorithm, JWTOptions options) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getPublicKeySignatureVerifier(JwkUtils.toRSAPublicKey(jsonWebKey), algo);
			this.options=options;
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}
	
	
	public JsonVerifySignature(JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, null, options);
	}
	public JsonVerifySignature(Properties propsTrustStoreHttps, java.security.KeyStore trustStoreVerificaCertificato, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(propsTrustStoreHttps, new KeyStore(trustStoreVerificaCertificato), options);
	}
	public JsonVerifySignature(Properties propsTrustStoreHttps, KeyStore trustStore, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(propsTrustStoreHttps, trustStore, options);
	}
	public JsonVerifySignature(java.security.KeyStore trustStoreVerificaCertificato, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, new KeyStore(trustStoreVerificaCertificato), options);
	}
	public JsonVerifySignature(KeyStore trustStore, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, trustStore, options);
	}
	private void _initVerifyCertificatiHeaderJWT(Properties propsTrustStoreHttps, KeyStore trustStoreVerificaCertificato, JWTOptions options) throws UtilsException{
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.properties = propsTrustStoreHttps; // le proprieta' servono per risolvere le url https
		this.trustStoreCertificatiX509 = trustStoreVerificaCertificato;
	}
	
	public JsonVerifySignature(JsonWebKeys jsonWebKeys, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(jsonWebKeys, options);
	}
	private void _initVerifyCertificatiHeaderJWT(JsonWebKeys jsonWebKeys, JWTOptions options) throws UtilsException{
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.jsonWebKeys = jsonWebKeys;
	}


	public boolean verify(String jsonString) throws UtilsException{
		try {
			switch(this.options.getSerialization()) {
				case JSON: return verifyJson(jsonString);
				case COMPACT: return verifyCompact(jsonString);
				default: throw new UtilsException("Unsupported serialization '"+this.options.getSerialization()+"'");
			}
		}
		catch(Throwable t) {
			t.printStackTrace(System.out);
			throw JsonUtils.convert(this.options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}

	public boolean verifyDetached(String jsonDetachedSignature, String jsonDetachedPayload) throws UtilsException{
		try {
			switch(this.options.getSerialization()) {
				case JSON: return verifyDetachedJson(jsonDetachedSignature, jsonDetachedPayload);
				case COMPACT: return verifyDetachedCompact(jsonDetachedSignature, jsonDetachedPayload);
				default: throw new UtilsException("Unsupported serialization '"+this.options.getSerialization()+"'");
			}
		}
		catch(Throwable t) {
			throw JsonUtils.convert(this.options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}

	private boolean verifyDetachedJson(String jsonDetachedSignature, String jsonDetachedPayload) throws Exception {
		JwsJsonProducer producer = new JwsJsonProducer(jsonDetachedPayload);
		JwsJsonConsumer consumer = new JwsJsonConsumer(jsonDetachedSignature, producer.getUnsignedEncodedPayload());
		return this._verifyJson(consumer);
	}
	private boolean verifyJson(String jsonString) throws Exception {
		JwsJsonConsumer consumer = new JwsJsonConsumer(jsonString);
		return this._verifyJson(consumer);
	}	
	private boolean _verifyJson(JwsJsonConsumer consumer) throws Exception {
		
		JwsHeaders jwsHeaders = null;
		if(consumer.getSignatureEntries()!=null && !consumer.getSignatureEntries().isEmpty()) {
			// prendo la prima entry
			if(consumer.getSignatureEntries().get(0)!=null) {
				jwsHeaders = consumer.getSignatureEntries().get(0).getProtectedHeader();
			}
		}
		JwsSignatureVerifier provider = getProvider(jwsHeaders);
		
		boolean result = consumer.verifySignatureWith(provider);
		this.decodedPayload = consumer.getDecodedJwsPayload();
		this.decodedPayloadAsByte = consumer.getDecodedJwsPayloadBytes();
		return result;
	}

	private boolean verifyDetachedCompact(String jsonDetachedSignature, String jsonDetachedPayload) throws Exception {
		return _verifyCompact(jsonDetachedSignature, jsonDetachedPayload);
	}
	private boolean verifyCompact(String jsonSignature) throws Exception {
		return _verifyCompact(jsonSignature, null);
	}
	private boolean _verifyCompact(String jsonSignature, String jsonDetachedPayload) throws Exception {
		
		JwsCompactConsumer consumer = null;
		if(jsonDetachedPayload==null) {
			consumer = new JwsCompactConsumer(jsonSignature);
		}
		else {
			consumer = new JwsCompactConsumer(jsonSignature, jsonDetachedPayload);
		}
		
		JwsHeaders jwsHeaders = consumer.getJwsHeaders();
		JwsSignatureVerifier provider = getProvider(jwsHeaders);
				
		boolean result = consumer.verifySignatureWith(provider);
		if(jsonDetachedPayload!=null) {
			this.decodedPayload = jsonDetachedPayload;
			this.decodedPayloadAsByte = jsonDetachedPayload.getBytes();
		}
		else {
			this.decodedPayload = consumer.getDecodedJwsPayload();
			this.decodedPayloadAsByte = consumer.getDecodedJwsPayloadBytes();
		}
		return result;
	}
	
	public String getDecodedPayload() {
		return this.decodedPayload;
	}

	public byte[] getDecodedPayloadAsByte() {
		return this.decodedPayloadAsByte;
	}
	
	
	private JwsSignatureVerifier getProvider(JwsHeaders jwsHeaders) throws Exception {
		
		JwsSignatureVerifier provider = this.provider;
		if(jwsHeaders==null) {
			return provider;
		}
		
		if(this.dynamicProvider) {
			//String alias = JsonUtils.readAlias(jsonSignature);
			String alias = jwsHeaders.getKeyId();
			Properties pNew = new Properties();
			pNew.putAll(this.properties);
			//System.out.println("ALIAS ["+alias+"]");
			pNew.put(JoseConstants.RSSEC_KEY_STORE_ALIAS, alias);
			provider = loadProviderFromProperties(pNew);
		}
		
		if(provider==null) {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = jwsHeaders.getSignatureAlgorithm();
			if(algo==null) {
				throw new Exception("SignatureAlgorithm not found");
			}
			
			if(jwsHeaders.getX509Chain()!=null && !jwsHeaders.getX509Chain().isEmpty() && this.options.isPermitUseHeaderX5C()) {
				byte [] cer = Base64Utilities.decode(jwsHeaders.getX509Chain().get(0));
				CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
				if(this.trustStoreCertificatiX509!=null) {
					if(certificatoInfo.isVerified(this.trustStoreCertificatiX509)==false) {
						throw new Exception("Certificato presente nell'header '"+JwtHeaders.JWT_HDR_X5U+"' non è verificabile rispetto alle CA conosciute");
					}
				}
				X509Certificate cerx509 = certificatoInfo.getCertificate();
				provider = JwsUtils.getPublicKeySignatureVerifier(cerx509, algo);
			}
			else if(jwsHeaders.getJsonWebKey()!=null && this.options.isPermitUseHeaderJWK()) {
				provider = JwsUtils.getPublicKeySignatureVerifier(JwkUtils.toRSAPublicKey(jwsHeaders.getJsonWebKey()), algo);
			}
			else if(jwsHeaders.getX509Url()!=null && this.options.isPermitUseHeaderX5U()) {
				if(this.properties==null) {
					this.properties = new Properties();
				}
				this.properties.put(JoseConstants.RSSEC_KEY_STORE_FILE, jwsHeaders.getX509Url());
				File fTmp = null;
				try {
					fTmp = JsonUtils.normalizeProperties(this.properties); // in caso di url http viene letta la risorsa remota e salvata in tmp
					byte [] cer = FileSystemUtilities.readBytesFromFile(fTmp);
					CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
					if(this.trustStoreCertificatiX509!=null) {
						if(certificatoInfo.isVerified(this.trustStoreCertificatiX509)==false) {
							throw new Exception("Certificato presente nell'header '"+JwtHeaders.JWT_HDR_X5U+"' non è verificabile rispetto alle CA conosciute");
						}
					}
					X509Certificate cerx509 = certificatoInfo.getCertificate();
					provider = JwsUtils.getPublicKeySignatureVerifier(cerx509, algo);
				}finally {
					try {
						if(fTmp!=null) {
							fTmp.delete();
						}
					}catch(Throwable t) {}
				}
			}
			else if(jwsHeaders.getJsonWebKeysUrl()!=null && this.options.isPermitUseHeaderJKU()) {
				if(this.properties==null) {
					this.properties = new Properties();
				}
				this.properties.put(JoseConstants.RSSEC_KEY_STORE_FILE, jwsHeaders.getJsonWebKeysUrl());
				File fTmp = null;
				try {
					fTmp = JsonUtils.normalizeProperties(this.properties); // in caso di url http viene letta la risorsa remota e salvata in tmp
					JWKSet set = new JWKSet(FileSystemUtilities.readFile(fTmp));
					JsonWebKeys jsonWebKeys = set.getJsonWebKeys();
					JsonWebKey jsonWebKey = null;
					if(jsonWebKeys.size()==1) {
						jsonWebKey = jsonWebKeys.getKeys().get(0);
					}
					else {
						if(jwsHeaders.getKeyId()==null) {
							throw new Exception("Kid non definito e JwkSet contiene più di un certificato");
						}
						jsonWebKey = jsonWebKeys.getKey(jwsHeaders.getKeyId());
					}
					if(jsonWebKey==null) {
						throw new Exception("JsonWebKey non trovata");
					}
					provider = JwsUtils.getPublicKeySignatureVerifier(JwkUtils.toRSAPublicKey(jsonWebKey), algo);
				}finally {
					try {
						if(fTmp!=null) {
							fTmp.delete();
						}
					}catch(Throwable t) {}
				}
			}
			else if(jwsHeaders.getKeyId()!=null && this.options.isPermitUseHeaderKID()) {
				String kid = jwsHeaders.getKeyId();
				if(this.jsonWebKeys!=null) {
					JsonWebKey jsonWebKey = null;
					try {
						jsonWebKey = this.jsonWebKeys.getKey(kid);
					}catch(Exception e) {}
					if(jsonWebKey!=null) {
						provider = JwsUtils.getPublicKeySignatureVerifier(JwkUtils.toRSAPublicKey(jsonWebKey), algo);
					}
				}
				if(provider==null) {
					if(this.trustStoreCertificatiX509!=null) {
						if(this.trustStoreCertificatiX509.existsAlias(kid)) {
							Certificate cer = this.trustStoreCertificatiX509.getCertificate(kid);
							if(cer instanceof X509Certificate) {
								X509Certificate x509Certificate = (X509Certificate) cer;
								provider = JwsUtils.getPublicKeySignatureVerifier(x509Certificate, algo);
							}
						}
					}
				}
			}
			else {
				List<String> hdrNotPermitted = this.options.headersNotPermitted(jwsHeaders);
				String notPermitted = "";
				if(hdrNotPermitted!=null && !hdrNotPermitted.isEmpty()) {
					notPermitted = "; header trovati ma non abilitati all'utilizzo: "+hdrNotPermitted;
				}
				throw new Exception("Non è stato trovato alcun header che consentisse di recuperare il certificato per effettuare la validazione"+notPermitted);
			}
		}
		
		return provider;
	}
}
