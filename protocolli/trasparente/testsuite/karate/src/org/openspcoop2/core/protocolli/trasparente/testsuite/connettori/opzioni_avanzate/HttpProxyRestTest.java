/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
public class HttpProxyRestTest extends ConfigLoader {
	
	static final String API = "TestHttpProxyRest";
	
	
	public static final String mitmdumpCommand = System.getProperty(HttpProxyUtils.PROPERTY_MITMDUMP_OPENSSL_COMMAND);
	
	public static final int waitStartupServer = Integer.valueOf(System.getProperty(HttpProxyUtils.PROPERTY_MITMDUMP_WAIT_STARTUP_SERVER));
	
	public static final int waitStopServer = Integer.valueOf(System.getProperty(HttpProxyUtils.PROPERTY_MITMDUMP_WAIT_STOP_SERVER));
	
	@Test
	public void erogazioneGEThttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.GET;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP);
	}
	@Test
	public void erogazioneDELETEhttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.DELETE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP);
	}
	@Test
	public void erogazioneHEADhttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.HEAD;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP);
	}
	@Test
	public void erogazioneLINKhttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.LINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP);
	}
	@Test
	public void erogazioneOPTIONShttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.OPTIONS;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP);
	}
	@Test
	public void erogazionePATCHhttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PATCH;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP);
	}
	@Test
	public void erogazionePOSThttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP);
	}
	@Test
	public void erogazionePUThttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PUT;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP);
	}
	@Test
	public void erogazioneTRACEhttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.TRACE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP);
	}
	@Test
	public void erogazioneUNLINKhttp() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.UNLINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP);
	}
	
	
	
	
	
	
	
	
	@Test
	public void erogazioneGEThttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.GET;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS);
	}
	@Test
	public void erogazioneDELETEhttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.DELETE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS);
	}
	@Test
	public void erogazioneHEADhttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.HEAD;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS);
	}
	@Test
	public void erogazioneLINKhttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.LINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS);
	}
	@Test
	public void erogazioneOPTIONShttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.OPTIONS;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS);
	}
	@Test
	public void erogazionePATCHhttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PATCH;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS);
	}
	@Test
	public void erogazionePOSThttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS);
	}
	@Test
	public void erogazionePUThttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PUT;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS);
	}
	@Test
	public void erogazioneTRACEhttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.TRACE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS);
	}
	@Test
	public void erogazioneUNLINKhttps() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.UNLINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS);
	}
	
	
	
	
	
	
	
	@Test
	public void erogazioneGEThttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.GET;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM);
	}
	@Test
	public void erogazioneDELETEhttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.DELETE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM);
	}
	@Test
	public void erogazioneHEADhttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.HEAD;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM);
	}
	@Test
	public void erogazioneLINKhttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.LINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM);
	}
	@Test
	public void erogazioneOPTIONShttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.OPTIONS;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM);
	}
	@Test
	public void erogazionePATCHhttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PATCH;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM);
	}
	@Test
	public void erogazionePOSThttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM);
	}
	@Test
	public void erogazionePUThttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PUT;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM);
	}
	@Test
	public void erogazioneTRACEhttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.TRACE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM);
	}
	@Test
	public void erogazioneUNLINKhttpsCustom() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.UNLINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void erogazioneGEThttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.GET;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH);
	}
	@Test
	public void erogazioneDELETEhttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.DELETE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH);
	}
	@Test
	public void erogazioneHEADhttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.HEAD;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH);
	}
	@Test
	public void erogazioneLINKhttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.LINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH);
	}
	@Test
	public void erogazioneOPTIONShttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.OPTIONS;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH);
	}
	@Test
	public void erogazionePATCHhttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PATCH;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH);
	}
	@Test
	public void erogazionePOSThttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH);
	}
	@Test
	public void erogazionePUThttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PUT;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH);
	}
	@Test
	public void erogazioneTRACEhttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.TRACE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH);
	}
	@Test
	public void erogazioneUNLINKhttpAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.UNLINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTP_AUTH);
	}
	
	
	
	
	
	
	
	
	@Test
	public void erogazioneGEThttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.GET;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH);
	}
	@Test
	public void erogazioneDELETEhttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.DELETE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH);
	}
	@Test
	public void erogazioneHEADhttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.HEAD;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH);
	}
	@Test
	public void erogazioneLINKhttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.LINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH);
	}
	@Test
	public void erogazioneOPTIONShttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.OPTIONS;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH);
	}
	@Test
	public void erogazionePATCHhttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PATCH;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH);
	}
	@Test
	public void erogazionePOSThttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH);
	}
	@Test
	public void erogazionePUThttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PUT;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH);
	}
	@Test
	public void erogazioneTRACEhttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.TRACE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH);
	}
	@Test
	public void erogazioneUNLINKhttpsAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.UNLINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_AUTH);
	}
	
	
	
	
	
	
	
	@Test
	public void erogazioneGEThttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.GET;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH);
	}
	@Test
	public void erogazioneDELETEhttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.DELETE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH);
	}
	@Test
	public void erogazioneHEADhttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.HEAD;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH);
	}
	@Test
	public void erogazioneLINKhttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.LINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH);
	}
	@Test
	public void erogazioneOPTIONShttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.OPTIONS;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH);
	}
	@Test
	public void erogazionePATCHhttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PATCH;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH);
	}
	@Test
	public void erogazionePOSThttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.POST;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH);
	}
	@Test
	public void erogazionePUThttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.PUT;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH);
	}
	@Test
	public void erogazioneTRACEhttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.TRACE;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH);
	}
	@Test
	public void erogazioneUNLINKhttpsCustomAuth() throws Exception {
		HttpRequestMethod method = HttpRequestMethod.UNLINK;
		HttpProxyUtils.composedTestSuccess(logCore, method, TipoServizio.EROGAZIONE, API, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				HttpProxyUtils.ACTION_HTTPS_CUSTOM_AUTH);
	}
	
	
	
	
	
}
