/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.pdd.core.token.parser.BasicNegoziazioneTokenParser;
import org.openspcoop2.pdd.core.token.parser.INegoziazioneTokenParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaimsNegoziazione;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;

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
	
	
	public INegoziazioneTokenParser getNegoziazioneTokenParser() throws Exception {
		INegoziazioneTokenParser parser = null;
		TipologiaClaimsNegoziazione tipologiaClaims = TipologiaClaimsNegoziazione.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PARSER_TYPE));
		if(TipologiaClaims.CUSTOM.equals(tipologiaClaims)) {
			String className = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PARSER_CLASS_NAME);
			parser = (INegoziazioneTokenParser) ClassLoaderUtilities.newInstance(className);
		}
		else{
			parser = new BasicNegoziazioneTokenParser(tipologiaClaims);
		}
		return parser;
	}
	
	public String getEndpoint() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_URL);
	}
	
	public boolean isEndpointHttps() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_ENDPOINT_HTTPS_STATO);	
	}
	public boolean isHttpsAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_SSL_STATO);	
	}
	
	public boolean isBasicAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_STATO);	
	}
	public String getBasicAuthentication_username() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_USERNAME);
	}
	public String getBasicAuthentication_password() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD);
	}
	public boolean isBasicAuthenticationAsAuthorizationHeader() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_AS_AUTHORIZATION_HEADER);	
	}
	
	public boolean isBearerAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_STATO);	
	}
	public String getBeareAuthentication_token() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN);
	}
	
	public boolean isClientCredentialsGrant() throws ProviderException, ProviderValidationException{
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		return Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL.equals(mode);
	}
	public boolean isRfc7523_x509_Grant() throws ProviderException, ProviderValidationException{
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		return Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509.equals(mode);
	}
	public boolean isRfc7523_clientSecret_Grant() throws ProviderException, ProviderValidationException{
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		return Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET.equals(mode);
	}
	public boolean isUsernamePasswordGrant() throws ProviderException, ProviderValidationException{
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		return Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD.equals(mode);
	}
	public String getLabelGrant() throws ProviderException, ProviderValidationException {
		if(this.isClientCredentialsGrant()) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL_LABEL;
		}
		else if(this.isUsernamePasswordGrant()) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD_LABEL;
		}
		else if(this.isRfc7523_x509_Grant()) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509_LABEL;
		}
		else if(this.isRfc7523_clientSecret_Grant()) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET_LABEL;
		}
		return "Non definita";
	} 
	public boolean isPDND() throws ProviderException, ProviderValidationException{
		String mode = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE_PDND);
		return "true".equalsIgnoreCase(mode);
	}
	
	public String getUsernamePasswordGrant_username() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_USERNAME);
	}
	public String getUsernamePasswordGrant_password() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PASSWORD);
	}
	
	public String getScopeString() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_SCOPES);
	}
	public List<String> getScopes(NegoziazioneTokenDynamicParameters dynamicParameters) throws DynamicException{
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
	public String getFormClientId() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORM_CLIENT_ID);
	}
	public String getFormParameters() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORM_PARAMETERS);
	}
	
	public boolean isSaveErrorInCache() throws ProviderException, ProviderValidationException{
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
	
	public String getJwtClientId() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLIENT_ID);
	}
	public String getJwtClientSecret() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLIENT_SECRET);
	}
	public String getJwtIssuer() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_ISSUER);
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
		return tmp!=null ? Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_ALIAS.equals(tmp) : false;
	}
	public boolean isJwtSignIncludeKeyIdWithClientId() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID);
		return tmp!=null ? Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_CLIENT_ID.equals(tmp) : false;
	}
	public boolean isJwtSignIncludeKeyIdCustom() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID);
		return tmp!=null ? Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_CUSTOM.equals(tmp) : false;
	}
	public String getJwtSignIncludeKeyIdCustom() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_VALUE);
	}
	public boolean isJwtSignIncludeX509Cert() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_CERT);
		return tmp!=null ? Boolean.valueOf(tmp) : false;
	}
	public String getJwtSignIncludeX509URL() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_URL);
	}
	public boolean isJwtSignIncludeX509CertSha1() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_SHA1);
		return tmp!=null ? Boolean.valueOf(tmp) : false;
	}
	public boolean isJwtSignIncludeX509CertSha256() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_SHA256);
		return tmp!=null ? Boolean.valueOf(tmp) : false;
	}
	public boolean isJwtSignJoseContentType() {
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_JOSE_CONTENT_TYPE);
		return tmp!=null ? Boolean.valueOf(tmp) : false;
	}
	public String getJwtSignJoseType() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_JOSE_TYPE);
	}
	public String getJwtSignKeystoreType() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_TYPE);
	}
	public String getJwtSignKeystoreFile() {
		return this.defaultProperties.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_FILE);
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

}
