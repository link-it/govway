/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 16 mag 2018 $
 * 
 */
public interface IXml2Json {
	public String xml2json(String xmlString) throws Exception;
	public String xml2json(Node node) throws Exception;

}
