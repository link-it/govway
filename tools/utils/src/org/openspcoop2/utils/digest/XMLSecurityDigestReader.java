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

package org.openspcoop2.utils.digest;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.w3c.dom.Element;

/**
 * XMLSecurityDigestReader
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLSecurityDigestReader implements IDigestReader {

	private Logger log;
	@SuppressWarnings("unused")
	private AbstractXPathExpressionEngine xpathEngine;
	@SuppressWarnings("unused")
	private AbstractXMLUtils xmlUtils;

	public XMLSecurityDigestReader(AbstractXMLUtils xmlUtils, AbstractXPathExpressionEngine xpathEngine) {
		this(LoggerWrapperFactory.getLogger(XMLSecurityDigestReader.class),xmlUtils, xpathEngine);
	}
	public XMLSecurityDigestReader(Logger log, AbstractXMLUtils xmlUtils, AbstractXPathExpressionEngine xpathEngine) {
		this.log = log;
		this.xmlUtils = xmlUtils;
		this.xpathEngine = xpathEngine;
	}

	@Override
	public String getDigest(Element element,String referenceId,DynamicNamespaceContext dnc) throws UtilsException{

		try {
			// TODO 
			return null;
		} catch (Exception e) {
			this.log.error("Errore durante la getDigest", e);
			throw new UtilsException(e.getMessage(),e);
		} 
	

	}

}
