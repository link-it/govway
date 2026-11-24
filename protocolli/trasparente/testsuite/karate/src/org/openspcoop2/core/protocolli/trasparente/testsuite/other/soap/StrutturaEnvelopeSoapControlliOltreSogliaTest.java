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
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeTestUtils.Porta;

/**
 * Messaggi più grandi della soglia del buffer del reader (30 KB), con l'anomalia dopo un Body di 40 KB:
 * - il controllo completo nel reader verifica solo la parte letta e non rileva l'anomalia;
 * - il controllo durante l'inoltro verifica tutto il messaggio e la rileva.
 * I casi corretti devono essere accettati con tutte le combinazioni.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class StrutturaEnvelopeSoapControlliOltreSogliaTest extends ConfigLoader {

	@Parameters(name = "{index}: {0} soap11={1} {2}")
	public static Collection<Object[]> data() {
		Configurazione [] configurazioni = {
				Configurazione.NESSUN_CONTROLLO,
				Configurazione.CONTROLLO_INOLTRO_GRUPPO,
				Configurazione.CONTROLLO_COMPLETO_GRUPPO,
				Configurazione.CONTROLLO_COMPLETO,
				Configurazione.CONTROLLO_COMPLETO_E_INOLTRO
		};
		// casi la cui anomalia segue il Body del messaggio valido (oltre la soglia)
		StrutturaEnvelopeSoap.Caso [] casi = {
				StrutturaEnvelopeSoap.Caso.OK_BUSTA_CORRETTA,
				StrutturaEnvelopeSoap.Caso.DOPPIO_BODY,
				StrutturaEnvelopeSoap.Caso.BODY_PAYLOAD_E_BODY_FAULT,
				StrutturaEnvelopeSoap.Caso.HEADER_BODY_HEADER,
				StrutturaEnvelopeSoap.Caso.ELEMENTO_ALTRO_NAMESPACE_DOPO_BODY,
				StrutturaEnvelopeSoap.Caso.TESTO_DOPO_BODY,
				StrutturaEnvelopeSoap.Caso.DOPPIO_ENVELOPE_CONCATENATO
		};
		Collection<Object[]> l = new ArrayList<>();
		for (Configurazione configurazione : configurazioni) {
			for (boolean soap11 : new boolean[] {true, false}) {
				for (StrutturaEnvelopeSoap.Caso caso : casi) {
					l.add(new Object[] {configurazione, soap11, caso});
				}
			}
		}
		return l;
	}

	private final Configurazione configurazione;
	private final boolean soap11;
	private final StrutturaEnvelopeSoap.Caso caso;
	public StrutturaEnvelopeSoapControlliOltreSogliaTest(Configurazione configurazione, boolean soap11, StrutturaEnvelopeSoap.Caso caso) {
		this.configurazione = configurazione;
		this.soap11 = soap11;
		this.caso = caso;
	}

	private String getMessaggioValido() {
		return StrutturaEnvelopeTestUtils.getMessaggioValido(this.soap11, StrutturaEnvelopeTestUtils.DIMENSIONE_PAYLOAD_OLTRE_SOGLIA);
	}


	@Test
	public void erogazioneRichiesta() throws Exception {
		StrutturaEnvelopeSoapControlliTest.testRichiesta(this.configurazione, Porta.EROGAZIONE, this.soap11, this.caso, getMessaggioValido(), true);
	}
	@Test
	public void fruizioneRichiesta() throws Exception {
		StrutturaEnvelopeSoapControlliTest.testRichiesta(this.configurazione, Porta.FRUIZIONE, this.soap11, this.caso, getMessaggioValido(), true);
	}

	@Test
	public void erogazioneRisposta() throws Exception {
		StrutturaEnvelopeSoapControlliTest.testRisposta(this.configurazione, Porta.EROGAZIONE, this.soap11, this.caso, getMessaggioValido(), true);
	}
	@Test
	public void fruizioneRisposta() throws Exception {
		StrutturaEnvelopeSoapControlliTest.testRisposta(this.configurazione, Porta.FRUIZIONE, this.soap11, this.caso, getMessaggioValido(), true);
	}
}
