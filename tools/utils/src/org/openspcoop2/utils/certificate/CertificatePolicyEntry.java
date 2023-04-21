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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/**
 * CertificatePolicyEntry
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificatePolicyEntry {
	
	public CertificatePolicyEntry(org.bouncycastle.asn1.DLSequence dl) {
		this(dl,0);
	}
	private CertificatePolicyEntry(org.bouncycastle.asn1.DLSequence dl, int level) {
		for (int j = 0; j < dl.size(); j++) {
			Object o = dl.getObjectAt(j);
			if(o!=null) {
				init(o, level);
			}
		}
	}
	private void init(Object o, int level) {
		if(o instanceof org.bouncycastle.asn1.ASN1ObjectIdentifier) {
			this.asn1ObjectIdentifier = (org.bouncycastle.asn1.ASN1ObjectIdentifier) o;
		}
		else {
			if(o instanceof org.bouncycastle.asn1.DLSequence) {
				if(level<10) {
					CertificatePolicyEntry cpe = new CertificatePolicyEntry((org.bouncycastle.asn1.DLSequence)o,(level+1));
					this.entries.add(cpe);
				}
			}
			else {
				this.value.add(o);
			}
		}
	}
	
	private ASN1ObjectIdentifier asn1ObjectIdentifier;
	private List<Object> value = new ArrayList<>();
	private List<CertificatePolicyEntry> entries = new ArrayList<>();

	public ASN1ObjectIdentifier getAsn1ObjectIdentifier() {
		return this.asn1ObjectIdentifier;
	}
	public String getOID() {
		return this.asn1ObjectIdentifier!=null ? this.asn1ObjectIdentifier.getId() : null;
	}
	
	public List<Object> getObjectValues() {
		return this.value;
	}
	public Object getObjectValue(int index) {
		return this.value!=null && (this.value.size()>index) ? this.value.get(index) : null;
	}
	public List<String> getValues() {
		List<String> s = new ArrayList<>();
		if(this.value!=null && !this.value.isEmpty()) {
			for (Object o : this.value) {
				s.add(o.toString());
			}
		}
		return s;
	}
	public String getValue(int index) {
		if(this.value!=null && (this.value.size()>index)) {
			return this.value.get(index)!=null ? this.value.get(index).toString() : null;
		}
		return null;
	}
	public boolean containsValue(String value) throws CertificateParsingException {
		if(value==null) {
			throw new CertificateParsingException("Param value undefined");
		}
		if(this.value!=null && !this.value.isEmpty()) {
			for (Object o : this.value) {
				if(value.equals(o.toString())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<CertificatePolicyEntry> getEntries() {
		return this.entries;
	}
	public CertificatePolicyEntry getEntry(int index) {
		return this.entries!=null && (this.entries.size()>index) ? this.entries.get(index) : null;
	}
	public CertificatePolicyEntry getEntry(String oid) throws CertificateParsingException {
		return getEntryByOID(oid);
	}
	public CertificatePolicyEntry getEntryByOID(String oid) throws CertificateParsingException {
		if(oid==null) {
			throw new CertificateParsingException("Param oid undefined");
		}
		if(this.entries!=null && !this.entries.isEmpty()) {
			for (CertificatePolicyEntry certificatePolicyEntry : this.entries) {
				if(oid.equals(certificatePolicyEntry.getOID())) {
					return certificatePolicyEntry;
				}
			}
		}
		return null;
	}
	public boolean hasCertificatePolicyEntry(String oid) throws CertificateParsingException {
		if(oid==null) {
			throw new CertificateParsingException("Param oid undefined");
		}
		return this.getEntryByOID(oid)!=null;
	}
	
	@Override
	public String toString() {
		return toString("");
	}
	public String toString(String prefix) {
		StringBuilder sb = new StringBuilder();
		if(this.asn1ObjectIdentifier!=null) {
			sb.append(prefix);
			sb.append("OID:");
			sb.append(this.asn1ObjectIdentifier.getId());
		}
		if(this.value!=null && !this.value.isEmpty()) {
			int index = 0;
			for (Object o : this.value) {
				sb.append("\n");
				sb.append(prefix);
				sb.append("Value["+index+"]:");
				sb.append(o.toString());
				index++;
			}
			
		}
		if(this.entries!=null && !this.entries.isEmpty()) {
			int index = 0;
			for (CertificatePolicyEntry o : this.entries) {
				sb.append("\n");
				sb.append(prefix);
				sb.append("CertificatePolicyEntry["+index+"]{\n");
				sb.append(o.toString((prefix+"\t")));
				sb.append("\n"+prefix+"}");
				index++;
			}
		}
		return sb.toString();
	}
}
