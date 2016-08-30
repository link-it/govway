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
package org.openspcoop2.utils.wadl.validator;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.jvnet.ws.wadl.HTTPMethods;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.wadl.ApplicationWrapper;
import org.openspcoop2.utils.wadl.WADLUtilities;
import org.openspcoop2.utils.wadl.entity.DocumentHttpRequestEntity;
import org.openspcoop2.utils.wadl.entity.DocumentHttpResponseEntity;
import org.openspcoop2.utils.wadl.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Test {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Inizializzazione dell'oggetto ApplicationWrapper...");
		
		AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
		
		URI uri = Test.class.getResource("/org/openspcoop2/utils/wadl/test.wadl").toURI();
		Logger log = LoggerWrapperFactory.getLogger(Test.class);
		WADLUtilities wadlUtilities = WADLUtilities.getInstance(xmlUtils);
		
		//ApplicationWrapper wr = wadlUtilities.readWADLFromURI(log, uri, true, true, true);
		ApplicationWrapper wr = wadlUtilities.readWADLFromBytes(log, FileSystemUtilities.readBytesFromFile(new File(uri)), true, false, true);
		wr.addResource("test2.xsd", Utilities.getAsByteArray(Test.class.getResourceAsStream("/org/openspcoop2/utils/wadl/test2.xsd")));
		
		System.out.println("Inizializzazione dell'oggetto ApplicationWrapper completata");
		
		Validator validator = new Validator(log, wr);
		
		
		System.out.println("Test #1 (Richiesta GET con due parametri obbligatori)");
		
		TextHttpRequestEntity httpEntity = new TextHttpRequestEntity();
		httpEntity.setUrl("/prova2");
		httpEntity.setMethod(HTTPMethods.GET);
		Properties parametersFormBased = new Properties();
		parametersFormBased.put("idTrasmissionePROVA2Required", "true");
		parametersFormBased.put("idTrasmissionePROVA2NOTRequired", "true");
		httpEntity.setParametersFormBased(parametersFormBased);

		Properties parametersTrasporto = new Properties();
		parametersTrasporto.put("idTrasmissionePROVA2headerRequired", "true");
		parametersTrasporto.put("idTrasmissionePROVA2headerNOTRequired", "true");
		httpEntity.setParametersTrasporto(parametersTrasporto);
		
		validator.validate(httpEntity);

		System.out.println("Test #1 completato");
		
		System.out.println("Test #2 (Richiesta GET con tipo XML)");
		DocumentHttpRequestEntity httpEntity2 = new DocumentHttpRequestEntity();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		Element root = (Element) document.createElementNS("http://www.openspcoop.org/example/wadl/interno", "test");
		Element idTest = (Element) document.createElement("id-test");
		idTest.setTextContent("1");
		root.appendChild(idTest);
		Element nome = (Element) document.createElement("nome");
		nome.setTextContent("nome");
		root.appendChild(nome);
		
		document.appendChild(root);
		httpEntity2.setContent(document);
		
		httpEntity2.setContentType("application/xml");
		httpEntity2.setUrl("/allineamentopendenze/getStatoTrasmissioni");
		httpEntity2.setMethod(HTTPMethods.POST);
		
		validator.validate(httpEntity2);
		System.out.println("Test #2 completato");
		
		
		System.out.println("Test #3 (Risposta POST con differenti tipi di XML a seconda dello stato, caso 400)");
		DocumentHttpResponseEntity httpEntity3 = new DocumentHttpResponseEntity();
		httpEntity3.setStatus(400);
		Document document1 = builder.newDocument();
		Element raccolta = (Element) document1.createElementNS("http://www.openspcoop.org/example/wadl/inline/importInclude", "raccolta");

		Element idRaccolta = (Element) document1.createElementNS("http://www.openspcoop.org/example/wadl/inline/importInclude", "id-raccolta");
		idRaccolta.setTextContent("1");
		raccolta.appendChild(idRaccolta);
		Element raccoltaTest = (Element) document1.createElementNS("http://www.openspcoop.org/example/wadl/interno", "test");
		Element raccoltaTestIdTest = (Element) document1.createElementNS("http://www.openspcoop.org/example/wadl/interno", "id-test");
		raccoltaTestIdTest.setTextContent("1");
		raccoltaTest.appendChild(raccoltaTestIdTest);
		Element raccoltaTestNome = (Element) document1.createElementNS("http://www.openspcoop.org/example/wadl/interno", "nome");
		raccoltaTestNome.setTextContent("nome");
		raccoltaTest.appendChild(raccoltaTestNome);
		raccolta.appendChild(raccoltaTest);

		
		document1.appendChild(raccolta);
		httpEntity3.setContent(document1);
		
		httpEntity3.setContentType("application/xml");
		httpEntity3.setUrl("/allineamentopendenze/getStatoTrasmissioniConPiuResponse");
		httpEntity3.setMethod(HTTPMethods.POST);
		
		validator.validate(httpEntity3);

		System.out.println("Test #3 completato");
		
		System.out.println("Test #4 (Risposta POST con differenti tipi di XML a seconda dello stato, caso 200)");

		DocumentHttpResponseEntity httpEntity4 = new DocumentHttpResponseEntity();
		httpEntity4.setStatus(200);
		Document document2 = builder.newDocument();
		
		Element root2 = (Element) document2.createElementNS("http://www.openspcoop.org/example/wadl/interno", "test");
		Element idTest2 = (Element) document2.createElement("id-test");
		idTest2.setTextContent("1");
		root2.appendChild(idTest2);
		Element nome2 = (Element) document2.createElement("nome");
		nome2.setTextContent("nome");
		root2.appendChild(nome2);
		
		document2.appendChild(root2);
		httpEntity4.setContent(document2);
		
		httpEntity4.setContentType("application/xml");
		httpEntity4.setUrl("/allineamentopendenze/getStatoTrasmissioniConPiuResponse");
		httpEntity4.setMethod(HTTPMethods.POST);
		
		validator.validate(httpEntity4);
		
		System.out.println("Test #4 completato");

		System.out.println("Test #5 (Risposta POST con differenti tipi di XML a seconda dello stato, caso di XML sbagliato)");

		DocumentHttpResponseEntity httpEntity5 = new DocumentHttpResponseEntity();
		httpEntity5.setStatus(200);
		Document document3 = builder.newDocument();
		
		Element root3 = (Element) document3.createElement("sbagliato");
		
		document3.appendChild(root3);
		httpEntity5.setContent(document3);
		
		httpEntity5.setContentType("application/xml");
		httpEntity5.setUrl("/allineamentopendenze/getStatoTrasmissioniConPiuResponse");
		httpEntity5.setMethod(HTTPMethods.POST);

		try {
			validator.validate(httpEntity5);
			throw new Exception("Errore: Attesa " + WADLValidatorException.class.getName());
		} catch(WADLValidatorException e) {
			System.out.println("Test #5 completato");
		}
		
	}
}
