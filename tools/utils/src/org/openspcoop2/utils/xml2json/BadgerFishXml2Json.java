/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import javax.xml.stream.XMLOutputFactory;

import org.codehaus.jettison.badgerfish.BadgerFishXMLOutputFactory;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $ Rev: 12563 $, $Date$
 * 
 */
public class BadgerFishXml2Json extends AbstractXml2Json {

	private BadgerFishXMLOutputFactory badgerFishXMLOutputFactory;
	
	public BadgerFishXml2Json() {
		super();
		this.badgerFishXMLOutputFactory = new BadgerFishXMLOutputFactory();
	}
	@Override
	protected XMLOutputFactory getOutputFactory() {
		return this.badgerFishXMLOutputFactory;
	}

}
