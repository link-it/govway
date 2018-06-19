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
 * @version $ Rev: 12563 $, $Date$
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
