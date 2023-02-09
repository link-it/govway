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

package org.openspcoop2.pdd.core.token.attribute_authority;

/**     
 * Costanti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

	public final static String AA_SAVE_ERROR_IN_CACHE = "policy.saveErrorInCache";
	
	public final static String AA_URL = "policy.endpoint.url";
	
	public final static String AA_AUTH_BASIC_STATO = "policy.endpoint.basic.stato";
	public final static String AA_AUTH_BASIC_USERNAME = "policy.endpoint.basic.username";
	public final static String AA_AUTH_BASIC_PASSWORD = "policy.endpoint.basic.password";
	public final static String AA_AUTH_BEARER_STATO = "policy.endpoint.bearer.stato";
	public final static String AA_AUTH_BEARER_TOKEN = "policy.endpoint.bearer.token";
	public final static String AA_AUTH_SSL_STATO = "policy.endpoint.https.stato";
	public final static String AA_AUTH_SSL_CLIENT_STATO = "policy.endpoint.https.client.stato";
	
	public final static String AA_REQUEST_POSITION = "policy.attributeAuthority.request.position";
	public final static String AA_REQUEST_POSITION_VALUE_BEARER = "bearer";
	public final static String AA_REQUEST_POSITION_VALUE_PAYLOAD = "payload";
	public final static String AA_REQUEST_POSITION_VALUE_HEADER = "header";
	public final static String AA_REQUEST_POSITION_VALUE_QUERY = "query";
	public final static String AA_REQUEST_POSITION_HEADER_NAME = "policy.attributeAuthority.request.position.header";
	public final static String AA_REQUEST_POSITION_QUERY_PARAMETER_NAME = "policy.attributeAuthority.request.position.queryParameter";
	
	public final static String AA_REQUEST_HTTPMETHOD = "policy.attributeAuthority.httpMethod";
	
	public final static String AA_REQUEST_TYPE = "policy.attributeAuthority.request.type";
	public final static String AA_REQUEST_TYPE_VALUE_JSON = "json";
	public final static String AA_REQUEST_TYPE_VALUE_JWS = "jws";
	public final static String AA_REQUEST_TYPE_VALUE_CUSTOM = "custom";
	
	public final static String AA_REQUEST_CONTENT_TYPE = "policy.attributeAuthority.request.contentType";
	
	public final static String AA_REQUEST_DYNAMIC_PAYLOAD_TYPE = "policy.attributeAuthority.request.templateType";
	public final static String AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_JWT = "jwt";
	public final static String AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_TEMPLATE = "template";
	public final static String AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_FREEMARKER_TEMPLATE = "freemarker-template";
	public final static String AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_VELOCITY_TEMPLATE = "velocity-template";
	public final static String AA_REQUEST_DYNAMIC_PAYLOAD = "policy.attributeAuthority.request.template";
	
	public final static String AA_REQUEST_JWT_ISSUER = "policy.attributeAuthority.request.jwt.issuer";
	public final static String AA_REQUEST_JWT_SUBJECT = "policy.attributeAuthority.request.jwt.subject";
	public final static String AA_REQUEST_JWT_AUDIENCE = "policy.attributeAuthority.request.jwt.audience";
	public final static String AA_REQUEST_JWT_EXPIRED_TTL_SECONDS = "policy.attributeAuthority.request.jwt.expired";
	public final static String AA_REQUEST_JWT_EXPIRED_TTL_SECONDS_DEFAULT_VALUE = "300";
	public final static String AA_REQUEST_JWT_CLAIMS = "policy.attributeAuthority.request.jwt.claims";
	
	public final static String AA_REQUEST_JWT_SIGN_ALGORITHM = "policy.attributeAuthority.request.jws.signature.algorithm";
	public final static String AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID = "policy.attributeAuthority.request.jws.signature.include.key.id";
	public final static String AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_MODE_NOT_PRESENT = "false"; // per backward compatibility
	public final static String AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_MODE_ALIAS = "true"; // per backward compatibility
	public final static String AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_MODE_CUSTOM = "custom";
	public final static String AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_VALUE = "policy.attributeAuthority.request.jws.signature.include.key.id.value";
	public final static String AA_REQUEST_JWT_SIGN_INCLUDE_X509_CERT = "policy.attributeAuthority.request.jws.signature.include.cert";
	public final static String AA_REQUEST_JWT_SIGN_INCLUDE_X509_URL = "policy.attributeAuthority.request.jws.signature.include.x509url";
	public final static String AA_REQUEST_JWT_SIGN_INCLUDE_X509_SHA1 = "policy.attributeAuthority.request.jws.signature.include.cert.sha1";
	public final static String AA_REQUEST_JWT_SIGN_INCLUDE_X509_SHA256 = "policy.attributeAuthority.request.jws.signature.include.cert.sha256";
	public final static String AA_REQUEST_JWT_SIGN_JOSE_CONTENT_TYPE = "policy.attributeAuthority.request.jwsHeader.joseContentType";
	public final static String AA_REQUEST_JWT_SIGN_JOSE_TYPE = "policy.attributeAuthority.request.jwsHeader.joseType";
	
	public final static String AA_REQUEST_JWT_SIGN_KEYSTORE_TYPE = "policy.attributeAuthority.request.jws.keystore.type";
	public final static String AA_REQUEST_JWT_SIGN_KEYSTORE_FILE = "policy.attributeAuthority.request.jws.keystore.file";
	public final static String AA_REQUEST_JWT_SIGN_KEYSTORE_PASSWORD = "policy.attributeAuthority.request.jws.keystore.password";
	public final static String AA_REQUEST_JWT_SIGN_KEY_ALIAS = "policy.attributeAuthority.request.jws.key.alias";
	public final static String AA_REQUEST_JWT_SIGN_KEY_PASSWORD = "policy.attributeAuthority.request.jws.key.password";
	
	public final static String AA_RESPONSE_TYPE = "policy.attributeAuthority.response.type";
	public final static String AA_RESPONSE_TYPE_VALUE_JSON = "json";
	public final static String AA_RESPONSE_TYPE_VALUE_JWS = "jws";
	public final static String AA_RESPONSE_TYPE_VALUE_CUSTOM = "custom";
	
	public final static String AA_RESPONSE_PARSER = "policy.attributeAuthority.claimsParser.className";
	public final static String AA_RESPONSE_ATTRIBUTES = "policy.attributeAuthority.response.jwt.attributes";
	
	public final static String AA_RESPONSE_AUDIENCE = "policy.attributeAuthority.response.jwt.audience";
	
	public final static String POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID = "signatureVerifyPropRefId";

	
	
	// ELEMENTI ID
	
	public final static String ID_AA_ENDPOINT_URL = "endpointURL";
	public final static String ID_AA_AUTENTICAZIONE_ENDPOINT_BASIC_USERNAME = "autenticazioneEndpointBasicUsername";
	public final static String ID_AA_AUTENTICAZIONE_ENDPOINT_BASIC_PASSWORD = "autenticazioneEndpointBasicPassword";
	public final static String ID_AA_AUTENTICAZIONE_ENDPOINT_BEARER_TOKEN = "autenticazioneEndpointBearerToken";
	public final static String ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE = "aaRichiestaJwsPayloadTemplate";
	public final static String ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE_FREEMARKER = "aaRichiestaJwsPayloadTemplateFreemarker";
	public final static String ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE_VELOCITY = "aaRichiestaJwsPayloadTemplateVelocity";
	public final static String ID_AA_RICHIESTA_JWS_PAYLOAD_ISSUER = "aaRichiestaPayloadIssuer";
	public final static String ID_AA_RICHIESTA_JWS_PAYLOAD_SUBJECT = "aaRichiestaPayloadSubject";
	public final static String ID_AA_RICHIESTA_JWS_PAYLOAD_AUDIENCE = "aaRichiestaPayloadAudience";
	public final static String ID_AA_RICHIESTA_JWS_PAYLOAD_CLAIMS = "aaRichiestaJwsPayloadClaims";
	public final static String ID_AA_RICHIESTA_PAYLOAD_TEMPLATE = "aaRichiestaPayloadTemplate";
	public final static String ID_AA_RICHIESTA_PAYLOAD_TEMPLATE_FREEMARKER = "aaRichiestaPayloadTemplateFreemarker";
	public final static String ID_AA_RICHIESTA_PAYLOAD_TEMPLATE_VELOCITY = "aaRichiestaPayloadTemplateVelocity";
	public final static String ID_AA_RISPOSTA_JWS_PAYLOAD_AUDIENCE = "aaResponseAudience";
	
	public final static String ID_AA_SIGNATURE_ALGORITHM = "aaSignatureAlgorithm";
	public final static String ID_AA_TOKEN_JWT_EXPIRED_TTL_SECONDS= "aaRichiestaPayloadExpTtl";
}
