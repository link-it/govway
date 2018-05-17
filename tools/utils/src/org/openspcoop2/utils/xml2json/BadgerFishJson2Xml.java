/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import javax.xml.stream.XMLInputFactory;

import org.codehaus.jettison.badgerfish.BadgerFishXMLInputFactory;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 16 mag 2018 $
 * 
 */
public class BadgerFishJson2Xml extends AbstractJson2Xml {

	private XMLInputFactory jsonInputFactory;

	public BadgerFishJson2Xml() {
		super();
		this.jsonInputFactory= new BadgerFishXMLInputFactory();
	}

	@Override
	protected XMLInputFactory getInputFactory() {
		return this.jsonInputFactory;
	}

}
