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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;

/**
 * Casi di superamento dei limiti sugli header della risposta, applicabili al solo connettore
 * basato su Apache HttpClient 5 ('org.apache.hc.client5'), sia in modalita' BIO che NIO.
 *
 * <p>Con la libreria del JDK i medesimi limiti non esistono: il comportamento atteso in quel caso
 * e' verificato in {@link UrlConnBIOHeaderLimitTest}.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeaderLimitHttpCoreEngine extends HeaderLimitEngine {

	@Test
	public void erogazioneHeaderOltreLimite() throws Exception {
		verifyKo(TipoServizio.EROGAZIONE, RES_HTTP1, HEADER_BYTES_OVER_LIMIT, 0, DIAG_MAX_LINE_LENGTH);
	}

	@Test
	public void fruizioneHeaderOltreLimite() throws Exception {
		verifyKo(TipoServizio.FRUIZIONE, RES_HTTP1, HEADER_BYTES_OVER_LIMIT, 0, DIAG_MAX_LINE_LENGTH);
	}

	@Test
	public void erogazioneNumeroHeaderOltreLimite() throws Exception {
		verifyKo(TipoServizio.EROGAZIONE, RES_HTTP1, 0, HEADER_COUNT_OVER_LIMIT, DIAG_MAX_HEADER_COUNT);
	}

	@Test
	public void fruizioneNumeroHeaderOltreLimite() throws Exception {
		verifyKo(TipoServizio.FRUIZIONE, RES_HTTP1, 0, HEADER_COUNT_OVER_LIMIT, DIAG_MAX_HEADER_COUNT);
	}
}
