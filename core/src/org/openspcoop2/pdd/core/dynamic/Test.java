/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.dynamic;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.slf4j.Logger;

/**
 * Test
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Test {

	private static final String ENVELOPE = 
		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
		"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body><prova>test</prova><prova2>test2</prova2><list>v1</list><list>v2</list><list>v3</list></soapenv:Body></soapenv:Envelope>";
	
	private static final String JSON = 
		"{\n"+	   
            "\"SortAs\": \"SGML\",\n"+
            "\"GlossTerm\": \"Standard Generalized Markup Language\",\n"+
            "\"Acronym\": \"SGML\",\n"+
            "\"Abbrev\": \"ISO 8879:1986\",\n"+
            "\"Enabled\": true,\n"+
            "\"Year\": 2018,\n"+
            "\"Quote\": 1.45, \n"+
            "\"List\": [ \"Ford\", \"BMW\", \"Fiat\" ]\n"+
		"}";
		
	private static final String JSON_TEMPLATE = 
			"{\n"+	   
	            "\"SortAs\": \"${xpath://prova/text()}\",\n"+
	            "\"GlossTerm\": \"Standard Generalized Markup Language\",\n"+
	            "\"Acronym\": \"${xPath://{http://schemas.xmlsoap.org/soap/envelope/}:Envelope/{http://schemas.xmlsoap.org/soap/envelope/}:Body/prova2/text()}\",\n"+
	            "\"Abbrev\": \"ISO 8879:1986\",\n"+
	            "\"Enabled\": true,\n"+
	            "\"Year\": 2018,\n"+
	            "\"Quote\": 1.45 \n"+
			"}";
	
	private static final String XML_TEMPLATE = 
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
			"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body><prova>${jsonpath:$.Year}</prova><prova2>${jsonPath:$.Acronym}</prova2></soapenv:Body></soapenv:Envelope>";
	
	public static void main(String [] args) throws Exception{
		
		boolean forceDollaro = true;
		String prefix = "$";
		
		Logger log = LoggerWrapperFactory.getLogger(Test.class);
		
		Map<String, Object> dynamicMap = new HashMap<>();
		
		ConnettoreMsg connettoreMsg = new ConnettoreMsg();
		
		connettoreMsg.setPropertiesTrasporto(new HashMap<String, List<String>>());
		TransportUtils.addHeader(connettoreMsg.getPropertiesTrasporto(),"Header1", "Valore1");
		TransportUtils.addHeader(connettoreMsg.getPropertiesTrasporto(),"Header2", "Valore2");
		
		connettoreMsg.setPropertiesUrlBased(new HashMap<String, List<String>>());
		TransportUtils.addParameter(connettoreMsg.getPropertiesUrlBased(),"P1", "Valore1URL");
		TransportUtils.addParameter(connettoreMsg.getPropertiesUrlBased(),"P2", "Valore2URL");
		
		
		PdDContext pddContext = new PdDContext();
		pddContext.addObject("TEST1", "VALORE DI ESEMPIO");
		
		DynamicInfo dInfo = new DynamicInfo(connettoreMsg, pddContext);
		
		
		DynamicUtils.fillDynamicMap(log, dynamicMap, dInfo);
		
		System.out.println("TEST PDD CONTEXT: "+DynamicUtils.convertDynamicPropertyValue("testPddContext", prefix + "{context:TEST1}", dynamicMap, pddContext, forceDollaro));
	      
		System.out.println("HEADER: "+DynamicUtils.convertDynamicPropertyValue("testHeader", prefix+"{header:Header1}", dynamicMap, pddContext, forceDollaro));
		
		System.out.println("URL: "+DynamicUtils.convertDynamicPropertyValue("testUrl", prefix+"{query:P1}", dynamicMap, pddContext, forceDollaro));
		
		dynamicMap = new HashMap<>();
		dInfo = new DynamicInfo(connettoreMsg, pddContext);
		dInfo.setXml(XMLUtils.DEFAULT.newElement(ENVELOPE.getBytes()));
		DynamicUtils.fillDynamicMap(log, dynamicMap, dInfo);
		
		System.out.println("Pattern1: "+DynamicUtils.convertDynamicPropertyValue("testXml", prefix+"{xPath://{http://schemas.xmlsoap.org/soap/envelope/}:Envelope/{http://schemas.xmlsoap.org/soap/envelope/}:Body/prova/text()}", dynamicMap, pddContext, forceDollaro));
	
		System.out.println("Pattern2: "+DynamicUtils.convertDynamicPropertyValue("testXml2", prefix+"{xpath://prova/text()}", dynamicMap, pddContext, forceDollaro));
		
		System.out.println("Pattern3: "+DynamicUtils.convertDynamicPropertyValue("testXml3", "Metto un po ("+prefix+"{xPath://prova/text()}) di testo prima ("+prefix+"{xPath://{http://schemas.xmlsoap.org/soap/envelope/}:Envelope/{http://schemas.xmlsoap.org/soap/envelope/}:Body/prova2/text()}) e dopo", dynamicMap, pddContext, forceDollaro));
		
		System.out.println("Pattern4: "+DynamicUtils.convertDynamicPropertyValue("testXmlPatternNotFound", prefix+"{xpath://provaNonEsiste/text()}", dynamicMap, pddContext, forceDollaro));
		
		try {
			System.out.println("Pattern5: "+DynamicUtils.convertDynamicPropertyValue("testXmlPatternError", prefix+"{xpath://provaNonEsiste/Text()}", dynamicMap, pddContext, forceDollaro));
			throw new Exception("Attesa eccezione pattern malformato");
		}catch(Exception e) {
			System.out.println("Pattern5: attesa eccezione "+e.getMessage());
			//e.printStackTrace(System.out);
		}
		
		if(prefix.equals("$")) {
			System.out.println("Test conversione xml2json: \n"+DynamicUtils.convertDynamicPropertyValue("xml2json", JSON_TEMPLATE, dynamicMap, pddContext, forceDollaro));
		}
		
		if(prefix.equals("$")) {
			byte[]template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJson.ftl")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DynamicUtils.convertFreeMarkerTemplate("xml2jsonFTL", template, dynamicMap, bout);
			bout.flush();
			bout.close();
			System.out.println("Test conversione xml2json via freemarker: \n"+bout.toString());
		}
		
		if(prefix.equals("$")) {
			byte[]template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude.ftl")).toByteArray();
			byte[]prova1 = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude_1.ftl")).toByteArray();
			byte[]prova2 = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude_2.ftl")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			Map<String, byte[]> templateIncludes = new HashMap<>();
			templateIncludes.put("TestJsonInclude_1.ftl", prova1);
			templateIncludes.put("lib/TestJsonInclude_2.ftl", prova2);
			DynamicUtils.convertFreeMarkerTemplate("xml2jsonFTL_INCLUDE_MANUALE", template, templateIncludes, dynamicMap, bout);
			bout.flush();
			bout.close();
			System.out.println("Test conversione xml2json via freemarker (con include): \n"+bout.toString());
		}
		
		if(prefix.equals("$")) {
			byte[]zip = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude.ftl.zip")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DynamicUtils.convertZipFreeMarkerTemplate("xml2jsonFTL_INCLUDE_ZIP", zip, dynamicMap, bout);
			bout.flush();
			bout.close();
			System.out.println("Test conversione xml2json via freemarker (con include in archivio zip): \n"+bout.toString());
		}
		
		if(prefix.equals("$")) {
			byte[]zip = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude2.ftl.zip")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DynamicUtils.convertZipFreeMarkerTemplate("xml2jsonFTL_INCLUDE_ZIP2", zip, dynamicMap, bout);
			bout.flush();
			bout.close();
			System.out.println("Test conversione xml2json via freemarker (con include in archivio zip test2): \n"+bout.toString());
		}
		
		dynamicMap = new HashMap<>();
		dInfo = new DynamicInfo(connettoreMsg, pddContext);
		dInfo.setJson(JSON);
		DynamicUtils.fillDynamicMap(log, dynamicMap, dInfo);
		
		System.out.println("Pattern1: "+DynamicUtils.convertDynamicPropertyValue("testJson", prefix+"{jsonPath:$.Year}", dynamicMap, pddContext, forceDollaro));
		
		System.out.println("Pattern2: "+DynamicUtils.convertDynamicPropertyValue("test2Json", "Metto un po ("+prefix+"{jsonpath:$.Year}) di altro test ("+prefix+"{jsonPath:$.Acronym}) fine", dynamicMap, pddContext, forceDollaro));
		
		System.out.println("Pattern3: "+DynamicUtils.convertDynamicPropertyValue("testJsonPatternNotFound", prefix+"{jsonPath:$.NotFound}", dynamicMap, pddContext, forceDollaro));
		
		try {
			System.out.println("Pattern4: "+DynamicUtils.convertDynamicPropertyValue("testJsonPatternError", prefix+"{jsonPath:$$$dedde}", dynamicMap, pddContext, forceDollaro));
			throw new Exception("Attesa eccezione pattern malformato");
		}catch(Exception e) {
			System.out.println("Pattern4: attesa eccezione "+e.getMessage());
			//e.printStackTrace(System.out);
		}
		
		if(prefix.equals("$")) {
			System.out.println("Test conversione json2xml: \n"+DynamicUtils.convertDynamicPropertyValue("json2xml", XML_TEMPLATE, dynamicMap, pddContext, forceDollaro));
		}
		
		if(prefix.equals("$")) {
			byte[]template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestXml.ftl")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DynamicUtils.convertFreeMarkerTemplate("json2xmlFTL", template,  dynamicMap, bout);
			bout.flush();
			bout.close();
			System.out.println("Test conversione json2xml via freemarker: \n"+bout.toString());
		}
		
		
		
		String url = "/govway/out/ENTE/Erogatore/Servizio/v1/azione/test?prova=test&azione=az2";
		dynamicMap = new HashMap<>();
		dInfo = new DynamicInfo(connettoreMsg, pddContext);
		dInfo.setUrl(url);
		DynamicUtils.fillDynamicMap(log, dynamicMap, dInfo);
		
		System.out.println("PatternUrl1: "+DynamicUtils.convertDynamicPropertyValue("testUrl", prefix+"{urlRegExp:.+azione=([^&]*).*}", dynamicMap, pddContext, forceDollaro));
		
		if(prefix.equals("$")) {
			byte[]template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestUrl.ftl")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DynamicUtils.convertFreeMarkerTemplate("testUrlFTL", template, dynamicMap, bout);
			bout.flush();
			bout.close();
			System.out.println("Test conversione via freemarker: \n"+bout.toString());
		}
	}
	
}
