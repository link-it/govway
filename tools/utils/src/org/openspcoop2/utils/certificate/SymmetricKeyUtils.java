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

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.openspcoop2.utils.UtilsException;

/**	
 * SymmetricKeyUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SymmetricKeyUtils {

	public static final String ALGO_AES = "AES";
	
	
	public static SymmetricKeyUtils getInstance() throws UtilsException {
		return new SymmetricKeyUtils();
	}
	public static SymmetricKeyUtils getInstance(String algo) throws UtilsException {
		return new SymmetricKeyUtils(algo);
	}
	
	private String algorithm;
	
	public SymmetricKeyUtils() {
		this(ALGO_AES);
	}
	public SymmetricKeyUtils(String algorithm) {
		this.algorithm = algorithm;
	}
	
	
	// ** SECRET KEY **/
	
	public SecretKey getSecretKey(byte[] secretKey) throws UtilsException {

		try {
			return new SecretKeySpec(secretKey, this.algorithm);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
}
