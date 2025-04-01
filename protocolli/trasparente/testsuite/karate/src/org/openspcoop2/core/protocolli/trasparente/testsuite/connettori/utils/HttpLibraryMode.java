/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.util.regex.Pattern;

import org.openspcoop2.utils.transport.http.HttpRequest;

/**
* HttpLibraryMode: POJO che mantiene informazioni sul tipo di libreria usata e se
* impostata la gestione NIO/BIO del connettore.
* @author Tommaso Burlon (tommaso.burlon@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class HttpLibraryMode {
	private static final String LIBRARY_HEADER = "GovWay-TestSuite-HttpLibrary";
	
	private boolean nio;
	private HttpLibrary library;
	private boolean https;
	
	public HttpLibraryMode(HttpLibrary library, boolean isNio, boolean https) {
		this.nio = isNio;
		this.library = library;
		this.https = https;
	}
	
	public boolean isNIO() {
		return this.nio;
	}
	
	public HttpLibrary getLibrary() {
		return this.library;
	}
	
	public boolean isHttps() {
		return this.https;
	}
	
	public HttpLibraryMode https(boolean https) {
		this.https = https;
		return this;
	}
	
	public void patchRequest(HttpRequest req) {
		String syncType = this.nio ? "async/" : "";
		String url = req.getUrl()
				.replaceFirst("/in/", "/in/" + syncType)
				.replaceFirst("/out/", "/out/" + syncType);
		req.setUrl(url);
		req.addHeader(LIBRARY_HEADER, this.library.getJavaLibrary());
	}
	
	public Pattern getExpectedMessage() {
		String lib = "(" + this.library.getHttpsName() + "|" + this.library.getHttpName() + ")";
		if (this.nio)
			lib = lib + "-nio";
		return Pattern.compile(".*\\[" + lib + "\\].*", Pattern.DOTALL);
	}
}
