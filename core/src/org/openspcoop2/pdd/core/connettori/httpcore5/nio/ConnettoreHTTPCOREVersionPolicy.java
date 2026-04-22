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

package org.openspcoop2.pdd.core.connettori.httpcore5.nio;

import org.apache.hc.core5.http2.HttpVersionPolicy;

/**
 * Policy di negoziazione della versione HTTP (via ALPN) utilizzata dal connettore NIO
 * basato su Apache HttpClient 5.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ConnettoreHTTPCOREVersionPolicy {

	/** Negoziazione automatica via ALPN: HTTP/2 se supportato dal server, altrimenti HTTP/1.1 (default) */
	NEGOTIATE(HttpVersionPolicy.NEGOTIATE),
	/** Forza l'uso di HTTP/1.1, disabilitando la negoziazione HTTP/2 via ALPN */
	FORCE_HTTP_1(HttpVersionPolicy.FORCE_HTTP_1),
	/** Forza l'uso di HTTP/2 */
	FORCE_HTTP_2(HttpVersionPolicy.FORCE_HTTP_2);

	private final HttpVersionPolicy httpVersionPolicy;

	private ConnettoreHTTPCOREVersionPolicy(HttpVersionPolicy httpVersionPolicy) {
		this.httpVersionPolicy = httpVersionPolicy;
	}

	public HttpVersionPolicy getHttpVersionPolicy() {
		return this.httpVersionPolicy;
	}

}
