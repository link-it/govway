/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeSoap;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeTestUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeTestUtils.Porta;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * XML Signature Wrapping e varianti con cifratura: la struttura dell'Envelope viene alterata dopo l'applicazione
 * di WS-Security, lasciando valida la firma (o decifrabile il contenuto cifrato).
 * Esempi: un secondo Body non firmato (o in chiaro) prima o dopo quello protetto, un secondo Header senza
 * wsse:Security che quindi non viene trattato (anche con un header mustUnderstand), l'Header spostato dopo il Body.
 *
 * Il messaggio protetto si ottiene dall'erogazione stessa: l'azione 'firma-risposta' ('cifra-risposta') applica
 * WS-Security alla risposta del backend. Il messaggio, alterato, viene poi inviato
 * - in richiesta all'erogazione, azione 'verifica-firma' ('decifra');
 * - in risposta alla fruizione, azione 'verifica-firma' ('decifra'), tramite il backend TestService.
 * Per ogni caso viene prima verificato che il messaggio protetto non alterato sia accettato.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class WSSecurityStrutturaEnvelopeTest extends ConfigLoader {

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> data() {
		StrutturaEnvelopeSoap.Caso [] casi = {
				// Body
				StrutturaEnvelopeSoap.Caso.DOPPIO_BODY, // Body protetto seguito da un Body non protetto
				StrutturaEnvelopeSoap.Caso.DOPPIO_BODY_INVERTITO, // Body non protetto seguito dal Body protetto
				StrutturaEnvelopeSoap.Caso.DOPPIO_BODY_PRIMO_VUOTO,
				StrutturaEnvelopeSoap.Caso.DOPPIO_BODY_SECONDO_VUOTO,
				StrutturaEnvelopeSoap.Caso.DOPPIO_BODY_PREFISSO_DIVERSO,
				StrutturaEnvelopeSoap.Caso.BODY_FAULT_E_BODY_PAYLOAD,
				StrutturaEnvelopeSoap.Caso.BODY_PAYLOAD_E_BODY_FAULT,
				// Header: in uno dei due non c'è wsse:Security, quindi non viene trattato
				StrutturaEnvelopeSoap.Caso.DOPPIO_HEADER,
				StrutturaEnvelopeSoap.Caso.DOPPIO_HEADER_SECONDO_DIVERSO,
				StrutturaEnvelopeSoap.Caso.DOPPIO_HEADER_PRIMO_DIVERSO,
				StrutturaEnvelopeSoap.Caso.DOPPIO_HEADER_SECONDO_MUST_UNDERSTAND,
				StrutturaEnvelopeSoap.Caso.DOPPIO_HEADER_PRIMO_MUST_UNDERSTAND,
				StrutturaEnvelopeSoap.Caso.DOPPIO_HEADER_SECONDO_VUOTO,
				StrutturaEnvelopeSoap.Caso.DOPPIO_HEADER_PRIMO_VUOTO,
				// Ordine
				StrutturaEnvelopeSoap.Caso.HEADER_DOPO_BODY,
				StrutturaEnvelopeSoap.Caso.HEADER_BODY_HEADER,
				StrutturaEnvelopeSoap.Caso.BODY_HEADER_BODY,
				StrutturaEnvelopeSoap.Caso.DOPPIO_HEADER_DOPPIO_BODY,
				StrutturaEnvelopeSoap.Caso.BODY_CON_FAULT_PRIMA_DI_HEADER,
				// Fault e altri elementi
				StrutturaEnvelopeSoap.Caso.FAULT_FIGLIO_ENVELOPE_DOPO_BODY,
				StrutturaEnvelopeSoap.Caso.ENVELOPE_FIGLIO_DOPO_BODY,
				StrutturaEnvelopeSoap.Caso.BODY_ALTRA_VERSIONE_DOPO_BODY,
				// Struttura valida: il Body protetto viene spostato e sostituito da un Body non protetto
				StrutturaEnvelopeSoap.Caso.COPERTURA_BODY_PROTETTO_IN_HEADER,
				StrutturaEnvelopeSoap.Caso.COPERTURA_BODY_PROTETTO_NEL_PAYLOAD,
		};
		Collection<Object[]> l = new ArrayList<>();
		for (StrutturaEnvelopeSoap.Caso caso : casi) {
			l.add(new Object[] {caso});
		}
		return l;
	}

	private final StrutturaEnvelopeSoap.Caso caso;
	public WSSecurityStrutturaEnvelopeTest(StrutturaEnvelopeSoap.Caso caso) {
		this.caso = caso;
	}

	private static final boolean SOAP11 = true;


	@Test
	public void firmaRichiestaErogazione() throws Exception {
		testRichiesta(this.caso, StrutturaEnvelopeTestUtils.AZIONE_FIRMA_RISPOSTA, StrutturaEnvelopeTestUtils.AZIONE_VERIFICA_FIRMA);
	}
	@Test
	public void firmaRispostaFruizione() throws Exception {
		testRisposta(this.caso, StrutturaEnvelopeTestUtils.AZIONE_FIRMA_RISPOSTA, StrutturaEnvelopeTestUtils.AZIONE_VERIFICA_FIRMA);
	}

	@Test
	public void cifraturaRichiestaErogazione() throws Exception {
		testRichiesta(this.caso, StrutturaEnvelopeTestUtils.AZIONE_CIFRA_RISPOSTA, StrutturaEnvelopeTestUtils.AZIONE_DECIFRA);
	}
	@Test
	public void cifraturaRispostaFruizione() throws Exception {
		testRisposta(this.caso, StrutturaEnvelopeTestUtils.AZIONE_CIFRA_RISPOSTA, StrutturaEnvelopeTestUtils.AZIONE_DECIFRA);
	}


	/**
	 * Ogni invocazione usa un messaggio protetto nuovo (Timestamp e identificativi diversi).
	 */
	private static String getMessaggioProtetto(String azioneProtezione) throws Exception {
		String valido = StrutturaEnvelopeTestUtils.getMessaggioValido(SOAP11);
		HttpResponse response = StrutturaEnvelopeTestUtils.invoke(Porta.EROGAZIONE, azioneProtezione, SOAP11, valido, valido);
		assertEquals("Applicazione di WS-Security alla risposta fallita: "+new String(response.getContent()),
				200, response.getResultHTTPOperation());
		return new String(response.getContent());
	}

	static void testRichiesta(StrutturaEnvelopeSoap.Caso caso, String azioneProtezione, String azioneVerifica) throws Exception {
		String valido = StrutturaEnvelopeTestUtils.getMessaggioValido(SOAP11);

		// controllo: il messaggio protetto non alterato viene accettato
		HttpResponse response = StrutturaEnvelopeTestUtils.invoke(Porta.EROGAZIONE, azioneVerifica, SOAP11,
				getMessaggioProtetto(azioneProtezione), valido);
		StrutturaEnvelopeTestUtils.verifyOk(response);

		// messaggio protetto con struttura alterata
		response = StrutturaEnvelopeTestUtils.invoke(Porta.EROGAZIONE, azioneVerifica, SOAP11,
				caso.applica(getMessaggioProtetto(azioneProtezione)), valido);
		StrutturaEnvelopeTestUtils.verifyKoRichiesta(response, SOAP11, caso);
	}

	static void testRisposta(StrutturaEnvelopeSoap.Caso caso, String azioneProtezione, String azioneVerifica) throws Exception {
		String valido = StrutturaEnvelopeTestUtils.getMessaggioValido(SOAP11);

		// controllo: la risposta protetta non alterata viene accettata
		HttpResponse response = StrutturaEnvelopeTestUtils.invoke(Porta.FRUIZIONE, azioneVerifica, SOAP11,
				valido, getMessaggioProtetto(azioneProtezione));
		StrutturaEnvelopeTestUtils.verifyOk(response);

		// risposta protetta con struttura alterata
		response = StrutturaEnvelopeTestUtils.invoke(Porta.FRUIZIONE, azioneVerifica, SOAP11,
				valido, caso.applica(getMessaggioProtetto(azioneProtezione)));
		StrutturaEnvelopeTestUtils.verifyKoRisposta(response, SOAP11, caso);
	}
}
