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


package org.openspcoop2.utils.security;

import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**	
 * Signature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XmlSignature {

	public final static String DEFAULT_SIGNATURE_METHOD = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"; // SignatureMethod.RSA_SHA1
	public final static String DEFAULT_DIGEST_METHOD = DigestMethod.SHA256;
	public final static String DEFAULT_CANONICALIZATION_METHOD = CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS;
	
	private KeyStore keystore;
	private PrivateKey privateKey;
	private String alias;
	
	private String referenceUri;
	
	private java.util.List<Transform> transforms = new ArrayList<Transform>();
	
	private XMLSignatureFactory xmlSignatureFactory;
	
	private javax.xml.crypto.dsig.keyinfo.KeyInfoFactory keyInfoFactory;
	
	private KeyInfo keyInfo;
	
	public XmlSignature(java.security.KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException{
		this(new KeyStore(keystore), alias, passwordPrivateKey, false);
	}
	public XmlSignature(java.security.KeyStore keystore, String alias, String passwordPrivateKey, boolean addBouncyCastleProvider) throws UtilsException{
		this(new KeyStore(keystore), alias, passwordPrivateKey, addBouncyCastleProvider);
	}
	public XmlSignature(KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException{
		this(keystore, alias, passwordPrivateKey, false);
	}
	public XmlSignature(KeyStore keystore, String alias, String passwordPrivateKey, boolean addBouncyCastleProvider) throws UtilsException{
		this.keystore = keystore;
		this.privateKey = this.keystore.getPrivateKey(alias, passwordPrivateKey);
		this.alias = alias;
		
		try{
		
			// Providers
			if(addBouncyCastleProvider){
				BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
				Security.addProvider(bouncyCastleProvider);
			}
			
			// We assemble the different parts of the Signature element into an XMLSignature object. 
			// These objects are all created and assembled using an XMLSignatureFactory object.
			// An application obtains a DOM implementation of XMLSignatureFactory by calling the following line of code:
			this.xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
			
			// *** default init ***
			//The URI of the object to be signed (We specify a URI of "", which implies the root of the document.)
			this.referenceUri = ""; 
			// A single Transform, the enveloped Transform, which is required for enveloped signatures so that the signature itself is removed before calculating the signature value
			this.addTransform(this.xmlSignatureFactory.newTransform(Transform.ENVELOPED,(TransformParameterSpec) null));
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public String getReferenceUri() {
		return this.referenceUri;
	}
	public void setReferenceUri(String referenceUri) {
		this.referenceUri = referenceUri;
	}
	
	public java.util.List<Transform> getTransforms() {
		return this.transforms;
	}
	public void setTransforms(java.util.List<Transform> transforms) {
		this.transforms = transforms;
	}
	public void addTransform(Transform transform){
		this.transforms.add(transform);
	}
	
	public javax.xml.crypto.dsig.keyinfo.KeyInfoFactory getKeyInfoFactory(){
		if(this.keyInfoFactory==null){
			this.initKeyInfoFactory();
		}
		return this.keyInfoFactory;
	}
	private synchronized void initKeyInfoFactory(){
		if(this.keyInfoFactory==null){
			// Next, we create the optional KeyInfo object, which contains information that enables the recipient to find the key needed to validate the signature. 
			// In this example, we add a KeyValue object containing the public key. 
			// To create KeyInfo and its various subtypes, we use a KeyInfoFactory object, which can be obtained by invoking the getKeyInfoFactory method of the XMLSignatureFactory, as follows:
			this.keyInfoFactory=this.xmlSignatureFactory.getKeyInfoFactory(); 
		}
	}
	
	public KeyInfo getKeyInfo() {
		return this.keyInfo;
	}
	public void setKeyInfo(KeyInfo keyInfo) {
		this.keyInfo = keyInfo;
	}
	public void addRSAKeyInfo() throws UtilsException {
		this.addRSAKeyInfo(this.alias);
	}
	public void addRSAKeyInfo(String alias) throws UtilsException {
		try{
			// We then use the KeyInfoFactory to create the KeyValue object and add it to a KeyInfo object:
			javax.xml.crypto.dsig.keyinfo.KeyValue keyValue = this.getKeyInfoFactory().newKeyValue(this.keystore.getCertificate(alias).getPublicKey());
			this.keyInfo = this.getKeyInfoFactory().newKeyInfo(Collections.singletonList(keyValue)); 
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		
	}
	
	public void addX509KeyInfo() throws UtilsException {
		this.addX509KeyInfo(this.alias);
	}
	public void addX509KeyInfo(String alias) throws UtilsException {
		try{
            List<Object> x509Content = new ArrayList<Object>();
            Certificate cert = this.keystore.getCertificate(alias);
            if(cert instanceof X509Certificate){
            	x509Content.add(((X509Certificate)cert).getSubjectX500Principal().getName());
            }
            x509Content.add(cert);
            javax.xml.crypto.dsig.keyinfo.X509Data x509Data = this.getKeyInfoFactory().newX509Data(x509Content);
            this.keyInfo = this.getKeyInfoFactory().newKeyInfo(Collections.singletonList(x509Data));
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	public void sign(Document element) throws UtilsException{
		this._sign(element.getDocumentElement(), DEFAULT_SIGNATURE_METHOD, DEFAULT_DIGEST_METHOD, DEFAULT_CANONICALIZATION_METHOD);
	}
	public void sign(Element element) throws UtilsException{
		this._sign(element, DEFAULT_SIGNATURE_METHOD, DEFAULT_DIGEST_METHOD, DEFAULT_CANONICALIZATION_METHOD);
	}
	
	public void sign(Document element, String signatureMethod, String digestMethod, String canonicalizationMethod) throws UtilsException{
		this._sign(element.getDocumentElement(), signatureMethod, digestMethod, canonicalizationMethod);
	}
	public void sign(Element element, String signatureMethod, String digestMethod, String canonicalizationMethod) throws UtilsException{
		this._sign(element, signatureMethod, digestMethod, canonicalizationMethod);
	}
	
	public void sign(Document element, SignatureMethod signatureMethod, DigestMethod digestMethod, CanonicalizationMethod canonicalizationMethod) throws UtilsException{
		this._sign(element.getDocumentElement(), signatureMethod, digestMethod, canonicalizationMethod);
	}
	public void sign(Element element, SignatureMethod signatureMethod, DigestMethod digestMethod, CanonicalizationMethod canonicalizationMethod) throws UtilsException{
		this._sign(element, signatureMethod, digestMethod, canonicalizationMethod);
	}
	
	private void _sign(Element element, String signatureMethod, String digestMethod, String canonicalizationMethod) throws UtilsException{
		try{
			this._sign(element, 
					this.xmlSignatureFactory.newSignatureMethod(signatureMethod, null), 
					this.xmlSignatureFactory.newDigestMethod(digestMethod, null), 
					this.xmlSignatureFactory.newCanonicalizationMethod(canonicalizationMethod,(C14NMethodParameterSpec) null));
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private void _sign(Element element, SignatureMethod signatureMethod, DigestMethod digestMethod, CanonicalizationMethod canonicalizationMethod) throws UtilsException{
		try{
			
			// We create an XML Digital Signature XMLSignContext containing input parameters for generating the signature. 
			// Since we are using DOM, we instantiate a DOMSignContext (a subclass of XMLSignContext), and pass it two parameters, 
			// the private key that will be used to sign the document and the root of the document to be signed:
			DOMSignContext domSignContext = new DOMSignContext(this.privateKey, element); 
					
			// We create a Reference object che indica l'elemento da firmare
			Reference signedReference = this.xmlSignatureFactory.newReference(
					this.referenceUri, 
					digestMethod,
					this.transforms, null, null); 

			// Next, we create the SignedInfo object, which is the object that is actually signed
			SignedInfo signedInfo = this.xmlSignatureFactory.newSignedInfo(
						canonicalizationMethod,
					    signatureMethod,
					    Collections.singletonList(signedReference)); // A list of References da firmare
			
			// Finally, we create the XMLSignature object, passing as parameters the SignedInfo and KeyInfo objects that we created earlier:
			XMLSignature xmlSignatureEngine = this.xmlSignatureFactory.newXMLSignature(signedInfo,this.keyInfo);
			
			// Notice that we haven't actually generated the signature yet; we'll do that in the next step.
			// Now we are ready to generate the signature, which we do by invoking the sign method on the XMLSignature object, and pass it the signing context as follows:
			xmlSignatureEngine.sign(domSignContext);
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}


}
