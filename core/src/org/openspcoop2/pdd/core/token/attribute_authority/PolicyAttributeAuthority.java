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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.token.AbstractPolicyToken;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**     
 * PolicyAttributeAuthority
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyAttributeAuthority extends AbstractPolicyToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public IRetrieveAttributeAuthorityResponseParser getRetrieveAttributeAuthorityResponseParser(Logger log) throws TokenException {
		IRetrieveAttributeAuthorityResponseParser parser = null;
		TipologiaResponseAttributeAuthority tipologiaResponse = TipologiaResponseAttributeAuthority.valueOf(this.defaultProperties.getProperty(Costanti.AA_RESPONSE_TYPE));
		if(TipologiaResponseAttributeAuthority.custom.equals(tipologiaResponse)) {
			String className = this.defaultProperties.getProperty(Costanti.AA_RESPONSE_PARSER_CLASS_NAME);
			if(className!=null && StringUtils.isNotEmpty(className) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(className)) {
				try {
					parser = (IRetrieveAttributeAuthorityResponseParser) ClassLoaderUtilities.newInstance(className);
				}catch(Exception e) {
					throw new TokenException(e.getMessage(),e);
				}
			}
			else {
				String tipo = this.defaultProperties.getProperty(Costanti.AA_RESPONSE_PARSER_PLUGIN_TYPE);
				if(tipo!=null && StringUtils.isNotEmpty(tipo) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(tipo)) {
			    	try{
						PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
						parser = pluginLoader.newAttributeAuthority(tipo);
					}catch(Exception e){
						throw new TokenException(e.getMessage(),e); // descrizione errore già corretta
					}
				}
				else {
					throw new TokenException("Deve essere selezionato un plugin per la risposta");
				}
			}
		}
		else {
			String claims = this.defaultProperties.getProperty(Costanti.AA_RESPONSE_ATTRIBUTES);
			List<String> attributesClaims = new ArrayList<>();
			if(claims!=null && !"".equals(claims)) {
				if(claims.contains(",")) {
					String [] tmp = claims.split(",");
					if(tmp!=null && tmp.length>0) {
						for (int i = 0; i < tmp.length; i++) {
							String claim = tmp[i];
							if(claim!=null && !"".equals(claim)) {
								attributesClaims.add(claim.trim());
							}
						}
					}
				}
				else {
					attributesClaims.add(claims.trim());
				}
			}
			parser = new BasicRetrieveAttributeAuthorityResponseParser(this.getName(), log, tipologiaResponse, attributesClaims);
		}
		return parser;
	}
	
	public boolean isSaveErrorInCache() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.AA_SAVE_ERROR_IN_CACHE);	
	}
	
	public String getEndpoint() {
		return this.defaultProperties.getProperty(Costanti.AA_URL);
	}
	
	public boolean isEndpointHttps() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.AA_AUTH_SSL_STATO);	
	}
	public boolean isHttpsAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.AA_AUTH_SSL_CLIENT_STATO);	
	}
	
	public boolean isBasicAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.AA_AUTH_BASIC_STATO);	
	}
	public String getBasicAuthenticationUsername() {
		return this.defaultProperties.getProperty(Costanti.AA_AUTH_BASIC_USERNAME);
	}
	public String getBasicAuthenticationPassword() {
		return this.defaultProperties.getProperty(Costanti.AA_AUTH_BASIC_PASSWORD);
	}
	
	public boolean isBearerAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.AA_AUTH_BEARER_STATO);	
	}
	public String getBeareAuthenticationToken() {
		return this.defaultProperties.getProperty(Costanti.AA_AUTH_BEARER_TOKEN);
	}
	
	public String getRequestPosition() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_POSITION);
	}
	public boolean isRequestPositionBearer() {
		return Costanti.AA_REQUEST_POSITION_VALUE_BEARER.equals(this.getRequestPosition());
	}
	public boolean isRequestPositionPayload() {
		return Costanti.AA_REQUEST_POSITION_VALUE_PAYLOAD.equals(this.getRequestPosition());
	}
	public boolean isRequestPositionHeader() {
		return Costanti.AA_REQUEST_POSITION_VALUE_HEADER.equals(this.getRequestPosition());
	}
	public boolean isRequestPositionQuery() {
		return Costanti.AA_REQUEST_POSITION_VALUE_QUERY.equals(this.getRequestPosition());
	}
	public String getRequestPositionHeaderName() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_POSITION_HEADER_NAME);
	}
	public String getRequestPositionQueryParameterName() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_POSITION_QUERY_PARAMETER_NAME);
	}

	public HttpRequestMethod getRequestHttpMethod() {
		return HttpRequestMethod.valueOf(this.defaultProperties.getProperty(Costanti.AA_REQUEST_HTTPMETHOD));
	}
	
	public String getRequestType() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_TYPE);
	}
	public boolean isRequestJson() {
		return Costanti.AA_REQUEST_TYPE_VALUE_JSON.equals(this.getRequestType());
	}
	public boolean isRequestJws() {
		return Costanti.AA_REQUEST_TYPE_VALUE_JWS.equals(this.getRequestType());
	}
	public boolean isRequestCustom() {
		return Costanti.AA_REQUEST_TYPE_VALUE_CUSTOM.equals(this.getRequestType());
	}
	
	public String getRequestContentType() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_CONTENT_TYPE);
	}
	
	public String getRequestDynamicPayloadType() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_DYNAMIC_PAYLOAD_TYPE);
	}
	public boolean isRequestDynamicPayloadJwt() {
		return Costanti.AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_JWT.equals(this.getRequestDynamicPayloadType());
	}
	public boolean isRequestDynamicPayloadTemplate() {
		return Costanti.AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_TEMPLATE.equals(this.getRequestDynamicPayloadType());
	}
	public boolean isRequestDynamicPayloadFreemarkerTemplate() {
		return Costanti.AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_FREEMARKER_TEMPLATE.equals(this.getRequestDynamicPayloadType());
	}
	public boolean isRequestDynamicPayloadVelocityTemplate() {
		return Costanti.AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_VELOCITY_TEMPLATE.equals(this.getRequestDynamicPayloadType());
	}
	public String getRequestDynamicPayload() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_DYNAMIC_PAYLOAD);
	}
	
	public String getRequestJwtIssuer() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_ISSUER);
	}
	public String getRequestJwtSubject() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SUBJECT);
	}
	public String getRequestJwtAudience() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_AUDIENCE);
	}
	public Integer getJwtTtlSeconds() {
		String ttl = this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_EXPIRED_TTL_SECONDS);
		if(ttl==null || StringUtils.isEmpty(ttl)) {
			ttl = Costanti.AA_REQUEST_JWT_EXPIRED_TTL_SECONDS_DEFAULT_VALUE;
		}
		return Integer.valueOf(ttl);
	}
	public String getRequestJwtExpired() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_EXPIRED_TTL_SECONDS);
	}
	public String getRequestJwtClaims() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_CLAIMS);
	}
	
	
	
	public String getRequestJwtSignAlgorithm() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_ALGORITHM);
	}
	public boolean isRequestJwtSignIncludeKeyIdWithKeyAlias() {
		String tmp = this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID);
		return tmp!=null && Costanti.AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_MODE_ALIAS.equals(tmp);
	}
	public boolean isRequestJwtSignIncludeKeyIdCustom() {
		String tmp = this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID);
		return tmp!=null && Costanti.AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_MODE_CUSTOM.equals(tmp);
	}
	public String getRequestJwtSignIncludeKeyIdCustom() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_VALUE);
	}
	public boolean isRequestJwtSignIncludeX509Cert() {
		String tmp = this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_INCLUDE_X509_CERT);
		return tmp!=null && Boolean.valueOf(tmp);
	}
	public String getRequestJwtSignIncludeX509URL() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_INCLUDE_X509_URL);
	}
	public boolean isRequestJwtSignIncludeX509CertSha1() {
		String tmp = this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_INCLUDE_X509_SHA1);
		return tmp!=null && Boolean.valueOf(tmp);
	}
	public boolean isRequestJwtSignIncludeX509CertSha256() {
		String tmp = this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_INCLUDE_X509_SHA256);
		return tmp!=null && Boolean.valueOf(tmp);
	}
	public boolean isRequestJwtSignJoseContentType() {
		String tmp = this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_JOSE_CONTENT_TYPE);
		return tmp!=null && Boolean.valueOf(tmp);
	}
	public String getRequestJwtSignJoseType() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_JOSE_TYPE);
	}
	
	public String getRequestJwtSignKeystoreType() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_KEYSTORE_TYPE);
	}
	public String getRequestJwtSignKeystoreFile() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_KEYSTORE_FILE);
	}
	public String getRequestJwtSignKeystoreFilePublicKey() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_KEYSTORE_FILE_PUBLIC);
	}
	public String getRequestJwtSignKeystoreFileAlgorithm() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_KEYSTORE_FILE_ALGORITHM);
	}
	public String getRequestJwtSignKeystorePassword() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_KEYSTORE_PASSWORD);
	}
	public String getRequestJwtSignKeyAlias() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_KEY_ALIAS);
	}
	public String getRequestJwtSignKeyPassword() {
		return this.defaultProperties.getProperty(Costanti.AA_REQUEST_JWT_SIGN_KEY_PASSWORD);
	}
	
	public String getResponseType() {
		return this.defaultProperties.getProperty(Costanti.AA_RESPONSE_TYPE);
	}
	public boolean isResponseJson() {
		return Costanti.AA_RESPONSE_TYPE_VALUE_JSON.equals(this.getResponseType());
	}
	public boolean isResponseJws() {
		return Costanti.AA_RESPONSE_TYPE_VALUE_JWS.equals(this.getResponseType());
	}
	public boolean isResponseCustom() {
		return Costanti.AA_RESPONSE_TYPE_VALUE_CUSTOM.equals(this.getResponseType());
	}
	
	public String getResponseJwsOcspPolicy() {
		return this.defaultProperties.getProperty(SecurityConstants.SIGNATURE_OCSP);
	}
	public String getResponseJwsCrl() {
		return this.defaultProperties.getProperty(SecurityConstants.SIGNATURE_CRL);
	}
	
	public String getResponseAudience() {
		return this.defaultProperties.getProperty(Costanti.AA_RESPONSE_AUDIENCE);
	}
	
}
