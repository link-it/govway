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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils;

import org.openspcoop2.utils.transport.http.HttpRequest;

/**
 * TokenHttpLibraryMode
 *
 * Analogo di {@link HttpLibraryMode} ma relativo ai connettori INTERNI (negoziazione/validazione token,
 * attribute authority). Aggiunge alla richiesta l'header {@code GovWay-TestSuite-TokenHttpLibrary} che pilota
 * dinamicamente la libreria http utilizzata da tali connettori (proprieta' di govway
 * {@code org.openspcoop2.pdd.connettori.forceTokenLibraryViaHeader}). La modalita' NIO non e' applicabile ai
 * connettori interni: sono previste solo {@link HttpLibrary#HTTPCORE} (BIO) e {@link HttpLibrary#URLCONNECTION}.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TokenHttpLibraryMode {

	public static final String LIBRARY_HEADER = "GovWay-TestSuite-TokenHttpLibrary";

	private final HttpLibrary library;

	public TokenHttpLibraryMode(HttpLibrary library) {
		this.library = library;
	}

	public HttpLibrary getLibrary() {
		return this.library;
	}

	public void patchRequest(HttpRequest req) {
		req.addHeader(LIBRARY_HEADER, this.library.getJavaLibrary());
	}
}
