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


package org.openspcoop2.pdd.core.token.attribute_authority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
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
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.utils.AbstractSecurityProvider;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.OCSPProvider;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**     
 * GetTokenProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttributeAuthorityProvider implements IProvider {
	
	private OCSPProvider ocspProvider;

	public AttributeAuthorityProvider() {
		this.ocspProvider = new OCSPProvider();
	}
	
	@Override
	public void validateId(String name) throws ProviderException, ProviderValidationException {	
		if(name==null || StringUtils.isEmpty(name)) {
			throw new ProviderValidationException("Deve essere indicato un nome che identifica la policy per verso l'Attribute Authority");
		}
		if(name.contains(" ")) {
			throw new ProviderValidationException("Il nome associato alla policy non deve contenere spazi");
		}
		boolean match = false;
		try {
			match = RegularExpressionEngine.isMatch(name,"^[_A-Za-z][\\-_A-Za-z0-9]*$"); 
		}catch(Exception e) {
			throw new ProviderException(e.getMessage(),e);
		}
		if (!match) {
			throw new ProviderValidationException("Il nome associato alla policy può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , e '-'");
		}
	}
	
	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		
		Properties pDefault = TokenUtilities.getDefaultProperties(mapProperties);
			
		boolean endpointSSL = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_ENDPOINT_HTTPS_STATO);
		boolean ssl = TokenUtilities.isEnabled(pDefault, org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_SSL_CLIENT_STATO);
		
		if(endpointSSL || ssl) {
			validateEndpointSsl(mapProperties);
		}
		
		String url = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_URL);
		InputValidationUtils.validateTextAreaInput(url, "Endpoint - URL");
		try{
			org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(url, true);
		}catch(Exception e){
			throw new ProviderValidationException("La URL fornita non è valida: "+e.getMessage());
		}	
		
					
		boolean basic = TokenUtilities.isEnabled(pDefault, org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_BASIC_STATO);
		if(basic) {
			validateBasicCredentials(pDefault);
		}
		
		boolean bearer = TokenUtilities.isEnabled(pDefault, org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_BEARER_STATO);
		if(bearer) {
			String token = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_BEARER_TOKEN);
			InputValidationUtils.validateTextAreaInput(token, "Endpoint - Autenticazione Client - Token");
		}
		
		if(ssl) {
			validateSslCredentials(mapProperties);
		}
		
		boolean proxy = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_ENDPOINT_PROXY_STATO);
		if(proxy) {
			validateProxy(mapProperties);
		}
		
		validateClaims(pDefault);
		
		String jwtKeystore = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_REQUEST_JWT_SIGN_KEYSTORE_FILE);
		if(jwtKeystore!=null && StringUtils.isNotEmpty(jwtKeystore)) {
			InputValidationUtils.validateTextAreaInput(jwtKeystore, "Richiesta - JWS KeyStore - File");
		}
		
		String jwtKeystorePublicKey = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_REQUEST_JWT_SIGN_KEYSTORE_FILE_PUBLIC);
		if(jwtKeystorePublicKey!=null && StringUtils.isNotEmpty(jwtKeystorePublicKey)) {
			InputValidationUtils.validateTextAreaInput(jwtKeystorePublicKey, "Richiesta - JWS KeyStore - Chiave Pubblica");
		}
		
		String requestX5U = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_REQUEST_JWT_SIGN_INCLUDE_X509_URL);
		if(requestX5U!=null && StringUtils.isNotEmpty(requestX5U)) {
			InputValidationUtils.validateTextAreaInput(requestX5U, "Richiesta - JWS Header - URL");
		}
		
		String mode = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_RESPONSE_TYPE);
		
		if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_RESPONSE_TYPE_VALUE_JWS.equals(mode) ) {
			validateResponseTypeJWS(mapProperties, pDefault);
		}
		
		if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_RESPONSE_TYPE_VALUE_CUSTOM.equals(mode) ) {
			validateResponseTypeCustom(pDefault);
		}
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
	private void validateBasicCredentials(Properties pDefault) throws ProviderValidationException {
		String username = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_BASIC_USERNAME);
		if(username==null || "".equals(username)) {
			throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornito un 'Username' da utilizzare durante la connessione verso il servizio");
		}
		if(username.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nel 'Username'");
		}
		String password = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_BASIC_PASSWORD);
		if(password==null || "".equals(password)) {
			throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornita una 'Password' da utilizzare durante la connessione verso il servizio");
		}
		if(password.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nella 'Password'");
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
	private void validateClaims(Properties pDefault) throws ProviderValidationException {
		String claims = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_REQUEST_JWT_CLAIMS);
		if(claims!=null && !"".equals(claims)) {
			Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(claims);
			List<String> deny = new ArrayList<>();
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUER);
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_SUBJECT);
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE);
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT);
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE);
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED);
			deny.add(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID);
			TokenUtilities.checkClaims("claim", convertTextToProperties, "Claims", deny, false);
		}
	}
	private void validateResponseTypeJWS(Map<String, Properties> mapProperties, Properties pDefault) throws ProviderValidationException {
		Properties p = mapProperties.get(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID);
		if(p==null || p.size()<=0) {
			throw new ProviderValidationException("La validazione di una risposta JWS richiede una configurazione del TrustStore; configurazione non riscontrata");
		}
		
		if(!p.containsKey(RSSecurityConstants.RSSEC_KEY_STORE) && !p.containsKey(JoseConstants.RSSEC_KEY_STORE_JWKSET)) {
			// altrimenti è stato fatto inject del keystore
			String file = p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
			InputValidationUtils.validateTextAreaInput(file, "Risposta - TrustStore - File");
		}
		
		String crl = pDefault.getProperty(SecurityConstants.SIGNATURE_CRL);
		if(crl!=null && StringUtils.isNotEmpty(crl)) {
			InputValidationUtils.validateTextAreaInput(crl, "Risposta - TrustStore - CRL File(s)");
		}
	}
	private void validateResponseTypeCustom(Properties pDefault) throws ProviderValidationException {
		String pluginType = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_RESPONSE_PARSER_PLUGIN_TYPE);
		if(pluginType!=null && StringUtils.isNotEmpty(pluginType) && CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(pluginType)) {
			String className = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_RESPONSE_PARSER_CLASS_NAME);
			if(CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(className)) {
				throw new ProviderValidationException("Deve essere selezionato un plugin per la risposta");	
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

	@Override
	public List<String> getValues(String id) throws ProviderException {
		return this.getValues(id, null);
	}
	@Override
	public List<String> getValues(String id, ExternalResources externalResources) throws ProviderException{
		List<String> values = null;
		if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_SIGNATURE_ALGORITHM.equals(id)) {
			return getValuesSignatureAlgorithm();
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
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE.equals(id) || 
				Costanti.ID_AA_JWS_KEYSTORE_TYPE.equals(id) || 
				Costanti.ID_HTTPS_TRUSTSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id)) {
			return getStoreType(id,true);
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE_SELECT_CERTIFICATE.equals(id)) {
			return Costanti.getIdValidazioneJwtTruststoreTypeSelectCertificateValues();
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY.equals(id)) {
			return Costanti.getIdValidazioneJwtTruststoreTypeSelectJwkPublicKeyValues();
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_OCSP_POLICY.equals(id)) {
			return this.ocspProvider.getValues();
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_PARSER_TOKEN_CUSTOM_PLUGIN_CHOICE.equals(id)) {
			return TokenUtilities.getTokenPluginValues(externalResources, TipoPlugin.ATTRIBUTE_AUTHORITY);
		}
		return values;
	}
	private List<String> getValuesSignatureAlgorithm() {
		List<String> l = new ArrayList<>();
		org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm [] tmp = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.values();
		for (int i = 0; i < tmp.length; i++) {
			if(org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.NONE.equals(tmp[i])) {
				continue;
			}
			if(!tmp[i].name().toLowerCase().startsWith("hs")) {
				l.add(tmp[i].name());
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
		if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_SIGNATURE_ALGORITHM.equals(id)) {
			return getLabelsSignatureAlgorithm(id);
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE.equals(id) || 
				Costanti.ID_AA_JWS_KEYSTORE_TYPE.equals(id) || 
				Costanti.ID_HTTPS_TRUSTSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id)) {
			return getStoreType(id,false);
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE_SELECT_CERTIFICATE.equals(id)) {
			return Costanti.getIdValidazioneJwtTruststoreTypeSelectCertificateLabels();
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY.equals(id)) {
			return Costanti.getIdValidazioneJwtTruststoreTypeSelectJwkPublicKeyLabels();
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_OCSP_POLICY.equals(id)) {
			return this.ocspProvider.getLabels();
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_PARSER_TOKEN_CUSTOM_PLUGIN_CHOICE.equals(id)) {
			return TokenUtilities.getTokenPluginLabels(externalResources, TipoPlugin.ATTRIBUTE_AUTHORITY);
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
		AttributeAuthorityProvider.secret = secret;
	}

	private List<String> getStoreType(String id,boolean value){
		boolean trustStore = true;
		List<String> l = new ArrayList<>();
		l.add(value ? SecurityConstants.KEYSTORE_TYPE_JKS_VALUE : SecurityConstants.KEYSTORE_TYPE_JKS_LABEL);
		l.add(value ? SecurityConstants.KEYSTORE_TYPE_PKCS12_VALUE : SecurityConstants.KEYSTORE_TYPE_PKCS12_LABEL);
		if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE.equals(id) || 
				Costanti.ID_AA_JWS_KEYSTORE_TYPE.equals(id)) {
			l.add(value ? SecurityConstants.KEYSTORE_TYPE_JWK_VALUE: SecurityConstants.KEYSTORE_TYPE_JWK_LABEL);
		}
		if(Costanti.ID_AA_JWS_KEYSTORE_TYPE.equals(id)) {
			l.add(value ? SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE: SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_LABEL);
		}
		if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE.equals(id)) {
			l.add(value ? SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE: SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_LABEL);
		}
/**		if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE.equals(id)) {
//			l.add(value ? SecurityConstants.KEYSTORE_TYPE_JCEKS_VALUE : SecurityConstants.KEYSTORE_TYPE_JCEKS_LABEL);
//			secret = true;
//		} */
		
		if(Costanti.ID_AA_JWS_KEYSTORE_TYPE.equals(id) ||
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
	public String getNote(String id, String actualValue) throws ProviderException{
		if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE_SELECT_CERTIFICATE.equals(id)) {
			if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_ALIAS.equals(actualValue)) {
				return Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_ALIAS;
			}
			else if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C.equals(actualValue)) {
				return Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5C;
			}
			else if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256.equals(actualValue)) {
				return Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5T256;
			}
			else if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256.equals(actualValue)) {
				return Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5C_X5T256;
			}
			else if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_KID.equals(actualValue)) {
				return Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_KID;
			}
			else if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5U.equals(actualValue)) {
				return Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5U;
			}
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY.equals(id)) {
			if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUE_ALIAS.equals(actualValue)) {
				return Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_NOTE_ALIAS;
			}
			else if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUE_KID.equals(actualValue)) {
				return Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_NOTE_KID;
			}
		}
		return null;
	}
	
	@Override
	public String getDefault(String id) throws ProviderException {
		return getDefault(id, null);
	}
	@Override
	public String getDefault(String id, ExternalResources externalResources) throws ProviderException {
		if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_SIGNATURE_ALGORITHM.equals(id)) {
			return org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.RS256.name();
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_TOKEN_JWT_EXPIRED_TTL_SECONDS.equals(id)) {
			return Costanti.POLICY_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS_DEFAULT_VALUE;
		}
		else if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
			return SSLUtilities.getSafeDefaultProtocol();
		}
		return null;
	}
	
	@Override
	public ProviderInfo getProviderInfo(String id) throws ProviderException{
		if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_ENDPOINT_URL.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_AUTENTICAZIONE_ENDPOINT_BASIC_USERNAME.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_AUTENTICAZIONE_ENDPOINT_BASIC_PASSWORD.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_AUTENTICAZIONE_ENDPOINT_BEARER_TOKEN.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_ISSUER.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_SUBJECT.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_AUDIENCE.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RISPOSTA_JWS_PAYLOAD_AUDIENCE.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI);
			return pInfo;
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_PAYLOAD_TEMPLATE.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_CON_REQUIRED_ATTRIBUTES);
			return pInfo;
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_CLAIMS.equals(id)
			) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_CLAIMS);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_CON_REQUIRED_ATTRIBUTES);
			return pInfo;
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE_FREEMARKER.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_PAYLOAD_TEMPLATE_FREEMARKER.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI);
			return pInfo;
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE_VELOCITY.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_PAYLOAD_TEMPLATE_VELOCITY.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI);
			return pInfo;
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_PARSER_TOKEN_CUSTOM_PLUGIN_CLASSNAME.equals(id)) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.PLUGIN_CLASSNAME_INFO_SINGOLA);
			pInfo.setListBody(new ArrayList<>());
			pInfo.getListBody().add(IRetrieveAttributeAuthorityResponseParser.class.getName());
			return pInfo;
		}
		
		
		return null;
	}
	
	@Override
	public String dynamicUpdate(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		return dynamicUpdate(items, mapNameValue, item, actualValue, null);
	}
	@Override
	public String dynamicUpdate(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue, ExternalResources externalResources) {
	
		if(Costanti.ID_AA_JWS_TRUSTSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_AA_JWS_KEYSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_HTTPS_TRUSTSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_FILE.equals(item.getName())) {
			dynamicUpdateStoreFile(items, mapNameValue, item, actualValue);
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_AA_JWS_KEYSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_HTTPS_TRUSTSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_PASSWORD.equals(item.getName())) {
			dynamicUpdateStorePassword(items, mapNameValue, item, actualValue);	
		}
		else if(Costanti.ID_AA_JWS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName()) ) {
			
			String type = Costanti.ID_AA_JWS_KEYSTORE_TYPE;
			if(Costanti.ID_HTTPS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName())) {
				type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
			}
			
			return AbstractSecurityProvider.processStoreKeyPassword(type, items, mapNameValue, item, actualValue);
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_OCSP_POLICY.equals(item.getName())) {
			if(!this.ocspProvider.isOcspEnabled()) {
				item.setValue("");
				item.setType(ItemType.HIDDEN);
			}
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_PARSER_TOKEN_CUSTOM_PLUGIN_CHOICE.equals(item.getName())) {
			return TokenUtilities.dynamicUpdateTokenPluginChoice(externalResources, TipoPlugin.ATTRIBUTE_AUTHORITY, item, actualValue);
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_PARSER_TOKEN_CUSTOM_PLUGIN_CLASSNAME.equals(item.getName())) {
			return TokenUtilities.dynamicUpdateTokenPluginClassName(externalResources, TipoPlugin.ATTRIBUTE_AUTHORITY, 
					items, mapNameValue, item, 
					org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_PARSER_TOKEN_CUSTOM_PLUGIN_CHOICE, actualValue);			
		}
		
		return actualValue;
	}
	public String dynamicUpdateStoreFile(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		String type = Costanti.ID_AA_JWS_TRUSTSTORE_TYPE;
		if(Costanti.ID_AA_JWS_KEYSTORE_FILE.equals(item.getName())) {
			type = Costanti.ID_AA_JWS_KEYSTORE_TYPE;
		}
		else if(Costanti.ID_HTTPS_TRUSTSTORE_FILE.equals(item.getName())) {
			type = Costanti.ID_HTTPS_TRUSTSTORE_TYPE;
		}
		else if(Costanti.ID_HTTPS_KEYSTORE_FILE.equals(item.getName())) {
			type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
		}
		
		return AbstractSecurityProvider.processStoreFile(type, items, mapNameValue, item, actualValue);
	}
	public String dynamicUpdateStorePassword(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		String type = Costanti.ID_AA_JWS_TRUSTSTORE_TYPE;
		if(Costanti.ID_AA_JWS_KEYSTORE_PASSWORD.equals(item.getName())) {
			type = Costanti.ID_AA_JWS_KEYSTORE_TYPE;
		}
		else if(Costanti.ID_HTTPS_TRUSTSTORE_PASSWORD.equals(item.getName())) {
			type = Costanti.ID_HTTPS_TRUSTSTORE_TYPE;
		}
		else if(Costanti.ID_HTTPS_KEYSTORE_PASSWORD.equals(item.getName())) {
			type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
		}
		
		return AbstractSecurityProvider.processStorePassword(type, items, mapNameValue, item, actualValue);
	}

}
