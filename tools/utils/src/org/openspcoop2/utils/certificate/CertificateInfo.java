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

package org.openspcoop2.utils.certificate;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.PKIXParameters;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.Base64Utilities;

/**
 * CertificateInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificateInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private java.security.cert.X509Certificate certificate;

	private String name;
	
	private byte[] digest;
	private String digestBase64Encoded;
	
	public CertificateInfo(java.security.cert.X509Certificate certificate, String name) {
		this.certificate = certificate;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public CertificatePrincipal getSubject() {
		if(this.certificate.getSubjectX500Principal()!=null) {
			return new CertificatePrincipal(this.certificate.getSubjectX500Principal(), PrincipalType.SUBJECT);
		}
		return null;
	}
	
	public CertificatePrincipal getIssuer() {
		if(this.certificate.getIssuerX500Principal()!=null) {
			return new CertificatePrincipal(this.certificate.getIssuerX500Principal(), PrincipalType.ISSUER);
		}
		return null;
	}
	
	public String getSerialNumber() {
		if(this.certificate.getSerialNumber()!=null) {
			return this.certificate.getSerialNumber().toString();
		}
		else {
			return null;
		}
	}
	public String getSerialNumberHex() {
		if(this.certificate.getSerialNumber()!=null) {
			return this.certificate.getSerialNumber().toString(16);
		}
		else {
			return null;
		}
	}
	public String getSerialNumberHex(String delimiter) {
		if(this.certificate.getSerialNumber()!=null) {
			return formatSerialNumberHex(this.certificate.getSerialNumber(), delimiter);
		}
		else {
			return null;
		}
	}
	public static String formatSerialNumberHex(String serialNumber) {
		return (new BigInteger(serialNumber)).toString(16);
	}
	public static String formatSerialNumberHex(String serialNumber, String delimiter) {
		return formatSerialNumberHex(new BigInteger(serialNumber), delimiter);
	}
	public static String formatSerialNumberHex(BigInteger bi, String delimiter) {
		byte[] bytes = bi.toByteArray();
	    StringBuilder sb = new StringBuilder();
	    for (byte b : bytes) {
	    	String f = "%02X"+delimiter;
	        sb.append(String.format(f, b));
	    }
	    String s = sb.toString();
	    if(s.endsWith(delimiter) && s.length()>delimiter.length()) {
	    	return s.substring(0, (s.length()-delimiter.length()));
	    }
	    else {
	    	return s;
	    }
	}
	
	public java.security.cert.X509Certificate getCertificate() {
		return this.certificate;
	}
	
	public String getPEMEncoded() throws UtilsException {
		return CertificateUtils.toPEM(this.certificate);
	}
	public byte[] getEncoded() throws UtilsException {
		try {
			return this.certificate.getEncoded();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
	}
	
	public byte[] digest() throws CertificateException {
		return this.digest("MD5");
	}
	public byte[] digest(String algoritmo) throws CertificateException {
		if(this.digest==null) {
			this.initDigest(algoritmo);
		}
		return this.digest;
	}
	private synchronized void initDigest(String algoritmo) throws CertificateException {
		try {
			if(this.digest==null) {
				MessageDigest digestEngine = org.openspcoop2.utils.digest.MessageDigestFactory.getMessageDigest(algoritmo);
				digestEngine.update(this.certificate.getEncoded());
				this.digest = digestEngine.digest();
			}
		}catch(Exception e) {
			throw new CertificateException(e.getMessage(),e);
		}
	}
	
	public String digestBase64Encoded() throws CertificateException {
		return this.digestBase64Encoded("MD5");
	}
	public String digestBase64Encoded(String algoritmo) throws CertificateException {
		if(this.digestBase64Encoded==null) {
			this.initDigestBase64Encoded(algoritmo);
		}
		return this.digestBase64Encoded;
	}
	private synchronized void initDigestBase64Encoded(String algoritmo) throws CertificateException {
		try {
			if(this.digestBase64Encoded==null) {
				this.digestBase64Encoded = Base64Utilities.encodeAsString(this.digest(algoritmo));
			}
		}catch(Exception e) {
			throw new CertificateException(e.getMessage(),e);
		}
	}
	
	public Date getNotAfter() {
		return this.certificate.getNotAfter();
	}
	
	public Date getNotBefore() {
		return this.certificate.getNotBefore();
	}
	
	public String getSigAlgName() {
		return this.certificate.getSigAlgName();
	}
	
	public String getType() {
		return this.certificate.getType();
	}
	
	public int getVersion() {
		return this.certificate.getVersion();
	}
	
	public List<KeyUsage> getKeyUsage(){
		return KeyUsage.getKeyUsage(this.certificate);
	}
	public boolean[] getKeyUsageAsMap() {
		return this.certificate.getKeyUsage();
	}
	public boolean hasKeyUsage(KeyUsage keyUsage) {
		return keyUsage.hasKeyUsage(this.certificate);
	}
	public boolean hasKeyUsage(String keyUsage) {
		return hasKeyUsage(KeyUsage.valueOf(keyUsage.toUpperCase()));
	}
	public boolean hasKeyUsageByArrayBooleanPosition(int arrayBooleanPosition) {
		return KeyUsage.existsKeyUsageByArrayBooleanPosition(this.certificate, arrayBooleanPosition);
	}
	public boolean hasKeyUsageByBouncycastleCode(int bouncycastelValue) throws CertificateEncodingException {
		return KeyUsage.existsKeyUsageByBouncycastleCode(this.certificate.getEncoded(), bouncycastelValue);
	}
	
	public List<ExtendedKeyUsage> getExtendedKeyUsage() throws CertificateParsingException{
		return ExtendedKeyUsage.getKeyUsage(this.certificate);
	}
	public List<String> getExtendedKeyUsageByOID() throws CertificateParsingException {
		return this.certificate.getExtendedKeyUsage();
	}
	public boolean hasExtendedKeyUsage(ExtendedKeyUsage keyUsage) throws CertificateParsingException {
		return keyUsage.hasKeyUsage(this.certificate);
	}
	public boolean hasExtendedKeyUsage(String keyUsage) throws CertificateParsingException {
		return hasExtendedKeyUsage(ExtendedKeyUsage.valueOf(keyUsage.toUpperCase()));
	}
	public boolean hasExtendedKeyUsageByOID(String oid) throws CertificateParsingException {
		return ExtendedKeyUsage.existsKeyUsageByOID(this.certificate,oid);
	}
	public boolean hasExtendedKeyUsageByBouncycastleKeyPurposeId(String oid) throws CertificateEncodingException {
		return ExtendedKeyUsage.existsKeyUsageByBouncycastleKeyPurposeId(this.certificate.getEncoded(),oid);
	}
	
	public List<CertificatePolicy> getCertificatePolicies() throws CertificateEncodingException {
		return CertificatePolicy.getCertificatePolicies(this.certificate.getEncoded());
	}
	public CertificatePolicy getCertificatePolicy(String oid) throws CertificateParsingException, CertificateEncodingException {
		return getCertificatePolicyByOID(oid);
	}
	public CertificatePolicy getCertificatePolicyByOID(String oid) throws CertificateParsingException, CertificateEncodingException {
		if(oid==null) {
			throw new CertificateParsingException("Param oid undefined");
		}
		List<CertificatePolicy> l = getCertificatePolicies();
		if(l!=null && !l.isEmpty()) {
			for (CertificatePolicy certificatePolicy : l) {
				if(oid.equals(certificatePolicy.getOID())) {
					return certificatePolicy;
				}
			}
		}
		return null;
	}
	public boolean hasCertificatePolicy(String oid) throws CertificateParsingException, CertificateEncodingException {
		if(oid==null) {
			throw new CertificateParsingException("Param oid undefined");
		}
		return this.getCertificatePolicyByOID(oid)!=null;
	}
	
	public BasicConstraints getBasicConstraints() throws CertificateParsingException, CertificateEncodingException {
		return BasicConstraints.getBasicConstraints(this.certificate.getEncoded());
	}
	public boolean isCA() throws CertificateParsingException, CertificateEncodingException {
		BasicConstraints bc = this.getBasicConstraints();
		return bc !=null && bc.isCA();
	}
	public long getPathLen() throws CertificateParsingException, CertificateEncodingException {
		BasicConstraints bc = this.getBasicConstraints();
		return bc !=null ? bc.getPathLen() : -1;
	}
	
	public AuthorityKeyIdentifier getAuthorityKeyIdentifier() throws CertificateParsingException, CertificateEncodingException {
		return AuthorityKeyIdentifier.getAuthorityKeyIdentifier(this.certificate.getEncoded());
	}
	
	public AuthorityInformationAccess getAuthorityInformationAccess() throws CertificateParsingException, CertificateEncodingException {
		return AuthorityInformationAccess.getAuthorityInformationAccess(this.certificate.getEncoded());
	}
	
	public CRLDistributionPoints getCRLDistributionPoints() throws CertificateEncodingException {
		return CRLDistributionPoints.getCRLDistributionPoints(this.certificate.getEncoded());
	}
	
	public SubjectAlternativeNames getSubjectAlternativeNames() throws CertificateEncodingException {
		return SubjectAlternativeNames.getSubjectAlternativeNames(this.certificate.getEncoded());
	}
	public List<String> getAlternativeNames() throws CertificateEncodingException {
		SubjectAlternativeNames subjectAlternativeNames = this.getSubjectAlternativeNames();
		return subjectAlternativeNames !=null ? subjectAlternativeNames.getAlternativeNames() : null;
	}
	
	public Extensions getExtensions() throws CertificateEncodingException {
		return Extensions.getExtensions(this.certificate.getEncoded());
	}
	
    /**
     * Utility method to test if a certificate is self-issued. This is
     * the case iff the subject and issuer X500Principals are equal.
     */
    public boolean isSelfIssued() {
    	X500Principal subject = this.certificate.getSubjectX500Principal();
        X500Principal issuer = this.certificate.getIssuerX500Principal();
        return subject.equals(issuer);
    }

    /**
     * Utility method to test if a certificate is self-signed. This is
     * the case iff the subject and issuer X500Principals are equal
     * AND the certificate's subject public key can be used to verify
     * the certificate. In case of exception, returns false.
     */
    public boolean isSelfSigned() {
    	return this.isSelfSigned(null);
    }
    public boolean isSelfSigned(String sigProvider) {
        if (isSelfIssued()) {
            try {
                if (sigProvider == null) {
                	this.certificate.verify(this.certificate.getPublicKey());
                } else {
                	this.certificate.verify(this.certificate.getPublicKey(), sigProvider);
                }
                return true;
            } catch (Exception e) {
                // In case of exception, return false
            }
        }
        return false;
    }
	
	public boolean isValid() {
		try {
			this.certificate.checkValidity(DateManager.getDate());
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	public void checkValid() throws CertificateExpiredException, CertificateNotYetValidException {
		checkValid(DateManager.getDate());
	}
	public void checkValid(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
		this.certificate.checkValidity(date!=null ? date : DateManager.getDate());
	}
	
	public boolean isValid(CertStore crlCertstore, KeyStore trustStore) {
		try {
			this.checkValid(crlCertstore, trustStore);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	public void checkValid(CertStore crlCertstore, KeyStore trustStore) throws CertPathValidatorException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, SecurityException, NoSuchAlgorithmException {
		checkValid(crlCertstore, trustStore, DateManager.getDate());
	}
	public void checkValid(CertStore crlCertstore, KeyStore trustStore, Date date) throws CertPathValidatorException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, SecurityException, NoSuchAlgorithmException {
		PKIXParameters pkixParameters = new PKIXParameters(trustStore.getKeystore());
		pkixParameters.setDate(date!=null ? date : DateManager.getDate()); // per validare i certificati scaduti
		pkixParameters.addCertStore(crlCertstore);
		pkixParameters.setRevocationEnabled(true);
		CertPathValidator certPathValidator = org.openspcoop2.utils.certificate.CertificateFactory.getCertPathValidator();
		List<java.security.cert.Certificate> lCertificate = new ArrayList<>();
		lCertificate.add(this.certificate);
		CertificateFactory certificateFactory = org.openspcoop2.utils.certificate.CertificateFactory.getCertificateFactory();
		certPathValidator.validate(certificateFactory.generateCertPath(lCertificate), pkixParameters);
	}
	
	public boolean isVerified(KeyStore trustStore, boolean checkSameCertificateInTrustStore) {
		try {
			Enumeration<String> aliasesEnum = trustStore.aliases();
			while (aliasesEnum.hasMoreElements()) {
				String alias = aliasesEnum.nextElement();
				java.security.cert.Certificate certificateReaded = trustStore.getCertificate(alias);
				if(checkSameCertificateInTrustStore && this.equalsEngine(certificateReaded)) {
					return true;
				}
				if(this.isVerified(certificateReaded)) {
					return true;
				}
			}
		}catch(Exception e) {
			// ignore
		}
		return false;
	}
	public boolean isVerified(CertificateInfo caCert) {
		return this.isVerified(caCert.getCertificate());
	}
	public boolean isVerified(java.security.cert.Certificate caCert) {
		try {
			this.certificate.verify(caCert.getPublicKey());
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	public void verify(CertificateInfo caCert) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		verify(caCert.getCertificate());
	}
	public void verify(java.security.cert.Certificate caCert) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		this.certificate.verify(caCert.getPublicKey());
	}
	
	@Override
	public boolean equals(Object certificate) {
		return this.equalsEngine(certificate);
	}
	private boolean equalsEngine(Object certificate) {
		if(certificate instanceof java.security.cert.X509Certificate) {
			java.security.cert.X509Certificate x509 = (java.security.cert.X509Certificate) certificate;
			return this.equals(x509, true);
		}
		else if(certificate instanceof java.security.cert.Certificate) {
			if(this.getCertificate()!=null) {
				java.security.cert.Certificate cer = (java.security.cert.Certificate) certificate;
				return this.getCertificate().equals(cer);
			}
		}
		else if(certificate instanceof CertificateInfo) {
			CertificateInfo c = (CertificateInfo) certificate;
			return this.equals(c.getCertificate(), true);
		}
		return false;
	}
	/** Già gestite nell'equals(Object)
	public boolean equals(java.security.cert.X509Certificate certificate) {
		return this.equals(certificate, true);
	}
	public boolean equals(CertificateInfo certificate) {
		return this.equals(certificate.getCertificate(), true);
	}*/
	
	public boolean equals(java.security.cert.X509Certificate certificate, boolean strictVerifier) {
		CertificateInfo certificateCheck = new CertificateInfo(certificate, "check");
		return this.equals(certificateCheck, strictVerifier);
	}
	public boolean equals(CertificateInfo certificate, boolean strictVerifier) {
		if(strictVerifier) {
			return this.getCertificate().equals(certificate.getCertificate());
		}
		else {
			try {
				boolean checkIssuer = this.getIssuer().getNameNormalized().equals(certificate.getIssuer().getNameNormalized());
				boolean checkSubject = this.getSubject().getNameNormalized().equals(certificate.getSubject().getNameNormalized());
				return checkIssuer && checkSubject;
			}catch(Exception e) {
				LoggerWrapperFactory.getLogger(CertificateInfo.class.getName()).error(e.getMessage(),e);
				return false;
			}
		}
	}
	
    public boolean equals(X500Principal principal) {
    	if(principal==null) {
    		return false;
    	}
    	X500Principal subject = this.certificate.getSubjectX500Principal();
    	return principal.equals(subject);
    }
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		CertificateUtils.printCertificate(bf, this.certificate, this.name);
		return bf.toString();
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
