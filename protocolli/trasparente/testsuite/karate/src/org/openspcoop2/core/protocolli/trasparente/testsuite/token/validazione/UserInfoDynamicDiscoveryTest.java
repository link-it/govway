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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* UserInfoDynamicDiscoveryTest
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class UserInfoDynamicDiscoveryTest extends ConfigLoader {

	public static final String validazione = "TestValidazioneToken-UserInfoDynamicDiscovery";
	
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
		
		File f = File.createTempFile("userInfoResponse", ".json");
		
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
			StringBuilder jsonBody = new StringBuilder();
			String token = ValidazioneJWTKeystoreDinamicoTest.buildJWT(mapExpectedTokenInfo, jsonBody);
			mapExpectedTokenInfo.remove("\"sourceType\":\"JWT\"");
			mapExpectedTokenInfo.add("\"sourceType\":\"USER_INFO\"");
			String tokenPayload = jsonBody.toString();
						
			FileSystemUtilities.writeFile(f, tokenPayload.getBytes());
			FileSystemUtilities.writeFile(fDD, dd.getBytes());
			
			Map<String, String> headers = new HashMap<>();
			headers.put(DYNAMIC_HEADER_LOCATION, fDD.getAbsolutePath());
			headers.put(DYNAMIC_HEADER_SSL_LOCATION, "/etc/govway/keys/erogatore.jks");
			headers.put(DYNAMIC_HEADER_SSL_PASSWORD, "openspcoop");
			headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
					token);
			
			String tokenPolicy = "TestUserInfoDynamicDiscovery";
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
			
			String prefixHttp = "https://127.0.0.1:8445";
			if("custom".equals(operazione)) {
				prefixHttp = "https://Erogatore:8445";
			}
			List<String> invalid = Utilities.getMapExpectedTokenInfoInvalid(token);
			invalid.addAll(mapExpectedTokenInfoDD);
			String warningMessage = "warningOnly".equals(operazione) ? " (configurazione in modalità 'WarningOnly')" : "";
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					"Validazione del token, tramite il servizio di UserInfo ("+prefixHttp+"/TestService/echo?existsQueryParameters=test_token&destFile="+f.getAbsolutePath()+"&destFileContentType=application/json), fallita"+warningMessage+": Risposta del servizio di UserInfo non valida: Connessione terminata con errore (codice trasporto: 500)",
					null, 
					invalid),
					tokenPolicy,
					false, null);
			
			// deve nuovamente funzionare senza bisogno del reset della cache
			
			FileSystemUtilities.writeFile(f, tokenPayload.getBytes());
			
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
			invalid.addAll(ValidazioneJWTDynamicDiscoveryTest.getInvalidDD());
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					"Recupero informazioni tramite 'dynamic discovery' ("+prefixHttp+"/TestService/echo?destFile=${header:govway-testsuite-dynamic-location}) fallito: Risposta del servizio 'Dynamic Discovery' non valida: Connessione terminata con errore (codice trasporto: 500):",
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
	
	private static void check(HttpResponse response, String tokenPolicy, 
			boolean cache, String token) {
		
		ValidazioneJWTDynamicDiscoveryTest.check(response, tokenPolicy, 
				cache, token);
	}
	
	public static final String TEST = "http://test";
	
	private static String buildDD(File f, String prefix, List<String> mapExpectedTokenInfo) {
		
		String hostname = StringUtils.isNotEmpty(prefix) ? "Erogatore" : "127.0.0.1";
		String userInfoUri = "https://"+hostname+":8445/TestService/echo?existsQueryParameters=test_token&destFile="+f.getAbsolutePath()+"&destFileContentType=application/json";
		return ValidazioneJWTDynamicDiscoveryTest.buildDD(prefix, mapExpectedTokenInfo,
				TEST+"/jwkUri", TEST+"/intro", userInfoUri, TEST+"/altro");
		
	}
	
	
	
}