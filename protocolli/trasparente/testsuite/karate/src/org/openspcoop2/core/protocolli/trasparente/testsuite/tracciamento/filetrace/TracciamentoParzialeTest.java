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
package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.filetrace;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.TestTracciamentoCostanti;
import org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.TracciamentoUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.TracciamentoVerifica;
import org.openspcoop2.utils.BooleanNullable;

/**
 * TracciamentoParzialeTest
 *
 * Verifica due cose in un unico test (per ciascuna delle 4 risorse asimmetriche):
 *  1. Su {@code dump_messaggi}: nessuna riga inserita (registrazione messaggi disabilitata).
 *  2. Sul file di fileTrace: contenuto coerente con la configurazione asimmetrica del
 *     "Buffer dei Messaggi" — solo header, oppure solo payload, sul lato client o server.
 *
 * Le 4 risorse coprono le combinazioni:
 *  - flusso "richiesta" (Buffer scambiati col client / dump-in)   header-only / payload-only
 *  - flusso "risposta"  (Buffer scambiati col server / dump-out)  header-only / payload-only
 *
 * Mappatura config &rarr; flag di verifica:
 *  - "Scambiati con il client" governa inRequest + outResponse  (clientHeader/clientPayload)
 *  - "Scambiati con il server" governa outRequest + inResponse  (serverHeader/serverPayload)
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciamentoParzialeTest extends ConfigLoader {

	private static final String API = "TestTracciamentoParziale";


	/**
	 * Profilo comune:
	 *  - Tracciamento DB abilitato (serve la riga su 'transazioni' per la query)
	 *  - Registrazione Messaggi (blocco &lt;dump&gt;) completamente disabilitata
	 *  - FileTrace abilitato con configurazione asimmetrica sulla singola erogazione/fruizione
	 * Verifiche:
	 *  - dump_messaggi: 0 righe per qualunque tipo (verificaContenuti = false)
	 *  - file di fileTrace: contenuto coerente coi flag granulari (verificaFileTraceContent = true)
	 */
	private TracciamentoVerifica baseVerifica() {
		TracciamentoVerifica v = new TracciamentoVerifica(false);
		v.verificaInRequest        = BooleanNullable.FALSE();
		v.verificaOutRequest       = BooleanNullable.FALSE();
		v.verificaOutResponse      = BooleanNullable.FALSE();
		v.verificaPostOutResponse  = BooleanNullable.TRUE();
		v.forzaVerificaDBPostOutResponse = true;

		// (1) Verifica su dump_messaggi: 0 righe attese
		v.verificaContenuti = false;

		// (2) Verifica aggiuntiva sul file di fileTrace
		v.verificaFileTraceContent = true;

		// reset dei default 'tutto presente': useremo i flag granulari clientHeader/clientPayload/serverHeader/serverPayload
		v.client = false;
		v.server = false;

		return v;
	}


	// ----------------------------------------------------------------------
	// Flusso richiesta (dump-in): registra SOLO l'header, non il payload.
	// Flusso risposta (dump-out): non registra nulla.
	// ----------------------------------------------------------------------

	private TracciamentoVerifica getRichiestaHeaderOnly() {
		TracciamentoVerifica v = baseVerifica();
		v.clientHeader  = Boolean.TRUE;
		v.clientPayload = Boolean.FALSE;
		v.serverHeader  = Boolean.FALSE;
		v.serverPayload = Boolean.FALSE;
		return v;
	}

	@Test
	public void erogazioneRichiestaHeaderOnly() throws Exception {
		TracciamentoUtils.test(logCore, TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_PARZIALE_RICHIESTA_HEADER_ONLY,
				getRichiestaHeaderOnly(), true, null, false);
	}

	@Test
	public void fruizioneRichiestaHeaderOnly() throws Exception {
		TracciamentoUtils.test(logCore, TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_PARZIALE_RICHIESTA_HEADER_ONLY,
				getRichiestaHeaderOnly(), true, null, false);
	}


	// ----------------------------------------------------------------------
	// Flusso richiesta (dump-in): registra SOLO il payload, non l'header.
	// Flusso risposta (dump-out): non registra nulla.
	// ----------------------------------------------------------------------

	private TracciamentoVerifica getRichiestaPayloadOnly() {
		TracciamentoVerifica v = baseVerifica();
		v.clientHeader  = Boolean.FALSE;
		v.clientPayload = Boolean.TRUE;
		v.serverHeader  = Boolean.FALSE;
		v.serverPayload = Boolean.FALSE;
		return v;
	}

	@Test
	public void erogazioneRichiestaPayloadOnly() throws Exception {
		TracciamentoUtils.test(logCore, TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_PARZIALE_RICHIESTA_PAYLOAD_ONLY,
				getRichiestaPayloadOnly(), true, null, false);
	}

	@Test
	public void fruizioneRichiestaPayloadOnly() throws Exception {
		TracciamentoUtils.test(logCore, TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_PARZIALE_RICHIESTA_PAYLOAD_ONLY,
				getRichiestaPayloadOnly(), true, null, false);
	}


	// ----------------------------------------------------------------------
	// Flusso richiesta (dump-in): non registra nulla.
	// Flusso risposta (dump-out): registra SOLO l'header, non il payload.
	// ----------------------------------------------------------------------

	private TracciamentoVerifica getRispostaHeaderOnly() {
		TracciamentoVerifica v = baseVerifica();
		v.clientHeader  = Boolean.FALSE;
		v.clientPayload = Boolean.FALSE;
		v.serverHeader  = Boolean.TRUE;
		v.serverPayload = Boolean.FALSE;
		return v;
	}

	@Test
	public void erogazioneRispostaHeaderOnly() throws Exception {
		TracciamentoUtils.test(logCore, TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_PARZIALE_RISPOSTA_HEADER_ONLY,
				getRispostaHeaderOnly(), true, null, false);
	}

	@Test
	public void fruizioneRispostaHeaderOnly() throws Exception {
		TracciamentoUtils.test(logCore, TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_PARZIALE_RISPOSTA_HEADER_ONLY,
				getRispostaHeaderOnly(), true, null, false);
	}


	// ----------------------------------------------------------------------
	// Flusso richiesta (dump-in): non registra nulla.
	// Flusso risposta (dump-out): registra SOLO il payload, non l'header.
	// ----------------------------------------------------------------------

	private TracciamentoVerifica getRispostaPayloadOnly() {
		TracciamentoVerifica v = baseVerifica();
		v.clientHeader  = Boolean.FALSE;
		v.clientPayload = Boolean.FALSE;
		v.serverHeader  = Boolean.FALSE;
		v.serverPayload = Boolean.TRUE;
		return v;
	}

	@Test
	public void erogazioneRispostaPayloadOnly() throws Exception {
		TracciamentoUtils.test(logCore, TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_PARZIALE_RISPOSTA_PAYLOAD_ONLY,
				getRispostaPayloadOnly(), true, null, false);
	}

	@Test
	public void fruizioneRispostaPayloadOnly() throws Exception {
		TracciamentoUtils.test(logCore, TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_PARZIALE_RISPOSTA_PAYLOAD_ONLY,
				getRispostaPayloadOnly(), true, null, false);
	}

}
