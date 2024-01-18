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

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.security.Key;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.common.KeyManagementUtils;
import org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm;
import org.apache.cxf.rs.security.jose.jwe.ContentEncryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweEncryption;
import org.apache.cxf.rs.security.jose.jwe.JweEncryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweException;
import org.apache.cxf.rs.security.jose.jwe.JweHeaders;
import org.apache.cxf.rs.security.jose.jwe.JweUtils;
import org.apache.cxf.rs.security.jose.jwe.KeyEncryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.WrappedKeyDecryptionAlgorithm;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jwk.KeyOperation;
import org.apache.cxf.rs.security.jose.jws.JwsException;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureProvider;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rs.security.jose.jws.PrivateKeyJwsSignatureProvider;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.IOCSPValidator;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * JsonUtils
 * 
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JsonUtils {
	
	private JsonUtils(){}

	public static Message newMessage() {
		Message m = new MessageImpl();
		Exchange ex = new ExchangeImpl();
		m.setExchange(ex);
		return m;
	}
	
	private static final String KEYSTORE_PREFIX_FILE = "keystore";
	
	private static final String JCEKS_SIGNATURE_ALGO_UNDEFINED = "(JCEKS) Signature Algorithm undefined";
	private static final String PKCS11_SIGNATURE_ALGO_UNDEFINED = "(SecretKey PKCS11) Signature Algorithm undefined";
	private static final String SECRET_SIGNATURE_ALGO_UNDEFINED = "(Secret) Signature Algorithm undefined";
	
	private static final String JCEKS_CONTENT_ALGO_UNDEFINED = "(JCEKS) Content Algorithm undefined";
	
	public static final boolean SIGNATURE = true;
	public static final boolean ENCRYPT = false;
	public static final boolean DECRYPT = false;
	public static final boolean SENDER = true;
	public static final boolean RECEIVER = false;
	public static UtilsException convert(JOSESerialization serialization, boolean signature, boolean roleSender, Throwable t) {
		
		StringBuilder bf = new StringBuilder();
		if(serialization!=null) {
			bf.append("[").append(serialization.name()).append("] ");
		}
		
		if(t instanceof JwsException) {
			JwsException exc = (JwsException) t;
			if(exc.getError()==null) {
				if(roleSender) {
					bf.append("Signature failure");
				}
				else {
					bf.append("Signature verification failure");
				}
			}			
			else {
				bf.append(exc.getError().name());
			}
			if(exc.getMessage()==null && exc.getCause()==null && exc.getLocalizedMessage()==null) {
				return new UtilsException(bf.toString(),t);
			}
		}
		else if(t instanceof JweException) {
			JweException exc = (JweException) t;
			if(exc.getError()==null) {
				if(roleSender) {
					bf.append("Encrypt failure");
				}
				else {
					bf.append("Decrypt failure");
				}
			}			
			else {
				bf.append(exc.getError().name());
			}
			if(exc.getMessage()==null && exc.getCause()==null && exc.getLocalizedMessage()==null) {
				return new UtilsException(bf.toString(),t);
			}
		}
		else if(signature) {
			if(roleSender) {
				bf.append("Signature failure");
			}
			else {
				bf.append("Signature verification failure");
			}
		}
		else {
			if(roleSender) {
				bf.append("Encrypt failure");
			}
			else {
				bf.append("Decrypt failure");
			}
		}
				
		String msg = Utilities.getInnerNotEmptyMessageException(t).getMessage();
		
		Throwable innerExc = Utilities.getLastInnerException(t);
		String innerMsg = null;
		if(innerExc!=null){
			innerMsg = innerExc.getMessage();
		}
		
		String messaggio = null;
		if(msg!=null && !"".equals(msg) && !"null".equals(msg)) {
			messaggio = msg+"";
			if(innerMsg!=null && !"".equals(innerMsg) && !"null".equals(innerMsg) && !innerMsg.equals(msg) &&
				!messaggio.contains(innerMsg)) {
				messaggio = messaggio + " ; " + innerMsg;
			}
		}
		else{
			if(innerMsg!=null && !"".equals(innerMsg) && !"null".equals(innerMsg)) {
				messaggio = innerMsg;
			}
		}
		
		if(messaggio!=null) {
			bf.append(": ");
			bf.append(messaggio);
		}
		return new UtilsException(bf.toString(),t);
	}
	
	public static byte[] readKeystoreFromURI(Properties props) throws UtilsException {
		
		String propertyKeystoreName = RSSecurityConstants.RSSEC_KEY_STORE_FILE;
		String path = props.getProperty(propertyKeystoreName);
		byte[]content = null;
		if(path!=null && (path.startsWith("http") || path.startsWith("https"))) {
			HttpResponse httpResponse = null;
			String trustStoreProperty =  RSSecurityConstants.RSSEC_KEY_STORE_FILE+".ssl";
			String trustStorePasswordProperty =  RSSecurityConstants.RSSEC_KEY_STORE_PSWD+".ssl";
			String trustStoreTypeProperty =  RSSecurityConstants.RSSEC_KEY_STORE_TYPE+".ssl";
			String trustStore = props.getProperty(trustStoreProperty);
			String trustStorePassword = props.getProperty(trustStorePasswordProperty);
			String trustStoreType = props.getProperty(trustStoreTypeProperty);
			if(trustStore!=null) {
				if(trustStorePassword==null) {
					throw new UtilsException("TrustStore ssl password undefined");
				}
				if(trustStoreType==null) {
					throw new UtilsException("TrustStore ssl type undefined");
				}
			}
			if( (path.startsWith("https:") && trustStore==null) || path.startsWith("http:") ) {
				/**System.out.println("http");*/
				httpResponse = HttpUtilities.getHTTPResponse(path, 60000, 10000);
			}
			else {
				/**System.out.println("https");*/
				httpResponse = HttpUtilities.getHTTPSResponse(path, 60000, 10000, trustStore, trustStorePassword, trustStoreType);
			}
			if(httpResponse==null || httpResponse.getContent()==null) {
				throw new UtilsException("Keystore '"+path+"' unavailable");
			}
			if(httpResponse.getResultHTTPOperation()!=200) {
				throw new UtilsException("Retrieve keystore '"+path+"' failed (returnCode:"+httpResponse.getResultHTTPOperation()+")");
			}
			content = httpResponse.getContent();
		}
		else if(path!=null && (path.startsWith("file"))){
			try {
				File f = new File(new URI(path));
				content = FileSystemUtilities.readBytesFromFile(f);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		
		return content;
	}
	
	public static File normalizeProperties(Properties props) throws UtilsException {
		
		String propertyKeystoreName = RSSecurityConstants.RSSEC_KEY_STORE_FILE;
		byte[] content = readKeystoreFromURI(props);
		
		File fTmp = null;
		if(content!=null) {
		
			String tipo = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(tipo==null) {
				tipo = KeystoreType.JKS.getNome();
			}
			
			try {
				fTmp = FileSystemUtilities.createTempFile(KEYSTORE_PREFIX_FILE, "."+tipo);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
			FileSystemUtilities.writeFile(fTmp, content);
			props.remove(propertyKeystoreName);
			props.put(propertyKeystoreName, fTmp.getAbsolutePath());
			
		}
		
		return fTmp;
	}
	
	public static boolean isDynamicProvider(Properties props) {
		String alias = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS);
		if("*".equalsIgnoreCase(alias)) {
			props.remove(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS);
			return true;
		}
		return false;
	}
	
	public static String readAlias(String jsonString) throws UtilsException {
		if(!jsonString.contains(".")) {
			throw new UtilsException("Invalid format (expected COMPACT)");
		}
		String [] tmp = jsonString.split("\\.");
		byte[] header = Base64Utilities.decode(tmp[0].trim());
		JsonNode node = JSONUtils.getInstance().getAsNode(header).get("kid");
		if(node==null) {
			throw new UtilsException("Claim 'kid' not found");
		}
		String kid = node.asText();
		if(kid==null) {
			throw new UtilsException("Claim 'kid' without value");
		}
		return kid;
	}

	public static JsonWebKey readKey(JsonWebKeys jsonWebKeys, String alias) throws UtilsException {
		if(alias==null) {
			throw new UtilsException("Alias unknonw");
		}
		JsonWebKey jsonWebKey = jsonWebKeys.getKey(alias);
		if(jsonWebKey==null) {
			throw new UtilsException("Key with alias '"+alias+"' unknonw");
		}
		return jsonWebKey;
	}
	
	public static KeyStore getKeyStore(Properties props) throws UtilsException {
		Object oKeystore = props.get(RSSecurityConstants.RSSEC_KEY_STORE);
		if(oKeystore instanceof KeyStore) {
			return (KeyStore) oKeystore;
		}
		else if(oKeystore instanceof java.security.KeyStore) {
			return new KeyStore((java.security.KeyStore) oKeystore);
		}
		else {
			String fileK = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
			if(fileK!=null) {
				String password = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_PSWD);
				String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
				
				if(password==null || "".equals(password)){
					throw new UtilsException("Keystore password undefined");
				}
				if(type==null || "".equals(type)){
					type = KeystoreType.JKS.getNome();
				}
				if(KeystoreType.JWK_SET.getNome().equalsIgnoreCase(type)
					||
					KeystoreType.PUBLIC_KEY.getNome().equalsIgnoreCase(type)
					||
					KeystoreType.KEY_PAIR.getNome().equalsIgnoreCase(type)) {
					return null;
				}
				
				File file = new File(fileK);
				if(file.exists()) {
					return new KeyStore(file.getAbsolutePath(), type, password);
				}
				else {
					InputStream is = JsonUtils.class.getResourceAsStream(fileK);
					File fTmp = null;
					try {
						if(is!=null) {
							byte[] f = Utilities.getAsByteArray(is);
							try {
								fTmp = FileSystemUtilities.createTempFile(KEYSTORE_PREFIX_FILE, ".tmp");
								FileSystemUtilities.writeFile(fTmp, f);
								return new KeyStore(fTmp.getAbsolutePath(), type, password);
							}catch(Exception e) {
								throw new UtilsException(e.getMessage(),e);
							}
						}
					}finally {
						try {
							if(is!=null) {
								is.close();
							}
						}catch(Exception e) {
							// ignore
						}
						try {
							if(fTmp!=null) {
								Files.delete(fTmp.toPath());
							}
						}catch(Exception e) {
							// delete
						}
					}
				}	
			}
		}
		return null;
	}
	public static Certificate getCertificateKey(Properties props) throws UtilsException {
		KeyStore keystore = getKeyStore(props);
		if(keystore!=null) {
			String alias = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS);
			if(alias!=null && !"".equals(alias)){
				return keystore.getCertificate(alias);
			}
		}
		return null;
	}
	
	public static SecretKeyPkcs11 getSecretKeyPKCS11(Properties props) throws UtilsException {
		if(props.containsKey(RSSecurityConstants.RSSEC_KEY_STORE_TYPE)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			
			if(KeystoreType.PKCS11.getNome().equalsIgnoreCase(type)) {
				Object oKeystore = props.get(RSSecurityConstants.RSSEC_KEY_STORE);
				if(oKeystore instanceof java.security.KeyStore) {
					java.security.KeyStore keystorePKCS11 = (java.security.KeyStore) oKeystore;
					String alias = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS);
					if(alias==null || "".equals(alias)){
						throw new UtilsException("(Secret-PKCS11) Alias key undefined");
					}				
					Key key = null;
					try {
						key = keystorePKCS11.getKey(alias, null);
					}catch(Exception e) {
						throw new UtilsException(e.getMessage(),e);
					}
					if(key instanceof SecretKey) {
						return new SecretKeyPkcs11(keystorePKCS11.getProvider(), (SecretKey) key);
					}
				}
			}
		}
		return null;
	}
	public static SecretKey getSecretKey(Properties props) throws UtilsException {
		if(props.containsKey(RSSecurityConstants.RSSEC_KEY_STORE_TYPE)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			
			if(KeystoreType.JCEKS.getNome().equalsIgnoreCase(type)) {
				Object oKeystore = props.get(RSSecurityConstants.RSSEC_KEY_STORE);
				String fileK = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
				String password = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_PSWD);
				String alias = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS);
				String passwordKey = props.getProperty(RSSecurityConstants.RSSEC_KEY_PSWD);
				
				if(oKeystore instanceof java.security.KeyStore) {
					java.security.KeyStore keystoreJCEKS = (java.security.KeyStore) oKeystore;
					
					if(alias==null || "".equals(alias)){
						throw new UtilsException("(JCEKS) Alias key undefined");
					}
					if(passwordKey==null || "".equals(passwordKey)){
						throw new UtilsException("(JCEKS) Password key undefined");
					}
					
					try {
						return (SecretKey) keystoreJCEKS.getKey(alias, passwordKey.toCharArray());
					}catch(Exception e) {
						throw new UtilsException(e.getMessage(),e);
					}
				}
				else if(fileK!=null) {
					
					if(password==null || "".equals(password)){
						throw new UtilsException("(JCEKS) Keystore password undefined");
					}
					
					File file = new File(fileK);
					KeyStore keystoreJCEKS = null;
					if(file.exists()) {
						keystoreJCEKS = new KeyStore(file.getAbsolutePath(), KeystoreType.JCEKS.getNome(), password);
					}
					else {
						InputStream is = JsonUtils.class.getResourceAsStream(fileK);
						File fTmp = null;
						try {
							if(is!=null) {
								byte[] f = Utilities.getAsByteArray(is);
								try {
									fTmp = FileSystemUtilities.createTempFile(KEYSTORE_PREFIX_FILE, ".jceks");
									FileSystemUtilities.writeFile(fTmp, f);
								}catch(Exception e) {
									throw new UtilsException(e.getMessage(),e);
								}
								keystoreJCEKS = new KeyStore(fTmp.getAbsolutePath(), KeystoreType.JCEKS.getNome(), password);
							}
						}finally {
							try {
								if(is!=null) {
									is.close();
								}
							}catch(Exception e) {
								// ignore
							}
							try {
								if(fTmp!=null) {
									java.nio.file.Files.delete(fTmp.toPath());
								}
							}catch(Exception e) {
								// delete
							}
						}
					}	
					if(keystoreJCEKS!=null) {
						
						if(alias==null || "".equals(alias)){
							throw new UtilsException("(JCEKS) Alias key undefined");
						}
						if(passwordKey==null || "".equals(passwordKey)){
							throw new UtilsException("(JCEKS) Password key undefined");
						}
						
						return keystoreJCEKS.getSecretKey(alias, passwordKey);
					}

				}
			}
		}
		return null;
	}
	public static final String RSSEC_KEY_STORE_TYPE_SECRET = "secret";
	public static String getSecret(Properties props, org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algorithm) {
		if(props.containsKey(RSSecurityConstants.RSSEC_KEY_STORE_TYPE)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(RSSEC_KEY_STORE_TYPE_SECRET.equalsIgnoreCase(type)) { // customized
				if(algorithm!=null) {
					if(algorithm.name().toLowerCase().startsWith("hs")) {
						String passwordKey = props.getProperty(RSSecurityConstants.RSSEC_KEY_PSWD);
						if(passwordKey!=null && StringUtils.isNotEmpty(passwordKey)) {
							return passwordKey;
						}
					}
				}
				else {
					if(props.containsKey(RSSecurityConstants.RSSEC_SIGNATURE_ALGORITHM)) {
						
						String algo = props.getProperty(RSSecurityConstants.RSSEC_SIGNATURE_ALGORITHM);
						if(algo!=null && algo.toLowerCase().startsWith("hs")) {
							String passwordKey = props.getProperty(RSSecurityConstants.RSSEC_KEY_PSWD);
							if(passwordKey!=null && StringUtils.isNotEmpty(passwordKey)) {
								return passwordKey;
							}
						}
						
					}			
				}
			}
		}
		return null;
	}
		
	public static JwsSignatureProvider getJwsSymmetricProvider(Properties props) throws UtilsException {
		String algorithm = props.getProperty(RSSecurityConstants.RSSEC_SIGNATURE_ALGORITHM);
		return getJwsSymmetricProvider(props, algorithm);
	}
	public static JwsSignatureProvider getJwsSymmetricProvider(Properties props, String algorithm) throws UtilsException {
		org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = null;
		if(algorithm==null || "".equals(algorithm)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(KeystoreType.JCEKS.getNome().equalsIgnoreCase(type)) {
				throw new UtilsException(JCEKS_SIGNATURE_ALGO_UNDEFINED);	
			}
		}
		else{
			algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(algorithm);
		}
		return getJwsSymmetricProvider(props, algo);
	}
	public static JwsSignatureProvider getJwsSymmetricProvider(Properties props, org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algorithm) throws UtilsException {
		
		SecretKeyPkcs11 secretKeyPkcs11 = getSecretKeyPKCS11(props);
		if(secretKeyPkcs11!=null) {
			if(algorithm==null) {
				throw new UtilsException(PKCS11_SIGNATURE_ALGO_UNDEFINED);
			}
			return new HmacJwsSignatureProviderExtended(secretKeyPkcs11, algorithm);
		}
		
		SecretKey secretKey = getSecretKey(props);
		if(secretKey!=null) {
			if(algorithm==null) {
				throw new UtilsException(JCEKS_SIGNATURE_ALGO_UNDEFINED);
			}
			byte[] encoded = secretKey.getEncoded();
			JwsSignatureProvider provider = JwsUtils.getHmacSignatureProvider(encoded, algorithm);
			if(provider==null) {
				throw new UtilsException("(JCEKS) JwsSignatureProvider init failed; check signature algorithm ("+algorithm+")");
			}
			return provider;
		}
		else {
			String secret = getSecret(props, algorithm);
			if(secret!=null) {
				if(algorithm==null) {
					throw new UtilsException(SECRET_SIGNATURE_ALGO_UNDEFINED);
				}
				byte[] encoded = secret.getBytes();
				JwsSignatureProvider provider = JwsUtils.getHmacSignatureProvider(encoded, algorithm);
				if(provider==null) {
					throw new UtilsException("(Secret) JwsSignatureProvider init failed; check signature algorithm ("+algorithm+")");
				}
				return provider;
			}
		}
		return null;
	}
	
	// ** INIZIO METODI PER OTTENERE UN JwsSignatureProvider CON PKCS11
	public static JwsSignatureProvider getJwsAsymmetricProvider(Properties props) throws UtilsException {
		String algorithm = props.getProperty(RSSecurityConstants.RSSEC_SIGNATURE_ALGORITHM);
		return getJwsAsymmetricProvider(props, algorithm);
	}
	public static JwsSignatureProvider getJwsAsymmetricProvider(Properties props, String algorithm) throws UtilsException {
		org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = null;
		if(algorithm!=null && !"".equals(algorithm)) {
			algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(algorithm);
		}
		return getJwsAsymmetricProvider(props, algo);
	}
	public static JwsSignatureProvider getJwsAsymmetricProvider(Properties props, org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algorithm) throws UtilsException {
		String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
		if(type==null || "".equals(type)) {
			type="undefined";
		}
		if(algorithm==null) {
			throw new UtilsException("("+type+") Signature Algorithm undefined");
		}
		PrivateKey pKey = KeyManagementUtils.loadPrivateKey(null, props, KeyOperation.SIGN);
		return getJwsAsymmetricProvider(pKey, algorithm);
	}
	public static JwsSignatureProvider getJwsAsymmetricProvider(PrivateKey pKey, org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algorithm) {
		return new PrivateKeyJwsSignatureProvider(pKey, algorithm);
	}
	// ** FINE METODI PER OTTENERE UN JwsSignatureProvider CON PKCS11
	
	public static JwsSignatureVerifier getJwsSignatureVerifier(Properties props) throws UtilsException {
		String algorithm = props.getProperty(RSSecurityConstants.RSSEC_SIGNATURE_ALGORITHM);
		return getJwsSignatureVerifier(props, algorithm);
	}
	public static JwsSignatureVerifier getJwsSignatureVerifier(Properties props, String algorithm) throws UtilsException {
		org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = null;
		if(algorithm==null || "".equals(algorithm)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(KeystoreType.JCEKS.getNome().equalsIgnoreCase(type)) {
				throw new UtilsException(JCEKS_SIGNATURE_ALGO_UNDEFINED);
			}
		}
		else {
			algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(algorithm);
		}
		return getJwsSignatureVerifier(props, algo);
	}
	public static JwsSignatureVerifier getJwsSignatureVerifier(Properties props, org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algorithm) throws UtilsException {
		
		SecretKey secretKey = getSecretKey(props);
		if(secretKey!=null) {
			if(algorithm==null) {
				throw new UtilsException(JCEKS_SIGNATURE_ALGO_UNDEFINED);
			}
			JwsSignatureVerifier verifier = JwsUtils.getHmacSignatureVerifier(secretKey.getEncoded(), algorithm);
			if(verifier==null) {
				throw new UtilsException("(JCEKS) JwsSignatureVerifier init failed; check signature algorithm ("+algorithm+")");
			}
			return verifier;
		}
		else {
			String secret = getSecret(props, algorithm);
			if(secret!=null) {
				if(algorithm==null) {
					throw new UtilsException(SECRET_SIGNATURE_ALGO_UNDEFINED);
				}
				byte[] encoded = secret.getBytes();
				JwsSignatureVerifier verifier = JwsUtils.getHmacSignatureVerifier(encoded, algorithm);
				if(verifier==null) {
					throw new UtilsException("(Secret) JwsSignatureVerifier init failed; check signature algorithm ("+algorithm+")");
				}
				return verifier;
			}
		}
		return null;
	}
	
	
	
	public static JweEncryptionProvider getJweEncryptionProvider(Properties props) throws UtilsException {
		String algorithm = props.getProperty(JoseConstants.RSSEC_ENCRYPTION_CONTENT_ALGORITHM);
		return getJweEncryptionProvider(props, algorithm);
	}
	public static JweEncryptionProvider getJweEncryptionProvider(Properties props, String algorithm) throws UtilsException {
		org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm algo = null;
		if(algorithm==null || "".equals(algorithm)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(KeystoreType.JCEKS.getNome().equalsIgnoreCase(type)) {
				throw new UtilsException(JCEKS_CONTENT_ALGO_UNDEFINED);
			}
		}
		else {
			algo = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(algorithm);
		}
		return getJweEncryptionProvider(props, algo);
	}
	public static JweEncryptionProvider getJweEncryptionProvider(Properties props, org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm algorithm) throws UtilsException {
		
		SecretKey secretKey = getSecretKey(props);
		if(secretKey!=null) {
			if(algorithm==null) {
				throw new UtilsException(JCEKS_CONTENT_ALGO_UNDEFINED);
			}
			JweEncryptionProvider provider = JweUtils.getDirectKeyJweEncryption(secretKey, algorithm);
			if(provider==null) {
				throw new UtilsException("(JCEKS) JweEncryptionProvider init failed; check content algorithm ("+algorithm+")");
			}
			return provider;
		}
		return null;
	}
	
	public static JweDecryptionProvider getJweDecryptionProvider(Properties props) throws UtilsException {
		String algorithm = props.getProperty(JoseConstants.RSSEC_ENCRYPTION_CONTENT_ALGORITHM);
		return getJweDecryptionProvider(props, algorithm);
	}
	public static JweDecryptionProvider getJweDecryptionProvider(Properties props, String algorithm) throws UtilsException {
		org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm algo = null;
		if(algorithm==null || "".equals(algorithm)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(KeystoreType.JCEKS.getNome().equalsIgnoreCase(type)) {
				throw new UtilsException(JCEKS_CONTENT_ALGO_UNDEFINED);
			}
		}
		else {
			algo = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(algorithm);
		}
		return getJweDecryptionProvider(props, algo);
	}
	public static JweDecryptionProvider getJweDecryptionProvider(Properties props, org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm algorithm) throws UtilsException {
		
		SecretKey secretKey = getSecretKey(props);
		if(secretKey!=null) {
			if(algorithm==null) {
				throw new UtilsException(JCEKS_CONTENT_ALGO_UNDEFINED);
			}
			JweDecryptionProvider verifier = JweUtils.getDirectKeyJweDecryption(secretKey, algorithm);
			if(verifier==null) {
				throw new UtilsException("(JCEKS) JweDecryptionProvider init failed; check content algorithm ("+algorithm+")");
			}
			return verifier;
		}
		return null;
	}
	
	// ** INIZIO METODI PER OTTENERE UN JweDecryptionProvider CON PKCS11
	public static JweDecryptionProvider getJweAsymmetricDecryptionProvider(Properties props) throws UtilsException {
		String  contentAlgorithm = props.getProperty(JoseConstants.RSSEC_ENCRYPTION_CONTENT_ALGORITHM);
		String  keyAlgorithm = props.getProperty(JoseConstants.RSSEC_ENCRYPTION_KEY_ALGORITHM);
		return getJweAsymmetricDecryptionProvider(props, keyAlgorithm, contentAlgorithm);
	}
	public static JweDecryptionProvider getJweAsymmetricDecryptionProvider(Properties props, String keyAlgorithm, String contentAlgorithm) throws UtilsException {
		org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo = null;
		if(contentAlgorithm!=null && !"".equals(contentAlgorithm)) {
			contentAlgo = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
		}
		org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo = null;
		if(keyAlgorithm!=null && !"".equals(keyAlgorithm)) {
			keyAlgo = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
		}
		return getJweAsymmetricDecryptionProvider(props, keyAlgo, contentAlgo);
	}
	public static JweDecryptionProvider getJweAsymmetricDecryptionProvider(Properties props, 
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgorithm,
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgorithm) throws UtilsException {
		String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
		if(type==null || "".equals(type)) {
			type="undefined";
		}
		if(contentAlgorithm==null) {
			throw new UtilsException("("+type+") Content Algorithm undefined");
		}
		if(keyAlgorithm==null) {
			throw new UtilsException("("+type+") Key Algorithm undefined");
		}
		PrivateKey privateKey = KeyManagementUtils.loadPrivateKey(null, props, KeyOperation.DECRYPT);
		return getJweAsymmetricDecryptionProvider(privateKey, keyAlgorithm, contentAlgorithm);
	}
	public static JweDecryptionProvider getJweAsymmetricDecryptionProvider(PrivateKey privateKey,
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgorithm,
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgorithm) {
		WrappedKeyDecryptionAlgorithm privateKeyDecryptionProvider = new WrappedKeyDecryptionAlgorithm(privateKey, keyAlgorithm, false);
		return JweUtils.createJweDecryptionProvider(privateKeyDecryptionProvider, contentAlgorithm);
	}
	// ** FINE METODI PER OTTENERE UN JweDecryptionProvider CON PKCS11
	
	public static JweEncryptionProvider getJweEncryptionProviderFromJWKSymmetric(Properties props, JweHeaders headers) {

		// Metodo copiato da JweUtils per risolvere il problema indicato sotto.
		
		Message m = PhaseInterceptorChain.getCurrentMessage();
		
        KeyEncryptionProvider keyEncryptionProvider = JweUtils.loadKeyEncryptionProvider(props, m, headers);
        ContentAlgorithm contentAlgo = JweUtils.getContentEncryptionAlgorithm(m, props, null, ContentAlgorithm.A128GCM);
        if (m != null) {
            m.put(JoseConstants.RSSEC_ENCRYPTION_CONTENT_ALGORITHM, contentAlgo.getJwaName());
        }
        ContentEncryptionProvider ctEncryptionProvider = null;
        JsonWebKey jwk = JwkUtils.loadJsonWebKey(m, props, KeyOperation.ENCRYPT);
        // FIX 
        if(jwk.getAlgorithm()==null) {
        	jwk.setAlgorithm(contentAlgo.getJwaName());
        }
        // FIX
        contentAlgo = JweUtils.getContentEncryptionAlgorithm(m, props,
        		jwk.getAlgorithm() != null ? ContentAlgorithm.getAlgorithm(jwk.getAlgorithm()) : null, contentAlgo);
        ctEncryptionProvider = JweUtils.getContentEncryptionProvider(jwk, contentAlgo);
        String compression = props.getProperty(JoseConstants.RSSEC_ENCRYPTION_ZIP_ALGORITHM);
       
        headers = headers != null ? headers : new JweHeaders();
        headers.setKeyEncryptionAlgorithm(keyEncryptionProvider.getAlgorithm());
        headers.setContentEncryptionAlgorithm(contentAlgo);
        if (compression != null) {
            headers.setZipAlgorithm(compression);
        }
        
        return new JweEncryption(keyEncryptionProvider, ctEncryptionProvider);
        
	}
	
	public static JweDecryptionProvider getJweDecryptionProviderFromJWKSymmetric(Properties props, JweHeaders headers) {

		// Metodo copiato da JweUtils per risolvere il problema indicato sotto.
		
		if(headers!=null) {
			// nop
		}
		
		Message m = PhaseInterceptorChain.getCurrentMessage();
		
		JsonWebKey jwk = JwkUtils.loadJsonWebKey(m, props, KeyOperation.DECRYPT);

        ContentAlgorithm contentAlgo = JweUtils.getContentEncryptionAlgorithm(m, props,
                                         ContentAlgorithm.getAlgorithm(jwk.getAlgorithm()),
                                         ContentAlgorithm.A128GCM);
        // FIX 
        if(jwk.getAlgorithm()==null) {
        	jwk.setAlgorithm(contentAlgo.getJwaName());
        }
        // FIX
        
        SecretKey ctDecryptionKey = JweUtils.getContentDecryptionSecretKey(jwk, contentAlgo.getJwaName());

        return JweUtils.getDirectKeyJweDecryption(ctDecryptionKey, contentAlgo);
	}
	
	private static String getErrorNonValido(Exception e) {
		return "non valido: "+e.getMessage();
	}
	
	public static void validate(CertificateInfo certificatoInfo,
			KeyStore trustStoreCertificatiX509, CertStore crlX509, IOCSPValidator ocspValidatorX509, String headerName,
			boolean verifaCA) throws UtilsException {
		if(trustStoreCertificatiX509!=null) {
			String prefisso = headerName!=null ? ("Certificato presente nell'header '"+headerName+"' ") : "Certificato di firma ";
			if(verifaCA && !certificatoInfo.isVerified(trustStoreCertificatiX509, true)) {
				throw new UtilsException(prefisso+"non è verificabile rispetto alle CA conosciute");
			}
			try {
				certificatoInfo.checkValid();
			}catch(CertificateExpiredException e) {
				throw new UtilsException(prefisso+"scaduto: "+e.getMessage(),e);
			}catch(CertificateNotYetValidException e) {
				throw new UtilsException(prefisso+"non ancora valido: "+e.getMessage(),e);
			}catch(Exception e) {
				throw new UtilsException(prefisso+getErrorNonValido(e),e);
			}
			if(ocspValidatorX509!=null) {
				try {
					ocspValidatorX509.valid(certificatoInfo.getCertificate());
				}catch(Exception e) {
					throw new UtilsException(prefisso+getErrorNonValido(e),e);
				}
			}
			if(crlX509!=null) {
				try {
					certificatoInfo.checkValid(crlX509, trustStoreCertificatiX509);
				}catch(Exception e) {
					throw new UtilsException(prefisso+getErrorNonValido(e),e);
				}
			}
		}
	}
	
 }
