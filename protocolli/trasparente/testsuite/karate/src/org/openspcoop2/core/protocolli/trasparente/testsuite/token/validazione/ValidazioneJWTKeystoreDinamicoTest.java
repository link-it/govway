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
import java.util.Properties;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.JWKPublicKeyConverter;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* ValidazioneTest
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ValidazioneJWTKeystoreDinamicoTest extends ConfigLoader {

	public static final String validazione = "TestValidazioneTokenKeystore";
	
	public static final String DYNAMIC_HEADER_LOCATION = "govway-testsuite-keystore-location";
	public static final String DYNAMIC_HEADER_ALIAS ="govway-testsuite-keystore-alias";
	public static final String DYNAMIC_HEADER_SSL_LOCATION = "govway-testsuite-ssl-location";
	public static final String DYNAMIC_HEADER_SSL_PASSWORD = "govway-testsuite-ssl-password";
		
	@Test
	public void jksFile() throws Exception {
		test(TipoServizio.EROGAZIONE,"jks-file","jks");
	}
	@Test
	public void jksFileFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE,"jks-file","jks");
	}
	
	@Test
	public void jksUrl() throws Exception {
		test(TipoServizio.EROGAZIONE,"jks-url","jks");
	}
	@Test
	public void jksUrlFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE,"jks-url","jks");
	}
	
	
	@Test
	public void jwkFile() throws Exception {
		test(TipoServizio.EROGAZIONE,"jwk-file","jwk");
	}
	@Test
	public void jwkFileFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE,"jwk-file","jwk");
	}
	
	@Test
	public void jwkUrl() throws Exception {
		test(TipoServizio.EROGAZIONE,"jwk-url","jwk");
	}
	@Test
	public void jwkUrlFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE,"jwk-url","jwk");
	}
	
	
	@Test
	public void publicFile() throws Exception {
		test(TipoServizio.EROGAZIONE,"public-file","public");
	}
	@Test
	public void publicFileFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE,"public-file","public");
	}
	
	@Test
	public void publicUrl() throws Exception {
		test(TipoServizio.EROGAZIONE,"public-url","public");
	}
	@Test
	public void publicUrlFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE,"public-url","public");
	}
	
	
	private void test(TipoServizio tipoServizio, String operazione, String tipoKeystore) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheDatiRichieste(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheKeystore(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		File f = File.createTempFile("dynamicKeystore", "."+tipoKeystore);
		byte [] keystore = null;
		if("jks".equals(tipoKeystore)) {
			keystore = FileSystemUtilities.readBytesFromFile("/etc/govway/keys/erogatore.jks");
		}
		else if("jwk".equals(tipoKeystore)) {
			keystore = FileSystemUtilities.readBytesFromFile("/etc/govway/keys/erogatore.jks");
			PublicKey publicKey = ArchiveLoader.loadFromKeystoreJKS(keystore, "erogatore", "openspcoop").getCertificate().getCertificate().getPublicKey();
			keystore = JWKPublicKeyConverter.convert(publicKey, "erogatore", true, false).getBytes();
		}
		else if("public".equals(tipoKeystore)) {
			keystore = FileSystemUtilities.readBytesFromFile("/etc/govway/keys/erogatore.jks");
			PublicKey publicKey = ArchiveLoader.loadFromKeystoreJKS(keystore, "erogatore", "openspcoop").getCertificate().getCertificate().getPublicKey();
			keystore = publicKey.getEncoded();
		}
		try {
			FileSystemUtilities.writeFile(f, keystore);
			
			List<String> mapExpectedTokenInfo = new ArrayList<>();
			Map<String, String> headers = new HashMap<>();
			headers.put(DYNAMIC_HEADER_LOCATION, f.getAbsolutePath());
			if("jwk".equals(tipoKeystore)) {
				headers.put(DYNAMIC_HEADER_ALIAS, "erogatore");
				headers.put(DYNAMIC_HEADER_SSL_LOCATION, "/etc/govway/keys/erogatore.jks");
				headers.put(DYNAMIC_HEADER_SSL_PASSWORD, "openspcoop");
			}
			String token = buildJWT(mapExpectedTokenInfo);
			headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
					token);
			
			String tokenPolicy = "TestValidazioneJWTKeystore"+tipoKeystore.toUpperCase()+
					(operazione.endsWith("-url") ? "UrlDinamico" : "FileDinamico");
			if(operazione.startsWith("public")){
				tokenPolicy = tokenPolicy.replace("PUBLIC", "Public");
			}
			
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					null,
					Utilities.credenzialiMittente, mapExpectedTokenInfo),
					tokenPolicy);
			
			FileSystemUtilities.deleteFile(f);
			
			// deve funzionare comunque
			
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					null,
					Utilities.credenzialiMittente, mapExpectedTokenInfo),
					tokenPolicy);
			
			// deve dare errore
			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheDatiRichieste(logCore);
			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheKeystore(logCore);
			
			String prefixHttp = "https://127.0.0.1:8445";
			if("public".equals(tipoKeystore)) {
				prefixHttp = "http://127.0.0.1:8080";
			}
			if("jwk".equals(tipoKeystore)) {
				prefixHttp = "https://Erogatore:8445";
			}
			
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					operazione.endsWith("-url") ? 
							"Retrieve store '"+prefixHttp+"/TestService/echo?destFile="+f.getAbsolutePath()+"' failed (returnCode:500)"
							:
							"Store ["+f.getAbsolutePath()+"] not found",
					null, 
					Utilities.getMapExpectedTokenInfoInvalid(token)),
					tokenPolicy);
			
			// deve nuovamente funzionare senza bisogno del reset della cache
			
			FileSystemUtilities.writeFile(f, keystore);
			
			check(Utilities._test(logCore, tipoServizio, validazione, operazione, headers,  null,
					null,
					Utilities.credenzialiMittente, mapExpectedTokenInfo),
					tokenPolicy);
			
		}finally {
			FileSystemUtilities.deleteFile(f);
		}
	}
	
	private static void check(HttpResponse response, String tokenPolicy) {
		String diagnostico = "Gestione Token ["+tokenPolicy+"] ";
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		DBVerifier.checkDiagnostic(idTransazione, diagnostico);
	}
	
	
	
	public static String buildJWT(List<String> mapExpectedTokenInfo) throws Exception {
		return buildJWT(mapExpectedTokenInfo, null);
	}
	static String buildJWT(List<String> mapExpectedTokenInfo, StringBuilder jsonBody) throws Exception {
		
		boolean requiredClaims = true;
		boolean requiredClaimsClientId = true;
		boolean requiredClaimsIssuer = true;
		boolean requiredClaimsSubject = true;
		boolean requiredClaimsUsername = true;
		boolean requiredClaimsEMail = true;
		boolean scope1 = true;
		boolean scope2 = true;
		boolean scope3 = true;
		boolean role1 = true;
		boolean role2 = true;
		boolean role3 = true;
		boolean invalidIat = false; 
		boolean futureIat = false;
		boolean invalidNbf = false; 
		boolean invalidExp = false;
		boolean invalidClientId = false;
		boolean singleValueNoArrayAudience = false;
		boolean invalidAudience = false;
		boolean invalidUsername = false;
		boolean invalidClaimCheNonDeveEsistere = false;
		String prefix = "";
		
		String jsonInput = Utilities.buildJson(requiredClaims, 
				requiredClaimsClientId, requiredClaimsIssuer, requiredClaimsSubject, 
				requiredClaimsUsername, requiredClaimsEMail, 
				scope1, scope2, scope3, 
				role1, role2, role3, 
				invalidIat, futureIat, invalidNbf, invalidExp, 
				invalidClientId, 
				singleValueNoArrayAudience, invalidAudience, 
				invalidUsername, invalidClaimCheNonDeveEsistere, 
				mapExpectedTokenInfo,
				prefix); 
		if(jsonBody!=null) {
			jsonBody.append(jsonInput);
		}
		
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add("\"sourceType\":\"JWT\"");
		}
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		Properties props = new Properties();
		props.put("rs.security.keystore.type","JKS");
		String password = "openspcoop";
		props.put("rs.security.keystore.file", "/etc/govway/keys/erogatore.jks");
		props.put("rs.security.keystore.alias","erogatore");
		props.put("rs.security.keystore.password",password);
		props.put("rs.security.key.password",password);
				
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
			
		props.put("rs.security.signature.algorithm","RS256");
		props.put("rs.security.signature.include.cert","false");
		props.put("rs.security.signature.include.key.id","true");
		props.put("rs.security.signature.include.public.key","false");
		props.put("rs.security.signature.include.cert.sha1","false");
		props.put("rs.security.signature.include.cert.sha256","false");
			
		JsonSignature jsonSignature = new JsonSignature(props, options);
		String token = jsonSignature.sign(jsonInput);
		//System.out.println(token);
			
		return token;		
		
	}
	
}