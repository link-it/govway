/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.proxy_pass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.HttpCookie;
import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {

	protected static String NOME_COOKIE = "JSESSIONID";
	
	protected static String esempio_cookie_altro_contesto = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Path=/esempio/office-ex; HttpOnly";
	
	protected static String WEB_CONTEXT="/TestService/echo";
	
	protected static String esempio_cookie_contesto_test_without_slash = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; path="+WEB_CONTEXT+"; HttpOnly";
	protected static String esempio_cookie_contesto_test_with_slash = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Path ="+WEB_CONTEXT+"/; HttpOnly";
	protected static String esempio_cookie_contesto_test_without_slash_url_parameter = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Path= "+WEB_CONTEXT+"?p1=a1; HttpOnly";
	protected static String esempio_cookie_contesto_test_with_slash_url_parameter = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Path = "+WEB_CONTEXT+"/?p1=a1; HttpOnly";
	protected static String esempio_cookie_contesto_test_without_slash_url_parameters = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; path = "+WEB_CONTEXT+"?p1=a1&p2=a2; HttpOnly";
	protected static String esempio_cookie_contesto_test_with_slash_url_parameters = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; PATH="+WEB_CONTEXT+"/?p1=a1&p2=a2; HttpOnly";
	
	protected static String esempio_cookie_contesto_test_with_context_without_slash = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; Path=\""+WEB_CONTEXT+"/altro/contesto/params\"";
	protected static String esempio_cookie_contesto_test_with_context_with_slash = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; path = \""+WEB_CONTEXT+"/altro/contesto/params/\"";
	protected static String esempio_cookie_contesto_test_with_context_without_slash_url_parameter = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; PATH= \""+WEB_CONTEXT+"/altro/contesto/params?p1=a1\"";
	protected static String esempio_cookie_contesto_test_with_context_with_slash_url_parameter = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; Path=\""+WEB_CONTEXT+"/altro/contesto/params/?p1=a1\";";
	protected static String esempio_cookie_contesto_test_with_context_without_slash_url_parameters = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; Path=\""+WEB_CONTEXT+"/altro/contesto/params?p1=a1&p2=a2\";";
	protected static String esempio_cookie_contesto_test_with_context_with_slash_url_parameters = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; Path=\""+WEB_CONTEXT+"/altro/contesto/params/?p1=a1&p2=a2\"";
	
	protected static String DOMAIN="127.0.0.1";
	protected static String esempio_cookie_domain_tradotto_1 = NOME_COOKIE+"-C1"+"=36A06E750B31E8223B60D4FFA54D2E57; Domain="+DOMAIN+"; Path=/esempio/cambio/domain";
	protected static String esempio_cookie_domain_tradotto_2 = NOME_COOKIE+"-C2"+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\""+DOMAIN+"\"; Path=/esempio/cambio/domain";
	protected static String esempio_cookie_domain_non_tradotto = NOME_COOKIE+"-CNONTRADOTTO"+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=NonTradotto; Path=/esempio/cambio/domain";
	
	@SuppressWarnings("unused")
	protected static String HEADER_HTTP_CUSTOM_SET_COOKIE = "X-Custom-Set-Cookie";
	
	protected static String REQUEST_URI="http://127.0.0.1:8080";
	protected static String REQUEST_URI_2="http://127.0.0.2:8080";
	protected static String REQUEST_URI_3="http://127.0.0.1:8083";
	protected static String REQUEST_URI_4="https://127.0.0.1:8080";
	
	protected static String esempio_location_altro_contesto = "/esempio/office-ex";
	
	protected static String esempio_location_contesto_test_without_slash = WEB_CONTEXT;
	protected static String esempio_location_contesto_test_with_slash = WEB_CONTEXT+"/";
	protected static String esempio_location_contesto_test_without_slash_url_parameter = WEB_CONTEXT+"?p1=a1";
	protected static String esempio_location_contesto_test_with_slash_url_parameter = WEB_CONTEXT+"/?p1=a1";
	protected static String esempio_location_contesto_test_without_slash_url_parameters = WEB_CONTEXT+"?p1=a1&p2=a2";
	protected static String esempio_location_contesto_test_with_slash_url_parameters = WEB_CONTEXT+"/?p1=a1&p2=a2";
	
	protected static String esempio_location_contesto_test_with_context_without_slash = WEB_CONTEXT+"/altro/contesto/params";
	protected static String esempio_location_contesto_test_with_context_with_slash = WEB_CONTEXT+"/altro/contesto/params/";
	protected static String esempio_location_contesto_test_with_context_without_slash_url_parameter = WEB_CONTEXT+"/altro/contesto/params?p1=a1";
	protected static String esempio_location_contesto_test_with_context_with_slash_url_parameter = WEB_CONTEXT+"/altro/contesto/params/?p1=a1";
	protected static String esempio_location_contesto_test_with_context_without_slash_url_parameters = WEB_CONTEXT+"/altro/contesto/params?p1=a1&p2=a2";
	protected static String esempio_location_contesto_test_with_context_with_slash_url_parameters = WEB_CONTEXT+"/altro/contesto/params/?p1=a1&p2=a2";
	
	protected static String HEADER_HTTP_CUSTOM_LOCATION = "X-Custom-Location";
	
	
	
	// cookie
	
	@Test
	public void erogazione_setCookie() throws Exception {
		_setCookie(true, TipoServizio.EROGAZIONE, "end-without-slash");
	}
	@Test
	public void fruizione_setCookie() throws Exception {
		_setCookie(true, TipoServizio.FRUIZIONE, "end-without-slash");
	}

	@Test
	public void erogazione_setCookie_endWithSlashConnettore() throws Exception {
		_setCookie(true, TipoServizio.EROGAZIONE, "end-with-slash");
	}
	@Test
	public void fruizione_setCookie_endWithSlashConnettore() throws Exception {
		_setCookie(true, TipoServizio.FRUIZIONE, "end-with-slash");
	}
	
	
	// cookie (disabilitato proxy pass)
	
	@Test
	public void erogazione_setCookie_gestioneProxyPassDisabilitata() throws Exception {
		_setCookie_nonTradotto(true, TipoServizio.EROGAZIONE, "disabled");
	}
	@Test
	public void fruizione_setCookie_gestioneProxyPassDisabilitata() throws Exception {
		_setCookie_nonTradotto(true, TipoServizio.FRUIZIONE, "disabled");
	}

	
	// cookie (custom, not match header SetCookie)
	
	@Test
	public void erogazione_setCookie_gestioneProxyPassCustom() throws Exception {
		_setCookie_nonTradotto(true, TipoServizio.EROGAZIONE, "custom");
	}
	@Test
	public void fruizione_setCookie_gestioneProxyPassCustom() throws Exception {
		_setCookie_nonTradotto(true, TipoServizio.FRUIZIONE, "custom");
	}
	
	
	
	// relative location
	
	@Test
	public void erogazione_relative_location() throws Exception {
		_location(true, TipoServizio.EROGAZIONE,"end-without-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_relative_location() throws Exception {
		_location(true, TipoServizio.FRUIZIONE,"end-without-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_contentLocation() throws Exception {
		_location(true, TipoServizio.EROGAZIONE,"end-without-slash","",HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_relative_contentLocation() throws Exception {
		_location(true, TipoServizio.FRUIZIONE,"end-without-slash","",HttpConstants.CONTENT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_location_endWithSlashConnettore() throws Exception {
		_location(true, TipoServizio.EROGAZIONE,"end-with-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_relative_location_endWithSlashConnettore() throws Exception {
		_location(true, TipoServizio.FRUIZIONE,"end-with-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_contentLocation_endWithSlashConnettore() throws Exception {
		_location(true, TipoServizio.EROGAZIONE,"end-with-slash","",HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_relative_contentLocation_endWithSlashConnettore() throws Exception {
		_location(true, TipoServizio.FRUIZIONE,"end-with-slash","",HttpConstants.CONTENT_LOCATION);
	}
	
	
	// relative location (disabilitato proxy pass)
	
	@Test
	public void erogazione_relative_location_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(true, TipoServizio.EROGAZIONE,"disabled","",HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_relative_location_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(true, TipoServizio.FRUIZIONE,"disabled","",HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(true, TipoServizio.EROGAZIONE,"disabled","",HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_relative_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(true, TipoServizio.FRUIZIONE,"disabled","",HttpConstants.CONTENT_LOCATION);
	}
	
	// relative location (custom)
	
	@Test
	public void erogazione_relative_location_gestioneProxyPassCustom() throws Exception {
		_location_custom(true, TipoServizio.EROGAZIONE,"custom","",HttpConstants.REDIRECT_LOCATION, false);
		
		_location_custom(true, TipoServizio.EROGAZIONE,"custom","",HttpConstants.CONTENT_LOCATION, false);
		
		_location_custom(true, TipoServizio.EROGAZIONE,"custom","",HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	@Test
	public void fruizione_relative_location_gestioneProxyPassCustom() throws Exception {
		_location_custom(true, TipoServizio.FRUIZIONE,"custom","",HttpConstants.REDIRECT_LOCATION, false);
		
		_location_custom(true, TipoServizio.FRUIZIONE,"custom","",HttpConstants.CONTENT_LOCATION, false);
		
		_location_custom(true, TipoServizio.FRUIZIONE,"custom","",HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	

	

	
	// absolute location
	
	@Test
	public void erogazione_absolute_location() throws Exception {
		_location(true, TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location() throws Exception {
		_location(true, TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation() throws Exception {
		_location(true, TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation() throws Exception {
		_location(true, TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_location_endWithSlashConnettore() throws Exception {
		_location(true, TipoServizio.EROGAZIONE,"end-with-slash",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location_endWithSlashConnettore() throws Exception {
		_location(true, TipoServizio.FRUIZIONE,"end-with-slash",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_endWithSlashConnettore() throws Exception {
		_location(true, TipoServizio.EROGAZIONE,"end-with-slash",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation_endWithSlashConnettore() throws Exception {
		_location(true, TipoServizio.FRUIZIONE,"end-with-slash",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	
	
	
	// absolute location (no match)
	
	@Test
	public void erogazione_absolute_location_noMatchRequestUri() throws Exception {
		_location_nonTradotta(true, TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_2,HttpConstants.REDIRECT_LOCATION);
		
		_location_nonTradotta(true, TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_3,HttpConstants.REDIRECT_LOCATION);
		
		_location_nonTradotta(true, TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_4,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location_noMatchRequestUri() throws Exception {
		_location_nonTradotta(true, TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_2,HttpConstants.REDIRECT_LOCATION);
		
		_location_nonTradotta(true, TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_3,HttpConstants.REDIRECT_LOCATION);
		
		_location_nonTradotta(true, TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_4,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_noMatchRequestUri() throws Exception {
		_location_nonTradotta(true, TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_2,HttpConstants.CONTENT_LOCATION);
		
		_location_nonTradotta(true, TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_3,HttpConstants.CONTENT_LOCATION);
		
		_location_nonTradotta(true, TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_4,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation_noMatchRequestUri() throws Exception {
		_location_nonTradotta(true, TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_2,HttpConstants.CONTENT_LOCATION);
		
		_location_nonTradotta(true, TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_3,HttpConstants.CONTENT_LOCATION);
		
		_location_nonTradotta(true, TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_4,HttpConstants.CONTENT_LOCATION);
	}

	
	
	// absolute location (disabilitato proxy pass)
	
	@Test
	public void erogazione_absolute_location_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(true, TipoServizio.EROGAZIONE,"disabled",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(true, TipoServizio.FRUIZIONE,"disabled",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(true, TipoServizio.EROGAZIONE,"disabled",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(true, TipoServizio.FRUIZIONE,"disabled",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	
	
	// absolute location (custom)
	
	@Test
	public void erogazione_absolute_location_gestioneProxyPassCustom() throws Exception {
		_location_custom(true, TipoServizio.EROGAZIONE,"custom",REQUEST_URI,HttpConstants.REDIRECT_LOCATION, false);
		
		_location_custom(true, TipoServizio.EROGAZIONE,"custom",REQUEST_URI,HttpConstants.CONTENT_LOCATION, false);
		
		_location_custom(true, TipoServizio.EROGAZIONE,"custom",REQUEST_URI,HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	@Test
	public void fruizione_absolute_location_gestioneProxyPassCustom() throws Exception {
		_location_custom(true, TipoServizio.FRUIZIONE,"custom",REQUEST_URI,HttpConstants.REDIRECT_LOCATION, false);
		
		_location_custom(true, TipoServizio.FRUIZIONE,"custom",REQUEST_URI,HttpConstants.CONTENT_LOCATION, false);
		
		_location_custom(true, TipoServizio.FRUIZIONE,"custom",REQUEST_URI,HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	
	
	
	
	protected static void _setCookie(boolean rest,TipoServizio tipoServizio,String operazione) throws Exception {
		
		String tipologia = rest ? "Rest" : "Soap";
		
		String servizio = tipoServizio == TipoServizio.EROGAZIONE
				? "/nuovoContesto/ModificaTestProxyPass"+tipologia+"-v1"
				: "/govway/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestProxyPass"+tipologia+"/v1";
		
		String domain = tipoServizio == TipoServizio.EROGAZIONE
				? "provaModificaHostEContesto"
				: "localhost";
		
		// Caso non tradotto
		HttpRequest request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_altro_contesto);
		HttpResponse response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		List<String> setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		String setValue = setValues.get(0);
		String atteso = esempio_cookie_altro_contesto;
		String msg = "[testcookie1 esempio_cookie_altro_contesto] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_without_slash);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_without_slash.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto solo un header
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_altro_contesto);
		String c2 = esempio_cookie_contesto_test_without_slash.replace(NOME_COOKIE, "ALTRODICOOKIE");
		request.addHeader("testcookie2", c2);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1,testcookie2");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(2,setValues.size());
		String setValue0 = setValues.get(0);
		String setValue1 = setValues.get(1);
		atteso = esempio_cookie_altro_contesto;
		msg = "[testcookie1-testcookie2 esempio_cookie_altro_contesto] Ricevuto0 '"+setValue0+"' Ricevuto1 '"+setValue1+"' - Atteso '"+atteso+"'";
		boolean find1 = false;
		try {
			isEquals(msg, atteso, setValue0);
		}catch(Throwable t) {
			isEquals(msg, atteso, setValue1);
			find1 = true;
		}
		atteso = c2.replace(WEB_CONTEXT, servizio);
		if(find1) {
			msg = "[testcookie1-testcookie2 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue0+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue0);
		}
		else {
			msg = "[testcookie1-testcookie2 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue1+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue1);
		}
		
		// Caso tradotto entrammbi gli header
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_without_slash);
		request.addHeader("testcookie2", c2);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1,testcookie2");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(2,setValues.size());
		setValue0 = setValues.get(0);
		setValue1 = setValues.get(1);
		atteso = esempio_cookie_contesto_test_without_slash.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1-testcookie2 esempio_cookie_altro_contesto] Ricevuto0 '"+setValue0+"' Ricevuto1 '"+setValue1+"' - Atteso '"+atteso+"'";
		find1 = false;
		try {
			isEquals(msg, atteso, setValue0);
		}catch(Throwable t) {
			isEquals(msg, atteso, setValue1);
			find1 = true;
		}
		atteso = c2.replace(WEB_CONTEXT, servizio);
		if(find1) {
			msg = "[testcookie1-testcookie2 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue0+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue0);
		}
		else {
			msg = "[testcookie1-testcookie2 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue1+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue1);
		}
		
		// Caso tradotto esempio_cookie_contesto_test_with_slash
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_with_slash);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_slash.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_with_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_without_slash_url_parameter
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_without_slash_url_parameter);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_without_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_without_slash_url_parameter] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_slash_url_parameter
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_with_slash_url_parameter);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_with_slash_url_parameter] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_without_slash_url_parameters
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_without_slash_url_parameters);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_without_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_without_slash_url_parameters] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_slash_url_parameters
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_with_slash_url_parameters);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_with_slash_url_parameters] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_without_slash
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_with_context_without_slash);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_without_slash.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_with_context_without_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_with_slash
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_with_context_with_slash);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_with_slash.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_with_context_with_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_without_slash_url_parameter
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_with_context_without_slash_url_parameter);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_without_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_with_context_without_slash_url_parameter] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_with_slash_url_parameter
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_with_context_with_slash_url_parameter);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_with_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_with_context_with_slash_url_parameter] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_without_slash_url_parameters
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_with_context_without_slash_url_parameters);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_without_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_with_context_without_slash_url_parameters] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_with_slash_url_parameters
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_with_context_with_slash_url_parameters);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_with_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[testcookie1 esempio_cookie_contesto_test_with_context_with_slash_url_parameters] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		
		// Caso tradotto domain
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_domain_tradotto_1);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		setValue = setValues.get(0);
		atteso = esempio_cookie_domain_tradotto_1.replace(DOMAIN, domain);
		msg = "[testcookie1 esempio_cookie_domain_tradotto_1] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto solo un domain
		request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_domain_non_tradotto);
		c2 = esempio_cookie_domain_tradotto_1;
		request.addHeader("testcookie2", c2);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1,testcookie2");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(2,setValues.size());
		setValue0 = setValues.get(0);
		setValue1 = setValues.get(1);
		atteso = esempio_cookie_domain_non_tradotto;
		msg = "[testcookie1-testcookie2 esempio_cookie_domain_non_tradotto] Ricevuto0 '"+setValue0+"' Ricevuto1 '"+setValue1+"' - Atteso '"+atteso+"'";
		find1 = false;
		try {
			isEquals(msg, atteso, setValue0);
		}catch(Throwable t) {
			isEquals(msg, atteso, setValue1);
			find1 = true;
		}
		atteso = c2.replace(DOMAIN, domain);
		if(find1) {
			msg = "[testcookie1-testcookie2 esempio_cookie_domain_tradotto_1] Ricevuto '"+setValue0+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue0);
		}
		else {
			msg = "[testcookie1-testcookie2 esempio_cookie_domain_tradotto_1] Ricevuto '"+setValue1+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue1);
		}
		
		// Caso tradotto entrammbi gli header
		request = new HttpRequest();
		String c1 = esempio_cookie_domain_tradotto_1;
		request.addHeader("testcookie1", c1);
		c2 = esempio_cookie_domain_tradotto_2;
		request.addHeader("testcookie2", c2);
		response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1,testcookie2");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(2,setValues.size());
		setValue0 = setValues.get(0);
		setValue1 = setValues.get(1);
		atteso = c1.replace(DOMAIN, domain);
		msg = "[testcookie1-testcookie2 esempio_cookie_domain_tradotto_1] Ricevuto0 '"+setValue0+"' Ricevuto1 '"+setValue1+"' - Atteso '"+atteso+"'";
		find1 = false;
		try {
			isEquals(msg, atteso, setValue0);
		}catch(Throwable t) {
			isEquals(msg, atteso, setValue1);
			find1 = true;
		}
		atteso = c2.replace(DOMAIN, domain);
		if(find1) {
			msg = "[testcookie1-testcookie2 esempio_cookie_domain_tradotto_2] Ricevuto '"+setValue0+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue0);
		}
		else {
			msg = "[testcookie1-testcookie2 esempio_cookie_domain_tradotto_2] Ricevuto '"+setValue1+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue1);
		}
		
	}
	
	
	protected static void _setCookie_nonTradotto(boolean rest,TipoServizio tipoServizio,String operazione) throws Exception {
		// Caso non tradotto
		HttpRequest request = new HttpRequest();
		request.addHeader("testcookie1", esempio_cookie_contesto_test_without_slash);
		HttpResponse response = _test(rest, request, tipoServizio, operazione, "setCookie=testcookie1");
		List<String> setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(1,setValues.size());
		String setValue = setValues.get(0);
		String atteso = esempio_cookie_contesto_test_without_slash; // non tradotto
		String msg = "[testcookie1 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
	}
	
	protected static void isEquals(String msg, String setCookie1, String setCookie2) {
		List<HttpCookie> l1 = java.net.HttpCookie.parse(setCookie1);
		List<HttpCookie> l2 = java.net.HttpCookie.parse(setCookie2);
		assertEquals(msg, l1.size(), l2.size());
		for (int i = 0; i < l1.size(); i++) {
			HttpCookie c1 = l1.get(i);
			HttpCookie c2 = l2.get(i);
			//assertEquals(msg+" (toString)", c1.toString(), c2.toString());
			if(c1.getComment()!=null) {
				assertEquals(msg+" (getComment)", c1.getComment(), c2.getComment());
			}
			if(c1.getCommentURL()!=null) {
				assertEquals(msg+" (getCommentURL)", c1.getCommentURL(), c2.getCommentURL());
			}
			if(c1.getPath().contains("?") && !Utils.isJenkins()){
				assertEquals(msg+" (getDiscard)", true, c2.getDiscard());
			}
			else {
				assertEquals(msg+" (getDiscard)", c1.getDiscard(), c2.getDiscard());
			}
			if(c1.getDomain()!=null) {
				assertEquals(msg+" (getDomain)", c1.getDomain(), c2.getDomain());
			}
			if(c1.getName()!=null) {
				assertEquals(msg+" (getName)", c1.getName(), c2.getName());
			}
			assertEquals(msg+" (getMaxAge)", c1.getMaxAge(), c2.getMaxAge());
			if(c1.getPath()!=null) {
				assertEquals(msg+" (getPath)", c1.getPath(), c2.getPath());
			}
			if(c1.getPortlist()!=null) {
				assertEquals(msg+" (getPortlist)", c1.getPortlist(), c2.getPortlist());
			}
			assertEquals(msg+" (getSecure)", c1.getSecure(), c2.getSecure());
			if(c1.getValue()!=null) {
				assertEquals(msg+" (getValue)", c1.getValue(), c2.getValue());
			}
			if(c1.getPath().contains("?")){
				assertEquals(msg+" (getVersion)", 1, c2.getVersion());
			}
			else {
				assertEquals(msg+" (getVersion)", c1.getVersion(), c2.getVersion());
			}
		}
	}
	
	
	protected static void _location(boolean rest,TipoServizio tipoServizio,String operazione,String prefix, String header) throws Exception {
		
		String tipologia = rest ? "Rest" : "Soap";
		
		String servizio = tipoServizio == TipoServizio.EROGAZIONE
				? "/nuovoContesto/ModificaTestProxyPass"+tipologia+"-v1"
				: "/govway/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestProxyPass"+tipologia+"/v1";
		
		String reqPrefix = tipoServizio == TipoServizio.EROGAZIONE
				? "http://provaModificaHostEContesto"
				: "http://localhost:8080";
		
		// Caso non tradotto
		HttpRequest request = new HttpRequest();
		String parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_altro_contesto,Charset.UTF_8.getValue());
		HttpResponse response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		List<String> locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		String locationValue = locationValues.get(0);
		String atteso = prefix+esempio_location_altro_contesto;
		String msg = "[esempio_location_altro_contesto] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_without_slash
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_without_slash,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_without_slash.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_without_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_slash
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_slash,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_slash.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_without_slash_url_parameter
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_without_slash_url_parameter,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_without_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_without_slash_url_parameter] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_slash_url_parameter
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_slash_url_parameter,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_slash_url_parameter] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_without_slash_url_parameters
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_without_slash_url_parameters,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_without_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_without_slash_url_parameters] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_slash_url_parameters
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_slash_url_parameters,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_slash_url_parameters] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
			
		// esempio_location_contesto_test_with_context_without_slash
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_without_slash,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_without_slash.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_without_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_context_with_slash
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_with_slash,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_with_slash.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_with_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_context_without_slash_url_parameter
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_without_slash_url_parameter,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_without_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_without_slash_url_parameter] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_context_with_slash_url_parameter
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_with_slash_url_parameter,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_with_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_with_slash_url_parameter] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_context_without_slash_url_parameters
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_without_slash_url_parameters,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_without_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_without_slash_url_parameters] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_context_with_slash_url_parameters
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_with_slash_url_parameters,Charset.UTF_8.getValue());
		response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_with_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_with_slash_url_parameters] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
	}
	
	protected static void _location_nonTradotta(boolean rest, TipoServizio tipoServizio,String operazione, String prefix, String header) throws Exception {
		
		HttpRequest request = new HttpRequest();
		String parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_without_slash,Charset.UTF_8.getValue());
		HttpResponse response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		List<String> locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		String locationValue = locationValues.get(0);
		String atteso = prefix
				+
				esempio_location_contesto_test_without_slash;
		String msg = "[esempio_location_contesto_test_without_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
	}
	
	protected static void _location_custom(boolean rest,TipoServizio tipoServizio,String operazione, String prefix, String header, boolean attesaTraduzione) throws Exception {
		
		String tipologia = rest ? "Rest" : "Soap";
		
		String servizio = tipoServizio == TipoServizio.EROGAZIONE
				? "/nuovoContesto/ModificaTestProxyPass"+tipologia+"-v1"
				: "/govway/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestProxyPass"+tipologia+"/v1";
		
		String reqPrefix = tipoServizio == TipoServizio.EROGAZIONE
				? "http://provaModificaHostEContesto"
				: "http://localhost:8080";
		
		HttpRequest request = new HttpRequest();
		String parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_without_slash,Charset.UTF_8.getValue());
		HttpResponse response = _test(rest, request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		List<String> locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(1,locationValues.size());
		String locationValue = locationValues.get(0);
		String atteso = null;
		if(attesaTraduzione) {
			atteso = prefix.replace(REQUEST_URI, reqPrefix)
					+
					esempio_location_contesto_test_without_slash.replace(WEB_CONTEXT, servizio);
		}
		else {
			atteso = prefix
					+
					esempio_location_contesto_test_without_slash;
		}
		String msg = "[esempio_location_contesto_test_without_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
	}
		
	protected static HttpResponse _test(boolean rest,HttpRequest request, TipoServizio tipoServizio, String operazione,
			String parametri) throws Exception {
		
		String tipologia = rest ? "Rest" : "Soap";
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestProxyPass"+tipologia+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestProxyPass"+tipologia+"/v1/"+operazione;
		if(parametri!=null) {
			url = url + "?" + parametri;
		}
		
		String contentType= rest ? HttpConstants.CONTENT_TYPE_JSON : HttpConstants.CONTENT_TYPE_SOAP_1_1;
		byte[]content=rest ?  Bodies.getJson(Bodies.SMALL_SIZE).getBytes() : Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
		
		if(!rest) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "test");
		}
		
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);

		HttpResponse response = HttpUtilities.httpInvoke(request);
			
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
			
		assertEquals(200, response.getResultHTTPOperation());
		assertEquals(contentType, response.getContentType());

		return response;
		
	}
}
