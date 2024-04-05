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
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.token.parser.BasicDynamicDiscoveryParser;
import org.openspcoop2.pdd.core.token.parser.BasicTokenParser;
import org.openspcoop2.pdd.core.token.parser.IDynamicDiscoveryParser;
import org.openspcoop2.pdd.core.token.parser.ITokenParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.jose.JOSEUtils;
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
	
	private boolean dynamicDiscovery;
	
	private boolean validazioneJWT;
	private boolean validazioneJWTWarningOnly;
	
	private boolean introspection;
	private boolean introspectionWarningOnly;
	
	private boolean userInfo;
	private boolean userInfoWarningOnly;
	
	private boolean forwardToken;
	
	
	public boolean isTokenOpzionale() {
		return this.tokenOpzionale;
	}
	public void setTokenOpzionale(boolean tokenOpzionale) {
		this.tokenOpzionale = tokenOpzionale;
	}
	public boolean isDynamicDiscovery() {
		return this.dynamicDiscovery;
	}
	public void setDynamicDiscovery(boolean dynamicDiscovery) {
		this.dynamicDiscovery = dynamicDiscovery;
	}
	public boolean isValidazioneJWT() {
		return this.validazioneJWT;
	}
	public void setValidazioneJWT(boolean validazioneJWT) {
		this.validazioneJWT = validazioneJWT;
	}
	public boolean isValidazioneJWTWarningOnly() {
		return this.validazioneJWTWarningOnly;
	}
	public void setValidazioneJWTWarningOnly(boolean validazioneJWTWarningOnly) {
		this.validazioneJWTWarningOnly = validazioneJWTWarningOnly;
	}
	public boolean isIntrospection() {
		return this.introspection;
	}
	public void setIntrospection(boolean introspection) {
		this.introspection = introspection;
	}
	public boolean isIntrospectionWarningOnly() {
		return this.introspectionWarningOnly;
	}
	public void setIntrospectionWarningOnly(boolean introspectionWarningOnly) {
		this.introspectionWarningOnly = introspectionWarningOnly;
	}
	public boolean isUserInfo() {
		return this.userInfo;
	}
	public void setUserInfo(boolean userInfo) {
		this.userInfo = userInfo;
	}
	public boolean isUserInfoWarningOnly() {
		return this.userInfoWarningOnly;
	}
	public void setUserInfoWarningOnly(boolean userInfoWarningOnly) {
		this.userInfoWarningOnly = userInfoWarningOnly;
	}
	public boolean isForwardToken() {
		return this.forwardToken;
	}
	public void setForwardToken(boolean forwardToken) {
		this.forwardToken = forwardToken;
	}
	
	public String getRealm() {
		String realm = this.defaultProperties.getProperty(Costanti.POLICY_REALM);
		if(realm==null) {
			realm = this.name;
		}
		return realm;
	}
	public boolean isMessageErrorGenerateEmptyMessage() {
		boolean genericError = true;
		String tmp = this.defaultProperties.getProperty(Costanti.POLICY_MESSAGE_ERROR_BODY_EMPTY);
		if(tmp!=null) {
			genericError = Boolean.valueOf(tmp);
		}
		return genericError;
	}
	public boolean isMessageErrorGenerateGenericMessage() {
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
				/**first = false;*/
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
				/**first = false;*/
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
	
	public boolean isEndpointHttps() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_ENDPOINT_HTTPS_STATO);	
	}
	
	
	public String getDynamicDiscoveryEndpoint() {
		return this.defaultProperties.getProperty(Costanti.POLICY_DISCOVERY_URL);
	}
	public TipologiaClaims getDynamicDiscoveryType() {
		return TipologiaClaims.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_DISCOVERY_CLAIMS_PARSER_TYPE));
	}
	public IDynamicDiscoveryParser getDynamicDiscoveryParser() throws TokenException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		IDynamicDiscoveryParser parser = null;
		TipologiaClaims tipologiaClaims = TipologiaClaims.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_DISCOVERY_CLAIMS_PARSER_TYPE));
		if(TipologiaClaims.CUSTOM.equals(tipologiaClaims)) {
			String className = this.defaultProperties.getProperty(Costanti.POLICY_DISCOVERY_CLAIMS_PARSER_CLASS_NAME);
			if(className!=null && StringUtils.isNotEmpty(className) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(className)) {
				parser = (IDynamicDiscoveryParser) ClassLoaderUtilities.newInstance(className);
			}
			else {
				String tipo = this.defaultProperties.getProperty(Costanti.POLICY_DISCOVERY_CLAIMS_PARSER_PLUGIN_TYPE);
				if(tipo!=null && StringUtils.isNotEmpty(tipo) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(tipo)) {
			    	try{
						PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
						parser = pluginLoader.newDynamicDiscovery(tipo);
					}catch(Exception e){
						throw new TokenException(e.getMessage(),e); // descrizione errore già corretta
					}
				}
				else {
					throw new TokenException("Deve essere selezionato un plugin per il parser dei claims della risposta del servizio 'Introspection'");
				}
			}
		}
		else{
			parser = new BasicDynamicDiscoveryParser(tipologiaClaims, TokenUtilities.getDynamicDiscoveryClaimsMappingProperties(this.properties));
		}
		return parser;
	}
	
	
	
	public boolean isValidazioneJWTLocationHttp() {
		String location = this.getValidazioneJWTLocation();
		return location !=null && 
				(location.startsWith(JOSEUtils.HTTP_PROTOCOL) || location.startsWith(JOSEUtils.HTTPS_PROTOCOL));
	}
	public String getValidazioneJWTLocation() {
		if(this.properties!=null) {
			Properties p = this.properties.get(Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID);
			if(p!=null) {
				return p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
			}
		}
		return null;
	}
	public boolean isValidazioneJWTSaveErrorInCache() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_VALIDAZIONE_SAVE_ERROR_IN_CACHE);	
	}
	public ITokenParser getValidazioneJWTTokenParser() throws TokenException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ITokenParser parser = null;
		TipologiaClaims tipologiaClaims = TipologiaClaims.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE));
		if(TipologiaClaims.CUSTOM.equals(tipologiaClaims)) {
			String className = this.defaultProperties.getProperty(Costanti.POLICY_VALIDAZIONE_CLAIMS_PARSER_CLASS_NAME);
			if(className!=null && StringUtils.isNotEmpty(className) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(className)) {
				parser = (ITokenParser) ClassLoaderUtilities.newInstance(className);
			}
			else {
				String tipo = this.defaultProperties.getProperty(Costanti.POLICY_VALIDAZIONE_CLAIMS_PARSER_PLUGIN_TYPE);
				if(tipo!=null && StringUtils.isNotEmpty(tipo) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(tipo)) {
			    	try{
						PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
						parser = pluginLoader.newTokenValidazione(tipo);
					}catch(Exception e){
						throw new TokenException(e.getMessage(),e); // descrizione errore già corretta
					}
				}
				else {
					throw new TokenException("Deve essere selezionato un plugin per il parser del token JWT");
				}
			}
		}
		else{
			parser = new BasicTokenParser(tipologiaClaims, TokenUtilities.getValidazioneJwtClaimsMappingProperties(this.properties));
		}
		return parser;
	}
	public String getValidazioneJWTOcspPolicy() {
		return this.defaultProperties.getProperty(SecurityConstants.SIGNATURE_OCSP);
	}
	public String getValidazioneJWTCrl() {
		return this.defaultProperties.getProperty(SecurityConstants.SIGNATURE_CRL);
	}
	
	public boolean isValidazioneJWTHeader() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_VALIDAZIONE_JWS_HEADER);	
	}
	public List<String> getValidazioneJWTHeaderTyp() {
		return TokenUtilities.getClaims(this.defaultProperties, Costanti.POLICY_VALIDAZIONE_JWS_HEADER_TYP);
	}
	public List<String> getValidazioneJWTHeaderCty() {
		return TokenUtilities.getClaims(this.defaultProperties, Costanti.POLICY_VALIDAZIONE_JWS_HEADER_CTY);
	}
	public List<String> getValidazioneJWTHeaderAlg() {
		return TokenUtilities.getClaims(this.defaultProperties, Costanti.POLICY_VALIDAZIONE_JWS_HEADER_ALG);
	}
	
	public String getIntrospectionEndpoint() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_URL);
	}
	public boolean isIntrospectionSaveErrorInCache() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_INTROSPECTION_SAVE_ERROR_IN_CACHE);	
	}
	public HttpRequestMethod getIntrospectionHttpMethod() {
		return HttpRequestMethod.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_HTTP_METHOD));
	}
	public TipoTokenRequest getIntrospectionTipoTokenRequest() {
		return TipoTokenRequest.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION));
	}
	public String getIntrospectionTipoTokenRequestHeaderName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_HEADER_NAME);
	}
	public String getIntrospectionTipoTokenRequestUrlPropertyName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME);
	}
	public String getIntrospectionTipoTokenRequestFormPropertyName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_FORM_PROPERTY_NAME);
	}
	public String getIntrospectionContentType() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_CONTENT_TYPE);
	}
	public ITokenParser getIntrospectionTokenParser() throws TokenException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ITokenParser parser = null;
		TipologiaClaims tipologiaClaims = TipologiaClaims.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE));
		if(TipologiaClaims.CUSTOM.equals(tipologiaClaims)) {
			String className = this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME);
			if(className!=null && StringUtils.isNotEmpty(className) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(className)) {
				parser = (ITokenParser) ClassLoaderUtilities.newInstance(className);
			}
			else {
				String tipo = this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_CLAIMS_PARSER_PLUGIN_TYPE);
				if(tipo!=null && StringUtils.isNotEmpty(tipo) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(tipo)) {
			    	try{
						PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
						parser = pluginLoader.newTokenValidazione(tipo);
					}catch(Exception e){
						throw new TokenException(e.getMessage(),e); // descrizione errore già corretta
					}
				}
				else {
					throw new TokenException("Deve essere selezionato un plugin per il parser dei claims della risposta del servizio 'Introspection'");
				}
			}
		}
		else{
			parser = new BasicTokenParser(tipologiaClaims, TokenUtilities.getIntrospectionClaimsMappingProperties(this.properties));
		}
		return parser;
	}
	public boolean isIntrospectionBasicAuthentication() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_INTROSPECTION_AUTH_BASIC_STATO);	
	}
	public String getIntrospectionBasicAuthenticationUsername() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BASIC_USERNAME);
	}
	public String getIntrospectionBasicAuthenticationPassword() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD);
	}
	public boolean isIntrospectionBearerAuthentication() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_INTROSPECTION_AUTH_BEARER_STATO);	
	}
	public String getIntrospectionBeareAuthenticationToken() {
		return this.defaultProperties.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BEARER_TOKEN);
	}
	public boolean isIntrospectionHttpsAuthentication() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_INTROSPECTION_AUTH_SSL_STATO);	
	}
	
	public String getUserInfoEndpoint() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_URL);
	}
	public boolean isUserInfoSaveErrorInCache() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_USER_INFO_SAVE_ERROR_IN_CACHE);	
	}
	public HttpRequestMethod getUserInfoHttpMethod() {
		return HttpRequestMethod.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_HTTP_METHOD));
	}
	public TipoTokenRequest getUserInfoTipoTokenRequest() {
		return TipoTokenRequest.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION));
	}
	public String getUserInfoTipoTokenRequestHeaderName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_HEADER_NAME);
	}
	public String getUserInfoTipoTokenRequestUrlPropertyName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME);
	}
	public String getUserInfoTipoTokenRequestFormPropertyName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_FORM_PROPERTY_NAME);
	}
	public String getUserInfoContentType() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_CONTENT_TYPE);
	}
	public ITokenParser getUserInfoTokenParser() throws TokenException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ITokenParser parser = null;
		TipologiaClaims tipologiaClaims = TipologiaClaims.valueOf(this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_CLAIMS_PARSER_TYPE));
		if(TipologiaClaims.CUSTOM.equals(tipologiaClaims)) {
			String className = this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME);
			if(className!=null && StringUtils.isNotEmpty(className) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(className)) {
				parser = (ITokenParser) ClassLoaderUtilities.newInstance(className);
			}
			else {
				String tipo = this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_CLAIMS_PARSER_PLUGIN_TYPE);
				if(tipo!=null && StringUtils.isNotEmpty(tipo) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(tipo)) {
			    	try{
						PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
						parser = pluginLoader.newTokenValidazione(tipo);
					}catch(Exception e){
						throw new TokenException(e.getMessage(),e); // descrizione errore già corretta
					}
				}
				else {
					throw new TokenException("Deve essere selezionato un plugin per il parser dei claims della risposta del servizio 'UserInfo'");
				}
			}
		}
		else{
			parser = new BasicTokenParser(tipologiaClaims, TokenUtilities.getUserInfoClaimsMappingProperties(this.properties));
		}
		return parser;
	}
	public boolean isUserInfoBasicAuthentication() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_USER_INFO_AUTH_BASIC_STATO);	
	}
	public String getUserInfoBasicAuthenticationUsername() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_AUTH_BASIC_USERNAME);
	}
	public String getUserInfoBasicAuthenticationPassword() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_AUTH_BASIC_PASSWORD);
	}
	public boolean isUserInfoBearerAuthentication() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_USER_INFO_AUTH_BEARER_STATO);	
	}
	public String getUserInfoBeareAuthenticationToken() {
		return this.defaultProperties.getProperty(Costanti.POLICY_USER_INFO_AUTH_BEARER_TOKEN);
	}
	public boolean isUserInfoHttpsAuthentication() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_USER_INFO_AUTH_SSL_STATO);	
	}
	
	
	public boolean isForwardTokenTrasparente() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_STATO);	
	}
	public String getForwardTokenTrasparenteMode() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE);
	}
	public String getForwardTokenTrasparenteModeCustomHeader() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER_NAME);
	}
	public String getForwardTokenTrasparenteModeCustomUrl() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL_PARAMETER_NAME);
	}
	
	public boolean isForwardTokenInformazioniRaccolte() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_STATO);	
	}
	public String getForwardTokenInformazioniRaccolteMode() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE);
	}
	public boolean isForwardTokenInformazioniRaccolteEncodeBase64() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCODE_BASE64);	
	}
	
	public boolean isForwardTokenInformazioniRaccolteValidazioneJWT() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT);	
	}
	public String getForwardTokenInformazioniRaccolteValidazioneJWTMode() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE);
	}
	public String getForwardTokenInformazioniRaccolteValidazioneJWTModeHeaderName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_HEADER_NAME);
	}
	public String getForwardTokenInformazioniRaccolteValidazioneJWTModeQueryParameterName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_URL_PARAMETER_NAME);
	}
	
	public boolean isForwardTokenInformazioniRaccolteIntrospection() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION);	
	}
	public String getForwardTokenInformazioniRaccolteIntrospectionMode() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE);
	}
	public String getForwardTokenInformazioniRaccolteIntrospectionModeHeaderName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_HEADER_NAME);
	}
	public String getForwardTokenInformazioniRaccolteIntrospectionModeQueryParameterName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_URL_PARAMETER_NAME);
	}
	
	public boolean isForwardTokenInformazioniRaccolteUserInfo() {
		return TokenUtilities.isEnabled(this.defaultProperties, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO);	
	}
	public String getForwardTokenInformazioniRaccolteUserInfoMode() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE);
	}
	public String getForwardTokenInformazioniRaccolteUserInfoModeHeaderName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_HEADER_NAME);
	}
	public String getForwardTokenInformazioniRaccolteUserInfoModeQueryParameterName() {
		return this.defaultProperties.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_URL_PARAMETER_NAME);
	}

	public String getAzioniForwardToken() {
		StringBuilder bf = new StringBuilder();
		if(this.isForwardToken()) {
			boolean first = true;
			if(this.isForwardTokenTrasparente()) {
				String mode = this.getForwardTokenTrasparenteMode();
				addPolicyTokenForwardTrasparente(mode, bf);
				first = false;
			}
			if(this.isForwardTokenInformazioniRaccolte()) {
				if(!first) {
					bf.append(",");
				}
				String mode = this.getForwardTokenInformazioniRaccolteMode();
				addPolicyTokenForwardInfoRaccolte(mode, bf);
				/**first = false;*/
			}
			return bf.toString();
		}
		else {
			return "Disabilitato";
		}
	}
	private void addPolicyTokenForwardTrasparente(String mode, StringBuilder bf) {
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
			bf.append(MessageFormat.format(Costanti.LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER,getForwardTokenTrasparenteModeCustomHeader()));
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(mode)) {
			bf.append(MessageFormat.format(Costanti.LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL,getForwardTokenTrasparenteModeCustomUrl()));
		}
		else {
			bf.append("Originale '"+mode+"'");
		}
	}
	private void addPolicyTokenForwardInfoRaccolte(String mode, StringBuilder bf) {
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
	}
}
