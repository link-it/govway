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

import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

/**
 * CRLDistributionPoints
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CRLDistributionPoints {

	private List<CRLDistributionPoint> distributionPoints = new ArrayList<>();

	
	public List<CRLDistributionPoint> getCRLDistributionPoints() {
		return this.distributionPoints;
	}
	public CRLDistributionPoint getCRLDistributionPoint(int index) {
		return this.distributionPoints!=null && (this.distributionPoints.size()>index) ? this.distributionPoints.get(index) : null;
	}
	
	public static CRLDistributionPoints getCRLDistributionPoints(byte[]encoded) throws CertificateParsingException{
		
		org.bouncycastle.asn1.x509.Certificate c =org.bouncycastle.asn1.x509.Certificate.getInstance(encoded);
		Extensions exts = c.getTBSCertificate().getExtensions();
		if (exts != null){
			org.bouncycastle.asn1.x509.CRLDistPoint crlDistPoint = org.bouncycastle.asn1.x509.CRLDistPoint.fromExtensions(exts);
			if(crlDistPoint!=null) {
				CRLDistributionPoints crls = null;
				if(crlDistPoint.getDistributionPoints()!=null && crlDistPoint.getDistributionPoints().length>0) {
					
					crls = new CRLDistributionPoints();
					
					for (int i = 0; i < crlDistPoint.getDistributionPoints().length; i++) {
						DistributionPoint dt = crlDistPoint.getDistributionPoints()[i];
						if(dt!=null) {
							
							CRLDistributionPoint crl = new CRLDistributionPoint();
							
							if(dt.getCRLIssuer()!=null && dt.getCRLIssuer().getNames()!=null && dt.getCRLIssuer().getNames().length>0) {
								for (GeneralName gn : dt.getCRLIssuer().getNames()) {
									crl.crlIssuers.add(gn);
								}
							}
							if(dt.getReasons()!=null) {
								crl.reasonFlags = dt.getReasons();
							}
							if(dt.getDistributionPoint()!=null) {
								crl.distributionPointName = dt.getDistributionPoint();
								if(dt.getDistributionPoint().getName()!=null && (dt.getDistributionPoint().getName() instanceof GeneralNames)) {
									GeneralNames gns = (GeneralNames) dt.getDistributionPoint().getName();
									if(gns.getNames()!=null && gns.getNames().length>0) {
										for (GeneralName gn : gns.getNames()) {
											crl.distributionPointNames.add(gn);
										}
									}
								}
							}
							
							crls.distributionPoints.add(crl);
						}
					}
				}
				
//				System.out.println("======================");
//				//System.out.println("CRLDistributionPoints '"+crlDistPoint.toString()+"'");
//				for (int i = 0; i < crlDistPoint.getDistributionPoints().length; i++) {
//					DistributionPoint dt = crlDistPoint.getDistributionPoints()[i];
//					System.out.println("Issuer '"+dt.getCRLIssuer()+"'");
//					System.out.println("Point '"+dt.getDistributionPoint()+"'");
//					System.out.println("Reasons '"+dt.getReasons()+"'");
//				}
//				System.out.println("======================");
				
				return crls;
			}
		}
		return null;
		
	}
}
