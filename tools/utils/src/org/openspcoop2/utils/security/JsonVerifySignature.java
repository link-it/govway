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

import java.io.File;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Properties;

import org.apache.cxf.common.util.Base64UrlUtility;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.JwsCompactConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJsonConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsJsonProducer;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

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
	
	private KeyStore trustStoreHttps; // per accedere ad un endpoint https dove scaricare i certificati
	private CertStore crlHttps; // per verificare i certificati http server rispetto alle crl
	public void setCrlHttps(CertStore crlHttps) {
		this.crlHttps = crlHttps;
	}
	
	private CertStore crlX509; // per verificare i certificati rispetto alle crl
	public void setCrlX509(CertStore crlX509) {
		this.crlX509 = crlX509;
	}
	
	public JsonVerifySignature(Properties props, JWTOptions options) throws UtilsException{
		try {
			this.dynamicProvider = JsonUtils.isDynamicProvider(props); // rimuove l'alias
			if(this.dynamicProvider) {
				this.properties = props;
			}
			else {
				this.provider = loadProviderFromProperties(props, null); // nel caso di jceks deve essere definito l'algoritmo se non e' dinamico
			}
			this.options = options;
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}

	private JwsSignatureVerifier loadProviderFromProperties(Properties props, org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm signatureAlgorithm) throws Exception {
		File fTmp = null;
		try {
			fTmp = JsonUtils.normalizeProperties(props); // in caso di url http viene letta la risorsa remota e salvata in tmp
			/*java.util.Enumeration<?> en = props.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				System.out.println("- ["+key+"] ["+props.getProperty(key)+"]");
			}*/
			
			JwsSignatureVerifier provider = null;
			if(signatureAlgorithm!=null) {
				provider = JsonUtils.getJwsSignatureVerifier(props, signatureAlgorithm);
			}
			else {
				provider = JsonUtils.getJwsSignatureVerifier(props);
			}
			if(provider==null) {
				provider = JwsUtils.loadSignatureVerifier(JsonUtils.newMessage(), props, new JwsHeaders());
			}
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
	
	public JsonVerifySignature(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String signatureAlgorithm, JWTOptions options) throws UtilsException{
		this(new KeyStore(keystore), alias, passwordPrivateKey, signatureAlgorithm, options);
	}
	public JsonVerifySignature(KeyStore keystore, String alias, String passwordPrivateKey, String signatureAlgorithm, JWTOptions options) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getHmacSignatureVerifier(keystore.getSecretKey(alias, passwordPrivateKey).getEncoded(), algo);
			if(this.provider==null) {
				throw new Exception("(JCEKS) JwsSignatureVerifier init failed; check signature algorithm ("+signatureAlgorithm+")");
			}
			this.options=options;
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}
	
	public JsonVerifySignature(JsonWebKeys jsonWebKeys, boolean secretKey, String alias, String signatureAlgorithm, JWTOptions options) throws UtilsException{
		this(JsonUtils.readKey(jsonWebKeys, alias), secretKey, signatureAlgorithm, options);
	}	
	public JsonVerifySignature(JsonWebKey jsonWebKey, boolean secretKey, String signatureAlgorithm, JWTOptions options) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			if(secretKey) {
				//this.provider = JwsUtils.getHmacSignatureVerifier(JwkUtils.toSecretKey(jsonWebKey).getEncoded(), algo);
				this.provider = JwsUtils.getSignatureVerifier(jsonWebKey, algo);
				if(this.provider==null) {
					throw new Exception("(JsonWebKey) JwsSignatureVerifier init failed; check signature algorithm ("+signatureAlgorithm+")");
				}
			}
			else {
				this.provider = JwsUtils.getPublicKeySignatureVerifier(JwkUtils.toRSAPublicKey(jsonWebKey), algo);
			}
			this.options=options;
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}
	
	
	public JsonVerifySignature(JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, null, 
				null, options);
	}
	public JsonVerifySignature(Properties propsTrustStoreHttps, java.security.KeyStore trustStoreVerificaCertificato, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(propsTrustStoreHttps, null, 
				new KeyStore(trustStoreVerificaCertificato), options);
	}
	public JsonVerifySignature(Properties propsTrustStoreHttps, KeyStore trustStore, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(propsTrustStoreHttps, null,
				trustStore, options);
	}
	public JsonVerifySignature(java.security.KeyStore trustStoreHttps, java.security.KeyStore trustStoreVerificaCertificato, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, new KeyStore(trustStoreHttps), 
				new KeyStore(trustStoreVerificaCertificato), options);
	}
	public JsonVerifySignature(KeyStore trustStoreHttps, KeyStore trustStore, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, trustStoreHttps,
				trustStore, options);
	}
	public JsonVerifySignature(java.security.KeyStore trustStoreVerificaCertificato, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, null, 
				new KeyStore(trustStoreVerificaCertificato), options);
	}
	public JsonVerifySignature(KeyStore trustStore, JWTOptions options) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, null, 
				trustStore, options);
	}
	private void _initVerifyCertificatiHeaderJWT(Properties propsTrustStoreHttps, KeyStore trustStoreHttps,
			KeyStore trustStoreVerificaCertificato, JWTOptions options) throws UtilsException{
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.properties = propsTrustStoreHttps; // le proprieta' servono per risolvere le url https
		this.trustStoreHttps = trustStoreHttps;
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
			consumer = new JwsCompactConsumer(jsonSignature, Base64UrlUtility.encode(jsonDetachedPayload));
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
	
	
	private X509Certificate x509Certificate;
	private RSAPublicKey rsaPublicKey;
	public X509Certificate getX509Certificate() {
		return this.x509Certificate;
	}
	public RSAPublicKey getRsaPublicKey() {
		return this.rsaPublicKey;
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
			pNew.put(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS, alias);
			provider = loadProviderFromProperties(pNew, jwsHeaders.getSignatureAlgorithm());
		}
		
		if(provider==null) {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = jwsHeaders.getSignatureAlgorithm();
			if(algo==null) {
				throw new Exception("SignatureAlgorithm not found");
			}
			
			if(jwsHeaders.getX509Chain()!=null && !jwsHeaders.getX509Chain().isEmpty() && this.options.isPermitUseHeaderX5C()) {
				try {
					// https://tools.ietf.org/html/rfc7515#section-4.1.6: The certificate containing the public key corresponding to the key used to digitally sign the JWS MUST be the first certificate.
					byte [] cer = Base64Utilities.decode(jwsHeaders.getX509Chain().get(0));
					CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
					if(this.trustStoreCertificatiX509!=null) {
						JsonUtils.validate(certificatoInfo, this.trustStoreCertificatiX509, this.crlX509, JwtHeaders.JWT_HDR_X5C, true);
					}
					this.x509Certificate = certificatoInfo.getCertificate();
					provider = JwsUtils.getPublicKeySignatureVerifier(this.x509Certificate, algo);
				}catch(Exception e) {
					throw new Exception("Process '"+JwtHeaders.JWT_HDR_X5C+"' error: "+e.getMessage(),e);
				}
			}
			else if(jwsHeaders.getJsonWebKey()!=null && this.options.isPermitUseHeaderJWK()) {
				try {
					this.rsaPublicKey = JwkUtils.toRSAPublicKey(jwsHeaders.getJsonWebKey());
					provider = JwsUtils.getPublicKeySignatureVerifier(this.rsaPublicKey, algo);
				}catch(Exception e) {
					throw new Exception("Process '"+JwtHeaders.JWT_HDR_JWK+"' error: "+e.getMessage(),e);
				}
			}
			else if(
					(jwsHeaders.getX509Url()!=null && this.options.isPermitUseHeaderX5U()) 
					||
					(jwsHeaders.getJsonWebKeysUrl()!=null && this.options.isPermitUseHeaderJKU())
					) {
				
				boolean x509 = true;
				String path = jwsHeaders.getX509Url();
				String hdr = JwtHeaders.JWT_HDR_X5U;
				if(path==null) {
					path=jwsHeaders.getJsonWebKeysUrl();
					x509 = false;
					hdr = JwtHeaders.JWT_HDR_JKU;
				}
				try {
					byte [] cer = null;
					if(this.properties!=null) {
						this.properties.put(RSSecurityConstants.RSSEC_KEY_STORE_FILE, path);
						cer = JsonUtils.readKeystoreFromURI(this.properties);
					}
					else {
						HttpResponse httpResponse = null;
						try {
							if(this.trustStoreHttps!=null) {
								httpResponse = HttpUtilities.getHTTPSResponse(path, this.trustStoreHttps.getKeystore(), this.crlHttps);
							}
							else {
								httpResponse = HttpUtilities.getHTTPResponse(path);
							}
						}catch(Exception e) {
							throw new Exception("Resource '"+path+"' unavailable: "+e.getMessage(),e);
						}
						if(httpResponse==null || httpResponse.getContent()==null) {
							throw new Exception("Resource '"+path+"' unavailable");
						}
						if(httpResponse.getResultHTTPOperation()!=200) {
							throw new Exception("Retrieve '"+path+"' failed (returnCode:"+httpResponse.getResultHTTPOperation()+")");
						}
						cer = httpResponse.getContent();
					}
					if(cer==null) {
						throw new Exception("Resource '"+path+"' unavailable");
					}
					if(x509) {
						CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
						if(this.trustStoreCertificatiX509!=null) {
							JsonUtils.validate(certificatoInfo, this.trustStoreCertificatiX509, this.crlX509, hdr, true);
						}
						this.x509Certificate = certificatoInfo.getCertificate();
						provider = JwsUtils.getPublicKeySignatureVerifier(this.x509Certificate, algo);
					}
					else {
						JWKSet set = new JWKSet(new String(cer));
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
						this.rsaPublicKey = JwkUtils.toRSAPublicKey(jsonWebKey);
						provider = JwsUtils.getPublicKeySignatureVerifier(this.rsaPublicKey, algo);
					}
				}catch(Exception e) {
					throw new Exception("Process '"+hdr+"' error: "+e.getMessage(),e);
				}
			}
			else if(jwsHeaders.getKeyId()!=null && this.options.isPermitUseHeaderKID()) {
				try {
					String kid = jwsHeaders.getKeyId();
					if(this.jsonWebKeys!=null) {
						JsonWebKey jsonWebKey = null;
						try {
							jsonWebKey = this.jsonWebKeys.getKey(kid);
						}catch(Exception e) {}
						if(jsonWebKey!=null) {
							this.rsaPublicKey = JwkUtils.toRSAPublicKey(jsonWebKey);
							provider = JwsUtils.getPublicKeySignatureVerifier(this.rsaPublicKey, algo);
						}
					}
					if(provider==null) {
						if(this.trustStoreCertificatiX509!=null) {
							if(this.trustStoreCertificatiX509.existsAlias(kid)) {
								Certificate cer = this.trustStoreCertificatiX509.getCertificate(kid);
								if(cer instanceof X509Certificate) {
									this.x509Certificate = (X509Certificate) cer;
									
									// La validazione serve per verificare la data e il crl
									JsonUtils.validate(new CertificateInfo(this.x509Certificate, kid), this.trustStoreCertificatiX509, this.crlX509, JwtHeaders.JWT_HDR_KID, false);
									
									provider = JwsUtils.getPublicKeySignatureVerifier(this.x509Certificate, algo);
								}
							}
						}
					}
					if(provider==null) {
						throw new Exception("Certificato, corrispondente al kid indicato, non trovato nel TrustStore dei certificati");
					}
				}catch(Exception e) {
					throw new Exception("Process '"+JwtHeaders.JWT_HDR_KID+"' error: "+e.getMessage(),e);
				}
			}
			else if(
					(jwsHeaders.getX509ThumbprintSHA256()!=null && this.options.isPermitUseHeaderX5T_256())
					||
					(jwsHeaders.getX509Thumbprint()!=null && this.options.isPermitUseHeaderX5T())
					) {
				String hdr = JwtHeaders.JWT_HDR_X5T;
				if (jwsHeaders.getX509ThumbprintSHA256()!=null) {
					hdr = JwtHeaders.JWT_HDR_X5t_S256;
				}
				try {
					if(this.trustStoreCertificatiX509==null) {
						throw new Exception("TrustStore dei certificati non fornito"); 
					}
					Certificate cer = null;
					if(jwsHeaders.getX509ThumbprintSHA256()!=null) {
						cer = this.trustStoreCertificatiX509.getCertificateByDigestSHA256UrlEncoded(jwsHeaders.getX509ThumbprintSHA256());
					}
					else{
						cer = this.trustStoreCertificatiX509.getCertificateByDigestSHA1UrlEncoded(jwsHeaders.getX509Thumbprint());
					}
					if(cer==null) {
						throw new Exception("Certificato, corrispondente al digest indicato, non trovato nel TrustStore dei certificati");
					}	
					if(cer instanceof X509Certificate) {
						this.x509Certificate = (X509Certificate) cer;
						
						// La validazione serve per verificare la data e il crl
						JsonUtils.validate(new CertificateInfo(this.x509Certificate, hdr), this.trustStoreCertificatiX509, this.crlX509, hdr, false);
						
						provider = JwsUtils.getPublicKeySignatureVerifier(this.x509Certificate, algo);
					}
					else {
						throw new Exception("Certificato indicato non è nel formato X.509");
					}
				}catch(Exception e) {
					throw new Exception("Process '"+hdr+"' error: "+e.getMessage(),e);
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
