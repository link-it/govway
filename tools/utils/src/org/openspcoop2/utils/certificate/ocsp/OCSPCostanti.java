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

package org.openspcoop2.utils.certificate.ocsp;

/**
 * HSMManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPCostanti {

	public final static String PROPERTY_PREFIX = "ocsp.";
	
	public final static String PROPERTY_SUFFIX_TYPE = "type";
	
	public final static String PROPERTY_SUFFIX_LABEL = "label";
	
	public final static String PROPERTY_SUFFIX_WARNING_ONLY = "warningOnly";
	
	public final static String PROPERTY_SUFFIX_CERTIFICATE_CHAIN_VERIFY = "certificateChainVerify";
	
	public final static String PROPERTY_SUFFIX_CHECK_VALIDITY = "checkValidity";
	public final static String PROPERTY_SUFFIX_CHECK_CA_VALIDITY = "checkCAValidity";
	
	public final static String PROPERTY_SUFFIX_CA_SOURCE = "ca.source";
	public final static String PROPERTY_SUFFIX_CA_ALTERNATIVE_TRUST_STORE = "ca.alternativeTrustStore";
	public final static String PROPERTY_SUFFIX_CA_ALTERNATIVE_TRUST_STORE_PASSWORD = "ca.alternativeTrustStore.password";
	public final static String PROPERTY_SUFFIX_CA_ALTERNATIVE_TRUST_STORE_TYPE = "ca.alternativeTrustStore.type";
	public final static String PROPERTY_SUFFIX_CA_NOT_FOUD_REJECTS_CERTIFICATE = "ca.notFound.rejectsCertificate";
	
	public final static String PROPERTY_SUFFIX_NONCE_ENABLED = "nonce.enabled";
	
	public final static String PROPERTY_SUFFIX_SIGNER_TRUST_STORE = "signer.trustStore";
	public final static String PROPERTY_SUFFIX_SIGNER_TRUST_STORE_PASSWORD = "signer.trustStore.password";
	public final static String PROPERTY_SUFFIX_SIGNER_TRUST_STORE_TYPE = "signer.trustStore.type";
	public final static String PROPERTY_SUFFIX_SIGNER_ALIAS = "signer.alias";
		
	public final static String PROPERTY_SUFFIX_URL_SOURCE = "url.source";
	public final static String PROPERTY_SUFFIX_URL_ALTERNATIVE = "url.alternative";
	public final static String PROPERTY_SUFFIX_URL_NOT_FOUND_REJECTS_CERTIFICATE = "url.notFound.rejectsCertificate";
	public final static String PROPERTY_SUFFIX_URL_NOT_FOUND_REJECTS_CA = "url.notFound.rejectsCA";
	public final static String PROPERTY_SUFFIX_URL_BREAK_STATUS = "url.breakStatus";
	public final static String PROPERTY_SUFFIX_URL_RETURN_CODE_OK = "url.returnCodeOk";

	public final static String PROPERTY_SUFFIX_EXTENDED_KEY_USAGE = "extendedKeyUsage";
	
	public final static String PROPERTY_SUFFIX_READ_TIMEOUT = "readTimeout";
	public final static String PROPERTY_SUFFIX_CONNECT_TIMEOUT = "connectTimeout";
	
	public final static String PROPERTY_SUFFIX_HTTPS_TRUST_ALL_CERTS = "https.trustAllCerts";
	public final static String PROPERTY_SUFFIX_HTTPS_TRUST_STORE = "https.trustStore";
	public final static String PROPERTY_SUFFIX_HTTPS_TRUST_STORE_PASSWORD = "https.trustStore.password";
	public final static String PROPERTY_SUFFIX_HTTPS_TRUST_STORE_TYPE = "https.trustStore.type";
	
	public final static String PROPERTY_SUFFIX_SECURE_RANDOM_ALGORITHM = "secureRandomAlgorithm";
	
	public final static String PROPERTY_SUFFIX_RESPONSE_DATE_TOLERANCE_MS = "response.date.toleranceMilliseconds";
	
	public final static String PROPERTY_SUFFIX_CRL_SIGNING_CERT_CHECK = "crl.signingCert.check";
	
	public final static String PROPERTY_SUFFIX_CRL_CA_CHECK = "crl.ca.check";
	
	public final static String PROPERTY_SUFFIX_CRL_ENABLED = "crl.enabled";
	
	public final static String PROPERTY_SUFFIX_CRL_SOURCE = "crl.source";
	public final static String PROPERTY_SUFFIX_CRL_ALTERNATIVE = "crl.alternative";
	public final static String PROPERTY_SUFFIX_CRL_NOT_FOUND_REJECTS_CERTIFICATE = "crl.notFound.rejectsCertificate";
	public final static String PROPERTY_SUFFIX_CRL_NOT_FOUND_REJECTS_CA = "crl.notFound.rejectsCA";

	public final static String PROPERTY_SUFFIX_CRL_TRUSTSTORE_SOURCE = "crl.trustStore.source";
	public final static String PROPERTY_SUFFIX_CRL_ALTERNATIVE_TRUST_STORE = "crl.alternativeTrustStore";
	public final static String PROPERTY_SUFFIX_CRL_ALTERNATIVE_TRUST_STORE_PASSWORD = "crl.alternativeTrustStore.password";
	public final static String PROPERTY_SUFFIX_CRL_ALTERNATIVE_TRUST_STORE_TYPE = "crl.alternativeTrustStore.type";
	
}
