/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML;
import org.openspcoop2.message.AttachmentsProcessingMode;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.PddProperties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.cache.CacheType;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
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
	
	private static final String EXPECTED_JSON = 
			"{\n"+	   
	            "\"SortAs\": \"test\",\n"+
	            "\"GlossTerm\": \"Standard Generalized Markup Language\",\n"+
	            "\"Acronym\": \"test2\",\n"+
	            "\"Abbrev\": \"ISO 8879:1986\",\n"+
	            "\"Enabled\": true,\n"+
	            "\"Year\": 2018,\n"+
	            "\"Quote\": 1.45 \n"+
			"}";
	private static final String EXPECTED_JSON_2 = 
			"{\n"+	   
	            "    \"SortAs\": \"test\",\n"+
	            "    \"GlossTerm\": \"Standard Generalized Markup Language\",\n"+
	            "    \"Acronym\": \"test2\",\n"+
	            "    \"Abbrev\": \"ISO 8879:1986\",\n"+
	            "    \"Enabled\": true,\n"+
	            "    \"Year\": 2018,\n"+
	            "    \"Quote\": 1.45,\n"+
	            "    \"List\": [ \"v1\" ,\"v2\" ,\"v3\"  ]\n"+
			"}";
	private static final String EXPECTED_JSON_3 = 
			"{\n"+	   
	            "    \"SortAs\": \"test\",\n"+
	            "    \"GlossTerm\": \"Standard Generalized Markup Language\",\n"+
	            "    \"Acronym\": \"test2\",\n"+
	            "    \"Abbrev\": \"ISO 8879:1986\",\n"+
	            "    \"Enabled\": true,\n"+
	            "    \"Year\": 2018,\n"+
	            "    \"Quote\": 1.45,\n"+
	            "    \"include1\": {\n"+
	            "		\"SortAs\": \"111_test\",\n"+
	            "		\"GlossTerm\": \"TestPROVA1\"\n"+
	            "    },\n"+
	            "    \"include2\": {\n"+
	            "		\"SortAs\": \"222_test\",\n"+
	            "		\"GlossTerm\": \"TestPROVA2\"\n"+
	            "    },\n"+
	            "    \"List\": [ \"v1\" ,\"v2\" ,\"v3\"  ]\n"+
			"}";
			
	private static final String EXPECTED_XML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" 	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body><prova>2018</prova><prova2>SGML</prova2></soapenv:Body></soapenv:Envelope>";
	
	private static final String EXPECTED_XML_2 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n"+ 
			"			xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"+
			"			<soapenv:Body>\n"+
			"			<prova>2018</prova>\n"+
			"			<prova2>SGML</prova2>\n"+
			"			<list>Ford</list>\n"+
			"			<list>BMW</list>\n"+
			"			<list>Fiat</list>\n"+
			"		</soapenv:Body>\n"+
			"</soapenv:Envelope>\n"+
			"";
	
	private static final String EXPECTED_XML_3 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n"+
			"			xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"+
			"			<soapenv:Body>\n"+
			"			<prova>az2</prova>\n"+
			"			<prova2>test</prova2>\n"+
			"		</soapenv:Body>\n"+
			"</soapenv:Envelope>\n";
	
	private static final String CONFIG = "<openspcoop2 xmlns=\"http://www.openspcoop2.org/core/config\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openspcoop2.org/core/config config.xsd\">\n"+
			"	<soggetto tipo=\"proxy\" nome=\"MinisteroFruitore\" />\n"+
			"   <configurazione> \n"+
			"          <accesso-registro>\n"+
			"               <registro nome=\"registroXML\" tipo=\"xml\" location=\"registroServizi.xml\" />\n"+
			"          </accesso-registro>\n"+
			"          <inoltro-buste-non-riscontrate cadenza=\"60\" />\n"+
			"          <messaggi-diagnostici severita-log4j=\"infoIntegration\" severita=\"infoIntegration\" />\n"+
			"          <system-properties>\n"+
			"          		<system-property nome=\"systemP1\" valore=\"systemV1\"/>\n"+
			"          		<system-property nome=\"systemP2\" valore=\"systemV2\"/>\n"+
			"          </system-properties>\n"+
			"     </configurazione>\n"+
			"</openspcoop2>";
	
	public static void main(String [] args) throws Exception{
		
		File fTmpConfig = File.createTempFile("configTest", ".xml"); 
		File fTmpOp2Properties = File.createTempFile("govway", ".properties"); 
		try {
		
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
		
		FileSystemUtilities.writeFile(fTmpOp2Properties.getAbsolutePath(), "org.openspcoop2.pdd.confDirectory=/tmp\norg.openspcoop2.pdd.server=web".getBytes());
		OpenSPCoop2Properties.initialize(null, fTmpOp2Properties.getAbsolutePath());
		PddProperties.initialize(fTmpOp2Properties.getAbsolutePath(), null);
		
		AccessoConfigurazionePdD config = new AccessoConfigurazionePdD();
		config.setTipo("xml");
		config.setLocation(fTmpConfig.getAbsolutePath());
		FileSystemUtilities.writeFile(fTmpConfig.getAbsolutePath(), CONFIG.getBytes());
		DriverConfigurazioneXML.disableBuildXsdValidator();
		ConfigurazionePdDReader.initialize(config, log, log, null, null, true, false, false, false, null, CacheType.JCS);
						
		PdDContext pddContext = new PdDContext();
		MapKey<String> TEST1 = org.openspcoop2.utils.Map.newMapKey("TEST1");
		pddContext.addObject(TEST1, "VALORE DI ESEMPIO");
		
		boolean bufferMessage_readOnly = true;
		String idTransazione = "xxyy";
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, idTransazione);
				
		DynamicInfo dInfo = new DynamicInfo(connettoreMsg, pddContext);
		
		
		DynamicUtils.fillDynamicMap(log, dynamicMap, dInfo);
		
		String expr = prefix + "{transaction:id}";
		DynamicUtils.validate("testTransactionId", expr, forceDollaro, true);
		String value = DynamicUtils.convertDynamicPropertyValue("testTransactionId", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("TransactionID: "+value+"\n\n");
		String expected = idTransazione;
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix + "{context:TEST1}";
		DynamicUtils.validate("testPddContext", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testPddContext", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("testPddContext: "+value+"\n\n");
		expected = "VALORE DI ESEMPIO";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix + "{header:Header1}";
		DynamicUtils.validate("testHeader", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testHeader", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("testHeader: "+value+"\n\n");
		expected = "Valore1";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix + "{query:P1}";
		DynamicUtils.validate("testUrl", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testUrl", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("testUrl: "+value+"\n\n");
		expected = "Valore1URL";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		
		dynamicMap = new HashMap<>();
		dInfo = new DynamicInfo(connettoreMsg, pddContext);
		OpenSPCoop2MessageParseResult parser = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(MessageType.SOAP_11, MessageRole.NONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, ENVELOPE.getBytes(), AttachmentsProcessingMode.getMemoryCacheProcessingMode());
		MessageContent messageContent = new MessageContent(parser.getMessage().castAsSoap(), bufferMessage_readOnly, pddContext);
		dInfo.setMessageContent(messageContent);
		DynamicUtils.fillDynamicMap(log, dynamicMap, dInfo);
		
		expr = prefix+"{xPath://{http://schemas.xmlsoap.org/soap/envelope/}:Envelope/{http://schemas.xmlsoap.org/soap/envelope/}:Body/prova/text()}";
		DynamicUtils.validate("testXml", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testXml", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("Pattern1: "+value);
		expected = "test";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix+"{xPath://{http://schemas.xmlsoap.org/soap/envelope/}Envelope/{http://schemas.xmlsoap.org/soap/envelope/}Body/prova/text()}";
		DynamicUtils.validate("testXml", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testXml", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("Pattern1b: "+value);
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "{xPath://{http://schemas.xmlsoap.org/soap/envelope/}Envelope/{http://schemas.xmlsoap.org/soap/envelope/}Body/prova/text()}";
		DynamicUtils.validate("testXml", expr, !forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testXml", expr, dynamicMap, pddContext, !forceDollaro);
		System.out.println("Pattern1c: "+value);
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix+"{xpath://prova/text()}";
		DynamicUtils.validate("testXml2", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testXml2", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("Pattern2: "+value);
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "Metto un po ("+prefix+"{xPath://prova/text()}) di testo prima ("+prefix+"{xPath://{http://schemas.xmlsoap.org/soap/envelope/}:Envelope/{http://schemas.xmlsoap.org/soap/envelope/}:Body/prova2/text()}) e dopo";
		DynamicUtils.validate("testXml3", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testXml3", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("Pattern3: "+value);
		expected = "Metto un po (test) di testo prima (test2) e dopo";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix+"{xpath://provaNonEsiste/text()}";
		DynamicUtils.validate("testXmlPatternNotFound", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testXmlPatternNotFound", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("Pattern4: "+value);
		expected = "";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix+"{xpath://provaNonEsiste/Text()}";
		DynamicUtils.validate("testXmlPatternError", expr, forceDollaro, true);
		try {
			System.out.println("Pattern5: "+DynamicUtils.convertDynamicPropertyValue("testXmlPatternError", expr, dynamicMap, pddContext, forceDollaro));
			throw new Exception("Attesa eccezione pattern malformato");
		}catch(Exception e) {
			//e.printStackTrace(System.out);
			if(e.getMessage().contains("Compilazione dell'espressione XPATH ha causato un errore (Unknown nodetype: Text)")) {
				System.out.println("Pattern5: attesa eccezione "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		expr = prefix+"{xPath://{http://schemas.xmlsoap.org/soap/envelope/}Envelope/{http://schemas.xmlsoap.org/soap/envelope/}Body/prova/text()";
		try {
			DynamicUtils.validate("testChiusuraMancante", expr, forceDollaro, true);
			throw new Exception("Attesa eccezione expr malformata");
		}catch(Exception e) {
			if(e.getMessage().contains("Trovata istruzione 'xPath' non correttamente formata (chiusura '}' non trovata)")) {
				System.out.println("PatternExprErrata1: attesa eccezione "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		try {
			value = DynamicUtils.convertDynamicPropertyValue("testChiusuraMancante", expr, dynamicMap, pddContext, forceDollaro);
			throw new Exception("Attesa eccezione expr malformata");
		}catch(Exception e) {
			if(e.getMessage().contains("Trovata istruzione 'xPath' non correttamente formata (chiusura '}' non trovata)")) {
				System.out.println("PatternExprErrata1: attesa eccezione "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		expr = prefix+"{xPath://{http://schemas.xmlsoap.org/soap/envelope/}Envelope/{http://schemas.xmlsoap.org/soap/envelope/Body/prova/text()}";
		try {
			DynamicUtils.validate("testChiusuraInternaMancante", expr, forceDollaro, true);
			throw new Exception("Attesa eccezione expr malformata");
		}catch(Exception e) {
			if(e.getMessage().contains("Trovata istruzione 'xPath' non correttamente formata (chiusura '}' non trovata)")) {
				System.out.println("PatternExprErrata2: attesa eccezione "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		try {
			value = DynamicUtils.convertDynamicPropertyValue("testChiusuraInternaMancante", expr, dynamicMap, pddContext, forceDollaro);
			throw new Exception("Attesa eccezione expr malformata");
		}catch(Exception e) {
			if(e.getMessage().contains("Trovata istruzione 'xPath' non correttamente formata (chiusura '}' non trovata)")) {
				System.out.println("PatternExprErrata2: attesa eccezione "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		if(prefix.equals("$")) {
			expr = JSON_TEMPLATE;
			DynamicUtils.validate("xml2json", expr, forceDollaro, true);
			value = DynamicUtils.convertDynamicPropertyValue("xml2json", JSON_TEMPLATE, dynamicMap, pddContext, forceDollaro);
			System.out.println("Test conversione xml2json: \n"+value);
			expected = EXPECTED_JSON;
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
		}
		
		if(prefix.equals("$")) {
			
			// ** freemarker **
			byte[]template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJson.ftl")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			Template templateObject = new Template("xml2jsonFTL", template);
			DynamicUtils.convertFreeMarkerTemplate(templateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via freemarker: \n"+value);
			expected = EXPECTED_JSON_2;
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
			// ** velocity **
			template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJson.vm")).toByteArray();
			bout = new ByteArrayOutputStream();
			templateObject = new Template("xml2jsonVelocity", template);
			DynamicUtils.convertVelocityTemplate(templateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via velocity: \n"+value);
			expected = EXPECTED_JSON_2;
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
		}
		
		if(prefix.equals("$")) {
			
			// ** freemarker **
			byte[]template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude.ftl")).toByteArray();
			byte[]prova1 = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude_1.ftl")).toByteArray();
			byte[]prova2 = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude_2.ftl")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			Map<String, byte[]> templateIncludes = new HashMap<>();
			templateIncludes.put("TestJsonInclude_1.ftl", prova1);
			templateIncludes.put("lib/TestJsonInclude_2.ftl", prova2);
			Template templateObject = new Template("xml2jsonFTL_INCLUDE_MANUALE", template, templateIncludes);
			DynamicUtils.convertFreeMarkerTemplate(templateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via freemarker (con include): \n"+value);
			expected = EXPECTED_JSON_3;
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
			// ** velocity **
			template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude.vm")).toByteArray();
			prova1 = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude_1.vm")).toByteArray();
			prova2 = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude_2.vm")).toByteArray();
			bout = new ByteArrayOutputStream();
			templateIncludes = new HashMap<>();
			templateIncludes.put("TestJsonInclude_1.vm", prova1);
			templateIncludes.put("lib/TestJsonInclude_2.vm", prova2);
			templateObject = new Template("xml2jsonVelocity_INCLUDE_MANUALE", template, templateIncludes);
			DynamicUtils.convertVelocityTemplate(templateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via velocity (con include): \n"+value);
			expected = EXPECTED_JSON_3;
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
		}
		
		if(prefix.equals("$")) {
			
			// ** freemarker **
			byte[]zip = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude.ftl.zip")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ZipTemplate zipTemplateObject = new ZipTemplate("xml2jsonFTL_INCLUDE_ZIP", zip);
			DynamicUtils.convertZipFreeMarkerTemplate(zipTemplateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via freemarker (con include in archivio zip): \n"+value);
			expected = EXPECTED_JSON_3+"\n";
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
			// Riprovo passando da template
			zip = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude.ftl.zip")).toByteArray();
			bout = new ByteArrayOutputStream();
			Template templateObject = new Template("xml2jsonFTL_INCLUDE_ZIP", zip);
			zipTemplateObject = templateObject.getZipTemplate();
			DynamicUtils.convertZipFreeMarkerTemplate(zipTemplateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via freemarker (2) (con include in archivio zip): \n"+value);
			expected = EXPECTED_JSON_3+"\n";
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
			
			// ** velocity **
			zip = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude.vm.zip")).toByteArray();
			bout = new ByteArrayOutputStream();
			zipTemplateObject = new ZipTemplate("xml2jsonVelocity_INCLUDE_ZIP", zip);
			DynamicUtils.convertZipVelocityTemplate(zipTemplateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via velocity (con include in archivio zip): \n"+value);
			expected = EXPECTED_JSON_3+"\n";
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
			// Riprovo passando da template
			zip = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude.vm.zip")).toByteArray();
			bout = new ByteArrayOutputStream();
			templateObject = new Template("xml2jsonVelocity_INCLUDE_ZIP", zip);
			zipTemplateObject = templateObject.getZipTemplate();
			DynamicUtils.convertZipVelocityTemplate(zipTemplateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via velocity (2) (con include in archivio zip): \n"+value);
			expected = EXPECTED_JSON_3+"\n";
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
		}
		
		if(prefix.equals("$")) {
			
			// ** freemarker **
			byte[]zip = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude2.ftl.zip")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ZipTemplate zipTemplateObject = new ZipTemplate("xml2jsonFTL_INCLUDE_ZIP2", zip);
			DynamicUtils.convertZipFreeMarkerTemplate(zipTemplateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via freemarker (con include in archivio zip test2): \n"+value);
			expected = EXPECTED_JSON_3+"\n";
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
			// Riprovo passando da template
			zip = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude2.ftl.zip")).toByteArray();
			bout = new ByteArrayOutputStream();
			Template templateObject = new Template("xml2jsonFTL_INCLUDE_ZIP2", zip);
			zipTemplateObject = templateObject.getZipTemplate();
			DynamicUtils.convertZipFreeMarkerTemplate(zipTemplateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via freemarker (2) (con include in archivio zip test2): \n"+value);
			expected = EXPECTED_JSON_3+"\n";
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
			
			// ** velocity **
			zip = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude2.vm.zip")).toByteArray();
			bout = new ByteArrayOutputStream();
			zipTemplateObject = new ZipTemplate("xml2jsonVelocity_INCLUDE_ZIP2", zip);
			DynamicUtils.convertZipVelocityTemplate(zipTemplateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via velocity (con include in archivio zip test2): \n"+value);
			expected = EXPECTED_JSON_3+"\n";
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
			// Riprovo passando da template
			zip = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestJsonInclude2.vm.zip")).toByteArray();
			bout = new ByteArrayOutputStream();
			templateObject = new Template("xml2jsonVelocity_INCLUDE_ZIP2", zip);
			zipTemplateObject = templateObject.getZipTemplate();
			DynamicUtils.convertZipVelocityTemplate(zipTemplateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione xml2json via velocity (2) (con include in archivio zip test2): \n"+value);
			expected = EXPECTED_JSON_3+"\n";
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
		}
		
		if(prefix.equals("$")) {
			
			// ** freemarker **
			Date inizio = DateManager.getDate();
			boolean debug = false;
			int threadsNum = 100;
			ExecutorService threadsPool = Executors.newFixedThreadPool(threadsNum);
			Map<String, ClientTestThread> threads = new HashMap<String, ClientTestThread>();
			
			byte[]template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestDynamic.ftl")).toByteArray();
			Template templateObject = new Template("xml2jsonDynamicFTL", template);
			// verifico thread safe template
			
			boolean error = false;
			Exception exception = null;
			try {
			
				for (int i = 0; i < threadsNum; i++) {
					String threadName = "Thread-"+i;
					ClientTestThread c = new ClientTestThread(threadName,templateObject, true);
					threadsPool.execute(c);
					threads.put(threadName, c);
				}
			
				boolean terminated = false;
				while(terminated == false){
					if(debug)
						System.out.println("Attendo terminazione ...");
					boolean tmpTerminated = true;
					for (int i = 0; i < threadsNum; i++) {
						
						String threadName = "Thread-"+i;
						ClientTestThread c = threads.get(threadName);
						if(c.isError()){
							error = true;
							exception = c.getException();
						}
						if(c.isFinished()==false){
							tmpTerminated = false;
							break;
						}
					}
					if(tmpTerminated==false){
						Utilities.sleep(250);
					}
					else{
						terminated = true;
					}
				}
				
			} finally{
				threadsPool.shutdown(); 
			}
			
			
			Date fine = DateManager.getDate();
			long diff = fine.getTime() - inizio.getTime();
			System.out.println("Tempo impiegato: "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));
			
			if(error){
				throw new Exception("Error occurs in threads: "+exception.getMessage(),exception);
			}
		}
		
		if(prefix.equals("$")) {
			
			// ** velocity **
			Date inizio = DateManager.getDate();
			boolean debug = false;
			int threadsNum = 100;
			ExecutorService threadsPool = Executors.newFixedThreadPool(threadsNum);
			Map<String, ClientTestThread> threads = new HashMap<String, ClientTestThread>();
			
			byte[]template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestDynamic.vm")).toByteArray();
			Template templateObject = new Template("xml2jsonDynamicVelocity", template);
			// verifico thread safe template
			
			boolean error = false;
			Exception exception = null;
			try {
			
				for (int i = 0; i < threadsNum; i++) {
					String threadName = "Thread-"+i;
					ClientTestThread c = new ClientTestThread(threadName,templateObject, false);
					threadsPool.execute(c);
					threads.put(threadName, c);
				}
			
				boolean terminated = false;
				while(terminated == false){
					if(debug)
						System.out.println("Attendo terminazione ...");
					boolean tmpTerminated = true;
					for (int i = 0; i < threadsNum; i++) {
						
						String threadName = "Thread-"+i;
						ClientTestThread c = threads.get(threadName);
						if(c.isError()){
							error = true;
							exception = c.getException();
						}
						if(c.isFinished()==false){
							tmpTerminated = false;
							break;
						}
					}
					if(tmpTerminated==false){
						Utilities.sleep(250);
					}
					else{
						terminated = true;
					}
				}
				
			} finally{
				threadsPool.shutdown(); 
			}
			
			
			Date fine = DateManager.getDate();
			long diff = fine.getTime() - inizio.getTime();
			System.out.println("Tempo impiegato: "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));
			
			if(error){
				throw new Exception("Error occurs in threads: "+exception.getMessage(),exception);
			}
		}
		
		
		dynamicMap = new HashMap<>();
		dInfo = new DynamicInfo(connettoreMsg, pddContext);
		OpenSPCoop2MessageParseResult parserJson = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(MessageType.JSON, MessageRole.NONE, HttpConstants.CONTENT_TYPE_JSON, JSON.getBytes(), AttachmentsProcessingMode.getMemoryCacheProcessingMode());
		MessageContent messageContentJson = new MessageContent(parserJson.getMessage().castAsRestJson(), bufferMessage_readOnly, pddContext);
		dInfo.setMessageContent(messageContentJson);
		DynamicUtils.fillDynamicMap(log, dynamicMap, dInfo);
		
		expr = prefix+"{jsonPath:$.Year}";
		DynamicUtils.validate("testJson", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testJson", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("Pattern1: "+value);
		expected = "2018";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "Metto un po ("+prefix+"{jsonpath:$.Year}) di altro test ("+prefix+"{jsonPath:$.Acronym}) fine";
		DynamicUtils.validate("test2Json", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("test2Json", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("Pattern2: "+value);
		expected = "Metto un po (2018) di altro test (SGML) fine";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix+"{jsonPath:$.NotFound}";
		DynamicUtils.validate("testJsonPatternNotFound", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testJsonPatternNotFound", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("Pattern3: "+value);
		expected = "";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix+"{jsonPath:$$$dedde}";
		DynamicUtils.validate("testJsonPatternError", expr, forceDollaro, true);
		try {
			System.out.println("Pattern4: "+DynamicUtils.convertDynamicPropertyValue("testJsonPatternError", expr, dynamicMap, pddContext, forceDollaro));
			throw new Exception("Attesa eccezione pattern malformato");
		}catch(Exception e) {
			if(e.getMessage().contains("Illegal character at position 1 expected '.' or '[")) {
				System.out.println("Pattern4: attesa eccezione "+e.getMessage());
			}
			else {
				throw e;
			}
			//e.printStackTrace(System.out);
		}
		
		expr = prefix+"{jsonPath:$.NotFound";
		try {
			DynamicUtils.validate("testChiusuraMancante", expr, forceDollaro, true);
			throw new Exception("Attesa eccezione expr malformata");
		}catch(Exception e) {
			if(e.getMessage().contains("Trovata istruzione 'jsonPath' non correttamente formata (chiusura '}' non trovata)")) {
				System.out.println("PatternExprErrata1: attesa eccezione "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		try {
			value = DynamicUtils.convertDynamicPropertyValue("testChiusuraMancante", expr, dynamicMap, pddContext, forceDollaro);
			throw new Exception("Attesa eccezione expr malformata");
		}catch(Exception e) {
			if(e.getMessage().contains("Trovata istruzione 'jsonPath' non correttamente formata (chiusura '}' non trovata)")) {
				System.out.println("PatternExprErrata1: attesa eccezione "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		if(prefix.equals("$")) {
			expr = XML_TEMPLATE;
			DynamicUtils.validate("json2xml", expr, forceDollaro, true);
			value = DynamicUtils.convertDynamicPropertyValue("json2xml", expr, dynamicMap, pddContext, forceDollaro);
			System.out.println("Test conversione json2xml: \n"+value);
			expected = EXPECTED_XML;
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
		}
		
		if(prefix.equals("$")) {
			
			// freemarker
			byte[]template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestXml.ftl")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			Template templateObject = new Template("json2xmlFTL", template);
			DynamicUtils.convertFreeMarkerTemplate(templateObject,  dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione json2xml via freemarker: \n"+value);
			expected = EXPECTED_XML_2;
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
			// velocity
			template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestXml.vm")).toByteArray();
			bout = new ByteArrayOutputStream();
			templateObject = new Template("json2xmlVelocity", template);
			DynamicUtils.convertVelocityTemplate(templateObject,  dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione json2xml via velocity: \n"+value);
			expected = EXPECTED_XML_2;
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
		}
		
		
		
		String url = "/govway/out/ENTE/Erogatore/Servizio/v1/azione/test?prova=test&azione=az2";
		dynamicMap = new HashMap<>();
		dInfo = new DynamicInfo(connettoreMsg, pddContext);
		dInfo.setUrl(url);
		DynamicUtils.fillDynamicMap(log, dynamicMap, dInfo);
		
		expr = prefix+"{urlRegExp:.+azione=([^&]*).*}";
		DynamicUtils.validate("testUrl", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testUrl", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("PatternUrl1: "+value);
		expected = "az2";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix+"{urlRegExp:.+azione=([^&]*).*";
		try {
			DynamicUtils.validate("testChiusuraMancante", expr, forceDollaro, true);
			throw new Exception("Attesa eccezione expr malformata");
		}catch(Exception e) {
			if(e.getMessage().contains("Trovata istruzione 'urlRegExp' non correttamente formata (chiusura '}' non trovata)")) {
				System.out.println("PatternExprErrata1: attesa eccezione "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		try {
			value = DynamicUtils.convertDynamicPropertyValue("testChiusuraMancante", expr, dynamicMap, pddContext, forceDollaro);
			throw new Exception("Attesa eccezione expr malformata");
		}catch(Exception e) {
			if(e.getMessage().contains("Trovata istruzione 'urlRegExp' non correttamente formata (chiusura '}' non trovata)")) {
				System.out.println("PatternExprErrata1: attesa eccezione "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		if(prefix.equals("$")) {
			
			// freemarker
			byte[]template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestUrl.ftl")).toByteArray();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			Template templateObject = new Template("testUrlFTL", template);
			DynamicUtils.convertFreeMarkerTemplate(templateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione via freemarker: \n"+value);
			expected = EXPECTED_XML_3;
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
			
			// velocity
			template = Utilities.getAsByteArrayOuputStream(Test.class.getResourceAsStream("/org/openspcoop2/pdd/core/dynamic/TestUrl.vm")).toByteArray();
			bout = new ByteArrayOutputStream();
			templateObject = new Template("testUrlVelocity", template);
			DynamicUtils.convertVelocityTemplate(templateObject, dynamicMap, bout);
			bout.flush();
			bout.close();
			value = bout.toString();
			System.out.println("Test conversione via velocity: \n"+value);
			expected = EXPECTED_XML_3;
			if(!expected.equals(value)) {
				throw new Exception("Expected value '"+expected+"', found '"+value+"'");
			}
						
		}
		
		
		Map<String, Object> mapLivello1 = new HashMap<String, Object>();
		Map<String, Object> mapLivello2 = new HashMap<String, Object>();
		mapLivello1.put("aa", mapLivello2);
		mapLivello2.put("attr1", "value1");
		mapLivello2.put("attr2", "value2");
		Map<String, Object> mapLivello22 = new HashMap<String, Object>();
		mapLivello1.put("aa2", mapLivello22);
		mapLivello22.put("attr21", "value21");
		Map<String, Object> mapLivello3 = new HashMap<String, Object>();
		mapLivello3.put("attr31", "value31");
		mapLivello3.put("attr32", "value32");
		mapLivello2.put("attr3", mapLivello3);
		List<String> arrayLivello4 = new ArrayList<String>();
		arrayLivello4.add("41");
		arrayLivello4.add("42");
		arrayLivello4.add("43");
		mapLivello2.put("attr4", arrayLivello4);
		TestMap multimap = new TestMap();
		multimap.map = mapLivello1;
		MapKey<String> MULTIMAP = org.openspcoop2.utils.Map.newMapKey("MULTIMAP");
		pddContext.addObject(MULTIMAP, multimap);
		TestMap map = new TestMap();
		map.map = mapLivello2;
		MapKey<String> MAP = org.openspcoop2.utils.Map.newMapKey("MAP");
		pddContext.addObject(MAP, map);
		
		expr = "attribute value: ${context:MAP.map[attr1]}";
		DynamicUtils.validate("testMAP", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testMAP", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("MapAttr: "+value+"\n\n");
		expected = "attribute value: value1";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "attribute value: ${context:MULTIMAP.map[aa][attr1]}";
		DynamicUtils.validate("testMULTIMAP", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testMULTIMAP", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("MultiMapAttr: "+value+"\n\n");
		expected = "attribute value: value1";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "attribute value: ${context:MULTIMAP.map[aa][attr2]}";
		DynamicUtils.validate("testMULTIMAP2", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testMULTIMAP2", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("MultiMapAttr2: "+value+"\n\n");
		expected = "attribute value: value2";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "attribute value: ${context:MULTIMAP.map[aa2][attr21]}";
		DynamicUtils.validate("testMULTIMAP3", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testMULTIMAP3", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("MultiMapAttr3: "+value+"\n\n");
		expected = "attribute value: value21";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "attribute value: ${context:MULTIMAP.map[aa][attr3][attr32]}";
		DynamicUtils.validate("testMULTIMAP4", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testMULTIMAP4", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("MultiMapAttr4: "+value+"\n\n");
		expected = "attribute value: value32";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "attribute value: ${context:MULTIMAP.map[aa][attr4][2]}";
		DynamicUtils.validate("testMULTIMAP5", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testMULTIMAP5", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("MultiMapAttr5: "+value+"\n\n");
		expected = "attribute value: 43";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		
		

		expr = "Valore da non rimpiazzare {transaction:id} insieme ad altro";
		DynamicUtils.validate("test1{}DaNonRisolvere", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("test1{}DaNonRisolvere", expr, dynamicMap, pddContext, true); // forceDollaro a true
		System.out.println("test1{}DaNonRisolvere: "+value+"\n\n");
		expected = expr;
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "{transaction:id}";
		DynamicUtils.validate("test2{}DaNonRisolvere", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("test2{}DaNonRisolvere", expr, dynamicMap, pddContext, true); // forceDollaro a true
		System.out.println("test2{}DaNonRisolvere: "+value+"\n\n");
		expected = expr;
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "{\n \"transaction\": \"id\"\n}";
		DynamicUtils.validate("test3{}DaNonRisolvere", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("test3{}DaNonRisolvere", expr, dynamicMap, pddContext, true); // forceDollaro a true
		System.out.println("test3{}DaNonRisolvere: "+value+"\n\n");
		expected = expr;
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = "{\n \"transaction\": \"id\",\n \"transaction\": \"${transaction:id}\"\n}";
		DynamicUtils.validate("test4{}DaNonRisolvere", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("test4{}DaNonRisolvere", expr, dynamicMap, pddContext, true); // forceDollaro a true
		System.out.println("test4{}DaNonRisolvere: "+value+"\n\n");
		expected = expr.replace("${transaction:id}", idTransazione);
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}

		expr = prefix + "{system:systemP1}";
		DynamicUtils.validate("testSystem", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testSystem", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("testSystem: "+value+"\n\n");
		expected = "systemV1";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		System.setProperty("pCustomJava1", "v1java");
		System.setProperty("pCustomJava2", "v2java");
		expr = prefix + "{java:pCustomJava1}";
		DynamicUtils.validate("testJava", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testJava", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("testJava: "+value+"\n\n");
		expected = "v1java";
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		expr = prefix + "{env:HOSTNAME}";
		DynamicUtils.validate("testEnv", expr, forceDollaro, true);
		value = DynamicUtils.convertDynamicPropertyValue("testEnv", expr, dynamicMap, pddContext, forceDollaro);
		System.out.println("testEnv: "+value+"\n\n");
		expected = System.getenv("HOSTNAME");
		if(!expected.equals(value)) {
			throw new Exception("Expected value '"+expected+"', found '"+value+"'");
		}
		
		}finally {
			if(fTmpConfig!=null) {
				fTmpConfig.delete();
			}
			if(fTmpOp2Properties!=null) {
				fTmpOp2Properties.delete();
			}
		}
	}
	
}

class ClientTestThread implements Runnable{

	private static final String EXPECTED_JSON_TH_NAME = "THREADNAME"; 
	private static final String EXPECTED_JSON = 
			"{\n"+
			"    \"Thread\": \""+EXPECTED_JSON_TH_NAME+"\",\n"+
			"    \"Prova\": \"TEST\"\n"+
			"}";
	
	private Template template;
	private Map<String, Object> dynamicMap;
	private String threadName;
	private boolean freemarker;
	
	ClientTestThread(String threadName, Template template, boolean freemarker){
		this.threadName = threadName;
		this.template = template;
		
		this.dynamicMap = new HashMap<>();
		
		ConnettoreMsg connettoreMsg = new ConnettoreMsg();
		PdDContext pddContext = new PdDContext();
		MapKey<String> THREADNAME = org.openspcoop2.utils.Map.newMapKey("thread-name");
		pddContext.addObject(THREADNAME, this.threadName);
		DynamicInfo dInfo = new DynamicInfo(connettoreMsg, pddContext);
		Logger log = LoggerWrapperFactory.getLogger(Test.class);
		DynamicUtils.fillDynamicMap(log, this.dynamicMap, dInfo);
		this.freemarker = freemarker;
	}
	
	private boolean finished = false;
	private boolean error = false;
	
	private Exception exception = null;
	public Exception getException() {
		return this.exception;
	}

	public boolean isError() {
		return this.error;
	}

	public boolean isFinished() {
		return this.finished;
	}

	@Override
	public void run() {
		
		try{
		
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			if(this.freemarker) {
				DynamicUtils.convertFreeMarkerTemplate(this.template, this.dynamicMap, bout);
			}
			else {
				DynamicUtils.convertVelocityTemplate(this.template, this.dynamicMap, bout);
			}
			bout.flush();
			bout.close();
			String value = bout.toString();
			System.out.println("["+this.threadName+"] Test conversione xml2json via "+(this.freemarker ? "freemarker" : "velocity")+": \n"+value);
			String expected = EXPECTED_JSON.replace(EXPECTED_JSON_TH_NAME, this.threadName);
			if(!expected.equals(value)) {
				throw new Exception("("+this.threadName+") Expected value '"+expected+"', found '"+value+"'");
			}
			
		}catch(Exception e){
			this.error = true;
			this.exception = e;
			// Se si lancia l'eccezione, nell'output viene loggato e si sporcano i test. Comunque lo stato errore viene rilevato.
			//throw new RuntimeException(e.getMessage(),e);
		}
		finally{
			this.finished = true;
		}
	}
	
}
