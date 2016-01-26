/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.utils.xml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import org.openspcoop2.utils.resources.FileSystemUtilities;

/**	
 * XQueryClientTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLDiffClientTest {

	private final static String XML_ORIGINAL =  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"+
    "<soapenv:Header xmlns:prova=\"http://prova.openspcoop2.org\">\n"+
    "<a:example1 xmlns:a=\"http://www.openspcoop2.org\" soapenv:actor=\"http://www.prova.it\" soapenv:mustUnderstand=\"0\" >prova</a:example1>\n"+
    "<b:example2 xmlns:b=\"http://www.openspcoop2.org\" soapenv:actor=\"http://www.prova.it\" soapenv:mustUnderstand=\"0\" >prova2</b:example2>\n"+
    "</soapenv:Header>\n"+
    "<soapenv:Body>\n"+
    "	<!-- PROVA -->\n"+
    "	<ns1:getQuote xmlns:ns1=\"urn:xmethods-delayed-quotes\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:se=\"http://schemas.xmlsoap.org/soap/envelope/\" se:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"+
    "		<symbol a=\"1\" xsi:type=\"xsd:string\">VALORE</symbol>\n"+
    "\n"+
    "\n"+
    "			<prova2>CIAOCIAO</prova2>\n"+
    "			<altro><si>234</si></altro>\n"+
    "			<!-- Doppia commento -->\n"+
    "	</ns1:getQuote>\n"+
    "	<test/>\n"+
    "	<test2/>\n"+
    "	<tag1>\n"+
    "		<tag2>\n"+
    "		<![CDATA[  Some data ]]>\n"+
    "		</tag2>\n"+
    "	</tag1>\n"+
    "</soapenv:Body>\n"+
    "</soapenv:Envelope>\n";
	
	private final static String XML_COMPARE =  "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"+
    "<soapenv:Header xmlns:prova=\"http://prova.openspcoop2.org\">\n"+
    "<a:example1 xmlns:a=\"http://www.openspcoop2.org\" soapenv:actor=\"http://www.prova.it\" soapenv:mustUnderstand=\"0\" >prova</a:example1>\n"+
    "<b:example2 xmlns:b=\"http://www.openspcoop2.org\" soapenv:actor=\"http://www.prova.it\" soapenv:mustUnderstand=\"0\" >prova2</b:example2>\n"+
    "</soapenv:Header>\n"+
    "<soapenv:Body>\n"+
    "	<!-- PROVA -->\n"+
    "	<ns1:getQuote xmlns:ns1=\"urn:xmethods-delayed-quotes\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:se=\"http://schemas.xmlsoap.org/soap/envelope/\" se:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"+
    "		<symbol xsi:type=\"xsd:string\" a=\"1\" >VALORE</symbol>\n"+
    "\n"+
    "			<prova2>CIAOCIAO</prova2>\n"+
    "			<altro>\n"+
    "				<si>234</si>\n"+
    "			</altro>\n"+
    "\n"+
    "<!-- Doppia commento -->\n"+
    "	</ns1:getQuote>\n"+
    "	<test/>\n"+
    "	<test2/>\n"+
    "		<tag1><tag2>  Some data </tag2></tag1>\n"+
    "</soapenv:Body>\n"+
    "<!-- ALTRO commento -->\n"+
    "</soapenv:Envelope>\n";
	
	private final static String XML_COMPARE_2 =  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"+
    "<soapenv:Header xmlns:prova=\"http://prova.openspcoop2.org\">\n"+
    "<a:example1 xmlns:a=\"http://www.openspcoop2.org\" soapenv:actor=\"http://www.prova.it\" soapenv:mustUnderstand=\"0\" >prova</a:example1>\n"+
    "<b:example2 xmlns:b=\"http://www.openspcoop2.org\" soapenv:actor=\"http://www.prova.it\" soapenv:mustUnderstand=\"0\" >prova2</b:example2>\n"+
    "</soapenv:Header>\n"+
    "<soapenv:Body>\n"+
    "	<!-- PROVA -->\n"+
    "	<ns1:getQuote xmlns:ns1=\"urn:xmethods-delayed-quotes\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:se=\"http://schemas.xmlsoap.org/soap/envelope/\" se:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"+
    "		<symbol xsi:type=\"xsd:string\" a=\"1\" >VALORE</symbol>\n"+
    "\n"+
    "\n"+
    "			<prova2>CIAOCIAO</prova2>\n"+
    "			<altro><si>234</si></altro>\n"+
    "			<!-- Doppia commento -->\n"+
    "	</ns1:getQuote>\n"+
    "	<test/>\n"+
    "	<test2/>\n"+
    "	<tag1>\n"+
    "		<tag2>\n"+
    "		  Some data \n"+
    "		</tag2>\n"+
    "	</tag1>\n"+
    "</soapenv:Body><!-- ALTRO commento -->\n"+
    "</soapenv:Envelope>\n";
	
	
	
	public static void main(String[] args) throws Exception {
		
		File originalFile = File.createTempFile("src", ".xml");
		FileSystemUtilities.writeFile(originalFile, XML_ORIGINAL.getBytes());
		File compareFile = File.createTempFile("test", ".compare");
		FileSystemUtilities.writeFile(compareFile, XML_COMPARE.getBytes());
		
		XMLDiffOptions xmlDiffOptions = new XMLDiffOptions();
		XMLDiff xmlDiffEngine = new XMLDiff();
		
		xmlDiffEngine.initialize(XMLDiffImplType.XML_UNIT, xmlDiffOptions);
		test(xmlDiffEngine, originalFile, compareFile, true);
		
		
		File compare2File = File.createTempFile("test", ".compare2");
		FileSystemUtilities.writeFile(compare2File, XML_COMPARE_2.getBytes());
		
		// NOTA L'implementazione DOM DOCUMENT DIFF non gestisce in maniera ottimale gli '\n' e gli spazi.
		// Se si usa il file XML_COMPARE che è identico "in xml" al XML_ORIGINAL ma contiene diversi '\n' ...
		// l'implementazione DOM DOCUMENT DIFF non funziona bene.
		// Anche invece usanto il file XML_COMPARE_2 che contiene li stessi '\n' per alcuni tipi di oggetto comunque non funziona.
		// Conclusioni: USARE XML_UNIT come implementazione
		
		xmlDiffEngine.initialize(XMLDiffImplType.ORG_W3C_DOM_DOCUMENT, xmlDiffOptions);
		test(xmlDiffEngine, originalFile, compare2File, false);

	}
	
	private static void test(XMLDiff xmlDiffEngine,File originalFile,File compareFile, boolean printDiff) throws Exception{
		
		/* ****** TEST SRC OK ****** */
		
		System.out.println("\n\n\n*************** TESTSUITE **********************");
		
				
		System.out.println("========= AS DOCUMENT / DOCUMENT ==========");
		
		System.out.println(xmlDiffEngine.diff(xmlDiffEngine.getXMLUtils().newDocument(originalFile), xmlDiffEngine.getXMLUtils().newDocument(compareFile)));
		
		System.out.println("======== AS ELEMENT / ELEMENT ===========");
		
		System.out.println(xmlDiffEngine.diff(xmlDiffEngine.getXMLUtils().newElement(originalFile), xmlDiffEngine.getXMLUtils().newElement(compareFile)));
		
		System.out.println("======== AS NODE / NODE ===========");
		
		System.out.println(xmlDiffEngine.diff(xmlDiffEngine.getXMLUtils().newDocument(originalFile).getDocumentElement().getLastChild(), 
				xmlDiffEngine.getXMLUtils().newDocument(compareFile).getDocumentElement().getLastChild()));
		
		System.out.println("======== AS FILE / FILE ===========");
		
		System.out.println(xmlDiffEngine.diff(originalFile, compareFile));
		
		System.out.println("======== AS INPUT STREAM  / INPUT STREAM ===========");
		
		System.out.println(xmlDiffEngine.diff(new FileInputStream(originalFile), new FileInputStream(compareFile)));
		
		System.out.println("======== AS FILE / INPUT STREAM ===========");
		
		System.out.println(xmlDiffEngine.diff(originalFile, new FileInputStream(compareFile)));	
		
		System.out.println("======== AS INPUT STREAM / FILE ===========");
		
		System.out.println(xmlDiffEngine.diff(new FileInputStream(originalFile), compareFile));
		
		System.out.println("======== AS READER / READER ===========");
		
		System.out.println(xmlDiffEngine.diff(new FileReader(originalFile), new FileReader(compareFile) ));
		
		System.out.println("======== AS STRING / STRING ===========");
		
		System.out.println(xmlDiffEngine.diff(FileSystemUtilities.readFile(originalFile), FileSystemUtilities.readFile(compareFile) ));
		
		System.out.println("======== AS READER / STRING ===========");
		
		System.out.println(xmlDiffEngine.diff(new FileReader(originalFile), FileSystemUtilities.readFile(compareFile) ));
		
		System.out.println("======== AS STRING / READER ===========");
		
		System.out.println(xmlDiffEngine.diff(FileSystemUtilities.readFile(originalFile), new FileReader(compareFile) ));
		
		System.out.println("======== AS STRING / DOCUMENT ===========");
		
		System.out.println(xmlDiffEngine.diff(FileSystemUtilities.readFile(originalFile), xmlDiffEngine.getXMLUtils().newDocument(compareFile)) );
		
		System.out.println("======== AS INPUT STREAM / NODE ===========");
		
		System.out.println(xmlDiffEngine.diff(new FileInputStream(originalFile),
				xmlDiffEngine.getXMLUtils().newDocument(compareFile).getDocumentElement().getLastChild()));
		
		
		
		/* ****** TEST CASI ERRATI ****** */
		
		System.out.println("\n\n\n*************** TESTSUITE CASI ERRATI **********************");
		
		File compareErrato = File.createTempFile("compareErrato", ".xml");
		FileSystemUtilities.writeFile(compareErrato, (XML_COMPARE.replace(" a=", " b=\"3\" a=")).getBytes());
		
		System.out.println(xmlDiffEngine.diff(originalFile, compareErrato));
		System.out.println(xmlDiffEngine.getDifferenceDetails());
		
	}
	
}
