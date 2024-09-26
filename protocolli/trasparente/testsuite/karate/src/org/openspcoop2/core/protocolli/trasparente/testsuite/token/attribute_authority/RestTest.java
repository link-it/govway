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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.attribute_authority;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione.ValidazioneJWTTest;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.resources.FileSystemUtilities;
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

	private static final String API = "TestAttributeAuthority";
	private static final String API_VALIDITY_CHECK = "TestAttributeAuthority-ValidityCheck";
	
	private static final String EXAMPLE_CLIENT_1 = "ExampleClient1";
	private static final String EXAMPLE_CLIENT_SCADUTO = "ExampleClientScaduto";
	
	private static final String ERRORE_CERTIFICATO_SCADUTO = "Certificato presente nell'header 'x5c' scaduto: certificate expired";
	
	private static final String TIPO_TEST_VALIDITY_CHECK = "validityCheck"; 
	
	
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
	
	
	
	
	// *** singleAA: autorizzazione per contenuti (AA JWS Bearer con validazione tramite jwks 'kid') con url dinamica ***
	
	public static final String DYNAMIC_HEADER_LOCATION = "govway-testsuite-keystore-location";
	
	@Test
	public void erogazione_singleAA_authzContenuti_jwksKID_dynamic() throws Exception {
		
		File f = File.createTempFile("dynamicKeystore", ".jwks");
		try {
			byte [] content = FileSystemUtilities.readBytesFromFile("/etc/govway/keys/jose_truststore_example.jwks");
			FileSystemUtilities.writeFile(f, content);
			
			AAHeaderMap map = new AAHeaderMap();
			map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
			
			map.httpHeaders.put(DYNAMIC_HEADER_LOCATION, f.getAbsolutePath());
			
			_test(TipoServizio.EROGAZIONE, "contenuti_singleAA_jwksKID_dynamic",
					"singleAA_authzContenuti_jwksKID", null, map);
			
		}finally {
			FileSystemUtilities.deleteFile(f);
		}
	}
	
	
	
	// *** singleAA: autorizzazione per contenuti (AA JWS Bearer con validazione tramite keyPair protected) ***
	@Test
	public void erogazione_singleAA_authzContenuti_keyPairProtected() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		_test(TipoServizio.EROGAZIONE, "contenuti_singleAA_keyPairProtected",
				"singleAA_authzContenuti_keyPairProtected", null, map);
	}
	
	
	
	// *** singleAA: autorizzazione per contenuti (AA JWS Bearer con validazione tramite keyPair unprotected) ***
	@Test
	public void erogazione_singleAA_authzContenuti_keyPairUnprotected() throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		_test(TipoServizio.EROGAZIONE, "contenuti_singleAA_keyPairUnprotected",
				"singleAA_authzContenuti_keyPairUnprotected", null, map);
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

	

	
	
	
	
	
	
	
	
	
	
	

	
	
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiDefault() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiDefault(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiDefaultFruizione() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiDefault(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiDefault(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCertificati.validityCheck.default", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_1, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCertificati.validityCheck.default",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCADefault() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCADefault(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCADefaultFruizione() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCADefault(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertNonScadutoTrustStoreCADefault(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCA.validityCheck.default", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_1, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCA.validityCheck.default",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCertificatiDefault() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCertificatiDefault(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCertificatiDefaultFruizione() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCertificatiDefault(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertScadutoTrustStoreCertificatiDefault(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCertificati.validityCheck.default", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_SCADUTO, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCertificati.validityCheck.default",
					TIPO_TEST_VALIDITY_CHECK, 
					ERRORE_CERTIFICATO_SCADUTO, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCADefault() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCADefault(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCADefaultFruizione() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCADefault(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertScadutoTrustStoreCADefault(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCA.validityCheck.default", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_SCADUTO, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCA.validityCheck.default",
					TIPO_TEST_VALIDITY_CHECK, 
					ERRORE_CERTIFICATO_SCADUTO, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	
	
	
	
	
	
	
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiTrue() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiTrue(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiTrueFruizione() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiTrue(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiTrue(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCertificati.validityCheck.true", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_1, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCertificati.validityCheck.true",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCATrue() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCATrue(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCATrueFruizione() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCATrue(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertNonScadutoTrustStoreCATrue(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCA.validityCheck.true", ".jwt");
		try {
			
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_1, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCA.validityCheck.true",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCertificatiTrue() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCertificatiTrue(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCertificatiTrueFruizione() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCertificatiTrue(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertScadutoTrustStoreCertificatiTrue(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCertificati.validityCheck.true", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_SCADUTO, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCertificati.validityCheck.true",
					TIPO_TEST_VALIDITY_CHECK, 
					ERRORE_CERTIFICATO_SCADUTO, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCATrue() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCATrue(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCATrueFruizione() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCATrue(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertScadutoTrustStoreCATrue(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCA.validityCheck.true", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_SCADUTO, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCA.validityCheck.true",
					TIPO_TEST_VALIDITY_CHECK, 
					ERRORE_CERTIFICATO_SCADUTO, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiFalse() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiFalse(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiFalseFruizione() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiFalse(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiFalse(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCertificati.validityCheck.false", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_1, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCertificati.validityCheck.false",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCAFalse() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCAFalse(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCAFalseFruizione() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCAFalse(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertNonScadutoTrustStoreCAFalse(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCA.validityCheck.false", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_1, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCA.validityCheck.false",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCertificatiFalse() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCertificatiFalse(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCertificatiFalseFruizione() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCertificatiFalse(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertScadutoTrustStoreCertificatiFalse(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCertificati.validityCheck.false", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_SCADUTO, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCertificati.validityCheck.false",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCAFalse() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCAFalse(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCAFalseFruizione() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCAFalse(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertScadutoTrustStoreCAFalse(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCA.validityCheck.false", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_SCADUTO, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCA.validityCheck.false",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiIfNotInTruststore() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiIfNotInTruststore(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiIfNotInTruststoreFruizione() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiIfNotInTruststore(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertNonScadutoTrustStoreCertificatiIfNotInTruststore(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCertificati.validityCheck.ifNotInTruststore", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_1, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCertificati.validityCheck.ifNotInTruststore",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCAIfNotInTruststore() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCAIfNotInTruststore(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertNonScadutoTrustStoreCAIfNotInTruststoreFruizione() throws Exception {
		useX5CvalidityCheckCertNonScadutoTrustStoreCAIfNotInTruststore(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertNonScadutoTrustStoreCAIfNotInTruststore(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCA.validityCheck.ifNotInTruststore", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_1, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCA.validityCheck.ifNotInTruststore",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCertificatiIfNotInTruststore() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCertificatiIfNotInTruststore(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCertificatiIfNotInTruststoreFruizione() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCertificatiIfNotInTruststore(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertScadutoTrustStoreCertificatiIfNotInTruststore(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCertificati.validityCheck.ifNotInTruststore", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_SCADUTO, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCertificati.validityCheck.ifNotInTruststore",
					TIPO_TEST_VALIDITY_CHECK, 
					null, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCAIfNotInTruststore() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCAIfNotInTruststore(TipoServizio.EROGAZIONE);
	}
	@Test
	public void useX5CvalidityCheckCertScadutoTrustStoreCAIfNotInTruststoreFruizione() throws Exception {
		useX5CvalidityCheckCertScadutoTrustStoreCAIfNotInTruststore(TipoServizio.FRUIZIONE);
	}
	private void useX5CvalidityCheckCertScadutoTrustStoreCAIfNotInTruststore(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File fTmp = FileSystemUtilities.createTempFile("token.trustCA.validityCheck.ifNotInTruststore", ".jwt");
		try {
		
			AAHeaderMap map = new AAHeaderMap();
			map.httpHeaders.put("tokenjwtresponse",fTmp.getAbsolutePath());
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			String token = ValidazioneJWTTest.buildJWTwithX5C(EXAMPLE_CLIENT_SCADUTO, 
					mapExpectedTokenInfo);
			FileSystemUtilities.writeFile(fTmp, token.getBytes());
			
			_test(tipoServizio, "trustCA.validityCheck.ifNotInTruststore",
					TIPO_TEST_VALIDITY_CHECK, 
					ERRORE_CERTIFICATO_SCADUTO, 
					map);
			
		}finally {
			FileSystemUtilities.deleteFile(fTmp);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio,String operazione,
			String tipoTest, String msgErrore,
			AAHeaderMap map) throws Exception {
		
		String api = TIPO_TEST_VALIDITY_CHECK.equals(tipoTest) ? API_VALIDITY_CHECK : API;
		
		return _test(
				tipoServizio, api, operazione,
				tipoTest, msgErrore,
				 map);
	}
	private static HttpResponse _test(
			TipoServizio tipoServizio,String api, String operazione,
			String tipoTest, String msgErrore,
			AAHeaderMap map) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
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
			if(tipoTest.contains("authzContenuti") || TIPO_TEST_VALIDITY_CHECK.equals(tipoTest)) {
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
		
		if(TIPO_TEST_VALIDITY_CHECK.equals(tipoTest)) {
			DBVerifier.verify(idTransazione, esitoExpected, msgErrore, "skipCheckTokenInfo");
		}
		else {
			DBVerifier.verify(idTransazione, esitoExpected, msgErrore, map.toArray_expectedAttributes());
		}
		
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
	
	Map<String, String> httpHeaders = new HashMap<>();
	String applicativo = null;
	
	Map<String, String> expectedAttributes = new HashMap<>();
	
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
