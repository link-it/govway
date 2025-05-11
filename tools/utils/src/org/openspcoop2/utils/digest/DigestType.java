/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.digest;

import java.io.Serializable;

/**
 * DigestType
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum DigestType implements Serializable {
	SHA256("SHA-256"),
	SHA512_256("SHA-512/256"),
	SHA384("SHA-384"),
	SHA512("SHA-512"),
	SHA3_256("SHA3-256"),
	SHA3_384("SHA3-384"),
	SHA3_512("SHA3-512"),
	SHAKE128("SHAKE128"),
	SHAKE256("SHAKE256");
	
	private final String algorithmName;
	
	private DigestType(String algorithmName) {
		this.algorithmName = algorithmName;
	}
	
	public String getAlgorithmName() {
		return this.algorithmName;
	}
	
	public static DigestType fromAlgorithmName(String value) {
		for (DigestType type : DigestType.values()) {
			if (type.getAlgorithmName().equals(value))
				return type;
		}
		return null;
	}
}
