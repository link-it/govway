/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.swagger_request_validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.parametri.OpenAPI30_Utils;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {
		

	
	@Test
	public void erogazione_ok() throws Exception {
		test(logCore, requestValidContent.getBytes(), HttpRequestMethod.POST, "Invoice" , null);
	}
	
	@Test
	public void erogazione_request_invalid() throws Exception {
		test(logCore, requestInvalidContent.getBytes(), HttpRequestMethod.POST, "Invoice" , "Object instance has properties which are not allowed by the schema: [\"resourceNonEsistente\"]");
	}
	
	private static String requestContent = 
			"    \"resourceType\": \"Invoice\",\n"
			+ "    \"identifier\": [\n"
			+ "        {\n"
			+ "            \"system\": \"http://dati.ente_esempio.it/dataset/regioni\",\n"
			+ "            \"value\": \"090\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"system\": \"http://dati.ente_esempio.it/dataset/aziende_sanitarie\",\n"
			+ "            \"value\": \"202\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"system\": \"http://dati.ente_esempio.it/dataset/strutture_operative_skno\",\n"
			+ "            \"value\": \"09061301\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"value\": \"software A\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"type\": {\n"
			+ "                \"coding\": [\n"
			+ "                    {\n"
			+ "                        \"system\": \"http://dati.ente_esempio.it/dataset/tipo-documento-spesa730\",\n"
			+ "                        \"code\": \"F\",\n"
			+ "                        \"display\": \"Fattura\"\n"
			+ "                    }\n"
			+ "                ]\n"
			+ "            },\n"
			+ "            \"value\": \"2021/10/A000123\"\n"
			+ "        }\n"
			+ "    ],\n"
			+ "    \"status\": \"issued\",\n"
			+ "    \"recipient\": {\n"
			+ "        \"type\": \"Patient\",\n"
			+ "        \"identifier\": {\n"
			+ "            \"system\": \"http://hl7.it/sid/codiceFiscale\",\n"
			+ "            \"value\": \"AAABBB90C12D612X\"\n"
			+ "        },\n"
			+ "        \"display\": \"Anagrafe delle persone fisiche\"\n"
			+ "    },\n"
			+ "    \"date\": \"2021-09-23\",\n"
			+ "    \"participant\": [\n"
			+ "        {\n"
			+ "            \"role\": {\n"
			+ "                \"coding\": [\n"
			+ "                    {\n"
			+ "                        \"system\": \"http://dati.ente_esempio.it/dataset/ruolistruttura\",\n"
			+ "                        \"code\": \"1\",\n"
			+ "                        \"display\": \"Riferimento struttura per 730\"\n"
			+ "                    }\n"
			+ "                ]\n"
			+ "            },\n"
			+ "            \"actor\": {\n"
			+ "                \"type\": \"RelatedPerson\",\n"
			+ "                \"identifier\": {\n"
			+ "                    \"system\": \"http://hl7.it/sid/codiceFiscale\",\n"
			+ "                    \"value\": \"AAABBB90C12D612X\"\n"
			+ "                },\n"
			+ "                \"display\": \"Anagrafe delle persone fisiche\"\n"
			+ "            }\n"
			+ "        }\n"
			+ "    ],\n"
			+ "    \"issuer\": {\n"
			+ "        \"type\": \"Organization\",\n"
			+ "        \"identifier\": {\n"
			+ "            \"system\": \"http://hl7.it/sid/partitaIva\",\n"
			+ "            \"value\": \"09876543213\"\n"
			+ "        },\n"
			+ "        \"display\": \"Anagrafe delle strutture sanitarie\"\n"
			+ "    },\n"
			+ "    \"lineItem\": [\n"
			+ "        {\n"
			+ "            \"sequence\": 1,\n"
			+ "            \"chargeItemCodeableConcept\": {\n"
			+ "                \"coding\": [\n"
			+ "                    {\n"
			+ "                        \"system\": \"http://dati.ente_esempio.it/dataset/aliquotaIVA\",\n"
			+ "                        \"code\": \"22\"\n"
			+ "                    },\n"
			+ "                    {\n"
			+ "                        \"system\": \"http://dati.ente_esempio.it/dataset/flagtipospesa\",\n"
			+ "                        \"code\": \"2\"\n"
			+ "                    }\n"
			+ "                ]\n"
			+ "            },\n"
			+ "            \"priceComponent\": [\n"
			+ "                {\n"
			+ "                    \"type\": \"base\",\n"
			+ "                    \"code\": {\n"
			+ "                        \"coding\": [\n"
			+ "                            {\n"
			+ "                                \"system\": \"http://dati.ente_esempio.it/dataset/tipospesa\",\n"
			+ "                                \"code\": \"SR\"\n"
			+ "                            }\n"
			+ "                        ]\n"
			+ "                    },\n"
			+ "                    \"amount\": {\n"
			+ "                        \"value\": 122.00\n"
			+ "                    }\n"
			+ "                }\n"
			+ "            ]\n"
			+ "        }\n"
			+ "    ],\n"
			+ "    \"totalNet\": {\n"
			+ "        \"value\": 100.00\n"
			+ "    },\n"
			+ "    \"totalGross\": {\n"
			+ "        \"value\": 122.00\n"
			+ "    },\n"
			+ "    \"note\": [\n"
			+ "        {\n"
			+ "            \"time\": \"2021-09-20\",\n"
			+ "            \"text\": \"1\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"text\": \"SI\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"text\": \"0\"\n"
			+ "        }\n"
			+ "    ]\n";
	private static String requestValidContent = "{\n" + requestContent + "}";
	private static String requestInvalidContent = "{\n" + 
			"    \"resourceNonEsistente\": \"Invoice\",\n" +
			requestContent + "}";
	
	private static String contentType = "application/fhir+json";
	
	static void test(Logger logCore, byte[] payload, HttpRequestMethod method, String resource, String errore) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);
		
		int tempoMax = 1000; // su oracle impiega piu' tempo
		if(Utils.isJenkins()) {
			tempoMax = 1500; 
		}
		
		long now = DateManager.getTimeMillis();
		
		long finish = _test(logCore, payload, method, resource, errore);
		
		long latenzaPreCache = finish - now;
		
		logCore.info("Latenza pre-cache: "+latenzaPreCache+"ms");
		
		now = DateManager.getTimeMillis();
		
		finish = _test(logCore, payload, method, resource, errore);
		
		long latenzaAfterCache = finish - now;
		
		String msg = "Latenza after-cache: "+latenzaAfterCache+"ms";
		logCore.info(msg);
		
		assertTrue(msg+" < "+tempoMax, (latenzaAfterCache<tempoMax));
		
		
		
		// rieseguo il test in parallelo
		
		tempoMax = 4000; // su oracle impiega piu' tempo
		if(Utils.isJenkins()) {
			tempoMax = 5000; 
		}
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);
		
		now = DateManager.getTimeMillis();
		
		List<Long> finishParallel = _testParallel(logCore, payload, method, resource, errore); 
		
		for (Long l : finishParallel) {
			latenzaPreCache = l - now;
			logCore.info("[parallel] Latenza pre-cache: "+latenzaPreCache+"ms");
		}
		
		now = DateManager.getTimeMillis();
		
		finishParallel = _testParallel(logCore, payload, method, resource, errore); 
		
		for (Long l : finishParallel) {
			latenzaAfterCache = l - now;
			msg = "[parallel] Latenza after-cache: "+latenzaAfterCache+"ms";
			logCore.info(msg);
			
			assertTrue(msg+" < "+tempoMax, (latenzaAfterCache<tempoMax));
		}
		
	}
	static long _test(Logger logCore, byte[] payload, HttpRequestMethod method, String resource, String errore) throws Exception {
		

		final String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/BigInterface-SwaggerRequestValidatorTest/v1/"+resource;
		
		HttpRequest request = new HttpRequest();
		request.setMethod(method);
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		request.setUrl(sb.toString());
				
		if(payload!=null) {
			request.setContentType(contentType);
			request.setContent(payload);
		}
		
		logCore.info("Test con resource "+resource+" ...");
		HttpResponse response = HttpUtilities.httpInvoke(request);
		
		long returnDate = DateManager.getTimeMillis();
		
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		int esitoExpected = -1;
		if(errore==null) {
			assertEquals(201, response.getResultHTTPOperation());
			
			esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
						
		}
		else {
			OpenAPI30_Utils.verifyKo(response);
			
			esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_VALIDAZIONE_RICHIESTA);
		}

		DBVerifier.verify(idTransazione, esitoExpected, errore);
		
		return returnDate;
		
	}
	
	static List<Long> _testParallel(Logger logCore, byte[] payload, HttpRequestMethod method, String resource, String errore) throws Exception {

		List<Long> result = new ArrayList<>();

		final String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/BigInterface-SwaggerRequestValidatorTest/v1/"+resource;
		
		HttpRequest request = new HttpRequest();
		request.setMethod(method);
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		request.setUrl(sb.toString());
				
		if(payload!=null) {
			request.setContentType(contentType);
			request.setContent(payload);
		}
		
		logCore.info("Test con resource "+resource+" ...");
		
		int THREADS = 5;
		
		@SuppressWarnings("unchecked")
		List<HttpResponse> responses = (List<HttpResponse>) ((ArrayList<HttpResponse>) org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(request, THREADS)).clone();
		for (var resp : responses) {
			
			long returnDate = DateManager.getTimeMillis();
			result.add(returnDate);
			
			String idTransazione = resp.getHeaderFirstValue("GovWay-Transaction-ID");
			assertNotNull(idTransazione);
			
			int esitoExpected = -1;
			if(errore==null) {
				assertEquals(201, resp.getResultHTTPOperation());
				
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
							
			}
			else {
				OpenAPI30_Utils.verifyKo(resp);
				
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_VALIDAZIONE_RICHIESTA);
			}
				
			DBVerifier.verify(idTransazione, esitoExpected, errore);
		}	
		
		return result;
		
	}

	
}
