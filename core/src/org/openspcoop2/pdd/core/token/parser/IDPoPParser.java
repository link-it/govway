/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.openspcoop2.utils.UtilsException;

/**
 * IDPoPParser
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDPoPParser {

	public void init(String raw) throws UtilsException;

	public String getRaw() throws UtilsException;
	
	// Controllo se il DPoP token e' valido
	public void checkHttpTransaction(Integer httpResponseCode) throws UtilsException;

	// Indicazione se il token e' valido
	public boolean isValid();

	// Header claims

	// Token type ("dpop+jwt" per RFC 9449)
	public String getType();

	// Signature algorithm
	public String getAlgorithm();

	// JSON Web Key (public key)
	public String getJsonWebKeyAsString() throws UtilsException;
	public JsonWebKey getJsonWebKey() throws UtilsException;

	// Payload claims

	// The "jti" (JWT ID) claim provides a unique identifier for the DPoP JWT.
	public String getJWTIdentifier();

	// HTTP method for the request to which the JWT is attached ("GET", "POST", etc.)
	public String getHttpMethod();

	// HTTP target URI for the request to which the JWT is attached (without query and fragment parts)
	public String getHttpUri();

	// Integer timestamp, measured in the number of seconds since January 1 1970 UTC,
	// indicating when this DPoP proof was created, as defined in JWT [RFC7519].
	public Date getIssuedAt();

	// Hash of the access token (base64url encoding of the SHA-256 hash)
	public String getAccessTokenHash();
}
