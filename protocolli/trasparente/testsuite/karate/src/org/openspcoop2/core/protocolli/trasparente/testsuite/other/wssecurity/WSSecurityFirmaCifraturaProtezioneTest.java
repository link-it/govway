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
import java.util.Collections;

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
 * Protezione degli elementi cifrati ('Decifra solo Elementi Firmati' della maschera 'WS-Security Signature Decrypt',
 * proprietà WSS4J 'requireSignedEncryptedDataElements').
 *
 * Ogni mittente della matrice viene verificato dal ricevente 'Cifratura >> Firma' con la protezione abilitata
 * ('Elementi firmati/cifrati attesi' = 'Qualsiasi'), sulla richiesta e sulla risposta:
 * - accettato se il mittente ha cifrato e poi firmato e ogni elemento cifrato è anche firmato;
 * - rifiutato prima della decifratura ("... is not signed") negli altri casi: ordine 'Firma >> Cifratura'
 *   (gli elementi di cifratura precedono la firma nell'header) o elementi cifrati non coperti dalla firma.
 *
 * Gli allegati cifrati e firmati vengono accettati solo con la libreria WSS4J che estende la verifica agli allegati
 * (patch 'sorgenti_dom_require_signed_encrypted_attachment' del fork gov4j): con le versioni precedenti l'EncryptedData
 * dell'allegato risulta non firmato e il messaggio viene rifiutato.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class WSSecurityFirmaCifraturaProtezioneTest extends ConfigLoader {

	@Parameters(name = "{index}: {0} {1} <- {2} ({3})")
	public static Collection<Object[]> data() {
		Collection<Object[]> l = new ArrayList<>();
		for (Direzione direzione : Direzione.values()) {
			for (Mittente mittente : WSSecurityFirmaCifraturaUtils.getMittenti()) {
				String ricevente = WSSecurityFirmaCifraturaUtils.getAzioneRiceventeQualsiasi(Ordine.CIFRATURA_FIRMA, true, mittente.isTimestamp());
				for (Messaggio messaggio : mittente.getMessaggi()) {
					l.add(new Object[] {direzione, ricevente, mittente, messaggio});
				}
			}
		}
		return l;
	}

	private final Direzione direzione;
	private final String ricevente;
	private final Mittente mittente;
	private final Messaggio messaggio;
	public WSSecurityFirmaCifraturaProtezioneTest(Direzione direzione, String ricevente, Mittente mittente, Messaggio messaggio) {
		this.direzione = direzione;
		this.ricevente = ricevente;
		this.mittente = mittente;
		this.messaggio = messaggio;
	}

	@Test
	public void verificaProtezione() throws Exception {
		MessaggioProtetto protetto = WSSecurityFirmaCifraturaUtils.proteggi(WSSecurityFirmaCifraturaUtils.getApiFirmaCifratura(this.mittente.getAzione()), this.mittente.getAzione(), this.messaggio);
		HttpResponse response = WSSecurityFirmaCifraturaUtils.verifica(WSSecurityFirmaCifraturaUtils.getApiFirmaCifratura(this.ricevente), this.direzione, this.ricevente, protetto);
		String descrizione = this.direzione+" "+this.ricevente+" <- "+this.mittente+" ("+this.messaggio+")";
		if(this.mittente.isAccettatoConProtezione()) {
			WSSecurityFirmaCifraturaUtils.verifyOk(response, descrizione);
		}
		else {
			WSSecurityFirmaCifraturaUtils.verifyKo(response, this.direzione, descrizione,
					Collections.singletonList(WSSecurityFirmaCifraturaUtils.ERRORE_NON_FIRMATO));
		}
	}
}
