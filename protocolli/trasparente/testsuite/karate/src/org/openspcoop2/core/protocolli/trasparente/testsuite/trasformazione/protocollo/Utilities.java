/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.trasformazione.protocollo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.w3c.dom.Element;

/**
* Utilities
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Utilities extends ConfigLoader {

	public static String tipo_ok = "OK";
	public static String tipo_ko = "KO";
	
	public static String descr_ok = "Invio effettuato correttamente.";
	public static String descr_ko_xml = "&lt;ListaAnomalie&gt;&lt;Anomalia&gt;&lt;CampiInErrore&gt;InizioRapporto.QualificaProfessionale&lt;/CampiInErrore&gt;&lt;CodiceAnomalia&gt;0083&lt;/CodiceAnomalia&gt;&lt;DescrizioneAnomalia&gt;Codice Classificazione non presente in base dati per attributo 9.2.2.1.0.0&lt;/DescrizioneAnomalia&gt;&lt;/Anomalia&gt;&lt;Anomalia&gt;&lt;CampiInErrore&gt;InizioRapporto.QualificaProfessionale&lt;/CampiInErrore&gt;&lt;CodiceAnomalia&gt;0001&lt;/CodiceAnomalia&gt;&lt;DescrizioneAnomalia&gt;La Qualifica Professionale '9.2.2.1.0.0' non e' riconosciuta dalla corrispondente tabella di classificazione.&lt;/DescrizioneAnomalia&gt;&lt;/Anomalia&gt;&lt;/ListaAnomalie&gt;";
	public static String descr_emailError_xml = "&lt;ListaAnomalie&gt;&lt;Anomalia&gt;&lt;CodiceAnomalia&gt;0000&lt;/CodiceAnomalia&gt;&lt;DescrizioneAnomalia&gt;Errore generico:cvc-pattern-valid: Value 'paolino.paperinopaperopoli.it' is not facet-valid with respect to pattern '([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?' for type 'EMail'..&lt;/DescrizioneAnomalia&gt;&lt;Eccezione&gt;cvc-pattern-valid: Value 'paolino.paperinopaperopoli.it' is not facet-valid with respect to pattern '([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?' for type 'EMail'.&lt;/Eccezione&gt;&lt;/Anomalia&gt;&lt;/ListaAnomalie&gt;";
	
	public static String fault_code_server = "Server";
	public static String fault_code_server_soap = "soapenv:Server";
	public static String fault_string_server = "EGOV_IT_300 - Errore nel processamento del Messaggio SPCoop";
	
	public static String fault_code_client = "Client";
	public static String fault_code_client_soap = "soapenv:Client";
	public static String fault_string_client = "EGOV_IT_101 - Identificativo della parte Mittente sconosciuto";
	
	public static String descr_notifica_ok = "Notifica Comunicazione avvenuta con successo";
	public static String descr_notifica_ko = "Notifica fallita";
	public static String descr_notifica_ko_json = "{\"listaAnomalie\":[{\"codiceAnomalia\":\"0083\",\"descrizioneAnomalia\":\"Codice Classificazione non presente in base dati per attributo 9.2.2.1.0.0\",\"campiInErrore\":\"InizioRapporto.QualificaProfessionale\"},{\"codiceAnomalia\":\"0001\",\"descrizioneAnomalia\":\"La Qualifica Professionale '9.2.2.1.0.0' non e' riconosciuta dalla corrispondente tabella di classificazione.\",\"campiInErrore\":\"InizioRapporto.QualificaProfessionale\"}]}";
	
	private final static String soapaction = "";

	public static HttpResponse _test(
			TipoServizio tipoServizio, String contentTypeParam, byte[]content,
			String api, String azioneurl, String operazione, String responseFile, String responseContentType, int responseReturnCode,
			String msgErrore) throws Exception {
		

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+azioneurl
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+azioneurl;
		
		HttpRequest request = new HttpRequest();
		
		request.addHeader("GovWay-TestSuite-BackendType",operazione);
		request.addHeader("GovWay-TestSuite-ResponseFile",responseFile);
		request.addHeader("GovWay-TestSuite-ResponseContentType",responseContentType);
		request.addHeader("GovWay-TestSuite-ResponseReturnCode",responseReturnCode+"");
		
		String contentTypeRequest = contentTypeParam;
		String contentTypeResponse = contentTypeParam;
		if(HttpConstants.CONTENT_TYPE_SOAP_1_1.equals(contentTypeParam)) {
			String soapActionRequest = "\""+soapaction+"\"";
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, soapActionRequest);
		}
		else if(HttpConstants.CONTENT_TYPE_SOAP_1_2.equals(contentTypeParam)) {
			String soapActionRequest = "\""+soapaction+"\"";
			contentTypeRequest=contentTypeRequest +"; "+HttpConstants.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION+"="+soapActionRequest;
		}
				
		request.setReadTimeout(20000);
						
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentTypeRequest);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		
	
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(msgErrore!=null) {
			if(api.contains("Rest2Soap")) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.HTTP_5xx);
			}
			else {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO);
			}
			assertEquals(500, response.getResultHTTPOperation());
		}
		else {
			assertEquals(200, response.getResultHTTPOperation());
		}
		try{
			assertEquals(contentTypeResponse, response.getContentType());
		}catch(Throwable t) {
			assertEquals(contentTypeResponse+";charset=UTF-8", response.getContentType());
		}
		
		DBVerifier.verify(idTransazione, esitoExpected, msgErrore);
		
		return response;
		
	}

	
	public static void verificaXmlOk(String tipo, String descr, HttpResponse response) throws Exception {
		
		Element n = XMLUtils.getInstance().newElement(response.getContent());
		
		String tipoEstratto = AbstractXPathExpressionEngine.extractAndConvertResultAsString(n, new XPathExpressionEngine(), "//{http://servizi.lavoro.gov.it/comobbl/types}Tipo_Risposta/text()", logCore);
		assertEquals(tipo, tipoEstratto);
		
		String descrEstratto = AbstractXPathExpressionEngine.extractAndConvertResultAsString(n, new XPathExpressionEngine(), "//{http://servizi.lavoro.gov.it/comobbl/types}Descr_Esito/text()", logCore);
		try {
			assertEquals(descr, descrEstratto);
		}catch(Throwable e) {
			System.out.println("Ricevuto: "+descrEstratto);
			throw e;
		}
	}
	
	public static void verificaFault(boolean soap11, String code, String descr, HttpResponse response) throws Exception {
		
		Element n = XMLUtils.getInstance().newElement(response.getContent());
		
		String patternFaultCode = soap11 ? "//faultcode/text()" : "//{http://www.w3.org/2003/05/soap-envelope}Subcode/{http://www.w3.org/2003/05/soap-envelope}Value/text()";
		String patternFaultString = soap11 ? "//faultstring/text()" : "//{http://www.w3.org/2003/05/soap-envelope}Text/text()";
		
		String codeEstratto = AbstractXPathExpressionEngine.extractAndConvertResultAsString(n, new XPathExpressionEngine(), patternFaultCode, logCore);
		assertEquals(code, codeEstratto);
		
		String descrEstratto = AbstractXPathExpressionEngine.extractAndConvertResultAsString(n, new XPathExpressionEngine(), patternFaultString, logCore);
		try {
			assertEquals(descr, descrEstratto);
		}catch(Throwable e) {
			System.out.println("Ricevuto: "+descrEstratto);
			throw e;
		}
	}
	
	public static void verificaJsonOk(String tipo, String descr, HttpResponse response) throws Exception {
		
		String json = new String(response.getContent());
		
		//System.out.println("JSON: "+json);
		
		String tipoEstratto = JsonPathExpressionEngine.extractAndConvertResultAsString(json, "$.modello.payload.tipoRisposta", logCore);
		assertEquals(tipo, tipoEstratto);
		
		String descrEstratto = JsonPathExpressionEngine.extractAndConvertResultAsString(json, "$.modello.payload.descrEsito", logCore);
		try {
			assertEquals(descr, descrEstratto);
		}catch(Throwable e) {
			System.out.println("Ricevuto: "+descrEstratto);
			throw e;
		}
	}
	
	public static void verificaJsonFault(String code, String descr, HttpResponse response) throws Exception {
		
		String json = new String(response.getContent());
		
		//System.out.println("JSON: "+json);
		
		String tipoEstratto = JsonPathExpressionEngine.extractAndConvertResultAsString(json, "$.modello.payload.faultcode", logCore);
		assertEquals(code, tipoEstratto);
		
		String descrEstratto = JsonPathExpressionEngine.extractAndConvertResultAsString(json, "$.modello.payload.faultstring", logCore);
		try {
			assertEquals(descr, descrEstratto);
		}catch(Throwable e) {
			System.out.println("Ricevuto: "+descrEstratto);
			throw e;
		}
	}
}
