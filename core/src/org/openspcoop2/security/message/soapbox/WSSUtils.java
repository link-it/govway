/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.security.message.soapbox;

import java.security.cert.X509Certificate;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

import org.adroitlogic.soapbox.CryptoUtil;
import org.adroitlogic.soapbox.MessageSecurityContext;
import org.adroitlogic.soapbox.SBConstants;
import org.adroitlogic.soapbox.SecurityConfig;
import org.adroitlogic.soapbox.SecurityRequest;
import org.apache.wss4j.common.token.DOMX509Data;
import org.apache.wss4j.common.token.DOMX509IssuerSerial;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.security.SecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * WSSContext_soapbox
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSUtils {

	public static void initWSSecurityHeader(OpenSPCoop2Message message,String actor,boolean mustUnderstand) throws SOAPException {

		if(message.getSOAPHeader()==null){
			message.getSOAPPart().getEnvelope().addHeader();
		}

		Iterator<?> it = message.getSOAPHeader().getChildElements(new QName(SBConstants.WSSE, "Security"));
		if(it.hasNext()){
			return;
		}

		QName name = new QName(SBConstants.WSSE, "Security");
		SOAPHeaderElement headerwss = message.newSOAPHeaderElement(message.getSOAPHeader(), name);
		headerwss.setActor(actor);
		headerwss.setMustUnderstand(mustUnderstand);
		headerwss.setParentElement(message.getSOAPHeader());
		message.addHeaderElement(message.getSOAPHeader(), headerwss);
		
		return;
	}

	public static SOAPHeaderElement getWSSecurityHeader(OpenSPCoop2Message message,String actor,boolean mustUnderstand) throws SOAPException {

		if(message.getSOAPHeader()==null){
			message.getSOAPPart().getEnvelope().addHeader();
		}

		Iterator<?> it = message.getSOAPHeader().getChildElements(new QName(SBConstants.WSSE, "Security"));
		while(it.hasNext()){
			SOAPHeaderElement hdr = (SOAPHeaderElement) it.next();
			String actorFound = hdr.getActor();
			boolean mustUnderstandFound = hdr.getMustUnderstand();
			if(mustUnderstand!=mustUnderstandFound)
				continue;
			if(actor==null){
				if(actorFound!=null){
					continue;
				}
			}else{
				if(!actor.equals(actorFound)){
					continue;
				}
			}
			return hdr;
		}


		throw new SOAPException("NotFound");

	}

	public static Element getWSSecurityHeader(Document doc,String actor,boolean mustUnderstand) throws SecurityException {

		String ns = SBConstants.WSSE;
		String localName = "Security";

		NodeList nl = doc.getDocumentElement().getElementsByTagNameNS(ns, localName);
		if(nl==null || nl.getLength()<=0){
			throw new SecurityException("Header WSS not found");
		}
		for (int i = 0; i < nl.getLength(); i++) {

			Node n = nl.item(i);
			if((n instanceof Element) && localName.equals(n.getLocalName()) && ns.equals(n.getNamespaceURI())){

				String actorFound = null;
				boolean mustUnderstandFound = false;

				NamedNodeMap attributes = n.getAttributes();
				for (int j = 0; j < attributes.getLength(); j++) {

					Node a = attributes.item(j);
					String localNameAttribute = a.getLocalName();
					//String prefixAttribute = a.getPrefix();
					String namespaceAttribute = a.getNamespaceURI();
					String valueAttribute = a.getNodeValue();
					//System.out.println("LOCAL["+localNameAttribute+"] PREFIX["+prefixAttribute+"] NAMESPACe["+namespaceAttribute+"] VALUE["+valueAttribute+"]");

					if("actor".equals(localNameAttribute) && "http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceAttribute)){
						actorFound =  valueAttribute;
					}
					else if("mustUnderstand".equals(localNameAttribute) && "http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceAttribute)){
						mustUnderstandFound = "1".equals(valueAttribute);
					}

				}

				if(mustUnderstand!=mustUnderstandFound)
					continue;
				if(actor==null){
					if(actorFound!=null){
						continue;
					}
				}else{
					if(!actor.equals(actorFound)){
						continue;
					}
				}

				return (Element) n;

			}

		}
		throw new SecurityException("Header WSS not found");
	}

	@SuppressWarnings("incomplete-switch")
	public static Element createKeyInfoElement(
			Document doc, SecurityRequest secReq, MessageSecurityContext msgSecCtx, SecurityConfig secConfig) {

		Element keyInfoElem = CryptoUtil.createKeyInfoElement(doc, secReq, msgSecCtx, secConfig);

		// Gestisco casi non coperti da createKeyInfoElement

		switch (secReq.getKeyIdentifierType()) {
		case ISSUER_SERIAL : 
			String alias = secReq.getCertAlias();
			X509Certificate[] certs = secConfig.getCertificatesByAlias(alias);
			String issuer = certs[0].getIssuerX500Principal().getName();
			java.math.BigInteger serialNumber = certs[0].getSerialNumber();
			DOMX509IssuerSerial domIssuerSerial = 
					new DOMX509IssuerSerial(doc, issuer, serialNumber);
			DOMX509Data domX509Data = new DOMX509Data(doc, domIssuerSerial);
			keyInfoElem.getElementsByTagNameNS(SBConstants.WSSE, "SecurityTokenReference").item(0).appendChild(domX509Data.getElement());
			break;
		}

		return keyInfoElem;
		
	}

}	