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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.core.token.parser.INegoziazioneTokenParser;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**     
 * InformazioniToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniNegoziazioneToken extends org.openspcoop2.utils.beans.BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InformazioniNegoziazioneToken() {} // per serializzatore
	public InformazioniNegoziazioneToken(InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta,
			String rawResponse, INegoziazioneTokenParser tokenParser) throws Exception {
		this(datiRichiesta,null,rawResponse,tokenParser);
	}
	public InformazioniNegoziazioneToken(InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta,
			Integer httpResponseCode, String rawResponse, INegoziazioneTokenParser tokenParser) throws Exception {
		this(datiRichiesta, httpResponseCode, rawResponse, tokenParser, null);
	}
	public InformazioniNegoziazioneToken(InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta,
			Integer httpResponseCode, String rawResponse, INegoziazioneTokenParser tokenParser, InformazioniNegoziazioneToken previousToken) throws Exception {
		this.rawResponse = rawResponse;
		JSONUtils jsonUtils = JSONUtils.getInstance();
		if(jsonUtils.isJson(this.rawResponse)) {
			JsonNode root = jsonUtils.getAsNode(this.rawResponse);
			Map<String, Object> readClaims = jsonUtils.convertToSimpleMap(root);
			if(readClaims!=null && readClaims.size()>0) {
				this.claims.putAll(readClaims);
			}
		}
		tokenParser.init(this.rawResponse, this.claims);
		if(httpResponseCode!=null) {
			tokenParser.checkHttpTransaction(httpResponseCode);
		}
		this.request = datiRichiesta;
		this.valid = tokenParser.isValid();
		this.accessToken = tokenParser.getAccessToken();
		this.refreshToken = tokenParser.getRefreshToken();
		this.retrievedIn = DateManager.getDate();
		this.expiresIn = tokenParser.getExpired();
		this.retrievedRefreshTokenIn = this.retrievedIn;
		this.refreshExpiresIn = tokenParser.getRefreshExpired();
		this.tokenType = tokenParser.getTokenType();
		List<String> s = tokenParser.getScopes(); 
		if(s!=null && s.size()>0) {
			if(this.scopes == null) {
				this.scopes = new ArrayList<>();
			}
			this.scopes.addAll(s);
		}
		
		if(this.refreshToken==null && previousToken!=null) {
			// token aggiornato tramite refresh mode, e nella risposta non era presente nuovamente un nuovo refresh token
			this.refreshToken = previousToken.getRefreshToken();
			this.retrievedRefreshTokenIn = previousToken.getRetrievedRefreshTokenIn();
			this.refreshExpiresIn = previousToken.getRefreshExpiresIn();
		}
	}
	
		
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json
	
	private TipoInformazioni type = TipoInformazioni.retrieved_token;
	
	private InformazioniNegoziazioneToken_DatiRichiesta request;
	
	// Indicazione se il token e' valido
	private boolean valid;
		
	// String representing the access token issued by the authorization server [RFC6749].
	private String accessToken;
	
	// String representing the refresh token, which can be used to obtain new access tokens using the same authorization grant
	public String refreshToken;

	// Data in cui il token è stato recuperato
	public Date retrievedIn;
	
	// The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
	// expire in one hour from the time the response was generated.
    // If omitted, the authorization server SHOULD provide the expiration time via other means or document the default value.
	public Date expiresIn;
	
	// Data in cui il refresh token è stato recuperato
	public Date retrievedRefreshTokenIn;
	
	// The lifetime in seconds of the refresh token.  For example, the value "3600" denotes that the access token will
	// expire in one hour from the time the response was generated.
    // If omitted, the authorization server SHOULD provide the expiration time via other means or document the default value.
	public Date refreshExpiresIn;
	
	// The type of the token issued
	public String tokenType;
	
	// Scopes
	private List<String> scopes;
	
	// Claims
	private Map<String,Object> claims = new HashMap<String,Object>();
		
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json
	
	// RawResponse
	private String rawResponse;
		
	
	public TipoInformazioni getType() {
		return this.type;
	}
	public void setType(TipoInformazioni type) {
		this.type = type;
	}
	
	public InformazioniNegoziazioneToken_DatiRichiesta getRequest() {
		return this.request;
	}
	public void setRequest(InformazioniNegoziazioneToken_DatiRichiesta request) {
		this.request = request;
	}
	
	public boolean isValid() {
		return this.valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public String getAccessToken() {
		return this.accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getRefreshToken() {
		return this.refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public Date getRetrievedIn() {
		return this.retrievedIn;
	}
	public void setRetrievedIn(Date retrievedIn) {
		this.retrievedIn = retrievedIn;
	}
	
	public Date getExpiresIn() {
		return this.expiresIn;
	}
	public void setExpiresIn(Date expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	public Date getRetrievedRefreshTokenIn() {
		return this.retrievedRefreshTokenIn;
	}
	public void setRetrievedRefreshTokenIn(Date retrievedRefreshTokenIn) {
		this.retrievedRefreshTokenIn = retrievedRefreshTokenIn;
	}
	
	public Date getRefreshExpiresIn() {
		return this.refreshExpiresIn;
	}
	public void setRefreshExpiresIn(Date refreshExpiresIn) {
		this.refreshExpiresIn = refreshExpiresIn;
	}
	
	public String getTokenType() {
		return this.tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
		
	public List<String> getScopes() {
		return this.scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	public Map<String, Object> getClaims() {
		return this.claims;
	}

	public void setClaims(Map<String, Object> claims) {
		this.claims = claims;
	}
			
	public String getRawResponse() {
		return this.rawResponse;
	}
	public void replaceInRawResponse(String original, String newS) {
		if(this.rawResponse!=null) {
			this.rawResponse = this.rawResponse.replace(original, newS);
		}
	}
	
}
