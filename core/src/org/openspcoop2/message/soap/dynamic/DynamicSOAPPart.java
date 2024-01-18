/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;

import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_saaj_impl;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

/**
 * DynamicSOAPPart
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicSOAPPart<T extends AbstractOpenSPCoop2Message_saaj_impl> extends javax.xml.soap.SOAPPart {

	private AbstractOpenSPCoop2Message_soap_impl<T> wrapped;
	public DynamicSOAPPart(AbstractOpenSPCoop2Message_soap_impl<T> wrapped) {
		this.wrapped = wrapped;
	}

	private javax.xml.soap.SOAPPart _getSOAPPart(){
		try {
			return this.wrapped.getContent().getSOAPPart();
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	
	// Metodi ottimizzati per il dynamic behaviour
	
	private DynamicSOAPEnvelope<T> dynamic;
	
	@Override
	public SOAPEnvelope getEnvelope() throws SOAPException {
		if(this.wrapped.isSoapHeaderOptimizable()) {
			if(this.dynamic==null) {
				this.dynamic = new DynamicSOAPEnvelope<T>(this.wrapped);
			}
			return this.dynamic;
		}
		return _getSOAPPart().getEnvelope();
	}
	
	public void clearDynamicResources() {
		if(this.dynamic!=null) {
			this.dynamic.clearDynamicResources();
		}
		this.dynamic=null;
	}
	
	
	// Metodi implementati in SAAJ
	
	@Override
	public String getContentId() {
		return _getSOAPPart().getContentId();
	}

	@Override
	public String getContentLocation() {
		return _getSOAPPart().getContentLocation();
	}

	@Override
	public void setContentId(String contentId) {
		_getSOAPPart().setContentId(contentId);
	}

	@Override
	public void setContentLocation(String contentLocation) {
		_getSOAPPart().setContentLocation(contentLocation);
	}
	@Override
	public DocumentType getDoctype() {
		return _getSOAPPart().getDoctype();
	}
	@Override
	public DOMImplementation getImplementation() {
		return _getSOAPPart().getImplementation();
	}
	@Override
	public Element getDocumentElement() {
		return _getSOAPPart().getDocumentElement();
	}
	@Override
	public Element createElement(String tagName) throws DOMException {
		return _getSOAPPart().createElement(tagName);
	}
	@Override
	public DocumentFragment createDocumentFragment() {
		return _getSOAPPart().createDocumentFragment();
	}
	@Override
	public Text createTextNode(String data) {
		return _getSOAPPart().createTextNode(data);
	}
	@Override
	public Comment createComment(String data) {
		return _getSOAPPart().createComment(data);
	}
	@Override
	public CDATASection createCDATASection(String data) throws DOMException {
		return _getSOAPPart().createCDATASection(data);
	}
	@Override
	public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
		return _getSOAPPart().createProcessingInstruction(target, data);
	}
	@Override
	public Attr createAttribute(String name) throws DOMException {
		return _getSOAPPart().createAttribute(name);
	}
	@Override
	public EntityReference createEntityReference(String name) throws DOMException {
		return _getSOAPPart().createEntityReference(name);
	}
	@Override
	public NodeList getElementsByTagName(String tagname) {
		return _getSOAPPart().getElementsByTagName(tagname);
	}
	@Override
	public Node importNode(Node importedNode, boolean deep) throws DOMException {
		return _getSOAPPart().importNode(importedNode, deep);
	}
	@Override
	public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
		return _getSOAPPart().createElementNS(namespaceURI, qualifiedName);
	}
	@Override
	public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
		return _getSOAPPart().createAttributeNS(namespaceURI, qualifiedName);
	}
	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		return _getSOAPPart().getElementsByTagNameNS(namespaceURI, localName);
	}
	@Override
	public Element getElementById(String elementId) {
		return _getSOAPPart().getElementById(elementId);
	}
	@Override
	public String getInputEncoding() {
		return _getSOAPPart().getInputEncoding();
	}
	@Override
	public String getXmlEncoding() {
		return _getSOAPPart().getXmlEncoding();
	}
	@Override
	public boolean getXmlStandalone() {
		return _getSOAPPart().getXmlStandalone();
	}
	@Override
	public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
		_getSOAPPart().setXmlStandalone(xmlStandalone);
	}
	@Override
	public String getXmlVersion() {
		return _getSOAPPart().getXmlVersion();
	}
	@Override
	public void setXmlVersion(String xmlVersion) throws DOMException {
		_getSOAPPart().setXmlVersion(xmlVersion);
	}
	@Override
	public boolean getStrictErrorChecking() {
		return _getSOAPPart().getStrictErrorChecking();
	}
	@Override
	public void setStrictErrorChecking(boolean strictErrorChecking) {
		_getSOAPPart().setStrictErrorChecking(strictErrorChecking);
	}
	@Override
	public String getDocumentURI() {
		return _getSOAPPart().getDocumentURI();
	}
	@Override
	public void setDocumentURI(String documentURI) {
		_getSOAPPart().setDocumentURI(documentURI);
	}
	@Override
	public Node adoptNode(Node source) throws DOMException {
		return _getSOAPPart().adoptNode(source);
	}
	@Override
	public DOMConfiguration getDomConfig() {
		return _getSOAPPart().getDomConfig();
	}
	@Override
	public void normalizeDocument() {
		_getSOAPPart().normalizeDocument();
	}
	@Override
	public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
		return _getSOAPPart().renameNode(n, namespaceURI, qualifiedName);
	}
	@Override
	public String getNodeName() {
		return _getSOAPPart().getNodeName();
	}
	@Override
	public String getNodeValue() throws DOMException {
		return _getSOAPPart().getNodeValue();
	}
	@Override
	public void setNodeValue(String nodeValue) throws DOMException {
		_getSOAPPart().setNodeValue(nodeValue);
	}
	@Override
	public short getNodeType() {
		return _getSOAPPart().getNodeType();
	}
	@Override
	public Node getParentNode() {
		return _getSOAPPart().getParentNode();
	}
	@Override
	public NodeList getChildNodes() {
		return _getSOAPPart().getChildNodes();
	}
	@Override
	public Node getFirstChild() {
		return _getSOAPPart().getFirstChild();
	}
	@Override
	public Node getLastChild() {
		return _getSOAPPart().getLastChild();
	}
	@Override
	public Node getPreviousSibling() {
		return _getSOAPPart().getPreviousSibling();
	}
	@Override
	public Node getNextSibling() {
		return _getSOAPPart().getNextSibling();
	}
	@Override
	public NamedNodeMap getAttributes() {
		return _getSOAPPart().getAttributes();
	}
	@Override
	public Document getOwnerDocument() {
		return _getSOAPPart().getOwnerDocument();
	}
	@Override
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return _getSOAPPart().insertBefore(newChild, refChild);
	}
	@Override
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return _getSOAPPart().replaceChild(newChild, oldChild);
	}
	@Override
	public Node removeChild(Node oldChild) throws DOMException {
		return _getSOAPPart().removeChild(oldChild);
	}
	@Override
	public Node appendChild(Node newChild) throws DOMException {
		return _getSOAPPart().appendChild(newChild);
	}
	@Override
	public boolean hasChildNodes() {
		return _getSOAPPart().hasChildNodes();
	}
	@Override
	public Node cloneNode(boolean deep) {
		return _getSOAPPart().cloneNode(deep);
	}
	@Override
	public void normalize() {
		_getSOAPPart().normalize();
	}
	@Override
	public boolean isSupported(String feature, String version) {
		return _getSOAPPart().isSupported(feature, version);
	}
	@Override
	public String getNamespaceURI() {
		return _getSOAPPart().getNamespaceURI();
	}
	@Override
	public String getPrefix() {
		return _getSOAPPart().getPrefix();
	}
	@Override
	public void setPrefix(String prefix) throws DOMException {
		_getSOAPPart().setPrefix(prefix);
	}
	@Override
	public String getLocalName() {
		return _getSOAPPart().getLocalName();
	}
	@Override
	public boolean hasAttributes() {
		return _getSOAPPart().hasAttributes();
	}
	@Override
	public String getBaseURI() {
		return _getSOAPPart().getBaseURI();
	}
	@Override
	public short compareDocumentPosition(Node other) throws DOMException {
		return _getSOAPPart().compareDocumentPosition(other);
	}
	@Override
	public String getTextContent() throws DOMException {
		return _getSOAPPart().getTextContent();
	}
	@Override
	public void setTextContent(String textContent) throws DOMException {
		_getSOAPPart().setTextContent(textContent);
	}
	@Override
	public boolean isSameNode(Node other) {
		return _getSOAPPart().isSameNode(other);
	}
	@Override
	public String lookupPrefix(String namespaceURI) {
		return _getSOAPPart().lookupPrefix(namespaceURI);
	}
	@Override
	public boolean isDefaultNamespace(String namespaceURI) {
		return _getSOAPPart().isDefaultNamespace(namespaceURI);
	}
	@Override
	public String lookupNamespaceURI(String prefix) {
		return _getSOAPPart().lookupNamespaceURI(prefix);
	}
	@Override
	public boolean isEqualNode(Node arg) {
		return _getSOAPPart().isEqualNode(arg);
	}
	@Override
	public Object getFeature(String feature, String version) {
		return _getSOAPPart().getFeature(feature, version);
	}
	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return _getSOAPPart().setUserData(key, data, handler);
	}
	@Override
	public Object getUserData(String key) {
		return _getSOAPPart().getUserData(key);
	}
	@Override
	public void detachNode() {
		_getSOAPPart().detachNode();
	}
	@Override
	public SOAPElement getParentElement() {
		return _getSOAPPart().getParentElement();
	}
	@Override
	public String getValue() {
		return _getSOAPPart().getValue();
	}
	@Override
	public void recycleNode() {
		_getSOAPPart().recycleNode();
	}
	@Override
	public void setParentElement(SOAPElement arg0) throws SOAPException {
		_getSOAPPart().setParentElement(arg0);
	}
	@Override
	public void setValue(String arg0) {
		_getSOAPPart().setValue(arg0);
	}
	@Override
	public void addMimeHeader(String arg0, String arg1) {
		_getSOAPPart().addMimeHeader(arg0, arg1);
	}
	@Override
	public Iterator<MimeHeader> getAllMimeHeaders() {
		return _getSOAPPart().getAllMimeHeaders();
	}
	@Override
	public Source getContent() throws SOAPException {
		return _getSOAPPart().getContent();
	}

	@Override
	public Iterator<MimeHeader> getMatchingMimeHeaders(String[] arg0) {
		return _getSOAPPart().getMatchingMimeHeaders(arg0);
	}
	@Override
	public String[] getMimeHeader(String arg0) {
		return _getSOAPPart().getMimeHeader(arg0);
	}
	@Override
	public Iterator<MimeHeader> getNonMatchingMimeHeaders(String[] arg0) {
		return _getSOAPPart().getNonMatchingMimeHeaders(arg0);
	}
	@Override
	public void removeAllMimeHeaders() {
		_getSOAPPart().removeAllMimeHeaders();
	}
	@Override
	public void removeMimeHeader(String arg0) {
		_getSOAPPart().removeMimeHeader(arg0);
	}
	@Override
	public void setContent(Source arg0) throws SOAPException {
		_getSOAPPart().setContent(arg0);
	}
	@Override
	public void setMimeHeader(String arg0, String arg1) {
		_getSOAPPart().setMimeHeader(arg0, arg1);
	}
	
}
