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


/**
* HttpLibrary: enum che gestisce le librerie http su cui vengono fatti i test
* incrociati dei connettori
* @author Tommaso Burlon (tommaso.burlon@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public enum HttpLibrary {
	URLCONNECTION("java.net.HttpURLConnection", "httpUrlConn", "httpsUrlConn"),
	HTTPCORE("org.apache.hc.client5", "httpcore", "httpscore");
	
	private final String libName;
	private final String httpName;
	private final String httpsName;
	
	private HttpLibrary(String libName, String httpName, String httpsName) {
		this.libName = libName;
		this.httpName = httpName;
		this.httpsName = httpsName;
	}
	
	public String getJavaLibrary() {
		return this.libName;
	}
	
	public String getHttpName() {
		return this.httpName;
	}
	
	public String getHttpsName() {
		return this.httpsName;
	}
}
