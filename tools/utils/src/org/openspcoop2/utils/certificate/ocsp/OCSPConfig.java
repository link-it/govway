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
package org.openspcoop2.utils.certificate.ocsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ExtendedKeyUsage;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.random.SecureRandomAlgorithm;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * OCSPConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPConfig {
	
	private String id;

	private String type;
	
	private String label;
	
	private boolean certificateChainVerify = true;
	
	private boolean checkValidity = true;
	private boolean checkCAValidity = true;
	
	private List<CertificateSource> caSource = new ArrayList<>();
	private String alternativeTrustStoreCA_path;
	private String alternativeTrustStoreCA_password;
	private String alternativeTrustStoreCA_type;
	private boolean rejectsCertificateWithoutCA = true;
	
	private String trustStoreSigner_path;
	private String trustStoreSigner_password;
	private String trustStoreSigner_type;
	private String aliasCertificateSigner;
	
	private boolean nonce;
	
	private List<CertificateSource> responderUrlSource = new ArrayList<>();
	private List<String> alternativeResponderUrl;
	private List<String> alternativeResponderUrlCA;
	private boolean rejectsCertificateWithoutResponderUrl = true;
	private boolean rejectsCAWithoutResponderUrl = false;
	private List<OCSPResponseCode> responderBreakStatus = null;
	private List<Integer> responderReturnCodeOk = null;
	
	// fondamentale per prevenire attacchi man in the middle (siamo in http) firmando con un altro certificato rilasciato dalla CA, non adibito a firmare risposte OCSP
	private List<ExtendedKeyUsage> extendedKeyUsageRequired = null;
	
	private int readTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
	private int connectTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
		
	private boolean externalResources_hostnameVerifier = true;
	private boolean externalResources_trustAllCerts = false;
	private String externalResources_trustStorePath;
	private String externalResources_trustStorePassword;
	private String externalResources_trustStoreType;
	
	private String externalResources_keyStorePath;
	private String externalResources_keyStorePassword;
	private String externalResources_keyStoreType;
	private String externalResources_keyAlias;
	private String externalResources_keyPassword;
	
	private String forwardProxy_url;
	private String forwardProxy_header;
	private String forwardProxy_queryParameter;
	private boolean forwardProxy_base64;
	
	private SecureRandomAlgorithm secureRandomAlgorithm;
	
	private int responseCheckDateToleranceMilliseconds;

	private boolean crl_signingCertCheck=false;
	private boolean crl_caCheck=false;
	private boolean crl = false;
	
	private List<CertificateSource> crlSource = new ArrayList<>();
	private List<String> crlAlternative = null;
	private boolean rejectsCertificateWithoutCRL = false;
	private boolean rejectsCAWithoutCRL = false;
	
	private List<CertificateSource> crlTrustStoreSource = new ArrayList<>();
	private String alternativeTrustStoreCRL_path;
	private String alternativeTrustStoreCRL_password;
	private String alternativeTrustStoreCRL_type;
				

	public String getId() {
		return this.id;
	}

	public String getType() {
		return this.type;
	}
	public String getLabel() {
		return this.label;
	}
	
	public boolean isCertificateChainVerify() {
		return this.certificateChainVerify;
	}
	
	public boolean isCheckValidity() {
		return this.checkValidity;
	}

	public boolean isCheckCAValidity() {
		return this.checkCAValidity;
	}
	
	public List<CertificateSource> getCaSource() {
		return this.caSource;
	}
	public String getAlternativeTrustStoreCA_path() {
		return this.alternativeTrustStoreCA_path;
	}
	public String getAlternativeTrustStoreCA_password() {
		return this.alternativeTrustStoreCA_password;
	}
	public String getAlternativeTrustStoreCA_type() {
		return this.alternativeTrustStoreCA_type;
	}
	public boolean isRejectsCertificateWithoutCA() {
		return this.rejectsCertificateWithoutCA;
	}
	
	public String getTrustStoreSigner_path() {
		return this.trustStoreSigner_path;
	}
	public String getTrustStoreSigner_password() {
		return this.trustStoreSigner_password;
	}
	public String getTrustStoreSigner_type() {
		return this.trustStoreSigner_type;
	}
	public String getAliasCertificateSigner() {
		return this.aliasCertificateSigner;
	}	
	
	public boolean isNonce() {
		return this.nonce;
	}
	
	public List<CertificateSource> getResponderUrlSource() {
		return this.responderUrlSource;
	}
	public List<String> getAlternativeResponderUrl() {
		return this.alternativeResponderUrl;
	}
	public List<String> getAlternativeResponderUrlCA() {
		return this.alternativeResponderUrlCA;
	}
	public boolean isRejectsCertificateWithoutResponderUrl() {
		return this.rejectsCertificateWithoutResponderUrl;
	}
	public boolean isRejectsCAWithoutResponderUrl() {
		return this.rejectsCAWithoutResponderUrl;
	}
	public List<OCSPResponseCode> getResponderBreakStatus() {
		return this.responderBreakStatus;
	}
	public List<Integer> getResponderReturnCodeOk() {
		return this.responderReturnCodeOk;
	}
	
	public List<ExtendedKeyUsage> getExtendedKeyUsageRequired() {
		return this.extendedKeyUsageRequired;
	}
		
	public int getReadTimeout() {
		return this.readTimeout;
	}
	public int getConnectTimeout() {
		return this.connectTimeout;
	}

	public boolean isExternalResources_hostnameVerifier() {
		return this.externalResources_hostnameVerifier;
	}
	public boolean isExternalResources_trustAllCerts() {
		return this.externalResources_trustAllCerts;
	}
	public String getExternalResources_trustStorePath() {
		return this.externalResources_trustStorePath;
	}
	public String getExternalResources_trustStorePassword() {
		return this.externalResources_trustStorePassword;
	}
	public String getExternalResources_trustStoreType() {
		return this.externalResources_trustStoreType;
	}
	
	public String getExternalResources_keyStorePath() {
		return this.externalResources_keyStorePath;
	}
	public String getExternalResources_keyStorePassword() {
		return this.externalResources_keyStorePassword;
	}
	public String getExternalResources_keyStoreType() {
		return this.externalResources_keyStoreType;
	}
	public String getExternalResources_keyAlias() {
		return this.externalResources_keyAlias;
	}
	public String getExternalResources_keyPassword() {
		return this.externalResources_keyPassword;
	}

	public String getForwardProxy_url() {
		return this.forwardProxy_url;
	}
	public String getForwardProxy_header() {
		return this.forwardProxy_header;
	}
	public String getForwardProxy_queryParameter() {
		return this.forwardProxy_queryParameter;
	}
	public boolean isForwardProxy_base64() {
		return this.forwardProxy_base64;
	}
	
	public SecureRandomAlgorithm getSecureRandomAlgorithm() {
		return this.secureRandomAlgorithm;
	}
	
	public int getResponseCheckDateToleranceMilliseconds() {
		return this.responseCheckDateToleranceMilliseconds;
	}
	
	public boolean isCrlSigningCertCheck() {
		return this.crl_signingCertCheck;
	}
	
	public boolean isCrlCaCheck() {
		return this.crl_caCheck;
	}
	
	public boolean isCrl() {
		return this.crl;
	}

	public List<CertificateSource> getCrlSource() {
		return this.crlSource;
	}

	public List<String> getCrlAlternative() {
		return this.crlAlternative;
	}
	
	public boolean isRejectsCertificateWithoutCRL() {
		return this.rejectsCertificateWithoutCRL;
	}
	public boolean isRejectsCAWithoutCRL() {
		return this.rejectsCAWithoutCRL;
	}
	
	public List<CertificateSource> getCrlTrustStoreSource() {
		return this.crlTrustStoreSource;
	}

	public String getAlternativeTrustStoreCRL_path() {
		return this.alternativeTrustStoreCRL_path;
	}

	public String getAlternativeTrustStoreCRL_password() {
		return this.alternativeTrustStoreCRL_password;
	}

	public String getAlternativeTrustStoreCRL_type() {
		return this.alternativeTrustStoreCRL_type;
	}

	
	protected OCSPConfig(String id, Properties p, Logger log) throws UtilsException {
		this.id = id;
		
		if(p==null || p.isEmpty()) {
			throw new UtilsException("Properties '"+OCSPCostanti.PROPERTY_PREFIX+id+".*' undefined");
		}
		
		this.type = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_TYPE, true);	
		this.label = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_LABEL, true);	
		
		this.certificateChainVerify = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CERTIFICATE_CHAIN_VERIFY, false, true);	
		
		this.checkValidity = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CHECK_VALIDITY, false, true);	
		this.checkCAValidity = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CHECK_CA_VALIDITY, false, true);	
		
		this.caSource = getCertificateSourceProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CA_SOURCE, true, null);
		
		this.alternativeTrustStoreCA_path = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CA_ALTERNATIVE_TRUST_STORE, false);	
		if(this.alternativeTrustStoreCA_path!=null && StringUtils.isNotEmpty(this.alternativeTrustStoreCA_path)) {
			this.alternativeTrustStoreCA_password = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CA_ALTERNATIVE_TRUST_STORE_PASSWORD, true);	
			this.alternativeTrustStoreCA_type = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CA_ALTERNATIVE_TRUST_STORE_TYPE, false);
			if(this.alternativeTrustStoreCA_type==null || StringUtils.isEmpty(this.alternativeTrustStoreCA_type)) {
				this.alternativeTrustStoreCA_type = KeystoreType.JKS.getNome();
			}
		}
		
		this.rejectsCertificateWithoutCA = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CA_NOT_FOUD_REJECTS_CERTIFICATE, false, true);	
		
		this.trustStoreSigner_path = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_SIGNER_TRUST_STORE, false);	
		if(this.trustStoreSigner_path!=null && StringUtils.isNotEmpty(this.trustStoreSigner_path)) {
			this.trustStoreSigner_password = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_SIGNER_TRUST_STORE_PASSWORD, true);	
			this.trustStoreSigner_type = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_SIGNER_TRUST_STORE_TYPE, false);
			if(this.trustStoreSigner_type==null || StringUtils.isEmpty(this.trustStoreSigner_type)) {
				this.trustStoreSigner_type = KeystoreType.JKS.getNome();
			}
		}
		
		this.aliasCertificateSigner = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_SIGNER_ALIAS, false);	
		if(this.aliasCertificateSigner!=null && StringUtils.isNotEmpty(this.aliasCertificateSigner)) {
			if(this.trustStoreSigner_path==null || StringUtils.isEmpty(this.trustStoreSigner_path)) {
				throw new UtilsException("Property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_SIGNER_ALIAS+"' require property '"+
						OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_SIGNER_TRUST_STORE+"'");
			}
		}
		
		this.nonce = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_NONCE_ENABLED, false, true);
		
		this.crl=getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ENABLED, false, false);
		
		if(this.crl) {
			this.responderUrlSource = getCertificateSourceProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_URL_SOURCE, false, null);
		}
		else {
			this.responderUrlSource = getCertificateSourceProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_URL_SOURCE, true, null);
		}
		if(this.responderUrlSource!=null && this.responderUrlSource.contains(CertificateSource.CONFIG)) {
			throw new UtilsException("Property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_URL_SOURCE+"' declare unsupported '"+CertificateSource.CONFIG+"' mode");
		}
		
		this.alternativeResponderUrl = getListProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_URL_ALTERNATIVE, false, null);
		this.alternativeResponderUrlCA = getListProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_URL_ALTERNATIVE_CA, false, null);
		this.rejectsCertificateWithoutResponderUrl = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_URL_NOT_FOUND_REJECTS_CERTIFICATE, false, true);	
		this.rejectsCAWithoutResponderUrl = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_URL_NOT_FOUND_REJECTS_CA, false, false);	
		this.responderBreakStatus = getOCSPResponseCodeProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_URL_BREAK_STATUS, false, null);	
		this.responderReturnCodeOk = getListIntProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_URL_RETURN_CODE_OK, false, null);
		
		// fondamentale per prevenire attacchi man in the middle (siamo in http) firmando con un altro certificato rilasciato dalla CA, non adibito a firmare risposte OCSP
		List<ExtendedKeyUsage> extendedKeyUsageRequiredDefault = new ArrayList<>();
		extendedKeyUsageRequiredDefault.add(ExtendedKeyUsage.OCSP_SIGNING);
		this.extendedKeyUsageRequired = getExtendedKeyUsageProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_EXTENDED_KEY_USAGE, extendedKeyUsageRequiredDefault);
		
		this.readTimeout = getIntProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_READ_TIMEOUT, false, 15000); // HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT);
		this.connectTimeout = getIntProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CONNECT_TIMEOUT, false, HttpUtilities.HTTP_CONNECTION_TIMEOUT);
		
		this.externalResources_hostnameVerifier = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_HOSTNAME_VERIFIER, false, true);	
		this.externalResources_trustAllCerts = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_TRUST_ALL_CERTS, false, false);	
		this.externalResources_trustStorePath = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_TRUST_STORE, false);	
		if(this.externalResources_trustStorePath!=null && StringUtils.isNotEmpty(this.externalResources_trustStorePath)) {
			this.externalResources_trustStorePassword = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_TRUST_STORE_PASSWORD, true);	
			this.externalResources_trustStoreType = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_TRUST_STORE_TYPE, false);
			if(this.externalResources_trustStoreType==null || StringUtils.isEmpty(this.externalResources_trustStoreType)) {
				this.externalResources_trustStoreType = KeystoreType.JKS.getNome();
			}
		}
		
		this.externalResources_keyStorePath = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_KEY_STORE, false);	
		if(this.externalResources_keyStorePath!=null && StringUtils.isNotEmpty(this.externalResources_keyStorePath)) {
			this.externalResources_keyStorePassword = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_KEY_STORE_PASSWORD, true);	
			this.externalResources_keyStoreType = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_KEY_STORE_TYPE, false);
			if(this.externalResources_keyStoreType==null || StringUtils.isEmpty(this.externalResources_keyStoreType)) {
				this.externalResources_keyStoreType = KeystoreType.JKS.getNome();
			}
			
			this.externalResources_keyPassword = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_KEY_PASSWORD, true);	
			this.externalResources_keyAlias = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_KEY_ALIAS, false);	
		}
		
		this.forwardProxy_url = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_URL, false);	
		if(this.forwardProxy_url!=null && StringUtils.isNotEmpty(this.forwardProxy_url)) {
			this.forwardProxy_header = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_HEADER, false);	
			this.forwardProxy_queryParameter = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_QUERY_PARAMETER, false);
			if(this.forwardProxy_header==null && this.forwardProxy_queryParameter==null) {
				throw new UtilsException("ForwardProxy property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_URL+"' require '"+
						OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_HEADER+"' o '"+
						OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_QUERY_PARAMETER+"'");
			}
			this.forwardProxy_base64 = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_BASE64, false, true);
		}

		String tmp = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_SECURE_RANDOM_ALGORITHM, false);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			try {
				this.secureRandomAlgorithm = SecureRandomAlgorithm.valueOf(tmp);
			}catch(Throwable t) {
				throw new UtilsException("SecureRandomAlgorithm property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_SECURE_RANDOM_ALGORITHM+"' invalid (found value:["+tmp+"]): "+t.getMessage(),t);
			}
		}
		if(	this.secureRandomAlgorithm == null) {
			this.secureRandomAlgorithm = SecureRandomAlgorithm.SHA1PRNG;
		}
		
		int defaultTolerance = 1000 * 60 * 10; // 10 minuti
		this.responseCheckDateToleranceMilliseconds = getIntProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_RESPONSE_DATE_TOLERANCE_MS, false, defaultTolerance);

		this.crl_signingCertCheck=getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_SIGNING_CERT_CHECK, false, false);
		
		this.crl_caCheck=getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_CA_CHECK, false, true);
		
		//this.crl=getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ENABLED, false, false); spostato sopra
		
		if(this.crl || this.crl_signingCertCheck || this.crl_caCheck) {
			
			List<CertificateSource> defaultValue = new ArrayList<>();
			defaultValue.add(CertificateSource.AUTHORITY_INFORMATION_ACCESS);
			
			this.crlSource = getCertificateSourceProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_SOURCE, false, defaultValue);
			if(this.crlSource == null || this.crlSource.isEmpty()) {
				throw new UtilsException("Property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_CRL_SOURCE+"' is empty");
			}
			if(this.crl_signingCertCheck && !this.crlSource.contains(CertificateSource.AUTHORITY_INFORMATION_ACCESS)) {
				throw new UtilsException("Property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_CRL_SIGNING_CERT_CHECK+"' require mode '"+
						CertificateSource.AUTHORITY_INFORMATION_ACCESS+"' defined in property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_CRL_SOURCE+"'");
			}
			if(this.crl_caCheck && !this.crlSource.contains(CertificateSource.AUTHORITY_INFORMATION_ACCESS)) {
				throw new UtilsException("Property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_CRL_CA_CHECK+"' require mode '"+
						CertificateSource.AUTHORITY_INFORMATION_ACCESS+"' defined in property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_CRL_SOURCE+"'");
			}
			
			this.crlAlternative = getListProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ALTERNATIVE, false, null);
			
			this.rejectsCertificateWithoutCRL = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_NOT_FOUND_REJECTS_CERTIFICATE, false, false);	
			this.rejectsCAWithoutCRL = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_NOT_FOUND_REJECTS_CA, false, false);	
						
			this.crlTrustStoreSource = getCertificateSourceProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_TRUSTSTORE_SOURCE, false, defaultValue);
			if(this.crlTrustStoreSource == null || this.crlTrustStoreSource.isEmpty()) {
				throw new UtilsException("Property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_CRL_TRUSTSTORE_SOURCE+"' is empty");
			}
			
			this.alternativeTrustStoreCRL_path = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ALTERNATIVE_TRUST_STORE, false);	
			if(this.alternativeTrustStoreCRL_path!=null && StringUtils.isNotEmpty(this.alternativeTrustStoreCRL_path)) {
				this.alternativeTrustStoreCRL_password = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ALTERNATIVE_TRUST_STORE_PASSWORD, true);	
				this.alternativeTrustStoreCRL_type = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ALTERNATIVE_TRUST_STORE_TYPE, false);
				if(this.alternativeTrustStoreCRL_type==null || StringUtils.isEmpty(this.alternativeTrustStoreCRL_type)) {
					this.alternativeTrustStoreCRL_type = KeystoreType.JKS.getNome();
				}
			}
		}

	}
	
	private static String getProperty(String id, Properties p, String name, boolean required) throws UtilsException {
		String tmp = p.getProperty(name);
		if(tmp!=null) {
			return tmp.trim();
		}
		else {
			if(required) {
				throw new UtilsException("Property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' notFound");
			}
			return null;
		}
	}
	private static boolean getBooleanProperty(String id, Properties p, String name, boolean required, boolean defaultValue) throws UtilsException {
		String tmp = getProperty(id, p, name, required);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			try {
				return Boolean.valueOf(tmp);
			}catch(Throwable t) {
				throw new UtilsException("Boolean property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' invalid (found value:["+tmp+"]): "+t.getMessage(),t);
			}
		}
		return defaultValue;
	}
	private static int getIntProperty(String id, Properties p, String name, boolean required, int defaultValue) throws UtilsException {
		String tmp = getProperty(id, p, name, required);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			try {
				return Integer.valueOf(tmp);
			}catch(Throwable t) {
				throw new UtilsException("Boolean property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' invalid (found value:["+tmp+"]): "+t.getMessage(),t);
			}
		}
		return defaultValue;
	}
	private static List<String> getListProperty(String id, Properties p, String name, boolean required, List<String> defaultValue) throws UtilsException {
		String tmp = getProperty(id, p, name, required);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			try {
				List<String> l = new ArrayList<>();
				String [] tmpArray = tmp.split(",");
				if(tmpArray==null || tmpArray.length<=0) {
					throw new Exception("Undefined value");
				}
				for (String s : tmpArray) {
					if(s!=null && StringUtils.isNotEmpty(s.trim())) {
						l.add(s.trim());
					}
				}
				return l;
			}catch(Throwable t) {
				throw new UtilsException("CertificateSource property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' invalid (found value:["+tmp+"]): "+t.getMessage(),t);
			}
		}
		return defaultValue;
	}
	private static List<Integer> getListIntProperty(String id, Properties p, String name, boolean required, List<Integer> defaultValue) throws UtilsException {
		List<String> l = getListProperty(id, p, name, required, null);
		if(l==null || l.isEmpty()) {
			return defaultValue;
		}
		List<Integer> lCS = new ArrayList<>();
		for (String certificateSource : l) {
			try {
				int c = Integer.valueOf(certificateSource);
				lCS.add(c);
			}catch(Throwable t) {
				throw new UtilsException("CertificateSource property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' invalid (found value:["+certificateSource+"]): "+t.getMessage(),t);
			}
		}
		return lCS;
	}
	private static List<CertificateSource> getCertificateSourceProperty(String id, Properties p, String name, boolean required, List<CertificateSource> defaultValue) throws UtilsException {
		List<String> l = getListProperty(id, p, name, required, null);
		if(l==null || l.isEmpty()) {
			return defaultValue;
		}
		List<CertificateSource> lCS = new ArrayList<>();
		for (String certificateSource : l) {
			try {
				CertificateSource c = CertificateSource.valueOf(certificateSource.toUpperCase());
				lCS.add(c);
			}catch(Throwable t) {
				throw new UtilsException("CertificateSource property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' invalid (found value:["+certificateSource+"]): "+t.getMessage(),t);
			}
		}
		if(lCS==null || lCS.isEmpty()) {
			return defaultValue;
		}
		return lCS;
	}
	private static List<OCSPResponseCode> getOCSPResponseCodeProperty(String id, Properties p, String name, boolean required, List<OCSPResponseCode> defaultValue) throws UtilsException {
		List<String> l = getListProperty(id, p, name, required, null);
		if(l==null || l.isEmpty()) {
			return defaultValue;
		}
		List<OCSPResponseCode> lCS = new ArrayList<>();
		for (String certificateSource : l) {
			try {
				OCSPResponseCode c = OCSPResponseCode.valueOf(certificateSource.toUpperCase());
				lCS.add(c);
			}catch(Throwable t) {
				throw new UtilsException("OCSPResponseCode property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' invalid (found value:["+certificateSource+"]): "+t.getMessage(),t);
			}
		}
		if(lCS==null || lCS.isEmpty()) {
			return defaultValue;
		}
		return lCS;
	}
	private static List<ExtendedKeyUsage> getExtendedKeyUsageProperty(String id, Properties p, String name, List<ExtendedKeyUsage> defaultValue) throws UtilsException {
		
		String tmp = getProperty(id, p, name, false);
		if(tmp!=null && "".equals(tmp)) {
			// non si vuole attuare alcun controllo
			return new ArrayList<>();
		}
		
		List<String> l = getListProperty(id, p, name, false, null);
		if(l==null || l.isEmpty()) {
			return defaultValue;
		}
		List<ExtendedKeyUsage> lCS = new ArrayList<>();
		for (String e : l) {
			try {
				ExtendedKeyUsage c = ExtendedKeyUsage.valueOf(e.toUpperCase());
				lCS.add(c);
			}catch(Throwable t) {
				throw new UtilsException("ExtendedKeyUsage property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' invalid (found value:["+e+"]): "+t.getMessage(),t);
			}
		}
		if(lCS==null || lCS.isEmpty()) {
			return defaultValue;
		}
		return lCS;
	}
	
	
}
