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
package org.openspcoop2.pdd.core.token.attribute_authority;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.token.AbstractDynamicParameters;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**
 * AttributeAuthorityDynamicParameter
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttributeAuthorityDynamicParameters extends AbstractDynamicParameters {

	@SuppressWarnings("unused")
	private PolicyAttributeAuthority policyAttributeAuthority;
	
	private String endpoint;
	private String basicUsername;
	private String basicPassword;
	private String bearerToken;
	
	private String requestDynamicPayloadTemplate;
	
	private String issuer;
	private String subject;
	private String audience;
	private String claims;
	
	private String responseAudience;
	
	public AttributeAuthorityDynamicParameters(Map<String, Object> dynamicMap, 
			PdDContext pddContext, RequestInfo requestInfo,
			PolicyAttributeAuthority policyAttributeAuthority) throws Exception {
		super(dynamicMap, pddContext, requestInfo);
		
		this.policyAttributeAuthority = policyAttributeAuthority;
		
		this.endpoint = policyAttributeAuthority.getEndpoint();
		if(this.endpoint!=null && !"".equals(this.endpoint)) {
			this.endpoint = DynamicUtils.convertDynamicPropertyValue("endpoint.gwt", this.endpoint, dynamicMap, pddContext);	
		}
		
		boolean basic = policyAttributeAuthority.isBasicAuthentication();
		if(basic) {
			this.basicUsername = policyAttributeAuthority.getBasicAuthentication_username();
			if(this.basicUsername!=null && !"".equals(this.basicUsername)) {
				this.basicUsername = DynamicUtils.convertDynamicPropertyValue("username.gwt", this.basicUsername, dynamicMap, pddContext);	
			}
			
			this.basicPassword = policyAttributeAuthority.getBasicAuthentication_password();
			if(this.basicPassword!=null && !"".equals(this.basicPassword)) {
				this.basicPassword = DynamicUtils.convertDynamicPropertyValue("password.gwt", this.basicPassword, dynamicMap, pddContext);	
			}
		}
		
		boolean bearer = policyAttributeAuthority.isBearerAuthentication();
		if(bearer) {
			this.bearerToken = policyAttributeAuthority.getBeareAuthentication_token();
			if(this.bearerToken!=null && !"".equals(this.bearerToken)) {
				this.bearerToken = DynamicUtils.convertDynamicPropertyValue("bearerToken.gwt", this.bearerToken, dynamicMap, pddContext);	
			}
		}
		
		if(policyAttributeAuthority.isRequestDynamicPayloadTemplate() || policyAttributeAuthority.isRequestDynamicPayloadJwt()) {
			if(policyAttributeAuthority.isRequestDynamicPayloadTemplate()) {
				this.requestDynamicPayloadTemplate = DynamicUtils.convertDynamicPropertyValue("AADynamicRequest.gwt", policyAttributeAuthority.getRequestDynamicPayload(), dynamicMap, pddContext);
			}
			else {
				this.issuer = policyAttributeAuthority.getRequestJwtIssuer();
				if(this.issuer!=null && !"".equals(this.issuer)) {
					this.issuer = DynamicUtils.convertDynamicPropertyValue("issuer.gwt", this.issuer, dynamicMap, pddContext);	
				}
				
				this.subject = policyAttributeAuthority.getRequestJwtSubject();
				if(this.subject!=null && !"".equals(this.subject)) {
					this.subject = DynamicUtils.convertDynamicPropertyValue("subject.gwt", this.subject, dynamicMap, pddContext);	
				}
				
				this.audience = policyAttributeAuthority.getRequestJwtAudience();
				if(this.audience!=null && !"".equals(this.subject)) {
					this.audience = DynamicUtils.convertDynamicPropertyValue("audience.gwt", this.audience, dynamicMap, pddContext);	
				}
				
				this.claims = policyAttributeAuthority.getRequestJwtClaims();
				if(this.claims!=null && !"".equals(this.claims)) {
					this.claims = DynamicUtils.convertDynamicPropertyValue("claims.gwt", this.claims, dynamicMap, pddContext);	
				}
			}
		}
		
		this.responseAudience = policyAttributeAuthority.getResponseAudience();
		if(this.responseAudience!=null && !"".equals(this.responseAudience)) {
			this.responseAudience = DynamicUtils.convertDynamicPropertyValue("responseAudience.gwt", this.responseAudience, dynamicMap, pddContext);	
		}
	}
	
	@Override
	protected String toStringRepresentation() {
		return null; // viene ridefinito il metodo toString
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
		if(StringUtils.isNotEmpty(this.basicPassword) && (!cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("password:").append(this.basicPassword);
		}
		if(StringUtils.isNotEmpty(this.bearerToken)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("token:").append(this.bearerToken);
		}
		
		if(!cacheKey) {
			// Altrimenti questi parametri concorrono alla realizzazione della richiesta che viene poi aggiunta in cache.
			if(StringUtils.isNotEmpty(this.requestDynamicPayloadTemplate)) {
				if(sb.length()>0) {
					sb.append(separator);
				}
				sb.append("requestDynamicPayloadTemplate:").append(this.requestDynamicPayloadTemplate);
			}
			if(StringUtils.isNotEmpty(this.issuer)) {
				if(sb.length()>0) {
					sb.append(separator);
				}
				sb.append("issuer:").append(this.issuer);
			}
			if(StringUtils.isNotEmpty(this.subject)) {
				if(sb.length()>0) {
					sb.append(separator);
				}
				sb.append("subject:").append(this.subject);
			}
			if(StringUtils.isNotEmpty(this.audience)) {
				if(sb.length()>0) {
					sb.append(separator);
				}
				sb.append("audience:").append(this.audience);
			}
			if(StringUtils.isNotEmpty(this.claims)) {
				if(sb.length()>0) {
					sb.append(separator);
				}
				sb.append("claims:").append(this.claims);
			}
		}
		
		if(StringUtils.isNotEmpty(this.responseAudience)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("responseAudience:").append(this.responseAudience);
		}
		
		return sb.toString();
	}
	
	public String getEndpoint() {
		return this.endpoint;
	}

	public String getBasicUsername() {
		return this.basicUsername;
	}
	public String getBasicPassword() {
		return this.basicPassword;
	}
	
	public String getBearerToken() {
		return this.bearerToken;
	}
	
	public String getRequestDynamicPayloadTemplate() {
		return this.requestDynamicPayloadTemplate;
	}
	
	public String getIssuer() {
		return this.issuer;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getAudience() {
		return this.audience;
	}

	public String getClaims() {
		return this.claims;
	}
	
	public String getResponseAudience() {
		return this.responseAudience;
	}
}
