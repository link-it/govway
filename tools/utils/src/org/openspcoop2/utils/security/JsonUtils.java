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
import java.io.InputStream;
import java.net.URI;
import java.security.cert.CertStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm;
import org.apache.cxf.rs.security.jose.jwe.ContentEncryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweEncryption;
import org.apache.cxf.rs.security.jose.jwe.JweEncryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweException;
import org.apache.cxf.rs.security.jose.jwe.JweHeaders;
import org.apache.cxf.rs.security.jose.jwe.JweUtils;
import org.apache.cxf.rs.security.jose.jwe.KeyEncryptionProvider;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jwk.KeyOperation;
import org.apache.cxf.rs.security.jose.jws.JwsException;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureProvider;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

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

	public static Message newMessage() {
		Message m = new MessageImpl();
		Exchange ex = new ExchangeImpl();
		m.setExchange(ex);
		return m;
	}
	
	public static boolean SIGNATURE = true;
	public static boolean ENCRYPT = false;
	public static boolean DECRYPT = false;
	public static boolean SENDER = true;
	public static boolean RECEIVER = false;
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
			messaggio = new String(msg);
			if(innerMsg!=null && !"".equals(innerMsg) && !"null".equals(innerMsg) && !innerMsg.equals(msg)) {
				if(!messaggio.contains(innerMsg)) {
					messaggio = messaggio + " ; " + innerMsg;
				}
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
	
	public static byte[] readKeystoreFromURI(Properties props) throws Exception {
		
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
					throw new Exception("TrustStore ssl password undefined");
				}
				if(trustStoreType==null) {
					throw new Exception("TrustStore ssl type undefined");
				}
			}
			if( (path.startsWith("https:") && trustStore==null) || path.startsWith("http:") ) {
				//System.out.println("http");
				httpResponse = HttpUtilities.getHTTPResponse(path, 60000, 10000);
			}
			else {
				//System.out.println("https");
				httpResponse = HttpUtilities.getHTTPSResponse(path, 60000, 10000, trustStore, trustStorePassword, trustStoreType);
			}
			if(httpResponse==null || httpResponse.getContent()==null) {
				throw new Exception("Keystore '"+path+"' unavailable");
			}
			if(httpResponse.getResultHTTPOperation()!=200) {
				throw new Exception("Retrieve keystore '"+path+"' failed (returnCode:"+httpResponse.getResultHTTPOperation()+")");
			}
			content = httpResponse.getContent();
		}
		else if(path!=null && (path.startsWith("file"))){
			File f = new File(new URI(path));
			content = FileSystemUtilities.readBytesFromFile(f);
		}
		
		return content;
	}
	
	public static File normalizeProperties(Properties props) throws Exception {
		
		String propertyKeystoreName = RSSecurityConstants.RSSEC_KEY_STORE_FILE;
		byte[] content = readKeystoreFromURI(props);
		
		File fTmp = null;
		if(content!=null) {
		
			String tipo = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(tipo==null) {
				tipo = "jks";
			}
			
			fTmp = File.createTempFile("keystore", "."+tipo);
			FileSystemUtilities.writeFile(fTmp, content);
			props.remove(propertyKeystoreName);
			props.put(propertyKeystoreName, fTmp.getAbsolutePath());
			
		}
		
		return fTmp;
	}
	
	public static boolean isDynamicProvider(Properties props) throws Exception {
		String alias = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS);
		if("*".equalsIgnoreCase(alias)) {
			props.remove(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS);
			return true;
		}
		return false;
	}
	
	public static String readAlias(String jsonString) throws Exception {
		if(jsonString.contains(".")==false) {
			throw new Exception("Invalid format (expected COMPACT)");
		}
		String [] tmp = jsonString.split("\\.");
		byte[] header = Base64Utilities.decode(tmp[0].trim());
		JsonNode node = JSONUtils.getInstance().getAsNode(header).get("kid");
		if(node==null) {
			throw new Exception("Claim 'kid' not found");
		}
		String kid = node.asText();
		if(kid==null) {
			throw new Exception("Claim 'kid' without value");
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
	
	public static SecretKey getSecretKey(Properties props) throws Exception {
		if(props.containsKey(RSSecurityConstants.RSSEC_KEY_STORE_TYPE)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if("jceks".equalsIgnoreCase(type)) {
				Object oKeystore = props.get(RSSecurityConstants.RSSEC_KEY_STORE);
				String fileK = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
				String password = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_PSWD);
				String alias = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS);
				String passwordKey = props.getProperty(RSSecurityConstants.RSSEC_KEY_PSWD);
				
				if(oKeystore!=null && oKeystore instanceof java.security.KeyStore) {
					java.security.KeyStore keystoreJCEKS = (java.security.KeyStore) oKeystore;
					
					if(alias==null || "".equals(alias)){
						throw new Exception("(JCEKS) Alias key undefined");
					}
					if(passwordKey==null || "".equals(passwordKey)){
						throw new Exception("(JCEKS) Password key undefined");
					}
					
					SecretKey secretKey = (SecretKey) keystoreJCEKS.getKey(alias, passwordKey.toCharArray());
					return  secretKey;
				}
				else if(fileK!=null) {
					
					if(password==null || "".equals(password)){
						throw new Exception("(JCEKS) Keystore password undefined");
					}
					
					File file = new File(fileK);
					KeyStore keystoreJCEKS = null;
					if(file.exists()) {
						keystoreJCEKS = new KeyStore(file.getAbsolutePath(), "JCEKS", password);
					}
					else {
						InputStream is = JsonUtils.class.getResourceAsStream(fileK);
						File fTmp = null;
						try {
							if(is!=null) {
								byte[] f = Utilities.getAsByteArray(is);
								fTmp = File.createTempFile("keystore", ".jceks");
								FileSystemUtilities.writeFile(fTmp, f);
								keystoreJCEKS = new KeyStore(fTmp.getAbsolutePath(), "JCEKS", password);
							}
						}finally {
							try {
								if(is!=null) {
									is.close();
								}
							}catch(Exception e) {}
							try {
								if(fTmp!=null) {
									fTmp.delete();
								}
							}catch(Exception e) {}
						}
					}	
					if(keystoreJCEKS!=null) {
						
						if(alias==null || "".equals(alias)){
							throw new Exception("(JCEKS) Alias key undefined");
						}
						if(passwordKey==null || "".equals(passwordKey)){
							throw new Exception("(JCEKS) Password key undefined");
						}
						
						SecretKey secretKey = (SecretKey) keystoreJCEKS.getSecretKey(alias, passwordKey);
						return  secretKey;
					}

				}
			}
		}
		return null;
	}
	
	public static JwsSignatureProvider getJwsSymmetricProvider(Properties props) throws Exception {
		String algorithm = props.getProperty(RSSecurityConstants.RSSEC_SIGNATURE_ALGORITHM);
		return getJwsSymmetricProvider(props, algorithm);
	}
	public static JwsSignatureProvider getJwsSymmetricProvider(Properties props, String algorithm) throws Exception {
		org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = null;
		if(algorithm==null || "".equals(algorithm)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if("jceks".equalsIgnoreCase(type)) {
				throw new Exception("(JCEKS) Signature Algorithm undefined");	
			}
		}
		else{
			algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(algorithm);
		}
		return getJwsSymmetricProvider(props, algo);
	}
	public static JwsSignatureProvider getJwsSymmetricProvider(Properties props, org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algorithm) throws Exception {
		
		SecretKey secretKey = getSecretKey(props);
		if(secretKey!=null) {
			if(algorithm==null || "".equals(algorithm)) {
				throw new Exception("(JCEKS) Signature Algorithm undefined");
			}
			byte[] encoded = secretKey.getEncoded();
			JwsSignatureProvider provider = JwsUtils.getHmacSignatureProvider(encoded, algorithm);
			if(provider==null) {
				throw new Exception("(JCEKS) JwsSignatureProvider init failed; check signature algorithm ("+algorithm+")");
			}
			return provider;
		}
		return null;
	}
	
	public static JwsSignatureVerifier getJwsSignatureVerifier(Properties props) throws Exception {
		String algorithm = props.getProperty(RSSecurityConstants.RSSEC_SIGNATURE_ALGORITHM);
		return getJwsSignatureVerifier(props, algorithm);
	}
	public static JwsSignatureVerifier getJwsSignatureVerifier(Properties props, String algorithm) throws Exception {
		org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = null;
		if(algorithm==null || "".equals(algorithm)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if("jceks".equalsIgnoreCase(type)) {
				throw new Exception("(JCEKS) Signature Algorithm undefined");
			}
		}
		else {
			algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(algorithm);
		}
		return getJwsSignatureVerifier(props, algo);
	}
	public static JwsSignatureVerifier getJwsSignatureVerifier(Properties props, org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algorithm) throws Exception {
		
		SecretKey secretKey = getSecretKey(props);
		if(secretKey!=null) {
			if(algorithm==null || "".equals(algorithm)) {
				throw new Exception("(JCEKS) Signature Algorithm undefined");
			}
			JwsSignatureVerifier verifier = JwsUtils.getHmacSignatureVerifier(secretKey.getEncoded(), algorithm);
			if(verifier==null) {
				throw new Exception("(JCEKS) JwsSignatureVerifier init failed; check signature algorithm ("+algorithm+")");
			}
			return verifier;
		}
		return null;
	}
	
	
	
	public static JweEncryptionProvider getJweEncryptionProvider(Properties props) throws Exception {
		String algorithm = props.getProperty(JoseConstants.RSSEC_ENCRYPTION_CONTENT_ALGORITHM);
		return getJweEncryptionProvider(props, algorithm);
	}
	public static JweEncryptionProvider getJweEncryptionProvider(Properties props, String algorithm) throws Exception {
		org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm algo = null;
		if(algorithm==null || "".equals(algorithm)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if("jceks".equalsIgnoreCase(type)) {
				throw new Exception("(JCEKS) Content Algorithm undefined");
			}
		}
		else {
			algo = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(algorithm);
		}
		return getJweEncryptionProvider(props, algo);
	}
	public static JweEncryptionProvider getJweEncryptionProvider(Properties props, org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm algorithm) throws Exception {
		
		SecretKey secretKey = getSecretKey(props);
		if(secretKey!=null) {
			if(algorithm==null || "".equals(algorithm)) {
				throw new Exception("(JCEKS) Content Algorithm undefined");
			}
			JweEncryptionProvider provider = JweUtils.getDirectKeyJweEncryption(secretKey, algorithm);
			if(provider==null) {
				throw new Exception("(JCEKS) JweEncryptionProvider init failed; check content algorithm ("+algorithm+")");
			}
			return provider;
		}
		return null;
	}
	
	public static JweDecryptionProvider getJweDecryptionProvider(Properties props) throws Exception {
		String algorithm = props.getProperty(JoseConstants.RSSEC_ENCRYPTION_CONTENT_ALGORITHM);
		return getJweDecryptionProvider(props, algorithm);
	}
	public static JweDecryptionProvider getJweDecryptionProvider(Properties props, String algorithm) throws Exception {
		org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm algo = null;
		if(algorithm==null || "".equals(algorithm)) {
			String type = props.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if("jceks".equalsIgnoreCase(type)) {
				throw new Exception("(JCEKS) Content Algorithm undefined");
			}
		}
		else {
			algo = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(algorithm);
		}
		return getJweDecryptionProvider(props, algo);
	}
	public static JweDecryptionProvider getJweDecryptionProvider(Properties props, org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm algorithm) throws Exception {
		
		SecretKey secretKey = getSecretKey(props);
		if(secretKey!=null) {
			if(algorithm==null || "".equals(algorithm)) {
				throw new Exception("(JCEKS) Content Algorithm undefined");
			}
			JweDecryptionProvider verifier = JweUtils.getDirectKeyJweDecryption(secretKey, algorithm);
			if(verifier==null) {
				throw new Exception("(JCEKS) JweDecryptionProvider init failed; check content algorithm ("+algorithm+")");
			}
			return verifier;
		}
		return null;
	}
	
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
	
	public static void validate(CertificateInfo certificatoInfo,
			KeyStore trustStoreCertificatiX509, CertStore crlX509, String headerName,
			boolean verifaCA) throws Exception {
		if(trustStoreCertificatiX509!=null) {
			if(verifaCA && certificatoInfo.isVerified(trustStoreCertificatiX509, true)==false) {
				throw new Exception("Certificato presente nell'header '"+headerName+"' non è verificabile rispetto alle CA conosciute");
			}
			try {
				certificatoInfo.checkValid();
			}catch(CertificateExpiredException e) {
				throw new Exception("Certificato presente nell'header '"+headerName+"' scaduto: "+e.getMessage(),e);
			}catch(CertificateNotYetValidException e) {
				throw new Exception("Certificato presente nell'header '"+headerName+"' non ancora valido: "+e.getMessage(),e);
			}catch(Exception e) {
				throw new Exception("Certificato presente nell'header '"+headerName+"' non valido: "+e.getMessage(),e);
			}
			if(crlX509!=null) {
				try {
					certificatoInfo.checkValid(crlX509, trustStoreCertificatiX509);
				}catch(Exception e) {
					throw new Exception("Certificato presente nell'header '"+headerName+"' non valido: "+e.getMessage(),e);
				}
			}
		}
	}
	
 }
