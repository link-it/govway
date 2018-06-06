package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.core.token.parser.ITokenParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class InformazioniToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InformazioniToken(String rawResponse, ITokenParser tokenParser, TipologiaClaims tipologiaClaims) throws UtilsException {
		this.rawResponse = rawResponse;
		JSONUtils jsonUtils = JSONUtils.getInstance();
		if(jsonUtils.isJson(this.rawResponse)) {
			JsonNode root = jsonUtils.getAsNode(this.rawResponse);
			Map<String, String> readClaims = jsonUtils.convertToSimpleMap(root);
			if(readClaims!=null && readClaims.size()>0) {
				this.claims.putAll(readClaims);
			}
		}
		tokenParser.init(this.rawResponse, this.claims, tipologiaClaims);
	}
	
	// RawResponse
	private String rawResponse;
	
	// String representing the issuer of this token, as defined in JWT [RFC7519].
	private String iss; 
	
	// Subject of the token, as defined in JWT [RFC7519]. 
	// Usually a machine-readable identifier of the resource owner who authorized this token.
	private String sub; 
	
	// Human-readable identifier for the resource owner who authorized this token.
	private String username;
	
	// Service-specific string identifier or list of string identifiers representing the intended audience for this token, 
	// as defined in JWT [RFC7519].
	private String aud;
	
	// Integer timestamp, measured in the number of seconds since January 1 1970 UTC, 
	// indicating when this token will expire, as defined in JWT [RFC7519].
	private Date exp;
	
	// Integer timestamp, measured in the number of seconds since January 1 1970 UTC, 
	// indicating when this token was originally issued, as defined in JWT [RFC7519].
	private Date iat;
	
	// Integer timestamp, measured in the number of seconds since January 1 1970 UTC, 
	// indicating when this token is not to be used before, as defined in JWT [RFC7519].
	private Date nbf;
	
	// Client identifier for the OAuth 2.0 client that requested this token.
	// Per un ID_TOKEN, If present, it MUST contain the OAuth 2.0 Client ID of this party.
	private String clientId; // in oidc e' azp
	
	// Ruoli di un utente
	private List<String> roles = new ArrayList<>();
	
	// Scopes
	private List<String> scopes = new ArrayList<>();
	
	// Claims
	private Map<String,String> claims = new HashMap<String,String>();

	
	public String getIss() {
		return this.iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public String getSub() {
		return this.sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAud() {
		return this.aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public Date getExp() {
		return this.exp;
	}

	public void setExp(Date exp) {
		this.exp = exp;
	}

	public Date getIat() {
		return this.iat;
	}

	public void setIat(Date iat) {
		this.iat = iat;
	}

	public Date getNbf() {
		return this.nbf;
	}

	public void setNbf(Date nbf) {
		this.nbf = nbf;
	}

	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public List<String> getRoles() {
		return this.roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<String> getScopes() {
		return this.scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	public Map<String, String> getClaims() {
		return this.claims;
	}

	public void setClaims(Map<String, String> claims) {
		this.claims = claims;
	}
	
	public String getRawResponse() {
		return this.rawResponse;
	}
}
