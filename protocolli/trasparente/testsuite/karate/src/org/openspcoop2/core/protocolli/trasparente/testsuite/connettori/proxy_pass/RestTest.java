/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

	private static String NOME_COOKIE = "JSESSIONID";
	
	private static String esempio_cookie_altro_contesto = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Path=/esempio/office-ex; HttpOnly";
	
	private static String WEB_CONTEXT="/TestService/echo";
	
	private static String esempio_cookie_contesto_test_without_slash = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; path="+WEB_CONTEXT+"; HttpOnly";
	private static String esempio_cookie_contesto_test_with_slash = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Path ="+WEB_CONTEXT+"/; HttpOnly";
	private static String esempio_cookie_contesto_test_without_slash_url_parameter = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Path= "+WEB_CONTEXT+"?p1=a1; HttpOnly";
	private static String esempio_cookie_contesto_test_with_slash_url_parameter = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Path = "+WEB_CONTEXT+"/?p1=a1; HttpOnly";
	private static String esempio_cookie_contesto_test_without_slash_url_parameters = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; path = "+WEB_CONTEXT+"?p1=a1&p2=a2; HttpOnly";
	private static String esempio_cookie_contesto_test_with_slash_url_parameters = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; PATH="+WEB_CONTEXT+"/?p1=a1&p2=a2; HttpOnly";
	
	private static String esempio_cookie_contesto_test_with_context_without_slash = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; Path=\""+WEB_CONTEXT+"/altro/contesto/params\"";
	private static String esempio_cookie_contesto_test_with_context_with_slash = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; path = \""+WEB_CONTEXT+"/altro/contesto/params/\"";
	private static String esempio_cookie_contesto_test_with_context_without_slash_url_parameter = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; PATH= \""+WEB_CONTEXT+"/altro/contesto/params?p1=a1\"";
	private static String esempio_cookie_contesto_test_with_context_with_slash_url_parameter = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; Path=\""+WEB_CONTEXT+"/altro/contesto/params/?p1=a1\";";
	private static String esempio_cookie_contesto_test_with_context_without_slash_url_parameters = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; Path=\""+WEB_CONTEXT+"/altro/contesto/params?p1=a1&p2=a2\";";
	private static String esempio_cookie_contesto_test_with_context_with_slash_url_parameters = NOME_COOKIE+"=36A06E750B31E8223B60D4FFA54D2E57; Domain=\"link.it\"; Path=\""+WEB_CONTEXT+"/altro/contesto/params/?p1=a1&p2=a2\"";
	
	@SuppressWarnings("unused")
	private static String HEADER_HTTP_CUSTOM_SET_COOKIE = "X-Custom-Set-Cookie";
	
	private static String REQUEST_URI="http://127.0.0.1:8080";
	private static String REQUEST_URI_2="http://127.0.0.2:8080";
	private static String REQUEST_URI_3="http://127.0.0.1:8083";
	private static String REQUEST_URI_4="https://127.0.0.1:8080";
	
	private static String esempio_location_altro_contesto = "/esempio/office-ex";
	
	private static String esempio_location_contesto_test_without_slash = WEB_CONTEXT;
	private static String esempio_location_contesto_test_with_slash = WEB_CONTEXT+"/";
	private static String esempio_location_contesto_test_without_slash_url_parameter = WEB_CONTEXT+"?p1=a1";
	private static String esempio_location_contesto_test_with_slash_url_parameter = WEB_CONTEXT+"/?p1=a1";
	private static String esempio_location_contesto_test_without_slash_url_parameters = WEB_CONTEXT+"?p1=a1&p2=a2";
	private static String esempio_location_contesto_test_with_slash_url_parameters = WEB_CONTEXT+"/?p1=a1&p2=a2";
	
	private static String esempio_location_contesto_test_with_context_without_slash = WEB_CONTEXT+"/altro/contesto/params";
	private static String esempio_location_contesto_test_with_context_with_slash = WEB_CONTEXT+"/altro/contesto/params/";
	private static String esempio_location_contesto_test_with_context_without_slash_url_parameter = WEB_CONTEXT+"/altro/contesto/params?p1=a1";
	private static String esempio_location_contesto_test_with_context_with_slash_url_parameter = WEB_CONTEXT+"/altro/contesto/params/?p1=a1";
	private static String esempio_location_contesto_test_with_context_without_slash_url_parameters = WEB_CONTEXT+"/altro/contesto/params?p1=a1&p2=a2";
	private static String esempio_location_contesto_test_with_context_with_slash_url_parameters = WEB_CONTEXT+"/altro/contesto/params/?p1=a1&p2=a2";
	
	private static String HEADER_HTTP_CUSTOM_LOCATION = "X-Custom-Location";
	
	
	
	// cookie
	
	@Test
	public void erogazione_setCookie() throws Exception {
		_setCookie(TipoServizio.EROGAZIONE, "end-without-slash");
	}
	@Test
	public void fruizione_setCookie() throws Exception {
		_setCookie(TipoServizio.FRUIZIONE, "end-without-slash");
	}

	@Test
	public void erogazione_setCookie_endWithSlashConnettore() throws Exception {
		_setCookie(TipoServizio.EROGAZIONE, "end-with-slash");
	}
	@Test
	public void fruizione_setCookie_endWithSlashConnettore() throws Exception {
		_setCookie(TipoServizio.FRUIZIONE, "end-with-slash");
	}
	
	
	// cookie (disabilitato proxy pass)
	
	@Test
	public void erogazione_setCookie_gestioneProxyPassDisabilitata() throws Exception {
		// Caso non tradotto
		HttpRequest request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_without_slash);
		HttpResponse response = _test(request, TipoServizio.EROGAZIONE, "disabled", "setCookie=C1");
		List<String> setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		String setValue = setValues.get(0);
		String atteso = esempio_cookie_contesto_test_without_slash; // non tradotto
		String msg = "[C1 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
	}
	@Test
	public void fruizione_setCookie_gestioneProxyPassDisabilitata() throws Exception {
		// Caso non tradotto
		HttpRequest request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_without_slash);
		HttpResponse response = _test(request, TipoServizio.FRUIZIONE, "disabled", "setCookie=C1");
		List<String> setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		String setValue = setValues.get(0);
		String atteso = esempio_cookie_contesto_test_without_slash; // non tradotto
		String msg = "[C1 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
	}

	
	// cookie (custom, not match header SetCookie)
	
	@Test
	public void erogazione_setCookie_gestioneProxyPassCustom() throws Exception {
		// Caso non tradotto
		HttpRequest request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_without_slash);
		HttpResponse response = _test(request, TipoServizio.EROGAZIONE, "custom", "setCookie=C1");
		List<String> setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		String setValue = setValues.get(0);
		String atteso = esempio_cookie_contesto_test_without_slash; // non tradotto
		String msg = "[C1 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
	}
	@Test
	public void fruizione_setCookie_gestioneProxyPassCustom() throws Exception {
		// Caso non tradotto
		HttpRequest request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_without_slash);
		HttpResponse response = _test(request, TipoServizio.FRUIZIONE, "custom", "setCookie=C1");
		List<String> setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		String setValue = setValues.get(0);
		String atteso = esempio_cookie_contesto_test_without_slash; // non tradotto
		String msg = "[C1 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
	}
	
	
	
	// relative location
	
	@Test
	public void erogazione_relative_location() throws Exception {
		_location(TipoServizio.EROGAZIONE,"end-without-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_relative_location() throws Exception {
		_location(TipoServizio.FRUIZIONE,"end-without-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_contentLocation() throws Exception {
		_location(TipoServizio.EROGAZIONE,"end-without-slash","",HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_relative_contentLocation() throws Exception {
		_location(TipoServizio.FRUIZIONE,"end-without-slash","",HttpConstants.CONTENT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_location_endWithSlashConnettore() throws Exception {
		_location(TipoServizio.EROGAZIONE,"end-with-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_relative_location_endWithSlashConnettore() throws Exception {
		_location(TipoServizio.FRUIZIONE,"end-with-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_contentLocation_endWithSlashConnettore() throws Exception {
		_location(TipoServizio.EROGAZIONE,"end-with-slash","",HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_relative_contentLocation_endWithSlashConnettore() throws Exception {
		_location(TipoServizio.FRUIZIONE,"end-with-slash","",HttpConstants.CONTENT_LOCATION);
	}
	
	
	// relative location (disabilitato proxy pass)
	
	@Test
	public void erogazione_relative_location_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(TipoServizio.EROGAZIONE,"disabled","",HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_relative_location_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(TipoServizio.FRUIZIONE,"disabled","",HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(TipoServizio.EROGAZIONE,"disabled","",HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_relative_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(TipoServizio.FRUIZIONE,"disabled","",HttpConstants.CONTENT_LOCATION);
	}
	
	// relative location (custom)
	
	@Test
	public void erogazione_relative_location_gestioneProxyPassCustom() throws Exception {
		_location_custom(TipoServizio.EROGAZIONE,"custom","",HttpConstants.REDIRECT_LOCATION, false);
		
		_location_custom(TipoServizio.EROGAZIONE,"custom","",HttpConstants.CONTENT_LOCATION, false);
		
		_location_custom(TipoServizio.EROGAZIONE,"custom","",HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	@Test
	public void fruizione_relative_location_gestioneProxyPassCustom() throws Exception {
		_location_custom(TipoServizio.FRUIZIONE,"custom","",HttpConstants.REDIRECT_LOCATION, false);
		
		_location_custom(TipoServizio.FRUIZIONE,"custom","",HttpConstants.CONTENT_LOCATION, false);
		
		_location_custom(TipoServizio.FRUIZIONE,"custom","",HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	

	

	
	// absolute location
	
	@Test
	public void erogazione_absolute_location() throws Exception {
		_location(TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location() throws Exception {
		_location(TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation() throws Exception {
		_location(TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation() throws Exception {
		_location(TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_location_endWithSlashConnettore() throws Exception {
		_location(TipoServizio.EROGAZIONE,"end-with-slash",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location_endWithSlashConnettore() throws Exception {
		_location(TipoServizio.FRUIZIONE,"end-with-slash",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_endWithSlashConnettore() throws Exception {
		_location(TipoServizio.EROGAZIONE,"end-with-slash",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation_endWithSlashConnettore() throws Exception {
		_location(TipoServizio.FRUIZIONE,"end-with-slash",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	
	
	
	// absolute location (no match)
	
	@Test
	public void erogazione_absolute_location_noMatchRequestUri() throws Exception {
		_location_nonTradotta(TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_2,HttpConstants.REDIRECT_LOCATION);
		
		_location_nonTradotta(TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_3,HttpConstants.REDIRECT_LOCATION);
		
		_location_nonTradotta(TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_4,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location_noMatchRequestUri() throws Exception {
		_location_nonTradotta(TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_2,HttpConstants.REDIRECT_LOCATION);
		
		_location_nonTradotta(TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_3,HttpConstants.REDIRECT_LOCATION);
		
		_location_nonTradotta(TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_4,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_noMatchRequestUri() throws Exception {
		_location_nonTradotta(TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_2,HttpConstants.CONTENT_LOCATION);
		
		_location_nonTradotta(TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_3,HttpConstants.CONTENT_LOCATION);
		
		_location_nonTradotta(TipoServizio.EROGAZIONE,"end-without-slash",REQUEST_URI_4,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation_noMatchRequestUri() throws Exception {
		_location_nonTradotta(TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_2,HttpConstants.CONTENT_LOCATION);
		
		_location_nonTradotta(TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_3,HttpConstants.CONTENT_LOCATION);
		
		_location_nonTradotta(TipoServizio.FRUIZIONE,"end-without-slash",REQUEST_URI_4,HttpConstants.CONTENT_LOCATION);
	}

	
	
	// absolute location (disabilitato proxy pass)
	
	@Test
	public void erogazione_absolute_location_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(TipoServizio.EROGAZIONE,"disabled",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(TipoServizio.FRUIZIONE,"disabled",REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(TipoServizio.EROGAZIONE,"disabled",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		_location_nonTradotta(TipoServizio.FRUIZIONE,"disabled",REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	
	
	// absolute location (custom)
	
	@Test
	public void erogazione_absolute_location_gestioneProxyPassCustom() throws Exception {
		_location_custom(TipoServizio.EROGAZIONE,"custom",REQUEST_URI,HttpConstants.REDIRECT_LOCATION, false);
		
		_location_custom(TipoServizio.EROGAZIONE,"custom",REQUEST_URI,HttpConstants.CONTENT_LOCATION, false);
		
		_location_custom(TipoServizio.EROGAZIONE,"custom",REQUEST_URI,HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	@Test
	public void fruizione_absolute_location_gestioneProxyPassCustom() throws Exception {
		_location_custom(TipoServizio.FRUIZIONE,"custom",REQUEST_URI,HttpConstants.REDIRECT_LOCATION, false);
		
		_location_custom(TipoServizio.FRUIZIONE,"custom",REQUEST_URI,HttpConstants.CONTENT_LOCATION, false);
		
		_location_custom(TipoServizio.FRUIZIONE,"custom",REQUEST_URI,HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	
	
	
	
	private void _setCookie(TipoServizio tipoServizio,String operazione) throws Exception {
		
		String servizio = tipoServizio == TipoServizio.EROGAZIONE
				? "/nuovoContesto/ModificaTestProxyPassRest-v1"
				: "/govway/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestProxyPassRest/v1";
		
		// Caso non tradotto
		HttpRequest request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_altro_contesto);
		HttpResponse response = _test(request, tipoServizio, operazione, "setCookie=C1");
		List<String> setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		String setValue = setValues.get(0);
		String atteso = esempio_cookie_altro_contesto;
		String msg = "[C1 esempio_cookie_altro_contesto] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_without_slash);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_without_slash.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto solo un header
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_altro_contesto);
		String c2 = esempio_cookie_contesto_test_without_slash.replace(NOME_COOKIE, "ALTRODICOOKIE");
		request.addHeader("C2", c2);
		response = _test(request, tipoServizio, operazione, "setCookie=C1,C2");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),2);
		String setValue0 = setValues.get(0);
		String setValue1 = setValues.get(1);
		atteso = esempio_cookie_altro_contesto;
		msg = "[C1-C2 esempio_cookie_altro_contesto] Ricevuto0 '"+setValue0+"' Ricevuto1 '"+setValue1+"' - Atteso '"+atteso+"'";
		boolean find1 = false;
		try {
			isEquals(msg, atteso, setValue0);
		}catch(Throwable t) {
			isEquals(msg, atteso, setValue1);
			find1 = true;
		}
		atteso = c2.replace(WEB_CONTEXT, servizio);
		if(find1) {
			msg = "[C1-C2 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue0+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue0);
		}
		else {
			msg = "[C1-C2 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue1+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue1);
		}
		
		// Caso tradotto entrammbi gli header
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_without_slash);
		request.addHeader("C2", c2);
		response = _test(request, tipoServizio, operazione, "setCookie=C1,C2");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),2);
		setValue0 = setValues.get(0);
		setValue1 = setValues.get(1);
		atteso = esempio_cookie_contesto_test_without_slash.replace(WEB_CONTEXT, servizio);
		msg = "[C1-C2 esempio_cookie_altro_contesto] Ricevuto0 '"+setValue0+"' Ricevuto1 '"+setValue1+"' - Atteso '"+atteso+"'";
		find1 = false;
		try {
			isEquals(msg, atteso, setValue0);
		}catch(Throwable t) {
			isEquals(msg, atteso, setValue1);
			find1 = true;
		}
		atteso = c2.replace(WEB_CONTEXT, servizio);
		if(find1) {
			msg = "[C1-C2 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue0+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue0);
		}
		else {
			msg = "[C1-C2 esempio_cookie_contesto_test_without_slash] Ricevuto '"+setValue1+"' - Atteso '"+atteso+"'";
			isEquals(msg, atteso, setValue1);
		}
		
		// Caso tradotto esempio_cookie_contesto_test_with_slash
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_with_slash);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_slash.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_with_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_without_slash_url_parameter
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_without_slash_url_parameter);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_without_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_without_slash_url_parameter] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_slash_url_parameter
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_with_slash_url_parameter);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_with_slash_url_parameter] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_without_slash_url_parameters
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_without_slash_url_parameters);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_without_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_without_slash_url_parameters] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_slash_url_parameters
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_with_slash_url_parameters);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_with_slash_url_parameters] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_without_slash
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_with_context_without_slash);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_without_slash.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_with_context_without_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_with_slash
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_with_context_with_slash);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_with_slash.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_with_context_with_slash] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_without_slash_url_parameter
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_with_context_without_slash_url_parameter);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_without_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_with_context_without_slash_url_parameter] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_with_slash_url_parameter
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_with_context_with_slash_url_parameter);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_with_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_with_context_with_slash_url_parameter] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_without_slash_url_parameters
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_with_context_without_slash_url_parameters);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_without_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_with_context_without_slash_url_parameters] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
		
		// Caso tradotto esempio_cookie_contesto_test_with_context_with_slash_url_parameters
		request = new HttpRequest();
		request.addHeader("C1", esempio_cookie_contesto_test_with_context_with_slash_url_parameters);
		response = _test(request, tipoServizio, operazione, "setCookie=C1");
		setValues = TransportUtils.getValues(response.getHeadersValues(),HttpConstants.SET_COOKIE);
		assertNotNull(setValues);
		assertEquals(setValues.size(),1);
		setValue = setValues.get(0);
		atteso = esempio_cookie_contesto_test_with_context_with_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[C1 esempio_cookie_contesto_test_with_context_with_slash_url_parameters] Ricevuto '"+setValue+"' - Atteso '"+atteso+"'";
		isEquals(msg, atteso, setValue);
	}
	
	private void isEquals(String msg, String setCookie1, String setCookie2) {
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
			if(c1.getPath().contains("?")){
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
	
	
	private void _location(TipoServizio tipoServizio,String operazione,String prefix, String header) throws Exception {
		
		String servizio = tipoServizio == TipoServizio.EROGAZIONE
				? "/nuovoContesto/ModificaTestProxyPassRest-v1"
				: "/govway/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestProxyPassRest/v1";
		
		String reqPrefix = tipoServizio == TipoServizio.EROGAZIONE
				? "http://provaModificaHostEContesto"
				: "http://localhost:8080";
		
		// Caso non tradotto
		HttpRequest request = new HttpRequest();
		String parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_altro_contesto,Charset.UTF_8.getValue());
		HttpResponse response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		List<String> locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		String locationValue = locationValues.get(0);
		String atteso = prefix+esempio_location_altro_contesto;
		String msg = "[esempio_location_altro_contesto] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_without_slash
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_without_slash,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_without_slash.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_without_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_slash
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_slash,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_slash.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_without_slash_url_parameter
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_without_slash_url_parameter,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_without_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_without_slash_url_parameter] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_slash_url_parameter
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_slash_url_parameter,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_slash_url_parameter] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_without_slash_url_parameters
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_without_slash_url_parameters,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_without_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_without_slash_url_parameters] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_slash_url_parameters
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_slash_url_parameters,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_slash_url_parameters] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
			
		// esempio_location_contesto_test_with_context_without_slash
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_without_slash,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_without_slash.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_without_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_context_with_slash
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_with_slash,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_with_slash.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_with_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_context_without_slash_url_parameter
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_without_slash_url_parameter,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_without_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_without_slash_url_parameter] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_context_with_slash_url_parameter
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_with_slash_url_parameter,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_with_slash_url_parameter.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_with_slash_url_parameter] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_context_without_slash_url_parameters
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_without_slash_url_parameters,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_without_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_without_slash_url_parameters] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
		// esempio_location_contesto_test_with_context_with_slash_url_parameters
		request = new HttpRequest();
		parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_with_context_with_slash_url_parameters,Charset.UTF_8.getValue());
		response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		locationValue = locationValues.get(0);
		atteso = prefix.replace(REQUEST_URI, reqPrefix)
				+
				esempio_location_contesto_test_with_context_with_slash_url_parameters.replace(WEB_CONTEXT, servizio);
		msg = "[esempio_location_contesto_test_with_context_with_slash_url_parameters] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
	}
	
	private void _location_nonTradotta(TipoServizio tipoServizio,String operazione, String prefix, String header) throws Exception {
		
		HttpRequest request = new HttpRequest();
		String parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_without_slash,Charset.UTF_8.getValue());
		HttpResponse response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		List<String> locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
		String locationValue = locationValues.get(0);
		String atteso = prefix
				+
				esempio_location_contesto_test_without_slash;
		String msg = "[esempio_location_contesto_test_without_slash] Ricevuto '"+locationValue+"' - Atteso '"+atteso+"'";
		assertEquals(msg, atteso, locationValue);
		
	}
	
	private void _location_custom(TipoServizio tipoServizio,String operazione, String prefix, String header, boolean attesaTraduzione) throws Exception {
		
		String servizio = tipoServizio == TipoServizio.EROGAZIONE
				? "/nuovoContesto/ModificaTestProxyPassRest-v1"
				: "/govway/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestProxyPassRest/v1";
		
		String reqPrefix = tipoServizio == TipoServizio.EROGAZIONE
				? "http://provaModificaHostEContesto"
				: "http://localhost:8080";
		
		HttpRequest request = new HttpRequest();
		String parametroCodificare = TransportUtils.urlEncodeParam(header+":"+prefix+esempio_location_contesto_test_without_slash,Charset.UTF_8.getValue());
		HttpResponse response = _test(request, tipoServizio, operazione, "returnHttpHeader="+parametroCodificare);
		List<String> locationValues = TransportUtils.getValues(response.getHeadersValues(),header);
		assertNotNull(locationValues);
		assertEquals(locationValues.size(),1);
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
		
	private static HttpResponse _test(HttpRequest request, TipoServizio tipoServizio, String operazione,
			String parametri) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestProxyPassRest/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestProxyPassRest/v1/"+operazione;
		if(parametri!=null) {
			url = url + "?" + parametri;
		}
		
		String contentType= HttpConstants.CONTENT_TYPE_JSON;
		byte[]content=Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);

		HttpResponse response = HttpUtilities.httpInvoke(request);
			
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
			
		assertEquals(200, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_JSON, response.getContentType());

		return response;
		
	}
}
