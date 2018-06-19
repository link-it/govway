/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import org.openspcoop2.utils.UtilsException;
import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $ Rev: 12563 $, $Date$
 * 
 */
public interface IJson2Xml {
	public String json2xml(String jsonString) throws UtilsException;
	public Node json2xmlNode(String jsonString) throws UtilsException;

}
