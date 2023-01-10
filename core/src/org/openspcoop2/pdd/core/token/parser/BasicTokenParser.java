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
package org.openspcoop2.pdd.core.token.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.pdd.core.token.Costanti;
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
	protected Properties parserConfig;
	protected ITokenUserInfoParser userInfoParser;
	
	public BasicTokenParser(TipologiaClaims parser) {
		this(parser, null);
	}
	public BasicTokenParser(TipologiaClaims parser, Properties parserConfig) {
		this.parser = parser;
		this.parserConfig = parserConfig;
		this.userInfoParser = new BasicTokenUserInfoParser(parser, this.parserConfig);
	}
	
	@Override
	public void init(String raw, Map<String, Object> claims) {
		this.raw = raw;
		this.claims = claims;
		this.userInfoParser.init(raw, claims);
	}
	
	@Override
	public void checkHttpTransaction(Integer httpResponseCode) throws Exception{
		this.httpResponseCode = httpResponseCode;
		switch (this.parser) {
		case INTROSPECTION_RESPONSE_RFC_7662:
		case JSON_WEB_TOKEN_RFC_7519:
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
		case OIDC_ID_TOKEN:
		case MAPPING:
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
						String tmp = TokenUtilities.getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ERROR);
						if(tmp==null) {
							tmp = TokenUtilities.getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ERROR_DESCRIPTION);
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
			String claim = TokenUtilities.getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_ACTIVE);
			return Boolean.valueOf(claim);
		case JSON_WEB_TOKEN_RFC_7519:
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
		case OIDC_ID_TOKEN:
		case MAPPING:
		case CUSTOM:
			return true;
		case GOOGLE:
			if(this.httpResponseCode==null) {
				return true;
			}
			else {
				if(this.httpResponseCode.intValue() == 400 || this.httpResponseCode.intValue()==401) {
					String tmp = TokenUtilities.getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ERROR);
					if(tmp==null) {
						tmp = TokenUtilities.getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ERROR_DESCRIPTION);
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
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
			return TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_ISSUER);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return TokenUtilities.getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER);
		case OIDC_ID_TOKEN:
			return TokenUtilities.getClaimAsString(this.claims,Claims.OIDC_ID_TOKEN_ISSUER);
		case GOOGLE:
			return TokenUtilities.getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ISSUER);
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_ISSUER);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public String getSubject() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
			return TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_SUBJECT);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return TokenUtilities.getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT);
		case OIDC_ID_TOKEN:
			return TokenUtilities.getClaimAsString(this.claims,Claims.OIDC_ID_TOKEN_SUBJECT);
		case GOOGLE:
			return TokenUtilities.getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_SUBJECT);
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_SUBJECT);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
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
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
			return null; // unsupported
		case INTROSPECTION_RESPONSE_RFC_7662:
			return TokenUtilities.getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_USERNAME);
		case OIDC_ID_TOKEN:
			String tmp = TokenUtilities.getClaimAsString(this.claims,Claims.OIDC_ID_CLAIMS_PREFERRED_USERNAME);
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
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_USERNAME);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public List<String> getAudience() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
			return TokenUtilities.getClaimAsList(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return TokenUtilities.getClaimAsList(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE);
		case OIDC_ID_TOKEN:
			return TokenUtilities.getClaimAsList(this.claims,Claims.OIDC_ID_TOKEN_AUDIENCE);
		case GOOGLE:
			return TokenUtilities.getClaimAsList(this.claims,Claims.GOOGLE_CLAIMS_AUDIENCE);
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_AUDIENCE);
			return TokenUtilities.getFirstClaimAsList(this.claims, claimNames);
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
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED);
			break;
		case INTROSPECTION_RESPONSE_RFC_7662:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_EXPIRED);
			break;
		case OIDC_ID_TOKEN:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.OIDC_ID_TOKEN_EXPIRED);
			break;
		case GOOGLE:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_EXPIRED);
			break;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_EXPIRE);
			tmp = TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
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
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT);
			break;
		case INTROSPECTION_RESPONSE_RFC_7662:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUED_AT);
			break;
		case OIDC_ID_TOKEN:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.OIDC_ID_TOKEN_ISSUED_AT);
			break;
		case GOOGLE:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_ISSUED_AT);
			break;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_ISSUED_AT);
			tmp = TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
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
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE);
			break;
		case INTROSPECTION_RESPONSE_RFC_7662:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_NOT_TO_BE_USED_BEFORE);
			break;
		case OIDC_ID_TOKEN:
		case GOOGLE:
			return null; // unsupported
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_NOT_TO_BE_USED_BEFORE);
			tmp = TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
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
	public String getJWTIdentifier() {
		switch (this.parser) {
		case JSON_WEB_TOKEN_RFC_7519:
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
			return TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return TokenUtilities.getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_JWT_ID);
		case OIDC_ID_TOKEN:
		case GOOGLE:
			return null; // unsupported
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_JWT_IDENTIFIER);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
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
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
			return TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068_CLIENT_ID);
		case INTROSPECTION_RESPONSE_RFC_7662:
			return TokenUtilities.getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID);
		case OIDC_ID_TOKEN:
			return TokenUtilities.getClaimAsString(this.claims,Claims.OIDC_ID_TOKEN_AZP);
		case GOOGLE:
			return TokenUtilities.getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_AZP);
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_CLIENT_ID);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public List<String> getRoles() {
		switch (this.parser) {
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_ROLE);
			return TokenUtilities.getFirstClaimAsList(this.claims, claimNames);
		case JSON_WEB_TOKEN_RFC_7519:
		case JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068:
		case INTROSPECTION_RESPONSE_RFC_7662:
		case OIDC_ID_TOKEN:
		case GOOGLE:
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public List<String> getScopes() {
		
		String tmpScopes = null;
		if(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662.equals(this.parser) ||
				TipologiaClaims.JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068.equals(this.parser) ||
				TipologiaClaims.OIDC_ID_TOKEN.equals(this.parser) ||
				TipologiaClaims.JSON_WEB_TOKEN_RFC_7519.equals(this.parser)) {
			tmpScopes = TokenUtilities.getClaimAsString(this.claims,Claims.INTROSPECTION_RESPONSE_RFC_7662_SCOPE);
		}
		else if(TipologiaClaims.GOOGLE.equals(this.parser)) {
			tmpScopes = TokenUtilities.getClaimAsString(this.claims,Claims.GOOGLE_CLAIMS_SCOPE);
		}
		else if(TipologiaClaims.MAPPING.equals(this.parser)) {
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.TOKEN_PARSER_SCOPE);
			List<String> tmpScopes_list = TokenUtilities.getFirstClaimAsList(this.claims, claimNames);
			if(tmpScopes_list!=null && !tmpScopes_list.isEmpty()) {
				List<String> scopes = new ArrayList<>();
				for (String s : tmpScopes_list) {
					List<String> tmp = readScope(s);
					if(tmp!=null && !tmp.isEmpty()) {
						for (String sTmp : tmp) {
							if(!scopes.contains(sTmp)) {
								scopes.add(sTmp);
							}
						}
					}
				}
				return scopes;
			}
			else {
				return null;
			}
		}
		
		return readScope(tmpScopes);
		
	}
	private List<String> readScope(String tmpScopes) {
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
