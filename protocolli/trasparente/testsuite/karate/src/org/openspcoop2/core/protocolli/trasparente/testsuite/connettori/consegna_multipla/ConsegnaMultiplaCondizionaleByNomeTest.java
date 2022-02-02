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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla;

import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.checkAll200;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.buildRequestAndExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectationsFinal;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreIniziato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setIntersection;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.statusCode2xxVsConnettori;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * 
 * @author froggo
 *
 */
public class ConsegnaMultiplaCondizionaleByNomeTest extends ConfigLoader {
	
	@Test
	public void consegnaMultiplaFallita() {
		// TODO: 1 -Manca il campo identificativo.
		//					2- Nome connettore errato
		//					3 - Utilizzo connettore disabiltato
		//					4 - Utilizzo connettore con scheduling disabilitato.
	}
	
	@Test
	public void headerHttp() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeHeaderHttp";
		final String soapContentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		// Prima costruisco le richieste normalmente
		for (var entry : statusCode2xxVsConnettori.entrySet()) {
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue(), soapContentType) );
		}
		
		/*for (var entry : statusCode4xxVsConnettori.entrySet()) {
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue()));
		}*/

		/*for (var entry : statusCode5xxVsConnettori.entrySet()) { 
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue()));
		}*/
		
		// Successivamente associo ad ogni richiesta un filtro e rimuovo dal set di connettori ok e 
		// dall set di connettori errore di i connettori filtrati via dalla consegna condizionale
		for (int i = 0; i < requestsByKind.size(); i++) {
			String connettore = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			Set<String> connettoriPool = Set.of(connettore);

			RequestAndExpectations current = requestsByKind.get(i);
			current.request.addHeader(Common.HEADER_ID_CONDIZIONE, connettore);
			current.connettoriSuccesso = setIntersection(current.connettoriSuccesso, connettoriPool);
			current.connettoriFallimento = setIntersection(current.connettoriFallimento, connettoriPool);
		}

		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla
				.makeRequestsByKind(requestsByKind, 1);

		// Tutte le richieste indipendentemente dal tipo devono essere state prese in
		// consegna e lo scheduling inizia
		// su tutti i connettori abilitati e filtrati dalla consegna condizionale
		for (var requestAndExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestAndExpectation);

			checkAll200(responses);
			Set<String> connettoriGestiti = new HashSet<>(requestAndExpectation.connettoriSuccesso);
			connettoriGestiti.addAll(requestAndExpectation.connettoriFallimento);
			CommonConsegnaMultipla.checkStatoConsegna(responses, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA, connettoriGestiti.size());

			// Deve essere la fusione dei connettoriOk e i connettoriErrore\conettoriScheduling Disabilitato
			// NO, quando si usa la consegna multipla per nome, siccome viene identificato un solo connettore il sistema torna a lavorare 
			// con la consegna semplice sul connettore individuato. L'esito della tranzazione diventa 0 (ESITO_OK) 
			// Chiedi ad andrea se va bene così, ma immagino di si.
			
			checkSchedulingConnettoreIniziato(responses, connettoriGestiti);
			// // TODO: Controlla che lo scheduling sia arrivato solo ai connettori
			// specificati, modifico la checkSchedulingConnettoreInziato?
		}

		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2 * CommonConsegnaMultipla.intervalloControllo);

		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				checkRequestExpectations(requestAndExpectation, response);
			}
		}

		// Attendo l'intervallo di riconsegna e controllo che il contatore delle
		// consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2 * CommonConsegnaMultipla.intervalloControlloFallite);
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				checkRequestExpectationsFinal(requestAndExpectation, response);
			}
		}

	}
		
}
