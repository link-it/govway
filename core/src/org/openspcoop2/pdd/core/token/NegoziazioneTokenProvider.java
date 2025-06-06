/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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


package org.openspcoop2.pdd.core.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.constants.ItemType;
import org.openspcoop2.core.mvc.properties.provider.ExternalResources;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderInfo;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.core.token.parser.ClaimsNegoziazione;
import org.openspcoop2.pdd.core.token.parser.INegoziazioneTokenParser;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.utils.AbstractSecurityProvider;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.certificate.byok.BYOKProvider;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.OCSPProvider;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**     
 * GetTokenProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NegoziazioneTokenProvider implements IProvider {

	private OCSPProvider ocspProvider;
	private BYOKProvider byokProvider;

	public NegoziazioneTokenProvider() {
		this.ocspProvider = new OCSPProvider();
		try {
			this.byokProvider = BYOKProvider.getUnwrapInstance();
		}catch(Exception e) {
			throw new UtilsRuntimeException(e.getMessage(),e);
		}
	}
	
	
	
	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		
		Properties pDefault = TokenUtilities.getDefaultProperties(mapProperties);
			
		boolean endpointSSL = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_ENDPOINT_HTTPS_STATO);
		boolean ssl = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_SSL_STATO);
		
		if(endpointSSL || ssl) {
			validateEndpointSsl(mapProperties);
		}
		
		validateUrl(pDefault);
		
		String retMode = validateRetrieveMode(pDefault);
		
		boolean pdnd = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_RETRIEVE_TOKEN_MODE_PDND);
			
					
		boolean basic = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_STATO);
		if(basic) {
			validateBasicCredentials(pDefault);
		}
		
		boolean bearer = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_STATO);
		if(bearer) {
			String token = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN);
			InputValidationUtils.validateTextAreaInput(token, "Token Endpoint - Autenticazione Client - Token");
		}
		
		if(ssl) {
			validateSslCredentials(mapProperties);
		}
		
		boolean proxy = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_ENDPOINT_PROXY_STATO);
		if(proxy) {
			validateProxy(mapProperties);
		}
		
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509.equals(retMode) ||
				Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET.equals(retMode)) {
			validateJWTSigned(pDefault, retMode, pdnd);
		}
		
		validateFormParameters(pDefault, pdnd);
		
		validateCustomFormParameters(pDefault, pdnd);
		
		validateHeaders(pDefault);
				
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD_CUSTOM.equals(retMode)) {
			validateTokenParser(pDefault);
		}
		
		validateForward(pDefault);
		
	}
	private void validateEndpointSsl(Map<String, Properties> mapProperties) throws ProviderValidationException {
		Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
		if(p==null || p.size()<=0) {
			throw new ProviderValidationException("Nonostante sia stato indicato un endpoint 'https', non è stata fornita una configurazione dei parametri ssl da utilizzare");
		}
		
		String trustAllCerts = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS);
		boolean trustAll = false;
		if(trustAllCerts!=null && StringUtils.isNotEmpty(trustAllCerts)) {
			trustAll = Boolean.valueOf(trustAllCerts);
		}
		
		if(!trustAll) {
			String location = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			InputValidationUtils.validateTextAreaInput(location, "Https - Autenticazione Server - File (TrustStore per l'autenticazione server)");
			
			String algo = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITHM);
			if(algo==null || "".equals(algo)) {
				throw new ProviderValidationException("Indicare un algoritmo per l'autenticazione server");
			}
			if(algo.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nell'algoritmo per l'autenticazione server");
			}
			
			String locationCRL = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLS);
			if(locationCRL!=null && !"".equals(locationCRL)) {
				InputValidationUtils.validateTextAreaInput(locationCRL, "Https - Autenticazione Server - CRL File(s)");
			}
		}
	}
	private void validateUrl(Properties pDefault) throws ProviderValidationException {
		String url = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_URL);
		InputValidationUtils.validateTextAreaInput(url, "Token Endpoint - URL");
		try{
			org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(url, true);
		}catch(Exception e){
			throw new ProviderValidationException("La URL fornita non è valida: "+e.getMessage());
		}	
	}
	private String validateRetrieveMode(Properties pDefault) throws ProviderValidationException {
		String retMode = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD.equals(retMode)) {
			String username = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_USERNAME);
			if(username==null || "".equals(username)) {
				throw new ProviderValidationException("Non è stato fornito l'username");
			}
			if(username.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel'username");
			}
			String password = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PASSWORD);
			if(password==null || "".equals(password)) {
				throw new ProviderValidationException("Non è stato fornita una password");
			}
			if(password.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nella password");
			}
		}
		return retMode;
	}
	private void validateBasicCredentials(Properties pDefault) throws ProviderValidationException {
		String username = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_USERNAME);
		if(username==null || "".equals(username)) {
			throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornito un 'Client ID' da utilizzare durante la connessione verso il servizio");
		}
		if(username.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel 'Client ID'");
		}
		String password = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD);
		if(password==null || "".equals(password)) {
			throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornita un 'Client Secret' da utilizzare durante la connessione verso il servizio");
		}
		if(password.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel 'Client Secret'");
		}
	}
	private void validateSslCredentials(Map<String, Properties> mapProperties) throws ProviderValidationException {
		Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
		if(p==null || p.size()<=0) {
			throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Https', non sono stati forniti i parametri di connessione ssl client da utilizzare verso il servizio");
		}
		
		String location = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
		if(location!=null && !"".equals(location)) {
			InputValidationUtils.validateTextAreaInput(location, "Https - Autenticazione Client - File (KeyStore per l'autenticazione client)");
		}
		
		String algo = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITHM);
		if(algo==null || "".equals(algo)) {
			throw new ProviderValidationException("Indicare un algoritmo per l'autenticazione client");
		}
		if(algo.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nell'algoritmo per l'autenticazione client");
		}
	}
	private void validateProxy(Map<String, Properties> mapProperties) throws ProviderValidationException {
		Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_CONFIG);
		if(p==null || p.size()<=0) {
			throw new ProviderValidationException("Nonostante sia richiesta un proxy, non sono stati forniti i parametri di connessione");
		}
		
		String hostname = p.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
		if(hostname==null || "".equals(hostname)) {
			throw new ProviderValidationException("Indicare un hostname per il Proxy");
		}
		if(hostname.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nell'hostname del Proxy");
		}
		
		String username = p.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME);
		if(username!=null && !"".equals(username) &&
			username.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nell'username del Proxy");
		}
		
		String password = p.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD);
		if(password!=null && !"".equals(password) &&
			password.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nella password del Proxy");
		}
	}
	private void validateJWTSigned(Properties pDefault, String retMode, boolean pdnd) throws ProviderValidationException {
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509.equals(retMode)) {
			validateJWTSignedx509(pDefault);
		}
		else {
			/**
			// Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET.equals(retMode)) {
		
			// Una password può contenere gli spazi, i controlli di inizio e fine vengono gia' fatti dal framework
			
//			String clientSecret = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLIENT_SECRET);
//			if(clientSecret!=null && clientSecret.contains(" ")) {
//				throw new ProviderValidationException("Non indicare spazi nel campo 'Client Secret'");
//			}
			*/
		}
		
		// NOTA: i controlli seguenti di inizio e fine, vengono fatti gia' in automatico dal framework
		
		validateJWTSignedHeader(pDefault);
							
		validateJWTSignedPayload(pDefault, pdnd);
		
	}
	private void validateJWTSignedx509(Properties pDefault) throws ProviderValidationException {
		validateJWTSignedx509Keystore(pDefault);
		validateJWTSignedx509Key(pDefault);
	}
	private void validateJWTSignedx509Keystore(Properties pDefault) throws ProviderValidationException {
		String file = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_FILE);
		InputValidationUtils.validateTextAreaInput(file, "Token Endpoint - JWT KeyStore - File");
		
		String fileChiavePubblica = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_FILE_PUBLIC_KEY);
		if(fileChiavePubblica!=null && StringUtils.isNotEmpty(fileChiavePubblica)) {
			InputValidationUtils.validateTextAreaInput(file, "Token Endpoint - JWT KeyStore - Chiave Pubblica");
		}
		
		// NOTA: i controlli seguenti di inizio e fine, vengono fatti gia' in automatico dal framework
		String p = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_PASSWORD);
		if(p!=null) {
			if(p.startsWith(" ")) {
				throw new ProviderValidationException("Il valore indicato nel campo 'JWT KeyStore - Password', non deve iniziare con uno spazio");
			}
			if(p.endsWith(" ")) {
				throw new ProviderValidationException("Il valore indicato nel campo 'JWT KeyStore - Password', non deve terminare con uno spazio");
			}
		}
	}
	private void validateJWTSignedx509Key(Properties pDefault) throws ProviderValidationException {
		
		// NOTA: i controlli seguenti di inizio e fine, vengono fatti gia' in automatico dal framework
		String p = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_ALIAS);
		if(p!=null) {
			if(p.startsWith(" ")) {
				throw new ProviderValidationException("Il valore indicato nel campo 'JWT KeyStore - Alias Chiave Privata', non deve iniziare con uno spazio");
			}
			if(p.endsWith(" ")) {
				throw new ProviderValidationException("Il valore indicato nel campo 'JWT KeyStore - Alias Chiave Privata', non deve terminare con uno spazio");
			}
		}
		
		p = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_PASSWORD);
		if(p!=null) {
			if(p.startsWith(" ")) {
				throw new ProviderValidationException("Il valore indicato nel campo 'JWT KeyStore - Password Chiave Privata', non deve iniziare con uno spazio");
			}
			if(p.endsWith(" ")) {
				throw new ProviderValidationException("Il valore indicato nel campo 'JWT KeyStore - Password Chiave Privata', non deve terminare con uno spazio");
			}
		}
	}
	private void validateJWTSignedHeader(Properties pDefault) throws ProviderValidationException {
		String kidMode = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID);
		if(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_CUSTOM.equals(kidMode)) {
			String kidValue = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_VALUE);
			if(kidValue!=null) {
				if(kidValue.startsWith(" ")) {
					throw new ProviderValidationException("Il valore indicato nel campo 'JWT Header - Key Id (kid)', non deve iniziare con uno spazio");
				}
				if(kidValue.endsWith(" ")) {
					throw new ProviderValidationException("Il valore indicato nel campo 'JWT Header - Key Id (kid)', non deve terminare con uno spazio");
				}
			}
		}
		
		String type = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_JOSE_TYPE);
		if(type!=null && type.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel campo 'JWT Header - Type (typ)'");
		}
		
		String x5u = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_URL);
		if(x5u!=null && !"".equals(x5u)) {
			InputValidationUtils.validateTextAreaInput(x5u, "Token Endpoint - JWT Header - URL");
		}
	}
	private void validateJWTSignedPayload(Properties pDefault, boolean pdnd) throws ProviderValidationException {
		String clientId = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLIENT_ID);
		if(clientId!=null && clientId.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel campo 'JWT Payload - Client ID'");
		}
		
		String audience = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_AUDIENCE);
		if(audience!=null && audience.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel campo 'JWT Payload - Audience'");
		}
		
		String issuer = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_ISSUER);
		if(issuer!=null && issuer.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel campo 'JWT Payload - Issuer'");
		}
		
		String subject = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SUBJECT);
		if(subject!=null && subject.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel campo 'JWT Payload - Subject'");
		}
		
		String jti = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_IDENTIFIER);
		if(jti!=null && jti.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel campo 'JWT Payload - Identifier'");
		}
		
		if(pdnd) {
			validateJWTSignedPayloadPDND(pDefault);			
		}
		
		validateJWTSignedClaims(pDefault, pdnd);
	}
	private void validateJWTSignedPayloadPDND(Properties pDefault) throws ProviderValidationException {
		String purposeId = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_PURPOSE_ID);
		if(purposeId!=null && purposeId.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel campo 'JWT Payload - Purpose ID'");
		}
		
		String session = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SESSION_INFO);
		if(session!=null && !"".equals(session)) {
			Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(session);
			List<String> deny = new ArrayList<>();
			TokenUtilities.checkClaims("claim", convertTextToProperties, "Informazioni Sessione", deny, false);
		}
	}
	private void validateJWTSignedClaims(Properties pDefault, boolean pdnd) throws ProviderValidationException {
		String claims = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIMS);
		if(claims!=null && !"".equals(claims)) {
			Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(claims);
			List<String> deny = new ArrayList<>();
			deny.add(Claims.OIDC_ID_TOKEN_ISSUER);
			deny.add(Claims.OIDC_ID_TOKEN_SUBJECT);
			deny.add(Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID);
			deny.add(Claims.OIDC_ID_TOKEN_AUDIENCE);
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT);
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE);
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED);
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID);
			if(pdnd) {
				deny.add(Costanti.PDND_PURPOSE_ID);
				deny.add(Costanti.PDND_SESSION_INFO);
			}
			TokenUtilities.checkClaims("claim", convertTextToProperties, "Claims", deny, false);
		}
	}
	private void validateFormParameters(Properties pDefault, boolean pdnd) throws ProviderValidationException {
		String scopes = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_SCOPES);
		if(scopes!=null && !"".equals(scopes) &&
			scopes.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi tra gli scope forniti");
		}
		
		String audience = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUDIENCE);
		if(audience!=null && !"".equals(audience) &&
			audience.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel valore dell'audience");
		}
		
		if(pdnd) {
			String clientId = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORM_CLIENT_ID);
			if(clientId!=null && clientId.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel campo 'Client ID'");
			}
			
			String resource = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORM_RESOURCE);
			if(resource!=null && resource.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel campo 'Resource'");
			}
		}
	}
	private void validateCustomFormParameters(Properties pDefault, boolean pdnd) throws ProviderValidationException {
		String parameters = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORM_PARAMETERS);
		if(parameters!=null && !"".equals(parameters)) {
			Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(parameters);
			List<String> deny = new ArrayList<>();
			deny.add(ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE);
			deny.add(ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION_TYPE);
			deny.add(ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION);
			deny.add(ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_SCOPE);
			deny.add(ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_AUDIENCE);
			if(pdnd) {
				deny.add(Costanti.PDND_OAUTH2_RFC_6749_REQUEST_CLIENT_ID);
			}
			TokenUtilities.checkClaims("parametro", convertTextToProperties, "Parametri", deny, false);
		}	
	}
	private void validateHeaders(Properties pDefault) throws ProviderValidationException {
		String headers = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_HTTP_HEADERS);
		if(headers!=null && !"".equals(headers)) {
			Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(headers);
			List<String> deny = new ArrayList<>();
			deny.add(HttpConstants.AUTHORIZATION);
			deny.add(HttpConstants.CONTENT_TYPE);
			TokenUtilities.checkClaims("header http", convertTextToProperties, "Header HTTP", deny, false);
		}
	}
	private void validateTokenParser(Properties pDefault) throws ProviderValidationException {
		String parser = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM);
		if(parser!=null && Costanti.POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM_CYSTOM.equals(parser)) {
			String pluginType = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PARSER_PLUGIN_TYPE);
			validateTokenParser(pDefault, pluginType);
		}
	}
	private void validateTokenParser(Properties pDefault, String pluginType) throws ProviderValidationException {
		if(pluginType!=null && StringUtils.isNotEmpty(pluginType) && CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(pluginType)) {
			String className = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PARSER_CLASS_NAME);
			if(CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(className)) {
				throw new ProviderValidationException("Deve essere selezionato un plugin per il 'Formato Risposta'");	
			}
			else {
				if(className==null || "".equals(className)) {
					throw new ProviderValidationException("Non è stata fornita la classe del parser dei claims della risposta");
				}
				if(className.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nella classe del parser dei claims della risposta");
				}
			}
		}
	}
	private void validateForward(Properties pDefault) throws ProviderValidationException {
		String mode = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE);
		if(mode==null) {
			throw new ProviderValidationException("Nessuna modalità di forward indicata");
		}
		if(!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_HEADER.equals(mode) &&
				!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_URL.equals(mode) &&
				!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER.equals(mode) &&
				!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL.equals(mode)
				) {
			throw new ProviderValidationException("La modalità di forward indicata '"+mode+"' non è supportata");	
		}
		if(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER.equals(mode)) {
			String hdr = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER_NAME);
			if(hdr==null || "".equals(hdr)) {
				throw new ProviderValidationException("La modalità di forward indicata prevede l'indicazione del nome di un header http");
			}
			if(hdr.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel nome dell'header HTTP indicato per la modalità di forward");
			}
		}
		else if(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL.equals(mode)) {
			String urlP = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL_PARAMETER_NAME);
			if(urlP==null || "".equals(urlP)) {
				throw new ProviderValidationException("La modalità di forward indicata prevede l'indicazione del nome di una proprietà della url");
			}
			if(urlP.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel nome della proprietà della url indicata per la modalità di forward");
			}
		}
	}


	@Override
	public List<String> getValues(String id) throws ProviderException {
		return this.getValues(id, null);
	}
	@Override
	public List<String> getValues(String id, ExternalResources externalResources) throws ProviderException{
		List<String> values = null;
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD.equals(id)) {
			List<String> methodsList = new ArrayList<>();
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL);
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD);
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509);
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET);
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_CUSTOM);
			return methodsList;
		}
		else if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
			List<String> tipologie = null;
			try{
				tipologie = SSLUtilities.getSSLSupportedProtocols();
			}catch(Exception e){
				tipologie = SSLUtilities.getAllSslProtocol();
			}
			return tipologie;
		}
		else if(Costanti.ID_RETRIEVE_TOKEN_JWT_SYMMETRIC_SIGN_ALGORITHM.equals(id) ||
				Costanti.ID_RETRIEVE_TOKEN_JWT_ASYMMETRIC_SIGN_ALGORITHM.equals(id)) {
			return getValuesSignatureAlgorithm(id);
		}
		else if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE.equals(id) || 
				Costanti.ID_HTTPS_TRUSTSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id)) {
			return getStoreType(id,true);
		}
		else if(Costanti.ID_RETRIEVE_HTTP_METHOD.equals(id)) {
			List<String> methodsList = new ArrayList<>();
			HttpRequestMethod [] methods = HttpRequestMethod.values();
			for (int i = 0; i < methods.length; i++) {
				methodsList.add(methods[i].name());
			}
			return methodsList;
		}
		else if(Costanti.ID_NEGOZIAZIONE_CUSTOM_PARSER_PLUGIN_CHOICE.equals(id)) {
			return TokenUtilities.getTokenPluginValues(externalResources, TipoPlugin.TOKEN_NEGOZIAZIONE);
		}
		else if(Costanti.ID_HTTPS_TRUSTSTORE_OCSP_POLICY.equals(id)) {
			return this.ocspProvider.getValues();
		}
		else if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_BYOK_POLICY.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_BYOK_POLICY.equals(id)) {
			return this.byokProvider.getValues();
		}
		return values;
	}
	private List<String> getValuesSignatureAlgorithm(String id) {
		List<String> l = new ArrayList<>();
		org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm [] tmp = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.values();
		for (int i = 0; i < tmp.length; i++) {
			if(org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.NONE.equals(tmp[i])) {
				continue;
			}
			if(Costanti.ID_RETRIEVE_TOKEN_JWT_SYMMETRIC_SIGN_ALGORITHM.equals(id)) {
				if(tmp[i].name().toLowerCase().startsWith("hs")) {
					l.add(tmp[i].name());
				}
			}
			else {
				if(!tmp[i].name().toLowerCase().startsWith("hs")) {
					l.add(tmp[i].name());
				}
			}
		}
		return l;
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		return this.getLabels(id, null);
	}
	@Override
	public List<String> getLabels(String id, ExternalResources externalResources) throws ProviderException{
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD.equals(id)) {
			List<String> methodsList = new ArrayList<>();
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL_LABEL);
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD_LABEL);
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509_LABEL);
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET_LABEL);
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_CUSTOM_LABEL);
			return methodsList;
		}
		else if(Costanti.ID_RETRIEVE_TOKEN_JWT_SYMMETRIC_SIGN_ALGORITHM.equals(id) ||
				Costanti.ID_RETRIEVE_TOKEN_JWT_ASYMMETRIC_SIGN_ALGORITHM.equals(id)) {
			return getLabelsSignatureAlgorithm(id);
		}
		else if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE.equals(id) || 
				Costanti.ID_HTTPS_TRUSTSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id)) {
			return getStoreType(id,false);
		}
		else if(Costanti.ID_NEGOZIAZIONE_CUSTOM_PARSER_PLUGIN_CHOICE.equals(id)) {
			return TokenUtilities.getTokenPluginLabels(externalResources, TipoPlugin.TOKEN_NEGOZIAZIONE);
		}
		else if(Costanti.ID_HTTPS_TRUSTSTORE_OCSP_POLICY.equals(id)) {
			return this.ocspProvider.getLabels();
		}
		else if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_BYOK_POLICY.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_BYOK_POLICY.equals(id)) {
			return this.byokProvider.getLabels();
		}
		return this.getValues(id); // torno uguale ai valori negli altri casi
	}
	private List<String> getLabelsSignatureAlgorithm(String id) throws ProviderException{
		List<String> l = this.getValues(id);
		List<String> labels = new ArrayList<>();
		for (String value : l) {
			if(value.contains("_")) {
				String t = "" + value;
				while(t.contains("_")) {
					t = t.replace("_", "-");
				}
				labels.add(t);
			}
			else {
				labels.add(value);
			}
		}
		return labels;
	}
	
	private static boolean secret = false;
	public static boolean isSecret() {
		return secret;
	}
	public static void setSecret(boolean secret) {
		NegoziazioneTokenProvider.secret = secret;
	}

	private List<String> getStoreType(String id,boolean value){
		boolean trustStore = true;
		List<String> l = new ArrayList<>();
		l.add(value ? SecurityConstants.KEYSTORE_TYPE_JKS_VALUE : SecurityConstants.KEYSTORE_TYPE_JKS_LABEL);
		l.add(value ? SecurityConstants.KEYSTORE_TYPE_PKCS12_VALUE : SecurityConstants.KEYSTORE_TYPE_PKCS12_LABEL);
		if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE.equals(id)) {
			l.add(value ? SecurityConstants.KEYSTORE_TYPE_JWK_VALUE: SecurityConstants.KEYSTORE_TYPE_JWK_LABEL);
			l.add(value ? SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE: SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_LABEL);
		}
/**		if(Costanti.UNKNOWN.equals(id)) {
//			l.add(value ? SecurityConstants.KEYSTORE_TYPE_JCEKS_VALUE : SecurityConstants.KEYSTORE_TYPE_JCEKS_LABEL);
//			secret = true;
//		}
		**/
		if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE.equals(id)) {
			l.add(value ? Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE: Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_LABEL);
			l.add(value ? Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE: Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_LABEL);
		}
		
		if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id)) {
			trustStore = false;
		}
		HSMUtils.fillTipologieKeystore(trustStore, false, l);
		
		if(secret) {
			// aggiunto info mancanti come secret
			addStoreTypeSecret(l);
		}
		return l;
	}
	private void addStoreTypeSecret(List<String> l) {
		List<String> lSecret = new ArrayList<>();
		HSMUtils.fillTipologieKeystore(false, true, lSecret);
		if(!lSecret.isEmpty()) {
			for (String type : lSecret) {
				if(!l.contains(type)) {
					l.add(type);
				}
			}
		}
	}
	

	@Override
	public String getDefault(String id) throws ProviderException {
		return getDefault(id, null);
	}
	@Override
	public String getDefault(String id, ExternalResources externalResources) throws ProviderException {
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD.equals(id)) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL;
		}
		else if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
			return SSLUtilities.getSafeDefaultProtocol();
		}
		else if(Costanti.ID_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS.equals(id)) {
			return Costanti.POLICY_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS_DEFAULT_VALUE;
		}
		else if(Costanti.ID_RETRIEVE_TOKEN_JWT_SYMMETRIC_SIGN_ALGORITHM.equals(id)) {
			return org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.HS256.name();
		}
		else if(Costanti.ID_RETRIEVE_TOKEN_JWT_ASYMMETRIC_SIGN_ALGORITHM.equals(id)) {
			return org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.RS256.name();
		}
		else if(Costanti.ID_RETRIEVE_HTTP_METHOD.equals(id)) {
			return HttpRequestMethod.GET.name();
		}
		else if(Costanti.ID_RETRIEVE_JWT_PURPOSE_ID.equals(id)) {
			return Costanti.VALORE_RETRIEVE_JWT_PURPOSE_ID_DEFAULT;
		}
		return null;
	}
	
	private boolean isProviderInfoStandard(String id) {
		return 
			Costanti.ID_RETRIEVE_ENDPOINT_URL.equals(id) ||
			Costanti.ID_RETRIEVE_AUTENTICAZIONE_USERNAME.equals(id) ||
			Costanti.ID_RETRIEVE_AUTENTICAZIONE_PASSWORD.equals(id) ||
			Costanti.ID_RETRIEVE_CLIENT_ID.equals(id) ||
			Costanti.ID_RETRIEVE_CLIENT_ID_CUSTOM.equals(id) ||
			Costanti.ID_RETRIEVE_CLIENT_SECRET.equals(id) ||
			Costanti.ID_RETRIEVE_CLIENT_SECRET_CUSTOM.equals(id) ||
			Costanti.ID_RETRIEVE_BEARER_TOKEN.equals(id) ||
			Costanti.ID_RETRIEVE_JWT_X5U.equals(id) ||
			Costanti.ID_RETRIEVE_JWT_KID_VALUE.equals(id) ||
			Costanti.ID_RETRIEVE_SCOPE.equals(id) ||
			Costanti.ID_RETRIEVE_AUDIENCE.equals(id) ||
			Costanti.ID_RETRIEVE_FORM_PARAMETERS.equals(id) ||
			Costanti.ID_RETRIEVE_HTTP_CONTENT_TYPE.equals(id) ||
			Costanti.ID_RETRIEVE_HTTP_HEADERS.equals(id) ||
			Costanti.ID_RETRIEVE_HTTP_TEMPLATE_PAYLOAD.equals(id);
	}
	@Override
	public ProviderInfo getProviderInfo(String id) throws ProviderException{
		if(isProviderInfoStandard(id)) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI);
			return pInfo;
		}
		else if(Costanti.ID_RETRIEVE_JWT_ISSUER.equals(id) ||
				Costanti.ID_RETRIEVE_JWT_ISSUER_APPLICATIVO_MODI_CUSTOM.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_ISSUER);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO);
			return pInfo;
		}
		else if(Costanti.ID_RETRIEVE_JWT_SUBJECT.equals(id) ||
				Costanti.ID_RETRIEVE_JWT_SUBJECT_APPLICATIVO_MODI_CUSTOM.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_SUBJECT);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO);
			return pInfo;
		}
		else if(Costanti.ID_RETRIEVE_JWT_IDENTIFIER.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_IDENTIFIER);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO);
			return pInfo;
		}
		else if(Costanti.ID_RETRIEVE_JWT_CLIENT_ID.equals(id) ||
				Costanti.ID_RETRIEVE_JWT_CLIENT_ID_APPLICATIVO_MODI_CUSTOM.equals(id) ||
				Costanti.ID_RETRIEVE_JWT_AUDIENCE.equals(id) ||
				Costanti.ID_RETRIEVE_FORM_RESOURCE.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO);
			return pInfo;
		}
		else if(Costanti.ID_RETRIEVE_JWT_PURPOSE_ID.equals(id)) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_PURPOSE_ID);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO);
			return pInfo;
		}
		else if(Costanti.ID_RETRIEVE_FORM_CLIENT_ID.equals(id) ||
				Costanti.ID_RETRIEVE_FORM_CLIENT_ID_APPLICATIVO_MODI_CUSTOM.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_FORM_PARAMETRO_CLIENT_ID);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI_CON_OPZIONE_VALORE_NON_DEFINITO);
			return pInfo;
		}
		else if(Costanti.ID_RETRIEVE_JWT_CLAIMS.equals(id) ||
				Costanti.ID_RETRIEVE_JWT_SESSION_INFO.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_CLAIMS);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_VALORI);
			return pInfo;
		}
		else if(Costanti.ID_RETRIEVE_HTTP_FREEMARKER_PAYLOAD.equals(id)) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_OBJECT_VALORI);
			return pInfo;
		}
		else if(Costanti.ID_RETRIEVE_HTTP_VELOCITY_PAYLOAD.equals(id)) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_NEGOZIAZIONE_TOKEN_INFO_OBJECT_VALORI);
			return pInfo;
		}
		else if(Costanti.ID_NEGOZIAZIONE_CUSTOM_PARSER_PLUGIN_CLASSNAME.equals(id)) {
			return getProviderInfoCustomParserPluginClassname();
		}
		
		return null;
	}
	private ProviderInfo getProviderInfoCustomParserPluginClassname() {
		ProviderInfo pInfo = new ProviderInfo();
		pInfo.setHeaderBody(DynamicHelperCostanti.PLUGIN_CLASSNAME_INFO_SINGOLA);
		pInfo.setListBody(new ArrayList<>());
		pInfo.getListBody().add(INegoziazioneTokenParser.class.getName());
		return pInfo;
	}
	
	@Override
	public String dynamicUpdate(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		return dynamicUpdate(items, mapNameValue, item, actualValue, null);
	}
	@Override
	public String dynamicUpdate(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue, ExternalResources externalResources) {
	
		if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_FILE_PRIVATE_KEY.equals(item.getName()) ||
				Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_FILE_PUBLIC_KEY.equals(item.getName()) ||
				Costanti.ID_HTTPS_TRUSTSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_FILE.equals(item.getName())) {
			return dynamicUpdateStoreFile(items, mapNameValue, item, actualValue);
		}
		else if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_HTTPS_TRUSTSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_PASSWORD.equals(item.getName())) {
			return dynamicUpdateStorePassword(items, mapNameValue, item, actualValue);
		}
		else if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName()) ||
				Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_PASSWORD_PRIVATE_KEY_OPZIONALE.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName()) ) {
			return dynamicUpdateStoreKeyPassword(items, mapNameValue, item, actualValue);	
		}
		else if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_ALIAS_PRIVATE_KEY.equals(item.getName()) ) {
			return dynamicUpdateStoreKeyAlias(items, mapNameValue, item, actualValue);
		}		
		else if(Costanti.ID_NEGOZIAZIONE_CUSTOM_PARSER_PLUGIN_CHOICE.equals(item.getName())) {
			return TokenUtilities.dynamicUpdateTokenPluginChoice(externalResources, TipoPlugin.TOKEN_NEGOZIAZIONE, item, actualValue);
		}
		else if(Costanti.ID_NEGOZIAZIONE_CUSTOM_PARSER_PLUGIN_CLASSNAME.equals(item.getName())) {
			return TokenUtilities.dynamicUpdateTokenPluginClassName(externalResources, TipoPlugin.TOKEN_NEGOZIAZIONE, 
					items, mapNameValue, item, 
					Costanti.ID_NEGOZIAZIONE_CUSTOM_PARSER_PLUGIN_CHOICE, actualValue);			
		}
		else if(Costanti.ID_HTTPS_TRUSTSTORE_OCSP_POLICY.equals(item.getName())) {
			if(!this.ocspProvider.isOcspEnabled()) {
				item.setValue("");
				item.setType(ItemType.HIDDEN);
			}
		}
		else if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_BYOK_POLICY.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_BYOK_POLICY.equals(item.getName())) {
			return dynamicUpdateByok(items, mapNameValue, item, actualValue);
		}
		
		return actualValue;
	}
	private String dynamicUpdateStoreFile(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		String type = Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE;
		if(Costanti.ID_HTTPS_TRUSTSTORE_FILE.equals(item.getName())) {
			type = Costanti.ID_HTTPS_TRUSTSTORE_TYPE;
		}
		else if(Costanti.ID_HTTPS_KEYSTORE_FILE.equals(item.getName())) {
			type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
		}
		
		if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE.equals(type)) {
			String typeValue = AbstractSecurityProvider.readValue(type, items, mapNameValue);
			if(Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE.equals(typeValue) || Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE.equals(typeValue)) {
				item.setValue(typeValue);
				item.setType(ItemType.HIDDEN);
				return item.getValue();
			}
		}
		
		return AbstractSecurityProvider.processStoreFile(type, items, mapNameValue, item, actualValue);
	}
	private String dynamicUpdateStorePassword(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		String type = Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE;
		boolean keystore = true;
		if(Costanti.ID_HTTPS_TRUSTSTORE_PASSWORD.equals(item.getName())) {
			type = Costanti.ID_HTTPS_TRUSTSTORE_TYPE;
			keystore = false;
		}
		else if(Costanti.ID_HTTPS_KEYSTORE_PASSWORD.equals(item.getName())) {
			type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
		}
		
		if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE.equals(type)) {
			String typeValue = AbstractSecurityProvider.readValue(type, items, mapNameValue);
			if(Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE.equals(typeValue) || Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE.equals(typeValue)) {
				item.setValue("-");
				item.setType(ItemType.HIDDEN);
				return item.getValue();
			}
		}
		
		return AbstractSecurityProvider.processStorePassword(keystore, type, items, mapNameValue, item, actualValue);
	}
	private String dynamicUpdateStoreKeyPassword(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		String type = Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE;
		if(Costanti.ID_HTTPS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName())) {
			type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
		}
		
		if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE.equals(type)) {
			String typeValue = AbstractSecurityProvider.readValue(type, items, mapNameValue);
			if(Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE.equals(typeValue) || Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE.equals(typeValue)) {
				item.setValue("-");
				item.setType(ItemType.HIDDEN);
				return item.getValue();
			}
		}
		
		return AbstractSecurityProvider.processStoreKeyPassword(type, items, mapNameValue, item, actualValue);
	}
	private String dynamicUpdateStoreKeyAlias(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		String type = Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE;
		
		String typeValue = AbstractSecurityProvider.readValue(type, items, mapNameValue);
		if(Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE.equals(typeValue) || Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE.equals(typeValue)) {
			item.setValue("-");
			item.setType(ItemType.HIDDEN);
			return item.getValue();
		}
		else {
			item.setType(ItemType.TEXT);
			return actualValue;
		}
	}
	private String dynamicUpdateByok(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {		
		if(!this.byokProvider.isByokEnabled()) {
			item.setValue("");
			item.setType(ItemType.HIDDEN);
			return actualValue;
		}
		else {
			return dynamicUpdateByokPolicy(items, mapNameValue, item, actualValue);
		}
	}
	private String dynamicUpdateByokPolicy(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		String type = null;
		if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_BYOK_POLICY.equals(item.getName())) {
			type = Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE;
		}
		else if(Costanti.ID_HTTPS_KEYSTORE_BYOK_POLICY.equals(item.getName())) {
			type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
		}
		
		if(Costanti.ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE.equals(type)) {
			String typeValue = AbstractSecurityProvider.readValue(type, items, mapNameValue);
			if(Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE.equals(typeValue) || Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE.equals(typeValue)) {
				item.setValue(typeValue);
				item.setType(ItemType.HIDDEN);
				return item.getValue();
			}
		}
		
		return AbstractSecurityProvider.processStoreByokPolicy(type, items, mapNameValue, item, actualValue);
	}
}
