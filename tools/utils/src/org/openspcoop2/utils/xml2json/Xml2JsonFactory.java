/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import java.util.Map;

import org.codehaus.jettison.mapped.Configuration;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $ Rev: 12563 $, $Date$
 * 
 */
public class Xml2JsonFactory {
	
	public static IXml2Json getXml2JsonJsonML(){
		return new JsonMLXml2Json();
	}
	public static IXml2Json getXml2JsonBadgerFish() {
		return new BadgerFishXml2Json(); 
	}
	public static IXml2Json getXml2JsonMapped() {
		return new MappedXml2Json();
	}
	public static IXml2Json getXml2JsonMapped(Configuration configuration) {
		return new MappedXml2Json(configuration);
	}
	public static IXml2Json getXml2JsonMapped(Map<String, String> xmlToJsonNamespaces) {
		return new MappedXml2Json(xmlToJsonNamespaces);
	}

	public static IJson2Xml getJson2XmlJsonML(){
		return new JsonMLJson2Xml();
	}
	public static IJson2Xml getJson2XmlBadgerFish() {
		return new BadgerFishJson2Xml(); 
	}
	public static IJson2Xml getJson2XmlMapped(Configuration configuration) {
		return new MappedJson2Xml(configuration);
	}
	public static IJson2Xml getJson2XmlMapped(Map<String, String> xmlToJsonNamespaces) {
		return new MappedJson2Xml(xmlToJsonNamespaces);
	}
}
