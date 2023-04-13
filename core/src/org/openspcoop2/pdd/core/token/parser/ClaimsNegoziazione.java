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
import java.util.List;

/**     
 * Claims
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClaimsNegoziazione {

	// https://tools.ietf.org/html/rfc6749
	
	public static final String OAUTH2_RFC_6749_ACCESS_TOKEN = "access_token";
	public static final String OAUTH2_RFC_6749_REFRESH_TOKEN = "refresh_token";
	public static final String OAUTH2_RFC_6749_TOKEN_TYPE = "token_type";
	public static final String OAUTH2_RFC_6749_EXPIRES_IN = "expires_in";
	public static final String OAUTH2_RFC_6749_SCOPE = "scope";
	
	public static final String AZURE_EXPIRES_ON = "expires_on"; // azure (https://learn.microsoft.com/en-us/azure/container-apps/managed-identity?tabs=portal%2Chttp#connect-to-azure-services-in-app-code)
	
	public static final List<String> REFRESH_EXPIRE_IN_CUSTOM_CLAIMS = new ArrayList<String>();
	static {
		REFRESH_EXPIRE_IN_CUSTOM_CLAIMS.add("refresh_expires_in"); //keyclock
	}
	
	public static final List<String> REFRESH_EXPIRE_ON_CUSTOM_CLAIMS = new ArrayList<String>();
	static {
		REFRESH_EXPIRE_ON_CUSTOM_CLAIMS.add("refresh_expires_on");
	}
	
	public static final String OAUTH2_RFC_6749_REQUEST_GRANT_TYPE = "grant_type";
	public static final String OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_CLIENT_CREDENTIALS_GRANT = "client_credentials";
	public static final String OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_RESOURCE_OWNER_PASSWORD_CREDENTIALS_GRANT = "password";
	public static final String OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
	public static final String OAUTH2_RFC_6749_REQUEST_USERNAME = "username";
	public static final String OAUTH2_RFC_6749_REQUEST_PASSWORD = "password";
	public static final String OAUTH2_RFC_6749_REQUEST_CLIENT_ID = "client_id";
	public static final String OAUTH2_RFC_6749_REQUEST_CLIENT_SECRET = "client_secret";
	public static final String OAUTH2_RFC_6749_REQUEST_SCOPE = "scope";
	public static final String OAUTH2_RFC_6749_REQUEST_AUDIENCE = "audience"; // es. https://auth0.com/docs/api-auth/tutorials/client-credentials
	
	public static final String OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION_TYPE = "client_assertion_type";
	public static final String OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION_TYPE_RFC_7523 = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
	public static final String OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION = "client_assertion";
	
}
