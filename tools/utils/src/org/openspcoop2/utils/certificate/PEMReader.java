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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * PEMReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PEMReader implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String PKCS1_BEGIN = "-----BEGIN RSA PRIVATE KEY-----"; 
    public static final String PKCS1_END = "-----END RSA PRIVATE KEY-----";

    public static final String PKCS8_BEGIN = "-----BEGIN PRIVATE KEY-----"; 
    public static final String PKCS8_END = "-----END PRIVATE KEY-----";
    
    public static final String PKCS8_ENCRYPTED_BEGIN = "-----BEGIN ENCRYPTED PRIVATE KEY-----"; 
    public static final String PKCS8_ENCRYPTED_END = "-----END ENCRYPTED PRIVATE KEY-----";
    
    public static final String PUBLIC_KEY_BEGIN = "-----BEGIN PUBLIC KEY-----"; 
    public static final String PUBLIC_KEY_END = "-----END PUBLIC KEY-----";
    
    public static final String X509_BEGIN = "-----BEGIN CERTIFICATE-----"; 
    public static final String X509_END = "-----END CERTIFICATE-----";
    
	
	private String privateKey;
	private String publicKey;
	private List<String> certificates = new ArrayList<>();
	private boolean pkcs1; // l'encrypted dell'1 è una informazione interna
	private boolean pkcs8;
	private boolean pkcs8encrypted;
	
	public PEMReader(byte[] pem) {
		this(pem, true, true, true);
	}
	public PEMReader(byte[] pem, boolean pkcs1, boolean pkcs8, boolean pkcs8encrypted) {
		this(getAsString(pem), pkcs1, pkcs8, pkcs8encrypted);
	}
	static String getAsString(byte[] pem) {
		String publicKeyString = null;
		try {
			publicKeyString = new String(pem);
		}catch(Exception e) {
			// ignore
		}
		return publicKeyString;
	}
	public PEMReader(String pem) {
		this(pem, true, true, true);
	}
	public PEMReader(String pem, boolean pkcs1, boolean pkcs8, boolean pkcs8encrypted) {
		
		if(pem==null) {
			return;
		}
		
		pem = pem.trim();
		
		if(pkcs1 && pem.contains(PKCS1_BEGIN)) {
			this.privateKey = read(pem, PKCS1_BEGIN, PKCS1_END);
			this.pkcs1 = this.privateKey!=null && StringUtils.isNotEmpty(this.privateKey);
		}
		else if(pkcs8 && pem.contains(PKCS8_BEGIN)) {
			this.privateKey = read(pem, PKCS8_BEGIN, PKCS8_END);
			this.pkcs8 = this.privateKey!=null && StringUtils.isNotEmpty(this.privateKey);
		}
		else if(pkcs8encrypted && pem.contains(PKCS8_ENCRYPTED_BEGIN)) {
			this.privateKey = read(pem, PKCS8_ENCRYPTED_BEGIN, PKCS8_ENCRYPTED_END);
			this.pkcs8encrypted = this.privateKey!=null && StringUtils.isNotEmpty(this.privateKey);
		}
		
		if(pem.contains(PUBLIC_KEY_BEGIN)) {
			this.publicKey = read(pem, PUBLIC_KEY_BEGIN, PUBLIC_KEY_END);
		}
		
		if(pem.contains(X509_BEGIN)) {
			initCertificate(pem);
		}
	}
	private void initCertificate(String pem) {
		String cert = read(pem, X509_BEGIN, X509_END);
		String check = pem;
		int index = 0;
		while(cert!=null && StringUtils.isNotEmpty(cert) && index<=1000) {
			this.certificates.add(cert);
			check = check.replace(cert, "");
			if(check.contains(X509_BEGIN)) {
				cert = read(check, X509_BEGIN, X509_END);
			}
			else {
				cert=null;
			}
			index++;
		}
	}
	
	private String read(String pem, String begin, String end) {
		if(pem.contains(begin) && pem.contains(end)) {
			int indexOf = pem.indexOf(begin);
			if(indexOf>=0) {
				String tmp = indexOf>0 ? pem.substring(indexOf) : pem;
				int indexOfEnd = tmp.indexOf(end);
				if(indexOfEnd>=0) {
					return readEngine(tmp, indexOfEnd);
				}
			}
		}
		return null;
	}
	private String readEngine(String tmp, int indexOfEnd) {
		/**indexOfEnd = indexOfEnd + end.length();
		String s =  (indexOfEnd==(tmp.length()-1)) ? tmp : tmp.substring(0,indexOfEnd);
		System.out.println("LETTO ["+begin+"]: '"+s+"'");
		return s;*/
		
		int indexOfEndLine = tmp.indexOf("\n", indexOfEnd+1);
		/**System.out.println("END LINE '"+indexOfEndLine+"'");*/
		if(indexOfEndLine<=0) {
			indexOfEndLine = tmp.indexOf("\r", indexOfEnd+1);
			/**System.out.println("END LINE 2 '"+indexOfEndLine+"'");*/
		}
		if(indexOfEndLine<=0) {
			indexOfEndLine = tmp.length();
			/**System.out.println("END LINE 3 '"+indexOfEndLine+"'");*/
		}
		
		String s = tmp.substring(0,indexOfEndLine);
		if(s!=null) {
			s = s.trim();
		}
		/**System.out.println("LETTO ["+begin+"]: '"+s+"'");*/
		return s;
	}
	
	public String getPrivateKey() {
		return this.privateKey;
	}
	public String getPublicKey() {
		return this.publicKey;
	}
	public List<String> getCertificates() {
		return this.certificates;
	}
	
	public boolean isPkcs1() {
		return this.pkcs1;
	}
	public boolean isPkcs8() {
		return this.pkcs8;
	}
	public boolean isPkcs8encrypted() {
		return this.pkcs8encrypted;
	}
	
}
