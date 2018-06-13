/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.security.KeyStore;
import java.util.Properties;

import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.AbstractRESTMessageSecurityReceiver;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.utils.EncryptionBean;
import org.openspcoop2.security.message.utils.KeystoreUtils;
import org.openspcoop2.security.message.utils.PropertiesUtils;
import org.openspcoop2.security.message.utils.SignatureBean;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.security.JOSERepresentation;
import org.openspcoop2.utils.security.JsonDecrypt;
import org.openspcoop2.utils.security.JsonVerifySignature;



/**
 * Classe per la gestione della WS-Security (WSDoAllReceiver).
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MessageSecurityReceiver_jose extends AbstractRESTMessageSecurityReceiver {

	private JOSERepresentation joseRepresentation = null;
	private JsonVerifySignature jsonVerifierSignature = null;
	private JsonDecrypt jsonDecrypt = null;

	@Override
	public void process(MessageSecurityContext messageSecurityContext,OpenSPCoop2Message messageParam,Busta busta) throws SecurityException{
		
		boolean signatureProcess = false;
		boolean encryptProcess = false;
		try{
			
			if(ServiceBinding.REST.equals(messageParam.getServiceBinding())==false){
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" usable only with REST Binding");
			}
			if(MessageType.JSON.equals(messageParam.getMessageType())==false) {
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" usable only with REST Binding and a json message, found: "+messageParam.getMessageType());
			}
			OpenSPCoop2RestJsonMessage restJsonMessage = messageParam.castAsRestJson();
			
			
			
			// ********** Leggo operazioni ***************
			boolean encrypt = false;
			boolean signature = false;

			String[]actions = ((String)messageSecurityContext.getIncomingProperties().get(SecurityConstants.ACTION)).split(" ");
			for (int i = 0; i < actions.length; i++) {
				if(SecurityConstants.ENCRYPT_ACTION.equals(actions[i].trim()) || SecurityConstants.DECRYPT_ACTION.equals(actions[i].trim())){
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
					this.joseRepresentation = JOSEUtils.toJOSERepresentation(mode);
				}catch(Exception e) {
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+", '"+SecurityConstants.SIGNATURE_MODE+"' property error: "+e.getMessage(),e);
				}
				
				SignatureBean bean = null;
				NotFoundException notFound = null;
				try {
					bean = PropertiesUtils.getReceiverSignatureBean(messageSecurityContext);
				}catch(NotFoundException e) {
					notFound = e;
				}
				if(bean!=null) {
					Properties signatureProperties = bean.getProperties();
					this.jsonVerifierSignature = new JsonVerifySignature(signatureProperties, this.joseRepresentation);	
				}
				else {	
					KeyStore signatureKS = null;
					KeyStore signatureTrustStoreKS = null;
					String aliasSignatureUser = null;
					try {
						bean = KeystoreUtils.getReceiverSignatureBean(messageSecurityContext);
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
					aliasSignatureUser = bean.getUser();

					if(signatureKS==null && signatureTrustStoreKS==null) {
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+" require truststore");
					}
					if(aliasSignatureUser==null) {
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+" require alias certificate");
					}
					
					String signatureAlgorithm = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_ALGORITHM);
					if(signatureAlgorithm==null || "".equals(signatureAlgorithm.trim())){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+" require '"+SecurityConstants.SIGNATURE_ALGORITHM+"' property");
					}
					
					if(signatureTrustStoreKS!=null) {
						this.jsonVerifierSignature = new JsonVerifySignature(signatureTrustStoreKS, aliasSignatureUser, signatureAlgorithm, this.joseRepresentation);	
					}
					else {
						this.jsonVerifierSignature = new JsonVerifySignature(signatureKS, aliasSignatureUser, signatureAlgorithm, this.joseRepresentation);	
					}
				}
				
				String detachedSignature = null;
				if(JOSERepresentation.DETACHED.equals(this.joseRepresentation)) {
					detachedSignature = this.readDetachedSignatureFromMessage(messageSecurityContext.getIncomingProperties(), 
							restJsonMessage, JOSECostanti.JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION);
				}
				
				
				
				// **************** Process **************************

				signatureProcess = true; // le eccezioni lanciate da adesso sono registrato con codice relative alla verifica
				boolean verify = false;
				try {
					if(JOSERepresentation.DETACHED.equals(this.joseRepresentation)) {
						verify = this.jsonVerifierSignature.verify(detachedSignature, restJsonMessage.getContent());
					}else {
						verify = this.jsonVerifierSignature.verify(restJsonMessage.getContent());
					}
				}catch(Exception e) {
					throw new Exception("Signature verification failed: "+e.getMessage(),e);
				}
				if(!verify) {
					throw new Exception("Signature verification failed");
				}
				
			} // fine signature
			
			
			
			else if(encrypt) {
				
				
				// **************** Leggo parametri encryption store **************************
							
				String mode = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_MODE);
				if(mode==null || "".equals(mode.trim())){
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require '"+SecurityConstants.DECRYPTION_MODE+"' property");
				}
				try {
					this.joseRepresentation = JOSEUtils.toJOSERepresentation(mode);
				}catch(Exception e) {
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+", '"+SecurityConstants.DECRYPTION_MODE+"' property error: "+e.getMessage(),e);
				}
				if(JOSERepresentation.DETACHED.equals(this.joseRepresentation)) {
					throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+", "+SecurityConstants.DECRYPTION_MODE+" '"+mode+"' not supported");
				}
				
				EncryptionBean bean = null;
				NotFoundException notFound = null;
				try {
					bean = PropertiesUtils.getReceiverEncryptionBean(messageSecurityContext);
				}catch(NotFoundException e) {
					notFound = e;
				}
				if(bean!=null) {
					Properties encryptionProperties = bean.getProperties();
					this.jsonDecrypt = new JsonDecrypt(encryptionProperties, this.joseRepresentation);	
				}
				else {	
					KeyStore encryptionKS = null;
					boolean encryptionSymmetric = false;
					String aliasEncryptUser = null;
					String aliasEncryptPassword = null;
					try {
						bean = KeystoreUtils.getReceiverEncryptionBean(messageSecurityContext);
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
					aliasEncryptUser = bean.getUser();
					aliasEncryptPassword = bean.getPassword();

					if(encryptionKS==null) {
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
					if(aliasEncryptPassword==null) {
						if(encryptionSymmetric) {
							throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require password secret key");
						}
						else {
							throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require password private key");
						}
					}

					String encryptionKeyAlgorithm = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_KEY_ALGORITHM);
					if(encryptionKeyAlgorithm==null || "".equals(encryptionKeyAlgorithm.trim())){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require '"+SecurityConstants.ENCRYPTION_KEY_ALGORITHM+"' property");
					}
					
					String encryptionContentAlgorithm = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_CONTENT_ALGORITHM);
					if(encryptionContentAlgorithm==null || "".equals(encryptionContentAlgorithm.trim())){
						throw new SecurityException(JOSECostanti.JOSE_ENGINE_DECRYPT_DESCRIPTION+" require '"+SecurityConstants.ENCRYPTION_CONTENT_ALGORITHM+"' property");
					}
					
					this.jsonDecrypt = new JsonDecrypt(encryptionKS, encryptionSymmetric, aliasEncryptUser, aliasEncryptPassword,
							encryptionKeyAlgorithm, encryptionContentAlgorithm, this.joseRepresentation);	
				}
				
	
				
				// **************** Process **************************
							
				encryptProcess = true; // le eccezioni lanciate da adesso sono registrato con codice relative alla verifica
				try {
					this.jsonDecrypt.decrypt(restJsonMessage.getContent());
				}catch(Exception e) {
					throw new Exception("Decrypt failed: "+e.getMessage(),e);
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
		
			if(ServiceBinding.REST.equals(messageParam.getServiceBinding())==false){
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" usable only with REST Binding");
			}
			if(MessageType.JSON.equals(messageParam.getMessageType())==false) {
				throw new SecurityException(JOSECostanti.JOSE_ENGINE_DESCRIPTION+" usable only with REST Binding and a json message, found: "+messageParam.getMessageType());
			}
			OpenSPCoop2RestJsonMessage restJsonMessage = messageParam.castAsRestJson();
					
			if(this.jsonVerifierSignature!=null) {
				if(JOSERepresentation.DETACHED.equals(this.joseRepresentation)) {
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

	@Override
	public String getCertificate() throws SecurityException {
		return null;
	}


	
}
