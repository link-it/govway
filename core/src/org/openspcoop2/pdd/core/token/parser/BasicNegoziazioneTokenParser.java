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
package org.openspcoop2.pdd.core.token.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.core.token.TokenUtilities;
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
	protected Map<String, Object> claims;
	protected TipologiaClaimsNegoziazione parser;
	protected Date now;
	
	public BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione parser) {
		this.parser = parser;
	}
	
	@Override
	public void init(String raw, Map<String, Object> claims) {
		this.raw = raw;
		this.claims = claims;
		this.now = DateManager.getDate();
	}

	
	@Override
	public void checkHttpTransaction(Integer httpResponseCode) throws Exception{
		this.httpResponseCode = httpResponseCode;
		switch (this.parser) {
		case OAUTH2_RFC_6749:
		case CUSTOM:
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
		case OAUTH2_RFC_6749:
			String claim = TokenUtilities.getClaimAsString(this.claims,ClaimsNegoziazione.OAUTH2_RFC_6749_ACCESS_TOKEN);
			return !StringUtils.isEmpty(claim);
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
		case CUSTOM:
			return null;
		}
		return null;
	}
	
	@Override
	public Date getExpired() {
		String tmp = null;
		switch (this.parser) {
		case OAUTH2_RFC_6749:
			tmp =  TokenUtilities.getClaimAsString(this.claims,ClaimsNegoziazione.OAUTH2_RFC_6749_EXPIRES_IN);
			break;
		case CUSTOM:
			return null;
		}
		if(tmp!=null) {
			
			// The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
			// expire in one hour from the time the response was generated.
			return TokenUtils.convertLifeTimeInSeconds(this.now, tmp);

		}
		return null;
	}
	
	@Override
	public Date getRefreshExpired() {
		String tmp = null;
		switch (this.parser) {
		case OAUTH2_RFC_6749:
			for (String claim : ClaimsNegoziazione.REFRESH_EXPIRE_CUSTOM_CLAIMS) {
				String tmpC =  TokenUtilities.getClaimAsString(this.claims, claim);
				if(tmpC!=null && StringUtils.isNotEmpty(tmpC)) {
					tmp = tmpC;
					break;
				}
			}
			
			break;
		case CUSTOM:
			return null;
		}
		if(tmp!=null) {
			
			// The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
			// expire in one hour from the time the response was generated.
			return TokenUtils.convertLifeTimeInSeconds(this.now, tmp);

		}
		return null;
	}



	@Override
	public List<String> getScopes() {
		
		String tmpScopes = null;
		if(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749.equals(this.parser)) {
			tmpScopes = TokenUtilities.getClaimAsString(this.claims,ClaimsNegoziazione.OAUTH2_RFC_6749_SCOPE);
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
	
}
