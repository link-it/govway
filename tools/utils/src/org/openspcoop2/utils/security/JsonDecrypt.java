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
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwe.JweCompactConsumer;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionOutput;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweHeaders;
import org.apache.cxf.rs.security.jose.jwe.JweJsonConsumer;
import org.apache.cxf.rs.security.jose.jwe.JweUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
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
public class JsonDecrypt {

	private JweDecryptionProvider provider;
	private JWTOptions options;
	private Properties properties;
	private boolean dynamicProvider;
	
	private String decodedPayload;
	private byte[] decodedPayloadAsByte;
	
	private JsonWebKeys jsonWebKeys; // dove prendere la chiave privata
	private KeyStore keyStore; // dove prendere la chiave privata
	private HashMap<String, String> keystore_mapAliasPassword;
	private KeyStore trustStoreVerificaCertificatiX509; // per verificare i certificati presenti nell'header 
	
	public JsonDecrypt(Properties props, JWTOptions options) throws UtilsException{
		try {
			this.dynamicProvider = JsonUtils.isDynamicProvider(props); // rimuove l'alias
			if(this.dynamicProvider) {
				this.properties = props;
			}
			else {
				this.provider = this.loadProviderFromProperties(props);
			}
			this.options=options;		
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	private JweDecryptionProvider loadProviderFromProperties(Properties props) throws Exception {
		File fTmp = null;
		try {
			fTmp = JsonUtils.normalizeProperties(props); // in caso di url http viene letta la risorsa remota e salvata in tmp
			/*java.util.Enumeration<?> en = props.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				System.out.println("- ["+key+"] ["+props.getProperty(key)+"]");
			}*/
			return JweUtils.loadDecryptionProvider(props, JsonUtils.newMessage(), null); // lasciare null come secondo parametro senno non funziona il decrypt senza keyEncoding
		}finally {
			try {
				if(fTmp!=null) {
					fTmp.delete();
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
				this.provider = JweUtils.createJweDecryptionProvider( (PrivateKey) keystore.getPrivateKey(alias, passwordPrivateKey), keyAlgo, contentAlgo);
			}
		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	
	public JsonDecrypt(JsonWebKeys jsonWebKeys, String alias, String keyAlgorithm, String contentAlgorithm, JWTOptions options) throws UtilsException{
		this(JsonUtils.readKey(jsonWebKeys, alias), keyAlgorithm, contentAlgorithm, options);
	}
	public JsonDecrypt(JsonWebKey jsonWebKey, String keyAlgorithm, String contentAlgorithm, JWTOptions options) throws UtilsException{
		try {
			this.options=options;
			
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			this.provider = JweUtils.createJweDecryptionProvider( JwkUtils.toRSAPrivateKey(jsonWebKey), keyAlgo, contentAlgo);

		}catch(Throwable t) {
			throw JsonUtils.convert(options.getSerialization(), JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	


	public JsonDecrypt(Properties propsTrustStoreHttps, java.security.KeyStore trustStoreVerificaCertificato, 
			java.security.KeyStore keyStore, HashMap<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(propsTrustStoreHttps, new KeyStore(trustStoreVerificaCertificato), 
				new KeyStore(keyStore), keystore_mapAliasPassword,
				options);
	}
	public JsonDecrypt(Properties propsTrustStoreHttps, KeyStore trustStore, 
			KeyStore keyStore, HashMap<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(propsTrustStoreHttps, trustStore, 
				keyStore, keystore_mapAliasPassword,
				options);
	}
	public JsonDecrypt(java.security.KeyStore trustStoreVerificaCertificato, 
			java.security.KeyStore keyStore, HashMap<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(null, new KeyStore(trustStoreVerificaCertificato), 
				new KeyStore(keyStore), keystore_mapAliasPassword,
				options);
	}
	public JsonDecrypt(KeyStore trustStore, 
			KeyStore keyStore, HashMap<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(null, trustStore, 
				keyStore, keystore_mapAliasPassword,
				options);
	}
	private void _initDecryptHeaderJWT(Properties propsTrustStoreHttps, KeyStore trustStoreVerificaCertificato, 
			KeyStore keyStore, HashMap<String, String> keystore_mapAliasPassword, 
			JWTOptions options) throws UtilsException{
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.properties = propsTrustStoreHttps; // le proprieta' servono per risolvere le url https
		this.trustStoreVerificaCertificatiX509 = trustStoreVerificaCertificato;
		this.keyStore = keyStore;
		this.keystore_mapAliasPassword = keystore_mapAliasPassword;
	}
	
	public JsonDecrypt(Properties propsTrustStoreHttps, JsonWebKeys jsonWebKeys, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(propsTrustStoreHttps, jsonWebKeys,
				options);
	}
	public JsonDecrypt(JsonWebKeys jsonWebKeys, 
			JWTOptions options) throws UtilsException{
		_initDecryptHeaderJWT(null, jsonWebKeys,
				options);
	}
	private void _initDecryptHeaderJWT(Properties propsTrustStoreHttps, JsonWebKeys jsonWebKeys, 
			JWTOptions options) throws UtilsException{
		// verra usato l'header per validare ed ottenere il certificato
		this.options=options;
		this.properties = propsTrustStoreHttps; // le proprieta' servono per risolvere le url https
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
			pNew.put(JoseConstants.RSSEC_KEY_STORE_ALIAS, alias);
			provider = loadProviderFromProperties(pNew);
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
				byte [] cer = Base64Utilities.decode(jweHeaders.getX509Chain().get(0));
				CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
				if(this.trustStoreVerificaCertificatiX509!=null) {
					if(certificatoInfo.isVerified(this.trustStoreVerificaCertificatiX509)==false) {
						throw new Exception("Certificato presente nell'header '"+JwtHeaders.JWT_HDR_X5U+"' non è verificabile rispetto alle CA conosciute");
					}
				}
				provider = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
			}
			else if(jweHeaders.getJsonWebKey()!=null && this.options.isPermitUseHeaderJWK()) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "jwk" Header Parameter defined in Section 4.1.3 of [JWS], except
				//   that the key is the public key to which the JWE was encrypted; this
				//   can be used to determine the private key needed to decrypt the JWE.
				JsonWebKey webKey = jweHeaders.getJsonWebKey();
				provider = getProviderJWK(webKey, keyAlgo, contentAlgo);
			}
			else if(jweHeaders.getX509Url()!=null && this.options.isPermitUseHeaderX5U()) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "x5u" Header Parameter defined in Section 4.1.5 of [JWS], except
				//   that the X.509 public key certificate or certificate chain [RFC5280]
				//   contains the public key to which the JWE was encrypted; this can be
				//   used to determine the private key needed to decrypt the JWE.
				if(this.properties==null) {
					this.properties = new Properties();
				}
				this.properties.put(JoseConstants.RSSEC_KEY_STORE_FILE, jweHeaders.getX509Url());
				File fTmp = null;
				try {
					fTmp = JsonUtils.normalizeProperties(this.properties); // in caso di url http viene letta la risorsa remota e salvata in tmp
					byte [] cer = FileSystemUtilities.readBytesFromFile(fTmp);
					CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
					if(this.trustStoreVerificaCertificatiX509!=null) {
						if(certificatoInfo.isVerified(this.trustStoreVerificaCertificatiX509)==false) {
							throw new Exception("Certificato presente nell'header '"+JwtHeaders.JWT_HDR_X5U+"' non è verificabile rispetto alle CA conosciute");
						}
					}
					provider = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
				}finally {
					try {
						if(fTmp!=null) {
							fTmp.delete();
						}
					}catch(Throwable t) {}
				}
			}
			else if(jweHeaders.getJsonWebKeysUrl()!=null && this.options.isPermitUseHeaderJKU()) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "jku" Header Parameter defined in Section 4.1.2 of [JWS], except
				//   that the JWK Set resource contains the public key to which the JWE
				//   was encrypted; this can be used to determine the private key needed
				//   to decrypt the JWE.
				if(this.properties==null) {
					this.properties = new Properties();
				}
				this.properties.put(JoseConstants.RSSEC_KEY_STORE_FILE, jweHeaders.getJsonWebKeysUrl());
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
						if(jweHeaders.getKeyId()==null) {
							throw new Exception("Kid non definito e JwkSet contiene più di un certificato");
						}
						jsonWebKey = jsonWebKeys.getKey(jweHeaders.getKeyId());
					}
					if(jsonWebKey==null) {
						throw new Exception("JsonWebKey non trovata");
					}
					provider = getProviderJWK(jsonWebKey, keyAlgo, contentAlgo);
				}finally {
					try {
						if(fTmp!=null) {
							fTmp.delete();
						}
					}catch(Throwable t) {}
				}
			}
			else if(jweHeaders.getKeyId()!=null && this.options.isPermitUseHeaderKID()) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "kid" Header Parameter defined in Section 4.1.4 of [JWS], except
				//   that the key hint references the public key to which the JWE was
				//   encrypted; this can be used to determine the private key needed to
				//   decrypt the JWE.  This parameter allows originators to explicitly
				//   signal a change of key to JWE recipients.
				String kid = jweHeaders.getKeyId();
				if(this.jsonWebKeys!=null) {
					JsonWebKey jsonWebKey = null;
					try {
						jsonWebKey = this.jsonWebKeys.getKey(kid);
					}catch(Exception e) {}
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
								CertificateInfo certificatoInfo = new CertificateInfo(x509Certificate, kid);
								provider = getProviderX509(certificatoInfo, keyAlgo, contentAlgo);
							}
						}
					}
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
		return JweUtils.createJweDecryptionProvider( privateKey, keyAlgo, contentAlgo);
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
