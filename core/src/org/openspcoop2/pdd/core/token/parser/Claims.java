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

/**     
 * Claims
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Claims {
	
	private Claims() {}

	// https://tools.ietf.org/html/rfc7519
	
	public static final String JSON_WEB_TOKEN_RFC_7519_ISSUER = "iss";
	public static final String JSON_WEB_TOKEN_RFC_7519_SUBJECT = "sub";
	public static final String JSON_WEB_TOKEN_RFC_7519_AUDIENCE = "aud";
	public static final String JSON_WEB_TOKEN_RFC_7519_EXPIRED = "exp";
	public static final String JSON_WEB_TOKEN_RFC_7519_ISSUED_AT = "iat";
	public static final String JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE = "nbf";
	public static final String JSON_WEB_TOKEN_RFC_7519_JWT_ID = "jti";
	
	// https://www.rfc-editor.org/rfc/rfc9068.html
	
	public static final String JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068_CLIENT_ID = "client_id";
	
	// https://tools.ietf.org/html/rfc7662
	
	public static final String INTROSPECTION_RESPONSE_RFC_7662_ACTIVE = "active";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_SCOPE = "scope";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID = "client_id";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_USERNAME = "username";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_TOKEN_TYPE = "token_type";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_EXPIRED = "exp";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_ISSUED_AT = "iat";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_NOT_TO_BE_USED_BEFORE = "nbf";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_SUBJECT = "sub";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE = "aud";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_ISSUER = "iss";
	public static final String INTROSPECTION_RESPONSE_RFC_7662_JWT_ID = "jti";
	
	
	// http://openid.net/specs/openid-connect-core-1_0.html#IDToken
	
	public static final String OIDC_ID_TOKEN_ISSUER = "iss";
	public static final String OIDC_ID_TOKEN_SUBJECT = "sub";
	public static final String OIDC_ID_TOKEN_AUDIENCE = "aud";
	public static final String OIDC_ID_TOKEN_EXPIRED = "exp";
	public static final String OIDC_ID_TOKEN_ISSUED_AT = "iat";
	public static final String OIDC_ID_TOKEN_AUTH_TIME = "auth_time";
	public static final String OIDC_ID_TOKEN_NONCE = "nonce";
	public static final String OIDC_ID_TOKEN_ACR = "acr";
	public static final String OIDC_ID_TOKEN_AMR = "amr";
	public static final String OIDC_ID_TOKEN_AZP = "azp";
	
	// http://openid.net/specs/openid-connect-core-1_0.html#Claims
		
	public static final String OIDC_ID_CLAIMS_SUBJECT = "sub";
	public static final String OIDC_ID_CLAIMS_NAME = "name";
	public static final String OIDC_ID_CLAIMS_GIVE_NAME = "given_name";
	public static final String OIDC_ID_CLAIMS_FAMILY_NAME = "family_name";
	public static final String OIDC_ID_CLAIMS_LAST_NAME = "last_name"; // usato in talune implementazioni 
	public static final String OIDC_ID_CLAIMS_MIDDLE_NAME = "middle_name";
	public static final String OIDC_ID_CLAIMS_NICKNAME = "nickname";
	public static final String OIDC_ID_CLAIMS_PREFERRED_USERNAME = "preferred_username";
	public static final String OIDC_ID_CLAIMS_PROFILE = "profile";
	public static final String OIDC_ID_CLAIMS_PICTURE = "picture";
	public static final String OIDC_ID_CLAIMS_WEBSITE = "website";
	public static final String OIDC_ID_CLAIMS_EMAIL = "email";
	public static final String OIDC_ID_CLAIMS_EMAIL_VERIFIED = "email_verified";
	public static final String OIDC_ID_CLAIMS_GENDER = "gender";
	public static final String OIDC_ID_CLAIMS_BIRTHDATE = "birthdate";
	public static final String OIDC_ID_CLAIMS_ZONEINFO = "zoneinfo";
	public static final String OIDC_ID_CLAIMS_LOCALE = "locale";
	public static final String OIDC_ID_CLAIMS_PHONE_NUMBER = "phone_number";
	public static final String OIDC_ID_CLAIMS_PHONE_NUMBER_VERIFIED = "phone_number_verified";
	public static final String OIDC_ID_CLAIMS_ADDRESS = "address";
	public static final String OIDC_ID_CLAIMS_UPDATED_AT = "updated_at";
	
	// http://openid.net/specs/openid-connect-core-1_0.html#AddressClaim
	
	public static final String OIDC_ID_CLAIMS_FORMATTED = "formatted";
	public static final String OIDC_ID_CLAIMS_STREET_ADDRESS = "street_address";
	public static final String OIDC_ID_CLAIMS_LOCALITY = "locality";
	public static final String OIDC_ID_CLAIMS_REGION = "region";
	public static final String OIDC_ID_CLAIMS_POSTAL_CODE = "postal_code";
	public static final String OIDC_ID_CLAIMS_COUNTRY = "country";
	
	// Google
	
	public static final String GOOGLE_CLAIMS_ISSUER = "iss";
	public static final String GOOGLE_CLAIMS_SUBJECT = "sub";
	public static final String GOOGLE_CLAIMS_AUDIENCE = "aud";
	public static final String GOOGLE_CLAIMS_EXPIRED = "exp";
	public static final String GOOGLE_CLAIMS_ISSUED_AT = "iat";
	public static final String GOOGLE_CLAIMS_AZP = "azp";
	public static final String GOOGLE_CLAIMS_SCOPE = "scope";
	public static final String GOOGLE_CLAIMS_NAME = "name";
	public static final String GOOGLE_CLAIMS_GIVE_NAME = "given_name";
	public static final String GOOGLE_CLAIMS_FAMILY_NAME = "family_name";
	public static final String GOOGLE_CLAIMS_MIDDLE_NAME = "middle_name";
	public static final String GOOGLE_CLAIMS_EMAIL = "email";
	public static final String GOOGLE_CLAIMS_ERROR_DESCRIPTION = "error_description";
	public static final String GOOGLE_CLAIMS_ERROR = "error";

	
	
}
