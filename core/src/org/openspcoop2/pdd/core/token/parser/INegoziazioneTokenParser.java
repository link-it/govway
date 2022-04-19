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
public interface INegoziazioneTokenParser {

	public void init(String raw, Map<String,Object> claims);
	
	// Controllo se il token e' un token atteso su cui leggere le informazioni
	public void checkHttpTransaction(Integer httpResponseCode) throws Exception;
	
	// Indicazione se il token e' valido
	public boolean isValid();
	
	// String representing the access token issued by the authorization server [RFC6749].
	public String getAccessToken();
	
	// String representing the refresh token, which can be used to obtain new access tokens using the same authorization grant
	public String getRefreshToken();

	// The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
	// expire in one hour from the time the response was generated.
    // If omitted, the authorization server SHOULD provide the expiration time via other means or document the default value.
	public Date getExpired();
	
	// The lifetime in seconds of the refresh token.  For example, the value "3600" denotes that the access token will
	// expire in one hour from the time the response was generated.
    // If omitted, the authorization server SHOULD provide the expiration time via other means or document the default value.
	public Date getRefreshExpired();
	
	// The type of the token issued
	public String getTokenType();
	
	// Scopes
	public List<String> getScopes();
	
}
	
