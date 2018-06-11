package org.openspcoop2.pdd.core.token.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BasicTokenParser implements ITokenParser {

	protected String raw;
	protected Map<String, String> claims;
	protected TipologiaClaims parser;
	private ITokenUserInfoParser userInfoParser;
	
	public BasicTokenParser(TipologiaClaims parser) {
		this.parser = parser;
		this.userInfoParser = new BasicTokenUserInfoParser(parser);
	}
	
	@Override
	public void init(String raw, Map<String, String> claims) {
		this.raw = raw;
		this.claims = claims;
		this.userInfoParser.init(raw, claims);
	}

	@Override
	public boolean isValid() {
		switch (this.parser) {
		case INTROSPECTION_RESPONSE_RFC_7662:
			String claim = this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_ACTIVE);
			return Boolean.valueOf(claim);
		case JSON_WEB_TOKEN_RFC_7519:
		case OIDC_ID_TOKEN:
		case CUSTOM:
			return true;
		}
		return true;
	}
	
	@Override
	public String getIssuer() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return this.claims.get(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUER);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER);
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_TOKEN_ISSUER);
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public String getSubject() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return this.claims.get(Claims.JSON_WEB_TOKEN_RFC_7519_SUBJECT);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT);
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_TOKEN_SUBJECT);
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public String getUsername() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return null; // unsupported
		case INTROSPECTION_RESPONSE_RFC_7662:
			return this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_USERNAME);
		case OIDC_ID_TOKEN:
			String tmp = this.claims.get(Claims.OIDC_ID_CLAIMS_PREFERRED_USERNAME);
			/*if(tmp==null) {
				tmp = this.claims.get(Claims.OIDC_ID_CLAIMS_NICKNAME);
			}*/
			return tmp;
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public String getAudience() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return this.claims.get(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE);
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_TOKEN_AUDIENCE);
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public Date getExpired() {
		String tmp = null;
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			tmp =  this.claims.get(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED);
			break;
		case INTROSPECTION_RESPONSE_RFC_7662:
			tmp =  this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_EXPIRED);
			break;
		case OIDC_ID_TOKEN:
			tmp =  this.claims.get(Claims.OIDC_ID_TOKEN_EXPIRED);
			break;
		case CUSTOM:
			return null;
		}
		if(tmp!=null) {
			long l = Long.valueOf(tmp);
			if(l>0) {
				l = l * 1000;
				return new Date(l);
			}
		}
		return null;
	}

	@Override
	public Date getIssuedAt() {
		String tmp = null;
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			tmp =  this.claims.get(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT);
			break;
		case INTROSPECTION_RESPONSE_RFC_7662:
			tmp =  this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUED_AT);
			break;
		case OIDC_ID_TOKEN:
			tmp =  this.claims.get(Claims.OIDC_ID_TOKEN_ISSUED_AT);
			break;
		case CUSTOM:
			return null;
		}
		if(tmp!=null) {
			long l = Long.valueOf(tmp);
			if(l>0) {
				l = l * 1000;
				return new Date(l);
			}
		}
		return null;
	}

	@Override
	public Date getNotToBeUsedBefore() {
		String tmp = null;
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			tmp =  this.claims.get(Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE);
			break;
		case INTROSPECTION_RESPONSE_RFC_7662:
			tmp =  this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_NOT_TO_BE_USED_BEFORE);
			break;
		case OIDC_ID_TOKEN:
			return null; // unsupported
		case CUSTOM:
			return null;
		}
		if(tmp!=null) {
			long l = Long.valueOf(tmp);
			if(l>0) {
				l = l * 1000;
				return new Date(l);
			}
		}
		return null;
	}

	@Override
	public String getJWTIdentifier() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return this.claims.get(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_JWT_ID);
		case OIDC_ID_TOKEN:
			return null; // unsupported
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	@Override
	public String getClientId() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return null; // unsupported
		case INTROSPECTION_RESPONSE_RFC_7662:
			return this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID);
		case OIDC_ID_TOKEN:
			return this.claims.get(Claims.OIDC_ID_TOKEN_AZP);
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public List<String> getRoles() {
		return null;
	}

	@Override
	public List<String> getScopes() {
		if(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662.equals(this.parser)) {
			String tmp = this.claims.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_SCOPE);
			if(tmp!=null) {
				String [] tmpArray = tmp.split(" ");
				if(tmpArray!=null && tmpArray.length>0) {
					List<String> scopes = new ArrayList<>();
					for (int i = 0; i < tmpArray.length; i++) {
						scopes.add(tmpArray[i].trim());
					}
					return scopes;
				}
			}
		}
		return null;
	}

	// ITokenUserInfoParser
	@Override
	public ITokenUserInfoParser getUserInfoParser() {
		return this.userInfoParser;
	}
}
