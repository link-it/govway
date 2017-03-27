/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

import java.security.Key;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12820 $, $Date: 2017-03-22 15:27:09 +0100 (Wed, 22 Mar 2017) $
 */
public class XmlDecrypt extends AbstractXmlCipher {
	
	// SYMMETRIC
	
	public XmlDecrypt(int mode, SecretKey secretKey) throws UtilsException{
		super(XMLCipher.DECRYPT_MODE, secretKey);
	}
	public XmlDecrypt(int mode, SecretKey secretKey,boolean addBouncyCastleProvider) throws UtilsException{
		super(XMLCipher.DECRYPT_MODE, secretKey, addBouncyCastleProvider);
	}
	
	// Questo costruttore non ha senso, una chiave segreta ci vuole!
//	public XmlDecrypt(String keyAlgorithm, boolean addBouncyCastleProvider)
//			throws UtilsException {
//		super(XMLCipher.DECRYPT_MODE, keyAlgorithm, addBouncyCastleProvider);
//	}
//	public XmlDecrypt(String keyAlgorithm) throws UtilsException {
//		super(XMLCipher.DECRYPT_MODE, keyAlgorithm);
//	}

	
	// ASYMMETRIC
	
	public XmlDecrypt(Certificate certificate, boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.DECRYPT_MODE, certificate, addBouncyCastleProvider);
	}
	public XmlDecrypt(Certificate certificate) throws UtilsException {
		super(XMLCipher.DECRYPT_MODE, certificate);
	}

	public XmlDecrypt(Key key, boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.DECRYPT_MODE, key, addBouncyCastleProvider);
	}
	public XmlDecrypt(Key key) throws UtilsException {
		super(XMLCipher.DECRYPT_MODE, key);
	}

	public XmlDecrypt(KeyStore keystore, boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.DECRYPT_MODE, keystore, addBouncyCastleProvider);
	}
	public XmlDecrypt(KeyStore keystore) throws UtilsException {
		super(XMLCipher.DECRYPT_MODE, keystore);
	}

	
	public XmlDecrypt(KeyStore keystore, String alias, boolean addBouncyCastleProvider)
			throws UtilsException {
		super(XMLCipher.DECRYPT_MODE, keystore, alias, addBouncyCastleProvider);
	}
	public XmlDecrypt(KeyStore keystore, String alias) throws UtilsException {
		super(XMLCipher.DECRYPT_MODE, keystore, alias);
	}

	public XmlDecrypt(KeyStore keystore, String alias, String passwordPrivateKey,
			boolean addBouncyCastleProvider) throws UtilsException {
		super(XMLCipher.DECRYPT_MODE, keystore, alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	public XmlDecrypt(KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException {
		super(XMLCipher.DECRYPT_MODE, keystore, alias, passwordPrivateKey);
	}


	public Document decrypt(Document document) throws UtilsException{
		return this.decrypt(document, document.getDocumentElement());
	}
	public Document decrypt(Element element) throws UtilsException{
		return this.decrypt(element.getOwnerDocument(), element);
	}
	public Document decrypt(Document document, Element element) throws UtilsException{
		try{
			XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(element);
			
			// Estrazione element xenc
			
			Element encryptedDataElement = null;
			Object o = xpathEngine.getMatchPattern(element, dnc, "//{http://www.w3.org/2001/04/xmlenc#}EncryptedData", XPathReturnType.NODE);
			if (o == null) {
				throw new Exception("EncryptData not found");
			} 
			encryptedDataElement = (Element) o;
			
			Element encryptedKeyElement = null;
			if(super.isEncryptedKey()){
				o = xpathEngine.getMatchPattern(encryptedDataElement, dnc, "//{http://www.w3.org/2001/04/xmlenc#}EncryptedKey", XPathReturnType.NODE);
				if (o == null) {
					throw new Exception("EncryptedKey not found");
				} 
				encryptedKeyElement = (Element) o;
			}
			
			// Comprensione algoritmi
			
			org.apache.xml.security.encryption.XMLCipher xmlCipherReaderAlgo = super.getXMLCipher();
			EncryptedData encryptedData = xmlCipherReaderAlgo.loadEncryptedData(document, encryptedDataElement);
			EncryptedKey encryptedKey = null;
			if(super.isEncryptedKey()){
				encryptedKey = xmlCipherReaderAlgo.loadEncryptedKey(document, encryptedKeyElement);
			}
			String encryptAlgorithm = encryptedData.getEncryptionMethod().getAlgorithm();
			if(super.isEncryptedKey()){
				org.apache.xml.security.encryption.XMLCipher xmlCipherUnwrap = super.getXMLCipherUnwrappedKey();
				super.secretKey = (SecretKey) xmlCipherUnwrap.decryptKey(encryptedKey, encryptAlgorithm);
			}
					
			org.apache.xml.security.encryption.XMLCipher xmlCipher = super.getXMLCipher(encryptAlgorithm);
			return xmlCipher.doFinal(document, encryptedDataElement);			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
