/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import java.text.MessageFormat;

import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.pdd.core.token.parser.BasicTokenParser;
import org.openspcoop2.pdd.core.token.parser.ITokenParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**     
 * PolicyGestioneToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyGestioneToken extends AbstractPolicyToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean tokenOpzionale;
	
	private boolean validazioneJWT;
	private boolean validazioneJWT_warningOnly;
	
	private boolean introspection;
	private boolean introspection_warningOnly;
	
	private boolean userInfo;
	private boolean userInfo_warningOnly;
	
	private boolean forwardToken;
	
	
	public boolean isTokenOpzionale() {
		return this.tokenOpzionale;
	}
	public void setTokenOpzionale(boolean tokenOpzionale) {
		this.tokenOpzionale = tokenOpzionale;
	}
	public boolean isValidazioneJWT() {
		return this.validazioneJWT;
	}
	public void setValidazioneJWT(boolean validazioneJWT) {
		this.validazioneJWT = validazioneJWT;
	}
	public boolean isValidazioneJWT_warningOnly() {
		return this.validazioneJWT_warningOnly;
	}
	public void setValidazioneJWT_warningOnly(boolean validazioneJWT_warningOnly) {
		this.validazioneJWT_warningOnly = validazioneJWT_warningOnly;
	}
	public boolean isIntrospection() {
		return this.introspection;
	}
	public void setIntrospection(boolean introspection) {
		this.introspection = introspection;
	}
	public boolean isIntrospection_warningOnly() {
		return this.introspection_warningOnly;
	}
	public void setIntrospection_warningOnly(boolean introspection_warningOnly) {
		this.introspection_warningOnly = introspection_warningOnly;
	}
	public boolean isUserInfo() {
		return this.userInfo;
	}
	public void setUserInfo(boolean userInfo) {
		this.userInfo = userInfo;
	}
	public boolean isUserInfo_warningOnly() {
		return this.userInfo_warningOnly;
	}
	public void setUserInfo_warningOnly(boolean userInfo_warningOnly) {
		this.userInfo_warningOnly = userInfo_warningOnly;
	}
	public boolean isForwardToken() {
		return this.forwardToken;
	}
	public void setForwardToken(boolean forwardToken) {
		this.forwardToken = forwardToken;
	}
	
	public String getRealm() throws ProviderException, ProviderValidationException {
		String realm = this.defaultProperties.getProperty(Costanti.POLICY_REALM);
		if(realm==null) {
			realm = this.name;
		}
		return realm;
	}
	public boolean isMessageErrorGenerateEmptyMessage() throws ProviderException, ProviderValidationException{
		boolean genericError = true;
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_MESSAGE_ERROR_BODY_EMPTY);
		if(tmp!=null) {
			genericError = Boolean.valueOf(tmp);
		}
		return genericError;
	}
	public boolean isMessageErrorGenerateGenericMessage() throws ProviderException, ProviderValidationException{
		boolean genericError = true;
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_MESSAGE_ERROR_GENERIC_MESSAGE);
		if(tmp!=null) {
			genericError = Boolean.valueOf(tmp);
		}
		return genericError;
	}
	
	public String getLabelAzioniGestioneToken() {
		StringBuilder bf = new StringBuilder();
		if(this.isValidazioneJWT() || this.isIntrospection() || this.isUserInfo()) {
			bf.append("Validazione ");
			boolean first = true;
			if(this.isValidazioneJWT()) {
				bf.append("JWT");
				first = false;
			}
			if(this.isIntrospection()) {
				if(!first) {
					bf.append(",");
				}
				bf.append("Introspection");
				first = false;
			}
			if(this.isUserInfo()) {
				if(!first) {
					bf.append(",");
				}
				bf.append("UserInfo");
				first = false;
			}
			return bf.toString();
		}
		else {
			return "Nessuna Validazione Attiva";
		}
	}
	
	public String getAzioniGestioneToken() {
		StringBuilder bf = new StringBuilder();
		if(this.isValidazioneJWT() || this.isIntrospection() || this.isUserInfo()) {
			boolean first = true;
			if(this.isValidazioneJWT()) {
				bf.append(Costanti.GESTIONE_TOKEN_VALIDATION_ACTION_JWT);
				first = false;
			}
			if(this.isIntrospection()) {
				if(!first) {
					bf.append(",");
				}
				bf.append(Costanti.GESTIONE_TOKEN_VALIDATION_ACTION_INTROSPECTION);
				first = false;
			}
			if(this.isUserInfo()) {
				if(!first) {
					bf.append(",");
				}
				bf.append(Costanti.GESTIONE_TOKEN_VALIDATION_ACTION_USER_INFO);
				first = false;
			}
			return bf.toString();
		}
		else {
			return Costanti.GESTIONE_TOKEN_VALIDATION_ACTION_NONE;
		}
	}
	
	public String getTipoToken() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_TYPE);
	}
	public String getLabelTipoToken() {
		String tokenType = this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_TYPE);
		if(Costanti.POLICY_TOKEN_TYPE_OPAQUE.equals(tokenType)) {
			return "Opaco";
		}
		else {
			return tokenType.toUpperCase();
		}
	}
	
	public String getTokenSource() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_SOURCE);
	}
	public String getTokenSourceHeaderName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER_NAME);
	}
	public String getTokenSourceUrlPropertyName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL_PROPERTY_NAME);
	}
		
	public String getLabelPosizioneToken() {
		String position = this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_SOURCE);
		if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(position)) {
			return Costanti.POLICY_TOKEN_SOURCE_RFC6750_LABEL;
		}
		else if(Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER.equals(position)) {
			return Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER_LABEL;
		}
		else if(Costanti.POLICY_TOKEN_SOURCE_RFC6750_FORM.equals(position)) {
			return Costanti.POLICY_TOKEN_SOURCE_RFC6750_FORM_LABEL;
		}
		else if(Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL.equals(position)) {
			return Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL_LABEL;
		}
		else if(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER.equals(position)) {
			return Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER_LABEL.replace(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_TEMPLATE_LABEL, 
					this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER_NAME));
		}
		else if(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL.equals(position)) {
			return Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL_LABEL.replace(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_TEMPLATE_LABEL, 
					this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL_PROPERTY_NAME));
		}
		return "Sconosciuto"; // non dovrebbe mai succedere, esiste la validazione
	}
	
	public boolean isEndpointHttps() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_ENDPOINT_HTTPS_STATO);	
	}
	
	public boolean isValidazioneJWT_saveErrorInCache() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_VALIDAZIONE_SAVE_ERROR_IN_CACHE);	
	}
	public ITokenParser getValidazioneJWT_TokenParser() throws Exception {
		ITokenParser parser = null;
		TipologiaClaims tipologiaClaims = TipologiaClaims.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE));
		if(TipologiaClaims.CUSTOM.equals(tipologiaClaims)) {
			String className = this.defaultProperties.getProperty(Costanti.POLICY_VALIDAZIONE_CLAIMS_PARSER_CLASS_NAME);
			parser = (ITokenParser) ClassLoaderUtilities.newInstance(className);
		}
		else{
			parser = new BasicTokenParser(tipologiaClaims, TokenUtilities.getValidazioneJwtClaimsMappingProperties(this.properties));
		}
		return parser;
	}
	public String getValidazioneJWT_ocspPolicy() {
		return this.defaultProperties.getProperty(SecurityConstants.SIGNATURE_OCSP);
	}
	public String getValidazioneJWT_crl() {
		return this.defaultProperties.getProperty(SecurityConstants.SIGNATURE_CRL);
	}
	
	public String getIntrospection_endpoint() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_URL);
	}
	public boolean isIntrospection_saveErrorInCache() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_INTROSPECTION_SAVE_ERROR_IN_CACHE);	
	}
	public HttpRequestMethod getIntrospection_httpMethod() {
		return HttpRequestMethod.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_HTTP_METHOD));
	}
	public TipoTokenRequest getIntrospection_tipoTokenRequest() {
		return TipoTokenRequest.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION));
	}
	public String getIntrospection_tipoTokenRequest_headerName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_HEADER_NAME);
	}
	public String getIntrospection_tipoTokenRequest_urlPropertyName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME);
	}
	public String getIntrospection_tipoTokenRequest_formPropertyName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_FORM_PROPERTY_NAME);
	}
	public String getIntrospection_contentType() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_CONTENT_TYPE);
	}
	public ITokenParser getIntrospection_TokenParser() throws Exception {
		ITokenParser parser = null;
		TipologiaClaims tipologiaClaims = TipologiaClaims.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE));
		if(TipologiaClaims.CUSTOM.equals(tipologiaClaims)) {
			String className = this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME);
			parser = (ITokenParser) ClassLoaderUtilities.newInstance(className);
		}
		else{
			parser = new BasicTokenParser(tipologiaClaims, TokenUtilities.getIntrospectionClaimsMappingProperties(this.properties));
		}
		return parser;
	}
	public boolean isIntrospection_basicAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_INTROSPECTION_AUTH_BASIC_STATO);	
	}
	public String getIntrospection_basicAuthentication_username() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BASIC_USERNAME);
	}
	public String getIntrospection_basicAuthentication_password() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD);
	}
	public boolean isIntrospection_bearerAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_INTROSPECTION_AUTH_BEARER_STATO);	
	}
	public String getIntrospection_beareAuthentication_token() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BEARER_TOKEN);
	}
	public boolean isIntrospection_httpsAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_INTROSPECTION_AUTH_SSL_STATO);	
	}
	
	public String getUserInfo_endpoint() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_URL);
	}
	public boolean isUserInfo_saveErrorInCache() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_USER_INFO_SAVE_ERROR_IN_CACHE);	
	}
	public HttpRequestMethod getUserInfo_httpMethod() {
		return HttpRequestMethod.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_HTTP_METHOD));
	}
	public TipoTokenRequest getUserInfo_tipoTokenRequest() {
		return TipoTokenRequest.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION));
	}
	public String getUserInfo_tipoTokenRequest_headerName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_HEADER_NAME);
	}
	public String getUserInfo_tipoTokenRequest_urlPropertyName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME);
	}
	public String getUserInfo_tipoTokenRequest_formPropertyName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_FORM_PROPERTY_NAME);
	}
	public String getUserInfo_contentType() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_CONTENT_TYPE);
	}
	public ITokenParser getUserInfo_TokenParser() throws Exception {
		ITokenParser parser = null;
		TipologiaClaims tipologiaClaims = TipologiaClaims.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_CLAIMS_PARSER_TYPE));
		if(TipologiaClaims.CUSTOM.equals(tipologiaClaims)) {
			String className = this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME);
			parser = (ITokenParser) ClassLoaderUtilities.newInstance(className);
		}
		else{
			parser = new BasicTokenParser(tipologiaClaims, TokenUtilities.getUserInfoClaimsMappingProperties(this.properties));
		}
		return parser;
	}
	public boolean isUserInfo_basicAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_USER_INFO_AUTH_BASIC_STATO);	
	}
	public String getUserInfo_basicAuthentication_username() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_AUTH_BASIC_USERNAME);
	}
	public String getUserInfo_basicAuthentication_password() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_AUTH_BASIC_PASSWORD);
	}
	public boolean isUserInfo_bearerAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_USER_INFO_AUTH_BEARER_STATO);	
	}
	public String getUserInfo_beareAuthentication_token() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_AUTH_BEARER_TOKEN);
	}
	public boolean isUserInfo_httpsAuthentication() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_USER_INFO_AUTH_SSL_STATO);	
	}
	
	
	public boolean isForwardToken_trasparente() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_STATO);	
	}
	public String getForwardToken_trasparenteMode() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE);
	}
	public String getForwardToken_trasparenteModeCustomHeader() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER_NAME);
	}
	public String getForwardToken_trasparenteModeCustomUrl() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL_PARAMETER_NAME);
	}
	
	public boolean isForwardToken_informazioniRaccolte() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_STATO);	
	}
	public String getForwardToken_informazioniRaccolteMode() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE);
	}
	public boolean isForwardToken_informazioniRaccolteEncodeBase64() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCODE_BASE64);	
	}
	
	public boolean isForwardToken_informazioniRaccolte_validazioneJWT() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT);	
	}
	public String getForwardToken_informazioniRaccolte_validazioneJWT_mode() throws ProviderException, ProviderValidationException{
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE);
	}
	public String getForwardToken_informazioniRaccolte_validazioneJWT_mode_headerName() throws ProviderException, ProviderValidationException{
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_HEADER_NAME);
	}
	public String getForwardToken_informazioniRaccolte_validazioneJWT_mode_queryParameterName() throws ProviderException, ProviderValidationException{
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_URL_PARAMETER_NAME);
	}
	
	public boolean isForwardToken_informazioniRaccolte_introspection() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION);	
	}
	public String getForwardToken_informazioniRaccolte_introspection_mode() throws ProviderException, ProviderValidationException{
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE);
	}
	public String getForwardToken_informazioniRaccolte_introspection_mode_headerName() throws ProviderException, ProviderValidationException{
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_HEADER_NAME);
	}
	public String getForwardToken_informazioniRaccolte_introspection_mode_queryParameterName() throws ProviderException, ProviderValidationException{
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_URL_PARAMETER_NAME);
	}
	
	public boolean isForwardToken_informazioniRaccolte_userInfo() throws ProviderException, ProviderValidationException{
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO);	
	}
	public String getForwardToken_informazioniRaccolte_userInfo_mode() throws ProviderException, ProviderValidationException{
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE);
	}
	public String getForwardToken_informazioniRaccolte_userInfo_mode_headerName() throws ProviderException, ProviderValidationException{
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_HEADER_NAME);
	}
	public String getForwardToken_informazioniRaccolte_userInfo_mode_queryParameterName() throws ProviderException, ProviderValidationException{
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_URL_PARAMETER_NAME);
	}

	public String getAzioniForwardToken() throws ProviderException, ProviderValidationException {
		StringBuilder bf = new StringBuilder();
		if(this.isForwardToken()) {
			boolean first = true;
			if(this.isForwardToken_trasparente()) {
				String mode = this.getForwardToken_trasparenteMode();
				if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED.equals(mode)) {
					bf.append(Costanti.LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED_ORIGINALE);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER.equals(mode)) {
					bf.append(Costanti.LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL.equals(mode)) {
					bf.append(Costanti.LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(mode)) {
					bf.append(MessageFormat.format(Costanti.LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER,getForwardToken_trasparenteModeCustomHeader()));
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(mode)) {
					bf.append(MessageFormat.format(Costanti.LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL,getForwardToken_trasparenteModeCustomUrl()));
				}
				else {
					bf.append("Originale '"+mode+"'");
				}
				first = false;
			}
			if(this.isForwardToken_informazioniRaccolte()) {
				if(!first) {
					bf.append(",");
				}
				String mode = this.getForwardToken_informazioniRaccolteMode();
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS.equals(mode)) {
					bf.append(Costanti.LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON.equals(mode)) {
					bf.append(Costanti.LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(mode)) {
					bf.append(Costanti.LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(mode)) {
					bf.append(Costanti.LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(mode)) {
					bf.append(Costanti.LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON.equals(mode)) {
					bf.append(Costanti.LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON);
				}
				else {
					bf.append(mode);
				}
				first = false;
			}
			return bf.toString();
		}
		else {
			return "Disabilitato";
		}
	}
}
