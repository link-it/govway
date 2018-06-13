/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import java.util.Map;

import javax.xml.stream.XMLInputFactory;

import org.codehaus.jettison.mapped.MappedXMLInputFactory;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $ Rev: 12563 $, $Date$
 * 
 */
public class MappedJson2Xml extends AbstractJson2Xml {

	private XMLInputFactory xmlInputFactory;
	public MappedJson2Xml(Map<?, ?> jsonNamespaceMap) {
		super();
		this.xmlInputFactory =  new MappedXMLInputFactory(jsonNamespaceMap);
	}
	@Override
	protected XMLInputFactory getInputFactory() {
		return this.xmlInputFactory;
	}

}
