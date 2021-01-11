/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.token.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.core.token.TokenUtilities;

/**     
 * BasicTokenParser
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicTokenParser implements ITokenParser {

	protected Integer httpResponseCode;
	protected String raw;
	protected Map<String, Object> claims;
	protected TipologiaClaims parser;
	protected ITokenUserInfoParser userInfoParser;
	
	public BasicTokenParser(TipologiaClaims parser) {
		this.parser = parser;
		this.userInfoParser = new BasicTokenUserInfoParser(parser);
	}
	
	@Override
	public void init(String raw, Map<String, Object> claims) {
		this.raw = raw;
		this.claims = claims;
		this.userInfoParser.init(raw, claims);
	}

	public static String getClaimAsString(Map<String, Object> claims, String claim) {
		List<String> l = getClaimAsList(claims, claim);
		if(l==null || l.isEmpty()) {
			return null;
		}
		String claimValue = TokenUtilities.getClaimValuesAsString(l);
		return claimValue;
	}
	public static List<String> getClaimAsList(Map<String, Object> claims, String claim) {
		Object o = claims.get(claim);
		if(o==null) {
			return null;
		}
		List<String> l = TokenUtilities.getClaimValues(o);
		if(l==null || l.isEmpty()) {
			return null;
		}
		return l;
	}
	
	@Override
	public void checkHttpTransaction(Integer httpResponseCode) throws Exception{
		this.httpResponseCode = httpResponseCode;
		switch (this.parser) {
		case INTROSPECTION_RESPONSE_RFC_7662:
		case JSON_WEB_TOKEN_RFC_7519:
		case OIDC_ID_TOKEN:
		case CUSTOM:
			if(this.httpResponseCode!=null && 
				(this.httpResponseCode.intValue() < 200 || this.httpResponseCode.intValue()>299)) {
				String msgError = "Connessione terminata con errore (codice trasporto: "+this.httpResponseCode.intValue()+")";
				throw new Exception(msgError+": "+this.raw);
			}
			break;
		case GOOGLE:
			if(this.httpResponseCode!=null) {
				if(this.httpResponseCode.intValue() == 400 || this.httpResponseCode.intValue()==401) {
					if(this.claims!=null && this.claims.size()>0) {
						String tmp = getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ERROR);
						if(tmp==null) {
							tmp = getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ERROR_DESCRIPTION);
						}
						if(tmp!=null) {
							return;
						}
					}
				}	
			}
			if(this.httpResponseCode!=null && 
				(this.httpResponseCode.intValue() < 200 || this.httpResponseCode.intValue()>299)) {
				String msgError = "Connessione terminata con errore (codice trasporto: "+this.httpResponseCode.intValue()+")";
				throw new Exception(msgError+": "+this.raw);
			}
			break;
		}
	}
	
	@Override
	public boolean isValid() {
		
		if(this.claims==null || this.claims.size()<=0) {
			return false;
		}
		
		switch (this.parser) {
		case INTROSPECTION_RESPONSE_RFC_7662:
			String claim = getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_ACTIVE);
			return Boolean.valueOf(claim);
		case JSON_WEB_TOKEN_RFC_7519:
		case OIDC_ID_TOKEN:
		case CUSTOM:
			return true;
		case GOOGLE:
			if(this.httpResponseCode==null) {
				return true;
			}
			else {
				if(this.httpResponseCode.intValue() == 400 || this.httpResponseCode.intValue()==401) {
					String tmp = getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ERROR);
					if(tmp==null) {
						tmp = getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ERROR_DESCRIPTION);
					}
					if(tmp!=null) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public String getIssuer() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_ISSUER);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER);
		case OIDC_ID_TOKEN:
			return getClaimAsString(this.claims,Claims.OIDC_ID_TOKEN_ISSUER);
		case GOOGLE:
			return getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ISSUER);
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public String getSubject() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_SUBJECT);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT);
		case OIDC_ID_TOKEN:
			return getClaimAsString(this.claims,Claims.OIDC_ID_TOKEN_SUBJECT);
		case GOOGLE:
			return getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_SUBJECT);
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public String getUsername() {
		
		// NOTA: e' importante per le ricerche, quindi va bene anche nome e cognome se non c'è username
		
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return null; // unsupported
		case INTROSPECTION_RESPONSE_RFC_7662:
			return getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_USERNAME);
		case OIDC_ID_TOKEN:
			String tmp = getClaimAsString(this.claims,Claims.OIDC_ID_CLAIMS_PREFERRED_USERNAME);
			if(tmp==null) {
				if(this.getUserInfoParser()!=null) {
					return this.getUserInfoParser().getFullName();
				}
			}
			return tmp;
		case GOOGLE:
			if(this.getUserInfoParser()!=null) {
				return this.getUserInfoParser().getFullName();
			}
			return null;
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public List<String> getAudience() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return getClaimAsList(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return getClaimAsList(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE);
		case OIDC_ID_TOKEN:
			return getClaimAsList(this.claims,Claims.OIDC_ID_TOKEN_AUDIENCE);
		case GOOGLE:
			return getClaimAsList(this.claims,Claims.GOOGLE_CLAIMS_AUDIENCE);
		case CUSTOM:
			return null;
		}
		return  null;
	}

	@Override
	public Date getExpired() {
		String tmp = null;
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			tmp =  getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED);
			break;
		case INTROSPECTION_RESPONSE_RFC_7662:
			tmp =  getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_EXPIRED);
			break;
		case OIDC_ID_TOKEN:
			tmp =  getClaimAsString(this.claims,Claims.OIDC_ID_TOKEN_EXPIRED);
			break;
		case GOOGLE:
			tmp =  getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_EXPIRED);
			break;
		case CUSTOM:
			return null;
		}
		if(tmp!=null) {
			return TokenUtils.parseTimeInSecond(tmp);
		}
		return null;
	}

	@Override
	public Date getIssuedAt() {
		String tmp = null;
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			tmp =  getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT);
			break;
		case INTROSPECTION_RESPONSE_RFC_7662:
			tmp =  getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUED_AT);
			break;
		case OIDC_ID_TOKEN:
			tmp =  getClaimAsString(this.claims,Claims.OIDC_ID_TOKEN_ISSUED_AT);
			break;
		case GOOGLE:
			tmp =  getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ISSUED_AT);
			break;
		case CUSTOM:
			return null;
		}
		if(tmp!=null) {
			return TokenUtils.parseTimeInSecond(tmp);
		}
		return null;
	}

	@Override
	public Date getNotToBeUsedBefore() {
		String tmp = null;
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			tmp =  getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE);
			break;
		case INTROSPECTION_RESPONSE_RFC_7662:
			tmp =  getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_NOT_TO_BE_USED_BEFORE);
			break;
		case OIDC_ID_TOKEN:
		case GOOGLE:
			return null; // unsupported
		case CUSTOM:
			return null;
		}
		if(tmp!=null) {
			return TokenUtils.parseTimeInSecond(tmp);
		}
		return null;
	}

	@Override
	public String getJWTIdentifier() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
			return getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_JWT_ID);
		case OIDC_ID_TOKEN:
		case GOOGLE:
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
			return getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID);
		case OIDC_ID_TOKEN:
			return getClaimAsString(this.claims,Claims.OIDC_ID_TOKEN_AZP);
		case GOOGLE:
			return getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_AZP);
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
		
		String tmpScopes = null;
		if(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662.equals(this.parser)) {
			tmpScopes = getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_SCOPE);
		}
		else if(TipologiaClaims.GOOGLE.equals(this.parser)) {
			tmpScopes = getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_SCOPE);
		}
		if(tmpScopes!=null) {
			String [] tmpArray = tmpScopes.split(" ");
			if(tmpArray!=null && tmpArray.length>0) {
				List<String> scopes = new ArrayList<>();
				for (int i = 0; i < tmpArray.length; i++) {
					scopes.add(tmpArray[i].trim());
				}
				return scopes;
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
