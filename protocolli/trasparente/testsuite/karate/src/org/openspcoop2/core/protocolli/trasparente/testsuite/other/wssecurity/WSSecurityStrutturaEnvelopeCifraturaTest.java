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

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeSoap;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap.StrutturaEnvelopeTestUtils;

/**
 * Attacchi applicabili solo alla cifratura WS-Security (con la firma la manovra non è possibile, perché la firma del Body
 * ne copre anche il contenuto): il Body resta al suo posto, ma il contenuto cifrato (EncryptedData) viene spostato in un
 * header block e sostituito da un payload in chiaro.
 *
 * Il rifiuto è garantito dalla verifica degli elementi cifrati attesi configurata in ricezione (encryptionParts) sull'azione 'decifra'.
 * Vedi anche {@link WSSecurityStrutturaEnvelopeTest}.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSecurityStrutturaEnvelopeCifraturaTest extends ConfigLoader {

	@Test
	public void cifraturaRichiestaErogazioneContenutoCifratoSpostato() throws Exception {
		WSSecurityStrutturaEnvelopeTest.testRichiesta(StrutturaEnvelopeSoap.Caso.COPERTURA_CONTENUTO_CIFRATO_IN_HEADER,
				StrutturaEnvelopeTestUtils.AZIONE_CIFRA_RISPOSTA, StrutturaEnvelopeTestUtils.AZIONE_DECIFRA);
	}

	@Test
	public void cifraturaRispostaFruizioneContenutoCifratoSpostato() throws Exception {
		WSSecurityStrutturaEnvelopeTest.testRisposta(StrutturaEnvelopeSoap.Caso.COPERTURA_CONTENUTO_CIFRATO_IN_HEADER,
				StrutturaEnvelopeTestUtils.AZIONE_CIFRA_RISPOSTA, StrutturaEnvelopeTestUtils.AZIONE_DECIFRA);
	}
}
