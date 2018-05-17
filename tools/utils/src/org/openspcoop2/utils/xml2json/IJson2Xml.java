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
public interface IJson2Xml {
	public String json2xml(String jsonString) throws Exception;
	public Node json2xmlNode(String jsonString) throws Exception;

}
