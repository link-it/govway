/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.example.soap.pdnd_async;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utilità per la gestione dei messaggi SOAP 1.1 degli esempi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapUtils {
	
	private SoapUtils() {}

	public static final String NS_ESERVICE = "http://govway.org/example/modipa/pdnd/async/eservice";
	public static final String NS_CALLBACK = "http://govway.org/example/modipa/pdnd/async/callback";
	
	public static Element getOperation(SOAPMessage request) throws SOAPException {
		SOAPBody body = request.getSOAPBody();
		NodeList list = body.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if(n instanceof Element) {
				return (Element) n;
			}
		}
		throw new SOAPException("SOAP body empty");
	}
	
	public static String getChildValue(Element element, String localName) {
		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if(n instanceof Element && localName.equals(((Element) n).getLocalName())) {
				return ((Element) n).getTextContent();
			}
		}
		return null;
	}
	public static int getChildIntValue(Element element, String localName, int defaultValue) {
		String v = getChildValue(element, localName);
		return v!=null && !"".equals(v.trim()) ? Integer.parseInt(v.trim()) : defaultValue;
	}
	
	public static SOAPMessage newResponse() throws SOAPException {
		return MessageFactory.newInstance().createMessage();
	}
	public static SOAPElement addResponseElement(SOAPMessage response, String namespace, String localName) throws SOAPException {
		return response.getSOAPBody().addChildElement(new QName(namespace, localName, "tns"));
	}
	public static void addChild(SOAPElement parent, String namespace, String localName, String value) throws SOAPException {
		parent.addChildElement(new QName(namespace, localName, "tns")).addTextNode(value);
	}
	
	public static SOAPMessage newFault(String faultCode, String faultString) {
		try {
			SOAPMessage response = newResponse();
			SOAPFault fault = response.getSOAPBody().addFault();
			fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", faultCode, "SOAP-ENV"));
			fault.setFaultString(faultString);
			return response;
		}catch(SOAPException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	public static SOAPMessage parse(String envelope) throws Exception {
		return MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(envelope.getBytes(StandardCharsets.UTF_8)));
	}
	
	@SuppressWarnings("unchecked")
	public static String getHttpHeader(MessageContext ctx, String name) {
		Object o = ctx.get(MessageContext.HTTP_REQUEST_HEADERS);
		if(o instanceof Map<?,?>) {
			for (Map.Entry<String, List<String>> entry : ((Map<String, List<String>>) o).entrySet()) {
				if(entry.getKey()!=null && entry.getKey().equalsIgnoreCase(name) && entry.getValue()!=null && !entry.getValue().isEmpty()) {
					return entry.getValue().get(0);
				}
			}
		}
		return null;
	}
	
	public static String envelope(String body) {
		return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body>"+body+"</SOAP-ENV:Body></SOAP-ENV:Envelope>";
	}
	public static String escape(String value) {
		return value==null ? "" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
