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
	public MappedXml2Json() {
		this(null);
	}
	public MappedXml2Json(Map<String, String> jsonNamespaceMap) {
		super();
		this.xmlUtils = XMLUtils.getInstance();
		this.jsonNamespaceMap = jsonNamespaceMap;
		if(this.jsonNamespaceMap!=null)
			this.mappedXMLOutputFactory = new MappedXMLOutputFactory(this.jsonNamespaceMap);
		else
			this.mappedXMLOutputFactory = new MappedXMLOutputFactory(new Configuration());			
	}
	@Override
	protected XMLOutputFactory getOutputFactory() {
		return this.mappedXMLOutputFactory;
	}

	@Override
	public String xml2json(Node node) throws Exception {
		if(this.jsonNamespaceMap==null) {
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(node);
			this.refreshOutputFactory(dnc);
		}
		return super.xml2json(node);
	}
	@Override
	public String xml2json(String xmlString) throws Exception {
		return xml2json(this.xmlUtils.newElement(xmlString.getBytes()));
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
