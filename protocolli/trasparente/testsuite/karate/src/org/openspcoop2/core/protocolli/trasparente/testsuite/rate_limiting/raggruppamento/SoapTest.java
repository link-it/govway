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


package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.raggruppamento;

import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Credenziali;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.SoapBodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

public class SoapTest extends ConfigLoader {
	
	private static final String basePath = System.getProperty("govway_base_path");
	private static final String testIdHeader = "GovWay-TestSuite-RL-Grouping";	// TODO: Mettilo globale
	
	@Test
	public void perRichiedenteFruizione() {
		
		final String erogazione = "RaggruppamentoRichiedenteSoap";
		final String urlServizio =  basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.setUsername(Credenziali.applicativoSITFFiltrato.username);
		requestGroup1.setPassword(Credenziali.applicativoSITFFiltrato.password);
		requestGroup1.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/soap+xml");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.setUsername(Credenziali.applicativoSITFNonFiltrato.username);
		requestGroup2.setPassword(Credenziali.applicativoSITFNonFiltrato.password);
		requestGroup2.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/soap+xml");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.setUsername(Credenziali.applicativoSITFRuoloFiltrato.username);
		requestGroup3.setPassword(Credenziali.applicativoSITFRuoloFiltrato.password);
		requestGroup3.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		makeAndCheckGroupRequests(TipoServizio.FRUIZIONE, PolicyAlias.ORARIO, erogazione, requests);		
	}
	
	@Test
	public void perRichiedenteErogazione() {
		
		final String erogazione = "RaggruppamentoRichiedenteSoap";
		final String urlServizio =  basePath + "/SoggettoInternoTest/"+erogazione+"/v1";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.setUsername(Credenziali.applicativoRuoloFiltrato.username);
		requestGroup1.setPassword(Credenziali.applicativoRuoloFiltrato.password);
		requestGroup1.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/soap+xml");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.setUsername(Credenziali.applicativoRuoloFiltrato2.username);
		requestGroup2.setPassword(Credenziali.applicativoRuoloFiltrato2.password);
		requestGroup2.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/soap+xml");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.setUsername(Credenziali.soggettoRuoloFiltrato.username);
		requestGroup3.setPassword(Credenziali.soggettoRuoloFiltrato.password);
		requestGroup3.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());

		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		makeAndCheckGroupRequests(TipoServizio.EROGAZIONE, PolicyAlias.ORARIO, erogazione, requests);
		
	}
	
	
	
	private static void makeAndCheckGroupRequests(TipoServizio tipoServizio, PolicyAlias policy, String erogazione, HttpRequest[] requests) {
		
		final int maxRequests = 5;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		
		final String idPolicy = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
				
		
		Utils.resetCounters(idPolicy);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Faccio le richieste tra i gruppi in maniera simultanea
		//	per assicurarmi che il conteggio sia corretto anche in caso di
		//	richieste parallele e quindi codice concorrente lato server
		
		final Vector<HttpResponse> responsesOk = new Vector<>();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxRequests*requests.length);

		for (int i = 0; i < maxRequests; i++) {
			
			for(var request : requests) {
				executor.execute(() -> {
					try {
						logRateLimiting.info(request.getMethod() + " " + request.getUrl());
						responsesOk.add(HttpUtilities.httpInvoke(request));
						logRateLimiting.info("Richiesta effettuata..");
					} catch (UtilsException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				});
			}
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		responsesOk.forEach(r -> {
			logRateLimiting.info("statusCode: " + r.getResultHTTPOperation());
			logRateLimiting.info("headers: " + r.getHeaders());
		});
		
		Utils.waitForZeroGovWayThreads();
		logRateLimiting.info(Utils.getPolicy(idPolicy));
		// Le richieste di sopra devono andare tutte bene e devono essere conteggiate
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest.checkOkRequests(responsesOk, windowSize, maxRequests);
		
		
		final Vector<HttpResponse> responsesFailed = new Vector<>();
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxRequests*requests.length);
		for (int i = 0; i < maxRequests; i++) {
			
			for(var request : requests) {
			executor.execute(() -> {
				try {
					logRateLimiting.info(request.getMethod() + " " + request.getUrl());
					responsesFailed.add(HttpUtilities.httpInvoke(request));
					logRateLimiting.info("Richiesta effettuata..");
				} catch (UtilsException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
			}
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		responsesFailed.forEach(r -> {
			logRateLimiting.info("statusCode: " + r.getResultHTTPOperation());
			logRateLimiting.info("headers: " + r.getHeaders());
		});
		Utils.waitForZeroGovWayThreads();
		logRateLimiting.info(Utils.getPolicy(idPolicy));
		// Tutte le richieste di sopra falliscono perchè il limit è stato raggiunto dal primo set di richieste
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest.checkFailedRequests(responsesFailed, windowSize, maxRequests);
		
	}
	
	
	

	@Test
	public void perParametroUrlErogazione() {
		perParametroUrl(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void perParametroUrlFruizione() {
		perParametroUrl(TipoServizio.FRUIZIONE);
	}
	
	@Test
	public void perContenutoErogazione() {
		perContenuto(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void perContenutoFruizione() {
		perContenuto(TipoServizio.FRUIZIONE);
	}
	
	@Test
	public void perUrlInvocazioneErogazione() {
		perUrlInvocazione(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void perUrlInvocazioneFruizione() {
		perUrlInvocazione(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void perHeaderErogazione() {
		perHeader(TipoServizio.EROGAZIONE);
	}
	
	
	@Test
	public void perHeaderFruizione() {
		perHeader(TipoServizio.FRUIZIONE);
	}
	
	@Test
	public void perHeaderXForwardedForErogazione() throws UtilsException {
		HttpUtilities.getClientAddressHeaders().forEach( headerName ->
				perHeaderXForwardedFor(TipoServizio.EROGAZIONE, headerName)
			);
	}
	
	@Test
	public void perHeaderXForwardedForFruizione() throws UtilsException {
		HttpUtilities.getClientAddressHeaders().forEach( headerName ->
			perHeaderXForwardedFor(TipoServizio.FRUIZIONE, headerName)
		);
	}
	
	
	@Test
	public void perSoapActionErogazione() {
		perSoapAction(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void perSoapActionFruizione() {
		perSoapAction(TipoServizio.FRUIZIONE);
	}
	
	
	@Test
	public void perTokenErogazione() throws UtilsException {
		perToken(TipoServizio.EROGAZIONE);
	}
	
	
	@Test
	public void perTokenFruizione() throws UtilsException {
		perToken(TipoServizio.FRUIZIONE);
	}
	

	@Test
	public void perRisorsaErogazione() {
		perRisorsa(TipoServizio.EROGAZIONE);
	}
	
	
	@Test
	public void perRisorsaFruizione() {
		perRisorsa(TipoServizio.FRUIZIONE);
	}
	
	
	private static void perRisorsa(TipoServizio tipoServizio) {
		
		final String erogazione = "RaggruppamentoSoap";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setContent(SoapBodies.get(PolicyAlias.MINUTO).getBytes());
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/soap+xml");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/soap+xml");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.setContent(SoapBodies.get(PolicyAlias.GIORNALIERO).getBytes());
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTRORISORSA.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTRORISORSA, erogazione, requests);		
	}
	
	
	public static void perToken(TipoServizio tipoServizio) throws UtilsException {
		final String erogazione = "RaggruppamentoTokenSoap";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		// Signature Json
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
		Properties signatureProps = new Properties();
		signatureProps.put("rs.security.keystore.file", "/etc/govway/keys/pa.p12");
		signatureProps.put("rs.security.keystore.type","pkcs12");
		signatureProps.put("rs.security.keystore.alias","paP12");
		signatureProps.put("rs.security.keystore.password","keypa");
		signatureProps.put("rs.security.key.password","keypa");
		signatureProps.put("rs.security.signature.algorithm","RS256");
		signatureProps.put("rs.security.signature.include.cert","false");
		signatureProps.put("rs.security.signature.include.public.key","false");
		signatureProps.put("rs.security.signature.include.key.id","true");
		signatureProps.put("rs.security.signature.include.cert.sha1","false");
		signatureProps.put("rs.security.signature.include.cert.sha256","false");
		
		JsonSignature jsonSignature = null;
		jsonSignature = new JsonSignature(signatureProps, options);
		
		String token1 = "{\n"+
		  "\"sub\": \"gruppo1\",\n"+
		  "\"iss\": \"example.org\",\n"+
		  "\"preferred_username\": \"John Doe\",\n"+
		  "\"azp\": \"clientTest\",\n"+
		  "\"iat\": 1516239022\n"+
		"}";
		String token1Signed = jsonSignature.sign(token1);
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio+"?access_token="+token1Signed);
		requestGroup1.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		String token2 = "{\n"+
				  "\"sub\": \"gruppo2\",\n"+
				  "\"iss\": \"example.org\",\n"+
				  "\"preferred_username\": \"John Doe\",\n"+
				  "\"azp\": \"clientTest\",\n"+
				  "\"iat\": 1516239022\n"+
				"}";
				
		String token2Signed = jsonSignature.sign(token2);
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/soap+xml");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio+"?access_token="+token2Signed);
		requestGroup2.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		String token3 = "{\n"+
				  "\"sub\": \"gruppo3\",\n"+
				  "\"iss\": \"example.org\",\n"+
				  "\"preferred_username\": \"John Doe\",\n"+
				  "\"azp\": \"clientTest\",\n"+
				  "\"iat\": 1516239022\n"+
				"}";
				
		String token3Signed = jsonSignature.sign(token3);
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/soap+xml");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio+"?access_token="+token3Signed);
		requestGroup3.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());

		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		

		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.ORARIO, erogazione, requests);	
	}
	
	
	private static void perSoapAction(TipoServizio tipoServizio) {
		
		final String erogazione = "RaggruppamentoSoap";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.setContentType("application/soap+xml; action=Minuto");
		requestGroup1.setContent(SoapBodies.get(PolicyAlias.MINUTO).getBytes());
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/soap+xml; action=Orario");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/soap+xml; action=Giornaliero");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.setContent(SoapBodies.get(PolicyAlias.GIORNALIERO).getBytes());
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROSOAPACTION.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROSOAPACTION, erogazione, requests);		
	}
	

	public static void perContenuto(TipoServizio tipoServizio) {
		
		final String erogazione = "RaggruppamentoSoap";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		String body1 =  "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:Orario xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				"			<Gruppo>gruppo1</Gruppo>" +	
				"        </ns2:Orario>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.setContent(body1.getBytes());
		
		
		String body2 =  "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:Orario xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				"			<Gruppo>gruppo2</Gruppo>" +
				"        </ns2:Orario>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/soap+xml");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.setContent(body2.getBytes());
		
		String body3 =  "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
				"    <soap:Body>\n" + 
				"        <ns2:Orario xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
				"			<Gruppo>gruppo3</Gruppo>" +
				"        </ns2:Orario>\n" + 
				"    </soap:Body>\n" + 
				"</soap:Envelope>";
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/soap+xml");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.setContent(body3.getBytes());
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		// Dico quale policy di rate limiting deve attivarsi, perchè sono 
		// filtrate per header
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROCONTENUTO.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROCONTENUTO, erogazione, requests);
	}
	
	public static void perParametroUrl(TipoServizio tipoServizio) {
		
		final String erogazione = "RaggruppamentoSoap";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio+"?gruppo=gruppo1");
		requestGroup1.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/soap+xml");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio+"?gruppo=gruppo2");
		requestGroup2.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/soap+xml");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio+"?gruppo=gruppo3");
		requestGroup3.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROPARAMETROURL.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROPARAMETROURL, erogazione, requests);
		
	}
	
	
	public static void perUrlInvocazione(TipoServizio tipoServizio) {
		final String erogazione = "RaggruppamentoSoap";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio + "/Minuto");
		requestGroup1.setContent(SoapBodies.get(PolicyAlias.MINUTO).getBytes());
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/soap+xml");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio + "/Giornaliero");
		requestGroup2.setContent(SoapBodies.get(PolicyAlias.GIORNALIERO).getBytes());
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/soap+xml");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio + "/Orario");
		requestGroup3.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		// Dico quale policy di rate limiting deve attivarsi, perchè sono 
		// filtrate per header
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROURLINVOCAZIONE.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROURLINVOCAZIONE, erogazione, requests);
	}
	
	
	public static void perHeader(TipoServizio tipoServizio) {
		final String erogazione = "RaggruppamentoSoap";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.addHeader("Gruppo", "gruppo1");
		requestGroup1.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());

		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/soap+xml");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.addHeader("Gruppo", "gruppo2");
		requestGroup2.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());

		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/soap+xml");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.addHeader("Gruppo", "gruppo3");
		requestGroup3.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());

		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		// Dico quale policy di rate limiting deve attivarsi, perchè sono 
		// filtrate per header
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROHEADER.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROHEADER, erogazione, requests);	
	}

	
	public static void perHeaderXForwardedFor(TipoServizio tipoServizio, String headerName) {
		final String erogazione = "RaggruppamentoSoap";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/soap+xml");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.addHeader(headerName, "gruppo1");
		requestGroup1.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());

		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/soap+xml");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.addHeader(headerName, "gruppo2");
		requestGroup2.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());

		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/soap+xml");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.addHeader(headerName, "gruppo3");
		requestGroup3.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());

		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		// Dico quale policy di rate limiting deve attivarsi, perchè sono 
		// filtrate per header
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROXFORWARDEDFOR.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROXFORWARDEDFOR, erogazione, requests);
	}
}
