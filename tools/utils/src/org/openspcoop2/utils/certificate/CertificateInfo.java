/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

	public CertificatePrincipal getSubject() {
		if(this.certificate.getSubjectX500Principal()!=null) {
			return new CertificatePrincipal(this.certificate.getSubjectX500Principal(), PrincipalType.subject);
		}
		return null;
	}
	
	public CertificatePrincipal getIssuer() {
		if(this.certificate.getIssuerX500Principal()!=null) {
			return new CertificatePrincipal(this.certificate.getIssuerX500Principal(), PrincipalType.issuer);
		}
		return null;
	}
	
	public long getSerialNumber() {
		if(this.certificate.getSerialNumber()!=null) {
			return this.certificate.getSerialNumber().longValue();
		}
		else {
			return -1;
		}
	}
	
	public java.security.cert.X509Certificate getCertificate() {
		return this.certificate;
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
				MessageDigest digest  = MessageDigest.getInstance(algoritmo);
				digest.update(this.certificate.getEncoded());
				this.digest = digest.digest();
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
	
	public List<String> getExtendedKeyUsage() throws CertificateParsingException {
		return this.certificate.getExtendedKeyUsage();
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
		this.certificate.checkValidity(DateManager.getDate());
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
		PKIXParameters pkixParameters = new PKIXParameters(trustStore.getKeystore());
		pkixParameters.setDate(DateManager.getDate()); // per validare i certificati scaduti
		pkixParameters.addCertStore(crlCertstore);
		pkixParameters.setRevocationEnabled(true);
		CertPathValidator certPathValidator = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
		List<java.security.cert.Certificate> lCertificate = new ArrayList<java.security.cert.Certificate>();
		lCertificate.add(this.certificate);
		certPathValidator.validate(CertificateFactory.getInstance("X.509").generateCertPath(lCertificate), pkixParameters);
	}
	
	public boolean isVerified(KeyStore trustStore, boolean checkSameCertificateInTrustStore) {
		try {
			Enumeration<String> aliasesEnum = trustStore.aliases();
			while (aliasesEnum.hasMoreElements()) {
				String alias = (String) aliasesEnum.nextElement();
				java.security.cert.Certificate certificate = trustStore.getCertificate(alias);
				if(checkSameCertificateInTrustStore) {
					if(this.equals(certificate)) {
						return true;
					}
				}
				if(this.isVerified(certificate)) {
					return true;
				}
			}
		}catch(Exception e) {
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
		if(certificate instanceof java.security.cert.X509Certificate) {
			java.security.cert.X509Certificate x509 = (java.security.cert.X509Certificate) certificate;
			return this.equals(x509);
		}
		else if(certificate instanceof CertificateInfo) {
			CertificateInfo c = (CertificateInfo) certificate;
			return this.equals(c);
		}
		return false;
	}
	public boolean equals(java.security.cert.X509Certificate certificate) {
		return this.equals(certificate, true);
	}
	public boolean equals(CertificateInfo certificate) {
		return this.equals(certificate.getCertificate(), true);
	}
	
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
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		CertificateUtils.printCertificate(bf, this.certificate, this.name);
		return bf.toString();
	}
}
