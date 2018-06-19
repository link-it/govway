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
public interface IXml2Json {
	public String xml2json(String xmlString) throws UtilsException;
	public String xml2json(Node node) throws UtilsException;

}
