/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import java.util.Map;

import javax.xml.stream.XMLInputFactory;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MappedJson2Xml extends AbstractJson2Xml {

	private XMLInputFactory xmlInputFactory;
	private Configuration configuration;
	
	public MappedJson2Xml(Map<String, String> jsonNamespaceMap) {
		super();
		this.configuration = new Configuration(jsonNamespaceMap);
		this.init();
	}
	public MappedJson2Xml(Configuration configuration) {
		super();
		this.configuration = configuration;
		this.init();
	}
	private void init() {
		this.xmlInputFactory = new MappedXMLInputFactory(this.configuration);			
	}
	
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	@Override
	protected XMLInputFactory getInputFactory() {
		return this.xmlInputFactory;
	}

}
