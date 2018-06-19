/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $ Rev: 12563 $, $Date$
 * 
 */
public class MappedXml2Json extends AbstractXml2Json {

	private AbstractXMLUtils xmlUtils;
	private Map<String, String> jsonNamespaceMap;
	private MappedXMLOutputFactory mappedXMLOutputFactory;
	private Configuration configuration;
	public MappedXml2Json() {
		super();
		this.configuration = new Configuration();
		this.init();
	}
	public MappedXml2Json(Map<String, String> jsonNamespaceMap) {
		super();
		this.jsonNamespaceMap = jsonNamespaceMap;
		this.configuration = new Configuration(this.jsonNamespaceMap);
		this.init();
	}
	public MappedXml2Json(Configuration configuration) {
		super();
		this.configuration = configuration;
		this.init();
	}
	private void init() {
		this.xmlUtils = XMLUtils.getInstance();
		this.mappedXMLOutputFactory = new MappedXMLOutputFactory(this.configuration);			
	}
	
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	@Override
	protected XMLOutputFactory getOutputFactory() {
		return this.mappedXMLOutputFactory;
	}

	@Override
	public String xml2json(Node node) throws UtilsException {
		if(this.jsonNamespaceMap==null) {
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(node);
			this.refreshOutputFactory(dnc);
		}
		return super.xml2json(node);
	}
	@Override
	public String xml2json(String xmlString) throws UtilsException {
		try {
			return xml2json(this.xmlUtils.newElement(xmlString.getBytes()));
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	private void refreshOutputFactory(DynamicNamespaceContext f) {
		if(this.jsonNamespaceMap == null) {
			this.jsonNamespaceMap = new HashMap<>();
		}
		Enumeration<?> prefixes = f.getPrefixes();
		while(prefixes.hasMoreElements()){
			Object nextElement = prefixes.nextElement();
			if(!nextElement.equals("xmlns"))
				this.jsonNamespaceMap.put((String) nextElement, f.getNamespaceURI((String)nextElement));
		}
		this.mappedXMLOutputFactory = new MappedXMLOutputFactory(this.jsonNamespaceMap);
		
		
	}
	

}
