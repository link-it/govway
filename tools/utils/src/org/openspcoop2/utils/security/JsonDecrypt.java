/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.utils.resources.FileSystemUtilities;
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
	private Map<String, String> keystoreMapAliasPassword;
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
	
	private CertificateValidityCheck validityCheck = CertificateValidityCheck.ENABLED; // validazione (date) del certificato utilizzato per firmare il token 	
	public void setValidityCheck(CertificateValidityCheck validityCheck) {
		this.validityCheck = validityCheck;
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
		}catch(Exception t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	private JweDecryptionProvider loadProviderFromProperties(Properties props, org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgorithm) throws UtilsException {
		File fTmp = null;
		try {
			fTmp = JsonUtils.normalizeProperties(props); // in caso di url http viene letta la risorsa remota e salvata in tmp
			/**java.util.Enumeration<?> en = props.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				System.out.println("- ["+key+"] ["+props.getProperty(key)+"]");
			}*/
			
			JweDecryptionProvider providerBuild = buildProviderFromProperties(props, contentAlgorithm);
			
			try {
				Certificate cert = JsonUtils.getCertificateKey(props);
				if(cert instanceof X509Certificate) {
					this.x509Certificate = (X509Certificate) cert;
				}
			}catch(Exception t) {
				// ignore
			}
			
			return providerBuild;
			 
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	private JweDecryptionProvider buildProviderFromProperties(Properties props, org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgorithm) throws UtilsException {
		JweDecryptionProvider providerBuild = null;
		if(contentAlgorithm!=null) {
			providerBuild = JsonUtils.getJweDecryptionProvider(props, contentAlgorithm);
		}
		else {
			providerBuild = JsonUtils.getJweDecryptionProvider(props);
		}
		if(providerBuild==null) {
			providerBuild = buildProviderFromProperties(props);
		}
		if(providerBuild==null) {
			throw new UtilsException("JweDecryptionProvider provider not found");
		}
		return providerBuild;
	}
	private JweDecryptionProvider buildProviderFromProperties(Properties props) throws UtilsException {
		JweDecryptionProvider providerBuild = null;
		KeyAlgorithm keyAlgorithm = JweUtils.getKeyEncryptionAlgorithm(props, null);
		if (KeyAlgorithm.DIRECT.equals(keyAlgorithm)) {
			providerBuild = JsonUtils.getJweDecryptionProviderFromJWKSymmetric(props, null);
		}
		else {
			try {
				providerBuild = JweUtils.loadDecryptionProvider(props, JsonUtils.newMessage(), null); // lasciare null come secondo parametro senno non funziona il decrypt senza keyEncoding
			}catch(JweException jweExc) {
				if(jweExc.getMessage()!=null && jweExc.getMessage().contains("NO_ENCRYPTOR")) {
					// caso in cui la chiave privata PKCS11 non e' stata mappata in un PrivateKeyDecryptionProvider
					providerBuild = JsonUtils.getJweAsymmetricDecryptionProvider(props);
					if(providerBuild==null) {
						// rilancio eccezione precedente
						throw jweExc;
					}
				}
			}
		}
		return providerBuild;
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
				
				if (KeyAlgorithm.DIRECT.equals(keyAlgo)) {
					this.provider = JweUtils.getDirectKeyJweDecryption(keystore.getSecretKey(alias, passwordPrivateKey), contentAlgo);
				}
				else {
					this.provider = JweUtils.createJweDecryptionProvider( keystore.getSecretKey(alias, passwordPrivateKey), keyAlgo, contentAlgo);
				}

			}else {
				PrivateKey privateKey = keystore.getPrivateKey(alias, passwordPrivateKey);
				this.provider = JweUtils.createJweDecryptionProvider( privateKey, keyAlgo, contentAlgo);
				checkKeyAlgorithm();
				if(this.provider==null) {
					this.provider = JsonUtils.getJweAsymmetricDecryptionProvider(privateKey, keyAlgo, contentAlgo);
					if(this.provider==null) {
						throw new UtilsException("("+keystore.getKeystore().getType()+") JwsDecryptionProvider init failed; keyAlgorithm ("+keyAlgorithm+") contentAlgorithm("+contentAlgorithm+")");
					}
				}	
			}
			
			setSafeCertificate(keystore, alias);
			
		}catch(Exception t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	private void setSafeCertificate(KeyStore keystore, String alias) {
		try {
			Certificate cert = keystore.getCertificate(alias);
			if(cert instanceof X509Certificate) {
				this.x509Certificate = (X509Certificate) cert;
			}
		}catch(Exception t) {
			// ignore
		}
	}
	private void checkKeyAlgorithm() {
		try {
			this.provider.getKeyAlgorithm();
		}catch(NullPointerException nullPointer) {
			// caso in cui la chiave privata PKCS11 non e' stata mappata in un PrivateKeyDecryptionProvider
			this.provider = null;
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
				if (KeyAlgorithm.DIRECT.equals(keyAlgo)) {
					this.provider = JweUtils.getDirectKeyJweDecryption(JwkUtils.toSecretKey(jsonWebKey), contentAlgo);
				}
				else {
					this.provider = JweUtils.createJweDecryptionProvider( JwkUtils.toSecretKey(jsonWebKey), keyAlgo, contentAlgo);
				}
				if(this.provider==null) {
					throw new UtilsException("(JsonWebKey) JwsDecryptionProvider init failed; check content algorithm ("+contentAlgorithm+")");
				}
			}else {
				this.provider = JweUtils.createJweDecryptionProvider( JwkUtils.toRSAPrivateKey(jsonWebKey), keyAlgo, contentAlgo);
			}

		}catch(Exception t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	


	public JsonDecrypt(Properties propsTrustStoreHttps, java.security.KeyStore trustStoreVerificaCertificato, 
			java.security.KeyStore keyStore, Map<String, String> keystoreMapAliasPassword, 
			JWTOptions options) {
		initDecryptHeaderJWTEngine(propsTrustStoreHttps, null, 
				new KeyStore(trustStoreVerificaCertificato), 
				new KeyStore(keyStore), keystoreMapAliasPassword,
				options);
	}
	public JsonDecrypt(KeyStore trustStoreHttps, java.security.KeyStore trustStoreVerificaCertificato, 
			java.security.KeyStore keyStore, Map<String, String> keystoreMapAliasPassword, 
			JWTOptions options) {
		initDecryptHeaderJWTEngine(null, trustStoreHttps, 
				new KeyStore(trustStoreVerificaCertificato), 
				new KeyStore(keyStore), keystoreMapAliasPassword,
				options);
	}
	public JsonDecrypt(Properties propsTrustStoreHttps, KeyStore trustStore, 
			KeyStore keyStore, Map<String, String> keystoreMapAliasPassword, 
			JWTOptions options) {
		initDecryptHeaderJWTEngine(propsTrustStoreHttps, null, 
				trustStore, 
				keyStore, keystoreMapAliasPassword,
				options);
	}
	public JsonDecrypt(KeyStore trustStoreHttps, KeyStore trustStore, 
			KeyStore keyStore, Map<String, String> keystoreMapAliasPassword, 
			JWTOptions options) {
		initDecryptHeaderJWTEngine(null, trustStoreHttps, 
				trustStore, 
				keyStore, keystoreMapAliasPassword,
				options);
	}
	public JsonDecrypt(java.security.KeyStore trustStoreVerificaCertificato, 
			java.security.KeyStore keyStore, Map<String, String> keystoreMapAliasPassword, 
			JWTOptions options) {
		initDecryptHeaderJWTEngine(null, null, 
				new KeyStore(trustStoreVerificaCertificato), 
				new KeyStore(keyStore), keystoreMapAliasPassword,
				options);
	}
	public JsonDecrypt(KeyStore trustStore, 
			KeyStore keyStore, Map<String, String> keystoreMapAliasPassword, 
			JWTOptions options) {
		initDecryptHeaderJWTEngine(null, null, 
				trustStore, 
				keyStore, keystoreMapAliasPassword,
				options);
	}
	private void initDecryptHeaderJWTEngine(Properties propsTrustStoreHttps, KeyStore trustStoreHttps,
			KeyStore trustStoreVerificaCertificato, 
			KeyStore keyStore, Map<String, String> keystoreMapAliasPassword, 
			JWTOptions options) {
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.properties = propsTrustStoreHttps; // le proprieta' servono per risolvere le url https
		this.trustStoreHttps = trustStoreHttps;
		this.trustStoreVerificaCertificatiX509 = trustStoreVerificaCertificato;
		this.keyStore = keyStore;
		this.keystoreMapAliasPassword = keystoreMapAliasPassword;
	}
	
	
	public JsonDecrypt(Properties propsTrustStoreHttps, JsonWebKeys jsonWebKeys, 
			JWTOptions options) {
		initDecryptHeaderJWTEngine(propsTrustStoreHttps, null,
				jsonWebKeys,
				options);
	}
	public JsonDecrypt(KeyStore trustStoreHttps, JsonWebKeys jsonWebKeys, 
			JWTOptions options) {
		initDecryptHeaderJWTEngine(null, trustStoreHttps,
				jsonWebKeys,
				options);
	}
	public JsonDecrypt(JsonWebKeys jsonWebKeys, 
			JWTOptions options) {
		initDecryptHeaderJWTEngine(null, null, 
				jsonWebKeys,
				options);
	}
	private void initDecryptHeaderJWTEngine(Properties propsTrustStoreHttps, KeyStore trustStoreHttps,
			JsonWebKeys jsonWebKeys, 
			JWTOptions options) {
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
		catch(Exception t) {
			throw JsonUtils.convert(this.options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	

	private void decryptCompact(String jsonString) throws UtilsException {
		
		JweCompactConsumer consumer = new JweCompactConsumer(jsonString);
		JweHeaders jweHeaders = consumer.getJweHeaders();
		
		JweDecryptionProvider providerBuild = getProvider(jweHeaders, null);
		
		JweDecryptionOutput output =  providerBuild.decrypt(jsonString);
		this.decodedPayload = output.getContentText();
		this.decodedPayloadAsByte = output.getContent();
	}


	private void decryptJson(String jsonString) throws UtilsException {
		
		JweJsonConsumer consumer = new JweJsonConsumer(jsonString);
		JweHeaders jweHeaders = consumer.getProtectedHeader();
		JweHeaders jweUnprotectedHeaders = consumer.getSharedUnprotectedHeader();
		
		JweDecryptionProvider providerBuild = getProvider(jweHeaders, jweUnprotectedHeaders);
		
		// con gestione recipients
/**		org.apache.cxf.rs.security.jose.jwe.JweJsonEncryptionEntry entry = consumer.getRecipients().get(0);
//		JweDecryptionOutput output = consumer.decryptWith(provider, entry);*/
		
		// senza gestione recipients
		JweDecryptionOutput output = consumer.decryptWith(providerBuild);
		
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
	private String kid;
	public X509Certificate getX509Certificate() {
		return this.x509Certificate;
	}
	public RSAPublicKey getRsaPublicKey() {
		return this.rsaPublicKey;
	}
	public String getKid() {
		return this.kid;
	}
	
	private JweDecryptionProvider getProvider(JweHeaders jweHeaders, JweHeaders jweUnprotectedHeaders) throws UtilsException {
		
		JweDecryptionProvider providerBuild = this.provider;
		if(jweHeaders==null) {
			return providerBuild;
		}
		
		if(this.dynamicProvider) {
			/**String alias = JsonUtils.readAlias(jsonString);*/
			String alias = jweHeaders.getKeyId();
			Properties pNew = new Properties();
			pNew.putAll(this.properties);
			/**System.out.println("ALIAS ["+alias+"]");*/
			pNew.put(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS, alias);
			providerBuild = loadProviderFromProperties(pNew, jweHeaders.getContentEncryptionAlgorithm());
		}
		
		if(providerBuild==null) {
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo  = jweHeaders.getKeyEncryptionAlgorithm();
			if(keyAlgo==null && jweUnprotectedHeaders!=null) {
				keyAlgo = jweUnprotectedHeaders.getKeyEncryptionAlgorithm();
			}
			if(keyAlgo==null) {
				throw new UtilsException("KeyAlgorithm not found");
			}
			
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo = jweHeaders.getContentEncryptionAlgorithm();
			if(contentAlgo==null) {
				throw new UtilsException("ContentAlgorithm not found");
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
						JsonUtils.validate(certificatoInfo, 
								this.trustStoreVerificaCertificatiX509, this.crlX509, this.ocspValidatorX509, JwtHeaders.JWT_HDR_X5C, true,
								this.validityCheck);
					}
					providerBuild = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
				}catch(Exception e) {
					throw new UtilsException("Process '"+JwtHeaders.JWT_HDR_X5C+"' error: "+e.getMessage(),e);
				}
			}
			else if(jweHeaders.getJsonWebKey()!=null && this.options.isPermitUseHeaderJWK()) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "jwk" Header Parameter defined in Section 4.1.3 of [JWS], except
				//   that the key is the public key to which the JWE was encrypted; this
				//   can be used to determine the private key needed to decrypt the JWE.
				try {
					JsonWebKey webKey = jweHeaders.getJsonWebKey();
					providerBuild = getProviderJWK(webKey, keyAlgo, contentAlgo);
				}catch(Exception e) {
					throw new UtilsException("Process '"+JwtHeaders.JWT_HDR_JWK+"' error: "+e.getMessage(),e);
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
							throw new UtilsException("Resource '"+path+"' unavailable: "+e.getMessage(),e);
						}
						if(httpResponse==null || httpResponse.getContent()==null) {
							throw new UtilsException("Resource '"+path+"' unavailable");
						}
						if(httpResponse.getResultHTTPOperation()!=200) {
							throw new UtilsException("Retrieve '"+path+"' failed (returnCode:"+httpResponse.getResultHTTPOperation()+")");
						}
						cer = httpResponse.getContent();
					}
					if(cer==null) {
						throw new UtilsException("Resource '"+path+"' unavailable");
					}
					
					if(x509) {
						CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
						if(this.trustStoreVerificaCertificatiX509!=null) {
							JsonUtils.validate(certificatoInfo, 
									this.trustStoreVerificaCertificatiX509, this.crlX509, this.ocspValidatorX509, JwtHeaders.JWT_HDR_X5U, true,
									this.validityCheck);
						}
						providerBuild = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
					}
					else {
						JWKSet set = new JWKSet(new String(cer));
						JsonWebKeys jsonWebKeysBuild = set.getJsonWebKeys();
						JsonWebKey jsonWebKey = null;
						if(jsonWebKeysBuild.size()==1) {
							jsonWebKey = jsonWebKeysBuild.getKeys().get(0);
						}
						else {
							if(jweHeaders.getKeyId()==null) {
								throw new UtilsException("Kid non definito e JwkSet contiene più di un certificato");
							}
							jsonWebKey = jsonWebKeysBuild.getKey(jweHeaders.getKeyId());
						}
						if(jsonWebKey==null) {
							throw new UtilsException("JsonWebKey non trovata");
						}
						providerBuild = getProviderJWK(jsonWebKey, keyAlgo, contentAlgo);
					}
				}catch(Exception e) {
					throw new UtilsException("Process '"+hdr+"' error: "+e.getMessage(),e);
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
					this.kid = jweHeaders.getKeyId();
					if(this.jsonWebKeys!=null) {
						JsonWebKey jsonWebKey = null;
						try {
							jsonWebKey = this.jsonWebKeys.getKey(this.kid);
						}catch(Exception e) {
							// key non esistente
						}
						if(jsonWebKey!=null) {
							providerBuild = getProviderJWK(jsonWebKey, keyAlgo, contentAlgo);
						}
					}
					if(providerBuild==null &&
						this.keyStore!=null &&
						this.keyStore.existsAlias(this.kid)) {
						Certificate cer = this.keyStore.getCertificate(this.kid);
						if(cer instanceof X509Certificate) {
							X509Certificate x509CertificateBuild = (X509Certificate) cer;
							
							// La validazione serve per verificare la data e il crl
							if(this.trustStoreVerificaCertificatiX509!=null) {
								JsonUtils.validate(new CertificateInfo(x509CertificateBuild, this.kid), 
										this.trustStoreVerificaCertificatiX509, this.crlX509, this.ocspValidatorX509, JwtHeaders.JWT_HDR_KID, false,
										this.validityCheck);
							}
							else {
								JsonUtils.validate(new CertificateInfo(x509CertificateBuild, this.kid), 
										this.keyStore, this.crlX509, this.ocspValidatorX509, JwtHeaders.JWT_HDR_KID, false,
										this.validityCheck);
							}
							
							CertificateInfo certificatoInfo = new CertificateInfo(x509CertificateBuild, this.kid);
							providerBuild = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
						}
					}
				}catch(Exception e) {
					throw new UtilsException("Process '"+JwtHeaders.JWT_HDR_KID+"' error: "+e.getMessage(),e);
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
						throw new UtilsException("KeyStore dei certificati non fornito"); 
					}
					Certificate cer = null;
					if(jweHeaders.getX509ThumbprintSHA256()!=null) {
						cer = this.keyStore.getCertificateByDigestSHA256UrlEncoded(jweHeaders.getX509ThumbprintSHA256());
					}
					else{
						cer = this.keyStore.getCertificateByDigestSHA1UrlEncoded(jweHeaders.getX509Thumbprint());
					}
					if(cer==null) {
						throw new UtilsException("Certificato, corrispondente al digest indicato, non trovato nel KeyStore dei certificati");
					}
					if(cer instanceof X509Certificate) {
						X509Certificate x509CertificateBuild = (X509Certificate) cer;
						
						// La validazione serve per verificare la data e il crl
						if(this.trustStoreVerificaCertificatiX509!=null) {
							JsonUtils.validate(new CertificateInfo(x509CertificateBuild, hdr), 
									this.trustStoreVerificaCertificatiX509, this.crlX509, this.ocspValidatorX509, hdr, false,
									this.validityCheck);
						}
						else {
							JsonUtils.validate(new CertificateInfo(x509CertificateBuild, hdr), 
									this.keyStore, this.crlX509, this.ocspValidatorX509, hdr, false,
									this.validityCheck);
						}
						
						CertificateInfo certificatoInfo = new CertificateInfo(x509CertificateBuild, "x5t");
						providerBuild = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
					}
					else {
						throw new UtilsException("Certificato indicato non è nel formato X.509");
					}
				}catch(Exception e) {
					throw new UtilsException("Process '"+hdr+"' error: "+e.getMessage(),e);
				}
			}
			else {
				List<String> hdrNotPermitted = this.options.headersNotPermitted(jweHeaders);
				String notPermitted = "";
				if(hdrNotPermitted!=null && !hdrNotPermitted.isEmpty()) {
					notPermitted = "; header trovati ma non abilitati all'utilizzo: "+hdrNotPermitted;
				}
				throw new UtilsException("Non è stato trovato alcun header che consentisse di recuperare il certificato per decifrare"+notPermitted);
			}
		}
		else {
			if(this.x509Certificate!=null && this.trustStoreVerificaCertificatiX509!=null) {
				try {
					CertificateInfo certificatoInfo = new CertificateInfo(this.x509Certificate, "x509");
					JsonUtils.validate(certificatoInfo, 
							this.trustStoreVerificaCertificatiX509, this.crlX509, this.ocspValidatorX509, null, true,
							this.validityCheck);
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
		
		return providerBuild;
	}
	
	private JweDecryptionProvider getProviderX509(CertificateInfo certificatoInfo,
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo,
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo) throws UtilsException {
		if(this.keyStore==null) {
			throw new UtilsException("Keystore da utilizzare per il recupero dei certificati non definito");
		}
		if(this.keystoreMapAliasPassword==null) {
			throw new UtilsException("Mappping alias-password non definito");
		}
		this.x509Certificate = certificatoInfo.getCertificate();
		PrivateKey privateKey = null;
		Enumeration<String> aliases = this.keyStore.aliases();
		while (aliases.hasMoreElements()) {
			String alias = aliases.nextElement();
			Certificate certificateCheck = this.keyStore.getCertificate(alias);
			if(certificateCheck instanceof X509Certificate) {
				X509Certificate x509CertificateCheck = (X509Certificate) certificateCheck;
				if(certificatoInfo.equals(x509CertificateCheck, true)) {
					try {
						String passwordPrivateKey = this.keystoreMapAliasPassword.get(alias);
						if(passwordPrivateKey==null) {
							throw new UtilsException("password non definita");
						}
						privateKey = this.keyStore.getPrivateKey(alias, passwordPrivateKey);
					}catch(Exception e) {
						throw new UtilsException("Chiave privata associato al certificato (alias: "+alias+") non recuperabile: "+e.getMessage(),e);
					}
				}
			}
		}
		if(privateKey==null) {
			throw new UtilsException("Chiave privata associato al certificato (presente in header x5c) non recuperato");
		}
		JweDecryptionProvider providerBuild = JweUtils.createJweDecryptionProvider( privateKey, keyAlgo, contentAlgo);
		try {
			providerBuild.getKeyAlgorithm();
		}catch(NullPointerException nullPointer) {
			// caso in cui la chiave privata PKCS11 non e' stata mappata in un PrivateKeyDecryptionProvider
			providerBuild = null;
		}
		if(providerBuild==null) {
			providerBuild = JsonUtils.getJweAsymmetricDecryptionProvider(privateKey, keyAlgo, contentAlgo);
			if(providerBuild==null) {
				throw new UtilsException("JwsDecryptionProvider init failed; keyAlgorithm ("+keyAlgo+") contentAlgorithm("+contentAlgo+")");
			}
		}
		return providerBuild;

	}
	
	private JweDecryptionProvider getProviderJWK(JsonWebKey webKey,
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo,
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo) throws UtilsException {
		String n = webKey.getStringProperty("n");
		if(n==null) {
			throw new UtilsException("JsonWebKey uncorrect? 'n' not found");
		}
		if(this.jsonWebKeys==null) {
			throw new UtilsException("JWKSet da utilizzare per il recupero dei certificati non definito");
		}
		this.rsaPublicKey = JwkUtils.toRSAPublicKey(webKey);
		List<JsonWebKey> keys = this.jsonWebKeys.getKeys();
		if(keys==null || keys.isEmpty()) {
			throw new UtilsException("JWKSet da utilizzare per il recupero dei certificati vuoto");
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
