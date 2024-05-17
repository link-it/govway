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

package org.openspcoop2.security.message.constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.apache.wss4j.common.ConfigurationConstants;
import org.apache.wss4j.common.WSS4JConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.digest.Constants;

/**
 * WSSConstants
 *
 * @author Andrea Poli (apoli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityConstants {
	
	private SecurityConstants() {}

	public static final String ACTION = ConfigurationConstants.ACTION;
		
	public static final String TIPO_SECURITY_ENGINE_SEPARATOR = " ";
	public static final String TIPO_SECURITY_ACTION_SEPARATOR = ",";
	public static String convertActionToString(Map<String, Object> flow){
		if(flow!=null &&
			flow.containsKey(ACTION)){
				
			String engine = SECURITY_ENGINE_WSS4J;
			if(flow.containsKey(SECURITY_ENGINE)){
				Object o = flow.get(SECURITY_ENGINE);
				if(o instanceof String){
					engine = (String) o;
				}
			}
			
			Object o = flow.get(ACTION);
			if(o instanceof String){
				String actions = (String) o;
				return getActionValue(actions, engine);
			}
		}
		return null;
	}
	private static String getActionValue(String actions, String engine) {
		actions = actions.trim();
		if(actions.contains(" ")){
			String [] tmp = actions.split(" ");
			StringBuilder bf = new StringBuilder();
			bf.append(engine);
			bf.append(TIPO_SECURITY_ENGINE_SEPARATOR);
			for (int i = 0; i < tmp.length; i++) {
				if(tmp[i]!=null){
					if(i>0){
						bf.append(TIPO_SECURITY_ACTION_SEPARATOR);
					}
					bf.append(tmp[i].trim());
				}
			}
			return bf.toString();
		}
		else{
			return engine + TIPO_SECURITY_ENGINE_SEPARATOR + actions; // una sola azione presente
		}
	}
	
	public static final String SECURITY_ENGINE = "securityEngine";
	public static final String SECURITY_ENGINE_WSS4J = "wss4j";
	public static final String SECURITY_ENGINE_SOAPBOX = "soapbox";
	public static final String SECURITY_ENGINE_DSS = "dss";
	public static final String SECURITY_ENGINE_JOSE = "jose";
	public static final String SECURITY_ENGINE_XML = "xml";
	
	public static final String NORMALIZE_TO_SAAJ_IMPL = "normalizeToSaajImpl";
	
	public static final String WSS_HEADER_ELEMENT = "Security";
    public static final String WSS_HEADER_ELEMENT_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String WSS_HEADER_UTILITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    public static final QName QNAME_WSS_ELEMENT_SECURITY = new QName(WSS_HEADER_ELEMENT_NAMESPACE,WSS_HEADER_ELEMENT);
    public static final String WSS_HEADER_ATTRIBUTE_REFERENCE_ID_WSSECURITY = "Id";
    public static final String WSS_HEADER_DS_NAMESPACE = Constants.DS_NAMESPACE;
    public static final String WSS_HEADER_DS_REFERENCE_ELEMENT = Constants.DS_REFERENCE_ELEMENT;
    public static final String WSS_HEADER_DS_REFERENCE_ATTRIBUTE_URI = Constants.DS_REFERENCE_ATTRIBUTE_URI;
    public static final String WSS_HEADER_DS_REFERENCE_DIGEST_VALUE_ELEMENT = Constants.DS_REFERENCE_DIGEST_VALUE_ELEMENT;
    
    public static final String ACTOR = ConfigurationConstants.ACTOR;
    
    public static final String MUST_UNDERSTAND = ConfigurationConstants.MUST_UNDERSTAND;
    
    public static final boolean SECURITY_CLIENT = true;
    public static final boolean SECURITY_SERVER = false;
    
    public static final String USER = ConfigurationConstants.USER;
    
    public static final String CID_ATTACH_WSS4J = "cid:Attachments";
    public static final String NAMESPACE_ATTACH = "Attach";
    public static final String PART_CONTENT = "Content";
    public static final String PART_COMPLETE = "Complete";
    public static final String PART_ELEMENT = "Element";
    public static final String ATTACHMENT_INDEX_ALL =  "*";
    public static final String SOAP_NAMESPACE_TEMPLATE =  "SOAP_TEMPLATE_NS";
    
    public static final String MODE_JSON = "json";
    public static final String MODE_COMPACT = "compact";
    
    public static final String MULTI_USER_KEYWORD_PORTA_DOMINIO_FRUITORE = "#MultiPropUsePddFruitoreAsAlias#";
    public static final String MULTI_USER_KEYWORD_PORTA_DOMINIO_EROGATORE = "#MultiPropUsePddErogatoreAsAlias#";
    public static final String MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_FRUITORE = "#MultiPropUseIdentificativoPortaFruitoreAsAlias#";
    public static final String MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_EROGATORE = "#MultiPropUseIdentificativoPortaErogatoreAsAlias#";
    public static final String MULTI_USER_KEYWORD_FRUITORE = "#MultiPropUseFruitoreAsAlias#";
    public static final String MULTI_USER_KEYWORD_EROGATORE = "#MultiPropUseErogatoreAsAlias#";
    public static final String MULTI_USER_KEYWORD_FRUITORE_EROGATORE = "#MultiPropUseFruitoreErogatoreAsAlias#";
    
    public static final String PASSWORD_CALLBACK_CLASS = ConfigurationConstants.PW_CALLBACK_CLASS;
    public static final String PASSWORD_CALLBACK_REF = ConfigurationConstants.PW_CALLBACK_REF;
    
    public static final String SYMMETRIC_KEY = "symmetricKey";
    public static final String SYMMETRIC_KEY_TRUE = "true";
    public static final String SYMMETRIC_KEY_FALSE = "false";
    
    public static final String ENABLE_REVOCATION = ConfigurationConstants.ENABLE_REVOCATION;
    
    public static final String ENCRYPTION_ACTION = ConfigurationConstants.ENCRYPTION;
    @SuppressWarnings("deprecation")
	public static final String ENCRYPT_ACTION_OLD = ConfigurationConstants.ENCRYPT; // modificato costante in wss4j 2.3.x
    public static final String DECRYPTION_ACTION = "Decryption";  // modificato per adeguamento costante rispetto a wss4j 2.3.x
    public static final String DECRYPT_ACTION_OLD = "Decrypt";  // modificato per adeguamento costante rispetto a wss4j 2.3.x
    public static final String ENCRYPTION_USER = ConfigurationConstants.ENCRYPTION_USER;
    public static final String ENCRYPTION_PASSWORD = "encryptionPassword";
    public static final String ENCRYPTION_SOAP_FAULT = "encryptionSOAPFault";
    public static final String ENCRYPTION_PROBLEM_DETAILS = "encryptionProblemDetails";
    public static final String ENCRYPTION_PARTS = ConfigurationConstants.ENCRYPTION_PARTS;
    public static final String ENCRYPTION_PARTS_VERIFY = "encryptionPartsVerify";
    /** ENCRYPTION_ATTACHMENTS_PARTS: {Content/Complete}{indice} 
    // utilizzo: {Content}{Attach}{*}
    // o sintassi wss4j {}cid:Attachments
    // Il valore * o '' puo' essere usato come indice per indicare qualsiasi, altrimenti l'indice indica la posizione dell'attachment*/
    public static final String ENCRYPTION_NAMESPACE_ATTACH = NAMESPACE_ATTACH;
    public static final String ENCRYPTION_PART_CONTENT = PART_CONTENT;
    public static final String ENCRYPTION_PART_COMPLETE = PART_COMPLETE;
    public static final String ENCRYPTION_PART_ELEMENT = PART_ELEMENT;
    public static final String ENCRYPTION_JWK_SET_FILE = "encryptionJWKSetFile";
    public static final String ENCRYPTION_PROPERTY_REF_ID = ConfigurationConstants.ENC_PROP_REF_ID;
    public static final String ENCRYPTION_PROPERTY_FILE = ConfigurationConstants.ENC_PROP_FILE;
    public static final String ENCRYPTION_TRUSTSTORE_PROPERTY_FILE = "encryptionTrustStorePropFile";
    public static final String ENCRYPTION_TRUSTSTORE_PROPERTY_REF_ID= "encryptionTrustStorePropRefId";
    public static final String ENCRYPTION_MULTI_PROPERTY_FILE = "encryptionMultiPropFile";
    public static final String ENCRYPTION_SYMMETRIC_KEY_VALUE = "encryptionSymmetricKeyValue";
    public static final String ENCRYPTION_SYMMETRIC = "encryptionSymmetricKey";
    public static final String ENCRYPTION_SYMMETRIC_WRAPPED = "encryptionSymmetricKeyWrapped";
    public static final String ENCRYPTION_SYMMETRIC_WRAPPED_TRUE = "true";
    public static final String ENCRYPTION_SYMMETRIC_WRAPPED_FALSE = "false";
    public static final String ENCRYPTION_KEY_SIZE = "encryptionKeySize";
    public static final String ENCRYPTION_KEY_TRANSPORT_ALGORITHM = ConfigurationConstants.ENC_KEY_TRANSPORT;
    public static final String ENCRYPTION_SYMMETRIC_ALGORITHM = ConfigurationConstants.ENC_SYM_ALGO;
    public static final String ENCRYPTION_DIGEST_ALGORITHM = ConfigurationConstants.ENC_DIGEST_ALGO;
    public static final String ENCRYPTION_C14N_ALGORITHM = "encryptionC14nAlgorithm";
    public static final String ENCRYPTION_KEY_ALGORITHM = "encryptionKeyAlgorithm";
    public static final String ENCRYPTION_CONTENT_ALGORITHM = "encryptionContentAlgorithm";
    public static final String ENCRYPTION_ALGORITHM = "encryptionAlgorithm";
    public static final String ENCRYPTION_KEY_IDENTIFIER = ConfigurationConstants.ENC_KEY_ID;
    public static final String ENCRYPTION_MODE = "encryptionMode";
    public static final String ENCRYPTION_MODE_JSON = MODE_JSON;
    public static final String ENCRYPTION_MODE_COMPACT = MODE_COMPACT;
    public static final String ENCRYPTION_DEFLATE = "deflate";
    public static final String ENCRYPTION_DEFLATE_TRUE = "true";
    public static final String ENCRYPTION_DEFLATE_FALSE = "false";
    public static final String DECRYPTION_JWK_SET_FILE = "decryptionJWKSetFile";
    public static final String DECRYPTION_PROPERTY_FILE = ConfigurationConstants.DEC_PROP_FILE;
    public static final String DECRYPTION_PROPERTY_REF_ID = ConfigurationConstants.DEC_PROP_REF_ID;
    public static final String DECRYPTION_TRUSTSTORE_PROPERTY_FILE = "decryptionTrustStorePropFile";
    public static final String DECRYPTION_TRUSTSTORE_PROPERTY_REF_ID = "decryptionTrustStorePropRefId";
    public static final String DECRYPTION_MULTI_PROPERTY_FILE = "decryptionMultiPropFile";
    public static final String DECRYPTION_SYMMETRIC_KEY_VALUE = "decryptionSymmetricKeyValue";
    public static final String DECRYPTION_SYMMETRIC_ALGORITHM = "decryptionSymAlgorithm";
    public static final String DECRYPTION_USER = "decryptionUser";
    public static final String DECRYPTION_PASSWORD = "decryptionPassword";
    public static final String DECRYPTION_SYMMETRIC = "decryptionSymmetricKey";
    public static final String DECRYPTION_SYMMETRIC_WRAPPED = "decryptionSymmetricKeyWrapped";
    public static final String DECRYPTION_SYMMETRIC_WRAPPED_TRUE = "true";
    public static final String DECRYPTION_SYMMETRIC_WRAPPED_FALSE = "false";
    public static final String DECRYPTION_MODE = "decryptionMode";
    public static final String DECRYPTION_MODE_JSON = MODE_JSON;
    public static final String DECRYPTION_MODE_COMPACT = MODE_COMPACT;
    
	
    public static final String SIGNATURE_ACTION = ConfigurationConstants.SIGNATURE;
    public static final String SIGNATURE_USER = ConfigurationConstants.SIGNATURE_USER;
    public static final String SIGNATURE_PASSWORD = "signaturePassword";
    public static final String USE_REQ_SIG_CERT = ConfigurationConstants.USE_REQ_SIG_CERT;
    public static final String SIGNATURE_SOAP_FAULT = "signatureSOAPFault";
    public static final String SIGNATURE_PROBLEM_DETAILS = "signatureProblemDetails";
    public static final String SIGNATURE_PARTS = ConfigurationConstants.SIGNATURE_PARTS;
    public static final String SIGNATURE_PARTS_VERIFY = "signaturePartsVerify";
    /** SIGNATURE_ATTACHMENTS_PARTS: {Content/Complete}{indice} 
    // utilizzo: {Content}{Attach}{*}
    // o sintassi wss4j {}cid:Attachments
    // Il valore * o '' puo' essere usato come indice per indicare qualsiasi, altrimenti l'indice indica la posizione dell'attachment*/
    public static final String SIGNATURE_NAMESPACE_ATTACH = NAMESPACE_ATTACH;
    public static final String SIGNATURE_PART_CONTENT = PART_CONTENT;
    public static final String SIGNATURE_PART_COMPLETE = PART_COMPLETE;
    public static final String SIGNATURE_PART_ELEMENT = PART_ELEMENT;
    public static final String SIGNATURE_JWK_SET_FILE = "signatureJWKSetFile";
    public static final String SIGNATURE_PROPERTY_REF_ID = ConfigurationConstants.SIG_PROP_REF_ID;
    public static final String SIGNATURE_PROPERTY_FILE = ConfigurationConstants.SIG_PROP_FILE;
    public static final String SIGNATURE_TRUSTSTORE_PROPERTY_FILE = "signatureTrustStorePropFile";
    public static final String SIGNATURE_TRUSTSTORE_PROPERTY_REF_ID = "signatureTrustStorePropRefId";
    public static final String SIGNATURE_MULTI_PROPERTY_FILE = "signatureMultiPropFile";
    public static final String SIGNATURE_C14N_ALGORITHM = "signatureC14nAlgorithm";
    public static final String SIGNATURE_DIGEST_ALGORITHM = ConfigurationConstants.SIG_DIGEST_ALGO;
    public static final String SIGNATURE_ALGORITHM = ConfigurationConstants.SIG_ALGO;
    public static final String SIGNATURE_KEY_IDENTIFIER = ConfigurationConstants.SIG_KEY_ID;
    public static final String SIGNATURE_CRL = "signatureCRL";
    public static final String SIGNATURE_OCSP = "signatureOCSP";
    public static final String SIGNATURE_VERIFICATION_PROPERTY_REF_ID = ConfigurationConstants.SIG_VER_PROP_REF_ID;
    public static final String SIGNATURE_VERIFICATION_PROPERTY_FILE = ConfigurationConstants.SIG_VER_PROP_FILE;
    public static final String SIGNATURE_MODE = "signatureMode";
    public static final String SIGNATURE_MODE_JSON = MODE_JSON;
    public static final String SIGNATURE_MODE_COMPACT = MODE_COMPACT;
    public static final String SIGNATURE_PAYLOAD_ENCODING = "signaturePayloadEncoding";
    public static final String SIGNATURE_PAYLOAD_ENCODING_TRUE = "true";
    public static final String SIGNATURE_PAYLOAD_ENCODING_FALSE = "false";
    public static final String SIGNATURE_DETACHED = "signatureDetached";
    public static final String SIGNATURE_DETACHED_TRUE = "true";
    public static final String SIGNATURE_DETACHED_FALSE = "false";
    public static final String SIGNATURE_DETACHED_BASE64 = "signatureDetachedBase64";
    public static final String SIGNATURE_DETACHED_BASE64_TRUE = "true";
    public static final String SIGNATURE_DETACHED_BASE64_FALSE = "false";
    public static final boolean SIGNATURE_DETACHED_BASE64_DEFAULT = Boolean.parseBoolean(SIGNATURE_DETACHED_BASE64_TRUE);
    public static final String SIGNATURE_DETACHED_HEADER = "signatureDetachedHeader";
    public static final String SIGNATURE_DETACHED_PROPERTY_URL = "signatureDetachedPropertyURL";
    public static final String SIGNATURE_XML_KEY_INFO = "keyInfo";
    public static final String SIGNATURE_XML_KEY_INFO_ALIAS = "keyInfoAlias";
    public static final String SIGNATURE_XML_KEY_INFO_X509 = "x509";
    public static final String SIGNATURE_XML_KEY_INFO_RSA = "RSA";
    
    public static final String USERNAME_TOKEN_ACTION = ConfigurationConstants.USERNAME_TOKEN;
    public static final String USERNAME_TOKEN_NO_PASSWORD_ACTION = ConfigurationConstants.USERNAME_TOKEN_NO_PASSWORD;
    public static final String USERNAME_TOKEN_SIGNATURE_ACTION = ConfigurationConstants.USERNAME_TOKEN_SIGNATURE;
    public static final String USERNAME_TOKEN_SOAP_FAULT = "UsernameTokenSOAPFault";
    public static final String USERNAME_TOKEN_PROBLEM_DETAILS = "UsernameTokenProblemDetails"; 
    
    public static final String SAML_TOKEN_SIGNED_ACTION = ConfigurationConstants.SAML_TOKEN_SIGNED;
    public static final String SAML_TOKEN_UNSIGNED_ACTION = ConfigurationConstants.SAML_TOKEN_UNSIGNED;
    public static final String SAML_TOKEN_SOAP_FAULT = "SAMLTokenSOAPFault";
    public static final String SAML_TOKEN_PROBLEM_DETAILS = "SAMLTokenProblemDetails"; 
    
    public static final String JOSE_KID = "joseKeyId";
    public static final String JOSE_KID_TRUE = "true";
    public static final String JOSE_KID_FALSE = "false";
    public static final String JOSE_KID_CUSTOM = "joseKeyIdCustom";
    public static final String JOSE_INCLUDE_CERT = "joseIncludeCert";
    public static final String JOSE_INCLUDE_CERT_TRUE = "true";
    public static final String JOSE_INCLUDE_CERT_FALSE = "false";
    public static final String JOSE_INCLUDE_CERT_CHAIN = "joseIncludeCertChain";
    public static final String JOSE_INCLUDE_CERT_CHAIN_TRUE = "true";
    public static final String JOSE_INCLUDE_CERT_CHAIN_FALSE = "false";
    public static final String JOSE_INCLUDE_CERT_SHA = "joseIncludeCertSHA";
    public static final String JOSE_INCLUDE_CERT_SHA_1 = "sha1";
    public static final String JOSE_INCLUDE_CERT_SHA_256 = "sha256";
    public static final String JOSE_CONTENT_TYPE = "joseContentType";
    public static final String JOSE_CONTENT_TYPE_TRUE = "true";
    public static final String JOSE_CONTENT_TYPE_FALSE = "false";
    public static final String JOSE_TYPE = "joseType";
    public static final String JOSE_X509_URL = "joseX509Url";
    public static final String JOSE_JWK_SET_URL = "joseJWKSetUrl";
    public static final String JOSE_CRITICAL_HEADERS = "joseCriticalHeaders";
    public static final String JOSE_CRITICAL_HEADERS_SEPARATOR = ",";
    public static final String JOSE_EXT_HEADER_PREFIX = "joseExtensionHeader.";
    public static final String JOSE_EXT_HEADER_SUFFIX_NAME = ".name";
    public static final String JOSE_EXT_HEADER_SUFFIX_VALUE = ".value";
    
    public static final String JOSE_USE_HEADERS = "joseUseHeaders";
    public static final String JOSE_USE_HEADERS_X5C = "joseUseHeaders.x5c";
    public static final String JOSE_USE_HEADERS_X5U = "joseUseHeaders.x5u";
    public static final String JOSE_USE_HEADERS_X5T = "joseUseHeaders.x5t";
    public static final String JOSE_USE_HEADERS_X5T_256 = "joseUseHeaders.x5t256";
    public static final String JOSE_USE_HEADERS_JWK = "joseUseHeaders.jwk";
    public static final String JOSE_USE_HEADERS_JKU = "joseUseHeaders.jku";
    public static final String JOSE_USE_HEADERS_KID = "joseUseHeaders.kid";
    public static final String JOSE_USE_HEADERS_TRUE = "true";
    public static final String JOSE_USE_HEADERS_FALSE = "false";
    
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_TYPE = "joseUseHeaders.truststore.type";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_FILE = "joseUseHeaders.truststore.file";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_KEY_PAIR_ALGORITHM = "joseUseHeaders.truststore.file.algorithm";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_PASSWORD = "joseUseHeaders.truststore.password";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_CRL =  "joseUseHeaders.truststore.crl";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_OCSP =  "joseUseHeaders.truststore.ocsp";
    
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_SSL_TYPE = RSSecurityConstants.RSSEC_KEY_STORE_TYPE+".ssl";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_SSL_FILE = RSSecurityConstants.RSSEC_KEY_STORE_FILE+".ssl";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_SSL_PASSWORD = RSSecurityConstants.RSSEC_KEY_STORE_PSWD+".ssl";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_SSL_CRL =  RSSecurityConstants.RSSEC_KEY_STORE+".ssl.crl";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_SSL_OCSP =  RSSecurityConstants.RSSEC_KEY_STORE+".ssl.ocsp";
    
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_PROVIDER = "joseUseHeaders.truststore.remoteStoreProvider";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_KEY_TYPE = "joseUseHeaders.truststore.remoteStoreKeyType";
    public static final String JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_CONFIG = "joseUseHeaders.truststore.remoteStoreConfig";
    
    public static final String JOSE_USE_HEADERS_KEYSTORE_TYPE = "joseUseHeaders.keystore.type";
    public static final String JOSE_USE_HEADERS_KEYSTORE_FILE = "joseUseHeaders.keystore.file";
    public static final String JOSE_USE_HEADERS_KEYSTORE_KEY_PAIR_ALGORITHM = "joseUseHeaders.keystore.file.algorithm";
    public static final String JOSE_USE_HEADERS_KEYSTORE_PASSWORD = "joseUseHeaders.keystore.password";
    public static final String JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW = "joseUseHeaders.key.";
    public static final String JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW_SUFFIX_ALIAS = ".alias";
    public static final String JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW_SUFFIX_PW = ".password";
        
    public static final String TIMESTAMP_ACTION = ConfigurationConstants.TIMESTAMP;
    public static final String TIMESTAMP_TTL = ConfigurationConstants.TTL_TIMESTAMP;
    
	public static final String TIMESTAMP_STRICT = ConfigurationConstants.TIMESTAMP_STRICT;
	public static final String TIMESTAMP_PRECISION = ConfigurationConstants.TIMESTAMP_PRECISION;
    public static final String TIMESTAMP_FUTURE_TTL = ConfigurationConstants.TTL_FUTURE_TIMESTAMP;
    
    public static final String TIMESTAMP_SOAPBOX_TTL_DEFAULT = "300";
	public static final String TIMESTAMP_SOAPBOX_FUTURE_TTL_DEFAULT =  "60";

	public static final String USERNAME_TOKEN_PW = "usernameTokenPassword";
	public static final String USERNAME_TOKEN_PW_TYPE = ConfigurationConstants.PASSWORD_TYPE;
	public static final String USERNAME_TOKEN_PW_TYPE_DIGEST = WSS4JConstants.PW_DIGEST;
	public static final String USERNAME_TOKEN_PW_TYPE_TEXT = WSS4JConstants.PW_TEXT;
	public static final String USERNAME_TOKEN_PW_TYPE_NONE = WSS4JConstants.PW_NONE;
	
	public static final String USERNAME_TOKEN_PW_MAP_MODE = "usernameTokenPasswordMode";
	public static final String USERNAME_TOKEN_PW_MAP_MODE_SINGLE = "single";
	public static final String USERNAME_TOKEN_PW_MAP_MODE_MAP = "map";
	
	public static final String USERNAME_TOKEN_PW_MAP = "usernameTokenPasswordMap";
	
	public static final String SAML_PROF_FILE =  "samlPropFile";
	public static final String SAML_PROF_REF_ID  =  "samlPropRefId";
	public static final String SAML_CALLBACK_REF = ConfigurationConstants.SAML_CALLBACK_REF;
	public static final String SAML_VERSION_XMLCONFIG_ID =  "samlVersion";
	public static final String SAML_VERSION_XMLCONFIG_ID_VALUE_20 =  "2.0";
	public static final String SAML_ISSUER_FORMAT_XMLCONFIG_ID_2 =  "issuerFormat2";
	public static final String SAML_SUBJECT_FORMAT_XMLCONFIG_ID_1 =  "subjectFormat1";
	public static final String SAML_SUBJECT_FORMAT_XMLCONFIG_ID_2 =  "subjectFormat2";
	public static final String SAML_SUBJECT_CONFIRMATION_METHOD_XMLCONFIG_ID_1 =  "subjectConfirmationMethod1";
	public static final String SAML_SUBJECT_CONFIRMATION_METHOD_XMLCONFIG_ID_2 =  "subjectConfirmationMethod2";
	public static final String SAML_AUTHN_CONTEXT_CLASS_REF_XMLCONFIG_ID_1 =  "authnContextClassRef1";
	public static final String SAML_AUTHN_CONTEXT_CLASS_REF_XMLCONFIG_ID_2 =  "authnContextClassRef2";
	public static final String SAML_ENVELOPED_SAML_SIGNATURE_XMLCONFIG_PREFIX_ID =  "signatureActionChoice";
	public static final String SAML_ATTRIBUTE_STATEMENT_FORMAT_XMLCONFIG_PREFIX_ID =  "attributeStatementFormat_";
	public static final String SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID =  "validateSamlSubjectConfirmationType";
	public static final String SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID_SENDER_VOUCHES =  "sender-vouches";
	public static final String SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID_HOLDER_OF_KEY =  "holder-of-key";
	public static final String SAML_SIGNATURE_PARAM_CONVERTO_INTO_SAML_CONFIG = "signaturePropRefId_convertParamsIntoSamlPropRefId";
	public static final String SAML_SIGNATURE_PARAM_CONVERTO_INTO_SAML_CONFIG_HOLDER_OF_KEY = "signaturePropRefId_convertParamsIntoSamlPropRefId_holderOfKey";
	public static final String SAML_NAMESPACE_TEMPLATE = "SAML_TEMPLATE_NS";
	
	public static final String AUTH_PDP_LOCAL = "pdpLocal"; // true/false (default true)
	public static final String AUTH_PDP_REMOTE_URL = "pdpRemoteUrl";
	public static final String AUTH_PDP_REMOTE_CONNECTION_TIMEOUT = "pdpRemoteConnectionTimeout";
	public static final String AUTH_PDP_REMOTE_READ_CONNECTION_TIMEOUT = "pdpRemoteReadConnectionTimeout";
	
	// Do not perform any action, do nothing. Only applies to DOM code.
	public static final String ACTION_NO_SECURITY = WSHandlerConstants.NO_SECURITY;
	// Perform a UsernameTokenSignature action.
	public static final String ACTION_USERNAME_TOKEN_SIGNATURE = ConfigurationConstants.USERNAME_TOKEN_SIGNATURE;
	// Perform a UsernameToken action.
	public static final String ACTION_USERNAME_TOKEN = ConfigurationConstants.USERNAME_TOKEN; 
	// Used on the receiving side to specify a UsernameToken with no password
	public static final String ACTION_USERNAME_TOKEN_NO_PASSWORD = ConfigurationConstants.USERNAME_TOKEN_NO_PASSWORD; 
	// Perform an unsigned SAML Token action.
	public static final String ACTION_SAML_TOKEN_UNSIGNED = ConfigurationConstants.SAML_TOKEN_UNSIGNED; 	
	// Perform a signed SAML Token action.
	public static final String ACTION_SAML_TOKEN_SIGNED = ConfigurationConstants.SAML_TOKEN_SIGNED; 	
	// Perform a signature action.
	public static final String ACTION_SIGNATURE = SIGNATURE_ACTION;
	// Perform a encryption action.
	public static final String ACTION_ENCRYPTION = ENCRYPTION_ACTION;
	public static final String ACTION_ENCRYPT_OLD = ENCRYPT_ACTION_OLD;  // modificato costante in wss4j 2.3.x
	// Perform a decryption action.
	public static final String ACTION_DECRYPTION = DECRYPTION_ACTION;
	public static final String ACTION_DECRYPT_OLD = DECRYPT_ACTION_OLD;  // modificato costante in wss4j 2.3.x
	// Perform a Timestamp action.
	public static final String ACTION_TIMESTAMP = TIMESTAMP_ACTION;
	// Perform a Signature action with derived keys.
	public static final String ACTION_SIGNATURE_DERIVED = ConfigurationConstants.SIGNATURE_DERIVED;
	// Perform a Encryption action with derived keys.
	public static final String ACTION_ENCRYPTION_DERIVED = ConfigurationConstants.ENCRYPTION_DERIVED;
	@SuppressWarnings("deprecation")
	public static final String ACTION_ENCRYPT_DERIVED_OLD = ConfigurationConstants.ENCRYPT_DERIVED;  // modificato costante in wss4j 2.3.x
	// Perform a Signature action with a kerberos token. Only for StAX code.
	public static final String ACTION_SIGNATURE_WITH_KERBEROS_TOKEN = ConfigurationConstants.SIGNATURE_WITH_KERBEROS_TOKEN;
	// Perform a Encryption action with a kerberos token. Only for StAX code.
	public static final String ACTION_ENCRYPTION_WITH_KERBEROS_TOKEN = ConfigurationConstants.ENCRYPTION_WITH_KERBEROS_TOKEN;
	@SuppressWarnings("deprecation")
	public static final String ACTION_ENCRYPT_WITH_KERBEROS_TOKEN_OLD = ConfigurationConstants.ENCRYPT_WITH_KERBEROS_TOKEN;  // modificato costante in wss4j 2.3.x
	// Add a kerberos token.
	public static final String ACTION_KERBEROS_TOKEN = ConfigurationConstants.KERBEROS_TOKEN;
	// Add a "Custom" token from a CallbackHandler
	public static final String ACTION_CUSTOM_TOKEN = ConfigurationConstants.CUSTOM_TOKEN;
	
	public static boolean isActionEncryption(String action) {
		return 
				SecurityConstants.ACTION_ENCRYPTION.equals(action) || 
				SecurityConstants.ACTION_ENCRYPT_OLD.equals(action) || 
				SecurityConstants.ACTION_ENCRYPTION_DERIVED.equals(action) || 
				SecurityConstants.ACTION_ENCRYPT_DERIVED_OLD.equals(action) || 
				SecurityConstants.ACTION_ENCRYPTION_WITH_KERBEROS_TOKEN.equals(action) || 
				SecurityConstants.ACTION_ENCRYPT_WITH_KERBEROS_TOKEN_OLD.equals(action);
	}
	public static boolean containsActionEncryption(String action) {
		if(action==null) {
			return false;
		}
		return 
				action.contains(SecurityConstants.ACTION_ENCRYPTION) || 
				action.contains(SecurityConstants.ACTION_ENCRYPT_OLD) || 
				action.contains(SecurityConstants.ACTION_ENCRYPTION_DERIVED) || 
				action.contains(SecurityConstants.ACTION_ENCRYPT_DERIVED_OLD) || 
				action.contains(SecurityConstants.ACTION_ENCRYPTION_WITH_KERBEROS_TOKEN) || 
				action.contains(SecurityConstants.ACTION_ENCRYPT_WITH_KERBEROS_TOKEN_OLD);
	}
	public static boolean isActionDecryption(String action) {
		return 
				SecurityConstants.ACTION_DECRYPTION.equals(action) || 
				SecurityConstants.ACTION_DECRYPT_OLD.equals(action);
	}
	public static boolean containsActionDecryption(String action) {
		if(action==null) {
			return false;
		}
		return 
				action.contains(SecurityConstants.ACTION_DECRYPTION) || 
				action.contains(SecurityConstants.ACTION_DECRYPT_OLD);
	}
	
	public static boolean isActionUsernameToken(String action) {
		return 
				SecurityConstants.ACTION_USERNAME_TOKEN.equals(action) || 
				SecurityConstants.ACTION_USERNAME_TOKEN_NO_PASSWORD.equals(action) || 
				SecurityConstants.ACTION_USERNAME_TOKEN_SIGNATURE.equals(action);
	}
	public static boolean containsActionUsernameToken(String action) {
		if(action==null) {
			return false;
		}
		return 
				action.contains(SecurityConstants.ACTION_USERNAME_TOKEN) || 
				action.contains(SecurityConstants.ACTION_USERNAME_TOKEN_NO_PASSWORD) || 
				action.contains(SecurityConstants.ACTION_USERNAME_TOKEN_SIGNATURE);
	}
	
	public static boolean isActionSAMLToken(String action) {
		return 
				SecurityConstants.ACTION_SAML_TOKEN_SIGNED.equals(action) || 
				SecurityConstants.ACTION_SAML_TOKEN_UNSIGNED.equals(action);
	}
	public static boolean containsActionSAMLToken(String action) {
		if(action==null) {
			return false;
		}
		return 
				action.contains(SecurityConstants.ACTION_SAML_TOKEN_SIGNED) || 
				action.contains(SecurityConstants.ACTION_SAML_TOKEN_UNSIGNED);
	}
    
    public static final String KEY_IDENTIFIER_BST_DIRECT_REFERENCE = CostantiDB.KEY_IDENTIFIER_BST_DIRECT_REFERENCE;
    public static final String KEY_IDENTIFIER_ISSUER_SERIAL = CostantiDB.KEY_IDENTIFIER_ISSUER_SERIAL;
    public static final String KEY_IDENTIFIER_X509 = CostantiDB.KEY_IDENTIFIER_X509;
    public static final String KEY_IDENTIFIER_SKI = CostantiDB.KEY_IDENTIFIER_SKI;
    public static final String KEY_IDENTIFIER_EMBEDDED_KEY_NAME = CostantiDB.KEY_IDENTIFIER_EMBEDDED_KEY_NAME;
    public static final String KEY_IDENTIFIER_THUMBPRINT = CostantiDB.KEY_IDENTIFIER_THUMBPRINT;
    public static final String KEY_IDENTIFIER_ENCRYPTED_KEY_SHA1 = CostantiDB.KEY_IDENTIFIER_ENCRYPTED_KEY_SHA1;

    public static final String KEY_IDENTIFIER_BST_DIRECT_REFERENCE_USE_SINGLE_CERTIFICATE = ConfigurationConstants.USE_SINGLE_CERTIFICATE;
    
    public static final String KEY_IDENTIFIER_INCLUDE_SIGNATURE_TOKEN = ConfigurationConstants.INCLUDE_SIGNATURE_TOKEN;
    public static final String KEY_IDENTIFIER_INCLUDE_ENCRYPT_TOKEN = ConfigurationConstants.INCLUDE_ENCRYPTION_TOKEN;
    
    public static final String IS_BSP_COMPLIANT = ConfigurationConstants.IS_BSP_COMPLIANT;
    
    public static final String AUTHORIZATION_CLASS = "authorizationClass";
    
    public static final String DETACH_HEADER_WSS = "detachHeaderWSSecurity";
    public static final String DETACH_SECURITY_INFO = "detachSecurityInfo";
    
    // l'id puo' appartenere ad un altro header wssecurity con diverso actor/mustUnderstand. 
    // Se si abilita questa opzione in caso di presenza di differenti header wssecurity con actor differenti, 
    // la sicurezza può andare in errore sull'ultimo nodo, essendo state eliminate tutte le reference
    // Questa opzione serve però abilitarla dove sono presenti vecchi soap engine della sicurezza che lasciavano 'zomibie' degli id non riferiti ed utilizzati nell'header WSS
    // Questi attributi 'zombie' possono poi far fallire una eventuale validazione dei contenuti applicativi ad esempio (caso pdc)
    public static final String REMOVE_ALL_WSU_ID_REF = "removeAllWsuIdRef"; 
    
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    
    public static final String KEYSTORE_TYPE_KEY_PAIR_VALUE = CostantiDB.KEYSTORE_TYPE_KEY_PAIR;
    public static final String KEYSTORE_TYPE_KEY_PAIR_LABEL = CostantiLabel.KEYSTORE_TYPE_KEY_PAIR;
    
    public static final String KEYSTORE_TYPE_PUBLIC_KEY_VALUE = CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY;
    public static final String KEYSTORE_TYPE_PUBLIC_KEY_LABEL = CostantiLabel.KEYSTORE_TYPE_PUBLIC_KEY;
    
    public static final String KEYSTORE_TYPE_JWK_VALUE = CostantiDB.KEYSTORE_TYPE_JWK;
    public static final String KEYSTORE_TYPE_JWK_LABEL = CostantiLabel.KEYSTORE_TYPE_JWK;
    
    public static final String KEYSTORE_TYPE_JKS_VALUE = CostantiDB.KEYSTORE_TYPE_JKS;
    public static final String KEYSTORE_TYPE_JKS_LABEL = CostantiLabel.KEYSTORE_TYPE_JKS;
    
    public static final String KEYSTORE_TYPE_PKCS12_VALUE = CostantiDB.KEYSTORE_TYPE_PKCS12;
    public static final String KEYSTORE_TYPE_PKCS12_LABEL = CostantiLabel.KEYSTORE_TYPE_PKCS12;
    
    public static List<String> getTipologieKeystoreValues(boolean truststore){
		// NOTA:far ricreare la lista ogni volta, poiche' poi viene modificata
		List<String> l = new ArrayList<>();
		l.add(KEYSTORE_TYPE_JKS_VALUE);
		l.add(KEYSTORE_TYPE_PKCS12_VALUE);
		HSMUtils.fillTipologieKeystore(truststore, false, l);
		return l;
    }
    public static List<String> getTipologieKeystoreLabels(boolean truststore){
		// NOTA:far ricreare la lista ogni volta, poiche' poi viene modificata
		List<String> l = new ArrayList<>();
		l.add(KEYSTORE_TYPE_JKS_LABEL);
		l.add(KEYSTORE_TYPE_PKCS12_LABEL);
		HSMUtils.fillTipologieKeystore(truststore, false, l);
		return l;
    }
    
    public static final String KEYSTORE_TYPE_JCEKS_VALUE = "jceks";
    public static final String KEYSTORE_TYPE_JCEKS_LABEL = "JCEKS";
    public static List<String> getTipologieSecretKeystoreValues(){
		// NOTA:far ricreare la lista ogni volta, poiche' poi viene modificata
		List<String> l = new ArrayList<>();
		l.add(KEYSTORE_TYPE_JCEKS_VALUE);
		HSMUtils.fillTipologieKeystore(false, true, l);
		return l;
    }
    public static List<String> getTipologieSecretKeystoreLabels(){
		// NOTA:far ricreare la lista ogni volta, poiche' poi viene modificata
		List<String> l = new ArrayList<>();
		l.add(KEYSTORE_TYPE_JCEKS_LABEL);
		HSMUtils.fillTipologieKeystore(false, true, l);
		return l;
    }

    public static final String SECRETKEYSTORE_TYPE = "secretkeystoreType";
    public static final String SECRETKEYSTORE_FILE = "secretkeystoreFile";
    public static final String SECRETKEYSTORE_PASSWORD = "secretkeystorePassword";
    public static final String SECRETKEYSTORE_PRIVATE_KEY_PASSWORD = "secretkeystorePrivateKeyPassword";
    
    public static final String KEYSTORE_TYPE = "keystoreType";
    public static final String KEYSTORE_FILE = "keystoreFile";
    public static final String KEYSTORE_PASSWORD = "keystorePassword";
    public static final String KEYSTORE_PRIVATE_KEY_PASSWORD = "keystorePrivateKeyPassword";
    public static final String KEYSTORE_OCSP_POLICY = "keystoreOcspPolicy";
     
    public static final String TRUSTSTORE_TYPE = "truststoreType";
    public static final String TRUSTSTORE_FILE = "truststoreFile";
    public static final String TRUSTSTORE_PASSWORD = "truststorePassword";
    public static final String TRUSTSTORE_OCSP_POLICY = "truststoreOcspPolicy";

    /**
     * Produce l'encoding in base64 dell'attachment (prima di applicare la sicurezza)
     */
    public static final String PRE_BASE64_ENCODING_ATTACHMENT_TRUE = "true";
    public static final String PRE_BASE64_ENCODING_ATTACHMENT_FALSE= "false";
    public static final boolean PRE_BASE64_ENCODING_ATTACHMENT_DEFAULT = Boolean.parseBoolean(PRE_BASE64_ENCODING_ATTACHMENT_FALSE);
    public static final String PRE_BASE64_ENCODING_ATTACHMENT = "preBase64EncodingAttachment";

    /**
     * Produce l'encoding in base64 dell'attachment (dopo aver applicato la sicurezza)
     */
    public static final String POST_BASE64_ENCODING_ATTACHMENT_TRUE = "true";
    public static final String POST_BASE64_ENCODING_ATTACHMENT_FALSE = "false";
    public static final boolean POST_BASE64_ENCODING_ATTACHMENT_DEFAULT = Boolean.parseBoolean(POST_BASE64_ENCODING_ATTACHMENT_FALSE);
    public static final String POST_BASE64_ENCODING_ATTACHMENT = "postBase64DecodingAttachment";

    /**
     * Decodifica la rappresentazione base64 dell'attachment (dopo la validazione della sicurezza)
     */
    public static final String POST_BASE64_DECODING_ATTACHMENT_TRUE = "true";
    public static final String POST_BASE64_DECODING_ATTACHMENT_FALSE = "false";
    public static final boolean POST_BASE64_DECODING_ATTACHMENT_DEFAULT = Boolean.parseBoolean(POST_BASE64_DECODING_ATTACHMENT_FALSE);
    public static final String POST_BASE64_DECODING_ATTACHMENT = "postBase64DecodingAttachment";

    /**
     * Aggiunge le parentesi uncinate all'id degli attachment
     */
    public static final String ADD_ATTACHMENT_ID_BRACKETS_FALSE = "false";
    public static final String ADD_ATTACHMENT_ID_BRACKETS_TRUE = "true";
    public static final String ADD_ATTACHMENT_ID_BRACKETS = "addAttachmentIdBrackets";
    public static final boolean ADD_ATTACHMENT_ID_BRACKETS_DEFAULT = Boolean.parseBoolean(ADD_ATTACHMENT_ID_BRACKETS_FALSE);
    
}
