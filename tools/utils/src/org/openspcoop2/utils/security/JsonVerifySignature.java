/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import java.security.PublicKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
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
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.remote.IRemoteStoreProvider;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.IOCSPValidator;

/**	
 * JsonVerifySignature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonVerifySignature {

	private JwsSignatureVerifier provider;
	private Properties propsConfig;
	private JWTOptions options;
	private Properties properties;
	private boolean dynamicProvider;
	
	private String decodedPayload;
	private byte[] decodedPayloadAsByte;
	
	private KeyStore trustStoreCertificatiX509; // per verificare i certificati presenti nell'header
	public KeyStore getTrustStoreCertificatiX509() {
		return this.trustStoreCertificatiX509;
	}
	private JsonWebKeys jsonWebKeys; // dove prendere il certificato per validare rispetto al kid
	
	private IRemoteStoreProvider remoteStoreProvider; // dove prendere il certificato remoto per validare rispetto al kid
	private RemoteKeyType remoteStoreKeyType; 
	private RemoteStoreConfig remoteStoreConfig;
	
	private KeyStore trustStoreHttps; // per accedere ad un endpoint https dove scaricare i certificati
	public KeyStore getTrustStoreHttps() {
		return this.trustStoreHttps;
	}
	private CertStore crlHttps; // per verificare i certificati http server rispetto alle crl
	public void setCrlHttps(CertStore crlHttps) {
		this.crlHttps = crlHttps;
	}
	private IOCSPValidator ocspValidatorHttps; // per verificare i certificati http server rispetto a servizi OCSP
	public void setOcspValidatorHttps(IOCSPValidator ocspValidator) {
		this.ocspValidatorHttps = ocspValidator;
	}
	
	private CertStore crlX509; // per verificare i certificati rispetto alle crl
	public void setCrlX509(CertStore crlX509) {
		this.crlX509 = crlX509;
	}
	private IOCSPValidator ocspValidatorX509; // per verificare i certificati rispetto a servizi OCSP
	public void setOcspValidatorX509(IOCSPValidator ocspValidator) {
		this.ocspValidatorX509 = ocspValidator;
	}

	public JsonVerifySignature(Properties props, JWTOptions options) throws UtilsException{
		try {
			this.propsConfig = props;
			this.dynamicProvider = JsonUtils.isDynamicProvider(props); // rimuove l'alias
			if(this.dynamicProvider) {
				this.properties = props;
			}
			else {
				this.provider = loadProviderFromProperties(props, null); // nel caso di jceks deve essere definito l'algoritmo se non e' dinamico
			}
			this.options = options;
		}catch(Exception t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}

	private JwsSignatureVerifier loadProviderFromProperties(Properties props, org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm signatureAlgorithm) throws UtilsException {
		File fTmp = null;
		try {
			fTmp = JsonUtils.normalizeProperties(props); // in caso di url http viene letta la risorsa remota e salvata in tmp
			/**java.util.Enumeration<?> en = props.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				System.out.println("- ["+key+"] ["+props.getProperty(key)+"]");
			}*/
			
			JwsSignatureVerifier providerReturn = null;
			if(signatureAlgorithm!=null) {
				providerReturn = JsonUtils.getJwsSignatureVerifier(props, signatureAlgorithm);
			}
			else {
				providerReturn = JsonUtils.getJwsSignatureVerifier(props);
			}
			if(providerReturn==null) {
				providerReturn = JwsUtils.loadSignatureVerifier(JsonUtils.newMessage(), props, new JwsHeaders());
			}
			if(providerReturn==null) {
				throw new UtilsException("JwsSignatureVerifier provider not found");
			}
			return providerReturn;
		}finally {
			try {
				if(fTmp!=null) {
					java.nio.file.Files.delete(fTmp.toPath());
				}
			}catch(Exception t) {
				// ignore
			}
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
			
			this.trustStoreCertificatiX509 = keystore; // per abilitare la validazione crl o ocsp
			
		}catch(Exception t) {
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
				throw new UtilsException("(JCEKS) JwsSignatureVerifier init failed; check signature algorithm ("+signatureAlgorithm+")");
			}
			this.options=options;
			
			this.trustStoreCertificatiX509 = keystore; // per abilitare la validazione crl o ocsp
			
		}catch(Exception t) {
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
				this.provider = JwsUtils.getSignatureVerifier(jsonWebKey, algo);
				if(this.provider==null) {
					throw new UtilsException("(JsonWebKey) JwsSignatureVerifier init failed; check signature algorithm ("+signatureAlgorithm+")");
				}
			}
			else {
				this.provider = JwsUtils.getPublicKeySignatureVerifier(JwkUtils.toRSAPublicKey(jsonWebKey), algo);
			}
			this.options=options;
		}catch(Exception t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}
	
	public JsonVerifySignature(String secret, String signatureAlgorithm, JWTOptions options) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getHmacSignatureVerifier(secret.getBytes(), algo);
			if(this.provider==null) {
				throw new UtilsException("(Secret) JwsSignatureVerifier init failed; check signature algorithm ("+signatureAlgorithm+")");
			}
			this.options=options;
		}catch(Exception t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}
	
	
	public JsonVerifySignature(JWTOptions options) {
		initVerifyCertificatiHeaderJWTEngine(null, null, 
				null, options);
	}
	public JsonVerifySignature(Properties propsTrustStoreHttps, java.security.KeyStore trustStoreVerificaCertificato, JWTOptions options) {
		initVerifyCertificatiHeaderJWTEngine(propsTrustStoreHttps, null, 
				new KeyStore(trustStoreVerificaCertificato), options);
	}
	public JsonVerifySignature(Properties propsTrustStoreHttps, KeyStore trustStore, JWTOptions options){
		initVerifyCertificatiHeaderJWTEngine(propsTrustStoreHttps, null,
				trustStore, options);
	}
	public JsonVerifySignature(java.security.KeyStore trustStoreHttps, java.security.KeyStore trustStoreVerificaCertificato, JWTOptions options) {
		initVerifyCertificatiHeaderJWTEngine(null, new KeyStore(trustStoreHttps), 
				new KeyStore(trustStoreVerificaCertificato), options);
	}
	public JsonVerifySignature(KeyStore trustStoreHttps, KeyStore trustStore, JWTOptions options) {
		initVerifyCertificatiHeaderJWTEngine(null, trustStoreHttps,
				trustStore, options);
	}
	public JsonVerifySignature(java.security.KeyStore trustStoreVerificaCertificato, JWTOptions options) {
		initVerifyCertificatiHeaderJWTEngine(null, null, 
				new KeyStore(trustStoreVerificaCertificato), options);
	}
	public JsonVerifySignature(KeyStore trustStore, JWTOptions options) {
		initVerifyCertificatiHeaderJWTEngine(null, null, 
				trustStore, options);
	}
	private void initVerifyCertificatiHeaderJWTEngine(Properties propsTrustStoreHttps, KeyStore trustStoreHttps,
			KeyStore trustStoreVerificaCertificato, JWTOptions options) {
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.properties = propsTrustStoreHttps; // le proprieta' servono per risolvere le url https
		this.trustStoreHttps = trustStoreHttps;
		this.trustStoreCertificatiX509 = trustStoreVerificaCertificato;
	}
	
	public JsonVerifySignature(JsonWebKeys jsonWebKeys, JWTOptions options) {
		initVerifyCertificatiHeaderJWTEngine(jsonWebKeys, options);
	}
	private void initVerifyCertificatiHeaderJWTEngine(JsonWebKeys jsonWebKeys, JWTOptions options) {
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.jsonWebKeys = jsonWebKeys;
	}

	
	public JsonVerifySignature(IRemoteStoreProvider remoteStoreProvider, RemoteKeyType remoteStoreKeyType, RemoteStoreConfig remoteStoreConfig, JWTOptions options) {
		initVerifyCertificatiHeaderRemoteStoreEngine(remoteStoreProvider, remoteStoreKeyType, remoteStoreConfig, options);
	}
	private void initVerifyCertificatiHeaderRemoteStoreEngine(IRemoteStoreProvider remoteStoreProvider, RemoteKeyType remoteStoreKeyType, RemoteStoreConfig remoteStoreConfig, JWTOptions options) {
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.remoteStoreProvider = remoteStoreProvider;
		this.remoteStoreKeyType = remoteStoreKeyType;
		this.remoteStoreConfig = remoteStoreConfig;
	}
	

	public boolean verify(String jsonString) throws UtilsException{
		try {
			switch(this.options.getSerialization()) {
				case JSON: return verifyJson(jsonString);
				case COMPACT: return verifyCompact(jsonString);
				default: throw new UtilsException("Unsupported serialization '"+this.options.getSerialization()+"'");
			}
		}
		catch(Exception t) {
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
		catch(Exception t) {
			throw JsonUtils.convert(this.options.getSerialization(), JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}

	private boolean verifyDetachedJson(String jsonDetachedSignature, String jsonDetachedPayload) throws UtilsException {
		JwsJsonProducer producer = new JwsJsonProducer(jsonDetachedPayload);
		JwsJsonConsumer consumer = new JwsJsonConsumer(jsonDetachedSignature, producer.getUnsignedEncodedPayload());
		return this.verifyJsonEngine(consumer);
	}
	private boolean verifyJson(String jsonString) throws UtilsException {
		JwsJsonConsumer consumer = new JwsJsonConsumer(jsonString);
		return this.verifyJsonEngine(consumer);
	}	
	private boolean verifyJsonEngine(JwsJsonConsumer consumer) throws UtilsException {
		
		JwsHeaders jwsHeaders = null;
		if(consumer.getSignatureEntries()!=null && !consumer.getSignatureEntries().isEmpty() &&
			// prendo la prima entry
			consumer.getSignatureEntries().get(0)!=null
			) {
			jwsHeaders = consumer.getSignatureEntries().get(0).getProtectedHeader();
		}
		JwsSignatureVerifier providerInternal = getProvider(jwsHeaders);
		
		boolean result = consumer.verifySignatureWith(providerInternal);
		this.decodedPayload = consumer.getDecodedJwsPayload();
		this.decodedPayloadAsByte = consumer.getDecodedJwsPayloadBytes();
		return result;
	}

	private boolean verifyDetachedCompact(String jsonDetachedSignature, String jsonDetachedPayload) throws UtilsException {
		return verifyCompactEngine(jsonDetachedSignature, jsonDetachedPayload);
	}
	private boolean verifyCompact(String jsonSignature) throws UtilsException {
		return verifyCompactEngine(jsonSignature, null);
	}
	private boolean verifyCompactEngine(String jsonSignature, String jsonDetachedPayload) throws UtilsException {
		
		JwsCompactConsumer consumer = null;
		if(jsonDetachedPayload==null) {
			consumer = new JwsCompactConsumer(jsonSignature);
		}
		else {
			consumer = new JwsCompactConsumer(jsonSignature, Base64UrlUtility.encode(jsonDetachedPayload));
		}
		
		JwsHeaders jwsHeaders = consumer.getJwsHeaders();
		JwsSignatureVerifier providerInternal = getProvider(jwsHeaders);
				
		boolean result = consumer.verifySignatureWith(providerInternal);
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
	private PublicKey publicKey;
	private String kid;
	public X509Certificate getX509Certificate() {
		return this.x509Certificate;
	}
	public PublicKey getRsaPublicKey() {
		return this.publicKey;
	}
	public String getKid() {
		return this.kid;
	}

	private String getProcessErrorMsg(String mode, Exception e) {
		return "Process '"+mode+"' error: "+e.getMessage();
	}
	private JwsSignatureVerifier getProvider(JwsHeaders jwsHeaders) throws UtilsException {
		
		JwsSignatureVerifier providerReturn = this.provider;
		if(jwsHeaders==null) {
			return providerReturn;
		}
				
		if(this.dynamicProvider) {
			/** String alias = JsonUtils.readAlias(jsonSignature); */
			String alias = jwsHeaders.getKeyId();
			Properties pNew = new Properties();
			pNew.putAll(this.properties);
			/** System.out.println("ALIAS ["+alias+"]"); */
			pNew.put(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS, alias);
			providerReturn = loadProviderFromProperties(pNew, jwsHeaders.getSignatureAlgorithm());
		}
		
		if(providerReturn==null) {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = jwsHeaders.getSignatureAlgorithm();
			if(algo==null) {
				throw new UtilsException("SignatureAlgorithm not found");
			}
			
			if(jwsHeaders.getX509Chain()!=null && !jwsHeaders.getX509Chain().isEmpty() && this.options.isPermitUseHeaderX5C()) {
				try {
					// https://tools.ietf.org/html/rfc7515#section-4.1.6: The certificate containing the public key corresponding to the key used to digitally sign the JWS MUST be the first certificate.
					byte [] cer = Base64Utilities.decode(jwsHeaders.getX509Chain().get(0));
					CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
					if(this.trustStoreCertificatiX509!=null) {
						JsonUtils.validate(certificatoInfo, this.trustStoreCertificatiX509, this.crlX509, this.ocspValidatorX509, JwtHeaders.JWT_HDR_X5C, true);
					}
					this.x509Certificate = certificatoInfo.getCertificate();
					providerReturn = JwsUtils.getPublicKeySignatureVerifier(this.x509Certificate, algo);
				}catch(Exception e) {
					throw new UtilsException(getProcessErrorMsg(JwtHeaders.JWT_HDR_X5C,e),e);
				}
			}
			else if(jwsHeaders.getJsonWebKey()!=null && this.options.isPermitUseHeaderJWK()) {
				try {
					this.publicKey = JwkUtils.toRSAPublicKey(jwsHeaders.getJsonWebKey());
					providerReturn = JwsUtils.getPublicKeySignatureVerifier(this.publicKey, algo);
				}catch(Exception e) {
					throw new UtilsException(getProcessErrorMsg(JwtHeaders.JWT_HDR_JWK,e),e);
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
					String prefixPath = "Resource '"+path+"'";
					byte [] cer = null;
					if(this.properties!=null) {
						this.properties.put(RSSecurityConstants.RSSEC_KEY_STORE_FILE, path);
						cer = JsonUtils.readKeystoreFromURI(this.properties);
					}
					else {
						HttpResponse httpResponse = getHttpResponse(path, prefixPath);
						if(httpResponse==null || httpResponse.getContent()==null) {
							throw new UtilsException(prefixPath+" unavailable");
						}
						if(httpResponse.getResultHTTPOperation()!=200) {
							throw new UtilsException("Retrieve '"+path+"' failed (returnCode:"+httpResponse.getResultHTTPOperation()+")");
						}
						cer = httpResponse.getContent();
					}
					if(cer==null) {
						throw new UtilsException(prefixPath+" unavailable");
					}
					if(x509) {
						CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
						if(this.trustStoreCertificatiX509!=null) {
							JsonUtils.validate(certificatoInfo, this.trustStoreCertificatiX509, this.crlX509, this.ocspValidatorX509, hdr, true);
						}
						this.x509Certificate = certificatoInfo.getCertificate();
						providerReturn = JwsUtils.getPublicKeySignatureVerifier(this.x509Certificate, algo);
					}
					else {
						JWKSet set = new JWKSet(new String(cer));
						JsonWebKeys jsonWebKeysInternal = set.getJsonWebKeys();
						JsonWebKey jsonWebKey = null;
						if(jsonWebKeysInternal.size()==1) {
							jsonWebKey = jsonWebKeysInternal.getKeys().get(0);
						}
						else {
							if(jwsHeaders.getKeyId()==null) {
								throw new UtilsException("Kid non definito e JwkSet contiene più di un certificato");
							}
							jsonWebKey = jsonWebKeysInternal.getKey(jwsHeaders.getKeyId());
						}
						if(jsonWebKey==null) {
							throw new UtilsException("JsonWebKey non trovata");
						}
						this.publicKey = JwkUtils.toRSAPublicKey(jsonWebKey);
						providerReturn = JwsUtils.getPublicKeySignatureVerifier(this.publicKey, algo);
					}
				}catch(Exception e) {
					throw new UtilsException(getProcessErrorMsg(hdr,e),e);
				}
			}
			else if(jwsHeaders.getKeyId()!=null && this.options.isPermitUseHeaderKID()) {
				try {
					this.kid = jwsHeaders.getKeyId();
					if(this.jsonWebKeys!=null) {
						JsonWebKey jsonWebKey = getJsonWebKeyByKidIgnoreException();
						if(jsonWebKey!=null) {
							this.publicKey = JwkUtils.toRSAPublicKey(jsonWebKey);
							providerReturn = JwsUtils.getPublicKeySignatureVerifier(this.publicKey, algo);
						}
					}
					if(providerReturn==null &&
						this.trustStoreCertificatiX509!=null &&
						this.trustStoreCertificatiX509.existsAlias(this.kid)) {
						Certificate cer = this.trustStoreCertificatiX509.getCertificate(this.kid);
						if(cer instanceof X509Certificate) {
							this.x509Certificate = (X509Certificate) cer;
							
							// La validazione serve per verificare la data e il crl
							JsonUtils.validate(new CertificateInfo(this.x509Certificate, this.kid), this.trustStoreCertificatiX509, this.crlX509, this.ocspValidatorX509, JwtHeaders.JWT_HDR_KID, false);
							
							providerReturn = JwsUtils.getPublicKeySignatureVerifier(this.x509Certificate, algo);
						}
					}
					if(providerReturn==null &&
							this.remoteStoreProvider!=null && this.remoteStoreConfig!=null && this.remoteStoreKeyType!=null) {
						providerReturn = getProviderByRemoteStore(algo);
					}
					if(providerReturn==null) {
						throw new UtilsException("Certificato, corrispondente al kid '"+this.kid+"', non trovato nel TrustStore dei certificati");
					}
				}catch(Exception e) {
					throw new UtilsException(getProcessErrorMsg(JwtHeaders.JWT_HDR_KID,e),e);
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
						throw new UtilsException("TrustStore dei certificati non fornito"); 
					}
					Certificate cer = null;
					if(jwsHeaders.getX509ThumbprintSHA256()!=null) {
						cer = this.trustStoreCertificatiX509.getCertificateByDigestSHA256UrlEncoded(jwsHeaders.getX509ThumbprintSHA256());
					}
					else{
						cer = this.trustStoreCertificatiX509.getCertificateByDigestSHA1UrlEncoded(jwsHeaders.getX509Thumbprint());
					}
					if(cer==null) {
						throw new UtilsException("Certificato, corrispondente al digest indicato, non trovato nel TrustStore dei certificati");
					}	
					if(cer instanceof X509Certificate) {
						this.x509Certificate = (X509Certificate) cer;
						
						// La validazione serve per verificare la data e il crl
						JsonUtils.validate(new CertificateInfo(this.x509Certificate, hdr), this.trustStoreCertificatiX509, this.crlX509, this.ocspValidatorX509, hdr, false);
						
						providerReturn = JwsUtils.getPublicKeySignatureVerifier(this.x509Certificate, algo);
					}
					else {
						throw new UtilsException("Certificato indicato non è nel formato X.509");
					}
				}catch(Exception e) {
					throw new UtilsException(getProcessErrorMsg(hdr,e),e);
				}
			}
			else {
				List<String> hdrNotPermitted = this.options.headersNotPermitted(jwsHeaders);
				String notPermitted = "";
				if(hdrNotPermitted!=null && !hdrNotPermitted.isEmpty()) {
					notPermitted = "; header trovati ma non abilitati all'utilizzo: "+hdrNotPermitted;
				}
				throw new UtilsException("Non è stato trovato alcun header che consentisse di recuperare il certificato per effettuare la validazione"+notPermitted);
			}
		}
		else {
			if(this.x509Certificate==null && providerReturn instanceof org.apache.cxf.rs.security.jose.jws.PublicKeyJwsSignatureVerifier) {
				org.apache.cxf.rs.security.jose.jws.PublicKeyJwsSignatureVerifier certVer = (org.apache.cxf.rs.security.jose.jws.PublicKeyJwsSignatureVerifier) providerReturn;
				this.x509Certificate = certVer.getX509Certificate();
				
				KeyStore trustStore = this.trustStoreCertificatiX509;
				if(trustStore==null &&
					this.propsConfig!=null) {
					trustStore = JsonUtils.getKeyStore(this.propsConfig);
				}
				
				if(trustStore!=null) {
					try {
						CertificateInfo certificatoInfo = new CertificateInfo(this.x509Certificate, "x509");
						JsonUtils.validate(certificatoInfo, trustStore, this.crlX509, this.ocspValidatorX509, null, true);
					}catch(Exception e) {
						throw new UtilsException(e.getMessage(),e);
					}
				}
			}
		}
		
		return providerReturn;
	}
	
	private JsonWebKey getJsonWebKeyByKidIgnoreException(){
		try {
			return this.jsonWebKeys.getKey(this.kid);
		}catch(Exception e) {
			// key not found
		}
		return null;
	}
	
	private HttpResponse getHttpResponse(String path, String prefixPath) throws UtilsException{
		HttpResponse httpResponse = null;
		try {
			if(this.trustStoreHttps!=null) {
				httpResponse = HttpUtilities.getHTTPSResponse(path, this.trustStoreHttps.getKeystore(), this.crlHttps, this.ocspValidatorHttps);
			}
			else {
				httpResponse = HttpUtilities.getHTTPResponse(path);
			}
		}catch(Exception e) {
			throw new UtilsException(prefixPath+" unavailable: "+e.getMessage(),e);
		}
		return httpResponse;
	}
	
	private JwsSignatureVerifier getProviderByRemoteStore(org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo) throws UtilsException {
		switch (this.remoteStoreKeyType) {
		case JWK:
			JWK jwk = this.remoteStoreProvider.readJWK(this.kid, this.remoteStoreConfig);
			if(jwk==null) {
				throw this.newUtilsExceptionErrorKidUnavailable(true);
			}
			JsonWebKey jsonWebKey = jwk.getJsonWebKey();
			if(jsonWebKey!=null) {
				this.publicKey = JwkUtils.toRSAPublicKey(jsonWebKey);
				return JwsUtils.getPublicKeySignatureVerifier(this.publicKey, algo);
			}
			else {
				throw this.newUtilsExceptionErrorKidUnavailable(true);
			}
		case PUBLIC_KEY:
			this.publicKey = this.remoteStoreProvider.readPublicKey(this.kid, this.remoteStoreConfig);
			if(this.publicKey==null) {
				throw this.newUtilsExceptionErrorKidUnavailable(true);
			}
			return JwsUtils.getPublicKeySignatureVerifier(this.publicKey, algo);
		case X509:
			org.openspcoop2.utils.certificate.Certificate certificate = this.remoteStoreProvider.readX509(this.kid, this.remoteStoreConfig);
			if(certificate==null) {
				throw this.newUtilsExceptionErrorKidUnavailable(false);
			}
			this.x509Certificate = certificate.getCertificate().getCertificate();
			return JwsUtils.getPublicKeySignatureVerifier(this.x509Certificate, algo);
		default:
			throw new UtilsException("RemoteStoreKeyType '"+this.remoteStoreKeyType+"' unknown");
		}
	}
	private UtilsException newUtilsExceptionErrorKidUnavailable(boolean publicKey) {
		String prefix = publicKey? "Public Key" : "Certificate";
		return new UtilsException(prefix+" with kid '"+this.kid+"' unavailable");
	}
}
