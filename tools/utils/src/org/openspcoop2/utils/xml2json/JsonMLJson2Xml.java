/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import org.json.JSONObject;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 16 mag 2018 $
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
	public Node json2xmlNode(String jsonString) throws Exception {
		return this.xmlUtils.newElement(this.json2xml(jsonString).getBytes());
	}

}
