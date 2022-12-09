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

import org.bouncycastle.asn1.x509.Extensions;

/**
 * BasicConstraints
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicConstraints {

	private boolean ca;
	private long pathLen;
		
	public long getPathLen() {
		return this.pathLen;
	}
	public boolean isCa() {
		return this.ca;
	}
	public boolean isCA() {
		return this.ca;
	}

	public static BasicConstraints getBasicConstraints(byte[]encoded) throws CertificateParsingException{
		
		org.bouncycastle.asn1.x509.Certificate c =org.bouncycastle.asn1.x509.Certificate.getInstance(encoded);
		Extensions exts = c.getTBSCertificate().getExtensions();
		if (exts != null){
			org.bouncycastle.asn1.x509.BasicConstraints basicConstraints = org.bouncycastle.asn1.x509.BasicConstraints.fromExtensions(exts);
			if(basicConstraints!=null) {
//				System.out.println("======================");
//				System.out.println("BasicConstraints '"+basicConstraints.toString()+"'");
//				System.out.println("Len '"+basicConstraints.getPathLenConstraint()+"'");
//				System.out.println("Qual '"+basicConstraints.isCA()+"'");
//				System.out.println("======================");
				
				BasicConstraints bc = new BasicConstraints();
				bc.ca = basicConstraints.isCA();
				if(basicConstraints.getPathLenConstraint()!=null) {
					bc.pathLen = basicConstraints.getPathLenConstraint().longValue();
				}
				return bc;
			}
		}
		return null;
		
	}
}
