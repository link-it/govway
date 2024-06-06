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


package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.token.parser.BasicNegoziazioneTokenParser;
import org.openspcoop2.pdd.core.token.parser.INegoziazioneTokenParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaimsNegoziazione;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**     
 * PolicyGestioneToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyNegoziazioneToken extends AbstractPolicyToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public INegoziazioneTokenParser getNegoziazioneTokenParser() throws TokenException {
		INegoziazioneTokenParser parser = null;
		TipologiaClaimsNegoziazione tipologiaClaims = TipologiaClaimsNegoziazione.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PARSER_TYPE));
		
		if(this.isCustomGrant()) {
			String customParser = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM);
			if(customParser!=null && StringUtils.isNotEmpty(customParser)) {
				tipologiaClaims = TipologiaClaimsNegoziazione.valueOf(customParser);
			}
		}
		
		if(TipologiaClaimsNegoziazione.CUSTOM.equals(tipologiaClaims)) {
			return getNegoziazioneTokenCustomParser();
		}
		else{
			parser = new BasicNegoziazioneTokenParser(tipologiaClaims, TokenUtilities.getRetrieveResponseClaimsMappingProperties(this.properties));
		}
		return parser;
	}
	private INegoziazioneTokenParser getNegoziazioneTokenCustomParser() throws TokenException {
		INegoziazioneTokenParser parser = null;
		String className = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PARSER_CLASS_NAME);
		if(className!=null && StringUtils.isNotEmpty(className) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(className)) {
			try {
				parser = (INegoziazioneTokenParser) ClassLoaderUtilities.newInstance(className);
			}catch(Exception e) {
				throw new TokenException(e.getMessage(),e);
			}
		}
		else {
			String tipo = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PARSER_PLUGIN_TYPE);
			if(tipo!=null && StringUtils.isNotEmpty(tipo) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(tipo)) {
		    	try{
					PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
					parser = pluginLoader.newTokenNegoziazione(tipo);
				}catch(Exception e){
					throw new TokenException(e.getMessage(),e); // descrizione errore già corretta
				}
			}
			else {
				throw new TokenException("Deve essere selezionato un plugin per il 'Formato Risposta'");
			}
		}
		return parser;
	}
	
	public String getEndpoint() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_URL);
	}
	
	public boolean isEndpointHttps() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_ENDPOINT_HTTPS_STATO) 
				|| isHttpsAuthentication(); // anche solo se è abilitato httpsAuthentication, di fatto è abilitato https	
	}
	public boolean isHttpsAuthentication() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_SSL_STATO);	
	}
	
	public boolean isBasicAuthentication() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_STATO);	
	}
	public String getBasicAuthenticationUsername() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_USERNAME);
	}
	public String getBasicAuthenticationPassword() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD);
	}
	public boolean isBasicAuthenticationAsAuthorizationHeader() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_AS_AUTHORIZATION_HEADER);	
	}
	
	public boolean isBearerAuthentication() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_STATO);	
	}
	public String getBeareAuthenticationToken() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN);
	}
	
	public boolean isClientCredentialsGrant() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		return Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL.equals(mode);
	}
	public boolean isRfc7523x509Grant() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		return Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509.equals(mode);
	}
	public boolean isRfc7523ClientSecretGrant() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		return Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET.equals(mode);
	}
	public boolean isUsernamePasswordGrant() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		return Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD.equals(mode);
	}
	public boolean isCustomGrant() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		return Costanti.ID_RETRIEVE_TOKEN_METHOD_CUSTOM.equals(mode);
	}
	public String getLabelGrant() {
		if(this.isClientCredentialsGrant()) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL_LABEL;
		}
		else if(this.isUsernamePasswordGrant()) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD_LABEL;
		}
		else if(this.isRfc7523x509Grant()) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509_LABEL;
		}
		else if(this.isRfc7523ClientSecretGrant()) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET_LABEL;
		}
		else if(this.isCustomGrant()) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_CUSTOM_LABEL;
		}
		return "Non definita";
	} 
	public boolean isPDND() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE_PDND);
		return "true".equalsIgnoreCase(mode);
	}
	
	public String getUsernamePasswordGrantUsername() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_USERNAME);
	}
	public String getUsernamePasswordGrantPassword() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PASSWORD);
	}
	
	public String getScopeString() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_SCOPES);
	}
	public List<String> getScopes(NegoziazioneTokenDynamicParameters dynamicParameters) {
		List<String> l = new ArrayList<>();
		String scopes = dynamicParameters.getScope();
		if(scopes!=null) {
			if(scopes.contains(",")) {
				String [] tmp = scopes.split(",");
				for (String s : tmp) {
					l.add(s.trim());
				}
			}
			else {
				l.add(scopes.trim());
			}
		}
		return l;
	}
	public String getAudience() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUDIENCE);
	}
	public boolean isFormClientIdApplicativoModI() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORM_CLIENT_ID_MODE);
		return mode!=null && Costanti.CHOICE_APPLICATIVO_MODI_VALUE.equals(mode);
	}
	public boolean isFormClientIdFruizioneModI() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORM_CLIENT_ID_MODE);
		return mode!=null && Costanti.CHOICE_FRUIZIONE_MODI_VALUE.equals(mode);
	}
	public String getFormClientId() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORM_CLIENT_ID);
	}
	public String getFormResource() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORM_RESOURCE);
	}
	public String getFormParameters() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORM_PARAMETERS);
	}
	
	public String getHttpMethod() {
		if(this.isCustomGrant()) {
			String httpMethod = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_HTTP_METHOD);
			if(httpMethod!=null && StringUtils.isNotEmpty(httpMethod)) {
				return httpMethod;
			}
			return HttpRequestMethod.GET.name();
		}
		return HttpRequestMethod.POST.name();
	}
	public String getHttpContentType() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_HTTP_CONTENT_TYPE);
	}
	public String getHttpHeaders() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_HTTP_HEADERS);
	}
	
	public String getDynamicPayloadType() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE);
	}
	public boolean isDynamicPayloadTemplate() {
		return Costanti.POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE_TEMPLATE.equals(this.getDynamicPayloadType());
	}
	public boolean isDynamicPayloadFreemarkerTemplate() {
		return Costanti.POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE_FREEMARKER_TEMPLATE.equals(this.getDynamicPayloadType());
	}
	public boolean isDynamicPayloadVelocityTemplate() {
		return Costanti.POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE_VELOCITY_TEMPLATE.equals(this.getDynamicPayloadType());
	}
	public String getDynamicPayload() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD);
	}
	
	public boolean isSaveErrorInCache() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_RETRIEVE_TOKEN_SAVE_ERROR_IN_CACHE);	
	}
	
	
	public String getForwardTokenMode() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE);
	}
	public String getForwardTokenModeCustomHeader() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER_NAME);
	}
	public String getForwardTokenModeCustomUrl() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL_PARAMETER_NAME);
	}
	
	public boolean isJwtClientIdApplicativoModI() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLIENT_ID_MODE);
		return mode!=null && Costanti.CHOICE_APPLICATIVO_MODI_VALUE.equals(mode);
	}
	public boolean isJwtClientIdFruizioneModI() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLIENT_ID_MODE);
		return mode!=null && Costanti.CHOICE_FRUIZIONE_MODI_VALUE.equals(mode);
	}
	public String getJwtClientId() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLIENT_ID);
	}
	public String getJwtClientSecret() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLIENT_SECRET);
	}
	public boolean isJwtIssuerApplicativoModI() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_ISSUER_MODE);
		return mode!=null && Costanti.CHOICE_APPLICATIVO_MODI_VALUE.equals(mode);
	}
	public boolean isJwtIssuerFruizioneModI() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_ISSUER_MODE);
		return mode!=null && Costanti.CHOICE_FRUIZIONE_MODI_VALUE.equals(mode);
	}
	public String getJwtIssuer() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_ISSUER);
	}
	public boolean isJwtSubjectApplicativoModI() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SUBJECT_MODE);
		return mode!=null && Costanti.CHOICE_APPLICATIVO_MODI_VALUE.equals(mode);
	}
	public boolean isJwtSubjectFruizioneModI() {
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SUBJECT_MODE);
		return mode!=null && Costanti.CHOICE_FRUIZIONE_MODI_VALUE.equals(mode);
	}
	public String getJwtSubject() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SUBJECT);
	}
	public String getJwtIdentifier() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_IDENTIFIER);
	}
	public String getJwtAudience() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_AUDIENCE);
	}
	public Integer getJwtTtlSeconds() {
		String ttl = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS);
		if(ttl==null || StringUtils.isEmpty(ttl)) {
			ttl = Costanti.POLICY_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS_DEFAULT_VALUE;
		}
		return Integer.valueOf(ttl);
	}
	public String getJwtPurposeId() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_PURPOSE_ID);
	}
	public String getJwtSessionInfo() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SESSION_INFO);
	}
	public String getJwtClaims() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIMS);
	}
	public String getJwtSignAlgorithm() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_ALGORITHM);
	}
	public boolean isJwtSignIncludeKeyIdWithKeyAlias() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID);
		return tmp!=null && Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_ALIAS.equals(tmp);
	}
	public boolean isJwtSignIncludeKeyIdWithClientId() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID);
		return tmp!=null && Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_CLIENT_ID.equals(tmp);
	}
	public boolean isJwtSignIncludeKeyIdApplicativoModI() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID);
		return tmp!=null && Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_APPLICATIVO_MODI.equals(tmp);
	}
	public boolean isJwtSignIncludeKeyIdFruizioneModI() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID);
		return tmp!=null && Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_FRUIZIONE_MODI.equals(tmp);
	}
	public boolean isJwtSignIncludeKeyIdCustom() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID);
		return tmp!=null && Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_CUSTOM.equals(tmp);
	}
	public String getJwtSignIncludeKeyIdCustom() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_VALUE);
	}
	public boolean isJwtSignIncludeX509Cert() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_CERT);
		return tmp!=null && Boolean.valueOf(tmp);
	}
	public String getJwtSignIncludeX509URL() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_URL);
	}
	public boolean isJwtSignIncludeX509CertSha1() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_SHA1);
		return tmp!=null && Boolean.valueOf(tmp);
	}
	public boolean isJwtSignIncludeX509CertSha256() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_SHA256);
		return tmp!=null && Boolean.valueOf(tmp);
	}
	public boolean isJwtSignJoseContentType() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_JOSE_CONTENT_TYPE);
		return tmp!=null && Boolean.valueOf(tmp);
	}
	public String getJwtSignJoseType() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_JOSE_TYPE);
	}
	public boolean isJwtSignKeystoreApplicativoModI() {
		return Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE.equals(getJwtSignKeystoreType());
	}
	public boolean isJwtSignKeystoreFruizioneModI() {
		return Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE.equals(getJwtSignKeystoreType());
	}
	public String getJwtSignKeystoreType() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_TYPE);
	}
	public String getJwtSignKeystoreFile() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_FILE);
	}
	public String getJwtSignKeystoreFilePublicKey() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_FILE_PUBLIC_KEY);
	}
	public String getJwtSignKeyPairAlgorithm() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYPAIR_ALGORITHM);
	}
	public String getJwtSignKeystorePassword() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_PASSWORD);
	}
	public String getJwtSignKeyAlias() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_ALIAS);
	}
	public String getJwtSignKeyPassword() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_PASSWORD);
	}
	public String getJwtSignKeystoreByokPolicy() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_BYOK_POLICY);
	}

}
