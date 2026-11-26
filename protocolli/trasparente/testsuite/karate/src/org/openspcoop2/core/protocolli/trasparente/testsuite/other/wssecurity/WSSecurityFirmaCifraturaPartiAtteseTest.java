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
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Direzione;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Messaggio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.MessaggioProtetto;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Mittente;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Parte;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * Elementi firmati e cifrati attesi nella maschera 'WS-Security Signature Decrypt'.
 *
 * Ogni ricevente con 'Elementi firmati attesi' ed 'Elementi cifrati attesi' (tutte le opzioni della maschera, con e senza Timestamp)
 * viene provato con tutti i mittenti della matrice con lo stesso Timestamp, in entrambi gli ordini delle operazioni:
 * il messaggio (SOAP with Attachments) è accettato solo se gli elementi firmati e cifrati coincidono con quelli attesi,
 * altrimenti viene rifiutato con i diagnostici di firma e cifratura per ogni elemento atteso mancante o protetto ma non atteso.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class WSSecurityFirmaCifraturaPartiAtteseTest extends ConfigLoader {

	@Parameters(name = "{index}: {0} <- {3}")
	public static Collection<Object[]> data() {
		Collection<Object[]> l = new ArrayList<>();
		for (boolean timestamp : new boolean[] {false, true}) {
			for (Set<Parte> firmatiAttesi : WSSecurityFirmaCifraturaUtils.getFirmati(timestamp)) {
				for (Set<Parte> cifratiAttesi : WSSecurityFirmaCifraturaUtils.getCifrati()) {
					String ricevente = WSSecurityFirmaCifraturaUtils.getAzioneRiceventeAttesi(timestamp, firmatiAttesi, cifratiAttesi);
					for (Mittente mittente : WSSecurityFirmaCifraturaUtils.getMittenti()) {
						if(mittente.isTimestamp()==timestamp) {
							l.add(new Object[] {ricevente, firmatiAttesi, cifratiAttesi, mittente});
						}
					}
				}
			}
		}
		return l;
	}

	private final String ricevente;
	private final Set<Parte> firmatiAttesi;
	private final Set<Parte> cifratiAttesi;
	private final Mittente mittente;
	public WSSecurityFirmaCifraturaPartiAtteseTest(String ricevente, Set<Parte> firmatiAttesi, Set<Parte> cifratiAttesi, Mittente mittente) {
		this.ricevente = ricevente;
		this.firmatiAttesi = firmatiAttesi;
		this.cifratiAttesi = cifratiAttesi;
		this.mittente = mittente;
	}

	@Test
	public void verificaPartiAttese() throws Exception {
		MessaggioProtetto protetto = WSSecurityFirmaCifraturaUtils.proteggi(WSSecurityFirmaCifraturaUtils.getApiFirmaCifratura(this.mittente.getAzione()), this.mittente.getAzione(),
				Messaggio.SOAP_WITH_ATTACHMENTS);
		HttpResponse response = WSSecurityFirmaCifraturaUtils.verifica(WSSecurityFirmaCifraturaUtils.getApiFirmaCifratura(this.ricevente), Direzione.RICHIESTA, this.ricevente, protetto);
		String descrizione = this.ricevente+" <- "+this.mittente;
		List<String> erroriAttesi = new ArrayList<>();
		addErroriAttesi(erroriAttesi, "signature", this.firmatiAttesi, this.mittente.getFirmati());
		addErroriAttesi(erroriAttesi, "encryption", this.cifratiAttesi, this.mittente.getCifrati());
		if(erroriAttesi.isEmpty()) {
			WSSecurityFirmaCifraturaUtils.verifyOk(response, descrizione);
		}
		else {
			WSSecurityFirmaCifraturaUtils.verifyKo(response, Direzione.RICHIESTA, descrizione, erroriAttesi);
		}
	}

	/** Frammenti attesi nei diagnostici: un elemento atteso mancante e un elemento protetto non atteso per ciascuna differenza */
	private static void addErroriAttesi(List<String> l, String tipo, Set<Parte> attese, Set<Parte> protette) {
		for (Parte parte : attese) {
			if(!protette.contains(parte)) {
				l.add("Expected "+tipo+" part("+getPart(tipo, parte)+") not found");
			}
		}
		for (Parte parte : protette) {
			if(!attese.contains(parte)) {
				l.add("Found "+tipo+" part(");
			}
		}
	}

	/** Parte come indicata nel diagnostico (namespace SOAP risolto) */
	private static String getPart(String tipo, Parte parte) {
		switch (parte) {
		case TIMESTAMP:
			return "{Element}{"+WSSecurityFirmaCifraturaUtils.WSU+"}Timestamp";
		case BODY:
			return ("signature".equals(tipo) ? "{Element}" : "{Content}")+"{"+WSSecurityFirmaCifraturaUtils.SOAP_11+"}Body";
		default:
			return "{}cid:Attachments";
		}
	}
}
