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
 * Esecuzione di {@link HeaderLimitHttpCoreEngine} forzando il connettore in uscita a usare
 * Apache HttpClient 5 NIO.
 *
 * <p>Alla modalita' NIO sono riservati i test sul protocollo HTTP/2, l'unico client che lo
 * supporta: i limiti 'maxHeaderLineLength' e 'maxHeaderCount' sono definiti dalla configurazione
 * HTTP/1.1 della libreria e sulle connessioni HTTP/2 non vengono applicati, poiche' il protocollo
 * prevede unicamente un limite sulla dimensione complessiva della lista di header
 * (SETTINGS_MAX_HEADER_LIST_SIZE, 16777215 bytes per default). Le risposte che su HTTP/1.1
 * vengono rifiutate devono quindi transitare senza errori su HTTP/2.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpCoreNIOHeaderLimitTest extends HeaderLimitHttpCoreEngine {

	public HttpCoreNIOHeaderLimitTest() {
		this.setHttpLibraryMode(new HttpLibraryMode(HttpLibrary.HTTPCORE, true, false));
	}

	@Test
	public void erogazioneHttp2HeaderOltreLimiteHttp1() throws Exception {
		verifyOk(TipoServizio.EROGAZIONE, RES_HTTP2, HEADER_BYTES_OVER_LIMIT, 0, PROTOCOL_HTTP_2);
	}

	@Test
	public void fruizioneHttp2HeaderOltreLimiteHttp1() throws Exception {
		verifyOk(TipoServizio.FRUIZIONE, RES_HTTP2, HEADER_BYTES_OVER_LIMIT, 0, PROTOCOL_HTTP_2);
	}

	@Test
	public void erogazioneHttp2NumeroHeaderOltreLimiteHttp1() throws Exception {
		verifyOk(TipoServizio.EROGAZIONE, RES_HTTP2, 0, HEADER_COUNT_OVER_LIMIT, PROTOCOL_HTTP_2);
	}

	@Test
	public void fruizioneHttp2NumeroHeaderOltreLimiteHttp1() throws Exception {
		verifyOk(TipoServizio.FRUIZIONE, RES_HTTP2, 0, HEADER_COUNT_OVER_LIMIT, PROTOCOL_HTTP_2);
	}
}
