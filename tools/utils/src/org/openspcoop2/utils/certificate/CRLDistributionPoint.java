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

import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.ReasonFlags;

/**
 * CRLDistributionPoint
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CRLDistributionPoint {

	protected List<GeneralName> crlIssuers = new ArrayList<>();
	protected ReasonFlags reasonFlags;
	protected DistributionPointName distributionPointName;
	protected List<GeneralName> distributionPointNames = new ArrayList<>();
	
	
	
	public DistributionPointName getObjectDistributionPointName() {
		return this.distributionPointName;
	}
	public int getDistributionPointType() {
		return this.distributionPointName!=null ? this.distributionPointName.getType() : -1 ;
	}
	
	public List<GeneralName> getObjectDistributionPointNames() {
		return this.distributionPointNames;
	}
	public GeneralName getObjectDistributionPointName(int index) {
		return this.distributionPointNames!=null && (this.distributionPointNames.size()>index) ? this.distributionPointNames.get(index) : null;
	}
	public List<String> getDistributionPointNames() {
		List<String> s = new ArrayList<>();
		if(this.distributionPointNames!=null && !this.distributionPointNames.isEmpty()) {
			for (GeneralName o : this.distributionPointNames) {
				if(o.getName()!=null) {
					s.add(o.getName().toString());
				}
			}
		}
		return s;
	}
	public String getDistributionPointName(int index) {
		return this.distributionPointNames!=null && (this.distributionPointNames.size()>index) ? (this.distributionPointNames.get(index)!=null && this.distributionPointNames.get(index).getName()!=null) ? this.distributionPointNames.get(index).getName().toString() : null : null;
	}
	public boolean containsDistributionPointName(String name) throws CertificateParsingException {
		return _containsDistributionPointName(null, name);
	}
	public boolean containsDistributionPointName(int tagNum, String name) throws CertificateParsingException {
		return _containsDistributionPointName(tagNum, name);
	}
	private boolean _containsDistributionPointName(Integer tagNum, String name) throws CertificateParsingException {
		if(name==null) {
			throw new CertificateParsingException("Param name undefined");
		}
		if(this.distributionPointNames!=null && !this.distributionPointNames.isEmpty()) {
			for (GeneralName o : this.distributionPointNames) {
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
	

	public ReasonFlags getReasonFlags() {
		return this.reasonFlags;
	}
	
	public List<GeneralName> getObjectCRLIssuers() {
		return this.crlIssuers;
	}
	public GeneralName getObjectCRLIssuer(int index) {
		return this.crlIssuers!=null && (this.crlIssuers.size()>index) ? this.crlIssuers.get(index) : null;
	}
	public List<String> getCRLIssuers() {
		List<String> s = new ArrayList<>();
		if(this.crlIssuers!=null && !this.crlIssuers.isEmpty()) {
			for (GeneralName o : this.crlIssuers) {
				if(o.getName()!=null) {
					s.add(o.getName().toString());
				}
			}
		}
		return s;
	}
	public String getCRLIssuer(int index) {
		return this.crlIssuers!=null && (this.crlIssuers.size()>index) ? (this.crlIssuers.get(index)!=null && this.crlIssuers.get(index).getName()!=null) ? this.crlIssuers.get(index).getName().toString() : null : null;
	}
	public boolean containsCRLIssuer(String name) throws CertificateParsingException {
		return _containsCRLIssuer(null, name);
	}
	public boolean containsCRLIssuer(int tagNum, String name) throws CertificateParsingException {
		return _containsCRLIssuer(tagNum, name);
	}
	private boolean _containsCRLIssuer(Integer tagNum, String name) throws CertificateParsingException {
		if(name==null) {
			throw new CertificateParsingException("Param name undefined");
		}
		if(this.crlIssuers!=null && !this.crlIssuers.isEmpty()) {
			for (GeneralName o : this.crlIssuers) {
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
}
