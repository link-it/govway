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
 * @version $ Rev: 12563 $, $Date$
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
