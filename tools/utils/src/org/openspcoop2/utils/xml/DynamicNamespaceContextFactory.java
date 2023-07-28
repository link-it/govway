/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.xml;

import java.io.IOException;

import jakarta.xml.soap.SOAPException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * DynamicNamespaceContextFactory
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class DynamicNamespaceContextFactory {

	/**
	 * Dato un node, ritorna un namespace context, contenente i vari namespaces presenti nel nodo
	 *
	 * @param node Nodo su cui cercate i namespaces
	 * @return Un oggetto contenente i vari namespaces presenti nel nodo
	 * 
	 */
	public DynamicNamespaceContext getNamespaceContext(Node node)
	{
		DynamicNamespaceContext dnc = new DynamicNamespaceContext();
		dnc.findPrefixNamespace(node);
		return dnc;
	}
		
	/**
	 * Dato un node, ritorna un namespace context, contenente i vari namespaces presenti nel nodo
	 *
	 * @param soapenvelope byte[] su cui cercate i namespaces
	 * @return Un oggetto contenente i vari namespaces presenti nel nodo
	 * 
	 */
	public abstract DynamicNamespaceContext getNamespaceContextFromSoapEnvelope11(byte[] soapenvelope) throws SAXException, SOAPException, IOException, Exception;
	
	
	public abstract DynamicNamespaceContext getNamespaceContextFromSoapEnvelope12(byte[] soapenvelope) throws SAXException, SOAPException, IOException, Exception;
	
	
	public abstract DynamicNamespaceContext getNamespaceContextFromSoapEnvelope11(jakarta.xml.soap.SOAPEnvelope soapenvelope) throws SAXException, SOAPException;
	
	public abstract DynamicNamespaceContext getNamespaceContextFromSoapEnvelope12(jakarta.xml.soap.SOAPEnvelope soapenvelope) throws SAXException, SOAPException;
	
	
	
}
