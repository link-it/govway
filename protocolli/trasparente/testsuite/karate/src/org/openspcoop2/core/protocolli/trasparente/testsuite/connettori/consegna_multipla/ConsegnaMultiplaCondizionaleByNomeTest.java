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

import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.buildRequestAndExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkPresaInConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectationsFinal;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreIniziato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setIntersection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * 
 * @author froggo
 *
 */
public class ConsegnaMultiplaCondizionaleByNomeTest extends ConfigLoader {
	
	@Test
	public void headerHttp() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeHeaderHttp";
		
		// TODO: Configura l'erogazione come hai fatto per il test su TestConsegnaMultiplaVarieCombinazioniDiRegole
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		Map<Integer, Set<String>> statusCode2xxVsConnettori  = Map
				.of(200, Set.of(CONNETTORE_1,CONNETTORE_2),
					  201, Set.of(CONNETTORE_1, CONNETTORE_3),
					  202, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
					  206, Set.of(CONNETTORE_1),
					  299, Set.of(CONNETTORE_1));
		
		Map<Integer, Set<String>> statusCode4xxVsConnettori  = Map
				.of(400, Set.of(CONNETTORE_1,CONNETTORE_2),
					  401, Set.of(CONNETTORE_1, CONNETTORE_3),
					  402, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
					  406, Set.of(CONNETTORE_1),
					  499, Set.of(CONNETTORE_1));
		
		Map<Integer, Set<String>> statusCode5xxVsConnettori  = Map
				.of(500, Set.of(CONNETTORE_1,CONNETTORE_2),
					  501, Set.of(CONNETTORE_1, CONNETTORE_3),
					  502, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
					  506, Set.of(CONNETTORE_1),
				  	  599, Set.of(CONNETTORE_1));
		
		// Prima costruisco le richieste normalmente
		for (var entry : statusCode2xxVsConnettori.entrySet()) {
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue()));
		}
		
		// Successivamente associo ad ogni richiesta un filtro e rimuovo dal set di connettori ok e 
		// dall set di connettori errore di i connettori filtrati via dalla consegna condizionale
		for(int i=0;i<requestsByKind.size();i++) {
			String connettore = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			Set<String> connettoriPool = Set.of(connettore);
			
			RequestAndExpectations current = requestsByKind.get(i);
			current.request.addHeader(Common.HEADER_ID_CONDIZIONE, connettore);
			current.connettoriSuccesso = setIntersection(current.connettoriSuccesso,connettoriPool);
			current.connettoriFallimento = setIntersection(current.connettoriFallimento, connettoriPool);
		}
		
		/*for (var entry : statusCode4xxVsConnettori.entrySet()) {
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue()));
		}*/
	
		/*for (var entry : statusCode5xxVsConnettori.entrySet()) { 
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue()));
		}*/
		
		
	Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia 
		// su tutti i connettori abilitati e filtrati dalla consegna condizionale
		for (var requestAndExpectation : responsesByKind.keySet() ) {
			var responses = responsesByKind.get(requestAndExpectation);
			
			checkPresaInConsegna(responses);
			Set<String> connettoriGestiti = new HashSet<>(requestAndExpectation.connettoriSuccesso);
			connettoriGestiti.addAll(requestAndExpectation.connettoriFallimento);
			
			// Deve essere la fusione dei connettoriOk e i connettoriErrore\conettoriScheduling Disabilitato
			checkSchedulingConnettoreIniziato(responses, connettoriGestiti);
			// // TODO: Controlla che lo scheduling sia arrivato solo ai connettori specificati, modifico la checkSchedulingConnettoreInziato?
		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				checkRequestExpectations(requestAndExpectation, response);
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControlloFallite);
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				checkRequestExpectationsFinal(requestAndExpectation, response);
			}
		}
		
	}
		
}
