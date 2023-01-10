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
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;

/**
 * SubjectAlternativeNames
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SubjectAlternativeNames {

	private List<GeneralName> alternativeNames = new ArrayList<>();
	
	
	public List<GeneralName> getObjectAlternativeNames() {
		return this.alternativeNames;
	}
	public GeneralName getObjectAlternativeName(int index) {
		return this.alternativeNames!=null && (this.alternativeNames.size()>index) ? this.alternativeNames.get(index) : null;
	}
	public List<String> getAlternativeNames() {
		List<String> s = new ArrayList<>();
		if(this.alternativeNames!=null && !this.alternativeNames.isEmpty()) {
			for (GeneralName o : this.alternativeNames) {
				if(o.getName()!=null) {
					s.add(o.getName().toString());
				}
			}
		}
		return s;
	}
	public String getAlternativeName(int index) {
		return this.alternativeNames!=null && (this.alternativeNames.size()>index) ? (this.alternativeNames.get(index)!=null && this.alternativeNames.get(index).getName()!=null) ? this.alternativeNames.get(index).getName().toString() : null : null;
	}
	public boolean containsAlternativeName(String name) throws CertificateParsingException {
		return _containsAlternativeName(null, name);
	}
	public boolean containsAlternativeName(int tagNum, String name) throws CertificateParsingException {
		return _containsAlternativeName(tagNum, name);
	}
	private boolean _containsAlternativeName(Integer tagNum, String name) throws CertificateParsingException {
		if(name==null) {
			throw new CertificateParsingException("Param name undefined");
		}
		if(this.alternativeNames!=null && !this.alternativeNames.isEmpty()) {
			for (GeneralName o : this.alternativeNames) {
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
	
	public static SubjectAlternativeNames getSubjectAlternativeNames(byte[]encoded) throws CertificateParsingException{
		
		org.bouncycastle.asn1.x509.Certificate c =org.bouncycastle.asn1.x509.Certificate.getInstance(encoded);
		Extensions exts = c.getTBSCertificate().getExtensions();
		if (exts != null){
			org.bouncycastle.asn1.x509.GeneralNames gns = org.bouncycastle.asn1.x509.GeneralNames.fromExtensions(exts, Extension.subjectAlternativeName);
			if(gns!=null) {
				
				SubjectAlternativeNames san = null;
				
				org.bouncycastle.asn1.x509.GeneralName [] names = gns.getNames();
				if(names!=null && names.length>0) {
					
					san = new SubjectAlternativeNames();
					
					for (GeneralName generalName : names) {
						san.alternativeNames.add(generalName);
					}
				}
				
//				System.out.println("======================");
//				System.out.println("GeneralNames '"+gns.toString()+"'");
//				if(names!=null && names.length>0) {
//					System.out.println("Len '"+names.length+"'");
//					for (int i = 0; i < names.length; i++) {
//						GeneralName gn = names[i];
//						System.out.println("gn["+i+"]=["+gn.getName()+"]["+gn.getTagNo()+"]");
//					}
//				}
//				System.out.println("======================");
				
				return san;
			}
		}
		return null;
		
	}
}
