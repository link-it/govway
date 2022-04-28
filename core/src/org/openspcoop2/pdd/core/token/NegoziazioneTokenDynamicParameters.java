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
	private String parameters;
	

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
		
		if(StringUtils.isNotEmpty(this.signedJwtIssuer)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("iss:").append(this.signedJwtIssuer);
		}
		if(StringUtils.isNotEmpty(this.signedJwtClientId)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("clientId:").append(this.signedJwtClientId);
		}
		if(StringUtils.isNotEmpty(this.signedJwtSubject)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("sub:").append(this.signedJwtSubject);
		}
		if(StringUtils.isNotEmpty(this.signedJwtAudience)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("aud:").append(this.signedJwtAudience);
		}
		if(StringUtils.isNotEmpty(this.signedJwtJti)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("jti:").append(this.signedJwtJti);
		}
		if(StringUtils.isNotEmpty(this.signedJwtPurposeId)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("purposeId:").append(this.signedJwtPurposeId);
		}
		if(StringUtils.isNotEmpty(this.signedJwtSessionInfo)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("sInfo:").append(this.signedJwtSessionInfo);
		}
		if(StringUtils.isNotEmpty(this.signedJwtClaims)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("claims:").append(this.signedJwtClaims);
		}
		
		if(StringUtils.isNotEmpty(this.signedJwtCustomId)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("kid:").append(this.signedJwtCustomId);
		}
		if(StringUtils.isNotEmpty(this.signedJwtX509Url)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("x5u:").append(this.signedJwtX509Url);
		}
		
		if(StringUtils.isNotEmpty(this.scope)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("scope:").append(this.scope);
		}
		if(StringUtils.isNotEmpty(this.audience)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("formAudience:").append(this.audience);
		}
		if(StringUtils.isNotEmpty(this.formClientId)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("formClientId:").append(this.formClientId);
		}
		if(StringUtils.isNotEmpty(this.parameters)) {
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

	public String getParameters() {
		return this.parameters;
	}
}
