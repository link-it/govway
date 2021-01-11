/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.occupazione_banda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.HeaderValues;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.w3c.dom.Element;

public class SoapTest extends ConfigLoader {
	
	private static final int requestSizeBytes = 512;
	
	@Test
	public void perMinutoErogazione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.MINUTO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.MINUTO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
 
		
		Utils.waitForNewMinute();
		
		String body = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:Minuto xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				generatePayload(requestSizeBytes) +
				"        </ns2:Minuto>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/OccupazioneBandaSoap/v1");
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 60);		
		
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);		
	}
	
	
	@Test
	public void perMinutoDefaultErogazione() throws Exception {
		
		final int sogliaKb = 1024;
		final int requestToPass = 3;
		final int requestSizeBytes = (sogliaKb*1000/ (requestToPass-1))/4;
		
		logRateLimiting.info("Request size bytes: " + requestSizeBytes);


		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.MINUTODEFAULT);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.MINUTODEFAULT);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
 
		
		Utils.waitForNewMinute();
		
		String body = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:MinutoDefault xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				generatePayload(requestSizeBytes) +
				"        </ns2:MinutoDefault>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/OccupazioneBandaSoap/v1");
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 1024, 60);		
		
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);		
	}
	
	
	@Test
	public void orarioErogazione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.ORARIO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.ORARIO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
 
		Utils.waitForNewHour();
		
		String body = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:Orario xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				generatePayload(requestSizeBytes) +
				"        </ns2:Orario>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/OccupazioneBandaSoap/v1");
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 3600);		
		
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);		
	}
	
	
	@Test
	public void giornalieroErogazione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.GIORNALIERO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.GIORNALIERO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
 
		
		Utils.waitForNewDay();
		
		String body = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:Giornaliero xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				generatePayload(requestSizeBytes) +
				"        </ns2:Giornaliero>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/OccupazioneBandaSoap/v1");
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 86400);		
		
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);		
	}
	
	
	@Test
	public void perMinutoFruizione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.MINUTO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.MINUTO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
		
		Utils.waitForNewMinute();
		
		String body = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:Minuto xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				generatePayload(requestSizeBytes) +
				"        </ns2:Minuto>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/OccupazioneBandaSoap/v1");
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		
		responses.addAll(Utils.makeSequentialRequests(request, 1));
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		checkAssertions(responses, 5, 60);		
		
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);		
	}
	
	
	@Test
	public void perMinutoDefaultFruizione() throws Exception {
		
		final int sogliaKb = 1024;
		final int requestToPass = 3;
		final int requestSizeBytes = (sogliaKb*1000/ (requestToPass-1))/4;
		
		logRateLimiting.info("Request size bytes: " + requestSizeBytes);

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.MINUTODEFAULT);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.MINUTODEFAULT);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
		
		Utils.waitForNewMinute();
		
		String body = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:MinutoDefault xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				generatePayload(requestSizeBytes) +
				"        </ns2:MinutoDefault>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/OccupazioneBandaSoap/v1");
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, requestToPass);
		
		responses.addAll(Utils.makeSequentialRequests(request, 1));
		Utils.waitForZeroActiveRequests(idPolicy, requestToPass);
		checkAssertions(responses, 1024, 60);		
		
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);		
	}
	
	
	@Test
	public void orarioFruizione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.ORARIO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.ORARIO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
 
		Utils.waitForNewHour();
		
		String body = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:Orario xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				generatePayload(requestSizeBytes) +
				"        </ns2:Orario>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/OccupazioneBandaSoap/v1");
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 3600);		
		
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);		
	}
	
	
	@Test
	public void giornalieroFruizione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.GIORNALIERO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaSoap", PolicyAlias.GIORNALIERO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
 
		
		Utils.waitForNewDay();
		
		String body = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:Giornaliero xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				generatePayload(requestSizeBytes) +
				"        </ns2:Giornaliero>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/OccupazioneBandaSoap/v1");
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 86400);		
		
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);		
	}
	
	private void checkAssertions(Vector<HttpResponse> responses, int maxKb, int windowSize) throws Exception {
		
		responses.forEach(r -> { 			
				assertNotEquals(null,Integer.valueOf(r.getHeader(Headers.BandWidthQuotaReset)));
				Utils.checkXLimitHeader(logRateLimiting, Headers.BandWidthQuotaLimit, r.getHeader(Headers.BandWidthQuotaLimit), maxKb);
				
				if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
					Map<Integer,Integer> windowMap = Map.of(windowSize,maxKb);							
					Utils.checkXLimitWindows(r.getHeader(Headers.BandWidthQuotaLimit), maxKb, windowMap);
				}
			});

		// Tutte le richieste tranne una devono restituire 200
		
		assertEquals(responses.size()-1, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());

		
		// La richiesta fallita deve avere status code 429
		
		HttpResponse failedResponse = responses.stream().filter(r -> r.getResultHTTPOperation() == 429).findAny()
				.orElse(null);
		assertNotEquals(null,failedResponse);
		
		String body = new String(failedResponse.getContent());
		logRateLimiting.info(body);
		
		Element element = Utils.buildXmlElement(failedResponse.getContent());
		Utils.matchLimitExceededSoap(element);
		
		assertEquals("0", failedResponse.getHeader(Headers.BandWidthQuotaRemaining));
		assertEquals(HeaderValues.LimitExceeded, failedResponse.getHeader(Headers.GovWayTransactionErrorType));
		assertEquals(HeaderValues.ReturnCodeTooManyRequests, failedResponse.getHeader(Headers.ReturnCode));
		assertNotEquals(null, failedResponse.getHeader(Headers.RetryAfter));
	}
	
	
	private String generatePayload(int payloadSize) {
		byte[] ret = new byte[payloadSize];
		Arrays.fill(ret, (byte) 97);
		return new String(ret);
	}

}
