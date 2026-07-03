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
package org.openspcoop2.pdd.core.connettori.httpcore5;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.HttpEntityWrapper;

/**
 * Entity wrapper (BIO) che, al termine della scrittura del body della richiesta verso il backend,
 * valorizza sul connettore la data <code>data_uscita_richiesta_stream</code> (istante di completa
 * spedizione del body), coerentemente con quanto fa
 * {@link org.openspcoop2.pdd.core.connettori.ConnettoreHTTP} (UrlConnection) dopo il flush/close
 * dello stream di uscita.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORERequestSentEntity extends HttpEntityWrapper {

	private final ConnettoreHTTPCORE connettore;

	public ConnettoreHTTPCORERequestSentEntity(HttpEntity wrapped, ConnettoreHTTPCORE connettore) {
		super(wrapped);
		this.connettore = connettore;
	}

	@Override
	public void writeTo(OutputStream outStream) throws IOException {
		super.writeTo(outStream);
		// body della richiesta completamente inviato al backend
		this.connettore.setDataRichiestaInoltrataNow();
	}
}
