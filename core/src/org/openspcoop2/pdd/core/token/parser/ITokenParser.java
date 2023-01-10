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

import java.util.Date;
import java.util.List;
import java.util.Map;

/**     
 * ITokenParser
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ITokenParser {

	public void init(String raw, Map<String,Object> claims);
	
	// Controllo se il token e' un token atteso su cui leggere le informazioni
	public void checkHttpTransaction(Integer httpResponseCode) throws Exception;
	
	// Indicazione se il token e' valido
	public boolean isValid();
	
	// String representing the issuer of this token, as defined in JWT [RFC7519].
	public String getIssuer();
	
	// Subject of the token, as defined in JWT [RFC7519]. 
	// Usually a machine-readable identifier of the resource owner who authorized this token.
	public String getSubject();

	// Human-readable identifier for the resource owner who authorized this token.
	public String getUsername();
	
	// Service-specific string identifier or list of string identifiers representing the intended audience for this token, 
	// as defined in JWT [RFC7519].
	public List<String> getAudience();
	
	// Integer timestamp, measured in the number of seconds since January 1 1970 UTC, 
	// indicating when this token will expire, as defined in JWT [RFC7519].
	public Date getExpired();
	
	// Integer timestamp, measured in the number of seconds since January 1 1970 UTC, 
	// indicating when this token was originally issued, as defined in JWT [RFC7519].
	public Date getIssuedAt();
	
	// Integer timestamp, measured in the number of seconds since January 1 1970 UTC, 
	// indicating when this token is not to be used before, as defined in JWT [RFC7519].
	public Date getNotToBeUsedBefore();
	
	// The "jti" (JWT ID) claim provides a unique identifier for the JWT.
	public String getJWTIdentifier();
	
	// Client identifier for the OAuth 2.0 client that requested this token.
	// Per un ID_TOKEN, If present, it MUST contain the OAuth 2.0 Client ID of this party.
	public String getClientId(); // in oidc e' azp
	
	// Ruoli di un utente
	public List<String> getRoles();
	
	// Scopes
	public List<String> getScopes();

	// ITokenUserInfoParser
	public ITokenUserInfoParser getUserInfoParser();
}
