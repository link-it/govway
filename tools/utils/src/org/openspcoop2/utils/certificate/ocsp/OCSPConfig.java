/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
	private String alternativeTrustStoreCAPath;
	private String alternativeTrustStoreCAPassword;
	private String alternativeTrustStoreCAType;
	private boolean rejectsCertificateWithoutCA = true;
	
	private String trustStoreSignerPath;
	private String trustStoreSignerPassword;
	private String trustStoreSignerType;
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
		
	private boolean externalResourcesHostnameVerifier = true;
	private boolean externalResourcesTrustAllCerts = false;
	private String externalResourcesTrustStorePath;
	private String externalResourcesTrustStorePassword;
	private String externalResourcesTrustStoreType;
	
	private String externalResourcesKeyStorePath;
	private String externalResourcesKeyStorePassword;
	private String externalResourcesKeyStoreType;
	private String externalResourcesKeyAlias;
	private String externalResourcesKeyPassword;
	
	private String externalResourcesUsername;
	private String externalResourcesPassword;
	
	private String forwardProxyUrl;
	private String forwardProxyHeader;
	private String forwardProxyQueryParameter;
	private boolean forwardProxyBase64;
	
	private SecureRandomAlgorithm secureRandomAlgorithm;
	
	private int responseCheckDateToleranceMilliseconds;

	private boolean crlSigningCertCheck=false;
	private boolean crlCaCheck=false;
	private boolean crl = false;
	
	private List<CertificateSource> crlSource = new ArrayList<>();
	private List<String> crlAlternative = null;
	private boolean rejectsCertificateWithoutCRL = false;
	private boolean rejectsCAWithoutCRL = false;
	
	private List<CertificateSource> crlTrustStoreSource = new ArrayList<>();
	private String alternativeTrustStoreCRLPath;
	private String alternativeTrustStoreCRLPassword;
	private String alternativeTrustStoreCRLType;
				

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
	public String getAlternativeTrustStoreCAPath() {
		return this.alternativeTrustStoreCAPath;
	}
	public String getAlternativeTrustStoreCAPassword() {
		return this.alternativeTrustStoreCAPassword;
	}
	public String getAlternativeTrustStoreCAType() {
		return this.alternativeTrustStoreCAType;
	}
	public boolean isRejectsCertificateWithoutCA() {
		return this.rejectsCertificateWithoutCA;
	}
	
	public String getTrustStoreSignerPath() {
		return this.trustStoreSignerPath;
	}
	public String getTrustStoreSignerPassword() {
		return this.trustStoreSignerPassword;
	}
	public String getTrustStoreSignerType() {
		return this.trustStoreSignerType;
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

	public boolean isExternalResourcesHostnameVerifier() {
		return this.externalResourcesHostnameVerifier;
	}
	public boolean isExternalResourcesTrustAllCerts() {
		return this.externalResourcesTrustAllCerts;
	}
	public String getExternalResourcesTrustStorePath() {
		return this.externalResourcesTrustStorePath;
	}
	public String getExternalResourcesTrustStorePassword() {
		return this.externalResourcesTrustStorePassword;
	}
	public String getExternalResourcesTrustStoreType() {
		return this.externalResourcesTrustStoreType;
	}
	
	public String getExternalResourcesKeyStorePath() {
		return this.externalResourcesKeyStorePath;
	}
	public String getExternalResourcesKeyStorePassword() {
		return this.externalResourcesKeyStorePassword;
	}
	public String getExternalResourcesKeyStoreType() {
		return this.externalResourcesKeyStoreType;
	}
	public String getExternalResourcesKeyAlias() {
		return this.externalResourcesKeyAlias;
	}
	public String getExternalResourcesKeyPassword() {
		return this.externalResourcesKeyPassword;
	}
	
	public String getExternalResourcesUsername() {
		return this.externalResourcesUsername;
	}
	public String getExternalResourcesPassword() {
		return this.externalResourcesPassword;
	}

	public String getForwardProxyUrl() {
		return this.forwardProxyUrl;
	}
	public String getForwardProxyHeader() {
		return this.forwardProxyHeader;
	}
	public String getForwardProxyQueryParameter() {
		return this.forwardProxyQueryParameter;
	}
	public boolean isForwardProxyBase64() {
		return this.forwardProxyBase64;
	}
	
	public SecureRandomAlgorithm getSecureRandomAlgorithm() {
		return this.secureRandomAlgorithm;
	}
	
	public int getResponseCheckDateToleranceMilliseconds() {
		return this.responseCheckDateToleranceMilliseconds;
	}
	
	public boolean isCrlSigningCertCheck() {
		return this.crlSigningCertCheck;
	}
	
	public boolean isCrlCaCheck() {
		return this.crlCaCheck;
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

	public String getAlternativeTrustStoreCRLPath() {
		return this.alternativeTrustStoreCRLPath;
	}

	public String getAlternativeTrustStoreCRLPassword() {
		return this.alternativeTrustStoreCRLPassword;
	}

	public String getAlternativeTrustStoreCRLType() {
		return this.alternativeTrustStoreCRLType;
	}

	
	protected OCSPConfig(String id, Properties p, Logger log) throws UtilsException {
		this.id = id;
		
		if(log!=null) {
			// nop
		}
		
		if(p==null || p.isEmpty()) {
			throw new UtilsException("Properties '"+OCSPCostanti.PROPERTY_PREFIX+id+".*' undefined");
		}
		
		this.type = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_TYPE, true);	
		this.label = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_LABEL, true);	
		
		this.certificateChainVerify = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CERTIFICATE_CHAIN_VERIFY, false, true);	
		
		this.checkValidity = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CHECK_VALIDITY, false, true);	
		this.checkCAValidity = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CHECK_CA_VALIDITY, false, true);	
		
		this.caSource = getCertificateSourceProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CA_SOURCE, true, null);
		
		this.alternativeTrustStoreCAPath = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CA_ALTERNATIVE_TRUST_STORE, false);	
		if(this.alternativeTrustStoreCAPath!=null && StringUtils.isNotEmpty(this.alternativeTrustStoreCAPath)) {
			this.alternativeTrustStoreCAPassword = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CA_ALTERNATIVE_TRUST_STORE_PASSWORD, true);	
			this.alternativeTrustStoreCAType = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CA_ALTERNATIVE_TRUST_STORE_TYPE, false);
			if(this.alternativeTrustStoreCAType==null || StringUtils.isEmpty(this.alternativeTrustStoreCAType)) {
				this.alternativeTrustStoreCAType = KeystoreType.JKS.getNome();
			}
		}
		
		this.rejectsCertificateWithoutCA = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CA_NOT_FOUD_REJECTS_CERTIFICATE, false, true);	
		
		this.trustStoreSignerPath = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_SIGNER_TRUST_STORE, false);	
		if(this.trustStoreSignerPath!=null && StringUtils.isNotEmpty(this.trustStoreSignerPath)) {
			this.trustStoreSignerPassword = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_SIGNER_TRUST_STORE_PASSWORD, true);	
			this.trustStoreSignerType = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_SIGNER_TRUST_STORE_TYPE, false);
			if(this.trustStoreSignerType==null || StringUtils.isEmpty(this.trustStoreSignerType)) {
				this.trustStoreSignerType = KeystoreType.JKS.getNome();
			}
		}
		
		String prefix = "Property '"+OCSPCostanti.PROPERTY_PREFIX+id+".";
		
		this.aliasCertificateSigner = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_SIGNER_ALIAS, false);	
		if(
				(this.aliasCertificateSigner!=null && StringUtils.isNotEmpty(this.aliasCertificateSigner))
				&&
				(this.trustStoreSignerPath==null || StringUtils.isEmpty(this.trustStoreSignerPath))
			){
			throw new UtilsException(prefix+OCSPCostanti.PROPERTY_SUFFIX_SIGNER_ALIAS+"' require property '"+
					OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_SIGNER_TRUST_STORE+"'");
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
			throw new UtilsException(prefix+OCSPCostanti.PROPERTY_SUFFIX_URL_SOURCE+"' declare unsupported '"+CertificateSource.CONFIG+"' mode");
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
		
		this.readTimeout = getIntProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_READ_TIMEOUT, false, 15000); /** HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT); */
		this.connectTimeout = getIntProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CONNECT_TIMEOUT, false, HttpUtilities.HTTP_CONNECTION_TIMEOUT);
		
		this.externalResourcesHostnameVerifier = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_HOSTNAME_VERIFIER, false, true);	
		this.externalResourcesTrustAllCerts = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_TRUST_ALL_CERTS, false, false);	
		this.externalResourcesTrustStorePath = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_TRUST_STORE, false);	
		if(this.externalResourcesTrustStorePath!=null && StringUtils.isNotEmpty(this.externalResourcesTrustStorePath)) {
			this.externalResourcesTrustStorePassword = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_TRUST_STORE_PASSWORD, true);	
			this.externalResourcesTrustStoreType = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_TRUST_STORE_TYPE, false);
			if(this.externalResourcesTrustStoreType==null || StringUtils.isEmpty(this.externalResourcesTrustStoreType)) {
				this.externalResourcesTrustStoreType = KeystoreType.JKS.getNome();
			}
		}
		
		this.externalResourcesKeyStorePath = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_KEY_STORE, false);	
		if(this.externalResourcesKeyStorePath!=null && StringUtils.isNotEmpty(this.externalResourcesKeyStorePath)) {
			this.externalResourcesKeyStorePassword = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_KEY_STORE_PASSWORD, true);	
			this.externalResourcesKeyStoreType = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_KEY_STORE_TYPE, false);
			if(this.externalResourcesKeyStoreType==null || StringUtils.isEmpty(this.externalResourcesKeyStoreType)) {
				this.externalResourcesKeyStoreType = KeystoreType.JKS.getNome();
			}
			
			this.externalResourcesKeyPassword = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_KEY_PASSWORD, true);	
			this.externalResourcesKeyAlias = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_HTTPS_KEY_ALIAS, false);	
		}
		
		this.externalResourcesUsername = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_USERNAME, false);	
		if(this.externalResourcesUsername!=null && StringUtils.isNotEmpty(this.externalResourcesUsername)) {
			this.externalResourcesPassword = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_PASSWORD, false);	
		}
		
		this.forwardProxyUrl = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_URL, false);	
		if(this.forwardProxyUrl!=null && StringUtils.isNotEmpty(this.forwardProxyUrl)) {
			this.forwardProxyHeader = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_HEADER, false);	
			this.forwardProxyQueryParameter = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_QUERY_PARAMETER, false);
			if(this.forwardProxyHeader==null && this.forwardProxyQueryParameter==null) {
				throw new UtilsException("ForwardProxy property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_URL+"' require '"+
						OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_HEADER+"' o '"+
						OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_QUERY_PARAMETER+"'");
			}
			this.forwardProxyBase64 = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_FORWARD_PROXY_BASE64, false, true);
		}

		String tmp = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_SECURE_RANDOM_ALGORITHM, false);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			try {
				this.secureRandomAlgorithm = SecureRandomAlgorithm.valueOf(tmp);
			}catch(Exception t) {
				throw new UtilsException("SecureRandomAlgorithm property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_SECURE_RANDOM_ALGORITHM+"' invalid (found value:["+tmp+"]): "+t.getMessage(),t);
			}
		}
		if(	this.secureRandomAlgorithm == null) {
			this.secureRandomAlgorithm = SecureRandomAlgorithm.SHA1PRNG;
		}
		
		int defaultTolerance = 1000 * 60 * 10; // 10 minuti
		this.responseCheckDateToleranceMilliseconds = getIntProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_RESPONSE_DATE_TOLERANCE_MS, false, defaultTolerance);

		this.crlSigningCertCheck=getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_SIGNING_CERT_CHECK, false, false);
		
		this.crlCaCheck=getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_CA_CHECK, false, true);
		
		//this.crl=getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ENABLED, false, false); spostato sopra
		
		if(this.crl || this.crlSigningCertCheck || this.crlCaCheck) {
			
			List<CertificateSource> defaultValue = new ArrayList<>();
			defaultValue.add(CertificateSource.AUTHORITY_INFORMATION_ACCESS);
			
			this.crlSource = getCertificateSourceProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_SOURCE, false, defaultValue);
			if(this.crlSource == null || this.crlSource.isEmpty()) {
				throw new UtilsException(prefix+OCSPCostanti.PROPERTY_SUFFIX_CRL_SOURCE+"' is empty");
			}
			if(this.crlSigningCertCheck && !this.crlSource.contains(CertificateSource.AUTHORITY_INFORMATION_ACCESS)) {
				throw new UtilsException(prefix+OCSPCostanti.PROPERTY_SUFFIX_CRL_SIGNING_CERT_CHECK+"' require mode '"+
						CertificateSource.AUTHORITY_INFORMATION_ACCESS+"' defined in property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_CRL_SOURCE+"'");
			}
			if(this.crlCaCheck && !this.crlSource.contains(CertificateSource.AUTHORITY_INFORMATION_ACCESS)) {
				throw new UtilsException(prefix+OCSPCostanti.PROPERTY_SUFFIX_CRL_CA_CHECK+"' require mode '"+
						CertificateSource.AUTHORITY_INFORMATION_ACCESS+"' defined in property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+OCSPCostanti.PROPERTY_SUFFIX_CRL_SOURCE+"'");
			}
			
			this.crlAlternative = getListProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ALTERNATIVE, false, null);
			
			this.rejectsCertificateWithoutCRL = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_NOT_FOUND_REJECTS_CERTIFICATE, false, false);	
			this.rejectsCAWithoutCRL = getBooleanProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_NOT_FOUND_REJECTS_CA, false, false);	
						
			this.crlTrustStoreSource = getCertificateSourceProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_TRUSTSTORE_SOURCE, false, defaultValue);
			if(this.crlTrustStoreSource == null || this.crlTrustStoreSource.isEmpty()) {
				throw new UtilsException(prefix+OCSPCostanti.PROPERTY_SUFFIX_CRL_TRUSTSTORE_SOURCE+"' is empty");
			}
			
			this.alternativeTrustStoreCRLPath = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ALTERNATIVE_TRUST_STORE, false);	
			if(this.alternativeTrustStoreCRLPath!=null && StringUtils.isNotEmpty(this.alternativeTrustStoreCRLPath)) {
				this.alternativeTrustStoreCRLPassword = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ALTERNATIVE_TRUST_STORE_PASSWORD, true);	
				this.alternativeTrustStoreCRLType = getProperty(id, p, OCSPCostanti.PROPERTY_SUFFIX_CRL_ALTERNATIVE_TRUST_STORE_TYPE, false);
				if(this.alternativeTrustStoreCRLType==null || StringUtils.isEmpty(this.alternativeTrustStoreCRLType)) {
					this.alternativeTrustStoreCRLType = KeystoreType.JKS.getNome();
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
			}catch(Exception t) {
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
			}catch(Exception t) {
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
					throw new UtilsException("Undefined value");
				}
				for (String s : tmpArray) {
					if(s!=null && StringUtils.isNotEmpty(s.trim())) {
						l.add(s.trim());
					}
				}
				return l;
			}catch(Exception t) {
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
				int c = Integer.parseInt(certificateSource);
				lCS.add(c);
			}catch(Exception t) {
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
			}catch(Exception t) {
				throw new UtilsException("CertificateSource property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' invalid (found value:["+certificateSource+"]): "+t.getMessage(),t);
			}
		}
		if(lCS.isEmpty()) {
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
			}catch(Exception t) {
				throw new UtilsException("OCSPResponseCode property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' invalid (found value:["+certificateSource+"]): "+t.getMessage(),t);
			}
		}
		if(lCS.isEmpty()) {
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
			}catch(Exception t) {
				throw new UtilsException("ExtendedKeyUsage property '"+OCSPCostanti.PROPERTY_PREFIX+id+"."+name+"' invalid (found value:["+e+"]): "+t.getMessage(),t);
			}
		}
		if(lCS.isEmpty()) {
			return defaultValue;
		}
		return lCS;
	}
	
	
}
