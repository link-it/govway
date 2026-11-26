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

import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
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
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * Header SOAP firmati, indicati con la 'Configurazione Manuale' degli elementi da firmare e degli elementi firmati attesi
 * nelle maschere 'WS-Security Signature' e 'WS-Security Signature Encrypt/Decrypt' (Cifratura &gt;&gt; Firma, Body cifrato):
 * vengono firmati Timestamp, l'header 'TestHdr' del messaggio e il Body.
 *
 * Per ciascuna maschera, sulla richiesta e sulla risposta:
 * - header firmato: messaggio accettato;
 * - header firmato e poi alterato: messaggio rifiutato (firma non valida);
 * - header non firmato (mittente che firma solo Timestamp e Body): messaggio rifiutato, header atteso tra gli elementi firmati.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class WSSecurityHeaderFirmatiTest extends ConfigLoader {

	public static final String API = "TestWSSecurityHeaderFirmati";

	private static final String HEADER_PART = "{Element}{http://govway.org/example/header}TestHdr";
	private static final String ERRORE_FIRMA_NON_VALIDA = "The signature or decryption was invalid";

	enum Maschera {
		/** 'WS-Security Signature' */
		FIRMA("mitt-firma-header", "mitt-firma-senza-header", "ric-firma-header"),
		/** 'WS-Security Signature Encrypt/Decrypt' (Cifratura >> Firma, Body cifrato) */
		CIFRA_FIRMA("mitt-cifra-firma-header", "mitt-cifra-firma-senza-header", "ric-cifra-firma-header");

		private final String mittenteHeader;
		private final String mittenteSenzaHeader;
		private final String ricevente;
		Maschera(String mittenteHeader, String mittenteSenzaHeader, String ricevente) {
			this.mittenteHeader = mittenteHeader;
			this.mittenteSenzaHeader = mittenteSenzaHeader;
			this.ricevente = ricevente;
		}
	}

	enum Caso { HEADER_FIRMATO, HEADER_ALTERATO, HEADER_NON_FIRMATO }

	@Parameters(name = "{index}: {0} {1} {2}")
	public static Collection<Object[]> data() {
		Collection<Object[]> l = new ArrayList<>();
		for (Direzione direzione : Direzione.values()) {
			for (Maschera maschera : Maschera.values()) {
				for (Caso caso : Caso.values()) {
					l.add(new Object[] {direzione, maschera, caso});
				}
			}
		}
		return l;
	}

	private final Direzione direzione;
	private final Maschera maschera;
	private final Caso caso;
	public WSSecurityHeaderFirmatiTest(Direzione direzione, Maschera maschera, Caso caso) {
		this.direzione = direzione;
		this.maschera = maschera;
		this.caso = caso;
	}

	@Test
	public void verificaHeaderFirmato() throws Exception {
		String mittente = Caso.HEADER_NON_FIRMATO.equals(this.caso) ? this.maschera.mittenteSenzaHeader : this.maschera.mittenteHeader;
		MessaggioProtetto protetto = WSSecurityFirmaCifraturaUtils.proteggi(API, mittente, Messaggio.SOAP);
		if(Caso.HEADER_ALTERATO.equals(this.caso)) {
			// il valore dell'header (in chiaro anche con il Body cifrato) viene modificato dopo la firma
			String m = new String(protetto.getContenuto(), StandardCharsets.UTF_8);
			String alterato = m.replace(">header</", ">header alterato</");
			assertTrue("Header 'TestHdr' non trovato nel messaggio protetto: "+m, !alterato.equals(m));
			protetto = new MessaggioProtetto(alterato.getBytes(StandardCharsets.UTF_8), protetto.getContentType());
		}
		HttpResponse response = WSSecurityFirmaCifraturaUtils.verifica(API, this.direzione, this.maschera.ricevente, protetto);
		String descrizione = this.direzione+" "+this.maschera.ricevente+" <- "+mittente+" ("+this.caso+")";
		switch (this.caso) {
		case HEADER_FIRMATO:
			WSSecurityFirmaCifraturaUtils.verifyOk(response, descrizione);
			break;
		case HEADER_ALTERATO:
			WSSecurityFirmaCifraturaUtils.verifyKo(response, this.direzione, descrizione, Collections.singletonList(ERRORE_FIRMA_NON_VALIDA));
			break;
		default:
			WSSecurityFirmaCifraturaUtils.verifyKo(response, this.direzione, descrizione,
					Collections.singletonList("Expected signature part("+HEADER_PART+") not found"));
			break;
		}
	}
}
