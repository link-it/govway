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
package org.openspcoop2.core.protocolli.trasparente.testsuite.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jwk.KeyType;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione.ValidazioneJWTDynamicDiscoveryTest;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.JWKPrivateKeyConverter;
import org.openspcoop2.utils.certificate.JWKPublicKeyConverter;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
* RestTest
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {

	// dump abilitato
	
	@Test
	public void erogazione() throws Exception {
		_test(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione() throws Exception {
		_test(TipoServizio.FRUIZIONE);
	}
	
	private static void _test(TipoServizio tipoServizio) throws Exception {

		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);
		
		String id = IDUtilities.generateAlphaNumericRandomString(10);
		File f = new File("/tmp/govway.plugins.testsuite.id");
		f.delete();
		FileSystemUtilities.writeFile(f, id.getBytes());
		f.setReadable(true, false);
		f.setWritable(true, false);
		
		String role = tipoServizio == TipoServizio.EROGAZIONE ? "pa" : "pd";
		List<File> filesVerifica = new ArrayList<>();
		
		File fAutenticazione =  new File("/tmp/"+id+".autenticazione-"+role+".result");
		filesVerifica.add(fAutenticazione);
		
		File fAutorizzazione =  new File("/tmp/"+id+".autorizzazione-"+role+".result");
		filesVerifica.add(fAutorizzazione);
		
		File fAutorizzazioneContenuti =  new File("/tmp/"+id+".autorizzazioneContenuti-"+role+".result");
		filesVerifica.add(fAutorizzazioneContenuti);
		
		File fRateLimitingFiltro =  new File("/tmp/"+id+".rateLimiting-filtro-"+role+".result");
		filesVerifica.add(fRateLimitingFiltro);
		File fRateLimitingGroupBy =  new File("/tmp/"+id+".rateLimiting-groupBy-"+role+".result");
		filesVerifica.add(fRateLimitingGroupBy);
		
		File fIntegrazioneInRequest =  new File("/tmp/"+id+".integrazione-in-request-"+role+".result");
		filesVerifica.add(fIntegrazioneInRequest);
		File fIntegrazioneOutRequest =  new File("/tmp/"+id+".integrazione-out-request-"+role+".result");
		filesVerifica.add(fIntegrazioneOutRequest);
		File fIntegrazioneInResponse =  new File("/tmp/"+id+".integrazione-in-response-"+role+".result");
		if(tipoServizio == TipoServizio.EROGAZIONE ) {
			filesVerifica.add(fIntegrazioneInResponse);
		} // else nella fruizione per default l'in response non viene invocato
		File fIntegrazioneOutResponse =  new File("/tmp/"+id+".integrazione-out-response-"+role+".result");
		filesVerifica.add(fIntegrazioneOutResponse);
		
		File fConnettore =  new File("/tmp/"+id+".connettore-"+role+".result");
		filesVerifica.add(fConnettore);
		
		if(tipoServizio == TipoServizio.EROGAZIONE ) {
			File fBehaviour =  new File("/tmp/"+id+".behaviour-"+role+".result");
			filesVerifica.add(fBehaviour);
		}
		
		File fPreInRequestHandler =  new File("/tmp/"+id+".pre-in-request-handler-"+role+".result");
		filesVerifica.add(fPreInRequestHandler);
		File fInRequestHandler =  new File("/tmp/"+id+".in-request-handler-"+role+".result");
		filesVerifica.add(fInRequestHandler);
		File fInRequestProtocolHandler =  new File("/tmp/"+id+".in-request-protocol-handler-"+role+".result");
		filesVerifica.add(fInRequestProtocolHandler);
		File fOutRequestHandler =  new File("/tmp/"+id+".out-request-handler-"+role+".result");
		filesVerifica.add(fOutRequestHandler);
		File fPostOutRequestHandler =  new File("/tmp/"+id+".post-out-request-handler-"+role+".result");
		filesVerifica.add(fPostOutRequestHandler);
		
		File fPreInResponseHandler =  new File("/tmp/"+id+".pre-in-response-handler-"+role+".result");
		filesVerifica.add(fPreInResponseHandler);
		File fInResponseHandler =  new File("/tmp/"+id+".in-response-handler-"+role+".result");
		filesVerifica.add(fInResponseHandler);
		File fOutResponseHandler =  new File("/tmp/"+id+".out-response-handler-"+role+".result");
		filesVerifica.add(fOutResponseHandler);
		File fPostOutResponseHandler =  new File("/tmp/"+id+".post-out-response-handler-"+role+".result");
		//files.add(fPostOutResponseHandler); deve essere fatta a parte per gestire il disaccoppiamento tra la risposta ritornata e l'esecuzione dell'handler
		
		File fTokenDynamicDiscovery =  new File("/tmp/"+id+".token-dynamic-discovery-"+role+".result");
		filesVerifica.add(fTokenDynamicDiscovery);
		File fTokenValidazioneJWT=  new File("/tmp/"+id+".token-validazione-jwt-"+role+".result");
		filesVerifica.add(fTokenValidazioneJWT);
		File fTokenIntrospection =  new File("/tmp/"+id+".token-validazione-introspection-"+role+".result");
		filesVerifica.add(fTokenIntrospection);
		File fTokenUserInfo =  new File("/tmp/"+id+".token-validazione-userinfo-"+role+".result");
		filesVerifica.add(fTokenUserInfo);
		File fTokenValidazioneDPoP =  new File("/tmp/"+id+".token-validazione-dpop-dpop-"+role+".result");
		filesVerifica.add(fTokenValidazioneDPoP);
		
		File fIntrospection = new File("/tmp/introspectionResponse-"+role+".json");
		File fUserInfo = new File("/tmp/userinfoResponse-"+role+".json");
	
		File fTokenNegoziazioneVerifica =  new File("/tmp/"+id+".token-negoziazione-tokenNegoziazioneCustom-"+role+".result");
		filesVerifica.add(fTokenNegoziazioneVerifica);
		
		File fTokenNegoziazione = new File("/tmp/tokenNegoziazioneResponse-"+role+".json");
		
		File fAttributeAuthorityVerifica =  new File("/tmp/"+id+".attribute-authority-attributeAuthorityCustom-"+role+".result");
		filesVerifica.add(fAttributeAuthorityVerifica);
		
		File fAttributeAuthority = new File("/tmp/aaResponse-"+role+".json");
		
		File fJWK = File.createTempFile("dynamicKeystore", ".jwk");
		
		File fDynamicDiscovery = File.createTempFile("dynamic", ".json");
		
		try {

			byte [] keystoreBytes = null;
			keystoreBytes = FileSystemUtilities.readBytesFromFile("/etc/govway/keys/erogatore.jks");
			PublicKey publicKey = ArchiveLoader.loadFromKeystoreJKS(keystoreBytes, "erogatore", "openspcoop").getCertificate().getCertificate().getPublicKey();

			// Carica la chiave privata dal keystore
			org.openspcoop2.utils.certificate.KeyStore ks = new org.openspcoop2.utils.certificate.KeyStore(keystoreBytes, "openspcoop");
			PrivateKey privateKey = ks.getPrivateKey("erogatore", "openspcoop");
			KeyPair keyPair = new KeyPair(publicKey, privateKey);

			String jwks = JWKPublicKeyConverter.convert(publicKey, "erogatore", true, false);
			keystoreBytes = jwks.getBytes();
			FileSystemUtilities.writeFile(fJWK, keystoreBytes);

			// Calcola JWK e thumbprint per DPoP binding
			JsonWebKey jwk = createRsaJwk(keyPair);
			String jkt = computeJwkThumbprint(jwk);

			String dd = tipoServizio == TipoServizio.EROGAZIONE ? buildDD_PA(fJWK) : buildDD_PD(fJWK);
			FileSystemUtilities.writeFile(fDynamicDiscovery, dd.getBytes());

			// Costruisce l'access token con cnf.jkt per DPoP binding
			String jsonJWT = buildJSON(tipoServizio == TipoServizio.EROGAZIONE, "jwt", jkt);
			String jwt = buildJWT(jsonJWT);

			// Costruisce il DPoP token
			String url = tipoServizio == TipoServizio.EROGAZIONE
					? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestPlugins/v1/test"
					: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestPlugins/v1/test";
			String dpopToken = buildDPoP(tipoServizio == TipoServizio.EROGAZIONE, "dpop", jwt, url, keyPair, jwk);

			String jsonIntrospection = buildJSON(tipoServizio == TipoServizio.EROGAZIONE, "introspection", null);
			FileSystemUtilities.writeFile(fIntrospection, jsonIntrospection.getBytes());
			fIntrospection.setReadable(true, false);
			fIntrospection.setWritable(true, false);

			String jsonUserinfo = buildJSON(tipoServizio == TipoServizio.EROGAZIONE, "userinfo", null);
			FileSystemUtilities.writeFile(fUserInfo, jsonUserinfo.getBytes());
			fUserInfo.setReadable(true, false);
			fUserInfo.setWritable(true, false);

			String jsonNegoziazione = buildJSON_negoziazione(tipoServizio == TipoServizio.EROGAZIONE, "tokenNegoziazioneCustom");
			FileSystemUtilities.writeFile(fTokenNegoziazione, jsonNegoziazione.getBytes());
			fTokenNegoziazione.setReadable(true, false);
			fTokenNegoziazione.setWritable(true, false);

			String jsonAA = buildJSON(tipoServizio == TipoServizio.EROGAZIONE, "attributeAuthorityCustom", null);
			FileSystemUtilities.writeFile(fAttributeAuthority, jsonAA.getBytes());
			fAttributeAuthority.setReadable(true, false);
			fAttributeAuthority.setWritable(true, false);

			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

			HttpRequest request = new HttpRequest();
			request.addHeader(ValidazioneJWTDynamicDiscoveryTest.DYNAMIC_HEADER_LOCATION, fDynamicDiscovery.getAbsolutePath());
			request.addHeader("test-govway-role", role);
			request.addHeader("test-plugins-autenticazione", "govway-testsuite-plugins");
			request.addHeader("test-plugins-autorizzazione-contenuto", "govway-testsuite-plugins-authz");
			request.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_DPOP+jwt);
			request.addHeader(HttpConstants.AUTHORIZATION_DPOP, dpopToken);

			request.setMethod(HttpRequestMethod.GET);

			request.setUrl(url);
			
			HttpResponse response = HttpUtilities.httpInvoke(request);
			
			assertEquals(200, response.getResultHTTPOperation());
				
			String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
			assertNotNull(idTransazione);
			
			// Verifiche
			for (File file : filesVerifica) {
				verifica(file);
			}
			
			Throwable tVerifica = null;
			for (int i = 0; i < 10; i++) {
				try {
					verifica(fPostOutResponseHandler);
					tVerifica = null;
				}catch(Throwable t) {
					tVerifica = t;
				}
			}
			if(tVerifica!=null) {
				throw new Exception(tVerifica.getMessage(),tVerifica);
			}
			
		}finally {
			try {
				f.delete();
			}catch(Throwable t) {
				// ignore
			}
			for (File file : filesVerifica) {
				try {
					file.delete();
				}catch(Throwable t) {
					// ignore
				}
			}
			try {
				fPostOutResponseHandler.delete();
			}catch(Throwable t) {
				// ignore
			}
			try {
				fIntrospection.delete();
			}catch(Throwable t) {
				// ignore
			}
			try {
				fUserInfo.delete();
			}catch(Throwable t) {
				// ignore
			}
			try {
				fTokenNegoziazione.delete();
			}catch(Throwable t) {
				// ignore
			}
			try {
				fAttributeAuthority.delete();
			}catch(Throwable t) {
				// ignore
			}
			FileSystemUtilities.deleteFile(fJWK);
			FileSystemUtilities.deleteFile(fDynamicDiscovery);
		}
			
	}	
	
	private static void verifica(File f) throws Exception {
		String esitoAtteso = "OK";
		String esito = FileSystemUtilities.readFile(f);
		if(!esitoAtteso.equals(esito)) {
			throw new Exception("Atteso esito '"+esitoAtteso+"' trovato '"+esito+"' in file '"+f.getAbsolutePath()+"'");
		}
	}

	private static String buildJSON(boolean erogazione, String operazione, String jkt) throws Exception {

		String cliendId = "18192.apps";
		String audience = "23223.apps";
		String issuer = "testAuthEnte";
		String subject = "10623542342342323";
		String jti = "33aa1676-1f9e-34e2-8515-0cfca111a188";
		Date now = DateManager.getDate();
		Date campione = new Date( (now.getTime()/1000)*1000);
		Date iat = new Date(campione.getTime());
		Date nbf = new Date(campione.getTime() - (1000*20));
		Date exp = new Date(campione.getTime() + (1000*60));
		String aud = "\"aud\":[\""+audience+"\"]";
		String jsonInput =
				"{ "+aud+",";

		String clientId = "\"client_id\":\""+cliendId+"\"";
		jsonInput = jsonInput+
			clientId+" ,";

		String sub = "\"sub\":\""+subject+"\"";
		jsonInput = jsonInput+
				sub+" , ";

		String iss ="\"iss\":\""+issuer+"\"";
		jsonInput = jsonInput+
				iss+" , ";

		String iatJson = "\"iat\":"+(iat.getTime()/1000)+"";
		jsonInput = jsonInput +
				iatJson + " , ";


		String nbfJson = "\"nbf\":"+(nbf.getTime()/1000)+"";
		jsonInput = jsonInput +
				nbfJson+ " , ";

		String expJson = "\"exp\":"+(exp.getTime()/1000)+"";
		jsonInput = jsonInput +
				expJson + " ,  ";

		if(erogazione) {
			String porta = "\"pa\":\"pa\"";
			jsonInput = jsonInput +
					porta+" , ";
		}
		else {
			String porta = "\"pd\":\"pd\"";
			jsonInput = jsonInput +
					porta+" , ";
		}

		String operazioneS = "\"operazione\":\""+operazione+"\"";
		jsonInput = jsonInput +
				operazioneS+" , ";

		// Aggiunge cnf.jkt per DPoP binding se specificato
		if(jkt != null) {
			String cnf = "\"cnf\":{\"jkt\":\""+jkt+"\"}";
			jsonInput = jsonInput + cnf + " , ";
		}

		String jtiS = "\"jti\":\""+jti+"\"";
		jsonInput = jsonInput +
				" "+jtiS+"}";


		//System.out.println("TOKEN ["+jsonInput+"]");

		return jsonInput;
	}
	private static String buildJWT(String jsonInput) throws Exception {
		
		Properties props = new Properties();
		props.put("rs.security.keystore.type","JKS");
		String password = "openspcoop";
		props.put("rs.security.keystore.file", "/etc/govway/keys/erogatore.jks");
		props.put("rs.security.keystore.alias","erogatore");
		props.put("rs.security.keystore.password","openspcoop");
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
	
	
	private static String buildJSON_negoziazione(boolean erogazione, String operazione) throws Exception {
		
		String access_token = "2YotnFZFEjr1zCsicMWpAA";
		String token_type = "example";		
		String expires_in = "3600";

		String jsonInput = 
				"{ ";
		
		String access_tokenS = "\"access_token\":\""+access_token+"\"";
		jsonInput = jsonInput+
				access_tokenS+" ,";
		
		String token_typeS = "\"token_type\":\""+token_type+"\"";
		jsonInput = jsonInput+
				token_typeS+" , ";
				
		String expires_inS ="\"expires_in\":"+expires_in+"";
		jsonInput = jsonInput+
				expires_inS+" , ";
										
		if(erogazione) {
			String porta = "\"pa\":\"pa\"";
			jsonInput = jsonInput +
					porta+" , ";
		}
		else {
			String porta = "\"pd\":\"pd\"";
			jsonInput = jsonInput +
					porta+" , ";
		}
				
		String operazioneS = "\"operazione\":\""+operazione+"\"";
		jsonInput = jsonInput +
				operazioneS+" } ";
				
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		return jsonInput;
	}
	
	
	private static String buildDD_PA(File f) {
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		
		String jwkUri = f.getAbsolutePath();
		String introUri = "http://127.0.0.1:8080/TestService/echo?existsQueryParameters=test_token&destFile=/tmp/introspectionResponse-pa.json&destFileContentType=application/json";
		String userInfoUri = "http://127.0.0.1:8080/TestService/echo?existsQueryParameters=test_token_user_info&destFile=/tmp/userinfoResponse-pa.json&destFileContentType=application/json";
		return ValidazioneJWTDynamicDiscoveryTest.buildDD("", mapExpectedTokenInfo,
				jwkUri, introUri, userInfoUri, "pa");
		
	}
	private static String buildDD_PD(File f) {

		List<String> mapExpectedTokenInfo = new ArrayList<>();

		String jwkUri = f.getAbsolutePath();
		String introUri = "http://127.0.0.1:8080/TestService/echo?existsQueryParameters=test_token&destFile=/tmp/introspectionResponse-pd.json&destFileContentType=application/json";
		String userInfoUri = "http://127.0.0.1:8080/TestService/echo?existsQueryParameters=test_token_user_info&destFile=/tmp/userinfoResponse-pd.json&destFileContentType=application/json";
		return ValidazioneJWTDynamicDiscoveryTest.buildDD("", mapExpectedTokenInfo,
				jwkUri, introUri, userInfoUri, "pd");

	}


	// ===== Metodi per DPoP =====

	private static String buildDPoP(boolean erogazione, String operazione, String accessToken, String httpUri, KeyPair keyPair, JsonWebKey jwk) throws Exception {

		JSONUtils jsonUtils = JSONUtils.getInstance(false);
		ObjectNode payload = jsonUtils.newObjectNode();

		// Claims standard RFC 9449
		String jti = UUID.randomUUID().toString();
		payload.put("jti", jti);
		payload.put("htm", "GET");
		payload.put("htu", normalizeHtu(httpUri));
		payload.put("iat", DateManager.getDate().getTime() / 1000);

		// Access token hash (ath)
		String ath = computeAccessTokenHash(accessToken);
		payload.put("ath", ath);

		// Claims custom per il test
		payload.put("operazione", operazione);
		if(erogazione) {
			payload.put("pa", "pa");
		}
		else {
			payload.put("pd", "pd");
		}

		String payloadJson = jsonUtils.toString(payload);

		// Build JWS con JWK nell'header
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);

		JwtHeaders jwtHeaders = new JwtHeaders();
		jwtHeaders.setType("dpop+jwt");
		jwtHeaders.setJwKeyRaw(jwk.asMap());

		// Crea JWKSet dalla KeyPair per la firma
		String kid = UUID.randomUUID().toString();
		String jwkSetJson = JWKPrivateKeyConverter.convert(
				keyPair.getPublic(),
				keyPair.getPrivate(),
				kid, true, false);
		JWKSet jwkSet = new JWKSet(jwkSetJson);

		JsonSignature jsonSignature = new JsonSignature(
				jwkSet.getJsonWebKeys(),
				false, // secretKey
				kid,   // alias
				"RS256",
				jwtHeaders,
				options);

		return jsonSignature.sign(payloadJson);
	}

	private static JsonWebKey createRsaJwk(KeyPair keyPair) {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		JsonWebKey jwk = new JsonWebKey();
		jwk.setKeyType(KeyType.RSA);
		jwk.setProperty("n", Base64Utilities.encodeBase64URLSafeString(toUnsignedByteArray(publicKey.getModulus())));
		jwk.setProperty("e", Base64Utilities.encodeBase64URLSafeString(toUnsignedByteArray(publicKey.getPublicExponent())));

		return jwk;
	}

	private static byte[] toUnsignedByteArray(java.math.BigInteger bigInt) {
		byte[] bytes = bigInt.toByteArray();
		// Se il primo byte è 0 (segno), rimuovilo
		if (bytes.length > 1 && bytes[0] == 0) {
			byte[] result = new byte[bytes.length - 1];
			System.arraycopy(bytes, 1, result, 0, result.length);
			return result;
		}
		return bytes;
	}

	private static String computeJwkThumbprint(JsonWebKey jwk) {
		return JwkUtils.getThumbprint(jwk);
	}

	private static String computeAccessTokenHash(String accessToken) throws Exception {
		byte[] hash = org.openspcoop2.utils.digest.DigestUtils.getDigestValue(
				accessToken.getBytes(java.nio.charset.StandardCharsets.US_ASCII),
				org.openspcoop2.utils.digest.DigestType.SHA256.getAlgorithmName());
		return Base64Utilities.encodeBase64URLSafeString(hash);
	}

	private static String normalizeHtu(String url) throws Exception {
		java.net.URI uri = new java.net.URI(url);
		return new java.net.URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null).toString();
	}

}
