package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.global_policy;

import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.SoapBodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

public class SoapTest extends ConfigLoader {
	
	@Test
	public void erogazione() {
		testGlobalPolicy(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void fruizione() {
		testGlobalPolicy(TipoServizio.FRUIZIONE);
	}
	
	@BeforeClass
	public static void initTest() {
		RestTest.initTest();
	}
	
	
	static void testGlobalPolicy(TipoServizio tipoServizio) {
		// Usiamo l'erogazione già presente per il test numero_richieste
		// visto che li ci sono policy sul numero di richieste parallele passando lo header
		// GovWay-TestSuite-RL-GlobalPolicy=Orario per attivare la policy globale
		// vedo anche che succede agli headers quando c'è una policy globale+policy locale

		final int maxRequests = 5;
		final int windowSize = Utils.getPolicyWindowSize(PolicyAlias.ORARIO);
		final String idPolicy = dbUtils.getIdGlobalPolicy("Orario");
		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TempoMedioRispostaSoap/v1"
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TempoMedioRispostaSoap/v1";
		
		Utils.waitForNewHour();
		Utils.resetCounters(idPolicy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setContent(SoapBodies.get(PolicyAlias.RICHIESTE_SIMULTANEE).getBytes());
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(url);
		request.addHeader("GovWay-TestSuite-RL-GlobalPolicy", "Orario");
		
		Vector<HttpResponse> responseOk = Utils.makeSequentialRequests(request, maxRequests);
		Vector<HttpResponse> responseBlocked = Utils.makeSequentialRequests(request, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		// TODO: Gli header da controllare sono diversi.
		// X-RateLimit-Limit=10, 10;w=3600, X-RateLimit-Remaining=9
		// Sono solo questi due. Siamo sicuri? Chiedi ad andrea.
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest.checkOkRequests(responseOk, windowSize, maxRequests);
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest.checkFailedRequests(responseBlocked, windowSize, maxRequests);
	}

}
