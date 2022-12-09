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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.selector.jcajce.JcaX509CertificateHolderSelector;
import org.openspcoop2.utils.UtilsException;

/**
 * CertificatePrincipal
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificatePrincipal {

	private X500Principal principal;
	private PrincipalType type;
	private X500Name x500name;

	public CertificatePrincipal(X500Principal principal, PrincipalType type) {
		this.principal = principal;
		this.type = type;
	}

	@Override
	public String toString() {
		return this.principal.toString();
	}
	
	public String getName() {
		return this.principal.getName();
	}
	
	public String getName(String format) {
		return this.principal.getName(format);
	}
	
	public String getCanonicalName() {
		return this.principal.getName(X500Principal.CANONICAL);
	}
	
	public String getRFC1779Name() {
		return this.principal.getName(X500Principal.RFC1779);
	}
	
	public String getRFC2253Name() {
		return this.principal.getName(X500Principal.RFC2253);
	}
	
	public String getNameNormalized() throws UtilsException {
		return CertificateUtils.formatPrincipal(this.toString(), this.type);
	}
	public Map<String, List<String>> toMap() throws UtilsException {
		return CertificateUtils.getPrincipalIntoMap(this.toString(), this.type);
	}
	public Map<String, String> toSimpleMap() throws UtilsException {
		return CertificateUtils.formatPrincipalToMap(this.toString(), this.type);
	}
	
	
	private synchronized void initX500Name() {
		if(this.x500name==null) {
			this.x500name=new JcaX509CertificateHolderSelector(this.principal,null).getIssuer();
		}
	}
	
	public static final String CN_EMPTY = "__undefined__";
	public String getCN() {
		return getInfoByOID(BCStyle.CN, CN_EMPTY);
	}
	
	
	
	// ******* OID **********
	
	public List<OID> getOID(){
		List<OID> l = new ArrayList<OID>();
		if(this.x500name==null) {
			this.initX500Name();
		}
		RDN [] rdnArray = this.x500name.getRDNs();
		if(rdnArray!=null && rdnArray.length>0) {
			for (RDN rdn : rdnArray) {
				if(rdn!=null) {
					OID oid = OID.toOID(rdn.getFirst().getType());
//					if(oid==null) {
//						System.out.println("NULLLL ["+rdn.getFirst().getType()+"]: "+rdn.getFirst().getType().getId());
//					}
					if(oid!=null) { // custom
						l.add(oid);
					}
				}
			}
		}
		return l;
	}
		
	public String getInfo(String oid) {
		return getInfoByOID(OID.valueOf(oid.toUpperCase()));
	}
	public String getInfo(String oid, String defaultEmptyValue) {
		return getInfoByOID(OID.valueOf(oid.toUpperCase()),defaultEmptyValue);
	}
	
	public String getInfoByOID(String oid) {
		return getInfoByOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid));
	}
	public String getInfoByOID(String oid, String defaultEmptyValue) {
		return getInfoByOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid),defaultEmptyValue);
	}
	public String getInfoByOID(OID oid) {
		return getInfoByOID(oid.getOID());
	}
	public String getInfoByOID(OID oid, String defaultEmptyValue) {
		return getInfoByOID(oid.getOID(),defaultEmptyValue);
	}
	public String getInfoByOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid) {
		return getInfoByOID(oid, null);
	}
	public String getInfoByOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid, String defaultEmptyValue) {
		if(this.x500name==null) {
			this.initX500Name();
		}
		RDN [] rdnArray = this.x500name.getRDNs(oid);
		if(rdnArray!=null && rdnArray.length>0 && rdnArray[0]!=null) {
			RDN rdn = rdnArray[0];
			if(rdn.getFirst()!=null && rdn.getFirst().getValue()!=null) {
				return rdn.getFirst().getValue().toString();
			}
			else {
				return defaultEmptyValue;
			}
		}
		else {
			return defaultEmptyValue;
		}
	}
	
	public String getInfo(String oid, int position) {
		return getInfoByOID(OID.valueOf(oid.toUpperCase()), position);
	}
	public String getInfo(String oid, String defaultEmptyValue, int position) {
		return getInfoByOID(OID.valueOf(oid.toUpperCase()),defaultEmptyValue, position);
	}
	
	public String getInfoByOID(String oid, int position) {
		return getInfoByOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid), position);
	}
	public String getInfoByOID(String oid, String defaultEmptyValue, int position) {
		return getInfoByOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid),defaultEmptyValue, position);
	}
	public String getInfoByOID(OID oid, int position) {
		return getInfoByOID(oid.getOID(), position);
	}
	public String getInfoByOID(OID oid, String defaultEmptyValue, int position) {
		return getInfoByOID(oid.getOID(),defaultEmptyValue, position);
	}
	public String getInfoByOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid, int position) {
		return getInfoByOID(oid, null, position);
	}
	public String getInfoByOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid, String defaultEmptyValue, int position) {
		List<String> l = getInfosByOID(oid, defaultEmptyValue);
		if(l!=null && l.size()>position) {
			return l.get(position);
		}
		return defaultEmptyValue;
	}
	
	public List<String> getInfos(String oid) {
		return getInfosByOID(OID.valueOf(oid.toUpperCase()));
	}
	public List<String> getInfos(String oid, String defaultEmptyValue) {
		return getInfosByOID(OID.valueOf(oid.toUpperCase()),defaultEmptyValue);
	}
	
	public List<String> getInfosByOID(String oid) {
		return getInfosByOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid));
	}
	public List<String> getInfosByOID(String oid, String defaultEmptyValue) {
		return getInfosByOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid),defaultEmptyValue);
	}
	public List<String> getInfosByOID(OID oid) {
		return getInfosByOID(oid.getOID());
	}
	public List<String> getInfosByOID(OID oid, String defaultEmptyValue) {
		return getInfosByOID(oid.getOID(),defaultEmptyValue);
	}
	public List<String> getInfosByOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid) {
		return getInfosByOID(oid, null);
	}
	public List<String> getInfosByOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid, String defaultEmptyValue) {
		if(this.x500name==null) {
			this.initX500Name();
		}
		RDN [] rdnArray = this.x500name.getRDNs(oid);
		List<String> l = null;
		if(rdnArray!=null && rdnArray.length>0) {
			for (RDN rdn : rdnArray) {
				if(rdn!=null) {
					if(rdn.getFirst()!=null && rdn.getFirst().getValue()!=null) {
						if(l==null) {
							l = new ArrayList<String>();
						}
						l.add(rdn.getFirst().getValue().toString());
					}
				}
			}
		}
		
		if(l==null && defaultEmptyValue!=null) {
			l = new ArrayList<String>();
			l.add(defaultEmptyValue);
		}
		return l;
	}
	
	
	// ******* ID OID **********
	
	public List<String> getIdOID(){
		List<String> l = new ArrayList<String>();
		if(this.x500name==null) {
			this.initX500Name();
		}
		RDN [] rdnArray = this.x500name.getRDNs();
		if(rdnArray!=null && rdnArray.length>0) {
			for (RDN rdn : rdnArray) {
				if(rdn!=null) {
					l.add(rdn.getFirst().getType().getId());
				}
			}
		}
		return l;
	}
	
	public String getInfoByIdOID(String oid) {
		return getInfoByIdOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid));
	}
	public String getInfoByIdOID(String oid, String defaultEmptyValue) {
		return getInfoByIdOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid),defaultEmptyValue);
	}
	public String getInfoByIdOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid) {
		return getInfoByIdOID(oid, null);
	}
	public String getInfoByIdOID(org.bouncycastle.asn1.ASN1ObjectIdentifier idOID, String defaultEmptyValue) {
		if(this.x500name==null) {
			this.initX500Name();
		}
		RDN [] rdnArray = this.x500name.getRDNs(idOID);
		if(rdnArray!=null && rdnArray.length>0 && rdnArray[0]!=null) {
			RDN rdn = rdnArray[0];
			if(rdn.getFirst()!=null && rdn.getFirst().getValue()!=null) {
				return rdn.getFirst().getValue().toString();
			}
			else {
				return defaultEmptyValue;
			}
		}
		else {
			return defaultEmptyValue;
		}
	}
	
	public String getInfoByIdOID(String oid, int position) {
		return getInfoByIdOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid), position);
	}
	public String getInfoByIdOID(String oid, String defaultEmptyValue, int position) {
		return getInfoByIdOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid),defaultEmptyValue, position);
	}
	public String getInfoByIdOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid, int position) {
		return getInfoByIdOID(oid, null, position);
	}
	public String getInfoByIdOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid, String defaultEmptyValue, int position) {
		List<String> l = getInfosByIdOID(oid, defaultEmptyValue);
		if(l!=null && l.size()>position) {
			return l.get(position);
		}
		return defaultEmptyValue;
	}
	
	public List<String> getInfosByIdOID(String oid) {
		return getInfosByIdOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid));
	}
	public List<String> getInfosByIdOID(String oid, String defaultEmptyValue) {
		return getInfosByIdOID(new org.bouncycastle.asn1.ASN1ObjectIdentifier(oid),defaultEmptyValue);
	}
	public List<String> getInfosByIdOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid) {
		return getInfosByIdOID(oid, null);
	}
	public List<String> getInfosByIdOID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid, String defaultEmptyValue) {
		if(this.x500name==null) {
			this.initX500Name();
		}
		RDN [] rdnArray = this.x500name.getRDNs(oid);
		List<String> l = null;
		if(rdnArray!=null && rdnArray.length>0) {
			for (RDN rdn : rdnArray) {
				if(rdn!=null) {
					if(rdn.getFirst()!=null && rdn.getFirst().getValue()!=null) {
						if(l==null) {
							l = new ArrayList<String>();
						}
						l.add(rdn.getFirst().getValue().toString());
					}
				}
			}
		}
		
		if(l==null && defaultEmptyValue!=null) {
			l = new ArrayList<String>();
			l.add(defaultEmptyValue);
		}
		return l;
	}
}
