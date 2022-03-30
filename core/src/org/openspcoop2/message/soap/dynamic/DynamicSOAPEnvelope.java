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

package org.openspcoop2.message.soap.dynamic;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_saaj_impl;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * DynamicSOAPEnvelope
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicSOAPEnvelope<T extends AbstractOpenSPCoop2Message_saaj_impl> implements javax.xml.soap.SOAPEnvelope {

	private AbstractOpenSPCoop2Message_soap_impl<T> wrapped;
	public DynamicSOAPEnvelope(AbstractOpenSPCoop2Message_soap_impl<T> wrapped) {
		this.wrapped = wrapped;
	}

	private javax.xml.soap.SOAPEnvelope _getSOAPEnvelope(){
		try {
			return this.wrapped.getContent().getSOAPPart().getEnvelope();
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	

	// Metodi ottimizzati per il dynamic behaviour
	
	private DynamicSOAPHeader<T> dynamic;
	
	@Override
	public SOAPHeader addHeader() throws SOAPException {
		if(this.wrapped.isSoapHeaderOptimizable()) {
			if(this.dynamic==null) {
				try {
					this.wrapped.getSoapReader().addHeader();
				}catch(Exception e) {
					throw new SOAPException(e.getMessage(),e);
				}
				this.dynamic = new DynamicSOAPHeader<T>(this.wrapped);
				
			}
			return this.dynamic;
		}
		return _getSOAPEnvelope().addHeader();
	}
	
	@Override
	public SOAPHeader getHeader() throws SOAPException {
		if(this.wrapped.isSoapHeaderOptimizable()) {
			if(this.dynamic==null) {
				this.dynamic = new DynamicSOAPHeader<T>(this.wrapped);
			}
			return this.dynamic;
		}
		return _getSOAPEnvelope().getHeader();
	}
	
	public void clearDynamicResources() {
		this.dynamic=null;
	}
	
	
	
	// Metodi implementati in SAAJ

	@Override
	public SOAPElement addAttribute(Name arg0, String arg1) throws SOAPException {
		return _getSOAPEnvelope().addAttribute(arg0, arg1);
	}

	@Override
	public SOAPElement addAttribute(QName arg0, String arg1) throws SOAPException {
		return _getSOAPEnvelope().addAttribute(arg0, arg1);
	}

	@Override
	public SOAPElement addChildElement(Name arg0) throws SOAPException {
		return _getSOAPEnvelope().addChildElement(arg0);
	}

	@Override
	public SOAPElement addChildElement(QName arg0) throws SOAPException {
		return _getSOAPEnvelope().addChildElement(arg0);
	}

	@Override
	public SOAPElement addChildElement(String arg0) throws SOAPException {
		return _getSOAPEnvelope().addChildElement(arg0);
	}

	@Override
	public SOAPElement addChildElement(SOAPElement arg0) throws SOAPException {
		return _getSOAPEnvelope().addChildElement(arg0);
	}

	@Override
	public SOAPElement addChildElement(String arg0, String arg1) throws SOAPException {
		return _getSOAPEnvelope().addChildElement(arg0, arg1);
	}

	@Override
	public SOAPElement addChildElement(String arg0, String arg1, String arg2) throws SOAPException {
		return _getSOAPEnvelope().addChildElement(arg0, arg1, arg2);
	}

	@Override
	public SOAPElement addNamespaceDeclaration(String arg0, String arg1) throws SOAPException {
		return _getSOAPEnvelope().addNamespaceDeclaration(arg0, arg1);
	}

	@Override
	public SOAPElement addTextNode(String arg0) throws SOAPException {
		return _getSOAPEnvelope().addTextNode(arg0);
	}

	@Override
	public QName createQName(String arg0, String arg1) throws SOAPException {
		return _getSOAPEnvelope().createQName(arg0, arg1);
	}

	@Override
	public Iterator<Name> getAllAttributes() {
		return _getSOAPEnvelope().getAllAttributes();
	}

	@Override
	public Iterator<QName> getAllAttributesAsQNames() {
		return _getSOAPEnvelope().getAllAttributesAsQNames();
	}

	@Override
	public String getAttributeValue(Name arg0) {
		return _getSOAPEnvelope().getAttributeValue(arg0);
	}

	@Override
	public String getAttributeValue(QName arg0) {
		return _getSOAPEnvelope().getAttributeValue(arg0);
	}

	@Override
	public Iterator<Node> getChildElements() {
		return _getSOAPEnvelope().getChildElements();
	}

	@Override
	public Iterator<Node> getChildElements(Name arg0) {
		return _getSOAPEnvelope().getChildElements(arg0);
	}

	@Override
	public Iterator<Node> getChildElements(QName arg0) {
		return _getSOAPEnvelope().getChildElements(arg0);
	}

	@Override
	public Name getElementName() {
		return _getSOAPEnvelope().getElementName();
	}

	@Override
	public QName getElementQName() {
		return _getSOAPEnvelope().getElementQName();
	}

	@Override
	public String getEncodingStyle() {
		return _getSOAPEnvelope().getEncodingStyle();
	}

	@Override
	public Iterator<String> getNamespacePrefixes() {
		return _getSOAPEnvelope().getNamespacePrefixes();
	}

	@Override
	public String getNamespaceURI(String arg0) {
		return _getSOAPEnvelope().getNamespaceURI(arg0);
	}

	@Override
	public Iterator<String> getVisibleNamespacePrefixes() {
		return _getSOAPEnvelope().getVisibleNamespacePrefixes();
	}

	@Override
	public boolean removeAttribute(Name arg0) {
		return _getSOAPEnvelope().removeAttribute(arg0);
	}

	@Override
	public boolean removeAttribute(QName arg0) {
		return _getSOAPEnvelope().removeAttribute(arg0);
	}

	@Override
	public void removeContents() {
		_getSOAPEnvelope().removeContents();
	}

	@Override
	public boolean removeNamespaceDeclaration(String arg0) {
		return _getSOAPEnvelope().removeNamespaceDeclaration(arg0);
	}

	@Override
	public SOAPElement setElementQName(QName arg0) throws SOAPException {
		return _getSOAPEnvelope().setElementQName(arg0);
	}

	@Override
	public void setEncodingStyle(String arg0) throws SOAPException {
		_getSOAPEnvelope().setEncodingStyle(arg0);
	}

	@Override
	public void detachNode() {
		_getSOAPEnvelope().detachNode();
	}

	@Override
	public SOAPElement getParentElement() {
		return _getSOAPEnvelope().getParentElement();
	}

	@Override
	public String getValue() {
		return _getSOAPEnvelope().getValue();
	}

	@Override
	public void recycleNode() {
		_getSOAPEnvelope().recycleNode();
	}

	@Override
	public void setParentElement(SOAPElement arg0) throws SOAPException {
		_getSOAPEnvelope().setParentElement(arg0);
	}

	@Override
	public void setValue(String arg0) {
		_getSOAPEnvelope().setValue(arg0);
	}

	@Override
	public String getNodeName() {
		return _getSOAPEnvelope().getNodeName();
	}

	@Override
	public String getNodeValue() throws DOMException {
		return _getSOAPEnvelope().getNodeValue();
	}

	@Override
	public void setNodeValue(String nodeValue) throws DOMException {
		_getSOAPEnvelope().setNodeValue(nodeValue);
	}

	@Override
	public short getNodeType() {
		return _getSOAPEnvelope().getNodeType();
	}

	@Override
	public org.w3c.dom.Node getParentNode() {
		return _getSOAPEnvelope().getParentNode();
	}

	@Override
	public NodeList getChildNodes() {
		return _getSOAPEnvelope().getChildNodes();
	}

	@Override
	public org.w3c.dom.Node getFirstChild() {
		return _getSOAPEnvelope().getFirstChild();
	}

	@Override
	public org.w3c.dom.Node getLastChild() {
		return _getSOAPEnvelope().getLastChild();
	}

	@Override
	public org.w3c.dom.Node getPreviousSibling() {
		return _getSOAPEnvelope().getPreviousSibling();
	}

	@Override
	public org.w3c.dom.Node getNextSibling() {
		return _getSOAPEnvelope().getNextSibling();
	}

	@Override
	public NamedNodeMap getAttributes() {
		return _getSOAPEnvelope().getAttributes();
	}

	@Override
	public Document getOwnerDocument() {
		return _getSOAPEnvelope().getOwnerDocument();
	}

	@Override
	public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
		return _getSOAPEnvelope().insertBefore(newChild, refChild);
	}

	@Override
	public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
		return _getSOAPEnvelope().replaceChild(newChild, oldChild);
	}

	@Override
	public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
		return _getSOAPEnvelope().removeChild(oldChild);
	}

	@Override
	public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
		return _getSOAPEnvelope().appendChild(newChild);
	}

	@Override
	public boolean hasChildNodes() {
		return _getSOAPEnvelope().hasChildNodes();
	}

	@Override
	public org.w3c.dom.Node cloneNode(boolean deep) {
		return _getSOAPEnvelope().cloneNode(deep);
	}

	@Override
	public void normalize() {
		_getSOAPEnvelope().normalize();
	}

	@Override
	public boolean isSupported(String feature, String version) {
		return _getSOAPEnvelope().isSupported(feature, version);
	}

	@Override
	public String getNamespaceURI() {
		return _getSOAPEnvelope().getNamespaceURI();
	}

	@Override
	public String getPrefix() {
		return _getSOAPEnvelope().getPrefix();
	}

	@Override
	public void setPrefix(String prefix) throws DOMException {
		_getSOAPEnvelope().setPrefix(prefix);
	}

	@Override
	public String getLocalName() {
		return _getSOAPEnvelope().getLocalName();
	}

	@Override
	public boolean hasAttributes() {
		return _getSOAPEnvelope().hasAttributes();
	}

	@Override
	public String getBaseURI() {
		return _getSOAPEnvelope().getBaseURI();
	}

	@Override
	public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
		return _getSOAPEnvelope().compareDocumentPosition(other);
	}

	@Override
	public String getTextContent() throws DOMException {
		return _getSOAPEnvelope().getTextContent();
	}

	@Override
	public void setTextContent(String textContent) throws DOMException {
		_getSOAPEnvelope().setTextContent(textContent);
	}

	@Override
	public boolean isSameNode(org.w3c.dom.Node other) {
		return _getSOAPEnvelope().isSameNode(other);
	}

	@Override
	public String lookupPrefix(String namespaceURI) {
		return _getSOAPEnvelope().lookupPrefix(namespaceURI);
	}

	@Override
	public boolean isDefaultNamespace(String namespaceURI) {
		return _getSOAPEnvelope().isDefaultNamespace(namespaceURI);
	}

	@Override
	public String lookupNamespaceURI(String prefix) {
		return _getSOAPEnvelope().lookupNamespaceURI(prefix);
	}

	@Override
	public boolean isEqualNode(org.w3c.dom.Node arg) {
		return _getSOAPEnvelope().isEqualNode(arg);
	}

	@Override
	public Object getFeature(String feature, String version) {
		return _getSOAPEnvelope().getFeature(feature, version);
	}

	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return _getSOAPEnvelope().setUserData(key, data, handler);
	}

	@Override
	public Object getUserData(String key) {
		return _getSOAPEnvelope().getUserData(key);
	}

	@Override
	public String getTagName() {
		return _getSOAPEnvelope().getTagName();
	}

	@Override
	public String getAttribute(String name) {
		return _getSOAPEnvelope().getAttribute(name);
	}

	@Override
	public void setAttribute(String name, String value) throws DOMException {
		_getSOAPEnvelope().setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) throws DOMException {
		_getSOAPEnvelope().removeAttribute(name);
	}

	@Override
	public Attr getAttributeNode(String name) {
		return _getSOAPEnvelope().getAttributeNode(name);
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		return _getSOAPEnvelope().setAttributeNode(newAttr);
	}

	@Override
	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		return _getSOAPEnvelope().removeAttributeNode(oldAttr);
	}

	@Override
	public NodeList getElementsByTagName(String name) {
		return _getSOAPEnvelope().getElementsByTagName(name);
	}

	@Override
	public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
		return _getSOAPEnvelope().getAttributeNS(namespaceURI, localName);
	}

	@Override
	public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
		_getSOAPEnvelope().setAttributeNS(namespaceURI, qualifiedName, value);
	}

	@Override
	public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
		_getSOAPEnvelope().removeAttributeNS(namespaceURI, localName);
	}

	@Override
	public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
		return _getSOAPEnvelope().getAttributeNodeNS(namespaceURI, localName);
	}

	@Override
	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		return _getSOAPEnvelope().setAttributeNodeNS(newAttr);
	}

	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
		return _getSOAPEnvelope().getElementsByTagNameNS(namespaceURI, localName);
	}

	@Override
	public boolean hasAttribute(String name) {
		return _getSOAPEnvelope().hasAttribute(name);
	}

	@Override
	public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
		return _getSOAPEnvelope().hasAttributeNS(namespaceURI, localName);
	}

	@Override
	public TypeInfo getSchemaTypeInfo() {
		return _getSOAPEnvelope().getSchemaTypeInfo();
	}

	@Override
	public void setIdAttribute(String name, boolean isId) throws DOMException {
		_getSOAPEnvelope().setIdAttribute(name, isId);
	}

	@Override
	public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
		_getSOAPEnvelope().setIdAttributeNS(namespaceURI, localName, isId);
	}

	@Override
	public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
		_getSOAPEnvelope().setIdAttributeNode(idAttr, isId);
	}

	@Override
	public SOAPBody addBody() throws SOAPException {
		return _getSOAPEnvelope().addBody();
	}

	@Override
	public Name createName(String arg0) throws SOAPException {
		return _getSOAPEnvelope().createName(arg0);
	}

	@Override
	public Name createName(String arg0, String arg1, String arg2) throws SOAPException {
		return _getSOAPEnvelope().createName(arg0, arg1, arg2);
	}

	@Override
	public SOAPBody getBody() throws SOAPException {
		return _getSOAPEnvelope().getBody();
	}
	
}
