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
package org.openspcoop2.utils.certificate;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.Extensions;

/**
 * ExtendedKeyUsage
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ExtendedKeyUsage {

	ANY_EXTENDED_KEY_USAGE(org.bouncycastle.asn1.x509.KeyPurposeId.anyExtendedKeyUsage), // [2.5.29.37.0]
	
	SERVER_AUTH(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_serverAuth), // [1.3.6.1.5.5.7.3.1]
	
	CLIENT_AUTH(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_clientAuth), // [1.3.6.1.5.5.7.3.2]
	
	CODE_SIGNING(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_codeSigning), // [1.3.6.1.5.5.7.3.3]

	EMAIL_PROTECTION(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_emailProtection), // [1.3.6.1.5.5.7.3.4]
	
	IPSEC_END_SYSTEM(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_ipsecEndSystem), // [1.3.6.1.5.5.7.3.5]

	IPSEC_TUNNEL(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_ipsecTunnel), // [1.3.6.1.5.5.7.3.6]
	
	IPSEC_USER(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_ipsecUser), // [1.3.6.1.5.5.7.3.7]

	TIME_STAMPING(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_timeStamping), // [1.3.6.1.5.5.7.3.8]
	
	OCSP_SIGNING(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_OCSPSigning), // [1.3.6.1.5.5.7.3.9]
	
	DVCS(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_dvcs), // [1.3.6.1.5.5.7.3.10]
	
	SBGP_CERT_AA_SERVER_AUTH(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_sbgpCertAAServerAuth), // [1.3.6.1.5.5.7.3.11]
	
	SCVP_RESPONDER(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_scvp_responder), // [1.3.6.1.5.5.7.3.12]

	EAP_OVER_PPP(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_eapOverPPP), // [1.3.6.1.5.5.7.3.13]
	
	EAP_OVER_LAN(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_eapOverLAN), // [1.3.6.1.5.5.7.3.14]
	
	SCVP_SERVER(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_scvpServer), // [1.3.6.1.5.5.7.3.15]
	
	SCVP_CLIENT(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_scvpClient), // [1.3.6.1.5.5.7.3.16]
	
	IPSEC_IKE(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_ipsecIKE), // [1.3.6.1.5.5.7.3.17]
	
	CAPWAP_AC(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_capwapAC), // [1.3.6.1.5.5.7.3.18]
	
	CAPWAP_WTP(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_capwapWTP), // [1.3.6.1.5.5.7.3.19]
	
	SMART_CARD_LOGON(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_smartcardlogon), // [1.3.6.1.4.1.311.20.2.2]

	MAC_ADDRESS(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_macAddress), // [1.3.6.1.1.1.1.22]

	MS_SGC(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_msSGC), // [1.3.6.1.4.1.311.10.3.3]
	
	NS_SGC(org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_nsSGC); // [2.16.840.1.113730.4.1]

	
	ExtendedKeyUsage(org.bouncycastle.asn1.x509.KeyPurposeId purposeId ) {
		this.oid = purposeId.getId();
		this.purposeId = purposeId;
	}
	ExtendedKeyUsage(String oid ) {
		this.oid = oid;
	}
	
	private String oid;
	private org.bouncycastle.asn1.x509.KeyPurposeId purposeId;
	public String getId() {
		return this.oid;
	}
	public org.bouncycastle.asn1.x509.KeyPurposeId getPurposeId() {
		if(this.purposeId!=null) {
			return this.purposeId;
		}
		else {
			return org.bouncycastle.asn1.x509.KeyPurposeId.getInstance(new org.bouncycastle.asn1.ASN1ObjectIdentifier(this.oid));
		}
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	public String toString(boolean printOID) {
		if(printOID) {
			return this.name()+" ("+this.oid+")";
		}
		else {
			return this.name();
		}
	}
	
	public boolean hasKeyUsage(Certificate x509) throws CertificateParsingException {
		if(x509.getCertificate()!=null) {
			return hasKeyUsage(x509.getCertificate());
		}
		return false;
	}
	public boolean hasKeyUsage(CertificateInfo x509) throws CertificateParsingException {
		return hasKeyUsage(x509.getCertificate());
	}
	public boolean hasKeyUsage(X509Certificate x509) throws CertificateParsingException {
		return existsKeyUsageByOID(x509, this.oid);
	}
	public static boolean existsKeyUsageByOID(X509Certificate x509, String oid) throws CertificateParsingException {
		if(x509.getExtendedKeyUsage()!=null) {
			return x509.getExtendedKeyUsage().contains(oid);
		}
		return false;
	}
	
	public boolean hasKeyUsage(byte[]encoded) {
		return existsKeyUsageByBouncycastleKeyPurposeId(encoded, this.purposeId);
	}
	public static boolean existsKeyUsageByBouncycastleKeyPurposeId(byte[]encoded, String keyPurposeId)  {
		return existsKeyUsageByBouncycastleKeyPurposeId(encoded, org.bouncycastle.asn1.x509.KeyPurposeId.getInstance(new org.bouncycastle.asn1.ASN1ObjectIdentifier(keyPurposeId)));
	}
	public static boolean existsKeyUsageByBouncycastleKeyPurposeId(byte[]encoded, org.bouncycastle.asn1.x509.KeyPurposeId keyPurposeId)  {
		org.bouncycastle.asn1.x509.Certificate c =org.bouncycastle.asn1.x509.Certificate.getInstance(encoded);
		Extensions exts = c.getTBSCertificate().getExtensions();
		if (exts != null){
			org.bouncycastle.asn1.x509.ExtendedKeyUsage eKey = org.bouncycastle.asn1.x509.ExtendedKeyUsage.fromExtensions(exts);
			if(eKey!=null) {
				return eKey.hasKeyPurposeId(keyPurposeId);
			}
		}
		return false;
	}
	
	public static ExtendedKeyUsage toExtendedKeyUsage(String id) {
		ExtendedKeyUsage [] v = ExtendedKeyUsage.values();
		for (ExtendedKeyUsage usage : v) {
			if(usage.oid.equals(id)) {
				return usage;
			}
		}
		return null;
	}
	public static ExtendedKeyUsage toExtendedKeyUsage(org.bouncycastle.asn1.x509.KeyPurposeId purposeId) {
		ExtendedKeyUsage [] v = ExtendedKeyUsage.values();
		for (ExtendedKeyUsage usage : v) {
			if(usage.purposeId.equals(purposeId)) {
				return usage;
			}
		}
		return null;
	}
	
	public static List<ExtendedKeyUsage> getKeyUsage(Certificate x509) throws CertificateParsingException{
		if(x509.getCertificate()!=null) {
			return getKeyUsage(x509.getCertificate());
		}
		return new ArrayList<ExtendedKeyUsage>();
	}
	public static List<ExtendedKeyUsage> getKeyUsage(CertificateInfo x509) throws CertificateParsingException{
		return getKeyUsage(x509.getCertificate());
	}
	public static List<ExtendedKeyUsage> getKeyUsage(X509Certificate x509) throws CertificateParsingException{
		List<ExtendedKeyUsage> l = new ArrayList<ExtendedKeyUsage>();
		ExtendedKeyUsage [] values = ExtendedKeyUsage.values();
		for (ExtendedKeyUsage keyUsage : values) {
			if(keyUsage.hasKeyUsage(x509)) {
				l.add(keyUsage);
			}
		}
		return l;
	}
	public static List<ExtendedKeyUsage> getKeyUsage(byte[]encoded){
		List<ExtendedKeyUsage> l = new ArrayList<ExtendedKeyUsage>();
		ExtendedKeyUsage [] values = ExtendedKeyUsage.values();
		for (ExtendedKeyUsage keyUsage : values) {
			if(keyUsage.hasKeyUsage(encoded)) {
				l.add(keyUsage);
			}
		}
		return l;
	}
}
