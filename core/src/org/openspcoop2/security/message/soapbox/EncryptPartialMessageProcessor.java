/*
 * AdroitLogic UltraESB Enterprise Service Bus
 *
 * Copyright (c) 2010 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 *
 * GNU Affero General Public License Usage
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program (See LICENSE-AGPL.TXT).
 * If not, see http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Commercial Usage
 *
 * Licensees holding valid UltraESB Commercial licenses may use this file in accordance with the UltraESB Commercial
 * License Agreement provided with the Software or, alternatively, in accordance with the terms contained in a written
 * agreement between you and AdroitLogic.
 *
 * If you are unsure which license is appropriate for your use, or have questions regarding the use of this file,
 * please contact AdroitLogic at info@adroitlogic.com
 */
/*
 * Modificato per supportare le seguenti funzionalita':
 * - firma e cifratura degli attachments
 * - cifratura con chiave simmetrica
 * - supporto CRL 
 * 
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, 
 * either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope 
 * that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.security.message.soapbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

import org.adroitlogic.soapbox.CryptoSupport;
import org.adroitlogic.soapbox.CryptoUtil;
import org.adroitlogic.soapbox.EncryptionRequest;
import org.adroitlogic.soapbox.InvalidMessageDataException;
import org.adroitlogic.soapbox.InvalidOptionException;
import org.adroitlogic.soapbox.MessageSecurityContext;
import org.adroitlogic.soapbox.Processor;
import org.adroitlogic.soapbox.SBConstants;
import org.adroitlogic.soapbox.SecurityFailureException;
import org.slf4j.Logger;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.Base64;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.security.message.constants.WSSAttachmentsConstants;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.xml.wss.core.EncryptedDataHeaderBlock;
import com.sun.xml.wss.swa.MimeConstants;

/**
 * EncryptPartialMessageProcessor
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EncryptPartialMessageProcessor implements Processor {

	private static final Logger logger = LoggerWrapperFactory.getLogger(EncryptPartialMessageProcessor.class);


	private List<QName> elements;
	private List<Boolean> elementsEncryptContent;
	private Map<AttachmentPart, Boolean> attachments;
	private OpenSPCoop2Message message;
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	private String actor;
	private boolean mustUnderstand;
	public void setActor(String actor) {
		this.actor = actor;
	}
	public void setMustUnderstand(boolean mustUnderstand) {
		this.mustUnderstand = mustUnderstand;
	}

	public EncryptPartialMessageProcessor() {
		this.elements = new ArrayList<QName>();
		this.elementsEncryptContent = new ArrayList<Boolean>();
		this.attachments = new HashMap<AttachmentPart, Boolean>();
	}

	public void addElementToEncrypt(QName element , boolean content) {
		this.elements.add(element);
		this.elementsEncryptContent.add(content);
	}

	public void addAttachmentToEncrypt(AttachmentPart part, boolean contentOnly) {
		this.attachments.put(part, contentOnly);
	}


	@Override
	public void process(org.adroitlogic.soapbox.SecurityConfig secConfig, MessageSecurityContext msgSecCtx) {

		Document doc = msgSecCtx.getDocument();

		// ensure existence of the wsse:Security header, and create one if none exists
        Element wsseSecurityElem = null;
        try{
        	wsseSecurityElem = WSSUtils.getWSSecurityHeader(msgSecCtx.getDocument(), this.actor, this.mustUnderstand);
        }catch(Exception e){
			throw new SecurityFailureException(e.getMessage(), e);
		}
		//Element wsseSecurityElem = CryptoUtil.getWSSecurityHeader(doc);
		
		// we will not encrypt an already encrypted document
		if (CryptoUtil.getFirstChildOrNull(wsseSecurityElem, SBConstants.XENC, "EncryptedKey") != null) {
			throw new InvalidMessageDataException("Message is already encrypted");
		}

		Element env = doc.getDocumentElement();

		String secTokenRef = CryptoUtil.getRandomId();

		// by default Body is encrypted
		if(this.elements.isEmpty() && this.attachments.isEmpty()){
			this.elements.add(new QName(env.getNamespaceURI(), "Body"));
			this.elementsEncryptContent.add(true);
		}


		// Process element e attachments
		try {

			processElements(msgSecCtx, secTokenRef);
			processAttachments(msgSecCtx);

		} catch (Exception e) {
			throw new SecurityFailureException("Error encrypting an element or an attachment", e);
		}
		
		
		
		// Process KeyInstance
		
		Cipher cipher = null;
		String cipherValue = null;

		EncryptionRequest encReq = msgSecCtx.getEncryptionRequest();

		try {
			SecurityConfig secConfigOpenSPCoop = (SecurityConfig)secConfig;

			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			cipher = CryptoSupport.getInstance().getCipherInstance(encReq.getEncryptionAlgoURI());

			byte[] encKey = null;
			SecretKey encKeyObject = null;
			try {
				encKeyObject = encReq.getEphemeralKey();
				encKey = encReq.getEphemeralKey().getEncoded();
			} catch (NoSuchAlgorithmException ignore) { /*will/should not happen*/ }

			if( secConfigOpenSPCoop.isSymmetricSharedKey() ){
				
	            int blockSize = cipher.getBlockSize();
	            if(blockSize==0){
	            	blockSize = 8;
	            }
	            //System.out.println("cipher (Algoritmo["+cipher.getAlgorithm()+"]) blksize: " + blockSize);
				cipher.init(Cipher.WRAP_MODE, secConfigOpenSPCoop.getSymmetricKey(encReq.getCertAlias()), new IvParameterSpec(new byte[blockSize]));
				cipherValue = Base64.encode(cipher.wrap(encKeyObject));
			}
			else{

				cipher.init(Cipher.ENCRYPT_MODE, secConfig.getTrustedCertificatesByAlias(encReq.getCertAlias())[0]);
				
				if (cipher.getBlockSize() > 0 && cipher.getBlockSize() < encKey.length) {
					throw new SecurityFailureException("Public key algorithm too weak to encrypt symmetric key " +
							" - cipher block size : " + cipher.getBlockSize() + " encrypted bytes length : " + encKey.length);
				}

				cipherValue = Base64.encode(cipher.doFinal(encKey));
			}

		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new SecurityFailureException("Error preparing cipher for encryption : " + encReq.getEncryptionAlgoURI());
		} catch (Exception e) {
			throw new SecurityFailureException("Failed to encrypt session key", e);
		} finally {
			CryptoSupport.getInstance().returnCipherInstance(cipher);
		}

		Element encryptedKeyElem = createEncryptedKey(doc, encReq, cipherValue, secConfig, msgSecCtx, secTokenRef);
		Element firstChild = CryptoUtil.getFirstElementChild(wsseSecurityElem);
		if (firstChild != null) {
			wsseSecurityElem.insertBefore(encryptedKeyElem, firstChild);
		} else {
			wsseSecurityElem.appendChild(encryptedKeyElem);
		}
		
	}

	private Element createEncryptedKey(Document doc, EncryptionRequest encReq,
			String cipherValue, org.adroitlogic.soapbox.SecurityConfig secConfig, MessageSecurityContext msgSecCtx, String referenceId) {

		// create EncryptedKey element, and append EncryptionMethod with algorithm used
		Element encryptedKeyElem = doc.createElementNS(SBConstants.XENC, "xenc:EncryptedKey");
		encryptedKeyElem.setAttribute("Id", referenceId);
		Element encryptionMethodElem = doc.createElementNS(SBConstants.XENC, "xenc:EncryptionMethod");
		encryptionMethodElem.setAttribute("Algorithm", encReq.getEncryptionAlgoURI());
		encryptedKeyElem.appendChild(encryptionMethodElem);

		// create and attach the keyinfo element
		SecurityConfig securityConfigOpenSPCoop = (SecurityConfig) secConfig;
		if(securityConfigOpenSPCoop.isSymmetricSharedKey()){
			encryptedKeyElem.appendChild(SymmetricCryptoUtils.createKeyInfoElement(doc, encReq, msgSecCtx, secConfig));
		}else{
			//encryptedKeyElem.appendChild(CryptoUtil.createKeyInfoElement(doc, encReq, msgSecCtx, secConfig));
			encryptedKeyElem.appendChild(WSSUtils.createKeyInfoElement(doc, encReq, msgSecCtx, secConfig));
		}

		// create CipherData element and store the encrypted cipher value
		Element cipherDataElem = doc.createElementNS(SBConstants.XENC, "xenc:CipherData");
		Element cipherValueElem = doc.createElementNS(SBConstants.XENC, "xenc:CipherValue");
		cipherValueElem.setTextContent(cipherValue);
		cipherDataElem.appendChild(cipherValueElem);
		encryptedKeyElem.appendChild(cipherDataElem);

		// crate ReferenceList element and store encrypted element IDs
		Element referenceListElem = doc.createElementNS(SBConstants.XENC, "xenc:ReferenceList");
		for (String id : msgSecCtx.getEncryptedReferenceList()) {
			Element dataReferenceElem = doc.createElementNS(SBConstants.XENC, "xenc:DataReference");
			dataReferenceElem.setAttribute("URI", "#" + id);
			referenceListElem.appendChild(dataReferenceElem);
		}
		encryptedKeyElem.appendChild(referenceListElem);

		return encryptedKeyElem;
	}

	private void processElements(MessageSecurityContext msgSecCtx,
			String referenceId) throws Exception {

		Document doc = msgSecCtx.getDocument(); 
		Element env = doc.getDocumentElement();
		// TODO
		int index = 0;
		for(QName name : this.elements) {
			Element encElement = CryptoUtil.getFirstChild(env, name.getNamespaceURI(), name.getLocalPart());
			
			// L'attributo wsu:Id non serve nella encryption
//			String encId = encElement.getAttributeNS(SBConstants.WSU, "Id");
//			if (encId == null || encId.length() == 0) {
//				encId = encElement.getAttribute("Id");
//			}
//			if (encId == null || encId.length() == 0) {
//				encId = CryptoUtil.getRandomId();
//				encElement.setAttributeNS(SBConstants.WSU, "wsu:Id", encId);
//				CryptoUtil.setWsuId(encElement, encId);
//			}

			EncryptionRequest encReq = msgSecCtx.getEncryptionRequest();
			String symEncAlgo = encReq.getSymmetricKeyAlgoURI();
			XMLCipher xmlCipher = null;
			try {
				//xmlCipher = XMLCipher.getInstance(symEncAlgo);       	
				xmlCipher = CryptoSupport.getInstance().getXMLCipher(symEncAlgo);

				xmlCipher.init(XMLCipher.ENCRYPT_MODE, msgSecCtx.getEncryptionRequest().getEphemeralKey());
				EncryptedData encData = xmlCipher.getEncryptedData();
				String encEltId = CryptoUtil.getRandomId();
				encData.setId(encEltId);

				KeyInfo keyInfo = new KeyInfo(doc);
				Element securityTokenReferenceElem = doc.createElementNS(SBConstants.WSSE, "wsse:SecurityTokenReference");
				securityTokenReferenceElem.setAttributeNS(SBConstants.XMLNS, "xmlns:wsse", SBConstants.WSSE);
				Element referenceElem = doc.createElementNS(SBConstants.WSSE, "wsse:Reference");
				referenceElem.setAttribute("URI", "#" + referenceId);
				securityTokenReferenceElem.appendChild(referenceElem);

				keyInfo.addUnknownElement(securityTokenReferenceElem);
				encData.setKeyInfo(keyInfo);

				xmlCipher.doFinal(encElement.getOwnerDocument(), encElement, this.elementsEncryptContent.get(index++));
				msgSecCtx.addEncryptedReference(encEltId);

			} catch (XMLEncryptionException e) {
				throw new InvalidOptionException("Unsupported algorithm : " + symEncAlgo, e);
			} finally {
				try{
					CryptoSupport.getInstance().returnXMLCipherInstance(symEncAlgo, xmlCipher);
				}catch(Exception e){
					EncryptPartialMessageProcessor.logger.error(e.getMessage(),e);
				}
			}
		}
	}

	private static byte[] serializeHeaders(List<MimeHeader> mhs) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {

			StringBuffer line = new StringBuffer();

			for (MimeHeader mh : mhs) {

				String name = mh.getName();
				String vlue = mh.getValue();

				line.append(name);
				line.append(":");
				line.append(vlue);
				line.append("\r\n");

			}

			line.append("\r\n");
			byte[] b = line.toString().getBytes("US-ASCII");
			baos.write(b, 0, b.length);

		} catch (Exception e) {
			throw new Exception(e);
		}

		return baos.toByteArray();
	}

	private void processAttachments(MessageSecurityContext msgSecCtx) throws Exception {

		if(this.attachments.size()<=0){
			return;
		}

		Cipher _attachmentEncryptor = null;
		try {
			EncryptionRequest encReq = msgSecCtx.getEncryptionRequest();

			String encAlgo = encReq.getSymmetricKeyAlgoURI();

			_attachmentEncryptor = CryptoSupport.getInstance().getCipherInstance(encAlgo);
			_attachmentEncryptor.init(Cipher.ENCRYPT_MODE, encReq.getEphemeralKey());

			for(AttachmentPart p : this.attachments.keySet()) {

				boolean contentOnly = this.attachments.get(p);

				// create n push an ED

				EncryptedDataHeaderBlock edhb = new EncryptedDataHeaderBlock();

				String id = CryptoUtil.getRandomId();

				edhb.setId(id);
				edhb.setType( (contentOnly ?  WSSAttachmentsConstants.ATTACHMENT_CONTENT_ONLY_URI : WSSAttachmentsConstants.ATTACHMENT_COMPLETE_URI));
				edhb.setMimeType(p.getContentType());
				String uri = p.getContentId();
				if (uri != null) {           
					if(uri.startsWith("<")){
						uri = "cid:" + uri.substring(1, uri.length()-1);
					}else{
						uri = "cid:" + uri;
					}
				} else {
					uri = p.getContentLocation();
				}

				edhb.getCipherReference(true, uri);
				edhb.setEncryptionMethod(encAlgo);
				edhb.addTransform((contentOnly ?  WSSAttachmentsConstants.ATTACHMENT_CIPHERTEXT_TRANSFORM_URI : WSSAttachmentsConstants.ATTACHMENT_COMPLETE_TRANSFORM_URI));

				//System.out.println(" --PRIMA ENCRYPT --");
				//System.out.println(org.openspcoop2.pdd.logger.Dump.dumpMessage(this.message, true));
    							
				AttachmentPart encPart = EncryptPartialMessageProcessor.encryptAttachment(p, contentOnly, _attachmentEncryptor, this.message.createAttachmentPart());

				//System.out.println(" --DOPO ENCRYPT --");
				//System.out.println(org.openspcoop2.pdd.logger.Dump.dumpMessage(this.message, true));
				
//				this.attachments.remove(p); // Concurrent Modification
				MimeHeaders mhs = new MimeHeaders();
				mhs.addHeader(MimeConstants.CONTENT_ID, p.getContentId());
				this.message.removeAttachments(mhs);
				this.message.addAttachmentPart(encPart);
				
				msgSecCtx.addEncryptedReference(edhb.getId());
				//Element wssHeader = CryptoUtil.getWSSecurityHeader(msgSecCtx.getDocument());
				SOAPHeaderElement wssHeader = WSSUtils.getWSSecurityHeader(this.message, this.actor, this.mustUnderstand);
				
				SOAPElement elementToInsert = edhb.getAsSoapElement();
				wssHeader.addChildElement(elementToInsert);
				
				/*
				SOAPElement soapWssHeader = (SOAPElement) wssHeader;
				SecurityHeader _secHeader = new SecurityHeader(soapWssHeader) ;
				_secHeader.appendChild(edhb);*/            
			}
			this.attachments.clear();
		}
		finally {
			CryptoSupport.getInstance().returnCipherInstance(_attachmentEncryptor);
		}
	}

	private static AttachmentPart encryptAttachment(AttachmentPart part, boolean contentOnly, Cipher cipher, AttachmentPart encPart) throws Exception {

		byte[] cipherInput = (contentOnly) ? EncryptPartialMessageProcessor.getBytesFromAttachments(part.getDataHandler()) : EncryptPartialMessageProcessor.getCipherInput(part); 
		byte[] cipherOutput = cipher.doFinal(cipherInput);
		
		byte[] iv = cipher.getIV();
		byte[] encryptedBytes = new byte[iv.length + cipherOutput.length];

		System.arraycopy(iv, 0, encryptedBytes, 0, iv.length);
		System.arraycopy(cipherOutput, 0, encryptedBytes, iv.length, cipherOutput.length);

		int cLength  = encryptedBytes.length;
		String cType = MimeConstants.APPLICATION_OCTET_STREAM_TYPE;
		String uri   = part.getContentId();
		//Step 9 and 10.SWA spec.

		if (uri != null){
			encPart.setMimeHeader(MimeConstants.CONTENT_ID, uri);
		}else {
			uri = part.getContentLocation();
			if (uri != null){
				encPart.setMimeHeader(MimeConstants.CONTENT_LOCATION, uri);
			}
		}
		encPart.setContentType(cType);
		encPart.setMimeHeader(MimeConstants.CONTENT_LENGTH, Integer.toString(cLength));
		encPart.setMimeHeader(MimeConstants.CONTENT_TRANSFER_ENCODING, "base64");

		EncryptedAttachmentDataHandler dh = new EncryptedAttachmentDataHandler(new EncryptedAttachmentDataSource(encryptedBytes));
		encPart.setDataHandler(dh);

		cipherInput = (contentOnly) ? EncryptPartialMessageProcessor.getBytesFromAttachments(encPart.getDataHandler()) : EncryptPartialMessageProcessor.getCipherInput(encPart); 

		return encPart;
	}

	private static byte[] getCipherInput(AttachmentPart part) throws Exception,
	SOAPException, IOException {
		byte[] cipherInput;
		@SuppressWarnings("unchecked")
		byte[] headers = EncryptPartialMessageProcessor.getAttachmentHeaders(part.getAllMimeHeaders());
		byte[] content = EncryptPartialMessageProcessor.getBytesFromAttachments(part.getDataHandler());

		cipherInput = new byte[headers.length+content.length];

		System.arraycopy(headers, 0, cipherInput, 0, headers.length);
		System.arraycopy(content, 0, cipherInput, headers.length, content.length);
		return cipherInput;
	}

	private static byte[] getAttachmentHeaders(Iterator<MimeHeader> mhItr) throws Exception {

		List<MimeHeader> mhs = new ArrayList<MimeHeader>();
		while (mhItr.hasNext()) mhs.add(mhItr.next());
		return EncryptPartialMessageProcessor.serializeHeaders(mhs);

	}

	private static byte[] getBytesFromAttachments(DataHandler dh) throws SOAPException, IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		dh.writeTo(baos);
		return Base64.encode(baos.toByteArray()).getBytes("US-ASCII");

	}

	private static class EncryptedAttachmentDataHandler extends javax.activation.DataHandler {

		EncryptedAttachmentDataHandler(javax.activation.DataSource ds) {
			super(ds);
		}

		@Override
		public void writeTo(OutputStream os) throws java.io.IOException {
			((ByteArrayOutputStream) getDataSource().getOutputStream()).writeTo(os);
		}
	}

	private static class EncryptedAttachmentDataSource implements javax.activation.DataSource {
		byte[] datasource;

		EncryptedAttachmentDataSource(byte[] ds) {
			this.datasource = ds;
		}

		@Override
		public String getContentType() {
			return MimeConstants.APPLICATION_OCTET_STREAM_TYPE;
		}

		@Override
		public InputStream getInputStream() throws java.io.IOException {
			return new ByteArrayInputStream(this.datasource);
		}

		@Override
		public String getName() {
			return "Encrypted Attachment DataSource";
		}

		@Override
		public OutputStream getOutputStream() throws java.io.IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(this.datasource, 0, this.datasource.length);
			return baos;
		}
	}

}
