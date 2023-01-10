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


package org.openspcoop2.security.message.jose;


import java.util.Properties;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.AbstractRESTMessageSecuritySender;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.utils.EncryptionBean;
import org.openspcoop2.security.message.utils.KeystoreUtils;
import org.openspcoop2.security.message.utils.PropertiesUtils;
import org.openspcoop2.security.message.utils.SignatureBean;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWEOptions;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonEncrypt;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JwtHeaders;

/**
 * MessageSecuritySender_jose
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MessageSecuritySender_jose extends AbstractRESTMessageSecuritySender{

	
     @Override
	public void process(MessageSecurityContext messageSecurityContext,OpenSPCoop2Message messageParam, org.openspcoop2.utils.Map<Object> ctx) throws SecurityException{
		try{ 	
			
			if(ServiceBinding.REST.equals(messageParam.getServiceBinding())==false){
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" usable only with REST Binding");
			}
			if(MessageType.JSON.equals(messageParam.getMessageType())==false) {
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

			String[]actions = ((String)messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ACTION)).split(" ");
			for (int i = 0; i < actions.length; i++) {
				if(SecurityConstants.is_ACTION_ENCRYPTION(actions[i].trim())){
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

				JOSESerialization joseSerialization = null;
				String mode = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SIGNATURE_MODE);
				if(mode==null || "".equals(mode.trim())){
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_SIGNATURE_DESCRIPTION+" require '"+SecurityConstants.SIGNATURE_MODE+"' property");
				}
				try {
					joseSerialization = JOSEUtils.toJOSESerialization(mode);
				}catch(Exception e) {
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_SIGNATURE_DESCRIPTION+", '"+SecurityConstants.SIGNATURE_MODE+"' property error: "+e.getMessage(),e);
				}
				JWSOptions jwsOptions = new JWSOptions(joseSerialization);
				
				String signatureDetachedParam = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SIGNATURE_DETACHED);
				if(signatureDetachedParam!=null) {
					jwsOptions.setDetached(SecurityConstants.SIGNATURE_DETACHED_TRUE.equalsIgnoreCase(signatureDetachedParam));
				}
				
				String signaturePayloadEncodingParam = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SIGNATURE_PAYLOAD_ENCODING);
				if(signaturePayloadEncodingParam!=null) {
					jwsOptions.setPayloadEncoding(SecurityConstants.SIGNATURE_PAYLOAD_ENCODING_TRUE.equalsIgnoreCase(signaturePayloadEncodingParam));
				}
								
				JsonSignature jsonSignature = null;
				SignatureBean bean = null;
				NotFoundException notFound = null;
				try {
					bean = PropertiesUtils.getSenderSignatureBean(messageSecurityContext);
				}catch(NotFoundException e) {
					notFound = e;
				}
				if(bean!=null) {
					Properties signatureProperties = bean.getProperties();
					JOSEUtils.injectKeystore(requestInfo, signatureProperties, messageSecurityContext.getLog()); // serve per leggere il keystore dalla cache
					JwtHeaders jwtHeaders = JOSEUtils.getJwtHeaders(messageSecurityContext.getOutgoingProperties(), messageParam); // la configurazione per kid, jwk e x5c viene configurata via properties
					jsonSignature = new JsonSignature(signatureProperties, jwtHeaders, jwsOptions);	
				}
				else {	
					KeyStore signatureKS = null;
					//KeyStore signatureTrustStoreKS = null;
					JWKSet signatureJWKSet = null;
					String aliasSignatureUser = null;
					String aliasSignaturePassword = null;
					try {
						bean = KeystoreUtils.getSenderSignatureBean(messageSecurityContext, ctx);
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
					//signatureTrustStoreKS = bean.getTruststore();
					signatureJWKSet = bean.getJwkSet();
					aliasSignatureUser = bean.getUser();
					aliasSignaturePassword = bean.getPassword();

					if(signatureKS==null && signatureJWKSet==null) {
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_SIGNATURE_DESCRIPTION+" require keystore");
					}
					if(aliasSignatureUser==null) {
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_SIGNATURE_DESCRIPTION+" require alias private key");
					}
					if(signatureKS!=null && aliasSignaturePassword==null) {
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_SIGNATURE_DESCRIPTION+" require password private key");
					}
					
					String signatureAlgorithm = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SIGNATURE_ALGORITHM);
					if(signatureAlgorithm==null || "".equals(signatureAlgorithm.trim())){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_SIGNATURE_DESCRIPTION+" require '"+SecurityConstants.SIGNATURE_ALGORITHM+"' property");
					}
					
					String symmetricKeyParam = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SYMMETRIC_KEY);
					boolean symmetricKey = false;
					if(symmetricKeyParam!=null) {
						symmetricKey = SecurityConstants.SYMMETRIC_KEY_TRUE.equalsIgnoreCase(symmetricKeyParam);
					}
					
					if(signatureKS!=null) {
						JwtHeaders jwtHeaders = JOSEUtils.getJwtHeaders(messageSecurityContext.getOutgoingProperties(), messageParam, aliasSignatureUser, signatureKS);
						jsonSignature = new JsonSignature(signatureKS, symmetricKey, aliasSignatureUser, aliasSignaturePassword, signatureAlgorithm, jwtHeaders, jwsOptions);	
					}
					else {
						JwtHeaders jwtHeaders = JOSEUtils.getJwtHeaders(messageSecurityContext.getOutgoingProperties(), messageParam, aliasSignatureUser, signatureJWKSet);
						jsonSignature = new JsonSignature(signatureJWKSet.getJsonWebKeys(), symmetricKey, aliasSignatureUser, signatureAlgorithm, jwtHeaders, jwsOptions);	
					}
				}
				

				
				
				
				
				// **************** Process **************************
				
				String contentSign = jsonSignature.sign(restJsonMessage.getContent());
				if(jwsOptions.isDetached()) {
					this.setDetachedSignatureInMessage(messageSecurityContext.getOutgoingProperties(), 
							restJsonMessage, 
							JOSECostanti.JOSE_ENGINE_SIGNATURE_DESCRIPTION, 
							contentSign);
				}else {
					restJsonMessage.updateContent(contentSign);
				}
				
				
				
			} // fine signature
			
			
			
			
			
			else if(encrypt){
			
				// **************** Leggo parametri encryption store **************************
				
				JOSESerialization joseSerialization = null;
				String mode = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_MODE);
				if(mode==null || "".equals(mode.trim())){
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_ENCRYPT_DESCRIPTION+" require '"+SecurityConstants.ENCRYPTION_MODE+"' property");
				}
				try {
					joseSerialization = JOSEUtils.toJOSESerialization(mode);
				}catch(Exception e) {
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_ENCRYPT_DESCRIPTION+", '"+SecurityConstants.ENCRYPTION_MODE+"' property error: "+e.getMessage(),e);
				}
				JWEOptions jweOptions = new JWEOptions(joseSerialization);
				
				String encryptionDeflateParam = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_DEFLATE);
				if(encryptionDeflateParam!=null) {
					jweOptions.setDeflate(SecurityConstants.ENCRYPTION_DEFLATE_TRUE.equalsIgnoreCase(encryptionDeflateParam));
				}
				
				JsonEncrypt jsonEncrypt = null;
				EncryptionBean bean = null;
				NotFoundException notFound = null;
				try {
					bean = PropertiesUtils.getSenderEncryptionBean(messageSecurityContext);
				}catch(NotFoundException e) {
					notFound = e;
				}
				if(bean!=null) {
					Properties encryptionProperties = bean.getProperties();
					JOSEUtils.injectKeystore(requestInfo, encryptionProperties, messageSecurityContext.getLog()); // serve per leggere il keystore dalla cache
					JwtHeaders jwtHeaders = JOSEUtils.getJwtHeaders(messageSecurityContext.getOutgoingProperties(), messageParam); // la configurazione per kid, jwk e x5c viene configurata via properties
					jsonEncrypt = new JsonEncrypt(encryptionProperties, jwtHeaders, jweOptions); 
				}
				else {	
					KeyStore encryptionKS = null;
					KeyStore encryptionTrustStoreKS = null;
					boolean encryptionSymmetric = false;
					JWKSet encryptionJWKSet = null;
					String aliasEncryptUser = null;
					String aliasEncryptPassword = null;
					try {
						bean = KeystoreUtils.getSenderEncryptionBean(messageSecurityContext, ctx);
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
					encryptionTrustStoreKS = bean.getTruststore();
					encryptionSymmetric = bean.isEncryptionSimmetric();
					encryptionJWKSet = bean.getJwkSet();
					aliasEncryptUser = bean.getUser();
					aliasEncryptPassword = bean.getPassword();

					if(encryptionSymmetric) {
						if(encryptionKS==null) {
							throw new SecurityException(JOSECostanti.JOSE_ENGINE_ENCRYPT_DESCRIPTION+" require keystore");
						}
						if(aliasEncryptUser==null) {
							throw new SecurityException(JOSECostanti.JOSE_ENGINE_ENCRYPT_DESCRIPTION+" require alias secret key");
						}
						if(aliasEncryptPassword==null) {
							throw new SecurityException(JOSECostanti.JOSE_ENGINE_ENCRYPT_DESCRIPTION+" require password secret key");
						}
					}
					else {
						if(encryptionTrustStoreKS==null && encryptionJWKSet==null) {
							throw new SecurityException(JOSECostanti.JOSE_ENGINE_ENCRYPT_DESCRIPTION+" require truststore");
						}
						if(aliasEncryptUser==null) {
							throw new SecurityException(JOSECostanti.JOSE_ENGINE_ENCRYPT_DESCRIPTION+" require alias public key");
						}
					}

					
					String encryptionKeyAlgorithm = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_KEY_ALGORITHM);
					if(encryptionKeyAlgorithm==null || "".equals(encryptionKeyAlgorithm.trim())){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_ENCRYPT_DESCRIPTION+" require '"+SecurityConstants.ENCRYPTION_KEY_ALGORITHM+"' property");
					}
					
					String encryptionContentAlgorithm = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_CONTENT_ALGORITHM);
					if(encryptionContentAlgorithm==null || "".equals(encryptionContentAlgorithm.trim())){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_ENCRYPT_DESCRIPTION+" require '"+SecurityConstants.ENCRYPTION_CONTENT_ALGORITHM+"' property");
					}

					if(encryptionSymmetric) {
						JwtHeaders jwtHeaders = JOSEUtils.getJwtHeaders(messageSecurityContext.getOutgoingProperties(), messageParam, aliasEncryptUser);
						jsonEncrypt = new JsonEncrypt(encryptionKS, aliasEncryptUser, aliasEncryptPassword, encryptionKeyAlgorithm, encryptionContentAlgorithm,  jwtHeaders, jweOptions);
					}else {
						if(encryptionTrustStoreKS!=null) {
							JwtHeaders jwtHeaders = JOSEUtils.getJwtHeaders(messageSecurityContext.getOutgoingProperties(), messageParam, aliasEncryptUser, encryptionTrustStoreKS);
							jsonEncrypt = new JsonEncrypt(encryptionTrustStoreKS, aliasEncryptUser, encryptionKeyAlgorithm, encryptionContentAlgorithm, jwtHeaders, jweOptions);
						}
						else {
							JwtHeaders jwtHeaders = JOSEUtils.getJwtHeaders(messageSecurityContext.getOutgoingProperties(), messageParam, aliasEncryptUser, encryptionJWKSet);
							jsonEncrypt = new JsonEncrypt(encryptionJWKSet.getJsonWebKeys(), encryptionSymmetric, aliasEncryptUser, encryptionKeyAlgorithm, encryptionContentAlgorithm, jwtHeaders, jweOptions);
						}
					}
				}
		

				
				
				// **************** Process **************************
				
				String contentEncrypted = jsonEncrypt.encrypt(restJsonMessage.getContent());
				restJsonMessage.updateContent(contentEncrypted);
				

			} // fine encrypt


		}
		catch(Exception e){
			
			String msg = Utilities.getInnerNotEmptyMessageException(e).getMessage();
			
			Throwable innerExc = Utilities.getLastInnerException(e);
			String innerMsg = null;
			if(innerExc!=null){
				innerMsg = innerExc.getMessage();
			}
			
			String messaggio = null;
			if(msg!=null){
				messaggio = new String(msg);
				if(innerMsg!=null && !innerMsg.equals(msg)){
					messaggio = messaggio + " ; " + innerMsg;
				}
			}
			else{
				if(innerMsg!=null){
					messaggio = innerMsg;
				}
			}
			
			SecurityException wssException = new SecurityException(e.getMessage(), e);
			wssException.setMsgErrore(messaggio);
			throw wssException;
		}
		
    }

 
}





