/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import org.json.JSONML;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JsonMLXml2Json implements IXml2Json{

	private XMLUtils xmlUtils;

	public JsonMLXml2Json() {
		this.xmlUtils = XMLUtils.getInstance();
	}
	@Override
	public String xml2json(String xmlString) {
		return JSONML.toJSONObject(xmlString).toString();
	}

	@Override
	public String xml2json(Node node) throws UtilsException {
		try {
			return JSONML.toJSONObject(this.xmlUtils.toString(node)).toString();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
