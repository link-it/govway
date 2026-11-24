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

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeTestUtils.Configurazione;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeTestUtils.Direzione;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeTestUtils.Porta;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * Struttura dell'Envelope SOAP non conforme (più Header o Body, ordine errato, Fault multipli o fuori posto,
 * elementi non previsti come figli dell'Envelope), su erogazione e fruizione, in richiesta e in risposta,
 * per SOAP 1.1 e SOAP 1.2. I casi corretti (#00 e controlli) devono continuare ad essere accettati.
 *
 * L'API è gestita in streaming senza DOM e senza i controlli opzionali (configurazione di default): le anomalie nella parte
 * iniziale del messaggio vengono rifiutate, quelle nella coda (dopo il primo figlio del Body) vengono inoltrate (limite noto,
 * vedi {@link StrutturaEnvelopeSoapControlliTest}).
 *
 * Casi: {@link StrutturaEnvelopeSoap.Caso}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class StrutturaEnvelopeSoapTest extends ConfigLoader {

	@Parameters(name = "{index}: soap11={0} {1}")
	public static Collection<Object[]> data() {
		Collection<Object[]> l = new ArrayList<>();
		for (boolean soap11 : new boolean[] {true, false}) {
			for (StrutturaEnvelopeSoap.Caso caso : StrutturaEnvelopeSoap.Caso.values()) {
				if(caso.isCoperturaWSSecurity()) {
					continue; // struttura corretta: hanno senso solo con WS-Security (other.wssecurity.WSSecurityStrutturaEnvelopeTest)
				}
				l.add(new Object[] {soap11, caso});
			}
		}
		return l;
	}

	private final boolean soap11;
	private final StrutturaEnvelopeSoap.Caso caso;
	public StrutturaEnvelopeSoapTest(boolean soap11, StrutturaEnvelopeSoap.Caso caso) {
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


	// Nessun controllo opzionale: le anomalie nella coda dei messaggi gestiti in streaming non vengono rilevate
	// (vedi StrutturaEnvelopeSoapControlliTest per i controlli 'soap.envelopeStructure.*')
	private void testRichiesta(Porta porta) throws Exception {
		String valido = StrutturaEnvelopeTestUtils.getMessaggioValido(this.soap11);
		HttpResponse response = StrutturaEnvelopeTestUtils.invoke(porta, StrutturaEnvelopeTestUtils.AZIONE_ECHO, this.soap11,
				this.caso.applica(valido), valido);
		StrutturaEnvelopeTestUtils.verify(response, 
				StrutturaEnvelopeTestUtils.getEsitoAtteso(Configurazione.NESSUN_CONTROLLO, porta, Direzione.RICHIESTA, this.caso, false),
				Direzione.RICHIESTA, this.soap11, this.caso, valido);
	}

	private void testRisposta(Porta porta) throws Exception {
		String valido = StrutturaEnvelopeTestUtils.getMessaggioValido(this.soap11);
		String risposta = this.caso.applica(valido);
		HttpResponse response = StrutturaEnvelopeTestUtils.invoke(porta, StrutturaEnvelopeTestUtils.AZIONE_ECHO, this.soap11,
				valido, risposta);
		StrutturaEnvelopeTestUtils.verify(response, 
				StrutturaEnvelopeTestUtils.getEsitoAtteso(Configurazione.NESSUN_CONTROLLO, porta, Direzione.RISPOSTA, this.caso, false),
				Direzione.RISPOSTA, this.soap11, this.caso, risposta);
	}
}
