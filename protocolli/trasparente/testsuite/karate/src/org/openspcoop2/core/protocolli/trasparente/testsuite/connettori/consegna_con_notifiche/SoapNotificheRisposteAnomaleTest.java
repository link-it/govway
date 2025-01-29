/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.IDUtilities;
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
public class SoapNotificheRisposteAnomaleTest extends ConfigLoader{

	private static final String erogazione = "TestConsegnaConNotificheContextSyncSoapCasiAnomali";


	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
	}
	
	@AfterClass
	public static void After() {
		Common.fermaRiconsegne(dbUtils);
	}

	@org.junit.After
	public void AfterEach() {
		Common.fermaRiconsegne(dbUtils);
	}
	
	
		
	private static HttpRequest buildRestRequest(String erogazione, HttpRequestMethod method, String idApplicativo, String tipoTest, boolean soap11,
			boolean generateErroreConnectionRefused,boolean generateResponseEmpty, boolean generateFault, Integer returnCode, String contentTypeNotificaAtteso) {
		
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		
		String contentType = soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8" : HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=utf-8";
		byte[]content = soap11 ? Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes() : Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes();
				
		HttpRequest request = new HttpRequest();
		if(soap11) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "test");
		}
		request.setMethod(method);
		request.setContent(content);
		request.setContentType(contentType);
		String azione = generateResponseEmpty ? "ping" : "echo";
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/"+azione
				+ "?tipoTest="+tipoTest);
		
		if(generateErroreConnectionRefused) {
			request.addHeader("GovWay-TestSuite-Port", "8880");
		}
		else {
			request.addHeader("GovWay-TestSuite-Port", "8080");
		}
		if(generateResponseEmpty) {
			request.addHeader("GovWay-TestSuite-Service", "ping");
		}
		else {
			request.addHeader("GovWay-TestSuite-Service", "echo");
		}
		if(generateFault) {
			request.setUrl(request.getUrl()+"&fault=true");
			if(soap11) {
				request.setUrl(request.getUrl()+"&faultSoapVersion=11");
			}
			else {
				request.setUrl(request.getUrl()+"&faultSoapVersion=12");
			}
		}
		if(returnCode!=null) {
			request.setUrl(request.getUrl()+"&returnCode="+returnCode);
		}
		request.addHeader("GovWay-TestSuite-ContentTypeAttesoNotifica", contentTypeNotificaAtteso);
		
		return request;
	}
	
	
	@Test
	public void notificaRichiestaSoap11RispostaVuota() throws Exception {
		_notifica("Richiesta","Richiesta",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				false, null,
				true,
				false,true,false,null, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8");
	}
	@Test
	public void notificaRichiestaSoap11ConnectionRefused() throws Exception {
		_notifica("Richiesta","Richiesta",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				false, null,
				true,
				true,false,false,null, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8"); 
	}
	@Test
	public void notificaRichiestaSoap11RispostaVuota500() throws Exception {
		_notifica("Richiesta","Richiesta",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				false, null,
				true,
				false,true,false,500, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8");
	}
	@Test
	public void notificaRichiestaSoap11RispostaSOAPFault() throws Exception {
		_notifica("Richiesta","Richiesta",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				false, null,
				true,
				false,false,true,500, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8");
	}
	
	
	
	
	
	@Test
	public void notificaRichiestaSoap12TrasformataRispostaVuota() throws Exception {
		_notifica("RichiestaTrasformata","RichiestaTrasformata",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=utf-8",
				false, null,
				false,
				false,true,false,null, HttpConstants.CONTENT_TYPE_JSON);
	}
	@Test
	public void notificaRichiestaSoap12TrasformataConnectionRefused() throws Exception {
		_notifica("RichiestaTrasformata","RichiestaTrasformata",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=utf-8",
				false, null,
				false,
				true,false,false,null, HttpConstants.CONTENT_TYPE_JSON);
	}
	@Test
	public void notificaRichiestaSoap12TrasformataRispostaVuota500() throws Exception {
		_notifica("RichiestaTrasformata","RichiestaTrasformata",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=utf-8",
				false, null,
				false,
				false,true,false,500, HttpConstants.CONTENT_TYPE_JSON);
	}
	@Test
	public void notificaRichiestaSoap12TrasformataRispostaSOAPFault() throws Exception {
		_notifica("RichiestaTrasformata","RichiestaTrasformata",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=utf-8",
				false, null,
				false,
				false,false,true,500, HttpConstants.CONTENT_TYPE_JSON);
	}
	
	
	
	
	
	
	
	@Test
	public void notificaRispostaSoap12RispostaVuota() throws Exception {
		_notificaNonAttiva("Risposta","Risposta",
				false,
				false,true,false,null, "header-non-atteso");
	}
	@Test
	public void notificaRispostaSoap12ConnectionRefused() throws Exception {
		_notificaNonAttiva("Risposta","Risposta",
				false,
				true,false,false,null, HttpConstants.CONTENT_TYPE_JSON); // non essendoci anche la risposta viene prodotto un json della richiesta solamente
	}
	@Test
	public void notificaRispostaSoap12RispostaVuota500() throws Exception {
		_notificaNonAttiva("Risposta","Risposta",
				false,
				false,true,false,500, "header-non-atteso");
	}
	@Test
	public void notificaRispostaSoap11RispostaSOAPFault() throws Exception {
		_notifica("Risposta","Risposta",
				false, null,
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				true,
				false,false,true,500, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8"); 
	}
	
	
	
	
	
	
	@Test
	public void notificaRispostaSoap11TrasformataRispostaVuota() throws Exception {
		_notificaNonAttiva("RispostaTrasformata","RispostaTrasformata",
				true,
				false,true,false,null, HttpConstants.CONTENT_TYPE_JSON);
	}
	@Test
	public void notificaRispostaSoap11TrasformataConnectionRefused() throws Exception {
		_notificaNonAttiva("RispostaTrasformata","RispostaTrasformata",
				true,
				true,false,false,null, HttpConstants.CONTENT_TYPE_JSON); 
	}
	@Test
	public void notificaRispostaSoap11TrasformataRispostaVuota500() throws Exception {
		_notificaNonAttiva("RispostaTrasformata","RispostaTrasformata",
				true,
				false,true,false,500, HttpConstants.CONTENT_TYPE_JSON);
	}
	@Test
	public void notificaRispostaSoap11TrasformataRispostaSOAPFault() throws Exception {
		_notifica("RispostaTrasformata","RispostaTrasformata",
				false, null,
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				true,
				false,false,true,500, HttpConstants.CONTENT_TYPE_JSON);
	}
	
	
	
	
	@Test
	public void notificaEntrambiSoap11RispostaVuota() throws Exception {
		_notifica("Entrambi","Entrambi",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				false, null,
				true,
				false,true,false,null, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8"); // non essendoci anche la risposta viene inviata solamente la richiesta
	}
	@Test
	public void notificaEntrambiSoap11ConnectionRefused() throws Exception {
		_notifica("Entrambi","Entrambi",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				false, null,
				true,
				true,false,false,null, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8"); // non essendoci anche la risposta viene inviata solamente la richiesta
	}
	@Test
	public void notificaEntrambiSoap11RispostaVuota500() throws Exception {
		_notifica("Entrambi","Entrambi",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				false, null,
				true,
				false,true,false,500, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8"); // non essendoci anche la risposta viene inviata solamente la richiesta
	}
	@Test
	public void notificaEntrambiSoap11RispostaSOAPFault() throws Exception {
		_notifica("Entrambi","Entrambi",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=utf-8",
				true,
				false,false,true,500, HttpConstants.CONTENT_TYPE_ZIP);
	}
	
	
	
	
	
	
	@Test
	public void notificaEntrambiSoap12TrasformataRispostaVuota() throws Exception {
		_notifica("EntrambiTrasformata","EntrambiTrasformata",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=utf-8",
				false, null,
				false,
				false,true,false,null, HttpConstants.CONTENT_TYPE_JSON);
	}
	@Test
	public void notificaEntrambiSoap12TrasformataConnectionRefused() throws Exception {
		_notifica("EntrambiTrasformata","EntrambiTrasformata",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=utf-8",
				false, null,
				false,
				true,false,false,null, HttpConstants.CONTENT_TYPE_JSON);
	}
	@Test
	public void notificaEntrambiSoap12TrasformataRispostaVuota500() throws Exception {
		_notifica("EntrambiTrasformata","EntrambiTrasformata",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=utf-8",
				false, null,
				false,
				false,true,false,500, HttpConstants.CONTENT_TYPE_JSON);
	}
	@Test
	public void notificaEntrambiSoap12TrasformataRispostaSOAPFault() throws Exception {
		_notifica("EntrambiTrasformata","EntrambiTrasformata",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=utf-8",
				true, HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=utf-8",
				false,
				false,false,true,500, HttpConstants.CONTENT_TYPE_JSON);
	}
	
	
	
	public void _notifica(String nomeConnettore, String tag,
			boolean expectedRequest, String contentTypeRequest,
			boolean expectedResponse, String contentTypeResponse, boolean soap11,
			boolean generateErroreConnectionRefused,boolean generateResponseEmpty, boolean generateFault, Integer returnCode, String contentTypeNotificaAtteso) throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
				
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, idApplicativo, tag, soap11,
				generateErroreConnectionRefused, generateResponseEmpty, generateFault, returnCode, contentTypeNotificaAtteso);

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		RestNotificheRisposteTest.checkRuntime(msgId, 
				expectedRequest, contentTypeRequest,
				expectedResponse, contentTypeResponse,
				true);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			if(generateErroreConnectionRefused) {
				assertEquals(500, r.getResultHTTPOperation());
			}
			else if(returnCode!=null) {
				assertEquals(returnCode.intValue(), r.getResultHTTPOperation());
			}
			else {
				assertEquals(200, r.getResultHTTPOperation());
			}
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}
		
	}
	
	public void _notificaNonAttiva(String nomeConnettore, String tag,
			boolean soap11,
			boolean generateErroreConnectionRefused,boolean generateResponseEmpty, boolean generateFault, Integer returnCode, String contentTypeNotificaAtteso) throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
				
		Set<String> connettori = new HashSet<>();
		connettori.add(nomeConnettore);
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, idApplicativo, tag, soap11,
				generateErroreConnectionRefused, generateResponseEmpty, generateFault, returnCode, contentTypeNotificaAtteso);
		
		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
				
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			if(generateErroreConnectionRefused) {
				assertEquals(500, r.getResultHTTPOperation());
			}
			else if(returnCode!=null) {
				assertEquals(returnCode.intValue(), r.getResultHTTPOperation());
			}
			else {
				assertEquals(200, r.getResultHTTPOperation());
			}
			
		}
		
		Utilities.sleep(3000); // attendo 3 secondi per l'ipotetica attivazione della consegna
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		RestNotificheRisposteTest.checkRuntimeNotExists(msgId);
		
	}

}
