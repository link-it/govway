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

import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;

/**
 * AuthorityInformationAccess
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthorityInformationAccess {

	private static String CA_ISSUERS = "1.3.6.1.5.5.7.48.2";
	private static String OCSP = "1.3.6.1.5.5.7.48.1";
	
	private List<GeneralName> caIssuers = new ArrayList<>();
	private List<GeneralName> ocsps = new ArrayList<>();

	
	public List<GeneralName> getObjectCAIssuers() {
		return this.caIssuers;
	}
	public GeneralName getObjectCAIssuer(int index) {
		return this.caIssuers!=null && (this.caIssuers.size()>index) ? this.caIssuers.get(index) : null;
	}
	public List<String> getCAIssuers() {
		List<String> s = new ArrayList<>();
		if(this.caIssuers!=null && !this.caIssuers.isEmpty()) {
			for (GeneralName o : this.caIssuers) {
				if(o.getName()!=null) {
					s.add(o.getName().toString());
				}
			}
		}
		return s;
	}
	public String getCAIssuer(int index) {
		return this.caIssuers!=null && (this.caIssuers.size()>index) ? (this.caIssuers.get(index)!=null && this.caIssuers.get(index).getName()!=null) ? this.caIssuers.get(index).getName().toString() : null : null;
	}
	public boolean containsCAIssuer(String name) throws CertificateParsingException {
		return _containsCAIssuer(null, name);
	}
	public boolean containsCAIssuer(int tagNum, String name) throws CertificateParsingException {
		return _containsCAIssuer(tagNum, name);
	}
	private boolean _containsCAIssuer(Integer tagNum, String name) throws CertificateParsingException {
		if(name==null) {
			throw new CertificateParsingException("Param name undefined");
		}
		if(this.caIssuers!=null && !this.caIssuers.isEmpty()) {
			for (GeneralName o : this.caIssuers) {
				if(o.getName()!=null) {
					if(name.equals(o.getName().toString())) {
						if(tagNum==null) {
							return true;
						}
						else {
							if(tagNum.intValue() == o.getTagNo()) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	
	public List<GeneralName> getObjectOCSPs() {
		return this.ocsps;
	}
	public GeneralName getObjectOCSP(int index) {
		return this.ocsps!=null && (this.ocsps.size()>index) ? this.ocsps.get(index) : null;
	}
	public List<String> getOCSPs() {
		List<String> s = new ArrayList<>();
		if(this.ocsps!=null && !this.ocsps.isEmpty()) {
			for (GeneralName o : this.ocsps) {
				if(o.getName()!=null) {
					s.add(o.getName().toString());
				}
			}
		}
		return s;
	}
	public String getOCSP(int index) {
		return this.ocsps!=null && (this.ocsps.size()>index) ? (this.ocsps.get(index)!=null && this.ocsps.get(index).getName()!=null) ? this.ocsps.get(index).getName().toString() : null : null;
	}
	public boolean containsOCSP(String name) throws CertificateParsingException {
		return _containsOCSP(null, name);
	}
	public boolean containsOCSP(int tagNum, String name) throws CertificateParsingException {
		return _containsOCSP(tagNum, name);
	}
	private boolean _containsOCSP(Integer tagNum, String name) throws CertificateParsingException {
		if(name==null) {
			throw new CertificateParsingException("Param name undefined");
		}
		if(this.ocsps!=null && !this.ocsps.isEmpty()) {
			for (GeneralName o : this.ocsps) {
				if(o.getName()!=null) {
					if(name.equals(o.getName().toString())) {
						if(tagNum==null) {
							return true;
						}
						else {
							if(tagNum.intValue() == o.getTagNo()) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public static AuthorityInformationAccess getAuthorityInformationAccess(byte[]encoded) throws CertificateParsingException{
		
		org.bouncycastle.asn1.x509.Certificate c =org.bouncycastle.asn1.x509.Certificate.getInstance(encoded);
		Extensions exts = c.getTBSCertificate().getExtensions();
		if (exts != null){
			org.bouncycastle.asn1.x509.AuthorityInformationAccess auth = org.bouncycastle.asn1.x509.AuthorityInformationAccess.fromExtensions(exts);
			if(auth!=null) {
				AuthorityInformationAccess aia = null;
				if(auth.getAccessDescriptions()!=null && auth.getAccessDescriptions().length>0) {
					
					aia = new AuthorityInformationAccess();
					
					for (int i = 0; i < auth.getAccessDescriptions().length; i++) {
						AccessDescription ad = auth.getAccessDescriptions()[i];
						if(ad.getAccessMethod()!=null) {
							if(CA_ISSUERS.equals(ad.getAccessMethod().getId())) {
								aia.caIssuers.add(ad.getAccessLocation());
							}
							else if(OCSP.equals(ad.getAccessMethod().getId())) {
								aia.ocsps.add(ad.getAccessLocation());
							}
						}
					}
				}
				
//				System.out.println("======================");
//				System.out.println("AuthorityInformationAccess '"+auth.toString()+"'");
//				if(auth.getAccessDescriptions()!=null && auth.getAccessDescriptions().length>0) {
//					System.out.println("Len '"+auth.getAccessDescriptions().length+"'");
//					for (int i = 0; i < auth.getAccessDescriptions().length; i++) {
//						AccessDescription ad = auth.getAccessDescriptions()[i];
//						System.out.println("AD["+i+"]=["+ad.getAccessMethod()+"]["+ad.getAccessLocation()+"]["+ad.getAccessLocation().getTagNo()+"]["+ad.getAccessLocation().getName()+"]");
//					}
//				}
//				System.out.println("======================");
				
				return aia;
			}
		}
		return null;
		
	}
}
