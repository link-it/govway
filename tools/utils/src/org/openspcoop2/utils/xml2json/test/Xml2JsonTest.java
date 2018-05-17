/**
 * 
 */
package org.openspcoop2.utils.xml2json.test;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml2json.IJson2Xml;
import org.openspcoop2.utils.xml2json.IXml2Json;
import org.openspcoop2.utils.xml2json.Xml2JsonFactory;
import org.w3c.dom.Node;

import com.google.common.base.Charsets;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 16 mag 2018 $
 * 
 */
public class Xml2JsonTest {


	public static void main(String[] args) throws Exception {

		String xmlString = Utilities.getAsString(Xml2JsonTest.class.getResource("file.xml"), Charsets.ISO_8859_1.name());
		Map<String, String> xmlToJsonNamespaces = new HashMap<String,String>(1);
		xmlToJsonNamespaces.put("http://www.link.it/", "b");
		xmlToJsonNamespaces.put("http://www.link.it/employee", "a");
		System.out.println("XML iniziale: "+xmlString);
		{
			IXml2Json xml2json = Xml2JsonFactory.getXml2JsonJsonML();
			IJson2Xml json2xml = Xml2JsonFactory.getJson2XmlJsonML();
			test(xmlString, "JSONML (String)", xml2json, json2xml);
		}
		{
			IXml2Json xml2json = Xml2JsonFactory.getXml2JsonMapped(xmlToJsonNamespaces);
			IJson2Xml json2xml = Xml2JsonFactory.getJson2XmlMapped(xmlToJsonNamespaces);
			test(xmlString, "Mapped (String)", xml2json, json2xml);
		}
		{
			IXml2Json xml2json = Xml2JsonFactory.getXml2JsonBadgerFish();
			IJson2Xml json2xml = Xml2JsonFactory.getJson2XmlBadgerFish();
			test(xmlString, "BadgerFish (String)", xml2json, json2xml);
		}

		XMLUtils instance = XMLUtils.getInstance();
		Node node = instance.newElement(xmlString.getBytes());

		{
			IXml2Json xml2json = Xml2JsonFactory.getXml2JsonJsonML();
			IJson2Xml json2xml = Xml2JsonFactory.getJson2XmlJsonML();
			test(instance, node, "JSONML (Node)", xml2json, json2xml);
		}
		{
			IXml2Json xml2json = Xml2JsonFactory.getXml2JsonMapped(xmlToJsonNamespaces);
			IJson2Xml json2xml = Xml2JsonFactory.getJson2XmlMapped(xmlToJsonNamespaces);
			test(instance, node, "Mapped (Node)", xml2json, json2xml);
		}
		{
			IXml2Json xml2json = Xml2JsonFactory.getXml2JsonBadgerFish();
			IJson2Xml json2xml = Xml2JsonFactory.getJson2XmlBadgerFish();
			test(instance, node, "BadgerFish (Node)", xml2json, json2xml);
		}

	}

	private static void test(String xml, String library, IXml2Json xml2json, IJson2Xml json2xml)
			throws Exception {
		System.out.println("---------------"+library+"-------------");
		String json = xml2json.xml2json(xml);
		System.out.println("Library ["+library+"]. XML -> Json: "+json);
		String xmlAfter = json2xml.json2xml(json);
		System.out.println("Library ["+library+"]. Json-> XML: " + xmlAfter);
		System.out.println("---------------"+library+"-------------");
	}

	private static void test(XMLUtils xmlUtils, Node xml, String library, IXml2Json xml2json, IJson2Xml json2xml)
			throws Exception {
		System.out.println("---------------"+library+"-------------");
		String json = xml2json.xml2json(xml);
		System.out.println("Library ["+library+"]. XML -> Json: "+json);
		Node xmlNode = json2xml.json2xmlNode(json);
		String xmlAfter = xmlUtils.toString(xmlNode);
		System.out.println("Library ["+library+"]. Json-> XML: " + xmlAfter);
		System.out.println("---------------"+library+"-------------");
	}

}
