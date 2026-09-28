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

package org.openspcoop2.core.protocolli.modipa.testsuite.rest.content_encoding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Messaggio HTTP registrato dal mock o dal proxy: header (nomi in minuscolo) e byte del body così come transitati.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentEncodingDigestExchange {

	private final String method;
	private final String uri;
	private final int status;
	private final Map<String, String> headers = new HashMap<>();
	private final byte[] body;

	/** Richiesta registrata. */
	public ContentEncodingDigestExchange(String method, String uri, Map<String, List<String>> headers, byte[] body) {
		this(method, uri, -1, headers, body);
	}

	/** Risposta registrata. */
	public ContentEncodingDigestExchange(int status, Map<String, List<String>> headers, byte[] body) {
		this(null, null, status, headers, body);
	}

	private ContentEncodingDigestExchange(String method, String uri, int status, Map<String, List<String>> headers, byte[] body) {
		this.method = method;
		this.uri = uri;
		this.status = status;
		if (headers != null) {
			for (Map.Entry<String, List<String>> e : headers.entrySet()) {
				if (e.getKey() != null && e.getValue() != null && !e.getValue().isEmpty()) {
					this.headers.put(e.getKey().toLowerCase(), e.getValue().get(0));
				}
			}
		}
		this.body = body != null ? body : new byte[0];
	}

	public String getMethod() {
		return this.method;
	}

	public String getUri() {
		return this.uri;
	}

	public int getStatus() {
		return this.status;
	}

	/** Primo valore dell'header indicato (nome case insensitive) o null se assente. */
	public String getHeader(String name) {
		return this.headers.get(name.toLowerCase());
	}

	public byte[] getBody() {
		return this.body;
	}
}
