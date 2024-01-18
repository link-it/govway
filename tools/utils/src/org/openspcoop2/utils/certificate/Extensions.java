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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/**
 * Extensions
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Extensions {

	private org.bouncycastle.asn1.x509.Extensions exts;

	public org.bouncycastle.asn1.x509.Extensions getAll() {
		return this.exts;
	}
	public org.bouncycastle.asn1.x509.Extensions getExtensions() {
		return this.exts;
	}

	public org.bouncycastle.asn1.x509.Extension getExtension(String id) {
		org.bouncycastle.asn1.ASN1ObjectIdentifier asn1 = new ASN1ObjectIdentifier(id);
		return getExtension(asn1);
	}
	public org.bouncycastle.asn1.x509.Extension getExtension(org.bouncycastle.asn1.ASN1ObjectIdentifier id) {
		if(this.exts!=null) {
			return this.exts.getExtension(id);
		}
		return null;
	}
	
	public List<String> getOIDs(){
		return getExtensionOIDsEngine(this.getASN1OIDs());
	}
	public List<org.bouncycastle.asn1.ASN1ObjectIdentifier> getASN1OIDs(){
		return getExtensionASN1OIDsEngine(this.exts!=null ? this.exts.getExtensionOIDs() : null);
	}
	
	public List<String> getCriticalOIDs(){
		return getExtensionOIDsEngine(this.getCriticalASN1OIDs());
	}
	public List<org.bouncycastle.asn1.ASN1ObjectIdentifier> getCriticalASN1OIDs(){
		return getExtensionASN1OIDsEngine(this.exts!=null ? this.exts.getCriticalExtensionOIDs() : null);
	}
	
	public List<String> getNonCriticalOIDs(){
		return getExtensionOIDsEngine(this.getNonCriticalASN1OIDs());
	}
	public List<org.bouncycastle.asn1.ASN1ObjectIdentifier> getNonCriticalASN1OIDs(){
		return getExtensionASN1OIDsEngine(this.exts!=null ? this.exts.getNonCriticalExtensionOIDs() : null);
	}
	
	private List<String> getExtensionOIDsEngine(List<org.bouncycastle.asn1.ASN1ObjectIdentifier> l){
		List<String> lR = null;
		if(l!=null && !l.isEmpty()) {
			lR = new ArrayList<>();
			for (org.bouncycastle.asn1.ASN1ObjectIdentifier asn1 : l) {
				lR.add(asn1.getId());
			}
		}
		return lR;
	}
	private List<org.bouncycastle.asn1.ASN1ObjectIdentifier> getExtensionASN1OIDsEngine(org.bouncycastle.asn1.ASN1ObjectIdentifier [] ids){
		List<org.bouncycastle.asn1.ASN1ObjectIdentifier> l = null;
		if(ids!=null && ids.length>0) {
			l = new ArrayList<>();
			l.addAll(Arrays.asList(ids));
		}
		return l;
	}
	
	public boolean hasExtension(String id) {
		return this.getExtension(id)!=null;
	}
	public boolean hasExtension(org.bouncycastle.asn1.ASN1ObjectIdentifier id) {
		return this.getExtension(id)!=null;
	}
	
	public boolean hasCriticalExtension(String id) {
		List<String> criticalList = this.getCriticalOIDs();
		if(criticalList!=null) {
			return criticalList.contains(id);
		}
		return false;
	}
	public boolean hasCriticalExtension(org.bouncycastle.asn1.ASN1ObjectIdentifier id) {
		List<org.bouncycastle.asn1.ASN1ObjectIdentifier> criticalList = this.getCriticalASN1OIDs();
		if(criticalList!=null) {
			return criticalList.contains(id);
		}
		return false;
	}
	
	public boolean hasNonCriticalExtension(String id) {
		List<String> nonCriticalList = this.getNonCriticalOIDs();
		if(nonCriticalList!=null) {
			return nonCriticalList.contains(id);
		}
		return false;
	}
	public boolean hasNonCriticalExtension(org.bouncycastle.asn1.ASN1ObjectIdentifier id) {
		List<org.bouncycastle.asn1.ASN1ObjectIdentifier> nonCriticalList = this.getNonCriticalASN1OIDs();
		if(nonCriticalList!=null) {
			return nonCriticalList.contains(id);
		}
		return false;
	}
	
	public static Extensions getExtensions(byte[]encoded) {
		
		Extensions extObj = new Extensions();
		
		org.bouncycastle.asn1.x509.Certificate c =org.bouncycastle.asn1.x509.Certificate.getInstance(encoded);
		extObj.exts = c.getTBSCertificate().getExtensions();

		return extObj;
		
	}
}
