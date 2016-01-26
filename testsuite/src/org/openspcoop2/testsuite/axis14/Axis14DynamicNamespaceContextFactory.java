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

package org.openspcoop2.testsuite.axis14;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * DynamicNamespaceContextFactory
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Axis14DynamicNamespaceContextFactory extends org.openspcoop2.utils.xml.DynamicNamespaceContextFactory {


	private static Axis14DynamicNamespaceContextFactory dncfactory = null;
	private static synchronized void init(){
		if(Axis14DynamicNamespaceContextFactory.dncfactory==null){
			Axis14DynamicNamespaceContextFactory.dncfactory = new Axis14DynamicNamespaceContextFactory();
		}
	}
	public static Axis14DynamicNamespaceContextFactory getInstance(){
		if(Axis14DynamicNamespaceContextFactory.dncfactory==null){
			Axis14DynamicNamespaceContextFactory.init();
		}
		return Axis14DynamicNamespaceContextFactory.dncfactory;
	}
	
	
	@Override
	public DynamicNamespaceContext getNamespaceContextFromSoapEnvelope11(
			byte[] soapenvelope) throws SAXException, SOAPException,
			IOException, Exception {
		
		throw new IOException("Not Implemented");
	}

	@Override
	public DynamicNamespaceContext getNamespaceContextFromSoapEnvelope12(
			byte[] soapenvelope) throws SAXException, SOAPException,
			IOException, Exception {
		
		throw new IOException("Not Implemented");
	}

	@Override
	public DynamicNamespaceContext getNamespaceContextFromSoapEnvelope11(
			SOAPEnvelope soapenvelope) throws SAXException, SOAPException {
		DynamicNamespaceContext dnc = new DynamicNamespaceContext();
		SOAPBody body = soapenvelope.getBody();
		if(body.hasFault()){
			dnc.setSoapFault(true);
		}else if(body.hasChildNodes() == false){
			dnc.setSoapBodyEmpty(true);
		}else{
			String prefix = null;
			try {
				prefix = getFirstChildElement(body).getPrefix();
			} catch (Exception e) {
				prefix = body.getFirstChild().getPrefix();
			}
			if(prefix==null)
				prefix = "";
			else if(prefix!=null && !"".equals(prefix)){
				prefix = prefix+":";
			}
			dnc.setPrefixChildSoapBody(prefix);
		}
		dnc.findPrefixNamespace(soapenvelope);
		return dnc;
	}
	private Element getFirstChildElement(SOAPElement element) {
		Element firstElement = null;
		Iterator<?> it = element.getChildElements();
		while (it.hasNext() && firstElement==null){
			Node tmp = (Node) it.next();
			if(tmp instanceof Element) firstElement = (Element) tmp;
		}
		return firstElement;
	}

	@Override
	public DynamicNamespaceContext getNamespaceContextFromSoapEnvelope12(
			SOAPEnvelope soapenvelope) throws SAXException, SOAPException {
		throw new SOAPException("Not Implemented");
	}


}
