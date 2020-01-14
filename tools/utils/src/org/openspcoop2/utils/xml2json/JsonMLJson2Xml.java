/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.json.JSONObject;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JsonMLJson2Xml implements IJson2Xml{

	private XMLUtils xmlUtils;

	public JsonMLJson2Xml() {
		this.xmlUtils = XMLUtils.getInstance();
	}

	@Override
	public String json2xml(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		return  org.json.JSONML.toString(jsonObject);
	}

	@Override
	public Node json2xmlNode(String jsonString) throws UtilsException {
		try {
			return this.xmlUtils.newElement(this.json2xml(jsonString).getBytes());
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
