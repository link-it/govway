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


package org.openspcoop2.security.message.xml;

import java.security.KeyStore;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
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
import org.openspcoop2.security.message.utils.SignatureBean;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.security.SymmetricKeyWrappedMode;
import org.openspcoop2.utils.security.VerifyXmlSignature;
import org.openspcoop2.utils.security.XmlDecrypt;
import org.w3c.dom.Element;



/**
 * Classe per la gestione della WS-Security (WSDoAllReceiver).
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MessageSecurityReceiver_xml extends AbstractRESTMessageSecurityReceiver {

	private VerifyXmlSignature xmlVerifierSignature = null;
	private XmlDecrypt xmlDecrypt = null;

	@Override
	public void process(MessageSecurityContext messageSecurityContext,OpenSPCoop2Message messageParam,Busta busta) throws SecurityException{
		
		boolean signatureProcess = false;
		boolean encryptProcess = false;
		try{
			
			if(ServiceBinding.REST.equals(messageParam.getServiceBinding())==false){
				throw new SecurityException(XMLCostanti.XML_ENGINE_DESCRIPTION+" usable only with REST Binding");
			}
			if(MessageType.XML.equals(messageParam.getMessageType())==false) {
				throw new SecurityException(XMLCostanti.XML_ENGINE_DESCRIPTION+" usable only with REST Binding and a xml message, found: "+messageParam.getMessageType());
			}
			OpenSPCoop2RestXmlMessage restXmlMessage = messageParam.castAsRestXml();
			
			
			
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
					throw new SecurityException(XMLCostanti.XML_ENGINE_DESCRIPTION+"; action '"+actions[i]+"' unsupported");
				}
			}
			
			if(encrypt && signature) {
				throw new SecurityException(XMLCostanti.XML_ENGINE_DESCRIPTION+" usable only with one function beetwen encrypt or signature");
			}
			if(!encrypt && !signature) {
				throw new SecurityException(XMLCostanti.XML_ENGINE_DESCRIPTION+" require one function beetwen encrypt or signature");
			}
			
			
			
			if(signature) {
				
				
				// **************** Leggo parametri signature store **************************
							
				SignatureBean bean = null;
				try {
					bean = KeystoreUtils.getReceiverSignatureBean(messageSecurityContext);
				}catch(Exception e) {
					throw e;
				}
					
				KeyStore signatureKS = bean.getKeystore();
				KeyStore signatureTrustStoreKS = bean.getTruststore();
				String aliasSignatureUser = bean.getUser();

				if(signatureKS==null && signatureTrustStoreKS==null) {
					throw new SecurityException(XMLCostanti.XML_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+" require truststore");
				}
				if(aliasSignatureUser==null) {
					throw new SecurityException(XMLCostanti.XML_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION+" require alias certificate");
				}
					
				if(signatureTrustStoreKS!=null) {
					this.xmlVerifierSignature = new VerifyXmlSignature(signatureTrustStoreKS, aliasSignatureUser);	
				}
				else {
					this.xmlVerifierSignature = new VerifyXmlSignature(signatureKS, aliasSignatureUser);	
				}
				

				// **************** Process **************************
				
				signatureProcess = true; // le eccezioni lanciate da adesso sono registrato con codice relative alla verifica
				boolean verify = this.xmlVerifierSignature.verify(restXmlMessage.getContent(),false);
				if(!verify) {
					throw new Exception("Signature verification failed");
				}
				
			} // fine signature
			
			
			
			else if(encrypt) {
				
				
				// **************** Leggo parametri encryption store **************************
							
				EncryptionBean bean = null;
				try {
					bean = KeystoreUtils.getReceiverEncryptionBean(messageSecurityContext);
				}catch(Exception e) {
					throw e;
				}
				
				KeyStore encryptionKS = bean.getKeystore();
				boolean encryptionSymmetric = bean.isEncryptionSimmetric();
				SymmetricKeyWrappedMode encryptionSymmetricWrappedMode = null;
				if(encryptionSymmetric) {
					encryptionSymmetricWrappedMode = SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_SYMMETRIC_KEY;
				}
				else {
					encryptionSymmetricWrappedMode = SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY;
				}
				String aliasEncryptUser = bean.getUser();
				String aliasEncryptPassword = bean.getPassword();

				if(encryptionSymmetric) {
					String encryptionSymmetricWrapped = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_SYMMETRIC_WRAPPED);
					if(encryptionSymmetricWrapped!=null) {
						if(SecurityConstants.DECRYPTION_SYMMETRIC_WRAPPED_TRUE.equalsIgnoreCase(encryptionSymmetricWrapped)) {
							encryptionSymmetricWrappedMode = SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_SYMMETRIC_KEY;
						}
						else if(SecurityConstants.DECRYPTION_SYMMETRIC_WRAPPED_FALSE.equalsIgnoreCase(encryptionSymmetricWrapped)) {
							encryptionSymmetricWrappedMode = SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED;
						}
					}
				}
				
				if(encryptionKS==null) {
					throw new SecurityException(XMLCostanti.XML_ENGINE_DECRYPT_DESCRIPTION+" require keystore");
				}
				if(aliasEncryptUser==null) {
					if(encryptionSymmetric) {
						throw new SecurityException(XMLCostanti.XML_ENGINE_DECRYPT_DESCRIPTION+" require alias secret key");
					}
					else {
						throw new SecurityException(XMLCostanti.XML_ENGINE_DECRYPT_DESCRIPTION+" require alias private key");
					}
				}
				if(aliasEncryptPassword==null) {
					if(encryptionSymmetric) {
						throw new SecurityException(XMLCostanti.XML_ENGINE_DECRYPT_DESCRIPTION+" require password secret key");
					}
					else {
						throw new SecurityException(XMLCostanti.XML_ENGINE_DECRYPT_DESCRIPTION+" require password private key");
					}
				}

				this.xmlDecrypt = new XmlDecrypt(encryptionKS, encryptionSymmetric, encryptionSymmetricWrappedMode, aliasEncryptUser, aliasEncryptPassword);

				boolean detach = true;
				String encryptionSymmetricWrapped = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.DETACH_SECURITY_INFO);
				if(encryptionSymmetricWrapped!=null) {
					if(SecurityConstants.FALSE.equalsIgnoreCase(encryptionSymmetricWrapped)) {
						detach = false;
					}
				}
				Element xmlEncrypted = null;
				if(!detach) {
					xmlEncrypted = (Element) restXmlMessage.getContent().cloneNode(true);
				}
	
				
				// **************** Process **************************
							
				encryptProcess = true; // le eccezioni lanciate da adesso sono registrato con codice relative alla verifica
				if(detach) {
					this.xmlDecrypt.decrypt(restXmlMessage.getContent());
				}
				else {
					this.xmlDecrypt.decrypt(xmlEncrypted);
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
				throw new SecurityException(XMLCostanti.XML_ENGINE_DESCRIPTION+" usable only with REST Binding");
			}
			if(MessageType.XML.equals(messageParam.getMessageType())==false) {
				throw new SecurityException(XMLCostanti.XML_ENGINE_DESCRIPTION+" usable only with REST Binding and a xml message, found: "+messageParam.getMessageType());
			}
			OpenSPCoop2RestXmlMessage restXmlMessage = messageParam.castAsRestXml();
					
			if(this.xmlVerifierSignature!=null) {
				this.xmlVerifierSignature.detach(restXmlMessage.getContent());
			}
			else if(this.xmlDecrypt!=null) {
				// nop: già fatto prima
			}
			else {
				throw new SecurityException(XMLCostanti.XML_ENGINE_DESCRIPTION+" (detach method) usable only after one function beetwen encrypt or signature");
			}
			
		}catch(Exception e) {
			throw new SecurityException(e.getMessage(), e);
		}
	}

	@Override
	public String getCertificate() throws SecurityException {
		try {
			if(this.xmlVerifierSignature!=null) {
				if(	this.xmlVerifierSignature.getKeyInfo()!=null ) {
					// X509
					if(	this.xmlVerifierSignature.getKeyInfo().getX509Certificate()!=null && this.xmlVerifierSignature.getKeyInfo().getX509Certificate().getIssuerX500Principal()!=null ) {
						return this.xmlVerifierSignature.getKeyInfo().getX509Certificate().getIssuerX500Principal().getName();
					}
					// RSA
					if(	this.xmlVerifierSignature.getKeyInfo().getPublicKey()!=null) {
						// non c'e' una stringa da tornare
					}
				}
				return null;
			}
			else if(this.xmlDecrypt!=null) {
				return null;
			}
			else {
				throw new SecurityException(XMLCostanti.XML_ENGINE_DESCRIPTION+" (getCertificate method) usable only after one function beetwen encrypt or signature");
			}
		}catch(Exception e) {
			throw new SecurityException(e.getMessage(), e);
		}
	}


	
}
