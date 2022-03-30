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
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_saaj_impl;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * DynamicSOAPHeader
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicSOAPHeader<T extends AbstractOpenSPCoop2Message_saaj_impl> implements javax.xml.soap.SOAPHeader {

	private AbstractOpenSPCoop2Message_soap_impl<T> wrapped;
	public DynamicSOAPHeader(AbstractOpenSPCoop2Message_soap_impl<T> wrapped) {
		this.wrapped = wrapped;
	}

	private javax.xml.soap.SOAPHeader _getSOAPHeader(boolean modifica){
		try {
			if(this.wrapped.isSoapHeaderOptimizable()) {
				OpenSPCoop2MessageSoapStreamReader soapReader = this.wrapped.getSoapReader();
				// se arrivo a chiamare questo metodo significa che sono "dentro" un header.
				// Quindi se non esiste lo devo creare
				javax.xml.soap.SOAPHeader hdr = null;
				if(modifica) {
					hdr = soapReader.getModifiableHeader();
				}
				else {
					hdr = soapReader.getHeader();
				}
				if(hdr==null) {
					hdr = soapReader.addHeader();
				}
				return hdr;
			}
			
			return this.wrapped.getContent().getSOAPPart().getEnvelope().getHeader();
		}catch(Throwable t) {
			MessageUtils.registerParseException(this.wrapped, t, true);
			throw new RuntimeException(t.getMessage(),t);
		}
	}

	
	
	// Metodi ottimizzati per il dynamic behaviour
	
	@Override
	public SOAPElement addAttribute(Name arg0, String arg1) throws SOAPException {
		return _getSOAPHeader(true).addAttribute(arg0, arg1);
	}

	@Override
	public SOAPElement addAttribute(QName arg0, String arg1) throws SOAPException {
		return _getSOAPHeader(true).addAttribute(arg0, arg1);
	}

	@Override
	public SOAPElement addChildElement(Name arg0) throws SOAPException {
		return _getSOAPHeader(true).addChildElement(arg0);
	}

	@Override
	public SOAPElement addChildElement(QName arg0) throws SOAPException {
		return _getSOAPHeader(true).addChildElement(arg0);
	}

	@Override
	public SOAPElement addChildElement(String arg0) throws SOAPException {
		return _getSOAPHeader(true).addChildElement(arg0);
	}

	@Override
	public SOAPElement addChildElement(SOAPElement arg0) throws SOAPException {
		return _getSOAPHeader(true).addChildElement(arg0);
	}

	@Override
	public SOAPElement addChildElement(String arg0, String arg1) throws SOAPException {
		return _getSOAPHeader(true).addChildElement(arg0, arg1);
	}

	@Override
	public SOAPElement addChildElement(String arg0, String arg1, String arg2) throws SOAPException {
		return _getSOAPHeader(true).addChildElement(arg0, arg1, arg2);
	}

	@Override
	public SOAPElement addNamespaceDeclaration(String arg0, String arg1) throws SOAPException {
		return _getSOAPHeader(true).addNamespaceDeclaration(arg0, arg1);
	}

	@Override
	public SOAPElement addTextNode(String arg0) throws SOAPException {
		return _getSOAPHeader(true).addTextNode(arg0);
	}

	@Override
	public QName createQName(String arg0, String arg1) throws SOAPException {
		return _getSOAPHeader(true).createQName(arg0, arg1);
	}

	@Override
	public Iterator<Name> getAllAttributes() {
		return _getSOAPHeader(false).getAllAttributes();
	}

	@Override
	public Iterator<QName> getAllAttributesAsQNames() {
		return _getSOAPHeader(false).getAllAttributesAsQNames();
	}

	@Override
	public String getAttributeValue(Name arg0) {
		return _getSOAPHeader(false).getAttributeValue(arg0);
	}

	@Override
	public String getAttributeValue(QName arg0) {
		return _getSOAPHeader(false).getAttributeValue(arg0);
	}

	@Override
	public Iterator<Node> getChildElements() {
		return _getSOAPHeader(false).getChildElements();
	}

	@Override
	public Iterator<Node> getChildElements(Name arg0) {
		return _getSOAPHeader(false).getChildElements(arg0);
	}

	@Override
	public Iterator<Node> getChildElements(QName arg0) {
		return _getSOAPHeader(false).getChildElements();
	}

	@Override
	public Name getElementName() {
		return _getSOAPHeader(false).getElementName();
	}

	@Override
	public QName getElementQName() {
		return _getSOAPHeader(false).getElementQName();
	}

	@Override
	public String getEncodingStyle() {
		return _getSOAPHeader(false).getEncodingStyle();
	}

	@Override
	public Iterator<String> getNamespacePrefixes() {
		return _getSOAPHeader(false).getNamespacePrefixes();
	}

	@Override
	public String getNamespaceURI(String arg0) {
		return _getSOAPHeader(false).getNamespaceURI(arg0);
	}

	@Override
	public Iterator<String> getVisibleNamespacePrefixes() {
		return _getSOAPHeader(false).getVisibleNamespacePrefixes();
	}

	@Override
	public boolean removeAttribute(Name arg0) {
		return _getSOAPHeader(true).removeAttribute(arg0);
	}

	@Override
	public boolean removeAttribute(QName arg0) {
		return _getSOAPHeader(true).removeAttribute(arg0);
	}

	@Override
	public void removeContents() {
		_getSOAPHeader(true).removeContents();
	}

	@Override
	public boolean removeNamespaceDeclaration(String arg0) {
		return _getSOAPHeader(true).removeNamespaceDeclaration(arg0);
	}

	@Override
	public SOAPElement setElementQName(QName arg0) throws SOAPException {
		return _getSOAPHeader(true).setElementQName(arg0);
	}

	@Override
	public void setEncodingStyle(String arg0) throws SOAPException {
		_getSOAPHeader(true).setEncodingStyle(arg0);
	}

	@Override
	public void detachNode() {
		_getSOAPHeader(true).detachNode();
	}

	@Override
	public SOAPElement getParentElement() {
		return _getSOAPHeader(true).getParentElement();
	}

	@Override
	public String getValue() {
		return _getSOAPHeader(false).getValue();
	}

	@Override
	public void recycleNode() {
		_getSOAPHeader(true).recycleNode();
	}

	@Override
	public void setParentElement(SOAPElement arg0) throws SOAPException {
		_getSOAPHeader(true).setParentElement(arg0);
	}

	@Override
	public void setValue(String arg0) {
		_getSOAPHeader(true).setValue(arg0);
	}

	@Override
	public String getNodeName() {
		return _getSOAPHeader(false).getNodeName();
	}

	@Override
	public String getNodeValue() throws DOMException {
		return _getSOAPHeader(false).getNodeValue();
	}

	@Override
	public void setNodeValue(String nodeValue) throws DOMException {
		_getSOAPHeader(true).setNodeValue(nodeValue);
	}

	@Override
	public short getNodeType() {
		return _getSOAPHeader(false).getNodeType();
	}

	@Override
	public org.w3c.dom.Node getParentNode() {
		return _getSOAPHeader(true).getParentNode();
	}

	@Override
	public NodeList getChildNodes() {
		return _getSOAPHeader(true).getChildNodes();
	}

	@Override
	public org.w3c.dom.Node getFirstChild() {
		return _getSOAPHeader(true).getFirstChild();
	}

	@Override
	public org.w3c.dom.Node getLastChild() {
		return _getSOAPHeader(true).getLastChild();
	}

	@Override
	public org.w3c.dom.Node getPreviousSibling() {
		return _getSOAPHeader(true).getPreviousSibling();
	}

	@Override
	public org.w3c.dom.Node getNextSibling() {
		return _getSOAPHeader(true).getNextSibling();
	}

	@Override
	public NamedNodeMap getAttributes() {
		return _getSOAPHeader(true).getAttributes();
	}

	@Override
	public Document getOwnerDocument() {
		return _getSOAPHeader(true).getOwnerDocument();
	}

	@Override
	public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
		return _getSOAPHeader(true).insertBefore(newChild, refChild);
	}

	@Override
	public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
		return _getSOAPHeader(true).replaceChild(newChild, oldChild);
	}

	@Override
	public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
		return _getSOAPHeader(true).removeChild(oldChild);
	}

	@Override
	public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
		return _getSOAPHeader(true).appendChild(newChild);
	}

	@Override
	public boolean hasChildNodes() {
		return _getSOAPHeader(false).hasChildNodes();
	}

	@Override
	public org.w3c.dom.Node cloneNode(boolean deep) {
		return _getSOAPHeader(true).cloneNode(deep);
	}

	@Override
	public void normalize() {
		_getSOAPHeader(true).normalize();
	}

	@Override
	public boolean isSupported(String feature, String version) {
		return _getSOAPHeader(false).isSupported(feature,version);
	}

	@Override
	public String getNamespaceURI() {
		return _getSOAPHeader(false).getNamespaceURI();
	}

	@Override
	public String getPrefix() {
		return _getSOAPHeader(false).getPrefix();
	}

	@Override
	public void setPrefix(String prefix) throws DOMException {
		_getSOAPHeader(true).setPrefix(prefix);
	}

	@Override
	public String getLocalName() {
		return _getSOAPHeader(false).getLocalName();
	}

	@Override
	public boolean hasAttributes() {
		return _getSOAPHeader(false).hasAttributes();
	}

	@Override
	public String getBaseURI() {
		return _getSOAPHeader(false).getBaseURI();
	}

	@Override
	public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
		return _getSOAPHeader(false).compareDocumentPosition(other);
	}

	@Override
	public String getTextContent() throws DOMException {
		return _getSOAPHeader(false).getTextContent();
	}

	@Override
	public void setTextContent(String textContent) throws DOMException {
		_getSOAPHeader(true).setTextContent(textContent);
	}

	@Override
	public boolean isSameNode(org.w3c.dom.Node other) {
		return _getSOAPHeader(false).isSameNode(other);
	}

	@Override
	public String lookupPrefix(String namespaceURI) {
		return _getSOAPHeader(false).lookupPrefix(namespaceURI);
	}

	@Override
	public boolean isDefaultNamespace(String namespaceURI) {
		return _getSOAPHeader(false).isDefaultNamespace(namespaceURI);
	}

	@Override
	public String lookupNamespaceURI(String prefix) {
		return _getSOAPHeader(false).lookupNamespaceURI(prefix);
	}

	@Override
	public boolean isEqualNode(org.w3c.dom.Node arg) {
		return _getSOAPHeader(false).isEqualNode(arg);
	}

	@Override
	public Object getFeature(String feature, String version) {
		return _getSOAPHeader(true).getFeature(feature, version);
	}

	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return _getSOAPHeader(true).setUserData(key, data, handler);
	}

	@Override
	public Object getUserData(String key) {
		return _getSOAPHeader(true).getUserData(key);
	}

	@Override
	public String getTagName() {
		return _getSOAPHeader(false).getTagName();
	}

	@Override
	public String getAttribute(String name) {
		return _getSOAPHeader(false).getAttribute(name);
	}

	@Override
	public void setAttribute(String name, String value) throws DOMException {
		_getSOAPHeader(true).setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) throws DOMException {
		_getSOAPHeader(true).removeAttribute(name);
	}

	@Override
	public Attr getAttributeNode(String name) {
		return _getSOAPHeader(true).getAttributeNode(name);
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		return _getSOAPHeader(true).setAttributeNode(newAttr);
	}

	@Override
	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		return _getSOAPHeader(true).removeAttributeNode(oldAttr);
	}

	@Override
	public NodeList getElementsByTagName(String name) {
		return _getSOAPHeader(true).getElementsByTagName(name);
	}

	@Override
	public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
		return _getSOAPHeader(false).getAttributeNS(namespaceURI, localName);
	}

	@Override
	public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
		_getSOAPHeader(true).setAttributeNS(namespaceURI, qualifiedName, value);
	}

	@Override
	public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
		_getSOAPHeader(true).removeAttributeNS(namespaceURI, localName);
	}

	@Override
	public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
		return _getSOAPHeader(true).getAttributeNodeNS(namespaceURI, localName);
	}

	@Override
	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		return _getSOAPHeader(true).setAttributeNodeNS(newAttr);
	}

	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
		return _getSOAPHeader(true).getElementsByTagNameNS(namespaceURI, localName);
	}

	@Override
	public boolean hasAttribute(String name) {
		return _getSOAPHeader(false).hasAttribute(name);
	}

	@Override
	public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
		return _getSOAPHeader(false).hasAttributeNS(namespaceURI, localName);
	}

	@Override
	public TypeInfo getSchemaTypeInfo() {
		return _getSOAPHeader(true).getSchemaTypeInfo();
	}

	@Override
	public void setIdAttribute(String name, boolean isId) throws DOMException {
		_getSOAPHeader(true).setIdAttribute(name, isId);
	}

	@Override
	public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
		_getSOAPHeader(true).setIdAttributeNS(namespaceURI, localName, isId);
	}

	@Override
	public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
		_getSOAPHeader(true).setIdAttributeNode(idAttr, isId);
	}

	@Override
	public SOAPHeaderElement addHeaderElement(Name arg0) throws SOAPException {
		return _getSOAPHeader(true).addHeaderElement(arg0);
	}

	@Override
	public SOAPHeaderElement addHeaderElement(QName arg0) throws SOAPException {
		return _getSOAPHeader(true).addHeaderElement(arg0);
	}

	@Override
	public SOAPHeaderElement addNotUnderstoodHeaderElement(QName arg0) throws SOAPException {
		return _getSOAPHeader(true).addNotUnderstoodHeaderElement(arg0);
	}

	@Override
	public SOAPHeaderElement addUpgradeHeaderElement(Iterator<String> arg0) throws SOAPException {
		return _getSOAPHeader(true).addUpgradeHeaderElement(arg0);
	}

	@Override
	public SOAPHeaderElement addUpgradeHeaderElement(String[] arg0) throws SOAPException {
		return _getSOAPHeader(true).addUpgradeHeaderElement(arg0);
	}

	@Override
	public SOAPHeaderElement addUpgradeHeaderElement(String arg0) throws SOAPException {
		return _getSOAPHeader(true).addUpgradeHeaderElement(arg0);
	}

	@Override
	public Iterator<SOAPHeaderElement> examineAllHeaderElements() {
		return _getSOAPHeader(true).examineAllHeaderElements();
	}

	@Override
	public Iterator<SOAPHeaderElement> examineHeaderElements(String arg0) {
		return _getSOAPHeader(true).examineHeaderElements(arg0);
	}

	@Override
	public Iterator<SOAPHeaderElement> examineMustUnderstandHeaderElements(String arg0) {
		return _getSOAPHeader(true).examineMustUnderstandHeaderElements(arg0);
	}

	@Override
	public Iterator<SOAPHeaderElement> extractAllHeaderElements() {
		return _getSOAPHeader(true).extractAllHeaderElements();
	}

	@Override
	public Iterator<SOAPHeaderElement> extractHeaderElements(String arg0) {
		return _getSOAPHeader(true).extractHeaderElements(arg0);
	}
	
	
}
