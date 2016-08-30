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
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
import java.lang.reflect.Constructor;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.adroitlogic.soapbox.CryptoSupport;
import org.adroitlogic.soapbox.CryptoUtil;
import org.adroitlogic.soapbox.EncryptionRequest;
import org.adroitlogic.soapbox.InvalidMessageDataException;
import org.adroitlogic.soapbox.MessageSecurityContext;
import org.adroitlogic.soapbox.Processor;
import org.adroitlogic.soapbox.SBConstants;
import org.adroitlogic.soapbox.SecurityFailureException;
import org.slf4j.Logger;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.security.message.constants.WSSAttachmentsConstants;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.core.EncryptedDataHeaderBlock;
import com.sun.xml.wss.swa.MimeConstants;

/**
 * ProcessPartialEncryptedMessage
 *
 * @author Andrea Poli <apoli@link.it>
 * @author Giovanni Bussu <bussu@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProcessPartialEncryptedMessage implements Processor {

	protected OpenSPCoop2Message message;
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	protected String actor;
	protected boolean mustUnderstand;
	public void setActor(String actor) {
		this.actor = actor;
	}
	public void setMustUnderstand(boolean mustUnderstand) {
		this.mustUnderstand = mustUnderstand;
	}
	

	public static final Logger logger = LoggerWrapperFactory.getLogger(ProcessPartialEncryptedMessage.class);
    
    /**
     *
     * @param secConfig
     * @param msgSecCtx
     */
    @Override
	public void process(org.adroitlogic.soapbox.SecurityConfig secConfig, MessageSecurityContext msgSecCtx) {

    	Element wsseSecurityElem = null;
        try{
        	wsseSecurityElem = WSSUtils.getWSSecurityHeader(msgSecCtx.getDocument(), this.actor, this.mustUnderstand);
        }catch(Exception e){
			throw new SecurityFailureException(e.getMessage(), e);
		}
    	
        Element elem = CryptoUtil.getSecurityProcessorElement(wsseSecurityElem,
            SBConstants.XENC, "EncryptedKey");
        if (elem == null) {
            if (ProcessPartialEncryptedMessage.logger.isDebugEnabled()) {
                ProcessPartialEncryptedMessage.logger.debug("Message is not encrypted - skipping ProcessEncryptedMessage");
            }
            throw new SecurityFailureException("WS-Security failure - Message is not encrypted");
        }

        Cipher cipher = null;
        byte[] decryptedEphemeralKey = null;
        try {
        	 SecurityConfig secContextOpenSPCoop = (SecurityConfig)secConfig;
        	
            // 1. Find the encryption method
        	String algorithm = CryptoUtil.getFirstChild(
                    elem, SBConstants.XENC, SBConstants.ENCRYPTION_METHOD).getAttribute(SBConstants.ATT_ALGORITHM);
            cipher = CryptoSupport.getInstance().getCipherInstance(algorithm);

            if (ProcessPartialEncryptedMessage.logger.isDebugEnabled()) {
                ProcessPartialEncryptedMessage.logger.debug("Processing EncryptedKey element - encryption method : " + cipher.getAlgorithm());
            }

            // 2. Find the session key used
            byte[] encryptedEphemeralKey = CryptoUtil.decodeBase64EncodedText(
                CryptoUtil.getFirstChild(
                    CryptoUtil.getFirstChild(elem, SBConstants.XENC, SBConstants.CIPHER_DATA),
                    SBConstants.XENC, "CipherValue"));

           
            if(secContextOpenSPCoop.isSymmetricSharedKey()){
            	
            	// 3. Get the symmetric key
            	EncryptionRequest encReq = msgSecCtx.getEncryptionRequest();
            	Key key = secContextOpenSPCoop.getSymmetricKey(encReq.getCertAlias());
            	
            	// 4. Decrypt the session key (ephemeral key)
            	cipher.init(Cipher.UNWRAP_MODE, key);
            	String keyAlgorithm = JCEMapper.getJCEKeyAlgorithmFromURI(algorithm);
            	decryptedEphemeralKey = cipher.unwrap(encryptedEphemeralKey, keyAlgorithm, Cipher.SECRET_KEY).getEncoded();
            	            	
            }
            else{
          
	            // 3. Get the private encryption key
	            PrivateKey privateKey = CryptoUtil.getPrivateKeyFromSecurityTokenReference(secConfig, msgSecCtx,
	                CryptoUtil.getFirstChild(
	                    CryptoUtil.getFirstChild(elem, SBConstants.DS, SBConstants.KEY_INFO),
	                    SBConstants.WSSE, SBConstants.SECURITY_TOKEN_REFERENCE));
	
	            // 4. Decrypt the session key (ephemeral key)
	            cipher.init(Cipher.DECRYPT_MODE, privateKey);
	            decryptedEphemeralKey = cipher.doFinal(encryptedEphemeralKey);
          
            }

        } catch (Exception e) {
            throw new SecurityFailureException("Error decrypting ephemeral key", e);
        } finally {
            CryptoSupport.getInstance().returnCipherInstance(cipher);
        }
       

        // 5. get Reference list
        // According to the W3C XML-Enc this key is used to decrypt _any_ references contained in the reference list
        // Now lookup the references that are encrypted with this key
        Element refListElem = CryptoUtil.getFirstChildOrNull(elem, SBConstants.XENC, SBConstants.REFERENCE_LIST);
        NodeList nl = refListElem.getElementsByTagNameNS(SBConstants.XENC, SBConstants.DATA_REFERENCE);
        if (nl == null) {
            throw new InvalidMessageDataException("No DataReference elements that are signed");
        } else {
            for (int i=0; i<nl.getLength(); i++) {
                Element dataRefElem = (Element) nl.item(i);
                String wsuId = dataRefElem.getAttribute("URI");
            	if (wsuId.charAt(0) == '#') {
            		wsuId = wsuId.substring(1);  // trim first "#" of local ref
            	}

            	decryptDataReference(msgSecCtx, wsuId, decryptedEphemeralKey);
            }
        }
    }
    
    public void decryptDataReference(MessageSecurityContext msgSecCtx, String wsuId, byte[] decryptedeEphemeralKey) {

    	Document doc = msgSecCtx.getDocument();
    	Element refElem = CryptoUtil.findElementById(doc, wsuId, SBConstants.WSU);
        
    	if (refElem == null) {
            refElem = CryptoUtil.findElementById(doc, wsuId, null);
        }
        
        if(refElem != null) {
    			
                Element encData = CryptoUtil.getFirstChild(refElem, SBConstants.XENC, SBConstants.ENCRYPTED_DATA);
                
                String encAlgo = CryptoUtil.getFirstChild(
	                    encData, SBConstants.XENC, "EncryptionMethod").getAttribute(SBConstants.ALGORITHM);
                
                SecretKey symmetricKey = new SecretKeySpec(decryptedeEphemeralKey, JCEMapper.getJCEKeyAlgorithmFromURI(encAlgo));
                
                String type = encData.getAttribute("Type");
                //attachments
                if(type.equals(WSSAttachmentsConstants.ATTACHMENT_COMPLETE_URI) || type.equals(WSSAttachmentsConstants.ATTACHMENT_CONTENT_ONLY_URI)) {
                	try {
                		Class<?> edhb = Class.forName(OpenSPCoop2MessageFactory.getMessageFactory().getEncryptedDataHeaderBlockClass()); 
                		
                		Constructor<?> constructor = edhb.getConstructor(SOAPElement.class);
                		EncryptedDataHeaderBlock xencEncryptedData = (EncryptedDataHeaderBlock) constructor.newInstance((SOAPElement)encData);
                		
                		String uri = xencEncryptedData.getCipherReference(false, null).getAttribute("URI");
                		AttachmentPart part = (AttachmentPart) msgSecCtx.getProperty(uri.substring(4));
		                
		                if(part != null)
		                	ProcessPartialEncryptedMessage.decryptAttachment(part, xencEncryptedData, symmetricKey, type);
		                
		                
		            } catch (Exception e) {
		                throw new InvalidMessageDataException("Failed to decrypt attachment referenced by element with WSU-ID : " + wsuId, e);
		            }
                } else { //xml elements
		            XMLCipher xmlCipher = null;
		            try {
		                xmlCipher = CryptoSupport.getInstance().getXMLCipher(encAlgo);
		                xmlCipher.init(XMLCipher.DECRYPT_MODE, symmetricKey);
		                xmlCipher.doFinal(doc, encData);
		
		            } catch (XMLEncryptionException e) {
		                throw new InvalidMessageDataException("Unsupported algorithm for decryption : " + encAlgo, e);
		            } catch (Exception e) {
		                throw new InvalidMessageDataException("Failed to decrypt element with WSU-ID : " + wsuId, e);
		            } finally {
		                CryptoSupport.getInstance().returnXMLCipherInstance(encAlgo, xmlCipher);
		            }
                }
    		}
    }
    
	public static AttachmentPart decryptAttachment(AttachmentPart part, EncryptedDataHeaderBlock edhb,
    				SecretKey key, String type) throws XWSSecurityException, IOException, SOAPException, MessagingException, Base64DecodingException {
        
        String mimeType = edhb.getMimeType();
        Element dsTransform = (Element)edhb.getTransforms().next();
        
        if (!(dsTransform.getAttribute("Algorithm").equals(
                WSSAttachmentsConstants.ATTACHMENT_CONTENT_ONLY_TRANSFORM_URI) || dsTransform.getAttribute("Algorithm").equals(
                        WSSAttachmentsConstants.ATTACHMENT_COMPLETE_TRANSFORM_URI) || dsTransform.getAttribute("Algorithm").equals(
                                WSSAttachmentsConstants.ATTACHMENT_CIPHERTEXT_TRANSFORM_URI))) {
//            logger.log(Level.SEVERE, "WSS1234.invalid.transform=");
            throw new XWSSecurityException("Unexpected ds:Transform, " + dsTransform.getAttribute("Algorithm"));
        }
        
        // initialize Cipher
        Cipher decryptor = null;
        byte[] cipherOutput = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            part.getDataHandler().writeTo(baos);
            
            byte[] cipherInput  = baos.toByteArray();
            decryptor = CryptoSupport.getInstance().getCipherInstance(edhb.getEncryptionMethodURI());
            
            int ivLen = decryptor.getBlockSize();
            byte[] ivBytes = new byte[ivLen];
            
            System.arraycopy(cipherInput, 0, ivBytes, 0, ivLen);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            
            decryptor.init(Cipher.DECRYPT_MODE, key, iv);
            
            cipherOutput = decryptor.doFinal(cipherInput, ivLen, cipherInput.length-ivLen);
        } catch (Exception e) {
//            logger.log(Level.SEVERE, "WSS1232.failedto.decrypt.attachment", e);
            throw new XWSSecurityException(e);
        } finally {
        	CryptoSupport.getInstance().returnCipherInstance(decryptor);
        }
        
        if (type.equals(WSSAttachmentsConstants.ATTACHMENT_CONTENT_ONLY_URI) || type.equals(WSSAttachmentsConstants.ATTACHMENT_CIPHERTEXT_TRANSFORM_URI)) {
            // update headers and content
            part.setContentType(mimeType);
            
            String[] cLength = part.getMimeHeader(MimeConstants.CONTENT_LENGTH);
            if (cLength != null && !cLength[0].equals(""))
                part.setMimeHeader(MimeConstants.CONTENT_LENGTH, Integer.toString(cipherOutput.length));
            
            part.removeMimeHeader(MimeConstants.CONTENT_TRANSFER_ENCODING);
            
            part.clearContent();
			part.setDataHandler(new javax.activation.DataHandler(new _DS(Base64.decode(cipherOutput), mimeType)));
           
        } else {
            MimeBodyPart decryptedAttachment = new MimeBodyPart(new ByteArrayInputStream(cipherOutput));

            // validate cid
            String uri = edhb.getCipherReference(false, null).getAttribute("URI");
            String dcId = decryptedAttachment.getContentID();
            if (dcId == null || !uri.substring(4).equals(dcId.substring(1, dcId.length()-1))) {
//                logger.log(Level.SEVERE, "WSS1234.unmatched.content-id");
                throw new XWSSecurityException("Content-Ids in encrypted and decrypted attachments donot match");
            }
            
            part.removeAllMimeHeaders();
            
            // copy headers
            Enumeration<?> h_enum = decryptedAttachment.getAllHeaders();
            while (h_enum.hasMoreElements()) {
                Header hdr = (Header)h_enum.nextElement();
                String hname = hdr.getName();
                String hvale = hdr.getValue();
                part.setMimeHeader(hname, hvale);
            }
            
            // set content
            part.clearContent();
            
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            decryptedAttachment.getDataHandler().writeTo(bs);
			part.setDataHandler(new javax.activation.DataHandler(new _DS(Base64.decode(bs.toByteArray()), mimeType)));
        }
        return part;
    }
    
    private static class _DS implements javax.activation.DataSource {
        byte[] content = null;
        String contentType = null;
        
        _DS(byte[] content, String contentType) { this.content = content; this.contentType = contentType; }
        
        @Override
		public java.io.InputStream getInputStream() throws java.io.IOException {
            return new java.io.ByteArrayInputStream(this.content);
        }
        
        @Override
		public java.io.OutputStream getOutputStream() throws java.io.IOException {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            baos.write(this.content, 0, this.content.length);
            return baos;
        }
        
        @Override
		public String getName() { return "_DS"; }
        
        @Override
		public String getContentType() { return this.contentType; }
    }
}