/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.apache.xml.security.keys.KeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**	
 * VerifyXmlSignature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VerifyXmlSignature {

	private KeyStore keystore;
	private Certificate certificate;
	
	private KeyInfo keyInfo;
		
	public VerifyXmlSignature(java.security.KeyStore keystore) throws UtilsException{
		this(new KeyStore(keystore), null, false);
	}
	public VerifyXmlSignature(java.security.KeyStore keystore, String alias) throws UtilsException{
		this(new KeyStore(keystore), alias, false);
	}
	public VerifyXmlSignature(java.security.KeyStore keystore, boolean addBouncyCastleProvider) throws UtilsException{
		this(new KeyStore(keystore), null, addBouncyCastleProvider);
	}
	public VerifyXmlSignature(java.security.KeyStore keystore, String alias, boolean addBouncyCastleProvider) throws UtilsException{
		this(new KeyStore(keystore), alias, addBouncyCastleProvider);
	}
	
	public VerifyXmlSignature(KeyStore keystore) throws UtilsException{
		this(keystore, null, false);
	}
	public VerifyXmlSignature(KeyStore keystore, String alias) throws UtilsException{
		this(keystore, alias, false);
	}
	public VerifyXmlSignature(KeyStore keystore, boolean addBouncyCastleProvider) throws UtilsException{
		this(keystore, null, addBouncyCastleProvider);
	}
	public VerifyXmlSignature(KeyStore keystore, String alias, boolean addBouncyCastleProvider) throws UtilsException{
		this.keystore = keystore;
		if(alias==null){
			this.certificate = this.keystore.getCertificate();
		}
		else{
			this.certificate = this.keystore.getCertificate(alias);
		}
		try{
		
			// Providers
			if(addBouncyCastleProvider){
				BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
				Security.addProvider(bouncyCastleProvider);
			}
			
			org.apache.xml.security.Init.init();
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	private Node signatureElement;

	public boolean verify(Document element, boolean clean) throws UtilsException{
		return this._verify(element.getDocumentElement(), clean);
	}
	public boolean verify(Element element, boolean clean) throws UtilsException{
		return this._verify(element, clean);
	}
	private boolean _verify(Element element, boolean clean) throws UtilsException{
		try{
			
			XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(element);
			Object o = xpathEngine.getMatchPattern(element, dnc, "//{http://www.w3.org/2000/09/xmldsig#}Signature", XPathReturnType.NODE);
			if (o == null) {
				throw new Exception("Signature element not found");
			} 
			this.signatureElement = (Node) o;
			
			org.apache.xml.security.signature.XMLSignature sigXMLSec = new org.apache.xml.security.signature.XMLSignature((Element)this.signatureElement, null);
			
			boolean valida = sigXMLSec.checkSignatureValue((X509Certificate)this.certificate);
			
			this.keyInfo = sigXMLSec.getKeyInfo();
			
			// elimino elemento signature dal document
			if(clean){
				this.detach(element);
			}
			
			return valida;
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	public KeyInfo getKeyInfo() {
		return this.keyInfo;
	}
	
	public void detach(Element element) throws UtilsException {
		try{
			if(this.signatureElement==null) {
				throw new Exception("Signature element not found; invoke 'verify' method first");
			}
			element.removeChild(this.signatureElement);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
