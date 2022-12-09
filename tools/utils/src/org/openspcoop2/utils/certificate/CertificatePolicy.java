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
package org.openspcoop2.utils.certificate;

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;

/**
 * CertificatePolicy
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificatePolicy {

	private ASN1ObjectIdentifier asn1ObjectIdentifier;
	private List<CertificatePolicyEntry> qualifiers = new ArrayList<>();
	public ASN1ObjectIdentifier getAsn1ObjectIdentifier() {
		return this.asn1ObjectIdentifier;
	}
	public String getOID() {
		return this.asn1ObjectIdentifier!=null ? this.asn1ObjectIdentifier.getId() : null;
	}
	
	public List<CertificatePolicyEntry> getQualifiers() {
		return this.qualifiers;
	}
	public CertificatePolicyEntry getQualifier(int index) {
		return this.qualifiers!=null ? this.qualifiers.get(index) : null;
	}
	
	public CertificatePolicyEntry getQualifier(String oid) throws CertificateParsingException {
		return getQualifierByOID(oid);
	}
	public CertificatePolicyEntry getQualifierByOID(String oid) throws CertificateParsingException {
		if(oid==null) {
			throw new CertificateParsingException("Param oid undefined");
		}
		if(this.qualifiers!=null && !this.qualifiers.isEmpty()) {
			for (CertificatePolicyEntry certificatePolicyQualifier : this.qualifiers) {
				if(oid.equals(certificatePolicyQualifier.getOID())) {
					return certificatePolicyQualifier;
				}
			}
		}
		return null;
	}
	public boolean hasCertificatePolicyQualifier(String oid) throws CertificateParsingException {
		if(oid==null) {
			throw new CertificateParsingException("Param oid undefined");
		}
		return this.getQualifierByOID(oid)!=null;
	}
	
	
	public static boolean existsCertificatePolicy(X509Certificate x509, String oid) throws CertificateParsingException, CertificateEncodingException {
		if(oid==null) {
			throw new CertificateParsingException("Param oid undefined");
		}
		List<CertificatePolicy> list = CertificatePolicy.getCertificatePolicies(x509.getEncoded());
		if(list!=null && !list.isEmpty()) {
			for (CertificatePolicy certificatePolicy : list) {
				if(oid.equals(certificatePolicy.getOID())) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this.asn1ObjectIdentifier!=null) {
			sb.append("OID:");
			sb.append(this.asn1ObjectIdentifier.getId());
		}
		if(this.qualifiers!=null && !this.qualifiers.isEmpty()) {
			int index = 0;
			for (CertificatePolicyEntry o : this.qualifiers) {
				if(sb.length()>0) {
					sb.append("\n");
				}
				sb.append("Qualifier["+index+"]{\n");
				sb.append(o.toString("\t"));
				sb.append("}");
				index++;
			}
		}
		return sb.toString();
	}
	
	public static List<CertificatePolicy> getCertificatePolicies(byte[]encoded) throws CertificateParsingException{
		
		List<CertificatePolicy> l = new ArrayList<>();
		
		org.bouncycastle.asn1.x509.Certificate c =org.bouncycastle.asn1.x509.Certificate.getInstance(encoded);
		Extensions exts = c.getTBSCertificate().getExtensions();
		if (exts != null){
			org.bouncycastle.asn1.x509.CertificatePolicies certPolicies = org.bouncycastle.asn1.x509.CertificatePolicies.fromExtensions(exts);
			if(certPolicies!=null) {
				PolicyInformation [] pi = certPolicies.getPolicyInformation();
				if(pi!=null && pi.length>0) {
					for (PolicyInformation policyInformation : pi) {
						CertificatePolicy cp = new CertificatePolicy();
						cp.asn1ObjectIdentifier = policyInformation.getPolicyIdentifier();
						if(policyInformation.getPolicyQualifiers()!=null) {
							for (int i = 0; i < policyInformation.getPolicyQualifiers().size(); i++) {
								ASN1Encodable e = policyInformation.getPolicyQualifiers().getObjectAt(i);
								if(e instanceof org.bouncycastle.asn1.DLSequence) {
									org.bouncycastle.asn1.DLSequence dl = (org.bouncycastle.asn1.DLSequence) e;
									CertificatePolicyEntry cpe = new CertificatePolicyEntry(dl);
									cp.qualifiers.add(cpe);
								}								
							}
						}
						l.add(cp);
						 
//						System.out.println("======================");
//						System.out.println("PolicyInformation '"+policyInformation.toString()+"'");
//						System.out.println("Id '"+policyInformation.getPolicyIdentifier()+"'");
//						System.out.println("Qual '"+policyInformation.getPolicyQualifiers()+"'");
//						System.out.println("======================");
					}
				}
			}
		}
		return l;
		
	}
}
