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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.JWKPublicKeyConverter;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* ValidazioneJWTDynamicDiscoveryTest
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ValidazioneJWTDynamicDiscoveryTest extends ConfigLoader {

	public static final String validazione = "TestValidazioneTokenDynamicDiscovery";
	
	public static final String DYNAMIC_HEADER_LOCATION = "govway-testsuite-dynamic-location";
	public static final String DYNAMIC_HEADER_SSL_LOCATION = "govway-testsuite-ssl-location";
	public static final String DYNAMIC_HEADER_SSL_PASSWORD = "govway-testsuite-ssl-password";
		
	@Test
	public void oidc() throws Exception {
		test(TipoServizio.EROGAZIONE,"oidc");
	}
	@Test
	public void oidcFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE,"oidc");
	}
	
	@Test
	public void custom() throws Exception {
		test(TipoServizio.EROGAZIONE,"custom");
	}
	@Test
	public void customFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE,"custom");
	}
	
	@Test
	public void warningOnly() throws Exception {
		test(TipoServizio.EROGAZIONE,"warningOnly");
	}
	@Test
	public void warningOnlyFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE,"warningOnly");
	}
	
	
	private void test(TipoServizio tipoServizio, String operazione) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheConfigurazione(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheDatiRichieste(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheKeystore(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File f = File.createTempFile("dynamicKeystore", ".jwk");
		byte [] keystore = null;
		keystore = FileSystemUtilities.readBytesFromFile("/etc/govway/keys/erogatore.jks");
		PublicKey publicKey = ArchiveLoader.loadFromKeystoreJKS(keystore, "erogatore", "openspcoop").getCertificate().getCertificate().getPublicKey();
		keystore = JWKPublicKeyConverter.convert(publicKey, "erogatore", true, false).getBytes();
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		
		String prefix = "";
		if("custom".equals(operazione)) {
			prefix = "test";
		}		
		String dd = buildDD(f, prefix, mapExpectedTokenInfo);
		List<String> mapExpectedTokenInfoDD = new ArrayList<>();
		mapExpectedTokenInfoDD.addAll(mapExpectedTokenInfo);
		File fDD = File.createTempFile("dynamic", ".json");

		try {
			FileSystemUtilities.writeFile(f, keystore);
			FileSystemUtilities.writeFile(fDD, dd.getBytes());
			
			Map<String, String> headers = new HashMap<>();
			headers.put(DYNAMIC_HEADER_LOCATION, fDD.getAbsolutePath());
			headers.put(DYNAMIC_HEADER_SSL_LOCATION, "/etc/govway/keys/erogatore.jks");
			headers.put(DYNAMIC_HEADER_SSL_PASSWORD, "openspcoop");
			String token = ValidazioneJWTKeystoreDinamicoTest.buildJWT(mapExpectedTokenInfo);
			headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
					token);
			
			String tokenPolicy = "TestValidazioneJWTDynamicDiscovery";
			if("custom".equals(operazione)) {
				tokenPolicy = tokenPolicy + "Custom";
			}
			
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					null,
					Utilities.credenzialiMittente, mapExpectedTokenInfo),
					tokenPolicy,
					false, dd);
			
			// elimino il keystore indicato nel json del d.d.
			
			FileSystemUtilities.deleteFile(f);
			
			// deve funzionare comunque
			
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					null,
					Utilities.credenzialiMittente, mapExpectedTokenInfo),
					tokenPolicy,
					true, dd);
			
			// deve dare errore
			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheDatiRichieste(logCore);
			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheKeystore(logCore);
			
			String prefixHttp = "https://127.0.0.1:8445";
			
			List<String> invalid = Utilities.getMapExpectedTokenInfoInvalid(token);
			invalid.addAll(mapExpectedTokenInfoDD);
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					"Retrieve store '"+prefixHttp+"/TestService/echo?destFile="+f.getAbsolutePath()+"' failed (returnCode:500)",
					null, 
					invalid),
					tokenPolicy,
					false, null);
			
			// deve nuovamente funzionare senza bisogno del reset della cache
			
			FileSystemUtilities.writeFile(f, keystore);
			
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					null,
					Utilities.credenzialiMittente, mapExpectedTokenInfo),
					tokenPolicy,
					true, dd);
			
			// elimino il d.d.
			
			FileSystemUtilities.deleteFile(fDD);
			
			// deve funzionare comunque
						
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					null,
					Utilities.credenzialiMittente, mapExpectedTokenInfo),
					tokenPolicy,
					true, dd);
			
			// deve dare errore
			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheConfigurazione(logCore);
			
			invalid = Utilities.getMapExpectedTokenInfoInvalid(token);
			invalid.addAll(getInvalidDD());
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					"Recupero informazioni tramite 'dynamic discovery' (https://127.0.0.1:8445/TestService/echo?destFile=${header:govway-testsuite-dynamic-location}) fallito: Risposta del servizio 'Dynamic Discovery' non valida: Connessione terminata con errore (codice trasporto: 500):",
					null, 
					invalid),
					tokenPolicy,
					false, null);
			
			// deve nuovamente funzionare senza bisogno del reset della cache
			
			FileSystemUtilities.writeFile(fDD, dd.getBytes());
			
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					null,
					Utilities.credenzialiMittente, mapExpectedTokenInfo),
					tokenPolicy,
					false, dd);
			
		}finally {
			FileSystemUtilities.deleteFile(f);
			FileSystemUtilities.deleteFile(fDD);
		}
	}
	
	public static void check(HttpResponse response, String tokenPolicy, 
			boolean cache, String token) {
		String diagnostico = "Gestione Token ["+tokenPolicy+"] ";
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		DBVerifier.checkDiagnostic(idTransazione, diagnostico);
		
		String diagnostico2 = "Recupero informazioni tramite 'dynamic discovery' ";
		DBVerifier.checkDiagnostic(idTransazione, diagnostico2);
		
		if(token!=null) {
			String diagnostico3 = "Individuate le seguenti informazioni"+
					(cache ? ", presenti in cache" : "")+
					": "+token; 
			DBVerifier.checkDiagnostic(idTransazione, diagnostico3);
		}
	}
	
	public static final String ALTRO = "altro";
	public static final String TEST = "http://test";
	
	private static String buildDD(File f, String prefix, List<String> mapExpectedTokenInfo) {
		
		String jwkUri = "https://127.0.0.1:8445/TestService/echo?destFile="+f.getAbsolutePath();
		return buildDD(prefix, mapExpectedTokenInfo,
				jwkUri, TEST+"/intro", TEST+"/user", TEST+"/altro");
		
	}
	public static String buildDD(String prefix, List<String> mapExpectedTokenInfo,
			String jwkUri, String introspectionUri, String userInfoUri, String altro) {
		
		mapExpectedTokenInfo.add("\"dynamicDiscovery\":");
		
		mapExpectedTokenInfo.add("\""+prefix+""+ALTRO+"\":\""+altro+"\"");
		
		if(StringUtils.isNotEmpty(introspectionUri) && !introspectionUri.startsWith(TEST)) {
			mapExpectedTokenInfo.add("\"introspectionEndpoint\":\""+introspectionUri+"\"");
		}
		mapExpectedTokenInfo.add("\""+prefix+"introspection_endpoint\":\""+introspectionUri+"\"");
		
		if(StringUtils.isNotEmpty(userInfoUri) && !userInfoUri.startsWith(TEST)) {
			mapExpectedTokenInfo.add("\"userinfoEndpoint\":\""+userInfoUri+"\"");
		}
		mapExpectedTokenInfo.add("\""+prefix+"userinfo_endpoint\":\""+userInfoUri+"\"");
		
		if(StringUtils.isNotEmpty(jwkUri) && !jwkUri.startsWith(TEST)) {
			mapExpectedTokenInfo.add("\"jwksUri\":\""+jwkUri+"\"");
		}
		mapExpectedTokenInfo.add("\""+prefix+"jwks_uri\":\""+jwkUri+"\"");
		
		return "{"+
				"\""+prefix+""+ALTRO+"\":\""+altro+"\""+
				","+
				"\""+prefix+"introspection_endpoint\":\""+introspectionUri+"\""+
				","+
				"\""+prefix+"userinfo_endpoint\":\""+userInfoUri+"\""+
				","+
				"\""+prefix+"jwks_uri\":\""+jwkUri+"\""+
				"}";
		
	}
	
	
	public static List<String> getInvalidDD() {
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		mapExpectedTokenInfo.add("\"dynamicDiscovery\":");
		mapExpectedTokenInfo.add("\"valid\":false");
		mapExpectedTokenInfo.add("\"httpResponseCode\":\"500\"");
		mapExpectedTokenInfo.add("\"errorDetails\":\"Risposta del servizio 'Dynamic Discovery' non valida: Connessione terminata con errore (codice trasporto: 500)");
		return mapExpectedTokenInfo;
	}
}