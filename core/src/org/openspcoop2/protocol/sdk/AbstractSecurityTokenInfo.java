/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.sdk;

import java.io.Serializable;
import java.security.PublicKey;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.openspcoop2.utils.certificate.CertificateInfo;

/**     
 * AbstractSecurityTokenInfo
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractSecurityTokenInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CertificateInfo certificate;
	private JsonWebKey jsonWebKey;
	private PublicKey publicKey;
	private String kid;

	public CertificateInfo getCertificate() {
		return this.certificate;
	}
	public void setCertificate(CertificateInfo certificate) {
		this.certificate = certificate;
	}
	public JsonWebKey getJsonWebKey() {
		return this.jsonWebKey;
	}
	public void setJsonWebKey(JsonWebKey jsonWebKey) {
		this.jsonWebKey = jsonWebKey;
	}
	public PublicKey getPublicKey() {
		if(this.publicKey==null && this.jsonWebKey!=null) {
			this.publicKey = JwkUtils.toRSAPublicKey(this.jsonWebKey);
		}
		return this.publicKey;
	}
	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
	public String getKid() {
		return this.kid;
	}
	public void setKid(String kid) {
		this.kid = kid;
	}
	
}
