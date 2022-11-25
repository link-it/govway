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

package org.openspcoop2.pdd.core.dynamic;

import java.util.List;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.slf4j.Logger;
import org.w3c.dom.Node;

/**
 * ContentExtractor
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentExtractor extends ContentReader {


	public ContentExtractor(OpenSPCoop2Message message, Logger log) {
		super(message, log);
	}


	
	
	// TRANSPORT
	

	public void addTransportHeader(String name, String value) {
		if(this.message!=null) {
			this.message.forceTransportHeader(name, value);
		}
	}
	public void addTransportHeader(String name, List<String> values) {
		if(this.message!=null) {
			this.message.forceTransportHeader(name, values);
		}
	}
	public void removeTransportHeader(String name) {
		if(this.message!=null) {
			if(this.message.getTransportRequestContext()!=null) {
				this.message.getTransportRequestContext().removeHeader(name);
			}
			else if(this.message.getTransportResponseContext()!=null) {
				this.message.getTransportResponseContext().removeHeader(name);
			}
		}
	}
	
	public void addUrlProperty(String name, String value) {
		if(this.message!=null) {
			this.message.forceUrlProperty(name, value);
		}
	}
	public void addUrlProperty(String name, List<String> values) {
		if(this.message!=null) {
			this.message.forceUrlProperty(name, values);
		}
	}
	public void removeUrlProperty(String name) {
		if(this.message!=null) {
			if(this.message.getTransportRequestContext()!=null) {
				this.message.getTransportRequestContext().removeParameter(name);
			}
		}
	}

	
	
	// SOAP 
	
	public void disableExceptionIfFoundMoreSecurityHeader() {
		if(this.message!=null && ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
			try {
				this.message.castAsSoap().setThrowExceptionIfFoundMoreSecurityHeader(false);
			}catch(Throwable t) {
				throw new RuntimeException(t.getMessage(),t); // non dovrebbe mai avvenire
			}
		}
	}
	public void addSoapHeader(String xml) throws DynamicException {
		this.addSoapHeader(xml.getBytes());
	}
	public void addSoapHeader(byte[] xml) throws DynamicException {
		if(this.message!=null && ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
			
			try {
			
				OpenSPCoop2SoapMessage soapMsg = this.message.castAsSoap();
				
				SOAPHeader header = soapMsg.getSOAPHeader();
				if(header==null) {
					header = soapMsg.getSOAPPart().getEnvelope().addHeader();
				}
				
				SOAPElement soapElement = soapMsg.createSOAPElement(xml);
				
				header.addChildElement(soapElement);
				
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			
		}
	}
	
	public void setSoapBody(String xml) throws DynamicException {
		this.setSoapBody(xml.getBytes());
	}
	public void setSoapBody(byte[] xml) throws DynamicException {
		if(this.message!=null && ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
			
			try {
			
				OpenSPCoop2SoapMessage soapMsg = this.message.castAsSoap();
				
				SOAPElement soapElement = soapMsg.createSOAPElement(xml);
				
				soapMsg.getSOAPBody().removeContents();
				soapMsg.getSOAPBody().addChildElement(soapElement);
				
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			
		}
	}
	
	public void addSoapBody(String xml, String xpath) throws DynamicException {
		this.addSoapBody(xml.getBytes(), xpath);
	}
	public void addSoapBody(byte[] xml, String xpath) throws DynamicException {
		if(this.message!=null && ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
			
			try {
			
				OpenSPCoop2SoapMessage soapMsg = this.message.castAsSoap();
				
				SOAPBody body = soapMsg.getSOAPBody();
				XPathExpressionEngine xpathEngine = new XPathExpressionEngine(this.message.getFactory());
				DynamicNamespaceContext dnc = new DynamicNamespaceContext();
				dnc.findPrefixNamespace(body);
				Node n = (Node) xpathEngine.getMatchPattern(body, dnc, xpath, XPathReturnType.NODE);
				
				SOAPElement soapElement = soapMsg.createSOAPElement(xml);
				
				if(n instanceof SOAPElement) {
					SOAPElement s = (SOAPElement) n;
					s.addChildElement(soapElement);
				}
				else {
					Node nImported = body.getOwnerDocument().importNode(soapElement, true);
					n.appendChild(nImported);
				}
				
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			
		}
	}
	
	
	
	
	// Utility JSON
	
	public void prettyFormatJsonContent() throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().prettyFormatContent();
		}catch(Exception e) {
			throw new DynamicException("Operazione fallita: "+e.getMessage(),e);
		}
	}
	
	public void addSimpleJsonElement(String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addSimpleElement(name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addSimpleJsonElement(String jsonPath, String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addSimpleElement(jsonPath, name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addObjectJsonElement(String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addObjectElement(name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addObjectJsonElement(String jsonPath, String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addObjectElement(jsonPath, name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addArrayJsonElement(String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addArrayElement(name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addArrayJsonElement(String jsonPath, String name, Object value) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().addArrayElement(jsonPath, name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void removeJsonField(String name) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().removeElement(name);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void removeJsonField(String jsonPath, String name) throws DynamicException {
		if(!isRestJson()) {
			throw new DynamicException("Funzionalità richiede un messaggio JSON");
		}
		try {
			this.message.castAsRestJson().removeElement(jsonPath, name);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	

	// Utility XML
	
	public void addXmlElement(String name, String value) throws DynamicException {
		if(!isRestXml()) {
			throw new DynamicException("Funzionalità richiede un messaggio XML");
		}
		try {
			this.message.castAsRestXml().addElement(name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addXmlElement(String name, String namespace, String value) throws DynamicException {
		if(!isRestXml()) {
			throw new DynamicException("Funzionalità richiede un messaggio XML");
		}
		try {
			this.message.castAsRestXml().addElement(name, namespace, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	public void addXmlElementIn(String pattern, String name, String value) throws DynamicException {
		if(!isRestXml()) {
			throw new DynamicException("Funzionalità richiede un messaggio XML");
		}
		try {
			this.message.castAsRestXml().addElementIn(pattern, name, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void addXmlElementIn(String pattern, String name, String namespace, String value) throws DynamicException {
		if(!isRestXml()) {
			throw new DynamicException("Funzionalità richiede un messaggio XML");
		}
		try {
			this.message.castAsRestXml().addElementIn(pattern, name, namespace, value);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	public void removeXmlElement(String name) throws DynamicException {
		if(!isRestXml()) {
			throw new DynamicException("Funzionalità richiede un messaggio XML");
		}
		try {
			this.message.castAsRestXml().removeElement(name);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void removeXmlElement(String name, String namespace) throws DynamicException {
		if(!isRestXml()) {
			throw new DynamicException("Funzionalità richiede un messaggio XML");
		}
		try {
			this.message.castAsRestXml().removeElement(name, namespace);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	public void removeXmlElementIn(String pattern, String name) throws DynamicException {
		if(!isRestXml()) {
			throw new DynamicException("Funzionalità richiede un messaggio XML");
		}
		try {
			this.message.castAsRestXml().removeElementIn(pattern, name);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public void removeXmlElementIn(String pattern, String name, String namespace) throws DynamicException {
		if(!isRestXml()) {
			throw new DynamicException("Funzionalità richiede un messaggio XML");
		}
		try {
			this.message.castAsRestXml().removeElementIn(pattern, name, namespace);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}

}

