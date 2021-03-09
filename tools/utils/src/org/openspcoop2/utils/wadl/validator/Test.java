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
package org.openspcoop2.utils.wadl.validator;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.rest.entity.DocumentHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.ElementHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.wadl.WADLApi;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.slf4j.Logger;
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
		//org.openspcoop2.utils.wadl.WADLUtilities wadlUtilities = org.openspcoop2.utils.wadl.WADLUtilities.getInstance(xmlUtils);
		
		IApiReader reader = ApiFactory.newApiReader(ApiFormats.WADL);
		
		//org.openspcoop2.utils.wadl.ApplicationWrapper wr = wadlUtilities.readWADLFromURI(log, uri, true, true, true);
		//org.openspcoop2.utils.wadl.ApplicationWrapper wr = wadlUtilities.readWADLFromBytes(log, org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(new java.io.File(uri)), true, false, true);
		
		ApiSchema apiSchema = new ApiSchema("test2.xsd", Utilities.getAsByteArray(Test.class.getResourceAsStream("/org/openspcoop2/utils/wadl/test2.xsd")), ApiSchemaType.XSD);
		
		ApiReaderConfig readerConfig = new ApiReaderConfig();
		readerConfig.setVerbose(true);
		readerConfig.setProcessInclude(true);
		readerConfig.setProcessInlineSchema(true);
		reader.init(log, uri, readerConfig, apiSchema);
		Api api = reader.read();
		WADLApi wadlApi = (WADLApi) api;
		
		//wadlApi.getApplicationWadlWrapper().addResource("test2.xsd", Utilities.getAsByteArray(Test.class.getResourceAsStream("/org/openspcoop2/utils/wadl/test2.xsd")));
		
		System.out.println("Inizializzazione dell'oggetto ApplicationWrapper completata");
		
		ApiValidatorConfig config = new ApiValidatorConfig();
		IApiValidator validator = ApiFactory.newApiValidator(ApiFormats.WADL);
		validator.init(log, wadlApi, config);
		
		
		try {
			System.out.println("Test #1 (Richiesta GET con due parametri obbligatori)");
			
			TextHttpRequestEntity httpEntity = new TextHttpRequestEntity();
			httpEntity.setUrl("/prova2");
			httpEntity.setMethod(HttpRequestMethod.GET);
			Map<String, List<String>> parametersFormBased = new HashMap<String, List<String>>();
			TransportUtils.setParameter(parametersFormBased,"idTrasmissionePROVA2Required", "24");
			TransportUtils.setParameter(parametersFormBased,"idTrasmissionePROVA2NOTRequired", "true");
			httpEntity.setParameters(parametersFormBased);
	
			Map<String, List<String>> parametersTrasporto = new HashMap<String, List<String>>();
			TransportUtils.setHeader(parametersTrasporto,"idTrasmissionePROVA2headerRequired", "67");
			TransportUtils.setHeader(parametersTrasporto,"idTrasmissionePROVA2headerNOTRequired", "true");
			httpEntity.setHeaders(parametersTrasporto);
			
			validator.validate(httpEntity);
	
			System.out.println("Test #1 completato");
			
			System.out.println("Test #2 (Richiesta GET con tipo XML)");
			ElementHttpRequestEntity httpEntity2 = new ElementHttpRequestEntity();
			
			DocumentBuilderFactory factory = xmlUtils.getDocumentBuilderFactory();
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
			httpEntity2.setContent(document.getDocumentElement());
			
			httpEntity2.setContentType("application/xml");
			httpEntity2.setUrl("/allineamentopendenze/getStatoTrasmissioni");
			httpEntity2.setMethod(HttpRequestMethod.POST);
			
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
			httpEntity3.setMethod(HttpRequestMethod.POST);
			
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
			httpEntity4.setMethod(HttpRequestMethod.POST);
			
			validator.validate(httpEntity4);
			
			System.out.println("Test #4 completato");
	
			System.out.println("Test #5 (Richiesta GET con parametro dinamico)");
			
			TextHttpRequestEntity httpEntity5 = new TextHttpRequestEntity();
			httpEntity5.setUrl("/allineamentopendenze/id23");
			httpEntity5.setMethod(HttpRequestMethod.GET);
			validator.validate(httpEntity5);
	
			System.out.println("Test #5 completato");	
			
			System.out.println("Test #6 (Risposta POST con differenti tipi di XML a seconda dello stato, caso di XML sbagliato)");
	
			DocumentHttpResponseEntity httpEntity6 = new DocumentHttpResponseEntity();
			httpEntity6.setStatus(200);
			Document document3 = builder.newDocument();
			
			Element root3 = (Element) document3.createElement("sbagliato");
			
			document3.appendChild(root3);
			httpEntity6.setContent(document3);
			
			httpEntity6.setContentType("application/xml");
			httpEntity6.setUrl("/allineamentopendenze/getStatoTrasmissioniConPiuResponse");
			httpEntity6.setMethod(HttpRequestMethod.POST);
	
			try {
				validator.validate(httpEntity6);
				throw new Exception("Errore: Attesa " + WADLValidatorException.class.getName());
			} catch(WADLValidatorException e) {
				System.out.println("Test #6 completato");
			}
			
		}finally {
			validator.close(log, wadlApi, config);
		}
	}
}
