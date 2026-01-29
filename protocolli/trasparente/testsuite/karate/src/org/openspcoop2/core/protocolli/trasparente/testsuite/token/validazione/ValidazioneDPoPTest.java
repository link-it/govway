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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jwk.KeyType;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.certificate.JWKPrivateKeyConverter;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
* ValidazioneDPoPTest
*
* @author Andrea Poli (andrea.poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ValidazioneDPoPTest extends ConfigLoader {


	// ===== Costanti API =====

	public static final String API = "TestValidazioneDPoP";
	private static final String AZIONE_RSA = "rsa";
	private static final String AZIONE_EC = "ec";
	
	private static final String CUSTOM_HEADER = "custom-dpop";
	/**private static final String CUSTOM_FORWARD_HEADER = "custom-forward-dpop";*/
	private static final String CUSTOM_PARAM = "custom_dpop";
	/**private static final String CUSTOM_FORWARD_PARAM = "custom_forward_dpop";*/

	// ===== TEST =====

	@Test
	public void rsa() throws Exception {
		test(HttpConstants.AUTHORIZATION_DPOP, null, AZIONE_RSA, 
				true, null);
	}
	
	@Test
	public void ec() throws Exception {
		test(HttpConstants.AUTHORIZATION_DPOP, null, AZIONE_EC, 
				false, null);
	}
	
	@Test
	public void forwardOnPolicyOffErogazione() throws Exception {
		test(HttpConstants.AUTHORIZATION_DPOP, null, "forward-onPolicy-offErogazione", 
				true, null);
	}
	
	@Test
	public void forwardOnPolicyOnErogazione() throws Exception {
		test(HttpConstants.AUTHORIZATION_DPOP, null, "forward-onPolicy-onErogazione", 
				true, null);
	}
	
	@Test
	public void customHeaderForwardOffPolicy() throws Exception {
		test(CUSTOM_HEADER, null, "customHeader-forward-offPolicy", 
				true, null);
	}
	
	@Test
	public void customHeaderForwardOnPolicyOffErogazione() throws Exception {
		test(CUSTOM_HEADER, null, "customHeader-forward-onPolicy-offErogazione", 
				true, null);
	}
	
	@Test
	public void customHeaderForwardOnPolicyOnErogazioneAsReceived() throws Exception {
		test(CUSTOM_HEADER, null, "customHeader-forward-onPolicy-onErogazione-asReceived", 
				true, null);
	}
	
	@Test
	public void customHeaderForwardOnPolicyOnErogazioneCustom() throws Exception {
		test(CUSTOM_HEADER, null, "customHeader-forward-onPolicy-onErogazione-custom", 
				true, null);
	}
	
	@Test
	public void customHeaderForwardOnPolicyOnErogazioneDPoP() throws Exception {
		test(CUSTOM_HEADER, null, "customHeader-forward-onPolicy-onErogazione-dpop", 
				true, null);
	}
	
	@Test
	public void customQueryParamForwardOffPolicy() throws Exception {
		test(null, CUSTOM_PARAM, "customQueryParam-forward-offPolicy", 
				true, null);
	}
	
	@Test
	public void customQueryParamForwardOnPolicyOffErogazione() throws Exception {
		test(null, CUSTOM_PARAM, "customQueryParam-forward-onPolicy-offErogazione", 
				true, null);
	}
	
	@Test
	public void customQueryParamForwardOnPolicyOnErogazioneAsReceived() throws Exception {
		test(null, CUSTOM_PARAM, "customQueryParam-forward-onPolicy-onErogazione-asReceived", 
				true, null);
	}
	
	@Test
	public void customQueryParamForwardOnPolicyOnErogazioneCustom() throws Exception {
		test(null, CUSTOM_PARAM, "customQueryParam-forward-onPolicy-onErogazione-custom", 
				true, null);
	}
	
	@Test
	public void customQueryParamForwardOnPolicyOnErogazioneDPoP() throws Exception {
		test(null, CUSTOM_PARAM, "customQueryParam-forward-onPolicy-onErogazione-dpop", 
				true, null);
	}
	
	@Test
	public void formatoCustom() throws Exception {
		
		String azione = "formato-custom";
		
		// Genera la coppia di token (access token + DPoP) con chiavi RSA
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		DPoPTokenParams dpopParams = new DPoPTokenParams();
		dpopParams.setPrefix("TEST");
		dpopParams.useRSA();
		DPoPTokenPair tokenPair = buildDPoPTokenPair(HttpRequestMethod.POST.name(), buildPasePath(azione),
				new AccessTokenParams(),
				dpopParams);
	
		// Prepara gli header
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION_DPOP, tokenPair.getDpopToken());

		// Query Parameters
		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("access_token", tokenPair.getAccessToken());
		
		test(tokenPair, mapExpectedTokenInfo,
				headers, queryParameters,
				azione, true, null);
	}
	
	
	@Test
	public void invalidTypHeader() throws Exception {
		test(HttpConstants.AUTHORIZATION_DPOP, null, "invalid-typ-hdr", 
				true, "Validazione del DPoP token fallita: Token non valido: Claim 'typ' with invalid value 'dpop+jwt'");
	}
	
	@Test
	public void invalidAlgHeader() throws Exception {
		test(CUSTOM_HEADER, null, "customHeader-forward-offPolicy", 
				false, "Validazione del DPoP token fallita: Token non valido: Claim 'alg' with invalid value 'ES256'");
	}
	
	@Test
	public void invalidSignature() throws Exception {
		
		String azione = AZIONE_RSA;
		
		// Genera la coppia di token (access token + DPoP) con chiavi RSA
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		DPoPTokenParams dpopParams = new DPoPTokenParams();
		dpopParams.useRSA();
		DPoPTokenPair tokenPair = buildDPoPTokenPair(HttpRequestMethod.POST.name(), buildPasePath(azione),
				new AccessTokenParams(),
				dpopParams);
	
		// Prepara gli header
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_DPOP+tokenPair.getAccessToken());
		String [] split = tokenPair.getDpopToken().split("\\.");
		headers.put(HttpConstants.AUTHORIZATION_DPOP, (split[0]+"."+split[1]+"."+"ALTER"+split[2]));

		// Query Parameters
		Map<String, String> queryParameters = new HashMap<>();
		
		test(tokenPair, mapExpectedTokenInfo,
				headers, queryParameters,
				azione, true, "Validazione del DPoP token fallita: Token non valido: Invalid DPoP token: signature verification failed: Invalid DPoP token: signature verification failed");
	}
	
	@Test
	public void invalidIat() throws Exception {
		String azione = "low-iat";
		
		// Genera la coppia di token (access token + DPoP) con chiavi RSA
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		DPoPTokenParams dpopParams = new DPoPTokenParams();
		dpopParams.useRSA();
		DPoPTokenPair tokenPair = buildDPoPTokenPair(HttpRequestMethod.POST.name(), buildPasePath(azione),
				new AccessTokenParams(),
				dpopParams);
	
		// Prepara gli header
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_DPOP+tokenPair.getAccessToken());
		headers.put(HttpConstants.AUTHORIZATION_DPOP, tokenPair.getDpopToken());

		// Query Parameters
		Map<String, String> queryParameters = new HashMap<>();
		
		// Attendo scadenza iat
		org.openspcoop2.utils.Utilities.sleep(5000);
		
		test(tokenPair, mapExpectedTokenInfo,
				headers, queryParameters,
				azione, true, "Validazione del DPoP token fallita: Token non valido: Expired DPoP token");
	}
	
	@Test
	public void duplicateJtiLocal() throws Exception {
		duplicateJti(AZIONE_RSA);
	}
	@Test
	public void duplicateJtiRedis() throws Exception {
		duplicateJti("redis");
	}
	private void duplicateJti(String azione) throws Exception {
		
		// Genera la coppia di token (access token + DPoP) con chiavi RSA
		DPoPTokenParams dpopParams = new DPoPTokenParams();
		dpopParams.useRSA();
		DPoPTokenPair tokenPair = buildDPoPTokenPair(HttpRequestMethod.POST.name(), buildPasePath(azione),
				new AccessTokenParams(),
				dpopParams);
	
		// Prepara gli header
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_DPOP+tokenPair.getAccessToken());
		headers.put(HttpConstants.AUTHORIZATION_DPOP, tokenPair.getDpopToken());

		// Query Parameters
		Map<String, String> queryParameters = new HashMap<>();
		
		List<String> mapExpectedTokenInfoPrimaInvocazione = new ArrayList<>();
		test(tokenPair, mapExpectedTokenInfoPrimaInvocazione,
				headers, queryParameters,
				azione, true, null);
		
		List<String> mapExpectedTokenInfoSecondaInvocazione = new ArrayList<>();
		test(tokenPair, mapExpectedTokenInfoSecondaInvocazione,
				headers, queryParameters,
				azione, true, ("Validazione del DPoP token fallita: Token non valido: DPoP JTI validation failed: DPoP JTI ["+dpopParams.getJti()+"] has already been used (replay attack detected)"));
		
	}
	
	@Test
	public void duplicateJtiDisabled() throws Exception {
		
		String azione = "antiReplyDisabilitato";
		
		// Genera la coppia di token (access token + DPoP) con chiavi RSA
		DPoPTokenParams dpopParams = new DPoPTokenParams();
		dpopParams.useRSA();
		DPoPTokenPair tokenPair = buildDPoPTokenPair(HttpRequestMethod.POST.name(), buildPasePath(azione),
				new AccessTokenParams(),
				dpopParams);
	
		// Prepara gli header
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_DPOP+tokenPair.getAccessToken());
		headers.put(HttpConstants.AUTHORIZATION_DPOP, tokenPair.getDpopToken());

		// Query Parameters
		Map<String, String> queryParameters = new HashMap<>();
		
		List<String> mapExpectedTokenInfoPrimaInvocazione = new ArrayList<>();
		test(tokenPair, mapExpectedTokenInfoPrimaInvocazione,
				headers, queryParameters,
				azione, true, null);
		
		List<String> mapExpectedTokenInfoSecondaInvocazione = new ArrayList<>();
		test(tokenPair, mapExpectedTokenInfoSecondaInvocazione,
				headers, queryParameters,
				azione, true, null); // OK!
	}
	
	@Test
	public void localReject() throws Exception {
		
		String azione = "localReject";
		
		test(HttpConstants.AUTHORIZATION_DPOP, null, azione, 
				true, null);
		
		test(HttpConstants.AUTHORIZATION_DPOP, null, azione, 
				true, null);
		
		test(HttpConstants.AUTHORIZATION_DPOP, null, azione, 
				true, "Validazione del DPoP token fallita: Token non valido: DPoP JTI validation error: DPoP JTI validation failed: local cache capacity exceeded (2/2 entries). Consider increasing cache size or switching to distributed validation");
		
		// Il cleanup scheduler gira ogni 10 secondi di default (org.openspcoop2.pdd.gestioneToken.dpop.jti.localCache.cleanupIntervalSeconds=10)
		// con 20 sono sicuro
		org.openspcoop2.utils.Utilities.sleep(20000);
		
		test(HttpConstants.AUTHORIZATION_DPOP, null, azione, 
				true, null);
		
	}
	
	@Test
	public void localLRU() throws Exception {
		
		String azione = "localLRU";
		
		test(HttpConstants.AUTHORIZATION_DPOP, null, azione, 
				true, null);
		
		test(HttpConstants.AUTHORIZATION_DPOP, null, azione, 
				true, null);
		
		test(HttpConstants.AUTHORIZATION_DPOP, null, azione, 
				true, null);
		
	}
	
	
	
	private void test(String headerName, String paramName, 
			String azione, boolean rsa, String errore) throws Exception {

		// Genera la coppia di token (access token + DPoP) con chiavi RSA
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		DPoPTokenParams dpopParams = new DPoPTokenParams();
		if(rsa) {
			dpopParams.useRSA();
		}
		else {
			dpopParams.useEC();
		}
		DPoPTokenPair tokenPair = buildDPoPTokenPair(HttpRequestMethod.POST.name(), buildPasePath(azione),
				new AccessTokenParams(),
				dpopParams);
		
		// Prepara gli header
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_DPOP + tokenPair.getAccessToken());
		if(headerName!=null) {
			headers.put(headerName, tokenPair.getDpopToken());
		}

		// Query Parameters
		Map<String, String> queryParameters = new HashMap<>();
		if(paramName!=null) {
			queryParameters.put(paramName, tokenPair.getDpopToken());
		}
		
		test(tokenPair, mapExpectedTokenInfo,
				headers, queryParameters,
				azione, rsa, errore);
	}

	private String buildPasePath(String azione) {
		// Costruisce l'URL per calcolare htu
		String basePath = System.getProperty("govway_base_path");
		return basePath + "/SoggettoInternoTest/" + API + "/v1/"+azione;
	}
	
	private void test(DPoPTokenPair tokenPair, List<String> mapExpectedTokenInfo,
			Map<String, String> headers, Map<String, String> queryParameters,
			String azione, boolean rsa, String errore) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		// Aggiungi le info attese per la validazione
		mapExpectedTokenInfo.add("\"client_id\":\"" + Utilities.client_id + "\"");
		mapExpectedTokenInfo.add("\"iss\":\"" + Utilities.issuer + "\"");
		mapExpectedTokenInfo.add("\"sub\":\"" + Utilities.subject + "\"");
		mapExpectedTokenInfo.add("\"sourceType\":\"JWT\"");
		
		if(errore==null) {
			mapExpectedTokenInfo.add("\"cnf.jkt\":\"" + tokenPair.getAccessTokenParams().getJkt() + "\""); 
			mapExpectedTokenInfo.add("\"dpop\":"); 
			mapExpectedTokenInfo.add("\"typ\":\"dpop+jwt\"");
			mapExpectedTokenInfo.add("\"jwkThumbprint\":");
			mapExpectedTokenInfo.add("\"ath\":\"" + tokenPair.getDpopParams().getAth() + "\""); 
			mapExpectedTokenInfo.add("\"alg\":\"" + (rsa ? "RS256" : "ES256") + "\""); 
			mapExpectedTokenInfo.add("\"jti\":\"" + tokenPair.getDpopParams().getJti() + "\""); 
			mapExpectedTokenInfo.add("\"htm\":\"" + tokenPair.getDpopParams().getHtm() + "\""); 
			mapExpectedTokenInfo.add("\"htu\":\"" + tokenPair.getDpopParams().getHtu() + "\""); 
		}
		
		HttpResponse response = Utilities._test(logCore, API, azione, headers, queryParameters,
				errore,
				Utilities.credenzialiMittente_usernameMailNull, mapExpectedTokenInfo);
		
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		if(errore!=null) {
			DBVerifier.checkDiagnostic(idTransazione, errore);
		}
		else {
			DBVerifier.checkDiagnostic(idTransazione, "Validazione del DPoP token effettuata con successo");
		}
	}
	
	
	

	// ===== Costanti =====

	private static final String DPOP_TYPE = "dpop+jwt";
	private static final String DPOP_HTTP_METHOD = "htm";
	private static final String DPOP_HTTP_URI = "htu";
	private static final String DPOP_ACCESS_TOKEN_HASH = "ath";

	// ===== Path delle chiavi =====

	// RSA
	private static final String RSA_PRIVATE_KEY_PATH = "/etc/govway/keys/keyPair-test.rsa.pkcs8_encrypted.privateKey.pem";
	private static final String RSA_PUBLIC_KEY_PATH = "/etc/govway/keys/keyPair-test.rsa.publicKey.pem";
	private static final String RSA_KEY_PASSWORD = "123456";

	// EC (ES256)
	private static final String EC_PRIVATE_KEY_PATH = "/etc/govway/keys/keyPair-test-es256_pkcs8_encrypted_private.pem";
	private static final String EC_PUBLIC_KEY_PATH = "/etc/govway/keys/keyPair-test-es256_public.pem";
	private static final String EC_KEY_PASSWORD = "123456";

	// ===== KeyPair caricate dai file =====

	private static KeyPair rsaKeyPair;
	private static KeyPair ecKeyPair;

	static {
		// Registra temporaneamente il provider BouncyCastle per la gestione delle chiavi cifrate
		java.security.Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		try {
			// Carica la coppia di chiavi RSA dai file
			rsaKeyPair = loadKeyPair(RSA_PRIVATE_KEY_PATH, RSA_PUBLIC_KEY_PATH, RSA_KEY_PASSWORD, KeyUtils.ALGO_RSA);

			// Carica la coppia di chiavi EC dai file
			ecKeyPair = loadKeyPair(EC_PRIVATE_KEY_PATH, EC_PUBLIC_KEY_PATH, EC_KEY_PASSWORD, KeyUtils.ALGO_EC);
		} catch(Exception e) {
			throw new UtilsRuntimeException("Errore nel caricamento delle chiavi di test: " + e.getMessage(), e);
		} finally {
			java.security.Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
		}
	}

	/**
	 * Carica una KeyPair dai file PEM
	 */
	private static KeyPair loadKeyPair(String privateKeyPath, String publicKeyPath, String password, String algorithm) throws Exception {
		KeyUtils keyUtils = KeyUtils.getInstance(algorithm);

		// Carica la chiave privata (cifrata)
		byte[] privateKeyBytes = FileSystemUtilities.readBytesFromFile(new File(privateKeyPath));
		PrivateKey privateKey = keyUtils.getPrivateKey(privateKeyBytes, password);

		// Carica la chiave pubblica
		byte[] publicKeyBytes = FileSystemUtilities.readBytesFromFile(new File(publicKeyPath));
		PublicKey publicKey = keyUtils.getPublicKey(publicKeyBytes);

		return new KeyPair(publicKey, privateKey);
	}

	// ===== Classe per parametri DPoP =====

	public static class DPoPTokenParams {

		private String prefix = "";
		
		// Header params
		private String typ = DPOP_TYPE;
		private String algorithm = "RS256";
		private boolean includeJwk = true;
		private JsonWebKey jwk;

		// Payload params
		private String jti = UUID.randomUUID().toString();
		private boolean includeJti = true;
		private String htm = "POST";
		private boolean includeHtm = true;
		private String htu = "http://localhost:8080/resource";
		private boolean includeHtu = true;
		private Date iat = DateManager.getDate();
		private boolean includeIat = true;
		private String ath;
		private boolean includeAth = true;

		// Custom claims
		private String nonce;
		private boolean includeNonce = false;

		// Key for signing
		private KeyPair keyPair;

		public DPoPTokenParams() {
			this.keyPair = rsaKeyPair;
		}

		public DPoPTokenParams useRSA() {
			this.algorithm = "RS256";
			this.keyPair = rsaKeyPair;
			return this;
		}

		public DPoPTokenParams useEC() {
			this.algorithm = "ES256";
			this.keyPair = ecKeyPair;
			return this;
		}

		public String getPrefix() {
			return this.prefix;
		}
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
		
		// Getters e Setters con metodo fluent

		public String getTyp() { return this.typ; }
		public DPoPTokenParams setTyp(String typ) { this.typ = typ; return this; }
		public DPoPTokenParams withoutTyp() { this.typ = null; return this; }

		public String getAlgorithm() { return this.algorithm; }
		public DPoPTokenParams setAlgorithm(String algorithm) { this.algorithm = algorithm; return this; }

		public boolean isIncludeJwk() { return this.includeJwk; }
		public DPoPTokenParams setIncludeJwk(boolean includeJwk) { this.includeJwk = includeJwk; return this; }
		public DPoPTokenParams withoutJwk() { this.includeJwk = false; return this; }

		public JsonWebKey getJwk() { return this.jwk; }
		public DPoPTokenParams setJwk(JsonWebKey jwk) { this.jwk = jwk; return this; }

		public String getJti() { return this.jti; }
		public DPoPTokenParams setJti(String jti) { this.jti = jti; return this; }
		public boolean isIncludeJti() { return this.includeJti; }
		public DPoPTokenParams withoutJti() { this.includeJti = false; return this; }

		public String getHtm() { return this.htm; }
		public DPoPTokenParams setHtm(String htm) { this.htm = htm; return this; }
		public boolean isIncludeHtm() { return this.includeHtm; }
		public DPoPTokenParams withoutHtm() { this.includeHtm = false; return this; }

		public String getHtu() { return this.htu; }
		public DPoPTokenParams setHtu(String htu) { this.htu = htu; return this; }
		public boolean isIncludeHtu() { return this.includeHtu; }
		public DPoPTokenParams withoutHtu() { this.includeHtu = false; return this; }

		public Date getIat() { return this.iat; }
		public DPoPTokenParams setIat(Date iat) { this.iat = iat; return this; }
		public DPoPTokenParams setIatFuture(long millisInFuture) {
			this.iat = new Date(DateManager.getTimeMillis() + millisInFuture);
			return this;
		}
		public DPoPTokenParams setIatPast(long millisInPast) {
			this.iat = new Date(DateManager.getTimeMillis() - millisInPast);
			return this;
		}
		public boolean isIncludeIat() { return this.includeIat; }
		public DPoPTokenParams withoutIat() { this.includeIat = false; return this; }

		public String getAth() { return this.ath; }
		public DPoPTokenParams setAth(String ath) { this.ath = ath; return this; }
		public boolean isIncludeAth() { return this.includeAth; }
		public DPoPTokenParams withoutAth() { this.includeAth = false; return this; }

		public String getNonce() { return this.nonce; }
		public DPoPTokenParams setNonce(String nonce) { this.nonce = nonce; this.includeNonce = true; return this; }
		public boolean isIncludeNonce() { return this.includeNonce; }

		public KeyPair getKeyPair() { return this.keyPair; }
		public DPoPTokenParams setKeyPair(KeyPair keyPair) { this.keyPair = keyPair; return this; }
	}

	// ===== Classe per parametri Access Token =====

	public static class AccessTokenParams {
		// Standard claims
		private String clientId = Utilities.client_id;
		private boolean includeClientId = true;
		private String issuer = Utilities.issuer;
		private boolean includeIssuer = true;
		private String subject = Utilities.subject;
		private boolean includeSubject = true;
		private String audience = "23223.apps";
		private boolean includeAudience = true;
		private Date iat;
		private boolean includeIat = true;
		private Date exp;
		private boolean includeExp = true;
		private Date nbf;
		private boolean includeNbf = true;
		private String jti = "33aa1676-1f9e-34e2-8515-0cfca111a188";
		private boolean includeJti = true;

		// DPoP binding - cnf/jkt
		private String jkt;
		private boolean includeCnf = true;

		// Scope e roles
		private String scope;
		private String[] roles;

		// Signing
		private String algorithm = "RS256";

		public AccessTokenParams() {
			Date now = DateManager.getDate();
			Date campione = new Date((now.getTime()/1000)*1000);
			this.iat = campione;
			this.nbf = new Date(campione.getTime() - (1000*20));
			this.exp = new Date(campione.getTime() + (1000*60));
		}

		// Getters e Setters con metodo fluent

		public String getClientId() { return this.clientId; }
		public AccessTokenParams setClientId(String clientId) { this.clientId = clientId; return this; }
		public AccessTokenParams withoutClientId() { this.includeClientId = false; return this; }
		public boolean isIncludeClientId() { return this.includeClientId; }

		public String getIssuer() { return this.issuer; }
		public AccessTokenParams setIssuer(String issuer) { this.issuer = issuer; return this; }
		public AccessTokenParams withoutIssuer() { this.includeIssuer = false; return this; }
		public boolean isIncludeIssuer() { return this.includeIssuer; }

		public String getSubject() { return this.subject; }
		public AccessTokenParams setSubject(String subject) { this.subject = subject; return this; }
		public AccessTokenParams withoutSubject() { this.includeSubject = false; return this; }
		public boolean isIncludeSubject() { return this.includeSubject; }

		public String getAudience() { return this.audience; }
		public AccessTokenParams setAudience(String audience) { this.audience = audience; return this; }
		public AccessTokenParams withoutAudience() { this.includeAudience = false; return this; }
		public boolean isIncludeAudience() { return this.includeAudience; }

		public Date getIat() { return this.iat; }
		public AccessTokenParams setIat(Date iat) { this.iat = iat; return this; }
		public AccessTokenParams withoutIat() { this.includeIat = false; return this; }
		public boolean isIncludeIat() { return this.includeIat; }

		public Date getExp() { return this.exp; }
		public AccessTokenParams setExp(Date exp) { this.exp = exp; return this; }
		public AccessTokenParams setExpExpired() {
			this.exp = new Date(DateManager.getTimeMillis() - (1000*20));
			return this;
		}
		public AccessTokenParams withoutExp() { this.includeExp = false; return this; }
		public boolean isIncludeExp() { return this.includeExp; }

		public Date getNbf() { return this.nbf; }
		public AccessTokenParams setNbf(Date nbf) { this.nbf = nbf; return this; }
		public AccessTokenParams setNbfFuture() {
			this.nbf = new Date(DateManager.getTimeMillis() + (1000*60));
			return this;
		}
		public AccessTokenParams withoutNbf() { this.includeNbf = false; return this; }
		public boolean isIncludeNbf() { return this.includeNbf; }

		public String getJti() { return this.jti; }
		public AccessTokenParams setJti(String jti) { this.jti = jti; return this; }
		public AccessTokenParams withoutJti() { this.includeJti = false; return this; }
		public boolean isIncludeJti() { return this.includeJti; }

		public String getJkt() { return this.jkt; }
		public AccessTokenParams setJkt(String jkt) { this.jkt = jkt; return this; }
		public AccessTokenParams withoutCnf() { this.includeCnf = false; return this; }
		public boolean isIncludeCnf() { return this.includeCnf; }

		public String getScope() { return this.scope; }
		public AccessTokenParams setScope(String scope) { this.scope = scope; return this; }

		public String[] getRoles() { return this.roles; }
		public AccessTokenParams setRoles(String... roles) { this.roles = roles; return this; }

		public String getAlgorithm() { return this.algorithm; }
		public AccessTokenParams setAlgorithm(String algorithm) { this.algorithm = algorithm; return this; }
	}

	// ===== Metodi di utilità per generare JWK =====

	/**
	 * Crea un JsonWebKey dalla KeyPair RSA
	 */
	public static JsonWebKey createRsaJwk(KeyPair keyPair) {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		JsonWebKey jwk = new JsonWebKey();
		jwk.setKeyType(KeyType.RSA);
		jwk.setProperty("n", Base64Utilities.encodeBase64URLSafeString(toUnsignedByteArray(publicKey.getModulus())));
		jwk.setProperty("e", Base64Utilities.encodeBase64URLSafeString(toUnsignedByteArray(publicKey.getPublicExponent())));

		return jwk;
	}

	/**
	 * Crea un JsonWebKey dalla KeyPair EC
	 */
	public static JsonWebKey createEcJwk(KeyPair keyPair) {
		ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();

		JsonWebKey jwk = new JsonWebKey();
		jwk.setKeyType(KeyType.EC);
		jwk.setProperty("crv", "P-256");
		jwk.setProperty("x", Base64Utilities.encodeBase64URLSafeString(
				toFixedLengthUnsignedByteArray(publicKey.getW().getAffineX(), 32)));
		jwk.setProperty("y", Base64Utilities.encodeBase64URLSafeString(
				toFixedLengthUnsignedByteArray(publicKey.getW().getAffineY(), 32)));

		return jwk;
	}

	/**
	 * Converte un BigInteger in un array di byte senza segno (rimuove il leading zero se presente)
	 */
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

	/**
	 * Converte un BigInteger in un array di byte di lunghezza fissa (per EC)
	 */
	private static byte[] toFixedLengthUnsignedByteArray(java.math.BigInteger bigInt, int length) {
		byte[] bytes = toUnsignedByteArray(bigInt);
		if (bytes.length == length) {
			return bytes;
		} else if (bytes.length < length) {
			// Pad con zeri iniziali
			byte[] result = new byte[length];
			System.arraycopy(bytes, 0, result, length - bytes.length, bytes.length);
			return result;
		} else {
			// Tronca (non dovrebbe succedere)
			byte[] result = new byte[length];
			System.arraycopy(bytes, bytes.length - length, result, 0, length);
			return result;
		}
	}

	/**
	 * Calcola il JWK Thumbprint (RFC 7638) per il binding cnf/jkt
	 */
	public static String computeJwkThumbprint(JsonWebKey jwk) {
		return JwkUtils.getThumbprint(jwk);
	}

	/**
	 * Calcola l'access token hash (ath) come da RFC 9449 §4.3
	 * @throws UtilsException 
	 */
	public static String computeAccessTokenHash(String accessToken) throws UtilsException {
		byte[] hash = org.openspcoop2.utils.digest.DigestUtils.getDigestValue(
				accessToken.getBytes(java.nio.charset.StandardCharsets.US_ASCII),
				org.openspcoop2.utils.digest.DigestType.SHA256.getAlgorithmName());
		return Base64Utilities.encodeBase64URLSafeString(hash);
	}

	// ===== Metodi per costruire il token DPoP =====

	/**
	 * Costruisce un token DPoP con parametri di default
	 */
	public static String buildDPoPToken(String accessToken) throws Exception {
		DPoPTokenParams params = new DPoPTokenParams();
		if(accessToken != null) {
			params.setAth(computeAccessTokenHash(accessToken));
		}
		return buildDPoPToken(params);
	}

	/**
	 * Costruisce un token DPoP con parametri personalizzati
	 * @throws UtilsException 
	 * @throws URISyntaxException 
	 */
	public static String buildDPoPToken(DPoPTokenParams params) throws UtilsException, URISyntaxException {

		JSONUtils jsonUtils = JSONUtils.getInstance(false);

		// Build payload
		ObjectNode payload = jsonUtils.newObjectNode();

		if(params.isIncludeJti() && params.getJti() != null) {
			payload.put(params.getPrefix()+"jti", params.getJti());
		}
		if(params.isIncludeHtm() && params.getHtm() != null) {
			payload.put(params.getPrefix()+DPOP_HTTP_METHOD, params.getHtm());
		}
		if(params.isIncludeHtu() && params.getHtu() != null) {
			payload.put(params.getPrefix()+DPOP_HTTP_URI, normalizeHtu(params.getHtu()));
		}
		if(params.isIncludeIat() && params.getIat() != null) {
			payload.put(params.getPrefix()+"iat", params.getIat().getTime() / 1000);
		}
		if(params.isIncludeAth() && params.getAth() != null) {
			payload.put(params.getPrefix()+DPOP_ACCESS_TOKEN_HASH, params.getAth());
		}
		if(params.isIncludeNonce() && params.getNonce() != null) {
			payload.put(params.getPrefix()+"nonce", params.getNonce());
		}

		String payloadJson = jsonUtils.toString(payload);

		// Build JWK per l'header
		JsonWebKey jwk = params.getJwk();
		if(jwk == null && params.isIncludeJwk()) {
			if(isECAlgorithm(params.getAlgorithm())) {
				jwk = createEcJwk(params.getKeyPair());
			} else {
				jwk = createRsaJwk(params.getKeyPair());
			}
		}

		// Build JWS
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);

		JwtHeaders jwtHeaders = new JwtHeaders();
		if(params.getTyp() != null) {
			jwtHeaders.setType(params.getTyp());
		}

		// Aggiungi JWK all'header se richiesto
		if(params.isIncludeJwk() && jwk != null) {
			jwtHeaders.setJwKeyRaw(jwk.asMap());
		}

		// Crea JWKSet dalla KeyPair per la firma (supporta sia RSA che EC)
		String kid = UUID.randomUUID().toString();
		String jwkSetJson = JWKPrivateKeyConverter.convert(
				params.getKeyPair().getPublic(),
				params.getKeyPair().getPrivate(),
				kid, true, false);
		JWKSet jwkSet = new JWKSet(jwkSetJson);

		JsonSignature jsonSignature = new JsonSignature(
				jwkSet.getJsonWebKeys(),
				false, // secretKey
				kid,   // alias
				params.getAlgorithm(),
				jwtHeaders,
				options);

		return jsonSignature.sign(payloadJson);
	}

	// ===== Metodi per costruire l'Access Token con DPoP binding =====

	/**
	 * Costruisce un access token con DPoP binding (cnf/jkt) usando parametri di default
	 */
	public static String buildAccessToken(KeyPair dpopKeyPair) throws Exception {
		return buildAccessToken(dpopKeyPair, null);
	}

	/**
	 * Costruisce un access token con DPoP binding (cnf/jkt)
	 */
	public static String buildAccessToken(KeyPair dpopKeyPair, List<String> mapExpectedTokenInfo) throws Exception {
		AccessTokenParams params = new AccessTokenParams();

		// Calcola il JWK thumbprint dalla chiave pubblica DPoP
		JsonWebKey jwk;
		if(dpopKeyPair.getPublic() instanceof ECPublicKey) {
			jwk = createEcJwk(dpopKeyPair);
		} else {
			jwk = createRsaJwk(dpopKeyPair);
		}
		params.setJkt(computeJwkThumbprint(jwk));

		return buildAccessToken(params, mapExpectedTokenInfo);
	}

	/**
	 * Costruisce un access token con parametri personalizzati
	 * @throws UtilsException 
	 */
	public static String buildAccessToken(AccessTokenParams params, List<String> mapExpectedTokenInfo) throws UtilsException {

		JSONUtils jsonUtils = JSONUtils.getInstance(false);
		ObjectNode payload = jsonUtils.newObjectNode();

		// Standard claims
		if(params.isIncludeClientId() && params.getClientId() != null) {
			payload.put("client_id", params.getClientId());
			addToExpected(mapExpectedTokenInfo, "\"client_id\":\"" + params.getClientId() + "\"");
		}
		if(params.isIncludeIssuer() && params.getIssuer() != null) {
			payload.put("iss", params.getIssuer());
			addToExpected(mapExpectedTokenInfo, "\"iss\":\"" + params.getIssuer() + "\"");
		}
		if(params.isIncludeSubject() && params.getSubject() != null) {
			payload.put("sub", params.getSubject());
			addToExpected(mapExpectedTokenInfo, "\"sub\":\"" + params.getSubject() + "\"");
		}
		if(params.isIncludeAudience() && params.getAudience() != null) {
			payload.put("aud", params.getAudience());
			addToExpected(mapExpectedTokenInfo, "\"aud\":\"" + params.getAudience() + "\"");
		}
		if(params.isIncludeIat() && params.getIat() != null) {
			payload.put("iat", params.getIat().getTime() / 1000);
			addToExpected(mapExpectedTokenInfo, "\"iat\":\"" + (params.getIat().getTime() / 1000) + "\"");
		}
		if(params.isIncludeExp() && params.getExp() != null) {
			payload.put("exp", params.getExp().getTime() / 1000);
			addToExpected(mapExpectedTokenInfo, "\"exp\":\"" + (params.getExp().getTime() / 1000) + "\"");
		}
		if(params.isIncludeNbf() && params.getNbf() != null) {
			payload.put("nbf", params.getNbf().getTime() / 1000);
			addToExpected(mapExpectedTokenInfo, "\"nbf\":\"" + (params.getNbf().getTime() / 1000) + "\"");
		}
		if(params.isIncludeJti() && params.getJti() != null) {
			payload.put("jti", params.getJti());
			addToExpected(mapExpectedTokenInfo, "\"jti\":\"" + params.getJti() + "\"");
		}

		// DPoP binding - cnf/jkt (RFC 9449)
		if(params.isIncludeCnf() && params.getJkt() != null) {
			ObjectNode cnf = jsonUtils.newObjectNode();
			cnf.put("jkt", params.getJkt());
			payload.set("cnf", cnf);
			addToExpected(mapExpectedTokenInfo, "\"cnf.jkt\":\"" + params.getJkt() + "\"");
		}

		// Scope
		if(params.getScope() != null) {
			payload.put("scope", params.getScope());
			addToExpected(mapExpectedTokenInfo, "\"scope\":\"" + params.getScope() + "\"");
		}

		// Roles
		if(params.getRoles() != null && params.getRoles().length > 0) {
			com.fasterxml.jackson.databind.node.ArrayNode rolesArray = payload.putArray("role");
			for(String role : params.getRoles()) {
				rolesArray.add(role);
			}
		}

		String payloadJson = jsonUtils.toString(payload);

		if(mapExpectedTokenInfo != null) {
			mapExpectedTokenInfo.add("\"sourceType\":\"JWT\"");
		}

		// Sign token
		Properties props = new Properties();
		props.put("rs.security.keystore.type", "JKS");
		props.put("rs.security.keystore.file", "/etc/govway/keys/erogatore.jks");
		props.put("rs.security.keystore.alias", "erogatore");
		props.put("rs.security.keystore.password", "openspcoop");
		props.put("rs.security.key.password", "openspcoop");
		props.put("rs.security.signature.algorithm", params.getAlgorithm());
		props.put("rs.security.signature.include.cert", "false");
		props.put("rs.security.signature.include.key.id", "true");
		props.put("rs.security.signature.include.public.key", "false");
		props.put("rs.security.signature.include.cert.sha1", "false");
		props.put("rs.security.signature.include.cert.sha256", "false");

		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
		JsonSignature jsonSignature = new JsonSignature(props, options);

		return jsonSignature.sign(payloadJson);
	}

	// ===== Metodi per costruire coppie Access Token + DPoP Token =====

	/**
	 * Risultato contenente sia l'access token che il token DPoP
	 */
	public static class DPoPTokenPair {
		private final String accessToken;
		private final String dpopToken;
		private final KeyPair keyPair;
		private final JsonWebKey jwk;
		private final AccessTokenParams accessTokenParams;
		private final DPoPTokenParams dpopParams;

		public DPoPTokenPair(String accessToken, String dpopToken, KeyPair keyPair, JsonWebKey jwk,
				AccessTokenParams accessTokenParams, DPoPTokenParams dpopParams) {
			this.accessToken = accessToken;
			this.dpopToken = dpopToken;
			this.keyPair = keyPair;
			this.jwk = jwk;
			this.accessTokenParams = accessTokenParams;
			this.dpopParams = dpopParams;
		}

		public String getAccessToken() { return this.accessToken; }
		public String getDpopToken() { return this.dpopToken; }
		public KeyPair getKeyPair() { return this.keyPair; }
		public JsonWebKey getJwk() { return this.jwk; }
		public AccessTokenParams getAccessTokenParams() { return this.accessTokenParams; }
		public DPoPTokenParams getDpopParams() { return this.dpopParams; }
	}

	/**
	 * Costruisce una coppia Access Token + DPoP Token con parametri di default (RSA)
	 */
	public static DPoPTokenPair buildDPoPTokenPair(String httpMethod, String httpUri) throws Exception {
		return buildDPoPTokenPair(httpMethod, httpUri, null, null);
	}

	/**
	 * Costruisce una coppia Access Token + DPoP Token con parametri personalizzati
	 */
	public static DPoPTokenPair buildDPoPTokenPair(String httpMethod, String httpUri,
			AccessTokenParams accessTokenParams, DPoPTokenParams dpopParams) throws Exception {

		// Usa parametri di default se non specificati
		if(accessTokenParams == null) {
			accessTokenParams = new AccessTokenParams();
		}
		if(dpopParams == null) {
			dpopParams = new DPoPTokenParams();
		}

		// Usa la stessa KeyPair per entrambi
		KeyPair keyPair = dpopParams.getKeyPair() != null ? dpopParams.getKeyPair() : rsaKeyPair;

		// Crea JWK dalla chiave pubblica
		JsonWebKey jwk;
		if(keyPair.getPublic() instanceof ECPublicKey) {
			jwk = createEcJwk(keyPair);
		} else {
			jwk = createRsaJwk(keyPair);
		}

		// Imposta il JWK thumbprint nell'access token
		accessTokenParams.setJkt(computeJwkThumbprint(jwk));

		// Costruisce l'access token
		String accessToken = buildAccessToken(accessTokenParams, null);

		// Imposta i parametri del DPoP
		dpopParams.setHtm(httpMethod);
		dpopParams.setHtu(httpUri);
		dpopParams.setAth(computeAccessTokenHash(accessToken));
		dpopParams.setKeyPair(keyPair);
		dpopParams.setJwk(jwk);

		// Costruisce il token DPoP
		String dpopToken = buildDPoPToken(dpopParams);

		return new DPoPTokenPair(accessToken, dpopToken, keyPair, jwk,
				accessTokenParams, dpopParams);
	}

	/**
	 * Costruisce una coppia Access Token + DPoP Token usando chiavi EC
	 */
	public static DPoPTokenPair buildDPoPTokenPairEC(String httpMethod, String httpUri) throws Exception {
		DPoPTokenParams dpopParams = new DPoPTokenParams().useEC();
		return buildDPoPTokenPair(httpMethod, httpUri, null, dpopParams);
	}

	// ===== Utility methods =====

	private static boolean isECAlgorithm(String algorithm) {
		return "ES256".equals(algorithm) || "ES384".equals(algorithm) || "ES512".equals(algorithm);
	}

	private static String normalizeHtu(String url) throws URISyntaxException {
		java.net.URI uri = new java.net.URI(url);
		return new java.net.URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null).toString();
	}

	private static void addToExpected(List<String> list, String value) {
		if(list != null) {
			list.add(value);
		}
	}

	/**
	 * Restituisce la KeyPair RSA di default per i test
	 */
	public static KeyPair getDefaultRsaKeyPair() {
		return rsaKeyPair;
	}

	/**
	 * Restituisce la KeyPair EC di default per i test
	 */
	public static KeyPair getDefaultEcKeyPair() {
		return ecKeyPair;
	}
}
