/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

	// https://tools.ietf.org/html/rfc7519
	
	public final static String JSON_WEB_TOKEN_RFC_7519_ISSUER = "iss";
	public final static String JSON_WEB_TOKEN_RFC_7519_SUBJECT = "sub";
	public final static String JSON_WEB_TOKEN_RFC_7519_AUDIENCE = "aud";
	public final static String JSON_WEB_TOKEN_RFC_7519_EXPIRED = "exp";
	public final static String JSON_WEB_TOKEN_RFC_7519_ISSUED_AT = "iat";
	public final static String JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE = "nbf";
	public final static String JSON_WEB_TOKEN_RFC_7519_JWT_ID = "jti";
	
	
	// https://tools.ietf.org/html/rfc7662
	
	public final static String INTROSPECTION_RESPONSE_RFC_7662_ACTIVE = "active";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_SCOPE = "scope";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID = "client_id";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_USERNAME = "username";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_TOKEN_TYPE = "token_type";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_EXPIRED = "exp";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_ISSUED_AT = "iat";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_NOT_TO_BE_USED_BEFORE = "nbf";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_SUBJECT = "sub";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE = "aud";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_ISSUER = "iss";
	public final static String INTROSPECTION_RESPONSE_RFC_7662_JWT_ID = "jti";
	
	
	// http://openid.net/specs/openid-connect-core-1_0.html#IDToken
	
	public final static String OIDC_ID_TOKEN_ISSUER = "iss";
	public final static String OIDC_ID_TOKEN_SUBJECT = "sub";
	public final static String OIDC_ID_TOKEN_AUDIENCE = "aud";
	public final static String OIDC_ID_TOKEN_EXPIRED = "exp";
	public final static String OIDC_ID_TOKEN_ISSUED_AT = "iat";
	public final static String OIDC_ID_TOKEN_AUTH_TIME = "auth_time";
	public final static String OIDC_ID_TOKEN_NONCE = "nonce";
	public final static String OIDC_ID_TOKEN_ACR = "acr";
	public final static String OIDC_ID_TOKEN_AMR = "amr";
	public final static String OIDC_ID_TOKEN_AZP = "azp";
	
	// http://openid.net/specs/openid-connect-core-1_0.html#Claims
		
	public final static String OIDC_ID_CLAIMS_SUBJECT = "sub";
	public final static String OIDC_ID_CLAIMS_NAME = "name";
	public final static String OIDC_ID_CLAIMS_GIVE_NAME = "given_name";
	public final static String OIDC_ID_CLAIMS_FAMILY_NAME = "family_name";
	public final static String OIDC_ID_CLAIMS_LAST_NAME = "last_name"; // usato in talune implementazioni 
	public final static String OIDC_ID_CLAIMS_MIDDLE_NAME = "middle_name";
	public final static String OIDC_ID_CLAIMS_NICKNAME = "nickname";
	public final static String OIDC_ID_CLAIMS_PREFERRED_USERNAME = "preferred_username";
	public final static String OIDC_ID_CLAIMS_PROFILE = "profile";
	public final static String OIDC_ID_CLAIMS_PICTURE = "picture";
	public final static String OIDC_ID_CLAIMS_WEBSITE = "website";
	public final static String OIDC_ID_CLAIMS_EMAIL = "email";
	public final static String OIDC_ID_CLAIMS_EMAIL_VERIFIED = "email_verified";
	public final static String OIDC_ID_CLAIMS_GENDER = "gender";
	public final static String OIDC_ID_CLAIMS_BIRTHDATE = "birthdate";
	public final static String OIDC_ID_CLAIMS_ZONEINFO = "zoneinfo";
	public final static String OIDC_ID_CLAIMS_LOCALE = "locale";
	public final static String OIDC_ID_CLAIMS_PHONE_NUMBER = "phone_number";
	public final static String OIDC_ID_CLAIMS_PHONE_NUMBER_VERIFIED = "phone_number_verified";
	public final static String OIDC_ID_CLAIMS_ADDRESS = "address";
	public final static String OIDC_ID_CLAIMS_UPDATED_AT = "updated_at";
	
	// http://openid.net/specs/openid-connect-core-1_0.html#AddressClaim
	
	public final static String OIDC_ID_CLAIMS_FORMATTED = "formatted";
	public final static String OIDC_ID_CLAIMS_STREET_ADDRESS = "street_address";
	public final static String OIDC_ID_CLAIMS_LOCALITY = "locality";
	public final static String OIDC_ID_CLAIMS_REGION = "region";
	public final static String OIDC_ID_CLAIMS_POSTAL_CODE = "postal_code";
	public final static String OIDC_ID_CLAIMS_COUNTRY = "country";
	
	// Google
	
	public final static String GOOGLE_CLAIMS_ISSUER = "iss";
	public final static String GOOGLE_CLAIMS_SUBJECT = "sub";
	public final static String GOOGLE_CLAIMS_AUDIENCE = "aud";
	public final static String GOOGLE_CLAIMS_EXPIRED = "exp";
	public final static String GOOGLE_CLAIMS_ISSUED_AT = "iat";
	public final static String GOOGLE_CLAIMS_AZP = "azp";
	public final static String GOOGLE_CLAIMS_SCOPE = "scope";
	public final static String GOOGLE_CLAIMS_NAME = "name";
	public final static String GOOGLE_CLAIMS_GIVE_NAME = "given_name";
	public final static String GOOGLE_CLAIMS_FAMILY_NAME = "family_name";
	public final static String GOOGLE_CLAIMS_MIDDLE_NAME = "middle_name";
	public final static String GOOGLE_CLAIMS_EMAIL = "email";
	public final static String GOOGLE_CLAIMS_ERROR_DESCRIPTION = "error_description";
	public final static String GOOGLE_CLAIMS_ERROR = "error";

	
	
}
