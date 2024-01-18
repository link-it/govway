/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**
 * BasicConstraints
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthorityKeyIdentifier {

	private org.bouncycastle.asn1.x509.AuthorityKeyIdentifier authorityKeyIdentifierBC;
	private long certSerialNumber;
	private byte[] keyIdentifier;
	private List<GeneralName> certIssuers = new ArrayList<>();

	public org.bouncycastle.asn1.x509.AuthorityKeyIdentifier getAuthorityKeyIdentifier() {
		return this.authorityKeyIdentifierBC;
	}

	public long getCertSerialNumber() {
		return this.certSerialNumber;
	}

	public byte[] getKeyIdentifier() {
		return this.keyIdentifier;
	}
	public String getBase64KeyIdentifier() {
		return this.keyIdentifier!=null ? Base64Utilities.encodeAsString(this.keyIdentifier) : null;
	}
	public String getHexKeyIdentifier() throws UtilsException {
		return this.keyIdentifier!=null ? HexBinaryUtilities.encodeAsString(this.keyIdentifier) : null;
	}
	
	public List<GeneralName> getObjectCertIssuers() {
		return this.certIssuers;
	}
	public GeneralName getObjectCertIssuer(int index) {
		return this.certIssuers!=null && (this.certIssuers.size()>index) ? this.certIssuers.get(index) : null;
	}
	public List<String> getCertIssuers() {
		List<String> s = new ArrayList<>();
		if(this.certIssuers!=null && !this.certIssuers.isEmpty()) {
			for (GeneralName o : this.certIssuers) {
				if(o.getName()!=null) {
					s.add(o.getName().toString());
				}
			}
		}
		return s;
	}
	public String getCertIssuer(int index) {
		if( this.certIssuers!=null && (this.certIssuers.size()>index) ) {
			return (this.certIssuers.get(index)!=null && this.certIssuers.get(index).getName()!=null) ? this.certIssuers.get(index).getName().toString() : null;
		}
		return null;
	}
	public boolean containsCertIssuer(String name) throws CertificateParsingException {
		return containsCertIssuerEngine(null, name);
	}
	public boolean containsCertIssuer(int tagNum, String name) throws CertificateParsingException {
		return containsCertIssuerEngine(tagNum, name);
	}
	private boolean containsCertIssuerEngine(Integer tagNum, String name) throws CertificateParsingException {
		if(name==null) {
			throw new CertificateParsingException("Param name undefined");
		}
		if(this.certIssuers!=null && !this.certIssuers.isEmpty()) {
			for (GeneralName o : this.certIssuers) {
				if(isEquals(o, tagNum, name)) {
					return true;
				}
			}
		}
		return false;
	}
	private boolean isEquals(GeneralName o, Integer tagNum, String name) {
		if(o.getName()!=null && name.equals(o.getName().toString())) {
			if(tagNum==null) {
				return true;
			}
			else {
				if(tagNum.intValue() == o.getTagNo()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static AuthorityKeyIdentifier getAuthorityKeyIdentifier(byte[]encoded) {
		
		org.bouncycastle.asn1.x509.Certificate c =org.bouncycastle.asn1.x509.Certificate.getInstance(encoded);
		Extensions exts = c.getTBSCertificate().getExtensions();
		if (exts != null){
			org.bouncycastle.asn1.x509.AuthorityKeyIdentifier authorityKeyIdentifier = org.bouncycastle.asn1.x509.AuthorityKeyIdentifier.fromExtensions(exts);
			if(authorityKeyIdentifier!=null) {
				/**System.out.println("======================");
				System.out.println("AuthorityKeyIdentifier '"+authorityKeyIdentifier.toString()+"'");
				System.out.println("======================");*/
				
				AuthorityKeyIdentifier aki = new AuthorityKeyIdentifier();
				aki.authorityKeyIdentifierBC = authorityKeyIdentifier;
				if(authorityKeyIdentifier.getAuthorityCertSerialNumber()!=null) {
					aki.certSerialNumber = authorityKeyIdentifier.getAuthorityCertSerialNumber().longValue();
				}
				aki.keyIdentifier = authorityKeyIdentifier.getKeyIdentifier();
				if(authorityKeyIdentifier.getAuthorityCertIssuer()!=null && authorityKeyIdentifier.getAuthorityCertIssuer().getNames()!=null && authorityKeyIdentifier.getAuthorityCertIssuer().getNames().length>0) {
					for (GeneralName gn : authorityKeyIdentifier.getAuthorityCertIssuer().getNames()) {
						aki.certIssuers.add(gn);
					}
				}
				return aki;
			}
		}
		return null;
		
	}
}
