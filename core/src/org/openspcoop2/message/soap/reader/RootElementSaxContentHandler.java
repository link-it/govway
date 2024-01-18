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

package org.openspcoop2.message.soap.reader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * RootElementSaxContentHandler
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RootElementSaxContentHandler extends DefaultHandler {

	private String localName;
	private String namespace;
	private String prefix;
	
	private String excludedNamespace;
	
	public RootElementSaxContentHandler(String excludedNamespace) {
		this.excludedNamespace = excludedNamespace;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		//System.out.println("--------- startElement uri["+uri+"] localName["+localName+"] qName["+qName+"]");
		
		if(!this.excludedNamespace.equals(uri)) {
			this.localName = localName;
			this.namespace = uri;
			if(qName!=null && qName.contains(":") && !qName.startsWith(":")) {
				this.prefix = qName.substring(0, qName.indexOf(":"));
			}
			else {
				this.prefix = "";
			}
		}
	}

	public String getLocalName() {
		return this.localName;
	}

	public String getNamespace() {
		return this.namespace;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
}