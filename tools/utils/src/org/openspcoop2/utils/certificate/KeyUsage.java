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

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.Extensions;

/**
 * KeyUsage
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum KeyUsage {

	/*
	 *  KeyUsage ::= BIT STRING {
		     digitalSignature        (0),
		     nonRepudiation          (1),
		     keyEncipherment         (2),
		     dataEncipherment        (3),
		     keyAgreement            (4),
		     keyCertSign             (5),
		     cRLSign                 (6),
		     encipherOnly            (7),
		     decipherOnly            (8)
	 **/
	
	DIGITAL_SIGNATURE(0, org.bouncycastle.asn1.x509.KeyUsage.digitalSignature),
	
	NON_REPUDIATION(1, org.bouncycastle.asn1.x509.KeyUsage.nonRepudiation),

	KEY_ENCIPHERMENT(2, org.bouncycastle.asn1.x509.KeyUsage.keyEncipherment),
	
	DATA_ENCIPHERMENT(3, org.bouncycastle.asn1.x509.KeyUsage.dataEncipherment),
	
	KEY_AGREEMENT(4, org.bouncycastle.asn1.x509.KeyUsage.keyAgreement),
	
	KEY_CERT_SIGN(5, org.bouncycastle.asn1.x509.KeyUsage.keyCertSign),
	
	CRL_SIGN(6, org.bouncycastle.asn1.x509.KeyUsage.cRLSign),
	
	ENCIPHER_ONLY(7, org.bouncycastle.asn1.x509.KeyUsage.encipherOnly),
	
	DECIPHER_ONLY(8, org.bouncycastle.asn1.x509.KeyUsage.decipherOnly);
	  
	
	KeyUsage(int arrayBooleanPosition, int bouncyValue ) {
		this.arrayBooleanPosition = arrayBooleanPosition;
		this.bouncyValue = bouncyValue;
	}
	
	private int arrayBooleanPosition;
	private int bouncyValue;

	public int getX509CertificatePosition() {
		return this.arrayBooleanPosition;
	}
	public int getBouncyCastleCode() {
		return this.bouncyValue;
	}
	
	public boolean hasKeyUsage(Certificate x509) {
		if(x509.getCertificate()!=null) {
			return hasKeyUsage(x509.getCertificate());
		}
		return false;
	}
	public boolean hasKeyUsage(CertificateInfo x509) {
		return hasKeyUsage(x509.getCertificate());
	}
	public boolean hasKeyUsage(X509Certificate x509) {
		return existsKeyUsageByArrayBooleanPosition(x509, this.arrayBooleanPosition);
	}
	public static boolean existsKeyUsageByArrayBooleanPosition(X509Certificate x509, int arrayBooleanPosition) {
		if(x509.getKeyUsage()!=null && x509.getKeyUsage().length>arrayBooleanPosition) {
			return x509.getKeyUsage()[arrayBooleanPosition];
		}
		return false;
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	public String toString(boolean printArrayPosition) {
		if(printArrayPosition) {
			return this.name()+" ("+this.arrayBooleanPosition+")";
		}
		else {
			return this.name();
		}
	}
	
	public boolean hasKeyUsage(byte[]encoded) {
		return existsKeyUsageByBouncycastleCode(encoded, this.bouncyValue);
	}
	public static boolean existsKeyUsageByBouncycastleCode(byte[]encoded, int bouncyValue) {
		org.bouncycastle.asn1.x509.Certificate c =org.bouncycastle.asn1.x509.Certificate.getInstance(encoded);
		Extensions exts = c.getTBSCertificate().getExtensions();
		if (exts != null){
			org.bouncycastle.asn1.x509.KeyUsage ku = org.bouncycastle.asn1.x509.KeyUsage.fromExtensions(exts);
			if(ku!=null) {
				return ku.hasUsages(bouncyValue);
			}
		}
		return false;
	}
		
	public static List<KeyUsage> getKeyUsage(Certificate x509){
		if(x509.getCertificate()!=null) {
			return getKeyUsage(x509.getCertificate());
		}
		return new ArrayList<KeyUsage>();
	}
	public static List<KeyUsage> getKeyUsage(CertificateInfo x509){
		return getKeyUsage(x509.getCertificate());
	}
	public static List<KeyUsage> getKeyUsage(X509Certificate x509){
		List<KeyUsage> l = new ArrayList<KeyUsage>();
		KeyUsage [] values = KeyUsage.values();
		for (KeyUsage keyUsage : values) {
			if(keyUsage.hasKeyUsage(x509)) {
				l.add(keyUsage);
			}
		}
		return l;
	}
	public static List<KeyUsage> getKeyUsage(byte[]encoded){
		List<KeyUsage> l = new ArrayList<KeyUsage>();
		KeyUsage [] values = KeyUsage.values();
		for (KeyUsage keyUsage : values) {
			if(keyUsage.hasKeyUsage(encoded)) {
				l.add(keyUsage);
			}
		}
		return l;
	}
	
}
