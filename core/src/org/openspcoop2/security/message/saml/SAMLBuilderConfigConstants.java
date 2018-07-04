/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.security.message.saml;

import org.apache.wss4j.common.saml.builder.SAML1Constants;
import org.apache.wss4j.common.saml.builder.SAML2Constants;

/**
 * SAMLBuilderConfigConstants
 * 	
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SAMLBuilderConfigConstants {

	public static final String SAML_CONFIG_BUILDER_SAML_CALLBACK = "org.apache.ws.security.saml.callback";
	
	public static final String SAML_CONFIG_BUILDER_CONFIG_NAME = "openspcoop2.saml.config.name";
	public static final String SAML_CONFIG_BUILDER_CACHE = "openspcoop2.saml.cached";
	
	public static final String SAML_CONFIG_BUILDER_VERSION = "openspcoop2.saml.version";
	public static final String SAML_CONFIG_BUILDER_VERSION_10 = "1.0";
	public static final String SAML_CONFIG_BUILDER_VERSION_11 = "1.1";
	public static final String SAML_CONFIG_BUILDER_VERSION_20 = "2.0";
	
	public static final String SAML_CONFIG_BUILDER_ISSUER_VALUE = "openspcoop2.saml.issuer.value";
	public static final String SAML_CONFIG_BUILDER_ISSUER_QUALIFIER = "openspcoop2.saml.issuer.qualifier";
	public static final String SAML_CONFIG_BUILDER_ISSUER_FORMAT = "openspcoop2.saml.issuer.format";
	
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION = "openspcoop2.saml.assertion.sign";
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_CRYPTO_PROP_FILE = "openspcoop2.saml.assertion.sign.cryptoProp.file";
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_CRYPTO_PROP_REF_ID = "openspcoop2.saml.assertion.sign.cryptoProp.refId"; // si indica true. A questo punto il properties stesso e' usato anche come crypto
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_CRYPTO_PROP_KEYSTORE_TYPE = "openspcoop2.saml.assertion.sign.cryptoProp.keystore.type";
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_CRYPTO_PROP_KEYSTORE_FILE = "openspcoop2.saml.assertion.sign.cryptoProp.keystore.file";
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_CRYPTO_PROP_KEYSTORE_PASSWORD = "openspcoop2.saml.assertion.sign.cryptoProp.keystore.password";
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_KEY_NAME = "openspcoop2.saml.assertion.sign.key.name";
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_KEY_PASSWORD = "openspcoop2.saml.assertion.sign.key.password";
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_SEND_KEY_VALUE = "openspcoop2.saml.assertion.sign.sendKeyValue";
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_SIGNATURE_ALGORITHM = "openspcoop2.saml.assertion.sign.signatureAlgorithm";
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_SIGNATURE_DIGEST_ALGORITHM = "openspcoop2.saml.assertion.sign.signatureDigestAlgorithm";
	public static final String SAML_CONFIG_BUILDER_SIGN_ASSERTION_SIGNATURE_CANONICALIZATION_ALGORITHM = "openspcoop2.saml.assertion.sign.canonicalizationAlgorithm";
	
	//public static final String SAML_CONFIG_BUILDER_SUBJECT_ENABLED = "openspcoop2.saml.subject.enabled"; E' OBBLIGATORIO IL SUBJECT, SENNO VA IN NULL POINTER OPENSAML
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_VALUE = "openspcoop2.saml.subject.nameID.value";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_QUALIFIER = "openspcoop2.saml.subject.nameID.qualifier";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT = "openspcoop2.saml.subject.nameID.format";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_UNSPECIFIED = "UNSPECIFIED";  // urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_EMAIL = "EMAIL"; // urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_X509_SUBJECT = "X509_SUBJECT"; // urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_WIN_DOMAIN_QUALIFIED = "WIN_DOMAIN_QUALIFIED"; // urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_KERBEROS = "KERBEROS"; // urn:oasis:names:tc:SAML:2.0:nameid-format:kerberos
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_ENTITY = "ENTITY"; // urn:oasis:names:tc:SAML:2.0:nameid-format:entity 
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_PERSISTENT = "PERSISTENT"; // urn:oasis:names:tc:SAML:2.0:nameid-format:persistent
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_TRANSIENT = "TRANSIENT"; // urn:oasis:names:tc:SAML:2.0:nameid-format:transient
	public static final String SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_ENCRYPTED = "ENCRYPTED"; // urn:oasis:names:tc:SAML:2.0:nameid-format:encrypted

	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD = "openspcoop2.saml.subject.confirmation.method";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_ARTIFACT = "ARTIFACT";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_ARTIFACT_SAML_10 = "urn:oasis:names:tc:SAML:1.0:cm:artifact";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_IDENTITY = "IDENTITY";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_IDENTITY_SAML_10 = "urn:com:sun:identity";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_BEARER = "BEARER";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_BEARER_SAML_10 = SAML1Constants.CONF_BEARER;
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_BEARER_SAML_20 = SAML2Constants.CONF_BEARER;
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_HOLDER_OF_KEY = "HOLDER_OF_KEY";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_SAML_10 = SAML1Constants.CONF_HOLDER_KEY;
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_SAML_20 = SAML2Constants.CONF_HOLDER_KEY;                                 
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_SENDER_VOUCHES = "SENDER_VOUCHES";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_SENDER_VOUCHES_SAML_10 = SAML1Constants.CONF_SENDER_VOUCHES;
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_SENDER_VOUCHES_SAML_20 = SAML2Constants.CONF_SENDER_VOUCHES;
	
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_DATA_NOT_BEFORE = "openspcoop2.saml.subject.confirmation.data.notBefore.minutes";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_DATA_NOT_ON_OR_AFTER = "openspcoop2.saml.subject.confirmation.data.notOnOrAfter.minutes";
	
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_PROPERTIES_FILE = "openspcoop2.saml.subject.confirmation.method.holderOfKey.cryptoProperties";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_PROPERTIES_REF_ID = "openspcoop2.saml.subject.confirmation.method.holderOfKey.cryptoProperties.refId"; // si indica true. A questo punto il properties stesso e' usato anche come crypto
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_PROPERTIES_KEYSTORE_TYPE = "openspcoop2.saml.subject.confirmation.method.holderOfKey.cryptoProperties.keystore.type";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_PROPERTIES_KEYSTORE_FILE = "openspcoop2.saml.subject.confirmation.method.holderOfKey.cryptoProperties.keystore.file";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_PROPERTIES_KEYSTORE_PASSWORD = "openspcoop2.saml.subject.confirmation.method.holderOfKey.cryptoProperties.keystore.password";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_ALIAS = "openspcoop2.saml.subject.confirmation.method.holderOfKey.cryptoCertificateAlias";
	
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_DATA_ADDRESS = "openspcoop2.saml.subject.confirmation.data.address";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_DATA_IN_RESPONSE_TO = "openspcoop2.saml.subject.confirmation.data.inResponseTo";
	public static final String SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_DATA_RECIPIENT = "openspcoop2.saml.subject.confirmation.data.recipient";
	
	//public static final String SAML_CONFIG_BUILDER_CONDITIONS_ENABLED = "openspcoop2.saml.conditions.enabled"; VENGONO GENERATE COMUNQUE
	public static final String SAML_CONFIG_BUILDER_CONDITIONS_DATA_NOT_BEFORE = "openspcoop2.saml.conditions.notBefore.minutes";
	public static final String SAML_CONFIG_BUILDER_CONDITIONS_DATA_NOT_ON_OR_AFTER = "openspcoop2.saml.conditions.notOnOrAfter.minutes";
	public static final String SAML_CONFIG_BUILDER_CONDITIONS_AUDIENCE_URI = "openspcoop2.saml.conditions.audienceURI";
	
	public static final String SAML_CONFIG_BUILDER_AUTHN_STATEMENT_ENABLED = "openspcoop2.saml.authn.statement.enabled";
	public static final String SAML_CONFIG_BUILDER_AUTHN_STATEMENT_DATA_INSTANT = "openspcoop2.saml.authn.statement.instant.minutes";
	public static final String SAML_CONFIG_BUILDER_AUTHN_STATEMENT_DATA_INSTANT_FORMAT = "openspcoop2.saml.authn.statement.instant.minutes.format";
	public static final String SAML_CONFIG_BUILDER_AUTHN_STATEMENT_DATA_INSTANT_VALUE = "openspcoop2.saml.authn.statement.instant.minutes.value";
	public static final String SAML_CONFIG_BUILDER_AUTHN_STATEMENT_DATA_NOT_ON_OR_AFTER = "openspcoop2.saml.authn.statement.notOnOrAfter.minutes";
	public static final String SAML_CONFIG_BUILDER_AUTHN_STATEMENT_DATA_NOT_ON_OR_AFTER_FORMAT = "openspcoop2.saml.authn.statement.notOnOrAfter.minutes.format";
	public static final String SAML_CONFIG_BUILDER_AUTHN_STATEMENT_DATA_NOT_ON_OR_AFTER_VALUE = "openspcoop2.saml.authn.statement.notOnOrAfter.minutes.value";
	public static final String SAML_CONFIG_BUILDER_AUTHN = "openspcoop2.saml.authn";
	public static final String SAML_CONFIG_BUILDER_AUTHN_SUBJECT_LOCALITY_IP_ADDRESS = "openspcoop2.saml.authn.subjectLocality.ipAddress";
	public static final String SAML_CONFIG_BUILDER_AUTHN_SUBJECT_LOCALITY_DNS_ADDRESS = "openspcoop2.saml.authn.subjectLocality.dnsAddress";
	// sia 1.1 che 2.0
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_UNSPECIFIED = "UNSPECIFIED";
	public static final String SAML_CONFIG_BUILDER_AUTHN_UNSPECIFIED_SAML10 = SAML1Constants.AUTH_METHOD_UNSPECIFIED;
	public static final String SAML_CONFIG_BUILDER_AUTHN_UNSPECIFIED_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_UNSPECIFIED;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_PASSWORD = "PASSWORD";
	public static final String SAML_CONFIG_BUILDER_AUTHN_PASSWORD_SAML10 = SAML1Constants.AUTH_METHOD_PASSWORD;
	public static final String SAML_CONFIG_BUILDER_AUTHN_PASSWORD_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_PASSWORD;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_KERBEROS = "KERBEROS";
	public static final String SAML_CONFIG_BUILDER_AUTHN_KERBEROS_SAML10 = SAML1Constants.AUTH_METHOD_KERBEROS;
	public static final String SAML_CONFIG_BUILDER_AUTHN_KERBEROS_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_KERBEROS;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_TLS = "TLS";
	public static final String SAML_CONFIG_BUILDER_AUTHN_TLS_SAML10 = SAML1Constants.AUTH_METHOD_TLS_CLIENT;
	public static final String SAML_CONFIG_BUILDER_AUTHN_TLS_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_TLS_CLIENT;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_X509 = "X509";
	public static final String SAML_CONFIG_BUILDER_AUTHN_X509_SAML10 = SAML1Constants.AUTH_METHOD_X509;
	public static final String SAML_CONFIG_BUILDER_AUTHN_X509_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_X509;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_PGP = "PGP";
	public static final String SAML_CONFIG_BUILDER_AUTHN_PGP_SAML10 = SAML1Constants.AUTH_METHOD_PGP;
	public static final String SAML_CONFIG_BUILDER_AUTHN_PGP_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_PGP;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_SRP = "SRP";
	public static final String SAML_CONFIG_BUILDER_AUTHN_SRP_SAML10 = SAML1Constants.AUTH_METHOD_SRP;
	public static final String SAML_CONFIG_BUILDER_AUTHN_SRP_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_SECURED_REMOTE_PASSWORD;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_SPKI = "SPKI";
	public static final String SAML_CONFIG_BUILDER_AUTHN_SPKI_SAML10 = SAML1Constants.AUTH_METHOD_SPKI;
	public static final String SAML_CONFIG_BUILDER_AUTHN_SPKI_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_SPKI;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_DSIG = "DSIG";
	public static final String SAML_CONFIG_BUILDER_AUTHN_DSIG_SAML10 = SAML1Constants.AUTH_METHOD_DSIG;
	public static final String SAML_CONFIG_BUILDER_AUTHN_DSIG_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_XMLDSIG;
	// 1.1
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_HARDWARE = "HARDWARE";
	public static final String SAML_CONFIG_BUILDER_AUTHN_HARDWARE_SAML10 = SAML1Constants.AUTH_METHOD_HARDWARE_TOKEN;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_XKMS = "XKMS";
	public static final String SAML_CONFIG_BUILDER_AUTHN_XKMS_SAML10 = SAML1Constants.AUTH_METHOD_XKMS;
	// 2.0
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_INTERNET_PROTOCOL = "INTERNET_PROTOCOL";
	public static final String SAML_CONFIG_BUILDER_AUTHN_INTERNET_PROTOCOL_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_INTERNET_PROTOCOL;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_INTERNET_PROTOCOL_PASSWORD = "INTERNET_PROTOCOL_PASSWORD";
	public static final String SAML_CONFIG_BUILDER_AUTHN_INTERNET_PROTOCOL_PASSWORD_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_INTERNET_PROTOCOL_PASSWORD;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_ONE_FACTOR_UNREGISTERED = "MOBILE_ONE_FACTOR_UNREGISTERED";
	public static final String SAML_CONFIG_BUILDER_AUTHN_MOBILE_ONE_FACTOR_UNREGISTERED_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_MOBILE_ONE_FACTOR_UNREGISTERED;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_TWO_FACTOR_UNREGISTERED = "MOBILE_TWO_FACTOR_UNREGISTERED";
	public static final String SAML_CONFIG_BUILDER_AUTHN_MOBILE_TWO_FACTOR_UNREGISTERED_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_MOBILE_TWO_FACTOR_UNREGISTERED;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_ONE_FACTOR_CONTRACT = "MOBILE_ONE_FACTOR_CONTRACT";
	public static final String SAML_CONFIG_BUILDER_AUTHN_MOBILE_ONE_FACTOR_CONTRACT_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_MOBILE_ONE_FACTOR_CONTRACT;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_MOBILE_TWO_FACTOR_CONTRACT = "MOBILE_TWO_FACTOR_CONTRACT";
	public static final String SAML_CONFIG_BUILDER_AUTHN_MOBILE_TWO_FACTOR_CONTRACT_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_MOBILE_TWO_FACTOR_CONTRACT;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_PASSWORD_PROTECTED_TRANSPORT = "PASSWORD_PROTECTED_TRANSPORT";
	public static final String SAML_CONFIG_BUILDER_AUTHN_PASSWORD_PROTECTED_TRANSPORT_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_PASSWORD_PROTECTED_TRANSPORT;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_PREVIOUS_SESSION = "PREVIOUS_SESSION";
	public static final String SAML_CONFIG_BUILDER_AUTHN_PREVIOUS_SESSION_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_PREVIOUS_SESSION;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_SMARTCARD = "SMARTCARD";
	public static final String SAML_CONFIG_BUILDER_AUTHN_SMARTCARD_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_SMARTCARD;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_SMARTCARD_PKI = "SMARTCARD_PKI";
	public static final String SAML_CONFIG_BUILDER_AUTHN_SMARTCARD_PKI_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_SMARTCARD_PKI;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_SOFTWARE_PKI = "SOFTWARE_PKI";
	public static final String SAML_CONFIG_BUILDER_AUTHN_SOFTWARE_PKI_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_SOFTWARE_PKI;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_TELEPHONY = "TELEPHONY";
	public static final String SAML_CONFIG_BUILDER_AUTHN_TELEPHONY_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_TELEPHONY;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_NOMAD_TELEPHONY = "NOMAD_TELEPHONY";
	public static final String SAML_CONFIG_BUILDER_AUTHN_NOMAD_TELEPHONY_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_NOMAD_TELEPHONY;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_PERSONAL_TELEPHONY = "PERSONAL_TELEPHONY";
	public static final String SAML_CONFIG_BUILDER_AUTHN_PERSONAL_TELEPHONY_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_PERSONAL_TELEPHONY;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_AUTHENTICATED_TELEPHONY = "AUTHENTICATED_TELEPHONY";
	public static final String SAML_CONFIG_BUILDER_AUTHN_AUTHENTICATED_TELEPHONY_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_AUTHENTICATED_TELEPHONY;
	public static final String SAML_CONFIG_BUILDER_AUTHN_VALUE_TIME_SYNC = "TIME_SYNC";
	public static final String SAML_CONFIG_BUILDER_AUTHN_TIME_SYNC_SAML20 = SAML2Constants.AUTH_CONTEXT_CLASS_REF_TIME_SYNC_TOKEN;
	  
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX = "openspcoop2.saml.attribute.statement.";
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_QUALIFIED_NAME = ".name.qualified";
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_SIMPLE_NAME = ".name.simple";
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME = ".name.format";
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE_SEPARATOR = ".values.separator";
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE = ".values";
	
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_UNSPECIFIED = "UNSPECIFIED";
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_UNSPECIFIED_SAML20 = SAML2Constants.ATTRNAME_FORMAT_UNSPECIFIED;
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_URI = "URI";
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_URI_SAML20 = SAML2Constants.ATTRNAME_FORMAT_URI;
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_BASIC = "BASIC";
	public static final String SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_BASIC_SAML20 = SAML2Constants.ATTRNAME_FORMAT_BASIC;
	
}
