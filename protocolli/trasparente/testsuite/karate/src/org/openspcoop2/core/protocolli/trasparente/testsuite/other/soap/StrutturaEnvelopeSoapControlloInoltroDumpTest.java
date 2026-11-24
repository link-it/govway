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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap;

import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeTestUtils.Configurazione;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeTestUtils.Porta;

/**
 * Controllo durante l'inoltro con la registrazione binaria dei messaggi abilitata: quando l'inoltro viene interrotto per
 * un'anomalia della struttura, il messaggio ricevuto (richiesta dal client o risposta dal backend) deve comunque essere registrato per intero.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class StrutturaEnvelopeSoapControlloInoltroDumpTest extends ConfigLoader {

	private static final String RICHIESTA_INGRESSO = "RichiestaIngressoDumpBinario";
	private static final String RISPOSTA_INGRESSO = "RispostaIngressoDumpBinario";

	@Parameters(name = "{index}: soap11={0} {1}")
	public static Collection<Object[]> data() {
		StrutturaEnvelopeSoap.Caso [] casi = {
				StrutturaEnvelopeSoap.Caso.OK_BUSTA_CORRETTA,
				StrutturaEnvelopeSoap.Caso.DOPPIO_BODY,
				StrutturaEnvelopeSoap.Caso.HEADER_DOPO_BODY,
				StrutturaEnvelopeSoap.Caso.PAYLOAD_E_FAULT,
				StrutturaEnvelopeSoap.Caso.DOPPIO_ENVELOPE_CONCATENATO
		};
		Collection<Object[]> l = new ArrayList<>();
		for (boolean soap11 : new boolean[] {true, false}) {
			for (StrutturaEnvelopeSoap.Caso caso : casi) {
				l.add(new Object[] {soap11, caso});
			}
		}
		return l;
	}

	private final boolean soap11;
	private final StrutturaEnvelopeSoap.Caso caso;
	public StrutturaEnvelopeSoapControlloInoltroDumpTest(boolean soap11, StrutturaEnvelopeSoap.Caso caso) {
		this.soap11 = soap11;
		this.caso = caso;
	}


	@Test
	public void erogazioneRichiesta() throws Exception {
		testRichiesta(Porta.EROGAZIONE);
	}
	@Test
	public void fruizioneRichiesta() throws Exception {
		testRichiesta(Porta.FRUIZIONE);
	}

	@Test
	public void erogazioneRisposta() throws Exception {
		testRisposta(Porta.EROGAZIONE);
	}
	@Test
	public void fruizioneRisposta() throws Exception {
		testRisposta(Porta.FRUIZIONE);
	}


	private void testRichiesta(Porta porta) throws Exception {
		String valido = StrutturaEnvelopeTestUtils.getMessaggioValido(this.soap11);
		String idTransazione = StrutturaEnvelopeSoapControlliTest.testRichiesta(Configurazione.CONTROLLO_INOLTRO_CON_DUMP, porta, this.soap11, this.caso, valido, false);
		verificaDumpCompleto(idTransazione, RICHIESTA_INGRESSO, this.caso.applica(valido));
	}

	private void testRisposta(Porta porta) throws Exception {
		String valido = StrutturaEnvelopeTestUtils.getMessaggioValido(this.soap11);
		String idTransazione = StrutturaEnvelopeSoapControlliTest.testRisposta(Configurazione.CONTROLLO_INOLTRO_CON_DUMP, porta, this.soap11, this.caso, valido, false);
		verificaDumpCompleto(idTransazione, RISPOSTA_INGRESSO, this.caso.applica(valido));
	}

	private static void verificaDumpCompleto(String idTransazione, String tipoMessaggio, String messaggioInviato) throws Exception {
		byte[] atteso = messaggioInviato.getBytes(StandardCharsets.UTF_8);
		byte[] registrato = DBVerifier.getDumpMessaggioBody(idTransazione, tipoMessaggio);
		assertTrue("IdTransazione: "+idTransazione+" messaggio registrato ("+tipoMessaggio+", "+registrato.length+" byte) differente da quello inviato ("+
				atteso.length+" byte):\n"+new String(registrato, StandardCharsets.UTF_8), 
				Arrays.equals(atteso, registrato));
	}
}
