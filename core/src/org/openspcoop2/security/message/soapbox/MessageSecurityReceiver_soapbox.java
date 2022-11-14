/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPPart;

import org.adroitlogic.soapbox.MessageSecurityContext;
import org.adroitlogic.ultraesb.core.MessageImpl;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.AbstractSOAPMessageSecurityReceiver;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageUtilities;
import org.openspcoop2.security.message.engine.WSSUtilities;
import org.openspcoop2.security.message.utils.EncryptionBean;
import org.openspcoop2.security.message.utils.KeystoreUtils;
import org.openspcoop2.security.message.utils.SignatureBean;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.w3c.dom.Document;



/**
 * WSSContext_soapbox
 *
 * @author Andrea Poli (apoli@link.it)
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecurityReceiver_soapbox extends AbstractSOAPMessageSecurityReceiver{

	private X509Certificate [] certificates = null;
	

	@Override
	public void process(org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext,OpenSPCoop2Message messageParam,Busta busta,org.openspcoop2.utils.Map<Object> ctx) throws SecurityException{
		try{
			
			if(ServiceBinding.SOAP.equals(messageParam.getServiceBinding())==false){
				throw new SecurityException("SoapBox Engine usable only with SOAP Binding");
			}
			OpenSPCoop2SoapMessage message = messageParam.castAsSoap();
			
			
			
			// ********** Leggo operazioni ***************
			boolean decrypt = false;
			boolean signature = false;
			boolean timestamp = false;

			String[]actions = ((String)messageSecurityContext.getIncomingProperties().get(SecurityConstants.ACTION)).split(" ");
			for (int i = 0; i < actions.length; i++) {
				if(SecurityConstants.is_ACTION_ENCRYPTION(actions[i].trim())){
					decrypt = true;
				}
				else if(SecurityConstants.SIGNATURE_ACTION.equals(actions[i].trim())){
					signature = true;
				}
				else if(SecurityConstants.TIMESTAMP_ACTION.equals(actions[i].trim())){
					timestamp = true;
				}
			}
			// rilasciato vincolo di abilitazione signature o encryption per abilitare il timestamp
//			if(!signature && !decrypt && timestamp){
//				throw new WSSException("La funzionalita' "+WSSConstants.TIMESTAMP_ACTION+" richiede per essere abilitata almeno una delle seguenti altre funzionalita': "+
//						WSSConstants.ENCRYPT_ACTION+","+WSSConstants.SIGNATURE_ACTION);
//			}
			
			
			
			
			// ********** Inizializzo Header WSS ***************

			SOAPPart sp = message.getSOAPPart();
			//Document d = sp;
			Document d = sp.getDocumentElement().getOwnerDocument();
			Object mustUnderstandObject = messageSecurityContext.getIncomingProperties().get(SecurityConstants.MUST_UNDERSTAND);
			boolean mustUnderstand = false;
			if(mustUnderstandObject!=null){
				mustUnderstand = SecurityConstants.TRUE.equals(mustUnderstandObject);
			}
			MessageSecurityContext msgSecCtx = new MessageSecurityContext(d, new MessageImpl(true, null, "http"));
			Iterator<?> it = message.getAttachments();
			if(it!=null){
				while(it.hasNext()) {
					AttachmentPart part = (AttachmentPart) it.next();
					String contentId = part.getContentId();
					if(contentId.startsWith("<"))
						contentId = contentId.substring(1);
					if(contentId.endsWith(">"))
						contentId = contentId.substring(0,contentId.length()-1);
					msgSecCtx.setProperty(contentId, part); //tolgo < e > dal nome eventualmente
				}
			}
			
			
			
			
			
			
			// **************** Inizializzo process per validare timestamp **************************
			
			ProcessTimestampedMessage verifyTimestampedProc = null;
			if(timestamp){
				verifyTimestampedProc = new ProcessTimestampedMessage();
				
				//future time to live
				Object futureTtlObject = messageSecurityContext.getIncomingProperties().get(SecurityConstants.TIMESTAMP_FUTURE_TTL);
				if(futureTtlObject==null){
					futureTtlObject = SecurityConstants.TIMESTAMP_SOAPBOX_FUTURE_TTL_DEFAULT;
				}
				String ttl = (String) futureTtlObject; 
				long futureTtlLong = -1;
				try{
					futureTtlLong = Long.parseLong(ttl);
				}catch(Exception e){
					throw new Exception("Indicazione "+SecurityConstants.TIMESTAMP_FUTURE_TTL+" non corretta: "+e.getMessage());
				}

				msgSecCtx.setProperty(SecurityConstants.TIMESTAMP_FUTURE_TTL, futureTtlLong * 1000l);

				//strict Timestamp handling
				Object strictObject = messageSecurityContext.getIncomingProperties().get(SecurityConstants.TIMESTAMP_STRICT);
				if(strictObject==null){
					strictObject = SecurityConstants.TRUE;
				}
				String strict = (String) strictObject; 
				boolean strictBoolean = false;
				try{
					strictBoolean = Boolean.parseBoolean(strict);
				}catch(Exception e){
					throw new Exception("Indicazione "+SecurityConstants.TIMESTAMP_STRICT+" non corretta: "+e.getMessage());
				}

				msgSecCtx.setProperty(SecurityConstants.TIMESTAMP_STRICT, strictBoolean);
			}
			
			
			
			
			
			
			// **************** Inizializzo process per decifrare **************************

			ProcessPartialEncryptedMessage decryptMsgProc = null;
			if(decrypt){
				decryptMsgProc = (ProcessPartialEncryptedMessage) ClassLoaderUtilities.newInstance(message.getProcessPartialEncryptedMessageClass());
				decryptMsgProc.setMessage(message);
				decryptMsgProc.setActor(messageSecurityContext.getActor());
				decryptMsgProc.setMustUnderstand(mustUnderstand);
			}
			
			
			
			
			
			
			
			// **************** Inizializzo process per validare la firma **************************
			
			ProcessSignedMessage signMsgProc = null;
			if(signature){
				signMsgProc = new ProcessSignedMessage();
				signMsgProc.setMessage(message);
				signMsgProc.setActor(messageSecurityContext.getActor());
				signMsgProc.setMustUnderstand(mustUnderstand);
			}
			
			
			
			
			
			
			
			
			
			// **************** Leggo parametri decryption store **************************
			KeyStore decryptionKS = null;
			KeyStore decryptionTrustStoreKS = null;
			String aliasDecryptUser = null;
			String aliasDecryptPassword = null;
			boolean decryptionSymmetric = false;
			if(decrypt){
				EncryptionBean bean = KeystoreUtils.getReceiverEncryptionBean(messageSecurityContext,ctx);
				
				decryptionKS = bean.getKeystore();
				decryptionTrustStoreKS = bean.getTruststore();
				decryptionSymmetric = bean.isEncryptionSimmetric();
				aliasDecryptUser = bean.getUser();
				aliasDecryptPassword = bean.getPassword();
			
			}
			
			
			
			
			// **************** Leggo parametri signature store **************************
			KeyStore signatureKS = null;
			KeyStore signatureTrustStoreKS = null;
			String aliasSignatureUser = null;
			String aliasSignaturePassword = null;
			String crlPath = null;
			if(signature){
				
				SignatureBean bean = KeystoreUtils.getReceiverSignatureBean(messageSecurityContext,ctx);
				
				signatureKS = bean.getKeystore();
				signatureTrustStoreKS = bean.getTruststore();
				aliasSignatureUser = bean.getUser();
				aliasSignaturePassword = bean.getPassword();
				crlPath = bean.getCrlPath();

			}
			
			
			
			
			
			
			
			
			
			
			// **************** Inizializzo Secure Context for encryption **************************
			org.openspcoop2.security.message.soapbox.SoapBoxSecurityConfig securityConfig_decryption = null;
			if(decrypt){			
				msgSecCtx.getEncryptionRequest().setCertAlias(aliasDecryptUser);
				
				Map<String, String> passwordMap_decryption = new HashMap<String, String>();
				passwordMap_decryption.put(aliasDecryptUser, aliasDecryptPassword);
				if(decryptionTrustStoreKS==null){
					decryptionTrustStoreKS = decryptionKS;
				}
				securityConfig_decryption = new org.openspcoop2.security.message.soapbox.SoapBoxSecurityConfig(decryptionKS, decryptionTrustStoreKS, passwordMap_decryption,ctx);
				securityConfig_decryption.setSymmetricSharedKey(decryptionSymmetric);
			}
			
			
			
			
			
			
			
			
			
			// **************** Inizializzo Secure Context for signature **************************
			org.openspcoop2.security.message.soapbox.SoapBoxSecurityConfig securityConfig_signature = null;
			if(signature){			
				Map<String, String> passwordMap_signature = new HashMap<String, String>();
				passwordMap_signature.put(aliasSignatureUser, aliasSignaturePassword);
				if(signatureTrustStoreKS==null){
					signatureTrustStoreKS = signatureKS;
				}
				securityConfig_signature = new org.openspcoop2.security.message.soapbox.SoapBoxSecurityConfig(signatureKS, signatureTrustStoreKS, passwordMap_signature,crlPath,ctx);
			}	
			
			
			
			
			
			
			
			
			
			// **************** Process **************************

			// Devo rileggerle per eseguire nell'ordine
			actions = ((String)messageSecurityContext.getIncomingProperties().get(SecurityConstants.ACTION)).split(" ");
			for (int i = actions.length-1; i >= 0; i--) {
				if(SecurityConstants.is_ACTION_ENCRYPTION(actions[i].trim()) || SecurityConstants.is_ACTION_DECRYPTION(actions[i].trim())){
					decryptMsgProc.process(securityConfig_decryption, msgSecCtx);
					//refreshAttachments(message); // per impostare il nuovo contenuto degli attachment, una volta decriptati (non serve se non si imposta il CONTENT_TRANSFER_ENCODING a base64!!)
				}
				else if(SecurityConstants.SIGNATURE_ACTION.equals(actions[i].trim())){
					signMsgProc.process(securityConfig_signature, msgSecCtx);
					this.certificates = signMsgProc.getCertificates();
				}
				else if(SecurityConstants.TIMESTAMP_ACTION.equals(actions[i].trim())){
					if(securityConfig_signature!=null){
						verifyTimestampedProc.process(securityConfig_signature, msgSecCtx);
					}
					else if(securityConfig_decryption!=null){
						verifyTimestampedProc.process(securityConfig_decryption, msgSecCtx);
					} else {
						verifyTimestampedProc.process(null, msgSecCtx);
					}
				}
			}
			
			
			
			
			
			
			
			
			
		} catch (Exception e) {
			
			SecurityException wssException = new SecurityException(e.getMessage(), e);
			
			
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
			
			wssException.setMsgErrore(messaggio);
			
			

			/* ***** CODICE **** */
			
			boolean signature = false;
			boolean encrypt = false;
			try{
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintStream printStream = new PrintStream(bout);
				e.printStackTrace(printStream);
				bout.flush();
				printStream.flush();
				bout.close();
				printStream.close();
				
				if(bout.toString().contains("ProcessSignedMessage")){
					signature = true;
				}
				else if(bout.toString().contains("Signature verification failed")){
					signature = true;
				}
				else if(bout.toString().contains("Message is not signed")){
					signature = true;
				}
				else if(bout.toString().contains("ProcessPartialEncryptedMessage")){
					encrypt = true;
				}
				
			}catch(Exception eClose){}
			
			if(signature){
				wssException.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA);
			}
			else if(encrypt){
				wssException.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA_CIFRATURA_NON_VALIDA);
			}
			else {
				wssException.setCodiceErrore(CodiceErroreCooperazione.SICUREZZA);
			}
			
			
			
			throw wssException;
		}

	}

	@SuppressWarnings("unused")
	private void refreshAttachments(OpenSPCoop2SoapMessage openspcoop2Message) throws MessageException, MessageNotSupportedException, SOAPException{
		java.util.Iterator<?> itAp = openspcoop2Message.getAttachments();
		List<AttachmentPart> v = new ArrayList<AttachmentPart>();
	    while(itAp.hasNext()){
	    	AttachmentPart ap = 
	    		(AttachmentPart) itAp.next();
	    	v.add(ap);
	    }
		openspcoop2Message.removeAllAttachments();
		while(v.size()>0){
			AttachmentPart ap = v.remove(0);
			AttachmentPart apNew = openspcoop2Message.createAttachmentPart();
			apNew.setDataHandler(ap.getDataHandler());
			Iterator<?> itMhs = ap.getAllMimeHeaders();
			while (itMhs.hasNext()) {
				javax.xml.soap.MimeHeader mh = (javax.xml.soap.MimeHeader) itMhs.next();
				//System.out.println("TIPO["+mh.getName()+"] VALUE["+mh.getValue()+"]");
				if(!"Content-Transfer-Encoding".equals(mh.getName())){
					apNew.addMimeHeader(mh.getName(), mh.getValue());
				}
			}
			openspcoop2Message.addAttachmentPart(apNew);
		}
	} 
	

	
	@Override
	public String getCertificate() throws SecurityException{
		if(this.certificates!=null){
			if(this.certificates.length > 0){
				return this.certificates[0].getSubjectX500Principal().toString();
			}
		}
		return null;
	}
	
	@Override
	public X509Certificate getX509Certificate() throws SecurityException {
		if(this.certificates!=null){
			if(this.certificates.length > 0){
				return this.certificates[0];
			}
		}
		return null;
	}

	@Override
	public PublicKey getPublicKey() {
		if(this.certificates!=null){
			if(this.certificates.length > 0){
				return this.certificates[0].getPublicKey();
			}
		}
		return null;
	}
	

	@Override
	public List<Reference> getDirtyElements(
			org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext,
			OpenSPCoop2SoapMessage message) throws SecurityException {
		return WSSUtilities.getDirtyElements(messageSecurityContext, message);
	}

	@Override
	public Map<QName, QName> checkEncryptSignatureParts(
			org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext,
			List<Reference> elementsToClean, OpenSPCoop2SoapMessage message,
			List<SubErrorCodeSecurity> codiciErrore) throws SecurityException {
		return MessageUtilities.checkEncryptSignatureParts(messageSecurityContext, elementsToClean, message, codiciErrore, SecurityConstants.QNAME_WSS_ELEMENT_SECURITY);
	}

	@Override
	public void checkEncryptionPartElements(Map<QName, QName> notResolved,
			OpenSPCoop2SoapMessage message,
			List<SubErrorCodeSecurity> erroriRilevati) throws SecurityException {
		MessageUtilities.checkEncryptionPartElements(notResolved, message, erroriRilevati);
	}

	@Override
	public void cleanDirtyElements(
			org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext,
			OpenSPCoop2SoapMessage message, List<Reference> elementsToClean,
			boolean detachHeaderWSSecurity, boolean removeAllIdRef)
			throws SecurityException {
		WSSUtilities.cleanDirtyElements(messageSecurityContext, message, elementsToClean, detachHeaderWSSecurity,removeAllIdRef);
		
	}

	
}
