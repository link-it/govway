/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.token.parser.ITokenParser;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**     
 * InformazioniToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniToken extends org.openspcoop2.utils.beans.BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InformazioniToken() {} // per serializzatore
	public InformazioniToken(SorgenteInformazioniToken sourceType, String rawResponse, ITokenParser tokenParser) throws Exception {
		this(null,sourceType, rawResponse,tokenParser);
	}
	public InformazioniToken(Integer httpResponseCode, SorgenteInformazioniToken sourceType, String rawResponse, ITokenParser tokenParser) throws Exception {
		this.rawResponse = rawResponse;
		this.sourceType = sourceType;
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
		this.valid = tokenParser.isValid();
		this.iss = tokenParser.getIssuer();
		this.sub = tokenParser.getSubject();
		this.username = tokenParser.getUsername();
		List<String> a = tokenParser.getAudience();
		if(a!=null && !a.isEmpty()) {
			this.aud = new ArrayList<>();
			this.aud.addAll(a);
		}
		this.exp = tokenParser.getExpired();
		this.iat = tokenParser.getIssuedAt();
		this.nbf = tokenParser.getNotToBeUsedBefore();
		this.clientId = tokenParser.getClientId();
		this.jti = tokenParser.getJWTIdentifier();
		List<String> r = tokenParser.getRoles();
		if(r!=null && !r.isEmpty()) {
			this.roles = new ArrayList<>();
			this.roles.addAll(r);
		}
		List<String> s = tokenParser.getScopes(); 
		if(s!=null && !s.isEmpty()) {
			this.scopes = new ArrayList<>();
			this.scopes.addAll(s);
		}
		if(tokenParser.getUserInfoParser()!=null) {
			this.userInfo = new InformazioniTokenUserInfo(this, tokenParser.getUserInfoParser());
		}
	}
	
	public InformazioniToken(String errorDetails, SorgenteInformazioniToken sourceType, String token) throws Exception {
		this(errorDetails,null,null,sourceType, token);
	}
	public InformazioniToken(String errorDetails, Integer httpResponseCode, byte[] rawResponse, SorgenteInformazioniToken sourceType, String token) throws Exception {
		this.valid = false;
		this.token = token;
		this.claims = null;
		this.sourceType = sourceType;
		if(httpResponseCode!=null) {
			this.httpResponseCode = httpResponseCode+"";
		}
		if(rawResponse!=null) {
			this.rawResponse = new String(rawResponse);
		}
		this.errorDetails = errorDetails;
	}
	
	@SuppressWarnings("unchecked")
	public InformazioniToken(boolean saveSourceTokenInfo, InformazioniToken ... informazioniTokens ) throws Exception {
		if(informazioniTokens!=null && informazioniTokens.length>0) {
			
			if(saveSourceTokenInfo) {
				this.sourcesTokenInfo = new HashMap<>();
				for (int i = 0; i < informazioniTokens.length; i++) {
					this.sourcesTokenInfo.put(informazioniTokens[i].getSourceType(),
							informazioniTokens[i].getRawResponse());
				}
			}
			else {
				this.sourceTypes = new ArrayList<>();
				for (int i = 0; i < informazioniTokens.length; i++) {
					this.sourceTypes.add(informazioniTokens[i].getSourceType());
				}
			}
			
			for (int i = 0; i < informazioniTokens.length; i++) {
				if(informazioniTokens[i].getClaims().size()>0) {
					this.claims.putAll(informazioniTokens[i].getClaims());
				}
			}
			
			Object issV = getValue("iss", informazioniTokens); 
			if(issV instanceof String) {
				this.iss = (String) issV;
			}
			
			Object subV = getValue("sub", informazioniTokens); 
			if(subV instanceof String) {
				this.sub = (String) subV;
			}
			
			Object usernameV = getValue("username", informazioniTokens); 
			if(usernameV instanceof String) {
				this.username = (String) usernameV;
			}
			
			Object audV = getValue("aud", informazioniTokens); 
			if(audV instanceof List) {
				List<String> l = (List<String>) audV;
				if(!l.isEmpty()) {
					if(this.aud == null) {
						this.aud = new ArrayList<>();
					}
					this.aud.addAll(l);
				}
			}
			
			Object expV = getValue("exp", informazioniTokens); 
			if(expV instanceof Date) {
				this.exp = (Date) expV;
			}
			
			Object iatV = getValue("iat", informazioniTokens); 
			if(iatV instanceof Date) {
				this.iat = (Date) iatV;
			}
			
			Object nbfV = getValue("nbf", informazioniTokens); 
			if(nbfV instanceof Date) {
				this.nbf = (Date) nbfV;
			}
			
			Object clientIdV = getValue("clientId", informazioniTokens); 
			if(clientIdV instanceof String) {
				this.clientId = (String) clientIdV;
			}
			
			Object jtiV = getValue("jti", informazioniTokens); 
			if(jtiV instanceof String) {
				this.jti = (String) jtiV;
			}
			
			Object rolesV = getValue("roles", informazioniTokens); 
			if(rolesV instanceof List) {
				List<String> l = (List<String>) rolesV;
				if(!l.isEmpty()) {
					if(this.roles == null) {
						this.roles = new ArrayList<>();
					}
					this.roles.addAll(l);
				}
			}
			
			Object scopesV = getValue("scopes", informazioniTokens); 
			if(scopesV instanceof List) {
				List<String> l = (List<String>) scopesV;
				if(!l.isEmpty()) {
					if(this.scopes == null) {
						this.scopes = new ArrayList<>();
					}
					this.scopes.addAll(l);
				}
			}
			
			List<InformazioniTokenUserInfo> listUserInfo = new ArrayList<>();
			for (int i = 0; i < informazioniTokens.length; i++) {
				InformazioniTokenUserInfo userInfoV = informazioniTokens[i].getUserInfo();
				if(userInfoV!=null) {
					listUserInfo.add(userInfoV);
				}
			}
			if(listUserInfo.size()==1) {
				this.userInfo = listUserInfo.get(0);
			}
			else {
				this.userInfo = new InformazioniTokenUserInfo(listUserInfo.toArray(new InformazioniTokenUserInfo[1]));
			}
		}
	}
	private static Object getValue(String field, InformazioniToken ... informazioniTokens) throws Exception {
		Object tmp = null;
		List<Object> listTmp = new ArrayList<>();
		String getMethodName = "get"+((field.charAt(0)+"").toUpperCase())+field.substring(1);
		Method getMethod = InformazioniToken.class.getMethod(getMethodName);
		
		// La fusione avviene per precedenza dall'ultimo fino al primo (a meno che non sia una lista)
		for (int i = 0; i < informazioniTokens.length; i++) {
			InformazioniToken infoToken = informazioniTokens[i];
			Object o = getMethod.invoke(infoToken);
			if(o!=null) {
				if(o instanceof List<?>) {
					List<?> list = (List<?>) o;
					if(!list.isEmpty()) {
						for (Object object : list) {
							if(!listTmp.contains(object)) {
								listTmp.add(object);
							}
						}
					}
				}
				else {
					tmp = o;
				}
			}
		}
		if(!listTmp.isEmpty()) {
			return listTmp;
		}
		else {
			return tmp;
		}
	}
		
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json
	
	private TipoInformazioni type = TipoInformazioni.validated_token;
	
	// Indicazione se il token e' valido
	private boolean valid;
	
	// String representing the issuer of this token, as defined in JWT [RFC7519].
	private String iss; 
	
	// Subject of the token, as defined in JWT [RFC7519]. 
	// Usually a machine-readable identifier of the resource owner who authorized this token.
	private String sub; 
	
	// Human-readable identifier for the resource owner who authorized this token.
	private String username;
	
	// Service-specific string identifier or list of string identifiers representing the intended audience for this token, 
	// as defined in JWT [RFC7519].
	private List<String> aud;
	
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
	
	//  The "jti" (JWT ID) claim provides a unique identifier for the JWT.
	private String jti;
	
	// Ruoli di un utente
	private List<String> roles;
	
	// Scopes
	private List<String> scopes;

	// UserInfo
	private InformazioniTokenUserInfo userInfo;
			
	// Claims
	private Map<String,Object> claims = new HashMap<>();
		
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json

	// RawResponse
	private String rawResponse;
	
	// Token (nel caso di errori)
	private String token;
	
	// HttpCode (nel caso di errori)
	private String httpResponseCode;
	
	// Failed
	private String errorDetails;
	
	// SorgenteInformazioniToken
	private SorgenteInformazioniToken sourceType;
	
	// Multiple Source
	private List<SorgenteInformazioniToken> sourceTypes = null;
	private Map<SorgenteInformazioniToken,String> sourcesTokenInfo = null;
	
	// Attributes Authority (ulteriore informazione valorizzata con la funzionalità specifica)
	private InformazioniAttributi aa;
	
	// InformazioniNegoziazioneToken (ulteriore informazione valorizzata con la funzionalità specifica)
	private InformazioniNegoziazioneToken retrievedToken;
		
	// InformazioniPDND
	private Map<String,Object> pdnd = null;
	
	public TipoInformazioni getType() {
		return this.type;
	}
	public void setType(TipoInformazioni type) {
		this.type = type;
	}
	
	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
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

	public List<String> getAud() {
		return this.aud;
	}

	public void setAud(List<String> aud) {
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

	public String getJti() {
		return this.jti;
	}
	public void setJti(String jti) {
		this.jti = jti;
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

	public InformazioniTokenUserInfo getUserInfo() {
		return this.userInfo;
	}
	public void setUserInfo(InformazioniTokenUserInfo userInfo) {
		this.userInfo = userInfo;
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
	
	public String getToken() {
		return this.token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getHttpResponseCode() {
		return this.httpResponseCode;
	}
	public void setHttpResponseCode(String httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}
	
	public String getErrorDetails() {
		return this.errorDetails;
	}
	public void setErrorDetails(String errorDetails) {
		this.errorDetails = errorDetails;
	}
	
	public SorgenteInformazioniToken getSourceType() {
		return this.sourceType;
	}
	
	public Map<SorgenteInformazioniToken, String> getSourcesTokenInfo() {
		return this.sourcesTokenInfo;
	}

	public List<SorgenteInformazioniToken> getSourceTypes() {
		return this.sourceTypes;
	}

	public InformazioniAttributi getAa() {
		return this.aa;
	}
	public void setAa(InformazioniAttributi aa) {
		this.aa = aa;
	}
	
	public InformazioniNegoziazioneToken getRetrievedToken() {
		return this.retrievedToken;
	}
	public void setRetrievedToken(InformazioniNegoziazioneToken retrievedToken) {
		this.retrievedToken = retrievedToken;
	}
	
	public Map<String, Object> getPdnd() {
		return this.pdnd;
	}
	public void setPdnd(Map<String, Object> pdnd) {
		this.pdnd = pdnd;
	}
}
