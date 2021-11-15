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

package org.openspcoop2.pdd.core.trasformazioni;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.AttachmentPart;

import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.PddProperties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.dynamic.ContentExtractor;
import org.openspcoop2.pdd.core.dynamic.Costanti;
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.ErrorHandler;
import org.openspcoop2.pdd.core.dynamic.MessageContent;
import org.openspcoop2.pdd.core.dynamic.PatternExtractor;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.cache.CacheType;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.utils.io.ArchiveType;
import org.openspcoop2.utils.io.CompressorUtilities;
import org.openspcoop2.utils.io.Entry;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Test
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Test {
	
	private static final String DATA = "DATA";
	private static final String DATA_RISPOSTA = "DATA_RISPOSTA";
	private static final String DATA_INCLUDE_1 = "DATA_INCLUDE_1";
	private static final String DATA_INCLUDE_2 = "DATA_INCLUDE_2";
	
	private static final String HEADER1 = "Header1";
	private static final String HEADER1_CASE_INSENTIVE = "header1"; // tomcat
	private static final String HEADER1_VALORE = "ValoreHeader1";
	private static final String HEADER2 = "Header2";
	private static final String HEADER2_VALORE = "ValoreHeader2";
	private static final String HEADER2_VALORE_POSIZIONE_2 = "ValoreHeaderPosizione2";
	
	private static final String HEADER1_RISPOSTA = "ResponseHeader1";
	private static final String HEADER1_RISPOSTA_CASE_INSENTIVE = "responseheader1"; // tomcat
	private static final String HEADER1_VALORE_RISPOSTA = "ResponseValoreHeader1";
	private static final String HEADER2_RISPOSTA = "ResponseHeader2";
	private static final String HEADER2_VALORE_RISPOSTA = "ResponseValoreHeader2";
	private static final String HEADER2_VALORE_RISPOSTA_POSIZIONE_2 = "ResponseValoreHeaderPosizione2";
	
	private static final String QUERY1 = "Query1";
	private static final String QUERY1_VALORE = "QueryValore1";
	private static final String QUERY2 = "Query2";
	private static final String QUERY2_VALORE = "QueryValore2";
	private static final String QUERY2_VALORE_POSIZIONE_2 = "QueryValorePosizione2";
	private static final String QUERY3 = "Query3";
	private static final String QUERY3_VALORE = "QueryValore3";
	private static final String QUERY4 = "Query4";
	private static final String QUERY4_VALORE = "QueryValore4";
	
	private static final String FORM1 = "Form1";
	private static final String FORM1_VALORE = "FormValore1";
	private static final String FORM2 = "Form2";
	private static final String FORM2_VALORE = "FormValore2";
	private static final String FORM2_VALORE_POSIZIONE_2 = "FormValorePosizione2";
	
	private static final String CONFIG1 = "Config1";
	private static final String CONFIG1_VALORE = "ConfigValore1";
	private static final String CONFIG2 = "Config2";
	private static final String CONFIG2_VALORE = "ConfigValore2";
	private static final String CONFIG3 = "Config3";
	private static final String CONFIG3_VALORE = "ConfigValore3";
	private static final String CONFIG4 = "Config4";
	private static final String CONFIG4_VALORE = "ConfigValore4";

	private static final String CONFIG1_SOGGETTO_FRUITORE = "ConfigSogFru1";
	private static final String CONFIG1_SOGGETTO_FRUITORE_VALORE = "ConfigSogFruValore1";
	private static final String CONFIG2_SOGGETTO_FRUITORE = "ConfigSogFru2";
	private static final String CONFIG2_SOGGETTO_FRUITORE_VALORE = "ConfigSogFruValore2";
	private static final String CONFIG3_SOGGETTO_FRUITORE = "ConfigSogFru3";
	private static final String CONFIG3_SOGGETTO_FRUITORE_VALORE = "ConfigSogFruValore3";
	private static final String CONFIG4_SOGGETTO_FRUITORE = "ConfigSogFru4";
	private static final String CONFIG4_SOGGETTO_FRUITORE_VALORE = "ConfigSogFruValore4";
	
	private static final String CONFIG1_SOGGETTO_EROGATORE = "ConfigSogEro1";
	private static final String CONFIG1_SOGGETTO_EROGATORE_VALORE = "ConfigSogEroValore1";
	private static final String CONFIG2_SOGGETTO_EROGATORE = "ConfigSogEro2";
	private static final String CONFIG2_SOGGETTO_EROGATORE_VALORE = "ConfigSogEroValore2";
	private static final String CONFIG3_SOGGETTO_EROGATORE = "ConfigSogEro3";
	private static final String CONFIG3_SOGGETTO_EROGATORE_VALORE = "ConfigSogEroValore3";
	private static final String CONFIG4_SOGGETTO_EROGATORE = "ConfigSogEro4";
	private static final String CONFIG4_SOGGETTO_EROGATORE_VALORE = "ConfigSogEroValore4";
	
	private static final String CONFIG1_APPLICATIVO = "ConfigApp1";
	private static final String CONFIG1_APPLICATIVO_VALORE = "ConfigAppValore1";
	private static final String CONFIG2_APPLICATIVO = "ConfigApp2";
	private static final String CONFIG2_APPLICATIVO_VALORE = "ConfigAppValore2";
	private static final String CONFIG3_APPLICATIVO = "ConfigApp3";
	private static final String CONFIG3_APPLICATIVO_VALORE = "ConfigAppValore3";
	private static final String CONFIG4_APPLICATIVO = "ConfigApp4";
	private static final String CONFIG4_APPLICATIVO_VALORE = "ConfigAppValore4";
	
	private static final String SYSTEM_CONFIG1 = "System1";
	private static final String SYSTEM_CONFIG1_VALORE = "SystemValore1";
	private static final String SYSTEM_CONFIG2 = "System2";
	private static final String SYSTEM_CONFIG2_VALORE = "SystemValore2";
	
	private static final String JAVA_CONFIG1 = "Java1";
	private static final String JAVA_CONFIG1_VALORE = "JavaValore1";
	private static final String JAVA_CONFIG2 = "Java2";
	private static final String JAVA_CONFIG2_VALORE = "JavaValore2";
	
	private static final String ENV_CONFIG1 = "HOSTNAME";
	private static final String ENV_CONFIG1_VALORE = System.getenv(ENV_CONFIG1);
	
	private static final String PDDCONTEXT_1 = "PDDCONTEXT_1";
	private static final String PDDCONTEXT_1_VALORE = "PDDCONTEXT_Valore1";
	private static final String PDDCONTEXT_2 = "PDDCONTEXT_2";
	private static final String PDDCONTEXT_2_VALORE = "PDDCONTEXT_Valore2";
	private static final String PDDCONTEXT_3 = "PDDCONTEXT_3";
	private static final String PDDCONTEXT_3_VALORE = "PDDCONTEXT_Valore3";
	private static final String PDDCONTEXT_4 = "PDDCONTEXT_4";
	private static final String PDDCONTEXT_4_VALORE = "PDDCONTEXT_Valore4";
	
	private static final String PDDCONTEXT_CONFIG_REQ = "PDDCONTEXT_CONFIG_REQ";
	private static final String PDDCONTEXT_CONFIG_RES = "PDDCONTEXT_CONFIG_RES";
	
	private static final String PATH1 = "elementoPath1";
	private static final String PATH1_VALORE = "elementoPathValore1";
	private static final String PATH2 = "elementoPath2";
	private static final String PATH2_VALORE = "elementoPathValore2";
	
	private static final String PATH1_RISPOSTA = "responseElementoPath1";
	private static final String PATH1_VALORE_RISPOSTA = "responseElementoPathValore1";
	private static final String PATH2_RISPOSTA = "responseElementoPath2";
	private static final String PATH2_VALORE_RISPOSTA = "responseElementoPathValore2";
	
	private static final String TRANSACTION_ID = "TRANSACTION_ID";
	
	private static final String BUSTA = "BUSTA";
	private static final String BUSTA_MITTENTE_VALORE = "ComuneMittente";
	
	private static final String BUSTA_PROPERTY = "BustaProperty";
	private static final String BUSTA_PROPERTY_VALORE = "BustaPropertyValore";
	
	private static final String XML_REQUEST_CONTENT = "<"+PATH1+">"+PATH1_VALORE+"</"+PATH1+"><"+PATH2+">"+PATH2_VALORE+"</"+PATH2+">";
	private static final String XML_RESPONSE_CONTENT = "<"+PATH1_RISPOSTA+">"+PATH1_VALORE_RISPOSTA+"</"+PATH1_RISPOSTA+"><"+PATH2_RISPOSTA+">"+PATH2_VALORE_RISPOSTA+"</"+PATH2_RISPOSTA+">";
	private static final String XML_PREFIX = 
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body>";
	private static final String XML_END = 
			"<prova>test</prova><prova2>test2</prova2><list>v1</list><list>v2</list><list>v3</list></soapenv:Body></soapenv:Envelope>";
	private static final String XML_REQUEST = XML_PREFIX + XML_REQUEST_CONTENT + XML_END;
	private static final String XML_RESPONSE = XML_PREFIX + XML_RESPONSE_CONTENT + XML_END;
		
	private static final String JSON_REQUEST_CONTENT = 
	            "\""+PATH1+"\": \""+PATH1_VALORE+"\",\n"+
	            "\""+PATH2+"\": \""+PATH2_VALORE+"\",\n";
	private static final String JSON_RESPONSE_CONTENT = 
            "\""+PATH1_RISPOSTA+"\": \""+PATH1_VALORE_RISPOSTA+"\",\n"+
            "\""+PATH2_RISPOSTA+"\": \""+PATH2_VALORE_RISPOSTA+"\",\n";
	private static final String JSON_PREFIX = 
		"{\n"+	   
            "\"SortAs\": \"SGML\",\n"+
            "\"GlossTerm\": \"Standard Generalized Markup Language\",\n"+
            "\"Acronym\": \"SGML\",\n";
	private static final String JSON_END = 
	            "\"Abbrev\": \"ISO 8879:1986\",\n"+
	            "\"Enabled\": true,\n"+
	            "\"Year\": 2018,\n"+
	            "\"Quote\": 1.45, \n"+
	            "\"ObjectInternal\": {\n"+
	            	"  \"Enabled\": true,\n"+
	            	"  \"TestNumber\": 20,\n"+
	            	"  \"TestString\": \"ISO\"\n"+
	            	"}, \n"+
		        "\"ObjectList\": [\n"+
			        "  {\n"+
	            	"    \"ListEnabled\": true,\n"+
	            	"    \"ListTestNumber\": 20,\n"+
	            	"    \"ListTestString\": \"ISO\"\n"+
	            	"  }, \n"+
			        "  {\n"+
	            	"    \"ListEnabled\": true,\n"+
	            	"    \"ListTestNumber\": 20,\n"+
	            	"    \"ListTestString\": \"ISO\"\n"+
	            	"  } \n"+
	            " ],\n"+
	            "\"List\": [ \"Ford\", \"BMW\", \"Fiat\" ]\n"+
			"}";
	private static final String JSON_REQUEST = JSON_PREFIX + JSON_REQUEST_CONTENT + JSON_END;
	private static final String JSON_RESPONSE = JSON_PREFIX + JSON_RESPONSE_CONTENT + JSON_END;
	
	private static final String HELLO_WORLD_PLAIN = "HELLO WORLD!";
			
	
	
	// **** Template GovWay *****
	
	private static final String JSON_TEMPLATE_BODY = 	   
			"\""+DATA+"\": \"${date:yyyyMMdd_HHmmssSSS}\",\n"+
			"\""+HEADER1+"\": \"${header:"+HEADER1+"}\",\n"+
			"\""+HEADER2+"\": \"${header:"+HEADER2+"}\",\n"+
			"\""+QUERY1+"\": \"${query:"+QUERY1+"}\",\n"+
			"\""+QUERY2+"\": \"${query:"+QUERY2+"}\",\n"+
			"\""+FORM1+"\": \"${form:"+FORM1+"}\",\n"+
			"\""+FORM2+"\": \"${form:"+FORM2+"}\",\n"+
            "\""+PATH1+"\": \"${xpath://"+PATH1+"/text()}\",\n"+
            "\""+PATH2+"\": \"${xPath://"+PATH2+"/text()}\",\n"+
            "\"GlossTerm\": \"Standard Generalized Markup Language\",\n"+
            "\"Acronym\": \"${xPath://{http://schemas.xmlsoap.org/soap/envelope/}:Envelope/{http://schemas.xmlsoap.org/soap/envelope/}:Body/prova2/text()}\",\n"+
            "\""+TRANSACTION_ID+"\": \"${transaction:id}\",\n"+
            "\"Enabled\": true,\n"+
            "\"Year\": 2018,\n"+
            "\""+QUERY3+"\": \"${urlRegExp:.+"+QUERY3+"=([^&]*).*}\" \n"+
            "\""+QUERY4+"\": \"${urlregexp:.+"+QUERY4+"=([^&]*).*}\" \n"+
            "\""+BUSTA+"\": \"${busta:mittente}\",\n"+
            "\""+BUSTA_PROPERTY+"\": \"${property:"+BUSTA_PROPERTY+"}\",\n"+
			"\""+CONFIG1+"\": \"${config:"+CONFIG1+"}\",\n"+
			"\""+CONFIG1_APPLICATIVO+"\": \"${clientApplicationConfig:"+CONFIG1_APPLICATIVO+"}\",\n"+
			"\""+CONFIG1_SOGGETTO_FRUITORE+"\": \"${clientOrganizationConfig:"+CONFIG1_SOGGETTO_FRUITORE+"}\",\n"+
			"\""+CONFIG1_SOGGETTO_EROGATORE+"\": \"${providerOrganizationConfig:"+CONFIG1_SOGGETTO_EROGATORE+"}\",\n"+
			"\""+SYSTEM_CONFIG1+"\": \"${system:"+SYSTEM_CONFIG1+"}\",\n"+
			"\""+ENV_CONFIG1+"\": \"${env:"+ENV_CONFIG1+"}\",\n"+
			"\""+JAVA_CONFIG1+"\": \"${java:"+JAVA_CONFIG1+"}\",\n";
	private static final String JSON_TEMPLATE_BODY_RESPONSE = 	   
			"\""+DATA_RISPOSTA+"\": \"${dateResponse:yyyyMMdd_HHmmssSSS}\",\n"+
			"\""+HEADER1_RISPOSTA+"\": \"${headerResponse:"+HEADER1_RISPOSTA+"}\",\n"+
			"\""+HEADER2_RISPOSTA+"\": \"${headerResponse:"+HEADER2_RISPOSTA+"}\",\n"+
            "\""+PATH1_RISPOSTA+"\": \"${xpathResponse://"+PATH1_RISPOSTA+"/text()}\",\n"+
            "\""+PATH2_RISPOSTA+"\": \"${xPathResponse://"+PATH2_RISPOSTA+"/text()}\"";
	private static final String JSON_TEMPLATE_REQUEST= 
			"{\n"+	   
					JSON_TEMPLATE_BODY+
			"\n}";
	private static final String JSON_TEMPLATE_RESPONSE= 
			"{\n"+	   
					JSON_TEMPLATE_BODY+
					JSON_TEMPLATE_BODY_RESPONSE+
			"\n}";
	
	private static final String XML_TEMPLATE_BODY =
			"<"+DATA+">"+"${date:yyyyMMdd_HHmmssSSS}"+"</"+DATA+">\n"+
			"<"+HEADER1+">"+"${header:"+HEADER1+"}"+"</"+HEADER1+">\n"+
			"<"+HEADER2+">"+"${header:"+HEADER2+"}"+"</"+HEADER2+">\n"+
			"<"+QUERY1+">"+"${query:"+QUERY1+"}"+"</"+QUERY1+">\n"+
			"<"+QUERY2+">"+"${query:"+QUERY2+"}"+"</"+QUERY2+">\n"+
			"<"+FORM1+">"+"${form:"+FORM1+"}"+"</"+FORM1+">\n"+
			"<"+FORM2+">"+"${form:"+FORM2+"}"+"</"+FORM2+">\n"+
			"<"+PATH1+">"+"${jsonpath:$."+PATH1+"}"+"</"+PATH1+">\n"+
			"<"+PATH2+">"+"${jsonPath:$."+PATH2+"}"+"</"+PATH2+">\n"+
			"<"+TRANSACTION_ID+">"+"${transaction:id}"+"</"+TRANSACTION_ID+">\n"+
			"<"+QUERY3+">"+"${urlRegExp:.+"+QUERY3+"=([^&]*).*}"+"</"+QUERY3+">\n"+
			"<"+QUERY4+">"+"${urlregexp:.+"+QUERY4+"=([^&]*).*}"+"</"+QUERY4+">\n"+
			"<"+BUSTA+">"+"${busta:mittente}"+"</"+BUSTA+">\n"+
			"<"+BUSTA_PROPERTY+">"+"${property:"+BUSTA_PROPERTY+"}"+"</"+BUSTA_PROPERTY+">\n"+
			"<"+CONFIG1+">"+"${config:"+CONFIG1+"}"+"</"+CONFIG1+">\n"+
			"<"+CONFIG1_APPLICATIVO+">"+"${clientapplicationconfig:"+CONFIG1_APPLICATIVO+"}"+"</"+CONFIG1_APPLICATIVO+">\n"+
			"<"+CONFIG1_SOGGETTO_FRUITORE+">"+"${clientorganizationconfig:"+CONFIG1_SOGGETTO_FRUITORE+"}"+"</"+CONFIG1_SOGGETTO_FRUITORE+">\n"+
			"<"+CONFIG1_SOGGETTO_EROGATORE+">"+"${providerorganizationconfig:"+CONFIG1_SOGGETTO_EROGATORE+"}"+"</"+CONFIG1_SOGGETTO_EROGATORE+">\n"+
			"<"+SYSTEM_CONFIG1+">"+"${system:"+SYSTEM_CONFIG1+"}"+"</"+SYSTEM_CONFIG1+">\n"+
			"<"+ENV_CONFIG1+">"+"${env:"+ENV_CONFIG1+"}"+"</"+ENV_CONFIG1+">\n"+
			"<"+JAVA_CONFIG1+">"+"${java:"+JAVA_CONFIG1+"}"+"</"+JAVA_CONFIG1+">\n";
	private static final String XML_TEMPLATE_BODY_RESPONSE =
			"<"+DATA_RISPOSTA+">"+"${dateResponse:yyyyMMdd_HHmmssSSS}"+"</"+DATA_RISPOSTA+">\n"+
			"<"+HEADER1_RISPOSTA+">"+"${headerResponse:"+HEADER1_RISPOSTA+"}"+"</"+HEADER1_RISPOSTA+">\n"+
			"<"+HEADER2_RISPOSTA+">"+"${headerResponse:"+HEADER2_RISPOSTA+"}"+"</"+HEADER2_RISPOSTA+">\n"+
			"<"+PATH1_RISPOSTA+">"+"${jsonpathResponse:$."+PATH1_RISPOSTA+"}"+"</"+PATH1_RISPOSTA+">\n"+
			"<"+PATH2_RISPOSTA+">"+"${jsonPathResponse:$."+PATH2_RISPOSTA+"}"+"</"+PATH2_RISPOSTA+">\n";
	private static final String XML_TEMPLATE_REQUEST = 
			XML_PREFIX + 
			XML_TEMPLATE_BODY + 
			XML_END;
	private static final String XML_TEMPLATE_RESPONSE = 
			XML_PREFIX + 
			XML_TEMPLATE_BODY + 
			XML_TEMPLATE_BODY_RESPONSE + 
			XML_END;
	
	
	
	
	
	// **** Template FreeMarker *****
	
	private static final String JSON_TEMPLATE_FREEMARKER_BODY = 	   
			"\""+DATA+"\": \"${date?string('dd.MM.yyyy HH:mm:ss')}\",\n"+
			"\""+HEADER1+"\": \"<#if header[\""+HEADER1+"\"]??>${header[\""+HEADER1+"\"]}<#else>${header[\""+HEADER1+"\"?lower_case]}</#if>\",\n"+ 
			"\""+HEADER2+"\": \"${headerValues[\""+HEADER2+"\"][0]}\",\n"+
			"\""+HEADER2+"\": \"${headerValues[\""+HEADER2+"\"][1]}\",\n"+
			"\""+QUERY1+"\": \"${query[\""+QUERY1+"\"]}\",\n"+
			"\""+QUERY2+"\": \"${queryValues[\""+QUERY2+"\"][0]}\",\n"+
			"\""+QUERY2+"\": \"${queryValues[\""+QUERY2+"\"][1]}\",\n"+
			"\""+FORM1+"\": \"${form[\""+FORM1+"\"]}\",\n"+
			"\""+FORM2+"\": \"${formValues[\""+FORM2+"\"][0]}\",\n"+
			"\""+FORM2+"\": \"${formValues[\""+FORM2+"\"][1]}\",\n"+
            "\""+PATH1+"\": \"${xpath.read(\"//"+PATH1+"/text()\")}\",\n"+
            "\""+PATH2+"\": \"${xPath.read(\"//"+PATH2+"/text()\")}\",\n"+
            "\""+PATH1+"boolean\": \"${xpath.match(\"//"+PATH1+"/text()\")?string('yes', 'no')}\",\n"+
            "\""+PATH2+"boolean\": \"${xPath.match(\"//"+PATH2+"/text()\")?string('yes', 'no')}\",\n"+
            "<#list xpath.readList(\"//"+PATH1+"/text()\") as item>${item}</#list>\n"+
            "\"GlossTerm\": \"Standard Generalized Markup Language\",\n"+
            "\"Acronym\": \"${xPath.read(\"//{http://schemas.xmlsoap.org/soap/envelope/}:Envelope/{http://schemas.xmlsoap.org/soap/envelope/}:Body/prova2/text()\")}\",\n"+
            "\""+TRANSACTION_ID+"\": \"${transactionId}\",\n"+
            "\"Enabled\": true,\n"+
            "\"Year\": 2018,\n"+
            "\""+QUERY3+"\": \"${urlRegExp.read(\".+"+QUERY3+"=([^&]*).*\")}\" \n"+
            "\""+QUERY4+"\": \"${urlregexp.read(\".+"+QUERY4+"=([^&]*).*\")}\" \n"+
            "\""+BUSTA+"\": \"${busta.getMittente()}\",\n"+
            "\""+BUSTA_PROPERTY+"\": \"${property[\""+BUSTA_PROPERTY+"\"]}\",\n"+
            "\""+CONFIG1+"\": \"${config[\""+CONFIG1+"\"]}\",\n"+
            "\""+CONFIG1_APPLICATIVO+"\": \"${clientApplicationConfig[\""+CONFIG1_APPLICATIVO+"\"]}\",\n"+
            "\""+CONFIG1_SOGGETTO_FRUITORE+"\": \"${clientOrganizationConfig[\""+CONFIG1_SOGGETTO_FRUITORE+"\"]}\",\n"+
            "\""+CONFIG1_SOGGETTO_EROGATORE+"\": \"${providerOrganizationConfig[\""+CONFIG1_SOGGETTO_EROGATORE+"\"]}\",\n"+
            "\""+SYSTEM_CONFIG1+"\": \"${system.read(\""+SYSTEM_CONFIG1+"\")}\",\n"+
            "\""+ENV_CONFIG1+"\": \"${env.read(\""+ENV_CONFIG1+"\")}\",\n"+
            "\""+JAVA_CONFIG1+"\": \"${java.read(\""+JAVA_CONFIG1+"\")}\",\n";
	private static final String JSON_TEMPLATE_FREEMARKER_BODY_RESPONSE = 	   
			"\""+DATA_RISPOSTA+"\": \"${dateResponse?string('dd.MM.yyyy HH:mm:ss')}\",\n"+
			"\""+HEADER1_RISPOSTA+"\": \"<#if headerResponse[\""+HEADER1_RISPOSTA+"\"]??>${headerResponse[\""+HEADER1_RISPOSTA+"\"]}<#else>${headerResponse[\""+HEADER1_RISPOSTA+"\"?lower_case]}</#if>\",\n"+
			"\""+HEADER2_RISPOSTA+"\": \"${headerResponseValues[\""+HEADER2_RISPOSTA+"\"][0]}\",\n"+
			"\""+HEADER2_RISPOSTA+"\": \"${headerResponseValues[\""+HEADER2_RISPOSTA+"\"][1]}\",\n"+
            "\""+PATH1_RISPOSTA+"\": \"${xpathResponse.read(\"//"+PATH1_RISPOSTA+"/text()\")}\",\n"+
            "\""+PATH2_RISPOSTA+"\": \"${xPathResponse.read(\"//"+PATH2_RISPOSTA+"/text()\")}\",\n"+
            "\""+PATH1_RISPOSTA+"boolean\": \"${xpathResponse.match(\"//"+PATH1_RISPOSTA+"/text()\")?string('yes', 'no')}\",\n"+
            "\""+PATH2_RISPOSTA+"boolean\": \"${xPathResponse.match(\"//"+PATH2_RISPOSTA+"/text()\")?string('yes', 'no')}\"\n"+
            "<#list xpathResponse.readList(\"//"+PATH1_RISPOSTA+"/text()\") as item>${item}</#list>";
	private static final String JSON_TEMPLATE_FREEMARKER_REQUEST= 
			"{\n"+	   
					JSON_TEMPLATE_FREEMARKER_BODY+
			"\n}";
	private static final String JSON_TEMPLATE_FREEMARKER_RESPONSE= 
			"{\n"+	   
					JSON_TEMPLATE_FREEMARKER_BODY+
					JSON_TEMPLATE_FREEMARKER_BODY_RESPONSE+
			"\n}";
	private static final String JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_1_PATH = "lib/json/TestInclude_1.ftl"; 
	private static final String JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_2_PATH = "lib/json/TestInclude_2.ftl";
	private static final String JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_1 = "<#include \""+JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_1_PATH+"\">\n"; 
	private static final String JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_2 = "<#include \""+JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_2_PATH+"\">\n";
	private static final String JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_1 = 	   
			"\""+DATA_INCLUDE_1+"\": \"${date?string('dd.MM.yyyy HH:mm:ss')}\"\n";
	private static final String JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_2 = 	   
			"\""+DATA_INCLUDE_2+"\": \"${date?string('dd.MM.yyyy HH:mm:ss')}\"\n";
	private static final String JSON_TEMPLATE_FREEMARKER_REQUEST_INCLUDE= 
			"{\n"+	   
					JSON_TEMPLATE_FREEMARKER_BODY+
					JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_1+
					JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_2+
			"\n}";
	private static final String JSON_TEMPLATE_FREEMARKER_RESPONSE_INCLUDE= 
			"{\n"+	   
					JSON_TEMPLATE_FREEMARKER_BODY+
					JSON_TEMPLATE_FREEMARKER_BODY_RESPONSE+
					JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_1+
					JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_2+
			"\n}";
	
	
	private static final String XML_TEMPLATE_FREEMARKER_BODY =
			"<"+DATA+">"+"${date?string('dd.MM.yyyy HH:mm:ss')}"+"</"+DATA+">\n"+
			"<"+HEADER1+">"+"<#if header[\""+HEADER1+"\"]??>${header[\""+HEADER1+"\"]}<#else>${header[\""+HEADER1+"\"?lower_case]}</#if>"+"</"+HEADER1+">\n"+
			"<"+HEADER2+">"+"${headerValues[\""+HEADER2+"\"][0]}"+"</"+HEADER2+">\n"+
			"<"+HEADER2+">"+"${headerValues[\""+HEADER2+"\"][1]}"+"</"+HEADER2+">\n"+
			"<"+QUERY1+">"+"${query[\""+QUERY1+"\"]}"+"</"+QUERY1+">\n"+
			"<"+QUERY2+">"+"${queryValues[\""+QUERY2+"\"][0]}"+"</"+QUERY2+">\n"+
			"<"+QUERY2+">"+"${queryValues[\""+QUERY2+"\"][1]}"+"</"+QUERY2+">\n"+
			"<"+FORM1+">"+"${form[\""+FORM1+"\"]}"+"</"+FORM1+">\n"+
			"<"+FORM2+">"+"${formValues[\""+FORM2+"\"][0]}"+"</"+FORM2+">\n"+
			"<"+FORM2+">"+"${formValues[\""+FORM2+"\"][1]}"+"</"+FORM2+">\n"+
			"<"+PATH1+">"+"${jsonpath.read(\"$."+PATH1+"\")}"+"</"+PATH1+">\n"+
			"<"+PATH2+">"+"${jsonPath.read(\"$."+PATH2+"\")}"+"</"+PATH2+">\n"+
			"<"+PATH1+"boolean>"+"${jsonpath.match(\"$."+PATH1+"\")?string('yes', 'no')}"+"</"+PATH1+"boolean>\n"+
			"<"+PATH2+"boolean>"+"${jsonPath.match(\"$."+PATH2+"\")?string('yes', 'no')}"+"</"+PATH2+"boolean>\n"+
			"<#list jsonPath.readList(\"$."+PATH1+"\") as item>${item}</#list>\n"+
			"<"+TRANSACTION_ID+">"+"${transactionId}"+"</"+TRANSACTION_ID+">\n"+
			"<"+QUERY3+">"+"${urlRegExp.read(\".+"+QUERY3+"=([^&]*).*\")}"+"</"+QUERY3+">\n"+
			"<"+QUERY4+">"+"${urlregexp.read(\".+"+QUERY4+"=([^&]*).*\")}"+"</"+QUERY4+">\n"+
			"<"+BUSTA+">"+"${busta.getMittente()}"+"</"+BUSTA+">\n"+
			"<"+BUSTA_PROPERTY+">"+"${property[\""+BUSTA_PROPERTY+"\"]}"+"</"+BUSTA_PROPERTY+">\n"+
			"<"+CONFIG1+">"+"${config[\""+CONFIG1+"\"]}"+"</"+CONFIG1+">\n"+
			"<"+CONFIG1_APPLICATIVO+">"+"${clientapplicationconfig[\""+CONFIG1_APPLICATIVO+"\"]}"+"</"+CONFIG1_APPLICATIVO+">\n"+
			"<"+CONFIG1_SOGGETTO_FRUITORE+">"+"${clientorganizationconfig[\""+CONFIG1_SOGGETTO_FRUITORE+"\"]}"+"</"+CONFIG1_SOGGETTO_FRUITORE+">\n"+
			"<"+CONFIG1_SOGGETTO_EROGATORE+">"+"${providerorganizationconfig[\""+CONFIG1_SOGGETTO_EROGATORE+"\"]}"+"</"+CONFIG1_SOGGETTO_EROGATORE+">\n"+
			"<"+SYSTEM_CONFIG1+">"+"${system.read(\""+SYSTEM_CONFIG1+"\")}"+"</"+SYSTEM_CONFIG1+">\n"+
			"<"+ENV_CONFIG1+">"+"${env.read(\""+ENV_CONFIG1+"\")}"+"</"+ENV_CONFIG1+">\n"+
			"<"+JAVA_CONFIG1+">"+"${java.read(\""+JAVA_CONFIG1+"\")}"+"</"+JAVA_CONFIG1+">\n";	
	private static final String XML_TEMPLATE_FREEMARKER_BODY_RESPONSE =
			"<"+DATA_RISPOSTA+">"+"${dateResponse?string('dd.MM.yyyy HH:mm:ss')}"+"</"+DATA_RISPOSTA+">\n"+
			"<"+HEADER1_RISPOSTA+">"+"<#if headerResponse[\""+HEADER1_RISPOSTA+"\"]??>${headerResponse[\""+HEADER1_RISPOSTA+"\"]}<#else>${headerResponse[\""+HEADER1_RISPOSTA+"\"?lower_case]}</#if>"+"</"+HEADER1_RISPOSTA+">\n"+
			"<"+HEADER2_RISPOSTA+">"+"${headerResponseValues[\""+HEADER2_RISPOSTA+"\"][0]}"+"</"+HEADER2_RISPOSTA+">\n"+
			"<"+HEADER2_RISPOSTA+">"+"${headerResponseValues[\""+HEADER2_RISPOSTA+"\"][1]}"+"</"+HEADER2_RISPOSTA+">\n"+
			"<"+PATH1_RISPOSTA+">"+"${jsonpathResponse.read(\"$."+PATH1_RISPOSTA+"\")}"+"</"+PATH1_RISPOSTA+">\n"+
			"<"+PATH2_RISPOSTA+">"+"${jsonPathResponse.read(\"$."+PATH2_RISPOSTA+"\")}"+"</"+PATH2_RISPOSTA+">\n"+
			"<"+PATH1_RISPOSTA+"boolean>"+"${jsonpathResponse.match(\"$."+PATH1_RISPOSTA+"\")?string('yes', 'no')}"+"</"+PATH1_RISPOSTA+"boolean>\n"+
			"<"+PATH2_RISPOSTA+"boolean>"+"${jsonPathResponse.match(\"$."+PATH2_RISPOSTA+"\")?string('yes', 'no')}"+"</"+PATH2_RISPOSTA+"boolean>\n"+
			"<#list jsonPathResponse.readList(\"$."+PATH1_RISPOSTA+"\") as item>${item}</#list>\n";
	private static final String XML_TEMPLATE_FREEMARKER_REQUEST = 
			XML_PREFIX + 
			XML_TEMPLATE_FREEMARKER_BODY + 
			XML_END;
	private static final String XML_TEMPLATE_FREEMARKER_RESPONSE = 
			XML_PREFIX + 
			XML_TEMPLATE_FREEMARKER_BODY + 
			XML_TEMPLATE_FREEMARKER_BODY_RESPONSE + 
			XML_END;
	private static final String XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_1_PATH = "lib/xml/TestInclude_1.ftl"; 
	private static final String XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_2_PATH = "lib/xml/TestInclude_2.ftl";
	private static final String XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_1 = "<#include \""+XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_1_PATH+"\">\n"; 
	private static final String XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_2 = "<#include \""+XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_2_PATH+"\">\n";
	private static final String XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_1 = 	   
			"<"+DATA_INCLUDE_1+">"+"${date?string('dd.MM.yyyy HH:mm:ss')}"+"</"+DATA+">\n";
	private static final String XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_2 = 	   
			"<"+DATA_INCLUDE_2+">"+"${date?string('dd.MM.yyyy HH:mm:ss')}"+"</"+DATA+">\n";
	private static final String XML_TEMPLATE_FREEMARKER_REQUEST_INCLUDE= 
			"{\n"+	   
					XML_TEMPLATE_FREEMARKER_BODY+
					XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_1+
					XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_2+
			"\n}";
	private static final String XML_TEMPLATE_FREEMARKER_RESPONSE_INCLUDE= 
			"{\n"+	   
					XML_TEMPLATE_FREEMARKER_BODY+
					XML_TEMPLATE_FREEMARKER_BODY_RESPONSE+
					XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_1+
					XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_2+
			"\n}";
		
	private static final String XML_TEMPLATE_FREEMARKER_REQUEST_CONTEXT = 
			"<#assign tmp=context?api.put(\""+PDDCONTEXT_1+"\", \""+PDDCONTEXT_1_VALORE+"\")!/>\n"+
			"<#assign cfg1 = config[\""+CONFIG1+"\"]/>\n" + 
			"<#assign tmp=context?api.put(\""+PDDCONTEXT_CONFIG_REQ+"\", cfg1)!/>";
	private static final String XML_TEMPLATE_FREEMARKER_RESPONSE_CONTEXT = 
			"<#assign tmp=context?api.put(\""+PDDCONTEXT_3+"\", \""+PDDCONTEXT_3_VALORE+"\")!/>\n"+
			"<#assign cfg3 = config[\""+CONFIG3+"\"]/>\n" + 
			"<#assign tmp=context?api.put(\""+PDDCONTEXT_CONFIG_RES+"\", cfg3)!/>";
	
	
	
	// **** Template Velocity *****
	
	private static final String JSON_TEMPLATE_VELOCITY_BODY = 	   
			"\""+DATA+"\": \"$date\",\n"+
			"\""+HEADER1+"\": \"#if ($header[\""+HEADER1+"\"])${header[\""+HEADER1+"\"]}#else#set($tmp = \""+HEADER1+"\")${header[$tmp.toLowerCase()]}#end\",\n"+
			"\""+HEADER2+"\": \"${headerValues[\""+HEADER2+"\"][0]}\",\n"+
			"\""+HEADER2+"\": \"${headerValues[\""+HEADER2+"\"][1]}\",\n"+
			"\""+QUERY1+"\": \"${query[\""+QUERY1+"\"]}\",\n"+
			"\""+QUERY2+"\": \"${queryValues[\""+QUERY2+"\"][0]}\",\n"+
			"\""+QUERY2+"\": \"${queryValues[\""+QUERY2+"\"][1]}\",\n"+
			"\""+FORM1+"\": \"${form[\""+FORM1+"\"]}\",\n"+
			"\""+FORM2+"\": \"${formValues[\""+FORM2+"\"][0]}\",\n"+
			"\""+FORM2+"\": \"${formValues[\""+FORM2+"\"][1]}\",\n"+
            "\""+PATH1+"\": \"${xpath.read(\"//"+PATH1+"/text()\")}\",\n"+
            "\""+PATH2+"\": \"${xPath.read(\"//"+PATH2+"/text()\")}\",\n"+
            "\""+PATH1+"boolean\": \"${xpath.match(\"//"+PATH1+"/text()\")}\",\n"+
            "\""+PATH2+"boolean\": \"${xPath.match(\"//"+PATH2+"/text()\")}\",\n"+
            "<#list xpath.readList(\"//"+PATH1+"/text()\") as item>${item}</#list>\n"+
            "\"GlossTerm\": \"Standard Generalized Markup Language\",\n"+
            "\"Acronym\": \"${xPath.read(\"//{http://schemas.xmlsoap.org/soap/envelope/}:Envelope/{http://schemas.xmlsoap.org/soap/envelope/}:Body/prova2/text()\")}\",\n"+
            "\""+TRANSACTION_ID+"\": \"${transactionId}\",\n"+
            "\"Enabled\": true,\n"+
            "\"Year\": 2018,\n"+
            "\""+QUERY3+"\": \"${urlRegExp.read(\".+"+QUERY3+"=([^&]*).*\")}\" \n"+
            "\""+QUERY4+"\": \"${urlregexp.read(\".+"+QUERY4+"=([^&]*).*\")}\" \n"+
            "\""+BUSTA+"\": \"${busta.getMittente()}\",\n"+
            "\""+BUSTA_PROPERTY+"\": \"${property[\""+BUSTA_PROPERTY+"\"]}\",\n"+
            "\""+CONFIG1+"\": \"${config[\""+CONFIG1+"\"]}\",\n"+
            "\""+CONFIG1_APPLICATIVO+"\": \"${clientApplicationConfig[\""+CONFIG1_APPLICATIVO+"\"]}\",\n"+
            "\""+CONFIG1_SOGGETTO_FRUITORE+"\": \"${clientOrganizationConfig[\""+CONFIG1_SOGGETTO_FRUITORE+"\"]}\",\n"+
            "\""+CONFIG1_SOGGETTO_EROGATORE+"\": \"${providerOrganizationConfig[\""+CONFIG1_SOGGETTO_EROGATORE+"\"]}\",\n"+
            "\""+SYSTEM_CONFIG1+"\": \"${system.read(\""+SYSTEM_CONFIG1+"\")}\",\n"+
			"\""+ENV_CONFIG1+"\": \"${env.read(\""+ENV_CONFIG1+"\")}\",\n"+
			"\""+JAVA_CONFIG1+"\": \"${java.read(\""+JAVA_CONFIG1+"\")}\",\n";
	private static final String JSON_TEMPLATE_VELOCITY_BODY_RESPONSE = 	   
			"\""+DATA_RISPOSTA+"\": \"${dateResponse}\",\n"+
			"\""+HEADER1_RISPOSTA+"\": \"#if ($headerResponse[\""+HEADER1_RISPOSTA+"\"])${headerResponse[\""+HEADER1_RISPOSTA+"\"]}#else#set($tmp = \""+HEADER1_RISPOSTA_CASE_INSENTIVE+"\")${headerResponse[$tmp.toLowerCase()]}#end\",\n"+
			"\""+HEADER2_RISPOSTA+"\": \"${headerResponseValues[\""+HEADER2_RISPOSTA+"\"][0]}\",\n"+
			"\""+HEADER2_RISPOSTA+"\": \"${headerResponseValues[\""+HEADER2_RISPOSTA+"\"][1]}\",\n"+
            "\""+PATH1_RISPOSTA+"\": \"${xpathResponse.read(\"//"+PATH1_RISPOSTA+"/text()\")}\",\n"+
            "\""+PATH2_RISPOSTA+"\": \"${xPathResponse.read(\"//"+PATH2_RISPOSTA+"/text()\")}\",\n"+
            "\""+PATH1_RISPOSTA+"boolean\": \"${xpathResponse.match(\"//"+PATH1_RISPOSTA+"/text()\")}\",\n"+
            "\""+PATH2_RISPOSTA+"boolean\": \"${xPathResponse.match(\"//"+PATH2_RISPOSTA+"/text()\")}\"\n"+
            "<#list xpathResponse.readList(\"//"+PATH1_RISPOSTA+"/text()\") as item>${item}</#list>";
	private static final String JSON_TEMPLATE_VELOCITY_REQUEST= 
			"{\n"+	   
					JSON_TEMPLATE_VELOCITY_BODY+
			"\n}";
	private static final String JSON_TEMPLATE_VELOCITY_RESPONSE= 
			"{\n"+	   
					JSON_TEMPLATE_VELOCITY_BODY+
					JSON_TEMPLATE_VELOCITY_BODY_RESPONSE+
			"\n}";
	private static final String JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_1_PATH = "lib/json/TestInclude_1.vm"; 
	private static final String JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_2_PATH = "lib/json/TestInclude_2.vm";
	private static final String JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_1 = "<#include \""+JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_1_PATH+"\">\n"; 
	private static final String JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_2 = "<#include \""+JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_2_PATH+"\">\n";
	private static final String JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_1 = 	   
			"\""+DATA_INCLUDE_1+"\": \"$date\"\n";
	private static final String JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_2 = 	   
			"\""+DATA_INCLUDE_2+"\": \"$date\"\n";
	private static final String JSON_TEMPLATE_VELOCITY_REQUEST_INCLUDE= 
			"{\n"+	   
					JSON_TEMPLATE_VELOCITY_BODY+
					JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_1+
					JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_2+
			"\n}";
	private static final String JSON_TEMPLATE_VELOCITY_RESPONSE_INCLUDE= 
			"{\n"+	   
					JSON_TEMPLATE_VELOCITY_BODY+
					JSON_TEMPLATE_VELOCITY_BODY_RESPONSE+
					JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_1+
					JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_2+
			"\n}";
	
	private static final String XML_TEMPLATE_VELOCITY_BODY =
			"<"+DATA+">"+"${date}"+"</"+DATA+">\n"+
			"<"+HEADER1+">"+"#if ($header[\""+HEADER1+"\"])${header[\""+HEADER1+"\"]}#else#set($tmp = \""+HEADER1+"\")${header[$tmp.toLowerCase()]}#end"+"</"+HEADER1+">\n"+
			"<"+HEADER2+">"+"${headerValues[\""+HEADER2+"\"][0]}"+"</"+HEADER2+">\n"+
			"<"+HEADER2+">"+"${headerValues[\""+HEADER2+"\"][1]}"+"</"+HEADER2+">\n"+
			"<"+QUERY1+">"+"${query[\""+QUERY1+"\"]}"+"</"+QUERY1+">\n"+
			"<"+QUERY2+">"+"${queryValues[\""+QUERY2+"\"][0]}"+"</"+QUERY2+">\n"+
			"<"+QUERY2+">"+"${queryValues[\""+QUERY2+"\"][1]}"+"</"+QUERY2+">\n"+
			"<"+FORM1+">"+"${form[\""+FORM1+"\"]}"+"</"+FORM1+">\n"+
			"<"+FORM2+">"+"${formValues[\""+FORM2+"\"][0]}"+"</"+FORM2+">\n"+
			"<"+FORM2+">"+"${formValues[\""+FORM2+"\"][1]}"+"</"+FORM2+">\n"+
			"<"+PATH1+">"+"${jsonpath.read(\"$."+PATH1+"\")}"+"</"+PATH1+">\n"+
			"<"+PATH2+">"+"${jsonPath.read(\"$."+PATH2+"\")}"+"</"+PATH2+">\n"+
			"<"+PATH1+"boolean>"+"${jsonpath.match(\"$."+PATH1+"\")}"+"</"+PATH1+"boolean>\n"+
			"<"+PATH2+"boolean>"+"${jsonPath.match(\"$."+PATH2+"\")}"+"</"+PATH2+"boolean>\n"+
			"<#list jsonPath.readList(\"$."+PATH1+"\") as item>${item}</#list>\n"+
			"<"+TRANSACTION_ID+">"+"${transactionId}"+"</"+TRANSACTION_ID+">\n"+
			"<"+QUERY3+">"+"${urlRegExp.read(\".+"+QUERY3+"=([^&]*).*\")}"+"</"+QUERY3+">\n"+
			"<"+QUERY4+">"+"${urlregexp.read(\".+"+QUERY4+"=([^&]*).*\")}"+"</"+QUERY4+">\n"+
			"<"+BUSTA+">"+"${busta.getMittente()}"+"</"+BUSTA+">\n"+
			"<"+BUSTA_PROPERTY+">"+"${property[\""+BUSTA_PROPERTY+"\"]}"+"</"+BUSTA_PROPERTY+">\n"+
			"<"+CONFIG1+">"+"${config[\""+CONFIG1+"\"]}"+"</"+CONFIG1+">\n"+
			"<"+CONFIG1_APPLICATIVO+">"+"${clientapplicationconfig[\""+CONFIG1_APPLICATIVO+"\"]}"+"</"+CONFIG1_APPLICATIVO+">\n"+
			"<"+CONFIG1_SOGGETTO_FRUITORE+">"+"${clientorganizationconfig[\""+CONFIG1_SOGGETTO_FRUITORE+"\"]}"+"</"+CONFIG1_SOGGETTO_FRUITORE+">\n"+
			"<"+CONFIG1_SOGGETTO_EROGATORE+">"+"${providerorganizationconfig[\""+CONFIG1_SOGGETTO_EROGATORE+"\"]}"+"</"+CONFIG1_SOGGETTO_EROGATORE+">\n"+
			"<"+SYSTEM_CONFIG1+">"+"${system.read(\""+SYSTEM_CONFIG1+"\")}"+"</"+SYSTEM_CONFIG1+">\n"+
			"<"+ENV_CONFIG1+">"+"${env.read(\""+ENV_CONFIG1+"\")}"+"</"+ENV_CONFIG1+">\n"+
			"<"+JAVA_CONFIG1+">"+"${java.read(\""+JAVA_CONFIG1+"\")}"+"</"+JAVA_CONFIG1+">\n";
	private static final String XML_TEMPLATE_VELOCITY_BODY_RESPONSE =
			"<"+DATA_RISPOSTA+">"+"${dateResponse}"+"</"+DATA_RISPOSTA+">\n"+
			"<"+HEADER1_RISPOSTA+">"+"#if ($headerResponse[\""+HEADER1_RISPOSTA+"\"])${headerResponse[\""+HEADER1_RISPOSTA+"\"]}#else#set($tmp = \""+HEADER1_RISPOSTA_CASE_INSENTIVE+"\")${headerResponse[$tmp.toLowerCase()]}#end"+"</"+HEADER1_RISPOSTA+">\n"+
			"<"+HEADER2_RISPOSTA+">"+"${headerResponseValues[\""+HEADER2_RISPOSTA+"\"][0]}"+"</"+HEADER2_RISPOSTA+">\n"+
			"<"+HEADER2_RISPOSTA+">"+"${headerResponseValues[\""+HEADER2_RISPOSTA+"\"][1]}"+"</"+HEADER2_RISPOSTA+">\n"+
			"<"+PATH1_RISPOSTA+">"+"${jsonpathResponse.read(\"$."+PATH1_RISPOSTA+"\")}"+"</"+PATH1_RISPOSTA+">\n"+
			"<"+PATH2_RISPOSTA+">"+"${jsonPathResponse.read(\"$."+PATH2_RISPOSTA+"\")}"+"</"+PATH2_RISPOSTA+">\n"+
			"<"+PATH1_RISPOSTA+"boolean>"+"${jsonpathResponse.match(\"$."+PATH1_RISPOSTA+"\")}"+"</"+PATH1_RISPOSTA+"boolean>\n"+
			"<"+PATH2_RISPOSTA+"boolean>"+"${jsonPathResponse.match(\"$."+PATH2_RISPOSTA+"\")}"+"</"+PATH2_RISPOSTA+"boolean>\n"+
			"<#list jsonPathResponse.readList(\"$."+PATH1_RISPOSTA+"\") as item>${item}</#list>\n";
	private static final String XML_TEMPLATE_VELOCITY_REQUEST = 
			XML_PREFIX + 
			XML_TEMPLATE_VELOCITY_BODY + 
			XML_END;
	private static final String XML_TEMPLATE_VELOCITY_RESPONSE = 
			XML_PREFIX + 
			XML_TEMPLATE_VELOCITY_BODY + 
			XML_TEMPLATE_VELOCITY_BODY_RESPONSE + 
			XML_END;
	private static final String XML_TEMPLATE_VELOCITY_BODY_INCLUDE_1_PATH = "lib/xml/TestInclude_1.vm"; 
	private static final String XML_TEMPLATE_VELOCITY_BODY_INCLUDE_2_PATH = "lib/xml/TestInclude_2.vm";
	private static final String XML_TEMPLATE_VELOCITY_BODY_INCLUDE_1 = "<#include \""+XML_TEMPLATE_VELOCITY_BODY_INCLUDE_1_PATH+"\">\n"; 
	private static final String XML_TEMPLATE_VELOCITY_BODY_INCLUDE_2 = "<#include \""+XML_TEMPLATE_VELOCITY_BODY_INCLUDE_2_PATH+"\">\n";
	private static final String XML_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_1 = 	   
			"<"+DATA_INCLUDE_1+">"+"$date"+"</"+DATA+">\n";
	private static final String XML_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_2 = 	   
			"<"+DATA_INCLUDE_2+">"+"$date"+"</"+DATA+">\n";
	private static final String XML_TEMPLATE_VELOCITY_REQUEST_INCLUDE= 
			"{\n"+	   
					XML_TEMPLATE_VELOCITY_BODY+
					XML_TEMPLATE_VELOCITY_BODY_INCLUDE_1+
					XML_TEMPLATE_VELOCITY_BODY_INCLUDE_2+
			"\n}";
	private static final String XML_TEMPLATE_VELOCITY_RESPONSE_INCLUDE= 
			"{\n"+	   
					XML_TEMPLATE_VELOCITY_BODY+
					XML_TEMPLATE_VELOCITY_BODY_RESPONSE+
					XML_TEMPLATE_VELOCITY_BODY_INCLUDE_1+
					XML_TEMPLATE_VELOCITY_BODY_INCLUDE_2+
			"\n}";

	private static final String XML_TEMPLATE_VELOCITY_REQUEST_CONTEXT = 
			"${context.put(\""+PDDCONTEXT_1+"\", \""+PDDCONTEXT_1_VALORE+"\")}\n"+
			"#set ( $cfg1 = ${config[\""+CONFIG1+"\"]} )\n"+
	        "${context.put(\""+PDDCONTEXT_CONFIG_REQ+"\", ${cfg1})}";
	private static final String XML_TEMPLATE_VELOCITY_RESPONSE_CONTEXT = 
			"${context.put(\""+PDDCONTEXT_3+"\", \""+PDDCONTEXT_3_VALORE+"\")}\n"+
			"#set ( $cfg3 = ${config[\""+CONFIG3+"\"]} )\n"+
	        "${context.put(\""+PDDCONTEXT_CONFIG_RES+"\", ${cfg3})}";
	
	
	
	// **** Template XSLT *****
	
	private static final String PREFIX_ORIGINALE = "orig";
	private static final String PREFIX_TO = "modificato";
	
	private static final String XSLT_PREFIX_REPLACE_ATTRIBUTE_ELEMENT = 
	"  <xsl:template match=\"*\">\n"+
	"    <xsl:element name=\""+PREFIX_TO+":{local-name()}\" namespace=\"https://example.govway.org\">\n"+
	"      <xsl:apply-templates select=\"@* | node()\"/>\n"+
	"    </xsl:element>\n"+
	"  </xsl:template>\n"+
	"\n"+
	"  <xsl:template match=\"text()\">\n"+
	"    <xsl:element name=\""+PREFIX_TO+":{local-name(../../*)}\" namespace=\"https://example.govway.org\">\n"+
	"     <xsl:value-of select=\".\"/>\n"+
	"   </xsl:element>\n"+
	" </xsl:template>\n"+
	"\n"+
	" <xsl:template match=\"@*\">\n"+
	"   <xsl:element name=\""+PREFIX_TO+":{local-name()}\" namespace=\"https://example.govway.org\">\n"+
	"     <xsl:value-of select=\".\"/>\n"+
	"   </xsl:element>\n"+
	" </xsl:template>\n";
	
	private static final String XSLT_PREFIX_REPLACE_PREFIX_ONLY_NODE = 
	"<xsl:template match=\""+PREFIX_ORIGINALE+":*\" xmlns:"+PREFIX_ORIGINALE+"=\"https://example.govway.org\">\n"+
	"  <xsl:element name=\""+PREFIX_TO+":{local-name()}\" namespace=\"{namespace-uri(.)}\">\n"+
	"    <xsl:apply-templates select=\"@*|node()\"/>\n"+
	"  </xsl:element>\n"+
	"</xsl:template>\n"+
	"\n"+
	"<xsl:template match=\"@*|node()\"><!--identity for all other nodes-->\n"+
	"  <xsl:copy>\n"+
	"    <xsl:apply-templates select=\"@*|node()\"/>\n"+
	"  </xsl:copy>\n"+
	"</xsl:template>";
	
	private static final String XSLT_PREFIX_REPLACE_ALL = ""+
	"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"+
	"  <xsl:output omit-xml-declaration=\"yes\" indent=\"yes\"/>\n"+
	"  <xsl:strip-space elements=\"*\"/>\n"+
	"\n"+
	XSLT_PREFIX_REPLACE_ATTRIBUTE_ELEMENT+
	"\n"+
	"</xsl:stylesheet>";
	
	private static final String XSLT_PREFIX_REPLACE_ONLY_PREFIX = ""+
	"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"+
	"  <xsl:output omit-xml-declaration=\"yes\" indent=\"yes\"/>\n"+
	"\n"+
	XSLT_PREFIX_REPLACE_PREFIX_ONLY_NODE+
	"\n"+
	"</xsl:stylesheet>";
	
	private static final String XSLT_XML_INPUT = ""+
	"<"+PREFIX_ORIGINALE+":Request xmlns:"+PREFIX_ORIGINALE+"=\"https://example.govway.org\">\n"+
	"   <"+PREFIX_ORIGINALE+":Body>\n"+
	"      <"+PREFIX_ORIGINALE+":Fields>\n"+
	"         <"+PREFIX_ORIGINALE+":Field1 attribute1=\"valore primo attributo\">valore primo field</"+PREFIX_ORIGINALE+":Field1>\n"+
	"         <"+PREFIX_ORIGINALE+":Field2 attribute2=\"valore secondo attributo\"/>\n"+
	"         <"+PREFIX_ORIGINALE+":Field3>valore terzo field</"+PREFIX_ORIGINALE+":Field3>\n"+
	"      </"+PREFIX_ORIGINALE+":Fields>\n"+
	"   </"+PREFIX_ORIGINALE+":Body>\n"+
	"</"+PREFIX_ORIGINALE+":Request>\n";
	
	
	
	
	// **** Template Compress *****
	
	private static final String COMPRESS_ENTRY_NAME1 = "entryName1";
	private static final String COMPRESS_ENTRY_NAME2_PREFIX = "dir/subdir/entryName2_";
	private static final String COMPRESS_ENTRY_NAME2_VALUE_PREFIX = "Valore dinamico ";
	private static final String COMPRESS_ENTRY_NAME2_VALUE_SUFFIX = " terminato";
	private static final String COMPRESS_ENTRY_NAME3 = "entryName3";
	private static final String COMPRESS_ENTRY_NAME3_VALORE_STATICO = "valoreStatico";
	private static final String COMPRESS_REST_TEMPLATE_REQUEST= 
			COMPRESS_ENTRY_NAME1+"="+Costanti.COMPRESS_CONTENT+"\n"+
			COMPRESS_ENTRY_NAME2_PREFIX+"${header\\:"+HEADER1+"}="+COMPRESS_ENTRY_NAME2_VALUE_PREFIX+"${header\\:"+HEADER2+"}"+COMPRESS_ENTRY_NAME2_VALUE_SUFFIX;
	private static final String COMPRESS_REST_TEMPLATE_RESPONSE=
			COMPRESS_REST_TEMPLATE_REQUEST+"\n"+
			COMPRESS_ENTRY_NAME3+"="+COMPRESS_ENTRY_NAME3_VALORE_STATICO;
	
	private static final String COMPRESS_ENTRY_NAME_SOAPENVELOPE = "soapStructure/soapEnvelope";
	private static final String COMPRESS_ENTRY_NAME_SOAPBODY = "soapStructure/soapBody";
	private static final String COMPRESS_ENTRY_NAME_ATTACH1 = "soapStructure/attachments/attach1";
	private static final String COMPRESS_ENTRY_NAME_ATTACH2 = "soapStructure/attachments/attach2";
	private static final String COMPRESS_SOAP_TEMPLATE_REQUEST= 
			COMPRESS_REST_TEMPLATE_REQUEST+"\n"+
			COMPRESS_ENTRY_NAME_SOAPBODY+"="+Costanti.COMPRESS_BODY+"\n"+
			COMPRESS_ENTRY_NAME_SOAPENVELOPE+"="+Costanti.COMPRESS_ENVELOPE+"\n"+
			COMPRESS_ENTRY_NAME_ATTACH1+"="+Costanti.COMPRESS_ATTACH_PREFIX+0+Costanti.COMPRESS_SUFFIX+"\n"+
			COMPRESS_ENTRY_NAME_ATTACH2+"="+Costanti.COMPRESS_ATTACH_PREFIX+1+Costanti.COMPRESS_SUFFIX;
	private static final String COMPRESS_SOAP_TEMPLATE_RESPONSE=
			COMPRESS_SOAP_TEMPLATE_REQUEST+"\n"+
			COMPRESS_ENTRY_NAME3+"="+COMPRESS_ENTRY_NAME3_VALORE_STATICO;
	
	private static final String CONFIG = "<openspcoop2 xmlns=\"http://www.openspcoop2.org/core/config\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openspcoop2.org/core/config config.xsd\">\n"+
			"	<soggetto tipo=\"proxy\" nome=\"MinisteroFruitore\" />\n"+
			"   <configurazione> \n"+
			"          <accesso-registro>\n"+
			"               <registro nome=\"registroXML\" tipo=\"xml\" location=\"registroServizi.xml\" />\n"+
			"          </accesso-registro>\n"+
			"          <inoltro-buste-non-riscontrate cadenza=\"60\" />\n"+
			"          <messaggi-diagnostici severita-log4j=\"infoIntegration\" severita=\"infoIntegration\" />\n"+
			"          <system-properties>\n"+
			"          		<system-property nome=\""+SYSTEM_CONFIG1+"\" valore=\""+SYSTEM_CONFIG1_VALORE+"\"/>\n"+
			"          		<system-property nome=\""+SYSTEM_CONFIG2+"\" valore=\""+SYSTEM_CONFIG2_VALORE+"\"/>\n"+
			"          </system-properties>\n"+
			"     </configurazione>\n"+
			"</openspcoop2>";
	
	
	public static void main(String [] args) throws Exception{
		
		File fTmpConfig = File.createTempFile("configTest", ".xml"); 
		File fTmpOp2Properties = File.createTempFile("govway", ".properties"); 
		try {
		
		TipoTrasformazione tipoTest = null;
		if(args!=null && args.length>0) {
			tipoTest = TipoTrasformazione.valueOf(args[0]);
		}
		
		
		
		// Preparo Contesto
		
		Logger log = LoggerWrapperFactory.getLogger(Test.class);
		
		PdDContext pddContext = new PdDContext();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, UUIDUtilsGenerator.newUUID());
		
		boolean bufferMessage_readOnly = true;
		
		String urlInvocazione =  "/govway/out/ENTE/Erogatore/Servizio/v1/azione/test?"+
				QUERY1+"="+QUERY1_VALORE+"&"+
				QUERY2+"="+QUERY2_VALORE+"&"+
				QUERY2+"="+QUERY2_VALORE_POSIZIONE_2+"&"+
				QUERY3+"="+QUERY3_VALORE+"&"+
				QUERY4+"="+QUERY4_VALORE;
		
		Busta busta = new Busta("trasparente");
		busta.setMittente(BUSTA_MITTENTE_VALORE);
		busta.addProperty(BUSTA_PROPERTY, BUSTA_PROPERTY_VALORE);
		
		Map<String, List<String>> parametriTrasporto = new HashMap<String, List<String>>();
		TransportUtils.addHeader(parametriTrasporto,HEADER1_CASE_INSENTIVE, HEADER1_VALORE);
		TransportUtils.addHeader(parametriTrasporto,HEADER2, HEADER2_VALORE);
		TransportUtils.addHeader(parametriTrasporto,HEADER2, HEADER2_VALORE_POSIZIONE_2);
		
		Map<String, List<String>> parametriTrasportoRisposta = new HashMap<String, List<String>>();
		TransportUtils.addHeader(parametriTrasportoRisposta,HEADER1_RISPOSTA_CASE_INSENTIVE, HEADER1_VALORE_RISPOSTA);
		TransportUtils.addHeader(parametriTrasportoRisposta,HEADER2_RISPOSTA, HEADER2_VALORE_RISPOSTA);
		TransportUtils.addHeader(parametriTrasportoRisposta,HEADER2_RISPOSTA, HEADER2_VALORE_RISPOSTA_POSIZIONE_2);
		
		Map<String, List<String>> parametriUrl = new HashMap<String, List<String>>();
		TransportUtils.addParameter(parametriUrl,QUERY1, QUERY1_VALORE);
		TransportUtils.addParameter(parametriUrl,QUERY2, QUERY2_VALORE);
		TransportUtils.addParameter(parametriUrl,QUERY2, QUERY2_VALORE_POSIZIONE_2);
		TransportUtils.addParameter(parametriUrl,QUERY3, QUERY3_VALORE);
		TransportUtils.addParameter(parametriUrl,QUERY4, QUERY4_VALORE);
		
		Map<String, List<String>> parametriForm = new HashMap<String, List<String>>();
		TransportUtils.addParameter(parametriForm,FORM1, FORM1_VALORE);
		TransportUtils.addParameter(parametriForm,FORM2, FORM2_VALORE);
		TransportUtils.addParameter(parametriForm,FORM2, FORM2_VALORE_POSIZIONE_2);
		
		Map<String, String> config = new HashMap<String, String>();
		config.put(CONFIG1, CONFIG1_VALORE);
		config.put(CONFIG2, CONFIG2_VALORE);
		config.put(CONFIG3, CONFIG3_VALORE);
		config.put(CONFIG4, CONFIG4_VALORE);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE, config);
		
		Map<String, String> configSoggettoFruitore = new HashMap<String, String>();
		configSoggettoFruitore.put(CONFIG1_SOGGETTO_FRUITORE, CONFIG1_SOGGETTO_FRUITORE_VALORE);
		configSoggettoFruitore.put(CONFIG2_SOGGETTO_FRUITORE, CONFIG2_SOGGETTO_FRUITORE_VALORE);
		configSoggettoFruitore.put(CONFIG3_SOGGETTO_FRUITORE, CONFIG3_SOGGETTO_FRUITORE_VALORE);
		configSoggettoFruitore.put(CONFIG4_SOGGETTO_FRUITORE, CONFIG4_SOGGETTO_FRUITORE_VALORE);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE, configSoggettoFruitore);
		
		Map<String, String> configSoggettoErogatore = new HashMap<String, String>();
		configSoggettoErogatore.put(CONFIG1_SOGGETTO_EROGATORE, CONFIG1_SOGGETTO_EROGATORE_VALORE);
		configSoggettoErogatore.put(CONFIG2_SOGGETTO_EROGATORE, CONFIG2_SOGGETTO_EROGATORE_VALORE);
		configSoggettoErogatore.put(CONFIG3_SOGGETTO_EROGATORE, CONFIG3_SOGGETTO_EROGATORE_VALORE);
		configSoggettoErogatore.put(CONFIG4_SOGGETTO_EROGATORE, CONFIG4_SOGGETTO_EROGATORE_VALORE);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_EROGATORE, configSoggettoErogatore);
		
		Map<String, String> configServizioApplicativo = new HashMap<String, String>();
		configServizioApplicativo.put(CONFIG1_APPLICATIVO, CONFIG1_APPLICATIVO_VALORE);
		configServizioApplicativo.put(CONFIG2_APPLICATIVO, CONFIG2_APPLICATIVO_VALORE);
		configServizioApplicativo.put(CONFIG3_APPLICATIVO, CONFIG3_APPLICATIVO_VALORE);
		configServizioApplicativo.put(CONFIG4_APPLICATIVO, CONFIG4_APPLICATIVO_VALORE);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO, configServizioApplicativo);
		
		pddContext.addObject(PDDCONTEXT_1, PDDCONTEXT_1_VALORE);
		pddContext.addObject(PDDCONTEXT_2, PDDCONTEXT_2_VALORE);
		pddContext.addObject(PDDCONTEXT_3, PDDCONTEXT_3_VALORE);
		pddContext.addObject(PDDCONTEXT_4, PDDCONTEXT_4_VALORE);
		
		FileSystemUtilities.writeFile(fTmpOp2Properties.getAbsolutePath(), "org.openspcoop2.pdd.confDirectory=/tmp\norg.openspcoop2.pdd.server=web".getBytes());
		OpenSPCoop2Properties.initialize(null, fTmpOp2Properties.getAbsolutePath());
		PddProperties.initialize(fTmpOp2Properties.getAbsolutePath(), null);
		
		AccessoConfigurazionePdD configPdD = new AccessoConfigurazionePdD();
		configPdD.setTipo("xml");
		configPdD.setLocation(fTmpConfig.getAbsolutePath());
		FileSystemUtilities.writeFile(fTmpConfig.getAbsolutePath(), CONFIG.getBytes());
		DriverConfigurazioneXML.disableBuildXsdValidator();
		ConfigurazionePdDReader.initialize(configPdD, log, log, null, null, true, false, false, false, null, CacheType.JCS);
		
		System.setProperty(JAVA_CONFIG1, JAVA_CONFIG1_VALORE);
		System.setProperty(JAVA_CONFIG2, JAVA_CONFIG2_VALORE);
		
		OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
//		Element elementRequest = XMLUtils.getInstance(messageFactory).newElement(XML_REQUEST.getBytes());
//		Element elementResponse = XMLUtils.getInstance(messageFactory).newElement(XML_RESPONSE.getBytes());
		
		OpenSPCoop2Message jsonMessageRequest = messageFactory.createMessage(MessageType.JSON, MessageRole.REQUEST, 
				HttpConstants.CONTENT_TYPE_JSON, JSON_REQUEST.getBytes()).getMessage();
		MessageContent messageContentJsonRequest = new MessageContent(jsonMessageRequest.castAsRestJson(),
				bufferMessage_readOnly, pddContext);
		
		OpenSPCoop2Message jsonMessageResponse = messageFactory.createMessage(MessageType.JSON, MessageRole.RESPONSE, 
				HttpConstants.CONTENT_TYPE_JSON, JSON_RESPONSE.getBytes()).getMessage();
		MessageContent messageContentJsonResponse = new MessageContent(jsonMessageResponse.castAsRestJson(),
				bufferMessage_readOnly, pddContext);
		
		OpenSPCoop2Message jsonMessageTestAlterazioni = messageFactory.createMessage(MessageType.JSON, MessageRole.REQUEST, 
				HttpConstants.CONTENT_TYPE_JSON, JSON_REQUEST.getBytes()).getMessage();
		MessageContent messageContentJsonTestAlterazioni = new MessageContent(jsonMessageTestAlterazioni.castAsRestJson(),
				bufferMessage_readOnly, pddContext);
		
		OpenSPCoop2Message xmlMessageRequest = messageFactory.createMessage(MessageType.SOAP_11, MessageRole.REQUEST, 
				HttpConstants.CONTENT_TYPE_SOAP_1_1, XML_REQUEST.getBytes()).getMessage();
		MessageContent messageContentRequest = new MessageContent(xmlMessageRequest.castAsSoap(),
				bufferMessage_readOnly, pddContext);
		AttachmentPart ap1 = xmlMessageRequest.castAsSoap().createAttachmentPart();
		ap1.setContent(JSON_REQUEST, HttpConstants.CONTENT_TYPE_JSON);
		xmlMessageRequest.castAsSoap().addAttachmentPart(ap1);
		AttachmentPart ap2 = xmlMessageRequest.castAsSoap().createAttachmentPart();
		ap2.setContent(HELLO_WORLD_PLAIN, HttpConstants.CONTENT_TYPE_PLAIN);
		xmlMessageRequest.castAsSoap().addAttachmentPart(ap2);
		
		OpenSPCoop2Message xmlMessageResponse = messageFactory.createMessage(MessageType.SOAP_11, MessageRole.RESPONSE, 
				HttpConstants.CONTENT_TYPE_SOAP_1_1, XML_RESPONSE.getBytes()).getMessage();
		MessageContent messageContentResponse = new MessageContent(xmlMessageResponse.castAsSoap(),
				bufferMessage_readOnly, pddContext);
		ap1 = xmlMessageResponse.castAsSoap().createAttachmentPart();
		ap1.setContent(JSON_REQUEST, HttpConstants.CONTENT_TYPE_JSON);
		xmlMessageResponse.castAsSoap().addAttachmentPart(ap1);
		ap2 = xmlMessageResponse.castAsSoap().createAttachmentPart();
		ap2.setContent(HELLO_WORLD_PLAIN, HttpConstants.CONTENT_TYPE_PLAIN);
		xmlMessageResponse.castAsSoap().addAttachmentPart(ap2);
		
		RicezioneContenutiApplicativiInternalErrorGenerator generator = null;
		
		Map<String, Object> dynamicMapXmlRequest = new HashMap<String, Object>();
		ErrorHandler errorHandlerXmlRequest = new ErrorHandler(generator, IntegrationFunctionError.TRANSFORMATION_RULE_REQUEST_FAILED, pddContext);
		DynamicUtils.fillDynamicMapRequest(log, dynamicMapXmlRequest, pddContext, urlInvocazione,
				xmlMessageRequest,
				messageContentRequest, 
				busta, 
				parametriTrasporto, 
				parametriUrl,
				parametriForm,
				errorHandlerXmlRequest);
		
		Map<String, Object> dynamicMapXmlResponse = new HashMap<String, Object>();
		ErrorHandler errorHandlerXmlResponse = new ErrorHandler(generator, IntegrationFunctionError.TRANSFORMATION_RULE_RESPONSE_FAILED, pddContext);
		DynamicUtils.fillDynamicMapResponse(log, dynamicMapXmlResponse, dynamicMapXmlRequest, pddContext, 
				xmlMessageResponse,
				messageContentResponse, 
				busta, parametriTrasportoRisposta,
				errorHandlerXmlResponse);
		
		Map<String, Object> dynamicMapJsonRequest = new HashMap<String, Object>();
		ErrorHandler errorHandlerJsonRequest = new ErrorHandler(generator, IntegrationFunctionError.TRANSFORMATION_RULE_REQUEST_FAILED, pddContext);
		DynamicUtils.fillDynamicMapRequest(log, dynamicMapJsonRequest, pddContext, urlInvocazione,
				jsonMessageRequest,
				messageContentJsonRequest, 
				busta, 
				parametriTrasporto, 
				parametriUrl,
				parametriForm,
				errorHandlerJsonRequest);
		
		Map<String, Object> dynamicMapJsonResponse = new HashMap<String, Object>();
		ErrorHandler errorHandlerJsonResponse = new ErrorHandler(generator, IntegrationFunctionError.TRANSFORMATION_RULE_RESPONSE_FAILED, pddContext);
		DynamicUtils.fillDynamicMapResponse(log, dynamicMapJsonResponse, dynamicMapJsonRequest, pddContext, 
				jsonMessageResponse,
				messageContentJsonResponse,  
				busta, parametriTrasportoRisposta,
				errorHandlerJsonResponse);
		
		Map<String, Object> dynamicMapJsonTestAlterazioni = new HashMap<String, Object>();
		ErrorHandler errorHandlerJsonTestAlterazioni = new ErrorHandler(generator, IntegrationFunctionError.TRANSFORMATION_RULE_REQUEST_FAILED, pddContext);
		DynamicUtils.fillDynamicMapRequest(log, dynamicMapJsonTestAlterazioni, pddContext, urlInvocazione,
				jsonMessageTestAlterazioni,
				messageContentJsonTestAlterazioni, 
				busta, 
				parametriTrasporto, 
				parametriUrl,
				parametriForm,
				errorHandlerJsonTestAlterazioni);
		
		if(tipoTest==null || TipoTrasformazione.TEMPLATE.equals(tipoTest)) {
		
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.TEMPLATE) , "xml", pddContext, 
					dynamicMapXmlRequest, null, JSON_TEMPLATE_REQUEST.getBytes(), 
					dynamicMapXmlResponse, null, JSON_TEMPLATE_RESPONSE.getBytes());
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.TEMPLATE) , "json", pddContext, 
					dynamicMapJsonRequest, null, XML_TEMPLATE_REQUEST.getBytes(), 
					dynamicMapJsonResponse, null,  XML_TEMPLATE_RESPONSE.getBytes());
			
		}
		
		if(tipoTest==null || TipoTrasformazione.FREEMARKER_TEMPLATE.equals(tipoTest)) {
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.FREEMARKER_TEMPLATE) , "xml", pddContext, 
					dynamicMapXmlRequest, null,  JSON_TEMPLATE_FREEMARKER_REQUEST.getBytes(), 
					dynamicMapXmlResponse, null,  JSON_TEMPLATE_FREEMARKER_RESPONSE.getBytes());
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.FREEMARKER_TEMPLATE) , "json", pddContext, 
					dynamicMapJsonRequest, null,  XML_TEMPLATE_FREEMARKER_REQUEST.getBytes(), 
					dynamicMapJsonResponse, null,  XML_TEMPLATE_FREEMARKER_RESPONSE.getBytes());
			
		}
		
		if(tipoTest==null || TipoTrasformazione.CONTEXT_FREEMARKER_TEMPLATE.equals(tipoTest)) {
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.CONTEXT_FREEMARKER_TEMPLATE) , "contextVerifica", pddContext, 
					dynamicMapJsonRequest, null,  XML_TEMPLATE_FREEMARKER_REQUEST_CONTEXT.getBytes(), 
					dynamicMapJsonResponse, null,  XML_TEMPLATE_FREEMARKER_RESPONSE_CONTEXT.getBytes());
			
			elaborazioniJson(log, dynamicMapJsonTestAlterazioni,
					bufferMessage_readOnly, pddContext);
		}
		
		if(tipoTest==null || TipoTrasformazione.FREEMARKER_TEMPLATE_ZIP.equals(tipoTest)) {
			
			List<Entry> zipEntriesFreeMarkerJsonRequest = new ArrayList<>();
			zipEntriesFreeMarkerJsonRequest.add(new Entry(Costanti.ZIP_INDEX_ENTRY_FREEMARKER, JSON_TEMPLATE_FREEMARKER_REQUEST_INCLUDE.getBytes()));
			zipEntriesFreeMarkerJsonRequest.add(new Entry(JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_1_PATH, JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_1.getBytes()));
			zipEntriesFreeMarkerJsonRequest.add(new Entry(JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_2_PATH, JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_2.getBytes()));
			byte [] zipJsonRequest = ZipUtilities.zip(zipEntriesFreeMarkerJsonRequest);		
			List<Entry> zipEntriesFreeMarkerJsonResponse = new ArrayList<>();
			zipEntriesFreeMarkerJsonResponse.add(new Entry(Costanti.ZIP_INDEX_ENTRY_FREEMARKER, JSON_TEMPLATE_FREEMARKER_RESPONSE_INCLUDE.getBytes()));
			zipEntriesFreeMarkerJsonResponse.add(new Entry(JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_1_PATH, JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_1.getBytes()));
			zipEntriesFreeMarkerJsonResponse.add(new Entry(JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_2_PATH, JSON_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_2.getBytes()));
			byte [] zipJsonResponse = ZipUtilities.zip(zipEntriesFreeMarkerJsonResponse);		
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.FREEMARKER_TEMPLATE_ZIP) , "xml", pddContext, 
					dynamicMapXmlRequest, null,  zipJsonRequest, 
					dynamicMapXmlResponse, null,  zipJsonResponse);
			
			List<Entry> zipEntriesFreeMarkerXmlRequest = new ArrayList<>();
			zipEntriesFreeMarkerXmlRequest.add(new Entry(Costanti.ZIP_INDEX_ENTRY_FREEMARKER, XML_TEMPLATE_FREEMARKER_REQUEST_INCLUDE.getBytes()));
			zipEntriesFreeMarkerXmlRequest.add(new Entry(XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_1_PATH, XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_1.getBytes()));
			zipEntriesFreeMarkerXmlRequest.add(new Entry(XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_2_PATH, XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_2.getBytes()));
			byte [] zipXmlRequest = ZipUtilities.zip(zipEntriesFreeMarkerXmlRequest);		
			List<Entry> zipEntriesFreeMarkerXmlResponse = new ArrayList<>();
			zipEntriesFreeMarkerXmlResponse.add(new Entry(Costanti.ZIP_INDEX_ENTRY_FREEMARKER, XML_TEMPLATE_FREEMARKER_RESPONSE_INCLUDE.getBytes()));
			zipEntriesFreeMarkerXmlResponse.add(new Entry(XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_1_PATH, XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_1.getBytes()));
			zipEntriesFreeMarkerXmlResponse.add(new Entry(XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_2_PATH, XML_TEMPLATE_FREEMARKER_BODY_INCLUDE_CONTENT_2.getBytes()));
			byte [] zipXmlResponse = ZipUtilities.zip(zipEntriesFreeMarkerXmlResponse);		
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.FREEMARKER_TEMPLATE_ZIP) , "json", pddContext, 
					dynamicMapJsonRequest, null,  zipXmlRequest, 
					dynamicMapJsonResponse, null, zipXmlResponse);
			
		}
		
		if(tipoTest==null || TipoTrasformazione.VELOCITY_TEMPLATE.equals(tipoTest)) {
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.VELOCITY_TEMPLATE) , "xml", pddContext, 
					dynamicMapXmlRequest, null,  JSON_TEMPLATE_VELOCITY_REQUEST.getBytes(), 
					dynamicMapXmlResponse, null,  JSON_TEMPLATE_VELOCITY_RESPONSE.getBytes());
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.VELOCITY_TEMPLATE) , "json", pddContext, 
					dynamicMapJsonRequest, null,  XML_TEMPLATE_VELOCITY_REQUEST.getBytes(), 
					dynamicMapJsonResponse, null,  XML_TEMPLATE_VELOCITY_RESPONSE.getBytes());
			
		}
		
		if(tipoTest==null || TipoTrasformazione.CONTEXT_VELOCITY_TEMPLATE.equals(tipoTest)) {
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.CONTEXT_VELOCITY_TEMPLATE) , "contextVerifica", pddContext, 
					dynamicMapJsonRequest, null,  XML_TEMPLATE_VELOCITY_REQUEST_CONTEXT.getBytes(), 
					dynamicMapJsonResponse, null,  XML_TEMPLATE_VELOCITY_RESPONSE_CONTEXT.getBytes());
			
			elaborazioniJson(log, dynamicMapJsonTestAlterazioni,
					bufferMessage_readOnly, pddContext);
			
		}
		
		if(tipoTest==null || TipoTrasformazione.VELOCITY_TEMPLATE_ZIP.equals(tipoTest)) {
			
			List<Entry> zipEntriesVelocityJsonRequest = new ArrayList<>();
			zipEntriesVelocityJsonRequest.add(new Entry(Costanti.ZIP_INDEX_ENTRY_VELOCITY, JSON_TEMPLATE_VELOCITY_REQUEST_INCLUDE.getBytes()));
			zipEntriesVelocityJsonRequest.add(new Entry(JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_1_PATH, JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_1.getBytes()));
			zipEntriesVelocityJsonRequest.add(new Entry(JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_2_PATH, JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_2.getBytes()));
			byte [] zipJsonRequest = ZipUtilities.zip(zipEntriesVelocityJsonRequest);		
			List<Entry> zipEntriesVelocityJsonResponse = new ArrayList<>();
			zipEntriesVelocityJsonResponse.add(new Entry(Costanti.ZIP_INDEX_ENTRY_VELOCITY, JSON_TEMPLATE_VELOCITY_RESPONSE_INCLUDE.getBytes()));
			zipEntriesVelocityJsonResponse.add(new Entry(JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_1_PATH, JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_1.getBytes()));
			zipEntriesVelocityJsonResponse.add(new Entry(JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_2_PATH, JSON_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_2.getBytes()));
			byte [] zipJsonResponse = ZipUtilities.zip(zipEntriesVelocityJsonResponse);		
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.VELOCITY_TEMPLATE_ZIP) , "xml", pddContext, 
					dynamicMapXmlRequest, null,  zipJsonRequest, 
					dynamicMapXmlResponse, null,  zipJsonResponse);
			
			List<Entry> zipEntriesVelocityXmlRequest = new ArrayList<>();
			zipEntriesVelocityXmlRequest.add(new Entry(Costanti.ZIP_INDEX_ENTRY_VELOCITY, XML_TEMPLATE_VELOCITY_REQUEST_INCLUDE.getBytes()));
			zipEntriesVelocityXmlRequest.add(new Entry(XML_TEMPLATE_VELOCITY_BODY_INCLUDE_1_PATH, XML_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_1.getBytes()));
			zipEntriesVelocityXmlRequest.add(new Entry(XML_TEMPLATE_VELOCITY_BODY_INCLUDE_2_PATH, XML_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_2.getBytes()));
			byte [] zipXmlRequest = ZipUtilities.zip(zipEntriesVelocityXmlRequest);		
			List<Entry> zipEntriesVelocityXmlResponse = new ArrayList<>();
			zipEntriesVelocityXmlResponse.add(new Entry(Costanti.ZIP_INDEX_ENTRY_VELOCITY, XML_TEMPLATE_VELOCITY_RESPONSE_INCLUDE.getBytes()));
			zipEntriesVelocityXmlResponse.add(new Entry(XML_TEMPLATE_VELOCITY_BODY_INCLUDE_1_PATH, XML_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_1.getBytes()));
			zipEntriesVelocityXmlResponse.add(new Entry(XML_TEMPLATE_VELOCITY_BODY_INCLUDE_2_PATH, XML_TEMPLATE_VELOCITY_BODY_INCLUDE_CONTENT_2.getBytes()));
			byte [] zipXmlResponse = ZipUtilities.zip(zipEntriesVelocityXmlResponse);		
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.VELOCITY_TEMPLATE_ZIP) , "json", pddContext, 
					dynamicMapJsonRequest, null,  zipXmlRequest, 
					dynamicMapJsonResponse, null,  zipXmlResponse);
			
		}
		
		if(tipoTest==null || TipoTrasformazione.XSLT.equals(tipoTest)) {
			
			OpenSPCoop2Message xsltElementRequest = messageFactory.createMessage(MessageType.XML, MessageRole.REQUEST, 
					HttpConstants.CONTENT_TYPE_XML, XSLT_XML_INPUT.getBytes()).getMessage();
			MessageContent messageContentXsltElementRequest = new MessageContent(xsltElementRequest.castAsRestXml(),
					bufferMessage_readOnly, pddContext);
			
			OpenSPCoop2Message xsltElementResponse = messageFactory.createMessage(MessageType.XML, MessageRole.RESPONSE, 
					HttpConstants.CONTENT_TYPE_XML, XSLT_XML_INPUT.getBytes()).getMessage();
			MessageContent messageContentXsltElementResponse = new MessageContent(xsltElementResponse.castAsRestXml(),
					bufferMessage_readOnly, pddContext);
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.XSLT) , "xml", pddContext, 
					null, messageContentXsltElementRequest,  XSLT_PREFIX_REPLACE_ALL.getBytes(), 
					null, messageContentXsltElementResponse,  XSLT_PREFIX_REPLACE_ONLY_PREFIX.getBytes());
			
		}
		
		if(tipoTest==null || TipoTrasformazione.ZIP.equals(tipoTest)) {
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.ZIP) , "soap", pddContext, 
					dynamicMapXmlRequest, null,  COMPRESS_SOAP_TEMPLATE_REQUEST.getBytes(), 
					dynamicMapXmlResponse, null,  COMPRESS_SOAP_TEMPLATE_RESPONSE.getBytes());
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.ZIP) , "json", pddContext, 
					dynamicMapJsonRequest, null,  COMPRESS_REST_TEMPLATE_REQUEST.getBytes(), 
					dynamicMapJsonResponse, null,  COMPRESS_REST_TEMPLATE_RESPONSE.getBytes());
			
		}
		
		if(tipoTest==null || TipoTrasformazione.TGZ.equals(tipoTest)) {
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.TGZ) , "soap", pddContext, 
					dynamicMapXmlRequest, null,  COMPRESS_SOAP_TEMPLATE_REQUEST.getBytes(), 
					dynamicMapXmlResponse, null,  COMPRESS_SOAP_TEMPLATE_RESPONSE.getBytes());
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.TGZ) , "json", pddContext, 
					dynamicMapJsonRequest, null,  COMPRESS_REST_TEMPLATE_REQUEST.getBytes(), 
					dynamicMapJsonResponse, null,  COMPRESS_REST_TEMPLATE_RESPONSE.getBytes());
			
		}
		
		if(tipoTest==null || TipoTrasformazione.TAR.equals(tipoTest)) {
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.TAR) , "soap", pddContext, 
					dynamicMapXmlRequest, null,  COMPRESS_SOAP_TEMPLATE_REQUEST.getBytes(), 
					dynamicMapXmlResponse, null,  COMPRESS_SOAP_TEMPLATE_RESPONSE.getBytes());
			
			test(log, messageFactory, (tipoTest!=null ? tipoTest : TipoTrasformazione.TAR) , "json", pddContext, 
					dynamicMapJsonRequest, null,  COMPRESS_REST_TEMPLATE_REQUEST.getBytes(), 
					dynamicMapJsonResponse, null,  COMPRESS_REST_TEMPLATE_RESPONSE.getBytes());
			
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
	
	private static void test(Logger log, OpenSPCoop2MessageFactory messageFactory, TipoTrasformazione tipoTest, String prefix,
			PdDContext pddContext,
			Map<String, Object> dynamicMapRequest, MessageContent messageContentRequest, byte[] templateRequest, 
			Map<String, Object> dynamicMapResponse, MessageContent messageContentResponse, byte[] templateResponse) throws Exception {
		
		System.out.println("Test ["+tipoTest+"-"+prefix+"] in corso ...");
		
		System.out.println("\trequest ...");
		RisultatoTrasformazioneContenuto risultato = null;
		try {
			risultato = GestoreTrasformazioniUtilities.trasformazioneContenuto(log, 
					tipoTest.getValue(), templateRequest, "richiesta", dynamicMapRequest, null, messageContentRequest, pddContext);
		}catch(Throwable e) {
			System.out.println("\tTemplate:\n "+new String(templateRequest));
			Utilities.sleep(1000);
			throw e;
		}
		String contenuto = risultato.getContenutoAsString();
		if(TipoTrasformazione.TEMPLATE.equals(tipoTest) ||
				TipoTrasformazione.FREEMARKER_TEMPLATE.equals(tipoTest)  ||
				TipoTrasformazione.FREEMARKER_TEMPLATE_ZIP.equals(tipoTest) ||
				TipoTrasformazione.VELOCITY_TEMPLATE.equals(tipoTest) ) {
			try {
				checkRequest(contenuto, pddContext);
				if(TipoTrasformazione.FREEMARKER_TEMPLATE_ZIP.equals(tipoTest) ||
						TipoTrasformazione.VELOCITY_TEMPLATE_ZIP.equals(tipoTest)) {
					checkInclude(contenuto, pddContext);
				}
			}catch(Throwable e) {
				System.out.println("\tTemplate:\n "+new String(templateRequest));
				System.out.println("\tOttenuto:\n "+contenuto);
				Utilities.sleep(1000);
				throw e;
			}
		}
		else if(TipoTrasformazione.CONTEXT_FREEMARKER_TEMPLATE.equals(tipoTest) ||
				TipoTrasformazione.CONTEXT_VELOCITY_TEMPLATE.equals(tipoTest)) {
			try {
				if(pddContext.containsKey(PDDCONTEXT_1)==false) {
					throw new Exception("Oggetto con chiave '"+PDDCONTEXT_1+"' non presente nel contesto");
				}
				if(pddContext.containsKey(PDDCONTEXT_CONFIG_REQ)==false) {
					throw new Exception("Oggetto con chiave '"+PDDCONTEXT_CONFIG_REQ+"' non presente nel contesto");
				}
				String valore1 = (String) pddContext.getObject(PDDCONTEXT_1);
				String valore2 = (String) pddContext.getObject(PDDCONTEXT_CONFIG_REQ);
				if(!PDDCONTEXT_1_VALORE.equals(valore1)) {
					throw new Exception("Oggetto con chiave '"+PDDCONTEXT_1+"' presente nel contesto con un valore '"+valore1+"' diverso da quello atteso '"+PDDCONTEXT_1_VALORE+"'");
				}
				if(!CONFIG1_VALORE.equals(valore2)) {
					throw new Exception("Oggetto con chiave '"+PDDCONTEXT_CONFIG_REQ+"' presente nel contesto con un valore '"+valore2+"' diverso da quello atteso '"+CONFIG1_VALORE+"'");
				}
			}catch(Throwable e) {
				System.out.println("\tTemplate:\n "+new String(templateRequest));
				Utilities.sleep(1000);
				throw e;
			}			
		}
		else if(TipoTrasformazione.ZIP.equals(tipoTest) ||
				TipoTrasformazione.TGZ.equals(tipoTest)  ||
				TipoTrasformazione.TAR.equals(tipoTest) ) {
			try {
				ArchiveType type = ArchiveType.valueOf(tipoTest.name());
				List<Entry> list = CompressorUtilities.read(risultato.getContenuto(), type);
				if("json".equals(prefix)) {
					if(list.size()!=2) {
						throw new Exception("Attesa lista di 2 elementi; trovati: "+list.size());
					}
				}
				else {
					if(list.size()!=6) {
						throw new Exception("Attesa lista di 6 elementi; trovati: "+list.size());
					}
				}
				for (Entry entry : list) {
					
					String contentEntry = new String(entry.getContent());
					
					String entryName1 = COMPRESS_ENTRY_NAME1;
					String entryName2 = COMPRESS_ENTRY_NAME2_PREFIX+HEADER1_VALORE;
					if(entryName1.equals(entry.getName())) {
						if("soap".equals(prefix)) {
							if(!contentEntry.contains("-----")) {
								throw new Exception("Payload entry '"+entryName1+"' differente da quello atteso (Multipart not found): "+contentEntry);
							}
							if(!contentEntry.contains(XML_REQUEST)) {
								throw new Exception("Payload entry '"+entryName1+"' differente da quello atteso: "+contentEntry);
							}
							if(!contentEntry.contains(JSON_REQUEST)) { // attach1
								throw new Exception("Payload entry '"+entryName1+"' differente da quello atteso: "+contentEntry);
							}
							if(!contentEntry.contains(HELLO_WORLD_PLAIN)) { // attach2
								throw new Exception("Payload entry '"+entryName1+"' differente da quello atteso: "+contentEntry);
							}
						}
						else {
							if(!contentEntry.equals(JSON_REQUEST)) {
								throw new Exception("Payload entry '"+entryName1+"' differente da quello atteso: "+contentEntry+" atteso: "+JSON_REQUEST);
							}
						}				
					}
					else if(entryName2.equals(entry.getName())) {
						String contentEntry2 = COMPRESS_ENTRY_NAME2_VALUE_PREFIX+HEADER2_VALORE+","+HEADER2_VALORE_POSIZIONE_2+COMPRESS_ENTRY_NAME2_VALUE_SUFFIX;
						if(!contentEntry2.equals(contentEntry)) {
							throw new Exception("Payload entry '"+entryName2+"' con un valore '"+contentEntry2+"' differente da quello atteso: "+contentEntry);
						}
					}
					else if("soap".equals(prefix) && COMPRESS_ENTRY_NAME_SOAPENVELOPE.equals(entry.getName())) {
						if(!contentEntry.equals(XML_REQUEST)) {
							throw new Exception("Payload entry '"+COMPRESS_ENTRY_NAME_SOAPENVELOPE+"' differente da quello atteso: "+contentEntry+"; atteso: "+XML_REQUEST);
						}
					}
					else if("soap".equals(prefix) && COMPRESS_ENTRY_NAME_SOAPBODY.equals(entry.getName())) {
						if(contentEntry.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
							throw new Exception("Payload entry '"+COMPRESS_ENTRY_NAME_SOAPBODY+"' differente da quello atteso; non si attendeva la busta SOAP: "+contentEntry);
						}
					}
					else if("soap".equals(prefix) && COMPRESS_ENTRY_NAME_ATTACH1.equals(entry.getName())) {
						if(!contentEntry.equals(JSON_REQUEST)) {
							throw new Exception("Payload entry '"+COMPRESS_ENTRY_NAME_ATTACH1+"' differente da quello atteso: "+contentEntry);
						}
					}
					else if("soap".equals(prefix) && COMPRESS_ENTRY_NAME_ATTACH2.equals(entry.getName())) {
						if(!contentEntry.equals(HELLO_WORLD_PLAIN)) {
							throw new Exception("Payload entry '"+COMPRESS_ENTRY_NAME_ATTACH2+"' differente da quello atteso: "+contentEntry);
						}
					}
					else {
						throw new Exception("Trovato entry '"+entry.getName()+"' non atteso");
					}
					
				}
				
			}catch(Exception e) {
				Utilities.sleep(1000);
				throw e;
			}
		}
		else {
			if(contenuto.contains(PREFIX_ORIGINALE)) {
				
				System.out.println("Template:\n "+new String(templateRequest));
				System.out.println("PRIMA: "+XMLUtils.getInstance(messageFactory).toString(messageContentRequest.getElement()));
				System.out.println("DOPO: "+contenuto);

				throw new Exception("Trovato '"+PREFIX_ORIGINALE+"' non atteso");
			}
		}
		System.out.println("\trequest ok");
		
		System.out.println("\tresponse ...");
		risultato = null;
		try {
			risultato = GestoreTrasformazioniUtilities.trasformazioneContenuto(log, 
					tipoTest.getValue(), templateResponse, "risposta", dynamicMapResponse, null, messageContentResponse, pddContext);
		}catch(Throwable e) {
			System.out.println("\tTemplate:\n "+new String(templateResponse));
			Utilities.sleep(1000);
			throw e;
		}
		contenuto = risultato.getContenutoAsString();
		if(TipoTrasformazione.TEMPLATE.equals(tipoTest) ||
				TipoTrasformazione.FREEMARKER_TEMPLATE.equals(tipoTest)  ||
				TipoTrasformazione.FREEMARKER_TEMPLATE_ZIP.equals(tipoTest) ||
				TipoTrasformazione.VELOCITY_TEMPLATE.equals(tipoTest)) {
			try{
				checkRequest(contenuto, pddContext);
				checkResponse(contenuto);
				if(TipoTrasformazione.FREEMARKER_TEMPLATE_ZIP.equals(tipoTest) ||
						TipoTrasformazione.VELOCITY_TEMPLATE_ZIP.equals(tipoTest)) {
					checkInclude(contenuto, pddContext);
				}
			}catch(Throwable e) {
				System.out.println("\tTemplate:\n "+new String(templateResponse));
				System.out.println("\tOttenuto:\n "+contenuto);
				Utilities.sleep(1000);
				throw e;
			}
		}
		else if(TipoTrasformazione.CONTEXT_FREEMARKER_TEMPLATE.equals(tipoTest) ||
				TipoTrasformazione.CONTEXT_VELOCITY_TEMPLATE.equals(tipoTest)) {
			try {
				if(pddContext.containsKey(PDDCONTEXT_3)==false) {
					throw new Exception("Oggetto con chiave '"+PDDCONTEXT_3+"' non presente nel contesto");
				}
				if(pddContext.containsKey(PDDCONTEXT_CONFIG_RES)==false) {
					throw new Exception("Oggetto con chiave '"+PDDCONTEXT_CONFIG_RES+"' non presente nel contesto");
				}
				String valore1 = (String) pddContext.getObject(PDDCONTEXT_3);
				String valore2 = (String) pddContext.getObject(PDDCONTEXT_CONFIG_RES);
				if(!PDDCONTEXT_3_VALORE.equals(valore1)) {
					throw new Exception("Oggetto con chiave '"+PDDCONTEXT_3+"' presente nel contesto con un valore '"+valore1+"' diverso da quello atteso '"+PDDCONTEXT_3_VALORE+"'");
				}
				if(!CONFIG3_VALORE.equals(valore2)) {
					throw new Exception("Oggetto con chiave '"+PDDCONTEXT_CONFIG_RES+"' presente nel contesto con un valore '"+valore2+"' diverso da quello atteso '"+CONFIG3_VALORE+"'");
				}
			}catch(Throwable e) {
				System.out.println("\tTemplate:\n "+new String(templateResponse));
				Utilities.sleep(1000);
				throw e;
			}			
		}
		else if(TipoTrasformazione.ZIP.equals(tipoTest) ||
				TipoTrasformazione.TGZ.equals(tipoTest)  ||
				TipoTrasformazione.TAR.equals(tipoTest) ) {
			try {
				ArchiveType type = ArchiveType.valueOf(tipoTest.name());
				List<Entry> list = CompressorUtilities.read(risultato.getContenuto(), type);
				if("json".equals(prefix)) {
					if(list.size()!=3) {
						throw new Exception("Attesa lista di 3 elementi; trovati: "+list.size());
					}
				}
				else {
					if(list.size()!=7) {
						throw new Exception("Attesa lista di 7 elementi; trovati: "+list.size());
					}
				}
				for (Entry entry : list) {
					
					String contentEntry = new String(entry.getContent());
					
					String entryName1 = COMPRESS_ENTRY_NAME1;
					String entryName2 = COMPRESS_ENTRY_NAME2_PREFIX+HEADER1_VALORE;
					String entryName3 = COMPRESS_ENTRY_NAME3;
					if(entryName1.equals(entry.getName())) {
						if("soap".equals(prefix)) {
							if(!contentEntry.contains("-----")) {
								throw new Exception("Payload entry '"+entryName1+"' differente da quello atteso (Multipart not found): "+contentEntry);
							}
							if(!contentEntry.contains(XML_RESPONSE)) {
								throw new Exception("Payload entry '"+entryName1+"' differente da quello atteso: "+contentEntry);
							}
							if(!contentEntry.contains(JSON_REQUEST)) { // attach1
								throw new Exception("Payload entry '"+entryName1+"' differente da quello atteso: "+contentEntry);
							}
							if(!contentEntry.contains(HELLO_WORLD_PLAIN)) { // attach2
								throw new Exception("Payload entry '"+entryName1+"' differente da quello atteso: "+contentEntry);
							}
						}
						else {
							if(!contentEntry.equals(JSON_RESPONSE)) {
								throw new Exception("Payload entry '"+entryName1+"' differente da quello atteso: "+contentEntry);
							}
						}				
					}
					else if(entryName2.equals(entry.getName())) {
						String contentEntry2 = COMPRESS_ENTRY_NAME2_VALUE_PREFIX+HEADER2_VALORE+","+HEADER2_VALORE_POSIZIONE_2+COMPRESS_ENTRY_NAME2_VALUE_SUFFIX;
						if(!contentEntry2.equals(contentEntry)) {
							throw new Exception("Payload entry '"+entryName2+"' con valore '"+contentEntry2+"' differente da quello atteso: "+contentEntry);
						}
					}
					else if(entryName3.equals(entry.getName())) {
						String contentEntry3 = COMPRESS_ENTRY_NAME3_VALORE_STATICO;
						if(!contentEntry3.equals(contentEntry)) {
							throw new Exception("Payload entry '"+entryName3+"' differente da quello atteso: "+contentEntry);
						}
					}
					else if("soap".equals(prefix) && COMPRESS_ENTRY_NAME_SOAPENVELOPE.equals(entry.getName())) {
						if(!contentEntry.equals(XML_RESPONSE)) {
							throw new Exception("Payload entry '"+COMPRESS_ENTRY_NAME_SOAPENVELOPE+"' differente da quello atteso: "+contentEntry+"; atteso: "+XML_RESPONSE);
						}
					}
					else if("soap".equals(prefix) && COMPRESS_ENTRY_NAME_SOAPBODY.equals(entry.getName())) {
						if(contentEntry.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
							throw new Exception("Payload entry '"+COMPRESS_ENTRY_NAME_SOAPBODY+"' differente da quello atteso; non si attendeva la busta SOAP: "+contentEntry);
						}
					}
					else if("soap".equals(prefix) && COMPRESS_ENTRY_NAME_ATTACH1.equals(entry.getName())) {
						if(!contentEntry.equals(JSON_REQUEST)) {
							throw new Exception("Payload entry '"+COMPRESS_ENTRY_NAME_ATTACH1+"' differente da quello atteso: "+contentEntry);
						}
					}
					else if("soap".equals(prefix) && COMPRESS_ENTRY_NAME_ATTACH2.equals(entry.getName())) {
						if(!contentEntry.equals(HELLO_WORLD_PLAIN)) {
							throw new Exception("Payload entry '"+COMPRESS_ENTRY_NAME_ATTACH2+"' differente da quello atteso: "+contentEntry);
						}
					}
					else {
						throw new Exception("Trovato entry '"+entry.getName()+"' non atteso");
					}
					
				}
				
			}catch(Exception e) {
				Utilities.sleep(1000);
				throw e;
			}
		}
		else {
			if(contenuto.contains(PREFIX_ORIGINALE)) {
				
				System.out.println("Template:\n "+new String(templateResponse));
				System.out.println("PRIMA: "+XMLUtils.getInstance(messageFactory).toString(messageContentResponse.getElement()));
				System.out.println("DOPO: "+contenuto);
				
				throw new Exception("Trovato '"+PREFIX_ORIGINALE+"' non atteso");
			}
		}
		System.out.println("\tresponse ok");
		
		System.out.println("Test ["+tipoTest+"-"+prefix+"] completato con successo");
	}
	
	private static void checkRequest(String contenuto, PdDContext pddContext) throws Exception {
		// verifiche
		if(!contenuto.contains(DATA)) {
			throw new Exception("Nome '"+DATA+"' non trovato");
		}
		
		if(!contenuto.contains(HEADER1)) {
			throw new Exception("Nome '"+HEADER1+"' non trovato");
		}
		if(!contenuto.contains(HEADER1_VALORE)) {
			throw new Exception("Valore '"+HEADER1_VALORE+"' per field '"+HEADER1+"' non trovato");
		}		
		if(!contenuto.contains(HEADER2)) {
			throw new Exception("Nome '"+HEADER2+"' non trovato");
		}
		if(!contenuto.contains(HEADER2_VALORE)) {
			throw new Exception("Valore '"+HEADER2_VALORE+"' per field '"+HEADER2+"' non trovato");
		}
		if(!contenuto.contains(HEADER2_VALORE_POSIZIONE_2)) {
			throw new Exception("Valore '"+HEADER2_VALORE_POSIZIONE_2+"' per field '"+HEADER2+"' non trovato");
		}
		
		if(!contenuto.contains(QUERY1)) {
			throw new Exception("Nome '"+QUERY1+"' non trovato");
		}
		if(!contenuto.contains(QUERY1_VALORE)) {
			throw new Exception("Valore '"+QUERY1_VALORE+"' per field '"+QUERY1+"' non trovato");
		}		
		if(!contenuto.contains(QUERY2)) {
			throw new Exception("Nome '"+QUERY2+"' non trovato");
		}
		if(!contenuto.contains(QUERY2_VALORE)) {
			throw new Exception("Valore '"+QUERY2_VALORE+"' per field '"+QUERY2+"' non trovato");
		}
		if(!contenuto.contains(QUERY2_VALORE_POSIZIONE_2)) {
			throw new Exception("Valore '"+QUERY2_VALORE_POSIZIONE_2+"' per field '"+QUERY2+"' non trovato");
		}
		if(!contenuto.contains(QUERY3)) {
			throw new Exception("Nome '"+QUERY3+"' non trovato");
		}
		if(!contenuto.contains(QUERY3_VALORE)) {
			throw new Exception("Valore '"+QUERY3_VALORE+"' per field '"+QUERY3+"' non trovato");
		}		
		if(!contenuto.contains(QUERY4)) {
			throw new Exception("Nome '"+QUERY4+"' non trovato");
		}
		if(!contenuto.contains(QUERY4_VALORE)) {
			throw new Exception("Valore '"+QUERY4_VALORE+"' per field '"+QUERY4+"' non trovato");
		}
		
		if(!contenuto.contains(FORM1)) {
			throw new Exception("Nome '"+FORM1+"' non trovato");
		}
		if(!contenuto.contains(FORM1_VALORE)) {
			throw new Exception("Valore '"+FORM1_VALORE+"' per field '"+FORM1+"' non trovato");
		}		
		if(!contenuto.contains(FORM2)) {
			throw new Exception("Nome '"+FORM2+"' non trovato");
		}
		if(!contenuto.contains(FORM2_VALORE)) {
			throw new Exception("Valore '"+FORM2_VALORE+"' per field '"+FORM2+"' non trovato");
		}
		if(!contenuto.contains(FORM2_VALORE_POSIZIONE_2)) {
			throw new Exception("Valore '"+FORM2_VALORE_POSIZIONE_2+"' per field '"+FORM2+"' non trovato");
		}
		
		String uuid = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		if(!contenuto.contains(TRANSACTION_ID)) {
			throw new Exception("Nome '"+TRANSACTION_ID+"' non trovato");
		}
		if(!contenuto.contains(uuid)) {
			throw new Exception("Valore '"+uuid+"' per field '"+TRANSACTION_ID+"' non trovato");
		}	
		
		if(!contenuto.contains(PATH1)) {
			throw new Exception("Nome '"+PATH1+"' non trovato");
		}
		if(!contenuto.contains(PATH1_VALORE)) {
			throw new Exception("Valore '"+PATH1_VALORE+"' per field '"+PATH1+"' non trovato");
		}		
		if(!contenuto.contains(PATH2)) {
			throw new Exception("Nome '"+PATH2+"' non trovato");
		}
		if(!contenuto.contains(PATH2_VALORE)) {
			throw new Exception("Valore '"+PATH2_VALORE+"' per field '"+PATH2+"' non trovato");
		}
		
		if(!contenuto.contains(BUSTA)) {
			throw new Exception("Nome '"+BUSTA+"' non trovato");
		}
		if(!contenuto.contains(BUSTA_MITTENTE_VALORE)) {
			throw new Exception("Valore '"+BUSTA_MITTENTE_VALORE+"' per field '"+BUSTA+"' non trovato");
		}
		
		if(!contenuto.contains(BUSTA_PROPERTY)) {
			throw new Exception("Nome '"+BUSTA_PROPERTY+"' non trovato");
		}
		if(!contenuto.contains(BUSTA_PROPERTY_VALORE)) {
			throw new Exception("Valore '"+BUSTA_PROPERTY_VALORE+"' per field '"+BUSTA_PROPERTY+"' non trovato");
		}	
		
		if(!contenuto.contains(CONFIG1)) {
			throw new Exception("Configurazione '"+CONFIG1+"' non trovata");
		}
		if(!contenuto.contains(CONFIG1_VALORE)) {
			throw new Exception("Valore '"+CONFIG1_VALORE+"' per field '"+CONFIG1+"' non trovato");
		}	

		if(!contenuto.contains(CONFIG1_APPLICATIVO)) {
			throw new Exception("Configurazione '"+CONFIG1_APPLICATIVO+"' non trovata");
		}
		if(!contenuto.contains(CONFIG1_APPLICATIVO_VALORE)) {
			throw new Exception("Valore '"+CONFIG1_APPLICATIVO_VALORE+"' per field '"+CONFIG1_APPLICATIVO+"' non trovato");
		}	
		
		if(!contenuto.contains(CONFIG1_SOGGETTO_FRUITORE)) {
			throw new Exception("Configurazione '"+CONFIG1_SOGGETTO_FRUITORE+"' non trovata");
		}
		if(!contenuto.contains(CONFIG1_SOGGETTO_FRUITORE_VALORE)) {
			throw new Exception("Valore '"+CONFIG1_SOGGETTO_FRUITORE_VALORE+"' per field '"+CONFIG1_SOGGETTO_FRUITORE+"' non trovato");
		}	

		if(!contenuto.contains(CONFIG1_SOGGETTO_EROGATORE)) {
			throw new Exception("Configurazione '"+CONFIG1_SOGGETTO_EROGATORE+"' non trovata");
		}
		if(!contenuto.contains(CONFIG1_SOGGETTO_EROGATORE_VALORE)) {
			throw new Exception("Valore '"+CONFIG1_SOGGETTO_EROGATORE_VALORE+"' per field '"+CONFIG1_SOGGETTO_EROGATORE+"' non trovato");
		}	
		
		if(!contenuto.contains(SYSTEM_CONFIG1)) {
			throw new Exception("Configurazione '"+SYSTEM_CONFIG1+"' non trovata");
		}
		if(!contenuto.contains(SYSTEM_CONFIG1_VALORE)) {
			throw new Exception("Valore '"+SYSTEM_CONFIG1_VALORE+"' per field '"+SYSTEM_CONFIG1+"' non trovato");
		}
		
		if(!contenuto.contains(JAVA_CONFIG1)) {
			throw new Exception("Configurazione '"+JAVA_CONFIG1+"' non trovata");
		}
		if(!contenuto.contains(JAVA_CONFIG1_VALORE)) {
			throw new Exception("Valore '"+JAVA_CONFIG1_VALORE+"' per field '"+JAVA_CONFIG1+"' non trovato");
		}
		
		if(!contenuto.contains(ENV_CONFIG1)) {
			throw new Exception("Configurazione '"+ENV_CONFIG1+"' non trovata");
		}
		if(!contenuto.contains(ENV_CONFIG1_VALORE)) {
			throw new Exception("Valore '"+ENV_CONFIG1_VALORE+"' per field '"+ENV_CONFIG1+"' non trovato");
		}

	}
	
	private static void checkResponse(String contenuto) throws Exception {
		// verifiche
		if(!contenuto.contains(DATA_RISPOSTA)) {
			throw new Exception("Nome '"+DATA_RISPOSTA+"' non trovato");
		}
		
		if(!contenuto.contains(HEADER1_RISPOSTA)) {
			throw new Exception("Nome '"+HEADER1_RISPOSTA+"' non trovato");
		}
		if(!contenuto.contains(HEADER1_VALORE_RISPOSTA)) {
			throw new Exception("Valore '"+HEADER1_VALORE_RISPOSTA+"' per field '"+HEADER1_RISPOSTA+"' non trovato");
		}		
		if(!contenuto.contains(HEADER2_RISPOSTA)) {
			throw new Exception("Nome '"+HEADER2_RISPOSTA+"' non trovato");
		}
		if(!contenuto.contains(HEADER2_VALORE_RISPOSTA)) {
			throw new Exception("Valore '"+HEADER2_VALORE_RISPOSTA+"' per field '"+HEADER2_RISPOSTA+"' non trovato");
		}
		if(!contenuto.contains(HEADER2_VALORE_RISPOSTA_POSIZIONE_2)) {
			throw new Exception("Valore '"+HEADER2_VALORE_RISPOSTA_POSIZIONE_2+"' per field '"+HEADER2_RISPOSTA+"' non trovato");
		}
		
		if(!contenuto.contains(PATH1_RISPOSTA)) {
			throw new Exception("Nome '"+PATH1_RISPOSTA+"' non trovato");
		}
		if(!contenuto.contains(PATH1_VALORE_RISPOSTA)) {
			throw new Exception("Valore '"+PATH1_VALORE_RISPOSTA+"' per field '"+PATH1_RISPOSTA+"' non trovato");
		}		
		if(!contenuto.contains(PATH2_RISPOSTA)) {
			throw new Exception("Nome '"+PATH2_RISPOSTA+"' non trovato");
		}
		if(!contenuto.contains(PATH2_VALORE_RISPOSTA)) {
			throw new Exception("Valore '"+PATH2_VALORE_RISPOSTA+"' per field '"+PATH2_RISPOSTA+"' non trovato");
		}
	}
	
	private static void checkInclude(String contenuto, PdDContext pddContext) throws Exception {
		// verifiche
		if(!contenuto.contains(DATA_INCLUDE_1)) {
			throw new Exception("Nome '"+DATA_INCLUDE_1+"' non trovato");
		}
		if(!contenuto.contains(DATA_INCLUDE_2)) {
			throw new Exception("Nome '"+DATA_INCLUDE_2+"' non trovato");
		}
	}
	
	private static void elaborazioniJson(Logger log, Map<String, Object> dynamicMapJsonRequest,
			boolean bufferMessage_readOnly, PdDContext pddContext)throws Exception {
		
		ContentExtractor ce = (ContentExtractor) dynamicMapJsonRequest.get(Costanti.MAP_REQUEST);
		//OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
		List<Object> testOggetti = new ArrayList<Object>();
		testOggetti.add("valoreElementoAggiunto");
		testOggetti.add(23);
		testOggetti.add(23l);
		testOggetti.add(22.34d);
		testOggetti.add(93.49f);
		testOggetti.add(true);
		
		String jsonObject = "{\n"+
		    	"  \"stato\": false,\n"+
		    	"  \"codice\": 320,\n"+
		    	"  \"message\": \"prova\"\n"+
		    	"}";
		testOggetti.add(new JsonStructure(jsonObject, false));

		String jsonArray = "[\n"+
		        "  {\n"+
		    	"    \"stato\": false,\n"+
		    	"    \"message\": \"prova singola\",\n"+
		    	"    \"codiceArray\": 30\n"+
		    	"  } \n"+
		    " ]";
		testOggetti.add(new JsonStructure(jsonArray, true));
		
		String jsonArray2 = "[\n"+
		        "  {\n"+
		    	"    \"stato\": true,\n"+
		    	"    \"message\": \"prova 1\",\n"+
		    	"    \"codiceArray\": 20\n"+
		    	"  }, \n"+
		        "  {\n"+
		    	"    \"stato\": false,\n"+
		    	"    \"message\": \"prova 2\",\n"+
		    	"    \"codiceArray\": 30\n"+
		    	"  } \n"+
		    " ]";
		testOggetti.add(new JsonStructure(jsonArray2, true));
    
		for (Object valoreElemento : testOggetti) {
			
			String nomeElemento = "TEST__elementAggiunto__TEST";
			
			System.out.println("Test aggiunta elemento semplice senza jsonpath '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ...");
			if(valoreElemento instanceof JsonStructure) {
				JsonStructure js = (JsonStructure) valoreElemento;
				if(js.array) {
					ce.addArrayJsonElement(nomeElemento, js.json);
				}
				else {
					ce.addObjectJsonElement(nomeElemento, js.json);
				}
			}
			else {
				ce.addSimpleJsonElement(nomeElemento, valoreElemento);
			}
			
			PatternExtractor pe = PatternExtractor.getJsonPatternExtractor(ce.getContentAsString(), log,
					bufferMessage_readOnly, pddContext);
			if(!pe.match("$."+nomeElemento)) {
				throw new Exception("Elemento '"+nomeElemento+"' non aggiunto");
			}
			String s = pe.read("$."+nomeElemento);
			if(valoreElemento instanceof String) {
				if(!valoreElemento.equals(s)) {
					throw new Exception("Elemento '"+nomeElemento+"' aggiunto con un valore '"+s+"' differente da quello atteso '"+valoreElemento+"'");
				}
			}
			else if(valoreElemento instanceof JsonStructure) {
				JsonStructure js = (JsonStructure) valoreElemento;
				equalsJsonString(nomeElemento,((js.array?"[":"")+s+(js.array?"]":"")),js.json);
			}
			else {
				String valore = valoreElemento.toString();
				if(!valore.equals(s)) {
					throw new Exception("Elemento '"+nomeElemento+"' aggiunto con un valore '"+s+"' differente da quello atteso '"+valore+"'");
				}
			}
			ce.prettyFormatJsonContent();
			System.out.println(ce.getContentAsString());
			System.out.println("Test aggiunta elemento semplice senza jsonpath '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ok");
			
			System.out.println("Test rimozione elemento semplice senza jsonpath '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ...");
			ce.removeJsonField(nomeElemento);
			
			pe = PatternExtractor.getJsonPatternExtractor(ce.getContentAsString(), log,
					bufferMessage_readOnly, pddContext);
			if(pe.match("$."+nomeElemento)) {
				throw new Exception("Elemento '"+nomeElemento+"' non rimosso");
			}
			ce.prettyFormatJsonContent();
			System.out.println(ce.getContentAsString());
			System.out.println("Test rimozione elemento semplice senza jsonpath '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ok");
		}
		
		for (Object valoreElemento : testOggetti) {
			
			String nomeElemento = "TEST__elementAggiunto__TEST";
			
			System.out.println("Test aggiunta elemento semplice '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ...");
			if(valoreElemento instanceof JsonStructure) {
				JsonStructure js = (JsonStructure) valoreElemento;
				if(js.array) {
					ce.addArrayJsonElement("$", nomeElemento, js.json);
				}
				else {
					ce.addObjectJsonElement("$", nomeElemento, js.json);
				}
			}
			else {
				ce.addSimpleJsonElement("$", nomeElemento, valoreElemento);
			}
			
			PatternExtractor pe = PatternExtractor.getJsonPatternExtractor(ce.getContentAsString(), log,
					bufferMessage_readOnly, pddContext);
			if(!pe.match("$."+nomeElemento)) {
				throw new Exception("Elemento '"+nomeElemento+"' non aggiunto");
			}
			String s = pe.read("$."+nomeElemento);
			if(valoreElemento instanceof String) {
				if(!valoreElemento.equals(s)) {
					throw new Exception("Elemento '"+nomeElemento+"' aggiunto con un valore '"+s+"' differente da quello atteso '"+valoreElemento+"'");
				}
			}
			else if(valoreElemento instanceof JsonStructure) {
				JsonStructure js = (JsonStructure) valoreElemento;
				equalsJsonString(nomeElemento,((js.array?"[":"")+s+(js.array?"]":"")),js.json);
			}
			else {
				String valore = valoreElemento.toString();
				if(!valore.equals(s)) {
					throw new Exception("Elemento '"+nomeElemento+"' aggiunto con un valore '"+s+"' differente da quello atteso '"+valore+"'");
				}
			}
			ce.prettyFormatJsonContent();
			System.out.println(ce.getContentAsString());
			System.out.println("Test aggiunta elemento semplice '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ok");
			
			System.out.println("Test rimozione elemento semplice '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ...");
			ce.removeJsonField("$", nomeElemento);
			
			pe = PatternExtractor.getJsonPatternExtractor(ce.getContentAsString(), log,
					bufferMessage_readOnly, pddContext);
			if(pe.match("$."+nomeElemento)) {
				throw new Exception("Elemento '"+nomeElemento+"' non rimosso");
			}
			ce.prettyFormatJsonContent();
			System.out.println(ce.getContentAsString());
			System.out.println("Test rimozione elemento semplice '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ok");
		}

		for (Object valoreElemento : testOggetti) {
			
			String nomeElemento = "TESTOBJ__elementAggiunto__TESTOBJ";
			
			System.out.println("Test aggiunta elemento interno '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ...");
			if(valoreElemento instanceof JsonStructure) {
				JsonStructure js = (JsonStructure) valoreElemento;
				if(js.array) {
					ce.addArrayJsonElement("$.ObjectInternal", nomeElemento, js.json);
				}
				else {
					ce.addObjectJsonElement("$.ObjectInternal", nomeElemento, js.json);
				}
			}
			else {
				ce.addSimpleJsonElement("$.ObjectInternal", nomeElemento, valoreElemento);
			}
			
			PatternExtractor pe = PatternExtractor.getJsonPatternExtractor(ce.getContentAsString(), log,
					bufferMessage_readOnly, pddContext);
			if(!pe.match("$.ObjectInternal."+nomeElemento)) {
				throw new Exception("Elemento '"+nomeElemento+"' non aggiunto");
			}
			String s = pe.read("$.ObjectInternal."+nomeElemento);
			if(valoreElemento instanceof String) {
				if(!valoreElemento.equals(s)) {
					throw new Exception("Elemento '"+nomeElemento+"' aggiunto con un valore '"+s+"' differente da quello atteso '"+valoreElemento+"'");
				}
			}
			else if(valoreElemento instanceof JsonStructure) {
				JsonStructure js = (JsonStructure) valoreElemento;
				equalsJsonString(nomeElemento,((js.array?"[":"")+s+(js.array?"]":"")),js.json);
			}
			else {
				String valore = valoreElemento.toString();
				if(!valore.equals(s)) {
					throw new Exception("Elemento '"+nomeElemento+"' aggiunto con un valore '"+s+"' differente da quello atteso '"+valore+"'");
				}
			}
			ce.prettyFormatJsonContent();
			System.out.println(ce.getContentAsString());
			System.out.println("Test aggiunta elemento interno '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ok");
			
			System.out.println("Test rimozione elemento interno '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ...");
			ce.removeJsonField("$.ObjectInternal", nomeElemento);
			
			pe = PatternExtractor.getJsonPatternExtractor(ce.getContentAsString(), log,
					bufferMessage_readOnly, pddContext);
			if(pe.match("$.ObjectInternal."+nomeElemento)) {
				throw new Exception("Elemento '"+nomeElemento+"' non rimosso");
			}
			ce.prettyFormatJsonContent();
			System.out.println(ce.getContentAsString());
			System.out.println("Test rimozione elemento interno '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ok");
		}
		
		for (Object valoreElemento : testOggetti) {
			
			String nomeElemento = "TESTARRAY__elementAggiunto__TESTARRAY";
			
			System.out.println("Test aggiunta elemento ad array '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ...");
			if(valoreElemento instanceof JsonStructure) {
				JsonStructure js = (JsonStructure) valoreElemento;
				if(js.array) {
					ce.addArrayJsonElement("$.ObjectList[*]", nomeElemento, js.json);
				}
				else {
					ce.addObjectJsonElement("$.ObjectList[*]", nomeElemento, js.json);
				}
			}
			else {
				ce.addSimpleJsonElement("$.ObjectList[*]", nomeElemento, valoreElemento);
			}
			
			PatternExtractor pe = PatternExtractor.getJsonPatternExtractor(ce.getContentAsString(), log,
					bufferMessage_readOnly, pddContext);
			if(!pe.match("$.ObjectList[*]."+nomeElemento)) {
				throw new Exception("Elemento '"+nomeElemento+"' non aggiunto");
			}
			List<String> l = pe.readList("$.ObjectList[*]."+nomeElemento);
			if(l==null || l.isEmpty()) {
				throw new Exception("Elemento '"+nomeElemento+"' non aggiunto");
			}
			for (int i = 0; i < l.size(); i++) {
				String valoreElementoLista = l.get(i);
				if(valoreElemento instanceof String) {
					if(!valoreElemento.equals(valoreElementoLista)) {
						throw new Exception("Elemento '"+nomeElemento+"' aggiunto con un valore '"+valoreElementoLista+"' differente da quello atteso '"+valoreElemento+"'");
					}
				}
				else if(valoreElemento instanceof JsonStructure) {
					JsonStructure js = (JsonStructure) valoreElemento;
					equalsJsonString(nomeElemento,valoreElementoLista,js.json);
				}
				else {
					String valore = valoreElemento.toString();
					if(!valore.equals(valoreElementoLista)) {
						throw new Exception("Elemento '"+nomeElemento+"' aggiunto con un valore '"+valoreElementoLista+"' differente da quello atteso '"+valore+"'");
					}
				}	
			}
			ce.prettyFormatJsonContent();
			System.out.println(ce.getContentAsString());
			System.out.println("Test aggiunta elemento ad array '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ok");
			
			System.out.println("Test rimozione elemento ad array '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ...");
			ce.removeJsonField("$.ObjectList[*]", nomeElemento);
			
			pe = PatternExtractor.getJsonPatternExtractor(ce.getContentAsString(), log,
					bufferMessage_readOnly, pddContext);
			if(pe.match("$.ObjectList[*]."+nomeElemento)) {
				throw new Exception("Elemento '"+nomeElemento+"' non rimosso");
			}
			ce.prettyFormatJsonContent();
			System.out.println(ce.getContentAsString());
			System.out.println("Test rimozione elemento ad array '"+nomeElemento+"' (tipo: "+valoreElemento.getClass().getName()+") alla radice dell'oggetto json ok");
		}
		
				
	}
	private static void equalsJsonString(String nomeElemento, String s1, String s2) throws DynamicException {
		try {
			JSONUtils jsonUtils = JSONUtils.getInstance(true);
			JsonNode nodeS1 = jsonUtils.getAsNode(s1);
			String prettyS1 = jsonUtils.toString(nodeS1);
			JsonNode nodeS2 = jsonUtils.getAsNode(s2);
			String prettyS2 = jsonUtils.toString(nodeS2);
			if(!prettyS1.equals(prettyS2)) {
				throw new Exception("Elemento '"+nomeElemento+"' aggiunto con un valore '"+prettyS1+"' differente da quello atteso '"+prettyS2+"'");
			}
		}catch(Exception e) {
			throw new DynamicException("Operazione fallita (s1:"+s1+") (s2:"+s2+"): "+e.getMessage(),e);
		}
	}
	
}

class JsonStructure{
	public JsonStructure(String json, boolean array) {
		this.json = json;
		this.array = array;
	}
	String json = null;	
	boolean array = false;
}
