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
package org.openspcoop2.utils.certificate;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.KeyType;

/**
 * JwkUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JwkUtils {

	private JwkUtils() {}

	public static PrivateKey toPrivateKey(JsonWebKey jsonWebKey) {
		if(KeyType.EC.equals(jsonWebKey.getKeyType())) {
			return org.apache.cxf.rs.security.jose.jwk.JwkUtils.toECPrivateKey(jsonWebKey);
		}
		else {
			return org.apache.cxf.rs.security.jose.jwk.JwkUtils.toRSAPrivateKey(jsonWebKey);
		}
	}

	public static PublicKey toPublicKey(JsonWebKey jsonWebKey) {
		if(KeyType.EC.equals(jsonWebKey.getKeyType())) {
			return org.apache.cxf.rs.security.jose.jwk.JwkUtils.toECPublicKey(jsonWebKey);
		}
		else {
			return org.apache.cxf.rs.security.jose.jwk.JwkUtils.toRSAPublicKey(jsonWebKey);
		}
	}

	public static SecretKey toSecretKey(JsonWebKey jsonWebKey) {
		return org.apache.cxf.rs.security.jose.jwk.JwkUtils.toSecretKey(jsonWebKey);
	}

}
