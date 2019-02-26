/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.security.message.constants;

/**     
 * EncryptionAlgorithm
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum EncryptionAlgorithm {

	TRIPLEDES("http://www.w3.org/2001/04/xmlenc#tripledes-cbc"),
	AES_128("http://www.w3.org/2001/04/xmlenc#aes128-cbc"),
	AES_192("http://www.w3.org/2001/04/xmlenc#aes192-cbc"),
	AES_256("http://www.w3.org/2001/04/xmlenc#aes256-cbc"),
	AES_128_GCM("http://www.w3.org/2009/xmlenc11#aes128-gcm"),
	AES_192_GCM("http://www.w3.org/2009/xmlenc11#aes192-gcm"),
	AES_256_GCM("http://www.w3.org/2009/xmlenc11#aes256-gcm"),
	SEED_128("http://www.w3.org/2007/05/xmldsig-more#seed128-cbc"),
	CAMELLIA_128("http://www.w3.org/2001/04/xmldsig-more#camellia128-cbc"),
	CAMELLIA_192("http://www.w3.org/2001/04/xmldsig-more#camellia192-cbc"),
	CAMELLIA_256("http://www.w3.org/2001/04/xmldsig-more#camellia256-cbc");
	
	private String uri;
	EncryptionAlgorithm(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return this.uri;
	}
}
