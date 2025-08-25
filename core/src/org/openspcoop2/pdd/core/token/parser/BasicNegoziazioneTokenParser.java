/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;

/**     
 * BasicTokenParser
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicNegoziazioneTokenParser implements INegoziazioneTokenParser {

	protected Integer httpResponseCode;
	protected String raw;
	protected Map<String, Serializable> claims;
	protected TipologiaClaimsNegoziazione parser;
	protected Properties parserConfig;
	protected Date now;
	
	public BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione parser) {
		this(parser, null);
	}
	public BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione parser, Properties parserConfig) {
		this.parser = parser;
		this.parserConfig = parserConfig;
	}
	
	@Override
	public void init(String raw, Map<String, Serializable> claims) {
		this.raw = raw;
		this.claims = claims;
		this.now = DateManager.getDate();
	}

	
	@Override
	public void checkHttpTransaction(Integer httpResponseCode) throws UtilsException{
		this.httpResponseCode = httpResponseCode;
		switch (this.parser) {
		case OAUTH2_RFC_6749:
		case MAPPING:
		case RAW:
		case CUSTOM:
			if(this.httpResponseCode!=null && 
				(this.httpResponseCode.intValue() < 200 || this.httpResponseCode.intValue()>299)) {
				String msgError = "Connessione terminata con errore (codice trasporto: "+this.httpResponseCode.intValue()+")";
				throw new UtilsException(msgError+": "+this.raw);
			}
			break;
		}
	}
	
	@Override
	public boolean isValid() {
		
		if(TipologiaClaimsNegoziazione.RAW.equals(this.parser)) {
			if(this.raw==null || StringUtils.isEmpty(this.raw)) {
				return false;
			}
		}
		else {
			if(this.claims==null || this.claims.size()<=0) {
				return false;
			}
		}
		
		switch (this.parser) {
		case OAUTH2_RFC_6749:
			String claim = TokenUtilities.getClaimAsString(this.claims,ClaimsNegoziazione.OAUTH2_RFC_6749_ACCESS_TOKEN);
			return !StringUtils.isEmpty(claim);
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.RETRIEVE_TOKEN_PARSER_ACCESS_TOKEN);
			if(claimNames==null || claimNames.isEmpty()) {
				return false;
			}
			claim = TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
			return !StringUtils.isEmpty(claim);
		case RAW:
		case CUSTOM:
			return true;
		}
		
		return false;
	}
	
	
	@Override
	public String getAccessToken() {
		switch (this.parser) {
		case OAUTH2_RFC_6749:
			String claim = TokenUtilities.getClaimAsString(this.claims,ClaimsNegoziazione.OAUTH2_RFC_6749_ACCESS_TOKEN);
			return claim;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.RETRIEVE_TOKEN_PARSER_ACCESS_TOKEN);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		case RAW:
			return this.raw;
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public String getRefreshToken() {
		switch (this.parser) {
		case OAUTH2_RFC_6749:
			String claim = TokenUtilities.getClaimAsString(this.claims,ClaimsNegoziazione.OAUTH2_RFC_6749_REFRESH_TOKEN);
			return claim;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.RETRIEVE_TOKEN_PARSER_REFRESH_TOKEN);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		case RAW:
		case CUSTOM:
			return null;
		}
		return null;
	}

	@Override
	public String getTokenType() {
		switch (this.parser) {
		case OAUTH2_RFC_6749:
			String claim = TokenUtilities.getClaimAsString(this.claims,ClaimsNegoziazione.OAUTH2_RFC_6749_TOKEN_TYPE);
			return claim;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.RETRIEVE_TOKEN_PARSER_TOKEN_TYPE);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		case RAW:
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	@Override
	public Date getExpired() {
		String tmpIn = null;
		String tmpOn = null;
		switch (this.parser) {
		case OAUTH2_RFC_6749:{
			tmpIn =  TokenUtilities.getClaimAsString(this.claims,ClaimsNegoziazione.OAUTH2_RFC_6749_EXPIRES_IN);
			if(tmpIn==null || StringUtils.isEmpty(tmpIn)) {
				tmpOn =  TokenUtilities.getClaimAsString(this.claims,ClaimsNegoziazione.AZURE_EXPIRES_ON);
			}
			break;
		}
		case MAPPING:{
			tmpIn = parseMappingExpiresIn();
			if(tmpIn==null || StringUtils.isEmpty(tmpIn)) {
				tmpOn = parseMappingExpiresOn();
			}
			break;
		}
		case RAW:{
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.RETRIEVE_TOKEN_PARSER_EXPIRES_IN_SECONDS);
			if(claimNames!=null && !claimNames.isEmpty()) {
				tmpIn = claimNames.get(0);
			}
			break;
		}
		case CUSTOM:
			return null;
		}
		
		return parseDate(tmpIn, tmpOn);
	}
	private String parseMappingExpiresIn() {
		List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.RETRIEVE_TOKEN_PARSER_EXPIRES_IN);
		if(claimNames!=null && !claimNames.isEmpty()) {
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		}
		return null;
	}
	private String parseMappingExpiresOn() {
		List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.RETRIEVE_TOKEN_PARSER_EXPIRES_ON);
		if(claimNames!=null && !claimNames.isEmpty()) {
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		}
		return null;
	}

	
	@Override
	public Date getRefreshExpired() {
		String tmpIn = null;
		String tmpOn = null;
		switch (this.parser) {
		case OAUTH2_RFC_6749:
			tmpIn = parseOauth2RFC6749RefreshExpiresIn();
			if(tmpIn==null || StringUtils.isEmpty(tmpIn)) {
				tmpOn = parseOauth2RFC6749RefreshExpiresOn();
			}
			break;
			
		case MAPPING:
			tmpIn = parseMappingRefreshExpiresIn();
			if(tmpIn==null || StringUtils.isEmpty(tmpIn)) {
				tmpOn = parseMappingRefreshExpiresOn();
			}
			break;
			
		case RAW:
		case CUSTOM:
			return null;
		}
		
		return parseDate(tmpIn, tmpOn);
	}
	private String parseOauth2RFC6749RefreshExpiresIn() {
		for (String claim : ClaimsNegoziazione.REFRESH_EXPIRE_IN_CUSTOM_CLAIMS) {
			String tmpC =  TokenUtilities.getClaimAsString(this.claims, claim);
			if(tmpC!=null && StringUtils.isNotEmpty(tmpC)) {
				return tmpC;
			}
		}
		return null;
	}
	private String parseOauth2RFC6749RefreshExpiresOn() {
		for (String claim : ClaimsNegoziazione.REFRESH_EXPIRE_ON_CUSTOM_CLAIMS) {
			String tmpC =  TokenUtilities.getClaimAsString(this.claims, claim);
			if(tmpC!=null && StringUtils.isNotEmpty(tmpC)) {
				return tmpC;
			}
		}
		return null;
	}
	private String parseMappingRefreshExpiresIn() {
		List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.RETRIEVE_TOKEN_PARSER_REFRESH_EXPIRES_IN);
		if(claimNames!=null && !claimNames.isEmpty()) {
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		}
		return null;
	}
	private String parseMappingRefreshExpiresOn() {
		List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.RETRIEVE_TOKEN_PARSER_REFRESH_EXPIRES_ON);
		if(claimNames!=null && !claimNames.isEmpty()) {
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		}
		return null;
	}
	
	
	private Date parseDate(String tmpIn, String tmpOn) {
		if(tmpIn!=null) {
			// The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
			// expire in one hour from the time the response was generated.
			return TokenUtils.convertLifeTimeInSeconds(this.now, tmpIn);
		}
		
		// provo a vedere se ci fosse un claim che indica la data di scadenza in forma https://www.rfc-editor.org/rfc/rfc7519#section-4.1.4
		if(tmpOn!=null) {
			return TokenUtils.parseTimeInSecond(tmpOn);
		}
		
		return null;
	}



	@Override
	public List<String> getScopes() {
		
		String tmpScopes = null;
		if(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749.equals(this.parser)) {
			tmpScopes = TokenUtilities.getClaimAsString(this.claims,ClaimsNegoziazione.OAUTH2_RFC_6749_SCOPE);
		}
		else if(TipologiaClaimsNegoziazione.MAPPING.equals(this.parser)) {
			return readMappingScopes();
		}
		
		return readScope(tmpScopes);
	}
	public List<String> readMappingScopes() {
		List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.RETRIEVE_TOKEN_PARSER_SCOPE);
		List<String> tmpScopesList = TokenUtilities.getFirstClaimAsList(this.claims, claimNames);
		List<String> lNull = null;
		if(tmpScopesList!=null && !tmpScopesList.isEmpty()) {
			return readMappingScopes(tmpScopesList);
		}
		else {
			return lNull;
		}
	}
	public List<String> readMappingScopes(List<String> tmpScopesList) {
		List<String> scopes = new ArrayList<>();
		for (String s : tmpScopesList) {
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
	
	private List<String> readScope(String tmpScopes) {
		List<String> lNull = null;
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
		return lNull;
	}
	
}
