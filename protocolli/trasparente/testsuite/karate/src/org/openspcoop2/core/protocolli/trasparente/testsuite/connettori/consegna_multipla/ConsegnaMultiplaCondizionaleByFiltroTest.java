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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.buildRequestAndExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectationsFinal;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreIniziato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkStatoConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setIntersection;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.statusCode2xxVsConnettori;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.statusCode4xxVsConnettori;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.statusCode5xxVsConnettori;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
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
public class ConsegnaMultiplaCondizionaleByFiltroTest extends ConfigLoader {
	// Prefisso e suffisso? TODOS
	
	// Prima di ogni test  fermo le attuali riconsegne in atto.
	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		// TODO: Pulisci la cartella delle richieste dei coinnettori file, ma fallo dentro Common.fermaRiconsegne
	}
	
	
	@Test
	public void headerHttp() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroHeaderHttp";
		final String soapContentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		// Prima costruisco le richieste normalmente
		for (var entry : statusCode2xxVsConnettori.entrySet()) {
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue(), soapContentType));
		}
		
	/*	for (var entry : statusCode4xxVsConnettori.entrySet()) {
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue(), soapContentType));
		}
		
		for (var entry : statusCode5xxVsConnettori.entrySet()) { 
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue(), soapContentType));
		}*/
		
		// Successivamente associo ad ogni richiesta un filtro e rimuovo dal set di connettori ok e 
		// dal set di connettori errore di i connettori filtrati via dalla consegna condizionale
		for(int i=0;i<requestsByKind.size();i++) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			
			RequestAndExpectations current = requestsByKind.get(i);
			current.request.addHeader(Common.HEADER_ID_CONDIZIONE, Common.filtriPools.get(pool).get(0));
			current.connettoriSuccesso = setIntersection(current.connettoriSuccesso,connettoriPool);
			current.connettoriFallimento = setIntersection(current.connettoriFallimento, connettoriPool);
		}
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia 
		// su tutti i connettori abilitati e filtrati dalla consegna condizionale
		for (var requestAndExpectation : responsesByKind.keySet() ) {
			var responses = responsesByKind.get(requestAndExpectation);
			checkAll200(responses);
			
			Set<String> connettoriGestiti = new HashSet<>(requestAndExpectation.connettoriSuccesso);
			connettoriGestiti.addAll(requestAndExpectation.connettoriFallimento);

			checkStatoConsegna(responses, ESITO_CONSEGNA_MULTIPLA, connettoriGestiti.size());

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
