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
package org.openspcoop2.utils.oauth2;

import java.io.Serializable;


/**
 * Bean contenente le informazioni del token oauth
 *  
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OAuth2Token extends Oauth2BaseResponse implements Serializable {

private static final long serialVersionUID = 1L;
/**
   {
  "access_token": "ya29.a0ARrdaM-...",
  "expires_in": 3599,
  "refresh_token": "1//0gXb...",
  "scope": "openid profile email",
  "token_type": "Bearer",
  "id_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
} 
 */
	
	private String accessToken;
	private Long expiresIn;
	private Long expiresAt;
	private String refreshToken;
	private String scope;
	private String tokenType;
	private String idToken;
	private String kid;
	private String alg;
	
	public String getAccessToken() {
		return this.accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public Long getExpiresIn() {
		return this.expiresIn;
	}
	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}
	public Long getExpiresAt() {
		return this.expiresAt;
	}
	public void setExpiresAt(Long expiresAt) {
		this.expiresAt = expiresAt;
	}
	public String getRefreshToken() {
		return this.refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getScope() {
		return this.scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getTokenType() {
		return this.tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public String getIdToken() {
		return this.idToken;
	}
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
	public String getKid() {
		return this.kid;
	}
	public void setKid(String kid) {
		this.kid = kid;
	}
	public String getAlg() {
		return this.alg;
	}
	public void setAlg(String alg) {
		this.alg = alg;
	}
}
