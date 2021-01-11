/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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


package org.openspcoop2.security.message.soapbox;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMSource;

import org.adroitlogic.soapbox.MessageSecurityContext;
import org.adroitlogic.soapbox.SecurityRequest;
import org.adroitlogic.ultraesb.core.MessageImpl;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecuritySender;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.utils.AttachmentProcessingPart;
import org.openspcoop2.security.message.utils.ElementProcessingPart;
import org.openspcoop2.security.message.utils.EncryptionBean;
import org.openspcoop2.security.message.utils.KeystoreUtils;
import org.openspcoop2.security.message.utils.ProcessingPart;
import org.openspcoop2.security.message.utils.ProcessingPartUtils;
import org.openspcoop2.security.message.utils.SignatureBean;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;

/**
 * WSSContext_soapbox
 *
 * @author Andrea Poli (apoli@link.it)
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecuritySender_soapbox implements IMessageSecuritySender{

	
	@Override
	public void process(org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext,OpenSPCoop2Message messageParam) throws SecurityException{
		try{ 	

			if(ServiceBinding.SOAP.equals(messageParam.getServiceBinding())==false){
				throw new SecurityException("SoapBox Engine usable only with SOAP Binding");
			}
			OpenSPCoop2SoapMessage message = messageParam.castAsSoap();
			AbstractXMLUtils xmlUtils = XMLUtils.getInstance(message.getFactory());
			



			// ********** Leggo operazioni ***************
			boolean encrypt = false;
			boolean signature = false;
			boolean timestamp = false;

			String[]actions = ((String)messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ACTION)).split(" ");
			for (int i = 0; i < actions.length; i++) {
				if(SecurityConstants.ENCRYPT_ACTION.equals(actions[i].trim())){
					encrypt = true;
				}
				else if(SecurityConstants.SIGNATURE_ACTION.equals(actions[i].trim())){
					signature = true;
				}
				else if(SecurityConstants.TIMESTAMP_ACTION.equals(actions[i].trim())){
					timestamp = true;
				}
			}
			
			//rilasciato vincolo di abilitazione signature o encryption per abilitare il timestamp
//			if(!signature && !encrypt && timestamp){
//				throw new WSSException("La funzionalita' "+WSSConstants.TIMESTAMP_ACTION+" richiede per essere abilitata almeno una delle seguenti altre funzionalita': "+
//						WSSConstants.ENCRYPT_ACTION+","+WSSConstants.SIGNATURE_ACTION);
//			}
			




			// ********** Inizializzo Header WSS ***************

			SOAPPart sp = message.getSOAPPart();
			//Document d = sp;
			Document d = sp.getDocumentElement().getOwnerDocument();
			MessageSecurityContext msgSecCtx = new MessageSecurityContext(d, new MessageImpl(true, null, "http"));
			Object mustUnderstandObject = messageSecurityContext.getOutgoingProperties().get(SecurityConstants.MUST_UNDERSTAND);
			boolean mustUnderstand = false;
			if(mustUnderstandObject!=null){
				mustUnderstand = SecurityConstants.TRUE.equals(mustUnderstandObject);
			}
			WSSUtils.initWSSecurityHeader(message,messageSecurityContext.getActor(),mustUnderstand);


			
			
			
			
			
			// **************** Inizializzo process per timestamp **************************
			
			TimestampMessageProcessor timestampProc = null;
			if(timestamp){
				timestampProc = new TimestampMessageProcessor();
			}
			
			
			
			
			

			// **************** Inizializzo process per cifrare **************************

			EncryptPartialMessageProcessor encMsgProc = null;
			if(encrypt){
				encMsgProc = new EncryptPartialMessageProcessor();
				encMsgProc.setMessage(message);
				encMsgProc.setActor(messageSecurityContext.getActor());
				encMsgProc.setMustUnderstand(mustUnderstand);

				// encryptionParts
				Object encryptionParts =  messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_PARTS);
				List<ProcessingPart<?,?>> lstEncryptionParts = null;

				if(encryptionParts!=null){
					lstEncryptionParts = ProcessingPartUtils.getEncryptionInstance().getProcessingParts((String)encryptionParts);
				}

				boolean allPresente = false;
				boolean allComplete = false;
				List<AttachmentProcessingPart> lstAttachments = new ArrayList<AttachmentProcessingPart>();
				
				for(ProcessingPart<?,?> part: lstEncryptionParts) {

					if(part instanceof AttachmentProcessingPart) {
						AttachmentProcessingPart attProcessingPart = (AttachmentProcessingPart) part;
						if(attProcessingPart.isAllAttachments()) {
							allPresente = true;
							allComplete = !attProcessingPart.isContent();
						} else {
							lstAttachments.add(attProcessingPart);
						}
						

					} else if(part instanceof ElementProcessingPart) {  
						ElementProcessingPart elProcessingPart = (ElementProcessingPart) part;
						encMsgProc.addElementToEncrypt(elProcessingPart.getPart(),elProcessingPart.isContent());
					}
				}
				
				if(allPresente || lstAttachments.size()>0){
					
					Iterator<?> it = message.getAttachments();
					
					if(it.hasNext()==false){
						throw new Exception("Property "+SecurityConstants.ENCRYPTION_PARTS+ " contiene la richiesta di cifrare attachments, ma il messaggio non ne contiene");
					}
				
					int indexAllegatoEncrypt = 1;
					while (it.hasNext()) {
						AttachmentPart ap = (AttachmentPart) it.next();
						if(allPresente){
							encMsgProc.addAttachmentToEncrypt(ap, !allComplete);
						}else{
							boolean found = false;
							for (int i = 0; i < lstAttachments.size() && !found; i++) {
								if(lstAttachments.get(i).getPart().equals(indexAllegatoEncrypt)) {
									encMsgProc.addAttachmentToEncrypt(ap, !lstAttachments.get(i).isContent());
									found = true;
								}
							}
							indexAllegatoEncrypt++;
						}
					}
					
				}

			}
		
			





			// **************** Inizializzo process per firmare **************************
			
			SignPartialMessageProcessor signMsgProc = null;
			if(signature){
				signMsgProc = (SignPartialMessageProcessor) ClassLoaderUtilities.newInstance(message.getSignPartialMessageProcessorClass());
				signMsgProc.setMessage(message);
				signMsgProc.setActor(messageSecurityContext.getActor());
				signMsgProc.setMustUnderstand(mustUnderstand);

				// encryptionParts
				Object signatureParts =  messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SIGNATURE_PARTS);
				List<ProcessingPart<?,?>> lstSignatureParts = null;

				if(signatureParts!=null){
					lstSignatureParts = ProcessingPartUtils.getSignatureInstance().getProcessingParts((String)signatureParts);
				}

				boolean allPresente = false;
				boolean allComplete = false;
				List<AttachmentProcessingPart> lstAttachments = new ArrayList<AttachmentProcessingPart>();
				
				for(ProcessingPart<?,?> part: lstSignatureParts) {

					if(part instanceof AttachmentProcessingPart) {
						AttachmentProcessingPart attProcessingPart = (AttachmentProcessingPart) part;
						if(attProcessingPart.isAllAttachments()) {
							allPresente = true;
							allComplete = !attProcessingPart.isContent();
						} else {
							lstAttachments.add(attProcessingPart);
						}
						

					} else if(part instanceof ElementProcessingPart) {  
						ElementProcessingPart elProcessingPart = (ElementProcessingPart) part;
						signMsgProc.addElementToSign(elProcessingPart.getPart(),elProcessingPart.isContent());
					}
				}
				
				if(allPresente || lstAttachments.size()>0){
					
					Iterator<?> it = message.getAttachments();
					
					if(it.hasNext()==false){
						throw new Exception("Property "+SecurityConstants.SIGNATURE_PARTS+ " contiene la richiesta di cifrare attachments, ma il messaggio non ne contiene");
					}
				
					int indexAllegatoEncrypt = 1;
					while (it.hasNext()) {
						AttachmentPart ap = (AttachmentPart) it.next();
						if(allPresente){
							signMsgProc.addAttachmentsToSign(ap, !allComplete);
						}else{
							boolean found = false;
							for (int i = 0; i < lstAttachments.size() && !found; i++) {
								if(lstAttachments.get(i).getPart().equals(indexAllegatoEncrypt)) {
									signMsgProc.addAttachmentsToSign(ap, !lstAttachments.get(i).isContent());
									found = true;
								}
							}
							indexAllegatoEncrypt++;
						}
					}
					
				}

			}
		
			




			// **************** Leggo parametri encryption store **************************
			KeyStore encryptionKS = null;
			KeyStore encryptionTrustStoreKS = null;
			boolean encryptionSymmetric = false;
			String aliasEncryptUser = null;
			String aliasEncryptPassword = null;
			if(encrypt){
				
				EncryptionBean bean = KeystoreUtils.getSenderEncryptionBean(messageSecurityContext);
				
				encryptionKS = bean.getKeystore();
				encryptionTrustStoreKS = bean.getTruststore();
				encryptionSymmetric = bean.isEncryptionSimmetric();
				aliasEncryptUser = bean.getUser();
				aliasEncryptPassword = bean.getPassword();

			}







			// **************** Leggo parametri signature store **************************
			KeyStore signatureKS = null;
			KeyStore signatureTrustStoreKS = null;
			String aliasSignatureUser = null;
			String aliasSignaturePassword = null;
			if(signature){
			
				SignatureBean bean = KeystoreUtils.getSenderSignatureBean(messageSecurityContext);
				
				signatureKS = bean.getKeystore();
				signatureTrustStoreKS = bean.getTruststore();
				aliasSignatureUser = bean.getUser();
				aliasSignaturePassword = bean.getPassword();

			}
			 

			
			
			
			
			
			
			// **************** Inizializzo Context for timestamp **************************
			
			if(timestamp){
				Object ttlObject = messageSecurityContext.getOutgoingProperties().get(SecurityConstants.TIMESTAMP_TTL);
				if(ttlObject==null){
					ttlObject = SecurityConstants.TIMESTAMP_SOAPBOX_TTL_DEFAULT;
				}
				String ttl = (String) ttlObject; 
				long ttlLong = -1;
				try{
					ttlLong = Long.parseLong(ttl);
				}catch(Exception e){
					throw new Exception("Indicazione "+SecurityConstants.TIMESTAMP_TTL+" non corretta: "+e.getMessage());
				}
				msgSecCtx.getTimestampRequest().setTimeForExpiryMillis(ttlLong*1000l); // secondi
				
				//precision in milliseconds in Timestamp handling
				Object precisionObject = messageSecurityContext.getOutgoingProperties().get(SecurityConstants.TIMESTAMP_PRECISION);
				if(precisionObject==null){
					precisionObject = SecurityConstants.TRUE;
				}
				String precision = (String) precisionObject; 
				boolean precisionBoolean;
				try{
					precisionBoolean = Boolean.parseBoolean(precision);
				}catch(Exception e){
					throw new Exception("Indicazione "+SecurityConstants.TIMESTAMP_PRECISION+" non corretta: "+e.getMessage());
				}

				msgSecCtx.setProperty(SecurityConstants.TIMESTAMP_PRECISION, precisionBoolean);

			}
			
			
			
			



			// **************** Inizializzo Secure Context for encryption **************************
			
			org.openspcoop2.security.message.soapbox.SecurityConfig securityConfig_encryption = null;
			if(encrypt){
				updateSecurityContextForEncryption(msgSecCtx, messageSecurityContext.getOutgoingProperties(), aliasEncryptUser);
				
				Map<String, String> passwordMap_encryption = new HashMap<String, String>();
				passwordMap_encryption.put(aliasEncryptUser, aliasEncryptPassword);

				if(encryptionTrustStoreKS==null){
					encryptionTrustStoreKS = encryptionKS;
				}
				securityConfig_encryption = new org.openspcoop2.security.message.soapbox.SecurityConfig(encryptionKS, encryptionTrustStoreKS, passwordMap_encryption);
				securityConfig_encryption.setSymmetricSharedKey(encryptionSymmetric);
			}










			// **************** Inizializzo Secure Context for signature **************************
			org.openspcoop2.security.message.soapbox.SecurityConfig securityConfig_signature = null;
			if(signature){
				
				updateSecurityContextForSignature(msgSecCtx, messageSecurityContext.getOutgoingProperties(), aliasSignatureUser);
								
				Map<String, String> passwordMap_signature = new HashMap<String, String>();
				passwordMap_signature.put(aliasSignatureUser, aliasSignaturePassword);

				if(signatureTrustStoreKS==null){
					signatureTrustStoreKS = signatureKS;
				}
				securityConfig_signature = new org.openspcoop2.security.message.soapbox.SecurityConfig(signatureKS, signatureTrustStoreKS, passwordMap_signature);
			}				
			








			// **************** Process **************************

			// Devo rileggerle per eseguire nell'ordine
			actions = ((String)messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ACTION)).split(" ");
			boolean actionSignatureOrEncryptDo = false;
			for (int i = 0; i < actions.length; i++) {
				if(SecurityConstants.ENCRYPT_ACTION.equals(actions[i].trim())){
					if(actionSignatureOrEncryptDo){
						// Refresh serve anche per la encrypt ??
						//byte[]xmlCifrato=this.xmlUtils.toByteArray(msgSecCtx.getDocument().getDocumentElement());
						//message.getSOAPPart().setContent(new DOMSource(this.xmlUtils.newElement(xmlCifrato)));
						//signMsgProc.setMessage(message);
						//Document dUpdated = message.getSOAPPart().getDocumentElement().getOwnerDocument();
						//msgSecCtx = new MessageSecurityContext(dUpdated, new MessageImpl(true, null, "http"));
						//updateSecurityContextForEncryption(msgSecCtx, messageSecurityContext.getOutgoingProperties(), aliasEncryptUser);
					}
					encMsgProc.process(securityConfig_encryption, msgSecCtx);
					actionSignatureOrEncryptDo = true;
				}
				else if(SecurityConstants.SIGNATURE_ACTION.equals(actions[i].trim())){
					if(actionSignatureOrEncryptDo){
						byte[]xmlCifrato=xmlUtils.toByteArray(msgSecCtx.getDocument().getDocumentElement());
						message.getSOAPPart().setContent(new DOMSource(xmlUtils.newElement(xmlCifrato)));
						signMsgProc.setMessage(message);
						Document dUpdated = message.getSOAPPart().getDocumentElement().getOwnerDocument();
						msgSecCtx = new MessageSecurityContext(dUpdated, new MessageImpl(true, null, "http"));
						updateSecurityContextForSignature(msgSecCtx, messageSecurityContext.getOutgoingProperties(), aliasSignatureUser);
					}
					signMsgProc.process(securityConfig_signature, msgSecCtx);
					actionSignatureOrEncryptDo = true;
				}
				else if(SecurityConstants.TIMESTAMP_ACTION.equals(actions[i].trim())){
					if(securityConfig_signature!=null){
						timestampProc.process(securityConfig_signature,msgSecCtx);
					}
					else if(securityConfig_encryption!=null){
						timestampProc.process(securityConfig_encryption,msgSecCtx);
					} else {
						timestampProc.process(null,msgSecCtx);
					}
				}
			}




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
			
			// L'if scopre l'eventuale motivo preciso riguardo al fallimento della cifratura/firma.
			if(Utilities.existsInnerException(e, org.adroitlogic.soapbox.ElementNotFoundException.class)){
				Throwable t = Utilities.getLastInnerException(e);
				if(t instanceof org.adroitlogic.soapbox.ElementNotFoundException){
					String notFoundMessage = t.getMessage();
					messaggio = t.getMessage().substring(0, notFoundMessage.indexOf("not found as a child of"));
					messaggio = messaggio + "not found as a child of SoapEnvelope";
				}
			}
			
			SecurityException wssException = new SecurityException(e.getMessage(), e);
			wssException.setMsgErrore(messaggio);
			throw wssException;

		}

	}


	
	private void updateSecurityContextForSignature(MessageSecurityContext msgSecCtx, Hashtable<String, Object> outProps, String aliasSignatureUser) throws Exception {
		
		Object c14nAlgoURI = outProps.get(SecurityConstants.SIGNATURE_C14N_ALGORITHM);
		Object digestAlgoURI = outProps.get(SecurityConstants.SIGNATURE_DIGEST_ALGORITHM);
		Object signatureAlgoURI = outProps.get(SecurityConstants.SIGNATURE_ALGORITHM);
		Object wsiBPCompliant = outProps.get(SecurityConstants.IS_BSP_COMPLIANT);
		Object signatureKeyIdentifier = outProps.get(SecurityConstants.SIGNATURE_KEY_IDENTIFIER);

		if(c14nAlgoURI!=null){
			msgSecCtx.getSignatureRequest().setC14nAlgoURI((String)c14nAlgoURI);
		}
		if(digestAlgoURI!=null){
			msgSecCtx.getSignatureRequest().setDigestAlgoURI((String)digestAlgoURI);
		}
		if(wsiBPCompliant!=null){
			try{
				msgSecCtx.getSignatureRequest().setWsiBPCompliant(Boolean.parseBoolean((String)wsiBPCompliant));
			}catch(Exception e){
				throw new Exception(SecurityConstants.IS_BSP_COMPLIANT+" con valore non valido (atteso true/false): ["+wsiBPCompliant+"]");
			}
		}else{
			msgSecCtx.getSignatureRequest().setWsiBPCompliant(true);
		}
		if(signatureAlgoURI!=null){
			msgSecCtx.getSignatureRequest().setSignatureAlgoURI((String)signatureAlgoURI);
		}else{
			throw new Exception(SecurityConstants.SIGNATURE_ALGORITHM+" non fornito");
		}
		if(signatureKeyIdentifier!=null){
			if(SecurityConstants.KEY_IDENTIFIER_BST_DIRECT_REFERENCE.equals(signatureKeyIdentifier)){
				msgSecCtx.getSignatureRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.BST_DIRECT_REFERENCE);		
			}
			else if(SecurityConstants.KEY_IDENTIFIER_ISSUER_SERIAL.equals(signatureKeyIdentifier)){
				msgSecCtx.getSignatureRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.ISSUER_SERIAL);		
			}
			else if(SecurityConstants.KEY_IDENTIFIER_SKI.equals(signatureKeyIdentifier)){
				msgSecCtx.getSignatureRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.SKI_KEY_IDENTIFIER);		
			}
			else if(SecurityConstants.KEY_IDENTIFIER_THUMBPRINT.equals(signatureKeyIdentifier)){
				msgSecCtx.getSignatureRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.THUMBPRINT_IDENTIFIER);		
			}
			else if(SecurityConstants.KEY_IDENTIFIER_X509.equals(signatureKeyIdentifier)){
				msgSecCtx.getSignatureRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.X509_KEY_IDENTIFIER);		
			}
			else{
				throw new Exception(SecurityConstants.SIGNATURE_KEY_IDENTIFIER+" not supported ["+signatureKeyIdentifier+"]");
			}
		}else{
			// default: BST
			msgSecCtx.getSignatureRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.BST_DIRECT_REFERENCE);	
		}
		msgSecCtx.getSignatureRequest().setCertAlias(aliasSignatureUser);
	}
	
	private void updateSecurityContextForEncryption(MessageSecurityContext msgSecCtx, Hashtable<String, Object> outProps, String aliasEncryptUser) throws Exception {
		
		Object encryptionKeySizeObject = outProps.get(SecurityConstants.ENCRYPTION_KEY_SIZE);
		int encryptionKeySize = -1;
		if(encryptionKeySizeObject!=null){
			encryptionKeySize = Integer.parseInt((String)encryptionKeySizeObject);
		}

		Object encryptionSymmetricAlgoritm = outProps.get(SecurityConstants.ENCRYPTION_SYMMETRIC_ALGORITHM);
		Object encryptionKeyTransport = outProps.get(SecurityConstants.ENCRYPTION_KEY_TRANSPORT_ALGORITHM);
		
		if(encryptionKeySize>0){
			msgSecCtx.getEncryptionRequest().setKeySize(encryptionKeySize);
		}
		if(encryptionSymmetricAlgoritm!=null){
			msgSecCtx.getEncryptionRequest().setSymmetricKeyAlgoURI((String)encryptionSymmetricAlgoritm);
		}
		if(encryptionKeyTransport!=null){
			msgSecCtx.getEncryptionRequest().setEncryptionAlgoURI((String)encryptionKeyTransport);
		}
		Object encryptionKeyIdentifier = outProps.get(SecurityConstants.ENCRYPTION_KEY_IDENTIFIER);
		if(encryptionKeyIdentifier!=null){
			if(SecurityConstants.KEY_IDENTIFIER_BST_DIRECT_REFERENCE.equals(encryptionKeyIdentifier)){
				msgSecCtx.getEncryptionRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.BST_DIRECT_REFERENCE);		
			}
			else if(SecurityConstants.KEY_IDENTIFIER_ISSUER_SERIAL.equals(encryptionKeyIdentifier)){
				msgSecCtx.getEncryptionRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.ISSUER_SERIAL);		
			}
			else if(SecurityConstants.KEY_IDENTIFIER_SKI.equals(encryptionKeyIdentifier)){
				msgSecCtx.getEncryptionRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.SKI_KEY_IDENTIFIER);		
			}
			else if(SecurityConstants.KEY_IDENTIFIER_THUMBPRINT.equals(encryptionKeyIdentifier)){
				msgSecCtx.getEncryptionRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.THUMBPRINT_IDENTIFIER);		
			}
			else if(SecurityConstants.KEY_IDENTIFIER_X509.equals(encryptionKeyIdentifier)){
				msgSecCtx.getEncryptionRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.X509_KEY_IDENTIFIER);		
			}
			else{
				throw new Exception(SecurityConstants.ENCRYPTION_KEY_IDENTIFIER+" not supported ["+encryptionKeyIdentifier+"]");
			}
		}else{
			// default: BST
			msgSecCtx.getEncryptionRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.BST_DIRECT_REFERENCE);	
		}
		
		msgSecCtx.getEncryptionRequest().setCertAlias(aliasEncryptUser);
	}
}





