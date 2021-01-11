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


package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.flusso;

import java.util.Vector;

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
	
	/**
	 * Le policy per una metrica vengono valutate dall'alto verso il basso.
	 * Quando il flusso di elaborazione per una policy è impostato su "Interrompi", nel momento in cui una policy per quella metrica viene
	 * conteggtiata, si interrompe il controllo delle policy.
	 * 
	 * Impostando l'elaborazione a "Prosegui", il conteggio continua.
	 * Questi test verificano che il conteggio continui.
	 */
	private static final String basePath = System.getProperty("govway_base_path");
	
	@Test
	public void controlloFlussoErogazione() {
		controlloFlusso(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void controlloFlussoFruizione() {
		controlloFlusso(TipoServizio.FRUIZIONE);
	}
	
	public void controlloFlusso(TipoServizio tipoServizio) {
		
		final String erogazione = "FlussoValutazionePolicySoap";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		PolicyAlias policy1 = PolicyAlias.ORARIO;				// Elaborazione: Prosegui
		PolicyAlias policy2 = PolicyAlias.MINUTO;				// Elaborazione: Interrompi
		PolicyAlias policy3 = PolicyAlias.GIORNALIERO;			// Elaborazione: Prosegui
		
		// int maxRequestsPolicy1 = 10;
		int maxRequestsPolicy3 = 5;
		
		// int windowSizePolicy1 = Utils.getPolicyWindowSize(policy1);
		int windowSizePolicy3 = Utils.getPolicyWindowSize(policy3);
		
		final String idPolicy1 = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy1)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy1);
				
		final String idPolicy2 = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy2)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy2);
				
		final String idPolicy3 = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy3)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy3);
		
		Utils.resetCounters(idPolicy1);
		Utils.checkConditionsNumeroRichieste(idPolicy1, 0, 0, 0);
		
		Utils.resetCounters(idPolicy2);
		Utils.checkConditionsNumeroRichieste(idPolicy2, 0, 0, 0);
		
		Utils.resetCounters(idPolicy3);
		Utils.checkConditionsNumeroRichieste(idPolicy3, 0, 0, 0);

		// Se scatta l'attesa per il nuovo giorno allora la chiamata di dopo
		// non crea attesa.
		Utils.waitForNewDay();
		Utils.waitForNewHour();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(urlServizio);
		request.setContent(SoapBodies.get(PolicyAlias.ORARIO).getBytes());
		
		// Faccio maxRequestsPolicy2 Richieste e verifico che vengano conteggiate
		Vector<HttpResponse> responsesOk = Utils.makeSequentialRequests(request, maxRequestsPolicy3);
		
		// Gli headers vengono valorizzati con i dati della policy più stringente, per questo passo le info sulla policy 3
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest.checkOkRequests(responsesOk, windowSizePolicy3, maxRequestsPolicy3);
		
		Utils.checkConditionsNumeroRichieste(idPolicy1, 0, maxRequestsPolicy3, 0);
		Utils.checkConditionsNumeroRichieste(idPolicy2, 0, 0, 0);
		Utils.checkConditionsNumeroRichieste(idPolicy3, 0, maxRequestsPolicy3, 0);
		
		
		Vector<HttpResponse> responsesFailed = Utils.makeSequentialRequests(request, 3);
		
		Utils.checkConditionsNumeroRichieste(idPolicy1, 0, maxRequestsPolicy3, 0);
		Utils.checkConditionsNumeroRichieste(idPolicy2, 0, 0, 0);
		Utils.checkConditionsNumeroRichieste(idPolicy3, 0, maxRequestsPolicy3, 3);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest.checkFailedRequests(responsesFailed, windowSizePolicy3, maxRequestsPolicy3);

	}

}
