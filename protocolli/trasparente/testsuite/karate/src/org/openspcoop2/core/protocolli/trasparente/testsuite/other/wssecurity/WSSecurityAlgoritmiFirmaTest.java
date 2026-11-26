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
import java.util.EnumSet;
import java.util.Set;

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
 * Algoritmi di firma accettati in ricezione ('Algoritmi accettati' delle maschere 'WS-Security Signature' e
 * 'WS-Security Signature Decrypt'): con 'Qualsiasi' non viene impostata alcuna proprietà, con 'Elenco' vengono accettati
 * solo gli algoritmi di firma e di digest (dei riferimenti della firma) indicati, separati da ','.
 * Con 'Elenco' la maschera propone SHA-256 come digest.
 *
 * Per ciascun tipo di messaggio (solo firma o cifratura e firma, con e senza allegati) il mittente firma con:
 * RSA-SHA1 e digest SHA-1, RSA-SHA256 e digest SHA-256, oppure in modo ibrido RSA-SHA256 con digest SHA-1.
 * Il ricevente accetta: qualsiasi algoritmo, solo SHA-256 (firma e digest), solo SHA-1 (firma e digest),
 * entrambi (firma e digest), firma RSA-SHA256 con digest SHA-256 o SHA-1 (ibrido ammesso).
 * Il messaggio è accettato se sia l'algoritmo di firma sia quello di digest del mittente sono tra quelli accettati,
 * altrimenti rifiutato con errore di sicurezza.
 * La verifica avviene sia sulla richiesta (erogazione) sia sulla risposta (fruizione).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class WSSecurityAlgoritmiFirmaTest extends ConfigLoader {

	/** Errore WSS4J per un algoritmo non accettato (AlgorithmSuite) */
	public static final String ERRORE_ALGORITMO = "An error was discovered processing the <wsse:Security> header";

	enum Tipo {
		FIRMATA("firmata", false, false),
		ALLEGATI_FIRMATA("allegati-firmata", true, false),
		CIFRATA_FIRMATA("cifrata-firmata", false, true),
		ALLEGATI_CIFRATA_FIRMATA("allegati-cifrata-firmata", true, true);

		private final String codice;
		private final boolean allegati;
		private final boolean cifratura;
		Tipo(String codice, boolean allegati, boolean cifratura) {
			this.codice = codice;
			this.allegati = allegati;
			this.cifratura = cifratura;
		}
		/** Solo firma: API TestWSSecurityAlgoritmiFirma; firma + cifratura: API TestWSSecurityAlgoritmiFirmaCifratura */
		String getApi() {
			return this.cifratura ? WSSecurityFirmaCifraturaUtils.API_ALGORITMI_FIRMA_CIFRATURA : WSSecurityFirmaCifraturaUtils.API_ALGORITMI_FIRMA;
		}
	}

	enum Hash { SHA1, SHA256 }

	/** Algoritmo di firma e digest dei riferimenti usati dal mittente */
	enum Algoritmo {
		SHA1("sha1", Hash.SHA1, Hash.SHA1),
		SHA256("sha256", Hash.SHA256, Hash.SHA256),
		/** firma ibrida: RSA-SHA256 con digest SHA-1 */
		SHA256_DIGEST_SHA1("sha256-digest-sha1", Hash.SHA256, Hash.SHA1);

		private final String codice;
		private final Hash firma;
		private final Hash digest;
		Algoritmo(String codice, Hash firma, Hash digest) {
			this.codice = codice;
			this.firma = firma;
			this.digest = digest;
		}
	}

	/** Algoritmi di firma e di digest accettati dal ricevente */
	enum Accettati {
		QUALSIASI("qualsiasi", EnumSet.allOf(Hash.class), EnumSet.allOf(Hash.class)),
		SHA256("sha256", EnumSet.of(Hash.SHA256), EnumSet.of(Hash.SHA256)),
		SHA1("sha1", EnumSet.of(Hash.SHA1), EnumSet.of(Hash.SHA1)),
		ENTRAMBI("entrambi", EnumSet.allOf(Hash.class), EnumSet.allOf(Hash.class)),
		/** firma RSA-SHA256, digest SHA-256 o SHA-1: firma ibrida ammessa */
		SHA256_DIGEST_ENTRAMBI("sha256-digest-entrambi", EnumSet.of(Hash.SHA256), EnumSet.allOf(Hash.class));

		private final String codice;
		private final Set<Hash> firma;
		private final Set<Hash> digest;
		Accettati(String codice, Set<Hash> firma, Set<Hash> digest) {
			this.codice = codice;
			this.firma = firma;
			this.digest = digest;
		}
		boolean isAccettato(Algoritmo algoritmo) {
			return this.firma.contains(algoritmo.firma) && this.digest.contains(algoritmo.digest);
		}
	}

	@Parameters(name = "{index}: {0} {1} ric {3} <- mitt {2}")
	public static Collection<Object[]> data() {
		Collection<Object[]> l = new ArrayList<>();
		for (Direzione direzione : Direzione.values()) {
			for (Tipo tipo : Tipo.values()) {
				for (Algoritmo algoritmo : Algoritmo.values()) {
					for (Accettati accettati : Accettati.values()) {
						l.add(new Object[] {direzione, tipo, algoritmo, accettati});
					}
				}
			}
		}
		return l;
	}

	private final Direzione direzione;
	private final Tipo tipo;
	private final Algoritmo algoritmo;
	private final Accettati accettati;
	public WSSecurityAlgoritmiFirmaTest(Direzione direzione, Tipo tipo, Algoritmo algoritmo, Accettati accettati) {
		this.direzione = direzione;
		this.tipo = tipo;
		this.algoritmo = algoritmo;
		this.accettati = accettati;
	}

	@Test
	public void verificaAlgoritmo() throws Exception {
		String api = this.tipo.getApi();
		String mittente = "mitt-"+this.tipo.codice+"-"+this.algoritmo.codice;
		String ricevente = "ric-"+this.tipo.codice+"-"+this.accettati.codice;
		MessaggioProtetto protetto = WSSecurityFirmaCifraturaUtils.proteggi(api, mittente,
				this.tipo.allegati ? Messaggio.SOAP_WITH_ATTACHMENTS : Messaggio.SOAP);
		HttpResponse response = WSSecurityFirmaCifraturaUtils.verifica(api, this.direzione, ricevente, protetto);
		String descrizione = this.direzione+" "+ricevente+" <- "+mittente;
		if(this.accettati.isAccettato(this.algoritmo)) {
			WSSecurityFirmaCifraturaUtils.verifyOk(response, descrizione);
		}
		else {
			WSSecurityFirmaCifraturaUtils.verifyKo(response, this.direzione, descrizione, Collections.singletonList(ERRORE_ALGORITMO));
		}
	}
}
