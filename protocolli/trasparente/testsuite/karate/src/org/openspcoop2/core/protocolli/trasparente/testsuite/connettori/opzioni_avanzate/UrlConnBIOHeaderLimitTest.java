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
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibrary;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;

/**
 * Esecuzione di {@link HeaderLimitEngine} forzando il connettore in uscita a usare la libreria del
 * JDK ('java.net.HttpURLConnection').
 *
 * <p>I limiti 'maxHeaderLineLength' e 'maxHeaderCount' sono propri della libreria Apache
 * HttpClient 5: con il connettore JDK non vengono applicati, quindi le medesime risposte che
 * l'altro connettore rifiuta devono qui transitare senza errori. Questi test documentano tale
 * differenza e prevengono l'introduzione involontaria di limiti sul connettore JDK.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UrlConnBIOHeaderLimitTest extends HeaderLimitEngine {

	public UrlConnBIOHeaderLimitTest() {
		this.setHttpLibraryMode(new HttpLibraryMode(HttpLibrary.URLCONNECTION, false, false));
	}

	@Test
	public void erogazioneHeaderOltreLimiteHttpCore() throws Exception {
		verifyOk(TipoServizio.EROGAZIONE, RES_HTTP1, HEADER_BYTES_OVER_LIMIT, 0, PROTOCOL_HTTP_1_1);
	}

	@Test
	public void fruizioneHeaderOltreLimiteHttpCore() throws Exception {
		verifyOk(TipoServizio.FRUIZIONE, RES_HTTP1, HEADER_BYTES_OVER_LIMIT, 0, PROTOCOL_HTTP_1_1);
	}

	@Test
	public void erogazioneNumeroHeaderOltreLimiteHttpCore() throws Exception {
		verifyOk(TipoServizio.EROGAZIONE, RES_HTTP1, 0, HEADER_COUNT_OVER_LIMIT, PROTOCOL_HTTP_1_1);
	}

	@Test
	public void fruizioneNumeroHeaderOltreLimiteHttpCore() throws Exception {
		verifyOk(TipoServizio.FRUIZIONE, RES_HTTP1, 0, HEADER_COUNT_OVER_LIMIT, PROTOCOL_HTTP_1_1);
	}
}
