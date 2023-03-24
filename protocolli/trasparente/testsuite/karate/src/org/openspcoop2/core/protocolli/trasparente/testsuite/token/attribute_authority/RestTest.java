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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.attribute_authority;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import net.minidev.json.JSONObject;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {

	// *** singleAA: autorizzazione per contenuti (AA JWS Bearer) ***
	@Test
	public void erogazione_singleAA_authzContenuti() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		_test(TipoServizio.EROGAZIONE, "contenuti_singleAA",
				"singleAA_authzContenuti", null, map);
	}
	@Test
	public void fruizione_singleAA_authzContenuti() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		_test(TipoServizio.FRUIZIONE, "contenuti_singleAA",
				"singleAA_authzContenuti", null, map);
	}
	@Test
	public void erogazione_singleAA_authzContenuti_errorAppFruitore() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore2";
		map.expectedAttributes.put("sub", map.expectedAttributes.get("sub").replace("ApplicativoSoggettoInternoTestFruitore1", "ApplicativoSoggettoInternoTestFruitore2"));
		_test(TipoServizio.EROGAZIONE, "contenuti_singleAA",
				"singleAA_authzContenuti_errorAppFruitore", 
				"Resource '${aa:attributes[sub]}' with unexpected value 'ApplicativoSoggettoInternoTestFruitore2'", 
				map);
	}
	@Test
	public void fruizione_singleAA_authzContenuti_errorAppFruitore() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore2";
		map.expectedAttributes.put("sub", map.expectedAttributes.get("sub").replace("ApplicativoSoggettoInternoTestFruitore1", "ApplicativoSoggettoInternoTestFruitore2"));
		_test(TipoServizio.FRUIZIONE, "contenuti_singleAA",
				"singleAA_authzContenuti_errorAppFruitore",
				"Resource '${aa:attributes[sub]}' with unexpected value 'ApplicativoSoggettoInternoTestFruitore2'", 
				map);
	}
	@Test
	public void erogazione_singleAA_authzContenuti_errorAttr1() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		map.httpHeaders.put("GovWay-TestSuite-AAa1vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a1", map.expectedAttributes.get("a1").replace("v1-OK", "ValoreErrato"));
		_test(TipoServizio.EROGAZIONE, "contenuti_singleAA",
				"singleAA_authzContenuti_errorAttr1", 
				"Resource '${aa:attributes[a1]}' with unexpected value '[av11, av12, av13, ValoreErrato]' (regExpr find failed)", 
				map);
	}
	@Test
	public void fruizione_singleAA_authzContenuti_errorAttr1() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		map.httpHeaders.put("GovWay-TestSuite-AAa1vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a1", map.expectedAttributes.get("a1").replace("v1-OK", "ValoreErrato"));
		_test(TipoServizio.FRUIZIONE, "contenuti_singleAA",
				"singleAA_authzContenuti_errorAttr1",
				"Resource '${aa:attributes[a1]}' with unexpected value '[av11, av12, av13, ValoreErrato]' (regExpr find failed)", 
				map);
	}
	@Test
	public void erogazione_singleAA_authzContenuti_errorAttr2() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		map.httpHeaders.put("GovWay-TestSuite-AAa2vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a2", map.expectedAttributes.get("a2").replace("v2-OK", "ValoreErrato"));
		_test(TipoServizio.EROGAZIONE, "contenuti_singleAA",
				"singleAA_authzContenuti_errorAttr2", 
				"Resource '${aa:attributes[a2]}' with unexpected value 'ValoreErrato'", 
				map);
	}
	@Test
	public void fruizione_singleAA_authzContenuti_errorAttr2() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		map.httpHeaders.put("GovWay-TestSuite-AAa2vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a2", map.expectedAttributes.get("a2").replace("v2-OK", "ValoreErrato"));
		_test(TipoServizio.FRUIZIONE, "contenuti_singleAA",
				"singleAA_authzContenuti_errorAttr2",
				"Resource '${aa:attributes[a2]}' with unexpected value 'ValoreErrato'", 
				map);
	}
	
	
	
	// *** singleAA: autorizzazione per contenuti (AA JWS Bearer con validazione tramite jwks 'kid') ***
	@Test
	public void erogazione_singleAA_authzContenuti_jwksKID() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		_test(TipoServizio.EROGAZIONE, "contenuti_singleAA_jwksKID",
				"singleAA_authzContenuti_jwksKID", null, map);
	}
	

	
	// *** multipleAA: autorizzazione per contenuti (AA JWS Bearer, Payload, Header HTTP e Parametro URL) ***
	@Test
	public void erogazione_multipleAA_authzContenuti() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.EROGAZIONE, "contenuti_multipleAA",
				"multipleAA_authzContenuti", null, map);
	}
	@Test
	public void fruizione_multipleAA_authzContenuti() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.FRUIZIONE, "contenuti_multipleAA",
				"multipleAA_authzContenuti", null, map);
	}
	@Test
	public void erogazione_multipleAA_authzContenuti_errorUsername() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username2", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username2\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.EROGAZIONE, "contenuti_multipleAA",
				"multipleAA_authzContenuti_errorUsername", 
				//"Resource '${aa:attributes[HeaderHTTP][sub]}' with unexpected value 'Username2'", 
				"[sub]}' with unexpected value 'Username2'",  // tomcat fix
				map);
	}
	@Test
	public void fruizione_multipleAA_authzContenuti_errorUsername() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username2", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username2\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.FRUIZIONE, "contenuti_multipleAA",
				"multipleAA_authzContenuti_errorUsername", 
				//"Resource '${aa:attributes[HeaderHTTP][sub]}' with unexpected value 'Username2'",
				"[sub]}' with unexpected value 'Username2'",  // tomcat fix
				map);
	}
	@Test
	public void erogazione_multipleAA_authzContenuti_errorAudience() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://AudienceERRATO.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://AudienceERRATO.example\"");
		
		_test(TipoServizio.EROGAZIONE, "contenuti_multipleAA",
				"multipleAA_authzContenuti_errorAudience", 
				//"Resource '${aa:attributes[HeaderHTTP][aud]}' with unexpected value 'http://AudienceERRATO.example'",
				"[aud]}' with unexpected value 'http://AudienceERRATO.example'", // tomcat fix
				map);
	}
	@Test
	public void fruizione_multipleAA_authzContenuti_errorAudience() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://AudienceERRATO.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://AudienceERRATO.example\"");
		
		_test(TipoServizio.FRUIZIONE, "contenuti_multipleAA",
				"multipleAA_authzContenuti_errorAudience", 
				//"Resource '${aa:attributes[HeaderHTTP][aud]}' with unexpected value 'http://AudienceERRATO.example'",
				"[aud]}' with unexpected value 'http://AudienceERRATO.example'", // tomcat fix
				map);
	}
	
	
	
	
	// *** singleAA: autorizzazione per token claims (AA PayloadJSON) ***
	@Test
	public void erogazione_singleAA_authzTokenClaims() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		_test(TipoServizio.EROGAZIONE, "tokenClaims_singleAA",
				"singleAA_authzTokenClaims", null, map);
	}
	@Test
	public void fruizione_singleAA_authzTokenClaims() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		_test(TipoServizio.FRUIZIONE, "tokenClaims_singleAA",
				"singleAA_authzTokenClaims", null, map);
	}
	@Test
	public void erogazione_singleAA_authzTokenClaims_errorAppFruitore() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore2");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));		
		map.expectedAttributes.put("sub", map.expectedAttributes.get("sub").replace("ApplicativoSoggettoInternoTestFruitore1", "ApplicativoSoggettoInternoTestFruitore2"));
		_test(TipoServizio.EROGAZIONE, "tokenClaims_singleAA",
				"singleAA_authzTokenClaims_errorAppFruitore", 
				"Token attribute 'sub' with unexpected value", 
				map);
	}
	@Test
	public void fruizione_singleAA_authzTokenClaims_errorAppFruitore() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore2");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
			JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		map.expectedAttributes.put("sub", map.expectedAttributes.get("sub").replace("ApplicativoSoggettoInternoTestFruitore1", "ApplicativoSoggettoInternoTestFruitore2"));
		_test(TipoServizio.FRUIZIONE, "tokenClaims_singleAA",
				"singleAA_authzTokenClaims_errorAppFruitore",
				"Token attribute 'sub' with unexpected value", 
				map);
	}
	@Test
	public void erogazione_singleAA_authzTokenClaims_errorAttr1() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
			JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		map.httpHeaders.put("GovWay-TestSuite-AAa1vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a1", map.expectedAttributes.get("a1").replace("v1-OK", "ValoreErrato"));
		_test(TipoServizio.EROGAZIONE, "tokenClaims_singleAA",
				"singleAA_authzTokenClaims_errorAttr1", 
				"Token attribute 'a1' with unexpected value", 
				map);
	}
	@Test
	public void fruizione_singleAA_authzTokenClaims_errorAttr1() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
			JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		map.httpHeaders.put("GovWay-TestSuite-AAa1vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a1", map.expectedAttributes.get("a1").replace("v1-OK", "ValoreErrato"));
		_test(TipoServizio.FRUIZIONE, "tokenClaims_singleAA",
				"singleAA_authzTokenClaims_errorAttr1",
				"Token attribute 'a1' with unexpected value", 
				map);
	}
	@Test
	public void erogazione_singleAA_authzTokenClaims_errorAttr2() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
			JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		map.httpHeaders.put("GovWay-TestSuite-AAa2vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a2", map.expectedAttributes.get("a2").replace("v2-OK", "ValoreErrato"));
		_test(TipoServizio.EROGAZIONE, "tokenClaims_singleAA",
				"singleAA_authzTokenClaims_errorAttr2", 
				"Token attribute 'a2' with unexpected value", 
				map);
	}
	@Test
	public void fruizione_singleAA_authzTokenClaims_errorAttr2() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
			JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		map.httpHeaders.put("GovWay-TestSuite-AAa2vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a2", map.expectedAttributes.get("a2").replace("v2-OK", "ValoreErrato"));
		_test(TipoServizio.FRUIZIONE, "tokenClaims_singleAA",
				"singleAA_authzTokenClaims_errorAttr2",
				"Token attribute 'a2' with unexpected value", 
				map);
	}
	
	
	
	
	// *** multipleAA: autorizzazione per token claims (AA PayloadJSON, PayloadJSON-Freemarker, PayloadJSON-Template) ***
	@Test
	public void erogazione_multipleAA_authzTokenClaims() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.EROGAZIONE, "tokenClaims_multipleAA",
				"multipleAA_authzTokenClaims", null, map);
	}
	@Test
	public void fruizione_multipleAA_authzTokenClaims() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.FRUIZIONE, "tokenClaims_multipleAA",
				"multipleAA_authzTokenClaims", null, map);
	}
	@Test
	public void erogazione_multipleAA_authzTokenClaims_errorUsername() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username2", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username2\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.EROGAZIONE, "tokenClaims_multipleAA",
				"multipleAA_authzTokenClaims_errorUsername", 
				"Token attribute 'sub' with unexpected value", 
				map);
	}
	@Test
	public void fruizione_multipleAA_authzTokenClaims_errorUsername() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username2", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username2\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.FRUIZIONE, "tokenClaims_multipleAA",
				"multipleAA_authzTokenClaims_errorUsername", 
				"Token attribute 'sub' with unexpected value", 
				map);
	}
	@Test
	public void erogazione_multipleAA_authzTokenClaims_errorAudience() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://AudienceERRATO.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://AudienceERRATO.example\"");
		
		_test(TipoServizio.EROGAZIONE, "tokenClaims_multipleAA",
				"multipleAA_authzTokenClaims_errorAudience", 
				"Token attribute 'aud' with unexpected value", 
				map);
	}
	@Test
	public void fruizione_multipleAA_authzTokenClaims_errorAudience() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://AudienceERRATO.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://AudienceERRATO.example\"");
		
		_test(TipoServizio.FRUIZIONE, "tokenClaims_multipleAA",
				"multipleAA_authzTokenClaims_errorAudience", 
				"Token attribute 'aud' with unexpected value", 
				map);
	}
	
	
	
	
	// *** singleAA: autorizzazione tramite xacmlPolicy (AA PayloadJSON) ***
	@Test
	public void erogazione_singleAA_authzXacmlPolicy() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		_test(TipoServizio.EROGAZIONE, "xacmlPolicy_singleAA",
				"singleAA_authzXacmlPolicy", null, map);
	}
	@Test
	public void fruizione_singleAA_authzXacmlPolicy() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		_test(TipoServizio.FRUIZIONE, "xacmlPolicy_singleAA",
				"singleAA_authzXacmlPolicy", null, map);
	}
	@Test
	public void erogazione_singleAA_authzXacmlPolicy_errorAppFruitore() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore2";
		map.expectedAttributes.put("sub", map.expectedAttributes.get("sub").replace("ApplicativoSoggettoInternoTestFruitore1", "ApplicativoSoggettoInternoTestFruitore2"));
		_test(TipoServizio.EROGAZIONE, "xacmlPolicy_singleAA",
				"singleAA_authzXacmlPolicy_errorAppFruitore", 
				"result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok", 
				map);
	}
	@Test
	public void fruizione_singleAA_authzXacmlPolicy_errorAppFruitore() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore2";
		map.expectedAttributes.put("sub", map.expectedAttributes.get("sub").replace("ApplicativoSoggettoInternoTestFruitore1", "ApplicativoSoggettoInternoTestFruitore2"));
		_test(TipoServizio.FRUIZIONE, "xacmlPolicy_singleAA",
				"singleAA_authzXacmlPolicy_errorAppFruitore",
				"result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok", 
				map);
	}
	@Test
	public void erogazione_singleAA_authzXacmlPolicy_errorAttr1() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		map.httpHeaders.put("GovWay-TestSuite-AAa1vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a1", map.expectedAttributes.get("a1").replace("v1-OK", "ValoreErrato"));
		_test(TipoServizio.EROGAZIONE, "xacmlPolicy_singleAA",
				"singleAA_authzXacmlPolicy_errorAttr1", 
				"result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok", 
				map);
	}
	@Test
	public void fruizione_singleAA_authzXacmlPolicy_errorAttr1() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		map.httpHeaders.put("GovWay-TestSuite-AAa1vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a1", map.expectedAttributes.get("a1").replace("v1-OK", "ValoreErrato"));
		_test(TipoServizio.FRUIZIONE, "xacmlPolicy_singleAA",
				"singleAA_authzXacmlPolicy_errorAttr1",
				"result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok", 
				map);
	}
	@Test
	public void erogazione_singleAA_authzXacmlPolicy_errorAttr2() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		map.httpHeaders.put("GovWay-TestSuite-AAa2vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a2", map.expectedAttributes.get("a2").replace("v2-OK", "ValoreErrato"));
		_test(TipoServizio.EROGAZIONE, "xacmlPolicy_singleAA",
				"singleAA_authzXacmlPolicy_errorAttr2", 
				"result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok", 
				map);
	}
	@Test
	public void fruizione_singleAA_authzXacmlPolicy_errorAttr2() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		map.httpHeaders.put("GovWay-TestSuite-AAa2vDynamic", "ValoreErrato");
		map.expectedAttributes.put("a2", map.expectedAttributes.get("a2").replace("v2-OK", "ValoreErrato"));
		_test(TipoServizio.FRUIZIONE, "xacmlPolicy_singleAA",
				"singleAA_authzXacmlPolicy_errorAttr2",
				"result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok", 
				map);
	}
	
	
	// *** multipleAA: autorizzazione tramite xacmlPolicy (AA PayloadJSON, PayloadJSON-Freemarker, PayloadJSON-Template) ***
	@Test
	public void erogazione_multipleAA_authzXacmlPolicy() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.EROGAZIONE, "xacmlPolicy_multipleAA",
				"multipleAA_authzXacmlPolicy", null, map);
	}
	@Test
	public void fruizione_multipleAA_authzXacmlPolicy() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.FRUIZIONE, "xacmlPolicy_multipleAA",
				"multipleAA_authzXacmlPolicy", null, map);
	}
	@Test
	public void erogazione_multipleAA_authzXacmlPolicy_errorUsername() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username2", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username2\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.EROGAZIONE, "xacmlPolicy_multipleAA",
				"multipleAA_authzXacmlPolicy_errorUsername", 
				"result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok", 
				map);
	}
	@Test
	public void fruizione_multipleAA_authzXacmlPolicy_errorUsername() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username2", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username2\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.FRUIZIONE, "xacmlPolicy_multipleAA",
				"multipleAA_authzXacmlPolicy_errorUsername", 
				"result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok", 
				map);
	}
	@Test
	public void erogazione_multipleAA_authzXacmlPolicy_errorAudience() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://AudienceERRATO.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://AudienceERRATO.example\"");
		
		_test(TipoServizio.EROGAZIONE, "xacmlPolicy_multipleAA",
				"multipleAA_authzXacmlPolicy_errorAudience", 
				"result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok", 
				map);
	}
	@Test
	public void fruizione_multipleAA_authzXacmlPolicy_errorAudience() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://AudienceERRATO.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"PayloadJSON\",\"PayloadJSON-Freemarker\",\"PayloadJSON-Velocity\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJSON","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"template\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Freemarker","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"freemarker\"]");
		map.expectedAttributes.put("attributesAAPayloadJSON-Velocity","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"velocity\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://AudienceERRATO.example\"");
		
		_test(TipoServizio.FRUIZIONE, "xacmlPolicy_multipleAA",
				"multipleAA_authzXacmlPolicy_errorAudience", 
				"result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok", 
				map);
	}
	
	
	
	// *** singleAA: trasformazione (AA JWS Bearer) ***
	@Test
	public void erogazione_singleAA_trasformazione() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		_test(TipoServizio.EROGAZIONE, "trasformazione_singleAA",
				"singleAA_trasformazione", null, map);
	}
	@Test
	public void fruizione_singleAA_trasformazione() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		_test(TipoServizio.FRUIZIONE, "trasformazione_singleAA",
				"singleAA_trasformazione", null, map);
	}
	@Test
	public void erogazione_singleAA_trasformazione_errorAppFruitore() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore2";
		map.expectedAttributes.put("sub", map.expectedAttributes.get("sub").replace("ApplicativoSoggettoInternoTestFruitore1", "ApplicativoSoggettoInternoTestFruitore2"));
		_test(TipoServizio.EROGAZIONE, "trasformazione_singleAA",
				"singleAA_trasformazione_errorAppFruitore", 
				"errore HTTP 500", 
				map);
	}
	@Test
	public void fruizione_singleAA_trasformazione_errorAppFruitore() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore2";
		map.expectedAttributes.put("sub", map.expectedAttributes.get("sub").replace("ApplicativoSoggettoInternoTestFruitore1", "ApplicativoSoggettoInternoTestFruitore2"));
		_test(TipoServizio.FRUIZIONE, "trasformazione_singleAA",
				"singleAA_trasformazione_errorAppFruitore",
				"errore HTTP 500", 
				map);
	}
	
	
	// *** multipleAA: trasformazione (AA JWS Bearer, Payload, Header HTTP e Parametro URL) ***
	@Test
	public void erogazione_multipleAA_trasformazione() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.EROGAZIONE, "trasformazione_multipleAA",
				"multipleAA_trasformazione", null, map);
	}
	@Test
	public void fruizione_multipleAA_trasformazione() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.FRUIZIONE, "trasformazione_multipleAA",
				"multipleAA_trasformazione", null, map);
	}
	@Test
	public void erogazione_multipleAA_trasformazione_errorUsername() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username2", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username2\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.EROGAZIONE, "trasformazione_multipleAA",
				"multipleAA_trasformazione_errorUsername", 
				"errore HTTP 500", 
				map);
	}
	@Test
	public void fruizione_multipleAA_trasformazione_errorUsername() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username2", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username2\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.FRUIZIONE, "trasformazione_multipleAA",
				"multipleAA_trasformazione_errorUsername", 
				"errore HTTP 500", 
				map);
	}
	
	
	
	
	// *** singleAA: connettoriMultipli (AA JWS Bearer) ***
	@Test
	public void erogazione_singleAA_connettoriMultipli() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		_test(TipoServizio.EROGAZIONE, "connettoriMultipli_singleAA",
				"singleAA_connettoriMultipli", null, map);
	}
	@Test
	public void erogazione_singleAA_connettoriMultipli_errorAppFruitore() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore2";
		map.expectedAttributes.put("sub", map.expectedAttributes.get("sub").replace("ApplicativoSoggettoInternoTestFruitore1", "ApplicativoSoggettoInternoTestFruitore2"));
		_test(TipoServizio.EROGAZIONE, "connettoriMultipli_singleAA",
				"singleAA_connettoriMultipli_errorAppFruitore", 
				"Il valore estratto dalla richiesta 'ApplicativoSoggettoInternoTestFruitore2', ottenuto tramite identificazione 'Template' (${aa:sub}), non corrisponde al nome di nessun connettore", 
				map);
	}
	
	
	// *** multipleAA: connettoriMultipli (AA JWS Bearer, Payload, Header HTTP e Parametro URL) ***
	@Test
	public void erogazione_multipleAA_connettoriMultipli() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username1", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username1\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.EROGAZIONE, "connettoriMultipli_multipleAA",
				"multipleAA_connettoriMultipli", null, map);
	}
	@Test
	public void erogazione_multipleAA_connettoriMultipli_errorUsername() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.httpHeaders.put("GovWay-TestSuite-Principal","ApplicativoSoggettoInternoTestFruitore1");
		map.httpHeaders.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				JWTUtilities.builtJWT_OIDC(null, "http://Issuer.example", "Username2", null, "http://Audience.example"));
		
		map.expectedAttributes.put("AA","\"attributeAuthorities\":[\"AuthorizationBearer\",\"HeaderHTTP\",\"ParametroURL\",\"PayloadJWS\"]");
		map.expectedAttributes.remove("attributes");
		map.expectedAttributes.put("attributesAAPayloadJWS","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"pJWS\"]");
		map.expectedAttributes.put("attributesAAAuthorizationBearer","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"AB\"]");
		map.expectedAttributes.put("attributesAAHeaderHTTP","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"httpHDR\"]");
		map.expectedAttributes.put("attributesAAParametroURL","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\",\"queryParam\"]");
		map.expectedAttributes.put("sub","\"sub\":\"Username2\"");
		map.expectedAttributes.put("iss","\"iss\":\"http://Issuer.example\"");
		map.expectedAttributes.put("aud","\"aud\":\"http://Audience.example\"");
		
		_test(TipoServizio.EROGAZIONE, "connettoriMultipli_multipleAA",
				"multipleAA_connettoriMultipli_errorUsername", 
				"Il valore estratto dalla richiesta 'Username2', ottenuto tramite identificazione 'Template' (${aa:attributes[HeaderHTTP][sub]}), non corrisponde al nome di nessun connettore", 
				map);
	}

	

	
	private static HttpResponse _test(
			TipoServizio tipoServizio,String operazione,
			String tipoTest, String msgErrore,
			AAHeaderMap map) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestAttributeAuthority/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestAttributeAuthority/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url);
		if(map.applicativo!=null) {
			request.setUsername(map.applicativo);
			request.setPassword("123456");
		}
		
		if(map!=null && map.httpHeaders!=null && !map.httpHeaders.isEmpty()) {
			for (String key : map.httpHeaders.keySet()) {
				String value = map.httpHeaders.get(key);
				request.addHeader(key, value);
			}
		}
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(msgErrore!=null) {
			int code = -1;
			String error = null;
			String msg = null;
			if(tipoTest.contains("authzContenuti")) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				error = AUTHORIZATION_CONTENT_DENY;
				msg = AUTHORIZATION_CONTENT_DENY_MESSAGE;
			}
			else if(tipoTest.contains("authzTokenClaims")) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				error = AUTHORIZATION_TOKEN_DENY;
				msg = AUTHORIZATION_TOKEN_DENY_MESSAGE;
			}
			else if(tipoTest.contains("authzXacmlPolicy")) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				error = AUTHORIZATION_POLICY_DENY;
				msg = AUTHORIZATION_POLICY_DENY_MESSAGE;
			}
			else if(tipoTest.contains("trasformazione")) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.HTTP_5xx);
				code = 500;
			}
			else if(tipoTest.contains("connettoriMultipli")) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_4XX);
				code = 400;
				error = BAD_REQUEST;
				msg = BAD_REQUEST_MESSAGE;
			}
			verifyKo(response, error, code, msg);
		}
		else {
			verifyOk(response, 200);
		}
		
		DBVerifier.verify(idTransazione, esitoExpected, msgErrore, map.toArray_expectedAttributes());
		
		return response;
		
	}
	
	public static final String AUTHORIZATION_CONTENT_DENY = "AuthorizationContentDeny";
	public static final String AUTHORIZATION_CONTENT_DENY_MESSAGE = "Unauthorized request content";
	
	public static final String AUTHORIZATION_TOKEN_DENY = "AuthorizationTokenDeny";
	public static final String AUTHORIZATION_TOKEN_DENY_MESSAGE = "Insufficient token claims";
	
	public static final String AUTHORIZATION_POLICY_DENY = "AuthorizationPolicyDeny";
	public static final String AUTHORIZATION_POLICY_DENY_MESSAGE = "Authorization deny by policy";
	
	public static final String BAD_REQUEST = "BadRequest";
	public static final String BAD_REQUEST_MESSAGE = "Bad request";
	
	public static void verifyKo(HttpResponse response, String error, int code, String errorMsg) {
		
		assertEquals(code, response.getResultHTTPOperation());
		
		if(error!=null) {
			assertEquals(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, response.getContentType());
			
			try {
				JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
				JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(response.getContent()));
				
				assertEquals("https://govway.org/handling-errors/"+code+"/"+error+".html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
				assertEquals(error, jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
				assertEquals(code, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
				assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
				assertEquals(true, jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0).equals(errorMsg));
				
				assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
	
				if(code==504) {
					assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static void verifyOk(HttpResponse response, int code) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_JSON, response.getContentType());
		
	}
}

class AAHeaderMap{
	
	Map<String, String> httpHeaders = new HashMap<String, String>();
	String applicativo = null;
	
	Map<String, String> expectedAttributes = new HashMap<String, String>();
	
	public AAHeaderMap() {
		
		this.expectedAttributes.put("a1","\"a1\":[\"av11\",\"av12\",\"av13\",\"v1-OK\"]");
		this.expectedAttributes.put("a2","\"a2\":\"v2-OK\"");
		this.expectedAttributes.put("a31","\"a31\":\"av31\"");
		this.expectedAttributes.put("a32","\"a32\":\"v3-OK\"");
		this.expectedAttributes.put("a41","\"a41\":\"av41\"");
		this.expectedAttributes.put("a42","\"a42\":\"v4-OK\"");
		this.expectedAttributes.put("attributes","\"attributes\":[\"a1\",\"a2\",\"a3\",\"a4\"]");
		this.expectedAttributes.put("sub","\"sub\":\"ApplicativoSoggettoInternoTestFruitore1\"");
		this.expectedAttributes.put("iss","\"iss\":\"IssuerHDR\"");
		this.expectedAttributes.put("aud","\"aud\":\"AudienceHDR\"");
		
		this.httpHeaders.put("GovWay-TestSuite-AAOperation", "echo");
		this.httpHeaders.put("GovWay-TestSuite-AAIssuer", "IssuerHDR");
		this.httpHeaders.put("GovWay-TestSuite-AAAudience", "AudienceHDR");
		this.httpHeaders.put("GovWay-TestSuite-AAa1vDynamic", "v1-OK");
		this.httpHeaders.put("GovWay-TestSuite-AAa2vDynamic", "v2-OK");
		this.httpHeaders.put("GovWay-TestSuite-AAa3vDynamic", "v3-OK");
		this.httpHeaders.put("GovWay-TestSuite-AAa4vDynamic", "v4-OK");
	}
	
	public String[] toArray_expectedAttributes() {
		if(this.expectedAttributes==null || this.expectedAttributes.isEmpty()) {
			return null;
		}
		return this.expectedAttributes.values().toArray(new String[1]);
	}
}
