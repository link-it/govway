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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;

/**
 * AttributeAuthorityDynamicParameter
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NegoziazioneTokenDynamicParameters extends AbstractDynamicParameters {

	@SuppressWarnings("unused")
	private PolicyNegoziazioneToken policyNegoziazioneToken;
	
	private String endpoint;
	private String basicUsername;
	private String bearerToken;
	
	private String usernamePasswordGrant;
	
	private String signedJwtIssuer;
	private String signedJwtClientId;
	private String signedJwtSubject;
	private String signedJwtAudience;
	private String signedJwtJti;
	private String signedJwtPurposeId;
	private String signedJwtSessionInfo;
	private String signedJwtClaims;
	
	private String signedJwtCustomId; 
	private String signedJwtX509Url;
	
	private String scope;
	private String audience;
	private String formClientId;
	private String formResource;
	private String parameters;
	
	
	// static config
	
	private static boolean signedJwtIssuer_cacheKey;
	private static boolean signedJwtClientId_cacheKey;
	private static boolean signedJwtSubject_cacheKey;
	private static boolean signedJwtAudience_cacheKey;
	private static boolean signedJwtJti_cacheKey;
	private static boolean signedJwtPurposeId_cacheKey;
	private static boolean signedJwtSessionInfo_cacheKey;
	private static boolean signedJwtClaims_cacheKey;
	
	private static boolean signedJwtCustomId_cacheKey; 
	private static boolean signedJwtX509Url_cacheKey;
	
	private static boolean scope_cacheKey;
	private static boolean audience_cacheKey;
	private static boolean formClientId_cacheKey;
	private static boolean formResource_cacheKey;
	private static boolean parameters_cacheKey; 
	
	private static Boolean init_cacheKey = null;
	private static synchronized void initCacheKey() {
		if(init_cacheKey==null) {
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			
			signedJwtIssuer_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUER);
			signedJwtClientId_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLIENT_ID);
			signedJwtSubject_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SUBJECT);
			signedJwtAudience_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_AUDIENCE);
			signedJwtJti_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_IDENTIFIER);
			signedJwtPurposeId_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PURPOSE_ID);
			signedJwtSessionInfo_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SESSION_INFO);
			signedJwtClaims_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLAIMS);
			
			signedJwtCustomId_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_HEADER_KID); 
			signedJwtX509Url_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_HEADER_X509_URL);
			
			scope_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_REQUEST_SCOPE);
			audience_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_REQUEST_AUDIENCE);
			formClientId_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_REQUEST_CLIENT_ID);
			formResource_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_REQUEST_RESOURCE);
			parameters_cacheKey = op2Properties.isGestioneRetrieveToken_cacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_REQUEST_PARAMETERS); 
			
			init_cacheKey = true;
		}
	}
	private static void checkInitCacheKey() {
		if(init_cacheKey==null) {
			initCacheKey();
		}
	}
	

	public NegoziazioneTokenDynamicParameters(Map<String, Object> dynamicMap, PdDContext pddContext, PolicyNegoziazioneToken policyNegoziazioneToken) throws Exception {
		super(dynamicMap, pddContext);
		
		this.policyNegoziazioneToken = policyNegoziazioneToken;
		
		this.endpoint = policyNegoziazioneToken.getEndpoint();
		if(this.endpoint!=null && !"".equals(this.endpoint)) {
			this.endpoint = DynamicUtils.convertDynamicPropertyValue("endpoint.gwt", this.endpoint, dynamicMap, pddContext, true);	
		}
		
		boolean basic = policyNegoziazioneToken.isBasicAuthentication();
		if(basic) {
			this.basicUsername = policyNegoziazioneToken.getBasicAuthentication_username();
			if(this.basicUsername!=null && !"".equals(this.basicUsername)) {
				this.basicUsername = DynamicUtils.convertDynamicPropertyValue("username.gwt", this.basicUsername, dynamicMap, pddContext, true);	
			}
		}
		
		boolean bearer = policyNegoziazioneToken.isBearerAuthentication();
		if(bearer) {
			this.bearerToken = policyNegoziazioneToken.getBeareAuthentication_token();
			if(this.bearerToken!=null && !"".equals(this.bearerToken)) {
				this.bearerToken = DynamicUtils.convertDynamicPropertyValue("bearerToken.gwt", this.bearerToken, dynamicMap, pddContext, true);	
			}
		}
		
		if(policyNegoziazioneToken.isUsernamePasswordGrant()) {
			this.usernamePasswordGrant = policyNegoziazioneToken.getUsernamePasswordGrant_username();
			if(this.usernamePasswordGrant!=null && !"".equals(this.usernamePasswordGrant)) {
				this.usernamePasswordGrant = DynamicUtils.convertDynamicPropertyValue("usernamePasswordGrant.gwt", this.usernamePasswordGrant, dynamicMap, pddContext, true);	
			}
		}
		
		if(policyNegoziazioneToken.isRfc7523_x509_Grant() || policyNegoziazioneToken.isRfc7523_clientSecret_Grant()) {
			
			this.signedJwtIssuer = policyNegoziazioneToken.getJwtIssuer();
			if(this.signedJwtIssuer!=null && !"".equals(this.signedJwtIssuer) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.signedJwtIssuer)) {
				this.signedJwtIssuer = DynamicUtils.convertDynamicPropertyValue("issuer.gwt", this.signedJwtIssuer, dynamicMap, pddContext, true);	
			}
			
			this.signedJwtClientId = policyNegoziazioneToken.getJwtClientId();
			if(this.signedJwtClientId!=null && !"".equals(this.signedJwtClientId) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.signedJwtClientId)) {
				this.signedJwtClientId = DynamicUtils.convertDynamicPropertyValue("clientId.gwt", this.signedJwtClientId, dynamicMap, pddContext, true);	
			}
			
			this.signedJwtSubject = policyNegoziazioneToken.getJwtSubject();
			if(this.signedJwtSubject!=null && !"".equals(this.signedJwtSubject) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.signedJwtSubject)) {
				this.signedJwtSubject = DynamicUtils.convertDynamicPropertyValue("subject.gwt", this.signedJwtSubject, dynamicMap, pddContext, true);	
			}
			
			this.signedJwtAudience = policyNegoziazioneToken.getJwtAudience();
			if(this.signedJwtAudience!=null && !"".equals(this.signedJwtAudience) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.signedJwtAudience)) {
				this.signedJwtAudience = DynamicUtils.convertDynamicPropertyValue("jwtAudience.gwt", this.signedJwtAudience, dynamicMap, pddContext, true);	
			}
			
			this.signedJwtJti = policyNegoziazioneToken.getJwtIdentifier();
			if(this.signedJwtJti!=null && !"".equals(this.signedJwtJti) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.signedJwtJti)) {
				this.signedJwtJti = DynamicUtils.convertDynamicPropertyValue("jti.gwt", this.signedJwtJti, dynamicMap, pddContext, true);	
			}
			
			if(policyNegoziazioneToken.isPDND()) {
				
				this.signedJwtPurposeId = policyNegoziazioneToken.getJwtPurposeId();
				if(this.signedJwtPurposeId!=null && !"".equals(this.signedJwtPurposeId)) {
					this.signedJwtPurposeId = DynamicUtils.convertDynamicPropertyValue("jwtPurposeId.gwt", this.signedJwtPurposeId, dynamicMap, pddContext, true);	
				}
				
				this.signedJwtSessionInfo = policyNegoziazioneToken.getJwtSessionInfo();
				if(this.signedJwtSessionInfo!=null && !"".equals(this.signedJwtSessionInfo)) {
					this.signedJwtSessionInfo = DynamicUtils.convertDynamicPropertyValue("sessionInfo.gwt", this.signedJwtSessionInfo, dynamicMap, pddContext, true);	
				}
				
			}
			
			this.signedJwtClaims = policyNegoziazioneToken.getJwtClaims();
			if(this.signedJwtClaims!=null && !"".equals(this.signedJwtClaims)) {
				this.signedJwtClaims = DynamicUtils.convertDynamicPropertyValue("claims.gwt", this.signedJwtClaims, dynamicMap, pddContext, true);	
			}
			
			this.signedJwtCustomId = policyNegoziazioneToken.getJwtSignIncludeKeyIdCustom();
			if(this.signedJwtCustomId!=null && !"".equals(this.signedJwtCustomId)) {
				this.signedJwtCustomId = DynamicUtils.convertDynamicPropertyValue("kid.customId.gwt", this.signedJwtCustomId, dynamicMap, pddContext, true);	
			}
			
			this.signedJwtX509Url = policyNegoziazioneToken.getJwtSignIncludeX509URL();
			if(this.signedJwtX509Url!=null && !"".equals(this.signedJwtX509Url)) {
				this.signedJwtX509Url = DynamicUtils.convertDynamicPropertyValue("url-x5u.gwt", this.signedJwtX509Url, dynamicMap, pddContext, true);	
			}
		}
		
		this.scope = policyNegoziazioneToken.getScopeString();
		if(this.scope!=null && !"".equals(this.scope)) {
			this.scope = DynamicUtils.convertDynamicPropertyValue("scopes.gwt", this.scope, dynamicMap, pddContext, true);	
		}
		
		this.audience = policyNegoziazioneToken.getAudience();
		if(this.audience!=null && !"".equals(this.audience)) {
			this.audience = DynamicUtils.convertDynamicPropertyValue("aud.gwt", this.audience, dynamicMap, pddContext, true);	
		}
		
		if(policyNegoziazioneToken.isPDND()) {
			this.formClientId = policyNegoziazioneToken.getFormClientId();
			if(this.formClientId==null || "".equals(this.formClientId)) {
				this.formClientId = policyNegoziazioneToken.getJwtClientId();
			}
			if(this.formClientId!=null && !"".equals(this.formClientId) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.formClientId)) {
				this.formClientId = DynamicUtils.convertDynamicPropertyValue("formClientId.gwt", this.formClientId, dynamicMap, pddContext, true);	
			}
			
			this.formResource = policyNegoziazioneToken.getFormResource();
			if(this.formResource!=null && !"".equals(this.formResource) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.formResource)) {
				this.formResource = DynamicUtils.convertDynamicPropertyValue("formResource.gwt", this.formResource, dynamicMap, pddContext, true);	
			}
		}
		
		this.parameters = policyNegoziazioneToken.getFormParameters();
		if(this.parameters!=null && !"".equals(this.parameters)) {
			this.parameters = DynamicUtils.convertDynamicPropertyValue("parameters.gwt", this.parameters, dynamicMap, pddContext, true);	
		}
	}
	
	@Override
	public String toString() {
		return toString("\n", false);
	}
	public String toString(String separator, boolean cacheKey) {
		StringBuilder sb = new StringBuilder();
		String superS = super.toString();
		if(StringUtils.isNotEmpty(superS)) {
			sb.append(superS);
		}
		
		if(cacheKey) {
			checkInitCacheKey();
		}
		
		if(StringUtils.isNotEmpty(this.endpoint)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("endpoint:").append(this.endpoint);
		}
		if(StringUtils.isNotEmpty(this.basicUsername)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("user:").append(this.basicUsername);
		}
		if(StringUtils.isNotEmpty(this.bearerToken)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("token:").append(this.bearerToken);
		}
		
		if(StringUtils.isNotEmpty(this.usernamePasswordGrant)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("usernamePasswordGrant:").append(this.usernamePasswordGrant);
		}
		
		if(StringUtils.isNotEmpty(this.signedJwtIssuer) && (!cacheKey || signedJwtIssuer_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("iss:").append(this.signedJwtIssuer);
		}
		if(StringUtils.isNotEmpty(this.signedJwtClientId) && (!cacheKey || signedJwtClientId_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("clientId:").append(this.signedJwtClientId);
		}
		if(StringUtils.isNotEmpty(this.signedJwtSubject) && (!cacheKey || signedJwtSubject_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("sub:").append(this.signedJwtSubject);
		}
		if(StringUtils.isNotEmpty(this.signedJwtAudience) && (!cacheKey || signedJwtAudience_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("aud:").append(this.signedJwtAudience);
		}
		if(StringUtils.isNotEmpty(this.signedJwtJti) && (!cacheKey || signedJwtJti_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("jti:").append(this.signedJwtJti);
		}
		if(StringUtils.isNotEmpty(this.signedJwtPurposeId) && (!cacheKey || signedJwtPurposeId_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("purposeId:").append(this.signedJwtPurposeId);
		}
		if(StringUtils.isNotEmpty(this.signedJwtSessionInfo) && (!cacheKey || signedJwtSessionInfo_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("sInfo:").append(this.signedJwtSessionInfo);
		}
		if(StringUtils.isNotEmpty(this.signedJwtClaims) && (!cacheKey || signedJwtClaims_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("claims:").append(this.signedJwtClaims);
		}
		
		if(StringUtils.isNotEmpty(this.signedJwtCustomId) && (!cacheKey || signedJwtCustomId_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("kid:").append(this.signedJwtCustomId);
		}
		if(StringUtils.isNotEmpty(this.signedJwtX509Url) && (!cacheKey || signedJwtX509Url_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("x5u:").append(this.signedJwtX509Url);
		}
		
		if(StringUtils.isNotEmpty(this.scope) && (!cacheKey || scope_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("scope:").append(this.scope);
		}
		if(StringUtils.isNotEmpty(this.audience) && (!cacheKey || audience_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("formAudience:").append(this.audience);
		}
		if(StringUtils.isNotEmpty(this.formClientId) && (!cacheKey || formClientId_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("formClientId:").append(this.formClientId);
		}
		if(StringUtils.isNotEmpty(this.formResource) && (!cacheKey || formResource_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("formResource:").append(this.formResource);
		}
		if(StringUtils.isNotEmpty(this.parameters) && (!cacheKey || parameters_cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("parameters:").append(this.parameters);
		}
		
		return sb.toString();
	}
	
	public String getEndpoint() {
		return this.endpoint;
	}

	public String getBasicUsername() {
		return this.basicUsername;
	}
	
	public String getBearerToken() {
		return this.bearerToken;
	}
	
	public String getUsernamePasswordGrant() {
		return this.usernamePasswordGrant;
	}
	
	public String getSignedJwtIssuer() {
		return this.signedJwtIssuer;
	}

	public String getSignedJwtClientId() {
		return this.signedJwtClientId;
	}

	public String getSignedJwtSubject() {
		return this.signedJwtSubject;
	}

	public String getSignedJwtAudience() {
		return this.signedJwtAudience;
	}

	public String getSignedJwtJti() {
		return this.signedJwtJti;
	}

	public String getSignedJwtPurposeId() {
		return this.signedJwtPurposeId;
	}

	public String getSignedJwtSessionInfo() {
		return this.signedJwtSessionInfo;
	}

	public String getSignedJwtClaims() {
		return this.signedJwtClaims;
	}
	
	public String getSignedJwtCustomId() {
		return this.signedJwtCustomId;
	}
	
	public String getSignedJwtX509Url() {
		return this.signedJwtX509Url;
	}
	
	public String getScope() {
		return this.scope;
	}
	
	public String getAudience() {
		return this.audience;
	}

	public String getFormClientId() {
		return this.formClientId;
	}
	
	public String getFormResource() {
		return this.formResource;
	}

	public String getParameters() {
		return this.parameters;
	}
}
