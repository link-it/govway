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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_con_notifiche;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * RestNotificheRisposteTest
 * 
 * @author Francesco Scarlato
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SoapNotificheRisposteTest extends ConfigLoader{

	private static final String erogazione = "TestConsegnaConNotificheRispostaSoap";

	private static final String NAMESPACE = "NAMESPACE";
	private static final String _contentRichiestaTrasformata = "<soap:Envelope xmlns:soap=\""+NAMESPACE+"\">\n"+
		"    <soap:Header xmlns:soap=\""+NAMESPACE+"\"><prova xmlns=\"http://test\"><xmlFragment1>TEST ESEMPIO 1</xmlFragment1></prova></soap:Header><soap:Body>\n"+
		"        <ns2:Test xmlns:ns2=\"http://govway.org/example\">\n"+
		"           <xmlFragment1>TEST ESEMPIO 1</xmlFragment1>\n"+
		"           <xmlFragment2>TEST ESEMPIO 2</xmlFragment2>\n"+
		"           <xmlFragment3>TEST ESEMPIO 3</xmlFragment3>\n"+
		"           <xmlFragment4>TEST ESEMPIO 4</xmlFragment4>\n"+
		"           <xmlFragment5>TEST ESEMPIO 5</xmlFragment5>\n"+
		"           <xmlFragment6>TEST ESEMPIO 6</xmlFragment6>\n"+
		"           <xmlFragment7>TEST ESEMPIO 7</xmlFragment7>\n"+
		"           <xmlFragment8>TEST ESEMPIO 8</xmlFragment8>\n"+
		"           <xmlFragment9>TEST ESEMPIO 9</xmlFragment9>\n"+
		"           <xmlFragment10>TEST ESEMPIO 10</xmlFragment10>\n"+
		"           <xmlFragment11>TEST ESEMPIO 11</xmlFragment11>\n"+
		"           <xmlFragment12>TEST ESEMPIO 12</xmlFragment12>\n"+
		"           <xmlFragment13>TEST ESEMPIO 13</xmlFragment13>\n"+
		"           <xmlFragment14>TEST ESEMPIO 14</xmlFragment14>\n"+
		"           <xmlFragment15>TEST ESEMPIO 15</xmlFragment15>\n"+
		"           <xmlFragment16>TEST ESEMPIO 16</xmlFragment16>\n"+
		"           <xmlFragment17>TEST ESEMPIO 17</xmlFragment17>\n"+
		"           <xmlFragment18>TEST ESEMPIO 18</xmlFragment18>\n"+
		"           <xmlFragment19>TEST ESEMPIO 19</xmlFragment19>\n"+
		"           <id:TestReq xmlns:id=\"http://govway.org/example/req\">IDREQ</id:TestReq>\n"+
		"        </ns2:Test>\n"+
		"    </soap:Body>\n"+
		"</soap:Envelope>";
	private static final String contentRichiestaTrasformataSoap11 = _contentRichiestaTrasformata.replace(NAMESPACE, Costanti.SOAP_ENVELOPE_NAMESPACE);
	private static final String contentRichiestaTrasformataSoap12 = _contentRichiestaTrasformata.replace(NAMESPACE, Costanti.SOAP12_ENVELOPE_NAMESPACE);
	
	private static final String _contentRispostaTrasformata = "<soap:Envelope xmlns:soap=\""+NAMESPACE+"\">\n"+
		"    <soap:Header xmlns:soap=\""+NAMESPACE+"\"><prova xmlns=\"http://test\"><xmlFragment1>TEST ESEMPIO 1</xmlFragment1></prova><provaReqAsRes xmlns=\"http://test\"><id:TestRes xmlns:id=\"http://govway.org/example/res\">IDRES</id:TestRes></provaReqAsRes><prova2 xmlns=\"http://test\"><xmlFragment2>TEST ESEMPIO 2</xmlFragment2></prova2><provaRes xmlns=\"http://test\"><id:TestRes xmlns:id=\"http://govway.org/example/res\">IDRES</id:TestRes></provaRes></soap:Header><soap:Body>\n"+
		"        <ns2:Test xmlns:ns2=\"http://govway.org/example\">\n"+
		"           <xmlFragment1>TEST ESEMPIO 1</xmlFragment1>\n"+
		"           <xmlFragment2>TEST ESEMPIO 2</xmlFragment2>\n"+
		"           <xmlFragment3>TEST ESEMPIO 3</xmlFragment3>\n"+
		"           <xmlFragment4>TEST ESEMPIO 4</xmlFragment4>\n"+
		"           <xmlFragment5>TEST ESEMPIO 5</xmlFragment5>\n"+
		"           <xmlFragment6>TEST ESEMPIO 6</xmlFragment6>\n"+
		"           <xmlFragment7>TEST ESEMPIO 7</xmlFragment7>\n"+
		"           <xmlFragment8>TEST ESEMPIO 8</xmlFragment8>\n"+
		"           <xmlFragment9>TEST ESEMPIO 9</xmlFragment9>\n"+
		"           <xmlFragment10>TEST ESEMPIO 10</xmlFragment10>\n"+
		"           <xmlFragment11>TEST ESEMPIO 11</xmlFragment11>\n"+
		"           <xmlFragment12>TEST ESEMPIO 12</xmlFragment12>\n"+
		"           <xmlFragment13>TEST ESEMPIO 13</xmlFragment13>\n"+
		"           <xmlFragment14>TEST ESEMPIO 14</xmlFragment14>\n"+
		"           <xmlFragment15>TEST ESEMPIO 15</xmlFragment15>\n"+
		"           <xmlFragment16>TEST ESEMPIO 16</xmlFragment16>\n"+
		"           <xmlFragment17>TEST ESEMPIO 17</xmlFragment17>\n"+
		"           <xmlFragment18>TEST ESEMPIO 18</xmlFragment18>\n"+
		"           <xmlFragment19>TEST ESEMPIO 19</xmlFragment19>\n"+
		"           <id:TestRes xmlns:id=\"http://govway.org/example/res\">IDRES</id:TestRes>\n"+
		"        </ns2:Test>\n"+
		"    </soap:Body>\n"+
		"</soap:Envelope>";
	private static final String contentRispostaTrasformataSoap11 = _contentRispostaTrasformata.replace(NAMESPACE, Costanti.SOAP_ENVELOPE_NAMESPACE);
	private static final String contentRispostaTrasformataSoap12 = _contentRispostaTrasformata.replace(NAMESPACE, Costanti.SOAP12_ENVELOPE_NAMESPACE);
	
	private static final String _contentRichiestaRispostaTrasformata = "<soap:Envelope xmlns:soap=\""+NAMESPACE+"\">\n"+
		"    <soap:Body><xmlTest>\n"+
		"<prova xmlns=\"http://test\"><xmlFragment1>TEST ESEMPIO 1</xmlFragment1></prova>\n"+
		"<provaReqAsRes xmlns=\"http://test\"><id:TestReq xmlns:id=\"http://govway.org/example/req\">IDREQ</id:TestReq></provaReqAsRes>\n"+
		"<prova2 xmlns=\"http://test\"><xmlFragment2>TEST ESEMPIO 2</xmlFragment2></prova2>\n"+
		"<provaRes xmlns=\"http://test\"><id:TestRes xmlns:id=\"http://govway.org/example/res\">IDRES</id:TestRes></provaRes>\n"+
		"</xmlTest></soap:Body>\n"+
		"</soap:Envelope>";
	private static final String contentRichiestaRispostaTrasformataSoap11 = _contentRichiestaRispostaTrasformata.replace(NAMESPACE, Costanti.SOAP_ENVELOPE_NAMESPACE);
	private static final String contentRichiestaRispostaTrasformataSoap12 = _contentRichiestaRispostaTrasformata.replace(NAMESPACE, Costanti.SOAP12_ENVELOPE_NAMESPACE);
	
	private static String reqId =  "<id:TestReq xmlns:id=\"http://govway.org/example/req\">IDREQ</id:TestReq>";
	private static String resId =  "<id:TestRes xmlns:id=\"http://govway.org/example/res\">IDRES</id:TestRes>";
	private static byte[] responseSoap11Xml = null;
	private static File responseSoap11XmlFile = null;
	private static byte[] responseSoap12Xml = null;
	private static File responseSoap12XmlFile = null;
	
	private static File _cartellaFiles = null;
	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		_cartellaFiles = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if(!_cartellaFiles.exists()) {
			_cartellaFiles.mkdir();
		}
		if (!_cartellaFiles.isDirectory()|| !_cartellaFiles.canWrite()) {
			throw new RuntimeException("E' necessario creare la cartella per scrivere le richieste dei connettori, indicata dalla poprietà: <connettori.consegna_multipla.connettore_file.path> ");
		}
		
		try {
			responseSoap11Xml = Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, resId).getBytes();
			responseSoap11XmlFile = File.createTempFile("responseSoap11_", "xml", _cartellaFiles);
			FileSystemUtilities.writeFile(responseSoap11XmlFile, responseSoap11Xml);
		}catch(Exception e) {
			throw new RuntimeException("Creazione risposta soap11 non riuscita");
		}
		
		try {
			responseSoap12Xml = Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE, resId).getBytes();
			responseSoap12XmlFile = File.createTempFile("responseSoap12_", "xml", _cartellaFiles);
			FileSystemUtilities.writeFile(responseSoap12XmlFile, responseSoap12Xml);
		}catch(Exception e) {
			throw new RuntimeException("Creazione risposta soap12 non riuscita");
		}
	}
	
	@AfterClass
	public static void After() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if(cartellaRisposte.exists() && cartellaRisposte.isDirectory() && cartellaRisposte.canWrite()) {
			FileSystemUtilities.emptyDir(cartellaRisposte);
		}
		
		try {
			responseSoap11XmlFile.delete();
		}catch(Exception e) {
			throw new RuntimeException("Eliminazione risposta soap11 non riuscita");
		}
		
		try {
			responseSoap12XmlFile.delete();
		}catch(Exception e) {
			throw new RuntimeException("Eliminazione risposta soap12 non riuscita");
		}
	}

	@org.junit.After
	public void AfterEach() {
		Common.fermaRiconsegne(dbUtils);
	}
	
	

	
	private static HttpRequest buildRestRequest(String erogazione, HttpRequestMethod method, String tipoTest, boolean soap11) throws Exception {
		
		String contentType = soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
		byte[]content = soap11 ? Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, reqId).getBytes() : Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE, reqId).getBytes();
		
		HttpRequest request = new HttpRequest();
		if(soap11) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "test");
		}
		request.setMethod(method);
		request.setContent(content);
		request.setContentType(contentType);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/TestConsegnaMultipla"
				+ "?tipoTest="+tipoTest+"&destFile="+(soap11?responseSoap11XmlFile:responseSoap12XmlFile).getAbsolutePath()+"&destFileContentType="+TransportUtils.urlEncodeParam(contentType,Charset.UTF_8.getValue()));
		
		return request;
	}

	
		
	
	@Test
	public void notificaRichiestaSoap11() throws Exception {
		 _notificaRichiesta("Richiesta","RichiestaDefault", true);
	}
	@Test
	public void notificaRichiestaTrasformataSoap11() throws Exception {
		 _notificaRichiesta("RichiestaTrasformata", "RichiestaTrasformata", true);
	}
	@Test
	public void notificaRichiestaSoap12() throws Exception {
		 _notificaRichiesta("Richiesta","RichiestaDefault", false);
	}
	@Test
	public void notificaRichiestaTrasformataSoap12() throws Exception {
		 _notificaRichiesta("RichiestaTrasformata", "RichiestaTrasformata", false);
	}
	private void _notificaRichiesta(String nomeConnettore, String tag, boolean soap11) throws Exception {

				
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, tag, soap11);

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		RestNotificheRisposteTest.checkRuntime(msgId, 
				true, request1.getContentType()+"; charset=utf-8", 
				false, null);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		String contentAtteso = new String(request1.getContent());
		String nomeFile = "richiesta";
		if("RichiestaTrasformata".equals(nomeConnettore)) {
			nomeFile = "richiestaTrasformata";
			contentAtteso = soap11 ? contentRichiestaTrasformataSoap11 : contentRichiestaTrasformataSoap12;
		}
		String contentTypeAtteso = request1.getContentType();
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, nomeFile, contentAtteso, contentTypeAtteso);
		
	}
	
	
	
	
	
	@Test
	public void notificaRichiesteSoap11() throws Exception {
		notificaRichieste(true);
	}
	@Test
	public void notificaRichiesteSoap12() throws Exception {
		notificaRichieste(false);
	}
	private void notificaRichieste(boolean soap11) throws Exception {

		
		Set<String> connettori = new HashSet<String>();
		connettori.add("Richiesta");
		connettori.add("RichiestaTrasformata");
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, "Richiesta", soap11);

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		RestNotificheRisposteTest.checkRuntime(msgId, 
				true, request1.getContentType()+"; charset=utf-8", 
				false, null);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, connettori.size()));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		String contentAtteso = new String(request1.getContent());
		String contentTypeAtteso = request1.getContentType();
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, "richiesta", contentAtteso, contentTypeAtteso);
		
		contentAtteso = soap11 ? contentRichiestaTrasformataSoap11 : contentRichiestaTrasformataSoap12;
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, "richiestaTrasformata", contentAtteso, contentTypeAtteso);
		
	}
	
	
	
	
	
	@Test
	public void notificaRispostaSoap11() throws Exception {
		_notificaRisposta("Risposta","RispostaDefault", true);
	}
	@Test
	public void notificaRispostaTrasformataSoap11() throws Exception {
		_notificaRisposta("RispostaTrasformata", "RispostaTrasformata", true);
	}
	@Test
	public void notificaRispostaSoap12() throws Exception {
		_notificaRisposta("Risposta","RispostaDefault", false);
	}
	@Test
	public void notificaRispostaTrasformataSoap12() throws Exception {
		_notificaRisposta("RispostaTrasformata", "RispostaTrasformata", false);
	}
	private void _notificaRisposta(String nomeConnettore, String tag, boolean soap11) throws Exception {

				
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, tag, soap11);
		
		request1.addHeader("GovWay-TestSuite-ReqID", "RequestValoreEsempioPerHeader");

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		String contentTypeRisposta = soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
		RestNotificheRisposteTest.checkRuntime(msgId, 
				false, null, 
				true, contentTypeRisposta);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		String contentAtteso = new String(soap11 ? responseSoap11Xml : responseSoap12Xml);
		String nomeFile = "risposta";
		if("RispostaTrasformata".equals(nomeConnettore)) {
			nomeFile = "rispostaTrasformata";
			contentAtteso = soap11 ? contentRispostaTrasformataSoap11 : contentRispostaTrasformataSoap12;
		}
		String contentTypeAtteso = contentTypeRisposta;
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, nomeFile, contentAtteso, contentTypeAtteso);
		
	}
	
	
	
	
	@Test
	public void notificaRisposteSoap11() throws Exception {
		notificaRisposte(true);
	}
	@Test
	public void notificaRisposteSoap12() throws Exception {
		notificaRisposte(false);
	}
	private void notificaRisposte(boolean soap11) throws Exception {

		
		Set<String> connettori = new HashSet<String>();
		connettori.add("Risposta");
		connettori.add("RispostaTrasformata");
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, "Risposta", soap11);

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		String contentTypeRisposta = soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
		RestNotificheRisposteTest.checkRuntime(msgId, 
				false, null, 
				true, contentTypeRisposta);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, connettori.size()));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		String contentAtteso = new String(soap11 ? responseSoap11Xml : responseSoap12Xml);
		String contentTypeAtteso = contentTypeRisposta;
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, "risposta", contentAtteso, contentTypeAtteso);
		
		contentAtteso = soap11 ? contentRispostaTrasformataSoap11 : contentRispostaTrasformataSoap12;
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, "rispostaTrasformata", contentAtteso, contentTypeAtteso);
		
	}
	
	
	
	@Test
	public void notificaRichiestaRispostaSoap11() throws Exception {
		_notificaRichiestaRisposta("RichiestaRisposta","RichiestaRispostaDefault", true);
	}
	@Test
	public void notificaRichiestaRispostaTrasformataSoap11() throws Exception {
		_notificaRichiestaRisposta("RichiestaRispostaTrasformata", "RichiestaRispostaTrasformata", true);
	}
	@Test
	public void notificaRichiestaRispostaSoap12() throws Exception {
		_notificaRichiestaRisposta("RichiestaRisposta","RichiestaRispostaDefault", false);
	}
	@Test
	public void notificaRichiestaRispostaTrasformataSoap12() throws Exception {
		_notificaRichiestaRisposta("RichiestaRispostaTrasformata", "RichiestaRispostaTrasformata", false);
	}
	private void _notificaRichiestaRisposta(String nomeConnettore, String tag, boolean soap11) throws Exception {

				
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, tag, soap11);

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		String contentTypeRisposta = soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
		RestNotificheRisposteTest.checkRuntime(msgId, 
				true, request1.getContentType()+"; charset=utf-8", 
				true, contentTypeRisposta);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		if("RichiestaRispostaTrasformata".equals(nomeConnettore)) {	
			String nomeFile = "richiestaRispostaTrasformata";
			String contentAtteso = soap11 ? contentRichiestaRispostaTrasformataSoap11 : contentRichiestaRispostaTrasformataSoap12;
			String contentTypeAtteso = contentTypeRisposta;
			RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, nomeFile, contentAtteso, contentTypeAtteso);
		}
		else {
			String nomeFile = "richiestaRisposta";
			String contentRichiestaAtteso = new String(request1.getContent());
			String contentRispostaAtteso = new String(soap11 ? responseSoap11Xml : responseSoap12Xml);
			String contentRispostaTypeAtteso = contentTypeRisposta;
			RestNotificheRisposteTest.checkFileZip(_cartellaFiles, govwayId, nomeFile, 
					contentRichiestaAtteso,request1.getContentType(),
					contentRispostaAtteso, contentRispostaTypeAtteso
					);
		}
		
	}
	
	
	
	@Test
	public void notificaRichiestaRispostaCheckMetodoHTTPDefaultSoap11() throws Exception {
		notificaRichiestaRispostaVerificaHttpMethod(null, "RichiestaRispostaCheckMetodoHttpDefault", true);
	}
	@Test
	public void notificaRichiestaRispostaMetodoPUTSoap11() throws Exception {
		notificaRichiestaRispostaVerificaHttpMethod(HttpRequestMethod.PUT, "RichiestaRispostaMetodoPUT", true);
	}
	@Test
	public void notificaRichiestaRispostaCheckMetodoHTTPDefaultSoap12() throws Exception {
		notificaRichiestaRispostaVerificaHttpMethod(null, "RichiestaRispostaCheckMetodoHttpDefault", false);
	}
	@Test
	public void notificaRichiestaRispostaMetodoPUTSoap12() throws Exception {
		notificaRichiestaRispostaVerificaHttpMethod(HttpRequestMethod.PUT, "RichiestaRispostaMetodoPUT", false);
	}
	
	private void notificaRichiestaRispostaVerificaHttpMethod(HttpRequestMethod httpRequestMethodExpected, String nomeConnettore, boolean soap11) throws Exception {

		
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, nomeConnettore, soap11);

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		String contentTypeRisposta = soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
		RestNotificheRisposteTest.checkRuntime(msgId, 
				true, request1.getContentType()+"; charset=utf-8", 
				true, contentTypeRisposta);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}
		
	}
	
	
	
	
	
	
	
	@Test
	public void notificaRichiesteRisposteSoap11() throws Exception {
		notificaRichiesteRisposte(true);
	}
	@Test
	public void notificaRichiesteRisposteSoap12() throws Exception {
		notificaRichiesteRisposte(false);
	}
	private void notificaRichiesteRisposte(boolean soap11) throws Exception {

		
		Set<String> connettori = new HashSet<String>();
		connettori.add("RichiestaRisposta");
		connettori.add("RichiestaRispostaCheckMetodoHttpDefault");
		connettori.add("RichiestaRispostaMetodoPUT");
		connettori.add("RichiestaRispostaTrasformata");
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, "RichiestaRisposta", soap11);

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		String contentTypeRisposta = soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
		RestNotificheRisposteTest.checkRuntime(msgId, 
				true, request1.getContentType()+"; charset=utf-8", 
				true, contentTypeRisposta);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, connettori.size()));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		String nomeFile = "richiestaRispostaTrasformata";
		String contentAtteso = soap11 ? contentRichiestaRispostaTrasformataSoap11 : contentRichiestaRispostaTrasformataSoap12;
		String contentTypeAtteso = contentTypeRisposta;
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, nomeFile, contentAtteso, contentTypeAtteso);

		nomeFile = "richiestaRisposta";
		String contentRichiestaAtteso = new String(request1.getContent());
		String contentRispostaAtteso = new String(soap11 ? responseSoap11Xml : responseSoap12Xml);
		String contentRispostaTypeAtteso = contentTypeRisposta;
		RestNotificheRisposteTest.checkFileZip(_cartellaFiles, govwayId, nomeFile, 
				contentRichiestaAtteso,request1.getContentType(),
				contentRispostaAtteso, contentRispostaTypeAtteso
				);		
	}
	
	
	
	
	@Test
	public void allSoap11() throws Exception {
		all(true);
	}
	@Test
	public void allSoap12() throws Exception {
		all(false);
	}
	private void all(boolean soap11) throws Exception {

		
		Set<String> connettori = new HashSet<String>();
		connettori.add("Richiesta");
		connettori.add("RichiestaTrasformata");
		connettori.add("Risposta");
		connettori.add("RispostaTrasformata");
		connettori.add("RichiestaRisposta");
		connettori.add("RichiestaRispostaCheckMetodoHttpDefault");
		connettori.add("RichiestaRispostaMetodoPUT");
		connettori.add("RichiestaRispostaTrasformata");
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, "All", soap11);

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		String contentTypeRisposta = soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
		RestNotificheRisposteTest.checkRuntime(msgId, 
				true, request1.getContentType()+"; charset=utf-8", 
				true, contentTypeRisposta);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, connettori.size()));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		
		// Check file attesi per Richiesta
		String contentAtteso = new String(request1.getContent());
		String contentTypeAtteso = request1.getContentType();
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, "richiesta", contentAtteso, contentTypeAtteso);
		
		contentAtteso = soap11 ? contentRichiestaTrasformataSoap11 : contentRichiestaTrasformataSoap12;
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, "richiestaTrasformata", contentAtteso, contentTypeAtteso);
		
		
		// Check file attesi per Risposta
		contentAtteso = new String(soap11 ? responseSoap11Xml : responseSoap12Xml);
		contentTypeAtteso = contentTypeRisposta;
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, "risposta", contentAtteso, contentTypeAtteso);
		
		contentAtteso = soap11 ? contentRispostaTrasformataSoap11 : contentRispostaTrasformataSoap12;
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, "rispostaTrasformata", contentAtteso, contentTypeAtteso);
		
		
		// Check file attesi per RichiestaRisposta
		String nomeFile = "richiestaRispostaTrasformata";
		contentAtteso = soap11 ? contentRichiestaRispostaTrasformataSoap11 : contentRichiestaRispostaTrasformataSoap12;
		contentTypeAtteso = contentTypeRisposta;
		RestNotificheRisposteTest.checkFile(_cartellaFiles, govwayId, nomeFile, contentAtteso, contentTypeAtteso);

		nomeFile = "richiestaRisposta";
		String contentRichiestaAtteso = new String(request1.getContent());
		String contentRispostaAtteso = new String(soap11 ? responseSoap11Xml : responseSoap12Xml);
		String contentRispostaTypeAtteso = contentTypeRisposta;
		RestNotificheRisposteTest.checkFileZip(_cartellaFiles, govwayId, nomeFile, 
				contentRichiestaAtteso,request1.getContentType(),
				contentRispostaAtteso, contentRispostaTypeAtteso
				);	
		
	}
	
}
