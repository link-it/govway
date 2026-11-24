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
 * Controlli opzionali della struttura dell'Envelope per i messaggi gestiti in streaming senza DOM, ridefiniti per API:
 * - 'soap.envelopeStructure.reader.fullCheck.enabled': il reader prosegue la lettura fino alla soglia del buffer e verifica
 *   tutto il messaggio letto; per la richiesta vale solo sulla configurazione predefinita, sul gruppo si applica alla sola risposta;
 * - 'soap.envelopeStructure.forward.check.enabled': il messaggio viene verificato durante l'inoltro.
 *
 * Per ogni combinazione ({@link Configurazione}) vengono verificati i casi con l'anomalia nella coda del messaggio (non rilevati
 * senza i controlli, vedi {@link StrutturaEnvelopeSoapTest}) e i casi corretti, che devono continuare ad essere accettati.
 * La risposta interrotta durante l'inoltro (consegna al client già iniziata) viene ricevuta incompleta con codice 200
 * e registrata con esito 'contenuto risposta non riconosciuto'.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class StrutturaEnvelopeSoapControlliTest extends ConfigLoader {

	@Parameters(name = "{index}: {0} soap11={1} {2}")
	public static Collection<Object[]> data() {
		Configurazione [] configurazioni = {
				Configurazione.CONTROLLO_INOLTRO_GRUPPO,
				Configurazione.CONTROLLO_COMPLETO_GRUPPO,
				Configurazione.CONTROLLO_COMPLETO,
				Configurazione.CONTROLLO_COMPLETO_E_INOLTRO
		};
		Collection<Object[]> l = new ArrayList<>();
		for (Configurazione configurazione : configurazioni) {
			for (boolean soap11 : new boolean[] {true, false}) {
				for (StrutturaEnvelopeSoap.Caso caso : StrutturaEnvelopeSoap.Caso.values()) {
					if(caso.isAnomaliaInCoda() || caso.isCorretto()) {
						l.add(new Object[] {configurazione, soap11, caso});
					}
				}
			}
		}
		return l;
	}

	private final Configurazione configurazione;
	private final boolean soap11;
	private final StrutturaEnvelopeSoap.Caso caso;
	public StrutturaEnvelopeSoapControlliTest(Configurazione configurazione, boolean soap11, StrutturaEnvelopeSoap.Caso caso) {
		this.configurazione = configurazione;
		this.soap11 = soap11;
		this.caso = caso;
	}


	@Test
	public void erogazioneRichiesta() throws Exception {
		testRichiesta(this.configurazione, Porta.EROGAZIONE, this.soap11, this.caso, 
				StrutturaEnvelopeTestUtils.getMessaggioValido(this.soap11), false);
	}
	@Test
	public void fruizioneRichiesta() throws Exception {
		testRichiesta(this.configurazione, Porta.FRUIZIONE, this.soap11, this.caso, 
				StrutturaEnvelopeTestUtils.getMessaggioValido(this.soap11), false);
	}

	@Test
	public void erogazioneRisposta() throws Exception {
		testRisposta(this.configurazione, Porta.EROGAZIONE, this.soap11, this.caso, 
				StrutturaEnvelopeTestUtils.getMessaggioValido(this.soap11), false);
	}
	@Test
	public void fruizioneRisposta() throws Exception {
		testRisposta(this.configurazione, Porta.FRUIZIONE, this.soap11, this.caso, 
				StrutturaEnvelopeTestUtils.getMessaggioValido(this.soap11), false);
	}


	/**
	 * @return id della transazione
	 */
	static String testRichiesta(Configurazione configurazione, Porta porta, boolean soap11, StrutturaEnvelopeSoap.Caso caso, 
			String valido, boolean anomaliaOltreSoglia) throws Exception {
		HttpResponse response = StrutturaEnvelopeTestUtils.invoke(configurazione, porta, soap11, caso.applica(valido), valido);
		StrutturaEnvelopeTestUtils.verify(response, 
				StrutturaEnvelopeTestUtils.getEsitoAtteso(configurazione, porta, Direzione.RICHIESTA, caso, anomaliaOltreSoglia),
				Direzione.RICHIESTA, soap11, caso, valido);
		return StrutturaEnvelopeTestUtils.getIdTransazione(response);
	}

	/**
	 * @return id della transazione
	 */
	static String testRisposta(Configurazione configurazione, Porta porta, boolean soap11, StrutturaEnvelopeSoap.Caso caso, 
			String valido, boolean anomaliaOltreSoglia) throws Exception {
		String risposta = caso.applica(valido);
		HttpResponse response = StrutturaEnvelopeTestUtils.invoke(configurazione, porta, soap11, valido, risposta);
		StrutturaEnvelopeTestUtils.verify(response, 
				StrutturaEnvelopeTestUtils.getEsitoAtteso(configurazione, porta, Direzione.RISPOSTA, caso, anomaliaOltreSoglia),
				Direzione.RISPOSTA, soap11, caso, risposta);
		return StrutturaEnvelopeTestUtils.getIdTransazione(response);
	}
}
