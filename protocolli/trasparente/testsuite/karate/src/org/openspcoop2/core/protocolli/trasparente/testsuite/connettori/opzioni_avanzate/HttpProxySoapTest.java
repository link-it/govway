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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
* RestTest
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class HttpProxySoapTest extends ConfigLoader {
	
	static final String API = "TestHttpProxySoap";
	
	
	public static final String mitmdumpCommand = System.getProperty(HttpProxyUtils.PROPERTY_MITMDUMP_OPENSSL_COMMAND);
	
	public static final int waitStartupServer = Integer.valueOf(System.getProperty(HttpProxyUtils.PROPERTY_MITMDUMP_WAIT_STARTUP_SERVER));
	
	public static final int waitStopServer = Integer.valueOf(System.getProperty(HttpProxyUtils.PROPERTY_MITMDUMP_WAIT_STOP_SERVER));
	
	private static final boolean govwayUseHttpUrlConnection = false; // Da gestire nei test con le varie possibilità di utilizzo dei connettori

	
	
	@Test
	public void erogazionePOSThttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP,
				govwayUseHttpUrlConnection);
	}
		
	@Test
	public void erogazionePOSThttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS,
				govwayUseHttpUrlConnection);
	}
	
	@Test
	public void erogazionePOSThttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM,
				govwayUseHttpUrlConnection);
	}

	@Test
	public void erogazionePOSThttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH,
				govwayUseHttpUrlConnection);
	}
	
	@Test
	public void erogazionePOSThttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH,
				govwayUseHttpUrlConnection);
	}
	
	@Test
	public void erogazionePOSThttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH,
				govwayUseHttpUrlConnection);
	}
}
