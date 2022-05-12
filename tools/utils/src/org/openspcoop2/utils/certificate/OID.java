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

import org.bouncycastle.asn1.x500.style.BCStyle;

/**
 * OID
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum OID {

	CN(BCStyle.CN), // [2.5.4.3] common name
	C(BCStyle.C), // [2.5.4.6] country code 
	OU(BCStyle.OU), // [2.5.4.11] organizational unit name
	O(BCStyle.O), // [2.5.4.10] organization name
	L(BCStyle.L), // [2.5.4.7] locality name
	ST(BCStyle.ST), // [2.5.4.8] state, or province name
	
	ORGANIZATION_IDENTIFIER(BCStyle.ORGANIZATION_IDENTIFIER), // [2.5.4.97]
	
	T(BCStyle.T),  // [2.5.4.12]
	SERIALNUMBER(BCStyle.SERIALNUMBER), // [2.5.4.5] device serial number name
	NAME(BCStyle.NAME), //  [2.5.4.41]
	SURNAME(BCStyle.SURNAME), // [2.5.4.4] Naming attributes of type X520name
	GIVENNAME(BCStyle.GIVENNAME), // [2.5.4.42] 
	STREET(BCStyle.STREET), // [2.5.4.9]
	POSTAL_CODE(BCStyle.POSTAL_CODE), // [2.5.4.17]
	POSTAL_ADDRESS(BCStyle.POSTAL_ADDRESS), // [2.5.4.16] RFC 3039 PostalAddress
	TELEPHONE_NUMBER(BCStyle.TELEPHONE_NUMBER), // [2.5.4.20]
	INITIALS(BCStyle.INITIALS), // [2.5.4.43] 
	GENERATION(BCStyle.GENERATION), // [2.5.4.44]
	UNIQUE_IDENTIFIER(BCStyle.UNIQUE_IDENTIFIER), // [2.5.4.45]
	DESCRIPTION(BCStyle.DESCRIPTION), // [2.5.4.13] 
	BUSINESS_CATEGORY(BCStyle.BUSINESS_CATEGORY), // [2.5.4.15] 
	DN_QUALIFIER(BCStyle.DN_QUALIFIER), // [2.5.4.46]
	PSEUDONYM(BCStyle.PSEUDONYM), // [2.5.4.65] RFC 3039 Pseudonym
	ROLE(BCStyle.ROLE), // [2.5.4.72]
	DMD_NAME(BCStyle.DMD_NAME), // [2.5.4.54] RFC 2256 dmdName
	@SuppressWarnings("deprecation")
	@Deprecated
	SN(BCStyle.SN), // [2.5.4.5] use SERIALNUMBER or SURNAME
	
	UID(BCStyle.UID), // [0.9.2342.19200300.100.1.1]
	DC(BCStyle.DC), // [0.9.2342.19200300.100.1.25]
	
	EMAIL_ADDRESS(BCStyle.EmailAddress), // [1.2.840.113549.1.9.1]
	E(BCStyle.E), // [1.2.840.113549.1.9.1] email address in Verisign certificates
	UNSTRUCTURED_NAME(BCStyle.UnstructuredName), // [1.2.840.113549.1.9.2]
	UNSTRUCTURED_ADDRESS(BCStyle.UnstructuredAddress), // [1.2.840.113549.1.9.8]
	
	NAME_AT_BIRTH(BCStyle.NAME_AT_BIRTH),  // [1.3.36.8.3.14] ISIS-MTT NameAtBirth - DirectoryString(SIZE(1..64)
	DATE_OF_BIRTH(BCStyle.DATE_OF_BIRTH), // [1.3.6.1.5.5.7.9.1] RFC 3039 DateOfBirth - GeneralizedTime - YYYYMMDD000000Z
	PLACE_OF_BIRTH(BCStyle.PLACE_OF_BIRTH), // [1.3.6.1.5.5.7.9.2] RFC 3039 PlaceOfBirth
	GENDER(BCStyle.GENDER), // [1.3.6.1.5.5.7.9.3]
	COUNTRY_OF_CITIZENSHIP(BCStyle.COUNTRY_OF_CITIZENSHIP), // [1.3.6.1.5.5.7.9.4] RFC 3039 CountryOfCitizenship - PrintableString (SIZE (2)) -- ISO 3166 codes only
	COUNTRY_OF_RESIDENCE(BCStyle.COUNTRY_OF_RESIDENCE); // [1.3.6.1.5.5.7.9.5] RFC 3039 CountryOfResidence - PrintableString (SIZE (2)) -- ISO 3166 codes only
		

	
	
	private String oid;
	private org.bouncycastle.asn1.ASN1ObjectIdentifier oidBC;
	
	OID(String oid) {
		this.oid = oid;
	}
	OID(org.bouncycastle.asn1.ASN1ObjectIdentifier oid) {
		this.oid = oid.getId();
		this.oidBC = oid;
	}
	
	public String getID() {
		return this.oid;
	}
	public org.bouncycastle.asn1.ASN1ObjectIdentifier getOID() {
		if(this.oidBC!=null) {
			return this.oidBC;
		}
		else {
			return new org.bouncycastle.asn1.ASN1ObjectIdentifier(this.oid);
		}
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	public String toString(boolean printOID) {
		if(printOID) {
			return this.name()+" ("+this.oid+")";
		}
		else {
			return this.name();
		}
	}
	
	public static OID toOID(String id) {
		OID [] v = OID.values();
		for (OID oid : v) {
			if(oid.oid.equals(id)) {
				return oid;
			}
		}
		return null;
	}
	public static OID toOID(org.bouncycastle.asn1.ASN1ObjectIdentifier idBC) {
		OID [] v = OID.values();
		for (OID oid : v) {
			if(oid.oidBC.equals(idBC)) {
				return oid;
			}
		}
		return null;
	}
}
