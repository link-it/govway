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


package org.openspcoop2.security.message.jose;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.keystore.cache.GestoreOCSPResource;
import org.openspcoop2.security.keystore.cache.GestoreOCSPValidator;
import org.openspcoop2.security.message.AbstractRESTMessageSecurityReceiver;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.utils.EncryptionBean;
import org.openspcoop2.security.message.utils.KeystoreUtils;
import org.openspcoop2.security.message.utils.PropertiesUtils;
import org.openspcoop2.security.message.utils.SignatureBean;
import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.remote.IRemoteStoreProvider;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.security.CertificateValidityCheck;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonDecrypt;
import org.openspcoop2.utils.security.JsonVerifySignature;
import org.openspcoop2.utils.transport.http.IOCSPValidator;



/**
 * Classe per la gestione della WS-Security (WSDoAllReceiver).
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MessageSecurityReceiver_jose extends AbstractRESTMessageSecurityReceiver {

	private JOSESerialization joseSerialization = null;
	private boolean detached = false;
	private JsonVerifySignature jsonVerifierSignature = null;
	private JsonDecrypt jsonDecrypt = null;

	@Override
	public void process(MessageSecurityContext messageSecurityContext,OpenSPCoop2Message messageParam,Busta busta,org.openspcoop2.utils.Map<Object> ctx) throws SecurityException{
		process(messageSecurityContext, messageParam, busta,
				false, null, ctx);
	}
	public void process(MessageSecurityContext messageSecurityContext,OpenSPCoop2Message messageParam,Busta busta,
			boolean bufferMessageReadOnly, String idTransazione,org.openspcoop2.utils.Map<Object> ctx) throws SecurityException{
		
		boolean signatureProcess = false;
		boolean encryptProcess = false;
		try{
			
			if(!ServiceBinding.REST.equals(messageParam.getServiceBinding())){
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" usable only with REST Binding");
			}
			if(!MessageType.JSON.equals(messageParam.getMessageType())) {
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" usable only with REST Binding and a json message, found: "+messageParam.getMessageType());
			}
			OpenSPCoop2RestJsonMessage restJsonMessage = messageParam.castAsRestJson();
			
    		RequestInfo requestInfo = null;
    		if(ctx!=null && ctx.containsKey(Costanti.REQUEST_INFO)) {
    			requestInfo = (RequestInfo) ctx.get(Costanti.REQUEST_INFO);
    		}
    		
			
			
			// ********** Leggo operazioni ***************
			boolean encrypt = false;
			boolean signature = false;

			String[]actions = ((String)messageSecurityContext.getIncomingProperties().get(SecurityConstants.ACTION)).split(" ");
			for (int i = 0; i < actions.length; i++) {
				if(SecurityConstants.isActionEncryption(actions[i].trim()) || SecurityConstants.isActionDecryption(actions[i].trim())){
					encrypt = true;
				}
				else if(SecurityConstants.SIGNATURE_ACTION.equals(actions[i].trim())){
					signature = true;
				}
				else {
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+"; action '"+actions[i]+"' unsupported");
				}
			}
			
			if(encrypt && signature) {
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" usable only with one function beetwen encrypt or signature");
			}
			if(!encrypt && !signature) {
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" require one function beetwen encrypt or signature");
			}
			
			
			
			
			if(signature) {
				
				
				// **************** Leggo parametri signature store **************************
							
				String mode = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_MODE);
				if(mode==null || "".equals(mode.trim())){
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+" require '"+SecurityConstants.SIGNATURE_MODE+"' property");
				}
				try {
					this.joseSerialization = JOSEUtils.toJOSESerialization(mode);
				}catch(Exception e) {
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+", '"+SecurityConstants.SIGNATURE_MODE+"' property error: "+e.getMessage(),e);
				}
				JWTOptions options = new JWTOptions(this.joseSerialization);
				boolean useHeaders = JOSEUtils.useJwtHeadersMap(messageSecurityContext.getIncomingProperties(), options);
				
				SignatureBean bean = null;
				NotFoundException notFound = null;
				try {
					bean = PropertiesUtils.getReceiverSignatureBean(messageSecurityContext);
				}catch(NotFoundException e) {
					notFound = e;
				}
				if(bean!=null) {
					Properties signatureProperties = bean.getProperties();
					boolean throwError = true;
					Map<String,Object> dynamicMap = Costanti.readDynamicMap(ctx);
    				JOSEUtils.injectKeystore(requestInfo, dynamicMap, signatureProperties, messageSecurityContext.getLog(), throwError); // serve per leggere il keystore dalla cache
					this.jsonVerifierSignature = new JsonVerifySignature(signatureProperties, options);	
				}
				else if(useHeaders) {
					
					if(JOSEUtils.isRemoteStore(messageSecurityContext.getIncomingProperties())) {
						IRemoteStoreProvider provider = (IRemoteStoreProvider) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_PROVIDER);
						RemoteKeyType keyType = (RemoteKeyType) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_KEY_TYPE);
						RemoteStoreConfig config = (RemoteStoreConfig) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_CONFIG);
						this.jsonVerifierSignature = new JsonVerifySignature(provider, keyType, config, options);
					}
					else if(JOSEUtils.isJWKSetTrustStore(messageSecurityContext.getIncomingProperties())) {
						JsonWebKeys jsonWebKeys = null;
						JWKSet jkwSet = JOSEUtils.readTrustStoreJwtJsonWebKeysCert(requestInfo, messageSecurityContext.getIncomingProperties());
						if(jkwSet!=null) {
							jsonWebKeys = jkwSet.getJsonWebKeys();
						}
						this.jsonVerifierSignature = new JsonVerifySignature(jsonWebKeys, options);
					}
					else if(JOSEUtils.isPublicKeyTrustStore(messageSecurityContext.getIncomingProperties())) {
						JsonWebKeys jsonWebKeys = null;
						JWKSet jkwSet = JOSEUtils.readTrustStorePublicKey(requestInfo, messageSecurityContext.getIncomingProperties());
						if(jkwSet!=null) {
							jsonWebKeys = jkwSet.getJsonWebKeys();
						}
						this.jsonVerifierSignature = new JsonVerifySignature(jsonWebKeys, options);
					}
					else {
						KeyStore trustStore = JOSEUtils.readTrustStoreJwtX509Cert(requestInfo, messageSecurityContext.getIncomingProperties());
						KeyStore trustStoreSsl = JOSEUtils.readTrustStoreSsl(requestInfo, messageSecurityContext.getIncomingProperties());
						this.jsonVerifierSignature = new JsonVerifySignature(trustStoreSsl, trustStore, options);
					}
				}
				else {	
					KeyStore signatureKS = null;
					KeyStore signatureTrustStoreKS = null;
					JWKSet signatureJWKSet = null;
					String aliasSignatureUser = null;
					try {
						bean = KeystoreUtils.getReceiverSignatureBean(messageSecurityContext, ctx);
					}catch(Exception e) {
						// Lancio come messaggio eccezione precedente
						if(notFound!=null) {
							messageSecurityContext.getLog().error(e.getMessage(),e);
							throw notFound;
						}
						else {
							throw e;
						}
					}
					
					signatureKS = bean.getKeystore();
					signatureTrustStoreKS = bean.getTruststore();
					signatureJWKSet = bean.getJwkSet();
					aliasSignatureUser = bean.getUser();

					if(signatureKS==null && signatureTrustStoreKS==null && signatureJWKSet==null) {
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+" require truststore");
					}
					if(aliasSignatureUser==null) {
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+" require alias certificate");
					}
					
					String signatureAlgorithm = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_ALGORITHM);
					if(signatureAlgorithm==null || "".equals(signatureAlgorithm.trim())){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+" require '"+SecurityConstants.SIGNATURE_ALGORITHM+"' property");
					}
					
					String symmetricKeyParam = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SYMMETRIC_KEY);
					boolean symmetricKey = false;
					if(symmetricKeyParam!=null) {
						symmetricKey = SecurityConstants.SYMMETRIC_KEY_TRUE.equalsIgnoreCase(symmetricKeyParam);
					}
					
					if(signatureTrustStoreKS!=null) {
						this.jsonVerifierSignature = new JsonVerifySignature(signatureTrustStoreKS, aliasSignatureUser, signatureAlgorithm, options);	
					}
					else if(signatureKS!=null){
						this.jsonVerifierSignature = new JsonVerifySignature(signatureKS, aliasSignatureUser, signatureAlgorithm, options);	
					}
					else {
						this.jsonVerifierSignature = new JsonVerifySignature(signatureJWKSet.getJsonWebKeys(), symmetricKey, aliasSignatureUser, signatureAlgorithm, options);	
					}
				}
				
				String signatureDetachedParam = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_DETACHED);
				if(signatureDetachedParam!=null) {
					this.detached = SecurityConstants.SIGNATURE_DETACHED_TRUE.equalsIgnoreCase(signatureDetachedParam);
				}
				
				String detachedSignature = null;
				if(this.detached) {
					detachedSignature = this.readDetachedSignatureFromMessage(messageSecurityContext.getIncomingProperties(), 
							restJsonMessage, JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION);
				}
				
				String signatureValidityCheck = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_VALIDITY_CHECK);
				if(signatureValidityCheck!=null && StringUtils.isNotEmpty(signatureValidityCheck)) {
					CertificateValidityCheck c = CertificateValidityCheck.parseCertificateValidityCheck(signatureValidityCheck);
					if(c!=null) {
						this.jsonVerifierSignature.setValidityCheck(c);
					}
				}
				
				String signatureCRL = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_CRL);
				if(signatureCRL==null || "".equals(signatureCRL)) {
					signatureCRL = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_CRL);	
				}
				String signatureOCSP = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_OCSP);
				if(signatureOCSP==null || "".equals(signatureOCSP)) {
					signatureOCSP = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_OCSP);	
				}
				
				boolean crlByOcsp = false;
				if(this.jsonVerifierSignature.getTrustStoreCertificatiX509()!=null && signatureOCSP!=null && !"".equals(signatureOCSP)) {
					LoggerBuffer lb = new LoggerBuffer();
					lb.setLogDebug(messageSecurityContext.getLog());
					lb.setLogError(messageSecurityContext.getLog());
					GestoreOCSPResource ocspResourceReader = new GestoreOCSPResource(requestInfo);
					IOCSPValidator ocspValidator = null;
					try {
						ocspValidator = new GestoreOCSPValidator(requestInfo, lb, 
								this.jsonVerifierSignature.getTrustStoreCertificatiX509(),
								signatureCRL, 
								signatureOCSP, 
								ocspResourceReader);
					}catch(Exception e){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+"; ocsp initialization (policy:'"+signatureOCSP+"') failed: "+e.getMessage(),e);
					}
					if(ocspValidator!=null) {
						this.jsonVerifierSignature.setOcspValidatorX509(ocspValidator);
						GestoreOCSPValidator gOcspValidator = (GestoreOCSPValidator) ocspValidator;
						if(gOcspValidator.getOcspConfig()!=null) {
							crlByOcsp = gOcspValidator.getOcspConfig().isCrl();
						}
					}
				}
				
				if(signatureCRL!=null && !"".equals(signatureCRL) && !crlByOcsp) {
					CRLCertstore crlCertstore = GestoreKeystoreCache.getCRLCertstore(requestInfo, signatureCRL);
					if(crlCertstore==null) {
						throw new SecurityException("Process CRL '"+signatureCRL+"' failed");
					}
					this.jsonVerifierSignature.setCrlX509(crlCertstore.getCertStore());
				}
				
				String httpsCRL = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_CRL);
				String httpsOCSP = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_OCSP);
				
				boolean httpsCrlByOcsp = false;
				if(this.jsonVerifierSignature.getTrustStoreHttps()!=null && httpsOCSP!=null && !"".equals(httpsOCSP)) {
					LoggerBuffer lb = new LoggerBuffer();
					lb.setLogDebug(messageSecurityContext.getLog());
					lb.setLogError(messageSecurityContext.getLog());
					GestoreOCSPResource ocspResourceReader = new GestoreOCSPResource(requestInfo);
					IOCSPValidator ocspValidator = null;
					try {
						ocspValidator = new GestoreOCSPValidator(requestInfo, lb, 
								this.jsonVerifierSignature.getTrustStoreHttps(),
								httpsCRL, 
								httpsOCSP, 
								ocspResourceReader);
					}catch(Exception e){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+"; ocsp initialization for https (policy:'"+signatureOCSP+"') failed: "+e.getMessage(),e);
					}
					if(ocspValidator!=null) {
						this.jsonVerifierSignature.setOcspValidatorHttps(ocspValidator);
						GestoreOCSPValidator gOcspValidator = (GestoreOCSPValidator) ocspValidator;
						if(gOcspValidator.getOcspConfig()!=null) {
							httpsCrlByOcsp = gOcspValidator.getOcspConfig().isCrl();
						}
					}
				}
				
				if(httpsCRL!=null && !"".equals(httpsCRL) && !httpsCrlByOcsp) {
					CRLCertstore crlCertstore = GestoreKeystoreCache.getCRLCertstore(requestInfo, httpsCRL);
					if(crlCertstore==null) {
						throw new SecurityException("Process CRL '"+httpsCRL+"' failed");
					}
					this.jsonVerifierSignature.setCrlHttps(crlCertstore.getCertStore());
				}
				
				// **************** Process **************************

				signatureProcess = true; // le eccezioni lanciate da adesso sono registrato con codice relative alla verifica
				boolean verify = false;
				try {
					if(this.detached) {
						verify = this.jsonVerifierSignature.verifyDetached(detachedSignature, restJsonMessage.getContent(bufferMessageReadOnly, idTransazione));
					}else {
						verify = this.jsonVerifierSignature.verify(restJsonMessage.getContent(bufferMessageReadOnly, idTransazione));
					}
				}catch(Exception e) {
					throw new SecurityException("Signature verification failed: "+e.getMessage(),e);
				}
				if(!verify) {
					throw new SecurityException("Signature verification failed");
				}
				
			} // fine signature
			
			
			
			else if(encrypt) {
				
				
				// **************** Leggo parametri encryption store **************************
							
				String mode = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_MODE);
				if(mode==null || "".equals(mode.trim())){
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require '"+SecurityConstants.DECRYPTION_MODE+"' property");
				}
				try {
					this.joseSerialization = JOSEUtils.toJOSESerialization(mode);
				}catch(Exception e) {
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+", '"+SecurityConstants.DECRYPTION_MODE+"' property error: "+e.getMessage(),e);
				}
				JWTOptions options = new JWTOptions(this.joseSerialization);
				boolean useHeaders = JOSEUtils.useJwtHeadersMap(messageSecurityContext.getIncomingProperties(), options);
				
				EncryptionBean bean = null;
				NotFoundException notFound = null;
				try {
					bean = PropertiesUtils.getReceiverEncryptionBean(messageSecurityContext);
				}catch(NotFoundException e) {
					notFound = e;
				}
				if(bean!=null) {
					Properties encryptionProperties = bean.getProperties();
					boolean throwError = true;
					Map<String,Object> dynamicMap = Costanti.readDynamicMap(ctx);
					JOSEUtils.injectKeystore(requestInfo, dynamicMap, encryptionProperties, messageSecurityContext.getLog(), throwError); // serve per leggere il keystore dalla cache
					this.jsonDecrypt = new JsonDecrypt(encryptionProperties, options);	
				}
				else if(useHeaders) {
					KeyStore trustStoreSsl = JOSEUtils.readTrustStoreSsl(requestInfo, messageSecurityContext.getIncomingProperties());
					if(JOSEUtils.isJWKSetKeyStore(messageSecurityContext.getIncomingProperties())) {
						JsonWebKeys jsonWebKeys = null;
						JWKSet jkwSet = JOSEUtils.readKeyStoreJwtJsonWebKeysCert(requestInfo, messageSecurityContext.getIncomingProperties());
						if(jkwSet!=null) {
							jsonWebKeys = jkwSet.getJsonWebKeys();
						}
						this.jsonDecrypt = new JsonDecrypt(trustStoreSsl, jsonWebKeys, options);
					}
					else {
						KeyStore trustStore = JOSEUtils.readTrustStoreJwtX509Cert(requestInfo, messageSecurityContext.getIncomingProperties());
						KeyStore keyStore = JOSEUtils.readKeyStoreJwtX509Cert(requestInfo, messageSecurityContext.getIncomingProperties());
						Map<String, String> keystoreMapAliasPassword = JOSEUtils.covertToJwtX509CertMapAliasPassword(messageSecurityContext.getIncomingProperties());						
						this.jsonDecrypt = new JsonDecrypt(trustStoreSsl, trustStore, keyStore, keystoreMapAliasPassword, options);
					}
				}
				else {	
					KeyStore encryptionKS = null;
					boolean encryptionSymmetric = false;
					JWKSet encryptionJWKSet = null;
					String aliasEncryptUser = null;
					String aliasEncryptPassword = null;
					try {
						bean = KeystoreUtils.getReceiverEncryptionBean(messageSecurityContext, ctx);
					}catch(Exception e) {
						// Lancio come messaggio eccezione precedente
						if(notFound!=null) {
							messageSecurityContext.getLog().error(e.getMessage(),e);
							throw notFound;
						}
						else {
							throw e;
						}
					}
					
					encryptionKS = bean.getKeystore();
					encryptionSymmetric = bean.isEncryptionSimmetric();
					encryptionJWKSet = bean.getJwkSet();
					aliasEncryptUser = bean.getUser();
					aliasEncryptPassword = bean.getPassword();

					if(encryptionKS==null && encryptionJWKSet==null) {
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require keystore");
					}
					if(aliasEncryptUser==null) {
						if(encryptionSymmetric) {
							throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require alias secret key");
						}
						else {
							throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require alias private key");
						}
					}
					if(encryptionKS!=null && aliasEncryptPassword==null) {
						if(encryptionSymmetric) {
							throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require password secret key");
						}
						else {
							boolean required = true;
							if(KeystoreType.JKS.isType(encryptionKS.getKeystoreType())) {
								required = DBUtils.isKeystoreJksKeyPasswordRequired();
							}
							else if(KeystoreType.PKCS12.isType(encryptionKS.getKeystoreType())) {
								required = DBUtils.isKeystorePkcs12KeyPasswordRequired();
							}
							if(required) {
								throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require password private key");
							}
						}
					}

					String encryptionKeyAlgorithm = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.ENCRYPTION_KEY_ALGORITHM);
					if(encryptionKeyAlgorithm==null || "".equals(encryptionKeyAlgorithm.trim())){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require '"+SecurityConstants.ENCRYPTION_KEY_ALGORITHM+"' property");
					}
					
					String encryptionContentAlgorithm = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.ENCRYPTION_CONTENT_ALGORITHM);
					if(encryptionContentAlgorithm==null || "".equals(encryptionContentAlgorithm.trim())){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require '"+SecurityConstants.ENCRYPTION_CONTENT_ALGORITHM+"' property");
					}
					
					if(encryptionKS!=null) {
						this.jsonDecrypt = new JsonDecrypt(encryptionKS, encryptionSymmetric, aliasEncryptUser, aliasEncryptPassword,
								encryptionKeyAlgorithm, encryptionContentAlgorithm, options);	
					}
					else {
						this.jsonDecrypt = new JsonDecrypt(encryptionJWKSet.getJsonWebKeys(), encryptionSymmetric, aliasEncryptUser,
								encryptionKeyAlgorithm, encryptionContentAlgorithm, options);	
					}

				}
				
				String encryptionValidityCheck = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_VALIDITY_CHECK);
				if(encryptionValidityCheck!=null && StringUtils.isNotEmpty(encryptionValidityCheck)) {
					CertificateValidityCheck c = CertificateValidityCheck.parseCertificateValidityCheck(encryptionValidityCheck);
					if(c!=null) {
						this.jsonDecrypt.setValidityCheck(c);
					}
				}
				
				String encryptionCRL = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_CRL);
				String encryptionOCSP = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_OCSP);
				
				boolean crlByOcsp = false;
				if(this.jsonDecrypt.getTrustStoreVerificaCertificatiX509()!=null && encryptionOCSP!=null && !"".equals(encryptionOCSP)) {
					LoggerBuffer lb = new LoggerBuffer();
					lb.setLogDebug(messageSecurityContext.getLog());
					lb.setLogError(messageSecurityContext.getLog());
					GestoreOCSPResource ocspResourceReader = new GestoreOCSPResource(requestInfo);
					IOCSPValidator ocspValidator = null;
					try {
						ocspValidator = new GestoreOCSPValidator(requestInfo, lb, 
								this.jsonDecrypt.getTrustStoreVerificaCertificatiX509(),
								encryptionCRL, 
								encryptionOCSP, 
								ocspResourceReader);
					}catch(Exception e){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+"; ocsp initialization (policy:'"+encryptionOCSP+"') failed: "+e.getMessage(),e);
					}
					if(ocspValidator!=null) {
						this.jsonDecrypt.setOcspValidatorX509(ocspValidator);
						GestoreOCSPValidator gOcspValidator = (GestoreOCSPValidator) ocspValidator;
						if(gOcspValidator.getOcspConfig()!=null) {
							crlByOcsp = gOcspValidator.getOcspConfig().isCrl();
						}
					}
				}
				
				if(encryptionCRL!=null && !"".equals(encryptionCRL) && !crlByOcsp) {
					CRLCertstore crlCertstore = GestoreKeystoreCache.getCRLCertstore(requestInfo, encryptionCRL);
					if(crlCertstore==null) {
						throw new SecurityException("Process CRL '"+encryptionCRL+"' failed");
					}
					this.jsonDecrypt.setCrlX509(crlCertstore.getCertStore());
				}
				
				String httpsCRL = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_CRL);
				String httpsOCSP = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_OCSP);
				
				boolean httpsCrlByOcsp = false;
				if(this.jsonDecrypt.getTrustStoreHttps()!=null && httpsOCSP!=null && !"".equals(httpsOCSP)) {
					LoggerBuffer lb = new LoggerBuffer();
					lb.setLogDebug(messageSecurityContext.getLog());
					lb.setLogError(messageSecurityContext.getLog());
					GestoreOCSPResource ocspResourceReader = new GestoreOCSPResource(requestInfo);
					IOCSPValidator ocspValidator = null;
					try {
						ocspValidator = new GestoreOCSPValidator(requestInfo, lb, 
								this.jsonDecrypt.getTrustStoreHttps(),
								httpsCRL, 
								httpsOCSP, 
								ocspResourceReader);
					}catch(Exception e){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+"; ocsp initialization for https (policy:'"+httpsOCSP+"') failed: "+e.getMessage(),e);
					}
					if(ocspValidator!=null) {
						this.jsonDecrypt.setOcspValidatorHttps(ocspValidator);
						GestoreOCSPValidator gOcspValidator = (GestoreOCSPValidator) ocspValidator;
						if(gOcspValidator.getOcspConfig()!=null) {
							httpsCrlByOcsp = gOcspValidator.getOcspConfig().isCrl();
						}
					}
				}
				
				if(httpsCRL!=null && !"".equals(httpsCRL) && !httpsCrlByOcsp) {
					CRLCertstore crlCertstore = GestoreKeystoreCache.getCRLCertstore(requestInfo, httpsCRL);
					if(crlCertstore==null) {
						throw new SecurityException("Process CRL '"+httpsCRL+"' failed");
					}
					this.jsonDecrypt.setCrlHttps(crlCertstore.getCertStore());
				}
	
				
				// **************** Process **************************
							
				encryptProcess = true; // le eccezioni lanciate da adesso sono registrato con codice relative alla verifica
				try {
					this.jsonDecrypt.decrypt(restJsonMessage.getContent(bufferMessageReadOnly, idTransazione));
				}catch(Exception e) {
					throw new SecurityException("Decrypt failed: "+e.getMessage(),e);
				}
		
			
			} // fine encrypt
			
			
			
						
		} catch (Exception e) {
			
			SecurityException secException = new SecurityException(e.getMessage(), e);
			
			
			/* **** MESSAGGIO ***** */
			String msg = Utilities.getInnerNotEmptyMessageException(e).getMessage();
			
			Throwable innerExc = Utilities.getLastInnerException(e);
			String innerMsg = null;
			if(innerExc!=null){
				innerMsg = innerExc.getMessage();
			}
			
			String messaggio = null;
			if(msg!=null){
				messaggio = msg + "";
				if(innerMsg!=null && !innerMsg.equals(msg)){
					messaggio = messaggio + " ; " + innerMsg;
				}
			}
			else{
				if(innerMsg!=null){
					messaggio = innerMsg;
				}
			}
			
			secException.setMsgErrore(messaggio);
			
			
			/* ***** CODICE **** */
			
			if(signatureProcess){
				secException.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA);
			}
			else if(encryptProcess){
				secException.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA_CIFRATURA_NON_VALIDA);
			}
			else {
				secException.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA);
			}
			
			
			throw secException;
		}

	}

	@Override
	public void detachSecurity(MessageSecurityContext messageSecurityContext, OpenSPCoop2RestMessage<?> messageParam)
			throws SecurityException {
		
		try {
		
			if(!ServiceBinding.REST.equals(messageParam.getServiceBinding())){
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" usable only with REST Binding");
			}
			if(!MessageType.JSON.equals(messageParam.getMessageType())) {
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" usable only with REST Binding and a json message, found: "+messageParam.getMessageType());
			}
			OpenSPCoop2RestJsonMessage restJsonMessage = messageParam.castAsRestJson();
					
			if(this.jsonVerifierSignature!=null) {
				if(this.detached) {
					this.deleteDetachedSignatureFromMessage(restJsonMessage, JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION);
				}	
				else {
					restJsonMessage.updateContent(this.jsonVerifierSignature.getDecodedPayload());
				}
			}
			else if(this.jsonDecrypt!=null) {
				restJsonMessage.updateContent(this.jsonDecrypt.getDecodedPayload());
			}
			else {
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" (detach method) usable only after one function beetwen encrypt or signature");
			}
			
		}catch(Exception e) {
			throw new SecurityException(e.getMessage(), e);
		}
	}

	public JOSESerialization getJoseSerialization() {
		return this.joseSerialization;
	}
	
	@Override
	public String getCertificate() throws SecurityException {
		if(this.jsonVerifierSignature!=null && this.jsonVerifierSignature.getX509Certificate()!=null) {
			return this.jsonVerifierSignature.getX509Certificate().getSubjectX500Principal().toString();
		}
		else if(this.jsonDecrypt!=null && this.jsonDecrypt.getX509Certificate()!=null) {
			return this.jsonDecrypt.getX509Certificate().getSubjectX500Principal().toString();
		}
		return null;
	}

	@Override
	public X509Certificate getX509Certificate() throws SecurityException {
		if(this.jsonVerifierSignature!=null) {
			return this.jsonVerifierSignature.getX509Certificate();
		}
		else if(this.jsonDecrypt!=null) {
			return this.jsonDecrypt.getX509Certificate();
		}
		return null;
	}

	@Override
	public PublicKey getPublicKey() {
		if(this.jsonVerifierSignature!=null) {
			return this.jsonVerifierSignature.getRsaPublicKey();
		}
		else if(this.jsonDecrypt!=null) {
			return this.jsonDecrypt.getRsaPublicKey();
		}
		return null;
	}

	@Override
	public String getCertificateId() {
		if(this.jsonVerifierSignature!=null) {
			return this.jsonVerifierSignature.getKid();
		}
		else if(this.jsonDecrypt!=null) {
			return this.jsonDecrypt.getKid();
		}
		return null;
	}
	
}
