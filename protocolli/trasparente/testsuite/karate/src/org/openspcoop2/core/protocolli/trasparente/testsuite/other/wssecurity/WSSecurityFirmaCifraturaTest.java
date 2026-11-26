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

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Direzione;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Messaggio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.MessaggioProtetto;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Mittente;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Ordine;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * Firma e cifratura nella stessa configurazione (maschere 'WS-Security Signature Encrypt' e 'WS-Security Signature Decrypt').
 *
 * Ogni mittente della matrice (ordine 'Firma >> Cifratura' o 'Cifratura >> Firma', con e senza Timestamp,
 * per ogni combinazione di elementi firmati e cifrati, con e senza allegati) viene verificato dai riceventi con
 * 'Elementi firmati/cifrati attesi' = 'Qualsiasi' e senza la protezione degli elementi cifrati, di entrambi gli ordini:
 * in ricezione le azioni sono verificate in qualsiasi ordine, quindi il messaggio deve essere sempre accettato.
 * La verifica avviene sia sulla richiesta (erogazione) sia sulla risposta (fruizione).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class WSSecurityFirmaCifraturaTest extends ConfigLoader {

	@Parameters(name = "{index}: {0} {1} <- {2} ({3})")
	public static Collection<Object[]> data() {
		Collection<Object[]> l = new ArrayList<>();
		for (Direzione direzione : Direzione.values()) {
			for (Mittente mittente : WSSecurityFirmaCifraturaUtils.getMittenti()) {
				for (Ordine ordineRicevente : Ordine.values()) {
					String ricevente = WSSecurityFirmaCifraturaUtils.getAzioneRiceventeQualsiasi(ordineRicevente, false, mittente.isTimestamp());
					for (Messaggio messaggio : mittente.getMessaggi()) {
						l.add(new Object[] {direzione, ricevente, mittente, messaggio});
					}
				}
			}
		}
		return l;
	}

	private final Direzione direzione;
	private final String ricevente;
	private final Mittente mittente;
	private final Messaggio messaggio;
	public WSSecurityFirmaCifraturaTest(Direzione direzione, String ricevente, Mittente mittente, Messaggio messaggio) {
		this.direzione = direzione;
		this.ricevente = ricevente;
		this.mittente = mittente;
		this.messaggio = messaggio;
	}

	@Test
	public void verifica() throws Exception {
		MessaggioProtetto protetto = WSSecurityFirmaCifraturaUtils.proteggi(WSSecurityFirmaCifraturaUtils.getApiFirmaCifratura(this.mittente.getAzione()), this.mittente.getAzione(), this.messaggio);
		HttpResponse response = WSSecurityFirmaCifraturaUtils.verifica(WSSecurityFirmaCifraturaUtils.getApiFirmaCifratura(this.ricevente), this.direzione, this.ricevente, protetto);
		WSSecurityFirmaCifraturaUtils.verifyOk(response, this.direzione+" "+this.ricevente+" <- "+this.mittente+" ("+this.messaggio+")");
	}
}
