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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.timeout;

import static org.junit.Assert.assertEquals;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_CONNECTION_TIMEOUT;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_READ_TIMEOUT;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_ERRORE_CONNECTION_TIMEOUT;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_ERRORE_READ_TIMEOUT;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkPresaInConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreIniziato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.esitoConsegnaFromStatusCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDati;
import org.openspcoop2.utils.transport.http.HttpRequest;

/**
* ConsegnaConNotificheTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ConsegnaConNotificheTest extends ConfigLoader {

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
	
	
	
	@Test
	public void connettoreConnectionTimeout() throws DriverRegistroServiziException {
		connettoreTimeout("Connection", CONNETTORE_CONNECTION_TIMEOUT, ESITO_ERRORE_CONNECTION_TIMEOUT, 
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"));
	}
	@Test
	public void connettoreReadTimeout() throws DriverRegistroServiziException {
		connettoreTimeout("Read", CONNETTORE_READ_TIMEOUT, ESITO_ERRORE_READ_TIMEOUT, 
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	
	private void connettoreTimeout(String tipo, String nomeConnettore, int esito, TipoEvento tipoEvento, String descrizioneEvento) throws DriverRegistroServiziException {
		final String erogazione = "TestConsegnaConNotificheConnettore"+tipo+"TimeoutRest";
		
		LocalDateTime dataSpedizione = LocalDateTime.now();
		
		// Va come il primo test, ci si aspetta che funzionino tutti i connettori
		// eccetto quello rotto, dove devono avvenire le rispedizioni.
		
		HttpRequest request1 = RequestBuilder.buildRestRequest(erogazione);

		var responses = Common.makeParallelRequests(request1, 5);
		
		Common.checkAll200(responses);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna		
   	   	Set<String> connettoriSchedulati = new HashSet<>(Common.setConnettoriAbilitati);
   	   	connettoriSchedulati.add(nomeConnettore);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r, connettoriSchedulati.size()));
			checkSchedulingConnettoreIniziato(r, connettoriSchedulati);	
		}
	
		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettoriSchedulati);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);
	
		// La consegna deve risultare ancora in corso per via dell'errore sul connettore rotto, con un connettore
		// in rispedizione
		String fault = "";
		String formatoFault = "";
		for (var r : responses) {
			CommonConsegnaMultipla.checkStatoConsegna(r, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			CommonConsegnaMultipla.checkSchedulingConnettoreInCorso(r, nomeConnettore, esito,0, fault, formatoFault);
			for (var connettore : Common.setConnettoriAbilitati) {				
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(r, connettore, esitoConsegnaFromStatusCode(200), 200, fault, formatoFault);
			}
		}
		
		// Eventi
		if(tipoEvento!=null) {
			
			String idEventoServizio = "SoggettoInternoTest/"+erogazione+"/v1";
			
			PolicyDati dati = new PolicyDati();
			dati.setProfilo(CostantiLabel.TRASPARENTE_PROTOCOL_NAME);
			dati.setRuoloPorta(RuoloPolicy.APPLICATIVA);
			
			IDSoggetto idErogatore = new IDSoggetto("gw", "SoggettoInternoTest");
			IDServizio idServizioObject = IDServizioFactory.getInstance().getIDServizioFromValues("gw", erogazione, idErogatore, 1);
			dati.setIdServizio(idServizioObject);
			
			dati.setConnettore(nomeConnettore);
			
			DBVerifier.checkEventiConViolazioneTimeout(idEventoServizio, tipoEvento, Optional.empty(), Optional.of(nomeConnettore), null, 
					dati,
					descrizioneEvento, dataSpedizione,
					null, logCore);
		}
	}
	
}
