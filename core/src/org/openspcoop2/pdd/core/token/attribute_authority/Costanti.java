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

import org.openspcoop2.core.config.constants.CostantiConfigurazione;

/**     
 * Costanti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	private Costanti() {}

	public static final String AA_SAVE_ERROR_IN_CACHE = "policy.saveErrorInCache";
	
	public static final String AA_URL = "policy.endpoint.url";
	
	public static final String AA_AUTH_BASIC_STATO = "policy.endpoint.basic.stato";
	public static final String AA_AUTH_BASIC_USERNAME = "policy.endpoint.basic.username";
	public static final String AA_AUTH_BASIC_PASSWORD = "policy.endpoint.basic.password";
	public static final String AA_AUTH_BEARER_STATO = "policy.endpoint.bearer.stato";
	public static final String AA_AUTH_BEARER_TOKEN = "policy.endpoint.bearer.token";
	public static final String AA_AUTH_SSL_STATO = "policy.endpoint.https.stato";
	public static final String AA_AUTH_SSL_CLIENT_STATO = "policy.endpoint.https.client.stato";
	
	public static final String AA_REQUEST_POSITION = "policy.attributeAuthority.request.position";
	public static final String AA_REQUEST_POSITION_VALUE_BEARER = "bearer";
	public static final String AA_REQUEST_POSITION_VALUE_PAYLOAD = "payload";
	public static final String AA_REQUEST_POSITION_VALUE_HEADER = "header";
	public static final String AA_REQUEST_POSITION_VALUE_QUERY = "query";
	public static final String AA_REQUEST_POSITION_HEADER_NAME = "policy.attributeAuthority.request.position.header";
	public static final String AA_REQUEST_POSITION_QUERY_PARAMETER_NAME = "policy.attributeAuthority.request.position.queryParameter";
	
	public static final String AA_REQUEST_HTTPMETHOD = "policy.attributeAuthority.httpMethod";
	
	public static final String AA_REQUEST_TYPE = "policy.attributeAuthority.request.type";
	public static final String AA_REQUEST_TYPE_VALUE_JSON = "json";
	public static final String AA_REQUEST_TYPE_VALUE_JWS = "jws";
	public static final String AA_REQUEST_TYPE_VALUE_CUSTOM = "custom";
	
	public static final String AA_REQUEST_CONTENT_TYPE = "policy.attributeAuthority.request.contentType";
	
	public static final String AA_REQUEST_DYNAMIC_PAYLOAD_TYPE = "policy.attributeAuthority.request.templateType";
	public static final String AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_JWT = "jwt";
	public static final String AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_TEMPLATE = "template";
	public static final String AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_FREEMARKER_TEMPLATE = "freemarker-template";
	public static final String AA_REQUEST_DYNAMIC_PAYLOAD_TYPE_VELOCITY_TEMPLATE = "velocity-template";
	public static final String AA_REQUEST_DYNAMIC_PAYLOAD = "policy.attributeAuthority.request.template";
	
	public static final String AA_REQUEST_JWT_ISSUER = "policy.attributeAuthority.request.jwt.issuer";
	public static final String AA_REQUEST_JWT_SUBJECT = "policy.attributeAuthority.request.jwt.subject";
	public static final String AA_REQUEST_JWT_AUDIENCE = "policy.attributeAuthority.request.jwt.audience";
	public static final String AA_REQUEST_JWT_EXPIRED_TTL_SECONDS = "policy.attributeAuthority.request.jwt.expired";
	public static final String AA_REQUEST_JWT_EXPIRED_TTL_SECONDS_DEFAULT_VALUE = "300";
	public static final String AA_REQUEST_JWT_CLAIMS = "policy.attributeAuthority.request.jwt.claims";
	
	public static final String AA_REQUEST_JWT_SIGN_ALGORITHM = "policy.attributeAuthority.request.jws.signature.algorithm";
	public static final String AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID = "policy.attributeAuthority.request.jws.signature.include.key.id";
	public static final String AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_MODE_NOT_PRESENT = "false"; // per backward compatibility
	public static final String AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_MODE_ALIAS = "true"; // per backward compatibility
	public static final String AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_MODE_CUSTOM = "custom";
	public static final String AA_REQUEST_JWT_SIGN_INCLUDE_KEY_ID_VALUE = "policy.attributeAuthority.request.jws.signature.include.key.id.value";
	public static final String AA_REQUEST_JWT_SIGN_INCLUDE_X509_CERT = "policy.attributeAuthority.request.jws.signature.include.cert";
	public static final String AA_REQUEST_JWT_SIGN_INCLUDE_X509_URL = "policy.attributeAuthority.request.jws.signature.include.x509url";
	public static final String AA_REQUEST_JWT_SIGN_INCLUDE_X509_SHA1 = "policy.attributeAuthority.request.jws.signature.include.cert.sha1";
	public static final String AA_REQUEST_JWT_SIGN_INCLUDE_X509_SHA256 = "policy.attributeAuthority.request.jws.signature.include.cert.sha256";
	public static final String AA_REQUEST_JWT_SIGN_JOSE_CONTENT_TYPE = "policy.attributeAuthority.request.jwsHeader.joseContentType";
	public static final String AA_REQUEST_JWT_SIGN_JOSE_TYPE = "policy.attributeAuthority.request.jwsHeader.joseType";
	
	public static final String AA_REQUEST_JWT_SIGN_KEYSTORE_TYPE = "policy.attributeAuthority.request.jws.keystore.type";
	public static final String AA_REQUEST_JWT_SIGN_KEYSTORE_FILE = "policy.attributeAuthority.request.jws.keystore.file";
	public static final String AA_REQUEST_JWT_SIGN_KEYSTORE_FILE_PUBLIC = "policy.attributeAuthority.request.jws.keystore.file.public";
	public static final String AA_REQUEST_JWT_SIGN_KEYSTORE_FILE_ALGORITHM = "policy.attributeAuthority.request.jws.keystore.file.algorithm";
	public static final String AA_REQUEST_JWT_SIGN_KEYSTORE_PASSWORD = "policy.attributeAuthority.request.jws.keystore.password";
	public static final String AA_REQUEST_JWT_SIGN_KEY_ALIAS = "policy.attributeAuthority.request.jws.key.alias";
	public static final String AA_REQUEST_JWT_SIGN_KEY_PASSWORD = "policy.attributeAuthority.request.jws.key.password";
	
	public static final String AA_RESPONSE_TYPE = CostantiConfigurazione.AA_RESPONSE_TYPE; 
	public static final String AA_RESPONSE_TYPE_VALUE_JSON = CostantiConfigurazione.AA_RESPONSE_TYPE_VALUE_JSON;
	public static final String AA_RESPONSE_TYPE_VALUE_JWS = CostantiConfigurazione.AA_RESPONSE_TYPE_VALUE_JWS; 
	public static final String AA_RESPONSE_TYPE_VALUE_CUSTOM = CostantiConfigurazione.AA_RESPONSE_TYPE_VALUE_CUSTOM; 
	
	public static final String AA_RESPONSE_PARSER_CLASS_NAME = CostantiConfigurazione.AA_RESPONSE_PARSER_CLASS_NAME; 
	public static final String AA_RESPONSE_PARSER_PLUGIN_TYPE = CostantiConfigurazione.AA_RESPONSE_PARSER_PLUGIN_TYPE;
	public static final String AA_RESPONSE_ATTRIBUTES = "policy.attributeAuthority.response.jwt.attributes";
	
	public static final String AA_RESPONSE_AUDIENCE = "policy.attributeAuthority.response.jwt.audience";
	
	public static final String POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID = "signatureVerifyPropRefId";

	
	
	// ELEMENTI ID
	
	public static final String ID_AA_ENDPOINT_URL = "endpointURL";
	public static final String ID_AA_AUTENTICAZIONE_ENDPOINT_BASIC_USERNAME = "autenticazioneEndpointBasicUsername";
	public static final String ID_AA_AUTENTICAZIONE_ENDPOINT_BASIC_PASSWORD = "autenticazioneEndpointBasicPassword";
	public static final String ID_AA_AUTENTICAZIONE_ENDPOINT_BEARER_TOKEN = "autenticazioneEndpointBearerToken";
	public static final String ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE = "aaRichiestaJwsPayloadTemplate";
	public static final String ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE_FREEMARKER = "aaRichiestaJwsPayloadTemplateFreemarker";
	public static final String ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE_VELOCITY = "aaRichiestaJwsPayloadTemplateVelocity";
	public static final String ID_AA_RICHIESTA_JWS_PAYLOAD_ISSUER = "aaRichiestaPayloadIssuer";
	public static final String ID_AA_RICHIESTA_JWS_PAYLOAD_SUBJECT = "aaRichiestaPayloadSubject";
	public static final String ID_AA_RICHIESTA_JWS_PAYLOAD_AUDIENCE = "aaRichiestaPayloadAudience";
	public static final String ID_AA_RICHIESTA_JWS_PAYLOAD_CLAIMS = "aaRichiestaJwsPayloadClaims";
	public static final String ID_AA_RICHIESTA_PAYLOAD_TEMPLATE = "aaRichiestaPayloadTemplate";
	public static final String ID_AA_RICHIESTA_PAYLOAD_TEMPLATE_FREEMARKER = "aaRichiestaPayloadTemplateFreemarker";
	public static final String ID_AA_RICHIESTA_PAYLOAD_TEMPLATE_VELOCITY = "aaRichiestaPayloadTemplateVelocity";
	public static final String ID_AA_RISPOSTA_JWS_PAYLOAD_AUDIENCE = "aaResponseAudience";
	
	public static final String ID_AA_SIGNATURE_ALGORITHM = "aaSignatureAlgorithm";
	public static final String ID_AA_TOKEN_JWT_EXPIRED_TTL_SECONDS= "aaRichiestaPayloadExpTtl";
	
	public static final String ID_AA_PARSER_TOKEN_CUSTOM_PLUGIN_CLASSNAME = "aaResponseParserCustom";
	public static final String ID_AA_PARSER_TOKEN_CUSTOM_PLUGIN_CHOICE = "aaResponseParserCustomPluginChoice";
}
