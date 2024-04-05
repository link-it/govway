/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import javax.xml.stream.XMLInputFactory;

import org.codehaus.jettison.badgerfish.BadgerFishXMLInputFactory;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
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
