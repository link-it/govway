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


package org.openspcoop2.pdd.core.token.dpop;

import java.io.Serializable;
import java.util.Date;

/**
 * DPoP
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DPoP implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// Header claims

	// Token type ("dpop+jwt" per RFC 9449)
	private String typ;

	// Signature algorithm
	private String alg;

	// JSON Web Key (public key) as string
	private String jwk;
	private String jwkThumbprint;

	// Payload claims

	// The "jti" (JWT ID) claim provides a unique identifier for the DPoP JWT
	private String jti;

	// HTTP method for the request to which the JWT is attached
	private String htm;

	// HTTP target URI for the request to which the JWT is attached
	private String htu;

	// Timestamp of when this DPoP proof was created
	private Date iat;

	// Hash of the access token (base64url encoding of the SHA-256 hash)
	private String ath;

	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json

	

	public String getTyp() {
		return this.typ;
	}
	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getAlg() {
		return this.alg;
	}
	public void setAlg(String alg) {
		this.alg = alg;
	}

	public String getJwk() {
		return this.jwk;
	}
	public void setJwk(String jwk) {
		this.jwk = jwk;
	}
	
	public String getJwkThumbprint() {
		return this.jwkThumbprint;
	}
	public void setJwkThumbprint(String jwkThumbprint) {
		this.jwkThumbprint = jwkThumbprint;
	}

	public String getJti() {
		return this.jti;
	}
	public void setJti(String jti) {
		this.jti = jti;
	}

	public String getHtm() {
		return this.htm;
	}
	public void setHtm(String htm) {
		this.htm = htm;
	}

	public String getHtu() {
		return this.htu;
	}
	public void setHtu(String htu) {
		this.htu = htu;
	}

	public Date getIat() {
		return this.iat;
	}
	public void setIat(Date iat) {
		this.iat = iat;
	}

	public String getAth() {
		return this.ath;
	}
	public void setAth(String ath) {
		this.ath = ath;
	}

}
