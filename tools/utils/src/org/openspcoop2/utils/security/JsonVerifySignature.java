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
import java.security.cert.X509Certificate;
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
	private JOSERepresentation representation;
	private Properties properties;
	private boolean dynamicProvider;
	
	private String decodedPayload;
	private byte[] decodedPayloadAsByte;
	
	private KeyStore trustStore; // per verificare i certificati presenti nell'header 

	public JsonVerifySignature(Properties props, JOSERepresentation representation) throws UtilsException{
		try {
			this.dynamicProvider = JsonUtils.isDynamicProvider(props); // rimuove l'alias
			if(JOSERepresentation.COMPACT.equals(representation) && this.dynamicProvider) {
				this.properties = props;
			}
			else {
				this.provider = loadProviderFromProperties(props);
			}
			this.representation = representation;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
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
	
	public JsonVerifySignature(java.security.KeyStore keystore, String alias, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, signatureAlgorithm, representation);
	}
	public JsonVerifySignature(KeyStore keystore, String alias, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getPublicKeySignatureVerifier((X509Certificate) keystore.getCertificate(alias), algo);
			this.representation=representation;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}
	
	public JsonVerifySignature(JsonWebKeys jsonWebKeys, String alias, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(JsonUtils.readKey(jsonWebKeys, alias), signatureAlgorithm, representation);
	}
	
	public JsonVerifySignature(JsonWebKey jsonWebKey, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getPublicKeySignatureVerifier(JwkUtils.toRSAPublicKey(jsonWebKey), algo);
			this.representation=representation;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}
	
	
	public JsonVerifySignature() throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, null);
	}
	public JsonVerifySignature(Properties props) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(props, null);
	}
	public JsonVerifySignature(Properties props, java.security.KeyStore trustStore) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(props, new KeyStore(trustStore));
	}
	public JsonVerifySignature(Properties props, KeyStore trustStore) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(props, trustStore);
	}
	public JsonVerifySignature(java.security.KeyStore trustStore) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, new KeyStore(trustStore));
	}
	public JsonVerifySignature(KeyStore trustStore) throws UtilsException{
		_initVerifyCertificatiHeaderJWT(null, trustStore);
	}
	private void _initVerifyCertificatiHeaderJWT(Properties props, KeyStore trustStore) throws UtilsException{
		// verra usato l'header per validare ed ottenere il certificato
		this.representation=JOSERepresentation.COMPACT;
		this.properties = props; // le proprieta' servono per risolvere le url https
		this.trustStore = trustStore;
	}


	public boolean verify(String jsonString) throws UtilsException{
		try {
			switch(this.representation) {
				case SELF_CONTAINED: return verifySelfContained(jsonString);
				case COMPACT: return verifyCompact(jsonString);
				case DETACHED:   throw new UtilsException("Use method verify(String, String) with representation '"+this.representation+"'");
				default: throw new UtilsException("Unsupported representation '"+this.representation+"'");
			}
		}
		catch(Throwable t) {
			t.printStackTrace(System.out);
			throw JsonUtils.convert(this.representation, JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}

	public boolean verify(String jsonDetachedSignature, String jsonDetachedPayload) throws UtilsException{
		try {
			switch(this.representation) {
				case SELF_CONTAINED: throw new UtilsException("Use method verify(String) with representation '"+this.representation+"'");
				case COMPACT: throw new UtilsException("UUse method verify(String) with representation '"+this.representation+"'");
				case DETACHED:  return verifyDetached(jsonDetachedSignature, jsonDetachedPayload);
				default: throw new UtilsException("Unsupported representation '"+this.representation+"'");
			}
		}
		catch(Throwable t) {
			throw JsonUtils.convert(this.representation, JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}

	private boolean verifyDetached(String jsonDetachedSignature, String jsonDetachedPayload) {
		JwsJsonProducer producer = new JwsJsonProducer(jsonDetachedPayload);
		JwsJsonConsumer consumer = new JwsJsonConsumer(jsonDetachedSignature, producer.getUnsignedEncodedPayload());
		return this._verify(consumer);
	}
	private boolean verifySelfContained(String jsonString) {
		JwsJsonConsumer consumer = new JwsJsonConsumer(jsonString);
		return this._verify(consumer);
	}	
	private boolean _verify(JwsJsonConsumer consumer) {
		boolean result = consumer.verifySignatureWith(this.provider);
		this.decodedPayload = consumer.getDecodedJwsPayload();
		this.decodedPayloadAsByte = consumer.getDecodedJwsPayloadBytes();
		return result;
	}

	
	private boolean verifyCompact(String jsonString) throws Exception {
		
		JwsCompactConsumer consumer = new JwsCompactConsumer(jsonString);
		
		JwsSignatureVerifier provider = this.provider;
		if(this.dynamicProvider) {
			String alias = JsonUtils.readAlias(jsonString);
			Properties pNew = new Properties();
			pNew.putAll(this.properties);
			//System.out.println("ALIAS ["+alias+"]");
			pNew.put(JoseConstants.RSSEC_KEY_STORE_ALIAS, alias);
			provider = loadProviderFromProperties(pNew);
		}
		
		if(provider==null) {
			JwsHeaders jwsHeaders = consumer.getJwsHeaders();
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = jwsHeaders.getSignatureAlgorithm();
			if(jwsHeaders.getX509Chain()!=null && !jwsHeaders.getX509Chain().isEmpty()) {
				byte [] cer = Base64Utilities.decode(jwsHeaders.getX509Chain().get(0));
				CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
				if(this.trustStore!=null) {
					if(certificatoInfo.isVerified(this.trustStore)==false) {
						throw new Exception("Certificato presente nell'header '"+JwtHeaders.JWT_HDR_X5U+"' non è verificabile rispetto alle CA conosciute");
					}
				}
				X509Certificate cerx509 = certificatoInfo.getCertificate();
				provider = JwsUtils.getPublicKeySignatureVerifier(cerx509, algo);
			}
			else if(jwsHeaders.getJsonWebKey()!=null) {
				provider = JwsUtils.getPublicKeySignatureVerifier(JwkUtils.toRSAPublicKey(jwsHeaders.getJsonWebKey()), algo);
			}
			else if(jwsHeaders.getX509Url()!=null) {
				if(this.properties==null) {
					this.properties = new Properties();
				}
				this.properties.put(JoseConstants.RSSEC_KEY_STORE_FILE, jwsHeaders.getX509Url());
				File fTmp = null;
				try {
					fTmp = JsonUtils.normalizeProperties(this.properties); // in caso di url http viene letta la risorsa remota e salvata in tmp
					byte [] cer = FileSystemUtilities.readBytesFromFile(fTmp);
					CertificateInfo certificatoInfo = ArchiveLoader.load(cer).getCertificate();
					if(this.trustStore!=null) {
						if(certificatoInfo.isVerified(this.trustStore)==false) {
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
			else if(jwsHeaders.getJsonWebKeysUrl()!=null) {
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
			else {
				throw new Exception("Non è stato trovato alcun header che consentisse di recuperare il certificato per effettuare la validazione");
			}
		}
		
		boolean result = consumer.verifySignatureWith(provider);
		this.decodedPayload = consumer.getDecodedJwsPayload();
		this.decodedPayloadAsByte = consumer.getDecodedJwsPayloadBytes();
		return result;
	}
	
	public String getDecodedPayload() {
		return this.decodedPayload;
	}

	public byte[] getDecodedPayloadAsByte() {
		return this.decodedPayloadAsByte;
	}
}
