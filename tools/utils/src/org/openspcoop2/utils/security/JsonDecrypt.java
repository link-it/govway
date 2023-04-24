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
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm;
import org.apache.cxf.rs.security.jose.jwe.JweCompactConsumer;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionOutput;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweException;
import org.apache.cxf.rs.security.jose.jwe.JweHeaders;
import org.apache.cxf.rs.security.jose.jwe.JweJsonConsumer;
import org.apache.cxf.rs.security.jose.jwe.JweUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.IOCSPValidator;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonDecrypt {

	private JweDecryptionProvider provider;
	private JWTOptions options;
	private Properties properties;
	private boolean dynamicProvider;
	
	private String decodedPayload;
	private byte[] decodedPayloadAsByte;
	
	private JsonWebKeys jsonWebKeys; // dove prendere la chiave privata
	private KeyStore keyStore; // dove prendere la chiave privata
	private Map<String, String> keystore_mapAliasPassword;
	private KeyStore trustStoreVerificaCertificatiX509; // per verificare i certificati presenti nell'header 
	public void setTrustStoreVerificaCertificatiX509(KeyStore trustStoreVerificaCertificatiX509) {
		this.trustStoreVerificaCertificatiX509 = trustStoreVerificaCertificatiX509;
	}
	public KeyStore getTrustStoreVerificaCertificatiX509() {
		return this.trustStoreVerificaCertificatiX509;
	}

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
	
	
	public JsonDecrypt(Properties props, JWTOptions options) throws UtilsException{
		try {
			this.dynamicProvider = JsonUtils.isDynamicProvider(props); // rimuove l'alias
			if(this.dynamicProvider) {
				this.properties = props;
			}
			else {
				this.provider = this.loadProviderFromProperties(props, null); // nel caso di jceks deve essere definito l'algoritmo del contenuto se non e' dinamico
			}
			this.options=options;		
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	private JweDecryptionProvider loadProviderFromProperties(Properties props, org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgorithm) throws Exception {
		File fTmp = null;
		try {
			fTmp = JsonUtils.normalizeProperties(props); // in caso di url http viene letta la risorsa remota e salvata in tmp
			/*java.util.Enumeration<?> en = props.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				System.out.println("- ["+key+"] ["+props.getProperty(key)+"]");
			}*/
			
			JweDecryptionProvider provider = null;
			if(contentAlgorithm!=null) {
				provider = JsonUtils.getJweDecryptionProvider(props, contentAlgorithm);
			}
			else {
				provider = JsonUtils.getJweDecryptionProvider(props);
			}
			if(provider==null) {
				KeyAlgorithm keyAlgorithm = JweUtils.getKeyEncryptionAlgorithm(props, null);
				if (KeyAlgorithm.DIRECT.equals(keyAlgorithm)) {
					provider = JsonUtils.getJweDecryptionProviderFromJWKSymmetric(props, null);
				}
				else {
					try {
						provider = JweUtils.loadDecryptionProvider(props, JsonUtils.newMessage(), null); // lasciare null come secondo parametro senno non funziona il decrypt senza keyEncoding
					}catch(JweException jweExc) {
						if(jweExc!=null && jweExc.getMessage()!=null && jweExc.getMessage().contains("NO_ENCRYPTOR")) {
							// caso in cui la chiave privata PKCS11 non e' stata mappata in un PrivateKeyDecryptionProvider
							provider = JsonUtils.getJweAsymmetricDecryptionProvider(props);
							if(provider==null) {
								// rilancio eccezione precedente
								throw jweExc;
							}
						}
					}
				}
			}
			if(provider==null) {
				throw new Exception("JweDecryptionProvider provider not found");
			}
			
			try {
				Certificate cert = JsonUtils.getCertificateKey(props);
				if(cert!=null && cert instanceof X509Certificate) {
					this.x509Certificate = (X509Certificate) cert;
				}
			}catch(Throwable t) {
				// ignore
			}
			
			return provider;
			 
		}finally {
			try {
				if(fTmp!=null) {
					if(!fTmp.delete()) {
						// ignore
					}
				}
			}catch(Throwable t) {}
		}
	}
	
	public JsonDecrypt(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, JWTOptions options) throws UtilsException{
		this(new KeyStore(keystore), false, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, options);
	}
	public JsonDecrypt(KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, JWTOptions options) throws UtilsException{
		this(keystore, false, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, options);
	}
	public JsonDecrypt(java.security.KeyStore keystore, boolean secretKey, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, JWTOptions options) throws UtilsException{
		this(new KeyStore(keystore), secretKey, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, options);
	}
	public JsonDecrypt(KeyStore keystore, boolean secretKey, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, JWTOptions options) throws UtilsException{
		try {
			this.options=options;
			
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			if(secretKey) {
				this.provider = JweUtils.createJweDecryptionProvider( (SecretKey) keystore.getSecretKey(alias, passwordPrivateKey), keyAlgo, contentAlgo);
			}else {
				PrivateKey privateKey = (PrivateKey) keystore.getPrivateKey(alias, passwordPrivateKey);
				this.provider = JweUtils.createJweDecryptionProvider( privateKey, keyAlgo, contentAlgo);
				try {
					this.provider.getKeyAlgorithm();
				}catch(NullPointerException nullPointer) {
					// caso in cui la chiave privata PKCS11 non e' stata mappata in un PrivateKeyDecryptionProvider
					this.provider = null;
				}
				if(this.provider==null) {
					this.provider = JsonUtils.getJweAsymmetricDecryptionProvider(privateKey, keyAlgo, contentAlgo);
					if(this.provider==null) {
						throw new Exception("("+keystore.getKeystore().getType()+") JwsDecryptionProvider init failed; keyAlgorithm ("+keyAlgorithm+") contentAlgorithm("+contentAlgorithm+")");
					}
				}	
			}
			
			try {
				Certificate cert = keystore.getCertificate(alias);
				if(cert!=null && cert instanceof X509Certificate) {
					this.x509Certificate = (X509Certificate) cert;
				}
			}catch(Throwable t) {
				// ignore
			}
			
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	
	public JsonDecrypt(JsonWebKeys jsonWebKeys, boolean secretKey, String alias, String keyAlgorithm, String contentAlgorithm, JWTOptions options) throws UtilsException{
		this(JsonUtils.readKey(jsonWebKeys, alias),secretKey, keyAlgorithm, contentAlgorithm, options);
	}
	public JsonDecrypt(JsonWebKey jsonWebKey, boolean secretKey, String keyAlgorithm, String contentAlgorithm, JWTOptions options) throws UtilsException{
		try {
			this.options=options;
			
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			
			if(secretKey) {
				if(jsonWebKey.getAlgorithm()==null) {
					jsonWebKey.setAlgorithm(contentAlgorithm);
				}
				this.provider = JweUtils.getDirectKeyJweDecryption(JwkUtils.toSecretKey(jsonWebKey), contentAlgo);
				if(this.provider==null) {
					throw new Exception("(JsonWebKey) JwsDecryptionProvider init failed; check content algorithm ("+contentAlgorithm+")");
				}
			}else {
				this.provider = JweUtils.createJweDecryptionProvider( JwkUtils.toRSAPrivateKey(jsonWebKey), keyAlgo, contentAlgo);
			}

		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	


	public JsonDecrypt(Properties propsTrustStoreHttps, java.security.KeyStore trustStoreVerificaCertificato, 
			java.security.KeyStore keyStore, Map<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(propsTrustStoreHttps, null, 
				new KeyStore(trustStoreVerificaCertificato), 
				new KeyStore(keyStore), keystore_mapAliasPassword,
				options);
	}
	public JsonDecrypt(KeyStore trustStoreHttps, java.security.KeyStore trustStoreVerificaCertificato, 
			java.security.KeyStore keyStore, Map<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(null, trustStoreHttps, 
				new KeyStore(trustStoreVerificaCertificato), 
				new KeyStore(keyStore), keystore_mapAliasPassword,
				options);
	}
	public JsonDecrypt(Properties propsTrustStoreHttps, KeyStore trustStore, 
			KeyStore keyStore, Map<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(propsTrustStoreHttps, null, 
				trustStore, 
				keyStore, keystore_mapAliasPassword,
				options);
	}
	public JsonDecrypt(KeyStore trustStoreHttps, KeyStore trustStore, 
			KeyStore keyStore, Map<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(null, trustStoreHttps, 
				trustStore, 
				keyStore, keystore_mapAliasPassword,
				options);
	}
	public JsonDecrypt(java.security.KeyStore trustStoreVerificaCertificato, 
			java.security.KeyStore keyStore, Map<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(null, null, 
				new KeyStore(trustStoreVerificaCertificato), 
				new KeyStore(keyStore), keystore_mapAliasPassword,
				options);
	}
	public JsonDecrypt(KeyStore trustStore, 
			KeyStore keyStore, Map<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(null, null, 
				trustStore, 
				keyStore, keystore_mapAliasPassword,
				options);
	}
	private void _initDecryptHeaderJWT(Properties propsTrustStoreHttps, KeyStore trustStoreHttps,
			KeyStore trustStoreVerificaCertificato, 
			KeyStore keyStore, Map<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.properties = propsTrustStoreHttps; // le proprieta' servono per risolvere le url https
		this.trustStoreHttps = trustStoreHttps;
		this.trustStoreVerificaCertificatiX509 = trustStoreVerificaCertificato;
		this.keyStore = keyStore;
		this.keystore_mapAliasPassword = keystore_mapAliasPassword;
	}
	
	
	public JsonDecrypt(Properties propsTrustStoreHttps, JsonWebKeys jsonWebKeys, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(propsTrustStoreHttps, null,
				jsonWebKeys,
				options);
	}
	public JsonDecrypt(KeyStore trustStoreHttps, JsonWebKeys jsonWebKeys, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(null, trustStoreHttps,
				jsonWebKeys,
				options);
	}
	public JsonDecrypt(JsonWebKeys jsonWebKeys, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(null, null, 
				jsonWebKeys,
				options);
	}
	private void _initDecryptHeaderJWT(Properties propsTrustStoreHttps, KeyStore trustStoreHttps,
			JsonWebKeys jsonWebKeys, 
			JWTOptions options) throws UtilsException{
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.properties = propsTrustStoreHttps; // le proprieta' servono per risolvere le url https
		this.trustStoreHttps = trustStoreHttps;
		this.jsonWebKeys = jsonWebKeys;
	}
	
	


	public void decrypt(String jsonString) throws UtilsException{
		try {
			switch(this.options.getSerialization()) {
				case JSON: decryptJson(jsonString); break;
				case COMPACT: decryptCompact(jsonString); break;
				default: throw new UtilsException("Unsupported serialization '"+this.options.getSerialization()+"'");
			}
		}
		catch(Throwable t) {
			throw JsonUtils.convert(this.options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	

	private void decryptCompact(String jsonString) throws Exception {
		
		JweCompactConsumer consumer = new JweCompactConsumer(jsonString);
		JweHeaders jweHeaders = consumer.getJweHeaders();
		
		JweDecryptionProvider provider = getProvider(jweHeaders, null);
		
		JweDecryptionOutput output =  provider.decrypt(jsonString);
		this.decodedPayload = output.getContentText();
		this.decodedPayloadAsByte = output.getContent();
	}


	private void decryptJson(String jsonString) throws Exception {
		
		JweJsonConsumer consumer = new JweJsonConsumer(jsonString);
		JweHeaders jweHeaders = consumer.getProtectedHeader();
		JweHeaders jweUnprotectedHeaders = consumer.getSharedUnprotectedHeader();
		
		JweDecryptionProvider provider = getProvider(jweHeaders, jweUnprotectedHeaders);
		
		// con gestione recipients
//		org.apache.cxf.rs.security.jose.jwe.JweJsonEncryptionEntry entry = consumer.getRecipients().get(0);
//		JweDecryptionOutput output = consumer.decryptWith(provider, entry);
		
		// senza gestione recipients
		JweDecryptionOutput output = consumer.decryptWith(provider);
		
		this.decodedPayload = output.getContentText();
		this.decodedPayloadAsByte = output.getContent();
		
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
	
	private JweDecryptionProvider getProvider(JweHeaders jweHeaders, JweHeaders jweUnprotectedHeaders) throws Exception {
		
		JweDecryptionProvider provider = this.provider;
		if(jweHeaders==null) {
			return provider;
		}
		
		if(this.dynamicProvider) {
			//String alias = JsonUtils.readAlias(jsonString);
			String alias = jweHeaders.getKeyId();
			Properties pNew = new Properties();
			pNew.putAll(this.properties);
			//System.out.println("ALIAS ["+alias+"]");
			pNew.put(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS, alias);
			provider = loadProviderFromProperties(pNew, jweHeaders.getContentEncryptionAlgorithm());
		}
		
		if(provider==null) {
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo  = jweHeaders.getKeyEncryptionAlgorithm();
			if(keyAlgo==null && jweUnprotectedHeaders!=null) {
				keyAlgo = jweUnprotectedHeaders.getKeyEncryptionAlgorithm();
			}
			if(keyAlgo==null) {
				throw new Exception("KeyAlgorithm not found");
			}
			
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo = jweHeaders.getContentEncryptionAlgorithm();
			if(contentAlgo==null) {
				throw new Exception("ContentAlgorithm not found");
			}
			
			if(jweHeaders.getX509Chain()!=null && !jweHeaders.getX509Chain().isEmpty() && this.options.isPermitUseHeaderX5C()) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "x5c" Header Parameter defined in Section 4.1.6 of [JWS], except
				//   that the X.509 public key certificate or certificate chain [RFC5280]
				//   contains the public key to which the JWE was encrypted; this can be
				//   used to determine the private key needed to decrypt the JWE.
				try {
					byte [] cer = Base64Utilities.decode(jweHeaders.getX509Chain().get(0));
					CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
					if(this.trustStoreVerificaCertificatiX509!=null) {
						JsonUtils.validate(certificatoInfo, this.trustStoreVerificaCertificatiX509, this.crlX509, this.ocspValidatorX509, JwtHeaders.JWT_HDR_X5C, true);
					}
					provider = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
				}catch(Exception e) {
					throw new Exception("Process '"+JwtHeaders.JWT_HDR_X5C+"' error: "+e.getMessage(),e);
				}
			}
			else if(jweHeaders.getJsonWebKey()!=null && this.options.isPermitUseHeaderJWK()) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "jwk" Header Parameter defined in Section 4.1.3 of [JWS], except
				//   that the key is the public key to which the JWE was encrypted; this
				//   can be used to determine the private key needed to decrypt the JWE.
				try {
					JsonWebKey webKey = jweHeaders.getJsonWebKey();
					provider = getProviderJWK(webKey, keyAlgo, contentAlgo);
				}catch(Exception e) {
					throw new Exception("Process '"+JwtHeaders.JWT_HDR_JWK+"' error: "+e.getMessage(),e);
				}
			}
			else if(
					(jweHeaders.getX509Url()!=null && this.options.isPermitUseHeaderX5U()) 
					||
					(jweHeaders.getJsonWebKeysUrl()!=null && this.options.isPermitUseHeaderJKU())
					) {
			
				boolean x509 = true;
				String path = jweHeaders.getX509Url();
				String hdr = JwtHeaders.JWT_HDR_X5U;
				if(path==null) {
					path=jweHeaders.getJsonWebKeysUrl();
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
								httpResponse = HttpUtilities.getHTTPSResponse(path, this.trustStoreHttps.getKeystore(), this.crlHttps, this.ocspValidatorHttps);
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
						if(this.trustStoreVerificaCertificatiX509!=null) {
							JsonUtils.validate(certificatoInfo, this.trustStoreVerificaCertificatiX509, this.crlX509, this.ocspValidatorX509, JwtHeaders.JWT_HDR_X5U, true);
						}
						provider = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
					}
					else {
						JWKSet set = new JWKSet(new String(cer));
						JsonWebKeys jsonWebKeys = set.getJsonWebKeys();
						JsonWebKey jsonWebKey = null;
						if(jsonWebKeys.size()==1) {
							jsonWebKey = jsonWebKeys.getKeys().get(0);
						}
						else {
							if(jweHeaders.getKeyId()==null) {
								throw new Exception("Kid non definito e JwkSet contiene più di un certificato");
							}
							jsonWebKey = jsonWebKeys.getKey(jweHeaders.getKeyId());
						}
						if(jsonWebKey==null) {
							throw new Exception("JsonWebKey non trovata");
						}
						provider = getProviderJWK(jsonWebKey, keyAlgo, contentAlgo);
					}
				}catch(Exception e) {
					throw new Exception("Process '"+hdr+"' error: "+e.getMessage(),e);
				}
			}
			else if(jweHeaders.getKeyId()!=null && this.options.isPermitUseHeaderKID()) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "kid" Header Parameter defined in Section 4.1.4 of [JWS], except
				//   that the key hint references the public key to which the JWE was
				//   encrypted; this can be used to determine the private key needed to
				//   decrypt the JWE.  This parameter allows originators to explicitly
				//   signal a change of key to JWE recipients.
				try {
					String kid = jweHeaders.getKeyId();
					if(this.jsonWebKeys!=null) {
						JsonWebKey jsonWebKey = null;
						try {
							jsonWebKey = this.jsonWebKeys.getKey(kid);
						}catch(Exception e) {
							// key non esistente
						}
						if(jsonWebKey!=null) {
							provider = getProviderJWK(jsonWebKey, keyAlgo, contentAlgo);
						}
					}
					if(provider==null) {
						if(this.keyStore!=null) {
							if(this.keyStore.existsAlias(kid)) {
								Certificate cer = this.keyStore.getCertificate(kid);
								if(cer instanceof X509Certificate) {
									X509Certificate x509Certificate = (X509Certificate) cer;
									
									// La validazione serve per verificare la data e il crl
									if(this.trustStoreVerificaCertificatiX509!=null) {
										JsonUtils.validate(new CertificateInfo(x509Certificate, kid), this.trustStoreVerificaCertificatiX509, this.crlX509, this.ocspValidatorX509, JwtHeaders.JWT_HDR_KID, false);
									}
									else {
										JsonUtils.validate(new CertificateInfo(x509Certificate, kid), this.keyStore, this.crlX509, this.ocspValidatorX509, JwtHeaders.JWT_HDR_KID, false);
									}
									
									CertificateInfo certificatoInfo = new CertificateInfo(x509Certificate, kid);
									provider = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
								}
							}
						}
					}
				}catch(Exception e) {
					throw new Exception("Process '"+JwtHeaders.JWT_HDR_KID+"' error: "+e.getMessage(),e);
				}
			}
			else if(
					(jweHeaders.getX509ThumbprintSHA256()!=null && this.options.isPermitUseHeaderX5T_256())
					||
					(jweHeaders.getX509Thumbprint()!=null && this.options.isPermitUseHeaderX5T())
					) {
				String hdr = JwtHeaders.JWT_HDR_X5T;
				if (jweHeaders.getX509ThumbprintSHA256()!=null) {
					hdr = JwtHeaders.JWT_HDR_X5t_S256;
				}
				try {
					if(this.keyStore==null) {
						throw new Exception("KeyStore dei certificati non fornito"); 
					}
					Certificate cer = null;
					if(jweHeaders.getX509ThumbprintSHA256()!=null) {
						cer = this.keyStore.getCertificateByDigestSHA256UrlEncoded(jweHeaders.getX509ThumbprintSHA256());
					}
					else{
						cer = this.keyStore.getCertificateByDigestSHA1UrlEncoded(jweHeaders.getX509Thumbprint());
					}
					if(cer==null) {
						throw new Exception("Certificato, corrispondente al digest indicato, non trovato nel KeyStore dei certificati");
					}
					if(cer instanceof X509Certificate) {
						X509Certificate x509Certificate = (X509Certificate) cer;
						
						// La validazione serve per verificare la data e il crl
						if(this.trustStoreVerificaCertificatiX509!=null) {
							JsonUtils.validate(new CertificateInfo(x509Certificate, hdr), this.trustStoreVerificaCertificatiX509, this.crlX509, this.ocspValidatorX509, hdr, false);
						}
						else {
							JsonUtils.validate(new CertificateInfo(x509Certificate, hdr), this.keyStore, this.crlX509, this.ocspValidatorX509, hdr, false);
						}
						
						CertificateInfo certificatoInfo = new CertificateInfo(x509Certificate, "x5t");
						provider = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
					}
					else {
						throw new Exception("Certificato indicato non è nel formato X.509");
					}
				}catch(Exception e) {
					throw new Exception("Process '"+hdr+"' error: "+e.getMessage(),e);
				}
			}
			else {
				List<String> hdrNotPermitted = this.options.headersNotPermitted(jweHeaders);
				String notPermitted = "";
				if(hdrNotPermitted!=null && !hdrNotPermitted.isEmpty()) {
					notPermitted = "; header trovati ma non abilitati all'utilizzo: "+hdrNotPermitted;
				}
				throw new Exception("Non è stato trovato alcun header che consentisse di recuperare il certificato per decifrare"+notPermitted);
			}
		}
		else {
			if(this.x509Certificate!=null && this.trustStoreVerificaCertificatiX509!=null) {
				try {
					CertificateInfo certificatoInfo = new CertificateInfo(this.x509Certificate, "x509");
					JsonUtils.validate(certificatoInfo, this.trustStoreVerificaCertificatiX509, this.crlX509, this.ocspValidatorX509, null, true);
				}catch(Exception e) {
					throw new Exception(e.getMessage(),e);
				}
			}
		}
		
		return provider;
	}
	
	private JweDecryptionProvider getProviderX509(CertificateInfo certificatoInfo,
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo,
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo) throws Exception {
		if(this.keyStore==null) {
			throw new Exception("Keystore da utilizzare per il recupero dei certificati non definito");
		}
		if(this.keystore_mapAliasPassword==null) {
			throw new Exception("Mappping alias-password non definito");
		}
		this.x509Certificate = certificatoInfo.getCertificate();
		PrivateKey privateKey = null;
		Enumeration<String> aliases = this.keyStore.aliases();
		while (aliases.hasMoreElements()) {
			String alias = (String) aliases.nextElement();
			Certificate certificateCheck = this.keyStore.getCertificate(alias);
			if(certificateCheck instanceof X509Certificate) {
				X509Certificate x509CertificateCheck = (X509Certificate) certificateCheck;
				if(certificatoInfo.equals(x509CertificateCheck, true)) {
					try {
						String passwordPrivateKey = this.keystore_mapAliasPassword.get(alias);
						if(passwordPrivateKey==null) {
							throw new Exception("password non definita");
						}
						privateKey = this.keyStore.getPrivateKey(alias, passwordPrivateKey);
					}catch(Exception e) {
						throw new Exception("Chiave privata associato al certificato (alias: "+alias+") non recuperabile: "+e.getMessage(),e);
					}
				}
			}
		}
		if(privateKey==null) {
			throw new Exception("Chiave privata associato al certificato (presente in header x5c) non recuperato");
		}
		JweDecryptionProvider provider = JweUtils.createJweDecryptionProvider( privateKey, keyAlgo, contentAlgo);
		try {
			provider.getKeyAlgorithm();
		}catch(NullPointerException nullPointer) {
			// caso in cui la chiave privata PKCS11 non e' stata mappata in un PrivateKeyDecryptionProvider
			provider = null;
		}
		if(provider==null) {
			provider = JsonUtils.getJweAsymmetricDecryptionProvider(privateKey, keyAlgo, contentAlgo);
			if(provider==null) {
				throw new Exception("JwsDecryptionProvider init failed; keyAlgorithm ("+keyAlgo+") contentAlgorithm("+contentAlgo+")");
			}
		}
		return provider;

	}
	
	private JweDecryptionProvider getProviderJWK(JsonWebKey webKey,
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo,
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo) throws Exception {
		String n = webKey.getStringProperty("n");
		if(n==null) {
			throw new Exception("JsonWebKey uncorrect? 'n' not found");
		}
		if(this.jsonWebKeys==null) {
			throw new Exception("JWKSet da utilizzare per il recupero dei certificati non definito");
		}
		this.rsaPublicKey = JwkUtils.toRSAPublicKey(webKey);
		List<JsonWebKey> keys = this.jsonWebKeys.getKeys();
		if(keys==null || keys.isEmpty()) {
			throw new Exception("JWKSet da utilizzare per il recupero dei certificati vuoto");
		}
		JsonWebKey privateKey = null;
		for (JsonWebKey jsonWebKeyCheck : keys) {
			String nCheck = jsonWebKeyCheck.getStringProperty("n");
			if(nCheck!=null && nCheck.equals(n)) {
				privateKey = jsonWebKeyCheck;
				break;
			}
		}
		return JweUtils.createJweDecryptionProvider( JwkUtils.toRSAPrivateKey(privateKey), keyAlgo, contentAlgo);
	}
}
