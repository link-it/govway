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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XQueryExpressionEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**	
 * XQueryClientTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XQueryClientTest {

	private final static String SOAP_XML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
    "        <soapenv:Header/>\n"+
    "        <soapenv:Body>\n"+
    "            <sam:searchResponse xmlns:sam=\"http://www.prova.org\">\n"+
    "                <sam:searchResponse>\n"+
    "                    <sam:item>\n"+
    "                        <id>1</id>\n"+
    "                        <description>One handy protocol droid</description>\n"+
    "                        <price>1</price>\n"+
    "                    </sam:item>\n"+
    "                    <sam:item>\n"+
    "                        <id>2</id>\n"+
    "                        <description>Item nr 2</description>\n"+
    "                        <price>2</price>\n"+
    "                    </sam:item>\n"+
    "                    <sam:item>\n"+
    "                        <id>3</id>\n"+
    "                        <description>Item nr 3</description>\n"+
    "                       <price>3</price>\n"+
    "                    </sam:item>\n"+
    "                    <sam:item>\n"+
    "                        <id>4</id>\n"+
    "                        <description>Item nr 4</description>\n"+
    "                        <price>4</price>\n"+
    "                    </sam:item>\n"+
    "                    <sam:item>\n"+
    "                        <id>5</id>\n"+
    "                        <description>Item nr 5</description>\n"+
    "                        <price>5</price>\n"+
    "                    </sam:item>\n"+
    "                </sam:searchResponse>\n"+
    "            </sam:searchResponse>\n"+
    "        </soapenv:Body>\n"+
    "    </soapenv:Envelope>\n";
	
	private final static String XQUERY = "declare namespace sam=\"http://www.prova.org\";\n"+
	"	<Result>\n"+
	"	{\n"+
	"	for $z in //sam:item\n"+
	"	order by $z/id\n"+
	"	return <price>{data($z/price/text())}</price>\n"+
	"	}\n"+
	"	</Result>\n";
	
	private final static String XQUERY_WITHOUT_CODE_EXTERNAL = "declare namespace sam=\"http://www.prova.org\";\n"+
	"	for $z in //sam:item\n"+
	"	order by $z/id\n"+
	"	return <price>{data($z/price/text())}</price>\n";
	
	
	public static void main(String[] args) throws Exception {
		
		File sourceFile = File.createTempFile("src", ".xml");
		FileSystemUtilities.writeFile(sourceFile, SOAP_XML.getBytes());
		File xqueryFile = File.createTempFile("test", ".xquery");
		FileSystemUtilities.writeFile(xqueryFile, XQUERY.getBytes());
		
		XQueryExpressionEngine xqueryEngine = new XQueryExpressionEngine();
		
		
		/* ****** TEST SRC OK ****** */
		
		System.out.println("\n\n\n*************** TESTSUITE SRC **********************");
		
		System.out.println("======== SRC AS FILE ===========");
		
		System.out.println(xqueryEngine.evaluate(sourceFile, xqueryFile, true));
				
		System.out.println("========= SRC AS DOCUMENT ==========");
		
		System.out.println(xqueryEngine.evaluate(xqueryEngine.getXMLUtils().newDocument(sourceFile), xqueryFile, true));
		
		System.out.println("======== SRC AS ELEMENT ===========");
		
		System.out.println(xqueryEngine.evaluate(xqueryEngine.getXMLUtils().newElement(sourceFile), xqueryFile, true));
		
		System.out.println("======== SRC AS NODE ===========");
		
		System.out.println(xqueryEngine.evaluate(xqueryEngine.getXMLUtils().newDocument(sourceFile).getDocumentElement().getLastChild(), xqueryFile, true));
		
		System.out.println("======== SRC AS INPUT STREAM ===========");
		
		System.out.println(xqueryEngine.evaluate(new FileInputStream(sourceFile), xqueryFile, true));
		
		System.out.println("======== SRC AS READER ===========");
		
		System.out.println(xqueryEngine.evaluate(new FileReader(sourceFile), xqueryFile, true));
		
		System.out.println("======== SRC AS STRING ===========");
		
		System.out.println(xqueryEngine.evaluate(FileSystemUtilities.readFile(sourceFile), xqueryFile, true));
		
		
		
		/* ****** TEST XQUERY OK ****** */
		
		System.out.println("\n\n\n*************** TESTSUITE XQUERY **********************");
		
		System.out.println("======== QUERY AS FILE ===========");
		
		System.out.println(xqueryEngine.evaluate(sourceFile, xqueryFile, true));
				
		System.out.println("========= QUERY AS INPUT STREAM ==========");
		
		System.out.println(xqueryEngine.evaluate(sourceFile, new FileInputStream(xqueryFile), true));
		
		System.out.println("========= QUERY AS READER ==========");
		
		System.out.println(xqueryEngine.evaluate(sourceFile, new FileReader(xqueryFile), true));
		
		System.out.println("========= QUERY AS STRING ==========");
		
		System.out.println(xqueryEngine.evaluate(sourceFile, FileSystemUtilities.readFile(xqueryFile), true));
		
		
		
		/* ****** TEST DESTINATION OK ****** */
		
		System.out.println("\n\n\n*************** TESTSUITE DESTINATION **********************");
		
		System.out.println("======== DESTINATION AS FILE ===========");
		
		File tmp = File.createTempFile("prova", ".tmp");
		xqueryEngine.evaluate(sourceFile, xqueryFile, tmp, true);
		System.out.println(FileSystemUtilities.readFile(tmp));
		tmp.delete();
		
		System.out.println("======== DESTINATION AS OUTPUT STREAM ===========");
		
		tmp = File.createTempFile("prova", ".tmp");
		FileOutputStream fout = new FileOutputStream(tmp);
		xqueryEngine.evaluate(sourceFile, xqueryFile, fout, true);
		fout.flush();
		fout.close();
		System.out.println(FileSystemUtilities.readFile(tmp));
		tmp.delete();
		
		System.out.println("======== DESTINATION AS WRITER ===========");
		
		tmp = File.createTempFile("prova", ".tmp");
		FileWriter fw = new FileWriter(tmp);
		xqueryEngine.evaluate(sourceFile, xqueryFile, fw, true);
		fw.flush();
		fw.close();
		System.out.println(FileSystemUtilities.readFile(tmp));
		tmp.delete();
		
		System.out.println("======== DESTINATION AS DOCUMENT ===========");
		
		Document dTmp = xqueryEngine.getXMLUtils().newDocument();
		xqueryEngine.evaluate(sourceFile, xqueryFile, dTmp, true);
		System.out.println(PrettyPrintXMLUtils.prettyPrintWithTrAX(dTmp,true));
		
		System.out.println("======== DESTINATION AS ELEMENT ===========");
		
		dTmp = xqueryEngine.getXMLUtils().newDocument();
		Element eTmp = dTmp.createElement("prova");
		xqueryEngine.evaluate(sourceFile, xqueryFile, eTmp, true);
		System.out.println(PrettyPrintXMLUtils.prettyPrintWithTrAX(eTmp,true));
		
		System.out.println("======== DESTINATION AS NODE ===========");
		
		dTmp = xqueryEngine.getXMLUtils().newDocument();
		eTmp = dTmp.createElement("prova");
		eTmp.appendChild(dTmp.createElement("provaInterno"));
		Node nTmp = eTmp.getFirstChild();
		xqueryEngine.evaluate(sourceFile, xqueryFile, nTmp, true);
		System.out.println(PrettyPrintXMLUtils.prettyPrintWithTrAX(nTmp,true));
		
		
		
		/* ****** TEST CASI ERRATI ****** */
		
		System.out.println("\n\n\n*************** TESTSUITE CASI ERRATI **********************");
		
		File sourceFileErrato = File.createTempFile("srcErrato", ".xml");
		FileSystemUtilities.writeFile(sourceFileErrato, (SOAP_XML+"ALTRO ELEMENTO IN FONDO CHE RENDE INVALIDO XML").getBytes());
		
		File sourceFileNamespaceDifferente = File.createTempFile("srcNamespaceErrato", ".xml");
		FileSystemUtilities.writeFile(sourceFileNamespaceDifferente, SOAP_XML.replace("www.prova.org", "www.altroNamespace.org").getBytes());
		
		File xqueryFileErrato = File.createTempFile("testErrato", ".xquery");
		FileSystemUtilities.writeFile(xqueryFileErrato, (XQUERY+"Codice errato che non significa nulla").getBytes());
		
		File xqueryFileCodeExtenral = File.createTempFile("testSenzaExternalCode", ".xquery");
		FileSystemUtilities.writeFile(xqueryFileCodeExtenral, XQUERY_WITHOUT_CODE_EXTERNAL.getBytes());
		
		System.out.println("======== SRC ERRATO ===========");
		
		try{
			xqueryEngine.evaluate(sourceFileErrato, xqueryFile, true);
			throw new Exception("Attesa eccezione non lanciata");
		}catch(org.openspcoop2.utils.xml.XQueryException x){
			System.out.println("Ricevuta eccezione ["+x.getClass().getName()+"] attesa: "+x.getMessage());
		}
		
		System.out.println("======== QEURY ERRATA ===========");
		
		try{
			xqueryEngine.evaluate(sourceFile, xqueryFileErrato, true);
			throw new Exception("Attesa eccezione non lanciata");
		}catch(org.openspcoop2.utils.xml.XQueryNotValidException x){
			System.out.println("Ricevuta eccezione ["+x.getClass().getName()+"] attesa: "+x.getMessage());
		}
		
		System.out.println("======== QEURY NON TROVA RISULTATI (con xml cmq riempito) ===========");
		
		System.out.println("STRING:");
		System.out.println(xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFile, true)+"\n");
		
		System.out.println("FILE:");
		tmp = File.createTempFile("prova", ".tmp");
		xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFile, tmp, true);
		System.out.println(FileSystemUtilities.readFile(tmp)+"\n");
		tmp.delete();
		
		System.out.println("OUTPUT STREAM:");
		tmp = File.createTempFile("prova", ".tmp");
		fout = new FileOutputStream(tmp);
		xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFile, fout, true);
		fout.flush();
		fout.close();
		System.out.println(FileSystemUtilities.readFile(tmp)+"\n");
		tmp.delete();
		
		System.out.println("DOCUMENT:");
		dTmp = xqueryEngine.getXMLUtils().newDocument();
		xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFile, dTmp, true);
		System.out.println(PrettyPrintXMLUtils.prettyPrintWithTrAX(dTmp,true));
		
		System.out.println("ELEMENT:");
		dTmp = xqueryEngine.getXMLUtils().newDocument();
		eTmp = dTmp.createElement("prova");
		xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFile, eTmp, true);
		System.out.println(PrettyPrintXMLUtils.prettyPrintWithTrAX(eTmp,true));
		
		System.out.println("NODe:");
		dTmp = xqueryEngine.getXMLUtils().newDocument();
		eTmp = dTmp.createElement("prova");
		eTmp.appendChild(dTmp.createElement("provaInterno"));
		nTmp = eTmp.getFirstChild();
		xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFile, nTmp, true);
		System.out.println(PrettyPrintXMLUtils.prettyPrintWithTrAX(nTmp,true));
		
		System.out.println("======== QEURY NON TROVA RISULTATI (con xml vuoto) ===========");
		
		try{
			System.out.println("STRING:");
			System.out.println(xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFileCodeExtenral, true)+"\n");
		}catch(org.openspcoop2.utils.xml.XQueryEvaluateNotFoundException x){
			System.out.println("Ricevuta eccezione ["+x.getClass().getName()+"] attesa: "+x.getMessage());
		}
		
		try{
			System.out.println("FILE:");
			tmp = File.createTempFile("prova", ".tmp");
			xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFileCodeExtenral, tmp, true);
			System.out.println(FileSystemUtilities.readFile(tmp)+"\n");
			tmp.delete();
		}catch(org.openspcoop2.utils.xml.XQueryEvaluateNotFoundException x){
			System.out.println("Ricevuta eccezione ["+x.getClass().getName()+"] attesa: "+x.getMessage());
		}
		
		try{		
			System.out.println("OUTPUT STREAM:");
			tmp = File.createTempFile("prova", ".tmp");
			fout = new FileOutputStream(tmp);
			xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFileCodeExtenral, fout, true);
			fout.flush();
			fout.close();
			System.out.println(FileSystemUtilities.readFile(tmp)+"\n");
			tmp.delete();
		}catch(org.openspcoop2.utils.xml.XQueryEvaluateNotFoundException x){
			System.out.println("Ricevuta eccezione ["+x.getClass().getName()+"] attesa: "+x.getMessage());
		}
			
		try{
			System.out.println("DOCUMENT:");
			dTmp = xqueryEngine.getXMLUtils().newDocument();
			xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFileCodeExtenral, dTmp, true);
			System.out.println(PrettyPrintXMLUtils.prettyPrintWithTrAX(dTmp,true));
		}catch(org.openspcoop2.utils.xml.XQueryEvaluateNotFoundException x){
			System.out.println("Ricevuta eccezione ["+x.getClass().getName()+"] attesa: "+x.getMessage());
		}
			
		try{
			System.out.println("ELEMENT:");
			dTmp = xqueryEngine.getXMLUtils().newDocument();
			eTmp = dTmp.createElement("prova");
			xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFileCodeExtenral, eTmp, true);
			System.out.println(PrettyPrintXMLUtils.prettyPrintWithTrAX(eTmp,true));
		}catch(org.openspcoop2.utils.xml.XQueryEvaluateNotFoundException x){
			System.out.println("Ricevuta eccezione ["+x.getClass().getName()+"] attesa: "+x.getMessage());
		}
			
		try{
			System.out.println("NODe:");
			dTmp = xqueryEngine.getXMLUtils().newDocument();
			eTmp = dTmp.createElement("prova");
			eTmp.appendChild(dTmp.createElement("provaInterno"));
			nTmp = eTmp.getFirstChild();
			xqueryEngine.evaluate(sourceFileNamespaceDifferente, xqueryFileCodeExtenral, nTmp, true);
			System.out.println(PrettyPrintXMLUtils.prettyPrintWithTrAX(nTmp,true));
		}catch(org.openspcoop2.utils.xml.XQueryEvaluateNotFoundException x){
			System.out.println("Ricevuta eccezione ["+x.getClass().getName()+"] attesa: "+x.getMessage());
		}
		
	}
	
}
