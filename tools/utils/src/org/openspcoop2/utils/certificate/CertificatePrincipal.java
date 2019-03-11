/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
	
	public String getCN() {
		X500Name x500name = new JcaX509CertificateHolderSelector(this.principal,null).getIssuer();
		RDN cn = x500name.getRDNs(BCStyle.CN)[0];
		return cn.getFirst().getValue().toString();
	}
	
}
