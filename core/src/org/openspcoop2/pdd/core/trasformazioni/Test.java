/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.UniversallyUniqueIdentifierGenerator;
import org.slf4j.Logger;
import org.w3c.dom.Element;

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
	
	private static final String HEADER1 = "Header1";
	private static final String HEADER1_VALORE = "ValoreHeader1";
	private static final String HEADER2 = "Header2";
	private static final String HEADER2_VALORE = "ValoreHeader2";
	
	private static final String HEADER1_RISPOSTA = "ResponseHeader1";
	private static final String HEADER1_VALORE_RISPOSTA = "ResponseValoreHeader1";
	private static final String HEADER2_RISPOSTA = "ResponseHeader2";
	private static final String HEADER2_VALORE_RISPOSTA = "ResponseValoreHeader2";
	
	private static final String QUERY1 = "Query1";
	private static final String QUERY1_VALORE = "QueryValore1";
	private static final String QUERY2 = "Query2";
	private static final String QUERY2_VALORE = "QueryValore2";
	private static final String QUERY3 = "Query3";
	private static final String QUERY3_VALORE = "QueryValore3";
	private static final String QUERY4 = "Query4";
	private static final String QUERY4_VALORE = "QueryValore4";
	
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
			"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body>";
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
	            "\"List\": [ \"Ford\", \"BMW\", \"Fiat\" ]\n"+
			"}";
	private static final String JSON_REQUEST = JSON_PREFIX + JSON_REQUEST_CONTENT + JSON_END;
	private static final String JSON_RESPONSE = JSON_PREFIX + JSON_RESPONSE_CONTENT + JSON_END;
			
	
	
	// **** Template GovWay *****
	
	private static final String JSON_TEMPLATE_BODY = 	   
			"\""+DATA+"\": \"${date:yyyyMMdd_HHmmssSSS}\",\n"+
			"\""+HEADER1+"\": \"${header:"+HEADER1+"}\",\n"+
			"\""+HEADER2+"\": \"${header:"+HEADER2+"}\",\n"+
			"\""+QUERY1+"\": \"${query:"+QUERY1+"}\",\n"+
			"\""+QUERY2+"\": \"${query:"+QUERY2+"}\",\n"+
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
            "\""+BUSTA_PROPERTY+"\": \"${property:"+BUSTA_PROPERTY+"}\",\n";
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
			"<"+PATH1+">"+"${jsonpath:$."+PATH1+"}"+"</"+PATH1+">\n"+
			"<"+PATH2+">"+"${jsonPath:$."+PATH2+"}"+"</"+PATH2+">\n"+
			"<"+TRANSACTION_ID+">"+"${transaction:id}"+"</"+TRANSACTION_ID+">\n"+
			"<"+QUERY3+">"+"${urlRegExp:.+"+QUERY3+"=([^&]*).*}"+"</"+QUERY3+">\n"+
			"<"+QUERY4+">"+"${urlregexp:.+"+QUERY4+"=([^&]*).*}"+"</"+QUERY4+">\n"+
			"<"+BUSTA+">"+"${busta:mittente}"+"</"+BUSTA+">\n"+
			"<"+BUSTA_PROPERTY+">"+"${property:"+BUSTA_PROPERTY+"}"+"</"+BUSTA_PROPERTY+">\n";
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
			"\""+HEADER1+"\": \"${header[\""+HEADER1+"\"]}\",\n"+
			"\""+HEADER2+"\": \"${header[\""+HEADER2+"\"]}\",\n"+
			"\""+QUERY1+"\": \"${query[\""+QUERY1+"\"]}\",\n"+
			"\""+QUERY2+"\": \"${query[\""+QUERY2+"\"]}\",\n"+
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
            "\""+BUSTA_PROPERTY+"\": \"${property[\""+BUSTA_PROPERTY+"\"]}\",\n";
	private static final String JSON_TEMPLATE_FREEMARKER_BODY_RESPONSE = 	   
			"\""+DATA_RISPOSTA+"\": \"${dateResponse?string('dd.MM.yyyy HH:mm:ss')}\",\n"+
			"\""+HEADER1_RISPOSTA+"\": \"${headerResponse[\""+HEADER1_RISPOSTA+"\"]}\",\n"+
			"\""+HEADER2_RISPOSTA+"\": \"${headerResponse[\""+HEADER2_RISPOSTA+"\"]}\",\n"+
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
	
	private static final String XML_TEMPLATE_FREEMARKER_BODY =
			"<"+DATA+">"+"${date?string('dd.MM.yyyy HH:mm:ss')}"+"</"+DATA+">\n"+
			"<"+HEADER1+">"+"${header[\""+HEADER1+"\"]}"+"</"+HEADER1+">\n"+
			"<"+HEADER2+">"+"${header[\""+HEADER2+"\"]}"+"</"+HEADER2+">\n"+
			"<"+QUERY1+">"+"${query[\""+QUERY1+"\"]}"+"</"+QUERY1+">\n"+
			"<"+QUERY2+">"+"${query[\""+QUERY2+"\"]}"+"</"+QUERY2+">\n"+
			"<"+PATH1+">"+"${jsonpath.read(\"$."+PATH1+"\")}"+"</"+PATH1+">\n"+
			"<"+PATH2+">"+"${jsonPath.read(\"$."+PATH2+"\")}"+"</"+PATH2+">\n"+
			"<"+PATH1+"boolean>"+"${jsonpath.match(\"$."+PATH1+"\")?string('yes', 'no')}"+"</"+PATH1+"boolean>\n"+
			"<"+PATH2+"boolean>"+"${jsonPath.match(\"$."+PATH2+"\")?string('yes', 'no')}"+"</"+PATH2+"boolean>\n"+
			"<#list jsonPath.readList(\"$."+PATH1+"\") as item>${item}</#list>\n"+
			"<"+TRANSACTION_ID+">"+"${transactionId}"+"</"+TRANSACTION_ID+">\n"+
			"<"+QUERY3+">"+"${urlRegExp.read(\".+"+QUERY3+"=([^&]*).*\")}"+"</"+QUERY3+">\n"+
			"<"+QUERY4+">"+"${urlregexp.read(\".+"+QUERY4+"=([^&]*).*\")}"+"</"+QUERY4+">\n"+
			"<"+BUSTA+">"+"${busta.getMittente()}"+"</"+BUSTA+">\n"+
			"<"+BUSTA_PROPERTY+">"+"${property[\""+BUSTA_PROPERTY+"\"]}"+"</"+BUSTA_PROPERTY+">\n";
	private static final String XML_TEMPLATE_FREEMARKER_BODY_RESPONSE =
			"<"+DATA_RISPOSTA+">"+"${dateResponse?string('dd.MM.yyyy HH:mm:ss')}"+"</"+DATA_RISPOSTA+">\n"+
			"<"+HEADER1_RISPOSTA+">"+"${headerResponse[\""+HEADER1_RISPOSTA+"\"]}"+"</"+HEADER1_RISPOSTA+">\n"+
			"<"+HEADER2_RISPOSTA+">"+"${headerResponse[\""+HEADER2_RISPOSTA+"\"]}"+"</"+HEADER2_RISPOSTA+">\n"+
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
	
	
	// **** Template Velocity *****
	
	private static final String JSON_TEMPLATE_VELOCITY_BODY = 	   
			"\""+DATA+"\": \"$date\",\n"+
			"\""+HEADER1+"\": \"${header[\""+HEADER1+"\"]}\",\n"+
			"\""+HEADER2+"\": \"${header[\""+HEADER2+"\"]}\",\n"+
			"\""+QUERY1+"\": \"${query[\""+QUERY1+"\"]}\",\n"+
			"\""+QUERY2+"\": \"${query[\""+QUERY2+"\"]}\",\n"+
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
            "\""+BUSTA_PROPERTY+"\": \"${property[\""+BUSTA_PROPERTY+"\"]}\",\n";
	private static final String JSON_TEMPLATE_VELOCITY_BODY_RESPONSE = 	   
			"\""+DATA_RISPOSTA+"\": \"${dateResponse}\",\n"+
			"\""+HEADER1_RISPOSTA+"\": \"${headerResponse[\""+HEADER1_RISPOSTA+"\"]}\",\n"+
			"\""+HEADER2_RISPOSTA+"\": \"${headerResponse[\""+HEADER2_RISPOSTA+"\"]}\",\n"+
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
	
	private static final String XML_TEMPLATE_VELOCITY_BODY =
			"<"+DATA+">"+"${date}"+"</"+DATA+">\n"+
			"<"+HEADER1+">"+"${header[\""+HEADER1+"\"]}"+"</"+HEADER1+">\n"+
			"<"+HEADER2+">"+"${header[\""+HEADER2+"\"]}"+"</"+HEADER2+">\n"+
			"<"+QUERY1+">"+"${query[\""+QUERY1+"\"]}"+"</"+QUERY1+">\n"+
			"<"+QUERY2+">"+"${query[\""+QUERY2+"\"]}"+"</"+QUERY2+">\n"+
			"<"+PATH1+">"+"${jsonpath.read(\"$."+PATH1+"\")}"+"</"+PATH1+">\n"+
			"<"+PATH2+">"+"${jsonPath.read(\"$."+PATH2+"\")}"+"</"+PATH2+">\n"+
			"<"+PATH1+"boolean>"+"${jsonpath.match(\"$."+PATH1+"\")}"+"</"+PATH1+"boolean>\n"+
			"<"+PATH2+"boolean>"+"${jsonPath.match(\"$."+PATH2+"\")}"+"</"+PATH2+"boolean>\n"+
			"<#list jsonPath.readList(\"$."+PATH1+"\") as item>${item}</#list>\n"+
			"<"+TRANSACTION_ID+">"+"${transactionId}"+"</"+TRANSACTION_ID+">\n"+
			"<"+QUERY3+">"+"${urlRegExp.read(\".+"+QUERY3+"=([^&]*).*\")}"+"</"+QUERY3+">\n"+
			"<"+QUERY4+">"+"${urlregexp.read(\".+"+QUERY4+"=([^&]*).*\")}"+"</"+QUERY4+">\n"+
			"<"+BUSTA+">"+"${busta.getMittente()}"+"</"+BUSTA+">\n"+
			"<"+BUSTA_PROPERTY+">"+"${property[\""+BUSTA_PROPERTY+"\"]}"+"</"+BUSTA_PROPERTY+">\n";
	private static final String XML_TEMPLATE_VELOCITY_BODY_RESPONSE =
			"<"+DATA_RISPOSTA+">"+"${dateResponse}"+"</"+DATA_RISPOSTA+">\n"+
			"<"+HEADER1_RISPOSTA+">"+"${headerResponse[\""+HEADER1_RISPOSTA+"\"]}"+"</"+HEADER1_RISPOSTA+">\n"+
			"<"+HEADER2_RISPOSTA+">"+"${headerResponse[\""+HEADER2_RISPOSTA+"\"]}"+"</"+HEADER2_RISPOSTA+">\n"+
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
	
	
	// **** Template XSLT *****
	
	private static final String PREFIX_ORIGINALE = "orig";
	private static final String PREFIX_TO = "modificato";
	
	private static final String XSLT_PREFIX_REPLACE_ATTRIBUTE_ELEMENT = 
	"  <xsl:template match=\"*\">\n"+
	"    <xsl:element name=\""+PREFIX_TO+":{local-name()}\" namespace=\"https://govway.org\">\n"+
	"      <xsl:apply-templates select=\"@* | node()\"/>\n"+
	"    </xsl:element>\n"+
	"  </xsl:template>\n"+
	"\n"+
	"  <xsl:template match=\"text()\">\n"+
	"    <xsl:element name=\""+PREFIX_TO+":{local-name(../../*)}\" namespace=\"https://govway.org\">\n"+
	"     <xsl:value-of select=\".\"/>\n"+
	"   </xsl:element>\n"+
	" </xsl:template>\n"+
	"\n"+
	" <xsl:template match=\"@*\">\n"+
	"   <xsl:element name=\""+PREFIX_TO+":{local-name()}\" namespace=\"https://govway.org\">\n"+
	"     <xsl:value-of select=\".\"/>\n"+
	"   </xsl:element>\n"+
	" </xsl:template>\n";
	
	private static final String XSLT_PREFIX_REPLACE_PREFIX_ONLY_NODE = 
	"<xsl:template match=\""+PREFIX_ORIGINALE+":*\" xmlns:"+PREFIX_ORIGINALE+"=\"https://govway.org\">\n"+
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
	"<"+PREFIX_ORIGINALE+":Request xmlns:"+PREFIX_ORIGINALE+"=\"https://govway.org\">\n"+
	"   <"+PREFIX_ORIGINALE+":Body>\n"+
	"      <"+PREFIX_ORIGINALE+":Fields>\n"+
	"         <"+PREFIX_ORIGINALE+":Field1 attribute1=\"valore primo attributo\">valore primo field</"+PREFIX_ORIGINALE+":Field1>\n"+
	"         <"+PREFIX_ORIGINALE+":Field2 attribute2=\"valore secondo attributo\"/>\n"+
	"         <"+PREFIX_ORIGINALE+":Field3>valore terzo field</"+PREFIX_ORIGINALE+":Field3>\n"+
	"      </"+PREFIX_ORIGINALE+":Fields>\n"+
	"   </"+PREFIX_ORIGINALE+":Body>\n"+
	"</"+PREFIX_ORIGINALE+":Request>\n";
	
	
	
	public static void main(String [] args) throws Exception{
		
		TipoTrasformazione tipoTest = null;
		if(args!=null && args.length>0) {
			tipoTest = TipoTrasformazione.valueOf(args[0]);
		}
		
		
		
		// Preparo Contesto
		
		Logger log = LoggerWrapperFactory.getLogger(Test.class);
		
		PdDContext pddContext = new PdDContext();
		UniversallyUniqueIdentifierGenerator uuidGenerator = new UniversallyUniqueIdentifierGenerator();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, uuidGenerator.newID().toString());
		
		String urlInvocazione =  "/govway/out/ENTE/Erogatore/Servizio/v1/azione/test?"+
				QUERY1+"="+QUERY1_VALORE+"&"+
				QUERY2+"="+QUERY2_VALORE+"&"+
				QUERY3+"="+QUERY3_VALORE+"&"+
				QUERY4+"="+QUERY4_VALORE;
		
		Busta busta = new Busta("trasparente");
		busta.setMittente(BUSTA_MITTENTE_VALORE);
		busta.addProperty(BUSTA_PROPERTY, BUSTA_PROPERTY_VALORE);
		
		Properties parametriTrasporto = new Properties();
		parametriTrasporto.put(HEADER1, HEADER1_VALORE);
		parametriTrasporto.put(HEADER2, HEADER2_VALORE);
		
		Properties parametriTrasportoRisposta = new Properties();
		parametriTrasportoRisposta.put(HEADER1_RISPOSTA, HEADER1_VALORE_RISPOSTA);
		parametriTrasportoRisposta.put(HEADER2_RISPOSTA, HEADER2_VALORE_RISPOSTA);
		
		Properties parametriUrl = new Properties();
		parametriUrl.put(QUERY1, QUERY1_VALORE);
		parametriUrl.put(QUERY2, QUERY2_VALORE);
		parametriUrl.put(QUERY3, QUERY3_VALORE);
		parametriUrl.put(QUERY4, QUERY4_VALORE);
		
		Element elementRequest = XMLUtils.getInstance().newElement(XML_REQUEST.getBytes());
		Element elementResponse = XMLUtils.getInstance().newElement(XML_RESPONSE.getBytes());
		
		Map<String, Object> dynamicMapXmlRequest = new Hashtable<String, Object>();
		GestoreTrasformazioniUtilities.fillDynamicMapRequest(log, dynamicMapXmlRequest, pddContext, urlInvocazione,
				elementRequest, null, 
				busta, parametriTrasporto, parametriUrl);
		
		Map<String, Object> dynamicMapXmlResponse = new Hashtable<String, Object>();
		GestoreTrasformazioniUtilities.fillDynamicMapResponse(log, dynamicMapXmlResponse, dynamicMapXmlRequest, pddContext, 
				elementResponse, null, 
				busta, parametriTrasportoRisposta);
		
		Map<String, Object> dynamicMapJsonRequest = new Hashtable<String, Object>();
		GestoreTrasformazioniUtilities.fillDynamicMapRequest(log, dynamicMapJsonRequest, pddContext, urlInvocazione,
				null, JSON_REQUEST, 
				busta, parametriTrasporto, parametriUrl);
		
		Map<String, Object> dynamicMapJsonResponse = new Hashtable<String, Object>();
		GestoreTrasformazioniUtilities.fillDynamicMapResponse(log, dynamicMapJsonResponse, dynamicMapJsonRequest, pddContext, 
				null, JSON_RESPONSE,  
				busta, parametriTrasportoRisposta);
		
		if(tipoTest==null || TipoTrasformazione.TEMPLATE.equals(tipoTest)) {
		
			test(log, (tipoTest!=null ? tipoTest : TipoTrasformazione.TEMPLATE) , "xml", pddContext, 
					dynamicMapXmlRequest, null, JSON_TEMPLATE_REQUEST.getBytes(), 
					dynamicMapXmlResponse, null, JSON_TEMPLATE_RESPONSE.getBytes());
			
			test(log, (tipoTest!=null ? tipoTest : TipoTrasformazione.TEMPLATE) , "json", pddContext, 
					dynamicMapJsonRequest, null, XML_TEMPLATE_REQUEST.getBytes(), 
					dynamicMapJsonResponse, null,  XML_TEMPLATE_RESPONSE.getBytes());
			
		}
		
		if(tipoTest==null || TipoTrasformazione.FREEMARKER_TEMPLATE.equals(tipoTest)) {
			
			test(log, (tipoTest!=null ? tipoTest : TipoTrasformazione.FREEMARKER_TEMPLATE) , "xml", pddContext, 
					dynamicMapXmlRequest, null,  JSON_TEMPLATE_FREEMARKER_REQUEST.getBytes(), 
					dynamicMapXmlResponse, null,  JSON_TEMPLATE_FREEMARKER_RESPONSE.getBytes());
			
			test(log, (tipoTest!=null ? tipoTest : TipoTrasformazione.FREEMARKER_TEMPLATE) , "json", pddContext, 
					dynamicMapJsonRequest, null,  XML_TEMPLATE_FREEMARKER_REQUEST.getBytes(), 
					dynamicMapJsonResponse, null,  XML_TEMPLATE_FREEMARKER_RESPONSE.getBytes());
			
		}
		
		if(tipoTest==null || TipoTrasformazione.VELOCITY_TEMPLATE.equals(tipoTest)) {
			
			test(log, (tipoTest!=null ? tipoTest : TipoTrasformazione.VELOCITY_TEMPLATE) , "xml", pddContext, 
					dynamicMapXmlRequest, null,  JSON_TEMPLATE_VELOCITY_REQUEST.getBytes(), 
					dynamicMapXmlResponse, null,  JSON_TEMPLATE_VELOCITY_RESPONSE.getBytes());
			
			test(log, (tipoTest!=null ? tipoTest : TipoTrasformazione.VELOCITY_TEMPLATE) , "json", pddContext, 
					dynamicMapJsonRequest, null,  XML_TEMPLATE_VELOCITY_REQUEST.getBytes(), 
					dynamicMapJsonResponse, null,  XML_TEMPLATE_VELOCITY_RESPONSE.getBytes());
			
		}
		
		if(tipoTest==null || TipoTrasformazione.XSLT.equals(tipoTest)) {
			
			elementRequest = XMLUtils.getInstance().newElement(XSLT_XML_INPUT.getBytes());
			elementResponse = XMLUtils.getInstance().newElement(XSLT_XML_INPUT.getBytes());
			
			test(log, (tipoTest!=null ? tipoTest : TipoTrasformazione.XSLT) , "xml", pddContext, 
					null, elementRequest,  XSLT_PREFIX_REPLACE_ALL.getBytes(), 
					null, elementResponse,  XSLT_PREFIX_REPLACE_ONLY_PREFIX.getBytes());
			
		}
		
	}
	
	private static void test(Logger log, TipoTrasformazione tipoTest, String prefix,
			PdDContext pddContext,
			Map<String, Object> dynamicMapRequest, Element elementRequest, byte[] templateRequest, 
			Map<String, Object> dynamicMapResponse, Element elementResponse, byte[] templateResponse) throws Exception {
		
		System.out.println("Test ["+tipoTest+"-"+prefix+"] in corso ...");
		
		System.out.println("\trequest ...");
		RisultatoTrasformazioneContenuto risultato = null;
		try {
			risultato = GestoreTrasformazioniUtilities.trasformazioneContenuto(log, 
					tipoTest.getValue(), templateRequest, "richiesta", dynamicMapRequest, null, elementRequest, pddContext);
		}catch(Throwable e) {
			System.out.println("\tTemplate:\n "+new String(templateRequest));
			Utilities.sleep(1000);
			throw e;
		}
		String contenuto = risultato.getContenutoAsString();
		if(TipoTrasformazione.TEMPLATE.equals(tipoTest) ||
				TipoTrasformazione.FREEMARKER_TEMPLATE.equals(tipoTest)  ||
				TipoTrasformazione.VELOCITY_TEMPLATE.equals(tipoTest) ) {
			try {
				checkRequest(contenuto, pddContext);
			}catch(Throwable e) {
				System.out.println("\tTemplate:\n "+new String(templateRequest));
				System.out.println("\tOttenuto:\n "+contenuto);
				Utilities.sleep(1000);
				throw e;
			}
		}
		else {
			if(contenuto.contains(PREFIX_ORIGINALE)) {
				
				System.out.println("Template:\n "+new String(templateRequest));
				System.out.println("PRIMA: "+XMLUtils.getInstance().toString(elementRequest));
				System.out.println("DOPO: "+contenuto);

				throw new Exception("Trovato '"+PREFIX_ORIGINALE+"' non atteso");
			}
		}
		System.out.println("\trequest ok");
		
		System.out.println("\tresponse ...");
		risultato = null;
		try {
			risultato = GestoreTrasformazioniUtilities.trasformazioneContenuto(log, 
					tipoTest.getValue(), templateResponse, "risposta", dynamicMapResponse, null, elementResponse, pddContext);
		}catch(Throwable e) {
			System.out.println("\tTemplate:\n "+new String(templateResponse));
			Utilities.sleep(1000);
			throw e;
		}
		contenuto = risultato.getContenutoAsString();
		if(TipoTrasformazione.TEMPLATE.equals(tipoTest) ||
				TipoTrasformazione.FREEMARKER_TEMPLATE.equals(tipoTest)  ||
				TipoTrasformazione.VELOCITY_TEMPLATE.equals(tipoTest)) {
			try{
				checkRequest(contenuto, pddContext);
				checkResponse(contenuto);
			}catch(Throwable e) {
				System.out.println("\tTemplate:\n "+new String(templateResponse));
				System.out.println("\tOttenuto:\n "+contenuto);
				Utilities.sleep(1000);
				throw e;
			}
		}
		else {
			if(contenuto.contains(PREFIX_ORIGINALE)) {
				
				System.out.println("Template:\n "+new String(templateResponse));
				System.out.println("PRIMA: "+XMLUtils.getInstance().toString(elementResponse));
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
}