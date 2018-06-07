package org.openspcoop2.pdd.core.token.parser;

import java.util.Map;

public class BasicTokenUserInfoParser implements ITokenUserInfoParser {

	protected String raw;
	protected Map<String, String> claims;
	protected TipologiaClaims parser;
	
	public BasicTokenUserInfoParser(TipologiaClaims parser) {
		this.parser = parser;
	}
	
	@Override
	public void init(String raw, Map<String, String> claims) {
		this.raw = raw;
		this.claims = claims;
	}

	@Override
	public String getFullName() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_CLAIMS_NAME);
		case JSON_WEB_TOKEN_RFC_7519:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	@Override
	public String getFirstName() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_CLAIMS_GIVE_NAME);
		case JSON_WEB_TOKEN_RFC_7519:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	@Override
	public String getMiddleName() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_CLAIMS_MIDDLE_NAME);
		case JSON_WEB_TOKEN_RFC_7519:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	
	@Override
	public String getFamilyName() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_CLAIMS_FAMILY_NAME);
		case JSON_WEB_TOKEN_RFC_7519:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	
	@Override
	public String getEMail() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_CLAIMS_EMAIL);
		case JSON_WEB_TOKEN_RFC_7519:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	
}
